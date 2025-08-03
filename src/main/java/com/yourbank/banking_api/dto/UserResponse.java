package com.yourbank.banking_api.dto;

import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String firstname,
        String lastname,
        String email
) {}