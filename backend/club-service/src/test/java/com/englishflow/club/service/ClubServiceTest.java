package com.englishflow.club.service;

import com.englishflow.club.dto.ClubDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.enums.ClubStatus;
import com.englishflow.club.exception.ClubNotFoundException;
import com.englishflow.club.mapper.ClubMapper;
import com.englishflow.club.repository.ClubRepository;
import com.englishflow.club.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClubServiceTest {

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private ClubUpdateRequestService updateRequestService;

    @Mock
    private ClubMapper clubMapper;

    @InjectMocks
    private ClubService clubService;

    private Club testClub;
    private ClubDTO testClubDTO;

    @BeforeEach
    void setUp() {
        testClub = Club.builder()
                .id(1)
                .name("English Conversation Club")
                .description("Practice English conversation")
                .objective("Improve speaking skills")
                .category(ClubCategory.CONVERSATION)
                .maxMembers(20)
                .status(ClubStatus.APPROVED)
                .createdBy(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testClubDTO = ClubDTO.builder()
                .id(1)
                .name("English Conversation Club")
                .description("Practice English conversation")
                .objective("Improve speaking skills")
                .category(ClubCategory.CONVERSATION)
                .maxMembers(20)
                .status(ClubStatus.APPROVED)
                .createdBy(1)
                .build();
    }

    @Test
    void getAllClubs_ShouldReturnAllClubs() {
        // Arrange
        List<Club> clubs = Arrays.asList(testClub);
        when(clubRepository.findAll()).thenReturn(clubs);
        when(clubMapper.toDTO(any(Club.class))).thenReturn(testClubDTO);

        // Act
        List<ClubDTO> result = clubService.getAllClubs();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(clubRepository, times(1)).findAll();
    }

    @Test
    void getClubById_WhenClubExists_ShouldReturnClub() {
        // Arrange
        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        when(clubMapper.toDTO(testClub)).thenReturn(testClubDTO);

        // Act
        ClubDTO result = clubService.getClubById(1);

        // Assert
        assertNotNull(result);
        assertEquals("English Conversation Club", result.getName());
        verify(clubRepository, times(1)).findById(1);
    }

    @Test
    void getClubById_WhenClubNotExists_ShouldThrowException() {
        // Arrange
        when(clubRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ClubNotFoundException.class, () -> clubService.getClubById(999));
        verify(clubRepository, times(1)).findById(999);
    }

    @Test
    void createClub_ShouldCreateAndReturnClub() {
        // Arrange
        when(clubMapper.toEntity(testClubDTO)).thenReturn(testClub);
        when(clubRepository.save(any(Club.class))).thenReturn(testClub);
        when(clubMapper.toDTO(testClub)).thenReturn(testClubDTO);

        // Act
        ClubDTO result = clubService.createClub(testClubDTO);

        // Assert
        assertNotNull(result);
        assertEquals("English Conversation Club", result.getName());
        verify(clubRepository, times(1)).save(any(Club.class));
    }

    @Test
    void getClubsByCategory_ShouldReturnFilteredClubs() {
        // Arrange
        List<Club> clubs = Arrays.asList(testClub);
        when(clubRepository.findByCategory(ClubCategory.CONVERSATION)).thenReturn(clubs);
        when(clubMapper.toDTO(any(Club.class))).thenReturn(testClubDTO);

        // Act
        List<ClubDTO> result = clubService.getClubsByCategory(ClubCategory.CONVERSATION);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(clubRepository, times(1)).findByCategory(ClubCategory.CONVERSATION);
    }

    @Test
    void deleteClub_WhenClubExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(clubRepository.existsById(1)).thenReturn(true);

        // Act
        clubService.deleteClub(1);

        // Assert
        verify(clubRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteClub_WhenClubNotExists_ShouldThrowException() {
        // Arrange
        when(clubRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(ClubNotFoundException.class, () -> clubService.deleteClub(999));
        verify(clubRepository, never()).deleteById(999);
    }
}
