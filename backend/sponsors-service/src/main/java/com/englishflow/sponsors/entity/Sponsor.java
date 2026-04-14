package com.englishflow.sponsors.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sponsors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String logo;

    private String website;
    private String contactEmail;
    private String contactPhone;

    // Link to the user account
    @Column
    private Long userId;

    @Column
    private String applicantFirstName;

    @Column
    private String applicantLastName;

    // Target club for this sponsorship
    @Column
    private Integer clubId;

    @Column
    private String clubName;

    @Enumerated(EnumType.STRING)
    private SponsorLevel level;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SponsorStatus status = SponsorStatus.PENDING;

    private Double contributionAmount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        roundContributionAmount();
        calculateAndSetLevel();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        roundContributionAmount();
        calculateAndSetLevel();
    }

    private void roundContributionAmount() {
        if (this.contributionAmount != null) {
            this.contributionAmount = (double) Math.round(this.contributionAmount);
        }
    }

    public void calculateAndSetLevel() {
        if (this.contributionAmount == null || this.contributionAmount < 0) {
            this.level = SponsorLevel.BRONZE;
            return;
        }
        double amount = this.contributionAmount;
        if (amount < 500.0) {
            this.level = SponsorLevel.BRONZE;
        } else if (amount < 1000.0) {
            this.level = SponsorLevel.SILVER;
        } else {
            this.level = SponsorLevel.GOLD;
        }
    }

    public enum SponsorLevel {
        GOLD, SILVER, BRONZE
    }

    public enum SponsorStatus {
        PENDING, APPROVED, REJECTED
    }
}
