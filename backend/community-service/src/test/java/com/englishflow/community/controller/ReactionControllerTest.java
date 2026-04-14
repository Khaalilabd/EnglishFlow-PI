package com.englishflow.community.controller;

import com.englishflow.community.dto.ReactionCountDTO;
import com.englishflow.community.dto.ReactionDTO;
import com.englishflow.community.entity.Reaction;
import com.englishflow.community.service.ReactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactionControllerTest {

    @Mock
    private ReactionService reactionService;

    @InjectMocks
    private ReactionController reactionController;

    private ReactionDTO reactionDTO;

    @BeforeEach
    void setUp() {
        reactionDTO = new ReactionDTO();
        reactionDTO.setId(1L);
        reactionDTO.setUserId(100L);
        reactionDTO.setType(Reaction.ReactionType.LIKE);
        reactionDTO.setPostId(10L);
    }

    @Test
    void addReactionToPost_Success_ReturnsCreatedReaction() {
        // Arrange
        when(reactionService.addReactionToPost(10L, 100L, Reaction.ReactionType.LIKE))
                .thenReturn(reactionDTO);

        // Act
        ResponseEntity<ReactionDTO> response = reactionController.addReactionToPost(
                10L, 100L, Reaction.ReactionType.LIKE
        );

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(reactionService, times(1)).addReactionToPost(10L, 100L, Reaction.ReactionType.LIKE);
    }

    @Test
    void addReactionToTopic_Success_ReturnsCreatedReaction() {
        // Arrange
        reactionDTO.setTopicId(5L);
        reactionDTO.setPostId(null);
        when(reactionService.addReactionToTopic(5L, 100L, Reaction.ReactionType.HELPFUL))
                .thenReturn(reactionDTO);

        // Act
        ResponseEntity<ReactionDTO> response = reactionController.addReactionToTopic(
                5L, 100L, Reaction.ReactionType.HELPFUL
        );

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reactionService, times(1)).addReactionToTopic(5L, 100L, Reaction.ReactionType.HELPFUL);
    }

    @Test
    void removeReactionFromPost_Success_ReturnsNoContent() {
        // Arrange
        doNothing().when(reactionService).removeReactionFromPost(10L, 100L);

        // Act
        ResponseEntity<Void> response = reactionController.removeReactionFromPost(10L, 100L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reactionService, times(1)).removeReactionFromPost(10L, 100L);
    }

    @Test
    void removeReactionFromPost_WithException_ReturnsNoContent() {
        // Arrange
        doThrow(new RuntimeException("Test exception"))
                .when(reactionService).removeReactionFromPost(10L, 100L);

        // Act
        ResponseEntity<Void> response = reactionController.removeReactionFromPost(10L, 100L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reactionService, times(1)).removeReactionFromPost(10L, 100L);
    }

    @Test
    void removeReactionFromTopic_Success_ReturnsNoContent() {
        // Arrange
        doNothing().when(reactionService).removeReactionFromTopic(5L, 100L);

        // Act
        ResponseEntity<Void> response = reactionController.removeReactionFromTopic(5L, 100L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reactionService, times(1)).removeReactionFromTopic(5L, 100L);
    }

    @Test
    void getPostReactionsByType_Success_ReturnsReactionCounts() {
        // Arrange
        ReactionCountDTO count1 = new ReactionCountDTO(Reaction.ReactionType.LIKE, 5L);
        ReactionCountDTO count2 = new ReactionCountDTO(Reaction.ReactionType.HELPFUL, 3L);
        List<ReactionCountDTO> counts = Arrays.asList(count1, count2);
        when(reactionService.getPostReactionsByType(10L)).thenReturn(counts);

        // Act
        ResponseEntity<List<ReactionCountDTO>> response = reactionController.getPostReactionsByType(10L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(reactionService, times(1)).getPostReactionsByType(10L);
    }

    @Test
    void getTopicReactionsByType_Success_ReturnsReactionCounts() {
        // Arrange
        ReactionCountDTO count = new ReactionCountDTO(Reaction.ReactionType.INSIGHTFUL, 7L);
        List<ReactionCountDTO> counts = Arrays.asList(count);
        when(reactionService.getTopicReactionsByType(5L)).thenReturn(counts);

        // Act
        ResponseEntity<List<ReactionCountDTO>> response = reactionController.getTopicReactionsByType(5L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reactionService, times(1)).getTopicReactionsByType(5L);
    }

    @Test
    void getUserReactionForPost_Found_ReturnsReaction() {
        // Arrange
        when(reactionService.getUserReactionForPost(10L, 100L))
                .thenReturn(Optional.of(reactionDTO));

        // Act
        ResponseEntity<ReactionDTO> response = reactionController.getUserReactionForPost(10L, 100L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reactionService, times(1)).getUserReactionForPost(10L, 100L);
    }

    @Test
    void getUserReactionForPost_NotFound_ReturnsNoContent() {
        // Arrange
        when(reactionService.getUserReactionForPost(10L, 100L))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<ReactionDTO> response = reactionController.getUserReactionForPost(10L, 100L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reactionService, times(1)).getUserReactionForPost(10L, 100L);
    }

    @Test
    void getUserReactionForTopic_Found_ReturnsReaction() {
        // Arrange
        when(reactionService.getUserReactionForTopic(5L, 100L))
                .thenReturn(Optional.of(reactionDTO));

        // Act
        ResponseEntity<ReactionDTO> response = reactionController.getUserReactionForTopic(5L, 100L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reactionService, times(1)).getUserReactionForTopic(5L, 100L);
    }

    @Test
    void recalculateAllScores_Success_ReturnsSuccessMessage() {
        // Arrange
        doNothing().when(reactionService).recalculateAllScores();

        // Act
        ResponseEntity<String> response = reactionController.recalculateAllScores();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("successfully"));
        verify(reactionService, times(1)).recalculateAllScores();
    }

    @Test
    void recalculateAllScores_WithException_ReturnsError() {
        // Arrange
        doThrow(new RuntimeException("Calculation error"))
                .when(reactionService).recalculateAllScores();

        // Act
        ResponseEntity<String> response = reactionController.recalculateAllScores();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error"));
        verify(reactionService, times(1)).recalculateAllScores();
    }
}
