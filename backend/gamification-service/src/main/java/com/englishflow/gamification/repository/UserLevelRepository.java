package com.englishflow.gamification.repository;

import com.englishflow.gamification.entity.UserLevel;
import com.englishflow.gamification.entity.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {
    
    Optional<UserLevel> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
    
    List<UserLevel> findByLoyaltyTier(LoyaltyTier tier);
    
    @Query("SELECT ul FROM UserLevel ul ORDER BY ul.totalXP DESC")
    List<UserLevel> findTopByTotalXPOrderByTotalXPDesc();
    
    @Query("SELECT ul FROM UserLevel ul WHERE ul.totalXP >= :minXP ORDER BY ul.totalXP DESC")
    List<UserLevel> findUsersWithMinXP(int minXP);
    
    @Query("SELECT COUNT(ul) FROM UserLevel ul WHERE ul.totalXP > :xp")
    long countUsersWithMoreXP(int xp);
}
