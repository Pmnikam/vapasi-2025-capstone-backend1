package com.tw.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String mail) {
        super("User with email " + mail + " not found");
    }

    public UserNotFoundException(Long userId) {  super("User with id " + userId + " not found"); }
}
