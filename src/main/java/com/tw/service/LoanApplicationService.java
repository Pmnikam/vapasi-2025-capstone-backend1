package com.tw.service;

import com.tw.dto.LoanApplicationDto;
import com.tw.entity.LoanApplication;

public interface LoanApplicationService {
    LoanApplication applyForLoan(LoanApplicationDto dto);
}
