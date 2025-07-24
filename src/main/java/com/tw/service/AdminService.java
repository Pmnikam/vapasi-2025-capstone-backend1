package com.tw.service;

import com.tw.dto.CustomerLoanInfoDto;

import java.util.List;

public interface AdminService {


    List<CustomerLoanInfoDto> getAllCustomerLoanInfo();

    String processLoanDecision(Long customerId, Long loanId, String action);
}
