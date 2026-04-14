package com.englishflow.community.service;

import com.englishflow.community.client.AuthServiceClient;
import com.englishflow.community.client.ClubServiceClient;
import com.englishflow.community.dto.CreateTopicRequest;
import com.englishflow.community.dto.TopicDTO;
import com.englishflow.community.entity.Category;
import com.englishflow.community.entity.SubCategory;
import com.englishflow.community.entity.Topic;
import com.englishflow.community.exception.ResourceNotFoundException;
import com.englishflow.community.exception.UnauthorizedException;
import com.englishflow.community.repository.SubCategoryRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @Mock
    private ClubServiceClient clubServiceClient;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private TopicService topicService;

    private SubCategory testSubCategory;
    private Category testCategory;
    private Topic testTopic;
    private CreateTopicRequest createRequest;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
        testCategory.setIsLocked(false);

        testSubCategory = new SubCategory();
        testSubCategory.setId(10L);
        testSubCategory.setName("Test SubCategory");
        testSubCategory.setCategory(testCategory);
        testSubCategory.setIsLocked(false);
        testSubCategory.setRequiresClubMembership(false);

        testTopic = new Topic();
        testTopic.setId(1L);
        testTopic.setTitle("Test Topic");
        testTopic.setContent("Test Content");
        testTopic.setUserId(100L);
        testTopic.setUserName("Test User");
        testTopic.setSubCategory(testSubCategory);
        testTopic.setViewsCount(0);
        testTopic.setIsPinned(false);
        testTopic.setIsLocked(false);
        testTopic.setPosts(new ArrayList<>());
        testTopic.setCreatedAt(LocalDateTime.now());

        createRequest = new CreateTopicRequest();
        createRequest.setTitle("Test Topic");
        createRequest.setContent("Test Content");
        createRequest.setUserId(100L);
        createRequest.setUserName("Test User");
        createRequest.setSubCategoryId(10L);
    }

    @Test
    void createTopic_Success_ReturnsTopic() {
        // Arrange
        when(subCategoryRepository.findById(10L)).thenReturn(Optional.of(testSubCategory));
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // Act
        TopicDTO result = topicService.createTopic(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Test Topic", result.getTitle());
        assertEquals("Test Content", result.getContent());
        assertEquals(100L, result.getUserId());
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void createTopic_SubCategoryNotFound_ThrowsException() {
        // Arrange
        when(subCategoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> topicService.createTopic(createRequest));
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void createTopic_WithResourceLink_Success() {
        // Arrange
        createRequest.setResourceType("PDF");
        createRequest.setResourceLink("http://example.com/file.pdf");
        when(subCategoryRepository.findById(10L)).thenReturn(Optional.of(testSubCategory));
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // Act
        TopicDTO result = topicService.createTopic(createRequest);

        // Assert
        assertNotNull(result);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void getTopicsBySubCategory_RecentSort_ReturnsPagedTopics() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Topic> topicPage = new PageImpl<>(Arrays.asList(testTopic));
        when(topicRepository.findBySubCategoryIdOrderByRecent(10L, pageable)).thenReturn(topicPage);

        // Act
        Page<TopicDTO> result = topicService.getTopicsBySubCategory(10L, "recent", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(topicRepository, times(1)).findBySubCategoryIdOrderByRecent(10L, pageable);
    }

    @Test
    void getTopicsBySubCategory_HelpfulSort_ReturnsPagedTopics() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Topic> topicPage = new PageImpl<>(Arrays.asList(testTopic));
        when(topicRepository.findBySubCategoryIdOrderByWeightedScore(10L, pageable)).thenReturn(topicPage);

        // Act
        Page<TopicDTO> result = topicService.getTopicsBySubCategory(10L, "helpful", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(topicRepository, times(1)).findBySubCategoryIdOrderByWeightedScore(10L, pageable);
    }

    @Test
    void getTopicsBySubCategory_TrendingSort_ReturnsPagedTopics() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Topic> topicPage = new PageImpl<>(Arrays.asList(testTopic));
        when(topicRepository.findBySubCategoryIdOrderByTrending(eq(10L), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(topicPage);

        // Act
        Page<TopicDTO> result = topicService.getTopicsBySubCategory(10L, "trending", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(topicRepository, times(1)).findBySubCategoryIdOrderByTrending(eq(10L), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void getTopicsBySubCategory_ViewsSort_ReturnsPagedTopics() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Topic> topicPage = new PageImpl<>(Arrays.asList(testTopic));
        when(topicRepository.findBySubCategoryIdOrderByViews(10L, pageable)).thenReturn(topicPage);

        // Act
        Page<TopicDTO> result = topicService.getTopicsBySubCategory(10L, "views", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(topicRepository, times(1)).findBySubCategoryIdOrderByViews(10L, pageable);
    }

    @Test
    void getTopicById_Success_IncrementsViewCount() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // Act
        TopicDTO result = topicService.getTopicById(1L, 200L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void getTopicById_SameUser_DoesNotIncrementViewCount() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));

        // Act
        TopicDTO result = topicService.getTopicById(1L, 100L);

        // Assert
        assertNotNull(result);
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void getTopicById_NotFound_ThrowsException() {
        // Arrange
        when(topicRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> topicService.getTopicById(999L, 100L));
    }

    @Test
    void updateTopic_Success_ReturnsUpdatedTopic() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        CreateTopicRequest updateRequest = new CreateTopicRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setContent("Updated Content");

        // Act
        TopicDTO result = topicService.updateTopic(1L, 100L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void updateTopic_UnauthorizedUser_ThrowsException() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> topicService.updateTopic(1L, 999L, createRequest));
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void deleteTopic_Success_DeletesTopic() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));

        // Act
        topicService.deleteTopic(1L, 100L);

        // Assert
        verify(topicRepository, times(1)).delete(testTopic);
    }

    @Test
    void deleteTopic_UnauthorizedUser_ThrowsException() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> topicService.deleteTopic(1L, 999L));
        verify(topicRepository, never()).delete(any(Topic.class));
    }

    @Test
    void deleteTopic_AutoGenerated_AllowsSystemDeletion() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));

        // Act
        topicService.deleteTopic(1L, 0L);

        // Assert
        verify(topicRepository, times(1)).delete(testTopic);
    }

    @Test
    void pinTopic_Success_ReturnsPinnedTopic() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> {
            Topic topic = invocation.getArgument(0);
            topic.setIsPinned(true);
            return topic;
        });

        // Act
        TopicDTO result = topicService.pinTopic(1L);

        // Assert
        assertNotNull(result);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void unpinTopic_Success_ReturnsUnpinnedTopic() {
        // Arrange
        testTopic.setIsPinned(true);
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> {
            Topic topic = invocation.getArgument(0);
            topic.setIsPinned(false);
            return topic;
        });

        // Act
        TopicDTO result = topicService.unpinTopic(1L);

        // Assert
        assertNotNull(result);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void lockTopic_Success_ReturnsLockedTopic() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> {
            Topic topic = invocation.getArgument(0);
            topic.setIsLocked(true);
            return topic;
        });

        // Act
        TopicDTO result = topicService.lockTopic(1L);

        // Assert
        assertNotNull(result);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void unlockTopic_Success_ReturnsUnlockedTopic() {
        // Arrange
        testTopic.setIsLocked(true);
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(any(Topic.class))).thenAnswer(invocation -> {
            Topic topic = invocation.getArgument(0);
            topic.setIsLocked(false);
            return topic;
        });

        // Act
        TopicDTO result = topicService.unlockTopic(1L);

        // Assert
        assertNotNull(result);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void bulkPinTopics_Success_ReturnsCount() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // Act
        int count = topicService.bulkPinTopics(Arrays.asList(1L));

        // Assert
        assertEquals(1, count);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void bulkUnpinTopics_Success_ReturnsCount() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // Act
        int count = topicService.bulkUnpinTopics(Arrays.asList(1L));

        // Assert
        assertEquals(1, count);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void bulkLockTopics_Success_ReturnsCount() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // Act
        int count = topicService.bulkLockTopics(Arrays.asList(1L));

        // Assert
        assertEquals(1, count);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void bulkUnlockTopics_Success_ReturnsCount() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // Act
        int count = topicService.bulkUnlockTopics(Arrays.asList(1L));

        // Assert
        assertEquals(1, count);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void bulkDeleteTopics_Success_ReturnsCount() {
        // Arrange
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));

        // Act
        int count = topicService.bulkDeleteTopics(Arrays.asList(1L), 100L);

        // Assert
        assertEquals(1, count);
        verify(topicRepository, times(1)).delete(testTopic);
    }

    @Test
    void getModerationStats_Success_ReturnsStats() {
        // Arrange
        when(topicRepository.count()).thenReturn(100L);
        when(topicRepository.countByIsPinned(true)).thenReturn(10L);
        when(topicRepository.countByIsLocked(true)).thenReturn(5L);

        // Act
        java.util.Map<String, Object> stats = topicService.getModerationStats();

        // Assert
        assertNotNull(stats);
        assertEquals(100L, stats.get("totalTopics"));
        assertEquals(10L, stats.get("pinnedTopics"));
        assertEquals(5L, stats.get("lockedTopics"));
        assertEquals(85L, stats.get("normalTopics"));
    }
}
