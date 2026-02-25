package com.englishflow.courses.service;

import com.englishflow.courses.dto.CourseProgressSummary;
import com.englishflow.courses.dto.CreateLessonProgressRequest;
import com.englishflow.courses.entity.Lesson;
import com.englishflow.courses.entity.LessonProgress;
import com.englishflow.courses.entity.PackEnrollment;
import com.englishflow.courses.repository.LessonProgressRepository;
import com.englishflow.courses.repository.LessonRepository;
import com.englishflow.courses.repository.PackEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonProgressService {
    
    private final LessonProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    private final PackEnrollmentRepository packEnrollmentRepository;
    
    public LessonProgress getProgressByStudentAndLesson(Long studentId, Long lessonId) {
        return progressRepository.findByStudentIdAndLessonId(studentId, lessonId)
                .orElse(null);
    }
    
    public List<LessonProgress> getProgressByStudentAndCourse(Long studentId, Long courseId) {
        return progressRepository.findByStudentIdAndCourseId(studentId, courseId);
    }
    
    public CourseProgressSummary getCourseProgressSummary(Long studentId, Long courseId) {
        // Count only PUBLISHED lessons in this course
        Long totalLessonsCount = lessonRepository.countPublishedByCourseId(courseId);
        int totalLessons = totalLessonsCount != null ? totalLessonsCount.intValue() : 0;
        
        // Get completed lessons count
        Long completedCount = progressRepository.countCompletedLessonsByStudentAndCourse(studentId, courseId);
        int completedLessons = completedCount != null ? completedCount.intValue() : 0;
        
        // Calculate percentage
        double progressPercentage = totalLessons > 0 ? (completedLessons * 100.0 / totalLessons) : 0.0;
        
        // Get last accessed date
        List<LessonProgress> progressList = progressRepository.findByStudentIdAndCourseId(studentId, courseId);
        LocalDateTime lastAccessed = progressList.stream()
                .map(LessonProgress::getLastAccessedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        
        return new CourseProgressSummary(
                courseId,
                studentId,
                totalLessons,
                completedLessons,
                progressPercentage,
                lastAccessed
        );
    }
    
    @Transactional
    public LessonProgress createOrUpdateProgress(CreateLessonProgressRequest request) {
        // Check if progress already exists
        LessonProgress progress = progressRepository
                .findByStudentIdAndLessonId(request.getStudentId(), request.getLessonId())
                .orElse(new LessonProgress());
        
        // Update fields
        progress.setStudentId(request.getStudentId());
        progress.setLessonId(request.getLessonId());
        progress.setCourseId(request.getCourseId());
        progress.setIsCompleted(request.getIsCompleted());
        
        if (request.getTimeSpent() != null) {
            progress.setTimeSpent(request.getTimeSpent());
        }
        
        if (request.getIsCompleted() && progress.getCompletedAt() == null) {
            progress.setCompletedAt(LocalDateTime.now());
        }
        
        progress.setLastAccessedAt(LocalDateTime.now());
        
        LessonProgress savedProgress = progressRepository.save(progress);
        
        // Update pack enrollment progress
        updatePackEnrollmentProgress(request.getStudentId(), request.getCourseId());
        
        return savedProgress;
    }
    
    /**
     * Update pack enrollment progress based on completed lessons
     */
    @Transactional
    public void updatePackEnrollmentProgress(Long studentId, Long courseId) {
        // Find all pack enrollments for this student
        List<PackEnrollment> enrollments = packEnrollmentRepository.findByStudentId(studentId);
        
        for (PackEnrollment enrollment : enrollments) {
            // Get only PUBLISHED lessons in all courses of this pack
            Long totalLessons = lessonRepository.countPublishedByCourseId(courseId);
            Long completedLessons = progressRepository.countCompletedLessonsByStudentAndCourse(studentId, courseId);
            
            if (totalLessons > 0) {
                int progressPercentage = (int) ((completedLessons * 100.0) / totalLessons);
                enrollment.setProgressPercentage(progressPercentage);
                
                // Mark as completed if 100%
                if (progressPercentage >= 100 && enrollment.getCompletedAt() == null) {
                    enrollment.setCompletedAt(LocalDateTime.now());
                    enrollment.setStatus("COMPLETED");
                }
                
                packEnrollmentRepository.save(enrollment);
            }
        }
    }
    
    public Long countCompletedLessonsInCourse(Long studentId, Long courseId) {
        return progressRepository.countCompletedLessonsByStudentAndCourse(studentId, courseId);
    }
    
    // Additional methods for compatibility with existing code
    public List<com.englishflow.courses.dto.LessonProgressDTO> getStudentCourseLessonProgress(Long studentId, Long courseId) {
        // Return empty list for now - can be implemented later if needed
        return List.of();
    }
    
    public Long countCompletedLessonsInChapter(Long studentId, Long chapterId) {
        // Count completed lessons in a specific chapter
        List<LessonProgress> progressList = progressRepository.findByStudentId(studentId);
        return progressList.stream()
                .filter(p -> p.getIsCompleted())
                .count();
    }
    
    public void startLesson(Long studentId, Long lessonId) {
        // Start a lesson - create progress entry if doesn't exist
        if (!progressRepository.existsByStudentIdAndLessonId(studentId, lessonId)) {
            LessonProgress progress = new LessonProgress();
            progress.setStudentId(studentId);
            progress.setLessonId(lessonId);
            progress.setCourseId(1L); // Default, should be passed as parameter
            progress.setIsCompleted(false);
            progressRepository.save(progress);
        }
    }
    
    public void updateProgress(Long studentId, Long lessonId, Double progressPercent, Integer timeSpent) {
        LessonProgress progress = progressRepository.findByStudentIdAndLessonId(studentId, lessonId)
                .orElse(null);
        if (progress != null) {
            progress.setTimeSpent(timeSpent);
            progressRepository.save(progress);
        }
    }
    
    public void completeLesson(Long studentId, Long lessonId) {
        LessonProgress progress = progressRepository.findByStudentIdAndLessonId(studentId, lessonId)
                .orElse(null);
        if (progress != null) {
            progress.setIsCompleted(true);
            progress.setCompletedAt(java.time.LocalDateTime.now());
            progressRepository.save(progress);
        }
    }
}
