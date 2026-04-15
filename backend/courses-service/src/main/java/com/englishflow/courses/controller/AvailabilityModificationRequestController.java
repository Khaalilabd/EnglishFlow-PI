package com.englishflow.courses.controller;

import com.englishflow.courses.dto.AvailabilityModificationRequestDTO;
import com.englishflow.courses.service.AvailabilityModificationRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/availability-modification-requests")
@RequiredArgsConstructor
public class AvailabilityModificationRequestController {
    
    private final AvailabilityModificationRequestService requestService;
    
    @PostMapping
    public ResponseEntity<AvailabilityModificationRequestDTO> createRequest(
            @RequestBody AvailabilityModificationRequestDTO dto) {
        try {
            AvailabilityModificationRequestDTO created = requestService.createRequest(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<AvailabilityModificationRequestDTO>> getAllRequests() {
        try {
            List<AvailabilityModificationRequestDTO> requests = requestService.getAllRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<AvailabilityModificationRequestDTO>> getRequestsByTutor(
            @PathVariable Long tutorId) {
        try {
            List<AvailabilityModificationRequestDTO> requests = requestService.getRequestsByTutor(tutorId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<AvailabilityModificationRequestDTO>> getPendingRequests() {
        try {
            List<AvailabilityModificationRequestDTO> requests = requestService.getPendingRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<AvailabilityModificationRequestDTO> approveRequest(
            @PathVariable Long id,
            @RequestBody Map<String, Object> reviewData) {
        try {
            Long reviewerId = Long.valueOf(reviewData.get("reviewerId").toString());
            String reviewerName = reviewData.get("reviewerName").toString();
            String comment = reviewData.getOrDefault("comment", "").toString();
            
            AvailabilityModificationRequestDTO approved = requestService.approveRequest(
                    id, reviewerId, reviewerName, comment);
            return ResponseEntity.ok(approved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<AvailabilityModificationRequestDTO> rejectRequest(
            @PathVariable Long id,
            @RequestBody Map<String, Object> reviewData) {
        try {
            Long reviewerId = Long.valueOf(reviewData.get("reviewerId").toString());
            String reviewerName = reviewData.get("reviewerName").toString();
            String comment = reviewData.getOrDefault("comment", "").toString();
            
            AvailabilityModificationRequestDTO rejected = requestService.rejectRequest(
                    id, reviewerId, reviewerName, comment);
            return ResponseEntity.ok(rejected);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
