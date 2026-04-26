package com.englishflow.auth.controller;

import com.englishflow.auth.dto.SessionSearchRequest;
import com.englishflow.auth.dto.UserSessionResponse;
import com.englishflow.auth.entity.UserSession;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.exception.InvalidTokenException;
import com.englishflow.auth.exception.SessionNotFoundException;
import com.englishflow.auth.repository.UserRepository;
import com.englishflow.auth.security.JwtUtil;
import com.englishflow.auth.service.UserSessionService;
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

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionControllerTest {

    @Mock
    private UserSessionService userSessionService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private SessionController sessionController;

    private UserSession testSession;
    private User testUser;
    private final Long userId = 1L;
    private final String token = "test-token";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        testSession = new UserSession();
        testSession.setId(1L);
        testSession.setUserId(userId);
        testSession.setSessionToken("session-token-123");
        testSession.setDeviceType("DESKTOP");
        testSession.setIpAddress("192.168.1.1");
        testSession.setStatus(UserSession.SessionStatus.ACTIVE);
        testSession.setCreatedAt(LocalDateTime.now());
        testSession.setLastActivity(LocalDateTime.now());
    }

    @Test
    void getMyActiveSessions_Success() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        when(userSessionService.getActiveUserSessions(userId)).thenReturn(List.of(testSession));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<List<UserSessionResponse>> response = 
            sessionController.getMyActiveSessions(request, "current-token");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(userSessionService).getActiveUserSessions(userId);
    }

    @Test
    void getMyActiveSessions_InvalidToken() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUserId(token)).thenThrow(new RuntimeException("Invalid token"));

        // Act & Assert
        assertThrows(InvalidTokenException.class, () -> 
            sessionController.getMyActiveSessions(request, null));
    }

    @Test
    void getMyActiveSessions_NoAuthHeader() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act & Assert
        assertThrows(InvalidTokenException.class, () -> 
            sessionController.getMyActiveSessions(request, null));
    }

    @Test
    void getMyAllSessions_Success() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        
        Page<UserSession> sessionPage = new PageImpl<>(List.of(testSession));
        when(userSessionService.getUserSessions(eq(userId), any(Pageable.class)))
            .thenReturn(sessionPage);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<Page<UserSessionResponse>> response = 
            sessionController.getMyAllSessions(request, 0, 20, "current-token");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(userSessionService).getUserSessions(eq(userId), any(Pageable.class));
    }

    @Test
    void terminateMySession_Success() {
        // Arrange
        Long sessionId = 1L;
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        when(userSessionService.terminateSession(eq(sessionId), any()))
            .thenReturn(true);

        // Act
        ResponseEntity<Map<String, String>> response = 
            sessionController.terminateMySession(sessionId, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Session terminated successfully", response.getBody().get("message"));
        verify(userSessionService).terminateSession(eq(sessionId), any());
    }

    @Test
    void terminateMySession_NotFound() {
        // Arrange
        Long sessionId = 999L;
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        when(userSessionService.terminateSession(eq(sessionId), any()))
            .thenReturn(false);

        // Act & Assert
        assertThrows(SessionNotFoundException.class, () -> 
            sessionController.terminateMySession(sessionId, request));
    }

    @Test
    void terminateOtherSessions_Success() {
        // Arrange
        UserSession currentSession = new UserSession();
        currentSession.setId(2L);
        currentSession.setUserId(userId);
        currentSession.setSessionToken("current-token");
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        when(userSessionService.getActiveUserSessions(userId))
            .thenReturn(List.of(testSession, currentSession));
        when(userSessionService.terminateSession(eq(1L), any())).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = 
            sessionController.terminateOtherSessions(request, "current-token");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().get("terminatedCount"));
        verify(userSessionService).terminateSession(eq(1L), any());
        verify(userSessionService, never()).terminateSession(eq(2L), any());
    }

    @Test
    void searchSessions_Success() {
        // Arrange
        SessionSearchRequest searchRequest = new SessionSearchRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(20);
        searchRequest.setSortBy("createdAt");
        searchRequest.setSortDirection("DESC");
        
        Page<UserSession> sessionPage = new PageImpl<>(List.of(testSession));
        when(userSessionService.searchSessions(
            any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
            .thenReturn(sessionPage);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<Page<UserSessionResponse>> response = 
            sessionController.searchSessions(searchRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void getUserSessions_Success() {
        // Arrange
        Page<UserSession> sessionPage = new PageImpl<>(List.of(testSession));
        when(userSessionService.getUserSessions(eq(userId), any(Pageable.class)))
            .thenReturn(sessionPage);

        // Act
        ResponseEntity<Page<UserSessionResponse>> response = 
            sessionController.getUserSessions(userId, 0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void getSuspiciousSessions_Success() {
        // Arrange
        testSession.setSuspicious(true);
        when(userSessionService.getSuspiciousSessions())
            .thenReturn(List.of(testSession));

        // Act
        ResponseEntity<List<UserSessionResponse>> response = 
            sessionController.getSuspiciousSessions();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void terminateSession_Success() {
        // Arrange
        Long sessionId = 1L;
        when(userSessionService.terminateSession(eq(sessionId), any()))
            .thenReturn(true);

        // Act
        ResponseEntity<Map<String, String>> response = 
            sessionController.terminateSession(sessionId, "ADMIN_TERMINATED");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Session terminated successfully", response.getBody().get("message"));
    }

    @Test
    void terminateSession_Failed() {
        // Arrange
        Long sessionId = 999L;
        when(userSessionService.terminateSession(eq(sessionId), any()))
            .thenReturn(false);

        // Act
        ResponseEntity<Map<String, String>> response = 
            sessionController.terminateSession(sessionId, "ADMIN_TERMINATED");

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Failed to terminate session", response.getBody().get("error"));
    }

    @Test
    void terminateAllUserSessions_Success() {
        // Arrange
        when(userSessionService.terminateAllUserSessions(eq(userId), any()))
            .thenReturn(3);

        // Act
        ResponseEntity<Map<String, Object>> response = 
            sessionController.terminateAllUserSessions(userId, "ADMIN_TERMINATED");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(3, response.getBody().get("terminatedCount"));
    }

    @Test
    void getSessionStatistics_Success() {
        // Arrange
        Map<String, Object> stats = Map.of(
            "totalSessions", 100L,
            "activeSessions", 50L,
            "suspiciousSessions", 5L
        );
        when(userSessionService.getSessionStatistics(30)).thenReturn(stats);

        // Act
        ResponseEntity<Map<String, Object>> response = 
            sessionController.getSessionStatistics(30);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(100L, response.getBody().get("totalSessions"));
    }

    @Test
    void getFilterOptions_Success() {
        // Act
        ResponseEntity<Map<String, Object>> response = 
            sessionController.getFilterOptions();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("statuses"));
        assertTrue(response.getBody().containsKey("deviceTypes"));
    }

    @Test
    void forceCleanup_Success() {
        // Arrange
        doNothing().when(userSessionService).cleanupSessions();

        // Act
        ResponseEntity<Map<String, String>> response = 
            sessionController.forceCleanup();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Session cleanup completed", response.getBody().get("message"));
        verify(userSessionService).cleanupSessions();
    }
}
