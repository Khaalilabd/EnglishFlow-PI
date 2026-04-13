package com.jungle.learning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jungle.learning.dto.QuizDTO;
import com.jungle.learning.model.Quiz;
import com.jungle.learning.repository.QuizRepository;
import com.jungle.learning.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class QuizControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;
    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        quizRepository.deleteAll();

        // Generate JWT token for testing
        jwtToken = jwtUtil.generateToken("testuser", "ROLE_USER", 1L);

        // Create test quiz
        testQuiz = new Quiz();
        testQuiz.setTitle("Integration Test Quiz");
        testQuiz.setDescription("Test Description");
        testQuiz.setCourseId(1L);
        testQuiz.setDurationMinutes(30);
        testQuiz.setMaxScore(100);
        testQuiz.setPassingScore(60);
        testQuiz.setPublished(true);
        testQuiz.setCategory("Grammar");
        testQuiz.setDifficulty("medium");
        testQuiz.setCreatedAt(LocalDateTime.now());
        testQuiz = quizRepository.save(testQuiz);
    }

    @Test
    void getAllQuizzes_WithValidToken_ShouldReturnQuizzes() throws Exception {
        mockMvc.perform(get("/learning/quizzes")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Integration Test Quiz")));
    }

    @Test
    void getAllQuizzes_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/learning/quizzes"))
                .andExpect(status().isForbidden()); // Spring Security returns 403 for missing auth
    }

    @Test
    void getPublishedQuizzes_ShouldReturnOnlyPublished() throws Exception {
        // Create unpublished quiz
        Quiz unpublished = new Quiz();
        unpublished.setTitle("Unpublished Quiz");
        unpublished.setDescription("Test");
        unpublished.setCourseId(1L);
        unpublished.setDurationMinutes(30);
        unpublished.setMaxScore(100);
        unpublished.setPassingScore(60);
        unpublished.setPublished(false);
        quizRepository.save(unpublished);

        mockMvc.perform(get("/learning/quizzes/published")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].published", is(true)));
    }

    @Test
    void getQuizById_WithValidId_ShouldReturnQuiz() throws Exception {
        mockMvc.perform(get("/learning/quizzes/" + testQuiz.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Integration Test Quiz")))
                .andExpect(jsonPath("$.category", is("Grammar")));
    }

    @Test
    void getQuizById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/learning/quizzes/999")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void createQuiz_WithValidData_ShouldCreateQuiz() throws Exception {
        QuizDTO newQuiz = new QuizDTO();
        newQuiz.setTitle("New Quiz");
        newQuiz.setDescription("New Description");
        newQuiz.setCourseId(1L);
        newQuiz.setDurationMin(45);
        newQuiz.setMaxScore(100);
        newQuiz.setPassingScore(70);
        newQuiz.setPublished(false);
        newQuiz.setCategory("Vocabulary");
        newQuiz.setDifficulty("hard");

        mockMvc.perform(post("/learning/quizzes")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newQuiz)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Quiz")))
                .andExpect(jsonPath("$.category", is("Vocabulary")));
    }

    @Test
    void updateQuiz_WithValidData_ShouldUpdateQuiz() throws Exception {
        QuizDTO updateDTO = new QuizDTO();
        updateDTO.setTitle("Updated Quiz");
        updateDTO.setDescription("Updated Description");
        updateDTO.setCourseId(1L);
        updateDTO.setDurationMin(60);
        updateDTO.setMaxScore(100);
        updateDTO.setPassingScore(75);
        updateDTO.setPublished(true);
        updateDTO.setCategory("Reading");
        updateDTO.setDifficulty("easy");

        mockMvc.perform(put("/learning/quizzes/" + testQuiz.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Quiz")))
                .andExpect(jsonPath("$.category", is("Reading")));
    }

    @Test
    void deleteQuiz_WithValidId_ShouldDeleteQuiz() throws Exception {
        mockMvc.perform(delete("/learning/quizzes/" + testQuiz.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/learning/quizzes/" + testQuiz.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getQuizzesByCategory_ShouldReturnFilteredQuizzes() throws Exception {
        mockMvc.perform(get("/learning/quizzes/category/Grammar")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category", is("Grammar")));
    }

    @Test
    void getQuizzesByDifficulty_ShouldReturnFilteredQuizzes() throws Exception {
        mockMvc.perform(get("/learning/quizzes/difficulty/medium")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].difficulty", is("medium")));
    }
}
