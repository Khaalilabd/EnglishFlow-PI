package com.englishflow.courses.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "online_lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineLesson {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false, unique = true)
    private Lesson lesson;
    
    @Column(nullable = false)
    private Integer durationMinutes;
    
    @Column(nullable = false)
    private String timezone;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @OneToMany(mappedBy = "onlineLesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonSchedule> schedules = new ArrayList<>();
    
    @OneToMany(mappedBy = "onlineLesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonSession> sessions = new ArrayList<>();
    
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
