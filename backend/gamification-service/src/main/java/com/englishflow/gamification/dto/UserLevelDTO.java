package com.englishflow.gamification.dto;

import com.englishflow.gamification.entity.EnglishLevel;
import com.englishflow.gamification.entity.LoyaltyTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLevelDTO {
    private Long userId;
    private EnglishLevel assessmentLevel;
    private String assessmentLevelIcon;
    private String assessmentLevelName;
    private Boolean hasCompletedAssessment;
    private String assessmentCompletedAt;
    private EnglishLevel certifiedLevel;
    private String certifiedLevelIcon;
    private String certifiedLevelName;
    private String certificationDate;
    private Integer currentXP;
    private Integer totalXP;
    private Integer xpForNextLevel;
    private Integer progressPercentage;
    private EnglishLevel nextLevel;
    private Integer jungleCoins;
    private LoyaltyTier loyaltyTier;
    private String loyaltyTierIcon;
    private Integer loyaltyDiscount;
    private Double totalSpent;
    private Integer consecutiveDays;
    private Integer rank; // Position dans le classement
}
