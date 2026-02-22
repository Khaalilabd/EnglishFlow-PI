package com.englishflow.complaints.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaint_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintNotification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long complaintId;
    
    @Column(nullable = false)
    private Long recipientId;
    
    @Column(nullable = false)
    private String recipientRole;
    
    @Column(nullable = false)
    private String notificationType; // NEW_COMPLAINT, STATUS_CHANGE, ESCALATION, OVERDUE, RESPONSE_ADDED
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(nullable = false)
    private Boolean isRead = false;
    
    @Column
    private LocalDateTime readAt;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
