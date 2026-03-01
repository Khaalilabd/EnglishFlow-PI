package com.englishflow.gamification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_badges", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "badge_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBadge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isDisplayed = false; // Affiché sur le profil
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isNew = true; // Nouveau badge non vu
    
    @Column(nullable = false)
    private LocalDateTime earnedAt;
    
    @PrePersist
    protected void onCreate() {
        earnedAt = LocalDateTime.now();
    }
}
