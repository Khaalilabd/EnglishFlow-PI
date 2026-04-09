package com.englishflow.auth.controller;

import com.englishflow.auth.service.EmailService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/club-membership-request-pending")
    public ResponseEntity<String> sendClubMembershipRequestPendingEmail(@RequestBody ClubMembershipRequestEmailDTO dto) {
        log.info("Received request to send club membership request pending email to: {}", dto.getEmail());
        try {
            CompletableFuture<Void> future = emailService.sendClubMembershipRequestPendingEmail(
                dto.getEmail(),
                dto.getFirstName(),
                dto.getClubName(),
                dto.getMessage()
            );
            future.exceptionally(ex -> {
                log.error("Failed to send email asynchronously: {}", ex.getMessage());
                return null;
            });
            return ResponseEntity.ok("Email queued for sending");
        } catch (Exception e) {
            log.error("Error queueing email: {}", e.getMessage());
            return ResponseEntity.status(500).body("Failed to queue email");
        }
    }

    @PostMapping("/club-payment-required")
    public ResponseEntity<String> sendClubPaymentRequiredEmail(@RequestBody ClubPaymentRequiredEmailDTO dto) {
        log.info("Received request to send club payment required email to: {}", dto.getEmail());
        try {
            emailService.sendClubPaymentRequiredEmail(
                dto.getEmail(),
                dto.getFirstName(),
                dto.getClubName(),
                dto.getRegistrationFee() != null ? Double.parseDouble(dto.getRegistrationFee()) : 0.0,
                dto.getPaymentLink()
            ).exceptionally(ex -> {
                log.error("Failed to send payment email asynchronously: {}", ex.getMessage());
                return null;
            });
            return ResponseEntity.ok("Email queued for sending");
        } catch (Exception e) {
            log.error("Error queueing payment email: {}", e.getMessage());
            return ResponseEntity.status(500).body("Failed to queue email");
        }
    }

    @Data
    public static class ClubMembershipRequestEmailDTO {
        private String email;
        private String firstName;
        private String clubName;
        private String message;
    }

    @Data
    public static class ClubPaymentRequiredEmailDTO {
        private String email;
        private String firstName;
        private String clubName;
        private String registrationFee;
        private String paymentLink;
    }
}
