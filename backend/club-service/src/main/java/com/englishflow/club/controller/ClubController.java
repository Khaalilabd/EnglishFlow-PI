package com.englishflow.club.controller;

import com.englishflow.club.dto.ClubDTO;
import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.service.ClubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {
    
    private final ClubService clubService;
    
    @GetMapping
    public ResponseEntity<List<ClubDTO>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubs());
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ClubDTO>> getClubsByCategory(@PathVariable ClubCategory category) {
        return ResponseEntity.ok(clubService.getClubsByCategory(category));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ClubDTO>> searchClubsByName(@RequestParam String name) {
        return ResponseEntity.ok(clubService.searchClubsByName(name));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClubDTO> getClubById(@PathVariable Integer id) {
        return ResponseEntity.ok(clubService.getClubById(id));
    }
    
    @PostMapping
    public ResponseEntity<ClubDTO> createClub(@Valid @RequestBody ClubDTO clubDTO) {
        ClubDTO createdClub = clubService.createClub(clubDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClub);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClubDTO> updateClub(
            @PathVariable Integer id,
            @Valid @RequestBody ClubDTO clubDTO) {
        ClubDTO updatedClub = clubService.updateClub(id, clubDTO);
        return ResponseEntity.ok(updatedClub);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable Integer id) {
        clubService.deleteClub(id);
        return ResponseEntity.noContent().build();
    }
}
