package com.englishflow.sponsors.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SponsorEntityTest {

    @Test
    void calculateAndSetLevel_WithAmountLessThan500_ShouldSetBronze() {
        Sponsor sponsor = Sponsor.builder()
                .name("Bronze Sponsor")
                .contributionAmount(300.0)
                .build();

        sponsor.calculateAndSetLevel();

        assertThat(sponsor.getLevel()).isEqualTo(Sponsor.SponsorLevel.BRONZE);
    }

    @Test
    void calculateAndSetLevel_WithAmountBetween500And1000_ShouldSetSilver() {
        Sponsor sponsor = Sponsor.builder()
                .name("Silver Sponsor")
                .contributionAmount(750.0)
                .build();

        sponsor.calculateAndSetLevel();

        assertThat(sponsor.getLevel()).isEqualTo(Sponsor.SponsorLevel.SILVER);
    }

    @Test
    void calculateAndSetLevel_WithAmount500_ShouldSetSilver() {
        Sponsor sponsor = Sponsor.builder()
                .name("Silver Sponsor")
                .contributionAmount(500.0)
                .build();

        sponsor.calculateAndSetLevel();

        assertThat(sponsor.getLevel()).isEqualTo(Sponsor.SponsorLevel.SILVER);
    }

    @Test
    void calculateAndSetLevel_WithAmount1000OrMore_ShouldSetGold() {
        Sponsor sponsor = Sponsor.builder()
                .name("Gold Sponsor")
                .contributionAmount(1500.0)
                .build();

        sponsor.calculateAndSetLevel();

        assertThat(sponsor.getLevel()).isEqualTo(Sponsor.SponsorLevel.GOLD);
    }

    @Test
    void calculateAndSetLevel_WithExactly1000_ShouldSetGold() {
        Sponsor sponsor = Sponsor.builder()
                .name("Gold Sponsor")
                .contributionAmount(1000.0)
                .build();

        sponsor.calculateAndSetLevel();

        assertThat(sponsor.getLevel()).isEqualTo(Sponsor.SponsorLevel.GOLD);
    }

    @Test
    void calculateAndSetLevel_WithNullAmount_ShouldSetBronze() {
        Sponsor sponsor = Sponsor.builder()
                .name("No Amount Sponsor")
                .contributionAmount(null)
                .build();

        sponsor.calculateAndSetLevel();

        assertThat(sponsor.getLevel()).isEqualTo(Sponsor.SponsorLevel.BRONZE);
    }

    @Test
    void calculateAndSetLevel_WithNegativeAmount_ShouldSetBronze() {
        Sponsor sponsor = Sponsor.builder()
                .name("Negative Sponsor")
                .contributionAmount(-100.0)
                .build();

        sponsor.calculateAndSetLevel();

        assertThat(sponsor.getLevel()).isEqualTo(Sponsor.SponsorLevel.BRONZE);
    }

    @Test
    void onCreate_ShouldSetTimestampsAndCalculateLevel() {
        Sponsor sponsor = Sponsor.builder()
                .name("New Sponsor")
                .contributionAmount(800.0)
                .build();

        sponsor.onCreate();

        assertThat(sponsor.getCreatedAt()).isNotNull();
        assertThat(sponsor.getUpdatedAt()).isNotNull();
        assertThat(sponsor.getLevel()).isEqualTo(Sponsor.SponsorLevel.SILVER);
    }

    @Test
    void onUpdate_ShouldUpdateTimestampAndRecalculateLevel() {
        Sponsor sponsor = Sponsor.builder()
                .name("Updated Sponsor")
                .contributionAmount(1200.0)
                .build();

        sponsor.onCreate();
        sponsor.setContributionAmount(400.0);
        sponsor.onUpdate();

        assertThat(sponsor.getUpdatedAt()).isNotNull();
        assertThat(sponsor.getLevel()).isEqualTo(Sponsor.SponsorLevel.BRONZE);
    }

    @Test
    void roundContributionAmount_ShouldRoundToNearestInteger() {
        Sponsor sponsor = Sponsor.builder()
                .name("Rounded Sponsor")
                .contributionAmount(499.7)
                .build();

        sponsor.onCreate();

        assertThat(sponsor.getContributionAmount()).isEqualTo(500.0);
    }

    @Test
    void builder_ShouldCreateSponsorWithAllFields() {
        Sponsor sponsor = Sponsor.builder()
                .id(1L)
                .name("Complete Sponsor")
                .description("Full description")
                .logo("logo.png")
                .website("https://sponsor.com")
                .contactEmail("contact@sponsor.com")
                .contactPhone("+1234567890")
                .userId(100L)
                .applicantFirstName("John")
                .applicantLastName("Doe")
                .clubId(5)
                .clubName("Tech Club")
                .level(Sponsor.SponsorLevel.GOLD)
                .status(Sponsor.SponsorStatus.APPROVED)
                .contributionAmount(1500.0)
                .build();

        assertThat(sponsor.getId()).isEqualTo(1L);
        assertThat(sponsor.getName()).isEqualTo("Complete Sponsor");
        assertThat(sponsor.getDescription()).isEqualTo("Full description");
        assertThat(sponsor.getLogo()).isEqualTo("logo.png");
        assertThat(sponsor.getWebsite()).isEqualTo("https://sponsor.com");
        assertThat(sponsor.getContactEmail()).isEqualTo("contact@sponsor.com");
        assertThat(sponsor.getContactPhone()).isEqualTo("+1234567890");
        assertThat(sponsor.getUserId()).isEqualTo(100L);
        assertThat(sponsor.getApplicantFirstName()).isEqualTo("John");
        assertThat(sponsor.getApplicantLastName()).isEqualTo("Doe");
        assertThat(sponsor.getClubId()).isEqualTo(5);
        assertThat(sponsor.getClubName()).isEqualTo("Tech Club");
        assertThat(sponsor.getLevel()).isEqualTo(Sponsor.SponsorLevel.GOLD);
        assertThat(sponsor.getStatus()).isEqualTo(Sponsor.SponsorStatus.APPROVED);
        assertThat(sponsor.getContributionAmount()).isEqualTo(1500.0);
    }

    @Test
    void defaultStatus_ShouldBePending() {
        Sponsor sponsor = Sponsor.builder()
                .name("Default Status Sponsor")
                .contributionAmount(500.0)
                .build();

        assertThat(sponsor.getStatus()).isEqualTo(Sponsor.SponsorStatus.PENDING);
    }
}
