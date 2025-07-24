package com.tw.service;

import com.tw.projection.CustomerLoanInfo;
import com.tw.projection.LoanApplicationView;

import java.util.List;

public interface AdminService {


    List<CustomerLoanInfo> getAllCustomerLoanInfo();
    List<LoanApplicationView> getCustomerLoanInfoById(Long loginId);

    String processLoanDecision(Long customerId, Long loanId, String action);
}
