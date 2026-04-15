package com.englishflow.auth.controller;

import com.englishflow.auth.dto.UserLevelDTO;
import com.englishflow.auth.service.GamificationIntegrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationControllerTest {

    @Mock
    private GamificationIntegrationService gamificationIntegrationService;

    @InjectMocks
    private GamificationController gamificationController;

    private UserLevelDTO userLevelDTO;

    @BeforeEach
    void setUp() {
        userLevelDTO = UserLevelDTO.builder()
                .userId(1L)
                .assessmentLevel("B1")
                .currentXP(250)
                .totalXP(500)
                .jungleCoins(100)
                .build();
    }

    @Test
    void testGetUserLevel_Success() {
        // Arrange
        when(gamificationIntegrationService.getUserLevel(1L)).thenReturn(userLevelDTO);

        // Act
        ResponseEntity<UserLevelDTO> response = gamificationController.getUserLevel(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getUserId());
        assertEquals("B1", response.getBody().getAssessmentLevel());
        assertEquals(250, response.getBody().getCurrentXP());
        
        verify(gamificationIntegrationService).getUserLevel(1L);
    }

    @Test
    void testInitializeUserLevel_Success() {
        // Arrange
        when(gamificationIntegrationService.initializeUserLevel(1L, "A1")).thenReturn(userLevelDTO);

        // Act
        ResponseEntity<UserLevelDTO> response = gamificationController.initializeUserLevel(1L, "A1");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getUserId());
        
        verify(gamificationIntegrationService).initializeUserLevel(1L, "A1");
    }

    @Test
    void testInitializeUserLevel_DefaultLevel() {
        // Arrange
        when(gamificationIntegrationService.initializeUserLevel(1L, "A1")).thenReturn(userLevelDTO);

        // Act
        ResponseEntity<UserLevelDTO> response = gamificationController.initializeUserLevel(1L, "A1");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(gamificationIntegrationService).initializeUserLevel(1L, "A1");
    }
}
