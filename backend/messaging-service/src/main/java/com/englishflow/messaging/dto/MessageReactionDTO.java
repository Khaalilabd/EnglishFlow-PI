package com.englishflow.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageReactionDTO {
    private Long id;
    private Long messageId;
    private Long userId;
    private String userName;
    private String emoji;
    private LocalDateTime createdAt;
}
