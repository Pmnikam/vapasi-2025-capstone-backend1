package com.tw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanApplicationRequestDto {

    @NotBlank(message = "Date of birth is required")
    private String dob;

    /*Customer Profile*/
    @NotBlank(message = "Mobile Number is required")
    private String mobileNo;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Aadhar number is required")
    private String aadharNo;

    @NotBlank(message = "PAN card number is required")
    private String panNo;

    /*Loan Application*/
    @NotNull(message = "Loan amount is required")
    @Positive(message = "Loan amount must be positive")
    private Double loanAmount;

    @NotNull(message = "Monthly income is required")
    @Positive(message = "Monthly income must be positive")
    private Double monthlyIncome;

    @NotBlank(message = "Property name is required")
    private String propertyName;

    @NotBlank(message = "Property location is required")
    private String location;

    @NotNull(message = "Estimated cost is required")
    @Positive(message = "Estimated cost must be positive")
    private Double estimatedCost;

    /*Identity Document*/
    @NotBlank(message = "Document Type cannot be null")
    private String documentType;

    @NotNull(message = "Tenure is required")
    @Positive(message = "Tenure must be positive")
    private Double tenure;

    @NotNull(message = "EMI is required")
    @Positive(message = "EMI must be positive")
    private Double emi;
}
