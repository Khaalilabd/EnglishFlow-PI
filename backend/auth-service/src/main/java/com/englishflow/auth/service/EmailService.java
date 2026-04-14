package com.englishflow.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MetricsService metricsService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${app.backend.url:http://localhost:8081}")
    private String backendUrl;

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendActivationEmail(String to, String firstName, String activationToken) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        // Pointer vers le backend pour afficher la page activation-success
        context.setVariable("activationLink", backendUrl + "/auth/activate?token=" + activationToken);
        
        String htmlContent = templateEngine.process("activation-email", context);
        
        try {
            sendHtmlEmail(to, "Activate Your Jungle in English Account", htmlContent);
            log.info("Activation email sent to: {}", to);
            metricsService.recordEmailSent();
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("Failed to send activation email to: {}", to, e);
            metricsService.recordEmailFailed();
            return CompletableFuture.failedFuture(
                new com.englishflow.auth.exception.EmailSendException("Failed to send activation email to: " + to, e)
            );
        }
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendPasswordResetEmail(String to, String firstName, String resetToken) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("resetLink", frontendUrl + "/reset-password?token=" + resetToken);
        
        String htmlContent = templateEngine.process("password-reset-email", context);
        
        try {
            sendHtmlEmail(to, "Reset Your Password - Jungle in English", htmlContent);
            log.info("Password reset email sent to: {}", to);
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", to, e);
            return CompletableFuture.failedFuture(
                new com.englishflow.auth.exception.EmailSendException("Failed to send password reset email to: " + to, e)
            );
        }
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendWelcomeEmail(String to, String firstName) {
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            
            String htmlContent = templateEngine.process("welcome-email", context);
            
            sendHtmlEmail(to, "Welcome to Jungle in English! 🎉", htmlContent);
            log.info("Welcome email sent to: {}", to);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", to, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendAccountCreatedEmail(String to, String firstName, String email, String password, String role, String activationToken) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("email", email);
        context.setVariable("password", password);
        context.setVariable("role", role);
        context.setVariable("activationLink", backendUrl + "/auth/activate?token=" + activationToken);
        
        String htmlContent = templateEngine.process("account-created-email", context);
        
        try {
            sendHtmlEmail(to, "Your Jungle in English Account - Login Credentials", htmlContent);
            log.info("Account created email sent to: {}", to);
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("Failed to send account created email to: {}", to, e);
            return CompletableFuture.failedFuture(
                new com.englishflow.auth.exception.EmailSendException("Failed to send account created email to: " + to, e)
            );
        }
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendInvitationEmail(String to, String role, String invitationToken) {
        log.info("Preparing to send invitation email to: {}", to);
        Context context = new Context();
        context.setVariable("role", role);
        context.setVariable("invitationLink", frontendUrl + "/accept-invitation?token=" + invitationToken);
        
        log.info("Processing email template...");
        String htmlContent = templateEngine.process("invitation-email", context);
        
        try {
            log.info("Sending invitation email...");
            sendHtmlEmail(to, "You're Invited to Join Jungle in English! 🎉", htmlContent);
            log.info("✅ Invitation email sent successfully to: {}", to);
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("❌ Failed to send invitation email to: {}", to, e);
            log.error("Error details: {}", e.getMessage());
            return CompletableFuture.failedFuture(
                new com.englishflow.auth.exception.EmailSendException("Failed to send invitation email to: " + to, e)
            );
        }
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendApplicationSubmittedEmail(String to, String firstName) {
        Context context = new Context();
        context.setVariable("firstName", firstName);

        String htmlContent = templateEngine.process("application-submitted-email", context);

        try {
            sendHtmlEmail(to, "Application Received - Jungle in English", htmlContent);
            log.info("Application submitted email sent to: {}", to);
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("Failed to send application submitted email to: {}", to, e);
            return CompletableFuture.failedFuture(
                new com.englishflow.auth.exception.EmailSendException("Failed to send application submitted email to: " + to, e)
            );
        }
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendApplicationUnderReviewEmail(String to, String firstName) {
        Context context = new Context();
        context.setVariable("firstName", firstName);

        String htmlContent = templateEngine.process("application-under-review-email", context);

        try {
            sendHtmlEmail(to, "Application Under Review - Jungle in English", htmlContent);
            log.info("Application under review email sent to: {}", to);
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("Failed to send application under review email to: {}", to, e);
            return CompletableFuture.failedFuture(
                new com.englishflow.auth.exception.EmailSendException("Failed to send application under review email to: " + to, e)
            );
        }
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendInterviewScheduledEmail(String to, String firstName, java.time.LocalDateTime interviewDate, String meetingLink) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("interviewDate", interviewDate);
        context.setVariable("meetingLink", meetingLink);

        String htmlContent = templateEngine.process("interview-scheduled-email", context);

        try {
            sendHtmlEmail(to, "Interview Scheduled - Jungle in English", htmlContent);
            log.info("Interview scheduled email sent to: {}", to);
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("Failed to send interview scheduled email to: {}", to, e);
            return CompletableFuture.failedFuture(
                new com.englishflow.auth.exception.EmailSendException("Failed to send interview scheduled email to: " + to, e)
            );
        }
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendInterviewCancellationEmail(String to, String firstName, java.time.LocalDateTime interviewDate, String reason) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("interviewDate", interviewDate);
        context.setVariable("reason", reason != null && !reason.isEmpty() ? reason : "Administrative reasons");

        String htmlContent = templateEngine.process("interview-cancellation-email", context);

        try {
            sendHtmlEmail(to, "Interview Cancelled - Jungle in English", htmlContent);
            log.info("Interview cancellation email sent to: {}", to);
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("Failed to send interview cancellation email to: {}", to, e);
            return CompletableFuture.failedFuture(
                new com.englishflow.auth.exception.EmailSendException("Failed to send interview cancellation email to: " + to, e)
            );
        }
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendTestPendingEmail(String to, String firstName) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("testLink", frontendUrl + "/tutor-test");

        String htmlContent = templateEngine.process("test-pending-email", context);

        try {
            sendHtmlEmail(to, "Skills Test Available - Jungle in English", htmlContent);
            log.info("Test pending email sent to: {}", to);
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("Failed to send test pending email to: {}", to, e);
            return CompletableFuture.failedFuture(
                new com.englishflow.auth.exception.EmailSendException("Failed to send test pending email to: " + to, e)
            );
        }
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendTutorAccountCreatedEmail(String to, String firstName, String tempPassword) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("email", to);
        context.setVariable("password", tempPassword);
        context.setVariable("loginLink", frontendUrl + "/login");

        String htmlContent = templateEngine.process("tutor-account-created-email", context);

        try {
            sendHtmlEmail(to, "Welcome to Jungle in English - Tutor Account Created! 🎉", htmlContent);
            log.info("Tutor account created email sent to: {}", to);
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("Failed to send tutor account created email to: {}", to, e);
            return CompletableFuture.failedFuture(
                new com.englishflow.auth.exception.EmailSendException("Failed to send tutor account created email to: " + to, e)
            );
        }
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendApplicationRejectedEmail(String to, String firstName, String reason) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("reason", reason);

        String htmlContent = templateEngine.process("application-rejected-email", context);

        try {
            sendHtmlEmail(to, "Application Update - Jungle in English", htmlContent);
            log.info("Application rejected email sent to: {}", to);
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("Failed to send application rejected email to: {}", to, e);
            return CompletableFuture.failedFuture(
                new com.englishflow.auth.exception.EmailSendException("Failed to send application rejected email to: " + to, e)
            );
        }
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendClubPaymentRequiredEmail(String to, String firstName, String clubName, Double registrationFee, String paymentLink) {
        log.info("Sending club payment required email to: {}", to);
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("clubName", clubName);
            context.setVariable("registrationFee", registrationFee);
            context.setVariable("paymentLink", paymentLink);
            context.setVariable("frontendUrl", frontendUrl);

            String htmlContent = templateEngine.process("club-payment-required-email", context);
            sendHtmlEmail(to, "Action Required: Payment to join " + clubName, htmlContent);
            log.info("Club payment required email sent to: {}", to);
            metricsService.recordEmailSent();
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("Failed to send club payment required email to: {}", to, e);
            metricsService.recordEmailFailed();
            return CompletableFuture.failedFuture(
                new com.englishflow.auth.exception.EmailSendException("Failed to send club payment required email to: " + to, e)
            );
        }
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendClubMembershipRequestPendingEmail(String to, String firstName, String clubName, String message) {
        log.info("🔵 Starting sendClubMembershipRequestPendingEmail");
        log.info("🔵 Parameters - to: {}, firstName: {}, clubName: {}, message: {}", to, firstName, clubName, message);
        
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("clubName", clubName);
            context.setVariable("message", message);
            context.setVariable("clubsLink", frontendUrl + "/user-panel/clubs");
            context.setVariable("frontendUrl", frontendUrl);
            
            log.info("🔵 Processing template...");
            String htmlContent = templateEngine.process("club-membership-request-pending-email", context);
            log.info("🔵 Template processed successfully. HTML length: {}", htmlContent.length());
            
            log.info("🔵 Sending email from {} to {} with subject: Club Membership Request Pending - {}", fromEmail, to, clubName);
            sendHtmlEmail(to, "Club Membership Request Pending - " + clubName, htmlContent);
            log.info("✅ Club membership request pending email SUCCESSFULLY SENT to: {}", to);
            
            metricsService.recordEmailSent();
            log.info("✅ Metrics recorded");
            
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("❌ MessagingException while sending club membership request pending email to: {}", to, e);
            log.error("❌ Exception message: {}", e.getMessage());
            log.error("❌ Exception cause: {}", e.getCause());
            metricsService.recordEmailFailed();
            return CompletableFuture.failedFuture(
                new com.englishflow.auth.exception.EmailSendException("Failed to send club membership request pending email to: " + to, e)
            );
        } catch (Exception e) {
            log.error("❌ Unexpected exception while sending club membership request pending email to: {}", to, e);
            log.error("❌ Exception type: {}", e.getClass().getName());
            log.error("❌ Exception message: {}", e.getMessage());
            metricsService.recordEmailFailed();
            return CompletableFuture.failedFuture(e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendEventPaymentConfirmedEmail(String to, String firstName, String eventTitle, Double amount, String eventLink) {
        log.info("Sending event payment confirmed email to: {}", to);
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("eventTitle", eventTitle);
            context.setVariable("amount", amount);
            context.setVariable("eventLink", eventLink);
            context.setVariable("frontendUrl", frontendUrl);

            String htmlContent = templateEngine.process("event-payment-confirmed-email", context);
            sendHtmlEmail(to, "✅ Registration Confirmed — " + eventTitle, htmlContent);
            log.info("Event payment confirmed email sent to: {}", to);
            metricsService.recordEmailSent();
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("Failed to send event payment confirmed email to: {}", to, e);
            metricsService.recordEmailFailed();
            return CompletableFuture.failedFuture(
                new com.englishflow.auth.exception.EmailSendException("Failed to send event payment confirmed email to: " + to, e)
            );
        }
    }
}
