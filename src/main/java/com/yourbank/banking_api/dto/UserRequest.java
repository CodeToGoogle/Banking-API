package com.yourbank.banking_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserRequest(
        @NotBlank String firstname,
        @NotBlank String lastname,
        @NotBlank @Email String email
) {}