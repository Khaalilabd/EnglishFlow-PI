package com.englishflow.club.service;

import com.englishflow.club.client.AuthServiceClient;
import com.englishflow.club.dto.ClubHistoryDTO;
import com.englishflow.club.dto.UserInfoDTO;
import com.englishflow.club.entity.ClubHistory;
import com.englishflow.club.enums.ClubHistoryType;
import com.englishflow.club.mapper.ClubHistoryMapper;
import com.englishflow.club.repository.ClubHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClubHistoryServiceTest {
    
    @Mock
    private ClubHistoryRepository historyRepository;
    
    @Mock
    private ClubHistoryMapper historyMapper;
    
    @Mock
    private AuthServiceClient authServiceClient;
    
    @InjectMocks
    private ClubHistoryService clubHistoryService;
    
    private ClubHistory testHistory;
    private ClubHistoryDTO testHistoryDTO;
    private UserInfoDTO testUserInfo;
    
    @BeforeEach
    void setUp() {
        testHistory = new ClubHistory();
        testHistory.setId(1L);
        testHistory.setClubId(1L);
        testHistory.setUserId(100L);
        testHistory.setType(ClubHistoryType.MEMBER_JOINED);
        testHistory.setAction("Member Joined");
        testHistory.setDescription("User joined the club");
        testHistory.setPerformedBy(200L);
        testHistory.setCreatedAt(LocalDateTime.now());
        
        testHistoryDTO = new ClubHistoryDTO(
                1L,
                1L,
                100L,
                ClubHistoryType.MEMBER_JOINED,
                "Member Joined",
                "User joined the club",
                null,
                null,
                200L,
                null,
                LocalDateTime.now()
        );
        
        testUserInfo = UserInfoDTO.builder()
                .id(200L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
    }
    
    @Test
    void testCreateHistory() {
        // Given
        when(historyMapper.toEntity(testHistoryDTO)).thenReturn(testHistory);
        when(historyRepository.save(testHistory)).thenReturn(testHistory);
        when(historyMapper.toDTO(testHistory)).thenReturn(testHistoryDTO);
        
        // When
        ClubHistoryDTO result = clubHistoryService.createHistory(testHistoryDTO);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(historyRepository).save(testHistory);
    }
    
    @Test
    void testLogHistory() {
        // Given
        when(historyRepository.save(any(ClubHistory.class))).thenReturn(testHistory);
        
        // When
        clubHistoryService.logHistory(
                1L, 100L, ClubHistoryType.MEMBER_JOINED, 
                "Member Joined", "User joined the club", 
                null, "new value", 200L);
        
        // Then
        verify(historyRepository).save(any(ClubHistory.class));
    }
    
    @Test
    void testGetClubHistory() {
        // Given
        List<ClubHistory> histories = Arrays.asList(testHistory);
        when(historyRepository.findByClubIdOrderByCreatedAtDesc(1L)).thenReturn(histories);
        when(historyMapper.toDTO(testHistory)).thenReturn(testHistoryDTO);
        when(authServiceClient.getUserInfo(200L)).thenReturn(testUserInfo);
        
        // When
        List<ClubHistoryDTO> result = clubHistoryService.getClubHistory(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getPerformedByName());
    }
    
    @Test
    void testGetClubHistory_EmptyList() {
        // Given
        when(historyRepository.findByClubIdOrderByCreatedAtDesc(1L)).thenReturn(Arrays.asList());
        
        // When
        List<ClubHistoryDTO> result = clubHistoryService.getClubHistory(1L);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testGetUserHistoryInClub() {
        // Given
        List<ClubHistory> histories = Arrays.asList(testHistory);
        when(historyRepository.findByClubIdAndUserIdOrderByCreatedAtDesc(1L, 100L)).thenReturn(histories);
        when(historyMapper.toDTO(testHistory)).thenReturn(testHistoryDTO);
        
        // When
        List<ClubHistoryDTO> result = clubHistoryService.getUserHistoryInClub(1L, 100L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getUserId());
    }
    
    @Test
    void testGetUserHistory() {
        // Given
        List<ClubHistory> histories = Arrays.asList(testHistory);
        when(historyRepository.findByUserIdOrderByCreatedAtDesc(100L)).thenReturn(histories);
        when(historyMapper.toDTO(testHistory)).thenReturn(testHistoryDTO);
        
        // When
        List<ClubHistoryDTO> result = clubHistoryService.getUserHistory(100L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    
    @Test
    void testGetHistoryByType() {
        // Given
        List<ClubHistory> histories = Arrays.asList(testHistory);
        when(historyRepository.findByClubIdAndTypeOrderByCreatedAtDesc(1L, ClubHistoryType.MEMBER_JOINED))
                .thenReturn(histories);
        when(historyMapper.toDTO(testHistory)).thenReturn(testHistoryDTO);
        
        // When
        List<ClubHistoryDTO> result = clubHistoryService.getHistoryByType(1L, ClubHistoryType.MEMBER_JOINED);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ClubHistoryType.MEMBER_JOINED, result.get(0).getType());
    }
    
    @Test
    void testGetRecentHistory() {
        // Given
        List<ClubHistory> histories = Arrays.asList(testHistory);
        when(historyRepository.findRecentHistory(eq(1L), any(LocalDateTime.class))).thenReturn(histories);
        when(historyMapper.toDTO(testHistory)).thenReturn(testHistoryDTO);
        
        // When
        List<ClubHistoryDTO> result = clubHistoryService.getRecentHistory(1L, 7);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    
    @Test
    void testCountClubHistory() {
        // Given
        when(historyRepository.countByClubId(1L)).thenReturn(5L);
        
        // When
        long count = clubHistoryService.countClubHistory(1L);
        
        // Then
        assertEquals(5L, count);
    }
    
    @Test
    void testCountUserHistoryInClub() {
        // Given
        when(historyRepository.countByClubIdAndUserId(1L, 100L)).thenReturn(3L);
        
        // When
        long count = clubHistoryService.countUserHistoryInClub(1L, 100L);
        
        // Then
        assertEquals(3L, count);
    }
    
    @Test
    void testDeleteClubHistory() {
        // Given
        List<ClubHistory> histories = Arrays.asList(testHistory);
        when(historyRepository.findByClubIdOrderByCreatedAtDesc(1L)).thenReturn(histories);
        
        // When
        clubHistoryService.deleteClubHistory(1L);
        
        // Then
        verify(historyRepository).deleteAll(histories);
    }
    
    @Test
    void testGetClubHistory_WithUserInfoException() {
        // Given
        List<ClubHistory> histories = Arrays.asList(testHistory);
        when(historyRepository.findByClubIdOrderByCreatedAtDesc(1L)).thenReturn(histories);
        when(historyMapper.toDTO(testHistory)).thenReturn(testHistoryDTO);
        when(authServiceClient.getUserInfo(200L)).thenThrow(new RuntimeException("Service unavailable"));
        
        // When
        List<ClubHistoryDTO> result = clubHistoryService.getClubHistory(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("User #200", result.get(0).getPerformedByName());
    }
}
