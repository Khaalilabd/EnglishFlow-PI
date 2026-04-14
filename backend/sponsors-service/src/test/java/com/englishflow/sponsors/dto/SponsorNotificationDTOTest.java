package com.englishflow.sponsors.dto;

import com.englishflow.sponsors.entity.Sponsor;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SponsorNotificationDTOTest {

    @Test
    void builder_ShouldCreateNotificationDTOWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        SponsorDTO sponsorDTO = SponsorDTO.builder()
                .id(1L)
                .name("Tech Corp")
                .contributionAmount(1500.0)
                .build();

        SponsorNotificationDTO dto = SponsorNotificationDTO.builder()
                .type("CREATED")
                .sponsorId(1L)
                .sponsorName("Tech Corp")
                .message("New sponsor created")
                .timestamp(now)
                .sponsor(sponsorDTO)
                .build();

        assertThat(dto.getType()).isEqualTo("CREATED");
        assertThat(dto.getSponsorId()).isEqualTo(1L);
        assertThat(dto.getSponsorName()).isEqualTo("Tech Corp");
        assertThat(dto.getMessage()).isEqualTo("New sponsor created");
        assertThat(dto.getTimestamp()).isEqualTo(now);
        assertThat(dto.getSponsor()).isNotNull();
        assertThat(dto.getSponsor().getId()).isEqualTo(1L);
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyDTO() {
        SponsorNotificationDTO dto = new SponsorNotificationDTO();

        assertThat(dto.getType()).isNull();
        assertThat(dto.getSponsorId()).isNull();
        assertThat(dto.getSponsorName()).isNull();
        assertThat(dto.getMessage()).isNull();
        assertThat(dto.getTimestamp()).isNull();
        assertThat(dto.getSponsor()).isNull();
    }

    @Test
    void allArgsConstructor_ShouldCreateDTOWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        SponsorDTO sponsorDTO = SponsorDTO.builder()
                .id(2L)
                .name("Business Inc")
                .build();

        SponsorNotificationDTO dto = new SponsorNotificationDTO(
                "UPDATED", 2L, "Business Inc", "Sponsor updated", now, sponsorDTO
        );

        assertThat(dto.getType()).isEqualTo("UPDATED");
        assertThat(dto.getSponsorId()).isEqualTo(2L);
        assertThat(dto.getSponsorName()).isEqualTo("Business Inc");
        assertThat(dto.getMessage()).isEqualTo("Sponsor updated");
        assertThat(dto.getTimestamp()).isEqualTo(now);
        assertThat(dto.getSponsor()).isEqualTo(sponsorDTO);
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        SponsorNotificationDTO dto = new SponsorNotificationDTO();
        LocalDateTime now = LocalDateTime.now();
        SponsorDTO sponsorDTO = SponsorDTO.builder()
                .id(3L)
                .name("Sponsor Name")
                .build();

        dto.setType("DELETED");
        dto.setSponsorId(3L);
        dto.setSponsorName("Sponsor Name");
        dto.setMessage("Sponsor deleted");
        dto.setTimestamp(now);
        dto.setSponsor(sponsorDTO);

        assertThat(dto.getType()).isEqualTo("DELETED");
        assertThat(dto.getSponsorId()).isEqualTo(3L);
        assertThat(dto.getSponsorName()).isEqualTo("Sponsor Name");
        assertThat(dto.getMessage()).isEqualTo("Sponsor deleted");
        assertThat(dto.getTimestamp()).isEqualTo(now);
        assertThat(dto.getSponsor()).isEqualTo(sponsorDTO);
    }

    @Test
    void builder_WithoutSponsorData_ShouldCreateNotification() {
        LocalDateTime now = LocalDateTime.now();

        SponsorNotificationDTO dto = SponsorNotificationDTO.builder()
                .type("DELETED")
                .sponsorId(5L)
                .sponsorName("Deleted Sponsor")
                .message("Sponsor has been deleted")
                .timestamp(now)
                .build();

        assertThat(dto.getType()).isEqualTo("DELETED");
        assertThat(dto.getSponsorId()).isEqualTo(5L);
        assertThat(dto.getSponsorName()).isEqualTo("Deleted Sponsor");
        assertThat(dto.getMessage()).isEqualTo("Sponsor has been deleted");
        assertThat(dto.getTimestamp()).isEqualTo(now);
        assertThat(dto.getSponsor()).isNull();
    }
}
