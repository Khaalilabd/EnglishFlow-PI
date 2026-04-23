package com.englishflow.auth.service;

import com.englishflow.auth.entity.AuditLog;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuditLogService auditLogService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(User.Role.STUDENT);

        when(httpServletRequest.getRemoteAddr()).thenReturn("192.168.1.1");
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
    }

    @Test
    void testLogLoginSuccess() {
        // Given
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // When
        auditLogService.logLoginSuccess(testUser, httpServletRequest);

        // Then
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogLoginFailed() {
        // Given
        String email = "test@example.com";
        String reason = "Invalid password";
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // When
        auditLogService.logLoginFailed(email, reason, httpServletRequest);

        // Then
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogLogout() {
        // Given
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // When
        auditLogService.logLogout(testUser, httpServletRequest);

        // Then
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogRegistration() {
        // Given
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // When
        auditLogService.logRegistration(testUser, httpServletRequest);

        // Then
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogPasswordResetRequest() {
        // Given
        String email = "test@example.com";
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // When
        auditLogService.logPasswordResetRequest(email, httpServletRequest);

        // Then
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogSuspiciousActivity() {
        // Given
        String email = "test@example.com";
        String reason = "Multiple failed login attempts";
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // When
        auditLogService.logSuspiciousActivity(email, reason, httpServletRequest);

        // Then
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogRateLimitExceeded() {
        // Given
        String email = "test@example.com";
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // When
        auditLogService.logRateLimitExceeded(email, httpServletRequest);

        // Then
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}
