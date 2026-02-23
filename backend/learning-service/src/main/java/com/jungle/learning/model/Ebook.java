package com.jungle.learning.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "ebook")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ebook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String description;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type")
    private String mimeType = "application/pdf";

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private Level level;

    @Enumerated(EnumType.STRING)
    private Category category;

    // Metadata relation
    @OneToOne(mappedBy = "ebook", cascade = CascadeType.ALL)
    private EbookMetadata metadata;

    // Pricing
    @Column(name = "is_free")
    private Boolean isFree = true;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_model")
    private PricingModel pricingModel = PricingModel.FREE;

    // Stats
    @Column(name = "download_count")
    private Integer downloadCount = 0;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    // Publishing
    @Enumerated(EnumType.STRING)
    private PublishStatus status = PublishStatus.DRAFT;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "scheduled_for")
    private LocalDateTime scheduledFor;

    // Relations
    @OneToMany(mappedBy = "ebook", cascade = CascadeType.ALL)
    private List<EbookChapter> chapters = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "ebook_tags",
        joinColumns = @JoinColumn(name = "ebook_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "ebook", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

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
    }

    public enum Level {
        A1, A2, B1, B2, C1
    }

    public enum Category {
        GRAMMAR, VOCABULARY, BUSINESS, EXAM_PREP, GENERAL
    }

    public enum PricingModel {
        FREE, FREEMIUM, PREMIUM
    }

    public enum PublishStatus {
        DRAFT, SCHEDULED, PUBLISHED, ARCHIVED
    }
}
