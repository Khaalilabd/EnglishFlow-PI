package com.englishflow.auth.dto;

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
    private String assessmentLevel;
    private String assessmentLevelIcon;
    private String assessmentLevelName;
    private String certifiedLevel;
    private String certifiedLevelIcon;
    private String certifiedLevelName;
    private Integer currentXP;
    private Integer totalXP;
    private Integer xpForNextLevel;
    private Integer progressPercentage;
    private String nextLevel;
    private Integer jungleCoins;
    private String loyaltyTier;
    private String loyaltyTierIcon;
    private Integer loyaltyDiscount;
    private Double totalSpent;
    private Integer consecutiveDays;
    private Integer rank;
}
