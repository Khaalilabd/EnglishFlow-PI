package com.englishflow.gamification.entity;

public enum EnglishLevel {
    A1("Beginner", "🌱", "Seedling"),
    A2("Elementary", "🌿", "Sprout"),
    B1("Intermediate", "🌳", "Growing Tree"),
    B2("Upper Intermediate", "🦁", "Young Lion"),
    C1("Advanced", "👑", "Jungle King"),
    C2("Proficient", "🏆", "Master of the Jungle");
    
    private final String description;
    private final String icon;
    private final String badgeName;
    
    EnglishLevel(String description, String icon, String badgeName) {
        this.description = description;
        this.icon = icon;
        this.badgeName = badgeName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public String getBadgeName() {
        return badgeName;
    }
    
    public String getFullName() {
        return this.name() + " - " + description;
    }
}
