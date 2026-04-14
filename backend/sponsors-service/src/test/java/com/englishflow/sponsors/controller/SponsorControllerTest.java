package com.englishflow.sponsors.controller;

import com.englishflow.sponsors.dto.SponsorDTO;
import com.englishflow.sponsors.entity.Sponsor;
import com.englishflow.sponsors.exception.SponsorNotFoundException;
import com.englishflow.sponsors.service.SponsorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SponsorControllerTest {

    @Mock
    private SponsorService sponsorService;

    @InjectMocks
    private SponsorController sponsorController;

    private SponsorDTO sponsorDTO;

    @BeforeEach
    void setUp() {
        sponsorDTO = SponsorDTO.builder()
                .id(1L)
                .name("Tech Corp")
                .description("Technology company")
                .contactEmail("contact@techcorp.com")
                .contributionAmount(1500.0)
                .level(Sponsor.SponsorLevel.GOLD)
                .status(Sponsor.SponsorStatus.PENDING)
                .build();
    }

    @Test
    void getAllSponsors_ShouldReturnListOfSponsors() {
        List<SponsorDTO> sponsors = Arrays.asList(sponsorDTO);
        when(sponsorService.getAllSponsors()).thenReturn(sponsors);

        ResponseEntity<List<SponsorDTO>> response = sponsorController.getAllSponsors();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(sponsorService).getAllSponsors();
    }

    @Test
    void getAllSponsors_WhenEmpty_ShouldReturnEmptyList() {
        when(sponsorService.getAllSponsors()).thenReturn(Arrays.asList());

        ResponseEntity<List<SponsorDTO>> response = sponsorController.getAllSponsors();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getSponsorById_WhenExists_ShouldReturnSponsor() {
        when(sponsorService.getSponsorById(1L)).thenReturn(sponsorDTO);

        ResponseEntity<SponsorDTO> response = sponsorController.getSponsorById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        verify(sponsorService).getSponsorById(1L);
    }

    @Test
    void getSponsorById_WhenNotExists_ShouldThrowException() {
        when(sponsorService.getSponsorById(999L)).thenThrow(new SponsorNotFoundException(999L));

        assertThatThrownBy(() -> sponsorController.getSponsorById(999L))
                .isInstanceOf(SponsorNotFoundException.class);
    }

    @Test
    void getSponsorsByLevel_ShouldReturnFilteredSponsors() {
        List<SponsorDTO> sponsors = Arrays.asList(sponsorDTO);
        when(sponsorService.getSponsorsByLevel(Sponsor.SponsorLevel.GOLD)).thenReturn(sponsors);

        ResponseEntity<List<SponsorDTO>> response = sponsorController.getSponsorsByLevel(Sponsor.SponsorLevel.GOLD);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(sponsorService).getSponsorsByLevel(Sponsor.SponsorLevel.GOLD);
    }

    @Test
    void getSponsorsByLevel_WhenNoMatches_ShouldReturnEmptyList() {
        when(sponsorService.getSponsorsByLevel(Sponsor.SponsorLevel.BRONZE)).thenReturn(Arrays.asList());

        ResponseEntity<List<SponsorDTO>> response = sponsorController.getSponsorsByLevel(Sponsor.SponsorLevel.BRONZE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void createSponsor_WithValidData_ShouldReturnCreated() {
        when(sponsorService.createSponsor(any(SponsorDTO.class))).thenReturn(sponsorDTO);

        ResponseEntity<SponsorDTO> response = sponsorController.createSponsor(sponsorDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        verify(sponsorService).createSponsor(any(SponsorDTO.class));
    }

    @Test
    void updateSponsor_WithValidData_ShouldReturnUpdated() {
        SponsorDTO updatedDTO = SponsorDTO.builder()
                .id(1L)
                .name("Updated Corp")
                .contributionAmount(2000.0)
                .build();

        when(sponsorService.updateSponsor(eq(1L), any(SponsorDTO.class))).thenReturn(updatedDTO);

        ResponseEntity<SponsorDTO> response = sponsorController.updateSponsor(1L, sponsorDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(sponsorService).updateSponsor(eq(1L), any(SponsorDTO.class));
    }

    @Test
    void updateSponsor_WhenNotExists_ShouldThrowException() {
        when(sponsorService.updateSponsor(eq(999L), any(SponsorDTO.class)))
                .thenThrow(new SponsorNotFoundException(999L));

        assertThatThrownBy(() -> sponsorController.updateSponsor(999L, sponsorDTO))
                .isInstanceOf(SponsorNotFoundException.class);
    }

    @Test
    void getSponsorsByUser_ShouldReturnUserSponsors() {
        List<SponsorDTO> sponsors = Arrays.asList(sponsorDTO);
        when(sponsorService.getSponsorsByUserId(100L)).thenReturn(sponsors);

        ResponseEntity<List<SponsorDTO>> response = sponsorController.getSponsorsByUser(100L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(sponsorService).getSponsorsByUserId(100L);
    }

    @Test
    void getPendingSponsors_ShouldReturnOnlyPending() {
        sponsorDTO.setStatus(Sponsor.SponsorStatus.PENDING);
        List<SponsorDTO> sponsors = Arrays.asList(sponsorDTO);
        when(sponsorService.getPendingSponsors()).thenReturn(sponsors);

        ResponseEntity<List<SponsorDTO>> response = sponsorController.getPendingSponsors();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(sponsorService).getPendingSponsors();
    }

    @Test
    void getApprovedSponsors_ShouldReturnOnlyApproved() {
        sponsorDTO.setStatus(Sponsor.SponsorStatus.APPROVED);
        List<SponsorDTO> sponsors = Arrays.asList(sponsorDTO);
        when(sponsorService.getApprovedSponsors()).thenReturn(sponsors);

        ResponseEntity<List<SponsorDTO>> response = sponsorController.getApprovedSponsors();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(sponsorService).getApprovedSponsors();
    }

    @Test
    void approveSponsor_ShouldChangeStatusToApproved() {
        sponsorDTO.setStatus(Sponsor.SponsorStatus.APPROVED);
        when(sponsorService.approveSponsor(1L)).thenReturn(sponsorDTO);

        ResponseEntity<SponsorDTO> response = sponsorController.approveSponsor(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(Sponsor.SponsorStatus.APPROVED);
        verify(sponsorService).approveSponsor(1L);
    }

    @Test
    void approveSponsor_WhenNotExists_ShouldThrowException() {
        when(sponsorService.approveSponsor(999L)).thenThrow(new SponsorNotFoundException(999L));

        assertThatThrownBy(() -> sponsorController.approveSponsor(999L))
                .isInstanceOf(SponsorNotFoundException.class);
    }

    @Test
    void rejectSponsor_ShouldChangeStatusToRejected() {
        sponsorDTO.setStatus(Sponsor.SponsorStatus.REJECTED);
        when(sponsorService.rejectSponsor(1L)).thenReturn(sponsorDTO);

        ResponseEntity<SponsorDTO> response = sponsorController.rejectSponsor(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(Sponsor.SponsorStatus.REJECTED);
        verify(sponsorService).rejectSponsor(1L);
    }

    @Test
    void rejectSponsor_WhenNotExists_ShouldThrowException() {
        when(sponsorService.rejectSponsor(999L)).thenThrow(new SponsorNotFoundException(999L));

        assertThatThrownBy(() -> sponsorController.rejectSponsor(999L))
                .isInstanceOf(SponsorNotFoundException.class);
    }

    @Test
    void deleteSponsor_WhenExists_ShouldReturnNoContent() {
        doNothing().when(sponsorService).deleteSponsor(1L);

        ResponseEntity<Void> response = sponsorController.deleteSponsor(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(sponsorService).deleteSponsor(1L);
    }

    @Test
    void deleteSponsor_WhenNotExists_ShouldThrowException() {
        doThrow(new SponsorNotFoundException(999L)).when(sponsorService).deleteSponsor(999L);

        assertThatThrownBy(() -> sponsorController.deleteSponsor(999L))
                .isInstanceOf(SponsorNotFoundException.class);
    }
}
