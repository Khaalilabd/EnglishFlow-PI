package com.englishflow.sponsors.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SponsorLevelTest {

    @Test
    void sponsorLevel_ShouldHaveThreeLevels() {
        Sponsor.SponsorLevel[] levels = Sponsor.SponsorLevel.values();
        
        assertThat(levels).hasSize(3);
        assertThat(levels).contains(
                Sponsor.SponsorLevel.GOLD,
                Sponsor.SponsorLevel.SILVER,
                Sponsor.SponsorLevel.BRONZE
        );
    }

    @Test
    void sponsorLevel_ValueOf_ShouldReturnCorrectEnum() {
        assertThat(Sponsor.SponsorLevel.valueOf("GOLD")).isEqualTo(Sponsor.SponsorLevel.GOLD);
        assertThat(Sponsor.SponsorLevel.valueOf("SILVER")).isEqualTo(Sponsor.SponsorLevel.SILVER);
        assertThat(Sponsor.SponsorLevel.valueOf("BRONZE")).isEqualTo(Sponsor.SponsorLevel.BRONZE);
    }

    @Test
    void sponsorStatus_ShouldHaveThreeStatuses() {
        Sponsor.SponsorStatus[] statuses = Sponsor.SponsorStatus.values();
        
        assertThat(statuses).hasSize(3);
        assertThat(statuses).contains(
                Sponsor.SponsorStatus.PENDING,
                Sponsor.SponsorStatus.APPROVED,
                Sponsor.SponsorStatus.REJECTED
        );
    }

    @Test
    void sponsorStatus_ValueOf_ShouldReturnCorrectEnum() {
        assertThat(Sponsor.SponsorStatus.valueOf("PENDING")).isEqualTo(Sponsor.SponsorStatus.PENDING);
        assertThat(Sponsor.SponsorStatus.valueOf("APPROVED")).isEqualTo(Sponsor.SponsorStatus.APPROVED);
        assertThat(Sponsor.SponsorStatus.valueOf("REJECTED")).isEqualTo(Sponsor.SponsorStatus.REJECTED);
    }
}
