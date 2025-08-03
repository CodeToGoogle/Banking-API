package com.yourbank.banking_api.service;

import com.yourbank.banking_api.dto.AccountDTO;
import com.yourbank.banking_api.exception.ResourceNotFoundException;
import com.yourbank.banking_api.model.Account;
import com.yourbank.banking_api.model.AccountStatus;
import com.yourbank.banking_api.model.Branch;
import com.yourbank.banking_api.model.Customer;
import com.yourbank.banking_api.repository.AccountRepository;
import com.yourbank.banking_api.repository.BranchRepository;
import com.yourbank.banking_api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final BranchRepository branchRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public AccountDTO createAccount(AccountDTO accountDTO) {
        Branch branch = branchRepository.findById(accountDTO.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + accountDTO.branchId()));

        Customer customer = customerRepository.findById(accountDTO.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + accountDTO.customerId()));

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .branch(branch)
                .customer(customer)
                .balance(BigDecimal.ZERO)
                .currency(accountDTO.currency() != null ? accountDTO.currency() : "INR")
                .type(accountDTO.type())
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        Account savedAccount = accountRepository.save(account);
        return toDTO(savedAccount);
    }

    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return toDTO(account);
    }

    public Page<AccountDTO> getAccountsByBranch(Long branchId, Pageable pageable) {
        if (!branchRepository.existsById(branchId)) {
            throw new ResourceNotFoundException("Branch not found with id: " + branchId);
        }
        return accountRepository.findByBranchId(branchId, pageable)
                .map(this::toDTO);
    }

    private String generateAccountNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private AccountDTO toDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .branchId(account.getBranch().getId())
                .customerId(account.getCustomer().getId())
                .currency(account.getCurrency())
                .type(account.getType())
                .build();
    }
}
