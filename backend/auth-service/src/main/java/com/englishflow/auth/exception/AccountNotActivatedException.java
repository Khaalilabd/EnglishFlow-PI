package com.englishflow.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccountNotActivatedException extends RuntimeException {
    
    public AccountNotActivatedException(String email) {
        super(String.format("Account not activated for email: %s. Please check your email for activation link.", email));
    }
    
    public AccountNotActivatedException(String message, String email) {
        super(message);
    }
}
