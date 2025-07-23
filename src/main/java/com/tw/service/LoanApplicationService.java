package com.tw.service;


import com.tw.dto.CustomerLoanRequestDto;
import com.tw.dto.CustomerLoanResponseDto;
import com.tw.dto.request.CustomerLoanAppBlobDto;
import org.springframework.http.ResponseEntity;
import com.tw.dto.request.SignUpDto;

public interface LoanApplicationService {
     String submitApplication(Long userId, CustomerLoanRequestDto requestDto);
     CustomerLoanResponseDto getApplicationById(Long userId, Long applicationId);
     String getApplicationStatusById(Long userId, Long applicationId);
     Boolean changeApplicationStatusById(Long userId, Long applicationId, String status);
     Boolean deleteApplicationById(Long userId, Long applicationId);
}
