package com.tw.exception;

public class FailedToReadDocument extends RuntimeException {
    public FailedToReadDocument(String message) {
        super(message);
    }
}
