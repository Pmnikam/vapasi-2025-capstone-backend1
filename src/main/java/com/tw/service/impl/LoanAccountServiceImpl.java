package com.tw.service.impl;

import com.tw.dto.LoanAccountRequestDto;
import com.tw.dto.LoanAccountResponseDto;
import com.tw.entity.LoanAccount;
import com.tw.entity.LoanApplication;
import com.tw.repository.LoanAccountRepository;
import com.tw.repository.LoanApplicationRepository;
import com.tw.service.LoanAccountService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanAccountServiceImpl implements LoanAccountService {
    @Autowired
    private LoanAccountRepository accountRepo;
    @Autowired
    private LoanApplicationRepository loanRepo;

    @Override
    public LoanAccountResponseDto createAccount(LoanAccountRequestDto dto) {
        LoanApplication loan = loanRepo.findById(dto.getApplicationId())
                .orElseThrow(() -> new EntityNotFoundException("Loan Application Not Found"));

        LoanAccount account = LoanAccount.builder()
                .ammountDispersed(dto.getAmountDispersed())
                .loanApplication(loan)
                .build();
        return mapToResponse(accountRepo.save(account));
    }


    @Override
    public LoanAccountResponseDto getLoanAccountById(long id) {
        LoanAccount acc = accountRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return mapToResponse(acc);
    }

    private LoanAccountResponseDto mapToResponse(LoanAccount account) {
        return LoanAccountResponseDto.builder()
                .accountNo(account.getAccontNo())
                .amountDispersed(account.getAmmountDispersed())
                .applicationId(account.getLoanApplication().getId())
                .build();
    }
}
