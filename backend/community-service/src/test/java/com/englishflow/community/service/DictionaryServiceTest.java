package com.englishflow.community.service;

import com.englishflow.community.dto.DictionaryResponse;
import com.englishflow.community.dto.EnrichedDictionaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DictionaryServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DictionaryService dictionaryService;

    private DictionaryResponse[] mockResponse;

    @BeforeEach
    void setUp() {
        DictionaryResponse response = new DictionaryResponse();
        response.setWord("hello");
        mockResponse = new DictionaryResponse[]{response};
    }

    @Test
    void lookupWord_ValidWord_ShouldReturnDefinition() {
        when(restTemplate.getForObject(anyString(), eq(DictionaryResponse[].class)))
                .thenReturn(mockResponse);

        DictionaryResponse[] result = dictionaryService.lookupWord("hello");

        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals("hello", result[0].getWord());
        verify(restTemplate).getForObject(anyString(), eq(DictionaryResponse[].class));
    }

    @Test
    void lookupWord_WordNotFound_ShouldThrowException() {
        when(restTemplate.getForObject(anyString(), eq(DictionaryResponse[].class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        assertThrows(RuntimeException.class, () -> {
            dictionaryService.lookupWord("nonexistentword123");
        });
    }

    @Test
    void lookupWord_ShouldConvertToLowerCase() {
        when(restTemplate.getForObject(contains("hello"), eq(DictionaryResponse[].class)))
                .thenReturn(mockResponse);

        DictionaryResponse[] result = dictionaryService.lookupWord("HELLO");

        assertNotNull(result);
        verify(restTemplate).getForObject(contains("hello"), eq(DictionaryResponse[].class));
    }

    @Test
    void lookupWord_EmptyResponse_ShouldThrowException() {
        when(restTemplate.getForObject(anyString(), eq(DictionaryResponse[].class)))
                .thenReturn(new DictionaryResponse[0]);

        assertThrows(RuntimeException.class, () -> {
            dictionaryService.lookupWord("test");
        });
    }

    @Test
    void lookupWordEnriched_ShouldReturnEnrichedData() {
        when(restTemplate.getForObject(anyString(), eq(DictionaryResponse[].class)))
                .thenReturn(mockResponse);

        EnrichedDictionaryResponse result = dictionaryService.lookupWordEnriched("hello", "Hello world");

        assertNotNull(result);
        assertNotNull(result.getBasicData());
        assertEquals("Hello world", result.getContext());
        assertNotNull(result.getSimilarWords());
        assertNotNull(result.getCommonConfusions());
    }

    @Test
    void lookupWordEnriched_WithoutContext_ShouldWork() {
        when(restTemplate.getForObject(anyString(), eq(DictionaryResponse[].class)))
                .thenReturn(mockResponse);

        EnrichedDictionaryResponse result = dictionaryService.lookupWordEnriched("hello", null);

        assertNotNull(result);
        assertNull(result.getContext());
    }

    @Test
    void lookupWordEnriched_ShouldIncludeCommonConfusions() {
        DictionaryResponse response = new DictionaryResponse();
        response.setWord("affect");
        DictionaryResponse[] affectResponse = new DictionaryResponse[]{response};

        when(restTemplate.getForObject(anyString(), eq(DictionaryResponse[].class)))
                .thenReturn(affectResponse);

        EnrichedDictionaryResponse result = dictionaryService.lookupWordEnriched("affect", null);

        assertNotNull(result);
        assertNotNull(result.getCommonConfusions());
        assertTrue(result.getCommonConfusions().contains("effect"));
    }

    @Test
    void lookupWordEnriched_ShouldIncludeSimilarWords() {
        DictionaryResponse response = new DictionaryResponse();
        response.setWord("right");
        DictionaryResponse[] rightResponse = new DictionaryResponse[]{response};

        when(restTemplate.getForObject(anyString(), eq(DictionaryResponse[].class)))
                .thenReturn(rightResponse);

        EnrichedDictionaryResponse result = dictionaryService.lookupWordEnriched("right", null);

        assertNotNull(result);
        assertNotNull(result.getSimilarWords());
        assertTrue(result.getSimilarWords().contains("write"));
    }
}
