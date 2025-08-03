package com.yourbank.banking_api.dto;

import com.yourbank.banking_api.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ListTransactionDTO (


            Long id,
            TransactionType type,
            BigDecimal amount,
            String currency,
            String description,
            LocalDateTime createdAt,
            String accountNumber,
            Long customerId,
            String customerName
    ) {}


