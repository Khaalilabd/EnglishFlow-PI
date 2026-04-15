package com.englishflow.courses.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "online_meeting_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineMeetingSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long lessonId;
    
    @Column(nullable = false)
    private String roomId;
    
    @Column(nullable = false)
    private String inviteLink;
    
    @Column(nullable = false)
    private Long tutorId;
    
    @Column(nullable = false)
    private LocalDateTime startedAt;
    
    @Column
    private LocalDateTime endedAt;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }
}
