package com.tw.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationResponseDto {
    private Long applicationNo;
    private String dob;
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
    private Double interestRate;
    private Double tenure;
    private Double emi;
}
