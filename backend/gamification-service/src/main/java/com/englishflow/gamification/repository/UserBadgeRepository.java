package com.englishflow.gamification.repository;

import com.englishflow.gamification.entity.UserBadge;
import com.englishflow.gamification.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    
    List<UserBadge> findByUserId(Long userId);
    
    List<UserBadge> findByUserIdAndIsDisplayedTrue(Long userId);
    
    List<UserBadge> findByUserIdAndIsNewTrue(Long userId);
    
    Optional<UserBadge> findByUserIdAndBadge(Long userId, Badge badge);
    
    boolean existsByUserIdAndBadge(Long userId, Badge badge);
    
    @Query("SELECT COUNT(ub) FROM UserBadge ub WHERE ub.userId = :userId")
    long countByUserId(Long userId);
    
    @Query("SELECT ub FROM UserBadge ub WHERE ub.userId = :userId ORDER BY ub.earnedAt DESC")
    List<UserBadge> findByUserIdOrderByEarnedAtDesc(Long userId);
}
