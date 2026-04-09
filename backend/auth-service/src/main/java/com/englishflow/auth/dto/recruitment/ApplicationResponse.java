package com.englishflow.auth.dto.recruitment;

import com.englishflow.auth.entity.TutorApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String cin;
    private String dateOfBirth;
    private String address;
    private String city;
    private String postalCode;
    private String nationality;

    private String education;
    private String certifications;
    private String workExperience;
    private Integer yearsOfExperience;
    private String englishLevel;
    private String specializations;

    private String motivationLetter;
    private String teachingPhilosophy;
    private String availability;

    private Integer testScore;
    private LocalDateTime testCompletedAt;

    private Boolean termsAccepted;
    private LocalDateTime termsAcceptedAt;

    private String status;
    private Integer currentStep;

    private Integer qualificationScore;
    private Integer presentationScore;
    private Integer overallScore;

    private LocalDateTime interviewScheduledAt;
    private String interviewMeetingLink;
    private String interviewNotes;

    private Long reviewedBy;
    private LocalDateTime reviewedAt;
    private String rejectionReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime submittedAt;

    private List<DocumentResponse> documents;
    private List<NoteResponse> notes;

    public static ApplicationResponse fromEntity(TutorApplication application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .firstName(application.getFirstName())
                .lastName(application.getLastName())
                .email(application.getEmail())
                .phone(application.getPhone())
                .cin(application.getCin())
                .dateOfBirth(application.getDateOfBirth())
                .address(application.getAddress())
                .city(application.getCity())
                .postalCode(application.getPostalCode())
                .nationality(application.getNationality())
                .education(application.getEducation())
                .certifications(application.getCertifications())
                .workExperience(application.getWorkExperience())
                .yearsOfExperience(application.getYearsOfExperience())
                .englishLevel(application.getEnglishLevel())
                .specializations(application.getSpecializations())
                .motivationLetter(application.getMotivationLetter())
                .teachingPhilosophy(application.getTeachingPhilosophy())
                .availability(application.getAvailability())
                .testScore(application.getTestScore())
                .testCompletedAt(application.getTestCompletedAt())
                .termsAccepted(application.getTermsAccepted())
                .termsAcceptedAt(application.getTermsAcceptedAt())
                .status(application.getStatus().name())
                .currentStep(application.getCurrentStep())
                .qualificationScore(application.getQualificationScore())
                .presentationScore(application.getPresentationScore())
                .overallScore(application.getOverallScore())
                .interviewScheduledAt(application.getInterviewScheduledAt())
                .interviewMeetingLink(application.getInterviewMeetingLink())
                .interviewNotes(application.getInterviewNotes())
                .reviewedBy(application.getReviewedBy())
                .reviewedAt(application.getReviewedAt())
                .rejectionReason(application.getRejectionReason())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .submittedAt(application.getSubmittedAt())
                .documents(application.getDocuments().stream()
                        .map(DocumentResponse::fromEntity)
                        .collect(Collectors.toList()))
                .notes(application.getNotes().stream()
                        .map(NoteResponse::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
