package com.englishflow.gamification.entity;

public enum LoyaltyTier {
    BRONZE("🥉", 5, 0),
    SILVER("🥈", 10, 500),
    GOLD("🥇", 15, 1500),
    PLATINUM("💎", 20, 3000);
    
    private final String icon;
    private final int discountPercentage;
    private final double minSpending;
    
    LoyaltyTier(String icon, int discountPercentage, double minSpending) {
        this.icon = icon;
        this.discountPercentage = discountPercentage;
        this.minSpending = minSpending;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public int getDiscountPercentage() {
        return discountPercentage;
    }
    
    public double getMinSpending() {
        return minSpending;
    }
    
    public String getDisplayName() {
        return icon + " " + this.name();
    }
}
