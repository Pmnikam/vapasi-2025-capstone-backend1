package com.tw.service;


import com.tw.dto.LoanApplicationRequestDto;
import com.tw.dto.LoanApplicationResponseDto;

public interface LoanApplicationService {
     Long submitApplication(Long userId, LoanApplicationRequestDto requestDto);
     LoanApplicationResponseDto getApplicationById(Long userId, Long applicationId);
     String getApplicationStatusById(Long userId, Long applicationId);
     Boolean changeApplicationStatusById(Long userId, Long applicationId, String status);
     Boolean deleteApplicationById(Long userId, Long applicationId);
}
