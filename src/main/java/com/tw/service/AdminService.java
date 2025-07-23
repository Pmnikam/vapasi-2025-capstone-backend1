package com.tw.service;

import com.tw.projection.CustomerLoanInfo;

import java.util.List;

public interface AdminService {


    List<CustomerLoanInfo> getAllCustomerLoanInfo();
    List<CustomerLoanInfo> getCustomerLoanInfoById(Long loginId);

}
