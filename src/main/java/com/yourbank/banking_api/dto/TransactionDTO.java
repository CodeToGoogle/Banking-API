package com.yourbank.banking_api.dto;

import com.yourbank.banking_api.model.TransactionStatus;
import com.yourbank.banking_api.model.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionDTO(
        Long id,
        String reference,
        Long accountId,
        BigDecimal amount,
        TransactionType type,
        TransactionStatus status,
        String description,
        LocalDateTime createdAt
) {}
