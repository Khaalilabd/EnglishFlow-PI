package com.englishflow.event.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer eventId;

    @Column(nullable = false)
    private Long senderId;

    private String senderName;

    @Column(nullable = false, length = 1000)
    private String content;

    private String translatedContent; // cached translation

    private boolean moderated = false; // flagged by moderator

    private LocalDateTime sentAt = LocalDateTime.now();
}
