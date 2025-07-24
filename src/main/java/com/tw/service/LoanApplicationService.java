package com.tw.service;


import com.tw.dto.LoanAppStatusChangeRequestDto;
import com.tw.dto.LoanAppStatusChangeResponseDto;
import com.tw.dto.LoanApplicationRequestDto;
import com.tw.dto.LoanApplicationResponseDto;

public interface LoanApplicationService {
     Long submitApplication(Long userId, LoanApplicationRequestDto requestDto);
     LoanApplicationResponseDto getApplicationById(Long userId, Long applicationId);
     String getApplicationStatusById(Long userId, Long applicationId);
     LoanAppStatusChangeResponseDto changeApplicationStatusById(Long userId, Long applicationId, LoanAppStatusChangeRequestDto requestDto);
     Boolean deleteApplicationById(Long userId, Long applicationId);
}
