package com.englishflow.messaging.controller;

import com.englishflow.messaging.client.AuthServiceClient;
import com.englishflow.messaging.dto.MessageDTO;
import com.englishflow.messaging.dto.SendMessageRequest;
import com.englishflow.messaging.service.MessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final MessagingService messagingService;
    private final AuthServiceClient authServiceClient;
    
    @MessageMapping("/chat/{conversationId}")
    public void sendMessage(@DestinationVariable Long conversationId,
                           @Payload SendMessageRequest request,
                           SimpMessageHeaderAccessor headerAccessor,
                           Principal principal) {
        try {
            log.debug("WebSocket message received for conversation: {}", conversationId);
            
            // Récupérer l'ID utilisateur depuis le principal
            Long userId = Long.parseLong(principal.getName());
            
            // Récupérer les infos utilisateur depuis auth-service
            AuthServiceClient.UserInfo userInfo = authServiceClient.getUserInfo(userId);
            String senderName = userInfo.getFullName();
            String senderAvatar = userInfo.getProfilePhotoUrl();
            
            // Enregistrer le message
            MessageDTO message = messagingService.sendMessage(
                conversationId, request, userId, senderName, senderAvatar);
            
            // Envoyer le message à tous les participants de la conversation
            messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId, 
                message);
            
            log.debug("Message sent to conversation: {}", conversationId);
            
        } catch (Exception e) {
            log.error("Error sending message via WebSocket", e);
        }
    }
    
    @MessageMapping("/typing/{conversationId}")
    public void sendTypingIndicator(@DestinationVariable Long conversationId,
                                    @Payload Map<String, Object> payload,
                                    Principal principal) {
        try {
            Long userId = Long.parseLong(principal.getName());
            
            // Récupérer les infos utilisateur
            AuthServiceClient.UserInfo userInfo = authServiceClient.getUserInfo(userId);
            
            Map<String, Object> typingIndicator = new HashMap<>();
            typingIndicator.put("conversationId", conversationId);
            typingIndicator.put("userId", userId);
            typingIndicator.put("userName", userInfo.getFullName());
            typingIndicator.put("isTyping", payload.get("isTyping"));
            
            // Envoyer l'indicateur de frappe aux autres participants
            messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId + "/typing",
                typingIndicator);
            
        } catch (Exception e) {
            log.error("Error sending typing indicator", e);
        }
    }
}
