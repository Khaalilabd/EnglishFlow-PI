package com.englishflow.auth.controller;

import com.englishflow.auth.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @Test
    void sendClubMembershipRequestPendingEmail_Success() {
        // Arrange
        EmailController.ClubMembershipRequestEmailDTO dto = new EmailController.ClubMembershipRequestEmailDTO();
        dto.setEmail("test@example.com");
        dto.setFirstName("John");
        dto.setClubName("English Club");
        dto.setMessage("Welcome to our club!");
        
        when(emailService.sendClubMembershipRequestPendingEmail(
            anyString(), anyString(), anyString(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        ResponseEntity<String> response = 
            emailController.sendClubMembershipRequestPendingEmail(dto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Email queued for sending", response.getBody());
        verify(emailService).sendClubMembershipRequestPendingEmail(
            "test@example.com", "John", "English Club", "Welcome to our club!");
    }

    @Test
    void sendClubMembershipRequestPendingEmail_ServiceThrowsException() {
        // Arrange
        EmailController.ClubMembershipRequestEmailDTO dto = new EmailController.ClubMembershipRequestEmailDTO();
        dto.setEmail("test@example.com");
        dto.setFirstName("John");
        dto.setClubName("English Club");
        dto.setMessage("Welcome!");
        
        when(emailService.sendClubMembershipRequestPendingEmail(
            anyString(), anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("Email service error"));

        // Act
        ResponseEntity<String> response = 
            emailController.sendClubMembershipRequestPendingEmail(dto);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("Failed to queue email", response.getBody());
    }

    @Test
    void sendClubPaymentRequiredEmail_Success() {
        // Arrange
        EmailController.ClubPaymentRequiredEmailDTO dto = new EmailController.ClubPaymentRequiredEmailDTO();
        dto.setEmail("test@example.com");
        dto.setFirstName("John");
        dto.setClubName("English Club");
        dto.setRegistrationFee("50.00");
        dto.setPaymentLink("https://payment.example.com/pay");
        
        when(emailService.sendClubPaymentRequiredEmail(
            anyString(), anyString(), anyString(), anyDouble(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        ResponseEntity<String> response = 
            emailController.sendClubPaymentRequiredEmail(dto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Email queued for sending", response.getBody());
        verify(emailService).sendClubPaymentRequiredEmail(
            "test@example.com", "John", "English Club", 50.00, "https://payment.example.com/pay");
    }

    @Test
    void sendClubPaymentRequiredEmail_NullFee() {
        // Arrange
        EmailController.ClubPaymentRequiredEmailDTO dto = new EmailController.ClubPaymentRequiredEmailDTO();
        dto.setEmail("test@example.com");
        dto.setFirstName("John");
        dto.setClubName("English Club");
        dto.setRegistrationFee(null);
        dto.setPaymentLink("https://payment.example.com/pay");
        
        when(emailService.sendClubPaymentRequiredEmail(
            anyString(), anyString(), anyString(), eq(0.0), anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        ResponseEntity<String> response = 
            emailController.sendClubPaymentRequiredEmail(dto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Email queued for sending", response.getBody());
        verify(emailService).sendClubPaymentRequiredEmail(
            "test@example.com", "John", "English Club", 0.0, "https://payment.example.com/pay");
    }

    @Test
    void sendClubPaymentRequiredEmail_ServiceThrowsException() {
        // Arrange
        EmailController.ClubPaymentRequiredEmailDTO dto = new EmailController.ClubPaymentRequiredEmailDTO();
        dto.setEmail("test@example.com");
        dto.setFirstName("John");
        dto.setClubName("English Club");
        dto.setRegistrationFee("50.00");
        dto.setPaymentLink("https://payment.example.com/pay");
        
        when(emailService.sendClubPaymentRequiredEmail(
            anyString(), anyString(), anyString(), anyDouble(), anyString()))
            .thenThrow(new RuntimeException("Email service error"));

        // Act
        ResponseEntity<String> response = 
            emailController.sendClubPaymentRequiredEmail(dto);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("Failed to queue email", response.getBody());
    }
}
