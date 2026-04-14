package com.englishflow.sponsors.mapper;

import com.englishflow.sponsors.dto.SponsorDTO;
import com.englishflow.sponsors.entity.Sponsor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SponsorMapperImplTest {

    private SponsorMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new SponsorMapperImpl();
    }

    @Test
    void toDTO_ShouldMapAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Sponsor sponsor = Sponsor.builder()
                .id(1L)
                .name("Tech Corp")
                .description("Technology company")
                .logo("logo.png")
                .website("https://techcorp.com")
                .contactEmail("contact@techcorp.com")
                .contactPhone("+1234567890")
                .userId(100L)
                .applicantFirstName("John")
                .applicantLastName("Doe")
                .clubId(5)
                .clubName("Tech Club")
                .level(Sponsor.SponsorLevel.GOLD)
                .status(Sponsor.SponsorStatus.APPROVED)
                .contributionAmount(1500.0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        SponsorDTO dto = mapper.toDTO(sponsor);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Tech Corp");
        assertThat(dto.getDescription()).isEqualTo("Technology company");
        assertThat(dto.getLogo()).isEqualTo("logo.png");
        assertThat(dto.getWebsite()).isEqualTo("https://techcorp.com");
        assertThat(dto.getContactEmail()).isEqualTo("contact@techcorp.com");
        assertThat(dto.getContactPhone()).isEqualTo("+1234567890");
        assertThat(dto.getUserId()).isEqualTo(100L);
        assertThat(dto.getApplicantFirstName()).isEqualTo("John");
        assertThat(dto.getApplicantLastName()).isEqualTo("Doe");
        assertThat(dto.getClubId()).isEqualTo(5);
        assertThat(dto.getClubName()).isEqualTo("Tech Club");
        assertThat(dto.getLevel()).isEqualTo(Sponsor.SponsorLevel.GOLD);
        assertThat(dto.getStatus()).isEqualTo(Sponsor.SponsorStatus.APPROVED);
        assertThat(dto.getContributionAmount()).isEqualTo(1500.0);
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        SponsorDTO dto = SponsorDTO.builder()
                .name("New Sponsor")
                .description("New description")
                .logo("new-logo.png")
                .website("https://newsponsor.com")
                .contactEmail("new@sponsor.com")
                .contactPhone("+9876543210")
                .userId(200L)
                .applicantFirstName("Jane")
                .applicantLastName("Smith")
                .clubId(10)
                .clubName("Science Club")
                .contributionAmount(800.0)
                .build();

        Sponsor entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("New Sponsor");
        assertThat(entity.getDescription()).isEqualTo("New description");
        assertThat(entity.getLogo()).isEqualTo("new-logo.png");
        assertThat(entity.getWebsite()).isEqualTo("https://newsponsor.com");
        assertThat(entity.getContactEmail()).isEqualTo("new@sponsor.com");
        assertThat(entity.getContactPhone()).isEqualTo("+9876543210");
        assertThat(entity.getUserId()).isEqualTo(200L);
        assertThat(entity.getApplicantFirstName()).isEqualTo("Jane");
        assertThat(entity.getApplicantLastName()).isEqualTo("Smith");
        assertThat(entity.getClubId()).isEqualTo(10);
        assertThat(entity.getClubName()).isEqualTo("Science Club");
        assertThat(entity.getContributionAmount()).isEqualTo(800.0);
    }

    @Test
    void updateEntityFromDTO_ShouldUpdateFields() {
        Sponsor existingEntity = Sponsor.builder()
                .id(1L)
                .name("Old Name")
                .description("Old description")
                .contributionAmount(500.0)
                .level(Sponsor.SponsorLevel.SILVER)
                .status(Sponsor.SponsorStatus.PENDING)
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();

        SponsorDTO updateDTO = SponsorDTO.builder()
                .name("Updated Name")
                .description("Updated description")
                .contactEmail("updated@sponsor.com")
                .contributionAmount(1200.0)
                .build();

        mapper.updateEntityFromDTO(updateDTO, existingEntity);

        assertThat(existingEntity.getName()).isEqualTo("Updated Name");
        assertThat(existingEntity.getDescription()).isEqualTo("Updated description");
        assertThat(existingEntity.getContactEmail()).isEqualTo("updated@sponsor.com");
        assertThat(existingEntity.getContributionAmount()).isEqualTo(1200.0);
    }

    @Test
    void toDTO_WithNullEntity_ShouldReturnNull() {
        SponsorDTO dto = mapper.toDTO(null);
        assertThat(dto).isNull();
    }

    @Test
    void toEntity_WithNullDTO_ShouldReturnNull() {
        Sponsor entity = mapper.toEntity(null);
        assertThat(entity).isNull();
    }
}
