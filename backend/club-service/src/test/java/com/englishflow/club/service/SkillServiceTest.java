package com.englishflow.club.service;

import com.englishflow.club.dto.SkillDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.Skill;
import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.enums.ClubStatus;
import com.englishflow.club.exception.ClubNotFoundException;
import com.englishflow.club.mapper.SkillMapper;
import com.englishflow.club.repository.ClubRepository;
import com.englishflow.club.repository.SkillRepository;
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
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillService skillService;

    private Club testClub;
    private Skill testSkill;
    private SkillDTO testSkillDTO;

    @BeforeEach
    void setUp() {
        testClub = Club.builder()
                .id(1)
                .name("Test Club")
                .description("Test Description")
                .category(ClubCategory.CONVERSATION)
                .maxMembers(20)
                .status(ClubStatus.APPROVED)
                .createdBy(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testSkill = Skill.builder()
                .id(1)
                .name("Public Speaking")
                .description("Ability to speak in public")
                .club(testClub)
                .build();

        testSkillDTO = SkillDTO.builder()
                .id(1)
                .name("Public Speaking")
                .description("Ability to speak in public")
                .clubId(1)
                .build();
    }

    @Test
    void getSkillsByClubId_ShouldReturnSkills() {
        // Arrange
        List<Skill> skills = Arrays.asList(testSkill);
        when(skillRepository.findByClubId(1)).thenReturn(skills);
        when(skillMapper.toDTO(any(Skill.class))).thenReturn(testSkillDTO);

        // Act
        List<SkillDTO> result = skillService.getSkillsByClubId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Public Speaking", result.get(0).getName());
        verify(skillRepository, times(1)).findByClubId(1);
    }

    @Test
    void addSkillToClub_WhenValid_ShouldAddSkill() {
        // Arrange
        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        when(skillMapper.toEntity(testSkillDTO)).thenReturn(testSkill);
        when(skillRepository.save(any(Skill.class))).thenReturn(testSkill);
        when(skillMapper.toDTO(testSkill)).thenReturn(testSkillDTO);

        // Act
        SkillDTO result = skillService.addSkillToClub(1, testSkillDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Public Speaking", result.getName());
        verify(skillRepository, times(1)).save(any(Skill.class));
    }

    @Test
    void addSkillToClub_WhenClubNotFound_ShouldThrowException() {
        // Arrange
        when(clubRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ClubNotFoundException.class, () -> skillService.addSkillToClub(999, testSkillDTO));
        verify(skillRepository, never()).save(any(Skill.class));
    }

    @Test
    void deleteSkill_ShouldDeleteSuccessfully() {
        // Arrange
        doNothing().when(skillRepository).deleteById(1);

        // Act
        skillService.deleteSkill(1);

        // Assert
        verify(skillRepository, times(1)).deleteById(1);
    }

    @Test
    void updateClubSkills_ShouldUpdateSkills() {
        // Arrange
        SkillDTO skill1 = SkillDTO.builder()
                .name("Leadership")
                .description("Leadership skills")
                .clubId(1)
                .build();

        SkillDTO skill2 = SkillDTO.builder()
                .name("Communication")
                .description("Communication skills")
                .clubId(1)
                .build();

        List<SkillDTO> skillDTOs = Arrays.asList(skill1, skill2);

        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        when(skillMapper.toEntity(any(SkillDTO.class))).thenReturn(testSkill);
        doNothing().when(skillRepository).deleteByClubId(1);
        when(skillRepository.saveAll(anyList())).thenReturn(Arrays.asList(testSkill));

        // Act
        skillService.updateClubSkills(1, skillDTOs);

        // Assert
        verify(skillRepository, times(1)).deleteByClubId(1);
        verify(skillRepository, times(1)).saveAll(anyList());
    }

    @Test
    void updateClubSkills_WhenClubNotFound_ShouldThrowException() {
        // Arrange
        when(clubRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ClubNotFoundException.class, () -> skillService.updateClubSkills(999, Arrays.asList(testSkillDTO)));
        verify(skillRepository, never()).deleteByClubId(anyInt());
    }

    @Test
    void updateClubSkills_WithEmptyList_ShouldDeleteOldSkills() {
        // Arrange
        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        doNothing().when(skillRepository).deleteByClubId(1);

        // Act
        skillService.updateClubSkills(1, Arrays.asList());

        // Assert
        verify(skillRepository, times(1)).deleteByClubId(1);
        verify(skillRepository, never()).saveAll(anyList());
    }
}
