package com.englishflow.community.controller;

import com.englishflow.community.dto.TopicDTO;
import com.englishflow.community.security.JwtAuthenticationFilter;
import com.englishflow.community.security.InternalServiceAuthenticationFilter;
import com.englishflow.community.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = SearchController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, InternalServiceAuthenticationFilter.class}))
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @Test
    @WithMockUser
    void searchTopics_ShouldReturnPagedResults() throws Exception {
        TopicDTO topic1 = new TopicDTO();
        topic1.setId(1L);
        topic1.setTitle("Java Programming");

        TopicDTO topic2 = new TopicDTO();
        topic2.setId(2L);
        topic2.setTitle("JavaScript Basics");

        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        Page<TopicDTO> page = new PageImpl<>(Arrays.asList(topic1, topic2), pageable, 2);
        when(searchService.searchTopics(eq("Java"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/community/search/topics")
                        .with(csrf())
                        .param("keyword", "Java")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Java Programming"))
                .andExpect(jsonPath("$.content[1].title").value("JavaScript Basics"));

        verify(searchService).searchTopics(eq("Java"), any(Pageable.class));
    }

    @Test
    @WithMockUser
    void searchTopics_NoResults_ShouldReturnEmptyPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        Page<TopicDTO> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);
        when(searchService.searchTopics(eq("NonExistent"), any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/community/search/topics")
                        .with(csrf())
                        .param("keyword", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));

        verify(searchService).searchTopics(eq("NonExistent"), any(Pageable.class));
    }

    @Test
    @WithMockUser
    void searchTopics_WithPagination_ShouldReturnCorrectPage() throws Exception {
        TopicDTO topic = new TopicDTO();
        topic.setId(3L);
        topic.setTitle("Advanced Topic");

        Pageable pageable = PageRequest.of(1, 10, Sort.by("createdAt").descending());
        Page<TopicDTO> page = new PageImpl<>(Arrays.asList(topic), pageable, 1);
        when(searchService.searchTopics(eq("Advanced"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/community/search/topics")
                        .with(csrf())
                        .param("keyword", "Advanced")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(3));

        verify(searchService).searchTopics(eq("Advanced"), any(Pageable.class));
    }
}
