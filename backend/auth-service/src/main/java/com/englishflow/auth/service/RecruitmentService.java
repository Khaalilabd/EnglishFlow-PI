package com.englishflow.auth.service;

import com.englishflow.auth.dto.recruitment.*;
import com.englishflow.auth.entity.*;
import com.englishflow.auth.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitmentService {

    private final TutorApplicationRepository applicationRepository;
    private final ApplicationDocumentRepository documentRepository;
    private final ApplicationNoteRepository noteRepository;
    private final ApplicationStatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final MeetingLinkService meetingLinkService;

    private static final String UPLOAD_DIR = "uploads/applications/";

    // Step 1: Create application with personal info
    @Transactional
    public ApplicationResponse createApplication(ApplicationStep1Request request) {
        // Check if email already exists in users or applications
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (applicationRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Application already exists for this email");
        }

        TutorApplication application = TutorApplication.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .cin(request.getCin())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .nationality(request.getNationality())
                .status(TutorApplication.ApplicationStatus.DRAFT)
                .currentStep(1)
                .build();

        TutorApplication saved = applicationRepository.save(application);
        log.info("Application created for email: {}", request.getEmail());

        return ApplicationResponse.fromEntity(saved);
    }

    // Step 2: Update qualifications
    @Transactional
    public ApplicationResponse updateQualifications(ApplicationStep2Request request) {
        TutorApplication application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        application.setEducation(request.getEducation());
        application.setCertifications(request.getCertifications());
        application.setWorkExperience(request.getWorkExperience());
        application.setYearsOfExperience(request.getYearsOfExperience());
        application.setEnglishLevel(request.getEnglishLevel());
        application.setSpecializations(request.getSpecializations());
        application.setCurrentStep(Math.max(application.getCurrentStep(), 2));

        TutorApplication saved = applicationRepository.save(application);
        log.info("Qualifications updated for application ID: {}", application.getId());

        return ApplicationResponse.fromEntity(saved);
    }

    // Step 3: Update presentation
    @Transactional
    public ApplicationResponse updatePresentation(ApplicationStep3Request request) {
        TutorApplication application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        application.setMotivationLetter(request.getMotivationLetter());
        application.setTeachingPhilosophy(request.getTeachingPhilosophy());
        application.setAvailability(request.getAvailability());
        application.setCurrentStep(Math.max(application.getCurrentStep(), 3));

        TutorApplication saved = applicationRepository.save(application);
        log.info("Presentation updated for application ID: {}", application.getId());

        return ApplicationResponse.fromEntity(saved);
    }

    // Upload document
    @Transactional
    public DocumentResponse uploadDocument(Long applicationId, MultipartFile file, String documentType) throws IOException {
        TutorApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        // Validate document type
        ApplicationDocument.DocumentType type;
        try {
            type = ApplicationDocument.DocumentType.valueOf(documentType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid document type: " + documentType);
        }

        // Create upload directory if not exists
        Path uploadPath = Paths.get(UPLOAD_DIR + applicationId);
        Files.createDirectories(uploadPath);

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(filename);

        // Save file
        Files.copy(file.getInputStream(), filePath);

        // Create document record
        ApplicationDocument document = ApplicationDocument.builder()
                .application(application)
                .type(type)
                .fileName(originalFilename)
                .filePath(filePath.toString())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .build();

        ApplicationDocument saved = documentRepository.save(document);
        log.info("Document uploaded for application ID: {}, type: {}", applicationId, type);

        return DocumentResponse.fromEntity(saved);
    }

    // Submit application
    @Transactional
    public ApplicationResponse submitApplication(Long applicationId) {
        TutorApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (application.getStatus() != TutorApplication.ApplicationStatus.DRAFT) {
            throw new IllegalArgumentException("Application already submitted");
        }

        // Validate required fields
        if (application.getCurrentStep() < 3) {
            throw new IllegalArgumentException("Please complete all steps before submitting");
        }

        // Check for required documents (CV at minimum)
        boolean hasCv = application.getDocuments().stream()
                .anyMatch(doc -> doc.getType() == ApplicationDocument.DocumentType.CV);
        if (!hasCv) {
            throw new IllegalArgumentException("CV is required to submit application");
        }

        TutorApplication.ApplicationStatus oldStatus = application.getStatus();
        application.setStatus(TutorApplication.ApplicationStatus.SUBMITTED);
        application.setSubmittedAt(LocalDateTime.now());
        application.setCurrentStep(4);

        TutorApplication saved = applicationRepository.save(application);

        // Record status change
        recordStatusChange(application, oldStatus, TutorApplication.ApplicationStatus.SUBMITTED, null, null);

        // Send confirmation email
        try {
            emailService.sendApplicationSubmittedEmail(application.getEmail(), application.getFirstName());
            log.info("Application submitted confirmation email sent to: {}", application.getEmail());
        } catch (Exception e) {
            log.error("Failed to send application submitted email", e);
        }

        log.info("Application submitted: {}", applicationId);
        return ApplicationResponse.fromEntity(saved);
    }

    // Get application by ID
    @Transactional(readOnly = true)
    public ApplicationResponse getApplication(Long applicationId) {
        TutorApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        return ApplicationResponse.fromEntity(application);
    }

    // Get all applications
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Get applications by status
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByStatus(String status) {
        TutorApplication.ApplicationStatus appStatus = TutorApplication.ApplicationStatus.valueOf(status.toUpperCase());
        return applicationRepository.findByStatus(appStatus).stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Update application status (admin/recruiter)
    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, UpdateStatusRequest request, Long changedBy) {
        TutorApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        TutorApplication.ApplicationStatus oldStatus = application.getStatus();
        TutorApplication.ApplicationStatus newStatus = TutorApplication.ApplicationStatus.valueOf(request.getStatus().toUpperCase());

        application.setStatus(newStatus);
        TutorApplication saved = applicationRepository.save(application);

        // Record status change
        recordStatusChange(application, oldStatus, newStatus, request.getComment(), changedBy);

        // Send notification email based on status
        sendStatusChangeEmail(application, newStatus);

        log.info("Application {} status changed from {} to {} by user {}", applicationId, oldStatus, newStatus, changedBy);
        return ApplicationResponse.fromEntity(saved);
    }

    // Score application (admin/recruiter)
    @Transactional
    public ApplicationResponse scoreApplication(Long applicationId, ScoreApplicationRequest request, Long reviewerId) {
        TutorApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        application.setQualificationScore(request.getQualificationScore());
        application.setPresentationScore(request.getPresentationScore());
        application.setOverallScore(request.getOverallScore());
        application.setReviewedBy(reviewerId);
        application.setReviewedAt(LocalDateTime.now());

        TutorApplication saved = applicationRepository.save(application);
        log.info("Application {} scored by user {}", applicationId, reviewerId);

        return ApplicationResponse.fromEntity(saved);
    }

    // Schedule interview
    @Transactional
    public ApplicationResponse scheduleInterview(Long applicationId, ScheduleInterviewRequest request, Long scheduledBy) {
        TutorApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        String meetingLink = request.getMeetingLink();

        // Génération automatique du lien si une plateforme est spécifiée
        if (request.getPlatform() != null && request.getPlatform() != com.englishflow.auth.enums.MeetingPlatform.MANUAL) {
            try {
                GenerateMeetingLinkRequest meetingRequest = new GenerateMeetingLinkRequest();
                meetingRequest.setPlatform(request.getPlatform());
                meetingRequest.setInterviewScheduledAt(request.getInterviewScheduledAt());
                meetingRequest.setTitle(request.getMeetingTitle() != null ? 
                    request.getMeetingTitle() : 
                    "Interview - " + application.getFirstName() + " " + application.getLastName());
                meetingRequest.setDescription("Entretien de recrutement pour le poste de tuteur");
                meetingRequest.setDurationMinutes(request.getDurationMinutes() != null ? 
                    request.getDurationMinutes() : 60);

                MeetingLinkResponse meetingResponse = meetingLinkService.generateMeetingLink(meetingRequest);
                meetingLink = meetingResponse.getMeetingLink();
                
                // Ajouter les infos supplémentaires dans les notes
                String additionalNotes = String.format(
                    "Plateforme: %s\nID de réunion: %s\n%s",
                    meetingResponse.getPlatform().getDisplayName(),
                    meetingResponse.getMeetingId(),
                    meetingResponse.getPassword() != null ? "Mot de passe: " + meetingResponse.getPassword() : ""
                );
                
                String combinedNotes = request.getNotes() != null ? 
                    request.getNotes() + "\n\n" + additionalNotes : additionalNotes;
                application.setInterviewNotes(combinedNotes);
                
                log.info("Meeting link generated automatically for application {} using {}", 
                    applicationId, request.getPlatform());
            } catch (Exception e) {
                log.error("Failed to generate meeting link automatically", e);
                throw new RuntimeException("Failed to generate meeting link: " + e.getMessage());
            }
        } else {
            // Lien manuel
            if (meetingLink == null || meetingLink.trim().isEmpty()) {
                throw new IllegalArgumentException("Meeting link is required when platform is not specified or is MANUAL");
            }
            application.setInterviewNotes(request.getNotes());
        }

        application.setInterviewScheduledAt(request.getInterviewScheduledAt());
        application.setInterviewMeetingLink(meetingLink);

        // Update status if not already scheduled
        if (application.getStatus() != TutorApplication.ApplicationStatus.INTERVIEW_SCHEDULED) {
            TutorApplication.ApplicationStatus oldStatus = application.getStatus();
            application.setStatus(TutorApplication.ApplicationStatus.INTERVIEW_SCHEDULED);
            recordStatusChange(application, oldStatus, TutorApplication.ApplicationStatus.INTERVIEW_SCHEDULED, 
                    "Interview scheduled", scheduledBy);
        }

        TutorApplication saved = applicationRepository.save(application);

        // Send interview invitation email
        try {
            emailService.sendInterviewScheduledEmail(
                    application.getEmail(),
                    application.getFirstName(),
                    request.getInterviewScheduledAt(),
                    meetingLink
            );
            log.info("Interview scheduled email sent to: {}", application.getEmail());
        } catch (Exception e) {
            log.error("Failed to send interview scheduled email", e);
        }

        log.info("Interview scheduled for application {}", applicationId);
        return ApplicationResponse.fromEntity(saved);
    }

    // Add note to application
    @Transactional
    public NoteResponse addNote(Long applicationId, AddNoteRequest request, Long createdBy) {
        TutorApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        ApplicationNote note = ApplicationNote.builder()
                .application(application)
                .content(request.getContent())
                .createdBy(createdBy)
                .build();

        ApplicationNote saved = noteRepository.save(note);
        log.info("Note added to application {} by user {}", applicationId, createdBy);

        return NoteResponse.fromEntity(saved);
    }

    // Accept application and create tutor account
    @Transactional
    public ApplicationResponse acceptApplication(Long applicationId, Long acceptedBy) {
        TutorApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (application.getStatus() == TutorApplication.ApplicationStatus.ACCEPTED) {
            throw new IllegalArgumentException("Application already accepted");
        }

        // Check if user already exists
        if (userRepository.existsByEmail(application.getEmail())) {
            throw new IllegalArgumentException("User account already exists for this email");
        }

        // Create tutor user account
        User tutor = new User();
        tutor.setEmail(application.getEmail());
        tutor.setFirstName(application.getFirstName());
        tutor.setLastName(application.getLastName());
        tutor.setPhone(application.getPhone());
        tutor.setCin(application.getCin());
        tutor.setDateOfBirth(application.getDateOfBirth());
        tutor.setAddress(application.getAddress());
        tutor.setCity(application.getCity());
        tutor.setPostalCode(application.getPostalCode());
        tutor.setYearsOfExperience(application.getYearsOfExperience());
        tutor.setBio(application.getMotivationLetter()); // Use motivation letter as bio
        tutor.setApplicationId(applicationId); // Link to recruitment application
        tutor.setRole(User.Role.TUTOR);
        tutor.setActive(true);
        tutor.setProfileCompleted(true);
        tutor.setRegistrationFeePaid(false);
        
        // Generate temporary password
        String tempPassword = UUID.randomUUID().toString().substring(0, 12);
        tutor.setPassword(passwordEncoder.encode(tempPassword));

        userRepository.save(tutor);

        // Update application status
        TutorApplication.ApplicationStatus oldStatus = application.getStatus();
        application.setStatus(TutorApplication.ApplicationStatus.ACCEPTED);
        application.setReviewedBy(acceptedBy);
        application.setReviewedAt(LocalDateTime.now());

        TutorApplication saved = applicationRepository.save(application);

        // Record status change
        recordStatusChange(application, oldStatus, TutorApplication.ApplicationStatus.ACCEPTED, 
                "Application accepted and tutor account created", acceptedBy);

        // Send welcome email with credentials
        try {
            emailService.sendTutorAccountCreatedEmail(
                    application.getEmail(),
                    application.getFirstName(),
                    tempPassword
            );
            log.info("Tutor account created email sent to: {}", application.getEmail());
        } catch (Exception e) {
            log.error("Failed to send tutor account created email", e);
        }

        log.info("Application {} accepted and tutor account created by user {}", applicationId, acceptedBy);
        return ApplicationResponse.fromEntity(saved);
    }

    // Reject application
    @Transactional
    public ApplicationResponse rejectApplication(Long applicationId, RejectApplicationRequest request, Long rejectedBy) {
        TutorApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        TutorApplication.ApplicationStatus oldStatus = application.getStatus();
        application.setStatus(TutorApplication.ApplicationStatus.REJECTED);
        application.setRejectionReason(request.getReason());
        application.setReviewedBy(rejectedBy);
        application.setReviewedAt(LocalDateTime.now());

        TutorApplication saved = applicationRepository.save(application);

        // Record status change
        recordStatusChange(application, oldStatus, TutorApplication.ApplicationStatus.REJECTED, 
                request.getReason(), rejectedBy);

        // Send rejection email
        try {
            emailService.sendApplicationRejectedEmail(
                    application.getEmail(),
                    application.getFirstName(),
                    request.getReason()
            );
            log.info("Application rejected email sent to: {}", application.getEmail());
        } catch (Exception e) {
            log.error("Failed to send application rejected email", e);
        }

        log.info("Application {} rejected by user {}", applicationId, rejectedBy);
        return ApplicationResponse.fromEntity(saved);
    }

    // Helper: Record status change
    private void recordStatusChange(TutorApplication application, 
                                   TutorApplication.ApplicationStatus fromStatus,
                                   TutorApplication.ApplicationStatus toStatus,
                                   String comment,
                                   Long changedBy) {
        ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                .application(application)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .comment(comment)
                .changedBy(changedBy)
                .build();

        statusHistoryRepository.save(history);
    }

    // Helper: Send status change email
    private void sendStatusChangeEmail(TutorApplication application, TutorApplication.ApplicationStatus newStatus) {
        try {
            switch (newStatus) {
                case UNDER_REVIEW:
                    emailService.sendApplicationUnderReviewEmail(application.getEmail(), application.getFirstName());
                    break;
                case TEST_PENDING:
                    emailService.sendTestPendingEmail(application.getEmail(), application.getFirstName());
                    break;
                // Add more cases as needed
            }
        } catch (Exception e) {
            log.error("Failed to send status change email", e);
        }
    }

    // Get application statistics
    @Transactional(readOnly = true)
    public ApplicationStatistics getStatistics() {
        long total = applicationRepository.count();
        long draft = applicationRepository.countByStatus(TutorApplication.ApplicationStatus.DRAFT);
        long submitted = applicationRepository.countByStatus(TutorApplication.ApplicationStatus.SUBMITTED);
        long underReview = applicationRepository.countByStatus(TutorApplication.ApplicationStatus.UNDER_REVIEW);
        long interviewScheduled = applicationRepository.countByStatus(TutorApplication.ApplicationStatus.INTERVIEW_SCHEDULED);
        long accepted = applicationRepository.countByStatus(TutorApplication.ApplicationStatus.ACCEPTED);
        long rejected = applicationRepository.countByStatus(TutorApplication.ApplicationStatus.REJECTED);

        return new ApplicationStatistics(total, draft, submitted, underReview, interviewScheduled, accepted, rejected);
    }

    // Get application by user ID
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (user.getApplicationId() == null) {
            throw new IllegalArgumentException("No recruitment application found for this user");
        }
        
        TutorApplication application = applicationRepository.findById(user.getApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        
        return ApplicationResponse.fromEntity(application);
    }

    // Generate meeting link
    public MeetingLinkResponse generateMeetingLink(GenerateMeetingLinkRequest request) {
        return meetingLinkService.generateMeetingLink(request);
    }

    // Get available meeting platforms
    public Map<String, Boolean> getAvailablePlatforms() {
        Map<String, Boolean> platforms = new java.util.HashMap<>();
        for (com.englishflow.auth.enums.MeetingPlatform platform : com.englishflow.auth.enums.MeetingPlatform.values()) {
            platforms.put(platform.name(), meetingLinkService.isPlatformAvailable(platform));
        }
        return platforms;
    }

    // Inner class for statistics
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ApplicationStatistics {
        private long total;
        private long draft;
        private long submitted;
        private long underReview;
        private long interviewScheduled;
        private long accepted;
        private long rejected;
    }
}
