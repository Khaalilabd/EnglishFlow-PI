package com.englishflow.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité pour stocker localement les rendez-vous d'entretien
 * Sert de backup et permet de gérer les disponibilités même si Google Calendar est indisponible
 */
@Entity
@Table(name = "interview_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private TutorApplication application;

    @Column(name = "interviewer_id", nullable = false)
    private Long interviewerId; // L'admin qui fait l'entretien

    @Column(name = "scheduled_start", nullable = false)
    private LocalDateTime scheduledStart;

    @Column(name = "scheduled_end", nullable = false)
    private LocalDateTime scheduledEnd;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "meeting_link", nullable = false, length = 500)
    private String meetingLink;

    @Column(name = "google_event_id", length = 255)
    private String googleEventId; // ID de l'événement dans Google Calendar

    @Column(name = "meeting_platform", length = 50)
    private String meetingPlatform; // GOOGLE_MEET, ZOOM, etc.

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ScheduleStatus status = ScheduleStatus.SCHEDULED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    public enum ScheduleStatus {
        SCHEDULED,      // Rendez-vous programmé
        COMPLETED,      // Entretien terminé
        CANCELLED,      // Annulé
        NO_SHOW         // Candidat absent
    }

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
