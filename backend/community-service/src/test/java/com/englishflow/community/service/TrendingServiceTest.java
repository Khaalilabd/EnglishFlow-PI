package com.englishflow.community.service;

import com.englishflow.community.entity.Post;
import com.englishflow.community.entity.Topic;
import com.englishflow.community.repository.PostRepository;
import com.englishflow.community.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrendingServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private TrendingService trendingService;

    private Post trendingPost;
    private Topic trendingTopic;

    @BeforeEach
    void setUp() {
        trendingPost = new Post();
        trendingPost.setId(1L);
        trendingPost.setWeightedScore(10);
        trendingPost.setCreatedAt(LocalDateTime.now().minusDays(2));

        trendingTopic = new Topic();
        trendingTopic.setId(1L);
        trendingTopic.setWeightedScore(15);
        trendingTopic.setCreatedAt(LocalDateTime.now().minusDays(3));
    }

    @Test
    void updateTrendingStatus_ShouldResetAllFlags() {
        when(postRepository.resetAllTrendingFlags()).thenReturn(10);
        when(topicRepository.resetAllTrendingFlags()).thenReturn(5);
        when(postRepository.findTrendingPosts(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Arrays.asList());
        when(topicRepository.findTrendingTopics(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Arrays.asList());

        trendingService.updateTrendingStatus();

        verify(postRepository).resetAllTrendingFlags();
        verify(topicRepository).resetAllTrendingFlags();
    }

    @Test
    void updateTrendingStatus_ShouldMarkTrendingPosts() {
        when(postRepository.resetAllTrendingFlags()).thenReturn(0);
        when(topicRepository.resetAllTrendingFlags()).thenReturn(0);
        when(postRepository.findTrendingPosts(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Arrays.asList(trendingPost));
        when(postRepository.markAsTrending(anyList())).thenReturn(1);
        when(topicRepository.findTrendingTopics(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Arrays.asList());

        trendingService.updateTrendingStatus();

        verify(postRepository).markAsTrending(anyList());
    }

    @Test
    void updateTrendingStatus_ShouldMarkTrendingTopics() {
        when(postRepository.resetAllTrendingFlags()).thenReturn(0);
        when(topicRepository.resetAllTrendingFlags()).thenReturn(0);
        when(postRepository.findTrendingPosts(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Arrays.asList());
        when(topicRepository.findTrendingTopics(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Arrays.asList(trendingTopic));
        when(topicRepository.markAsTrending(anyList())).thenReturn(1);

        trendingService.updateTrendingStatus();

        verify(topicRepository).markAsTrending(anyList());
    }

    @Test
    void updateTrendingStatus_LowScore_ShouldNotMarkAsTrending() {
        Post lowScorePost = new Post();
        lowScorePost.setId(2L);
        lowScorePost.setWeightedScore(2);
        lowScorePost.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(postRepository.resetAllTrendingFlags()).thenReturn(0);
        when(topicRepository.resetAllTrendingFlags()).thenReturn(0);
        when(postRepository.findTrendingPosts(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Arrays.asList(lowScorePost));
        when(topicRepository.findTrendingTopics(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Arrays.asList());

        trendingService.updateTrendingStatus();

        verify(postRepository, never()).markAsTrending(anyList());
    }

    @Test
    void updateTrendingStatus_NoTrendingContent_ShouldNotMarkAny() {
        when(postRepository.resetAllTrendingFlags()).thenReturn(0);
        when(topicRepository.resetAllTrendingFlags()).thenReturn(0);
        when(postRepository.findTrendingPosts(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Arrays.asList());
        when(topicRepository.findTrendingTopics(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Arrays.asList());

        trendingService.updateTrendingStatus();

        verify(postRepository, never()).markAsTrending(anyList());
        verify(topicRepository, never()).markAsTrending(anyList());
    }

    @Test
    void updateTrendingStatus_MultipleTrendingItems_ShouldMarkAll() {
        Post post2 = new Post();
        post2.setId(2L);
        post2.setWeightedScore(8);
        post2.setCreatedAt(LocalDateTime.now().minusDays(1));

        Topic topic2 = new Topic();
        topic2.setId(2L);
        topic2.setWeightedScore(12);
        topic2.setCreatedAt(LocalDateTime.now().minusDays(2));

        when(postRepository.resetAllTrendingFlags()).thenReturn(0);
        when(topicRepository.resetAllTrendingFlags()).thenReturn(0);
        when(postRepository.findTrendingPosts(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Arrays.asList(trendingPost, post2));
        when(topicRepository.findTrendingTopics(any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Arrays.asList(trendingTopic, topic2));
        when(postRepository.markAsTrending(anyList())).thenReturn(2);
        when(topicRepository.markAsTrending(anyList())).thenReturn(2);

        trendingService.updateTrendingStatus();

        verify(postRepository).markAsTrending(argThat(list -> list.size() == 2));
        verify(topicRepository).markAsTrending(argThat(list -> list.size() == 2));
    }
}
