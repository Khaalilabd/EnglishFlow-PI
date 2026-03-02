package com.englishflow.gamification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code; // Ex: LEVEL_A1, PACK_COLLECTOR, STREAK_7
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private String icon; // Emoji ou URL d'image
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BadgeType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BadgeRarity rarity;
    
    @Column(length = 1000)
    private String criteria; // JSON avec les critères d'obtention
    
    @Column(nullable = false)
    @Builder.Default
    private Integer coinsReward = 0;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
