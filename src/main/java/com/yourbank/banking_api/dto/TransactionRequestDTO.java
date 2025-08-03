package com.yourbank.banking_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionRequestDTO(
        @NotNull Long accountId,
        @NotNull @Positive BigDecimal amount,
        String currency,
        String description
) {}
