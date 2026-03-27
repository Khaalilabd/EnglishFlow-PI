package com.jungle.learning.repository;

import com.jungle.learning.model.ReadingProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Long> {
    
    Optional<ReadingProgress> findByEbookIdAndUserId(Long ebookId, Long userId);
    
    List<ReadingProgress> findByUserId(Long userId);
    
    List<ReadingProgress> findByUserIdAndIsCompleted(Long userId, Boolean isCompleted);
    
    List<ReadingProgress> findByUserIdOrderByLastReadAtDesc(Long userId);
    
    @Query("SELECT rp FROM ReadingProgress rp WHERE rp.userId = :userId AND rp.progressPercentage > 0 AND rp.isCompleted = false ORDER BY rp.lastReadAt DESC")
    List<ReadingProgress> findInProgressByUserId(Long userId);
    
    Long countByUserIdAndIsCompleted(Long userId, Boolean isCompleted);
}
