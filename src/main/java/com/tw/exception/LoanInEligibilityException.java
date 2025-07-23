package com.tw.exception;

public class LoanInEligibilityException extends RuntimeException {
    public LoanInEligibilityException(String message) {
        super(message);
    }
}
