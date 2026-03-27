package com.englishflow.courses.repository;

import com.englishflow.courses.entity.TutorAvailability;
import com.englishflow.courses.enums.TutorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorAvailabilityRepository extends JpaRepository<TutorAvailability, Long> {
    
    Optional<TutorAvailability> findByTutorId(Long tutorId);
    
    List<TutorAvailability> findByStatus(TutorStatus status);
    
    @Query("SELECT ta FROM TutorAvailability ta WHERE :category MEMBER OF ta.categories AND :level MEMBER OF ta.levels AND ta.status = 'AVAILABLE'")
    List<TutorAvailability> findAvailableTutorsByCategoryAndLevel(@Param("category") String category, @Param("level") String level);
    
    @Query("SELECT ta FROM TutorAvailability ta WHERE ta.currentStudentsCount < ta.maxStudentsCapacity AND ta.status = 'AVAILABLE'")
    List<TutorAvailability> findTutorsWithCapacity();
}
