package com.englishflow.club.entity;

import com.englishflow.club.enums.MembershipRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "membership_requests",
    indexes = {
        @Index(name = "idx_membership_request_club_id", columnList = "club_id"),
        @Index(name = "idx_membership_request_user_id", columnList = "userId"),
        @Index(name = "idx_membership_request_status", columnList = "status"),
        @Index(name = "idx_membership_request_club_user", columnList = "club_id,userId", unique = true)
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;
    
    @Column(nullable = false, name = "user_id")
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipRequestStatus status;
    
    @Column(length = 500)
    private String message;
    
    @Column(length = 2000)
    private String motivationLetter; // Lettre de motivation
    
    @Column(length = 1000)
    private String studentSkills; // Compétences de l'étudiant (stockées comme texte séparé par des virgules)
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;
    
    @Column
    private LocalDateTime reviewedAt;
    
    @Column
    private Long reviewedBy;
    
    @Column(length = 500)
    private String reviewComment;

    @Column(length = 50)
    private String paymentMethod; // KONNECT ou FLOUCI

    @Column(length = 500)
    private String paymentToken; // Token retourné par la passerelle de paiement

    @Column
    private LocalDateTime paymentConfirmedAt;

    @Column
    private LocalDateTime paymentDeadline; // 3 jours après approbation
    
    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
        if (status == null) {
            status = MembershipRequestStatus.PENDING;
        }
    }
}
