package com.englishflow.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RecaptchaVerificationException extends RuntimeException {
    
    public RecaptchaVerificationException(String message) {
        super(message);
    }
    
    public RecaptchaVerificationException() {
        super("reCAPTCHA verification failed. Please try again.");
    }
}
