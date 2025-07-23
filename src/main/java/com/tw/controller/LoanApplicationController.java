package com.tw.controller;

import com.tw.dto.CustomerLoanRequestDto;
import com.tw.dto.CustomerLoanResponseDto;
import com.tw.service.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
public class LoanApplicationController {

    @Autowired
    LoanApplicationService loanApplicationService;

    @PostMapping(value = "/apply", consumes = "application/json")
    public ResponseEntity<String> submitApplication(
            @PathVariable Long userId,
            @RequestBody CustomerLoanRequestDto requestDto) {

        String applicationId = loanApplicationService.submitApplication(userId, requestDto);
        return new ResponseEntity<>(applicationId, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{applicationId}", produces = "application/json")
    public ResponseEntity<CustomerLoanResponseDto> getApplicationById(
            @PathVariable Long userId,
            @PathVariable Long applicationId) {

        CustomerLoanResponseDto responseDto = loanApplicationService.getApplicationById(userId, applicationId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
