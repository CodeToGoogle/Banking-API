package com.yourbank.banking_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransferRequestDTO(
        @NotNull Long sourceAccountId,
        @NotNull Long targetAccountId,
        @NotNull @Positive BigDecimal amount,
        String currency,
        String description
) {}