package com.englishflow.messaging.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_reactions", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_id", "emoji"}),
    indexes = {
        @Index(name = "idx_reaction_message", columnList = "message_id"),
        @Index(name = "idx_reaction_user", columnList = "user_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageReaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @Column(name = "emoji", nullable = false, length = 10)
    private String emoji;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
