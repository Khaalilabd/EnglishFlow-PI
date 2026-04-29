package com.englishflow.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TranslationService translationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(translationService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(translationService, "apiUrl", "https://libretranslate.com");
        ReflectionTestUtils.setField(translationService, "apiKey", "test-api-key");
    }

    @Test
    void translate_ShouldReturnTranslatedText_WhenSuccessful() {
        // Arrange
        String text = "Hello";
        String targetLang = "fr";
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("translatedText", "Bonjour");
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        String result = translationService.translate(text, targetLang);

        // Assert
        assertEquals("Bonjour", result);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    void translate_ShouldReturnNull_WhenResponseBodyIsNull() {
        // Arrange
        String text = "Hello";
        String targetLang = "fr";
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        String result = translationService.translate(text, targetLang);

        // Assert
        assertNull(result);
    }

    @Test
    void translate_ShouldReturnNull_WhenTranslatedTextNotInResponse() {
        // Arrange
        String text = "Hello";
        String targetLang = "fr";
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", "Translation failed");
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        String result = translationService.translate(text, targetLang);

        // Assert
        assertNull(result);
    }

    @Test
    void translate_ShouldReturnNull_WhenExceptionOccurs() {
        // Arrange
        String text = "Hello";
        String targetLang = "fr";
        
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenThrow(new RuntimeException("Network error"));

        // Act
        String result = translationService.translate(text, targetLang);

        // Assert
        assertNull(result);
    }

    @Test
    void translate_ShouldUseCorrectApiUrl() {
        // Arrange
        String text = "Hello";
        String targetLang = "fr";
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("translatedText", "Bonjour");
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        translationService.translate(text, targetLang);

        // Assert
        verify(restTemplate).exchange(eq("https://libretranslate.com/translate"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    void translate_ShouldIncludeApiKeyInRequestBody() {
        // Arrange
        String text = "Hello";
        String targetLang = "fr";
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("translatedText", "Bonjour");
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        translationService.translate(text, targetLang);

        // Assert
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), argThat(entity -> {
            @SuppressWarnings("unchecked")
            Map<String, String> body = (Map<String, String>) entity.getBody();
            return body != null && "test-api-key".equals(body.get("api_key"));
        }), eq(Map.class));
    }

    @Test
    void translate_ShouldIncludeTextInRequestBody() {
        // Arrange
        String text = "Hello World";
        String targetLang = "fr";
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("translatedText", "Bonjour le monde");
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        translationService.translate(text, targetLang);

        // Assert
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), argThat(entity -> {
            @SuppressWarnings("unchecked")
            Map<String, String> body = (Map<String, String>) entity.getBody();
            return body != null && "Hello World".equals(body.get("q"));
        }), eq(Map.class));
    }

    @Test
    void translate_ShouldIncludeTargetLangInRequestBody() {
        // Arrange
        String text = "Hello";
        String targetLang = "es";
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("translatedText", "Hola");
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        translationService.translate(text, targetLang);

        // Assert
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), argThat(entity -> {
            @SuppressWarnings("unchecked")
            Map<String, String> body = (Map<String, String>) entity.getBody();
            return body != null && "es".equals(body.get("target"));
        }), eq(Map.class));
    }

    @Test
    void translate_ShouldSetSourceToAuto() {
        // Arrange
        String text = "Hello";
        String targetLang = "fr";
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("translatedText", "Bonjour");
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        translationService.translate(text, targetLang);

        // Assert
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), argThat(entity -> {
            @SuppressWarnings("unchecked")
            Map<String, String> body = (Map<String, String>) entity.getBody();
            return body != null && "auto".equals(body.get("source"));
        }), eq(Map.class));
    }

    @Test
    void translate_ShouldSetContentTypeToJson() {
        // Arrange
        String text = "Hello";
        String targetLang = "fr";
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("translatedText", "Bonjour");
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        translationService.translate(text, targetLang);

        // Assert
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), argThat(entity -> {
            HttpHeaders headers = entity.getHeaders();
            return MediaType.APPLICATION_JSON.equals(headers.getContentType());
        }), eq(Map.class));
    }
}
