package com.englishflow.courses.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "session_attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionAttendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonIgnore
    private LessonSession session;
    
    @Column(nullable = false)
    private Long studentId;
    
    private LocalDateTime joinedAt;
    
    private LocalDateTime leftAt;
    
    @Column(nullable = false)
    private String attendanceStatus; // 'attended', 'absent', 'partial'
    
    private BigDecimal attendancePercentage;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (attendanceStatus == null) {
            attendanceStatus = "absent";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
