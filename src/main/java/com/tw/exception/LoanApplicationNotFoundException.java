package com.tw.exception;

public class LoanApplicationNotFoundException extends RuntimeException {
    public LoanApplicationNotFoundException(Long applicationId) {
        super("Application with id " + applicationId + " not found");
    }
}
