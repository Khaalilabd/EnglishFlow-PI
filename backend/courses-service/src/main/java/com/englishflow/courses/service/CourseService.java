package com.englishflow.courses.service;

import com.englishflow.courses.dto.CourseDTO;
import com.englishflow.courses.entity.Course;
import com.englishflow.courses.entity.Chapter;
import com.englishflow.courses.enums.CourseStatus;
import com.englishflow.courses.repository.CourseRepository;
import com.englishflow.courses.repository.ChapterRepository;
import com.englishflow.courses.repository.LessonRepository;
import com.englishflow.courses.repository.CourseEnrollmentRepository;
import com.englishflow.courses.repository.LessonProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService implements ICourseService {
    
    private final CourseRepository courseRepository;
    private final UserValidationService userValidationService;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CourseDTO> getAllCoursesPaginated(Pageable pageable) {
        return courseRepository.findAll(pageable)
                .map(this::mapToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "courseDetails", key = "#id")
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        return mapToDTO(course);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "courses", key = "'published'")
    public List<CourseDTO> getPublishedCourses() {
        return courseRepository.findByStatus(CourseStatus.PUBLISHED).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByLevel(String level) {
        return courseRepository.findByLevel(level).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByStatus(CourseStatus status) {
        return courseRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "courses", allEntries = true),
        @CacheEvict(value = "courseDetails", allEntries = true)
    })
    public CourseDTO createCourse(CourseDTO courseDTO) {
        // Validate tutor exists and has TUTOR role
        if (courseDTO.getTutorId() != null) {
            userValidationService.validateTutorExists(courseDTO.getTutorId());
        }
        
        Course course = mapToEntity(courseDTO);
        Course savedCourse = courseRepository.save(course);
        return mapToDTO(savedCourse);
    }
    
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "courses", allEntries = true),
        @CacheEvict(value = "courseDetails", key = "#id")
    })
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        
        // Validate tutor if tutorId is being changed
        if (courseDTO.getTutorId() != null && !courseDTO.getTutorId().equals(course.getTutorId())) {
            userValidationService.validateTutorExists(courseDTO.getTutorId());
        }
        
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setCategory(courseDTO.getCategory());
        course.setLevel(courseDTO.getLevel());
        course.setMaxStudents(courseDTO.getMaxStudents());
        course.setSchedule(courseDTO.getSchedule());
        course.setDuration(courseDTO.getDuration());
        course.setTutorId(courseDTO.getTutorId());
        course.setPrice(courseDTO.getPrice());
        course.setFileUrl(courseDTO.getFileUrl());
        course.setThumbnailUrl(courseDTO.getThumbnailUrl());
        course.setObjectives(courseDTO.getObjectives());
        course.setPrerequisites(courseDTO.getPrerequisites());
        course.setIsFeatured(courseDTO.getIsFeatured());
        course.setStatus(courseDTO.getStatus());
        
        Course updatedCourse = courseRepository.save(course);
        return mapToDTO(updatedCourse);
    }
    
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "courses", allEntries = true),
        @CacheEvict(value = "courseDetails", key = "#id")
    })
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with id: " + id);
        }
        
        // Delete in correct order to avoid foreign key constraint violations
        
        // 1. Delete lesson progress for this course
        lessonProgressRepository.deleteByCourseId(id);
        
        // 2. Delete course enrollments
        courseEnrollmentRepository.deleteByCourseId(id);
        
        // 3. Get all chapters for this course
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByOrderIndexAsc(id);
        
        for (Chapter chapter : chapters) {
            // 4. Delete lessons for each chapter (this will cascade to lesson_media)
            lessonRepository.deleteByChapterId(chapter.getId());
            
            // 5. Delete chapter (this will cascade to chapter_objectives)
            chapterRepository.deleteById(chapter.getId());
        }
        
        // 6. Finally delete the course
        courseRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return courseRepository.existsById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByTutor(Long tutorId) {
        return courseRepository.findByTutorId(tutorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    private CourseDTO mapToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCategory(course.getCategory());
        dto.setLevel(course.getLevel());
        dto.setMaxStudents(course.getMaxStudents());
        dto.setSchedule(course.getSchedule());
        dto.setDuration(course.getDuration());
        dto.setTutorId(course.getTutorId());
        dto.setPrice(course.getPrice());
        dto.setFileUrl(course.getFileUrl());
        dto.setThumbnailUrl(course.getThumbnailUrl());
        dto.setObjectives(course.getObjectives());
        dto.setPrerequisites(course.getPrerequisites());
        dto.setIsFeatured(course.getIsFeatured());
        dto.setStatus(course.getStatus());
        dto.setChapterCount(course.getChapterCount());
        dto.setLessonCount(course.getLessonCount());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());
        return dto;
    }
    
    private Course mapToEntity(CourseDTO dto) {
        Course course = new Course();
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCategory(dto.getCategory());
        course.setLevel(dto.getLevel());
        course.setMaxStudents(dto.getMaxStudents());
        course.setSchedule(dto.getSchedule());
        course.setDuration(dto.getDuration());
        course.setTutorId(dto.getTutorId());
        course.setPrice(dto.getPrice());
        course.setFileUrl(dto.getFileUrl());
        course.setThumbnailUrl(dto.getThumbnailUrl());
        course.setObjectives(dto.getObjectives());
        course.setPrerequisites(dto.getPrerequisites());
        course.setIsFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false);
        course.setStatus(dto.getStatus() != null ? dto.getStatus() : CourseStatus.DRAFT);
        return course;
    }
}
