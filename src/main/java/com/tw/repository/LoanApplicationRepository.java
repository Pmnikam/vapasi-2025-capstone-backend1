package com.tw.repository;

import com.tw.entity.LoanApplication;
import com.tw.dto.CustomerLoanInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByCustomerProfile_LoginAccount_LoginId(Long userId);

    @Query("SELECT new com.tw.dto.CustomerLoanInfoDto(" +
            "ua.loginId, ua.name, ua.email, cp.pId, la.applicationId, cp.dob, cp.mobileNo, " +
            "cp.address, cp.aadharNo, cp.panNo, la.loanAmount, la.monthlyIncome, la.propertyName, " +
            "la.location, la.estimatedCost, la.documentType, la.loanStatus) " +
            "FROM LoanApplication la " +
            "JOIN la.customerProfile cp " +
            "JOIN cp.loginAccount ua " +
            "WHERE ua.role = 'CUSTOMER'")
    List<CustomerLoanInfoDto> fetchAllCustomerLoanApplications();


    Optional<LoanApplication> findByApplicationIdAndCustomerProfile_pId(Long loanId, Long customerId);

}
