package com.englishflow.auth.controller;

import com.englishflow.auth.dto.UserSessionResponse;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.entity.UserSession;
import com.englishflow.auth.repository.UserRepository;
import com.englishflow.auth.security.JwtUtil;
import com.englishflow.auth.service.UserSessionService;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@example.com");

        testSession = UserSession.builder()
                .id(1L)
                .userId(1L)
                .sessionToken("test-token-123")
                .deviceType("DESKTOP")
                .ipAddress("192.168.1.1")
                .status(UserSession.SessionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .lastActivity(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetMyActiveSessions_Success() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtil.extractUserId("valid-token")).thenReturn(1L);
        when(userSessionService.getActiveUserSessions(1L)).thenReturn(Arrays.asList(testSession));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<List<UserSessionResponse>> response = 
            sessionController.getMyActiveSessions(request, "test-token-123");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(userSessionService).getActiveUserSessions(1L);
    }

    @Test
    void testGetMyAllSessions_Success() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtil.extractUserId("valid-token")).thenReturn(1L);
        Page<UserSession> page = new PageImpl<>(Arrays.asList(testSession));
        when(userSessionService.getUserSessions(eq(1L), any(Pageable.class))).thenReturn(page);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<Page<UserSessionResponse>> response = 
            sessionController.getMyAllSessions(request, 0, 20, "test-token-123");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void testTerminateMySession_Success() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtil.extractUserId("valid-token")).thenReturn(1L);
        when(userSessionService.terminateSession(eq(1L), any())).thenReturn(true);

        // Act
        ResponseEntity<Map<String, String>> response = 
            sessionController.terminateMySession(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("message"));
    }

    @Test
    void testTerminateOtherSessions_Success() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtil.extractUserId("valid-token")).thenReturn(1L);
        
        UserSession session2 = UserSession.builder()
                .id(2L)
                .userId(1L)
                .sessionToken("other-token")
                .status(UserSession.SessionStatus.ACTIVE)
                .build();
        
        when(userSessionService.getActiveUserSessions(1L))
            .thenReturn(Arrays.asList(testSession, session2));
        when(userSessionService.terminateSession(eq(2L), any())).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = 
            sessionController.terminateOtherSessions(request, "test-token-123");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().get("terminatedCount"));
    }

    @Test
    void testGetSessionStatistics_Success() {
        // Arrange
        Map<String, Object> stats = Map.of(
            "totalSessions", 100,
            "activeSessions", 50
        );
        when(userSessionService.getSessionStatistics(30)).thenReturn(stats);

        // Act
        ResponseEntity<Map<String, Object>> response = 
            sessionController.getSessionStatistics(30);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(100, response.getBody().get("totalSessions"));
    }

    @Test
    void testGetFilterOptions_Success() {
        // Act
        ResponseEntity<Map<String, Object>> response = 
            sessionController.getFilterOptions();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("statuses"));
        assertTrue(response.getBody().containsKey("deviceTypes"));
    }

    @Test
    void testForceCleanup_Success() {
        // Arrange
        doNothing().when(userSessionService).cleanupSessions();

        // Act
        ResponseEntity<Map<String, String>> response = 
            sessionController.forceCleanup();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(userSessionService).cleanupSessions();
    }
}
