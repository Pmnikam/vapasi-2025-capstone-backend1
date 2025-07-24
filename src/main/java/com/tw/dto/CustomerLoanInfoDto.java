package com.tw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerLoanInfoDto {

    private Long loginId;
    private String name;
    private String email;
    private Long customerId;
    private Long applicationNo;
    private LocalDate dob;
    private String mobileNo;
    private String address;
    private String aadharNo;
    private String panNo;
    private Double loanAmount;
    private Double monthlyIncome;
    private String propertyName;
    private String location;
    private Double estimatedCost;
    private String documentSubmitted;
    private String status;
    Double tenure;
    Double emi;

}
