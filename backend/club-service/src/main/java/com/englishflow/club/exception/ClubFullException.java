package com.englishflow.club.exception;

public class ClubFullException extends RuntimeException {
    public ClubFullException(Integer maxMembers) {
        super("Club is full. Maximum members: " + maxMembers);
    }
}
