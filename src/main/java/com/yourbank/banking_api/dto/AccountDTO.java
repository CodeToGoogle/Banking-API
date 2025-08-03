package com.yourbank.banking_api.dto;

import com.yourbank.banking_api.model.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AccountDTO(
        Long id,
        String accountNumber,
        @NotNull Long branchId,
        @NotNull Long customerId,
        String currency,
        @NotNull AccountType type
) {}
