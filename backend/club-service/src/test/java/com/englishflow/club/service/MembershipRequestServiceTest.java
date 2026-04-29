package com.englishflow.club.service;

import com.englishflow.club.client.AuthServiceClient;
import com.englishflow.club.dto.MembershipRequestDTO;
import com.englishflow.club.dto.UserInfoDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.MembershipRequest;
import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.enums.ClubStatus;
import com.englishflow.club.enums.MembershipRequestStatus;
import com.englishflow.club.exception.ClubFullException;
import com.englishflow.club.exception.ClubNotFoundException;
import com.englishflow.club.exception.DuplicateMemberException;
import com.englishflow.club.exception.UnauthorizedException;
import com.englishflow.club.repository.ClubRepository;
import com.englishflow.club.repository.MemberRepository;
import com.englishflow.club.repository.MembershipRequestRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipRequestServiceTest {
    
    @Mock
    private MembershipRequestRepository requestRepository;
    
    @Mock
    private ClubRepository clubRepository;
    
    @Mock
    private MemberRepository memberRepository;
    
    @Mock
    private MemberService memberService;
    
    @Mock
    private WebSocketNotificationService wsNotificationService;
    
    @Mock
    private AuthServiceClient authServiceClient;
    
    @Mock
    private ClubHistoryService clubHistoryService;
    
    @Mock
    private ExpenseService expenseService;
    
    @InjectMocks
    private MembershipRequestService membershipRequestService;
    
    private Club testClub;
    private MembershipRequest testRequest;
    private UserInfoDTO testUserInfo;
    
    @BeforeEach
    void setUp() {
        testClub = Club.builder()
                .id(1)
                .name("Test Club")
                .description("Test Description")
                .category(ClubCategory.CONVERSATION)
                .maxMembers(50)
                .registrationFee(10.0)
                .status(ClubStatus.APPROVED)
                .createdBy(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testRequest = MembershipRequest.builder()
                .id(1)
                .club(testClub)
                .userId(100L)
                .message("I want to join")
                .motivationLetter("I am passionate about technology")
                .studentSkills("Java, Python")
                .status(MembershipRequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
        
        testUserInfo = UserInfoDTO.builder()
                .id(100L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
    }
    
    @Test
    void testCreateRequest_Success() {
        // Given
        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        when(memberRepository.existsByClubIdAndUserId(1, 100L)).thenReturn(false);
        when(requestRepository.findByClubIdAndUserId(1, 100L)).thenReturn(Optional.empty());
        when(requestRepository.save(any(MembershipRequest.class))).thenReturn(testRequest);
        when(authServiceClient.getUserInfo(100L)).thenReturn(testUserInfo);
        
        // When
        MembershipRequestDTO result = membershipRequestService.createRequest(
                1, 100L, "I want to join", "Motivation", "Skills");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(100L, result.getUserId());
        verify(requestRepository).save(any(MembershipRequest.class));
        verify(wsNotificationService).notifyNewMembershipRequest(anyLong(), anyString(), anyLong(), anyString());
    }
    
    @Test
    void testCreateRequest_ClubNotFound() {
        // Given
        when(clubRepository.findById(999)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ClubNotFoundException.class, () -> 
                membershipRequestService.createRequest(999, 100L, "Message", "Motivation", "Skills"));
    }
    
    @Test
    void testCreateRequest_UserAlreadyMember() {
        // Given
        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        when(memberRepository.existsByClubIdAndUserId(1, 100L)).thenReturn(true);
        
        // When & Then
        assertThrows(DuplicateMemberException.class, () -> 
                membershipRequestService.createRequest(1, 100L, "Message", "Motivation", "Skills"));
    }
    
    @Test
    void testCreateRequest_PendingRequestExists() {
        // Given
        MembershipRequest pendingRequest = MembershipRequest.builder()
                .status(MembershipRequestStatus.PENDING)
                .build();
        
        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        when(memberRepository.existsByClubIdAndUserId(1, 100L)).thenReturn(false);
        when(requestRepository.findByClubIdAndUserId(1, 100L)).thenReturn(Optional.of(pendingRequest));
        
        // When & Then
        assertThrows(DuplicateMemberException.class, () -> 
                membershipRequestService.createRequest(1, 100L, "Message", "Motivation", "Skills"));
    }
    
    @Test
    void testGetAllRequestsForClub() {
        // Given
        List<MembershipRequest> requests = Arrays.asList(testRequest);
        when(requestRepository.findByClubId(1)).thenReturn(requests);
        when(authServiceClient.getUserInfo(100L)).thenReturn(testUserInfo);
        
        // When
        List<MembershipRequestDTO> result = membershipRequestService.getAllRequestsForClub(1);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getUserId());
    }
    
    @Test
    void testApproveRequest_Success_NoFee() {
        // Given
        testClub.setRegistrationFee(null);
        when(requestRepository.findById(1)).thenReturn(Optional.of(testRequest));
        when(memberService.hasManagementRole(1, 200L)).thenReturn(true);
        when(requestRepository.save(any(MembershipRequest.class))).thenReturn(testRequest);
        
        // When
        MembershipRequestDTO result = membershipRequestService.approveRequest(1, 200L);
        
        // Then
        assertNotNull(result);
        verify(memberService).addMemberToClub(1, 100L);
        verify(requestRepository).save(any(MembershipRequest.class));
    }
    
    @Test
    void testApproveRequest_Success_WithFee() {
        // Given
        when(requestRepository.findById(1)).thenReturn(Optional.of(testRequest));
        when(memberService.hasManagementRole(1, 200L)).thenReturn(true);
        when(requestRepository.save(any(MembershipRequest.class))).thenReturn(testRequest);
        when(authServiceClient.getUserInfo(100L)).thenReturn(testUserInfo);
        
        // When
        MembershipRequestDTO result = membershipRequestService.approveRequest(1, 200L);
        
        // Then
        assertNotNull(result);
        verify(memberService).addMemberToClub(1, 100L);
        verify(authServiceClient).sendClubPaymentRequiredEmail(anyString(), anyString(), anyString(), anyDouble(), anyString());
    }
    
    @Test
    void testApproveRequest_Unauthorized() {
        // Given
        when(requestRepository.findById(1)).thenReturn(Optional.of(testRequest));
        when(memberService.hasManagementRole(1, 200L)).thenReturn(false);
        
        // When & Then
        assertThrows(UnauthorizedException.class, () -> 
                membershipRequestService.approveRequest(1, 200L));
    }
    
    @Test
    void testApproveRequest_ClubFull() {
        // Given
        testClub.setMaxMembers(0);
        when(requestRepository.findById(1)).thenReturn(Optional.of(testRequest));
        when(memberService.hasManagementRole(1, 200L)).thenReturn(true);
        
        // When & Then
        assertThrows(ClubFullException.class, () -> 
                membershipRequestService.approveRequest(1, 200L));
    }
    
    @Test
    void testRejectRequest_Success() {
        // Given
        when(requestRepository.findById(1)).thenReturn(Optional.of(testRequest));
        when(memberService.hasManagementRole(1, 200L)).thenReturn(true);
        when(requestRepository.save(any(MembershipRequest.class))).thenReturn(testRequest);
        
        // When
        MembershipRequestDTO result = membershipRequestService.rejectRequest(1, 200L, "Not qualified");
        
        // Then
        assertNotNull(result);
        verify(requestRepository).save(any(MembershipRequest.class));
    }
    
    @Test
    void testRejectRequest_Unauthorized() {
        // Given
        when(requestRepository.findById(1)).thenReturn(Optional.of(testRequest));
        when(memberService.hasManagementRole(1, 200L)).thenReturn(false);
        
        // When & Then
        assertThrows(UnauthorizedException.class, () -> 
                membershipRequestService.rejectRequest(1, 200L, "Comment"));
    }
    
    @Test
    void testConfirmPayment_Success() {
        // Given
        testRequest.setStatus(MembershipRequestStatus.PAYMENT_PENDING);
        when(requestRepository.findById(1)).thenReturn(Optional.of(testRequest));
        when(requestRepository.save(any(MembershipRequest.class))).thenReturn(testRequest);
        when(authServiceClient.getUserInfo(100L)).thenReturn(testUserInfo);
        
        // When
        MembershipRequestDTO result = membershipRequestService.confirmPayment(1, "CARD", "token123");
        
        // Then
        assertNotNull(result);
        verify(clubHistoryService).logHistory(anyLong(), anyLong(), any(), anyString(), anyString(), any(), anyString(), anyLong());
        verify(expenseService).createIncomeEntry(any());
    }
    
    @Test
    void testGetTotalConfirmedPayments() {
        // Given
        MembershipRequest confirmedRequest = MembershipRequest.builder()
                .club(testClub)
                .status(MembershipRequestStatus.APPROVED)
                .paymentConfirmedAt(LocalDateTime.now())
                .build();
        
        when(requestRepository.findByClubId(1)).thenReturn(Arrays.asList(confirmedRequest));
        
        // When
        Double total = membershipRequestService.getTotalConfirmedPayments(1);
        
        // Then
        assertEquals(10.0, total);
    }
    
    @Test
    void testGetRequestById() {
        // Given
        when(requestRepository.findById(1)).thenReturn(Optional.of(testRequest));
        when(authServiceClient.getUserInfo(100L)).thenReturn(testUserInfo);
        
        // When
        MembershipRequestDTO result = membershipRequestService.getRequestById(1);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
    }
}
