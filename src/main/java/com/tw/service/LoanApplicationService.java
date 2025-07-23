package com.tw.service;


import com.tw.dto.CustomerLoanRequestDto;
import com.tw.dto.CustomerLoanResponseDto;

public interface LoanApplicationService {
     String submitApplication(Long userId, CustomerLoanRequestDto requestDto);
     CustomerLoanResponseDto getApplicationById(Long userId, Long applicationId);
     String getApplicationStatusById(Long userId, Long applicationId);
     Boolean changeApplicationStatusById(Long userId, Long applicationId, String status);
     Boolean deleteApplicationById(Long userId, Long applicationId);
}
