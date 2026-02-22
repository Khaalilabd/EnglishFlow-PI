package com.englishflow.messaging.service;

import com.englishflow.messaging.exception.FileStorageException;
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
    
    private final Path fileStorageLocation;
    
    // Types de fichiers autorisés
    private static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };
    
    private static final String[] ALLOWED_DOCUMENT_TYPES = {
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "text/plain"
    };
    
    private static final String[] ALLOWED_AUDIO_TYPES = {
        "audio/webm",
        "audio/ogg",
        "audio/mpeg",
        "audio/mp3",
        "audio/wav",
        "audio/x-m4a"
    };
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    public FileStorageService(@Value("${file.upload-dir:uploads/messages}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("File storage location created at: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
    
    public String storeFile(MultipartFile file) {
        // Valider le fichier
        validateFile(file);
        
        // Normaliser le nom du fichier
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Vérifier si le nom contient des caractères invalides
            if (originalFileName.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence: " + originalFileName);
            }
            
            // Générer un nom unique pour éviter les collisions
            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }
            
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
            
            // Copier le fichier vers le répertoire de destination
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("File stored successfully: {}", uniqueFileName);
            
            return uniqueFileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }
    
    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
            log.info("File deleted successfully: {}", fileName);
        } catch (IOException ex) {
            log.error("Could not delete file: {}", fileName, ex);
        }
    }
    
    public Path getFilePath(String fileName) {
        return this.fileStorageLocation.resolve(fileName).normalize();
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("Cannot upload empty file");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileStorageException("File size exceeds maximum limit of 10MB");
        }
        
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new FileStorageException("File type cannot be determined");
        }
        
        boolean isAllowed = false;
        
        // Vérifier les images
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (contentType.equals(allowedType)) {
                isAllowed = true;
                break;
            }
        }
        
        // Vérifier les documents
        if (!isAllowed) {
            for (String allowedType : ALLOWED_DOCUMENT_TYPES) {
                if (contentType.equals(allowedType)) {
                    isAllowed = true;
                    break;
                }
            }
        }
        
        // Vérifier les fichiers audio
        if (!isAllowed) {
            for (String allowedType : ALLOWED_AUDIO_TYPES) {
                if (contentType.equals(allowedType)) {
                    isAllowed = true;
                    break;
                }
            }
        }
        
        if (!isAllowed) {
            throw new FileStorageException("File type not allowed: " + contentType);
        }
    }
    
    public boolean isImageFile(String contentType) {
        if (contentType == null) return false;
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (contentType.equals(allowedType)) {
                return true;
            }
        }
        return false;
    }
}
