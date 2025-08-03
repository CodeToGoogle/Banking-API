package com.yourbank.banking_api.exception;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp,
        List<String> details
) {
    public ErrorResponse(String code, String message, LocalDateTime timestamp) {
        this(code, message, timestamp, null);
    }
}