package com.englishflow.courses.controller;

import com.englishflow.courses.dto.LessonDTO;
import com.englishflow.courses.enums.LessonType;
import com.englishflow.courses.service.ILessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    
    private final ILessonService lessonService;
    
    @PostMapping
    public ResponseEntity<LessonDTO> createLesson(@RequestBody LessonDTO lessonDTO) {
        LessonDTO created = lessonService.createLesson(lessonDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LessonDTO> getLessonById(@PathVariable Long id) {
        LessonDTO lesson = lessonService.getLessonById(id);
        return ResponseEntity.ok(lesson);
    }
    
    @GetMapping
    public ResponseEntity<List<LessonDTO>> getAllLessons() {
        List<LessonDTO> lessons = lessonService.getAllLessons();
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<LessonDTO>> getLessonsByChapter(@PathVariable Long chapterId) {
        List<LessonDTO> lessons = lessonService.getLessonsByChapter(chapterId);
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/chapter/{chapterId}/published")
    public ResponseEntity<List<LessonDTO>> getPublishedLessonsByChapter(@PathVariable Long chapterId) {
        List<LessonDTO> lessons = lessonService.getPublishedLessonsByChapter(chapterId);
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LessonDTO>> getLessonsByCourse(@PathVariable Long courseId) {
        List<LessonDTO> lessons = lessonService.getLessonsByCourse(courseId);
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/type/{lessonType}")
    public ResponseEntity<List<LessonDTO>> getLessonsByType(@PathVariable LessonType lessonType) {
        List<LessonDTO> lessons = lessonService.getLessonsByType(lessonType);
        return ResponseEntity.ok(lessons);
    }
    
    @GetMapping("/course/{courseId}/preview")
    public ResponseEntity<List<LessonDTO>> getPreviewLessonsByCourse(@PathVariable Long courseId) {
        List<LessonDTO> lessons = lessonService.getPreviewLessonsByCourse(courseId);
        return ResponseEntity.ok(lessons);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LessonDTO> updateLesson(@PathVariable Long id, @RequestBody LessonDTO lessonDTO) {
        LessonDTO updated = lessonService.updateLesson(id, lessonDTO);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> lessonExists(@PathVariable Long id) {
        boolean exists = lessonService.existsById(id);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/{lessonId}/belongs-to-chapter/{chapterId}")
    public ResponseEntity<Boolean> lessonBelongsToChapter(@PathVariable Long lessonId, @PathVariable Long chapterId) {
        boolean belongs = lessonService.belongsToChapter(lessonId, chapterId);
        return ResponseEntity.ok(belongs);
    }
    
    @GetMapping("/{lessonId}/belongs-to-course/{courseId}")
    public ResponseEntity<Boolean> lessonBelongsToCourse(@PathVariable Long lessonId, @PathVariable Long courseId) {
        boolean belongs = lessonService.belongsToCourse(lessonId, courseId);
        return ResponseEntity.ok(belongs);
    }
}
