package com.englishflow.auth.repository;

import com.englishflow.auth.entity.StudentAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAnalyticsRepository extends JpaRepository<StudentAnalytics, Long> {
    
    Optional<StudentAnalytics> findByUserId(Long userId);
    
    List<StudentAnalytics> findByUserIdIn(List<Long> userIds);
    
    boolean existsByUserId(Long userId);
    
    void deleteByUserId(Long userId);
}
