package com.englishflow.complaints.service;

import com.englishflow.complaints.dto.ComplaintMessageDTO;
import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.entity.ComplaintMessage;
import com.englishflow.complaints.repository.ComplaintMessageRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplaintMessageServiceTest {

    @Mock
    private ComplaintMessageRepository messageRepository;

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ComplaintMessageService complaintMessageService;

    private ComplaintMessage testMessage;
    private Complaint testComplaint;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(complaintMessageService, "authServiceUrl", "http://localhost:8080");

        testMessage = new ComplaintMessage();
        testMessage.setId(1L);
        testMessage.setComplaintId(1L);
        testMessage.setAuthorId(100L);
        testMessage.setAuthorRole("STUDENT");
        testMessage.setContent("Test message");
        testMessage.setTimestamp(LocalDateTime.now());

        testComplaint = new Complaint();
        testComplaint.setId(1L);
    }

    @Test
    void createMessage_WithStudentRole_ShouldNotUpdateComplaintResponse() {
        // Arrange
        when(messageRepository.save(any(ComplaintMessage.class))).thenReturn(testMessage);
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of("firstName", "John", "lastName", "Doe"));

        // Act
        ComplaintMessageDTO result = complaintMessageService.createMessage(testMessage);

        // Assert
        assertNotNull(result);
        assertEquals(testMessage.getId(), result.getId());
        verify(messageRepository).save(testMessage);
        verify(complaintRepository, never()).save(any(Complaint.class));
    }

    @Test
    void createMessage_WithAdminRole_ShouldUpdateComplaintResponse() {
        // Arrange
        testMessage.setAuthorRole("ADMIN");
        when(messageRepository.save(any(ComplaintMessage.class))).thenReturn(testMessage);
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(testComplaint));
        when(complaintRepository.save(any(Complaint.class))).thenReturn(testComplaint);
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of("firstName", "Admin", "lastName", "User"));

        // Act
        ComplaintMessageDTO result = complaintMessageService.createMessage(testMessage);

        // Assert
        assertNotNull(result);
        verify(complaintRepository).findById(1L);
        verify(complaintRepository).save(any(Complaint.class));
        assertEquals("Test message", testComplaint.getResponse());
        assertEquals(100L, testComplaint.getResponderId());
        assertEquals("ADMIN", testComplaint.getResponderRole());
    }

    @Test
    void createMessage_WithTutorRole_ShouldUpdateComplaintResponse() {
        // Arrange
        testMessage.setAuthorRole("TUTOR");
        when(messageRepository.save(any(ComplaintMessage.class))).thenReturn(testMessage);
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(testComplaint));
        when(complaintRepository.save(any(Complaint.class))).thenReturn(testComplaint);
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of("firstName", "Tutor", "lastName", "Name"));

        // Act
        ComplaintMessageDTO result = complaintMessageService.createMessage(testMessage);

        // Assert
        assertNotNull(result);
        verify(complaintRepository).save(any(Complaint.class));
    }

    @Test
    void createMessage_WhenComplaintNotFound_ShouldHandleException() {
        // Arrange
        testMessage.setAuthorRole("ADMIN");
        when(messageRepository.save(any(ComplaintMessage.class))).thenReturn(testMessage);
        when(complaintRepository.findById(1L)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of("firstName", "Admin", "lastName", "User"));

        // Act
        ComplaintMessageDTO result = complaintMessageService.createMessage(testMessage);

        // Assert
        assertNotNull(result);
        verify(complaintRepository, never()).save(any(Complaint.class));
    }

    @Test
    void getMessagesByComplaintId_ShouldReturnMessagesWithAuthorNames() {
        // Arrange
        ComplaintMessage message1 = new ComplaintMessage();
        message1.setId(1L);
        message1.setComplaintId(1L);
        message1.setAuthorId(100L);
        message1.setAuthorRole("STUDENT");
        message1.setContent("Message 1");
        message1.setTimestamp(LocalDateTime.now());

        ComplaintMessage message2 = new ComplaintMessage();
        message2.setId(2L);
        message2.setComplaintId(1L);
        message2.setAuthorId(200L);
        message2.setAuthorRole("ADMIN");
        message2.setContent("Message 2");
        message2.setTimestamp(LocalDateTime.now());

        when(messageRepository.findByComplaintIdOrderByTimestampAsc(1L))
                .thenReturn(Arrays.asList(message1, message2));
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of("firstName", "John", "lastName", "Doe"))
                .thenReturn(Map.of("firstName", "Admin", "lastName", "User"));

        // Act
        List<ComplaintMessageDTO> result = complaintMessageService.getMessagesByComplaintId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(messageRepository).findByComplaintIdOrderByTimestampAsc(1L);
    }

    @Test
    void getMessagesByComplaintId_WhenAuthServiceFails_ShouldReturnFallbackNames() {
        // Arrange
        when(messageRepository.findByComplaintIdOrderByTimestampAsc(1L))
                .thenReturn(Arrays.asList(testMessage));
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        // Act
        List<ComplaintMessageDTO> result = complaintMessageService.getMessagesByComplaintId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAuthor().contains("User#"));
    }

    @Test
    void getMessagesByComplaintId_WhenAuthServiceReturnsNull_ShouldReturnFallbackNames() {
        // Arrange
        when(messageRepository.findByComplaintIdOrderByTimestampAsc(1L))
                .thenReturn(Arrays.asList(testMessage));
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(null);

        // Act
        List<ComplaintMessageDTO> result = complaintMessageService.getMessagesByComplaintId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAuthor().contains("User#"));
    }

    @Test
    void getMessagesByComplaintId_WhenAuthServiceReturnsEmptyNames_ShouldReturnFallbackNames() {
        // Arrange
        when(messageRepository.findByComplaintIdOrderByTimestampAsc(1L))
                .thenReturn(Arrays.asList(testMessage));
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of("firstName", "", "lastName", ""));

        // Act
        List<ComplaintMessageDTO> result = complaintMessageService.getMessagesByComplaintId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAuthor().contains("User#"));
    }

    @Test
    void getMessagesByComplaintId_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        when(messageRepository.findByComplaintIdOrderByTimestampAsc(1L))
                .thenReturn(Arrays.asList());

        // Act
        List<ComplaintMessageDTO> result = complaintMessageService.getMessagesByComplaintId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
