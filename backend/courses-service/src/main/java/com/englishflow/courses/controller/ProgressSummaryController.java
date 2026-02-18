package com.englishflow.courses.controller;

import com.englishflow.courses.dto.*;
import com.englishflow.courses.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressSummaryController {
    
    private final ICourseEnrollmentService enrollmentService;
    private final IChapterProgressService chapterProgressService;
    private final ILessonProgressService lessonProgressService;
    private final ICourseService courseService;
    
    @GetMapping("/summary")
    public ResponseEntity<StudentProgressSummaryDTO> getProgressSummary(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        
        // Get enrollment details
        CourseEnrollmentDTO enrollment = enrollmentService.getEnrollment(studentId, courseId);
        
        // Get course details
        CourseDTO course = courseService.getCourseById(courseId);
        
        // Get chapter progress
        List<ChapterProgressDTO> chapterProgress = chapterProgressService.getStudentCourseChapterProgress(studentId, courseId);
        
        // Get lesson progress
        List<LessonProgressDTO> lessonProgress = lessonProgressService.getStudentCourseLessonProgress(studentId, courseId);
        
        // Calculate stats
        StudentProgressSummaryDTO.ProgressStatsDTO stats = new StudentProgressSummaryDTO.ProgressStatsDTO();
        stats.setTotalLessons(enrollment.getTotalLessons());
        stats.setCompletedLessons(enrollment.getCompletedLessons());
        stats.setTotalChapters(chapterProgress.size());
        stats.setCompletedChapters((int) chapterProgress.stream().filter(ChapterProgressDTO::getIsCompleted).count());
        stats.setOverallProgress(enrollment.getProgress());
        stats.setTotalTimeSpentMinutes(lessonProgress.stream()
                .mapToInt(lp -> lp.getTimeSpentMinutes() != null ? lp.getTimeSpentMinutes() : 0)
                .sum());
        
        // Build summary
        StudentProgressSummaryDTO summary = new StudentProgressSummaryDTO();
        summary.setStudentId(studentId);
        summary.setCourseId(courseId);
        summary.setCourseTitle(course.getTitle());
        summary.setEnrollment(enrollment);
        summary.setChapterProgress(chapterProgress);
        summary.setLessonProgress(lessonProgress);
        summary.setStats(stats);
        
        return ResponseEntity.ok(summary);
    }
    
    @PostMapping("/demo")
    public ResponseEntity<String> createDemoProgress(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        
        try {
            // Enroll student if not already enrolled
            if (!enrollmentService.isStudentEnrolled(studentId, courseId)) {
                enrollmentService.enrollStudent(studentId, courseId);
            }
            
            // Get first few lessons and mark some as completed for demo
            List<LessonProgressDTO> lessons = lessonProgressService.getStudentCourseLessonProgress(studentId, courseId);
            
            if (lessons.isEmpty()) {
                // Start first lesson
                lessonProgressService.startLesson(studentId, 1L); // Assuming lesson ID 1 exists
                lessonProgressService.updateProgress(studentId, 1L, 50.0, 15);
                
                // Complete second lesson if it exists
                try {
                    lessonProgressService.startLesson(studentId, 2L);
                    lessonProgressService.completeLesson(studentId, 2L);
                } catch (Exception e) {
                    // Lesson 2 might not exist, ignore
                }
            }
            
            return ResponseEntity.ok("Demo progress created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating demo progress: " + e.getMessage());
        }
    }
}