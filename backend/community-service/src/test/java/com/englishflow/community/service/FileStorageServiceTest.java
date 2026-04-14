package com.englishflow.community.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService(tempDir.toString());
        ReflectionTestUtils.setField(fileStorageService, "serverPort", "8082");
    }

    @Test
    void storeFile_ValidFile_ShouldReturnFilePath() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        String filePath = fileStorageService.storeFile(file);

        assertNotNull(filePath);
        assertTrue(filePath.startsWith("/uploads/"));
        assertTrue(filePath.endsWith(".jpg"));
    }

    @Test
    void storeFile_ShouldCreateUniqueFileName() {
        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "content1".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "content2".getBytes()
        );

        String filePath1 = fileStorageService.storeFile(file1);
        String filePath2 = fileStorageService.storeFile(file2);

        assertNotEquals(filePath1, filePath2);
    }

    @Test
    void storeFile_InvalidFileName_ShouldThrowException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "../../../etc/passwd",
                "text/plain",
                "malicious content".getBytes()
        );

        assertThrows(RuntimeException.class, () -> {
            fileStorageService.storeFile(file);
        });
    }

    @Test
    void storeFile_ShouldPreserveFileExtension() {
        MockMultipartFile pdfFile = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "pdf content".getBytes()
        );

        String filePath = fileStorageService.storeFile(pdfFile);

        assertTrue(filePath.endsWith(".pdf"));
    }

    @Test
    void deleteFile_ExistingFile_ShouldDeleteSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        String filePath = fileStorageService.storeFile(file);
        String fileName = filePath.substring("/uploads/".length());
        Path storedFile = tempDir.resolve(fileName);

        assertTrue(Files.exists(storedFile));

        fileStorageService.deleteFile(filePath);

        assertFalse(Files.exists(storedFile));
    }

    @Test
    void deleteFile_NonExistingFile_ShouldNotThrowException() {
        assertDoesNotThrow(() -> {
            fileStorageService.deleteFile("/uploads/nonexistent.jpg");
        });
    }

    @Test
    void deleteFile_NullPath_ShouldNotThrowException() {
        assertDoesNotThrow(() -> {
            fileStorageService.deleteFile(null);
        });
    }

    @Test
    void deleteFile_InvalidPath_ShouldNotThrowException() {
        assertDoesNotThrow(() -> {
            fileStorageService.deleteFile("/invalid/path.jpg");
        });
    }
}
