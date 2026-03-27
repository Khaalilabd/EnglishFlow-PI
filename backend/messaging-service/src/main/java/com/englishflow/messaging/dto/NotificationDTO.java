package com.englishflow.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String type; // MESSAGE, REACTION, etc.
    private String title;
    private String message;
    private String senderName;
    private String senderAvatar;
    private Long conversationId;
    private Long messageId;
    private LocalDateTime createdAt;
    private Boolean isRead;
}
