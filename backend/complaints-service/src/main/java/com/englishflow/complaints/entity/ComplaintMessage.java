package com.englishflow.complaints.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaint_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long complaintId;
    
    @Column(nullable = false)
    private Long authorId;
    
    @Column(nullable = false)
    private String authorRole; // STUDENT, TUTOR, ACADEMIC_OFFICE_AFFAIR
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
}
