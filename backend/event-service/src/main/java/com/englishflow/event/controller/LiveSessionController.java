package com.englishflow.event.controller;

import com.englishflow.event.dto.live.*;
import com.englishflow.event.service.LiveSessionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/session")
@RequiredArgsConstructor
public class LiveSessionController {

    private final LiveSessionService liveService;

    // ─── PRESENCE ────────────────────────────────────────────────
    @GetMapping("/participants")
    public ResponseEntity<List<PresenceDTO>> getParticipants(@PathVariable Integer eventId) {
        return ResponseEntity.ok(liveService.getParticipants(eventId));
    }

    // ─── POLL (moderator creates via REST) ───────────────────────
    @PostMapping("/polls")
    public ResponseEntity<PollDTO> createPoll(@PathVariable Integer eventId,
                                               @RequestBody CreatePollRequest req) {
        return ResponseEntity.ok(liveService.createPoll(
            eventId, req.getQuestion(), req.getOptions(), req.isMultipleChoice()));
    }

    @GetMapping("/polls/active")
    public ResponseEntity<PollDTO> getActivePoll(@PathVariable Integer eventId,
                                                  @RequestParam Long userId) {
        PollDTO poll = liveService.getActivePoll(eventId, userId);
        return poll != null ? ResponseEntity.ok(poll) : ResponseEntity.noContent().build();
    }

    // ─── Q&A ─────────────────────────────────────────────────────
    @GetMapping("/questions")
    public ResponseEntity<List<QuestionDTO>> getQuestions(@PathVariable Integer eventId,
                                                           @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(liveService.getQuestions(eventId, userId));
    }

    @PutMapping("/questions/{questionId}/answered")
    public ResponseEntity<Void> markAnswered(@PathVariable Integer eventId,
                                              @PathVariable Long questionId) {
        liveService.markAnswered(eventId, questionId);
        return ResponseEntity.ok().build();
    }

    // ─── CHAT ────────────────────────────────────────────────────
    @GetMapping("/chat")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(@PathVariable Integer eventId) {
        return ResponseEntity.ok(liveService.getChatHistory(eventId));
    }

    @DeleteMapping("/chat/{messageId}")
    public ResponseEntity<Void> moderateMessage(@PathVariable Integer eventId,
                                                 @PathVariable Long messageId) {
        liveService.moderateMessage(eventId, messageId);
        return ResponseEntity.ok().build();
    }

    @Data
    public static class CreatePollRequest {
        private String question;
        private List<String> options;
        private boolean multipleChoice;
    }
}
