package com.englishflow.gamification.repository;

import com.englishflow.gamification.entity.Badge;
import com.englishflow.gamification.entity.BadgeType;
import com.englishflow.gamification.entity.BadgeRarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    
    Optional<Badge> findByCode(String code);
    
    List<Badge> findByType(BadgeType type);
    
    List<Badge> findByRarity(BadgeRarity rarity);
    
    List<Badge> findByIsActiveTrue();
    
    List<Badge> findByTypeAndIsActiveTrue(BadgeType type);
}
