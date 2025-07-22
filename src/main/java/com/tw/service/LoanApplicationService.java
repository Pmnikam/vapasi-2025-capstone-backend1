package com.tw.service;

import com.tw.dto.LoanApplicationRequestDto;
import com.tw.dto.LoanApplicationResponseDto;

public interface LoanApplicationService {
    LoanApplicationResponseDto createApplication(LoanApplicationRequestDto dto);
    LoanApplicationResponseDto getApplicationById(long id);}
