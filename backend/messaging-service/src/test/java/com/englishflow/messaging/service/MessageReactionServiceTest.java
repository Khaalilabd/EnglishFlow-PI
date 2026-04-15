package com.englishflow.messaging.service;

import com.englishflow.messaging.dto.AddReactionRequest;
import com.englishflow.messaging.dto.MessageReactionDTO;
import com.englishflow.messaging.exception.ResourceNotFoundException;
import com.englishflow.messaging.model.Message;
import com.englishflow.messaging.model.MessageReaction;
import com.englishflow.messaging.repository.MessageReactionRepository;
import com.englishflow.messaging.repository.MessageRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageReactionServiceTest {

    @Mock
    private MessageReactionRepository reactionRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageReactionService reactionService;

    private Message message;
    private MessageReaction reaction;
    private Long messageId = 1L;
    private Long userId = 100L;

    @BeforeEach
    void setUp() {
        message = new Message();
        message.setId(messageId);
        message.setContent("Test message");
        message.setCreatedAt(LocalDateTime.now());

        reaction = new MessageReaction();
        reaction.setId(1L);
        reaction.setMessage(message);
        reaction.setUserId(userId);
        reaction.setUserName("Test User");
        reaction.setEmoji("👍");
        reaction.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void addReaction_WhenNew_ShouldCreateReaction() {
        // Given
        AddReactionRequest request = new AddReactionRequest();
        request.setEmoji("👍");

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(reactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, userId, "👍"))
            .thenReturn(Optional.empty());
        when(reactionRepository.save(any(MessageReaction.class))).thenReturn(reaction);

        // When
        MessageReactionDTO result = reactionService.addReaction(messageId, request, userId, "Test User");

        // Then
        assertNotNull(result);
        assertEquals("👍", result.getEmoji());
        verify(reactionRepository).save(any(MessageReaction.class));
    }

    @Test
    void addReaction_WhenExists_ShouldNotDuplicate() {
        // Given
        AddReactionRequest request = new AddReactionRequest();
        request.setEmoji("👍");

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(reactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, userId, "👍"))
            .thenReturn(Optional.of(reaction));

        // When
        MessageReactionDTO result = reactionService.addReaction(messageId, request, userId, "Test User");

        // Then
        assertNotNull(result);
        verify(reactionRepository, never()).save(any(MessageReaction.class));
    }

    @Test
    void addReaction_WhenMessageNotFound_ShouldThrowException() {
        // Given
        AddReactionRequest request = new AddReactionRequest();
        request.setEmoji("👍");

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, 
            () -> reactionService.addReaction(messageId, request, userId, "Test User"));
    }

    @Test
    void removeReaction_WhenExists_ShouldDelete() {
        // Given
        String emoji = "👍";
        when(reactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, userId, emoji))
            .thenReturn(Optional.of(reaction));

        // When
        reactionService.removeReaction(messageId, emoji, userId);

        // Then
        verify(reactionRepository).delete(reaction);
    }

    @Test
    void removeReaction_WhenNotExists_ShouldThrowException() {
        // Given
        String emoji = "👍";
        when(reactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, userId, emoji))
            .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, 
            () -> reactionService.removeReaction(messageId, emoji, userId));
    }

    @Test
    void getMessageReactions_ShouldReturnAllReactions() {
        // Given
        MessageReaction reaction2 = new MessageReaction();
        reaction2.setId(2L);
        reaction2.setMessage(message);
        reaction2.setUserId(200L);
        reaction2.setUserName("User 2");
        reaction2.setEmoji("❤️");
        reaction2.setCreatedAt(LocalDateTime.now());

        when(reactionRepository.findByMessageId(messageId))
            .thenReturn(Arrays.asList(reaction, reaction2));

        // When
        List<MessageReactionDTO> result = reactionService.getMessageReactions(messageId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(reactionRepository).findByMessageId(messageId);
    }

    @Test
    void getMessageReactions_WhenEmpty_ShouldReturnEmptyList() {
        // Given
        when(reactionRepository.findByMessageId(messageId)).thenReturn(Arrays.asList());

        // When
        List<MessageReactionDTO> result = reactionService.getMessageReactions(messageId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getUserReactionForMessage_WhenExists_ShouldReturnReaction() {
        // Given
        when(reactionRepository.findByMessageIdAndUserId(messageId, userId))
            .thenReturn(Arrays.asList(reaction));

        // When
        List<MessageReactionDTO> result = reactionService.getUserReactionForMessage(messageId, userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("👍", result.get(0).getEmoji());
    }
}
