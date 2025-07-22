package com.tw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanApplicationDto {
    private String applicationId;
    private double monthlyIncome;
    private double loanAmount;
    private String loanStatus;
    private int loanTenure;
    private String propertyLocation;
    private String propertyName;
    private double estimatedCost;
    private String customerEmail;
}
