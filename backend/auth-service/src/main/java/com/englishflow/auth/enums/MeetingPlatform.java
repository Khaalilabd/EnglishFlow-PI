package com.englishflow.auth.enums;

public enum MeetingPlatform {
    GOOGLE_MEET("Google Meet"),
    ZOOM("Zoom"),
    MICROSOFT_TEAMS("Microsoft Teams"),
    MANUAL("Manual Link"); // Pour permettre l'option manuelle si nécessaire

    private final String displayName;

    MeetingPlatform(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
