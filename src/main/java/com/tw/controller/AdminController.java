package com.tw.controller;

import com.tw.projection.CustomerLoanInfo;
import com.tw.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping()
public class AdminController {

    @Autowired
    AdminService adminService;

    @GetMapping("/users")
    public List<CustomerLoanInfo> getAllCustomerLoanInfo() {
        return adminService.getAllCustomerLoanInfo();
    }

    @GetMapping("/users/{id}")
    public List<CustomerLoanInfo> getCustomerLoanInfoById(@PathVariable Long id) {
        return adminService.getCustomerLoanInfoById(id);
    }

}
