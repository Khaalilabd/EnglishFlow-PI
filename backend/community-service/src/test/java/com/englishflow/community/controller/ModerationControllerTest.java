package com.englishflow.community.controller;

import com.englishflow.community.dto.TopicDTO;
import com.englishflow.community.security.JwtAuthenticationFilter;
import com.englishflow.community.security.InternalServiceAuthenticationFilter;
import com.englishflow.community.service.CategoryService;
import com.englishflow.community.service.TopicService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ModerationController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, InternalServiceAuthenticationFilter.class}))
class ModerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TopicService topicService;

    @MockBean
    private CategoryService categoryService;

    @Test
    @WithMockUser
    void getAllTopicsForModeration_ShouldReturnPagedTopics() throws Exception {
        TopicDTO topic1 = new TopicDTO();
        topic1.setId(1L);
        topic1.setTitle("Topic 1");

        Pageable pageable = PageRequest.of(0, 20);
        Page<TopicDTO> page = new PageImpl<>(Arrays.asList(topic1), pageable, 1);
        when(topicService.getAllTopicsForModeration(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/community/moderation/topics")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(topicService).getAllTopicsForModeration(any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    @WithMockUser
    void bulkPinTopics_ShouldReturnSuccessResponse() throws Exception {
        List<Long> topicIds = Arrays.asList(1L, 2L, 3L);
        when(topicService.bulkPinTopics(topicIds)).thenReturn(3);

        mockMvc.perform(post("/community/moderation/topics/bulk-pin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(3));

        verify(topicService).bulkPinTopics(topicIds);
    }

    @Test
    @WithMockUser
    void bulkUnpinTopics_ShouldReturnSuccessResponse() throws Exception {
        List<Long> topicIds = Arrays.asList(1L, 2L);
        when(topicService.bulkUnpinTopics(topicIds)).thenReturn(2);

        mockMvc.perform(post("/community/moderation/topics/bulk-unpin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(2));

        verify(topicService).bulkUnpinTopics(topicIds);
    }

    @Test
    @WithMockUser
    void bulkLockTopics_ShouldReturnSuccessResponse() throws Exception {
        List<Long> topicIds = Arrays.asList(1L, 2L, 3L, 4L);
        when(topicService.bulkLockTopics(topicIds)).thenReturn(4);

        mockMvc.perform(post("/community/moderation/topics/bulk-lock")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(4));

        verify(topicService).bulkLockTopics(topicIds);
    }

    @Test
    @WithMockUser
    void bulkUnlockTopics_ShouldReturnSuccessResponse() throws Exception {
        List<Long> topicIds = Arrays.asList(1L);
        when(topicService.bulkUnlockTopics(topicIds)).thenReturn(1);

        mockMvc.perform(post("/community/moderation/topics/bulk-unlock")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1));

        verify(topicService).bulkUnlockTopics(topicIds);
    }

    @Test
    @WithMockUser
    void bulkDeleteTopics_ShouldReturnSuccessResponse() throws Exception {
        List<Long> topicIds = Arrays.asList(1L, 2L);
        when(topicService.bulkDeleteTopics(topicIds, 100L)).thenReturn(2);

        mockMvc.perform(post("/community/moderation/topics/bulk-delete")
                        .with(csrf())
                        .header("X-User-Id", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(2));

        verify(topicService).bulkDeleteTopics(topicIds, 100L);
    }

    @Test
    @WithMockUser
    void lockCategory_ShouldReturnSuccessResponse() throws Exception {
        doNothing().when(categoryService).lockCategory(1L, 100L);

        mockMvc.perform(put("/community/moderation/categories/1/lock")
                        .with(csrf())
                        .header("X-User-Id", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Category locked"));

        verify(categoryService).lockCategory(1L, 100L);
    }

    @Test
    @WithMockUser
    void unlockCategory_ShouldReturnSuccessResponse() throws Exception {
        doNothing().when(categoryService).unlockCategory(1L);

        mockMvc.perform(put("/community/moderation/categories/1/unlock")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Category unlocked"));

        verify(categoryService).unlockCategory(1L);
    }

    @Test
    @WithMockUser
    void getModerationStats_ShouldReturnStats() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTopics", 100);
        stats.put("pinnedTopics", 5);
        stats.put("lockedTopics", 3);

        when(topicService.getModerationStats()).thenReturn(stats);

        mockMvc.perform(get("/community/moderation/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTopics").value(100))
                .andExpect(jsonPath("$.pinnedTopics").value(5))
                .andExpect(jsonPath("$.lockedTopics").value(3));

        verify(topicService).getModerationStats();
    }
}
