package com.englishflow.courses.controller;

import com.englishflow.courses.dto.ChapterDTO;
import com.englishflow.courses.service.IChapterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChapterController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ChapterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IChapterService chapterService;

    private ChapterDTO chapterDTO;

    @BeforeEach
    void setUp() {
        chapterDTO = new ChapterDTO();
        chapterDTO.setId(1L);
        chapterDTO.setTitle("Chapter 1");
        chapterDTO.setDescription("First chapter");
        chapterDTO.setCourseId(1L);
        chapterDTO.setOrderIndex(1);
        chapterDTO.setIsPublished(true);
    }

    @Test
    void createChapter_ShouldReturnCreated() throws Exception {
        when(chapterService.createChapter(any(ChapterDTO.class))).thenReturn(chapterDTO);

        mockMvc.perform(post("/chapters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chapterDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Chapter 1"));

        verify(chapterService, times(1)).createChapter(any(ChapterDTO.class));
    }

    @Test
    void getChapterById_ShouldReturnChapter() throws Exception {
        when(chapterService.getChapterById(1L)).thenReturn(chapterDTO);

        mockMvc.perform(get("/chapters/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Chapter 1"));

        verify(chapterService, times(1)).getChapterById(1L);
    }

    @Test
    void getAllChapters_ShouldReturnAllChapters() throws Exception {
        List<ChapterDTO> chapters = Arrays.asList(chapterDTO);
        when(chapterService.getAllChapters()).thenReturn(chapters);

        mockMvc.perform(get("/chapters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(chapterService, times(1)).getAllChapters();
    }

    @Test
    void getChaptersByCourse_ShouldReturnChaptersByCourse() throws Exception {
        List<ChapterDTO> chapters = Arrays.asList(chapterDTO);
        when(chapterService.getChaptersByCourse(1L)).thenReturn(chapters);

        mockMvc.perform(get("/chapters/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseId").value(1));

        verify(chapterService, times(1)).getChaptersByCourse(1L);
    }

    @Test
    void getPublishedChaptersByCourse_ShouldReturnPublishedChapters() throws Exception {
        List<ChapterDTO> chapters = Arrays.asList(chapterDTO);
        when(chapterService.getPublishedChaptersByCourse(1L)).thenReturn(chapters);

        mockMvc.perform(get("/chapters/course/1/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isPublished").value(true));

        verify(chapterService, times(1)).getPublishedChaptersByCourse(1L);
    }

    @Test
    void updateChapter_ShouldReturnUpdatedChapter() throws Exception {
        when(chapterService.updateChapter(eq(1L), any(ChapterDTO.class))).thenReturn(chapterDTO);

        mockMvc.perform(put("/chapters/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chapterDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(chapterService, times(1)).updateChapter(eq(1L), any(ChapterDTO.class));
    }

    @Test
    void deleteChapter_ShouldReturnNoContent() throws Exception {
        doNothing().when(chapterService).deleteChapter(1L);

        mockMvc.perform(delete("/chapters/1"))
                .andExpect(status().isNoContent());

        verify(chapterService, times(1)).deleteChapter(1L);
    }

    @Test
    void chapterExists_ShouldReturnTrue() throws Exception {
        when(chapterService.existsById(1L)).thenReturn(true);

        mockMvc.perform(get("/chapters/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(chapterService, times(1)).existsById(1L);
    }

    @Test
    void chapterBelongsToCourse_ShouldReturnTrue() throws Exception {
        when(chapterService.belongsToCourse(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/chapters/1/belongs-to-course/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(chapterService, times(1)).belongsToCourse(1L, 1L);
    }
}
