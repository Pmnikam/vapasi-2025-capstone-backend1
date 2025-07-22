package com.tw.service.impl;

import com.tw.dto.LoanApplicationDto;
import com.tw.entity.CustomerProfile;
import com.tw.entity.LoanApplication;
import com.tw.repository.CustomerProfileRepository;
import com.tw.repository.LoanApplicationRepository;
import com.tw.service.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanApplicationServiceImpl implements LoanApplicationService {
    @Autowired
    private CustomerProfileRepository customerProfileRepository;
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Override
    public LoanApplication applyForLoan(LoanApplicationDto dto) {
        CustomerProfile profile = customerProfileRepository.findByEmail(dto.getCustomerEmail()).orElseThrow();
        LoanApplication loanApp = new LoanApplication();
        loanApp.setApplicationId(dto.getApplicationId());
        loanApp.setMonthlyIncome(dto.getMonthlyIncome());
        loanApp.setLoanAmount(dto.getLoanAmount());
        loanApp.setLoanTenure(dto.getLoanTenure());
        loanApp.setPropertyLocation(dto.getPropertyLocation());
        loanApp.setLoanStatus(dto.getPropertyName());
        loanApp.setEstimatedCost(dto.getEstimatedCost());
        loanApp.setCustomerProfile(profile);
        loanApp.setLoanStatus("Sent for verification");
        return loanApplicationRepository.save(loanApp);
    }
}
