package com.englishflow.club.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(Integer id) {
        super("Member not found with id: " + id);
    }
    
    public MemberNotFoundException(String message) {
        super(message);
    }
}
