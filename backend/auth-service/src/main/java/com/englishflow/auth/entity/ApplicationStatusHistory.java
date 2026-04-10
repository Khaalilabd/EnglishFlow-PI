package com.englishflow.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private TutorApplication application;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TutorApplication.ApplicationStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TutorApplication.ApplicationStatus toStatus;

    @Column(length = 500)
    private String comment;

    @Column(nullable = true)  // Nullable for applicant self-service actions
    private Long changedBy; // Admin/Recruiter ID (null for applicant actions)

    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}
