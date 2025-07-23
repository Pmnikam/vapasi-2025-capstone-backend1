package com.tw.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerLoanRequestDto {

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

    @NotBlank(message = "Monthly income is required")
    @Positive(message = "Monthly income must be positive")
    private Double monthlyIncome;

    @NotBlank(message = "Property name is required")
    private String propertyName;

    @NotBlank(message = "Property location is required")
    private String location;

    @NotBlank(message = "Estimated cost is required")
    private String estimatedCost;

    /*Identity Document*/
    @NotBlank(message = "Document Type cannot be null")
    private String documentType;

    @NotNull(message = "Document file is required")
    private MultipartFile fileData;
}
