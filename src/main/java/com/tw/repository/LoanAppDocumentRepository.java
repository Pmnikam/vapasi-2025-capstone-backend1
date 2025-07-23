package com.tw.repository;

import com.tw.entity.LoanAppDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanAppDocumentRepository extends JpaRepository<LoanAppDocument, Long> {
}
