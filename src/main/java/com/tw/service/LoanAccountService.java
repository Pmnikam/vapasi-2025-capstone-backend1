package com.tw.service;

import com.tw.dto.LoanAccountRequestDto;
import com.tw.dto.LoanAccountResponseDto;
import com.tw.dto.LoanApplicationResponseDto;

public interface LoanAccountService {
    LoanAccountResponseDto createAccount(LoanAccountRequestDto dto);
    LoanAccountResponseDto getLoanAccountById(long id);

}
