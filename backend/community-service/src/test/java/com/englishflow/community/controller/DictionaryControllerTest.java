package com.englishflow.community.controller;

import com.englishflow.community.dto.DictionaryResponse;
import com.englishflow.community.dto.EnrichedDictionaryResponse;
import com.englishflow.community.security.InternalServiceAuthenticationFilter;
import com.englishflow.community.security.JwtAuthenticationFilter;
import com.englishflow.community.service.DictionaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = DictionaryController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, InternalServiceAuthenticationFilter.class}))
class DictionaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DictionaryService dictionaryService;

    @Test
    @WithMockUser
    void lookupWord_ShouldReturnDictionaryResponse() throws Exception {
        DictionaryResponse response = new DictionaryResponse();
        response.setWord("hello");
        DictionaryResponse[] responses = {response};

        when(dictionaryService.lookupWord("hello")).thenReturn(responses);

        mockMvc.perform(get("/community/dictionary/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].word").value("hello"));

        verify(dictionaryService).lookupWord("hello");
    }

    @Test
    @WithMockUser
    void lookupWordEnriched_WithoutContext_ShouldReturnEnrichedResponse() throws Exception {
        EnrichedDictionaryResponse response = new EnrichedDictionaryResponse();
        DictionaryResponse basicData = new DictionaryResponse();
        basicData.setWord("test");
        response.setBasicData(new DictionaryResponse[]{basicData});

        when(dictionaryService.lookupWordEnriched("test", null)).thenReturn(response);

        mockMvc.perform(get("/community/dictionary/enriched/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basicData").isArray());

        verify(dictionaryService).lookupWordEnriched("test", null);
    }

    @Test
    @WithMockUser
    void lookupWordEnriched_WithContext_ShouldReturnEnrichedResponse() throws Exception {
        EnrichedDictionaryResponse response = new EnrichedDictionaryResponse();
        DictionaryResponse basicData = new DictionaryResponse();
        basicData.setWord("run");
        response.setBasicData(new DictionaryResponse[]{basicData});
        response.setContext("I like to run in the morning");

        when(dictionaryService.lookupWordEnriched(eq("run"), eq("I like to run in the morning")))
                .thenReturn(response);

        mockMvc.perform(get("/community/dictionary/enriched/run")
                        .param("context", "I like to run in the morning"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basicData").isArray())
                .andExpect(jsonPath("$.context").value("I like to run in the morning"));

        verify(dictionaryService).lookupWordEnriched("run", "I like to run in the morning");
    }
}
