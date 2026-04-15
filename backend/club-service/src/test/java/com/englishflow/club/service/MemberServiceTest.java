package com.englishflow.club.service;

import com.englishflow.club.dto.ClubWithRoleDTO;
import com.englishflow.club.dto.MemberDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.Member;
import com.englishflow.club.enums.RankType;
import com.englishflow.club.exception.MemberNotFoundException;
import com.englishflow.club.mapper.MemberMapper;
import com.englishflow.club.repository.ClubRepository;
import com.englishflow.club.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberService memberService;

    private Club club;
    private Member member;
    private MemberDTO memberDTO;
    private Long clubId = 1L;
    private Long userId = 100L;

    @BeforeEach
    void setUp() {
        club = new Club();
        club.setId(clubId);
        club.setName("English Club");

        member = new Member();
        member.setId(1L);
        member.setClub(club);
        member.setUserId(userId);
        member.setUserName("John Doe");
        member.setRank(RankType.MEMBER);

        memberDTO = new MemberDTO();
        memberDTO.setId(1L);
        memberDTO.setUserId(userId);
        memberDTO.setUserName("John Doe");
        memberDTO.setRank("MEMBER");
    }

    @Test
    void getClubMembers_ShouldReturnMembers() {
        // Given
        when(memberRepository.findByClubId(clubId)).thenReturn(Arrays.asList(member));
        when(memberMapper.toDTO(member)).thenReturn(memberDTO);

        // When
        List<MemberDTO> result = memberService.getClubMembers(clubId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        verify(memberRepository).findByClubId(clubId);
    }

    @Test
    void getUserClubs_ShouldReturnUserClubs() {
        // Given
        when(memberRepository.findByUserId(userId)).thenReturn(Arrays.asList(member));

        // When
        List<ClubWithRoleDTO> result = memberService.getUserClubs(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(clubId, result.get(0).getClubId());
        assertEquals("English Club", result.get(0).getClubName());
    }

    @Test
    void getMemberByClubAndUser_WhenExists_ShouldReturnMember() {
        // Given
        when(memberRepository.findByClubIdAndUserId(clubId, userId))
            .thenReturn(Optional.of(member));
        when(memberMapper.toDTO(member)).thenReturn(memberDTO);

        // When
        MemberDTO result = memberService.getMemberByClubAndUser(clubId, userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }

    @Test
    void getMemberByClubAndUser_WhenNotExists_ShouldThrowException() {
        // Given
        when(memberRepository.findByClubIdAndUserId(clubId, userId))
            .thenReturn(Optional.empty());

        // When & Then
        assertThrows(MemberNotFoundException.class, 
            () -> memberService.getMemberByClubAndUser(clubId, userId));
    }

    @Test
    void isUserMemberOfClub_WhenMember_ShouldReturnTrue() {
        // Given
        when(memberRepository.existsByClubIdAndUserId(clubId, userId)).thenReturn(true);

        // When
        boolean result = memberService.isUserMemberOfClub(clubId, userId);

        // Then
        assertTrue(result);
    }

    @Test
    void isUserMemberOfClub_WhenNotMember_ShouldReturnFalse() {
        // Given
        when(memberRepository.existsByClubIdAndUserId(clubId, userId)).thenReturn(false);

        // When
        boolean result = memberService.isUserMemberOfClub(clubId, userId);

        // Then
        assertFalse(result);
    }

    @Test
    void removeMember_WhenExists_ShouldRemove() {
        // Given
        when(memberRepository.findByClubIdAndUserId(clubId, userId))
            .thenReturn(Optional.of(member));

        // When
        memberService.removeMember(clubId, userId);

        // Then
        verify(memberRepository).delete(member);
    }

    @Test
    void getClubMemberCount_ShouldReturnCount() {
        // Given
        when(memberRepository.countByClubId(clubId)).thenReturn(25L);

        // When
        Long result = memberService.getClubMemberCount(clubId);

        // Then
        assertEquals(25L, result);
    }
}
