package com.tw.exception;

public class LoanInactiveException  extends RuntimeException {
    public LoanInactiveException(String message) {
        super(message);
    }
}