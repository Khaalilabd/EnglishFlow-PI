package com.englishflow.messaging.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_participants", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"conversation_id", "user_id"}),
       indexes = {
           @Index(name = "idx_participant_user", columnList = "user_id"),
           @Index(name = "idx_participant_conversation", columnList = "conversation_id"),
           @Index(name = "idx_participant_active", columnList = "is_active")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationParticipant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "user_email", nullable = false)
    private String userEmail;
    
    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @Column(name = "user_role", nullable = false, length = 50)
    private String userRole;
    
    @Column(name = "user_avatar", length = 500)
    private String userAvatar;
    
    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;
    
    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
