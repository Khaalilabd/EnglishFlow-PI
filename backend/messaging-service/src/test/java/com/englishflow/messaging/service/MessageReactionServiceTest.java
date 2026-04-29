package com.englishflow.messaging.service;

import com.englishflow.messaging.client.AuthServiceClient;
import com.englishflow.messaging.dto.MessageReactionDTO;
import com.englishflow.messaging.dto.ReactionSummaryDTO;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageReactionServiceTest {

    @Mock
    private MessageReactionRepository reactionRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private MessageReactionService reactionService;

    private Message message;
    private MessageReaction reaction;
    private AuthServiceClient.UserInfo userInfo;

    @BeforeEach
    void setUp() {
        message = new Message();
        message.setId(1L);
        message.setContent("Test message");

        reaction = new MessageReaction();
        reaction.setId(1L);
        reaction.setMessage(message);
        reaction.setUserId(1L);
        reaction.setUserName("Test User");
        reaction.setEmoji("👍");
        reaction.setCreatedAt(LocalDateTime.now());

        userInfo = new AuthServiceClient.UserInfo();
        userInfo.setId(1L);
        userInfo.setFirstName("Test");
        userInfo.setLastName("User");
        userInfo.setEmail("test@example.com");
    }

    @Test
    void toggleReaction_WhenReactionDoesNotExist_ShouldAddReaction() {
        // Arrange
        Long messageId = 1L;
        Long userId = 1L;
        String emoji = "👍";

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(authServiceClient.getUserInfo(userId)).thenReturn(userInfo);
        when(reactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, userId, emoji))
                .thenReturn(Optional.empty());
        when(reactionRepository.save(any(MessageReaction.class))).thenReturn(reaction);

        // Act
        MessageReactionDTO result = reactionService.toggleReaction(messageId, emoji, userId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(emoji, result.getEmoji());
        verify(reactionRepository).save(any(MessageReaction.class));
        verify(reactionRepository, never()).delete(any());
    }

    @Test
    void toggleReaction_WhenReactionExists_ShouldRemoveReaction() {
        // Arrange
        Long messageId = 1L;
        Long userId = 1L;
        String emoji = "👍";

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(reactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, userId, emoji))
                .thenReturn(Optional.of(reaction));

        // Act
        MessageReactionDTO result = reactionService.toggleReaction(messageId, emoji, userId);

        // Assert
        assertNull(result);
        verify(reactionRepository).delete(reaction);
        verify(reactionRepository, never()).save(any());
    }

    @Test
    void toggleReaction_WhenMessageNotFound_ShouldThrowException() {
        // Arrange
        Long messageId = 999L;
        Long userId = 1L;
        String emoji = "👍";

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                reactionService.toggleReaction(messageId, emoji, userId)
        );
    }

    @Test
    void toggleReaction_WhenAuthServiceFails_ShouldUseDefaultName() {
        // Arrange
        Long messageId = 1L;
        Long userId = 1L;
        String emoji = "👍";

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(authServiceClient.getUserInfo(userId)).thenThrow(new RuntimeException("Service unavailable"));
        when(reactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, userId, emoji))
                .thenReturn(Optional.empty());
        when(reactionRepository.save(any(MessageReaction.class))).thenReturn(reaction);

        // Act
        MessageReactionDTO result = reactionService.toggleReaction(messageId, emoji, userId);

        // Assert
        assertNotNull(result);
        verify(reactionRepository).save(any(MessageReaction.class));
    }

    @Test
    void getReactionSummary_ShouldReturnGroupedReactions() {
        // Arrange
        Long messageId = 1L;
        Long currentUserId = 1L;

        MessageReaction reaction1 = new MessageReaction();
        reaction1.setUserId(1L);
        reaction1.setUserName("User 1");
        reaction1.setEmoji("👍");

        MessageReaction reaction2 = new MessageReaction();
        reaction2.setUserId(2L);
        reaction2.setUserName("User 2");
        reaction2.setEmoji("👍");

        MessageReaction reaction3 = new MessageReaction();
        reaction3.setUserId(3L);
        reaction3.setUserName("User 3");
        reaction3.setEmoji("❤️");

        when(reactionRepository.findByMessageId(messageId))
                .thenReturn(Arrays.asList(reaction1, reaction2, reaction3));

        // Act
        List<ReactionSummaryDTO> result = reactionService.getReactionSummary(messageId, currentUserId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        ReactionSummaryDTO thumbsUp = result.stream()
                .filter(r -> r.getEmoji().equals("👍"))
                .findFirst()
                .orElse(null);
        assertNotNull(thumbsUp);
        assertEquals(2L, thumbsUp.getCount());
        assertTrue(thumbsUp.isReactedByCurrentUser());

        ReactionSummaryDTO heart = result.stream()
                .filter(r -> r.getEmoji().equals("❤️"))
                .findFirst()
                .orElse(null);
        assertNotNull(heart);
        assertEquals(1L, heart.getCount());
        assertFalse(heart.isReactedByCurrentUser());
    }

    @Test
    void getReactionSummary_WhenNoReactions_ShouldReturnEmptyList() {
        // Arrange
        Long messageId = 1L;
        Long currentUserId = 1L;

        when(reactionRepository.findByMessageId(messageId)).thenReturn(Arrays.asList());

        // Act
        List<ReactionSummaryDTO> result = reactionService.getReactionSummary(messageId, currentUserId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getReactionSummary_WhenCurrentUserIdIsNull_ShouldNotMarkAsReacted() {
        // Arrange
        Long messageId = 1L;

        MessageReaction reaction1 = new MessageReaction();
        reaction1.setUserId(1L);
        reaction1.setUserName("User 1");
        reaction1.setEmoji("👍");

        when(reactionRepository.findByMessageId(messageId))
                .thenReturn(Arrays.asList(reaction1));

        // Act
        List<ReactionSummaryDTO> result = reactionService.getReactionSummary(messageId, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isReactedByCurrentUser());
    }

    @Test
    void getReactionSummariesForMessages_ShouldReturnMapOfSummaries() {
        // Arrange
        List<Long> messageIds = Arrays.asList(1L, 2L);

        Message message1 = new Message();
        message1.setId(1L);

        Message message2 = new Message();
        message2.setId(2L);

        MessageReaction reaction1 = new MessageReaction();
        reaction1.setMessage(message1);
        reaction1.setUserId(1L);
        reaction1.setUserName("User 1");
        reaction1.setEmoji("👍");

        MessageReaction reaction2 = new MessageReaction();
        reaction2.setMessage(message2);
        reaction2.setUserId(2L);
        reaction2.setUserName("User 2");
        reaction2.setEmoji("❤️");

        when(reactionRepository.findByMessageIdIn(messageIds))
                .thenReturn(Arrays.asList(reaction1, reaction2));

        // Act
        var result = reactionService.getReactionSummariesForMessages(messageIds, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(1L));
        assertTrue(result.containsKey(2L));
        assertEquals(1, result.get(1L).size());
        assertEquals(1, result.get(2L).size());
    }
}
