package com.englishflow.courses.service;

import com.englishflow.courses.dto.ChapterDTO;
import com.englishflow.courses.entity.Chapter;
import com.englishflow.courses.entity.Course;
import com.englishflow.courses.repository.ChapterRepository;
import com.englishflow.courses.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterService implements IChapterService {
    
    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<ChapterDTO> getAllChapters() {
        return chapterRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ChapterDTO getChapterById(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found with id: " + id));
        return mapToDTO(chapter);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChapterDTO> getChaptersByCourse(Long courseId) {
        return chapterRepository.findByCourseIdOrderByOrderIndexAsc(courseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChapterDTO> getPublishedChaptersByCourse(Long courseId) {
        return chapterRepository.findByCourseIdAndIsPublished(courseId, true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ChapterDTO createChapter(ChapterDTO chapterDTO) {
        Course course = courseRepository.findById(chapterDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + chapterDTO.getCourseId()));
        
        Chapter chapter = mapToEntity(chapterDTO);
        chapter.setCourse(course);
        Chapter savedChapter = chapterRepository.save(chapter);
        return mapToDTO(savedChapter);
    }
    
    @Override
    @Transactional
    public ChapterDTO updateChapter(Long id, ChapterDTO chapterDTO) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found with id: " + id));
        
        chapter.setTitle(chapterDTO.getTitle());
        chapter.setDescription(chapterDTO.getDescription());
        chapter.setObjectives(chapterDTO.getObjectives());
        chapter.setOrderIndex(chapterDTO.getOrderIndex());
        chapter.setEstimatedDuration(chapterDTO.getEstimatedDuration());
        chapter.setIsPublished(chapterDTO.getIsPublished());
        
        Chapter updatedChapter = chapterRepository.save(chapter);
        return mapToDTO(updatedChapter);
    }
    
    @Override
    @Transactional
    public void deleteChapter(Long id) {
        if (!chapterRepository.existsById(id)) {
            throw new RuntimeException("Chapter not found with id: " + id);
        }
        chapterRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return chapterRepository.existsById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean belongsToCourse(Long chapterId, Long courseId) {
        return chapterRepository.findById(chapterId)
                .map(chapter -> chapter.getCourse().getId().equals(courseId))
                .orElse(false);
    }
    
    private ChapterDTO mapToDTO(Chapter chapter) {
        ChapterDTO dto = new ChapterDTO();
        dto.setId(chapter.getId());
        dto.setTitle(chapter.getTitle());
        dto.setDescription(chapter.getDescription());
        dto.setObjectives(chapter.getObjectives());
        dto.setOrderIndex(chapter.getOrderIndex());
        dto.setEstimatedDuration(chapter.getEstimatedDuration());
        dto.setIsPublished(chapter.getIsPublished());
        dto.setCourseId(chapter.getCourse().getId());
        dto.setCreatedAt(chapter.getCreatedAt());
        dto.setUpdatedAt(chapter.getUpdatedAt());
        return dto;
    }
    
    private Chapter mapToEntity(ChapterDTO dto) {
        Chapter chapter = new Chapter();
        chapter.setTitle(dto.getTitle());
        chapter.setDescription(dto.getDescription());
        chapter.setObjectives(dto.getObjectives());
        chapter.setOrderIndex(dto.getOrderIndex());
        chapter.setEstimatedDuration(dto.getEstimatedDuration());
        chapter.setIsPublished(dto.getIsPublished() != null ? dto.getIsPublished() : false);
        return chapter;
    }
}
