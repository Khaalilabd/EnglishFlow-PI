package com.englishflow.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedSessionAccessException extends RuntimeException {
    
    public UnauthorizedSessionAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedSessionAccessException() {
        super("You are not authorized to access this session.");
    }
}
