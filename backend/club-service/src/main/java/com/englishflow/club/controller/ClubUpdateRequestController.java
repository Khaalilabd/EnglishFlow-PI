package com.englishflow.club.controller;

import com.englishflow.club.dto.ClubUpdateRequestDTO;
import com.englishflow.club.service.ClubUpdateRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/club-update-requests")
@RequiredArgsConstructor
public class ClubUpdateRequestController {
    
    private final ClubUpdateRequestService updateRequestService;
    
    /**
     * Récupérer les demandes en attente pour un club
     */
    @GetMapping("/club/{clubId}/pending")
    public ResponseEntity<List<ClubUpdateRequestDTO>> getPendingRequestsForClub(@PathVariable Integer clubId) {
        return ResponseEntity.ok(updateRequestService.getPendingRequestsForClub(clubId));
    }
    
    /**
     * Récupérer toutes les demandes pour un club
     */
    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<ClubUpdateRequestDTO>> getAllRequestsForClub(@PathVariable Integer clubId) {
        return ResponseEntity.ok(updateRequestService.getAllRequestsForClub(clubId));
    }
    
    /**
     * Récupérer une demande par ID
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ClubUpdateRequestDTO> getRequestById(@PathVariable Integer requestId) {
        return ResponseEntity.ok(updateRequestService.getRequestById(requestId));
    }
    
    /**
     * Approuver une demande de modification
     */
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<ClubUpdateRequestDTO> approveRequest(
            @PathVariable Integer requestId,
            @RequestParam Long approverId) {
        ClubUpdateRequestDTO approved = updateRequestService.approveUpdateRequest(requestId, approverId);
        return ResponseEntity.ok(approved);
    }
    
    /**
     * Rejeter une demande de modification
     */
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<ClubUpdateRequestDTO> rejectRequest(
            @PathVariable Integer requestId,
            @RequestParam Long rejecterId) {
        ClubUpdateRequestDTO rejected = updateRequestService.rejectUpdateRequest(requestId, rejecterId);
        return ResponseEntity.ok(rejected);
    }
}
