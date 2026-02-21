package com.englishflow.messaging.controller;

import com.englishflow.messaging.dto.AddReactionRequest;
import com.englishflow.messaging.dto.MessageReactionDTO;
import com.englishflow.messaging.dto.ReactionSummaryDTO;
import com.englishflow.messaging.service.MessageReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages/{messageId}/reactions")
@RequiredArgsConstructor
@Slf4j
public class MessageReactionController {
    
    private final MessageReactionService reactionService;
    private final SimpMessagingTemplate messagingTemplate;
    
    @PostMapping
    public ResponseEntity<?> toggleReaction(
            @PathVariable Long messageId,
            @Valid @RequestBody AddReactionRequest request,
            Authentication authentication) {
        
        Long userId = Long.parseLong(authentication.getName());
        log.info("User {} toggling reaction {} on message {}", userId, request.getEmoji(), messageId);
        
        MessageReactionDTO reaction = reactionService.toggleReaction(messageId, request.getEmoji(), userId);
        
        // Récupérer le résumé mis à jour
        List<ReactionSummaryDTO> summary = reactionService.getReactionSummary(messageId, userId);
        
        // Envoyer la mise à jour via WebSocket à tous les participants
        // Note: On devrait récupérer le conversationId du message
        messagingTemplate.convertAndSend(
            "/topic/message/" + messageId + "/reactions",
            summary
        );
        
        if (reaction == null) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(reaction);
    }
    
    @GetMapping
    public ResponseEntity<List<ReactionSummaryDTO>> getReactions(
            @PathVariable Long messageId,
            Authentication authentication) {
        
        Long userId = Long.parseLong(authentication.getName());
        List<ReactionSummaryDTO> reactions = reactionService.getReactionSummary(messageId, userId);
        
        return ResponseEntity.ok(reactions);
    }
}
