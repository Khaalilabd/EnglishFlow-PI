package com.englishflow.community.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vocabulary_words", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "word"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyWord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String word;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String definition;
    
    private String phonetic;
    
    @Column(name = "part_of_speech")
    private String partOfSpeech;
    
    @Column(columnDefinition = "TEXT")
    private String example;
    
    @Column(columnDefinition = "TEXT")
    private String synonyms;
    
    @Column(columnDefinition = "TEXT")
    private String antonyms;
    
    @Column(name = "audio_url", length = 500)
    private String audioUrl;
    
    @Column(name = "source_topic_id")
    private Long sourceTopicId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "mastery_level")
    private MasteryLevel masteryLevel = MasteryLevel.NEW;
    
    @Column(name = "review_count")
    private Integer reviewCount = 0;
    
    @Column(name = "last_reviewed_at")
    private LocalDateTime lastReviewedAt;
    
    @Column(name = "created_at", updatable = false)
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
    }
    
    public enum MasteryLevel {
        NEW,        // Just added
        LEARNING    // Being reviewed
    }
}
