package com.tw.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Application ID is required")
    private String applicationId;

    private String loanStatus;

    @Min(value = 10000, message = "Loan amount must be at least 10000")
    private double loanAmount;

    private int loanTenure;

    @Min(value = 1000, message = "Monthly income must be at least 1000")
    private double monthlyIncome;

    @NotBlank(message = "Property location is required")
    private String propertyLocation;

    @NotBlank(message = "Property name is required")
    private String propertyName;

    @Min(value = 10000, message = "Estimated cost must be at least 10000")
    private double estimatedCost;

    @OneToOne
    @JoinColumn(name = "profile_id")
    private CustomerProfile customerProfile;

}
