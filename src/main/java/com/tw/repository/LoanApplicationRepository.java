package com.tw.repository;

import com.tw.entity.LoanApplication;
import com.tw.projection.CustomerLoanInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByCustomerProfile_LoginAccount_LoginId(Long userId);

    @Query("SELECT ua.loginId AS loginId, ua.name AS name, la.loanAmount AS loanAmount, la.loanStatus AS loanStatus " +
            "FROM LoanApplication la " +
            "JOIN la.customerProfile cp " +
            "JOIN cp.loginAccount ua " +
            "WHERE ua.role = 'CUSTOMER'")
    List<CustomerLoanInfo> fetchAllCustomerLoanInfo();

    @Query("SELECT ua.loginId AS loginId, ua.name AS name, la.loanAmount AS loanAmount, la.loanStatus AS loanStatus " +
            "FROM LoanApplication la " +
            "JOIN la.customerProfile cp " +
            "JOIN cp.loginAccount ua " +
            "WHERE ua.role = 'CUSTOMER' AND ua.loginId = :loginId")
    List<CustomerLoanInfo> fetchCustomerLoanInfoById(@Param("loginId") Long loginId);


}
