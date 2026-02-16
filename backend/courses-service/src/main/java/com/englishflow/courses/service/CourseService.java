package com.englishflow.courses.service;

import com.englishflow.courses.dto.CourseDTO;
import com.englishflow.courses.entity.Course;
import com.englishflow.courses.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = Course.builder()
                .title(courseDTO.getTitle())
                .description(courseDTO.getDescription())
                .thumbnailUrl(courseDTO.getThumbnailUrl())
                .instructor(courseDTO.getInstructor())
                .level(courseDTO.getLevel())
                .durationHours(courseDTO.getDurationHours())
                .price(courseDTO.getPrice())
                .isPublished(courseDTO.getIsPublished() != null ? courseDTO.getIsPublished() : false)
                .build();
        
        Course savedCourse = courseRepository.save(course);
        return mapToDTO(savedCourse);
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        return mapToDTO(course);
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getPublishedCourses() {
        return courseRepository.findByIsPublished(true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByInstructor(String instructor) {
        return courseRepository.findByInstructor(instructor).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByLevel(String level) {
        return courseRepository.findByLevel(level).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> searchCourses(String query) {
        return courseRepository.findByTitleContainingIgnoreCase(query).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        
        if (courseDTO.getTitle() != null) {
            course.setTitle(courseDTO.getTitle());
        }
        if (courseDTO.getDescription() != null) {
            course.setDescription(courseDTO.getDescription());
        }
        if (courseDTO.getThumbnailUrl() != null) {
            course.setThumbnailUrl(courseDTO.getThumbnailUrl());
        }
        if (courseDTO.getInstructor() != null) {
            course.setInstructor(courseDTO.getInstructor());
        }
        if (courseDTO.getLevel() != null) {
            course.setLevel(courseDTO.getLevel());
        }
        if (courseDTO.getDurationHours() != null) {
            course.setDurationHours(courseDTO.getDurationHours());
        }
        if (courseDTO.getPrice() != null) {
            course.setPrice(courseDTO.getPrice());
        }
        if (courseDTO.getIsPublished() != null) {
            course.setIsPublished(courseDTO.getIsPublished());
        }
        
        Course updatedCourse = courseRepository.save(course);
        return mapToDTO(updatedCourse);
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    private CourseDTO mapToDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .instructor(course.getInstructor())
                .level(course.getLevel())
                .durationHours(course.getDurationHours())
                .price(course.getPrice())
                .isPublished(course.getIsPublished())
                .enrolledCount(course.getEnrolledCount())
                .rating(course.getRating())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}

