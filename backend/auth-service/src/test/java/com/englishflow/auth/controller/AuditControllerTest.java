package com.englishflow.auth.controller;

import com.englishflow.auth.dto.AuditLogResponse;
import com.englishflow.auth.dto.AuditSearchRequest;
import com.englishflow.auth.entity.AuditLog;
import com.englishflow.auth.service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuditController auditController;

    private AuditLog testAuditLog;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        testAuditLog = AuditLog.builder()
                .id(1L)
                .userId(userId)
                .userEmail("test@example.com")
                .action(AuditLog.AuditAction.LOGIN_SUCCESS)
                .status(AuditLog.AuditStatus.SUCCESS)
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .details("User logged in successfully")
                .sessionId("session-123")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void searchAuditLogs_Success() {
        // Arrange
        AuditSearchRequest searchRequest = AuditSearchRequest.builder()
                .page(0)
                .size(20)
                .sortBy("createdAt")
                .sortDirection("DESC")
                .build();
        
        Page<AuditLog> auditPage = new PageImpl<>(List.of(testAuditLog));
        when(auditLogService.searchAuditLogs(
            any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
            .thenReturn(auditPage);

        // Act
        ResponseEntity<Page<AuditLogResponse>> response = 
            auditController.searchAuditLogs(searchRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(auditLogService).searchAuditLogs(
            any(), any(), any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void searchAuditLogs_WithQuickFilter() {
        // Arrange
        AuditSearchRequest searchRequest = AuditSearchRequest.builder()
                .page(0)
                .size(20)
                .sortBy("createdAt")
                .sortDirection("DESC")
                .quickFilter("TODAY")
                .build();
        
        Page<AuditLog> auditPage = new PageImpl<>(List.of(testAuditLog));
        when(auditLogService.searchAuditLogs(
            any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
            .thenReturn(auditPage);

        // Act
        ResponseEntity<Page<AuditLogResponse>> response = 
            auditController.searchAuditLogs(searchRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(searchRequest.getStartDate());
        assertNotNull(searchRequest.getEndDate());
    }

    @Test
    void searchAuditLogs_WithFilters() {
        // Arrange
        AuditSearchRequest searchRequest = AuditSearchRequest.builder()
                .userId(userId)
                .userEmail("test@example.com")
                .action(AuditLog.AuditAction.LOGIN_SUCCESS)
                .status(AuditLog.AuditStatus.SUCCESS)
                .ipAddress("192.168.1.1")
                .page(0)
                .size(20)
                .sortBy("createdAt")
                .sortDirection("ASC")
                .build();
        
        Page<AuditLog> auditPage = new PageImpl<>(List.of(testAuditLog));
        when(auditLogService.searchAuditLogs(
            eq(userId), eq("test@example.com"), eq(AuditLog.AuditAction.LOGIN_SUCCESS),
            eq(AuditLog.AuditStatus.SUCCESS), eq("192.168.1.1"), any(), any(), any(Pageable.class)))
            .thenReturn(auditPage);

        // Act
        ResponseEntity<Page<AuditLogResponse>> response = 
            auditController.searchAuditLogs(searchRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(auditLogService).searchAuditLogs(
            eq(userId), eq("test@example.com"), eq(AuditLog.AuditAction.LOGIN_SUCCESS),
            eq(AuditLog.AuditStatus.SUCCESS), eq("192.168.1.1"), any(), any(), any(Pageable.class));
    }

    @Test
    void getUserAuditLogs_Success() {
        // Arrange
        Page<AuditLog> auditPage = new PageImpl<>(List.of(testAuditLog));
        when(auditLogService.getUserAuditLogs(eq(userId), any(Pageable.class)))
            .thenReturn(auditPage);

        // Act
        ResponseEntity<Page<AuditLogResponse>> response = 
            auditController.getUserAuditLogs(userId, 0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(auditLogService).getUserAuditLogs(eq(userId), any(Pageable.class));
    }

    @Test
    void getUserAuditLogs_WithCustomPagination() {
        // Arrange
        Page<AuditLog> auditPage = new PageImpl<>(List.of(testAuditLog));
        when(auditLogService.getUserAuditLogs(eq(userId), any(Pageable.class)))
            .thenReturn(auditPage);

        // Act
        ResponseEntity<Page<AuditLogResponse>> response = 
            auditController.getUserAuditLogs(userId, 2, 50);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(auditLogService).getUserAuditLogs(eq(userId), any(Pageable.class));
    }

    @Test
    void getRecentSecurityEvents_Success() {
        // Arrange
        AuditLog securityEvent = AuditLog.builder()
                .id(2L)
                .userId(userId)
                .userEmail("test@example.com")
                .action(AuditLog.AuditAction.SUSPICIOUS_LOGIN_ATTEMPT)
                .status(AuditLog.AuditStatus.WARNING)
                .ipAddress("192.168.1.100")
                .createdAt(LocalDateTime.now())
                .build();
        
        when(auditLogService.getRecentSecurityEvents(24))
            .thenReturn(List.of(securityEvent));

        // Act
        ResponseEntity<List<AuditLogResponse>> response = 
            auditController.getRecentSecurityEvents(24);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(auditLogService).getRecentSecurityEvents(24);
    }

    @Test
    void getRecentSecurityEvents_CustomHours() {
        // Arrange
        when(auditLogService.getRecentSecurityEvents(48))
            .thenReturn(List.of(testAuditLog));

        // Act
        ResponseEntity<List<AuditLogResponse>> response = 
            auditController.getRecentSecurityEvents(48);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(auditLogService).getRecentSecurityEvents(48);
    }

    @Test
    void getAuditStatistics_Success() {
        // Arrange
        Map<String, Long> actionStats = Map.of(
            "LOGIN_SUCCESS", 100L,
            "LOGIN_FAILED", 5L,
            "LOGOUT", 80L
        );
        when(auditLogService.getAuditStatistics(30)).thenReturn(actionStats);
        when(auditLogService.getRecentSecurityEvents(24)).thenReturn(List.of(testAuditLog));

        // Act
        ResponseEntity<Map<String, Object>> response = 
            auditController.getAuditStatistics(30);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("actionStatistics"));
        assertTrue(response.getBody().containsKey("recentSecurityEvents"));
        assertTrue(response.getBody().containsKey("totalEvents"));
        assertEquals(185L, response.getBody().get("totalEvents"));
        verify(auditLogService).getAuditStatistics(30);
    }

    @Test
    void getAuditStatistics_CustomDays() {
        // Arrange
        Map<String, Long> actionStats = Map.of("LOGIN_SUCCESS", 50L);
        when(auditLogService.getAuditStatistics(7)).thenReturn(actionStats);
        when(auditLogService.getRecentSecurityEvents(24)).thenReturn(List.of());

        // Act
        ResponseEntity<Map<String, Object>> response = 
            auditController.getAuditStatistics(7);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("7 days", response.getBody().get("period"));
        verify(auditLogService).getAuditStatistics(7);
    }

    @Test
    void getFilterOptions_Success() {
        // Act
        ResponseEntity<Map<String, Object>> response = 
            auditController.getFilterOptions();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("actions"));
        assertTrue(response.getBody().containsKey("statuses"));
        assertTrue(response.getBody().containsKey("quickFilters"));
        assertTrue(response.getBody().containsKey("riskLevels"));
    }

    @Test
    void cleanupOldLogs_Success() {
        // Arrange
        doNothing().when(auditLogService).cleanupOldLogs(365);

        // Act
        ResponseEntity<Map<String, String>> response = 
            auditController.cleanupOldLogs(365);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().get("message").contains("365 days"));
        verify(auditLogService).cleanupOldLogs(365);
    }

    @Test
    void cleanupOldLogs_CustomDays() {
        // Arrange
        doNothing().when(auditLogService).cleanupOldLogs(90);

        // Act
        ResponseEntity<Map<String, String>> response = 
            auditController.cleanupOldLogs(90);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().get("message").contains("90 days"));
        verify(auditLogService).cleanupOldLogs(90);
    }

    @Test
    void exportAuditLogs_Success() {
        // Arrange
        AuditSearchRequest searchRequest = AuditSearchRequest.builder()
                .page(0)
                .size(1000)
                .sortBy("createdAt")
                .sortDirection("DESC")
                .build();
        
        Page<AuditLog> auditPage = new PageImpl<>(List.of(testAuditLog));
        when(auditLogService.searchAuditLogs(
            any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
            .thenReturn(auditPage);

        // Act
        ResponseEntity<List<AuditLogResponse>> response = 
            auditController.exportAuditLogs(searchRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(auditLogService).searchAuditLogs(
            any(), any(), any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void exportAuditLogs_LimitTo10000() {
        // Arrange
        AuditSearchRequest searchRequest = AuditSearchRequest.builder()
                .page(0)
                .size(50000) // Request more than max
                .sortBy("createdAt")
                .sortDirection("DESC")
                .build();
        
        Page<AuditLog> auditPage = new PageImpl<>(List.of(testAuditLog));
        when(auditLogService.searchAuditLogs(
            any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
            .thenReturn(auditPage);

        // Act
        ResponseEntity<List<AuditLogResponse>> response = 
            auditController.exportAuditLogs(searchRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        // Verify size was limited to 10000
        assertEquals(10000, searchRequest.getSize());
    }

    @Test
    void exportAuditLogs_WithQuickFilter() {
        // Arrange
        AuditSearchRequest searchRequest = AuditSearchRequest.builder()
                .page(0)
                .size(1000)
                .sortBy("createdAt")
                .sortDirection("DESC")
                .quickFilter("LAST_WEEK")
                .build();
        
        Page<AuditLog> auditPage = new PageImpl<>(List.of(testAuditLog));
        when(auditLogService.searchAuditLogs(
            any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
            .thenReturn(auditPage);

        // Act
        ResponseEntity<List<AuditLogResponse>> response = 
            auditController.exportAuditLogs(searchRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(searchRequest.getStartDate());
        assertNotNull(searchRequest.getEndDate());
    }
}
