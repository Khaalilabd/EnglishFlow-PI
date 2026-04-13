package com.englishflow.community.controller;

import com.englishflow.community.dto.SaveVocabularyRequest;
import com.englishflow.community.dto.VocabularyStatsDTO;
import com.englishflow.community.dto.VocabularyWordDTO;
import com.englishflow.community.service.VocabularyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community/vocabulary")
@RequiredArgsConstructor
@Tag(name = "Vocabulary", description = "Personal vocabulary management endpoints")
public class VocabularyController {
    
    private final VocabularyService vocabularyService;
    
    @PostMapping
    @Operation(summary = "Save a word to vocabulary", description = "Add a new word to user's personal vocabulary")
    public ResponseEntity<VocabularyWordDTO> saveWord(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody SaveVocabularyRequest request) {
        VocabularyWordDTO saved = vocabularyService.saveWord(userId, request);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping
    @Operation(summary = "Get user vocabulary", description = "Get paginated list of user's vocabulary words")
    public ResponseEntity<Page<VocabularyWordDTO>> getUserVocabulary(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false) String level) {
        Page<VocabularyWordDTO> vocabulary;
        
        if (level != null && !level.isEmpty() && !level.equals("all")) {
            vocabulary = vocabularyService.getUserVocabularyByLevel(userId, level, page, size, sortBy);
        } else {
            vocabulary = vocabularyService.getUserVocabulary(userId, page, size, sortBy);
        }
        
        return ResponseEntity.ok(vocabulary);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search vocabulary", description = "Search user's vocabulary by word")
    public ResponseEntity<Page<VocabularyWordDTO>> searchVocabulary(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<VocabularyWordDTO> results = vocabularyService.searchVocabulary(userId, query, page, size);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get vocabulary statistics", description = "Get user's vocabulary learning statistics")
    public ResponseEntity<VocabularyStatsDTO> getStats(@RequestHeader("X-User-Id") Long userId) {
        VocabularyStatsDTO stats = vocabularyService.getUserStats(userId);
        return ResponseEntity.ok(stats);
    }
    
    @PutMapping("/{wordId}/review")
    @Operation(summary = "Mark word as reviewed", description = "Increment review count and update mastery level")
    public ResponseEntity<VocabularyWordDTO> markAsReviewed(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long wordId) {
        VocabularyWordDTO updated = vocabularyService.markAsReviewed(userId, wordId);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{wordId}")
    @Operation(summary = "Delete word from vocabulary", description = "Remove a word from user's vocabulary")
    public ResponseEntity<Void> deleteWord(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long wordId) {
        vocabularyService.deleteWord(userId, wordId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/check/{word}")
    @Operation(summary = "Check if word is saved", description = "Check if a word exists in user's vocabulary")
    public ResponseEntity<Boolean> isWordSaved(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String word) {
        boolean exists = vocabularyService.isWordSaved(userId, word);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/export")
    @Operation(summary = "Export all vocabulary", description = "Get all vocabulary words for export")
    public ResponseEntity<List<VocabularyWordDTO>> exportVocabulary(
            @RequestHeader("X-User-Id") Long userId) {
        List<VocabularyWordDTO> allWords = vocabularyService.getAllUserVocabulary(userId);
        return ResponseEntity.ok(allWords);
    }
}
