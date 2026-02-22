package com.englishflow.messaging.controller;

import com.englishflow.messaging.dto.AddReactionRequest;
import com.englishflow.messaging.dto.MessageReactionDTO;
import com.englishflow.messaging.dto.ReactionSummaryDTO;
import com.englishflow.messaging.exception.ResourceNotFoundException;
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
@RequestMapping("/messaging/messages/{messageId}/reactions")
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
        
        try {
            Long userId = Long.parseLong(authentication.getName());
            log.info("User {} toggling reaction {} on message {}", userId, request.getEmoji(), messageId);
            
            MessageReactionDTO reaction = reactionService.toggleReaction(messageId, request.getEmoji(), userId);
            
            // Récupérer le résumé mis à jour
            List<ReactionSummaryDTO> summary = reactionService.getReactionSummary(messageId, userId);
            
            // Envoyer la mise à jour via WebSocket à tous les participants
            try {
                messagingTemplate.convertAndSend(
                    "/topic/message/" + messageId + "/reactions",
                    summary
                );
            } catch (Exception e) {
                log.error("Failed to send WebSocket update for reactions on message {}: {}", messageId, e.getMessage());
                // Continue même si WebSocket échoue
            }
            
            if (reaction == null) {
                return ResponseEntity.noContent().build();
            }
            
            return ResponseEntity.ok(reaction);
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error toggling reaction on message {}: {}", messageId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error toggling reaction: " + e.getMessage());
        }
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
