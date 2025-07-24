package com.tw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
    private Long pId;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(name = "mobile_no", nullable = false, length = 15)
    private String mobileNo;

    @Column(nullable = false)
    private String address;

    @Column(name = "aadhar_no", nullable = false, unique = true, length = 12)
    private String aadharNo;

    @Column(name = "pan_no", nullable = false, unique = true)
    private String panNo;

    @OneToOne
    @JoinColumn(name = "user_account_id", nullable = false, unique = true)
    private UserAccount loginAccount;

    @OneToMany(mappedBy = "customerProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LoanApplication> loanApplications;
}
