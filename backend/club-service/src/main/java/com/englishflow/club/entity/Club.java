package com.englishflow.club.entity;

import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.enums.ClubStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clubs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Club {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String objective;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClubCategory category;
    
    @Column(nullable = false)
    private Integer maxMembers;
    
    @Column(columnDefinition = "TEXT")
    private String image;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ClubStatus status = ClubStatus.PENDING;
    
    @Column(name = "created_by")
    private Integer createdBy; // User ID du student qui a créé la demande
    
    @Column(name = "reviewed_by")
    private Integer reviewedBy; // User ID de l'Academic Affairs Officer qui a traité
    
    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment; // Commentaire de l'officer
    
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Member> members = new ArrayList<>();
    
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();
    
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
    
    // Helper methods
    public int getCurrentMembersCount() {
        return members != null ? members.size() : 0;
    }
    
    public boolean isFull() {
        return getCurrentMembersCount() >= maxMembers;
    }
}
