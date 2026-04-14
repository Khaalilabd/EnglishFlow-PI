package com.englishflow.community.controller;

import com.englishflow.community.dto.SaveVocabularyRequest;
import com.englishflow.community.dto.VocabularyStatsDTO;
import com.englishflow.community.dto.VocabularyWordDTO;
import com.englishflow.community.entity.VocabularyWord;
import com.englishflow.community.security.JwtAuthenticationFilter;
import com.englishflow.community.security.InternalServiceAuthenticationFilter;
import com.englishflow.community.service.VocabularyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = VocabularyController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, InternalServiceAuthenticationFilter.class}))
class VocabularyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VocabularyService vocabularyService;

    @Test
    @WithMockUser
    void saveWord_ShouldReturnSavedWord() throws Exception {
        SaveVocabularyRequest request = new SaveVocabularyRequest();
        request.setWord("eloquent");
        request.setDefinition("Fluent or persuasive in speaking or writing");

        VocabularyWordDTO saved = new VocabularyWordDTO(
                1L, "eloquent", "Fluent or persuasive in speaking or writing",
                null, "adjective", null, null, null, null, null,
                VocabularyWord.MasteryLevel.NEW, 0, null, LocalDateTime.now()
        );

        when(vocabularyService.saveWord(eq(100L), any(SaveVocabularyRequest.class))).thenReturn(saved);

        mockMvc.perform(post("/community/vocabulary")
                        .with(csrf())
                        .header("X-User-Id", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.word").value("eloquent"));

        verify(vocabularyService).saveWord(eq(100L), any(SaveVocabularyRequest.class));
    }

    @Test
    @WithMockUser
    void getUserVocabulary_ShouldReturnPagedWords() throws Exception {
        VocabularyWordDTO word1 = new VocabularyWordDTO(
                1L, "test", "definition", null, "noun", null, null, null, null, null,
                VocabularyWord.MasteryLevel.NEW, 0, null, LocalDateTime.now()
        );

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 20);
        Page<VocabularyWordDTO> page = new PageImpl<>(Arrays.asList(word1), pageable, 1);
        when(vocabularyService.getUserVocabulary(eq(100L), eq(0), eq(20), eq("createdAt")))
                .thenReturn(page);

        mockMvc.perform(get("/community/vocabulary")
                        .with(csrf())
                        .header("X-User-Id", "100")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "createdAt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].word").value("test"));

        verify(vocabularyService).getUserVocabulary(100L, 0, 20, "createdAt");
    }

    @Test
    @WithMockUser
    void getUserVocabulary_WithLevel_ShouldReturnFilteredWords() throws Exception {
        VocabularyWordDTO word = new VocabularyWordDTO(
                1L, "learning", "definition", null, "noun", null, null, null, null, null,
                VocabularyWord.MasteryLevel.LEARNING, 3, null, LocalDateTime.now()
        );

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 20);
        Page<VocabularyWordDTO> page = new PageImpl<>(Arrays.asList(word), pageable, 1);
        when(vocabularyService.getUserVocabularyByLevel(eq(100L), eq("LEARNING"), eq(0), eq(20), eq("createdAt")))
                .thenReturn(page);

        mockMvc.perform(get("/community/vocabulary")
                        .with(csrf())
                        .header("X-User-Id", "100")
                        .param("level", "LEARNING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].masteryLevel").value("LEARNING"));

        verify(vocabularyService).getUserVocabularyByLevel(100L, "LEARNING", 0, 20, "createdAt");
    }

    @Test
    @WithMockUser
    void searchVocabulary_ShouldReturnMatchingWords() throws Exception {
        VocabularyWordDTO word = new VocabularyWordDTO(
                1L, "eloquent", "definition", null, "adjective", null, null, null, null, null,
                VocabularyWord.MasteryLevel.NEW, 0, null, LocalDateTime.now()
        );

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 20);
        Page<VocabularyWordDTO> page = new PageImpl<>(Arrays.asList(word), pageable, 1);
        when(vocabularyService.searchVocabulary(eq(100L), eq("elo"), eq(0), eq(20)))
                .thenReturn(page);

        mockMvc.perform(get("/community/vocabulary/search")
                        .with(csrf())
                        .header("X-User-Id", "100")
                        .param("query", "elo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].word").value("eloquent"));

        verify(vocabularyService).searchVocabulary(100L, "elo", 0, 20);
    }

    @Test
    @WithMockUser
    void getStats_ShouldReturnVocabularyStats() throws Exception {
        VocabularyStatsDTO stats = new VocabularyStatsDTO(50L, 10L, 30L, 8L, 2L, 150L);
        when(vocabularyService.getUserStats(100L)).thenReturn(stats);

        mockMvc.perform(get("/community/vocabulary/stats")
                        .with(csrf())
                        .header("X-User-Id", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWords").value(50))
                .andExpect(jsonPath("$.newWords").value(10))
                .andExpect(jsonPath("$.learningWords").value(30));

        verify(vocabularyService).getUserStats(100L);
    }

    @Test
    @WithMockUser
    void markAsReviewed_ShouldReturnUpdatedWord() throws Exception {
        VocabularyWordDTO updated = new VocabularyWordDTO(
                1L, "test", "definition", null, "noun", null, null, null, null, null,
                VocabularyWord.MasteryLevel.LEARNING, 1, LocalDateTime.now(), LocalDateTime.now()
        );

        when(vocabularyService.markAsReviewed(100L, 1L)).thenReturn(updated);

        mockMvc.perform(put("/community/vocabulary/1/review")
                        .with(csrf())
                        .header("X-User-Id", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewCount").value(1))
                .andExpect(jsonPath("$.masteryLevel").value("LEARNING"));

        verify(vocabularyService).markAsReviewed(100L, 1L);
    }

    @Test
    @WithMockUser
    void deleteWord_ShouldReturnNoContent() throws Exception {
        doNothing().when(vocabularyService).deleteWord(100L, 1L);

        mockMvc.perform(delete("/community/vocabulary/1")
                        .with(csrf())
                        .header("X-User-Id", "100"))
                .andExpect(status().isNoContent());

        verify(vocabularyService).deleteWord(100L, 1L);
    }

    @Test
    @WithMockUser
    void isWordSaved_ShouldReturnBoolean() throws Exception {
        when(vocabularyService.isWordSaved(100L, "test")).thenReturn(true);

        mockMvc.perform(get("/community/vocabulary/check/test")
                        .with(csrf())
                        .header("X-User-Id", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(vocabularyService).isWordSaved(100L, "test");
    }

    @Test
    @WithMockUser
    void exportVocabulary_ShouldReturnAllWords() throws Exception {
        VocabularyWordDTO word1 = new VocabularyWordDTO(
                1L, "word1", "def1", null, "noun", null, null, null, null, null,
                VocabularyWord.MasteryLevel.NEW, 0, null, LocalDateTime.now()
        );
        VocabularyWordDTO word2 = new VocabularyWordDTO(
                2L, "word2", "def2", null, "verb", null, null, null, null, null,
                VocabularyWord.MasteryLevel.LEARNING, 5, null, LocalDateTime.now()
        );

        List<VocabularyWordDTO> allWords = Arrays.asList(word1, word2);
        when(vocabularyService.getAllUserVocabulary(100L)).thenReturn(allWords);

        mockMvc.perform(get("/community/vocabulary/export")
                        .with(csrf())
                        .header("X-User-Id", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(vocabularyService).getAllUserVocabulary(100L);
    }
}
