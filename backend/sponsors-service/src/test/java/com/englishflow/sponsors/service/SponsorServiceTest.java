package com.englishflow.sponsors.service;

import com.englishflow.sponsors.client.ClubServiceFeignClient;
import com.englishflow.sponsors.dto.SponsorDTO;
import com.englishflow.sponsors.entity.Sponsor;
import com.englishflow.sponsors.exception.SponsorNotFoundException;
import com.englishflow.sponsors.mapper.SponsorMapper;
import com.englishflow.sponsors.repository.SponsorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SponsorServiceTest {

    @Mock
    private SponsorRepository sponsorRepository;

    @Mock
    private SponsorMapper sponsorMapper;

    @Mock
    private WebSocketNotificationService webSocketNotificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ClubServiceFeignClient clubServiceFeignClient;

    @InjectMocks
    private SponsorService sponsorService;

    private Sponsor testSponsor;
    private SponsorDTO testSponsorDTO;

    @BeforeEach
    void setUp() {
        testSponsor = new Sponsor();
        testSponsor.setId(1L);
        testSponsor.setName("Tech Corp");
        testSponsor.setContactEmail("contact@techcorp.com");
        testSponsor.setContactPhone("+216 12345678");
        testSponsor.setLevel(Sponsor.SponsorLevel.GOLD);
        testSponsor.setContributionAmount(5000.0);
        testSponsor.setStatus(Sponsor.SponsorStatus.PENDING);
        testSponsor.setUserId(1L);

        testSponsorDTO = new SponsorDTO();
        testSponsorDTO.setId(1L);
        testSponsorDTO.setName("Tech Corp");
        testSponsorDTO.setContactEmail("contact@techcorp.com");
        testSponsorDTO.setContactPhone("+216 12345678");
        testSponsorDTO.setLevel(Sponsor.SponsorLevel.GOLD);
        testSponsorDTO.setContributionAmount(5000.0);
        testSponsorDTO.setStatus(Sponsor.SponsorStatus.PENDING);
        testSponsorDTO.setUserId(1L);
    }

    @Test
    void getAllSponsors_ShouldReturnAllSponsors() {
        // Arrange
        List<Sponsor> sponsors = Arrays.asList(testSponsor);
        when(sponsorRepository.findAll()).thenReturn(sponsors);
        when(sponsorMapper.toDTO(any(Sponsor.class))).thenReturn(testSponsorDTO);

        // Act
        List<SponsorDTO> result = sponsorService.getAllSponsors();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sponsorRepository, times(1)).findAll();
    }

    @Test
    void getSponsorById_WhenSponsorExists_ShouldReturnSponsor() {
        // Arrange
        when(sponsorRepository.findById(1L)).thenReturn(Optional.of(testSponsor));
        when(sponsorMapper.toDTO(testSponsor)).thenReturn(testSponsorDTO);

        // Act
        SponsorDTO result = sponsorService.getSponsorById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Tech Corp", result.getName());
        verify(sponsorRepository, times(1)).findById(1L);
    }

    @Test
    void getSponsorById_WhenSponsorNotExists_ShouldThrowException() {
        // Arrange
        when(sponsorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SponsorNotFoundException.class, () -> sponsorService.getSponsorById(999L));
        verify(sponsorRepository, times(1)).findById(999L);
    }

    @Test
    void createSponsor_ShouldCreateAndReturnSponsor() {
        // Arrange
        when(sponsorMapper.toEntity(testSponsorDTO)).thenReturn(testSponsor);
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(testSponsor);
        when(sponsorMapper.toDTO(testSponsor)).thenReturn(testSponsorDTO);
        doNothing().when(webSocketNotificationService).notifySponsorCreated(any());

        // Act
        SponsorDTO result = sponsorService.createSponsor(testSponsorDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Tech Corp", result.getName());
        verify(sponsorRepository, times(1)).save(any(Sponsor.class));
        verify(webSocketNotificationService, times(1)).notifySponsorCreated(any());
    }

    @Test
    void getSponsorsByLevel_ShouldReturnFilteredSponsors() {
        // Arrange
        List<Sponsor> sponsors = Arrays.asList(testSponsor);
        when(sponsorRepository.findByLevel(Sponsor.SponsorLevel.GOLD)).thenReturn(sponsors);
        when(sponsorMapper.toDTO(any(Sponsor.class))).thenReturn(testSponsorDTO);

        // Act
        List<SponsorDTO> result = sponsorService.getSponsorsByLevel(Sponsor.SponsorLevel.GOLD);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sponsorRepository, times(1)).findByLevel(Sponsor.SponsorLevel.GOLD);
    }

    @Test
    void deleteSponsor_WhenSponsorExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(sponsorRepository.findById(1L)).thenReturn(Optional.of(testSponsor));
        doNothing().when(sponsorRepository).deleteById(1L);
        doNothing().when(webSocketNotificationService).notifySponsorDeleted(anyLong(), anyString());

        // Act
        sponsorService.deleteSponsor(1L);

        // Assert
        verify(sponsorRepository, times(1)).deleteById(1L);
        verify(webSocketNotificationService, times(1)).notifySponsorDeleted(anyLong(), anyString());
    }

    @Test
    void deleteSponsor_WhenSponsorNotExists_ShouldThrowException() {
        // Arrange
        when(sponsorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SponsorNotFoundException.class, () -> sponsorService.deleteSponsor(999L));
        verify(sponsorRepository, never()).deleteById(999L);
    }

    @Test
    void approveSponsor_ShouldUpdateStatusToApproved() {
        // Arrange
        when(sponsorRepository.findById(1L)).thenReturn(Optional.of(testSponsor));
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(testSponsor);
        when(sponsorMapper.toDTO(testSponsor)).thenReturn(testSponsorDTO);

        // Act
        SponsorDTO result = sponsorService.approveSponsor(1L);

        // Assert
        assertNotNull(result);
        assertEquals(Sponsor.SponsorStatus.APPROVED, testSponsor.getStatus());
        verify(sponsorRepository, times(1)).save(testSponsor);
    }

    @Test
    void rejectSponsor_ShouldUpdateStatusToRejected() {
        // Arrange
        when(sponsorRepository.findById(1L)).thenReturn(Optional.of(testSponsor));
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(testSponsor);
        when(sponsorMapper.toDTO(testSponsor)).thenReturn(testSponsorDTO);

        // Act
        SponsorDTO result = sponsorService.rejectSponsor(1L);

        // Assert
        assertNotNull(result);
        assertEquals(Sponsor.SponsorStatus.REJECTED, testSponsor.getStatus());
        verify(sponsorRepository, times(1)).save(testSponsor);
    }
}
