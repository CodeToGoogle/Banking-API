package com.yourbank.banking_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record BranchDTO(
        Long id,
        @NotBlank String name,
        @NotBlank String code,
        @NotBlank String address,
        String phone
) {}
