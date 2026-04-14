package com.englishflow.event.controller;

import com.englishflow.event.dto.JoinEventRequest;
import com.englishflow.event.dto.ParticipantDTO;
import com.englishflow.event.service.EventKonnectService;
import com.englishflow.event.service.ParticipantService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class ParticipantController {
    
    private final ParticipantService participantService;
    private final EventKonnectService eventKonnectService;
    
    @PostMapping("/{eventId}/join")
    public ResponseEntity<ParticipantDTO> joinEvent(
            @PathVariable Integer eventId,
            @Valid @RequestBody JoinEventRequest request) {
        ParticipantDTO participant = participantService.joinEvent(eventId, request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(participant);
    }
    
    @DeleteMapping("/{eventId}/leave/{userId}")
    public ResponseEntity<Void> leaveEvent(
            @PathVariable Integer eventId,
            @PathVariable Long userId) {
        participantService.leaveEvent(eventId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<ParticipantDTO>> getEventParticipants(@PathVariable Integer eventId) {
        return ResponseEntity.ok(participantService.getEventParticipants(eventId));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ParticipantDTO>> getUserEvents(@PathVariable Long userId) {
        return ResponseEntity.ok(participantService.getUserEvents(userId));
    }
    
    @GetMapping("/{eventId}/is-participant/{userId}")
    public ResponseEntity<Boolean> isUserParticipant(
            @PathVariable Integer eventId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(participantService.isUserParticipant(eventId, userId));
    }

    // ── Payment endpoints ──────────────────────────────────────

    @GetMapping("/participants/{participantId}")
    public ResponseEntity<ParticipantDTO> getParticipantById(@PathVariable Integer participantId) {
        return ResponseEntity.ok(participantService.getParticipantById(participantId));
    }

    @PostMapping("/participants/{participantId}/confirm-payment")
    public ResponseEntity<ParticipantDTO> confirmPayment(
            @PathVariable Integer participantId,
            @RequestBody ConfirmPaymentDTO dto) {
        return ResponseEntity.ok(participantService.confirmPayment(participantId, dto.getPaymentMethod(), dto.getPaymentToken()));
    }

    @PostMapping("/participants/{participantId}/init-konnect-payment")
    public ResponseEntity<Map<String, Object>> initKonnectPayment(
            @PathVariable Integer participantId,
            @RequestBody InitKonnectPaymentDTO dto) {
        Map<String, Object> result = eventKonnectService.initPayment(
            participantId, dto.getAmount(), dto.getFirstName(), dto.getEmail()
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{eventId}/total-payments")
    public ResponseEntity<Double> getTotalPayments(@PathVariable Integer eventId) {
        return ResponseEntity.ok(participantService.getTotalConfirmedPayments(eventId));
    }

    @GetMapping("/club/{clubId}/total-payments")
    public ResponseEntity<Double> getTotalPaymentsByClub(@PathVariable Integer clubId) {
        return ResponseEntity.ok(participantService.getTotalConfirmedPaymentsByClub(clubId));
    }

    @Data
    public static class ConfirmPaymentDTO {
        private String paymentMethod;
        private String paymentToken;
    }

    @Data
    public static class InitKonnectPaymentDTO {
        private Double amount;
        private String firstName;
        private String email;
    }
}
