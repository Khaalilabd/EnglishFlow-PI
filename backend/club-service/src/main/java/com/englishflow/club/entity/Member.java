package com.englishflow.club.entity;

import com.englishflow.club.enums.RankType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "members", 
    indexes = {
        @Index(name = "idx_member_club_id", columnList = "club_id"),
        @Index(name = "idx_member_user_id", columnList = "userId"),
        @Index(name = "idx_member_rank", columnList = "rank"),
        @Index(name = "idx_member_club_user", columnList = "club_id,userId", unique = true)
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RankType rank;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;
    
    @Column(nullable = false)
    private Long userId; // Reference to User from auth-service
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (rank == null) {
            rank = RankType.MEMBER;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
