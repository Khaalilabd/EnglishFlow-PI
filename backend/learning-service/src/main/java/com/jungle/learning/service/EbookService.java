package com.jungle.learning.service;

import com.jungle.learning.dto.EbookDTO;
import com.jungle.learning.exception.ResourceNotFoundException;
import com.jungle.learning.model.Ebook;
import com.jungle.learning.model.EbookAccess;
import com.jungle.learning.repository.EbookAccessRepository;
import com.jungle.learning.repository.EbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EbookService {
    
    private final EbookRepository ebookRepository;
    private final EbookAccessRepository ebookAccessRepository;
    private final UserServiceClient userServiceClient;
    
    @Value("${app.upload.dir:uploads/ebooks}")
    private String uploadDir;
    
    public List<EbookDTO> getAllEbooks() {
        return ebookRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<EbookDTO> getFreeEbooks() {
        return ebookRepository.findByIsFree(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<EbookDTO> getEbooksByLevel(String level) {
        return ebookRepository.findByLevel(Ebook.Level.valueOf(level))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public EbookDTO getEbookById(Long id) {
        Ebook ebook = ebookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ebook not found with id: " + id));
        return convertToDTO(ebook);
    }
    
    @Transactional
    public EbookDTO createEbook(EbookDTO dto, MultipartFile file, MultipartFile coverImage) throws IOException {
        String fileUrl = null;
        Long fileSize = null;
        String mimeType = null;
        String coverImageUrl = null;
        
        if (file != null && !file.isEmpty()) {
            fileUrl = saveFile(file);
            fileSize = file.getSize();
            mimeType = file.getContentType();
        }
        
        if (coverImage != null && !coverImage.isEmpty()) {
            coverImageUrl = saveFile(coverImage);
        }
        
        Ebook ebook = new Ebook();
        ebook.setTitle(dto.getTitle());
        ebook.setDescription(dto.getDescription());
        ebook.setFileUrl(fileUrl != null ? fileUrl : dto.getFileUrl());
        ebook.setFileSize(fileSize != null ? fileSize : dto.getFileSize());
        ebook.setMimeType(mimeType != null ? mimeType : dto.getMimeType());
        ebook.setCoverImageUrl(coverImageUrl);
        ebook.setLevel(dto.getLevel() != null ? Ebook.Level.valueOf(dto.getLevel()) : null);
        ebook.setCategory(dto.getCategory() != null ? Ebook.Category.valueOf(dto.getCategory()) : null);
        ebook.setIsFree(dto.getFree() != null ? dto.getFree() : true);
        ebook.setDownloadCount(0);
        ebook.setCreatedBy(dto.getCreatedBy()); // Set creator
        
        // Set status to PENDING for tutor uploads (requires approval)
        // ACADEMIC_OFFICE_AFFAIR can approve/reject
        ebook.setStatus(Ebook.PublishStatus.PENDING);
        
        Ebook saved = ebookRepository.save(ebook);
        return convertToDTO(saved);
    }
    
    @Transactional
    public EbookDTO updateEbook(Long id, EbookDTO dto, MultipartFile file, MultipartFile coverImage) throws IOException {
        Ebook ebook = ebookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ebook not found with id: " + id));
        
        if (file != null && !file.isEmpty()) {
            String fileUrl = saveFile(file);
            ebook.setFileUrl(fileUrl);
            ebook.setFileSize(file.getSize());
            ebook.setMimeType(file.getContentType());
        }
        
        if (coverImage != null && !coverImage.isEmpty()) {
            String coverImageUrl = saveFile(coverImage);
            ebook.setCoverImageUrl(coverImageUrl);
        }
        
        ebook.setTitle(dto.getTitle());
        ebook.setDescription(dto.getDescription());
        ebook.setLevel(dto.getLevel() != null ? Ebook.Level.valueOf(dto.getLevel()) : ebook.getLevel());
        ebook.setCategory(dto.getCategory() != null ? Ebook.Category.valueOf(dto.getCategory()) : ebook.getCategory());
        ebook.setIsFree(dto.getFree());
        
        Ebook updated = ebookRepository.save(ebook);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteEbook(Long id) {
        if (!ebookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ebook not found with id: " + id);
        }
        ebookRepository.deleteById(id);
    }
    
    @Transactional
    public void trackAccess(Long ebookId, Long studentId) {
        Ebook ebook = ebookRepository.findById(ebookId)
                .orElseThrow(() -> new ResourceNotFoundException("Ebook not found with id: " + ebookId));
        
        // Always increment download count when accessed
        ebook.setDownloadCount(ebook.getDownloadCount() + 1);
        ebookRepository.save(ebook);
        
        // Track user access for progress tracking
        EbookAccess access = ebookAccessRepository.findByEbook_IdAndStudentId(ebookId, studentId)
                .orElse(new EbookAccess());
        
        if (access.getId() == null) {
            access.setEbook(ebook);
            access.setStudentId(studentId);
            access.setProgressPercent(0);
            ebookAccessRepository.save(access);
        }
    }
    
    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return filename;
    }
    
    @Transactional
    public EbookDTO approveEbook(Long id) {
        Ebook ebook = ebookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ebook not found with id: " + id));
        
        ebook.setStatus(Ebook.PublishStatus.PUBLISHED);
        ebook.setPublishedAt(java.time.LocalDateTime.now());
        
        Ebook approved = ebookRepository.save(ebook);
        return convertToDTO(approved);
    }
    
    @Transactional
    public EbookDTO rejectEbook(Long id, String reason) {
        Ebook ebook = ebookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ebook not found with id: " + id));
        
        ebook.setStatus(Ebook.PublishStatus.REJECTED);
        // Store rejection reason in description metadata if needed
        
        Ebook rejected = ebookRepository.save(ebook);
        return convertToDTO(rejected);
    }
    
    public List<EbookDTO> getPendingEbooks() {
        return ebookRepository.findByStatus(Ebook.PublishStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private EbookDTO convertToDTO(Ebook ebook) {
        EbookDTO dto = new EbookDTO();
        dto.setId(ebook.getId());
        dto.setTitle(ebook.getTitle());
        dto.setDescription(ebook.getDescription());
        dto.setFileUrl(ebook.getFileUrl());
        dto.setFileSize(ebook.getFileSize());
        dto.setMimeType(ebook.getMimeType());
        dto.setCoverImageUrl(ebook.getCoverImageUrl());
        dto.setThumbnailUrl(ebook.getThumbnailUrl());
        dto.setLevel(ebook.getLevel() != null ? ebook.getLevel().name() : null);
        dto.setCategory(ebook.getCategory() != null ? ebook.getCategory().name() : null);
        dto.setFree(ebook.getIsFree());
        dto.setPrice(ebook.getPrice());
        dto.setPricingModel(ebook.getPricingModel() != null ? ebook.getPricingModel().name() : null);
        dto.setDownloadCount(ebook.getDownloadCount());
        dto.setViewCount(ebook.getViewCount());
        dto.setAverageRating(ebook.getAverageRating());
        dto.setReviewCount(ebook.getReviewCount());
        dto.setStatus(ebook.getStatus() != null ? ebook.getStatus().name() : null);
        dto.setPublishedAt(ebook.getPublishedAt());
        dto.setScheduledFor(ebook.getScheduledFor());
        dto.setCreatedAt(ebook.getCreatedAt());
        dto.setUpdatedAt(ebook.getUpdatedAt());
        dto.setCreatedBy(ebook.getCreatedBy());
        
        // Fetch creator name from auth service
        if (ebook.getCreatedBy() != null) {
            try {
                String creatorName = userServiceClient.getUserName(ebook.getCreatedBy());
                dto.setCreatorName(creatorName);
            } catch (Exception e) {
                dto.setCreatorName("Unknown");
            }
        }
        
        return dto;
    }
}
