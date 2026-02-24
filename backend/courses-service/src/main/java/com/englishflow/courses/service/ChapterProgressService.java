package com.englishflow.courses.service;

import com.englishflow.courses.dto.ChapterProgressDTO;
import com.englishflow.courses.entity.Chapter;
import com.englishflow.courses.entity.ChapterProgress;
import com.englishflow.courses.repository.ChapterProgressRepository;
import com.englishflow.courses.repository.ChapterRepository;
import com.englishflow.courses.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChapterProgressService implements IChapterProgressService {
    
    private final ChapterProgressRepository chapterProgressRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final ILessonProgressService lessonProgressService;
    
    public ChapterProgressService(
            ChapterProgressRepository chapterProgressRepository,
            ChapterRepository chapterRepository,
            LessonRepository lessonRepository,
            @Lazy ILessonProgressService lessonProgressService) {
        this.chapterProgressRepository = chapterProgressRepository;
        this.chapterRepository = chapterRepository;
        this.lessonRepository = lessonRepository;
        this.lessonProgressService = lessonProgressService;
    }
    
    @Override
    @Transactional
    public ChapterProgressDTO startChapter(Long studentId, Long chapterId) {
        // Check if chapter exists
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found with id: " + chapterId));
        
        // Check if progress already exists
        ChapterProgress existingProgress = chapterProgressRepository
                .findByStudentIdAndChapterId(studentId, chapterId)
                .orElse(null);
        
        if (existingProgress != null) {
            // Update last accessed time
            existingProgress.setLastAccessedAt(LocalDateTime.now());
            ChapterProgress updated = chapterProgressRepository.save(existingProgress);
            return mapToDTO(updated);
        }
        
        // Create new progress record
        ChapterProgress progress = new ChapterProgress();
        progress.setStudentId(studentId);
        progress.setChapter(chapter);
        progress.setIsCompleted(false);
        progress.setCompletedLessons(0);
        
        // Count total lessons in this chapter
        Long totalLessons = lessonRepository.countByChapterId(chapterId);
        progress.setTotalLessons(totalLessons.intValue());
        progress.setProgressPercentage(0.0);
        
        ChapterProgress savedProgress = chapterProgressRepository.save(progress);
        return mapToDTO(savedProgress);
    }
    
    @Override
    @Transactional
    public ChapterProgressDTO updateChapterProgress(Long studentId, Long chapterId) {
        ChapterProgress progress = chapterProgressRepository
                .findByStudentIdAndChapterId(studentId, chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter progress not found"));
        
        // Count completed lessons in this chapter
        Long completedLessons = lessonProgressService.countCompletedLessonsInChapter(studentId, chapterId);
        progress.setCompletedLessons(completedLessons.intValue());
        
        // Calculate progress percentage
        if (progress.getTotalLessons() > 0) {
            double progressPercentage = (completedLessons.doubleValue() / progress.getTotalLessons()) * 100.0;
            progress.setProgressPercentage(progressPercentage);
            
            // Mark as completed if all lessons are done
            if (completedLessons.equals(Long.valueOf(progress.getTotalLessons())) && !progress.getIsCompleted()) {
                progress.setIsCompleted(true);
                progress.setCompletedAt(LocalDateTime.now());
            }
        }
        
        ChapterProgress updatedProgress = chapterProgressRepository.save(progress);
        return mapToDTO(updatedProgress);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ChapterProgressDTO getChapterProgress(Long studentId, Long chapterId) {
        ChapterProgress progress = chapterProgressRepository
                .findByStudentIdAndChapterId(studentId, chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter progress not found"));
        
        return mapToDTO(progress);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChapterProgressDTO> getStudentChapterProgress(Long studentId) {
        return chapterProgressRepository.findByStudentId(studentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChapterProgressDTO> getStudentCourseChapterProgress(Long studentId, Long courseId) {
        return chapterProgressRepository.findByStudentIdAndCourseId(studentId, courseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasStartedChapter(Long studentId, Long chapterId) {
        return chapterProgressRepository.existsByStudentIdAndChapterId(studentId, chapterId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long countCompletedChaptersInCourse(Long studentId, Long courseId) {
        return chapterProgressRepository.countCompletedChaptersByStudentAndCourse(studentId, courseId);
    }
    
    @Override
    @Transactional
    public void updateCourseChapterProgress(Long studentId, Long courseId) {
        List<ChapterProgress> chapterProgresses = chapterProgressRepository
                .findByStudentIdAndCourseId(studentId, courseId);
        
        for (ChapterProgress progress : chapterProgresses) {
            updateChapterProgress(studentId, progress.getChapter().getId());
        }
    }
    
    private ChapterProgressDTO mapToDTO(ChapterProgress progress) {
        ChapterProgressDTO dto = new ChapterProgressDTO();
        dto.setId(progress.getId());
        dto.setStudentId(progress.getStudentId());
        dto.setChapterId(progress.getChapter().getId());
        dto.setChapterTitle(progress.getChapter().getTitle());
        dto.setIsCompleted(progress.getIsCompleted());
        dto.setStartedAt(progress.getStartedAt());
        dto.setCompletedAt(progress.getCompletedAt());
        dto.setLastAccessedAt(progress.getLastAccessedAt());
        dto.setCompletedLessons(progress.getCompletedLessons());
        dto.setTotalLessons(progress.getTotalLessons());
        dto.setProgressPercentage(progress.getProgressPercentage());
        return dto;
    }
}