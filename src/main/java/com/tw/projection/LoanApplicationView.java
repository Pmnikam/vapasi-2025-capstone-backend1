package com.tw.projection;

public interface LoanApplicationView {
    Long getApplicationNo();
    java.time.LocalDate getDob();
    String getMobileNo();
    String getAddress();
    String getAadharNo();
    String getPanNo();
    Double getLoanAmount();
    Double getMonthlyIncome();
    String getPropertyName();
    String getLocation();
    String getEstimatedCost();
    String getDocumentSubmitted();
    String getStatus();
}
