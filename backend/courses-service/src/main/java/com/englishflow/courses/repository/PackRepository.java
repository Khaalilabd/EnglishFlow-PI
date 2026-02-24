package com.englishflow.courses.repository;

import com.englishflow.courses.entity.Pack;
import com.englishflow.courses.enums.PackStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackRepository extends JpaRepository<Pack, Long> {
    
    List<Pack> findByTutorId(Long tutorId);
    
    List<Pack> findByStatus(PackStatus status);
    
    List<Pack> findByCategoryAndLevel(String category, String level);
    
    @Query("SELECT p FROM Pack p WHERE p.category = :category AND p.level = :level AND p.status = 'ACTIVE' AND p.currentEnrolledStudents < p.maxStudents")
    List<Pack> findAvailablePacksByCategoryAndLevel(@Param("category") String category, @Param("level") String level);
    
    @Query("SELECT p FROM Pack p WHERE p.status = 'ACTIVE' AND p.currentEnrolledStudents < p.maxStudents")
    List<Pack> findAllAvailablePacks();
    
    List<Pack> findByCreatedBy(Long academicId);
}
