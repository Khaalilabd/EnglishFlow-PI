package com.englishflow.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public void sendActivationEmail(String to, String firstName, String activationToken) {
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("activationLink", frontendUrl + "/activate?token=" + activationToken);
            
            String htmlContent = templateEngine.process("activation-email", context);
            
            sendHtmlEmail(to, "Activate Your Jungle in English Account", htmlContent);
            log.info("Activation email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send activation email to: {}", to, e);
            throw new RuntimeException("Failed to send activation email", e);
        }
    }

    public void sendPasswordResetEmail(String to, String firstName, String resetToken) {
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("resetLink", frontendUrl + "/reset-password?token=" + resetToken);
            
            String htmlContent = templateEngine.process("password-reset-email", context);
            
            sendHtmlEmail(to, "Reset Your Password - Jungle in English", htmlContent);
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", to, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendWelcomeEmail(String to, String firstName) {
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            
            String htmlContent = templateEngine.process("welcome-email", context);
            
            sendHtmlEmail(to, "Welcome to Jungle in English! 🎉", htmlContent);
            log.info("Welcome email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", to, e);
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
}
