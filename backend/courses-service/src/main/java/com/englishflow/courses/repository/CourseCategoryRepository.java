package com.englishflow.courses.repository;

import com.englishflow.courses.entity.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
    
    List<CourseCategory> findByActiveOrderByDisplayOrderAsc(Boolean active);
    
    List<CourseCategory> findAllByOrderByDisplayOrderAsc();
    
    Optional<CourseCategory> findByName(String name);
    
    boolean existsByName(String name);
}
