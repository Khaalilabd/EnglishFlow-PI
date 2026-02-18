package com.englishflow.messaging.dto;

import com.englishflow.messaging.model.Conversation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long id;
    private Conversation.ConversationType type;
    private String title;
    private List<ParticipantDTO> participants;
    private MessageDTO lastMessage;
    private Long unreadCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
}
