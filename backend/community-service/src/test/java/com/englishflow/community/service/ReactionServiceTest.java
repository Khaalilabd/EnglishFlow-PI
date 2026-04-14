package com.englishflow.community.service;

import com.englishflow.community.dto.ReactionCountDTO;
import com.englishflow.community.dto.ReactionDTO;
import com.englishflow.community.entity.Post;
import com.englishflow.community.entity.Reaction;
import com.englishflow.community.entity.Topic;
import com.englishflow.community.exception.ResourceNotFoundException;
import com.englishflow.community.repository.PostRepository;
import com.englishflow.community.repository.ReactionRepository;
import com.englishflow.community.repository.TopicRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private ReactionService reactionService;

    private Post testPost;
    private Topic testTopic;
    private Reaction testReaction;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setId(1L);
        testPost.setContent("Test Post");
        testPost.setUserId(100L);

        testTopic = new Topic();
        testTopic.setId(1L);
        testTopic.setTitle("Test Topic");
        testTopic.setUserId(100L);

        testReaction = new Reaction();
        testReaction.setId(1L);
        testReaction.setUserId(100L);
        testReaction.setType(Reaction.ReactionType.LIKE);
        testReaction.setPost(testPost);
    }

    @Test
    void addReactionToPost_NewReaction_Success() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(reactionRepository.findByUserIdAndPostId(100L, 1L)).thenReturn(Optional.empty());
        when(reactionRepository.save(any(Reaction.class))).thenReturn(testReaction);
        when(reactionRepository.countByPostIdAndType(eq(1L), any())).thenReturn(1L);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        ReactionDTO result = reactionService.addReactionToPost(1L, 100L, Reaction.ReactionType.LIKE);

        // Assert
        assertNotNull(result);
        assertEquals(Reaction.ReactionType.LIKE, result.getType());
        verify(reactionRepository, times(1)).save(any(Reaction.class));
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void addReactionToPost_UpdateExisting_Success() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(reactionRepository.findByUserIdAndPostId(100L, 1L)).thenReturn(Optional.of(testReaction));
        when(reactionRepository.save(any(Reaction.class))).thenReturn(testReaction);
        when(reactionRepository.countByPostIdAndType(eq(1L), any())).thenReturn(1L);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        ReactionDTO result = reactionService.addReactionToPost(1L, 100L, Reaction.ReactionType.HELPFUL);

        // Assert
        assertNotNull(result);
        verify(reactionRepository, times(1)).save(any(Reaction.class));
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void addReactionToPost_PostNotFound_ThrowsException() {
        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> reactionService.addReactionToPost(999L, 100L, Reaction.ReactionType.LIKE));
        verify(reactionRepository, never()).save(any(Reaction.class));
    }

    @Test
    void addReactionToTopic_NewReaction_Success() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(reactionRepository.findByUserIdAndTopicId(100L, 1L)).thenReturn(Optional.empty());
        when(reactionRepository.save(any(Reaction.class))).thenReturn(testReaction);
        when(reactionRepository.countByTopicIdAndType(eq(1L), any())).thenReturn(1L);
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // Act
        ReactionDTO result = reactionService.addReactionToTopic(1L, 100L, Reaction.ReactionType.INSIGHTFUL);

        // Assert
        assertNotNull(result);
        verify(reactionRepository, times(1)).save(any(Reaction.class));
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void addReactionToTopic_UpdateExisting_Success() {
        // Arrange
        testReaction.setTopic(testTopic);
        testReaction.setPost(null);
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(reactionRepository.findByUserIdAndTopicId(100L, 1L)).thenReturn(Optional.of(testReaction));
        when(reactionRepository.save(any(Reaction.class))).thenReturn(testReaction);
        when(reactionRepository.countByTopicIdAndType(eq(1L), any())).thenReturn(1L);
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // Act
        ReactionDTO result = reactionService.addReactionToTopic(1L, 100L, Reaction.ReactionType.HELPFUL);

        // Assert
        assertNotNull(result);
        verify(reactionRepository, times(1)).save(any(Reaction.class));
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void addReactionToTopic_TopicNotFound_ThrowsException() {
        // Arrange
        when(topicRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> reactionService.addReactionToTopic(999L, 100L, Reaction.ReactionType.LIKE));
        verify(reactionRepository, never()).save(any(Reaction.class));
    }

    @Test
    void removeReactionFromPost_Success() {
        // Arrange
        when(reactionRepository.findByUserIdAndPostId(100L, 1L)).thenReturn(Optional.of(testReaction));
        when(reactionRepository.countByPostIdAndType(eq(1L), any())).thenReturn(0L);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        reactionService.removeReactionFromPost(1L, 100L);

        // Assert
        verify(reactionRepository, times(1)).delete(testReaction);
        verify(reactionRepository, times(1)).flush();
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void removeReactionFromPost_NoReaction_SilentlyReturns() {
        // Arrange
        when(reactionRepository.findByUserIdAndPostId(100L, 1L)).thenReturn(Optional.empty());

        // Act
        reactionService.removeReactionFromPost(1L, 100L);

        // Assert
        verify(reactionRepository, never()).delete(any(Reaction.class));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void removeReactionFromTopic_Success() {
        // Arrange
        testReaction.setTopic(testTopic);
        testReaction.setPost(null);
        when(reactionRepository.findByUserIdAndTopicId(100L, 1L)).thenReturn(Optional.of(testReaction));
        when(reactionRepository.countByTopicIdAndType(eq(1L), any())).thenReturn(0L);
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // Act
        reactionService.removeReactionFromTopic(1L, 100L);

        // Assert
        verify(reactionRepository, times(1)).delete(testReaction);
        verify(reactionRepository, times(1)).flush();
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void removeReactionFromTopic_NoReaction_SilentlyReturns() {
        // Arrange
        when(reactionRepository.findByUserIdAndTopicId(100L, 1L)).thenReturn(Optional.empty());

        // Act
        reactionService.removeReactionFromTopic(1L, 100L);

        // Assert
        verify(reactionRepository, never()).delete(any(Reaction.class));
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void getPostReactionsCount_Success() {
        // Arrange
        when(reactionRepository.countByPostId(1L)).thenReturn(10L);

        // Act
        Long count = reactionService.getPostReactionsCount(1L);

        // Assert
        assertEquals(10L, count);
        verify(reactionRepository, times(1)).countByPostId(1L);
    }

    @Test
    void getTopicReactionsCount_Success() {
        // Arrange
        when(reactionRepository.countByTopicId(1L)).thenReturn(15L);

        // Act
        Long count = reactionService.getTopicReactionsCount(1L);

        // Assert
        assertEquals(15L, count);
        verify(reactionRepository, times(1)).countByTopicId(1L);
    }

    @Test
    void getPostReactionsByType_Success() {
        // Arrange
        when(reactionRepository.countByPostIdAndType(1L, Reaction.ReactionType.LIKE)).thenReturn(5L);
        when(reactionRepository.countByPostIdAndType(1L, Reaction.ReactionType.HELPFUL)).thenReturn(3L);
        when(reactionRepository.countByPostIdAndType(1L, Reaction.ReactionType.INSIGHTFUL)).thenReturn(2L);

        // Act
        List<ReactionCountDTO> counts = reactionService.getPostReactionsByType(1L);

        // Assert
        assertNotNull(counts);
        assertEquals(3, counts.size());
        verify(reactionRepository, times(3)).countByPostIdAndType(eq(1L), any());
    }

    @Test
    void getTopicReactionsByType_Success() {
        // Arrange
        when(reactionRepository.countByTopicIdAndType(1L, Reaction.ReactionType.LIKE)).thenReturn(7L);
        when(reactionRepository.countByTopicIdAndType(1L, Reaction.ReactionType.HELPFUL)).thenReturn(0L);
        when(reactionRepository.countByTopicIdAndType(1L, Reaction.ReactionType.INSIGHTFUL)).thenReturn(4L);

        // Act
        List<ReactionCountDTO> counts = reactionService.getTopicReactionsByType(1L);

        // Assert
        assertNotNull(counts);
        assertEquals(2, counts.size()); // Only non-zero counts
        verify(reactionRepository, times(3)).countByTopicIdAndType(eq(1L), any());
    }

    @Test
    void getUserReactionForPost_Found_ReturnsReaction() {
        // Arrange
        when(reactionRepository.findByUserIdAndPostId(100L, 1L)).thenReturn(Optional.of(testReaction));

        // Act
        Optional<ReactionDTO> result = reactionService.getUserReactionForPost(1L, 100L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Reaction.ReactionType.LIKE, result.get().getType());
        verify(reactionRepository, times(1)).findByUserIdAndPostId(100L, 1L);
    }

    @Test
    void getUserReactionForPost_NotFound_ReturnsEmpty() {
        // Arrange
        when(reactionRepository.findByUserIdAndPostId(100L, 1L)).thenReturn(Optional.empty());

        // Act
        Optional<ReactionDTO> result = reactionService.getUserReactionForPost(1L, 100L);

        // Assert
        assertFalse(result.isPresent());
        verify(reactionRepository, times(1)).findByUserIdAndPostId(100L, 1L);
    }

    @Test
    void getUserReactionForTopic_Found_ReturnsReaction() {
        // Arrange
        testReaction.setTopic(testTopic);
        testReaction.setPost(null);
        when(reactionRepository.findByUserIdAndTopicId(100L, 1L)).thenReturn(Optional.of(testReaction));

        // Act
        Optional<ReactionDTO> result = reactionService.getUserReactionForTopic(1L, 100L);

        // Assert
        assertTrue(result.isPresent());
        verify(reactionRepository, times(1)).findByUserIdAndTopicId(100L, 1L);
    }

    @Test
    void getUserReactionForTopic_NotFound_ReturnsEmpty() {
        // Arrange
        when(reactionRepository.findByUserIdAndTopicId(100L, 1L)).thenReturn(Optional.empty());

        // Act
        Optional<ReactionDTO> result = reactionService.getUserReactionForTopic(1L, 100L);

        // Assert
        assertFalse(result.isPresent());
        verify(reactionRepository, times(1)).findByUserIdAndTopicId(100L, 1L);
    }

    @Test
    void recalculateAllScores_Success() {
        // Arrange
        when(topicRepository.findAll()).thenReturn(Arrays.asList(testTopic));
        when(postRepository.findAll()).thenReturn(Arrays.asList(testPost));
        when(reactionRepository.countByTopicIdAndType(eq(1L), any())).thenReturn(1L);
        when(reactionRepository.countByPostIdAndType(eq(1L), any())).thenReturn(1L);
        when(topicRepository.saveAll(anyList())).thenReturn(Arrays.asList(testTopic));
        when(postRepository.saveAll(anyList())).thenReturn(Arrays.asList(testPost));

        // Act
        reactionService.recalculateAllScores();

        // Assert
        verify(topicRepository, times(1)).findAll();
        verify(postRepository, times(1)).findAll();
        verify(topicRepository, times(1)).saveAll(anyList());
        verify(postRepository, times(1)).saveAll(anyList());
    }
}
