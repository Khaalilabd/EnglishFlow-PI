package com.englishflow.auth.controller;

import com.englishflow.auth.dto.recruitment.*;
import com.englishflow.auth.service.RecruitmentService;
import com.englishflow.auth.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruitmentControllerTest {

    @Mock
    private RecruitmentService recruitmentService;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private RecruitmentController recruitmentController;

    private ApplicationResponse applicationResponse;

    @BeforeEach
    void setUp() {
        applicationResponse = new ApplicationResponse();
        applicationResponse.setId(1L);
        applicationResponse.setEmail("test@example.com");
        applicationResponse.setStatus("SUBMITTED");
    }

    @Test
    void createApplication_Success() {
        // Arrange
        ApplicationStep1Request request = new ApplicationStep1Request();
        when(recruitmentService.createApplication(any())).thenReturn(applicationResponse);

        // Act
        ResponseEntity<ApplicationResponse> response = recruitmentController.createApplication(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(recruitmentService).createApplication(any());
    }

    @Test
    void updateQualifications_Success() {
        // Arrange
        ApplicationStep2Request request = new ApplicationStep2Request();
        when(recruitmentService.updateQualifications(any())).thenReturn(applicationResponse);

        // Act
        ResponseEntity<ApplicationResponse> response = recruitmentController.updateQualifications(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void updatePresentation_Success() {
        // Arrange
        ApplicationStep3Request request = new ApplicationStep3Request();
        when(recruitmentService.updatePresentation(any())).thenReturn(applicationResponse);

        // Act
        ResponseEntity<ApplicationResponse> response = recruitmentController.updatePresentation(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void submitApplication_Success() {
        // Arrange
        when(recruitmentService.submitApplication(1L)).thenReturn(applicationResponse);

        // Act
        ResponseEntity<ApplicationResponse> response = recruitmentController.submitApplication(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void acceptTerms_Success() {
        // Arrange
        when(recruitmentService.acceptTerms(1L)).thenReturn(applicationResponse);

        // Act
        ResponseEntity<ApplicationResponse> response = recruitmentController.acceptTerms(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getApplication_Success() {
        // Arrange
        when(recruitmentService.getApplication(1L)).thenReturn(applicationResponse);

        // Act
        ResponseEntity<ApplicationResponse> response = recruitmentController.getApplication(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getAllApplications_Success() {
        // Arrange
        List<ApplicationResponse> applications = Arrays.asList(applicationResponse);
        when(recruitmentService.getAllApplications()).thenReturn(applications);

        // Act
        ResponseEntity<List<ApplicationResponse>> response = recruitmentController.getAllApplications();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getApplicationsByStatus_Success() {
        // Arrange
        List<ApplicationResponse> applications = Arrays.asList(applicationResponse);
        when(recruitmentService.getApplicationsByStatus("SUBMITTED")).thenReturn(applications);

        // Act
        ResponseEntity<List<ApplicationResponse>> response = recruitmentController.getApplicationsByStatus("SUBMITTED");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateStatus_Success() {
        // Arrange
        UpdateStatusRequest request = new UpdateStatusRequest();
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(recruitmentService.updateStatus(anyLong(), any(), anyLong())).thenReturn(applicationResponse);

        // Act
        ResponseEntity<ApplicationResponse> response = recruitmentController.updateStatus(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void scoreApplication_Success() {
        // Arrange
        ScoreApplicationRequest request = new ScoreApplicationRequest();
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(recruitmentService.scoreApplication(anyLong(), any(), anyLong())).thenReturn(applicationResponse);

        // Act
        ResponseEntity<ApplicationResponse> response = recruitmentController.scoreApplication(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void scheduleInterview_Success() {
        // Arrange
        ScheduleInterviewRequest request = new ScheduleInterviewRequest();
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(recruitmentService.scheduleInterview(anyLong(), any(), anyLong())).thenReturn(applicationResponse);

        // Act
        ResponseEntity<ApplicationResponse> response = recruitmentController.scheduleInterview(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void generateMeetingLink_Success() {
        // Arrange
        GenerateMeetingLinkRequest request = new GenerateMeetingLinkRequest();
        MeetingLinkResponse meetingResponse = new MeetingLinkResponse();
        when(recruitmentService.generateMeetingLink(any())).thenReturn(meetingResponse);

        // Act
        ResponseEntity<MeetingLinkResponse> response = recruitmentController.generateMeetingLink(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getAvailablePlatforms_Success() {
        // Arrange
        Map<String, Boolean> platforms = new HashMap<>();
        platforms.put("GOOGLE_MEET", true);
        platforms.put("ZOOM", false);
        when(recruitmentService.getAvailablePlatforms()).thenReturn(platforms);

        // Act
        ResponseEntity<Map<String, Boolean>> response = recruitmentController.getAvailablePlatforms();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void addNote_Success() {
        // Arrange
        AddNoteRequest request = new AddNoteRequest();
        NoteResponse noteResponse = new NoteResponse();
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(recruitmentService.addNote(anyLong(), any(), anyLong())).thenReturn(noteResponse);

        // Act
        ResponseEntity<NoteResponse> response = recruitmentController.addNote(1L, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void acceptApplication_Success() {
        // Arrange
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(recruitmentService.acceptApplication(anyLong(), anyLong())).thenReturn(applicationResponse);

        // Act
        ResponseEntity<ApplicationResponse> response = recruitmentController.acceptApplication(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void rejectApplication_Success() {
        // Arrange
        RejectApplicationRequest request = new RejectApplicationRequest();
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(recruitmentService.rejectApplication(anyLong(), any(), anyLong())).thenReturn(applicationResponse);

        // Act
        ResponseEntity<ApplicationResponse> response = recruitmentController.rejectApplication(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getStatistics_Success() {
        // Arrange
        RecruitmentService.ApplicationStatistics stats = 
            new RecruitmentService.ApplicationStatistics(10L, 5L, 3L, 2L, 1L, 1L, 1L);
        when(recruitmentService.getStatistics()).thenReturn(stats);

        // Act
        ResponseEntity<RecruitmentService.ApplicationStatistics> response = recruitmentController.getStatistics();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getApplicationByUserId_Success() {
        // Arrange
        when(recruitmentService.getApplicationByUserId(1L)).thenReturn(applicationResponse);

        // Act
        ResponseEntity<ApplicationResponse> response = recruitmentController.getApplicationByUserId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getMyApplication_Success() {
        // Arrange
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(recruitmentService.getApplicationByUserId(1L)).thenReturn(applicationResponse);

        // Act
        ResponseEntity<ApplicationResponse> response = recruitmentController.getMyApplication();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void cancelInterview_Success() {
        // Arrange
        doNothing().when(recruitmentService).cancelInterviewSchedule(anyLong(), anyString());

        // Act
        ResponseEntity<Void> response = recruitmentController.cancelInterview(1L, "Reason");

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(recruitmentService).cancelInterviewSchedule(1L, "Reason");
    }

    @Test
    void cancelInterviewByApplicationId_Success() {
        // Arrange
        doNothing().when(recruitmentService).cancelInterviewByApplicationId(anyLong(), anyString());

        // Act
        ResponseEntity<Void> response = recruitmentController.cancelInterviewByApplicationId(1L, "Reason");

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(recruitmentService).cancelInterviewByApplicationId(1L, "Reason");
    }
}
