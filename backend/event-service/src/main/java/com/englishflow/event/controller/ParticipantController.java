package com.englishflow.event.controller;

import com.englishflow.event.dto.JoinEventRequest;
import com.englishflow.event.dto.ParticipantDTO;
import com.englishflow.event.service.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class ParticipantController {
    
    private final ParticipantService participantService;
    
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
}
