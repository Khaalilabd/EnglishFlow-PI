package com.englishflow.courses.controller;

import com.englishflow.courses.dto.CourseEnrollmentDTO;
import com.englishflow.courses.service.ICourseEnrollmentService;
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

@WebMvcTest(CourseEnrollmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class CourseEnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICourseEnrollmentService enrollmentService;

    private CourseEnrollmentDTO enrollmentDTO;

    @BeforeEach
    void setUp() {
        enrollmentDTO = new CourseEnrollmentDTO();
        enrollmentDTO.setId(1L);
        enrollmentDTO.setStudentId(1L);
        enrollmentDTO.setCourseId(1L);
        enrollmentDTO.setProgress(50.0);
        enrollmentDTO.setCompletedLessons(5);
        enrollmentDTO.setEnrolledAt(LocalDateTime.now());
    }

    @Test
    void enrollStudent_ShouldReturnCreated() throws Exception {
        when(enrollmentService.enrollStudent(1L, 1L)).thenReturn(enrollmentDTO);

        mockMvc.perform(post("/enrollments/enroll")
                .param("studentId", "1")
                .param("courseId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.studentId").value(1));

        verify(enrollmentService, times(1)).enrollStudent(1L, 1L);
    }

    @Test
    void unenrollStudent_ShouldReturnNoContent() throws Exception {
        doNothing().when(enrollmentService).unenrollStudent(1L, 1L);

        mockMvc.perform(delete("/enrollments/unenroll")
                .param("studentId", "1")
                .param("courseId", "1"))
                .andExpect(status().isNoContent());

        verify(enrollmentService, times(1)).unenrollStudent(1L, 1L);
    }

    @Test
    void getStudentEnrollments_ShouldReturnEnrollments() throws Exception {
        List<CourseEnrollmentDTO> enrollments = Arrays.asList(enrollmentDTO);
        when(enrollmentService.getStudentEnrollments(1L)).thenReturn(enrollments);

        mockMvc.perform(get("/enrollments/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(1));

        verify(enrollmentService, times(1)).getStudentEnrollments(1L);
    }

    @Test
    void getCourseEnrollments_ShouldReturnEnrollments() throws Exception {
        List<CourseEnrollmentDTO> enrollments = Arrays.asList(enrollmentDTO);
        when(enrollmentService.getCourseEnrollments(1L)).thenReturn(enrollments);

        mockMvc.perform(get("/enrollments/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseId").value(1));

        verify(enrollmentService, times(1)).getCourseEnrollments(1L);
    }

    @Test
    void isStudentEnrolled_ShouldReturnTrue() throws Exception {
        when(enrollmentService.isStudentEnrolled(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/enrollments/check")
                .param("studentId", "1")
                .param("courseId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(enrollmentService, times(1)).isStudentEnrolled(1L, 1L);
    }

    @Test
    void updateProgress_ShouldReturnUpdatedEnrollment() throws Exception {
        when(enrollmentService.updateProgress(1L, 1L, 75.0, 10)).thenReturn(enrollmentDTO);

        mockMvc.perform(put("/enrollments/progress")
                .param("studentId", "1")
                .param("courseId", "1")
                .param("progress", "75.0")
                .param("completedLessons", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(enrollmentService, times(1)).updateProgress(1L, 1L, 75.0, 10);
    }

    @Test
    void getEnrollment_ShouldReturnEnrollment() throws Exception {
        when(enrollmentService.getEnrollment(1L, 1L)).thenReturn(enrollmentDTO);

        mockMvc.perform(get("/enrollments/details")
                .param("studentId", "1")
                .param("courseId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(enrollmentService, times(1)).getEnrollment(1L, 1L);
    }

    @Test
    void getCourseEnrollmentCount_ShouldReturnCount() throws Exception {
        when(enrollmentService.getCourseEnrollmentCount(1L)).thenReturn(10L);

        mockMvc.perform(get("/enrollments/course/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));

        verify(enrollmentService, times(1)).getCourseEnrollmentCount(1L);
    }

    @Test
    void calculateAndUpdateProgress_ShouldReturnUpdatedEnrollment() throws Exception {
        when(enrollmentService.calculateAndUpdateProgress(1L, 1L)).thenReturn(enrollmentDTO);

        mockMvc.perform(put("/enrollments/calculate-progress")
                .param("studentId", "1")
                .param("courseId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(enrollmentService, times(1)).calculateAndUpdateProgress(1L, 1L);
    }
}
