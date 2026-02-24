package com.englishflow.courses.controller;

import com.englishflow.courses.dto.LessonProgressDTO;
import com.englishflow.courses.service.ILessonProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lesson-progress")
@RequiredArgsConstructor
public class LessonProgressController {
    
    private final ILessonProgressService lessonProgressService;
    
    @PostMapping("/start")
    public ResponseEntity<LessonProgressDTO> startLesson(
            @RequestParam Long studentId,
            @RequestParam Long lessonId) {
        LessonProgressDTO progress = lessonProgressService.startLesson(studentId, lessonId);
        return ResponseEntity.status(HttpStatus.CREATED).body(progress);
    }
    
    @PutMapping("/update")
    public ResponseEntity<LessonProgressDTO> updateProgress(
            @RequestParam Long studentId,
            @RequestParam Long lessonId,
            @RequestParam Double progressPercentage,
            @RequestParam(required = false) Integer timeSpentMinutes) {
        LessonProgressDTO progress = lessonProgressService.updateProgress(studentId, lessonId, progressPercentage, timeSpentMinutes);
        return ResponseEntity.ok(progress);
    }
    
    @PutMapping("/complete")
    public ResponseEntity<LessonProgressDTO> completeLesson(
            @RequestParam Long studentId,
            @RequestParam Long lessonId) {
        LessonProgressDTO progress = lessonProgressService.completeLesson(studentId, lessonId);
        return ResponseEntity.ok(progress);
    }
    
    @PutMapping("/notes")
    public ResponseEntity<LessonProgressDTO> addNotes(
            @RequestParam Long studentId,
            @RequestParam Long lessonId,
            @RequestBody String notes) {
        LessonProgressDTO progress = lessonProgressService.addNotes(studentId, lessonId, notes);
        return ResponseEntity.ok(progress);
    }
    
    @GetMapping
    public ResponseEntity<LessonProgressDTO> getLessonProgress(
            @RequestParam Long studentId,
            @RequestParam Long lessonId) {
        LessonProgressDTO progress = lessonProgressService.getLessonProgress(studentId, lessonId);
        return ResponseEntity.ok(progress);
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<LessonProgressDTO>> getStudentLessonProgress(@PathVariable Long studentId) {
        List<LessonProgressDTO> progress = lessonProgressService.getStudentLessonProgress(studentId);
        return ResponseEntity.ok(progress);
    }
    
    @GetMapping("/student/{studentId}/chapter/{chapterId}")
    public ResponseEntity<List<LessonProgressDTO>> getStudentChapterLessonProgress(
            @PathVariable Long studentId,
            @PathVariable Long chapterId) {
        List<LessonProgressDTO> progress = lessonProgressService.getStudentChapterLessonProgress(studentId, chapterId);
        return ResponseEntity.ok(progress);
    }
    
    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<List<LessonProgressDTO>> getStudentCourseLessonProgress(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        List<LessonProgressDTO> progress = lessonProgressService.getStudentCourseLessonProgress(studentId, courseId);
        return ResponseEntity.ok(progress);
    }
    
    @GetMapping("/check")
    public ResponseEntity<Boolean> hasStartedLesson(
            @RequestParam Long studentId,
            @RequestParam Long lessonId) {
        boolean started = lessonProgressService.hasStartedLesson(studentId, lessonId);
        return ResponseEntity.ok(started);
    }
    
    @GetMapping("/count/chapter")
    public ResponseEntity<Long> countCompletedLessonsInChapter(
            @RequestParam Long studentId,
            @RequestParam Long chapterId) {
        Long count = lessonProgressService.countCompletedLessonsInChapter(studentId, chapterId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/count/course")
    public ResponseEntity<Long> countCompletedLessonsInCourse(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        Long count = lessonProgressService.countCompletedLessonsInCourse(studentId, courseId);
        return ResponseEntity.ok(count);
    }
}