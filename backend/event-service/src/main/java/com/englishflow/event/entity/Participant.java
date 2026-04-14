package com.englishflow.event.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "participants", indexes = {
    @Index(name = "idx_participant_event", columnList = "event_id"),
    @Index(name = "idx_participant_user", columnList = "userId"),
    @Index(name = "idx_participant_event_user", columnList = "event_id,userId", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference
    private Event event;
    
    @Column(nullable = false)
    private Long userId;

    @Column
    private String clubRole;
    
    @Column(nullable = false)
    private LocalDateTime joinDate;

    // ── Payment fields ──────────────────────────────────────────
    @Column
    private String paymentStatus; // PAYMENT_PENDING | PAID

    @Column(length = 50)
    private String paymentMethod; // KONNECT

    @Column(length = 500)
    private String paymentToken;

    @Column
    private LocalDateTime paymentConfirmedAt;

    @Column
    private LocalDateTime paymentDeadline;
    
    @PrePersist
    protected void onCreate() {
        joinDate = LocalDateTime.now();
    }
}
