package com.englishflow.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadStatusUpdateDTO {
    private Long conversationId;
    private Long userId;
    private String userName;
    private List<Long> messageIds; // IDs des messages qui ont été lus
    private LocalDateTime readAt;
}
