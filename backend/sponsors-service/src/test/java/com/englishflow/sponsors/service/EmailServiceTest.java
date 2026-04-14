package com.englishflow.sponsors.service;

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
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");
        ReflectionTestUtils.setField(emailService, "frontendUrl", "http://localhost:4200");
    }

    @Test
    void sendClubSponsorRequestReceivedEmail_ShouldSendEmail() {
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendClubSponsorRequestReceivedEmail("sponsor@test.com", "John", "Tech Club", 1000.0);

        verify(templateEngine, times(1)).process(eq("sponsor-club-request-received-email"), any(Context.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendClubSponsorApprovedEmail_ShouldSendEmail() {
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendClubSponsorApprovedEmail("sponsor@test.com", "John", "Tech Club", 1000.0, 500.0);

        verify(templateEngine, times(1)).process(eq("sponsor-club-approved-email"), any(Context.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendClubPresidentFundingEmail_ShouldSendEmail() {
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendClubPresidentFundingEmail("president@test.com", "Jane", "Tech Club", "Tech Corp", 1000.0);

        verify(templateEngine, times(1)).process(eq("club-president-funding-email"), any(Context.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendClubSponsorRejectedEmail_ShouldSendEmail() {
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendClubSponsorRejectedEmail("sponsor@test.com", "John", "Tech Club");

        verify(templateEngine, times(1)).process(eq("sponsor-club-rejected-email"), any(Context.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendSponsorRequestReceivedEmail_ShouldSendEmail() {
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendSponsorRequestReceivedEmail("sponsor@test.com", "John", "Tech Corp");

        verify(templateEngine, times(1)).process(eq("sponsor-request-received-email"), any(Context.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendSponsorApprovedEmail_ShouldSendEmail() {
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Test</html>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendSponsorApprovedEmail("sponsor@test.com", "John", "Tech Corp");

        verify(templateEngine, times(1)).process(eq("sponsor-approved-email"), any(Context.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
