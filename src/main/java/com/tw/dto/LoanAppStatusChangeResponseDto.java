package com.tw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanAppStatusChangeResponseDto {
    private Long accountId;
    private Double loanAmount;
}