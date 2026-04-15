package com.englishflow.courses.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lesson_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "online_lesson_id", nullable = false)
    @JsonIgnore
    private OnlineLesson onlineLesson;
    
    @Column(nullable = false)
    private LocalDate sessionDate;
    
    @Column(nullable = false)
    private LocalTime sessionTime;
    
    @Column(nullable = false)
    private String status; // 'scheduled', 'live', 'completed', 'cancelled'
    
    private String meetingUrl;
    
    private String recordingUrl;
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionAttendance> attendanceRecords = new ArrayList<>();
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "scheduled";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
