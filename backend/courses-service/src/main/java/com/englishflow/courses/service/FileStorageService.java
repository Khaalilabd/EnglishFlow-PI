package com.englishflow.courses.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir:uploads/courses}")
    private String uploadDir;
    
    @Value("${server.port:8086}")
    private String serverPort;

    /**
     * Store a file and return its URL
     */
    public String storeFile(MultipartFile file, String subDirectory) {
        try {
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, subDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // Copy file to target location
            Path targetLocation = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {}", targetLocation);

            // Return URL accessible via API Gateway
            // Format: http://localhost:8080/api/courses-service/uploads/courses/{subDirectory}/{filename}
            return "/uploads/courses/" + subDirectory + "/" + newFilename;

        } catch (IOException ex) {
            log.error("Could not store file. Error: {}", ex.getMessage());
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    /**
     * Store thumbnail image
     */
    public String storeThumbnail(MultipartFile file) {
        return storeFile(file, "thumbnails");
    }

    /**
     * Store course material file
     */
    public String storeCourseMaterial(MultipartFile file) {
        return storeFile(file, "materials");
    }

    /**
     * Delete a file
     */
    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl != null && !fileUrl.isEmpty()) {
                // Remove leading slash if present
                String filePath = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
                Path path = Paths.get(filePath);
                Files.deleteIfExists(path);
                log.info("Deleted file: {}", filePath);
            }
        } catch (IOException ex) {
            log.error("Could not delete file: {}. Error: {}", fileUrl, ex.getMessage());
        }
    }

    /**
     * Validate if file is an image
     */
    public boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp")
        );
    }

    /**
     * Validate if file is a valid course material
     */
    public boolean isValidCourseMaterial(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("application/pdf") ||
                contentType.equals("application/msword") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                contentType.equals("application/vnd.ms-powerpoint") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") ||
                contentType.equals("video/mp4") ||
                contentType.equals("audio/mpeg") ||
                contentType.equals("application/zip")
        );
    }

    /**
     * Validate file size
     */
    public boolean isValidFileSize(MultipartFile file, long maxSize) {
        return file.getSize() <= maxSize;
    }

    /**
     * Get file extension
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
}
