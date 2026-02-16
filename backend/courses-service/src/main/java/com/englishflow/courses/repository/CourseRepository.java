package com.englishflow.courses.repository;

import com.englishflow.courses.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByIsPublished(Boolean isPublished);
    
    List<Course> findByInstructor(String instructor);
    
    List<Course> findByLevel(String level);
    
    List<Course> findByTitleContainingIgnoreCase(String title);
}

