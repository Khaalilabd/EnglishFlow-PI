package com.englishflow.club.controller;

import com.englishflow.club.dto.MembershipRequestDTO;
import com.englishflow.club.service.KonnectService;
import com.englishflow.club.service.MembershipRequestService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/membership-requests")
@RequiredArgsConstructor
public class MembershipRequestController {

    private final MembershipRequestService requestService;
    private final KonnectService konnectService;
    
    @PostMapping
    public ResponseEntity<MembershipRequestDTO> createRequest(@RequestBody CreateRequestDTO dto) {
        log.info("📥 Received membership request: clubId={}, userId={}, message={}", 
            dto.getClubId(), dto.getUserId(), dto.getMessage());
        
        try {
            MembershipRequestDTO request = requestService.createRequest(
                dto.getClubId(), 
                dto.getUserId(), 
                dto.getMessage(),
                dto.getMotivationLetter(),
                dto.getStudentSkills()
            );
            log.info("✅ Membership request created successfully: id={}", request.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(request);
        } catch (Exception e) {
            log.error("❌ Failed to create membership request: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @GetMapping("/club/{clubId}/pending")
    public ResponseEntity<List<MembershipRequestDTO>> getPendingRequestsForClub(@PathVariable Integer clubId) {
        return ResponseEntity.ok(requestService.getPendingRequestsForClub(clubId));
    }

    @GetMapping("/club/{clubId}/all")
    public ResponseEntity<List<MembershipRequestDTO>> getAllRequestsForClub(@PathVariable Integer clubId) {
        return ResponseEntity.ok(requestService.getAllRequestsForClub(clubId));
    }

    @GetMapping("/club/{clubId}/total-payments")
    public ResponseEntity<Double> getTotalPayments(@PathVariable Integer clubId) {
        return ResponseEntity.ok(requestService.getTotalConfirmedPayments(clubId));
    }

    @PostMapping("/club/{clubId}/backfill-treasury")
    public ResponseEntity<Map<String, Object>> backfillTreasury(@PathVariable Integer clubId) {
        int count = requestService.backfillTreasuryIncomeEntries(clubId);
        return ResponseEntity.ok(Map.of(
            "clubId", clubId,
            "entriesCreated", count,
            "message", count > 0
                ? count + " missing income entries created in treasury"
                : "No missing entries — treasury is already up to date"
        ));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MembershipRequestDTO>> getUserRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(requestService.getUserRequests(userId));
    }
    
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<MembershipRequestDTO> approveRequest(
            @PathVariable Integer requestId,
            @RequestParam Long reviewerId) {
        return ResponseEntity.ok(requestService.approveRequest(requestId, reviewerId));
    }
    
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<MembershipRequestDTO> rejectRequest(
            @PathVariable Integer requestId,
            @RequestParam Long reviewerId,
            @RequestBody(required = false) RejectRequestDTO dto) {
        String comment = dto != null ? dto.getComment() : null;
        return ResponseEntity.ok(requestService.rejectRequest(requestId, reviewerId, comment));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<MembershipRequestDTO> getRequestById(@PathVariable Integer requestId) {
        return ResponseEntity.ok(requestService.getRequestById(requestId));
    }

    @PostMapping("/{requestId}/confirm-payment")
    public ResponseEntity<MembershipRequestDTO> confirmPayment(
            @PathVariable Integer requestId,
            @RequestBody ConfirmPaymentDTO dto) {
        return ResponseEntity.ok(requestService.confirmPayment(requestId, dto.getPaymentMethod(), dto.getPaymentToken()));
    }

    @PostMapping("/{requestId}/init-konnect-payment")
    public ResponseEntity<Map<String, Object>> initKonnectPayment(
            @PathVariable Integer requestId,
            @RequestBody InitKonnectPaymentDTO dto) {
        Map<String, Object> result = konnectService.initPayment(
            requestId, dto.getAmount(), dto.getFirstName(), dto.getEmail()
        );
        return ResponseEntity.ok(result);
    }

    @Data
    public static class InitKonnectPaymentDTO {
        private Double amount;
        private String firstName;
        private String email;
    }

    @Data
    public static class ConfirmPaymentDTO {
        private String paymentMethod; // KONNECT ou FLOUCI
        private String paymentToken;
    }
    
    @Data
    public static class CreateRequestDTO {
        private Integer clubId;
        private Long userId;
        private String message;
        private String motivationLetter;
        private String studentSkills;
    }
    
    @Data
    public static class RejectRequestDTO {
        private String comment;
    }
}
