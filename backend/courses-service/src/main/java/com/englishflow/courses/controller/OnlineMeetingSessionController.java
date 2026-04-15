package com.englishflow.courses.controller;

import com.englishflow.courses.dto.OnlineMeetingSessionDTO;
import com.englishflow.courses.service.OnlineMeetingSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/meeting-sessions")
@RequiredArgsConstructor
@Slf4j
public class OnlineMeetingSessionController {
    
    private final OnlineMeetingSessionService sessionService;
    
    @PostMapping
    public ResponseEntity<?> createSession(@RequestBody Map<String, Object> request) {
        try {
            Long lessonId = Long.valueOf(request.get("lessonId").toString());
            String roomId = request.get("roomId").toString();
            String inviteLink = request.get("inviteLink").toString();
            Long tutorId = Long.valueOf(request.get("tutorId").toString());
            
            OnlineMeetingSessionDTO session = sessionService.createSession(lessonId, roomId, inviteLink, tutorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(session);
        } catch (Exception e) {
            log.error("Error creating meeting session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create meeting session: " + e.getMessage()));
        }
    }
    
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<?> getActiveSessionByLessonId(@PathVariable Long lessonId) {
        try {
            Optional<OnlineMeetingSessionDTO> session = sessionService.getActiveSessionByLessonId(lessonId);
            if (session.isPresent()) {
                return ResponseEntity.ok(session.get());
            } else {
                return ResponseEntity.ok(Map.of("active", false));
            }
        } catch (Exception e) {
            log.error("Error getting meeting session for lesson {}", lessonId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get meeting session: " + e.getMessage()));
        }
    }
    
    @GetMapping("/room/{roomId}")
    public ResponseEntity<?> getActiveSessionByRoomId(@PathVariable String roomId) {
        try {
            Optional<OnlineMeetingSessionDTO> session = sessionService.getActiveSessionByRoomId(roomId);
            if (session.isPresent()) {
                return ResponseEntity.ok(session.get());
            } else {
                return ResponseEntity.ok(Map.of("active", false));
            }
        } catch (Exception e) {
            log.error("Error getting meeting session for room {}", roomId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get meeting session: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/lesson/{lessonId}")
    public ResponseEntity<?> endSession(@PathVariable Long lessonId) {
        try {
            sessionService.endSession(lessonId);
            return ResponseEntity.ok(Map.of("message", "Meeting session ended successfully"));
        } catch (Exception e) {
            log.error("Error ending meeting session for lesson {}", lessonId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to end meeting session: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<?> endSessionByRoomId(@PathVariable String roomId) {
        try {
            sessionService.endSessionByRoomId(roomId);
            return ResponseEntity.ok(Map.of("message", "Meeting session ended successfully"));
        } catch (Exception e) {
            log.error("Error ending meeting session for room {}", roomId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to end meeting session: " + e.getMessage()));
        }
    }
}
