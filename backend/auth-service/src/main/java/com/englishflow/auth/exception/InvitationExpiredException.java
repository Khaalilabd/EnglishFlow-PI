package com.englishflow.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class InvitationExpiredException extends RuntimeException {
    
    public InvitationExpiredException(String message) {
        super(message);
    }
    
    public InvitationExpiredException() {
        super("This invitation has expired. Please request a new invitation.");
    }
}
