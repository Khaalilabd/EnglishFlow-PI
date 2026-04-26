package com.englishflow.auth.service;

import com.englishflow.auth.dto.recruitment.*;
import com.englishflow.auth.entity.*;
import com.englishflow.auth.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruitmentServiceTest {

    @Mock
    private TutorApplicationRepository applicationRepository;

    @Mock
    private ApplicationDocumentRepository documentRepository;

    @Mock
    private ApplicationNoteRepository noteRepository;

    @Mock
    private ApplicationStatusHistoryRepository statusHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MeetingLinkService meetingLinkService;

    @Mock
    private InterviewScheduleService interviewScheduleService;

    @Mock
    private GoogleMeetService googleMeetService;

    @InjectMocks
    private RecruitmentService recruitmentService;

    private TutorApplication tutorApplication;
    private ApplicationStep1Request step1Request;
    private ApplicationStep2Request step2Request;
    private ApplicationStep3Request step3Request;

    @BeforeEach
    void setUp() {
        tutorApplication = TutorApplication.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .cin("AB123456")
                .dateOfBirth("1990-01-01")
                .address("123 Main St")
                .city("New York")
                .postalCode("10001")
                .nationality("American")
                .status(TutorApplication.ApplicationStatus.DRAFT)
                .currentStep(1)
                .build();

        step1Request = new ApplicationStep1Request();
        step1Request.setFirstName("John");
        step1Request.setLastName("Doe");
        step1Request.setEmail("john.doe@example.com");
        step1Request.setPhone("+1234567890");
        step1Request.setCin("AB123456");
        step1Request.setDateOfBirth("1990-01-01");
        step1Request.setAddress("123 Main St");
        step1Request.setCity("New York");
        step1Request.setPostalCode("10001");
        step1Request.setNationality("American");

        step2Request = new ApplicationStep2Request();
        step2Request.setApplicationId(1L);
        step2Request.setEducation("Bachelor's Degree");
        step2Request.setCertifications("TEFL");
        step2Request.setWorkExperience("5 years");
        step2Request.setYearsOfExperience(5);
        step2Request.setEnglishLevel("Native");
        step2Request.setSpecializations("Business English");

        step3Request = new ApplicationStep3Request();
        step3Request.setApplicationId(1L);
        step3Request.setMotivationLetter("I am passionate about teaching");
    }

    @Test
    void createApplication_Success() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(applicationRepository.existsByEmail(anyString())).thenReturn(false);
        when(applicationRepository.save(any(TutorApplication.class))).thenReturn(tutorApplication);

        // Act
        ApplicationResponse response = recruitmentService.createApplication(step1Request);

        // Assert
        assertNotNull(response);
        assertEquals("john.doe@example.com", response.getEmail());
        verify(applicationRepository).save(any(TutorApplication.class));
    }

    @Test
    void createApplication_EmailAlreadyRegistered() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            recruitmentService.createApplication(step1Request)
        );
    }

    @Test
    void createApplication_ApplicationAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(applicationRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            recruitmentService.createApplication(step1Request)
        );
    }

    @Test
    void updateQualifications_Success() {
        // Arrange
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(tutorApplication));
        when(applicationRepository.save(any(TutorApplication.class))).thenReturn(tutorApplication);

        // Act
        ApplicationResponse response = recruitmentService.updateQualifications(step2Request);

        // Assert
        assertNotNull(response);
        verify(applicationRepository).save(any(TutorApplication.class));
    }

    @Test
    void updateQualifications_ApplicationNotFound() {
        // Arrange
        when(applicationRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            recruitmentService.updateQualifications(step2Request)
        );
    }

    @Test
    void updatePresentation_Success() {
        // Arrange
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(tutorApplication));
        when(applicationRepository.save(any(TutorApplication.class))).thenReturn(tutorApplication);

        // Act
        ApplicationResponse response = recruitmentService.updatePresentation(step3Request);

        // Assert
        assertNotNull(response);
        verify(applicationRepository).save(any(TutorApplication.class));
    }

    @Test
    void updatePresentation_ApplicationNotFound() {
        // Arrange
        when(applicationRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            recruitmentService.updatePresentation(step3Request)
        );
    }

    @Test
    void submitApplication_Success() {
        // Arrange
        tutorApplication.setCurrentStep(3);
        tutorApplication.setTermsAccepted(true);
        
        // Add CV document
        ApplicationDocument cvDoc = new ApplicationDocument();
        cvDoc.setType(ApplicationDocument.DocumentType.CV);
        cvDoc.setFilePath("uploads/cv.pdf");
        cvDoc.setApplication(tutorApplication);
        tutorApplication.getDocuments().add(cvDoc);
        
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(tutorApplication));
        when(applicationRepository.save(any(TutorApplication.class))).thenReturn(tutorApplication);
        when(emailService.sendApplicationSubmittedEmail(anyString(), anyString()))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(null));

        // Act
        ApplicationResponse response = recruitmentService.submitApplication(1L);

        // Assert
        assertNotNull(response);
        verify(applicationRepository).save(any(TutorApplication.class));
    }

    @Test
    void getApplication_Success() {
        // Arrange
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(tutorApplication));

        // Act
        ApplicationResponse response = recruitmentService.getApplication(1L);

        // Assert
        assertNotNull(response);
        assertEquals("john.doe@example.com", response.getEmail());
    }

    @Test
    void getApplication_NotFound() {
        // Arrange
        when(applicationRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            recruitmentService.getApplication(1L)
        );
    }

    @Test
    void getAllApplications_Success() {
        // Arrange
        List<TutorApplication> applications = Arrays.asList(tutorApplication);
        when(applicationRepository.findAll()).thenReturn(applications);

        // Act
        List<ApplicationResponse> responses = recruitmentService.getAllApplications();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getApplicationsByStatus_Success() {
        // Arrange
        List<TutorApplication> applications = Arrays.asList(tutorApplication);
        when(applicationRepository.findByStatus(any())).thenReturn(applications);

        // Act
        List<ApplicationResponse> responses = recruitmentService.getApplicationsByStatus("DRAFT");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getAvailablePlatforms_Success() {
        // Act
        Map<String, Boolean> platforms = recruitmentService.getAvailablePlatforms();

        // Assert
        assertNotNull(platforms);
        assertTrue(platforms.containsKey("GOOGLE_MEET"));
        assertTrue(platforms.containsKey("ZOOM"));
    }

    @Test
    void getStatistics_Success() {
        // Arrange
        when(applicationRepository.count()).thenReturn(10L);
        when(applicationRepository.countByStatus(TutorApplication.ApplicationStatus.SUBMITTED)).thenReturn(5L);
        when(applicationRepository.countByStatus(TutorApplication.ApplicationStatus.UNDER_REVIEW)).thenReturn(3L);
        when(applicationRepository.countByStatus(TutorApplication.ApplicationStatus.INTERVIEW_SCHEDULED)).thenReturn(2L);
        when(applicationRepository.countByStatus(TutorApplication.ApplicationStatus.ACCEPTED)).thenReturn(1L);
        when(applicationRepository.countByStatus(TutorApplication.ApplicationStatus.REJECTED)).thenReturn(1L);
        when(applicationRepository.countByStatus(TutorApplication.ApplicationStatus.DRAFT)).thenReturn(1L);

        // Act
        RecruitmentService.ApplicationStatistics stats = recruitmentService.getStatistics();

        // Assert
        assertNotNull(stats);
        assertEquals(10L, stats.getTotal());
        assertEquals(5L, stats.getSubmitted());
    }

    @Test
    void getApplicationByEmail_Success() {
        // Arrange
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(tutorApplication));

        // Act
        ApplicationResponse response = recruitmentService.getApplication(1L);

        // Assert
        assertNotNull(response);
        assertEquals("john.doe@example.com", response.getEmail());
    }
}
