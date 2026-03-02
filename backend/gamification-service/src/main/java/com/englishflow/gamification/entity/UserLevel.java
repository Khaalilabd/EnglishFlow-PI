package com.englishflow.gamification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_levels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLevel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    private EnglishLevel assessmentLevel; // Niveau d'évaluation (non certifié) - NULL si pas encore évalué
    
    @Enumerated(EnumType.STRING)
    private EnglishLevel certifiedLevel; // Niveau certifié (après examen payant)
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean hasCompletedAssessment = false; // True si l'utilisateur a passé le test d'évaluation
    
    @Column
    private LocalDateTime assessmentCompletedAt; // Date du test d'évaluation
    
    @Column
    private LocalDateTime certificationDate; // Date de certification
    
    @Column(nullable = false)
    @Builder.Default
    private Integer currentXP = 0;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer totalXP = 0;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer jungleCoins = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LoyaltyTier loyaltyTier = LoyaltyTier.BRONZE;
    
    @Column(nullable = false)
    @Builder.Default
    private Double totalSpent = 0.0;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer consecutiveDays = 0;
    
    @Column
    private LocalDateTime lastActivityDate;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Méthodes utilitaires
    public void addXP(int xp) {
        this.currentXP += xp;
        this.totalXP += xp;
        checkLevelUp();
    }
    
    public void addCoins(int coins) {
        this.jungleCoins += coins;
    }
    
    public void spendCoins(int coins) {
        if (this.jungleCoins >= coins) {
            this.jungleCoins -= coins;
        }
    }
    
    public void addSpending(double amount) {
        this.totalSpent += amount;
        updateLoyaltyTier();
    }
    
    private void checkLevelUp() {
        // Ne pas calculer de level up si pas encore évalué
        if (assessmentLevel == null) return;
        
        EnglishLevel newLevel = calculateLevelFromXP(this.totalXP);
        if (newLevel != this.assessmentLevel) {
            this.assessmentLevel = newLevel;
            this.currentXP = this.totalXP - getXPRequiredForLevel(newLevel);
        }
    }
    
    private void updateLoyaltyTier() {
        if (totalSpent >= 3000) {
            loyaltyTier = LoyaltyTier.PLATINUM;
        } else if (totalSpent >= 1500) {
            loyaltyTier = LoyaltyTier.GOLD;
        } else if (totalSpent >= 500) {
            loyaltyTier = LoyaltyTier.SILVER;
        } else {
            loyaltyTier = LoyaltyTier.BRONZE;
        }
    }
    
    private EnglishLevel calculateLevelFromXP(int xp) {
        if (xp >= 12000) return EnglishLevel.C2;
        if (xp >= 8000) return EnglishLevel.C1;
        if (xp >= 5000) return EnglishLevel.B2;
        if (xp >= 2500) return EnglishLevel.B1;
        if (xp >= 1000) return EnglishLevel.A2;
        return EnglishLevel.A1;
    }
    
    private int getXPRequiredForLevel(EnglishLevel level) {
        return switch (level) {
            case A1 -> 0;
            case A2 -> 1000;
            case B1 -> 2500;
            case B2 -> 5000;
            case C1 -> 8000;
            case C2 -> 12000;
        };
    }
    
    public int getXPForNextLevel() {
        EnglishLevel nextLevel = getNextLevel();
        if (nextLevel == null) return 0;
        return getXPRequiredForLevel(nextLevel) - this.totalXP;
    }
    
    public EnglishLevel getNextLevel() {
        if (assessmentLevel == null) return EnglishLevel.A1; // Premier niveau par défaut
        
        return switch (assessmentLevel) {
            case A1 -> EnglishLevel.A2;
            case A2 -> EnglishLevel.B1;
            case B1 -> EnglishLevel.B2;
            case B2 -> EnglishLevel.C1;
            case C1 -> EnglishLevel.C2;
            case C2 -> null;
        };
    }
    
    public int getProgressPercentage() {
        if (assessmentLevel == null) return 0; // Pas encore évalué
        
        EnglishLevel nextLevel = getNextLevel();
        if (nextLevel == null) return 100;
        
        int currentLevelXP = getXPRequiredForLevel(assessmentLevel);
        int nextLevelXP = getXPRequiredForLevel(nextLevel);
        int xpInCurrentLevel = totalXP - currentLevelXP;
        int xpNeededForNextLevel = nextLevelXP - currentLevelXP;
        
        return (int) ((xpInCurrentLevel * 100.0) / xpNeededForNextLevel);
    }
}
