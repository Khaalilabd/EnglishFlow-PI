package com.englishflow.courses.controller;

import com.englishflow.courses.dto.TutorAvailabilityDTO;
import com.englishflow.courses.enums.TutorStatus;
import com.englishflow.courses.service.ITutorAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutor-availability")
@RequiredArgsConstructor
public class TutorAvailabilityController {
    
    private final ITutorAvailabilityService availabilityService;
    
    @PostMapping
    public ResponseEntity<TutorAvailabilityDTO> createOrUpdateAvailability(@RequestBody TutorAvailabilityDTO availabilityDTO) {
        TutorAvailabilityDTO created = availabilityService.createOrUpdateAvailability(availabilityDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TutorAvailabilityDTO> getById(@PathVariable Long id) {
        TutorAvailabilityDTO availability = availabilityService.getById(id);
        return ResponseEntity.ok(availability);
    }
    
    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<TutorAvailabilityDTO> getByTutorId(@PathVariable Long tutorId) {
        TutorAvailabilityDTO availability = availabilityService.getByTutorId(tutorId);
        if (availability == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(availability);
    }
    
    @GetMapping
    public ResponseEntity<List<TutorAvailabilityDTO>> getAllAvailabilities() {
        List<TutorAvailabilityDTO> availabilities = availabilityService.getAllAvailabilities();
        return ResponseEntity.ok(availabilities);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TutorAvailabilityDTO>> getByStatus(@PathVariable TutorStatus status) {
        List<TutorAvailabilityDTO> availabilities = availabilityService.getByStatus(status);
        return ResponseEntity.ok(availabilities);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<TutorAvailabilityDTO>> getAvailableTutors(
            @RequestParam String category,
            @RequestParam String level) {
        List<TutorAvailabilityDTO> availabilities = availabilityService.getAvailableTutorsByCategoryAndLevel(category, level);
        return ResponseEntity.ok(availabilities);
    }
    
    @GetMapping("/with-capacity")
    public ResponseEntity<List<TutorAvailabilityDTO>> getTutorsWithCapacity() {
        List<TutorAvailabilityDTO> availabilities = availabilityService.getTutorsWithCapacity();
        return ResponseEntity.ok(availabilities);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
