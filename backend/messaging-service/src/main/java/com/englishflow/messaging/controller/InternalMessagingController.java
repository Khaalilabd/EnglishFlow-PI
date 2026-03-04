package com.englishflow.messaging.controller;

import com.englishflow.messaging.client.AuthServiceClient;
import com.englishflow.messaging.dto.ConversationDTO;
import com.englishflow.messaging.dto.CreateConversationRequest;
import com.englishflow.messaging.model.Conversation;
import com.englishflow.messaging.model.ConversationParticipant;
import com.englishflow.messaging.model.Message;
import com.englishflow.messaging.repository.ConversationRepository;
import com.englishflow.messaging.repository.ConversationParticipantRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller pour les endpoints internes utilisés par d'autres microservices
 * Ces endpoints ne nécessitent pas d'authentification JWT utilisateur
 */
@RestController
@RequestMapping("/messaging/internal")
@RequiredArgsConstructor
@Slf4j
public class InternalMessagingController {
    
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final AuthServiceClient authServiceClient;
    private final com.englishflow.messaging.repository.MessageRepository messageRepository;
    
    /**
     * Créer une conversation (utilisé par courses-service pour créer des groupes de pack)
     */
    @PostMapping("/conversations")
    @Transactional
    public ResponseEntity<ConversationDTO> createConversation(
            @Valid @RequestBody CreateConversationRequest request) {
        log.info("Internal request to create conversation: type={}, title={}, participants={}", 
                request.getType(), request.getTitle(), request.getParticipantIds().size());
        
        Conversation conversation = new Conversation();
        conversation.setType(request.getType());
        conversation.setTitle(request.getTitle());
        conversation.setDescription(request.getDescription());
        conversation.setGroupPhoto(request.getGroupPhoto());
        conversation.setCreatedBy(request.getParticipantIds().get(0)); // Premier participant est le créateur
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        
        conversation = conversationRepository.save(conversation);
        
        // Ajouter tous les participants
        for (Long participantId : request.getParticipantIds()) {
            try {
                AuthServiceClient.UserInfo userInfo = authServiceClient.getUserInfo(participantId);
                
                ConversationParticipant participant = new ConversationParticipant();
                participant.setConversation(conversation);
                participant.setUserId(participantId);
                participant.setUserName(userInfo.getFullName());
                participant.setUserEmail(userInfo.getEmail());
                participant.setUserRole(userInfo.getRole());
                participant.setUserAvatar(userInfo.getProfilePhotoUrl());
                // Premier participant est ADMIN, les autres sont MEMBER
                participant.setParticipantRole(participantId.equals(request.getParticipantIds().get(0)) 
                    ? ConversationParticipant.ParticipantRole.ADMIN 
                    : ConversationParticipant.ParticipantRole.MEMBER);
                participant.setJoinedAt(LocalDateTime.now());
                
                participantRepository.save(participant);
            } catch (Exception e) {
                log.error("Error adding participant {}: {}", participantId, e.getMessage());
            }
        }
        
        // Recharger la conversation avec les participants
        conversation = conversationRepository.findByIdWithParticipants(conversation.getId())
            .orElseThrow(() -> new RuntimeException("Conversation not found after creation"));
        
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setType(conversation.getType());
        dto.setTitle(conversation.getTitle());
        dto.setDescription(conversation.getDescription());
        dto.setGroupPhoto(conversation.getGroupPhoto());
        dto.setCreatedBy(conversation.getCreatedBy());
        dto.setCreatedAt(conversation.getCreatedAt());
        
        return ResponseEntity.ok(dto);
    }
    
    /**
     * Ajouter des participants à une conversation
     */
    @PostMapping("/conversations/{conversationId}/participants")
    @Transactional
    public ResponseEntity<ConversationDTO> addParticipants(
            @PathVariable Long conversationId,
            @RequestBody List<Long> participantIds) {
        log.info("Internal request to add {} participants to conversation {}", 
                participantIds.size(), conversationId);
        
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        for (Long participantId : participantIds) {
            // Vérifier si le participant existe déjà
            if (participantRepository.findByConversationIdAndUserId(conversationId, participantId).isPresent()) {
                log.info("Participant {} already in conversation {}", participantId, conversationId);
                continue;
            }
            
            try {
                AuthServiceClient.UserInfo userInfo = authServiceClient.getUserInfo(participantId);
                
                ConversationParticipant participant = new ConversationParticipant();
                participant.setConversation(conversation);
                participant.setUserId(participantId);
                participant.setUserName(userInfo.getFullName());
                participant.setUserEmail(userInfo.getEmail());
                participant.setUserRole(userInfo.getRole());
                participant.setUserAvatar(userInfo.getProfilePhotoUrl());
                participant.setParticipantRole(ConversationParticipant.ParticipantRole.MEMBER);
                participant.setJoinedAt(LocalDateTime.now());
                
                participantRepository.save(participant);
            } catch (Exception e) {
                log.error("Error adding participant {}: {}", participantId, e.getMessage());
            }
        }
        
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setType(conversation.getType());
        dto.setTitle(conversation.getTitle());
        
        return ResponseEntity.ok(dto);
    }
    
    /**
     * Retirer un participant d'une conversation
     */
    @DeleteMapping("/conversations/{conversationId}/participants/{participantId}")
    @Transactional
    public ResponseEntity<Void> removeParticipant(
            @PathVariable Long conversationId,
            @PathVariable Long participantId) {
        log.info("Internal request to remove participant {} from conversation {}", 
                participantId, conversationId);
        
        ConversationParticipant participant = participantRepository
            .findByConversationIdAndUserId(conversationId, participantId)
            .orElseThrow(() -> new RuntimeException("Participant not found"));
        
        participantRepository.delete(participant);
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * Supprimer une conversation (utilisé par courses-service pour supprimer le groupe du pack)
     */
    @DeleteMapping("/conversations/{conversationId}")
    @Transactional
    public ResponseEntity<Void> deleteConversation(@PathVariable Long conversationId) {
        log.info("Internal request to delete conversation {}", conversationId);
        
        conversationRepository.deleteById(conversationId);
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * Envoyer un message système dans une conversation
     */
    @PostMapping("/conversations/{conversationId}/system-message")
    @Transactional
    public ResponseEntity<Void> sendSystemMessage(
            @PathVariable Long conversationId,
            @RequestBody java.util.Map<String, String> request) {
        log.info("Internal request to send system message to conversation {}", conversationId);
        
        String content = request.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        // Créer un message système (senderId = 0 pour indiquer un message système)
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(0L);
        message.setSenderName("Système");
        message.setSenderAvatar(null);
        message.setContent(content);
        message.setMessageType(Message.MessageType.TEXT);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        
        messageRepository.save(message);
        
        return ResponseEntity.ok().build();
    }
}
