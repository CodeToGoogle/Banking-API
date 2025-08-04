package com.yourbank.banking_api.service;

import com.yourbank.banking_api.config.TransactionEvent;
import com.yourbank.banking_api.dto.TransactionDTO;
import com.yourbank.banking_api.dto.TransactionRequestDTO;
import com.yourbank.banking_api.dto.TransferRequestDTO;
import com.yourbank.banking_api.dto.TransferResponseDTO;
import com.yourbank.banking_api.exception.CurrencyMismatchException;
import com.yourbank.banking_api.exception.ResourceNotFoundException;
import com.yourbank.banking_api.model.Account;
import com.yourbank.banking_api.model.Transaction;
import com.yourbank.banking_api.model.TransactionStatus;
import com.yourbank.banking_api.model.TransactionType;
import com.yourbank.banking_api.repository.AccountRepository;
import com.yourbank.banking_api.repository.BranchRepository;
import com.yourbank.banking_api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BranchRepository branchRepository;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Transactional
    public TransactionDTO deposit(TransactionRequestDTO request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        validateCurrency(account, request.currency());

        Transaction transaction = Transaction.builder()
                .reference(UUID.randomUUID().toString())
                .account(account)
                .amount(request.amount())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.COMPLETED)
                .description(request.description())
                .createdAt(LocalDateTime.now())
                .build();

        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Publish Kafka event
        kafkaTemplate.send("transaction-events", new TransactionEvent(savedTransaction));

        return toDTO(savedTransaction);
    }

    @Transactional
    public TransactionDTO withdraw(TransactionRequestDTO request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        validateCurrency(account, request.currency());

        if (account.getBalance().compareTo(request.amount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        Transaction transaction = Transaction.builder()
                .reference(UUID.randomUUID().toString())
                .account(account)
                .amount(request.amount().negate())
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.COMPLETED)
                .description(request.description())
                .createdAt(LocalDateTime.now())
                .build();

        account.setBalance(account.getBalance().subtract(request.amount()));
        accountRepository.save(account);
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Publish Kafka event
        kafkaTemplate.send("transaction-events", new TransactionEvent(savedTransaction));

        return toDTO(savedTransaction);
    }

    @Transactional
    public TransferResponseDTO transfer(TransferRequestDTO request) {
        Account sourceAccount = accountRepository.findById(request.sourceAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account targetAccount = accountRepository.findById(request.targetAccountId())
                .orElseThrow(() -> new RuntimeException("Target account not found"));

        validateCurrency(sourceAccount, request.currency());
        validateCurrency(targetAccount, request.currency());

        if (sourceAccount.getBalance().compareTo(request.amount()) < 0) {
            throw new RuntimeException("Insufficient funds for transfer");
        }

        // Create withdrawal transaction
        Transaction withdrawal = Transaction.builder()
                .reference(UUID.randomUUID().toString())
                .account(sourceAccount)
                .amount(request.amount().negate())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description(request.description() != null ?
                        request.description() : "Transfer to account " + targetAccount.getAccountNumber())
                .createdAt(LocalDateTime.now())
                .build();

        // Create deposit transaction
        Transaction deposit = Transaction.builder()
                .reference(UUID.randomUUID().toString())
                .account(targetAccount)
                .amount(request.amount())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description(request.description() != null ?
                        request.description() : "Transfer from account " + sourceAccount.getAccountNumber())
                .createdAt(LocalDateTime.now())
                .build();

        // Link transactions
        withdrawal.setRelatedTransaction(deposit);
        deposit.setRelatedTransaction(withdrawal);

        // Update balances
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.amount()));
        targetAccount.setBalance(targetAccount.getBalance().add(request.amount()));

        // Save all in transaction
        accountRepository.saveAll(List.of(sourceAccount, targetAccount));
        transactionRepository.saveAll(List.of(withdrawal, deposit));

        // Publish Kafka events
        kafkaTemplate.send("transaction-events", new TransactionEvent(withdrawal));
        kafkaTemplate.send("transaction-events", new TransactionEvent(deposit));

        return TransferResponseDTO.builder()
                .sourceTransaction(toDTO(withdrawal))
                .targetTransaction(toDTO(deposit))
                .message("Transfer completed successfully")
                .build();
    }

    public Page<TransactionDTO> getBranchTransactions(
            Long branchId,
            LocalDate date,
            String type,
            Pageable pageable
    ) {
        // Validate branch exists
        if (!branchRepository.existsById(branchId)) {
            throw new ResourceNotFoundException("Branch not found with id: " + branchId);
        }

        // Date range
        LocalDateTime startOfDay = (date != null) ? date.atStartOfDay() : LocalDateTime.now().minusMonths(1);
        LocalDateTime endOfDay = (date != null) ? date.atTime(23, 59, 59) : LocalDateTime.now();

        // Convert string type to enum safely
        TransactionType filterType = null;
        if (type != null && !type.isEmpty()) {
            try {
                filterType = TransactionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid transaction type: " + type);
            }
        }

        // Apply default sort by createdAt DESC
        Pageable finalPageable = pageable;
        if (pageable.getSort().isUnsorted()) {
            finalPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("createdAt").descending()
            );
        }

        // Fetch transactions
        Page<Transaction> transactions = transactionRepository.findBranchTransactions(
                branchId,
                startOfDay,
                endOfDay,
                filterType,
                finalPageable
        );

        // Convert to DTO
        return transactions.map(this::convertToDTO);
    }


    private TransactionDTO convertToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .reference(transaction.getReference())
                .accountId(transaction.getAccount().getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    //transactions can only be done in INR / No automatic conversions
    private void validateCurrency(Account account, String currency) {
        if (currency != null && !account.getCurrency().equals(currency)) {
            throw new CurrencyMismatchException(
                    "Currency mismatch: Expected " + account.getCurrency() + " but got " + currency
            );
        }
    }


    private TransactionDTO toDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .reference(transaction.getReference())
                .accountId(transaction.getAccount().getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
