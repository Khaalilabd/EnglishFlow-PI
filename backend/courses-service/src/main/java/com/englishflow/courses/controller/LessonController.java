package com.englishflow.courses.controller;

import com.englishflow.courses.dto.LessonDTO;
import com.englishflow.courses.enums.LessonType;
import com.englishflow.courses.service.ILessonService;
import com.englishflow.courses.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final ILessonService lessonService;
    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<?> createLesson(@RequestBody LessonDTO lessonDTO) {
        try {
            log.info("Creating lesson: {} with type: {}", lessonDTO.getTitle(), lessonDTO.getLessonType());
            
            // Validate required fields
            if (lessonDTO.getTitle() == null || lessonDTO.getTitle().trim().isEmpty()) {
                log.warn("Lesson title is required");
                return ResponseEntity.badRequest().body(Map.of("error", "Lesson title is required"));
            }
            if (lessonDTO.getChapterId() == null) {
                log.warn("Chapter ID is required");
                return ResponseEntity.badRequest().body(Map.of("error", "Chapter ID is required"));
            }
            if (lessonDTO.getLessonType() == null) {
                log.warn("Lesson type is required");
                return ResponseEntity.badRequest().body(Map.of("error", "Lesson type is required"));
            }
            
            LessonDTO created = lessonService.createLesson(lessonDTO);
            log.info("Lesson created successfully with ID: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.warn("Validation error creating lesson: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating lesson", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create lesson: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLessonById(@PathVariable Long id) {
        try {
            LessonDTO lesson = lessonService.getLessonById(id);
            return ResponseEntity.ok(lesson);
        } catch (Exception e) {
            log.error("Error getting lesson by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get lesson: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllLessons() {
        try {
            List<LessonDTO> lessons = lessonService.getAllLessons();
            return ResponseEntity.ok(lessons);
        } catch (Exception e) {
            log.error("Error getting all lessons", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get lessons: " + e.getMessage()));
        }
    }

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<?> getLessonsByChapter(@PathVariable Long chapterId) {
        try {
            log.info("Getting lessons for chapter: {}", chapterId);
            List<LessonDTO> lessons = lessonService.getLessonsByChapter(chapterId);
            log.info("Found {} lessons for chapter: {}", lessons.size(), chapterId);
            return ResponseEntity.ok(lessons);
        } catch (Exception e) {
            log.error("Error getting lessons by chapter: {}", chapterId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get lessons: " + e.getMessage()));
        }
    }

    @GetMapping("/chapter/{chapterId}/published")
    public ResponseEntity<?> getPublishedLessonsByChapter(@PathVariable Long chapterId) {
        try {
            List<LessonDTO> lessons = lessonService.getPublishedLessonsByChapter(chapterId);
            return ResponseEntity.ok(lessons);
        } catch (Exception e) {
            log.error("Error getting published lessons by chapter: {}", chapterId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get lessons: " + e.getMessage()));
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getLessonsByCourse(@PathVariable Long courseId) {
        try {
            List<LessonDTO> lessons = lessonService.getLessonsByCourse(courseId);
            return ResponseEntity.ok(lessons);
        } catch (Exception e) {
            log.error("Error getting lessons by course: {}", courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get lessons: " + e.getMessage()));
        }
    }

    @GetMapping("/type/{lessonType}")
    public ResponseEntity<?> getLessonsByType(@PathVariable LessonType lessonType) {
        try {
            List<LessonDTO> lessons = lessonService.getLessonsByType(lessonType);
            return ResponseEntity.ok(lessons);
        } catch (Exception e) {
            log.error("Error getting lessons by type: {}", lessonType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get lessons: " + e.getMessage()));
        }
    }

    @GetMapping("/course/{courseId}/preview")
    public ResponseEntity<?> getPreviewLessonsByCourse(@PathVariable Long courseId) {
        try {
            List<LessonDTO> lessons = lessonService.getPreviewLessonsByCourse(courseId);
            return ResponseEntity.ok(lessons);
        } catch (Exception e) {
            log.error("Error getting preview lessons by course: {}", courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get lessons: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLesson(@PathVariable Long id, @RequestBody LessonDTO lessonDTO) {
        try {
            log.info("Updating lesson: {}", id);
            LessonDTO updated = lessonService.updateLesson(id, lessonDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error updating lesson: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update lesson: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long id) {
        try {
            log.info("Deleting lesson: {}", id);
            lessonService.deleteLesson(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting lesson: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete lesson: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<?> lessonExists(@PathVariable Long id) {
        try {
            boolean exists = lessonService.existsById(id);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Error checking if lesson exists: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to check lesson: " + e.getMessage()));
        }
    }

    @GetMapping("/{lessonId}/belongs-to-chapter/{chapterId}")
    public ResponseEntity<?> lessonBelongsToChapter(@PathVariable Long lessonId, @PathVariable Long chapterId) {
        try {
            boolean belongs = lessonService.belongsToChapter(lessonId, chapterId);
            return ResponseEntity.ok(belongs);
        } catch (Exception e) {
            log.error("Error checking if lesson belongs to chapter: {} -> {}", lessonId, chapterId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to check lesson: " + e.getMessage()));
        }
    }

    @GetMapping("/{lessonId}/belongs-to-course/{courseId}")
    public ResponseEntity<?> lessonBelongsToCourse(@PathVariable Long lessonId, @PathVariable Long courseId) {
        try {
            boolean belongs = lessonService.belongsToCourse(lessonId, courseId);
            return ResponseEntity.ok(belongs);
        } catch (Exception e) {
            log.error("Error checking if lesson belongs to course: {} -> {}", lessonId, courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to check lesson: " + e.getMessage()));
        }
    }

    /**
     * Upload video file for a lesson
     * Max size: 500MB
     * Allowed types: MP4, AVI, MOV, MKV
     */
    @PostMapping("/{id}/upload-video")
    public ResponseEntity<?> uploadVideo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        try {
            log.info("Uploading video for lesson: {}", id);
            
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Please select a file to upload"));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only video files are allowed"));
            }

            // Validate file size (500MB)
            long maxSize = 500 * 1024 * 1024L;
            if (!fileStorageService.isValidFileSize(file, maxSize)) {
                return ResponseEntity.badRequest().body(Map.of("error", "File size must not exceed 500MB"));
            }

            // Store file
            String fileUrl = fileStorageService.storeFile(file, "lessons/videos");

            // Update lesson with video URL
            LessonDTO lesson = lessonService.getLessonById(id);
            lesson.setContentUrl(fileUrl);
            lessonService.updateLesson(id, lesson);

            log.info("Video uploaded successfully for lesson: {}", id);
            return ResponseEntity.ok(Map.of("url", fileUrl, "message", "Video uploaded successfully"));
        } catch (Exception e) {
            log.error("Error uploading video for lesson: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to upload video: " + e.getMessage()));
        }
    }

    /**
     * Upload document file for a lesson
     * Max size: 50MB
     * Allowed types: PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX
     */
    @PostMapping("/{id}/upload-document")
    public ResponseEntity<?> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        try {
            log.info("Uploading document for lesson: {}", id);
            
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Please select a file to upload"));
            }

            // Validate file type
            if (!fileStorageService.isValidCourseMaterial(file)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid file type. Allowed: PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX"));
            }

            // Validate file size (50MB)
            long maxSize = 50 * 1024 * 1024L;
            if (!fileStorageService.isValidFileSize(file, maxSize)) {
                return ResponseEntity.badRequest().body(Map.of("error", "File size must not exceed 50MB"));
            }

            // Store file
            String fileUrl = fileStorageService.storeFile(file, "lessons/documents");

            // Update lesson with document URL
            LessonDTO lesson = lessonService.getLessonById(id);
            lesson.setContentUrl(fileUrl);
            lessonService.updateLesson(id, lesson);

            log.info("Document uploaded successfully for lesson: {}", id);
            return ResponseEntity.ok(Map.of("url", fileUrl, "message", "Document uploaded successfully"));
        } catch (Exception e) {
            log.error("Error uploading document for lesson: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to upload document: " + e.getMessage()));
        }
    }

    /**
     * Delete lesson content file
     */
    @DeleteMapping("/{id}/content-file")
    public ResponseEntity<?> deleteContentFile(@PathVariable Long id) {
        try {
            log.info("Deleting content file for lesson: {}", id);
            LessonDTO lesson = lessonService.getLessonById(id);

            if (lesson.getContentUrl() != null && !lesson.getContentUrl().isEmpty()) {
                fileStorageService.deleteFile(lesson.getContentUrl());
                lesson.setContentUrl(null);
                lessonService.updateLesson(id, lesson);
            }

            log.info("Content file deleted successfully for lesson: {}", id);
            return ResponseEntity.ok(Map.of("message", "Content file deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting content file for lesson: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete content file: " + e.getMessage()));
        }
    }
    
    // FIX 3: Bulk publish/unpublish all lessons in a course
    @PutMapping("/course/{courseId}/publish-all")
    public ResponseEntity<?> publishAllLessonsByCourse(@PathVariable Long courseId) {
        try {
            log.info("Publishing all lessons for course: {}", courseId);
            List<LessonDTO> lessons = lessonService.publishAllLessonsByCourse(courseId);
            log.info("Published {} lessons for course: {}", lessons.size(), courseId);
            return ResponseEntity.ok(lessons);
        } catch (Exception e) {
            log.error("Error publishing all lessons for course: {}", courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to publish lessons: " + e.getMessage()));
        }
    }
    
    @PutMapping("/course/{courseId}/unpublish-all")
    public ResponseEntity<?> unpublishAllLessonsByCourse(@PathVariable Long courseId) {
        try {
            log.info("Unpublishing all lessons for course: {}", courseId);
            List<LessonDTO> lessons = lessonService.unpublishAllLessonsByCourse(courseId);
            log.info("Unpublished {} lessons for course: {}", lessons.size(), courseId);
            return ResponseEntity.ok(lessons);
        } catch (Exception e) {
            log.error("Error unpublishing all lessons for course: {}", courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to unpublish lessons: " + e.getMessage()));
        }
    }
}
