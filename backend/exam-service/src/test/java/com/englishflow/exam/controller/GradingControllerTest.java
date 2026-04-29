package com.englishflow.exam.controller;

import com.englishflow.exam.dto.request.ManualGradeDTO;
import com.englishflow.exam.dto.response.GradingQueueItemDTO;
import com.englishflow.exam.enums.QuestionType;
import com.englishflow.exam.service.IGradingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GradingController.class)
@AutoConfigureMockMvc(addFilters = false)
class GradingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IGradingService gradingService;

    private GradingQueueItemDTO queueItem;
    private ManualGradeDTO manualGradeDTO;

    @BeforeEach
    void setUp() {
        queueItem = GradingQueueItemDTO.builder()
                .answerId("ans1")
                .attemptId("attempt1")
                .userId(1L)
                .questionId("q1")
                .questionType(QuestionType.OPEN_WRITING)
                .prompt("Write an essay")
                .maxPoints(20.0)
                .submittedAt(LocalDateTime.now())
                .build();

        manualGradeDTO = new ManualGradeDTO();
        manualGradeDTO.setScore(18.0);
        manualGradeDTO.setFeedback("Excellent work");
    }

    @Test
    void getGradingQueue_ShouldReturnQueueItems() throws Exception {
        List<GradingQueueItemDTO> queue = Arrays.asList(queueItem);
        when(gradingService.getGradingQueue()).thenReturn(queue);

        mockMvc.perform(get("/grading/queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].answerId").value("ans1"))
                .andExpect(jsonPath("$[0].questionType").value("OPEN_WRITING"));

        verify(gradingService).getGradingQueue();
    }

    @Test
    void getPendingAttempts_ShouldReturnQueueItems() throws Exception {
        List<GradingQueueItemDTO> queue = Arrays.asList(queueItem);
        when(gradingService.getGradingQueue()).thenReturn(queue);

        mockMvc.perform(get("/grading/attempts/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].answerId").value("ans1"));

        verify(gradingService).getGradingQueue();
    }

    @Test
    void getAttemptGradingDetails_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/grading/attempts/attempt1/details"))
                .andExpect(status().isOk());
    }

    @Test
    void manualGradeAnswer_ValidRequest_ShouldReturnOk() throws Exception {
        doNothing().when(gradingService).manualGradeAnswer(eq("ans1"), eq(100L), any(ManualGradeDTO.class));

        mockMvc.perform(post("/grading/answers/ans1")
                .param("graderId", "100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(manualGradeDTO)))
                .andExpect(status().isOk());

        verify(gradingService).manualGradeAnswer(eq("ans1"), eq(100L), any(ManualGradeDTO.class));
    }

    @Test
    void finalizeAttemptGrading_ValidRequest_ShouldReturnOk() throws Exception {
        doNothing().when(gradingService).finalizeAttemptGrading("attempt1");

        mockMvc.perform(post("/grading/attempts/attempt1/finalize"))
                .andExpect(status().isOk());

        verify(gradingService).finalizeAttemptGrading("attempt1");
    }
}
