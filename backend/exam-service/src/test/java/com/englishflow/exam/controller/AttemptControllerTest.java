package com.englishflow.exam.controller;

import com.englishflow.exam.dto.request.AnswerItemDTO;
import com.englishflow.exam.dto.request.SaveAnswersDTO;
import com.englishflow.exam.dto.response.AttemptDTO;
import com.englishflow.exam.dto.response.AttemptWithExamDTO;
import com.englishflow.exam.enums.AttemptStatus;
import com.englishflow.exam.enums.ExamLevel;
import com.englishflow.exam.service.IAttemptService;
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

@WebMvcTest(AttemptController.class)
@AutoConfigureMockMvc(addFilters = false)
class AttemptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IAttemptService attemptService;

    private AttemptWithExamDTO attemptWithExam;
    private AttemptDTO attemptDTO;
    private SaveAnswersDTO saveAnswersDTO;

    @BeforeEach
    void setUp() {
        attemptWithExam = AttemptWithExamDTO.builder()
                .id("attempt1")
                .userId(1L)
                .status(AttemptStatus.STARTED)
                .startedAt(LocalDateTime.now())
                .build();

        attemptDTO = AttemptDTO.builder()
                .id("attempt1")
                .userId(1L)
                .status(AttemptStatus.SUBMITTED)
                .startedAt(LocalDateTime.now())
                .submittedAt(LocalDateTime.now())
                .build();

        // Create a valid AnswerItemDTO
        AnswerItemDTO answerItem = new AnswerItemDTO();
        answerItem.setQuestionId("q1");
        try {
            answerItem.setAnswerData(new ObjectMapper().readTree("\"answer\""));
        } catch (Exception e) {
            // Ignore
        }
        
        saveAnswersDTO = new SaveAnswersDTO();
        saveAnswersDTO.setAnswers(Arrays.asList(answerItem));
    }

    @Test
    void startExam_ValidRequest_ShouldReturnCreated() throws Exception {
        when(attemptService.startExam(1L, ExamLevel.B1)).thenReturn(attemptWithExam);

        mockMvc.perform(post("/exam-attempts/start")
                .param("userId", "1")
                .param("level", "B1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("attempt1"))
                .andExpect(jsonPath("$.userId").value(1));

        verify(attemptService).startExam(1L, ExamLevel.B1);
    }

    @Test
    void getAttempt_ValidRequest_ShouldReturnAttempt() throws Exception {
        when(attemptService.getAttempt("attempt1", 1L)).thenReturn(attemptWithExam);

        mockMvc.perform(get("/exam-attempts/attempt1")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("attempt1"));

        verify(attemptService).getAttempt("attempt1", 1L);
    }

    @Test
    void saveAnswers_ValidRequest_ShouldReturnOk() throws Exception {
        doNothing().when(attemptService).saveAnswers(eq("attempt1"), eq(1L), any(SaveAnswersDTO.class));

        mockMvc.perform(post("/exam-attempts/attempt1/answers")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saveAnswersDTO)))
                .andExpect(status().isOk());

        verify(attemptService).saveAnswers(eq("attempt1"), eq(1L), any(SaveAnswersDTO.class));
    }

    @Test
    void submitExam_ValidRequest_ShouldReturnAttempt() throws Exception {
        when(attemptService.submitExam("attempt1", 1L)).thenReturn(attemptDTO);

        mockMvc.perform(post("/exam-attempts/attempt1/submit")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("attempt1"))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));

        verify(attemptService).submitExam("attempt1", 1L);
    }

    @Test
    void getUserAttempts_ValidUserId_ShouldReturnAttemptList() throws Exception {
        List<AttemptDTO> attempts = Arrays.asList(attemptDTO);
        when(attemptService.getUserAttempts(1L)).thenReturn(attempts);

        mockMvc.perform(get("/exam-attempts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("attempt1"));

        verify(attemptService).getUserAttempts(1L);
    }

    @Test
    void getAllSubmittedAttempts_ShouldReturnAttemptList() throws Exception {
        List<AttemptDTO> attempts = Arrays.asList(attemptDTO);
        when(attemptService.getAllSubmittedAttempts()).thenReturn(attempts);

        mockMvc.perform(get("/exam-attempts/submitted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SUBMITTED"));

        verify(attemptService).getAllSubmittedAttempts();
    }

    @Test
    void getAttemptsByStatus_ValidStatus_ShouldReturnAttemptList() throws Exception {
        List<AttemptDTO> attempts = Arrays.asList(attemptDTO);
        when(attemptService.getAttemptsByStatus("SUBMITTED")).thenReturn(attempts);

        mockMvc.perform(get("/exam-attempts/status/SUBMITTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SUBMITTED"));

        verify(attemptService).getAttemptsByStatus("SUBMITTED");
    }

    @Test
    void deleteAttempt_ValidRequest_ShouldReturnNoContent() throws Exception {
        doNothing().when(attemptService).deleteAttempt("attempt1", 1L);

        mockMvc.perform(delete("/exam-attempts/attempt1")
                .param("userId", "1"))
                .andExpect(status().isNoContent());

        verify(attemptService).deleteAttempt("attempt1", 1L);
    }
}
