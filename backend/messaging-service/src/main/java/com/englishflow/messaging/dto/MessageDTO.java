package com.englishflow.messaging.dto;

import com.englishflow.messaging.model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private String content;
    private Message.MessageType messageType;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String emojiCode;
    private Integer voiceDuration;
    private Boolean isEdited;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReadStatusDTO> readBy;
    private List<ReactionSummaryDTO> reactions;
    private MessageStatus status; // SENT, DELIVERED, READ
    
    public enum MessageStatus {
        SENT,      // Message envoyé mais pas encore reçu
        DELIVERED, // Message reçu par au moins un destinataire
        READ       // Message lu par au moins un destinataire
    }
}
