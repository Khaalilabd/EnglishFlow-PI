package com.englishflow.courses.repository;

import com.englishflow.courses.entity.Course;
import com.englishflow.courses.enums.CourseStatus;
import com.englishflow.courses.enums.EnglishLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByLevel(EnglishLevel level);
    List<Course> findByTutorId(Long tutorId);
    List<Course> findByStatusAndLevel(CourseStatus status, EnglishLevel level);
}
