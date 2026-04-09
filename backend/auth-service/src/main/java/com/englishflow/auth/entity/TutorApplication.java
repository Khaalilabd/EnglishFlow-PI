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
@Table(name = "tutor_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Step 1: Personal Information
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    private String cin;
    private String dateOfBirth;
    private String address;
    private String city;
    private String postalCode;
    private String nationality;

    // Step 2: Qualifications
    @Column(length = 2000)
    private String education; // JSON array of degrees

    @Column(length = 2000)
    private String certifications; // JSON array of certifications

    @Column(length = 2000)
    private String workExperience; // JSON array of work history

    private Integer yearsOfExperience;

    @Column(length = 100)
    private String englishLevel; // A1, A2, B1, B2, C1, C2

    @Column(length = 500)
    private String specializations; // Teaching specializations (TOEFL, IELTS, Business English, etc.)

    // Step 3: Presentation
    @Column(length = 2000)
    private String motivationLetter;

    @Column(length = 1000)
    private String teachingPhilosophy;

    @Column(length = 500)
    private String availability; // Available hours/days

    // Step 4: Skills Test (completed later)
    private Integer testScore;
    private LocalDateTime testCompletedAt;

    // Terms and Conditions
    @Column(nullable = false)
    @Builder.Default
    private Boolean termsAccepted = false;
    
    private LocalDateTime termsAcceptedAt;

    // Application Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.DRAFT;

    // Current step in the application process (1-4)
    @Column(nullable = false)
    @Builder.Default
    private Integer currentStep = 1;

    // Scoring by recruiter
    private Integer qualificationScore; // 0-100
    private Integer presentationScore; // 0-100
    private Integer overallScore; // 0-100

    // Interview scheduling
    private LocalDateTime interviewScheduledAt;
    private String interviewMeetingLink;
    private String interviewNotes;

    // Decision
    private Long reviewedBy; // Admin/Recruiter ID
    private LocalDateTime reviewedAt;
    private String rejectionReason;

    // Timestamps
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime submittedAt;

    // Documents relationship
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ApplicationDocument> documents = new ArrayList<>();

    // Notes relationship
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ApplicationNote> notes = new ArrayList<>();

    // Status history relationship
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ApplicationStatusHistory> statusHistory = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ApplicationStatus {
        DRAFT,              // Application started but not submitted
        SUBMITTED,          // Application submitted, awaiting review
        UNDER_REVIEW,       // Being reviewed by recruiter
        INTERVIEW_SCHEDULED,// Interview scheduled
        TEST_PENDING,       // Waiting for skills test completion
        TEST_COMPLETED,     // Skills test completed
        ACCEPTED,           // Application accepted
        REJECTED,           // Application rejected
        WITHDRAWN           // Applicant withdrew
    }
}
