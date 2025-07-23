//package com.tw.controller;
//
//import com.tw.entity.LoanApplication;
//import com.tw.service.AdminService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/loans")
//public class LoanApplicationController {
//
//    @Autowired
//    private AdminService loanService;
//
//    @PostMapping("/apply")
//    public ResponseEntity<LoanApplication> apply(@RequestBody LoanApplicationDto dto) {
//        return ResponseEntity.ok(loanService.applyForLoan(dto));
//    }
//}
