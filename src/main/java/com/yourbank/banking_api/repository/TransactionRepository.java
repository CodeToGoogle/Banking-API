package com.yourbank.banking_api.repository;

import com.yourbank.banking_api.model.Branch;
import com.yourbank.banking_api.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByAccount_BranchAndCreatedAtBetween(
            Branch branch,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.account.branch.id = :branchId
      AND t.createdAt BETWEEN :startDate AND :endDate
      AND (:type IS NULL OR t.type = :type)
""")
    Page<Transaction> findBranchTransactions(
            @Param("branchId") Long branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("type") com.yourbank.banking_api.model.TransactionType type,
            Pageable pageable
    );

}