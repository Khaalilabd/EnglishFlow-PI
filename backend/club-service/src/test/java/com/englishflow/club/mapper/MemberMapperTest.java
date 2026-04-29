package com.englishflow.club.mapper;

import com.englishflow.club.dto.MemberDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.Member;
import com.englishflow.club.enums.RankType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MemberMapperTest {
    
    private MemberMapper memberMapper;
    
    @BeforeEach
    void setUp() {
        memberMapper = Mappers.getMapper(MemberMapper.class);
    }
    
    @Test
    void testToDTO_WithValidMember() {
        // Given
        Club club = Club.builder()
                .id(1)
                .name("Test Club")
                .build();
        
        Member member = Member.builder()
                .id(1)
                .rank(RankType.PRESIDENT)
                .club(club)
                .userId(100L)
                .joinedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // When
        MemberDTO dto = memberMapper.toDTO(member);
        
        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals(RankType.PRESIDENT, dto.getRank());
        assertEquals(1, dto.getClubId());
        assertEquals(100L, dto.getUserId());
        assertNotNull(dto.getJoinedAt());
    }
    
    @Test
    void testToDTO_WithDifferentRanks() {
        // Given
        Club club = Club.builder().id(2).build();
        
        Member vicePresident = Member.builder()
                .id(2)
                .rank(RankType.VICE_PRESIDENT)
                .club(club)
                .userId(200L)
                .joinedAt(LocalDateTime.now())
                .build();
        
        // When
        MemberDTO dto = memberMapper.toDTO(vicePresident);
        
        // Then
        assertNotNull(dto);
        assertEquals(RankType.VICE_PRESIDENT, dto.getRank());
        assertEquals(2, dto.getClubId());
    }
    
    @Test
    void testToDTO_WithSecretary() {
        // Given
        Club club = Club.builder().id(3).build();
        
        Member secretary = Member.builder()
                .id(3)
                .rank(RankType.SECRETARY)
                .club(club)
                .userId(300L)
                .joinedAt(LocalDateTime.now())
                .build();
        
        // When
        MemberDTO dto = memberMapper.toDTO(secretary);
        
        // Then
        assertNotNull(dto);
        assertEquals(RankType.SECRETARY, dto.getRank());
    }
    
    @Test
    void testToDTO_WithRegularMember() {
        // Given
        Club club = Club.builder().id(4).build();
        
        Member regularMember = Member.builder()
                .id(4)
                .rank(RankType.MEMBER)
                .club(club)
                .userId(400L)
                .joinedAt(LocalDateTime.now())
                .build();
        
        // When
        MemberDTO dto = memberMapper.toDTO(regularMember);
        
        // Then
        assertNotNull(dto);
        assertEquals(RankType.MEMBER, dto.getRank());
    }
}
