package com.englishflow.courses.service;

import com.englishflow.courses.dto.LessonProgressDTO;
import com.englishflow.courses.entity.Lesson;
import com.englishflow.courses.entity.LessonProgress;
import com.englishflow.courses.repository.LessonProgressRepository;
import com.englishflow.courses.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonProgressService implements ILessonProgressService {
    
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;
    private final IChapterProgressService chapterProgressService;
    private final ICourseEnrollmentService courseEnrollmentService;
    
    public LessonProgressService(
            LessonProgressRepository lessonProgressRepository,
            LessonRepository lessonRepository,
            @Lazy IChapterProgressService chapterProgressService,
            @Lazy ICourseEnrollmentService courseEnrollmentService) {
        this.lessonProgressRepository = lessonProgressRepository;
        this.lessonRepository = lessonRepository;
        this.chapterProgressService = chapterProgressService;
        this.courseEnrollmentService = courseEnrollmentService;
    }
    
    @Override
    @Transactional
    public LessonProgressDTO startLesson(Long studentId, Long lessonId) {
        // Check if lesson exists
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + lessonId));
        
        // Check if progress already exists
        LessonProgress existingProgress = lessonProgressRepository
                .findByStudentIdAndLessonId(studentId, lessonId)
                .orElse(null);
        
        if (existingProgress != null) {
            // Update last accessed time
            existingProgress.setLastAccessedAt(LocalDateTime.now());
            LessonProgress updated = lessonProgressRepository.save(existingProgress);
            return mapToDTO(updated);
        }
        
        // Create new progress record
        LessonProgress progress = new LessonProgress();
        progress.setStudentId(studentId);
        progress.setLesson(lesson);
        progress.setIsCompleted(false);
        progress.setProgressPercentage(0.0);
        progress.setTimeSpentMinutes(0);
        
        LessonProgress savedProgress = lessonProgressRepository.save(progress);
        
        // Start chapter progress if not already started
        chapterProgressService.startChapter(studentId, lesson.getChapter().getId());
        
        return mapToDTO(savedProgress);
    }
    
    @Override
    @Transactional
    public LessonProgressDTO updateProgress(Long studentId, Long lessonId, Double progressPercentage, Integer timeSpentMinutes) {
        LessonProgress progress = lessonProgressRepository
                .findByStudentIdAndLessonId(studentId, lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson progress not found"));
        
        progress.setProgressPercentage(progressPercentage);
        if (timeSpentMinutes != null) {
            progress.setTimeSpentMinutes(progress.getTimeSpentMinutes() + timeSpentMinutes);
        }
        
        // Auto-complete if progress reaches 100%
        if (progressPercentage >= 100.0 && !progress.getIsCompleted()) {
            progress.setIsCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
        }
        
        LessonProgress updatedProgress = lessonProgressRepository.save(progress);
        
        // Update chapter progress
        chapterProgressService.updateChapterProgress(studentId, progress.getLesson().getChapter().getId());
        
        // Update course enrollment progress
        courseEnrollmentService.calculateAndUpdateProgress(studentId, progress.getLesson().getChapter().getCourse().getId());
        
        return mapToDTO(updatedProgress);
    }
    
    @Override
    @Transactional
    public LessonProgressDTO completeLesson(Long studentId, Long lessonId) {
        LessonProgress progress = lessonProgressRepository
                .findByStudentIdAndLessonId(studentId, lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson progress not found"));
        
        progress.setIsCompleted(true);
        progress.setProgressPercentage(100.0);
        progress.setCompletedAt(LocalDateTime.now());
        
        LessonProgress completedProgress = lessonProgressRepository.save(progress);
        
        // Update chapter progress
        chapterProgressService.updateChapterProgress(studentId, progress.getLesson().getChapter().getId());
        
        // Update course enrollment progress
        courseEnrollmentService.calculateAndUpdateProgress(studentId, progress.getLesson().getChapter().getCourse().getId());
        
        return mapToDTO(completedProgress);
    }
    
    @Override
    @Transactional
    public LessonProgressDTO addNotes(Long studentId, Long lessonId, String notes) {
        LessonProgress progress = lessonProgressRepository
                .findByStudentIdAndLessonId(studentId, lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson progress not found"));
        
        progress.setNotes(notes);
        LessonProgress updatedProgress = lessonProgressRepository.save(progress);
        
        return mapToDTO(updatedProgress);
    }
    
    @Override
    @Transactional(readOnly = true)
    public LessonProgressDTO getLessonProgress(Long studentId, Long lessonId) {
        LessonProgress progress = lessonProgressRepository
                .findByStudentIdAndLessonId(studentId, lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson progress not found"));
        
        return mapToDTO(progress);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LessonProgressDTO> getStudentLessonProgress(Long studentId) {
        return lessonProgressRepository.findByStudentId(studentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LessonProgressDTO> getStudentChapterLessonProgress(Long studentId, Long chapterId) {
        return lessonProgressRepository.findByStudentIdAndChapterId(studentId, chapterId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LessonProgressDTO> getStudentCourseLessonProgress(Long studentId, Long courseId) {
        return lessonProgressRepository.findByStudentIdAndCourseId(studentId, courseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasStartedLesson(Long studentId, Long lessonId) {
        return lessonProgressRepository.existsByStudentIdAndLessonId(studentId, lessonId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long countCompletedLessonsInChapter(Long studentId, Long chapterId) {
        return lessonProgressRepository.countCompletedLessonsByStudentAndChapter(studentId, chapterId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long countCompletedLessonsInCourse(Long studentId, Long courseId) {
        return lessonProgressRepository.countCompletedLessonsByStudentAndCourse(studentId, courseId);
    }
    
    private LessonProgressDTO mapToDTO(LessonProgress progress) {
        LessonProgressDTO dto = new LessonProgressDTO();
        dto.setId(progress.getId());
        dto.setStudentId(progress.getStudentId());
        dto.setLessonId(progress.getLesson().getId());
        dto.setLessonTitle(progress.getLesson().getTitle());
        dto.setIsCompleted(progress.getIsCompleted());
        dto.setStartedAt(progress.getStartedAt());
        dto.setCompletedAt(progress.getCompletedAt());
        dto.setLastAccessedAt(progress.getLastAccessedAt());
        dto.setTimeSpentMinutes(progress.getTimeSpentMinutes());
        dto.setProgressPercentage(progress.getProgressPercentage());
        dto.setNotes(progress.getNotes());
        return dto;
    }
}