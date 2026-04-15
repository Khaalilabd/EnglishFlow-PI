package com.englishflow.auth.service;

import com.englishflow.auth.dto.AcceptInvitationRequest;
import com.englishflow.auth.dto.InvitationRequest;
import com.englishflow.auth.dto.InvitationResponse;
import com.englishflow.auth.entity.Invitation;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.exception.EmailAlreadyExistsException;
import com.englishflow.auth.exception.InvitationAlreadyUsedException;
import com.englishflow.auth.exception.InvitationExpiredException;
import com.englishflow.auth.repository.InvitationRepository;
import com.englishflow.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationServiceTest {

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private InvitationService invitationService;

    private Invitation testInvitation;
    private InvitationRequest invitationRequest;

    @BeforeEach
    void setUp() {
        testInvitation = Invitation.builder()
                .id(1L)
                .email("tutor@example.com")
                .token("test-token-123")
                .role(User.Role.TUTOR)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .used(false)
                .invitedBy(1L)
                .build();

        invitationRequest = new InvitationRequest();
        invitationRequest.setEmail("tutor@example.com");
        invitationRequest.setRole("TUTOR");
    }

    @Test
    void testSendInvitation_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(invitationRepository.existsByEmailAndUsedFalse(anyString())).thenReturn(false);
        when(invitationRepository.save(any(Invitation.class))).thenReturn(testInvitation);
        when(emailService.sendInvitationEmail(anyString(), anyString(), anyString()))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(null));

        // When
        InvitationResponse result = invitationService.sendInvitation(invitationRequest, 1L);

        // Then
        assertNotNull(result);
        assertEquals("tutor@example.com", result.getEmail());
        verify(invitationRepository).save(any(Invitation.class));
        verify(emailService).sendInvitationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendInvitation_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            invitationService.sendInvitation(invitationRequest, 1L);
        });
    }

    @Test
    void testSendInvitation_InvalidRole() {
        // Given
        invitationRequest.setRole("STUDENT");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            invitationService.sendInvitation(invitationRequest, 1L);
        });
    }

    @Test
    void testGetInvitationByToken_Success() {
        // Given
        when(invitationRepository.findByToken("test-token-123")).thenReturn(Optional.of(testInvitation));

        // When
        InvitationResponse result = invitationService.getInvitationByToken("test-token-123");

        // Then
        assertNotNull(result);
        assertEquals("tutor@example.com", result.getEmail());
    }

    @Test
    void testGetInvitationByToken_AlreadyUsed() {
        // Given
        testInvitation.setUsed(true);
        when(invitationRepository.findByToken("test-token-123")).thenReturn(Optional.of(testInvitation));

        // When & Then
        assertThrows(InvitationAlreadyUsedException.class, () -> {
            invitationService.getInvitationByToken("test-token-123");
        });
    }

    @Test
    void testGetInvitationByToken_Expired() {
        // Given
        testInvitation.setExpiryDate(LocalDateTime.now().minusDays(1));
        when(invitationRepository.findByToken("test-token-123")).thenReturn(Optional.of(testInvitation));

        // When & Then
        assertThrows(InvitationExpiredException.class, () -> {
            invitationService.getInvitationByToken("test-token-123");
        });
    }

    @Test
    void testAcceptInvitation_Success() {
        // Given
        AcceptInvitationRequest request = new AcceptInvitationRequest();
        request.setToken("test-token-123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");
        request.setPhone("0612345678");
        request.setCin("AB123456");
        request.setDateOfBirth("1990-01-01");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("tutor@example.com");

        when(invitationRepository.findByToken("test-token-123")).thenReturn(Optional.of(testInvitation));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(invitationRepository.save(any(Invitation.class))).thenReturn(testInvitation);
        when(emailService.sendWelcomeEmail(anyString(), anyString()))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(null));

        // When
        User result = invitationService.acceptInvitation(request);

        // Then
        assertNotNull(result);
        assertEquals("tutor@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
        verify(invitationRepository).save(testInvitation);
    }

    @Test
    void testGetAllInvitations_Success() {
        // Given
        when(invitationRepository.findAll()).thenReturn(Arrays.asList(testInvitation));

        // When
        List<InvitationResponse> result = invitationService.getAllInvitations();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetPendingInvitations_Success() {
        // Given
        when(invitationRepository.findByUsedFalse()).thenReturn(Arrays.asList(testInvitation));

        // When
        List<InvitationResponse> result = invitationService.getPendingInvitations();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testCancelInvitation_Success() {
        // Given
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(testInvitation));
        doNothing().when(invitationRepository).delete(testInvitation);

        // When
        invitationService.cancelInvitation(1L);

        // Then
        verify(invitationRepository).delete(testInvitation);
    }

    @Test
    void testResendInvitation_Success() {
        // Given
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(testInvitation));
        when(invitationRepository.save(any(Invitation.class))).thenReturn(testInvitation);
        when(emailService.sendInvitationEmail(anyString(), anyString(), anyString()))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(null));

        // When
        InvitationResponse result = invitationService.resendInvitation(1L);

        // Then
        assertNotNull(result);
        verify(invitationRepository).save(testInvitation);
        verify(emailService).sendInvitationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testCleanupExpiredInvitations_Success() {
        // Given
        List<Invitation> expiredInvitations = Arrays.asList(testInvitation);
        when(invitationRepository.findByExpiryDateBeforeAndUsedFalse(any(LocalDateTime.class)))
                .thenReturn(expiredInvitations);
        doNothing().when(invitationRepository).deleteAll(expiredInvitations);

        // When
        invitationService.cleanupExpiredInvitations();

        // Then
        verify(invitationRepository).deleteAll(expiredInvitations);
    }
}
