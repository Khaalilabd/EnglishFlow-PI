package com.englishflow.community.controller;

import com.englishflow.community.dto.CreatePostRequest;
import com.englishflow.community.dto.PostDTO;
import com.englishflow.community.entity.Category;
import com.englishflow.community.entity.SubCategory;
import com.englishflow.community.entity.Topic;
import com.englishflow.community.repository.TopicRepository;
import com.englishflow.community.security.JwtAuthenticationFilter;
import com.englishflow.community.security.InternalServiceAuthenticationFilter;
import com.englishflow.community.service.PermissionService;
import com.englishflow.community.service.PostService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PostController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, InternalServiceAuthenticationFilter.class}))
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private TopicRepository topicRepository;

    @Test
    @WithMockUser
    void createPost_WithPermission_ShouldReturnCreatedPost() throws Exception {
        CreatePostRequest request = new CreatePostRequest();
        request.setTopicId(1L);
        request.setContent("Test post content");
        request.setUserId(100L);
        request.setUserName("Test User");

        Topic topic = new Topic();
        topic.setId(1L);
        SubCategory subCategory = new SubCategory();
        subCategory.setId(1L);
        Category category = new Category();
        subCategory.setCategory(category);
        topic.setSubCategory(subCategory);

        PostDTO createdPost = new PostDTO();
        createdPost.setId(1L);
        createdPost.setContent("Test post content");

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(permissionService.canReplyToTopic(1L, "STUDENT")).thenReturn(true);
        when(postService.createPost(any(CreatePostRequest.class))).thenReturn(createdPost);

        mockMvc.perform(post("/community/posts")
                        .with(csrf())
                        .header("X-User-Role", "STUDENT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test post content"));

        verify(postService).createPost(any(CreatePostRequest.class));
    }

    @Test
    @WithMockUser
    void createPost_WithoutPermission_ShouldReturnForbidden() throws Exception {
        CreatePostRequest request = new CreatePostRequest();
        request.setTopicId(1L);
        request.setContent("Test post content");
        request.setUserId(100L);
        request.setUserName("Test User");

        Topic topic = new Topic();
        topic.setId(1L);
        SubCategory subCategory = new SubCategory();
        subCategory.setId(1L);
        Category category = new Category();
        subCategory.setCategory(category);
        topic.setSubCategory(subCategory);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(permissionService.canReplyToTopic(1L, "STUDENT")).thenReturn(false);

        mockMvc.perform(post("/community/posts")
                        .with(csrf())
                        .header("X-User-Role", "STUDENT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(postService, never()).createPost(any(CreatePostRequest.class));
    }

    @Test
    @WithMockUser
    void getPostsByTopic_ShouldReturnPagedPosts() throws Exception {
        PostDTO post1 = new PostDTO();
        post1.setId(1L);
        post1.setContent("Post 1");

        PostDTO post2 = new PostDTO();
        post2.setId(2L);
        post2.setContent("Post 2");

        Pageable pageable = PageRequest.of(0, 20);
        Page<PostDTO> page = new PageImpl<>(Arrays.asList(post1, post2), pageable, 2);
        when(postService.getPostsByTopic(eq(1L), eq("helpful"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/community/posts/topic/1")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "helpful"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(postService).getPostsByTopic(eq(1L), eq("helpful"), any(Pageable.class));
    }

    @Test
    @WithMockUser
    void updatePost_ShouldReturnUpdatedPost() throws Exception {
        CreatePostRequest request = new CreatePostRequest();
        request.setTopicId(1L);
        request.setContent("Updated content");
        request.setUserId(100L);
        request.setUserName("Test User");

        PostDTO updated = new PostDTO();
        updated.setId(1L);
        updated.setContent("Updated content");

        when(postService.updatePost(eq(1L), eq(100L), any(CreatePostRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/community/posts/1")
                        .with(csrf())
                        .header("X-User-Id", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Updated content"));

        verify(postService).updatePost(eq(1L), eq(100L), any(CreatePostRequest.class));
    }

    @Test
    @WithMockUser
    void deletePost_ShouldReturnNoContent() throws Exception {
        doNothing().when(postService).deletePost(1L, 100L);

        mockMvc.perform(delete("/community/posts/1")
                        .with(csrf())
                        .header("X-User-Id", "100"))
                .andExpect(status().isNoContent());

        verify(postService).deletePost(1L, 100L);
    }
}
