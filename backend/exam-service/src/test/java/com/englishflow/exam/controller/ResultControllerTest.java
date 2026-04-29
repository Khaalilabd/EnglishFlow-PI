package com.englishflow.exam.controller;

import com.englishflow.exam.dto.response.ResultDTO;
import com.englishflow.exam.dto.response.ResultWithReviewDTO;
import com.englishflow.exam.enums.ExamLevel;
import com.englishflow.exam.service.IResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResultController.class)
@AutoConfigureMockMvc(addFilters = false)
class ResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IResultService resultService;

    private ResultDTO resultDTO;
    private ResultWithReviewDTO resultWithReview;

    @BeforeEach
    void setUp() {
        resultDTO = ResultDTO.builder()
                .id("result1")
                .userId(1L)
                .attemptId("attempt1")
                .level(ExamLevel.B1)
                .totalScore(80.0)
                .percentageScore(80.0)
                .passed(true)
                .cefrBand(ExamLevel.B1)
                .createdAt(LocalDateTime.now())
                .build();

        resultWithReview = ResultWithReviewDTO.builder()
                .id("result1")
                .userId(1L)
                .attemptId("attempt1")
                .level(ExamLevel.B1)
                .totalScore(80.0)
                .percentageScore(80.0)
                .passed(true)
                .cefrBand(ExamLevel.B1)
                .createdAt(LocalDateTime.now())
                .questionReviews(Arrays.asList())
                .build();
    }

    @Test
    void getResultByAttemptId_ValidRequest_ShouldReturnResult() throws Exception {
        when(resultService.getResultByAttemptId("attempt1", 1L)).thenReturn(resultDTO);

        mockMvc.perform(get("/exam-results/attempt/attempt1")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("result1"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.passed").value(true));

        verify(resultService).getResultByAttemptId("attempt1", 1L);
    }

    @Test
    void getResultWithReview_ValidRequest_ShouldReturnResultWithReview() throws Exception {
        when(resultService.getResultWithReview("attempt1", 1L)).thenReturn(resultWithReview);

        mockMvc.perform(get("/exam-results/attempt/attempt1/review")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("result1"))
                .andExpect(jsonPath("$.questionReviews").isArray());

        verify(resultService).getResultWithReview("attempt1", 1L);
    }

    @Test
    void getUserResults_ValidUserId_ShouldReturnResultList() throws Exception {
        List<ResultDTO> results = Arrays.asList(resultDTO);
        when(resultService.getUserResults(1L)).thenReturn(results);

        mockMvc.perform(get("/exam-results/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("result1"))
                .andExpect(jsonPath("$[0].userId").value(1));

        verify(resultService).getUserResults(1L);
    }

    @Test
    void getUserResults_NoResults_ShouldReturnEmptyList() throws Exception {
        when(resultService.getUserResults(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/exam-results/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(resultService).getUserResults(1L);
    }
}
