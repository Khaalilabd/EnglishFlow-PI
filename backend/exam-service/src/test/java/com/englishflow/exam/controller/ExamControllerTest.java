package com.englishflow.exam.controller;

import com.englishflow.exam.dto.request.CreateExamDTO;
import com.englishflow.exam.dto.request.UpdateExamDTO;
import com.englishflow.exam.dto.response.ExamDetailDTO;
import com.englishflow.exam.dto.response.ExamSummaryDTO;
import com.englishflow.exam.enums.ExamLevel;
import com.englishflow.exam.service.IExamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExamController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IExamService examService;

    private ExamSummaryDTO examSummary;
    private ExamDetailDTO examDetail;
    private CreateExamDTO createDTO;
    private UpdateExamDTO updateDTO;

    @BeforeEach
    void setUp() {
        examSummary = ExamSummaryDTO.builder()
                .id("exam1")
                .title("Test Exam")
                .level(ExamLevel.B1)
                .totalDuration(120)
                .isPublished(true)
                .build();

        examDetail = ExamDetailDTO.builder()
                .id("exam1")
                .title("Test Exam")
                .level(ExamLevel.B1)
                .totalDuration(120)
                .isPublished(true)
                .build();

        createDTO = new CreateExamDTO();
        createDTO.setTitle("New Exam");
        createDTO.setLevel(ExamLevel.B1);
        createDTO.setTotalDuration(120);
        createDTO.setPassingScore(70.0);

        updateDTO = new UpdateExamDTO();
        updateDTO.setTitle("Updated Exam");
    }

    @Test
    void createExam_ValidDTO_ShouldReturnCreated() throws Exception {
        when(examService.createExam(any(CreateExamDTO.class))).thenReturn(examSummary);

        mockMvc.perform(post("/exams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("exam1"))
                .andExpect(jsonPath("$.title").value("Test Exam"));

        verify(examService).createExam(any(CreateExamDTO.class));
    }

    @Test
    void getAllExams_ShouldReturnExamList() throws Exception {
        List<ExamSummaryDTO> exams = Arrays.asList(examSummary);
        when(examService.getAllExams()).thenReturn(exams);

        mockMvc.perform(get("/exams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("exam1"));

        verify(examService).getAllExams();
    }

    @Test
    void getPublishedExams_WithoutLevel_ShouldReturnPublishedExams() throws Exception {
        List<ExamSummaryDTO> exams = Arrays.asList(examSummary);
        when(examService.getPublishedExams(null)).thenReturn(exams);

        mockMvc.perform(get("/exams/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("exam1"));

        verify(examService).getPublishedExams(null);
    }

    @Test
    void getPublishedExams_WithLevel_ShouldReturnFilteredExams() throws Exception {
        List<ExamSummaryDTO> exams = Arrays.asList(examSummary);
        when(examService.getPublishedExams(ExamLevel.B1)).thenReturn(exams);

        mockMvc.perform(get("/exams/published")
                .param("level", "B1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].level").value("B1"));

        verify(examService).getPublishedExams(ExamLevel.B1);
    }

    @Test
    void getExamById_ValidId_ShouldReturnExam() throws Exception {
        when(examService.getExamById("exam1")).thenReturn(examDetail);

        mockMvc.perform(get("/exams/exam1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("exam1"))
                .andExpect(jsonPath("$.title").value("Test Exam"));

        verify(examService).getExamById("exam1");
    }

    @Test
    void updateExam_ValidDTO_ShouldReturnUpdatedExam() throws Exception {
        when(examService.updateExam(eq("exam1"), any(UpdateExamDTO.class))).thenReturn(examSummary);

        mockMvc.perform(put("/exams/exam1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("exam1"));

        verify(examService).updateExam(eq("exam1"), any(UpdateExamDTO.class));
    }

    @Test
    void deleteExam_ValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(examService).deleteExam("exam1");

        mockMvc.perform(delete("/exams/exam1"))
                .andExpect(status().isNoContent());

        verify(examService).deleteExam("exam1");
    }

    @Test
    void publishExam_ValidId_ShouldReturnOk() throws Exception {
        doNothing().when(examService).publishExam("exam1");

        mockMvc.perform(put("/exams/exam1/publish"))
                .andExpect(status().isOk());

        verify(examService).publishExam("exam1");
    }

    @Test
    void unpublishExam_ValidId_ShouldReturnOk() throws Exception {
        doNothing().when(examService).unpublishExam("exam1");

        mockMvc.perform(put("/exams/exam1/unpublish"))
                .andExpect(status().isOk());

        verify(examService).unpublishExam("exam1");
    }
}
