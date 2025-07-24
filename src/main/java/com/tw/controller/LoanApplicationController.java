package com.tw.controller;

import com.tw.dto.LoanApplicationRequestDto;
import com.tw.dto.LoanApplicationResponseDto;
import com.tw.service.LoanApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/loan")
public class LoanApplicationController {
    @Autowired
    LoanApplicationService loanApplicationService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Long> submitApplication(
            @PathVariable Long userId,
            @Valid @RequestBody LoanApplicationRequestDto requestDto) {

        Long applicationId = loanApplicationService.submitApplication(userId, requestDto);
        return new ResponseEntity<>(applicationId, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{applicationId}", produces = "application/json")
    public ResponseEntity<LoanApplicationResponseDto> getApplicationById(
            @PathVariable Long userId,
            @PathVariable Long applicationId) {

        LoanApplicationResponseDto responseDto = loanApplicationService.getApplicationById(userId, applicationId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{applicationId}/status")
    public ResponseEntity<String> getApplicationStatusById(
            @PathVariable Long userId,
            @PathVariable Long applicationId) {

        String status = loanApplicationService.getApplicationStatusById(userId, applicationId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PutMapping("/{applicationId}/status")
    public ResponseEntity<Boolean> changeApplicationStatus(
            @PathVariable Long userId,
            @PathVariable Long applicationId,
            @RequestParam String status) {

        Boolean updated = loanApplicationService.changeApplicationStatusById(userId, applicationId, status);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Boolean> deleteApplication(
            @PathVariable Long userId,
            @PathVariable Long applicationId) {

        Boolean deleted = loanApplicationService.deleteApplicationById(userId, applicationId);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }
}
