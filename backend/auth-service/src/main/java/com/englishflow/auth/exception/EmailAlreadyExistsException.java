package com.englishflow.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {
    
    public EmailAlreadyExistsException(String email) {
        super(String.format("Email already exists: %s", email));
    }
    
    public EmailAlreadyExistsException(String message, String email) {
        super(message);
    }
}
