package com.englishflow.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @Mock
    private ImageOptimizationService imageOptimizationService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", "uploads/test");
    }

    @Test
    void isValidImageFile_WhenValid_ShouldReturnTrue() {
        // Arrange
        when(imageOptimizationService.isValidImage(any())).thenReturn(true);

        // Act
        boolean result = fileStorageService.isValidImageFile(multipartFile);

        // Assert
        assertTrue(result);
        verify(imageOptimizationService).isValidImage(multipartFile);
    }

    @Test
    void isValidImageFile_WhenInvalid_ShouldReturnFalse() {
        // Arrange
        when(imageOptimizationService.isValidImage(any())).thenReturn(false);

        // Act
        boolean result = fileStorageService.isValidImageFile(multipartFile);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidFileSize_WhenUnderLimit_ShouldReturnTrue() {
        // Arrange
        when(multipartFile.getSize()).thenReturn(1024L); // 1KB
        long maxSize = 5 * 1024 * 1024; // 5MB

        // Act
        boolean result = fileStorageService.isValidFileSize(multipartFile, maxSize);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidFileSize_WhenOverLimit_ShouldReturnFalse() {
        // Arrange
        when(multipartFile.getSize()).thenReturn(10 * 1024 * 1024L); // 10MB
        long maxSize = 5 * 1024 * 1024; // 5MB

        // Act
        boolean result = fileStorageService.isValidFileSize(multipartFile, maxSize);

        // Assert
        assertFalse(result);
    }

    @Test
    void getThumbnailUrl_WhenValidUrl_ShouldReturnThumbnailUrl() {
        // Arrange
        String imageUrl = "/uploads/photo.jpg";

        // Act
        String result = fileStorageService.getThumbnailUrl(imageUrl);

        // Assert
        assertEquals("/uploads/thumb_photo.jpg", result);
    }

    @Test
    void getThumbnailUrl_WhenNullUrl_ShouldReturnNull() {
        // Act
        String result = fileStorageService.getThumbnailUrl(null);

        // Assert
        assertNull(result);
    }

    @Test
    void getThumbnailUrl_WhenNoSlash_ShouldReturnOriginal() {
        // Arrange
        String imageUrl = "photo.jpg";

        // Act
        String result = fileStorageService.getThumbnailUrl(imageUrl);

        // Assert
        assertEquals("photo.jpg", result);
    }

    @Test
    void deleteFile_WhenValidUrl_ShouldNotThrowException() {
        // Arrange
        String fileUrl = "/uploads/photo.jpg";

        // Act & Assert
        assertDoesNotThrow(() -> fileStorageService.deleteFile(fileUrl));
    }

    @Test
    void deleteFile_WhenNullUrl_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> fileStorageService.deleteFile(null));
    }
}
