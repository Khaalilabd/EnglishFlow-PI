package com.jungle.learning.service;

import com.jungle.learning.dto.BookmarkDTO;
import com.jungle.learning.model.Bookmark;
import com.jungle.learning.model.ReadingProgress;
import com.jungle.learning.repository.BookmarkRepository;
import com.jungle.learning.repository.ReadingProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ReadingProgressRepository progressRepository;

    @Transactional
    public BookmarkDTO createBookmark(Long progressId, Integer pageNumber, String note) {
        ReadingProgress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new RuntimeException("Reading progress not found"));

        Bookmark bookmark = new Bookmark();
        bookmark.setProgress(progress);
        bookmark.setPageNumber(pageNumber);
        bookmark.setNote(note);

        Bookmark saved = bookmarkRepository.save(bookmark);
        return mapToDTO(saved);
    }

    @Transactional
    public void deleteBookmark(Long bookmarkId) {
        bookmarkRepository.deleteById(bookmarkId);
    }

    public List<BookmarkDTO> getProgressBookmarks(Long progressId) {
        return bookmarkRepository.findByProgressIdOrderByPageNumberAsc(progressId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private BookmarkDTO mapToDTO(Bookmark bookmark) {
        BookmarkDTO dto = new BookmarkDTO();
        dto.setId(bookmark.getId());
        dto.setProgressId(bookmark.getProgress().getId());
        dto.setPageNumber(bookmark.getPageNumber());
        dto.setNote(bookmark.getNote());
        dto.setCreatedAt(bookmark.getCreatedAt());
        return dto;
    }
}
