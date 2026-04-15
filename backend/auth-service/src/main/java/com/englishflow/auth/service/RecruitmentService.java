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
import java.util.HashMap;
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
    private final InterviewScheduleService interviewScheduleService;
    private final GoogleMeetService googleMeetService;

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

        // Check if terms and conditions are accepted
        if (application.getTermsAccepted() == null || !application.getTermsAccepted()) {
            throw new IllegalArgumentException("You must accept the terms and conditions to submit your application");
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

    // Accept terms and conditions
    @Transactional
    public ApplicationResponse acceptTerms(Long applicationId) {
        TutorApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        application.setTermsAccepted(true);
        application.setTermsAcceptedAt(LocalDateTime.now());

        TutorApplication saved = applicationRepository.save(application);
        log.info("Terms accepted for application: {}", applicationId);

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

        // Logique métier: Si on remet en UNDER_REVIEW un candidat qui était ACCEPTED
        // Il faut désactiver ou supprimer le compte utilisateur créé
        if (oldStatus == TutorApplication.ApplicationStatus.ACCEPTED && 
            newStatus == TutorApplication.ApplicationStatus.UNDER_REVIEW) {
            
            // Trouver et désactiver le compte utilisateur associé
            userRepository.findByEmail(application.getEmail()).ifPresent(user -> {
                if (user.getRole() == User.Role.TUTOR && user.getApplicationId() != null && 
                    user.getApplicationId().equals(applicationId)) {
                    
                    // Option 1: Désactiver le compte (recommandé pour garder l'historique)
                    user.setActive(false);
                    userRepository.save(user);
                    log.info("User account {} deactivated due to application status change to UNDER_REVIEW", user.getId());
                    
                    // Option 2: Supprimer le compte (décommenter si vous préférez supprimer)
                    // userRepository.delete(user);
                    // log.info("User account {} deleted due to application status change to UNDER_REVIEW", user.getId());
                }
            });
        }
        
        // Logique métier: Si on remet en UNDER_REVIEW un candidat qui était REJECTED
        // Réinitialiser la raison de rejet
        if (oldStatus == TutorApplication.ApplicationStatus.REJECTED && 
            newStatus == TutorApplication.ApplicationStatus.UNDER_REVIEW) {
            application.setRejectionReason(null);
        }

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

        // Valider la durée
        Integer durationMinutes = request.getDurationMinutes() != null ? request.getDurationMinutes() : 60;
        
        // Préparer le titre et la description
        String title = request.getMeetingTitle() != null ? 
                request.getMeetingTitle() : 
                "Interview - " + application.getFirstName() + " " + application.getLastName();
        String description = "Entretien de recrutement pour le poste de tuteur\n\n" +
                "Candidat: " + application.getFirstName() + " " + application.getLastName() + "\n" +
                "Email: " + application.getEmail();

        String meetingLink;
        InterviewSchedule schedule = null;

        // Génération automatique du lien si une plateforme est spécifiée
        if (request.getPlatform() != null && request.getPlatform() == com.englishflow.auth.enums.MeetingPlatform.GOOGLE_MEET) {
            try {
                // Utiliser le nouveau système avec vérification de disponibilité
                schedule = interviewScheduleService.createInterviewSchedule(
                        application,
                        scheduledBy,
                        request.getInterviewScheduledAt(),
                        durationMinutes,
                        title,
                        description
                );
                
                meetingLink = schedule.getMeetingLink();
                
                // Ajouter les infos dans les notes
                String additionalNotes = String.format(
                    "Plateforme: Google Meet\nDurée: %d minutes\nGoogle Event ID: %s",
                    durationMinutes,
                    schedule.getGoogleEventId() != null ? schedule.getGoogleEventId() : "N/A"
                );
                
                String combinedNotes = request.getNotes() != null ? 
                    request.getNotes() + "\n\n" + additionalNotes : additionalNotes;
                application.setInterviewNotes(combinedNotes);
                
                log.info("Interview scheduled with calendar integration for application {}", applicationId);
                
            } catch (IllegalStateException e) {
                // Conflit d'horaire détecté
                log.error("Schedule conflict detected for application {}: {}", applicationId, e.getMessage());
                throw new IllegalStateException("Conflit d'horaire: Un entretien est déjà programmé à cette heure. " +
                        "Veuillez consulter le calendrier et choisir un autre créneau.");
            } catch (Exception e) {
                log.error("Failed to create interview schedule", e);
                throw new RuntimeException("Échec de la création du rendez-vous: " + e.getMessage());
            }
        } else if (request.getPlatform() != null && request.getPlatform() != com.englishflow.auth.enums.MeetingPlatform.MANUAL) {
            // Autres plateformes (Zoom, etc.) - utiliser l'ancien système
            try {
                GenerateMeetingLinkRequest meetingRequest = new GenerateMeetingLinkRequest();
                meetingRequest.setPlatform(request.getPlatform());
                meetingRequest.setInterviewScheduledAt(request.getInterviewScheduledAt());
                meetingRequest.setTitle(title);
                meetingRequest.setDescription(description);
                meetingRequest.setDurationMinutes(durationMinutes);

                MeetingLinkResponse meetingResponse = meetingLinkService.generateMeetingLink(meetingRequest);
                meetingLink = meetingResponse.getMeetingLink();
                
                String additionalNotes = String.format(
                    "Plateforme: %s\nID de réunion: %s\n%s",
                    meetingResponse.getPlatform().getDisplayName(),
                    meetingResponse.getMeetingId(),
                    meetingResponse.getPassword() != null ? "Mot de passe: " + meetingResponse.getPassword() : ""
                );
                
                String combinedNotes = request.getNotes() != null ? 
                    request.getNotes() + "\n\n" + additionalNotes : additionalNotes;
                application.setInterviewNotes(combinedNotes);
                
                log.info("Meeting link generated for application {} using {}", 
                    applicationId, request.getPlatform());
            } catch (Exception e) {
                log.error("Failed to generate meeting link automatically", e);
                throw new RuntimeException("Failed to generate meeting link: " + e.getMessage());
            }
        } else {
            // Lien manuel
            if (request.getMeetingLink() == null || request.getMeetingLink().trim().isEmpty()) {
                throw new IllegalArgumentException("Meeting link is required when platform is not specified or is MANUAL");
            }
            meetingLink = request.getMeetingLink();
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
        User existingUser = userRepository.findByEmail(application.getEmail()).orElse(null);
        
        if (existingUser != null && existingUser.isActive()) {
            throw new IllegalArgumentException("Active user account already exists for this email");
        }

        User tutor;
        String tempPassword = null;
        boolean isReactivation = false;

        if (existingUser != null && !existingUser.isActive()) {
            // Réactiver le compte existant
            tutor = existingUser;
            tutor.setActive(true);
            tutor.setMustChangePassword(true);
            
            // Générer un nouveau mot de passe temporaire
            tempPassword = UUID.randomUUID().toString().substring(0, 12);
            tutor.setPassword(passwordEncoder.encode(tempPassword));
            
            isReactivation = true;
            log.info("Reactivating existing tutor account for email: {}", application.getEmail());
        } else {
            // Create new tutor user account
            tutor = new User();
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
            tutor.setMustChangePassword(true); // Force password change on first login
            
            // Generate temporary password
            tempPassword = UUID.randomUUID().toString().substring(0, 12);
            tutor.setPassword(passwordEncoder.encode(tempPassword));
            
            log.info("Creating new tutor account for email: {}", application.getEmail());
        }

        userRepository.save(tutor);

        // Update application status
        TutorApplication.ApplicationStatus oldStatus = application.getStatus();
        application.setStatus(TutorApplication.ApplicationStatus.ACCEPTED);
        application.setReviewedBy(acceptedBy);
        application.setReviewedAt(LocalDateTime.now());

        TutorApplication saved = applicationRepository.save(application);

        // Record status change
        String statusComment = isReactivation ? 
            "Application accepted and tutor account reactivated" : 
            "Application accepted and tutor account created";
        recordStatusChange(application, oldStatus, TutorApplication.ApplicationStatus.ACCEPTED, 
                statusComment, acceptedBy);

        // Send welcome email with credentials
        try {
            emailService.sendTutorAccountCreatedEmail(
                    application.getEmail(),
                    application.getFirstName(),
                    tempPassword
            );
            log.info("Tutor account {} email sent to: {}", 
                isReactivation ? "reactivated" : "created", application.getEmail());
        } catch (Exception e) {
            log.error("Failed to send tutor account email", e);
        }

        log.info("Application {} accepted and tutor account {} by user {}", 
            applicationId, isReactivation ? "reactivated" : "created", acceptedBy);
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

    // Calendar methods

    /**
     * Récupère la disponibilité du calendrier pour un interviewer
     */
    public CalendarAvailabilityResponse getCalendarAvailability(CalendarAvailabilityRequest request, Long currentUserId) {
        return interviewScheduleService.getCalendarAvailability(request, currentUserId);
    }

    /**
     * Récupère les rendez-vous à venir pour un interviewer
     */
    public List<CalendarEventResponse> getUpcomingInterviews(Long interviewerId) {
        List<InterviewSchedule> schedules = interviewScheduleService.getUpcomingInterviews(interviewerId);
        return schedules.stream()
                .map(this::convertScheduleToEventResponse)
                .collect(Collectors.toList());
    }

    /**
     * Annule un rendez-vous d'entretien
     */
    @Transactional
    public void cancelInterviewSchedule(Long scheduleId, String reason) {
        interviewScheduleService.cancelInterviewSchedule(scheduleId, reason);
    }

    /**
     * Annule un rendez-vous d'entretien par application ID
     */
    @Transactional
    public void cancelInterviewByApplicationId(Long applicationId, String reason) {
        interviewScheduleService.cancelInterviewByApplicationId(applicationId, reason);
    }

    /**
     * Convertit InterviewSchedule en CalendarEventResponse
     */
    private CalendarEventResponse convertScheduleToEventResponse(InterviewSchedule schedule) {
        return CalendarEventResponse.builder()
                .scheduleId(schedule.getId())
                .googleEventId(schedule.getGoogleEventId())
                .title(schedule.getTitle())
                .description(schedule.getDescription())
                .start(schedule.getScheduledStart())
                .end(schedule.getScheduledEnd())
                .durationMinutes(schedule.getDurationMinutes())
                .meetingLink(schedule.getMeetingLink())
                .platform(schedule.getMeetingPlatform())
                .status(schedule.getStatus().name())
                .applicationId(schedule.getApplication().getId())
                .candidateName(schedule.getApplication().getFirstName() + " " + schedule.getApplication().getLastName())
                .candidateEmail(schedule.getApplication().getEmail())
                .source(schedule.getGoogleEventId() != null ? 
                        CalendarEventResponse.EventSource.BOTH : 
                        CalendarEventResponse.EventSource.LOCAL_DB)
                .build();
    }

    /**
     * Teste la connexion Google Calendar et retourne l'état
     */
    public Map<String, Object> testGoogleCalendarConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Tester la création d'un événement de test
            LocalDateTime testTime = LocalDateTime.now().plusDays(7);
            String testTitle = "Test Meeting - EnglishFlow";
            String testDescription = "This is a test meeting to verify Google Calendar integration";
            
            GoogleMeetService.MeetingCreationResult meetingResult = googleMeetService.createMeetingWithDetails(
                    testTitle, testDescription, testTime, 30
            );
            
            result.put("success", meetingResult.isSuccess());
            result.put("meetingLink", meetingResult.getMeetingLink());
            result.put("googleEventId", meetingResult.getGoogleEventId());
            result.put("message", meetingResult.getMessage());
            result.put("isRealMeetLink", !meetingResult.getMeetingLink().contains("/new"));
            
            if (meetingResult.isSuccess()) {
                result.put("status", "✅ Google Calendar OAuth2 is working correctly!");
                result.put("recommendation", "You can now schedule interviews with real Google Meet links.");
            } else {
                result.put("status", "⚠️ Google Calendar OAuth2 is not working");
                result.put("recommendation", "Please follow the OAuth2 setup guide in docs/GOOGLE_OAUTH_SETUP.md");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("status", "❌ Error testing Google Calendar");
            result.put("error", e.getMessage());
            result.put("recommendation", "Check the logs and verify your OAuth2 configuration");
            log.error("Error testing Google Calendar connection", e);
        }
        
        return result;
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
