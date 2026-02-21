package com.englishflow.messaging.service;

import com.englishflow.messaging.client.AuthServiceClient;
import com.englishflow.messaging.constants.MessagingConstants;
import com.englishflow.messaging.dto.MessageReactionDTO;
import com.englishflow.messaging.dto.ReactionSummaryDTO;
import com.englishflow.messaging.exception.ResourceNotFoundException;
import com.englishflow.messaging.model.Message;
import com.englishflow.messaging.model.MessageReaction;
import com.englishflow.messaging.repository.MessageReactionRepository;
import com.englishflow.messaging.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageReactionService {
    
    private final MessageReactionRepository reactionRepository;
    private final MessageRepository messageRepository;
    private final AuthServiceClient authServiceClient;
    
    @Transactional
    public MessageReactionDTO toggleReaction(Long messageId, String emoji, Long userId) {
        log.debug("Toggling reaction {} for message {} by user {}", emoji, messageId, userId);
        
        // Vérifier que le message existe
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        
        // Récupérer les infos utilisateur
        AuthServiceClient.UserInfo userInfo = authServiceClient.getUserInfo(userId);
        String userName = userInfo.getFullName();
        
        // Vérifier si la réaction existe déjà
        var existingReaction = reactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, userId, emoji);
        
        if (existingReaction.isPresent()) {
            // Supprimer la réaction (toggle off)
            reactionRepository.delete(existingReaction.get());
            log.info("Removed reaction {} from message {} by user {}", emoji, messageId, userId);
            return null; // Indique que la réaction a été supprimée
        } else {
            // Ajouter la réaction (toggle on)
            MessageReaction reaction = new MessageReaction();
            reaction.setMessage(message);
            reaction.setUserId(userId);
            reaction.setUserName(userName);
            reaction.setEmoji(emoji);
            
            MessageReaction savedReaction = reactionRepository.save(reaction);
            log.info("Added reaction {} to message {} by user {}", emoji, messageId, userId);
            
            return mapToDTO(savedReaction);
        }
    }
    
    @Transactional(readOnly = true)
    public List<ReactionSummaryDTO> getReactionSummary(Long messageId, Long currentUserId) {
        List<MessageReaction> reactions = reactionRepository.findByMessageId(messageId);
        
        // Grouper par emoji
        Map<String, List<MessageReaction>> groupedReactions = reactions.stream()
            .collect(Collectors.groupingBy(MessageReaction::getEmoji));
        
        // Créer les résumés
        List<ReactionSummaryDTO> summaries = new ArrayList<>();
        for (Map.Entry<String, List<MessageReaction>> entry : groupedReactions.entrySet()) {
            String emoji = entry.getKey();
            List<MessageReaction> emojiReactions = entry.getValue();
            
            ReactionSummaryDTO summary = ReactionSummaryDTO.builder()
                .emoji(emoji)
                .count((long) emojiReactions.size())
                .userNames(emojiReactions.stream()
                    .map(MessageReaction::getUserName)
                    .collect(Collectors.toList()))
                .reactedByCurrentUser(emojiReactions.stream()
                    .anyMatch(r -> r.getUserId().equals(currentUserId)))
                .build();
            
            summaries.add(summary);
        }
        
        return summaries;
    }
    
    @Transactional(readOnly = true)
    public Map<Long, List<ReactionSummaryDTO>> getReactionSummariesForMessages(
            List<Long> messageIds, Long currentUserId) {
        
        List<MessageReaction> reactions = reactionRepository.findByMessageIdIn(messageIds);
        
        // Grouper par message ID puis par emoji
        Map<Long, Map<String, List<MessageReaction>>> groupedByMessageAndEmoji = reactions.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMessage().getId(),
                Collectors.groupingBy(MessageReaction::getEmoji)
            ));
        
        // Créer les résumés pour chaque message
        return groupedByMessageAndEmoji.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    Map<String, List<MessageReaction>> emojiGroups = entry.getValue();
                    return emojiGroups.entrySet().stream()
                        .map(emojiEntry -> ReactionSummaryDTO.builder()
                            .emoji(emojiEntry.getKey())
                            .count((long) emojiEntry.getValue().size())
                            .userNames(emojiEntry.getValue().stream()
                                .map(MessageReaction::getUserName)
                                .collect(Collectors.toList()))
                            .reactedByCurrentUser(emojiEntry.getValue().stream()
                                .anyMatch(r -> r.getUserId().equals(currentUserId)))
                            .build())
                        .collect(Collectors.toList());
                }
            ));
    }
    
    private MessageReactionDTO mapToDTO(MessageReaction reaction) {
        return MessageReactionDTO.builder()
            .id(reaction.getId())
            .messageId(reaction.getMessage().getId())
            .userId(reaction.getUserId())
            .userName(reaction.getUserName())
            .emoji(reaction.getEmoji())
            .createdAt(reaction.getCreatedAt())
            .build();
    }
}
