package com.englishflow.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageOptimizationServiceTest {

    @Mock
    private MultipartFile multipartFile;

    private ImageOptimizationService imageOptimizationService;

    @BeforeEach
    void setUp() {
        imageOptimizationService = new ImageOptimizationService();
    }

    @Test
    void isValidImage_WithJpegContentType_ShouldReturnTrue() {
        // Arrange
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        // Act
        boolean result = imageOptimizationService.isValidImage(multipartFile);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidImage_WithPngContentType_ShouldReturnTrue() {
        // Arrange
        when(multipartFile.getContentType()).thenReturn("image/png");

        // Act
        boolean result = imageOptimizationService.isValidImage(multipartFile);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidImage_WithWebpContentType_ShouldReturnTrue() {
        // Arrange
        when(multipartFile.getContentType()).thenReturn("image/webp");

        // Act
        boolean result = imageOptimizationService.isValidImage(multipartFile);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidImage_WithGifContentType_ShouldReturnTrue() {
        // Arrange
        when(multipartFile.getContentType()).thenReturn("image/gif");

        // Act
        boolean result = imageOptimizationService.isValidImage(multipartFile);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidImage_WithInvalidContentType_ShouldReturnFalse() {
        // Arrange
        when(multipartFile.getContentType()).thenReturn("application/pdf");

        // Act
        boolean result = imageOptimizationService.isValidImage(multipartFile);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidImage_WithNullContentType_ShouldReturnFalse() {
        // Arrange
        when(multipartFile.getContentType()).thenReturn(null);

        // Act
        boolean result = imageOptimizationService.isValidImage(multipartFile);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidImage_WithEmptyContentType_ShouldReturnFalse() {
        // Arrange
        when(multipartFile.getContentType()).thenReturn("");

        // Act
        boolean result = imageOptimizationService.isValidImage(multipartFile);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidImage_WithTextContentType_ShouldReturnFalse() {
        // Arrange
        when(multipartFile.getContentType()).thenReturn("text/plain");

        // Act
        boolean result = imageOptimizationService.isValidImage(multipartFile);

        // Assert
        assertFalse(result);
    }
}
