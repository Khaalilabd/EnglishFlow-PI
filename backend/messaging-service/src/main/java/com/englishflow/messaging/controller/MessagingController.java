package com.englishflow.messaging.controller;

import com.englishflow.messaging.config.JwtUtil;
import com.englishflow.messaging.dto.*;
import com.englishflow.messaging.service.MessagingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messaging")
@RequiredArgsConstructor
@Slf4j
public class MessagingController {
    
    private final MessagingService messagingService;
    private final JwtUtil jwtUtil;
    
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> getConversations(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.debug("GET /conversations for user: {}", userId);
        
        List<ConversationDTO> conversations = messagingService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }
    
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ConversationDTO> getConversation(
            @PathVariable Long conversationId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.debug("GET /conversations/{} for user: {}", conversationId, userId);
        
        ConversationDTO conversation = messagingService.getConversation(conversationId, userId);
        return ResponseEntity.ok(conversation);
    }
    
    @PostMapping("/conversations")
    public ResponseEntity<ConversationDTO> createConversation(
            @Valid @RequestBody CreateConversationRequest request,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = (Long) authentication.getPrincipal();
        log.debug("POST /conversations for user: {}", userId);
        
        // Extraire les infos utilisateur du token
        String token = authHeader.substring(7);
        String userEmail = jwtUtil.extractEmail(token);
        String userRole = jwtUtil.extractRole(token);
        
        // TODO: Récupérer le nom et l'avatar depuis auth-service
        String userName = "User " + userId;
        String userAvatar = null;
        
        ConversationDTO conversation = messagingService.createConversation(
            request, userId, userName, userEmail, userRole, userAvatar);
        return ResponseEntity.ok(conversation);
    }
    
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Page<MessageDTO>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.debug("GET /conversations/{}/messages for user: {} (page: {}, size: {})", 
                 conversationId, userId, page, size);
        
        Page<MessageDTO> messages = messagingService.getMessages(conversationId, userId, page, size);
        return ResponseEntity.ok(messages);
    }
    
    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<MessageDTO> sendMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody SendMessageRequest request,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = (Long) authentication.getPrincipal();
        log.debug("POST /conversations/{}/messages for user: {}", conversationId, userId);
        
        // Extraire les infos utilisateur du token
        String token = authHeader.substring(7);
        
        // TODO: Récupérer le nom et l'avatar depuis auth-service
        String senderName = "User " + userId;
        String senderAvatar = null;
        
        MessageDTO message = messagingService.sendMessage(
            conversationId, request, userId, senderName, senderAvatar);
        return ResponseEntity.ok(message);
    }
    
    @PostMapping("/conversations/{conversationId}/mark-read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long conversationId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.debug("POST /conversations/{}/mark-read for user: {}", conversationId, userId);
        
        messagingService.markAsRead(conversationId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.debug("GET /unread-count for user: {}", userId);
        
        Long count = messagingService.getUnreadCount(userId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}
