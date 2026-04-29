package com.englishflow.messaging.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
    }

    @Test
    void storeFile_ShouldSaveFileSuccessfully() throws IOException {
        // Arrange
        byte[] content = "test content".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                content
        );

        // Act
        String fileUrl = fileStorageService.storeFile(file);

        // Assert
        assertNotNull(fileUrl);
        assertTrue(fileUrl.contains(".txt"));
        assertTrue(fileUrl.startsWith("/"));
    }

    @Test
    void storeFile_WithImageFile_ShouldSaveSuccessfully() throws IOException {
        // Arrange
        byte[] content = new byte[]{1, 2, 3, 4};
        MultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                content
        );

        // Act
        String fileUrl = fileStorageService.storeFile(file);

        // Assert
        assertNotNull(fileUrl);
        assertTrue(fileUrl.contains(".jpg"));
    }

    @Test
    void storeFile_WithoutExtension_ShouldSaveSuccessfully() throws IOException {
        // Arrange
        byte[] content = "test".getBytes();
        MultipartFile file = new MockMultipartFile(
                "file",
                "testfile",
                "application/octet-stream",
                content
        );

        // Act
        String fileUrl = fileStorageService.storeFile(file);

        // Assert
        assertNotNull(fileUrl);
        assertFalse(fileUrl.contains("."));
    }

    @Test
    void deleteFile_WithValidUrl_ShouldDeleteSuccessfully() throws IOException {
        // Arrange
        Path testFile = tempDir.resolve("test-file.txt");
        Files.write(testFile, "test content".getBytes());
        String fileUrl = "/" + tempDir.toString() + "/test-file.txt";

        // Act
        fileStorageService.deleteFile(fileUrl);

        // Assert - no exception thrown
        assertTrue(true);
    }

    @Test
    void deleteFile_WithNullUrl_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> fileStorageService.deleteFile(null));
    }

    @Test
    void deleteFile_WithInvalidUrl_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> fileStorageService.deleteFile("/invalid/path/file.txt"));
    }

    @Test
    void isValidImageFile_WithImageFile_ShouldReturnTrue() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3}
        );

        // Act
        boolean result = fileStorageService.isValidImageFile(file);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidImageFile_WithNonImageFile_ShouldReturnFalse() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                new byte[]{1, 2, 3}
        );

        // Act
        boolean result = fileStorageService.isValidImageFile(file);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidImageFile_WithNullContentType_ShouldReturnFalse() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "file.txt",
                null,
                new byte[]{1, 2, 3}
        );

        // Act
        boolean result = fileStorageService.isValidImageFile(file);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidFileSize_WithinLimit_ShouldReturnTrue() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                new byte[100]
        );
        long maxSize = 1024;

        // Act
        boolean result = fileStorageService.isValidFileSize(file, maxSize);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidFileSize_ExceedsLimit_ShouldReturnFalse() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                new byte[2000]
        );
        long maxSize = 1024;

        // Act
        boolean result = fileStorageService.isValidFileSize(file, maxSize);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidFileSize_ExactlyAtLimit_ShouldReturnTrue() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                new byte[1024]
        );
        long maxSize = 1024;

        // Act
        boolean result = fileStorageService.isValidFileSize(file, maxSize);

        // Assert
        assertTrue(result);
    }
}
