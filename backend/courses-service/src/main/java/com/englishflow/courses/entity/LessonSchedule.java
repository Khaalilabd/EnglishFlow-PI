package com.englishflow.courses.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "lesson_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "online_lesson_id", nullable = false)
    @JsonIgnore
    private OnlineLesson onlineLesson;
    
    @Column(nullable = false)
    private Integer dayOfWeek; // 0=Sunday, 1=Monday, ..., 6=Saturday
    
    @Column(nullable = false)
    private LocalTime time;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
