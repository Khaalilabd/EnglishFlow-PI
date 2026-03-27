package com.jungle.learning.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ebook_access")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EbookAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ebook_id", nullable = false)
    private Ebook ebook;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "accessed_at")
    private LocalDateTime accessedAt;

    @Column(name = "progress_percent")
    private Integer progressPercent = 0;

    @PrePersist
    protected void onCreate() {
        accessedAt = LocalDateTime.now();
    }
}
