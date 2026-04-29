package com.englishflow.club.mapper;

import com.englishflow.club.dto.SkillDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SkillMapperTest {
    
    private SkillMapper skillMapper;
    
    @BeforeEach
    void setUp() {
        skillMapper = new SkillMapper();
    }
    
    @Test
    void testToDTO_WithValidSkill() {
        // Given
        Club club = Club.builder()
                .id(1)
                .name("Test Club")
                .build();
        
        Skill skill = Skill.builder()
                .id(1)
                .name("Java Programming")
                .description("Advanced Java skills")
                .club(club)
                .createdAt(LocalDateTime.now())
                .build();
        
        // When
        SkillDTO dto = skillMapper.toDTO(skill);
        
        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Java Programming", dto.getName());
        assertEquals("Advanced Java skills", dto.getDescription());
        assertEquals(1, dto.getClubId());
        assertNotNull(dto.getCreatedAt());
    }
    
    @Test
    void testToDTO_WithNullClub() {
        // Given
        Skill skill = Skill.builder()
                .id(1)
                .name("Python Programming")
                .description("Python skills")
                .club(null)
                .createdAt(LocalDateTime.now())
                .build();
        
        // When
        SkillDTO dto = skillMapper.toDTO(skill);
        
        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Python Programming", dto.getName());
        assertNull(dto.getClubId());
    }
    
    @Test
    void testToDTO_WithNullSkill() {
        // When
        SkillDTO dto = skillMapper.toDTO(null);
        
        // Then
        assertNull(dto);
    }
    
    @Test
    void testToEntity_WithValidDTO() {
        // Given
        SkillDTO dto = SkillDTO.builder()
                .id(1)
                .name("JavaScript")
                .description("Frontend development")
                .clubId(1)
                .createdAt(LocalDateTime.now())
                .build();
        
        // When
        Skill skill = skillMapper.toEntity(dto);
        
        // Then
        assertNotNull(skill);
        assertEquals(1, skill.getId());
        assertEquals("JavaScript", skill.getName());
        assertEquals("Frontend development", skill.getDescription());
        assertNull(skill.getClub()); // Club is not mapped in toEntity
    }
    
    @Test
    void testToEntity_WithNullDTO() {
        // When
        Skill skill = skillMapper.toEntity(null);
        
        // Then
        assertNull(skill);
    }
    
    @Test
    void testToEntity_WithMinimalDTO() {
        // Given
        SkillDTO dto = SkillDTO.builder()
                .name("React")
                .build();
        
        // When
        Skill skill = skillMapper.toEntity(dto);
        
        // Then
        assertNotNull(skill);
        assertNull(skill.getId());
        assertEquals("React", skill.getName());
        assertNull(skill.getDescription());
    }
}
