package com.englishflow.club.entity;

import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.enums.UpdateRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "club_update_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubUpdateRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;
    
    @Column(name = "requested_by", nullable = false)
    private Long requestedBy; // ID du président qui fait la demande
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "objective", columnDefinition = "TEXT")
    private String objective;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private ClubCategory category;
    
    @Column(name = "max_members")
    private Integer maxMembers;
    
    @Column(name = "image", columnDefinition = "TEXT")
    private String image;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private UpdateRequestStatus status = UpdateRequestStatus.PENDING;
    
    @Column(name = "vice_president_approved")
    @Builder.Default
    private Boolean vicePresidentApproved = false;
    
    @Column(name = "secretary_approved")
    @Builder.Default
    private Boolean secretaryApproved = false;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "applied_at")
    private LocalDateTime appliedAt;
}
