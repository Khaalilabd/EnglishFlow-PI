package com.englishflow.complaints.service;

import com.englishflow.complaints.dto.ComplaintWorkflowDTO;
import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.entity.ComplaintWorkflow;
import com.englishflow.complaints.enums.ComplaintStatus;
import com.englishflow.complaints.repository.ComplaintWorkflowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplaintWorkflowServiceTest {

    @Mock
    private ComplaintWorkflowRepository workflowRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ComplaintWorkflowService complaintWorkflowService;

    private Complaint testComplaint;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(complaintWorkflowService, "authServiceUrl", "http://localhost:8080");

        testComplaint = new Complaint();
        testComplaint.setId(1L);
        testComplaint.setStatus(ComplaintStatus.IN_PROGRESS);
    }

    @Test
    void recordStatusChange_WithNormalTransition_ShouldSaveWorkflow() {
        // Arrange
        ComplaintStatus oldStatus = ComplaintStatus.OPEN;
        Long actorId = 100L;
        String actorRole = "ADMIN";
        String comment = "Test comment";

        ArgumentCaptor<ComplaintWorkflow> workflowCaptor = ArgumentCaptor.forClass(ComplaintWorkflow.class);

        // Act
        complaintWorkflowService.recordStatusChange(testComplaint, oldStatus, actorId, actorRole, comment);

        // Assert
        verify(workflowRepository).save(workflowCaptor.capture());
        ComplaintWorkflow savedWorkflow = workflowCaptor.getValue();
        assertEquals(1L, savedWorkflow.getComplaintId());
        assertEquals(oldStatus, savedWorkflow.getFromStatus());
        assertEquals(ComplaintStatus.IN_PROGRESS, savedWorkflow.getToStatus());
        assertEquals(actorId, savedWorkflow.getActorId());
        assertEquals(actorRole, savedWorkflow.getActorRole());
        assertEquals(comment, savedWorkflow.getComment());
        assertFalse(savedWorkflow.getIsEscalation());
    }

    @Test
    void recordStatusChange_WithEscalation_ShouldMarkAsEscalation() {
        // Arrange
        testComplaint.setStatus(ComplaintStatus.OPEN);
        ComplaintStatus oldStatus = ComplaintStatus.RESOLVED;
        Long actorId = 100L;
        String actorRole = "ADMIN";
        String comment = "Reopening";

        ArgumentCaptor<ComplaintWorkflow> workflowCaptor = ArgumentCaptor.forClass(ComplaintWorkflow.class);

        // Act
        complaintWorkflowService.recordStatusChange(testComplaint, oldStatus, actorId, actorRole, comment);

        // Assert
        verify(workflowRepository).save(workflowCaptor.capture());
        ComplaintWorkflow savedWorkflow = workflowCaptor.getValue();
        assertTrue(savedWorkflow.getIsEscalation());
        assertNotNull(savedWorkflow.getEscalationReason());
        assertTrue(savedWorkflow.getEscalationReason().contains("RESOLVED"));
        assertTrue(savedWorkflow.getEscalationReason().contains("OPEN"));
    }

    @Test
    void recordStatusChange_FromRejectedToInProgress_ShouldMarkAsEscalation() {
        // Arrange
        testComplaint.setStatus(ComplaintStatus.IN_PROGRESS);
        ComplaintStatus oldStatus = ComplaintStatus.REJECTED;

        ArgumentCaptor<ComplaintWorkflow> workflowCaptor = ArgumentCaptor.forClass(ComplaintWorkflow.class);

        // Act
        complaintWorkflowService.recordStatusChange(testComplaint, oldStatus, 100L, "ADMIN", "Reopening");

        // Assert
        verify(workflowRepository).save(workflowCaptor.capture());
        assertTrue(workflowCaptor.getValue().getIsEscalation());
    }

    @Test
    void getComplaintHistory_ShouldReturnWorkflows() {
        // Arrange
        ComplaintWorkflow workflow1 = new ComplaintWorkflow();
        workflow1.setId(1L);
        workflow1.setComplaintId(1L);

        ComplaintWorkflow workflow2 = new ComplaintWorkflow();
        workflow2.setId(2L);
        workflow2.setComplaintId(1L);

        when(workflowRepository.findByComplaintIdOrderByTimestampDesc(1L))
                .thenReturn(Arrays.asList(workflow1, workflow2));

        // Act
        List<ComplaintWorkflow> result = complaintWorkflowService.getComplaintHistory(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(workflowRepository).findByComplaintIdOrderByTimestampDesc(1L);
    }

    @Test
    void getComplaintHistoryWithActorNames_ShouldReturnDTOsWithNames() {
        // Arrange
        ComplaintWorkflow workflow = new ComplaintWorkflow();
        workflow.setId(1L);
        workflow.setComplaintId(1L);
        workflow.setActorId(100L);
        workflow.setActorRole("ADMIN");
        workflow.setFromStatus(ComplaintStatus.OPEN);
        workflow.setToStatus(ComplaintStatus.IN_PROGRESS);

        when(workflowRepository.findByComplaintIdOrderByTimestampDesc(1L))
                .thenReturn(Arrays.asList(workflow));
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of("firstName", "John", "lastName", "Doe"));

        // Act
        List<ComplaintWorkflowDTO> result = complaintWorkflowService.getComplaintHistoryWithActorNames(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(restTemplate).getForObject(anyString(), eq(Map.class));
    }

    @Test
    void getComplaintHistoryWithActorNames_WithSystemActor_ShouldReturnSystem() {
        // Arrange
        ComplaintWorkflow workflow = new ComplaintWorkflow();
        workflow.setId(1L);
        workflow.setComplaintId(1L);
        workflow.setActorId(0L);
        workflow.setActorRole("SYSTEM");
        workflow.setFromStatus(ComplaintStatus.OPEN);
        workflow.setToStatus(ComplaintStatus.IN_PROGRESS);

        when(workflowRepository.findByComplaintIdOrderByTimestampDesc(1L))
                .thenReturn(Arrays.asList(workflow));

        // Act
        List<ComplaintWorkflowDTO> result = complaintWorkflowService.getComplaintHistoryWithActorNames(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getComplaintHistoryWithActorNames_WithNullActorId_ShouldReturnSystem() {
        // Arrange
        ComplaintWorkflow workflow = new ComplaintWorkflow();
        workflow.setId(1L);
        workflow.setComplaintId(1L);
        workflow.setActorId(null);
        workflow.setActorRole("SYSTEM");
        workflow.setFromStatus(ComplaintStatus.OPEN);
        workflow.setToStatus(ComplaintStatus.IN_PROGRESS);

        when(workflowRepository.findByComplaintIdOrderByTimestampDesc(1L))
                .thenReturn(Arrays.asList(workflow));

        // Act
        List<ComplaintWorkflowDTO> result = complaintWorkflowService.getComplaintHistoryWithActorNames(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getComplaintHistoryWithActorNames_WhenAuthServiceFails_ShouldReturnActorRole() {
        // Arrange
        ComplaintWorkflow workflow = new ComplaintWorkflow();
        workflow.setId(1L);
        workflow.setComplaintId(1L);
        workflow.setActorId(100L);
        workflow.setActorRole("ADMIN");
        workflow.setFromStatus(ComplaintStatus.OPEN);
        workflow.setToStatus(ComplaintStatus.IN_PROGRESS);

        when(workflowRepository.findByComplaintIdOrderByTimestampDesc(1L))
                .thenReturn(Arrays.asList(workflow));
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        // Act
        List<ComplaintWorkflowDTO> result = complaintWorkflowService.getComplaintHistoryWithActorNames(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getComplaintHistoryWithActorNames_WhenAuthServiceReturnsNull_ShouldReturnActorRole() {
        // Arrange
        ComplaintWorkflow workflow = new ComplaintWorkflow();
        workflow.setId(1L);
        workflow.setComplaintId(1L);
        workflow.setActorId(100L);
        workflow.setActorRole("ADMIN");
        workflow.setFromStatus(ComplaintStatus.OPEN);
        workflow.setToStatus(ComplaintStatus.IN_PROGRESS);

        when(workflowRepository.findByComplaintIdOrderByTimestampDesc(1L))
                .thenReturn(Arrays.asList(workflow));
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(null);

        // Act
        List<ComplaintWorkflowDTO> result = complaintWorkflowService.getComplaintHistoryWithActorNames(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getComplaintHistoryWithActorNames_WhenAuthServiceReturnsEmptyNames_ShouldReturnActorRole() {
        // Arrange
        ComplaintWorkflow workflow = new ComplaintWorkflow();
        workflow.setId(1L);
        workflow.setComplaintId(1L);
        workflow.setActorId(100L);
        workflow.setActorRole("ADMIN");
        workflow.setFromStatus(ComplaintStatus.OPEN);
        workflow.setToStatus(ComplaintStatus.IN_PROGRESS);

        when(workflowRepository.findByComplaintIdOrderByTimestampDesc(1L))
                .thenReturn(Arrays.asList(workflow));
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of("firstName", "", "lastName", ""));

        // Act
        List<ComplaintWorkflowDTO> result = complaintWorkflowService.getComplaintHistoryWithActorNames(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
