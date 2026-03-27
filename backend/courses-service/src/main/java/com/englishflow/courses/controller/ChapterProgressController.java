package com.englishflow.courses.controller;

import com.englishflow.courses.dto.ChapterProgressDTO;
import com.englishflow.courses.service.IChapterProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chapter-progress")
@RequiredArgsConstructor
public class ChapterProgressController {
    
    private final IChapterProgressService chapterProgressService;
    
    @PostMapping("/start")
    public ResponseEntity<ChapterProgressDTO> startChapter(
            @RequestParam Long studentId,
            @RequestParam Long chapterId) {
        ChapterProgressDTO progress = chapterProgressService.startChapter(studentId, chapterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(progress);
    }
    
    @PutMapping("/update")
    public ResponseEntity<ChapterProgressDTO> updateChapterProgress(
            @RequestParam Long studentId,
            @RequestParam Long chapterId) {
        ChapterProgressDTO progress = chapterProgressService.updateChapterProgress(studentId, chapterId);
        return ResponseEntity.ok(progress);
    }
    
    @GetMapping
    public ResponseEntity<ChapterProgressDTO> getChapterProgress(
            @RequestParam Long studentId,
            @RequestParam Long chapterId) {
        ChapterProgressDTO progress = chapterProgressService.getChapterProgress(studentId, chapterId);
        return ResponseEntity.ok(progress);
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ChapterProgressDTO>> getStudentChapterProgress(@PathVariable Long studentId) {
        List<ChapterProgressDTO> progress = chapterProgressService.getStudentChapterProgress(studentId);
        return ResponseEntity.ok(progress);
    }
    
    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<List<ChapterProgressDTO>> getStudentCourseChapterProgress(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        List<ChapterProgressDTO> progress = chapterProgressService.getStudentCourseChapterProgress(studentId, courseId);
        return ResponseEntity.ok(progress);
    }
    
    @GetMapping("/check")
    public ResponseEntity<Boolean> hasStartedChapter(
            @RequestParam Long studentId,
            @RequestParam Long chapterId) {
        boolean started = chapterProgressService.hasStartedChapter(studentId, chapterId);
        return ResponseEntity.ok(started);
    }
    
    @GetMapping("/count/course")
    public ResponseEntity<Long> countCompletedChaptersInCourse(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        Long count = chapterProgressService.countCompletedChaptersInCourse(studentId, courseId);
        return ResponseEntity.ok(count);
    }
    
    @PutMapping("/update-course")
    public ResponseEntity<Void> updateCourseChapterProgress(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        chapterProgressService.updateCourseChapterProgress(studentId, courseId);
        return ResponseEntity.ok().build();
    }
}