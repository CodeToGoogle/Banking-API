package com.yourbank.banking_api.config;

import com.yourbank.banking_api.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private Long id;
    private String reference;
    private Long accountId;
    private BigDecimal amount;
    private String type;
    private String status;
    private String description;
    private LocalDateTime createdAt;

    public TransactionEvent(Transaction transaction) {
        this.id = transaction.getId();
        this.reference = transaction.getReference();
        this.accountId = transaction.getAccount().getId();
        this.amount = transaction.getAmount();
        this.type = transaction.getType().name();
        this.status = transaction.getStatus().name();
        this.description = transaction.getDescription();
        this.createdAt = transaction.getCreatedAt();
    }
}