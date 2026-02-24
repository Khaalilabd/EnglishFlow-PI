package com.englishflow.courses.repository;

import com.englishflow.courses.entity.Course;
import com.englishflow.courses.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByLevel(String level);
    List<Course> findByTutorId(Long tutorId);
    List<Course> findByCategory(String category);
    List<Course> findByStatusAndLevel(CourseStatus status, String level);
    List<Course> findByCategoryAndLevel(String category, String level);
    
    @Query("SELECT c FROM Course c WHERE c.tutorId = :tutorId AND c.category = :category")
    List<Course> findByTutorIdAndCategory(@Param("tutorId") Long tutorId, @Param("category") String category);
    
    @Query("SELECT c FROM Course c WHERE c.status = :status AND c.category = :category AND c.level = :level")
    List<Course> findByStatusAndCategoryAndLevel(
        @Param("status") CourseStatus status, 
        @Param("category") String category, 
        @Param("level") String level
    );
}
