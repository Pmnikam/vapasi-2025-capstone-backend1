package com.tw.controller;

import com.tw.projection.CustomerLoanInfo;
import com.tw.projection.LoanApplicationView;
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
    public ResponseEntity<List<CustomerLoanInfo>> getAllCustomerLoanInfo() {
        return ResponseEntity.ok(adminService.getAllCustomerLoanInfo());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<LoanApplicationView>> getCustomerLoanInfoById(@PathVariable Long id) {
        List<LoanApplicationView> loanInfoList = adminService.getCustomerLoanInfoById(id);
        return ResponseEntity.ok(loanInfoList);
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
