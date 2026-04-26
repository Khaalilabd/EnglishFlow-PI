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
    private final Long userId = 1L;
    private final String token = "test-token-123";

    @BeforeEach
    void setUp() {
        invitationRequest = new InvitationRequest();
        invitationRequest.setEmail("invited@example.com");
        invitationRequest.setRole("STUDENT");

        invitationResponse = InvitationResponse.builder()
                .id(1L)
                .email("invited@example.com")
                .role("STUDENT")
                .token(token)
                .used(false)
                .invitedBy(userId)
                .createdAt(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        testUser = new User();
        testUser.setId(2L);
        testUser.setEmail("invited@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(User.Role.STUDENT);
    }

    @Test
    void sendInvitation_Success() {
        // Arrange
        when(securityUtil.getCurrentUserId()).thenReturn(userId);
        when(invitationService.sendInvitation(any(InvitationRequest.class), eq(userId)))
                .thenReturn(invitationResponse);

        // Act
        ResponseEntity<InvitationResponse> response = 
            invitationController.sendInvitation(invitationRequest);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("invited@example.com", response.getBody().getEmail());
        assertFalse(response.getBody().isUsed());
        verify(securityUtil).getCurrentUserId();
        verify(invitationService).sendInvitation(any(InvitationRequest.class), eq(userId));
    }

    @Test
    void getInvitationByToken_Success() {
        // Arrange
        when(invitationService.getInvitationByToken(token))
                .thenReturn(invitationResponse);

        // Act
        ResponseEntity<InvitationResponse> response = 
            invitationController.getInvitationByToken(token);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(token, response.getBody().getToken());
        verify(invitationService).getInvitationByToken(token);
    }

    @Test
    void acceptInvitation_Success() {
        // Arrange
        AcceptInvitationRequest acceptRequest = new AcceptInvitationRequest();
        acceptRequest.setToken(token);
        acceptRequest.setPassword("Password123!");
        acceptRequest.setFirstName("John");
        acceptRequest.setLastName("Doe");

        String jwtToken = "jwt-token-123";
        
        when(invitationService.acceptInvitation(any(AcceptInvitationRequest.class)))
                .thenReturn(testUser);
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong()))
                .thenReturn(jwtToken);

        // Act
        ResponseEntity<AuthResponse> response = 
            invitationController.acceptInvitation(acceptRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(jwtToken, response.getBody().getToken());
        assertEquals("invited@example.com", response.getBody().getEmail());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("STUDENT", response.getBody().getRole());
        verify(invitationService).acceptInvitation(any(AcceptInvitationRequest.class));
        verify(jwtUtil).generateToken("invited@example.com", "STUDENT", 2L);
    }

    @Test
    void getAllInvitations_Success() {
        // Arrange
        List<InvitationResponse> invitations = List.of(invitationResponse);
        when(invitationService.getAllInvitations()).thenReturn(invitations);

        // Act
        ResponseEntity<List<InvitationResponse>> response = 
            invitationController.getAllInvitations();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(invitationService).getAllInvitations();
    }

    @Test
    void getPendingInvitations_Success() {
        // Arrange
        List<InvitationResponse> pendingInvitations = List.of(invitationResponse);
        when(invitationService.getPendingInvitations()).thenReturn(pendingInvitations);

        // Act
        ResponseEntity<List<InvitationResponse>> response = 
            invitationController.getPendingInvitations();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertFalse(response.getBody().get(0).isUsed());
        verify(invitationService).getPendingInvitations();
    }

    @Test
    void cancelInvitation_Success() {
        // Arrange
        Long invitationId = 1L;
        doNothing().when(invitationService).cancelInvitation(invitationId);

        // Act
        ResponseEntity<Map<String, String>> response = 
            invitationController.cancelInvitation(invitationId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Invitation cancelled successfully", response.getBody().get("message"));
        verify(invitationService).cancelInvitation(invitationId);
    }

    @Test
    void resendInvitation_Success() {
        // Arrange
        Long invitationId = 1L;
        when(invitationService.resendInvitation(invitationId))
                .thenReturn(invitationResponse);

        // Act
        ResponseEntity<InvitationResponse> response = 
            invitationController.resendInvitation(invitationId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("invited@example.com", response.getBody().getEmail());
        verify(invitationService).resendInvitation(invitationId);
    }

    @Test
    void cleanupExpiredInvitations_Success() {
        // Arrange
        doNothing().when(invitationService).cleanupExpiredInvitations();

        // Act
        ResponseEntity<Map<String, String>> response = 
            invitationController.cleanupExpiredInvitations();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Expired invitations cleaned up successfully", response.getBody().get("message"));
        verify(invitationService).cleanupExpiredInvitations();
    }
}
