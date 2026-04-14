package com.englishflow.sponsors.dto;

import com.englishflow.sponsors.entity.Sponsor;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SponsorDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validSponsorDTO_ShouldPassValidation() {
        SponsorDTO dto = SponsorDTO.builder()
                .name("Valid Sponsor")
                .contactEmail("valid@email.com")
                .contributionAmount(500.0)
                .build();

        Set<ConstraintViolation<SponsorDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankName_ShouldFailValidation() {
        SponsorDTO dto = SponsorDTO.builder()
                .name("")
                .contactEmail("valid@email.com")
                .contributionAmount(500.0)
                .build();

        Set<ConstraintViolation<SponsorDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Sponsor name is required");
    }

    @Test
    void nullName_ShouldFailValidation() {
        SponsorDTO dto = SponsorDTO.builder()
                .name(null)
                .contactEmail("valid@email.com")
                .contributionAmount(500.0)
                .build();

        Set<ConstraintViolation<SponsorDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Sponsor name is required");
    }

    @Test
    void invalidEmail_ShouldFailValidation() {
        SponsorDTO dto = SponsorDTO.builder()
                .name("Valid Sponsor")
                .contactEmail("invalid-email")
                .contributionAmount(500.0)
                .build();

        Set<ConstraintViolation<SponsorDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Invalid email format");
    }

    @Test
    void nullContributionAmount_ShouldFailValidation() {
        SponsorDTO dto = SponsorDTO.builder()
                .name("Valid Sponsor")
                .contactEmail("valid@email.com")
                .contributionAmount(null)
                .build();

        Set<ConstraintViolation<SponsorDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Contribution amount is required");
    }

    @Test
    void negativeContributionAmount_ShouldFailValidation() {
        SponsorDTO dto = SponsorDTO.builder()
                .name("Valid Sponsor")
                .contactEmail("valid@email.com")
                .contributionAmount(-100.0)
                .build();

        Set<ConstraintViolation<SponsorDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Contribution amount must be positive");
    }

    @Test
    void zeroContributionAmount_ShouldPassValidation() {
        SponsorDTO dto = SponsorDTO.builder()
                .name("Valid Sponsor")
                .contactEmail("valid@email.com")
                .contributionAmount(0.0)
                .build();

        Set<ConstraintViolation<SponsorDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void multipleValidationErrors_ShouldReturnAllViolations() {
        SponsorDTO dto = SponsorDTO.builder()
                .name("")
                .contactEmail("invalid-email")
                .contributionAmount(-50.0)
                .build();

        Set<ConstraintViolation<SponsorDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(3);
    }

    @Test
    void builder_ShouldCreateDTOWithAllFields() {
        SponsorDTO dto = SponsorDTO.builder()
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

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Complete Sponsor");
        assertThat(dto.getDescription()).isEqualTo("Full description");
        assertThat(dto.getLogo()).isEqualTo("logo.png");
        assertThat(dto.getWebsite()).isEqualTo("https://sponsor.com");
        assertThat(dto.getContactEmail()).isEqualTo("contact@sponsor.com");
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
    void settersAndGetters_ShouldWorkCorrectly() {
        SponsorDTO dto = new SponsorDTO();
        dto.setId(1L);
        dto.setName("Test Sponsor");
        dto.setDescription("Test description");
        dto.setContributionAmount(750.0);
        dto.setLevel(Sponsor.SponsorLevel.SILVER);
        dto.setStatus(Sponsor.SponsorStatus.PENDING);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Sponsor");
        assertThat(dto.getDescription()).isEqualTo("Test description");
        assertThat(dto.getContributionAmount()).isEqualTo(750.0);
        assertThat(dto.getLevel()).isEqualTo(Sponsor.SponsorLevel.SILVER);
        assertThat(dto.getStatus()).isEqualTo(Sponsor.SponsorStatus.PENDING);
    }
}
