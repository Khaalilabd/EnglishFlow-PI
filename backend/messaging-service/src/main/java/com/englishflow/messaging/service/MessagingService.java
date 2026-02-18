package com.englishflow.messaging.service;

import com.englishflow.messaging.client.AuthServiceClient;
import com.englishflow.messaging.dto.*;
import com.englishflow.messaging.model.*;
import com.englishflow.messaging.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingService {
    
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final MessageReadStatusRepository readStatusRepository;
    private final AuthServiceClient authServiceClient;
    
    @Transactional(readOnly = true)
    public List<ConversationDTO> getUserConversations(Long userId) {
        log.debug("Getting conversations for user: {}", userId);
        List<Conversation> conversations = conversationRepository.findByUserId(userId);
        
        return conversations.stream()
            .map(conv -> convertToDTO(conv, userId))
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ConversationDTO getConversation(Long conversationId, Long userId) {
        log.debug("Getting conversation {} for user {}", conversationId, userId);
        Conversation conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new RuntimeException("Conversation not found or access denied"));
        
        return convertToDTO(conversation, userId);
    }
    
    @Transactional
    public ConversationDTO createConversation(CreateConversationRequest request, Long currentUserId, 
                                              String currentUserName, String currentUserEmail, 
                                              String currentUserRole, String currentUserAvatar) {
        log.debug("Creating conversation for user: {}", currentUserId);
        
        // Vérifier si c'est une conversation directe et si elle existe déjà
        if (request.getType() == Conversation.ConversationType.DIRECT && request.getParticipantIds().size() == 1) {
            Long otherUserId = request.getParticipantIds().get(0);
            var existing = conversationRepository.findDirectConversation(currentUserId, otherUserId);
            if (existing.isPresent()) {
                return convertToDTO(existing.get(), currentUserId);
            }
        }
        
        Conversation conversation = new Conversation();
        conversation.setType(request.getType());
        conversation.setTitle(request.getTitle());
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        
        conversation = conversationRepository.save(conversation);
        
        // Ajouter l'utilisateur actuel comme participant
        addParticipant(conversation, currentUserId, currentUserName, currentUserEmail, 
                      currentUserRole, currentUserAvatar);
        
        // Ajouter les autres participants
        for (Long participantId : request.getParticipantIds()) {
            if (!participantId.equals(currentUserId)) {
                // Récupérer les infos utilisateur depuis auth-service
                AuthServiceClient.UserInfo userInfo = authServiceClient.getUserInfo(participantId);
                addParticipant(conversation, participantId, userInfo.getFullName(), 
                             userInfo.getEmail(), userInfo.getRole(), userInfo.getProfilePhotoUrl());
            }
        }
        
        // Recharger la conversation avec les participants pour éviter LazyInitializationException
        conversation = conversationRepository.findByIdWithParticipants(conversation.getId())
            .orElseThrow(() -> new RuntimeException("Conversation not found after creation"));
        
        return convertToDTO(conversation, currentUserId);
    }
    
    private void addParticipant(Conversation conversation, Long userId, String userName, 
                               String userEmail, String userRole, String userAvatar) {
        ConversationParticipant participant = new ConversationParticipant();
        participant.setConversation(conversation);
        participant.setUserId(userId);
        participant.setUserName(userName);
        participant.setUserEmail(userEmail);
        participant.setUserRole(userRole);
        participant.setUserAvatar(userAvatar);
        participant.setIsActive(true);
        participant.setJoinedAt(LocalDateTime.now());
        
        participantRepository.save(participant);
    }
    
    @Transactional(readOnly = true)
    public Page<MessageDTO> getMessages(Long conversationId, Long userId, int page, int size) {
        log.debug("Getting messages for conversation {} (page: {}, size: {})", conversationId, page, size);
        
        // Vérifier que l'utilisateur est participant
        if (!participantRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new RuntimeException("Access denied to this conversation");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Message> messages = messageRepository.findMessagesByConversationId(conversationId, pageable);
        
        return messages.map(this::convertToMessageDTO);
    }
    
    @Transactional
    public MessageDTO sendMessage(Long conversationId, SendMessageRequest request, Long senderId, 
                                  String senderName, String senderAvatar) {
        log.debug("Sending message to conversation {} from user {}", conversationId, senderId);
        
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        // Vérifier que l'utilisateur est participant
        if (!participantRepository.existsByConversationIdAndUserId(conversationId, senderId)) {
            throw new RuntimeException("Access denied to this conversation");
        }
        
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(senderId);
        message.setSenderName(senderName);
        message.setSenderAvatar(senderAvatar);
        message.setContent(request.getContent());
        message.setMessageType(request.getMessageType());
        message.setFileUrl(request.getFileUrl());
        message.setFileName(request.getFileName());
        message.setFileSize(request.getFileSize());
        message.setIsEdited(false);
        message.setCreatedAt(LocalDateTime.now());
        
        message = messageRepository.save(message);
        
        // Mettre à jour la conversation
        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        
        return convertToMessageDTO(message);
    }
    
    @Transactional
    public void markAsRead(Long conversationId, Long userId) {
        log.debug("Marking messages as read for conversation {} by user {}", conversationId, userId);
        
        ConversationParticipant participant = participantRepository
            .findByConversationIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new RuntimeException("Participant not found"));
        
        participant.setLastReadAt(LocalDateTime.now());
        participantRepository.save(participant);
    }
    
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }
    
    private ConversationDTO convertToDTO(Conversation conversation, Long currentUserId) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setType(conversation.getType());
        dto.setTitle(conversation.getTitle());
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setLastMessageAt(conversation.getLastMessageAt());
        
        // Participants
        List<ParticipantDTO> participants = conversation.getParticipants().stream()
            .map(this::convertToParticipantDTO)
            .collect(Collectors.toList());
        dto.setParticipants(participants);
        
        // Dernier message
        if (!conversation.getMessages().isEmpty()) {
            Message lastMessage = conversation.getMessages().stream()
                .max((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()))
                .orElse(null);
            if (lastMessage != null) {
                dto.setLastMessage(convertToMessageDTO(lastMessage));
            }
        }
        
        // Compter les messages non lus
        ConversationParticipant currentParticipant = conversation.getParticipants().stream()
            .filter(p -> p.getUserId().equals(currentUserId))
            .findFirst()
            .orElse(null);
        
        if (currentParticipant != null) {
            LocalDateTime lastRead = currentParticipant.getLastReadAt();
            long unreadCount = conversation.getMessages().stream()
                .filter(m -> !m.getSenderId().equals(currentUserId))
                .filter(m -> lastRead == null || m.getCreatedAt().isAfter(lastRead))
                .count();
            dto.setUnreadCount(unreadCount);
        } else {
            dto.setUnreadCount(0L);
        }
        
        return dto;
    }
    
    private ParticipantDTO convertToParticipantDTO(ConversationParticipant participant) {
        ParticipantDTO dto = new ParticipantDTO();
        dto.setUserId(participant.getUserId());
        dto.setUserName(participant.getUserName());
        dto.setUserEmail(participant.getUserEmail());
        dto.setUserRole(participant.getUserRole());
        dto.setUserAvatar(participant.getUserAvatar());
        dto.setIsOnline(false); // TODO: Implémenter le statut en ligne
        dto.setLastReadAt(participant.getLastReadAt());
        return dto;
    }
    
    private MessageDTO convertToMessageDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        dto.setSenderId(message.getSenderId());
        dto.setSenderName(message.getSenderName());
        dto.setSenderAvatar(message.getSenderAvatar());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setFileUrl(message.getFileUrl());
        dto.setFileName(message.getFileName());
        dto.setFileSize(message.getFileSize());
        dto.setIsEdited(message.getIsEdited());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setUpdatedAt(message.getUpdatedAt());
        
        // Read statuses
        List<ReadStatusDTO> readStatuses = message.getReadStatuses().stream()
            .map(rs -> new ReadStatusDTO(rs.getUserId(), "User " + rs.getUserId(), rs.getReadAt()))
            .collect(Collectors.toList());
        dto.setReadBy(readStatuses);
        
        return dto;
    }
}
