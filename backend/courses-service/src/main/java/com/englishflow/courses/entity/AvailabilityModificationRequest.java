package com.englishflow.courses.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "availability_modification_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityModificationRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tutor_id", nullable = false)
    private Long tutorId;
    
    @Column(name = "tutor_name", nullable = false)
    private String tutorName;
    
    @Column(name = "tutor_email")
    private String tutorEmail;
    
    @Column(name = "reason", nullable = false, length = 1000)
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;
    
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "reviewer_id")
    private Long reviewerId;
    
    @Column(name = "reviewer_name")
    private String reviewerName;
    
    @Column(name = "review_comment", length = 1000)
    private String reviewComment;
    
    @Column(name = "proposed_availability", columnDefinition = "TEXT")
    private String proposedAvailability;
    
    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
        if (status == null) {
            status = RequestStatus.PENDING;
        }
    }
    
    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
