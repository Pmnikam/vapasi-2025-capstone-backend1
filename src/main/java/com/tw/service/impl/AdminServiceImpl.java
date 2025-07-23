package com.tw.service.impl;
import com.tw.projection.CustomerLoanInfo;
import com.tw.repository.LoanApplicationRepository;
import com.tw.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    LoanApplicationRepository loanApplicationRepository;

    @Override
    public List<CustomerLoanInfo> getAllCustomerLoanInfo() {
        return loanApplicationRepository.fetchAllCustomerLoanInfo();
    }

    @Override
    public List<CustomerLoanInfo> getCustomerLoanInfoById(Long loginId) {
        return loanApplicationRepository.fetchCustomerLoanInfoById(loginId);
    }
}
