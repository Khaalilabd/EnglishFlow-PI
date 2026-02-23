package com.englishflow.community.controller;

import com.englishflow.community.dto.ReactionDTO;
import com.englishflow.community.entity.Reaction;
import com.englishflow.community.service.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/community/reactions")
@RequiredArgsConstructor
@Tag(name = "Reactions", description = "Reaction management endpoints for posts and topics")
public class ReactionController {
    
    private final ReactionService reactionService;
    
    @PostMapping("/posts/{postId}")
    @Operation(summary = "Add reaction to post", description = "Add or update a reaction (LIKE, HELPFUL, INSIGHTFUL) to a post")
    public ResponseEntity<ReactionDTO> addReactionToPost(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestParam Reaction.ReactionType type) {
        ReactionDTO reaction = reactionService.addReactionToPost(postId, userId, type);
        return ResponseEntity.status(HttpStatus.CREATED).body(reaction);
    }
    
    @PostMapping("/topics/{topicId}")
    @Operation(summary = "Add reaction to topic", description = "Add or update a reaction to a topic")
    public ResponseEntity<ReactionDTO> addReactionToTopic(
            @PathVariable Long topicId,
            @RequestParam Long userId,
            @RequestParam Reaction.ReactionType type) {
        ReactionDTO reaction = reactionService.addReactionToTopic(topicId, userId, type);
        return ResponseEntity.status(HttpStatus.CREATED).body(reaction);
    }
    
    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "Remove reaction from post", description = "Remove user's reaction from a post")
    public ResponseEntity<Void> removeReactionFromPost(
            @PathVariable Long postId,
            @RequestParam Long userId) {
        reactionService.removeReactionFromPost(postId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/topics/{topicId}")
    @Operation(summary = "Remove reaction from topic", description = "Remove user's reaction from a topic")
    public ResponseEntity<Void> removeReactionFromTopic(
            @PathVariable Long topicId,
            @RequestParam Long userId) {
        reactionService.removeReactionFromTopic(topicId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/posts/{postId}/count")
    @Operation(summary = "Get post reactions count", description = "Get total number of reactions for a post")
    public ResponseEntity<Long> getPostReactionsCount(@PathVariable Long postId) {
        Long count = reactionService.getPostReactionsCount(postId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/topics/{topicId}/count")
    @Operation(summary = "Get topic reactions count", description = "Get total number of reactions for a topic")
    public ResponseEntity<Long> getTopicReactionsCount(@PathVariable Long topicId) {
        Long count = reactionService.getTopicReactionsCount(topicId);
        return ResponseEntity.ok(count);
    }
}
