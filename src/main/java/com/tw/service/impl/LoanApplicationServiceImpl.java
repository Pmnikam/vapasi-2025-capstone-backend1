package com.tw.service.impl;

import com.tw.dto.LoanApplicationRequestDto;
import com.tw.dto.LoanApplicationResponseDto;
import com.tw.entity.CustomerProfile;
import com.tw.entity.LoanApplication;
import com.tw.repository.CustomerProfileRepository;
import com.tw.repository.LoanApplicationRepository;
import com.tw.service.LoanApplicationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanApplicationServiceImpl implements LoanApplicationService {
    @Autowired
    private CustomerProfileRepository customerProfileRepository;
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Override
    public LoanApplicationResponseDto createApplication(LoanApplicationRequestDto dto) {
        CustomerProfile profile=customerProfileRepository.findById(dto.getProfileId()).orElseThrow(()->new EntityNotFoundException("Customer Profile Not Found"));
        LoanApplication loanApplication=LoanApplication.builder()
                .applicationId(dto.getApplicationId())
                .loanAmount(dto.getLoanAmount())
                .loanStatus(dto.getLoanStatus())
                .loanTenure(dto.getLoanTenure())
                .monthlyIncome(dto.getMonthlyIncome())
                .propertyLocation(dto.getPropertyLocation())
                .propertyName(dto.getPropertyName())
                .estimatedCost(dto.getEstimatedCost())
                .customerProfile(profile)
                .build();
        return mapToResponse(loanApplicationRepository.save(loanApplication));
    }

    @Override
    public LoanApplicationResponseDto getApplicationById(long id) {
        LoanApplication loanApplication=loanApplicationRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Loan Application Not Found"));
        return mapToResponse(loanApplication);
    }

    private LoanApplicationResponseDto mapToResponse(LoanApplication loanApplication) {
        return LoanApplicationResponseDto.builder()
                .id(loanApplication.getId())
                .applicationId(loanApplication.getApplicationId())
                .loanAmount(loanApplication.getLoanAmount())
                .loanStatus(loanApplication.getLoanStatus())
                .loanTenure(loanApplication.getLoanTenure())
                .monthlyIncome(loanApplication.getMonthlyIncome())
                .propertyLocation(loanApplication.getPropertyLocation())
                .propertyName(loanApplication.getPropertyName())
                .estimatedCost(loanApplication.getEstimatedCost())
                .profileId(loanApplication.getCustomerProfile().getPid())
                .build();
    }
}
