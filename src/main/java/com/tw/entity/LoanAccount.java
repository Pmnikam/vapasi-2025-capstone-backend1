package com.tw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="accont_details")
public class LoanAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accontNo;

    private double ammountDispersed;

    @OneToOne
    @JoinColumn(name = "application_id")
    private LoanApplication loanApplication;
}
