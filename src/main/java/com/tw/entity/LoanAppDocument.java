package com.tw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "loan_app_document")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanAppDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_id")
    private Long documentId;

    @Column(name = "doc_type", nullable = false)
    private String documentType;

    @Lob
    @Column(name = "file_data", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @OneToOne
    @JoinColumn(name = "app_id", nullable = false, unique = true)
    private LoanApplication loanApplication;

}
