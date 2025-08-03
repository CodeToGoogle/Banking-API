package com.yourbank.banking_api.repository;

import com.yourbank.banking_api.model.Account;
import com.yourbank.banking_api.model.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Page<Account> findByBranchId(Long branchId, Pageable pageable);

    List<Account> findByBranch(Branch branch);
}
