package com.englishflow.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MetricsService metricsService;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");
        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://localhost:4200");
        ReflectionTestUtils.setField(emailService, "backendUrl", "http://localhost:8081");
    }

    @Test
    void sendActivationEmail_Success() throws Exception {
        // Arrange
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        CompletableFuture<Void> result = emailService.sendActivationEmail("user@test.com", "John", "token123");

        // Assert
        assertNotNull(result);
        verify(templateEngine).process(eq("activation-email"), any(Context.class));
        verify(mailSender).send(any(MimeMessage.class));
        verify(metricsService).recordEmailSent();
    }

    @Test
    void sendPasswordResetEmail_Success() throws Exception {
        // Arrange
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        CompletableFuture<Void> result = emailService.sendPasswordResetEmail("user@test.com", "John", "resetToken");

        // Assert
        assertNotNull(result);
        verify(templateEngine).process(eq("password-reset-email"), any(Context.class));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendWelcomeEmail_Success() throws Exception {
        // Arrange
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        CompletableFuture<Void> result = emailService.sendWelcomeEmail("user@test.com", "John");

        // Assert
        assertNotNull(result);
        verify(templateEngine).process(eq("welcome-email"), any(Context.class));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendAccountCreatedEmail_Success() throws Exception {
        // Arrange
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        CompletableFuture<Void> result = emailService.sendAccountCreatedEmail(
            "user@test.com", "John", "john@test.com", "password123", "TUTOR", "activationToken");

        // Assert
        assertNotNull(result);
        verify(templateEngine).process(eq("account-created-email"), any(Context.class));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendInvitationEmail_Success() throws Exception {
        // Arrange
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        CompletableFuture<Void> result = emailService.sendInvitationEmail("user@test.com", "TUTOR", "inviteToken");

        // Assert
        assertNotNull(result);
        verify(templateEngine).process(eq("invitation-email"), any(Context.class));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendInterviewScheduledEmail_Success() throws Exception {
        // Arrange
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));
        LocalDateTime interviewDate = LocalDateTime.now().plusDays(1);

        // Act
        CompletableFuture<Void> result = emailService.sendInterviewScheduledEmail(
            "user@test.com", "John", interviewDate, "http://meet.link");

        // Assert
        assertNotNull(result);
        verify(templateEngine).process(eq("interview-scheduled-email"), any(Context.class));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendClubPaymentRequiredEmail_Success() throws Exception {
        // Arrange
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        CompletableFuture<Void> result = emailService.sendClubPaymentRequiredEmail(
            "user@test.com", "John", "English Club", 50.0, "http://payment.link");

        // Assert
        assertNotNull(result);
        verify(templateEngine).process(eq("club-payment-required-email"), any(Context.class));
        verify(mailSender).send(any(MimeMessage.class));
        verify(metricsService).recordEmailSent();
    }

    @Test
    void sendEventPaymentConfirmedEmail_Success() throws Exception {
        // Arrange
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        CompletableFuture<Void> result = emailService.sendEventPaymentConfirmedEmail(
            "user@test.com", "John", "Workshop", 100.0, "http://event.link");

        // Assert
        assertNotNull(result);
        verify(templateEngine).process(eq("event-payment-confirmed-email"), any(Context.class));
        verify(mailSender).send(any(MimeMessage.class));
        verify(metricsService).recordEmailSent();
    }
}
