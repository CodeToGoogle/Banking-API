package com.yourbank.banking_api.controller;

import com.yourbank.banking_api.dto.AccountDTO;
import com.yourbank.banking_api.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "Manage bank accounts")

public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create a new account")

    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(accountDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account details by ID")

    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping("/branch/{branchId}")
    @Operation(summary = "List all accounts in a branch")

    public ResponseEntity<Page<AccountDTO>> getAccountsByBranch(
            @PathVariable Long branchId,
            Pageable pageable) {
        return ResponseEntity.ok(accountService.getAccountsByBranch(branchId, pageable));
    }
}
