package com.tw.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private long pid;

    @NotBlank(message = "Date of birth is required")
    private String dob;

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Aadhar is required")
    @Column(name = "aadhar_no", nullable = false, unique = true, length = 12)
    private String aadharNo;

    @NotBlank(message = "PAN is required")
    @Column(name = "pan_no", nullable = false, unique = true)
    private String panNo;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    @OneToOne(mappedBy = "customerProfile", cascade = CascadeType.ALL)
    private LoanApplication loanApplication;
}
