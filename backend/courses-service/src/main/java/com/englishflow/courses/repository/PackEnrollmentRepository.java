package com.englishflow.courses.repository;

import com.englishflow.courses.entity.PackEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackEnrollmentRepository extends JpaRepository<PackEnrollment, Long> {
    
    List<PackEnrollment> findByStudentId(Long studentId);
    
    List<PackEnrollment> findByPackId(Long packId);
    
    List<PackEnrollment> findByTutorId(Long tutorId);
    
    Optional<PackEnrollment> findByStudentIdAndPackId(Long studentId, Long packId);
    
    List<PackEnrollment> findByStudentIdAndIsActive(Long studentId, Boolean isActive);
    
    Long countByPackId(Long packId);
    
    Long countByTutorId(Long tutorId);
}
