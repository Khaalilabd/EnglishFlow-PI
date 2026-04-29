package com.englishflow.complaints.service;

import com.englishflow.complaints.dto.ComplaintWithUserDTO;
import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.entity.ComplaintWorkflow;
import com.englishflow.complaints.enums.ComplaintCategory;
import com.englishflow.complaints.enums.ComplaintPriority;
import com.englishflow.complaints.enums.ComplaintStatus;
import com.englishflow.complaints.enums.TargetRole;
import com.englishflow.complaints.repository.ComplaintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcademicComplaintServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ComplaintWorkflowService workflowService;

    @Mock
    private ComplaintPriorityService priorityService;

    @InjectMocks
    private AcademicComplaintService academicComplaintService;

    private Complaint complaint1;
    private Complaint complaint2;
    private Complaint complaint3;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(academicComplaintService, "authServiceUrl", "http://localhost:8081");

        complaint1 = new Complaint();
        complaint1.setId(1L);
        complaint1.setUserId(100L);
        complaint1.setSubject("Technical Issue");
        complaint1.setDescription("Server down - need immediate attention");
        complaint1.setCategory(ComplaintCategory.TECHNICAL);
        complaint1.setStatus(ComplaintStatus.OPEN);
        complaint1.setPriority(ComplaintPriority.HIGH);
        complaint1.setTargetRole(TargetRole.ACADEMIC_OFFICE_AFFAIR);
        complaint1.setCreatedAt(LocalDateTime.now());

        complaint2 = new Complaint();
        complaint2.setId(2L);
        complaint2.setUserId(101L);
        complaint2.setSubject("Pedagogical Issue");
        complaint2.setDescription("Course content unclear and needs revision");
        complaint2.setCategory(ComplaintCategory.PEDAGOGICAL);
        complaint2.setStatus(ComplaintStatus.IN_PROGRESS);
        complaint2.setPriority(ComplaintPriority.MEDIUM);
        complaint2.setTargetRole(TargetRole.TUTOR);
        complaint2.setCreatedAt(LocalDateTime.now());

        complaint3 = new Complaint();
        complaint3.setId(3L);
        complaint3.setUserId(102L);
        complaint3.setSubject("Administrative Issue");
        complaint3.setDescription("Registration problem - cannot access system");
        complaint3.setCategory(ComplaintCategory.ADMINISTRATIVE);
        complaint3.setStatus(ComplaintStatus.RESOLVED);
        complaint3.setPriority(ComplaintPriority.CRITICAL);
        complaint3.setTargetRole(TargetRole.ACADEMIC_OFFICE_AFFAIR);
        complaint3.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getAllComplaintsWithUserInfo_ShouldReturnComplaintsWithUserData() {
        // Arrange
        List<Complaint> complaints = List.of(complaint1, complaint2);
        when(complaintRepository.findAllByOrderByCreatedAtDesc()).thenReturn(complaints);
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));
        when(workflowService.getComplaintHistory(anyLong())).thenReturn(new ArrayList<>());
        
        Map<String, Object> userInfo1 = Map.of("firstName", "John", "lastName", "Doe", "email", "john@test.com");
        Map<String, Object> userInfo2 = Map.of("firstName", "Jane", "lastName", "Smith", "email", "jane@test.com");
        
        when(restTemplate.getForObject(contains("/users/100/public"), eq(Map.class))).thenReturn(userInfo1);
        when(restTemplate.getForObject(contains("/users/101/public"), eq(Map.class))).thenReturn(userInfo2);

        // Act
        List<ComplaintWithUserDTO> result = academicComplaintService.getAllComplaintsWithUserInfo();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("John Doe");
        assertThat(result.get(0).getUserEmail()).isEqualTo("john@test.com");
        assertThat(result.get(1).getUsername()).isEqualTo("Jane Smith");
        verify(priorityService, times(2)).calculateRiskScore(any(Complaint.class));
    }

    @Test
    void getComplaintsForAcademicOffice_ShouldFilterCorrectCategories() {
        // Arrange
        List<Complaint> complaints = List.of(complaint1, complaint2, complaint3);
        when(complaintRepository.findAllByOrderByCreatedAtDesc()).thenReturn(complaints);
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));
        when(workflowService.getComplaintHistory(anyLong())).thenReturn(new ArrayList<>());
        
        Map<String, Object> userInfo = Map.of("firstName", "Test", "lastName", "User", "email", "test@test.com");
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(userInfo);

        // Act
        List<ComplaintWithUserDTO> result = academicComplaintService.getComplaintsForAcademicOffice();

        // Assert - Should include TECHNICAL and ADMINISTRATIVE, but not PEDAGOGICAL
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ComplaintWithUserDTO::getCategory)
                .containsExactlyInAnyOrder(ComplaintCategory.TECHNICAL, ComplaintCategory.ADMINISTRATIVE);
    }

    @Test
    void getComplaintsForTutor_ShouldFilterPedagogicalOnly() {
        // Arrange
        List<Complaint> complaints = List.of(complaint1, complaint2, complaint3);
        when(complaintRepository.findAllByOrderByCreatedAtDesc()).thenReturn(complaints);
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));
        when(workflowService.getComplaintHistory(anyLong())).thenReturn(new ArrayList<>());
        
        Map<String, Object> userInfo = Map.of("firstName", "Tutor", "lastName", "Test", "email", "tutor@test.com");
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(userInfo);

        // Act
        List<ComplaintWithUserDTO> result = academicComplaintService.getComplaintsForTutor();

        // Assert - Should include only PEDAGOGICAL
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(ComplaintCategory.PEDAGOGICAL);
    }

    @Test
    void getCriticalComplaints_ShouldReturnHighPriorityAndUrgent() {
        // Arrange
        List<Complaint> complaints = List.of(complaint1, complaint3); // HIGH and URGENT
        when(complaintRepository.findAllByOrderByCreatedAtDesc()).thenReturn(complaints);
        
        Map<String, Object> userInfo = Map.of("firstName", "Critical", "lastName", "User", "email", "critical@test.com");
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(userInfo);

        // Act
        List<ComplaintWithUserDTO> result = academicComplaintService.getCriticalComplaints();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ComplaintWithUserDTO::getPriority)
                .containsExactlyInAnyOrder(ComplaintPriority.HIGH, ComplaintPriority.CRITICAL);
    }

    @Test
    void getAllComplaintsWithUserInfo_WhenRestTemplateFails_ShouldUseDefaultUserInfo() {
        // Arrange
        List<Complaint> complaints = List.of(complaint1);
        when(complaintRepository.findAllByOrderByCreatedAtDesc()).thenReturn(complaints);
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));
        when(workflowService.getComplaintHistory(anyLong())).thenReturn(new ArrayList<>());
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenThrow(new RuntimeException("Service unavailable"));

        // Act
        List<ComplaintWithUserDTO> result = academicComplaintService.getAllComplaintsWithUserInfo();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("User#100");
        assertThat(result.get(0).getUserEmail()).isEqualTo("N/A");
    }

    @Test
    void getAllComplaintsWithUserInfo_WithEscalationHistory_ShouldIncludeEscalations() {
        // Arrange
        List<Complaint> complaints = List.of(complaint1);
        when(complaintRepository.findAllByOrderByCreatedAtDesc()).thenReturn(complaints);
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));
        
        ComplaintWorkflow workflow1 = new ComplaintWorkflow();
        workflow1.setIsEscalation(true);
        workflow1.setEscalationReason("High priority issue");
        
        ComplaintWorkflow workflow2 = new ComplaintWorkflow();
        workflow2.setIsEscalation(false);
        
        when(workflowService.getComplaintHistory(1L)).thenReturn(List.of(workflow1, workflow2));
        
        Map<String, Object> userInfo = Map.of("firstName", "Test", "lastName", "User", "email", "test@test.com");
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(userInfo);

        // Act
        List<ComplaintWithUserDTO> result = academicComplaintService.getAllComplaintsWithUserInfo();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEscalationHistory()).hasSize(1);
        assertThat(result.get(0).getEscalationHistory().get(0)).isEqualTo("High priority issue");
    }

    @Test
    void getAllComplaintsWithUserInfo_WhenUserInfoIsNull_ShouldUseDefaultValues() {
        // Arrange
        List<Complaint> complaints = List.of(complaint1);
        when(complaintRepository.findAllByOrderByCreatedAtDesc()).thenReturn(complaints);
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));
        when(workflowService.getComplaintHistory(anyLong())).thenReturn(new ArrayList<>());
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(null);

        // Act
        List<ComplaintWithUserDTO> result = academicComplaintService.getAllComplaintsWithUserInfo();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("User#100");
        assertThat(result.get(0).getUserEmail()).isEqualTo("N/A");
    }

    @Test
    void getAllComplaintsWithUserInfo_WhenUserInfoHasEmptyNames_ShouldUseDefaultUsername() {
        // Arrange
        List<Complaint> complaints = List.of(complaint1);
        when(complaintRepository.findAllByOrderByCreatedAtDesc()).thenReturn(complaints);
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));
        when(workflowService.getComplaintHistory(anyLong())).thenReturn(new ArrayList<>());
        
        Map<String, Object> userInfo = Map.of("firstName", "", "lastName", "", "email", "test@test.com");
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(userInfo);

        // Act
        List<ComplaintWithUserDTO> result = academicComplaintService.getAllComplaintsWithUserInfo();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("User#100");
        assertThat(result.get(0).getUserEmail()).isEqualTo("test@test.com");
    }

    @Test
    void getCriticalComplaints_WhenNoHighPriorityComplaints_ShouldReturnEmptyList() {
        // Arrange
        Complaint lowPriorityComplaint = new Complaint();
        lowPriorityComplaint.setId(4L);
        lowPriorityComplaint.setUserId(103L);
        lowPriorityComplaint.setPriority(ComplaintPriority.LOW);
        lowPriorityComplaint.setCreatedAt(LocalDateTime.now());
        
        when(complaintRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(lowPriorityComplaint));

        // Act
        List<ComplaintWithUserDTO> result = academicComplaintService.getCriticalComplaints();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void getComplaintsForAcademicOffice_ShouldIncludeAllRelevantCategories() {
        // Arrange
        Complaint behavioral = new Complaint();
        behavioral.setId(4L);
        behavioral.setUserId(103L);
        behavioral.setCategory(ComplaintCategory.BEHAVIORAL);
        behavioral.setCreatedAt(LocalDateTime.now());
        
        Complaint schedule = new Complaint();
        schedule.setId(5L);
        schedule.setUserId(104L);
        schedule.setCategory(ComplaintCategory.SCHEDULE);
        schedule.setCreatedAt(LocalDateTime.now());
        
        Complaint tutorBehavior = new Complaint();
        tutorBehavior.setId(6L);
        tutorBehavior.setUserId(105L);
        tutorBehavior.setCategory(ComplaintCategory.TUTOR_BEHAVIOR);
        tutorBehavior.setCreatedAt(LocalDateTime.now());
        
        Complaint clubSuspension = new Complaint();
        clubSuspension.setId(7L);
        clubSuspension.setUserId(106L);
        clubSuspension.setCategory(ComplaintCategory.CLUB_SUSPENSION);
        clubSuspension.setCreatedAt(LocalDateTime.now());
        
        Complaint other = new Complaint();
        other.setId(8L);
        other.setUserId(107L);
        other.setCategory(ComplaintCategory.OTHER);
        other.setCreatedAt(LocalDateTime.now());
        
        List<Complaint> complaints = List.of(complaint1, complaint2, behavioral, schedule, tutorBehavior, clubSuspension, other);
        when(complaintRepository.findAllByOrderByCreatedAtDesc()).thenReturn(complaints);
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));
        when(workflowService.getComplaintHistory(anyLong())).thenReturn(new ArrayList<>());
        
        Map<String, Object> userInfo = Map.of("firstName", "Test", "lastName", "User", "email", "test@test.com");
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(userInfo);

        // Act
        List<ComplaintWithUserDTO> result = academicComplaintService.getComplaintsForAcademicOffice();

        // Assert - Should exclude only PEDAGOGICAL
        assertThat(result).hasSize(6);
        assertThat(result).extracting(ComplaintWithUserDTO::getCategory)
                .doesNotContain(ComplaintCategory.PEDAGOGICAL);
    }
}
