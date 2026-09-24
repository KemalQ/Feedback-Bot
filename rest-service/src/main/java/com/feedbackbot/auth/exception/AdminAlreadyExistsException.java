package com.feedbackbot.auth.exception;

public class AdminAlreadyExistsException extends RuntimeException {
    public AdminAlreadyExistsException(String message) {
        super(message);
    }
}
