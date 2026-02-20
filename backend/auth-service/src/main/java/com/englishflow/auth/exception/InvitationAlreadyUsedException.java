package com.englishflow.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvitationAlreadyUsedException extends RuntimeException {
    
    public InvitationAlreadyUsedException(String message) {
        super(message);
    }
    
    public InvitationAlreadyUsedException() {
        super("This invitation has already been used.");
    }
}
