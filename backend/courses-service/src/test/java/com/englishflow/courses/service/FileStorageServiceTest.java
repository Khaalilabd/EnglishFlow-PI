package com.englishflow.courses.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @Mock
    private ImageOptimizationService imageOptimizationService;

    @InjectMocks
    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    private byte[] validImageBytes;

    @BeforeEach
    void setUp() throws IOException {
        // Set upload directory to temp directory
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(fileStorageService, "serverPort", "8086");

        // Create a simple test image
        BufferedImage testImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(testImage, "jpg", baos);
        validImageBytes = baos.toByteArray();
    }

    @Test
    void storeFile_WithValidFile_ShouldReturnFileUrl() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        // Act
        String result = fileStorageService.storeFile(file, "materials");

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("/uploads/courses/materials/"));
        assertTrue(result.endsWith(".pdf"));
    }

    @Test
    void storeFile_WithOptimization_ShouldOptimizeAndStoreImage() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                validImageBytes
        );

        when(imageOptimizationService.isValidImage(file)).thenReturn(true);
        when(imageOptimizationService.optimizeThumbnail(file)).thenReturn(validImageBytes);
        when(imageOptimizationService.generateSmallThumbnailFromBytes(any())).thenReturn(validImageBytes);

        // Act
        String result = fileStorageService.storeFile(file, "thumbnails", true);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("/uploads/courses/thumbnails/"));
        verify(imageOptimizationService).optimizeThumbnail(file);
        verify(imageOptimizationService).generateSmallThumbnailFromBytes(any());
    }

    @Test
    void storeFile_WithoutOptimization_ShouldStoreFileDirectly() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        // Act
        String result = fileStorageService.storeFile(file, "materials", false);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("/uploads/courses/materials/"));
        verifyNoInteractions(imageOptimizationService);
    }

    @Test
    void storeFile_WithIOException_ShouldThrowRuntimeException() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                validImageBytes
        );

        when(imageOptimizationService.isValidImage(file)).thenReturn(true);
        when(imageOptimizationService.optimizeThumbnail(file)).thenThrow(new IOException("Test exception"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            fileStorageService.storeFile(file, "thumbnails", true));
    }

    @Test
    void storeThumbnail_ShouldStoreWithOptimization() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                validImageBytes
        );

        when(imageOptimizationService.isValidImage(file)).thenReturn(true);
        when(imageOptimizationService.optimizeThumbnail(file)).thenReturn(validImageBytes);
        when(imageOptimizationService.generateSmallThumbnailFromBytes(any())).thenReturn(validImageBytes);

        // Act
        String result = fileStorageService.storeThumbnail(file);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("/thumbnails/"));
        verify(imageOptimizationService).optimizeThumbnail(file);
    }

    @Test
    void storeCourseMaterial_ShouldStoreWithoutOptimization() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        // Act
        String result = fileStorageService.storeCourseMaterial(file);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("/materials/"));
        verifyNoInteractions(imageOptimizationService);
    }

    @Test
    void deleteFile_WithValidUrl_ShouldNotThrowException() {
        // Arrange
        String fileUrl = "/uploads/courses/thumbnails/test.jpg";

        // Act & Assert
        assertDoesNotThrow(() -> fileStorageService.deleteFile(fileUrl));
    }

    @Test
    void deleteFile_WithNullUrl_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> fileStorageService.deleteFile(null));
    }

    @Test
    void deleteFile_WithEmptyUrl_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> fileStorageService.deleteFile(""));
    }

    @Test
    void deleteFile_WithNonExistentFile_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> fileStorageService.deleteFile("/non/existent/file.jpg"));
    }

    @Test
    void getSmallThumbnailUrl_WithValidUrl_ShouldReturnSmallThumbnailUrl() {
        // Arrange
        String imageUrl = "/uploads/courses/thumbnails/test.jpg";

        // Act
        String result = fileStorageService.getSmallThumbnailUrl(imageUrl);

        // Assert
        assertEquals("/uploads/courses/thumbnails/small_test.jpg", result);
    }

    @Test
    void getSmallThumbnailUrl_WithNullUrl_ShouldReturnNull() {
        // Act
        String result = fileStorageService.getSmallThumbnailUrl(null);

        // Assert
        assertNull(result);
    }

    @Test
    void getSmallThumbnailUrl_WithUrlWithoutSlash_ShouldReturnOriginalUrl() {
        // Arrange
        String imageUrl = "test.jpg";

        // Act
        String result = fileStorageService.getSmallThumbnailUrl(imageUrl);

        // Assert
        assertEquals(imageUrl, result);
    }

    @Test
    void isValidImageFile_WithValidImage_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                validImageBytes
        );

        when(imageOptimizationService.isValidImage(file)).thenReturn(true);

        // Act
        boolean result = fileStorageService.isValidImageFile(file);

        // Assert
        assertTrue(result);
        verify(imageOptimizationService).isValidImage(file);
    }

    @Test
    void isValidImageFile_WithInvalidImage_ShouldReturnFalse() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );

        when(imageOptimizationService.isValidImage(file)).thenReturn(false);

        // Act
        boolean result = fileStorageService.isValidImageFile(file);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidCourseMaterial_WithPdf_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        // Act
        boolean result = fileStorageService.isValidCourseMaterial(file);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidCourseMaterial_WithDocx_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "test content".getBytes()
        );

        // Act
        boolean result = fileStorageService.isValidCourseMaterial(file);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidCourseMaterial_WithPptx_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pptx",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "test content".getBytes()
        );

        // Act
        boolean result = fileStorageService.isValidCourseMaterial(file);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidCourseMaterial_WithMp4_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.mp4",
                "video/mp4",
                "test content".getBytes()
        );

        // Act
        boolean result = fileStorageService.isValidCourseMaterial(file);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidCourseMaterial_WithMp3_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.mp3",
                "audio/mpeg",
                "test content".getBytes()
        );

        // Act
        boolean result = fileStorageService.isValidCourseMaterial(file);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidCourseMaterial_WithZip_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.zip",
                "application/zip",
                "test content".getBytes()
        );

        // Act
        boolean result = fileStorageService.isValidCourseMaterial(file);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidCourseMaterial_WithInvalidType_ShouldReturnFalse() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );

        // Act
        boolean result = fileStorageService.isValidCourseMaterial(file);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidCourseMaterial_WithNullContentType_ShouldReturnFalse() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                null,
                "test content".getBytes()
        );

        // Act
        boolean result = fileStorageService.isValidCourseMaterial(file);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidFileSize_WithValidSize_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        // Act
        boolean result = fileStorageService.isValidFileSize(file, 1024 * 1024); // 1MB

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidFileSize_WithExceedingSize_ShouldReturnFalse() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                new byte[1024 * 1024] // 1MB
        );

        // Act
        boolean result = fileStorageService.isValidFileSize(file, 512 * 1024); // 512KB

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidFileSize_WithExactSize_ShouldReturnTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                new byte[1024] // 1KB
        );

        // Act
        boolean result = fileStorageService.isValidFileSize(file, 1024); // 1KB

        // Assert
        assertTrue(result);
    }
}
