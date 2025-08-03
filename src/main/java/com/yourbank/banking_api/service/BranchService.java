package com.yourbank.banking_api.service;

import com.yourbank.banking_api.dto.AccountDTO;
import com.yourbank.banking_api.dto.AccountRequestDTO;
import com.yourbank.banking_api.dto.BranchDTO;
import com.yourbank.banking_api.model.Account;
import com.yourbank.banking_api.model.Branch;
import com.yourbank.banking_api.repository.AccountRepository;
import com.yourbank.banking_api.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;
    private final AccountRepository accountRepository;


    //creating a branch
    public BranchDTO createBranch(BranchDTO branchDTO) {
        Branch branch = Branch.builder()
                .name(branchDTO.name())
                .code(branchDTO.code())
                .address(branchDTO.address())
                .phone(branchDTO.phone())
                .createdAt(LocalDateTime.now())
                .build();

        Branch savedBranch = branchRepository.save(branch);
        return toDTO(savedBranch);
    }

    //getting a branch detail with specific branch id
    public BranchDTO getBranchById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        return toDTO(branch);
    }

    private BranchDTO toDTO(Branch branch) {
        return BranchDTO.builder()
                .id(branch.getId())
                .name(branch.getName())
                .code(branch.getCode())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .build();
    }

    //getting all the account under a branch with specific branch id
    public List<AccountRequestDTO> getAccountsByBranch(Long branchId) {

            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new RuntimeException("Branch not found"));

            List<Account> accounts = accountRepository.findByBranch(branch);

            return accounts.stream()
                    .map(a -> AccountRequestDTO.builder()
                            .id(a.getId())
                            .accountNumber(a.getAccountNumber())
                            .balance(a.getBalance())
                            .currency(a.getCurrency())
                            .customerId(a.getCustomer().getId())
                            .customerName(a.getCustomer().getFirstName() + " " + a.getCustomer().getLastName())
                            .build())
                    .collect(Collectors.toList());

    }
}
