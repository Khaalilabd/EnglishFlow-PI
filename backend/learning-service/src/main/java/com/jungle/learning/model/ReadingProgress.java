package com.jungle.learning.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reading_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadingProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ebook_id", nullable = false)
    private Ebook ebook;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "current_page")
    private Integer currentPage = 0;

    @Column(name = "total_pages")
    private Integer totalPages;

    @Column(name = "progress_percentage")
    private Double progressPercentage = 0.0;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @Column(name = "reading_time_minutes")
    private Integer readingTimeMinutes = 0;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL)
    private List<Note> notes = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (totalPages != null && totalPages > 0) {
            progressPercentage = (currentPage.doubleValue() / totalPages) * 100;
        }
        if (progressPercentage >= 100) {
            isCompleted = true;
        }
    }
}
