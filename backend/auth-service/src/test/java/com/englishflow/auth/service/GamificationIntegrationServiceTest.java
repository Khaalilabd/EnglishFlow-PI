package com.englishflow.auth.service;

import com.englishflow.auth.client.GamificationClient;
import com.englishflow.auth.dto.UserLevelDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationIntegrationServiceTest {

    @Mock
    private GamificationClient gamificationClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GamificationIntegrationService gamificationIntegrationService;

    private Map<String, Object> mockResponse;
    private UserLevelDTO mockUserLevel;

    @BeforeEach
    void setUp() {
        mockResponse = new HashMap<>();
        mockResponse.put("userId", 1L);
        mockResponse.put("assessmentLevel", "A1");
        mockResponse.put("currentXP", 0);

        mockUserLevel = UserLevelDTO.builder()
                .userId(1L)
                .assessmentLevel("A1")
                .currentXP(0)
                .totalXP(0)
                .jungleCoins(0)
                .build();
    }

    @Test
    void testInitializeUserLevel_Success() {
        // Given
        when(gamificationClient.initializeUserLevel(1L, "A1")).thenReturn(mockResponse);
        when(objectMapper.convertValue(mockResponse, UserLevelDTO.class)).thenReturn(mockUserLevel);

        // When
        UserLevelDTO result = gamificationIntegrationService.initializeUserLevel(1L, "A1");

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("A1", result.getAssessmentLevel());
        verify(gamificationClient).initializeUserLevel(1L, "A1");
    }

    @Test
    void testInitializeUserLevel_WithNullLevel() {
        // Given
        when(gamificationClient.initializeUserLevel(1L, "A1")).thenReturn(mockResponse);
        when(objectMapper.convertValue(mockResponse, UserLevelDTO.class)).thenReturn(mockUserLevel);

        // When
        UserLevelDTO result = gamificationIntegrationService.initializeUserLevel(1L, null);

        // Then
        assertNotNull(result);
        verify(gamificationClient).initializeUserLevel(1L, "A1");
    }

    @Test
    void testInitializeUserLevel_Error() {
        // Given
        when(gamificationClient.initializeUserLevel(anyLong(), anyString()))
                .thenThrow(new RuntimeException("Service unavailable"));

        // When
        UserLevelDTO result = gamificationIntegrationService.initializeUserLevel(1L, "A1");

        // Then
        assertNotNull(result);
        assertEquals("A1", result.getAssessmentLevel());
    }

    @Test
    void testGetUserLevel_Success() {
        // Given
        when(gamificationClient.getUserLevel(1L)).thenReturn(mockResponse);
        when(objectMapper.convertValue(mockResponse, UserLevelDTO.class)).thenReturn(mockUserLevel);

        // When
        UserLevelDTO result = gamificationIntegrationService.getUserLevel(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(gamificationClient).getUserLevel(1L);
    }

    @Test
    void testGetUserLevel_NotFound() {
        // Given
        when(gamificationClient.getUserLevel(1L)).thenThrow(new RuntimeException("Not found"));

        // When
        UserLevelDTO result = gamificationIntegrationService.getUserLevel(1L);

        // Then
        assertNull(result);
    }

    @Test
    void testAddXP_Success() {
        // Given
        when(gamificationClient.addXP(anyLong(), anyMap()))
                .thenReturn(java.util.Map.of("success", true));

        // When
        gamificationIntegrationService.addXP(1L, 100, "Completed lesson");

        // Then
        verify(gamificationClient).addXP(eq(1L), anyMap());
    }

    @Test
    void testAddXP_Error() {
        // Given
        doThrow(new RuntimeException("Service error"))
                .when(gamificationClient).addXP(anyLong(), anyMap());

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> gamificationIntegrationService.addXP(1L, 100, "Test"));
    }

    @Test
    void testAddCoins_Success() {
        // Given
        when(gamificationClient.addCoins(anyLong(), anyMap()))
                .thenReturn(java.util.Map.of("success", true));

        // When
        gamificationIntegrationService.addCoins(1L, 50, "Daily bonus");

        // Then
        verify(gamificationClient).addCoins(eq(1L), anyMap());
    }

    @Test
    void testAddCoins_Error() {
        // Given
        doThrow(new RuntimeException("Service error"))
                .when(gamificationClient).addCoins(anyLong(), anyMap());

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> gamificationIntegrationService.addCoins(1L, 50, "Test"));
    }

    @Test
    void testRecordPurchase_Success() {
        // Given
        when(gamificationClient.recordPurchase(anyLong(), anyMap()))
                .thenReturn(java.util.Map.of("success", true));

        // When
        gamificationIntegrationService.recordPurchase(1L, 29.99);

        // Then
        verify(gamificationClient).recordPurchase(eq(1L), anyMap());
    }

    @Test
    void testRecordPurchase_Error() {
        // Given
        doThrow(new RuntimeException("Service error"))
                .when(gamificationClient).recordPurchase(anyLong(), anyMap());

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> gamificationIntegrationService.recordPurchase(1L, 29.99));
    }
}
