package com.englishflow.messaging.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_read_status",
       uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_id"}),
       indexes = {
           @Index(name = "idx_read_status_message", columnList = "message_id"),
           @Index(name = "idx_read_status_user", columnList = "user_id")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageReadStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @CreationTimestamp
    @Column(name = "read_at", nullable = false, updatable = false)
    private LocalDateTime readAt;
}
