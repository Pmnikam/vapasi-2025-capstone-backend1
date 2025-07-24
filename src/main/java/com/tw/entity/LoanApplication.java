    package com.tw.entity;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Entity
    @Data
    @Table(name = "loan_application")
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class LoanApplication {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)

        private Long applicationId;

        @Column(name = "loan_amount", nullable = false)
        private Double loanAmount;

        @Column(name = "loan_status", length = 50, nullable = false)
        private String loanStatus;

        @Column(name = "monthly_income", nullable = false)
        private Double monthlyIncome;

        @Column(name = "is_active",  nullable = false)
        private Boolean isActive;

        @Column(name = "property_name", nullable = false)
        private String propertyName;

        @Column(nullable = false)
        private String location;

        @Column(name = "estimated_cost", nullable = false)
        private Double estimatedCost;

        @ManyToOne
        @JoinColumn(name = "profile_id", nullable = false)
        private CustomerProfile customerProfile;

        @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL)
        private LoanAccount loanAccount;

        @Column(name="doc_type")
        private String documentType;

        @Column(name = "interest_rate", nullable = false)
        private Double interestRate;

        @Column(name = "tenure", nullable = false)
        private Double tenure;

        @Column(nullable = false)
        private Double emi;

        // @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL)
      //  private LoanAppDocument binaryDocument;

    }
