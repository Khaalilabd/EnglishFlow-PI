package com.englishflow.gamification.dto;

import com.englishflow.gamification.entity.BadgeType;
import com.englishflow.gamification.entity.BadgeRarity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String icon;
    private BadgeType type;
    private BadgeRarity rarity;
    private String rarityIcon;
    private String rarityColor;
    private Integer coinsReward;
    private Boolean isEarned;
    private Boolean isDisplayed;
    private Boolean isNew;
    private LocalDateTime earnedAt;
}
