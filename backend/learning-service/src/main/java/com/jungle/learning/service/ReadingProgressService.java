package com.jungle.learning.service;

import com.jungle.learning.dto.ReadingProgressDTO;
import com.jungle.learning.dto.UpdateProgressRequest;
import com.jungle.learning.model.Ebook;
import com.jungle.learning.model.ReadingProgress;
import com.jungle.learning.repository.EbookRepository;
import com.jungle.learning.repository.ReadingProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadingProgressService {

    private final ReadingProgressRepository progressRepository;
    private final EbookRepository ebookRepository;

    @Transactional
    public ReadingProgressDTO updateProgress(UpdateProgressRequest request, Long userId) {
        Ebook ebook = ebookRepository.findById(request.getEbookId())
                .orElseThrow(() -> new RuntimeException("Ebook not found"));

        ReadingProgress progress = progressRepository
                .findByEbookIdAndUserId(request.getEbookId(), userId)
                .orElseGet(() -> {
                    ReadingProgress newProgress = new ReadingProgress();
                    newProgress.setEbook(ebook);
                    newProgress.setUserId(userId);
                    return newProgress;
                });

        progress.setCurrentPage(request.getCurrentPage());
        progress.setTotalPages(request.getTotalPages());
        progress.setLastReadAt(LocalDateTime.now());
        
        if (request.getReadingTimeMinutes() != null) {
            progress.setReadingTimeMinutes(
                progress.getReadingTimeMinutes() + request.getReadingTimeMinutes()
            );
        }

        ReadingProgress saved = progressRepository.save(progress);
        return mapToDTO(saved);
    }

    public ReadingProgressDTO getProgress(Long ebookId, Long userId) {
        return progressRepository.findByEbookIdAndUserId(ebookId, userId)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public List<ReadingProgressDTO> getUserProgress(Long userId) {
        return progressRepository.findByUserIdOrderByLastReadAtDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ReadingProgressDTO> getInProgressBooks(Long userId) {
        return progressRepository.findInProgressByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ReadingProgressDTO> getCompletedBooks(Long userId) {
        return progressRepository.findByUserIdAndIsCompleted(userId, true)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ReadingProgressDTO mapToDTO(ReadingProgress progress) {
        ReadingProgressDTO dto = new ReadingProgressDTO();
        dto.setId(progress.getId());
        dto.setEbookId(progress.getEbook().getId());
        dto.setEbookTitle(progress.getEbook().getTitle());
        dto.setEbookCoverUrl(progress.getEbook().getCoverImageUrl());
        dto.setUserId(progress.getUserId());
        dto.setCurrentPage(progress.getCurrentPage());
        dto.setTotalPages(progress.getTotalPages());
        dto.setProgressPercentage(progress.getProgressPercentage());
        dto.setLastReadAt(progress.getLastReadAt());
        dto.setReadingTimeMinutes(progress.getReadingTimeMinutes());
        dto.setIsCompleted(progress.getIsCompleted());
        dto.setBookmarksCount(progress.getBookmarks().size());
        dto.setNotesCount(progress.getNotes().size());
        return dto;
    }
}
