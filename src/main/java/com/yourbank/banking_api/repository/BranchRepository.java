package com.yourbank.banking_api.repository;



import com.yourbank.banking_api.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
}
