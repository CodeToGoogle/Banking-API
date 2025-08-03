package com.yourbank.banking_api.service;

import com.yourbank.banking_api.dto.TransactionRequestDTO;
import com.yourbank.banking_api.model.Account;
import com.yourbank.banking_api.repository.AccountRepository;
import com.yourbank.banking_api.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void deposit_shouldIncreaseAccountBalance() {
        // Setup
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(100));
        account.setCurrency("INR");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        TransactionRequestDTO request = new TransactionRequestDTO(
                1L,
                BigDecimal.valueOf(50),
                "INR",
                "Deposit"
        );
        var result = transactionService.deposit(request);

        // Verify
        assertEquals(BigDecimal.valueOf(50), result.amount());
        verify(accountRepository).save(argThat(acc ->
                acc.getBalance().equals(BigDecimal.valueOf(150))));
        verify(kafkaTemplate).send(eq("transaction-events"), any());
    }
}