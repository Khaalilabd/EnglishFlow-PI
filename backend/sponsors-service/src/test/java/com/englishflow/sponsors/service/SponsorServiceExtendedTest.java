package com.englishflow.sponsors.service;

import com.englishflow.sponsors.client.ClubServiceFeignClient;
import com.englishflow.sponsors.dto.ExpenseDTO;
import com.englishflow.sponsors.dto.MemberDTO;
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
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SponsorServiceExtendedTest {

    @Mock
    private SponsorRepository sponsorRepository;

    @Mock
    private SponsorMapper sponsorMapper;

    @Mock
    private ClubServiceFeignClient clubServiceFeignClient;

    @Mock
    private EmailService emailService;

    @Mock
    private WebSocketNotificationService webSocketNotificationService;

    @InjectMocks
    private SponsorService sponsorService;

    private Sponsor sponsor;
    private SponsorDTO sponsorDTO;

    @BeforeEach
    void setUp() {
        sponsor = Sponsor.builder()
                .id(1L)
                .name("Tech Corp")
                .description("Technology company")
                .contactEmail("contact@techcorp.com")
                .contributionAmount(1500.0)
                .level(Sponsor.SponsorLevel.GOLD)
                .status(Sponsor.SponsorStatus.PENDING)
                .clubId(1)
                .clubName("Tech Club")
                .userId(100L)
                .applicantFirstName("John")
                .applicantLastName("Doe")
                .build();

        sponsorDTO = SponsorDTO.builder()
                .id(1L)
                .name("Tech Corp")
                .description("Technology company")
                .contactEmail("contact@techcorp.com")
                .contributionAmount(1500.0)
                .level(Sponsor.SponsorLevel.GOLD)
                .status(Sponsor.SponsorStatus.PENDING)
                .clubId(1)
                .clubName("Tech Club")
                .userId(100L)
                .applicantFirstName("John")
                .applicantLastName("Doe")
                .build();
    }

    @Test
    void updateSponsor_WhenExists_ShouldUpdateAndReturn() {
        SponsorDTO updateDTO = SponsorDTO.builder()
                .name("Updated Name")
                .contributionAmount(2000.0)
                .build();

        when(sponsorRepository.findById(1L)).thenReturn(Optional.of(sponsor));
        doNothing().when(sponsorMapper).updateEntityFromDTO(any(), any());
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);
        when(sponsorMapper.toDTO(any(Sponsor.class))).thenReturn(sponsorDTO);

        SponsorDTO result = sponsorService.updateSponsor(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(sponsorRepository).findById(1L);
        verify(sponsorMapper).updateEntityFromDTO(updateDTO, sponsor);
        verify(sponsorRepository).save(sponsor);
        verify(webSocketNotificationService).notifySponsorUpdated(any());
    }

    @Test
    void updateSponsor_WhenNotExists_ShouldThrowException() {
        when(sponsorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sponsorService.updateSponsor(999L, sponsorDTO))
                .isInstanceOf(SponsorNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getSponsorsByUserId_ShouldReturnUserSponsors() {
        List<Sponsor> sponsors = Arrays.asList(sponsor);
        when(sponsorRepository.findByUserId(100L)).thenReturn(sponsors);
        when(sponsorMapper.toDTO(any(Sponsor.class))).thenReturn(sponsorDTO);

        List<SponsorDTO> result = sponsorService.getSponsorsByUserId(100L);

        assertThat(result).hasSize(1);
        verify(sponsorRepository).findByUserId(100L);
    }

    @Test
    void getPendingSponsors_ShouldReturnOnlyPending() {
        List<Sponsor> sponsors = Arrays.asList(sponsor);
        when(sponsorRepository.findByStatus(Sponsor.SponsorStatus.PENDING)).thenReturn(sponsors);
        when(sponsorMapper.toDTO(any(Sponsor.class))).thenReturn(sponsorDTO);

        List<SponsorDTO> result = sponsorService.getPendingSponsors();

        assertThat(result).hasSize(1);
        verify(sponsorRepository).findByStatus(Sponsor.SponsorStatus.PENDING);
    }

    @Test
    void getApprovedSponsors_ShouldReturnOnlyApproved() {
        sponsor.setStatus(Sponsor.SponsorStatus.APPROVED);
        List<Sponsor> sponsors = Arrays.asList(sponsor);
        when(sponsorRepository.findByStatus(Sponsor.SponsorStatus.APPROVED)).thenReturn(sponsors);
        when(sponsorMapper.toDTO(any(Sponsor.class))).thenReturn(sponsorDTO);

        List<SponsorDTO> result = sponsorService.getApprovedSponsors();

        assertThat(result).hasSize(1);
        verify(sponsorRepository).findByStatus(Sponsor.SponsorStatus.APPROVED);
    }

    @Test
    void rejectSponsor_ShouldChangeStatusToRejected() {
        when(sponsorRepository.findById(1L)).thenReturn(Optional.of(sponsor));
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);
        when(sponsorMapper.toDTO(any(Sponsor.class))).thenReturn(sponsorDTO);

        SponsorDTO result = sponsorService.rejectSponsor(1L);

        assertThat(result).isNotNull();
        verify(sponsorRepository).findById(1L);
        verify(sponsorRepository).save(sponsor);
        assertThat(sponsor.getStatus()).isEqualTo(Sponsor.SponsorStatus.REJECTED);
    }

    @Test
    void rejectSponsor_WhenNotExists_ShouldThrowException() {
        when(sponsorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sponsorService.rejectSponsor(999L))
                .isInstanceOf(SponsorNotFoundException.class);
    }

    @Test
    void approveSponsor_WithClubId_ShouldCreateExpenseAndNotifyMembers() {
        sponsor.setClubId(1);
        sponsor.setClubName("Tech Club");

        MemberDTO member1 = MemberDTO.builder()
                .id(1)
                .userId(101L)
                .userName("Member 1")
                .rank("PRESIDENT")
                .build();

        MemberDTO member2 = MemberDTO.builder()
                .id(2)
                .userId(102L)
                .userName("Member 2")
                .rank("MEMBER")
                .build();

        List<MemberDTO> members = Arrays.asList(member1, member2);

        when(sponsorRepository.findById(1L)).thenReturn(Optional.of(sponsor));
        when(clubServiceFeignClient.getClubMembers(1)).thenReturn(members);
        when(clubServiceFeignClient.createExpense(any(ExpenseDTO.class))).thenReturn(new ExpenseDTO());
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);
        when(sponsorMapper.toDTO(any(Sponsor.class))).thenReturn(sponsorDTO);

        SponsorDTO result = sponsorService.approveSponsor(1L);

        assertThat(result).isNotNull();
        assertThat(sponsor.getStatus()).isEqualTo(Sponsor.SponsorStatus.APPROVED);
        verify(clubServiceFeignClient).getClubMembers(1);
        verify(clubServiceFeignClient).createExpense(any(ExpenseDTO.class));
        verify(emailService).sendClubSponsorApprovedEmail(anyString(), anyString(), anyString(), anyDouble(), anyDouble());
    }

    @Test
    void approveSponsor_WithoutClubId_ShouldNotCreateExpense() {
        sponsor.setClubId(null);
        sponsor.setClubName(null);
        
        SponsorDTO dtoWithoutClub = SponsorDTO.builder()
                .id(1L)
                .name("Tech Corp")
                .description("Technology company")
                .contactEmail("contact@techcorp.com")
                .contributionAmount(1500.0)
                .level(Sponsor.SponsorLevel.GOLD)
                .status(Sponsor.SponsorStatus.APPROVED)
                .clubId(null)
                .clubName(null)
                .userId(100L)
                .applicantFirstName("John")
                .applicantLastName("Doe")
                .build();

        when(sponsorRepository.findById(1L)).thenReturn(Optional.of(sponsor));
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);
        when(sponsorMapper.toDTO(any(Sponsor.class))).thenReturn(dtoWithoutClub);

        SponsorDTO result = sponsorService.approveSponsor(1L);

        assertThat(result).isNotNull();
        verify(clubServiceFeignClient, never()).createExpense(any());
        verify(emailService).sendSponsorApprovedEmail(anyString(), anyString(), anyString());
    }

    @Test
    void approveSponsor_WhenFeignClientFails_ShouldStillApprove() {
        sponsor.setClubId(1);

        when(sponsorRepository.findById(1L)).thenReturn(Optional.of(sponsor));
        when(clubServiceFeignClient.getClubMembers(1)).thenThrow(new RuntimeException("Feign error"));
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);
        when(sponsorMapper.toDTO(any(Sponsor.class))).thenReturn(sponsorDTO);

        SponsorDTO result = sponsorService.approveSponsor(1L);

        assertThat(result).isNotNull();
        assertThat(sponsor.getStatus()).isEqualTo(Sponsor.SponsorStatus.APPROVED);
    }

    @Test
    void createSponsor_ShouldSendEmailNotification() {
        when(sponsorMapper.toEntity(any(SponsorDTO.class))).thenReturn(sponsor);
        when(sponsorRepository.save(any(Sponsor.class))).thenReturn(sponsor);
        when(sponsorMapper.toDTO(any(Sponsor.class))).thenReturn(sponsorDTO);

        SponsorDTO result = sponsorService.createSponsor(sponsorDTO);

        assertThat(result).isNotNull();
        verify(emailService).sendClubSponsorRequestReceivedEmail(
                eq("contact@techcorp.com"),
                eq("John"),
                eq("Tech Club"),
                eq(1500.0)
        );
        verify(webSocketNotificationService).notifySponsorCreated(any());
    }

    @Test
    void deleteSponsor_ShouldSendWebSocketNotification() {
        when(sponsorRepository.findById(1L)).thenReturn(Optional.of(sponsor));
        doNothing().when(sponsorRepository).deleteById(1L);

        sponsorService.deleteSponsor(1L);

        verify(sponsorRepository).deleteById(1L);
        verify(webSocketNotificationService).notifySponsorDeleted(1L, "Tech Corp");
    }

    @Test
    void getAllSponsors_WhenEmpty_ShouldReturnEmptyList() {
        when(sponsorRepository.findAll()).thenReturn(Arrays.asList());

        List<SponsorDTO> result = sponsorService.getAllSponsors();

        assertThat(result).isEmpty();
    }

    @Test
    void getSponsorsByLevel_WithMultipleSponsors_ShouldReturnAll() {
        Sponsor sponsor2 = Sponsor.builder()
                .id(2L)
                .name("Another Gold")
                .contributionAmount(2000.0)
                .level(Sponsor.SponsorLevel.GOLD)
                .build();

        when(sponsorRepository.findByLevel(Sponsor.SponsorLevel.GOLD))
                .thenReturn(Arrays.asList(sponsor, sponsor2));
        when(sponsorMapper.toDTO(any(Sponsor.class))).thenReturn(sponsorDTO);

        List<SponsorDTO> result = sponsorService.getSponsorsByLevel(Sponsor.SponsorLevel.GOLD);

        assertThat(result).hasSize(2);
    }
}
