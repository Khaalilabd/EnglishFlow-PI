package com.englishflow.gamification.service;

import com.englishflow.gamification.entity.Badge;
import com.englishflow.gamification.entity.BadgeRarity;
import com.englishflow.gamification.entity.BadgeType;
import com.englishflow.gamification.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BadgeInitializationService implements CommandLineRunner {
    
    private final BadgeRepository badgeRepository;
    
    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing badges...");
        
        if (badgeRepository.count() > 0) {
            log.info("Badges already initialized, skipping...");
            return;
        }
        
        List<Badge> badges = new ArrayList<>();
        
        // ========== BADGES DE NIVEAU (ÉVALUATION) ==========
        badges.add(createBadge("LEVEL_A1", "Seedling", "🌱", 
            "Beginner level - Starting your English journey", 
            BadgeType.LEVEL, BadgeRarity.COMMON, 0));
            
        badges.add(createBadge("LEVEL_A2", "Sprout", "🌿", 
            "Elementary level - Growing your skills", 
            BadgeType.LEVEL, BadgeRarity.COMMON, 50));
            
        badges.add(createBadge("LEVEL_B1", "Growing Tree", "🌳", 
            "Intermediate level - Building strong foundations", 
            BadgeType.LEVEL, BadgeRarity.RARE, 100));
            
        badges.add(createBadge("LEVEL_B2", "Young Lion", "🦁", 
            "Upper Intermediate - Gaining confidence", 
            BadgeType.LEVEL, BadgeRarity.RARE, 150));
            
        badges.add(createBadge("LEVEL_C1", "Jungle King", "👑", 
            "Advanced level - Mastering the language", 
            BadgeType.LEVEL, BadgeRarity.EPIC, 200));
            
        badges.add(createBadge("LEVEL_C2", "Master of the Jungle", "🏆", 
            "Proficient level - Complete mastery", 
            BadgeType.LEVEL, BadgeRarity.LEGENDARY, 500));
        
        // ========== BADGES DE NIVEAU (CERTIFIÉ) ==========
        badges.add(createBadge("CERTIFIED_A1", "Certified Seedling", "⭐🌱", 
            "Officially certified A1 level", 
            BadgeType.CERTIFICATION, BadgeRarity.RARE, 100));
            
        badges.add(createBadge("CERTIFIED_A2", "Certified Sprout", "⭐🌿", 
            "Officially certified A2 level", 
            BadgeType.CERTIFICATION, BadgeRarity.RARE, 150));
            
        badges.add(createBadge("CERTIFIED_B1", "Certified Tree", "⭐🌳", 
            "Officially certified B1 level", 
            BadgeType.CERTIFICATION, BadgeRarity.EPIC, 200));
            
        badges.add(createBadge("CERTIFIED_B2", "Certified Lion", "⭐🦁", 
            "Officially certified B2 level", 
            BadgeType.CERTIFICATION, BadgeRarity.EPIC, 250));
            
        badges.add(createBadge("CERTIFIED_C1", "Certified King", "⭐👑", 
            "Officially certified C1 level", 
            BadgeType.CERTIFICATION, BadgeRarity.LEGENDARY, 300));
            
        badges.add(createBadge("CERTIFIED_C2", "Certified Master", "⭐🏆", 
            "Officially certified C2 level", 
            BadgeType.CERTIFICATION, BadgeRarity.LEGENDARY, 500));
        
        // ========== BADGES DE PROGRESSION ==========
        badges.add(createBadge("RISING_STAR", "Rising Star", "🌟", 
            "Reached A2 level - You're on your way!", 
            BadgeType.ACHIEVEMENT, BadgeRarity.COMMON, 50));
            
        badges.add(createBadge("INTERMEDIATE_ACHIEVER", "Intermediate Achiever", "🎯", 
            "Reached B1 level - Solid progress!", 
            BadgeType.ACHIEVEMENT, BadgeRarity.RARE, 100));
            
        badges.add(createBadge("ADVANCED_LEARNER", "Advanced Learner", "🦁", 
            "Reached B2 level - Almost there!", 
            BadgeType.ACHIEVEMENT, BadgeRarity.EPIC, 150));
            
        badges.add(createBadge("EXPERT", "Expert", "👑", 
            "Reached C1 level - You're an expert!", 
            BadgeType.ACHIEVEMENT, BadgeRarity.EPIC, 200));
            
        badges.add(createBadge("MASTER", "Master", "🏆", 
            "Reached C2 level - Complete mastery!", 
            BadgeType.ACHIEVEMENT, BadgeRarity.LEGENDARY, 500));
        
        // ========== BADGES D'ACTIVITÉ ==========
        badges.add(createBadge("PACK_COLLECTOR", "Pack Collector", "📚", 
            "Purchased 5 learning packs", 
            BadgeType.ACHIEVEMENT, BadgeRarity.COMMON, 30));
            
        badges.add(createBadge("DEDICATED_LEARNER", "Dedicated Learner", "🎓", 
            "Completed 10 learning packs", 
            BadgeType.ACHIEVEMENT, BadgeRarity.RARE, 100));
            
        badges.add(createBadge("SPEED_LEARNER", "Speed Learner", "⚡", 
            "Completed a pack in less than 7 days", 
            BadgeType.ACHIEVEMENT, BadgeRarity.RARE, 50));
            
        badges.add(createBadge("PERFECT_SCORE", "Perfect Score", "🎯", 
            "Achieved 100% on an exam", 
            BadgeType.ACHIEVEMENT, BadgeRarity.EPIC, 100));
            
        badges.add(createBadge("PERSISTENT", "Persistent", "💪", 
            "Completed 50 lessons", 
            BadgeType.ACHIEVEMENT, BadgeRarity.RARE, 75));
            
        badges.add(createBadge("CHAMPION", "Champion", "🏅", 
            "Obtained C2 certification", 
            BadgeType.ACHIEVEMENT, BadgeRarity.LEGENDARY, 500));
        
        // ========== BADGES DE SÉRIE (STREAK) ==========
        badges.add(createBadge("STREAK_7", "On Fire", "🔥", 
            "7 consecutive days of activity", 
            BadgeType.STREAK, BadgeRarity.COMMON, 30));
            
        badges.add(createBadge("STREAK_30", "Dedicated", "🔥🔥", 
            "30 consecutive days of activity", 
            BadgeType.STREAK, BadgeRarity.RARE, 100));
            
        badges.add(createBadge("STREAK_100", "Unstoppable", "🔥🔥🔥", 
            "100 consecutive days of activity", 
            BadgeType.STREAK, BadgeRarity.LEGENDARY, 500));
        
        // ========== BADGES SPÉCIAUX ==========
        badges.add(createBadge("EARLY_BIRD", "Early Bird", "🎁", 
            "Made first purchase within 24h of registration", 
            BadgeType.SPECIAL, BadgeRarity.RARE, 50));
            
        badges.add(createBadge("SMART_SHOPPER", "Smart Shopper", "💰", 
            "Purchased a complete bundle", 
            BadgeType.SPECIAL, BadgeRarity.RARE, 75));
            
        badges.add(createBadge("DIVERSITY_SEEKER", "Diversity Seeker", "🌈", 
            "Purchased packs from all levels", 
            BadgeType.SPECIAL, BadgeRarity.EPIC, 150));
            
        badges.add(createBadge("VIP_LEARNER", "VIP Learner", "⭐", 
            "Spent over 500€ on the platform", 
            BadgeType.SPECIAL, BadgeRarity.EPIC, 200));
            
        badges.add(createBadge("ANNIVERSARY", "Anniversary", "🎊", 
            "1 year on the platform", 
            BadgeType.SPECIAL, BadgeRarity.LEGENDARY, 300));
            
        badges.add(createBadge("PREMIUM_MEMBER", "Premium Member", "💎", 
            "Purchased a certification exam", 
            BadgeType.SPECIAL, BadgeRarity.RARE, 100));
        
        // ========== BADGES DE FIDÉLITÉ ==========
        badges.add(createBadge("LOYALTY_SILVER", "Silver Member", "🥈", 
            "Reached Silver loyalty tier", 
            BadgeType.SPECIAL, BadgeRarity.RARE, 100));
            
        badges.add(createBadge("LOYALTY_GOLD", "Gold Member", "🥇", 
            "Reached Gold loyalty tier", 
            BadgeType.SPECIAL, BadgeRarity.EPIC, 200));
            
        badges.add(createBadge("LOYALTY_PLATINUM", "Platinum Member", "💎", 
            "Reached Platinum loyalty tier", 
            BadgeType.SPECIAL, BadgeRarity.LEGENDARY, 500));
        
        // ========== BADGES DE MONTÉE DE NIVEAU ==========
        badges.add(createBadge("LEVEL_UP_MASTER", "Level Up Master", "🚀", 
            "Advanced 3 levels", 
            BadgeType.ACHIEVEMENT, BadgeRarity.EPIC, 200));
        
        // Sauvegarder tous les badges
        badgeRepository.saveAll(badges);
        
        log.info("Successfully initialized {} badges", badges.size());
    }
    
    private Badge createBadge(String code, String name, String icon, String description, 
                             BadgeType type, BadgeRarity rarity, int coinsReward) {
        return Badge.builder()
                .code(code)
                .name(name)
                .icon(icon)
                .description(description)
                .type(type)
                .rarity(rarity)
                .coinsReward(coinsReward)
                .isActive(true)
                .build();
    }
}
