package com.englishflow.club.service;

import com.englishflow.club.client.AuthServiceClient;
import com.englishflow.club.dto.MemberDTO;
import com.englishflow.club.dto.ClubWithRoleDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.Member;
import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.enums.ClubStatus;
import com.englishflow.club.enums.RankType;
import com.englishflow.club.exception.*;
import com.englishflow.club.mapper.ClubMapper;
import com.englishflow.club.mapper.MemberMapper;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private ClubMapper clubMapper;

    @Mock
    private ClubHistoryService clubHistoryService;

    @Mock
    private WebSocketNotificationService wsNotificationService;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private MemberService memberService;

    private Club testClub;
    private Member testMember;
    private MemberDTO testMemberDTO;

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

        testMember = Member.builder()
                .id(1)
                .club(testClub)
                .userId(100L)
                .rank(RankType.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();

        testMemberDTO = MemberDTO.builder()
                .id(1)
                .clubId(1)
                .userId(100L)
                .rank(RankType.MEMBER)
                .build();
    }

    @Test
    void getMembersByClub_ShouldReturnMembers() {
        // Arrange
        List<Member> members = Arrays.asList(testMember);
        when(memberRepository.findByClubId(1)).thenReturn(members);
        when(memberMapper.toDTO(any(Member.class))).thenReturn(testMemberDTO);

        // Act
        List<MemberDTO> result = memberService.getMembersByClub(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(memberRepository, times(1)).findByClubId(1);
    }

    @Test
    void getMembersByUser_ShouldReturnUserMemberships() {
        // Arrange
        List<Member> members = Arrays.asList(testMember);
        when(memberRepository.findByUserId(100L)).thenReturn(members);
        when(memberMapper.toDTO(any(Member.class))).thenReturn(testMemberDTO);

        // Act
        List<MemberDTO> result = memberService.getMembersByUser(100L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(memberRepository, times(1)).findByUserId(100L);
    }

    @Test
    void addMemberToClub_WhenValid_ShouldAddMember() {
        // Arrange
        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        when(memberRepository.existsByClubIdAndUserId(1, 100L)).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);
        when(memberMapper.toDTO(testMember)).thenReturn(testMemberDTO);
        doNothing().when(wsNotificationService).notifyMemberJoined(anyLong(), anyString(), anyLong(), anyString(), anyString());

        // Act
        MemberDTO result = memberService.addMemberToClub(1, 100L);

        // Assert
        assertNotNull(result);
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(wsNotificationService, times(1)).notifyMemberJoined(anyLong(), anyString(), anyLong(), anyString(), anyString());
    }

    @Test
    void addMemberToClub_WhenClubNotFound_ShouldThrowException() {
        // Arrange
        when(clubRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ClubNotFoundException.class, () -> memberService.addMemberToClub(999, 100L));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void addMemberToClub_WhenClubFull_ShouldThrowException() {
        // Arrange
        testClub.setMaxMembers(1);
        // Ajouter un membre existant pour remplir le club
        Member existingMember = Member.builder()
                .id(2)
                .club(testClub)
                .userId(200L)
                .rank(RankType.PRESIDENT)
                .build();
        testClub.setMembers(Arrays.asList(existingMember));
        
        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));

        // Act & Assert
        assertThrows(ClubFullException.class, () -> memberService.addMemberToClub(1, 100L));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void addMemberToClub_WhenAlreadyMember_ShouldThrowException() {
        // Arrange
        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        when(memberRepository.existsByClubIdAndUserId(1, 100L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateMemberException.class, () -> memberService.addMemberToClub(1, 100L));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void addPresidentToClub_ShouldAddPresidentSuccessfully() {
        // Arrange
        Member president = Member.builder()
                .id(2)
                .club(testClub)
                .userId(200L)
                .rank(RankType.PRESIDENT)
                .build();

        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        when(memberRepository.existsByClubIdAndUserId(1, 200L)).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(president);
        when(memberMapper.toDTO(president)).thenReturn(testMemberDTO);

        // Act
        MemberDTO result = memberService.addPresidentToClub(1, 200L);

        // Assert
        assertNotNull(result);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void updateMemberRank_WhenValid_ShouldUpdateRank() {
        // Arrange
        Member president = Member.builder()
                .id(2)
                .club(testClub)
                .userId(200L)
                .rank(RankType.PRESIDENT)
                .build();

        when(memberRepository.findById(1)).thenReturn(Optional.of(testMember));
        when(memberRepository.findByClubIdAndUserId(1, 200L)).thenReturn(Optional.of(president));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);
        when(memberMapper.toDTO(testMember)).thenReturn(testMemberDTO);
        doNothing().when(clubHistoryService).logHistory(anyLong(), anyLong(), any(), anyString(), anyString(), anyString(), anyString(), anyLong());

        // Act
        MemberDTO result = memberService.updateMemberRank(1, RankType.SECRETARY, 200L);

        // Assert
        assertNotNull(result);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void updateMemberRank_WhenNotPresident_ShouldThrowException() {
        // Arrange
        when(memberRepository.findById(1)).thenReturn(Optional.of(testMember));
        when(memberRepository.findByClubIdAndUserId(1, 300L)).thenReturn(Optional.of(testMember));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> memberService.updateMemberRank(1, RankType.SECRETARY, 300L));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void isPresident_WhenUserIsPresident_ShouldReturnTrue() {
        // Arrange
        Member president = Member.builder()
                .id(2)
                .club(testClub)
                .userId(200L)
                .rank(RankType.PRESIDENT)
                .build();

        when(memberRepository.findByClubIdAndUserId(1, 200L)).thenReturn(Optional.of(president));

        // Act
        boolean result = memberService.isPresident(1, 200L);

        // Assert
        assertTrue(result);
    }

    @Test
    void isPresident_WhenUserIsNotPresident_ShouldReturnFalse() {
        // Arrange
        when(memberRepository.findByClubIdAndUserId(1, 100L)).thenReturn(Optional.of(testMember));

        // Act
        boolean result = memberService.isPresident(1, 100L);

        // Assert
        assertFalse(result);
    }

    @Test
    void isMember_WhenUserIsMember_ShouldReturnTrue() {
        // Arrange
        when(memberRepository.existsByClubIdAndUserId(1, 100L)).thenReturn(true);

        // Act
        boolean result = memberService.isMember(1, 100L);

        // Assert
        assertTrue(result);
    }

    @Test
    void removeMemberFromClub_WhenValid_ShouldRemoveMember() {
        // Arrange
        Member president = Member.builder()
                .id(2)
                .club(testClub)
                .userId(200L)
                .rank(RankType.PRESIDENT)
                .build();

        when(memberRepository.findById(1)).thenReturn(Optional.of(testMember));
        when(memberRepository.findByClubIdAndUserId(1, 200L)).thenReturn(Optional.of(president));
        doNothing().when(memberRepository).deleteById(1);

        // Act
        memberService.removeMemberFromClub(1, 200L);

        // Assert
        verify(memberRepository, times(1)).deleteById(1);
    }

    @Test
    void removeMemberFromClub_WhenNotPresident_ShouldThrowException() {
        // Arrange
        when(memberRepository.findById(1)).thenReturn(Optional.of(testMember));
        when(memberRepository.findByClubIdAndUserId(1, 300L)).thenReturn(Optional.of(testMember));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> memberService.removeMemberFromClub(1, 300L));
        verify(memberRepository, never()).deleteById(1);
    }

    @Test
    void getClubMemberCount_ShouldReturnCount() {
        // Arrange
        when(memberRepository.countByClubId(1)).thenReturn(5L);

        // Act
        long result = memberService.getClubMemberCount(1);

        // Assert
        assertEquals(5L, result);
        verify(memberRepository, times(1)).countByClubId(1);
    }

    @Test
    void hasManagementRole_WhenUserIsSecretary_ShouldReturnTrue() {
        // Arrange
        Member secretary = Member.builder()
                .id(3)
                .club(testClub)
                .userId(300L)
                .rank(RankType.SECRETARY)
                .build();

        when(memberRepository.findByClubIdAndUserId(1, 300L)).thenReturn(Optional.of(secretary));

        // Act
        boolean result = memberService.hasManagementRole(1, 300L);

        // Assert
        assertTrue(result);
    }

    @Test
    void getMemberByClubAndUser_WhenExists_ShouldReturnMember() {
        // Arrange
        when(memberRepository.findByClubIdAndUserId(1, 100L)).thenReturn(Optional.of(testMember));
        when(memberMapper.toDTO(testMember)).thenReturn(testMemberDTO);

        // Act
        Optional<MemberDTO> result = memberService.getMemberByClubAndUser(1, 100L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testMemberDTO, result.get());
    }
}
