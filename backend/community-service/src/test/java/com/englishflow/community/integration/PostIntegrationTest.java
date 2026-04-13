package com.englishflow.community.integration;

import com.englishflow.community.dto.CreatePostRequest;
import com.englishflow.community.entity.Category;
import com.englishflow.community.entity.Post;
import com.englishflow.community.entity.SubCategory;
import com.englishflow.community.entity.Topic;
import com.englishflow.community.repository.CategoryRepository;
import com.englishflow.community.repository.PostRepository;
import com.englishflow.community.repository.SubCategoryRepository;
import com.englishflow.community.repository.TopicRepository;
import com.englishflow.community.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PostIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private String authToken;
    private Topic testTopic;
    private Long testUserId = 100L;

    @BeforeEach
    void setUp() {
        // Clean up
        postRepository.deleteAll();
        topicRepository.deleteAll();
        subCategoryRepository.deleteAll();
        categoryRepository.deleteAll();

        // Create test category
        Category category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Description");
        category.setIcon("test-icon");
        category.setColor("#FF0000");
        category = categoryRepository.save(category);

        // Create test subcategory
        SubCategory subCategory = new SubCategory();
        subCategory.setName("Test SubCategory");
        subCategory.setDescription("Test SubCategory Description");
        subCategory.setCategory(category);
        subCategory = subCategoryRepository.save(subCategory);

        // Create test topic
        testTopic = new Topic();
        testTopic.setTitle("Test Topic");
        testTopic.setContent("Test Content");
        testTopic.setUserId(testUserId);
        testTopic.setUserName("Test User");
        testTopic.setSubCategory(subCategory);
        testTopic.setIsLocked(false);
        testTopic = topicRepository.save(testTopic);

        // Generate JWT token
        authToken = generateToken(testUserId, "STUDENT", "test@example.com");
    }

    @Test
    void createPost_WithValidToken_ReturnsCreated() throws Exception {
        // Arrange
        CreatePostRequest request = new CreatePostRequest();
        request.setTopicId(testTopic.getId());
        request.setContent("This is a test post content");
        request.setUserId(testUserId);
        request.setUserName("Test User");

        // Act & Assert
        mockMvc.perform(post("/community/posts")
                .header("Authorization", "Bearer " + authToken)
                .header("X-User-Role", "STUDENT")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("This is a test post content"))
                .andExpect(jsonPath("$.userId").value(testUserId));
    }

    @Test
    void createPost_WithoutToken_ReturnsUnauthorized() throws Exception {
        // Arrange
        CreatePostRequest request = new CreatePostRequest();
        request.setTopicId(testTopic.getId());
        request.setContent("This is a test post content");
        request.setUserId(testUserId);
        request.setUserName("Test User");

        // Act & Assert
        mockMvc.perform(post("/community/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // Spring Security returns 403 when no token
    }

    @Test
    void getPostsByTopic_PublicEndpoint_ReturnsOk() throws Exception {
        // Arrange - Create a post first
        Post post = new Post();
        post.setContent("Test post");
        post.setUserId(testUserId);
        post.setUserName("Test User");
        post.setTopic(testTopic);
        postRepository.save(post);

        // Act & Assert
        mockMvc.perform(get("/community/posts/topic/" + testTopic.getId())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].content").value("Test post"));
    }

    @Test
    void deletePost_AsOwner_ReturnsNoContent() throws Exception {
        // Arrange - Create a post
        Post post = new Post();
        post.setContent("Test post to delete");
        post.setUserId(testUserId);
        post.setUserName("Test User");
        post.setTopic(testTopic);
        post = postRepository.save(post);

        // Act & Assert
        mockMvc.perform(delete("/community/posts/" + post.getId())
                .header("Authorization", "Bearer " + authToken)
                .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePost_AsNonOwner_ReturnsUnauthorized() throws Exception {
        // Arrange - Create a post owned by another user
        Post post = new Post();
        post.setContent("Test post");
        post.setUserId(999L); // Different user
        post.setUserName("Other User");
        post.setTopic(testTopic);
        post = postRepository.save(post);

        // Act & Assert
        mockMvc.perform(delete("/community/posts/" + post.getId())
                .header("Authorization", "Bearer " + authToken)
                .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isUnauthorized()); // Service throws UnauthorizedException which returns 401
    }

    @Test
    void updatePost_AsOwner_ReturnsUpdated() throws Exception {
        // Arrange - Create a post
        Post post = new Post();
        post.setContent("Original content");
        post.setUserId(testUserId);
        post.setUserName("Test User");
        post.setTopic(testTopic);
        post = postRepository.save(post);

        CreatePostRequest updateRequest = new CreatePostRequest();
        updateRequest.setTopicId(testTopic.getId());
        updateRequest.setContent("Updated content");
        updateRequest.setUserId(testUserId);
        updateRequest.setUserName("Test User");

        // Act & Assert
        mockMvc.perform(put("/community/posts/" + post.getId())
                .header("Authorization", "Bearer " + authToken)
                .header("X-User-Id", testUserId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    private String generateToken(Long userId, String role, String email) {
        Key signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(signingKey)
                .compact();
    }
}
