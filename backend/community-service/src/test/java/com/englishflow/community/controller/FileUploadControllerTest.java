package com.englishflow.community.controller;

import com.englishflow.community.security.InternalServiceAuthenticationFilter;
import com.englishflow.community.security.JwtAuthenticationFilter;
import com.englishflow.community.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = FileUploadController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, InternalServiceAuthenticationFilter.class}))
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    @WithMockUser
    void uploadFile_ValidImage_ShouldReturnFilePath() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(fileStorageService.storeFile(any())).thenReturn("/uploads/test-uuid.jpg");

        mockMvc.perform(multipart("/community/files/upload").file(file).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filePath").value("/uploads/test-uuid.jpg"))
                .andExpect(jsonPath("$.fileName").value("test.jpg"))
                .andExpect(jsonPath("$.fileType").value("image/jpeg"));

        verify(fileStorageService).storeFile(any());
    }

    @Test
    @WithMockUser
    void uploadFile_ValidPdf_ShouldReturnFilePath() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "test pdf content".getBytes()
        );

        when(fileStorageService.storeFile(any())).thenReturn("/uploads/doc-uuid.pdf");

        mockMvc.perform(multipart("/community/files/upload").file(file).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filePath").value("/uploads/doc-uuid.pdf"))
                .andExpect(jsonPath("$.fileName").value("document.pdf"))
                .andExpect(jsonPath("$.fileType").value("application/pdf"));

        verify(fileStorageService).storeFile(any());
    }

    @Test
    @WithMockUser
    void uploadFile_ValidVideo_ShouldReturnFilePath() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "video.mp4",
                "video/mp4",
                "test video content".getBytes()
        );

        when(fileStorageService.storeFile(any())).thenReturn("/uploads/video-uuid.mp4");

        mockMvc.perform(multipart("/community/files/upload").file(file).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filePath").value("/uploads/video-uuid.mp4"));

        verify(fileStorageService).storeFile(any());
    }

    @Test
    @WithMockUser
    void uploadFile_EmptyFile_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        mockMvc.perform(multipart("/community/files/upload").file(file).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Please select a file to upload"));

        verify(fileStorageService, never()).storeFile(any());
    }

    @Test
    @WithMockUser
    void uploadFile_InvalidFileType_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "script.exe",
                "application/x-msdownload",
                "malicious content".getBytes()
        );

        mockMvc.perform(multipart("/community/files/upload").file(file).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Only images, PDFs, and videos are allowed"));

        verify(fileStorageService, never()).storeFile(any());
    }

    @Test
    @WithMockUser
    void uploadFile_FileTooLarge_ShouldReturnBadRequest() throws Exception {
        byte[] largeContent = new byte[51 * 1024 * 1024]; // 51MB
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                largeContent
        );

        mockMvc.perform(multipart("/community/files/upload").file(file).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("File size must be less than 50MB"));

        verify(fileStorageService, never()).storeFile(any());
    }

    @Test
    @WithMockUser
    void uploadFile_StorageException_ShouldReturnInternalServerError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        when(fileStorageService.storeFile(any())).thenThrow(new RuntimeException("Storage error"));

        mockMvc.perform(multipart("/community/files/upload").file(file).with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());

        verify(fileStorageService).storeFile(any());
    }
}
