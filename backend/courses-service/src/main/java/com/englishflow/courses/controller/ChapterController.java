package com.englishflow.courses.controller;

import com.englishflow.courses.dto.ChapterDTO;
import com.englishflow.courses.service.IChapterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/chapters")
@RequiredArgsConstructor
public class ChapterController {
    
    private final IChapterService chapterService;
    
    @PostMapping
    public ResponseEntity<?> createChapter(@RequestBody ChapterDTO chapterDTO) {
        try {
            log.info("Creating chapter: {}", chapterDTO.getTitle());
            ChapterDTO created = chapterService.createChapter(chapterDTO);
            log.info("Chapter created successfully with ID: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            log.error("Error creating chapter", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create chapter: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getChapterById(@PathVariable Long id) {
        try {
            ChapterDTO chapter = chapterService.getChapterById(id);
            return ResponseEntity.ok(chapter);
        } catch (Exception e) {
            log.error("Error getting chapter by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get chapter: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllChapters() {
        try {
            List<ChapterDTO> chapters = chapterService.getAllChapters();
            return ResponseEntity.ok(chapters);
        } catch (Exception e) {
            log.error("Error getting all chapters", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get chapters: " + e.getMessage()));
        }
    }
    
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getChaptersByCourse(@PathVariable Long courseId) {
        try {
            List<ChapterDTO> chapters = chapterService.getChaptersByCourse(courseId);
            return ResponseEntity.ok(chapters);
        } catch (Exception e) {
            log.error("Error getting chapters by course: {}", courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get chapters: " + e.getMessage()));
        }
    }
    
    @GetMapping("/course/{courseId}/published")
    public ResponseEntity<?> getPublishedChaptersByCourse(@PathVariable Long courseId) {
        try {
            List<ChapterDTO> chapters = chapterService.getPublishedChaptersByCourse(courseId);
            return ResponseEntity.ok(chapters);
        } catch (Exception e) {
            log.error("Error getting published chapters by course: {}", courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get chapters: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateChapter(@PathVariable Long id, @RequestBody ChapterDTO chapterDTO) {
        try {
            log.info("Updating chapter: {}", id);
            ChapterDTO updated = chapterService.updateChapter(id, chapterDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error updating chapter: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update chapter: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChapter(@PathVariable Long id) {
        try {
            log.info("Deleting chapter: {}", id);
            chapterService.deleteChapter(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting chapter: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete chapter: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/exists")
    public ResponseEntity<?> chapterExists(@PathVariable Long id) {
        try {
            boolean exists = chapterService.existsById(id);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Error checking if chapter exists: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to check chapter: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{chapterId}/belongs-to-course/{courseId}")
    public ResponseEntity<?> chapterBelongsToCourse(@PathVariable Long chapterId, @PathVariable Long courseId) {
        try {
            boolean belongs = chapterService.belongsToCourse(chapterId, courseId);
            return ResponseEntity.ok(belongs);
        } catch (Exception e) {
            log.error("Error checking if chapter belongs to course: {} -> {}", chapterId, courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to check chapter: " + e.getMessage()));
        }
    }
    
    // FIX 3: Bulk publish/unpublish all chapters in a course
    @PutMapping("/course/{courseId}/publish-all")
    public ResponseEntity<?> publishAllChaptersByCourse(@PathVariable Long courseId) {
        try {
            log.info("Publishing all chapters for course: {}", courseId);
            List<ChapterDTO> chapters = chapterService.publishAllChaptersByCourse(courseId);
            log.info("Published {} chapters for course: {}", chapters.size(), courseId);
            return ResponseEntity.ok(chapters);
        } catch (Exception e) {
            log.error("Error publishing all chapters for course: {}", courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to publish chapters: " + e.getMessage()));
        }
    }
    
    @PutMapping("/course/{courseId}/unpublish-all")
    public ResponseEntity<?> unpublishAllChaptersByCourse(@PathVariable Long courseId) {
        try {
            log.info("Unpublishing all chapters for course: {}", courseId);
            List<ChapterDTO> chapters = chapterService.unpublishAllChaptersByCourse(courseId);
            log.info("Unpublished {} chapters for course: {}", chapters.size(), courseId);
            return ResponseEntity.ok(chapters);
        } catch (Exception e) {
            log.error("Error unpublishing all chapters for course: {}", courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to unpublish chapters: " + e.getMessage()));
        }
    }
}
