package com.jungle.learning.controller;

import com.jungle.learning.dto.BookmarkDTO;
import com.jungle.learning.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<BookmarkDTO> createBookmark(@RequestBody Map<String, Object> request) {
        Long progressId = ((Number) request.get("progressId")).longValue();
        Integer pageNumber = (Integer) request.get("pageNumber");
        String note = (String) request.get("note");
        
        BookmarkDTO bookmark = bookmarkService.createBookmark(progressId, pageNumber, note);
        return ResponseEntity.ok(bookmark);
    }

    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/progress/{progressId}")
    public ResponseEntity<List<BookmarkDTO>> getProgressBookmarks(@PathVariable Long progressId) {
        List<BookmarkDTO> bookmarks = bookmarkService.getProgressBookmarks(progressId);
        return ResponseEntity.ok(bookmarks);
    }
}
