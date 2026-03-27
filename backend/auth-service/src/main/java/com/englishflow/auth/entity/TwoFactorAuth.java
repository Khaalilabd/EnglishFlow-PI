package com.englishflow.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "two_factor_auth", indexes = {
    @Index(name = "idx_2fa_user", columnList = "user_id", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorAuth {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(nullable = false, length = 32)
    private String secret;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = false;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "two_factor_backup_codes", 
                     joinColumns = @JoinColumn(name = "two_factor_auth_id"))
    @Column(name = "backup_code", length = 10)
    @Builder.Default
    private List<String> backupCodes = new ArrayList<>();
    
    @Column(name = "enabled_at")
    private LocalDateTime enabledAt;
    
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
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
}
