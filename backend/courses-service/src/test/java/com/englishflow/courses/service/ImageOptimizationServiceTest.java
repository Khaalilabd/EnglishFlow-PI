package com.englishflow.courses.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageOptimizationServiceTest {

    @InjectMocks
    private ImageOptimizationService imageOptimizationService;

    private byte[] validImageBytes;

    @BeforeEach
    void setUp() throws IOException {
        // Create a simple test image
        BufferedImage testImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(testImage, "jpg", baos);
        validImageBytes = baos.toByteArray();
    }

    @Test
    void optimizeThumbnail_WithValidImage_ShouldReturnOptimizedBytes() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                validImageBytes
        );

        // Act
        byte[] result = imageOptimizationService.optimizeThumbnail(file);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void optimizeThumbnail_WithInvalidImage_ShouldThrowIOException() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "invalid image data".getBytes()
        );

        // Act & Assert
        assertThrows(IOException.class, () -> imageOptimizationService.optimizeThumbnail(file));
    }

    @Test
    void generateSmallThumbnail_WithValidImage_ShouldReturnThumbnailBytes() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                validImageBytes
        );

        // Act
        byte[] result = imageOptimizationService.generateSmallThumbnail(file);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void generateSmallThumbnail_WithInvalidImage_ShouldThrowIOException() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "invalid image data".getBytes()
        );

        // Act & Assert
        assertThrows(IOException.class, () -> imageOptimizationService.generateSmallThumbnail(file));
    }

    @Test
    void generateSmallThumbnailFromBytes_WithValidBytes_ShouldReturnThumbnailBytes() throws IOException {
        // Act
        byte[] result = imageOptimizationService.generateSmallThumbnailFromBytes(validImageBytes);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void generateSmallThumbnailFromBytes_WithInvalidBytes_ShouldThrowIOException() {
        // Arrange
        byte[] invalidBytes = "invalid image data".getBytes();

        // Act & Assert
        assertThrows(IOException.class, () -> 
            imageOptimizationService.generateSmallThumbnailFromBytes(invalidBytes));
    }

    @Test
    void isValidImage_WithValidJpegImage_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                validImageBytes
        );

        // Act
        boolean result = imageOptimizationService.isValidImage(file);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidImage_WithValidJpgImage_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpg",
                validImageBytes
        );

        // Act
        boolean result = imageOptimizationService.isValidImage(file);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidImage_WithValidPngImage_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                validImageBytes
        );

        // Act
        boolean result = imageOptimizationService.isValidImage(file);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidImage_WithValidGifImage_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.gif",
                "image/gif",
                validImageBytes
        );

        // Act
        boolean result = imageOptimizationService.isValidImage(file);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidImage_WithValidWebpImage_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.webp",
                "image/webp",
                validImageBytes
        );

        // Act
        boolean result = imageOptimizationService.isValidImage(file);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidImage_WithNullFile_ShouldReturnFalse() {
        // Act
        boolean result = imageOptimizationService.isValidImage(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidImage_WithEmptyFile_ShouldReturnFalse() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        // Act
        boolean result = imageOptimizationService.isValidImage(file);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidImage_WithNullContentType_ShouldReturnFalse() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                null,
                validImageBytes
        );

        // Act
        boolean result = imageOptimizationService.isValidImage(file);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidImage_WithInvalidContentType_ShouldReturnFalse() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                validImageBytes
        );

        // Act
        boolean result = imageOptimizationService.isValidImage(file);

        // Assert
        assertFalse(result);
    }
}
