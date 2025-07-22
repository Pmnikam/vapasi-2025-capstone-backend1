package com.tw.repository;

import com.tw.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    Optional<LoanApplication> findByApplicationId(String applicationId);
}
