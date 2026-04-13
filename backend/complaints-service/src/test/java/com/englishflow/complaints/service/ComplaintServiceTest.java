package com.englishflow.complaints.service;

import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.enums.ComplaintCategory;
import com.englishflow.complaints.enums.ComplaintStatus;
import com.englishflow.complaints.enums.TargetRole;
import com.englishflow.complaints.repository.ComplaintMessageRepository;
import com.englishflow.complaints.repository.ComplaintRepository;
import com.englishflow.complaints.repository.ComplaintWorkflowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplaintServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private ComplaintPriorityService priorityService;

    @Mock
    private ComplaintMessageRepository messageRepository;

    @Mock
    private ComplaintWorkflowRepository workflowRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ComplaintService complaintService;

    private Complaint testComplaint;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(complaintService, "authServiceUrl", "http://localhost:8081/auth");
        
        testComplaint = new Complaint();
        testComplaint.setId(1L);
        testComplaint.setUserId(100L);
        testComplaint.setSubject("Test Subject");
        testComplaint.setDescription("This is a test description with more than 20 characters");
        testComplaint.setCategory(ComplaintCategory.TECHNICAL);
        testComplaint.setTargetRole(TargetRole.SUPPORT);
        testComplaint.setStatus(ComplaintStatus.OPEN);
        testComplaint.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createComplaint_ValidComplaint_ReturnsCreatedComplaint() {
        // Arrange
        when(complaintRepository.save(any(Complaint.class))).thenReturn(testComplaint);
        doNothing().when(priorityService).calculatePriorityAndTarget(any(Complaint.class));

        // Act
        Complaint result = complaintService.createComplaint(testComplaint);

        // Assert
        assertNotNull(result);
        assertEquals(ComplaintStatus.OPEN, result.getStatus());
        verify(priorityService, times(1)).calculatePriorityAndTarget(testComplaint);
        verify(complaintRepository, times(1)).save(testComplaint);
    }

    @Test
    void createComplaint_EmptySubject_ThrowsException() {
        // Arrange
        testComplaint.setSubject("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> complaintService.createComplaint(testComplaint));
        verify(complaintRepository, never()).save(any(Complaint.class));
    }

    @Test
    void createComplaint_EmptyDescription_ThrowsException() {
        // Arrange
        testComplaint.setDescription("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> complaintService.createComplaint(testComplaint));
        verify(complaintRepository, never()).save(any(Complaint.class));
    }

    @Test
    void createComplaint_ShortDescription_ThrowsException() {
        // Arrange
        testComplaint.setDescription("Too short");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> complaintService.createComplaint(testComplaint));
        verify(complaintRepository, never()).save(any(Complaint.class));
    }

    @Test
    void createComplaint_InvalidUserId_ThrowsException() {
        // Arrange
        testComplaint.setUserId(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> complaintService.createComplaint(testComplaint));
        verify(complaintRepository, never()).save(any(Complaint.class));
    }

    @Test
    void createComplaint_ClubSuspensionWithoutClubId_ThrowsException() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.CLUB_SUSPENSION);
        testComplaint.setClubId(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> complaintService.createComplaint(testComplaint));
        verify(complaintRepository, never()).save(any(Complaint.class));
    }

    @Test
    void createComplaint_ClubSuspensionWithClubId_Success() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.CLUB_SUSPENSION);
        testComplaint.setClubId(5);
        when(complaintRepository.save(any(Complaint.class))).thenReturn(testComplaint);
        doNothing().when(priorityService).calculatePriorityAndTarget(any(Complaint.class));

        // Act
        Complaint result = complaintService.createComplaint(testComplaint);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getClubId().intValue());
        verify(complaintRepository, times(1)).save(testComplaint);
    }

    @Test
    void getComplaintsByUserId_ReturnsComplaintsList() {
        // Arrange
        List<Complaint> complaints = Arrays.asList(testComplaint);
        when(complaintRepository.findByUserIdOrderByCreatedAtDesc(100L)).thenReturn(complaints);

        // Act
        List<Complaint> result = complaintService.getComplaintsByUserId(100L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testComplaint.getId(), result.get(0).getId());
        verify(complaintRepository, times(1)).findByUserIdOrderByCreatedAtDesc(100L);
    }

    @Test
    void getAllComplaints_ReturnsAllComplaints() {
        // Arrange
        List<Complaint> complaints = Arrays.asList(testComplaint);
        when(complaintRepository.findAllByOrderByCreatedAtDesc()).thenReturn(complaints);

        // Act
        List<Complaint> result = complaintService.getAllComplaints();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(complaintRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getComplaintById_ExistingId_ReturnsComplaint() {
        // Arrange
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(testComplaint));

        // Act
        Complaint result = complaintService.getComplaintById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testComplaint.getId(), result.getId());
        verify(complaintRepository, times(1)).findById(1L);
    }

    @Test
    void getComplaintById_NonExistingId_ThrowsException() {
        // Arrange
        when(complaintRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> complaintService.getComplaintById(999L));
    }

    @Test
    void updateComplaint_ValidUpdate_ReturnsUpdatedComplaint() {
        // Arrange
        Complaint updateDetails = new Complaint();
        updateDetails.setSubject("Updated Subject");
        updateDetails.setStatus(ComplaintStatus.IN_PROGRESS);
        updateDetails.setResponse("Admin response");
        updateDetails.setResponderId(200L);
        
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(testComplaint));
        when(complaintRepository.save(any(Complaint.class))).thenReturn(testComplaint);

        // Act
        Complaint result = complaintService.updateComplaint(1L, updateDetails);

        // Assert
        assertNotNull(result);
        verify(complaintRepository, times(1)).save(testComplaint);
    }

    @Test
    void deleteComplaint_ExistingComplaint_DeletesSuccessfully() {
        // Arrange
        doNothing().when(messageRepository).deleteByComplaintId(1L);
        doNothing().when(workflowRepository).deleteByComplaintId(1L);
        doNothing().when(complaintRepository).deleteById(1L);

        // Act
        complaintService.deleteComplaint(1L);

        // Assert
        verify(messageRepository, times(1)).deleteByComplaintId(1L);
        verify(workflowRepository, times(1)).deleteByComplaintId(1L);
        verify(complaintRepository, times(1)).deleteById(1L);
    }

    @Test
    void getComplaintsByStatus_ReturnsFilteredComplaints() {
        // Arrange
        List<Complaint> complaints = Arrays.asList(testComplaint);
        when(complaintRepository.findByStatus(ComplaintStatus.OPEN)).thenReturn(complaints);

        // Act
        List<Complaint> result = complaintService.getComplaintsByStatus(ComplaintStatus.OPEN);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ComplaintStatus.OPEN, result.get(0).getStatus());
        verify(complaintRepository, times(1)).findByStatus(ComplaintStatus.OPEN);
    }

    @Test
    void createComplaint_NegativeSessionCount_ThrowsException() {
        // Arrange
        testComplaint.setSessionCount(-1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> complaintService.createComplaint(testComplaint));
        verify(complaintRepository, never()).save(any(Complaint.class));
    }
}
