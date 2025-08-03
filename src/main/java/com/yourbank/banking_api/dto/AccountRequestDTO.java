package com.yourbank.banking_api.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountRequestDTO (
            Long id,
            String accountNumber,
            BigDecimal balance,
            String currency,
            Long customerId,
            String customerName
    ) {}


