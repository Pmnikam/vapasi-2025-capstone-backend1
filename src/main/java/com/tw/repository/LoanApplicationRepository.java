package com.tw.repository;

import com.tw.entity.LoanApplication;
import com.tw.projection.CustomerLoanInfo;
import com.tw.projection.LoanApplicationView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByCustomerProfile_LoginAccount_LoginId(Long userId);

    @Query("SELECT ua.loginId AS loginId, ua.name AS name, la.loanAmount AS loanAmount, la.loanStatus AS loanStatus " +
            "FROM LoanApplication la " +
            "JOIN la.customerProfile cp " +
            "JOIN cp.loginAccount ua " +
            "WHERE ua.role = 'CUSTOMER'")
    List<CustomerLoanInfo> fetchAllCustomerLoanInfo();

    @Query("SELECT la.applicationId AS applicationNo, cp.dob AS dob, cp.mobileNo AS mobileNo, " +
            "cp.address AS address, cp.aadharNo AS aadharNo, cp.panNo AS panNo, " +
            "la.loanAmount AS loanAmount, la.monthlyIncome AS monthlyIncome, " +
            "la.propertyName AS propertyName, la.location AS location, la.estimatedCost AS estimatedCost, " +
            "la.documentType AS documentSubmitted, la.loanStatus AS status " +
            "FROM LoanApplication la " +
            "JOIN la.customerProfile cp " +
            "JOIN cp.loginAccount ua " +
            "WHERE la.isActive = true AND ua.loginId = :userId")
    List<LoanApplicationView> findLoanApplicationsByUserId(@Param("userId") Long userId);

    Optional<LoanApplication> findByApplicationIdAndCustomerProfile_pId(Long loanId, Long customerId);

}
