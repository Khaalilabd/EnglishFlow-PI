package com.englishflow.courses.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(length = 50)
    private String icon; // Icon name or emoji
    
    @Column(length = 20)
    private String color; // Hex color code for UI
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(nullable = false)
    private Integer displayOrder = 0;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private Long createdBy;
}
