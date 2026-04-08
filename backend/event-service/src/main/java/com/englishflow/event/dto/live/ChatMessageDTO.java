package com.englishflow.event.dto.live;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {
    private Long id;
    private Integer eventId;
    private Long senderId;
    private String senderName;
    private String content;
    private String translatedContent;
    private String targetLang; // requested translation language
    private boolean moderated;
    private LocalDateTime sentAt;
}
