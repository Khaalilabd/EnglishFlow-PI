package com.englishflow.courses.controller;

import com.englishflow.courses.dto.CourseDTO;
import com.englishflow.courses.enums.CourseStatus;
import com.englishflow.courses.service.ICourseService;
import com.englishflow.courses.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    
    private final ICourseService courseService;
    private final FileStorageService fileStorageService;
    
    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody CourseDTO courseDTO) {
        try {
            log.info("Creating course: {}", courseDTO.getTitle());
            
            // Validate required fields
            if (courseDTO.getTitle() == null || courseDTO.getTitle().trim().isEmpty()) {
                log.warn("Course title is required");
                return ResponseEntity.badRequest().body(Map.of("error", "Course title is required"));
            }
            if (courseDTO.getLevel() == null || courseDTO.getLevel().trim().isEmpty()) {
                log.warn("Course level is required");
                return ResponseEntity.badRequest().body(Map.of("error", "Course level is required"));
            }
            if (courseDTO.getTutorId() == null) {
                log.warn("Tutor ID is required");
                return ResponseEntity.badRequest().body(Map.of("error", "Tutor ID is required"));
            }
            
            CourseDTO created = courseService.createCourse(courseDTO);
            log.info("Course created successfully with ID: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            log.error("Error creating course", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create course: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        try {
            CourseDTO course = courseService.getCourseById(id);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            log.error("Error getting course by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get course: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
            Page<CourseDTO> courses = courseService.getAllCoursesPaginated(pageable);
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            log.error("Error getting all courses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get courses: " + e.getMessage()));
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllCoursesNoPagination() {
        try {
            List<CourseDTO> courses = courseService.getAllCourses();
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            log.error("Error getting all courses (no pagination)", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get courses: " + e.getMessage()));
        }
    }
    
    @GetMapping("/published")
    public ResponseEntity<?> getPublishedCourses() {
        try {
            List<CourseDTO> courses = courseService.getPublishedCourses();
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            log.error("Error getting published courses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get published courses: " + e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getCoursesByStatus(@PathVariable CourseStatus status) {
        try {
            List<CourseDTO> courses = courseService.getCoursesByStatus(status);
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            log.error("Error getting courses by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get courses: " + e.getMessage()));
        }
    }
    
    @GetMapping("/level/{level}")
    public ResponseEntity<?> getCoursesByLevel(@PathVariable String level) {
        try {
            List<CourseDTO> courses = courseService.getCoursesByLevel(level);
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            log.error("Error getting courses by level: {}", level, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get courses: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        try {
            log.info("Updating course: {}", id);
            CourseDTO updated = courseService.updateCourse(id, courseDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error updating course: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update course: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        try {
            log.info("Deleting course: {}", id);
            courseService.deleteCourse(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting course: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete course: " + e.getMessage()));
        }
    }
    
    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<?> getCoursesByTutor(@PathVariable Long tutorId) {
        try {
            List<CourseDTO> courses = courseService.getCoursesByTutor(tutorId);
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            log.error("Error getting courses by tutor: {}", tutorId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get courses: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/exists")
    public ResponseEntity<?> courseExists(@PathVariable Long id) {
        try {
            boolean exists = courseService.existsById(id);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Error checking if course exists: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to check course: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/upload-thumbnail")
    public ResponseEntity<?> uploadThumbnail(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        
        try {
            log.info("Uploading thumbnail for course: {}", id);
            
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please select a file"));
            }
            
            // Check if it's an image
            if (!fileStorageService.isValidImageFile(file)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Only image files are allowed"));
            }
            
            // Check file size (max 5MB)
            long maxSize = 5 * 1024 * 1024;
            if (!fileStorageService.isValidFileSize(file, maxSize)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "File size must be less than 5MB"));
            }
            
            // Get course
            CourseDTO course = courseService.getCourseById(id);
            
            // Delete old thumbnail if exists
            if (course.getThumbnailUrl() != null) {
                fileStorageService.deleteFile(course.getThumbnailUrl());
            }
            
            // Store new thumbnail
            String thumbnailUrl = fileStorageService.storeThumbnail(file);
            
            // Update course
            course.setThumbnailUrl(thumbnailUrl);
            courseService.updateCourse(id, course);
            
            Map<String, String> response = new HashMap<>();
            response.put("thumbnailUrl", thumbnailUrl);
            response.put("message", "Thumbnail uploaded successfully");
            
            log.info("Thumbnail uploaded successfully for course: {}", id);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error uploading thumbnail for course: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to upload thumbnail: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/upload-material")
    public ResponseEntity<?> uploadCourseMaterial(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        
        try {
            log.info("Uploading material for course: {}", id);
            
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please select a file"));
            }
            
            // Check if it's a valid course material
            if (!fileStorageService.isValidCourseMaterial(file)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid file type. Allowed: PDF, DOC, PPT, MP4, MP3, ZIP"));
            }
            
            // Check file size (max 50MB)
            long maxSize = 50 * 1024 * 1024;
            if (!fileStorageService.isValidFileSize(file, maxSize)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "File size must be less than 50MB"));
            }
            
            // Get course
            CourseDTO course = courseService.getCourseById(id);
            
            // Store material
            String materialUrl = fileStorageService.storeCourseMaterial(file);
            
            // Update course fileUrl (you might want to store multiple files in a separate table)
            course.setFileUrl(materialUrl);
            courseService.updateCourse(id, course);
            
            Map<String, String> response = new HashMap<>();
            response.put("fileUrl", materialUrl);
            response.put("message", "Course material uploaded successfully");
            
            log.info("Material uploaded successfully for course: {}", id);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error uploading material for course: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to upload material: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}/thumbnail")
    public ResponseEntity<?> deleteThumbnail(@PathVariable Long id) {
        try {
            log.info("Deleting thumbnail for course: {}", id);
            CourseDTO course = courseService.getCourseById(id);
            
            if (course.getThumbnailUrl() != null) {
                fileStorageService.deleteFile(course.getThumbnailUrl());
                course.setThumbnailUrl(null);
                courseService.updateCourse(id, course);
            }
            
            log.info("Thumbnail deleted successfully for course: {}", id);
            return ResponseEntity.ok(Map.of("message", "Thumbnail deleted successfully"));
            
        } catch (Exception e) {
            log.error("Error deleting thumbnail for course: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete thumbnail: " + e.getMessage()));
        }
    }
}
