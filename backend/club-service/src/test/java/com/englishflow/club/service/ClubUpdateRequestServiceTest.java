package com.englishflow.club.service;

import com.englishflow.club.dto.ClubDTO;
import com.englishflow.club.dto.ClubUpdateRequestDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.ClubUpdateRequest;
import com.englishflow.club.entity.Member;
import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.enums.ClubStatus;
import com.englishflow.club.enums.RankType;
import com.englishflow.club.enums.UpdateRequestStatus;
import com.englishflow.club.repository.ClubRepository;
import com.englishflow.club.repository.ClubUpdateRequestRepository;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClubUpdateRequestServiceTest {
    
    @Mock
    private ClubUpdateRequestRepository updateRequestRepository;
    
    @Mock
    private ClubRepository clubRepository;
    
    @Mock
    private MemberRepository memberRepository;
    
    @Mock
    private MemberService memberService;
    
    @Mock
    private WebSocketNotificationService wsNotificationService;
    
    @InjectMocks
    private ClubUpdateRequestService clubUpdateRequestService;
    
    private Club testClub;
    private ClubUpdateRequest testUpdateRequest;
    private ClubDTO testClubDTO;
    private Member presidentMember;
    private Member vicePresidentMember;
    private Member secretaryMember;
    
    @BeforeEach
    void setUp() {
        testClub = Club.builder()
                .id(1)
                .name("Test Club")
                .description("Test Description")
                .objective("Test Objective")
                .category(ClubCategory.CONVERSATION)
                .maxMembers(50)
                .status(ClubStatus.APPROVED)
                .createdBy(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testUpdateRequest = ClubUpdateRequest.builder()
                .id(1)
                .club(testClub)
                .requestedBy(100L)
                .name("Updated Club Name")
                .description("Updated Description")
                .objective("Updated Objective")
                .category(ClubCategory.BOOK)
                .maxMembers(60)
                .image("updated-image.jpg")
                .status(UpdateRequestStatus.PENDING)
                .vicePresidentApproved(false)
                .secretaryApproved(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        testClubDTO = ClubDTO.builder()
                .name("Updated Club Name")
                .description("Updated Description")
                .objective("Updated Objective")
                .category(ClubCategory.BOOK)
                .maxMembers(60)
                .image("updated-image.jpg")
                .build();
        
        presidentMember = Member.builder()
                .id(1)
                .userId(100L)
                .club(testClub)
                .rank(RankType.PRESIDENT)
                .build();
        
        vicePresidentMember = Member.builder()
                .id(2)
                .userId(200L)
                .club(testClub)
                .rank(RankType.VICE_PRESIDENT)
                .build();
        
        secretaryMember = Member.builder()
                .id(3)
                .userId(300L)
                .club(testClub)
                .rank(RankType.SECRETARY)
                .build();
    }
    
    @Test
    void testCreateUpdateRequest_Success() {
        // Given
        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        when(memberService.isPresident(1, 100L)).thenReturn(true);
        when(updateRequestRepository.findFirstByClubIdAndStatusOrderByCreatedAtDesc(1, UpdateRequestStatus.PENDING))
                .thenReturn(Optional.empty());
        when(updateRequestRepository.save(any(ClubUpdateRequest.class))).thenReturn(testUpdateRequest);
        
        // When
        ClubUpdateRequestDTO result = clubUpdateRequestService.createUpdateRequest(1, testClubDTO, 100L);
        
        // Then
        assertNotNull(result);
        assertEquals("Updated Club Name", result.getName());
        verify(updateRequestRepository).save(any(ClubUpdateRequest.class));
    }
    
    @Test
    void testCreateUpdateRequest_ClubNotFound() {
        // Given
        when(clubRepository.findById(999)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> 
                clubUpdateRequestService.createUpdateRequest(999, testClubDTO, 100L));
    }
    
    @Test
    void testCreateUpdateRequest_NotPresident() {
        // Given
        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        when(memberService.isPresident(1, 100L)).thenReturn(false);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> 
                clubUpdateRequestService.createUpdateRequest(1, testClubDTO, 100L));
    }
    
    @Test
    void testCreateUpdateRequest_PendingRequestExists() {
        // Given
        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        when(memberService.isPresident(1, 100L)).thenReturn(true);
        when(updateRequestRepository.findFirstByClubIdAndStatusOrderByCreatedAtDesc(1, UpdateRequestStatus.PENDING))
                .thenReturn(Optional.of(testUpdateRequest));
        
        // When & Then
        assertThrows(RuntimeException.class, () -> 
                clubUpdateRequestService.createUpdateRequest(1, testClubDTO, 100L));
    }
    
    @Test
    void testApproveUpdateRequest_ByVicePresident() {
        // Given
        when(updateRequestRepository.findById(1)).thenReturn(Optional.of(testUpdateRequest));
        when(memberRepository.findByClubIdAndUserId(1, 200L)).thenReturn(Optional.of(vicePresidentMember));
        when(updateRequestRepository.save(any(ClubUpdateRequest.class))).thenReturn(testUpdateRequest);
        
        // When
        ClubUpdateRequestDTO result = clubUpdateRequestService.approveUpdateRequest(1, 200L);
        
        // Then
        assertNotNull(result);
        verify(updateRequestRepository).save(any(ClubUpdateRequest.class));
    }
    
    @Test
    void testApproveUpdateRequest_BySecretary() {
        // Given
        when(updateRequestRepository.findById(1)).thenReturn(Optional.of(testUpdateRequest));
        when(memberRepository.findByClubIdAndUserId(1, 300L)).thenReturn(Optional.of(secretaryMember));
        when(updateRequestRepository.save(any(ClubUpdateRequest.class))).thenReturn(testUpdateRequest);
        
        // When
        ClubUpdateRequestDTO result = clubUpdateRequestService.approveUpdateRequest(1, 300L);
        
        // Then
        assertNotNull(result);
        verify(updateRequestRepository).save(any(ClubUpdateRequest.class));
    }
    
    @Test
    void testApproveUpdateRequest_BothApproved() {
        // Given
        testUpdateRequest.setVicePresidentApproved(true);
        testUpdateRequest.setSecretaryApproved(false);
        
        when(updateRequestRepository.findById(1)).thenReturn(Optional.of(testUpdateRequest));
        when(memberRepository.findByClubIdAndUserId(1, 300L)).thenReturn(Optional.of(secretaryMember));
        when(updateRequestRepository.save(any(ClubUpdateRequest.class))).thenReturn(testUpdateRequest);
        when(clubRepository.save(any(Club.class))).thenReturn(testClub);
        
        // When
        ClubUpdateRequestDTO result = clubUpdateRequestService.approveUpdateRequest(1, 300L);
        
        // Then
        assertNotNull(result);
        verify(clubRepository).save(any(Club.class));
        verify(wsNotificationService).notifyClubUpdated(anyLong(), anyString());
    }
    
    @Test
    void testApproveUpdateRequest_NotMember() {
        // Given
        when(updateRequestRepository.findById(1)).thenReturn(Optional.of(testUpdateRequest));
        when(memberRepository.findByClubIdAndUserId(1, 999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> 
                clubUpdateRequestService.approveUpdateRequest(1, 999L));
    }
    
    @Test
    void testApproveUpdateRequest_NotAuthorized() {
        // Given
        Member regularMember = Member.builder()
                .userId(400L)
                .club(testClub)
                .rank(RankType.MEMBER)
                .build();
        
        when(updateRequestRepository.findById(1)).thenReturn(Optional.of(testUpdateRequest));
        when(memberRepository.findByClubIdAndUserId(1, 400L)).thenReturn(Optional.of(regularMember));
        
        // When & Then
        assertThrows(RuntimeException.class, () -> 
                clubUpdateRequestService.approveUpdateRequest(1, 400L));
    }
    
    @Test
    void testRejectUpdateRequest_Success() {
        // Given
        when(updateRequestRepository.findById(1)).thenReturn(Optional.of(testUpdateRequest));
        when(memberRepository.findByClubIdAndUserId(1, 200L)).thenReturn(Optional.of(vicePresidentMember));
        when(updateRequestRepository.save(any(ClubUpdateRequest.class))).thenReturn(testUpdateRequest);
        
        // When
        ClubUpdateRequestDTO result = clubUpdateRequestService.rejectUpdateRequest(1, 200L);
        
        // Then
        assertNotNull(result);
        verify(updateRequestRepository).save(any(ClubUpdateRequest.class));
    }
    
    @Test
    void testRejectUpdateRequest_NotAuthorized() {
        // Given
        Member regularMember = Member.builder()
                .userId(400L)
                .club(testClub)
                .rank(RankType.MEMBER)
                .build();
        
        when(updateRequestRepository.findById(1)).thenReturn(Optional.of(testUpdateRequest));
        when(memberRepository.findByClubIdAndUserId(1, 400L)).thenReturn(Optional.of(regularMember));
        
        // When & Then
        assertThrows(RuntimeException.class, () -> 
                clubUpdateRequestService.rejectUpdateRequest(1, 400L));
    }
    
    @Test
    void testGetPendingRequestsForClub() {
        // Given
        List<ClubUpdateRequest> requests = Arrays.asList(testUpdateRequest);
        when(updateRequestRepository.findByClubIdAndStatus(1, UpdateRequestStatus.PENDING)).thenReturn(requests);
        
        // When
        List<ClubUpdateRequestDTO> result = clubUpdateRequestService.getPendingRequestsForClub(1);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    
    @Test
    void testGetAllRequestsForClub() {
        // Given
        List<ClubUpdateRequest> requests = Arrays.asList(testUpdateRequest);
        when(updateRequestRepository.findByClubId(1)).thenReturn(requests);
        
        // When
        List<ClubUpdateRequestDTO> result = clubUpdateRequestService.getAllRequestsForClub(1);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    
    @Test
    void testGetRequestById() {
        // Given
        when(updateRequestRepository.findById(1)).thenReturn(Optional.of(testUpdateRequest));
        
        // When
        ClubUpdateRequestDTO result = clubUpdateRequestService.getRequestById(1);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Updated Club Name", result.getName());
    }
    
    @Test
    void testGetRequestById_NotFound() {
        // Given
        when(updateRequestRepository.findById(999)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> 
                clubUpdateRequestService.getRequestById(999));
    }
}
