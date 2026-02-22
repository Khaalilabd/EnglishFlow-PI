package com.jungle.learning.controller;

import com.jungle.learning.dto.EbookDTO;
import com.jungle.learning.service.EbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/learning/ebooks")
@RequiredArgsConstructor
// CORS est géré par l'API Gateway - pas besoin d'annotation @CrossOrigin ici
public class EbookController {
    
    private final EbookService ebookService;
    
    @Value("${app.upload.dir:uploads/ebooks}")
    private String uploadDir;
    
    @GetMapping
    public ResponseEntity<List<EbookDTO>> getAllEbooks() {
        return ResponseEntity.ok(ebookService.getAllEbooks());
    }
    
    @GetMapping("/free")
    public ResponseEntity<List<EbookDTO>> getFreeEbooks() {
        return ResponseEntity.ok(ebookService.getFreeEbooks());
    }
    
    @GetMapping("/level/{level}")
    public ResponseEntity<List<EbookDTO>> getEbooksByLevel(@PathVariable String level) {
        return ResponseEntity.ok(ebookService.getEbooksByLevel(level));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EbookDTO> getEbookById(@PathVariable Long id) {
        return ResponseEntity.ok(ebookService.getEbookById(id));
    }
    
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadEbook(@PathVariable Long id) {
        try {
            EbookDTO ebook = ebookService.getEbookById(id);
            
            // Log for debugging
            System.out.println("=== Download Debug Info ===");
            System.out.println("Ebook ID: " + id);
            System.out.println("Ebook Title: " + ebook.getTitle());
            System.out.println("File URL from DB: " + ebook.getFileUrl());
            System.out.println("Upload Dir Config: " + uploadDir);
            System.out.println("Working Directory: " + System.getProperty("user.dir"));
            
            // Get the file path from the upload directory
            Path filePath = Paths.get(uploadDir).resolve(ebook.getFileUrl()).normalize().toAbsolutePath();
            System.out.println("Resolved File Path: " + filePath.toString());
            System.out.println("File exists: " + filePath.toFile().exists());
            System.out.println("File readable: " + filePath.toFile().canRead());
            
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                System.out.println("SUCCESS: File found and readable");
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + ebook.getTitle() + ".pdf\"")
                        .body(resource);
            } else {
                System.err.println("ERROR: File not found or not readable at path: " + filePath.toString());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("ERROR: Exception during download");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/cover")
    public ResponseEntity<Resource> getCoverImage(@PathVariable Long id) {
        try {
            EbookDTO ebook = ebookService.getEbookById(id);
            
            if (ebook.getCoverImageUrl() == null || ebook.getCoverImageUrl().isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Path filePath = Paths.get(uploadDir).resolve(ebook.getCoverImageUrl()).normalize().toAbsolutePath();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                // Determine content type based on file extension
                String contentType = "image/jpeg";
                String fileName = ebook.getCoverImageUrl().toLowerCase();
                if (fileName.endsWith(".png")) {
                    contentType = "image/png";
                } else if (fileName.endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (fileName.endsWith(".webp")) {
                    contentType = "image/webp";
                }
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<EbookDTO> createEbook(
            @RequestPart("ebook") EbookDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ebookService.createEbook(dto, file, coverImage));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EbookDTO> updateEbook(
            @PathVariable Long id,
            @RequestPart("ebook") EbookDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) throws IOException {
        return ResponseEntity.ok(ebookService.updateEbook(id, dto, file, coverImage));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEbook(@PathVariable Long id) {
        ebookService.deleteEbook(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{ebookId}/access")
    public ResponseEntity<Void> trackAccess(
            @PathVariable Long ebookId,
            @RequestParam Long studentId) {
        ebookService.trackAccess(ebookId, studentId);
        return ResponseEntity.ok().build();
    }
}
