package com.englishflow.community.service;

import com.englishflow.community.dto.CreatePostRequest;
import com.englishflow.community.dto.PostDTO;
import com.englishflow.community.entity.Post;
import com.englishflow.community.entity.Topic;
import com.englishflow.community.exception.ResourceNotFoundException;
import com.englishflow.community.exception.TopicLockedException;
import com.englishflow.community.exception.UnauthorizedException;
import com.englishflow.community.repository.PostRepository;
import com.englishflow.community.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private PostService postService;

    private Topic testTopic;
    private Post testPost;
    private CreatePostRequest createRequest;

    @BeforeEach
    void setUp() {
        testTopic = new Topic();
        testTopic.setId(1L);
        testTopic.setTitle("Test Topic");
        testTopic.setIsLocked(false);

        testPost = new Post();
        testPost.setId(1L);
        testPost.setContent("Test content");
        testPost.setUserId(100L);
        testPost.setUserName("Test User");
        testPost.setTopic(testTopic);
        testPost.setCreatedAt(LocalDateTime.now());

        createRequest = new CreatePostRequest();
        createRequest.setTopicId(1L);
        createRequest.setContent("Test content");
        createRequest.setUserId(100L);
        createRequest.setUserName("Test User");
    }

    @Test
    void createPost_Success_ReturnsPostDTO() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        PostDTO result = postService.createPost(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testPost.getId(), result.getId());
        assertEquals(testPost.getContent(), result.getContent());
        assertEquals(testPost.getUserId(), result.getUserId());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void createPost_TopicNotFound_ThrowsException() {
        // Arrange
        when(topicRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(createRequest));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void createPost_TopicLocked_ThrowsException() {
        // Arrange
        testTopic.setIsLocked(true);
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));

        // Act & Assert
        assertThrows(TopicLockedException.class, () -> postService.createPost(createRequest));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void deletePost_Success_DeletesPost() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // Act
        postService.deletePost(1L, 100L);

        // Assert
        verify(postRepository, times(1)).delete(testPost);
    }

    @Test
    void deletePost_UnauthorizedUser_ThrowsException() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> postService.deletePost(1L, 999L));
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    void deletePost_PostNotFound_ThrowsException() {
        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(1L, 100L));
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    void updatePost_Success_ReturnsUpdatedPost() {
        // Arrange
        CreatePostRequest updateRequest = new CreatePostRequest();
        updateRequest.setContent("Updated content");
        
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        PostDTO result = postService.updatePost(1L, 100L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    void updatePost_UnauthorizedUser_ThrowsException() {
        // Arrange
        CreatePostRequest updateRequest = new CreatePostRequest();
        updateRequest.setContent("Updated content");
        
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> postService.updatePost(1L, 999L, updateRequest));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void getPostsByTopic_HelpfulSort_ReturnsPagedPosts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(Arrays.asList(testPost));
        
        when(postRepository.findByTopicIdOrderByWeightedScore(1L, pageable)).thenReturn(postPage);

        // Act
        Page<PostDTO> result = postService.getPostsByTopic(1L, "helpful", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(postRepository, times(1)).findByTopicIdOrderByWeightedScore(1L, pageable);
    }

    @Test
    void getPostsByTopic_RecentSort_ReturnsPagedPosts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(Arrays.asList(testPost));
        
        when(postRepository.findByTopicIdOrderByRecent(1L, pageable)).thenReturn(postPage);

        // Act
        Page<PostDTO> result = postService.getPostsByTopic(1L, "recent", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(postRepository, times(1)).findByTopicIdOrderByRecent(1L, pageable);
    }

    @Test
    void getPostsByTopic_TrendingSort_ReturnsPagedPosts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(Arrays.asList(testPost));
        
        when(postRepository.findByTopicIdOrderByTrending(anyLong(), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(postPage);

        // Act
        Page<PostDTO> result = postService.getPostsByTopic(1L, "trending", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(postRepository, times(1)).findByTopicIdOrderByTrending(anyLong(), any(LocalDateTime.class), eq(pageable));
    }
}
