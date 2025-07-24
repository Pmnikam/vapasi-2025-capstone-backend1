package com.tw.controller;

import com.tw.dto.CustomerLoanInfoDto;
import com.tw.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<CustomerLoanInfoDto>> getAllCustomerLoanInfo() {
        return ResponseEntity.ok(adminService.getAllCustomerLoanInfo());
    }

    @PutMapping("/users/{customerId}/loan/{loanId}")
    public ResponseEntity<String> processLoanDecision(
            @PathVariable Long customerId,
            @PathVariable Long loanId,
            @RequestParam String action) {
        String message = adminService.processLoanDecision(customerId, loanId, action);
        return ResponseEntity.ok(message);
    }

}
