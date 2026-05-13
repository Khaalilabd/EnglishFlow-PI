package com.englishflow.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité pour tracker les analytics des étudiants pour le ML
 */
@Entity
@Table(name = "student_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    // Données d'interaction
    @Column(nullable = false)
    @Builder.Default
    private Integer totalClicks = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalSessions = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer avgClicksPerSession = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer maxClicksInSession = 0;

    // Données d'évaluation
    @Column(nullable = false)
    @Builder.Default
    private Double avgScore = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double minScore = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double maxScore = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalAssessments = 0;

    @Column(nullable = false, name = "completed_tma")
    @Builder.Default
    private Integer completedTMA = 0; // Tutor Marked Assignments

    @Column(nullable = false, name = "completed_cma")
    @Builder.Default
    private Integer completedCMA = 0; // Computer Marked Assignments

    @Column(nullable = false)
    @Builder.Default
    private Integer completedExams = 0;

    // Données d'engagement
    @Column(nullable = false)
    @Builder.Default
    private Integer previousAttempts = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer studiedCredits = 0;

    @Column
    private LocalDateTime lastActivityAt;

    @Column
    private LocalDateTime firstRegistrationDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isUnregistered = false;

    // Tracking des leçons
    @Column(nullable = false)
    @Builder.Default
    private Integer totalLessonsOpened = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalTimeSpentMinutes = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer avgTimePerLesson = 0;

    @Column
    private LocalDateTime lastLessonOpenedAt;

    // Métadonnées
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (firstRegistrationDate == null) {
            firstRegistrationDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public void incrementClicks(int clicks) {
        this.totalClicks += clicks;
        this.lastActivityAt = LocalDateTime.now();
    }

    public void incrementSession() {
        this.totalSessions++;
        this.lastActivityAt = LocalDateTime.now();
        // Ne pas recalculer ici - sera fait dans le service après sauvegarde
    }
    
    public void recalculateAvgClicks() {
        if (this.totalSessions > 0) {
            this.avgClicksPerSession = this.totalClicks / this.totalSessions;
        }
    }

    public void updateMaxClicks(int clicks) {
        if (clicks > this.maxClicksInSession) {
            this.maxClicksInSession = clicks;
        }
    }

    public void addAssessmentScore(double score, String assessmentType) {
        this.totalAssessments++;
        
        // Mettre à jour les scores
        if (this.totalAssessments == 1) {
            this.avgScore = score;
            this.minScore = score;
            this.maxScore = score;
        } else {
            // Recalculer la moyenne
            this.avgScore = ((this.avgScore * (this.totalAssessments - 1)) + score) / this.totalAssessments;
            
            // Mettre à jour min/max
            if (score < this.minScore) {
                this.minScore = score;
            }
            if (score > this.maxScore) {
                this.maxScore = score;
            }
        }

        // Incrémenter le type d'évaluation
        switch (assessmentType.toUpperCase()) {
            case "TMA":
                this.completedTMA++;
                break;
            case "CMA":
                this.completedCMA++;
                break;
            case "EXAM":
                this.completedExams++;
                break;
        }

        this.lastActivityAt = LocalDateTime.now();
    }

    public void addCredits(int credits) {
        this.studiedCredits += credits;
    }

    public void incrementAttempts() {
        this.previousAttempts++;
    }

    public void trackLessonOpened() {
        this.totalLessonsOpened++;
        this.lastLessonOpenedAt = LocalDateTime.now();
        this.lastActivityAt = LocalDateTime.now();
    }

    public void addTimeSpent(int minutes) {
        this.totalTimeSpentMinutes += minutes;
        this.lastActivityAt = LocalDateTime.now();
        // Recalculer la moyenne du temps par leçon
        if (this.totalLessonsOpened > 0) {
            this.avgTimePerLesson = this.totalTimeSpentMinutes / this.totalLessonsOpened;
        }
    }
}
