package com.yourbank.banking_api.controller;

import com.yourbank.banking_api.dto.AccountRequestDTO;
import com.yourbank.banking_api.dto.BranchDTO;
import com.yourbank.banking_api.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
@Tag(name = "Branch Management", description = "Manage bank branches")

public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @Operation(summary = "Create a new branch")
    public ResponseEntity<BranchDTO> createBranch(@Valid @RequestBody BranchDTO branchDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(branchService.createBranch(branchDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get branch details by ID")
    public ResponseEntity<BranchDTO> getBranchById(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }

    @GetMapping("/{branchId}/accounts")
    @Operation(summary = "List all accounts in a branch")
    public ResponseEntity<List<AccountRequestDTO>> getAccountsInBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(branchService.getAccountsByBranch(branchId));
    }

}
