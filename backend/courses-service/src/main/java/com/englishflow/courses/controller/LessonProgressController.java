package com.englishflow.courses.controller;

import com.englishflow.courses.dto.CourseProgressSummary;
import com.englishflow.courses.dto.CreateLessonProgressRequest;
import com.englishflow.courses.entity.LessonProgress;
import com.englishflow.courses.service.LessonProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lesson-progress")
@RequiredArgsConstructor
public class LessonProgressController {
    
    private final LessonProgressService progressService;
    
    @GetMapping("/student/{studentId}/lesson/{lessonId}")
    public ResponseEntity<LessonProgress> getProgressByStudentAndLesson(
            @PathVariable Long studentId,
            @PathVariable Long lessonId) {
        LessonProgress progress = progressService.getProgressByStudentAndLesson(studentId, lessonId);
        if (progress == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(progress);
    }
    
    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<List<LessonProgress>> getProgressByStudentAndCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        List<LessonProgress> progressList = progressService.getProgressByStudentAndCourse(studentId, courseId);
        return ResponseEntity.ok(progressList);
    }
    
    @GetMapping("/student/{studentId}/course/{courseId}/summary")
    public ResponseEntity<CourseProgressSummary> getCourseProgressSummary(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        CourseProgressSummary summary = progressService.getCourseProgressSummary(studentId, courseId);
        return ResponseEntity.ok(summary);
    }
    
    @PostMapping
    public ResponseEntity<LessonProgress> createProgress(@RequestBody CreateLessonProgressRequest request) {
        LessonProgress progress = progressService.createOrUpdateProgress(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(progress);
    }
}
