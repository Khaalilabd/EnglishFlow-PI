package com.englishflow.club.controller;

import com.englishflow.club.dto.SkillDTO;
import com.englishflow.club.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clubs/{clubId}/skills")
@RequiredArgsConstructor
public class SkillController {
    
    private final SkillService skillService;
    
    @GetMapping
    public ResponseEntity<List<SkillDTO>> getSkillsByClub(@PathVariable Integer clubId) {
        return ResponseEntity.ok(skillService.getSkillsByClubId(clubId));
    }
    
    @PostMapping
    public ResponseEntity<SkillDTO> addSkillToClub(
            @PathVariable Integer clubId,
            @Valid @RequestBody SkillDTO skillDTO) {
        SkillDTO createdSkill = skillService.addSkillToClub(clubId, skillDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSkill);
    }
    
    @PutMapping
    public ResponseEntity<Void> updateClubSkills(
            @PathVariable Integer clubId,
            @Valid @RequestBody List<SkillDTO> skillDTOs) {
        skillService.updateClubSkills(clubId, skillDTOs);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Integer skillId) {
        skillService.deleteSkill(skillId);
        return ResponseEntity.noContent().build();
    }
}
