package com.englishflow.complaints.entity;

import com.englishflow.complaints.enums.ComplaintStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaint_workflows")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintWorkflow {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long complaintId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus fromStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus toStatus;
    
    @Column(nullable = false)
    private Long actorId; // Who made the change
    
    @Column(nullable = false)
    private String actorRole; // STUDENT, TUTOR, MANAGER, ACADEMIC_OFFICE_AFFAIR
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Column
    private Boolean isEscalation = false;
    
    @Column
    private String escalationReason;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
}
