package com.feedbackbot.exception;

public class InviteTokenNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InviteTokenNotFoundException(String message){
        super(message);
    }
}
