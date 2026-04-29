package com.englishflow.courses.controller;

import com.englishflow.courses.dto.LessonDTO;
import com.englishflow.courses.enums.LessonType;
import com.englishflow.courses.service.ILessonService;
import com.englishflow.courses.service.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LessonController.class)
@AutoConfigureMockMvc(addFilters = false)
class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ILessonService lessonService;

    @MockBean
    private FileStorageService fileStorageService;

    private LessonDTO lessonDTO;

    @BeforeEach
    void setUp() {
        lessonDTO = new LessonDTO();
        lessonDTO.setId(1L);
        lessonDTO.setTitle("Lesson 1");
        lessonDTO.setDescription("First lesson");
        lessonDTO.setChapterId(1L);
        lessonDTO.setLessonType(LessonType.VIDEO);
        lessonDTO.setIsPublished(true);
    }

    @Test
    void createLesson_ShouldReturnCreated() throws Exception {
        when(lessonService.createLesson(any(LessonDTO.class))).thenReturn(lessonDTO);

        mockMvc.perform(post("/lessons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lessonDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Lesson 1"));

        verify(lessonService, times(1)).createLesson(any(LessonDTO.class));
    }

    @Test
    void getLessonById_ShouldReturnLesson() throws Exception {
        when(lessonService.getLessonById(1L)).thenReturn(lessonDTO);

        mockMvc.perform(get("/lessons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Lesson 1"));

        verify(lessonService, times(1)).getLessonById(1L);
    }

    @Test
    void getAllLessons_ShouldReturnAllLessons() throws Exception {
        List<LessonDTO> lessons = Arrays.asList(lessonDTO);
        when(lessonService.getAllLessons()).thenReturn(lessons);

        mockMvc.perform(get("/lessons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(lessonService, times(1)).getAllLessons();
    }

    @Test
    void getLessonsByChapter_ShouldReturnLessonsByChapter() throws Exception {
        List<LessonDTO> lessons = Arrays.asList(lessonDTO);
        when(lessonService.getLessonsByChapter(1L)).thenReturn(lessons);

        mockMvc.perform(get("/lessons/chapter/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].chapterId").value(1));

        verify(lessonService, times(1)).getLessonsByChapter(1L);
    }

    @Test
    void getPublishedLessonsByChapter_ShouldReturnPublishedLessons() throws Exception {
        List<LessonDTO> lessons = Arrays.asList(lessonDTO);
        when(lessonService.getPublishedLessonsByChapter(1L)).thenReturn(lessons);

        mockMvc.perform(get("/lessons/chapter/1/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isPublished").value(true));

        verify(lessonService, times(1)).getPublishedLessonsByChapter(1L);
    }

    @Test
    void getLessonsByCourse_ShouldReturnLessonsByCourse() throws Exception {
        List<LessonDTO> lessons = Arrays.asList(lessonDTO);
        when(lessonService.getLessonsByCourse(1L)).thenReturn(lessons);

        mockMvc.perform(get("/lessons/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(lessonService, times(1)).getLessonsByCourse(1L);
    }

    @Test
    void getLessonsByType_ShouldReturnLessonsByType() throws Exception {
        List<LessonDTO> lessons = Arrays.asList(lessonDTO);
        when(lessonService.getLessonsByType(LessonType.VIDEO)).thenReturn(lessons);

        mockMvc.perform(get("/lessons/type/VIDEO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lessonType").value("VIDEO"));

        verify(lessonService, times(1)).getLessonsByType(LessonType.VIDEO);
    }

    @Test
    void getPreviewLessonsByCourse_ShouldReturnPreviewLessons() throws Exception {
        List<LessonDTO> lessons = Arrays.asList(lessonDTO);
        when(lessonService.getPreviewLessonsByCourse(1L)).thenReturn(lessons);

        mockMvc.perform(get("/lessons/course/1/preview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(lessonService, times(1)).getPreviewLessonsByCourse(1L);
    }

    @Test
    void updateLesson_ShouldReturnUpdatedLesson() throws Exception {
        when(lessonService.updateLesson(eq(1L), any(LessonDTO.class))).thenReturn(lessonDTO);

        mockMvc.perform(put("/lessons/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lessonDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(lessonService, times(1)).updateLesson(eq(1L), any(LessonDTO.class));
    }

    @Test
    void deleteLesson_ShouldReturnNoContent() throws Exception {
        doNothing().when(lessonService).deleteLesson(1L);

        mockMvc.perform(delete("/lessons/1"))
                .andExpect(status().isNoContent());

        verify(lessonService, times(1)).deleteLesson(1L);
    }

    @Test
    void lessonExists_ShouldReturnTrue() throws Exception {
        when(lessonService.existsById(1L)).thenReturn(true);

        mockMvc.perform(get("/lessons/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(lessonService, times(1)).existsById(1L);
    }

    @Test
    void lessonBelongsToChapter_ShouldReturnTrue() throws Exception {
        when(lessonService.belongsToChapter(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/lessons/1/belongs-to-chapter/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(lessonService, times(1)).belongsToChapter(1L, 1L);
    }

    @Test
    void lessonBelongsToCourse_ShouldReturnTrue() throws Exception {
        when(lessonService.belongsToCourse(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/lessons/1/belongs-to-course/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(lessonService, times(1)).belongsToCourse(1L, 1L);
    }

    @Test
    void uploadVideo_WithValidFile_ShouldReturnSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "video.mp4", "video/mp4", "test video".getBytes());
        
        when(fileStorageService.isValidFileSize(any(), anyLong())).thenReturn(true);
        when(lessonService.getLessonById(1L)).thenReturn(lessonDTO);
        when(fileStorageService.storeFile(any(), anyString())).thenReturn("/uploads/video.mp4");
        when(lessonService.updateLesson(eq(1L), any())).thenReturn(lessonDTO);

        mockMvc.perform(multipart("/lessons/1/upload-video")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("/uploads/video.mp4"));

        verify(fileStorageService, times(1)).storeFile(any(), eq("lessons/videos"));
    }

    @Test
    void uploadVideo_WithEmptyFile_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "video.mp4", "video/mp4", new byte[0]);

        mockMvc.perform(multipart("/lessons/1/upload-video")
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Please select a file to upload"));
    }

    @Test
    void uploadVideo_WithInvalidType_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "test".getBytes());

        mockMvc.perform(multipart("/lessons/1/upload-video")
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Only video files are allowed"));
    }

    @Test
    void uploadDocument_WithValidFile_ShouldReturnSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "test pdf".getBytes());
        
        when(fileStorageService.isValidCourseMaterial(any())).thenReturn(true);
        when(fileStorageService.isValidFileSize(any(), anyLong())).thenReturn(true);
        when(lessonService.getLessonById(1L)).thenReturn(lessonDTO);
        when(fileStorageService.storeFile(any(), anyString())).thenReturn("/uploads/document.pdf");
        when(lessonService.updateLesson(eq(1L), any())).thenReturn(lessonDTO);

        mockMvc.perform(multipart("/lessons/1/upload-document")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("/uploads/document.pdf"));

        verify(fileStorageService, times(1)).storeFile(any(), eq("lessons/documents"));
    }

    @Test
    void deleteContentFile_ShouldReturnSuccess() throws Exception {
        lessonDTO.setContentUrl("/uploads/video.mp4");
        when(lessonService.getLessonById(1L)).thenReturn(lessonDTO);
        doNothing().when(fileStorageService).deleteFile(anyString());
        when(lessonService.updateLesson(eq(1L), any())).thenReturn(lessonDTO);

        mockMvc.perform(delete("/lessons/1/content-file"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Content file deleted successfully"));

        verify(fileStorageService, times(1)).deleteFile(anyString());
    }
}
