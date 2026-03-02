package com.englishflow.gamification.entity;

public enum BadgeRarity {
    COMMON("⚪", "#9CA3AF"),
    RARE("🔵", "#3B82F6"),
    EPIC("🟣", "#8B5CF6"),
    LEGENDARY("🟡", "#F59E0B");
    
    private final String icon;
    private final String color;
    
    BadgeRarity(String icon, String color) {
        this.icon = icon;
        this.color = color;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public String getColor() {
        return color;
    }
}
