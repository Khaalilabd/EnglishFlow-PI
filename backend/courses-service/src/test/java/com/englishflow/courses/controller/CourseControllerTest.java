package com.englishflow.courses.controller;

import com.englishflow.courses.dto.CourseDTO;
import com.englishflow.courses.enums.CourseStatus;
import com.englishflow.courses.service.ICourseService;
import com.englishflow.courses.service.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICourseService courseService;

    @MockBean
    private FileStorageService fileStorageService;

    private CourseDTO courseDTO;

    @BeforeEach
    void setUp() {
        courseDTO = new CourseDTO();
        courseDTO.setId(1L);
        courseDTO.setTitle("English Course");
        courseDTO.setDescription("Learn English");
        courseDTO.setLevel("Beginner");
        courseDTO.setStatus(CourseStatus.PUBLISHED);
        courseDTO.setTutorId(1L);
    }

    @Test
    void createCourse_ShouldReturnCreated() throws Exception {
        when(courseService.createCourse(any(CourseDTO.class))).thenReturn(courseDTO);

        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("English Course"));

        verify(courseService, times(1)).createCourse(any(CourseDTO.class));
    }

    @Test
    void getCourseById_ShouldReturnCourse() throws Exception {
        when(courseService.getCourseById(1L)).thenReturn(courseDTO);

        mockMvc.perform(get("/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("English Course"));

        verify(courseService, times(1)).getCourseById(1L);
    }

    @Test
    void getAllCourses_ShouldReturnPagedCourses() throws Exception {
        List<CourseDTO> courses = Arrays.asList(courseDTO);
        Page<CourseDTO> page = new PageImpl<>(courses, PageRequest.of(0, 20), 1);
        
        when(courseService.getAllCoursesPaginated(any())).thenReturn(page);

        mockMvc.perform(get("/courses")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(courseService, times(1)).getAllCoursesPaginated(any());
    }

    @Test
    void getAllCoursesNoPagination_ShouldReturnAllCourses() throws Exception {
        List<CourseDTO> courses = Arrays.asList(courseDTO);
        when(courseService.getAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/courses/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    void getPublishedCourses_ShouldReturnPublishedCourses() throws Exception {
        List<CourseDTO> courses = Arrays.asList(courseDTO);
        when(courseService.getPublishedCourses()).thenReturn(courses);

        mockMvc.perform(get("/courses/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PUBLISHED"));

        verify(courseService, times(1)).getPublishedCourses();
    }

    @Test
    void getCoursesByStatus_ShouldReturnCoursesByStatus() throws Exception {
        List<CourseDTO> courses = Arrays.asList(courseDTO);
        when(courseService.getCoursesByStatus(CourseStatus.PUBLISHED)).thenReturn(courses);

        mockMvc.perform(get("/courses/status/PUBLISHED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PUBLISHED"));

        verify(courseService, times(1)).getCoursesByStatus(CourseStatus.PUBLISHED);
    }

    @Test
    void getCoursesByLevel_ShouldReturnCoursesByLevel() throws Exception {
        List<CourseDTO> courses = Arrays.asList(courseDTO);
        when(courseService.getCoursesByLevel("Beginner")).thenReturn(courses);

        mockMvc.perform(get("/courses/level/Beginner"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].level").value("Beginner"));

        verify(courseService, times(1)).getCoursesByLevel("Beginner");
    }

    @Test
    void updateCourse_ShouldReturnUpdatedCourse() throws Exception {
        when(courseService.updateCourse(eq(1L), any(CourseDTO.class))).thenReturn(courseDTO);

        mockMvc.perform(put("/courses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(courseService, times(1)).updateCourse(eq(1L), any(CourseDTO.class));
    }

    @Test
    void deleteCourse_ShouldReturnNoContent() throws Exception {
        doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/courses/1"))
                .andExpect(status().isNoContent());

        verify(courseService, times(1)).deleteCourse(1L);
    }

    @Test
    void getCoursesByTutor_ShouldReturnCoursesByTutor() throws Exception {
        List<CourseDTO> courses = Arrays.asList(courseDTO);
        when(courseService.getCoursesByTutor(1L)).thenReturn(courses);

        mockMvc.perform(get("/courses/tutor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tutorId").value(1));

        verify(courseService, times(1)).getCoursesByTutor(1L);
    }

    @Test
    void courseExists_ShouldReturnTrue() throws Exception {
        when(courseService.existsById(1L)).thenReturn(true);

        mockMvc.perform(get("/courses/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(courseService, times(1)).existsById(1L);
    }

    @Test
    void uploadThumbnail_WithValidFile_ShouldReturnSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "thumbnail.jpg", "image/jpeg", "test image".getBytes());
        
        when(fileStorageService.isValidImageFile(any())).thenReturn(true);
        when(fileStorageService.isValidFileSize(any(), anyLong())).thenReturn(true);
        when(courseService.getCourseById(1L)).thenReturn(courseDTO);
        when(fileStorageService.storeThumbnail(any())).thenReturn("/uploads/thumbnail.jpg");
        when(courseService.updateCourse(eq(1L), any())).thenReturn(courseDTO);

        mockMvc.perform(multipart("/courses/1/upload-thumbnail")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thumbnailUrl").value("/uploads/thumbnail.jpg"));

        verify(fileStorageService, times(1)).storeThumbnail(any());
    }

    @Test
    void uploadThumbnail_WithEmptyFile_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "thumbnail.jpg", "image/jpeg", new byte[0]);

        mockMvc.perform(multipart("/courses/1/upload-thumbnail")
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Please select a file"));
    }

    @Test
    void uploadCourseMaterial_WithValidFile_ShouldReturnSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "material.pdf", "application/pdf", "test pdf".getBytes());
        
        when(fileStorageService.isValidCourseMaterial(any())).thenReturn(true);
        when(fileStorageService.isValidFileSize(any(), anyLong())).thenReturn(true);
        when(courseService.getCourseById(1L)).thenReturn(courseDTO);
        when(fileStorageService.storeCourseMaterial(any())).thenReturn("/uploads/material.pdf");
        when(courseService.updateCourse(eq(1L), any())).thenReturn(courseDTO);

        mockMvc.perform(multipart("/courses/1/upload-material")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileUrl").value("/uploads/material.pdf"));

        verify(fileStorageService, times(1)).storeCourseMaterial(any());
    }

    @Test
    void deleteThumbnail_ShouldReturnSuccess() throws Exception {
        courseDTO.setThumbnailUrl("/uploads/thumbnail.jpg");
        when(courseService.getCourseById(1L)).thenReturn(courseDTO);
        doNothing().when(fileStorageService).deleteFile(anyString());
        when(courseService.updateCourse(eq(1L), any())).thenReturn(courseDTO);

        mockMvc.perform(delete("/courses/1/thumbnail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Thumbnail deleted successfully"));

        verify(fileStorageService, times(1)).deleteFile(anyString());
    }
}
