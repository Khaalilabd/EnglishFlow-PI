package com.englishflow.auth.service;

import com.englishflow.auth.entity.AuditLog;
import com.englishflow.auth.repository.AuditLogRepository;
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

    @InjectMocks
    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        // Setup if needed
    }

    @Test
    void testLogAction() {
        // Given
        Long userId = 1L;
        String action = "LOGIN";
        String details = "User logged in successfully";
        String ipAddress = "192.168.1.1";

        AuditLog auditLog = new AuditLog();
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        // When
        auditLogService.logAction(userId, action, details, ipAddress);

        // Then
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogAction_WithNullUserId() {
        // Given
        String action = "ANONYMOUS_ACTION";
        String details = "Anonymous action";
        String ipAddress = "192.168.1.1";

        AuditLog auditLog = new AuditLog();
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        // When
        auditLogService.logAction(null, action, details, ipAddress);

        // Then
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogAction_WithNullIpAddress() {
        // Given
        Long userId = 1L;
        String action = "UPDATE_PROFILE";
        String details = "Profile updated";

        AuditLog auditLog = new AuditLog();
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        // When
        auditLogService.logAction(userId, action, details, null);

        // Then
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}
