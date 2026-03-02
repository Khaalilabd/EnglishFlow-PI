package com.englishflow.club.exception;

public class ClubNotFoundException extends RuntimeException {
    public ClubNotFoundException(Integer id) {
        super("Club not found with id: " + id);
    }
    
    public ClubNotFoundException(String message) {
        super(message);
    }
}
