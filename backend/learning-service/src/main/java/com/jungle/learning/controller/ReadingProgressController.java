package com.jungle.learning.controller;

import com.jungle.learning.dto.ReadingProgressDTO;
import com.jungle.learning.dto.UpdateProgressRequest;
import com.jungle.learning.service.ReadingProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reading-progress")
@RequiredArgsConstructor
public class ReadingProgressController {

    private final ReadingProgressService progressService;

    @PostMapping
    public ResponseEntity<ReadingProgressDTO> updateProgress(
            @RequestBody UpdateProgressRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        ReadingProgressDTO progress = progressService.updateProgress(request, userId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/ebook/{ebookId}")
    public ResponseEntity<ReadingProgressDTO> getProgress(
            @PathVariable Long ebookId,
            @RequestHeader("X-User-Id") Long userId) {
        ReadingProgressDTO progress = progressService.getProgress(ebookId, userId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/user")
    public ResponseEntity<List<ReadingProgressDTO>> getUserProgress(
            @RequestHeader("X-User-Id") Long userId) {
        List<ReadingProgressDTO> progress = progressService.getUserProgress(userId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/user/in-progress")
    public ResponseEntity<List<ReadingProgressDTO>> getInProgressBooks(
            @RequestHeader("X-User-Id") Long userId) {
        List<ReadingProgressDTO> progress = progressService.getInProgressBooks(userId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/user/completed")
    public ResponseEntity<List<ReadingProgressDTO>> getCompletedBooks(
            @RequestHeader("X-User-Id") Long userId) {
        List<ReadingProgressDTO> progress = progressService.getCompletedBooks(userId);
        return ResponseEntity.ok(progress);
    }
}
