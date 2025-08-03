package com.yourbank.banking_api.dto;

import lombok.Builder;

@Builder
public record TransferResponseDTO(
        TransactionDTO sourceTransaction,
        TransactionDTO targetTransaction,
        String message
) {}