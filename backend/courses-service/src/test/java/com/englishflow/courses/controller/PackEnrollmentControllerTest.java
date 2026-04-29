package com.englishflow.courses.controller;

import com.englishflow.courses.dto.PackEnrollmentDTO;
import com.englishflow.courses.service.IPackEnrollmentService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PackEnrollmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PackEnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPackEnrollmentService enrollmentService;

    private PackEnrollmentDTO enrollmentDTO;

    @BeforeEach
    void setUp() {
        enrollmentDTO = new PackEnrollmentDTO();
        enrollmentDTO.setId(1L);
        enrollmentDTO.setStudentId(1L);
        enrollmentDTO.setPackId(1L);
        enrollmentDTO.setProgressPercentage(50);
        enrollmentDTO.setEnrolledAt(LocalDateTime.now());
    }

    @Test
    void enrollStudent_ShouldReturnCreated() throws Exception {
        when(enrollmentService.enrollStudent(1L, 1L)).thenReturn(enrollmentDTO);

        mockMvc.perform(post("/pack-enrollments")
                .param("studentId", "1")
                .param("packId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.studentId").value(1));

        verify(enrollmentService, times(1)).enrollStudent(1L, 1L);
    }

    @Test
    void enrollStudent_WhenError_ShouldReturnBadRequest() throws Exception {
        when(enrollmentService.enrollStudent(1L, 1L)).thenThrow(new RuntimeException("Already enrolled"));

        mockMvc.perform(post("/pack-enrollments")
                .param("studentId", "1")
                .param("packId", "1"))
                .andExpect(status().isBadRequest());

        verify(enrollmentService, times(1)).enrollStudent(1L, 1L);
    }

    @Test
    void getById_ShouldReturnEnrollment() throws Exception {
        when(enrollmentService.getById(1L)).thenReturn(enrollmentDTO);

        mockMvc.perform(get("/pack-enrollments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(enrollmentService, times(1)).getById(1L);
    }

    @Test
    void getByStudentId_ShouldReturnEnrollments() throws Exception {
        List<PackEnrollmentDTO> enrollments = Arrays.asList(enrollmentDTO);
        when(enrollmentService.getByStudentId(1L)).thenReturn(enrollments);

        mockMvc.perform(get("/pack-enrollments/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(1));

        verify(enrollmentService, times(1)).getByStudentId(1L);
    }

    @Test
    void getActiveEnrollmentsByStudent_ShouldReturnActiveEnrollments() throws Exception {
        List<PackEnrollmentDTO> enrollments = Arrays.asList(enrollmentDTO);
        when(enrollmentService.getActiveEnrollmentsByStudent(1L)).thenReturn(enrollments);

        mockMvc.perform(get("/pack-enrollments/student/1/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(enrollmentService, times(1)).getActiveEnrollmentsByStudent(1L);
    }

    @Test
    void getByPackId_ShouldReturnEnrollments() throws Exception {
        List<PackEnrollmentDTO> enrollments = Arrays.asList(enrollmentDTO);
        when(enrollmentService.getByPackId(1L)).thenReturn(enrollments);

        mockMvc.perform(get("/pack-enrollments/pack/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].packId").value(1));

        verify(enrollmentService, times(1)).getByPackId(1L);
    }

    @Test
    void getByTutorId_ShouldReturnEnrollments() throws Exception {
        List<PackEnrollmentDTO> enrollments = Arrays.asList(enrollmentDTO);
        when(enrollmentService.getByTutorId(1L)).thenReturn(enrollments);

        mockMvc.perform(get("/pack-enrollments/tutor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(enrollmentService, times(1)).getByTutorId(1L);
    }

    @Test
    void updateProgress_ShouldReturnUpdatedEnrollment() throws Exception {
        when(enrollmentService.updateProgress(1L, 75)).thenReturn(enrollmentDTO);

        mockMvc.perform(put("/pack-enrollments/1/progress")
                .param("progressPercentage", "75"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(enrollmentService, times(1)).updateProgress(1L, 75);
    }

    @Test
    void completeEnrollment_ShouldReturnOk() throws Exception {
        doNothing().when(enrollmentService).completeEnrollment(1L);

        mockMvc.perform(put("/pack-enrollments/1/complete"))
                .andExpect(status().isOk());

        verify(enrollmentService, times(1)).completeEnrollment(1L);
    }

    @Test
    void cancelEnrollment_ShouldReturnNoContent() throws Exception {
        doNothing().when(enrollmentService).cancelEnrollment(1L);

        mockMvc.perform(delete("/pack-enrollments/1"))
                .andExpect(status().isNoContent());

        verify(enrollmentService, times(1)).cancelEnrollment(1L);
    }

    @Test
    void isStudentEnrolled_ShouldReturnTrue() throws Exception {
        when(enrollmentService.isStudentEnrolled(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/pack-enrollments/check")
                .param("studentId", "1")
                .param("packId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(enrollmentService, times(1)).isStudentEnrolled(1L, 1L);
    }
}
