package com.englishflow.sponsors.controller;

import com.englishflow.sponsors.dto.SponsorDTO;
import com.englishflow.sponsors.entity.Sponsor;
import com.englishflow.sponsors.service.SponsorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sponsors")
@RequiredArgsConstructor
@Slf4j
public class SponsorController {
    
    private final SponsorService sponsorService;
    
    @GetMapping
    public ResponseEntity<List<SponsorDTO>> getAllSponsors() {
        log.info("GET /sponsors - Fetching all sponsors");
        List<SponsorDTO> sponsors = sponsorService.getAllSponsors();
        return ResponseEntity.ok(sponsors);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SponsorDTO> getSponsorById(@PathVariable Long id) {
        log.info("GET /sponsors/{} - Fetching sponsor by id", id);
        SponsorDTO sponsor = sponsorService.getSponsorById(id);
        return ResponseEntity.ok(sponsor);
    }
    
    @GetMapping("/level/{level}")
    public ResponseEntity<List<SponsorDTO>> getSponsorsByLevel(@PathVariable Sponsor.SponsorLevel level) {
        log.info("GET /sponsors/level/{} - Fetching sponsors by level", level);
        List<SponsorDTO> sponsors = sponsorService.getSponsorsByLevel(level);
        return ResponseEntity.ok(sponsors);
    }
    
    @PostMapping
    public ResponseEntity<SponsorDTO> createSponsor(@Valid @RequestBody SponsorDTO sponsorDTO) {
        log.info("POST /sponsors - Creating new sponsor");
        log.info("DEBUG - Received DTO: name={}, contributionAmount={}, contributionAmountClass={}", 
                sponsorDTO.getName(), sponsorDTO.getContributionAmount(), 
                sponsorDTO.getContributionAmount() != null ? sponsorDTO.getContributionAmount().getClass().getName() : "null");
        SponsorDTO createdSponsor = sponsorService.createSponsor(sponsorDTO);
        log.info("DEBUG - Created sponsor: id={}, contributionAmount={}, level={}", 
                createdSponsor.getId(), createdSponsor.getContributionAmount(), createdSponsor.getLevel());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSponsor);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SponsorDTO> updateSponsor(
            @PathVariable Long id,
            @Valid @RequestBody SponsorDTO sponsorDTO) {
        log.info("PUT /sponsors/{} - Updating sponsor", id);
        log.info("DEBUG - Received DTO: name={}, contributionAmount={}, contributionAmountClass={}", 
                sponsorDTO.getName(), sponsorDTO.getContributionAmount(), 
                sponsorDTO.getContributionAmount() != null ? sponsorDTO.getContributionAmount().getClass().getName() : "null");
        SponsorDTO updatedSponsor = sponsorService.updateSponsor(id, sponsorDTO);
        log.info("DEBUG - Updated sponsor: id={}, contributionAmount={}, level={}", 
                updatedSponsor.getId(), updatedSponsor.getContributionAmount(), updatedSponsor.getLevel());
        return ResponseEntity.ok(updatedSponsor);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SponsorDTO>> getSponsorsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(sponsorService.getSponsorsByUserId(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<SponsorDTO>> getPendingSponsors() {
        return ResponseEntity.ok(sponsorService.getPendingSponsors());
    }

    @GetMapping("/approved")
    public ResponseEntity<List<SponsorDTO>> getApprovedSponsors() {
        return ResponseEntity.ok(sponsorService.getApprovedSponsors());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<SponsorDTO> approveSponsor(@PathVariable Long id) {
        return ResponseEntity.ok(sponsorService.approveSponsor(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<SponsorDTO> rejectSponsor(@PathVariable Long id) {
        return ResponseEntity.ok(sponsorService.rejectSponsor(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSponsor(@PathVariable Long id) {
        log.info("DELETE /sponsors/{} - Deleting sponsor", id);
        sponsorService.deleteSponsor(id);
        return ResponseEntity.noContent().build();
    }
}
