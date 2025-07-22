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
    private long id;
    private String applicationId;
    private String loanStatus;
    private double loanAmount;
    private int loanTenure;
    private double monthlyIncome;
    private String propertyLocation;
    private String propertyName;
    private double estimatedCost;
    private long profileId;
}
