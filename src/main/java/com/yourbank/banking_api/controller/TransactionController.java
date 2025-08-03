package com.yourbank.banking_api.controller;

import com.yourbank.banking_api.dto.TransactionDTO;
import com.yourbank.banking_api.dto.TransactionRequestDTO;
import com.yourbank.banking_api.dto.TransferRequestDTO;
import com.yourbank.banking_api.dto.TransferResponseDTO;
import com.yourbank.banking_api.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Management", description = "Handle financial transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    @Operation(summary = "Deposit money into an account")
    public ResponseEntity<TransactionDTO> deposit(
            @Valid @RequestBody TransactionRequestDTO request) {
        return ResponseEntity.ok(transactionService.deposit(request));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw money from an account")
    public ResponseEntity<TransactionDTO> withdraw(
            @Valid @RequestBody TransactionRequestDTO request) {
        return ResponseEntity.ok(transactionService.withdraw(request));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer money between accounts")

    public ResponseEntity<TransferResponseDTO> transfer(
            @Valid @RequestBody TransferRequestDTO request) {
        return ResponseEntity.ok(transactionService.transfer(request));
    }

    @GetMapping("/branch/{branchId}")
    @Operation(summary = "List all transactions for a branch")

    public ResponseEntity<Page<TransactionDTO>> getBranchTransactions(
            @PathVariable Long branchId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String type,
            Pageable pageable) {
        return ResponseEntity.ok(transactionService.getBranchTransactions(branchId, date, type, pageable));
    }
}
