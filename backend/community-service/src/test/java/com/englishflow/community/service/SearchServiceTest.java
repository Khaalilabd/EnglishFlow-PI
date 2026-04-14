package com.englishflow.community.service;

import com.englishflow.community.dto.TopicDTO;
import com.englishflow.community.entity.SubCategory;
import com.englishflow.community.entity.Topic;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private SearchService searchService;

    private Topic topic1;
    private Topic topic2;
    private SubCategory subCategory;

    @BeforeEach
    void setUp() {
        subCategory = new SubCategory();
        subCategory.setId(1L);
        subCategory.setName("General");

        topic1 = new Topic();
        topic1.setId(1L);
        topic1.setTitle("Java Programming Basics");
        topic1.setContent("Learn Java programming from scratch");
        topic1.setUserId(100L);
        topic1.setUserName("John Doe");
        topic1.setSubCategory(subCategory);
        topic1.setViewsCount(50);
        topic1.setReactionsCount(10);
        topic1.setIsPinned(false);
        topic1.setIsLocked(false);
        topic1.setPosts(new ArrayList<>());
        topic1.setCreatedAt(LocalDateTime.now());
        topic1.setUpdatedAt(LocalDateTime.now());

        topic2 = new Topic();
        topic2.setId(2L);
        topic2.setTitle("JavaScript Fundamentals");
        topic2.setContent("Introduction to JavaScript");
        topic2.setUserId(101L);
        topic2.setUserName("Jane Smith");
        topic2.setSubCategory(subCategory);
        topic2.setViewsCount(30);
        topic2.setReactionsCount(5);
        topic2.setIsPinned(false);
        topic2.setIsLocked(false);
        topic2.setPosts(new ArrayList<>());
        topic2.setCreatedAt(LocalDateTime.now());
        topic2.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void searchTopics_WithKeyword_ShouldReturnMatchingTopics() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Topic> topicPage = new PageImpl<>(Arrays.asList(topic1, topic2));

        when(topicRepository.searchByKeyword(eq("Java"), any(Pageable.class))).thenReturn(topicPage);

        Page<TopicDTO> result = searchService.searchTopics("Java", pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Java Programming Basics", result.getContent().get(0).getTitle());
        assertEquals("JavaScript Fundamentals", result.getContent().get(1).getTitle());
        verify(topicRepository).searchByKeyword(eq("Java"), any(Pageable.class));
    }

    @Test
    void searchTopics_NoResults_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Topic> emptyPage = new PageImpl<>(new ArrayList<>());

        when(topicRepository.searchByKeyword(eq("NonExistent"), any(Pageable.class))).thenReturn(emptyPage);

        Page<TopicDTO> result = searchService.searchTopics("NonExistent", pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(topicRepository).searchByKeyword(eq("NonExistent"), any(Pageable.class));
    }

    @Test
    void searchTopics_ShouldConvertToDTO() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Topic> topicPage = new PageImpl<>(Arrays.asList(topic1));

        when(topicRepository.searchByKeyword(eq("Programming"), any(Pageable.class))).thenReturn(topicPage);

        Page<TopicDTO> result = searchService.searchTopics("Programming", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        TopicDTO dto = result.getContent().get(0);
        assertEquals(topic1.getId(), dto.getId());
        assertEquals(topic1.getTitle(), dto.getTitle());
        assertEquals(topic1.getContent(), dto.getContent());
        assertEquals(topic1.getUserId(), dto.getUserId());
        assertEquals(topic1.getUserName(), dto.getUserName());
        assertEquals(topic1.getSubCategory().getId(), dto.getSubCategoryId());
        assertEquals(topic1.getViewsCount(), dto.getViewsCount());
        assertEquals(topic1.getReactionsCount(), dto.getReactionsCount());
        assertEquals(topic1.getIsPinned(), dto.getIsPinned());
        assertEquals(topic1.getIsLocked(), dto.getIsLocked());
        assertEquals(topic1.getPosts().size(), dto.getPostsCount());
    }

    @Test
    void searchTopics_WithPagination_ShouldRespectPageable() {
        Pageable pageable = PageRequest.of(1, 10);
        // Create 10 topics for page 1 (second page)
        java.util.List<Topic> topics = new ArrayList<>();
        for (int i = 10; i < 20; i++) {
            Topic topic = new Topic();
            topic.setId((long) i);
            topic.setTitle("Test Topic " + i);
            topic.setContent("Content " + i);
            topic.setUserId(100L);
            topic.setUserName("User " + i);
            topic.setSubCategory(subCategory);
            topic.setViewsCount(0);
            topic.setReactionsCount(0);
            topic.setIsPinned(false);
            topic.setIsLocked(false);
            topic.setPosts(new ArrayList<>());
            topic.setCreatedAt(LocalDateTime.now());
            topic.setUpdatedAt(LocalDateTime.now());
            topics.add(topic);
        }
        // Page 1 with 10 items, total 25 items across all pages
        Page<Topic> topicPage = new PageImpl<>(topics, pageable, 25);

        when(topicRepository.searchByKeyword(eq("test"), any(Pageable.class))).thenReturn(topicPage);

        Page<TopicDTO> result = searchService.searchTopics("test", pageable);

        assertNotNull(result);
        assertEquals(10, result.getContent().size());
        assertEquals(25, result.getTotalElements());
        assertEquals(1, result.getNumber());
        verify(topicRepository).searchByKeyword(eq("test"), any(Pageable.class));
    }
}
