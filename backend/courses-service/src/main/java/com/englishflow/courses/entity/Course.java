package com.englishflow.courses.entity;

import com.englishflow.courses.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses", indexes = {
    @Index(name = "idx_courses_category", columnList = "category"),
    @Index(name = "idx_courses_level", columnList = "level"),
    @Index(name = "idx_courses_tutor_id", columnList = "tutorId"),
    @Index(name = "idx_courses_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    // Dynamic category from course_categories table
    @Column(name = "category")
    private String category;
    
    // CEFR level: A1, A2, B1, B2, C1, C2
    @Column(nullable = false, length = 10)
    private String level;
    
    private Integer maxStudents;
    
    private LocalDateTime schedule;
    
    // Duration in hours
    private Integer duration;
    
    @Column(nullable = false)
    private Long tutorId;
    
    // Course price (can be null if only in packs)
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    
    // File/resource URL
    private String fileUrl;
    
    // Thumbnail image URL
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;
    
    // Learning objectives (can be JSON or plain text)
    @Column(columnDefinition = "TEXT")
    private String objectives;
    
    // Prerequisites
    @Column(columnDefinition = "TEXT")
    private String prerequisites;
    
    // Featured course flag
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status = CourseStatus.DRAFT;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters = new ArrayList<>();
    
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
    
    // Helper method to get chapter count
    @Transient
    public int getChapterCount() {
        return chapters != null ? chapters.size() : 0;
    }
    
    // Helper method to get total lesson count
    @Transient
    public int getLessonCount() {
        if (chapters == null) return 0;
        return chapters.stream()
            .mapToInt(chapter -> chapter.getLessons() != null ? chapter.getLessons().size() : 0)
            .sum();
    }
}
