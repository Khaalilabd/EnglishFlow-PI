package com.englishflow.auth.controller;

import com.englishflow.auth.dto.AcceptInvitationRequest;
import com.englishflow.auth.dto.AuthResponse;
import com.englishflow.auth.dto.InvitationRequest;
import com.englishflow.auth.dto.InvitationResponse;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.security.JwtUtil;
import com.englishflow.auth.service.InvitationService;
import com.englishflow.auth.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationControllerTest {

    @Mock
    private InvitationService invitationService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private InvitationController invitationController;

    private InvitationRequest invitationRequest;
    private InvitationResponse invitationResponse;
    private User testUser;

    @BeforeEach
    void setUp() {
        invitationRequest = new InvitationRequest();
        invitationRequest.setEmail("newuser@example.com");
        invitationRequest.setRole("TUTOR");

        invitationResponse = InvitationResponse.builder()
                .id(1L)
                .email("newuser@example.com")
                .role("TUTOR")
                .token("invitation-token-123")
                .used(false)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("newuser@example.com");
        testUser.setFirstName("New");
        testUser.setLastName("User");
        testUser.setRole(User.Role.TUTOR);
    }

    @Test
    void testSendInvitation_Success() {
        // Arrange
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(invitationService.sendInvitation(any(InvitationRequest.class), eq(1L)))
            .thenReturn(invitationResponse);

        // Act
        ResponseEntity<InvitationResponse> response = 
            invitationController.sendInvitation(invitationRequest);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("newuser@example.com", response.getBody().getEmail());
        verify(invitationService).sendInvitation(any(InvitationRequest.class), eq(1L));
    }

    @Test
    void testGetInvitationByToken_Success() {
        // Arrange
        when(invitationService.getInvitationByToken("invitation-token-123"))
            .thenReturn(invitationResponse);

        // Act
        ResponseEntity<InvitationResponse> response = 
            invitationController.getInvitationByToken("invitation-token-123");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("invitation-token-123", response.getBody().getToken());
    }

    @Test
    void testAcceptInvitation_Success() {
        // Arrange
        AcceptInvitationRequest acceptRequest = new AcceptInvitationRequest();
        acceptRequest.setToken("invitation-token-123");
        acceptRequest.setPassword("Password123!");
        acceptRequest.setFirstName("New");
        acceptRequest.setLastName("User");

        when(invitationService.acceptInvitation(any(AcceptInvitationRequest.class)))
            .thenReturn(testUser);
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong()))
            .thenReturn("jwt-token-123");

        // Act
        ResponseEntity<AuthResponse> response = 
            invitationController.acceptInvitation(acceptRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("jwt-token-123", response.getBody().getToken());
        assertEquals("newuser@example.com", response.getBody().getEmail());
    }

    @Test
    void testGetAllInvitations_Success() {
        // Arrange
        List<InvitationResponse> invitations = Arrays.asList(invitationResponse);
        when(invitationService.getAllInvitations()).thenReturn(invitations);

        // Act
        ResponseEntity<List<InvitationResponse>> response = 
            invitationController.getAllInvitations();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetPendingInvitations_Success() {
        // Arrange
        List<InvitationResponse> invitations = Arrays.asList(invitationResponse);
        when(invitationService.getPendingInvitations()).thenReturn(invitations);

        // Act
        ResponseEntity<List<InvitationResponse>> response = 
            invitationController.getPendingInvitations();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testCancelInvitation_Success() {
        // Arrange
        doNothing().when(invitationService).cancelInvitation(1L);

        // Act
        ResponseEntity<Map<String, String>> response = 
            invitationController.cancelInvitation(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("message"));
        verify(invitationService).cancelInvitation(1L);
    }

    @Test
    void testResendInvitation_Success() {
        // Arrange
        when(invitationService.resendInvitation(1L)).thenReturn(invitationResponse);

        // Act
        ResponseEntity<InvitationResponse> response = 
            invitationController.resendInvitation(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(invitationService).resendInvitation(1L);
    }

    @Test
    void testCleanupExpiredInvitations_Success() {
        // Arrange
        doNothing().when(invitationService).cleanupExpiredInvitations();

        // Act
        ResponseEntity<Map<String, String>> response = 
            invitationController.cleanupExpiredInvitations();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("message"));
        verify(invitationService).cleanupExpiredInvitations();
    }
}
