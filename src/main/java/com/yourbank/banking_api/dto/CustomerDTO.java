package com.yourbank.banking_api.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record CustomerDTO(
        Long id,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        String phone,
        String address,
        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9]+$", message = "ID number must be alphanumeric")
        String idNumber
) {}