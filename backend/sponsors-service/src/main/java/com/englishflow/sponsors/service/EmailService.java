package com.englishflow.sponsors.service;

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

    /**
     * Email sent to sponsor when they submit a club sponsorship request
     */
    @Async
    public void sendClubSponsorRequestReceivedEmail(String to, String firstName, String clubName, Double amount) {
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("clubName", clubName);
            context.setVariable("amount", amount);

            String html = templateEngine.process("sponsor-club-request-received-email", context);
            sendHtml(to, "Club Sponsorship Request Received – Jungle in English", html);
            log.info("Club sponsor request received email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send club sponsor request received email to: {}", to, e);
        }
    }

    /**
     * Email sent to sponsor when their club sponsorship request is approved
     */
    @Async
    public void sendClubSponsorApprovedEmail(String to, String firstName, String clubName,
                                              Double totalAmount, Double clubAllocation) {
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("clubName", clubName);
            context.setVariable("totalAmount", totalAmount);
            context.setVariable("clubAllocation", clubAllocation);
            context.setVariable("panelLink", frontendUrl + "/sponsor-panel/clubs");

            String html = templateEngine.process("sponsor-club-approved-email", context);
            sendHtml(to, "✅ Your Club Sponsorship Has Been Approved – Jungle in English", html);
            log.info("Club sponsor approved email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send club sponsor approved email to: {}", to, e);
        }
    }

    /**
     * Email sent to club president when their club receives a sponsorship
     */
    @Async
    public void sendClubPresidentFundingEmail(String to, String presidentFirstName,
                                               String clubName, String sponsorName, Double amount) {
        try {
            Context context = new Context();
            context.setVariable("presidentFirstName", presidentFirstName);
            context.setVariable("clubName", clubName);
            context.setVariable("sponsorName", sponsorName);
            context.setVariable("amount", amount);
            context.setVariable("panelLink", frontendUrl + "/user-panel/clubs");

            String html = templateEngine.process("club-president-funding-email", context);
            sendHtml(to, "🎉 Your Club Received a Sponsorship – Jungle in English", html);
            log.info("Club president funding email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send club president funding email to: {}", to, e);
        }
    }

    /**
     * Email sent to sponsor when their club sponsorship request is rejected
     */
    @Async
    public void sendClubSponsorRejectedEmail(String to, String firstName, String clubName) {
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("clubName", clubName);
            context.setVariable("panelLink", frontendUrl + "/sponsor-panel/clubs");

            String html = templateEngine.process("sponsor-club-rejected-email", context);
            sendHtml(to, "Club Sponsorship Request Update – Jungle in English", html);
            log.info("Club sponsor rejected email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send club sponsor rejected email to: {}", to, e);
        }
    }

    /**
     * Email sent to sponsor right after they submit the form — "we received your request"
     */
    @Async
    public void sendSponsorRequestReceivedEmail(String to, String firstName, String companyName) {
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("companyName", companyName);

            String html = templateEngine.process("sponsor-request-received-email", context);
            sendHtml(to, "Sponsor Application Received – Jungle in English", html);
            log.info("Sponsor request received email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send sponsor request received email to: {}", to, e);
        }
    }

    /**
     * Email sent to sponsor when the academic manager approves their request
     */
    @Async
    public void sendSponsorApprovedEmail(String to, String firstName, String companyName) {
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("companyName", companyName);
            context.setVariable("loginLink", frontendUrl + "/login");

            String html = templateEngine.process("sponsor-approved-email", context);
            sendHtml(to, "🎉 Your Sponsorship Has Been Approved – Jungle in English", html);
            log.info("Sponsor approved email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send sponsor approved email to: {}", to, e);
        }
    }

    private void sendHtml(String to, String subject, String html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        // Validate and use fromEmail, fallback to default if invalid
        String cleanFromEmail = "jungleinenglish.platform@gmail.com";
        if (fromEmail != null && !fromEmail.trim().isEmpty()) {
            cleanFromEmail = fromEmail.trim();
        }
        
        log.debug("Sending email from: {} to: {}", cleanFromEmail, to);
        
        helper.setFrom(cleanFromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }
}
