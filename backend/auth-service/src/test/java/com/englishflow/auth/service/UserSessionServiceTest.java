package com.englishflow.auth.service;

import com.englishflow.auth.entity.UserSession;
import com.englishflow.auth.repository.UserSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSessionServiceTest {

    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private UserSessionService userSessionService;

    private UserSession testSession;

    @BeforeEach
    void setUp() {
        testSession = UserSession.builder()
                .id(1L)
                .userId(100L)
                .sessionToken("sess_test123")
                .deviceInfo("Mozilla/5.0")
                .browserName("Chrome")
                .browserVersion("120.0")
                .operatingSystem("Windows")
                .deviceType("DESKTOP")
                .ipAddress("192.168.1.1")
                .country("France")
                .city("Paris")
                .status(UserSession.SessionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .lastActivity(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
    }

    @Test
    void testCreateSession_Success() {
        // Given
        Long userId = 100L;
        when(userSessionRepository.countActiveSessionsByUserId(userId)).thenReturn(2L);
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0");
        when(httpServletRequest.getRemoteAddr()).thenReturn("192.168.1.1");
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(testSession);

        // When
        UserSession session = userSessionService.createSession(userId, httpServletRequest);

        // Then
        assertNotNull(session);
        assertEquals(userId, session.getUserId());
        verify(userSessionRepository).save(any(UserSession.class));
    }

    @Test
    void testCreateSession_ExceedsMaxConcurrentSessions() {
        // Given
        Long userId = 100L;
        when(userSessionRepository.countActiveSessionsByUserId(userId)).thenReturn(5L);
        
        UserSession oldestSession = UserSession.builder()
                .id(1L)
                .userId(userId)
                .sessionToken("old_session")
                .status(UserSession.SessionStatus.ACTIVE)
                .build();
        
        when(userSessionRepository.findActiveSessionsByUserId(userId))
                .thenReturn(Arrays.asList(oldestSession));
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(httpServletRequest.getRemoteAddr()).thenReturn("192.168.1.1");
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(testSession);

        // When
        UserSession session = userSessionService.createSession(userId, httpServletRequest);

        // Then
        assertNotNull(session);
        verify(userSessionRepository).save(any(UserSession.class));
    }

    @Test
    void testGetActiveUserSessions() {
        // Given
        Long userId = 100L;
        List<UserSession> sessions = Arrays.asList(testSession);
        when(userSessionRepository.findActiveSessionsByUserId(userId)).thenReturn(sessions);

        // When
        List<UserSession> result = userSessionService.getActiveUserSessions(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testSession.getSessionToken(), result.get(0).getSessionToken());
    }

    @Test
    void testTerminateSession_Success() {
        // Given
        Long sessionId = 1L;
        when(userSessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(testSession);

        // When
        boolean result = userSessionService.terminateSession(sessionId, 
                UserSession.TerminationReason.USER_LOGOUT);

        // Then
        assertTrue(result);
        assertEquals(UserSession.SessionStatus.TERMINATED, testSession.getStatus());
        assertEquals(UserSession.TerminationReason.USER_LOGOUT, testSession.getTerminationReason());
        verify(userSessionRepository).save(testSession);
    }

    @Test
    void testTerminateSession_NotFound() {
        // Given
        Long sessionId = 999L;
        when(userSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // When
        boolean result = userSessionService.terminateSession(sessionId, 
                UserSession.TerminationReason.USER_LOGOUT);

        // Then
        assertFalse(result);
        verify(userSessionRepository, never()).save(any(UserSession.class));
    }

    @Test
    void testTerminateAllUserSessions() {
        // Given
        Long userId = 100L;
        when(userSessionRepository.terminateAllUserSessions(
                eq(userId), 
                any(UserSession.TerminationReason.class), 
                any(LocalDateTime.class)))
                .thenReturn(3);

        // When
        int count = userSessionService.terminateAllUserSessions(userId, 
                UserSession.TerminationReason.ADMIN_TERMINATED);

        // Then
        assertEquals(3, count);
        verify(userSessionRepository).terminateAllUserSessions(
                eq(userId), 
                eq(UserSession.TerminationReason.ADMIN_TERMINATED), 
                any(LocalDateTime.class));
    }

    @Test
    void testGetSessionByToken() {
        // Given
        String token = "sess_test123";
        when(userSessionRepository.findBySessionToken(token)).thenReturn(Optional.of(testSession));

        // When
        Optional<UserSession> result = userSessionService.getSessionByToken(token);

        // Then
        assertTrue(result.isPresent());
        assertEquals(token, result.get().getSessionToken());
    }

    @Test
    void testUpdateSessionActivity() {
        // Given
        String token = "sess_test123";
        when(userSessionRepository.findBySessionToken(token)).thenReturn(Optional.of(testSession));
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(testSession);

        // When
        userSessionService.updateSessionActivity(token);

        // Then
        verify(userSessionRepository).save(testSession);
    }
}
