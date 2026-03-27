package com.englishflow.courses.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chapter_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long studentId; // Reference to User ID from auth-service
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;
    
    @Column(nullable = false)
    private Boolean isCompleted = false;
    
    @Column
    private LocalDateTime startedAt;
    
    @Column
    private LocalDateTime completedAt;
    
    @Column
    private LocalDateTime lastAccessedAt;
    
    @Column
    private Integer completedLessons = 0;
    
    @Column
    private Integer totalLessons = 0;
    
    @Column
    private Double progressPercentage = 0.0; // Calculated from lesson completions
    
    @PrePersist
    protected void onCreate() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
        lastAccessedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastAccessedAt = LocalDateTime.now();
        if (isCompleted && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }
}