package com.englishflow.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadStatusDTO {
    private Long userId;
    private String userName;
    private LocalDateTime readAt;
}
