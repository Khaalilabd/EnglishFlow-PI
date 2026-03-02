package com.englishflow.gamification.service;

import com.englishflow.gamification.dto.BadgeDTO;
import com.englishflow.gamification.dto.UserLevelDTO;
import com.englishflow.gamification.entity.*;
import com.englishflow.gamification.repository.BadgeRepository;
import com.englishflow.gamification.repository.UserBadgeRepository;
import com.englishflow.gamification.repository.UserLevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamificationService {
    
    private final UserLevelRepository userLevelRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    
    /**
     * Initialiser le niveau d'un nouvel utilisateur (sans niveau d'anglais)
     */
    @Transactional
    public UserLevelDTO initializeUserLevel(Long userId) {
        if (userLevelRepository.existsByUserId(userId)) {
            throw new RuntimeException("User level already exists");
        }
        
        UserLevel userLevel = UserLevel.builder()
                .userId(userId)
                .assessmentLevel(null) // Pas encore évalué
                .certifiedLevel(null)
                .hasCompletedAssessment(false)
                .currentXP(0)
                .totalXP(0)
                .jungleCoins(0)
                .loyaltyTier(LoyaltyTier.BRONZE)
                .totalSpent(0.0)
                .consecutiveDays(0)
                .lastActivityDate(LocalDateTime.now())
                .build();
        
        userLevel = userLevelRepository.save(userLevel);
        
        log.info("Initialized level for user {} without assessment", userId);
        return convertToDTO(userLevel);
    }
    
    /**
     * Initialiser le niveau d'un nouvel utilisateur avec un niveau initial (legacy)
     */
    @Transactional
    public UserLevelDTO initializeUserLevel(Long userId, EnglishLevel initialLevel) {
        if (userLevelRepository.existsByUserId(userId)) {
            throw new RuntimeException("User level already exists");
        }
        
        UserLevel userLevel = UserLevel.builder()
                .userId(userId)
                .assessmentLevel(initialLevel)
                .currentXP(0)
                .totalXP(0)
                .jungleCoins(0)
                .loyaltyTier(LoyaltyTier.BRONZE)
                .totalSpent(0.0)
                .consecutiveDays(0)
                .lastActivityDate(LocalDateTime.now())
                .build();
        
        userLevel = userLevelRepository.save(userLevel);
        
        // Attribuer le badge de niveau initial
        awardLevelBadge(userId, initialLevel, false);
        
        log.info("Initialized level for user {} with level {}", userId, initialLevel);
        return convertToDTO(userLevel);
    }
    
    /**
     * Obtenir le niveau d'un utilisateur
     */
    public UserLevelDTO getUserLevel(Long userId) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User level not found"));
        return convertToDTO(userLevel);
    }
    
    /**
     * Soumettre le résultat du test d'évaluation
     */
    @Transactional
    public UserLevelDTO submitAssessmentResult(Long userId, EnglishLevel assessedLevel) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User level not found"));
        
        if (userLevel.getHasCompletedAssessment()) {
            throw new RuntimeException("User has already completed assessment");
        }
        
        userLevel.setAssessmentLevel(assessedLevel);
        userLevel.setHasCompletedAssessment(true);
        userLevel.setAssessmentCompletedAt(LocalDateTime.now());
        
        userLevel = userLevelRepository.save(userLevel);
        
        // Attribuer le badge de niveau (non certifié)
        awardLevelBadge(userId, assessedLevel, false);
        
        // Bonus coins pour avoir complété le test
        addCoins(userId, 50, "Completed assessment test");
        
        log.info("User {} completed assessment with level {}", userId, assessedLevel);
        return convertToDTO(userLevel);
    }
    
    /**
     * Certifier un niveau après examen payant
     */
    @Transactional
    public UserLevelDTO certifyLevel(Long userId, EnglishLevel certifiedLevel) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User level not found"));
        
        userLevel.setCertifiedLevel(certifiedLevel);
        userLevel.setCertificationDate(LocalDateTime.now());
        
        userLevel = userLevelRepository.save(userLevel);
        
        // Attribuer le badge de niveau certifié
        awardLevelBadge(userId, certifiedLevel, true);
        
        // Bonus coins pour la certification
        int coinsReward = switch (certifiedLevel) {
            case A1 -> 100;
            case A2 -> 150;
            case B1 -> 200;
            case B2 -> 300;
            case C1 -> 500;
            case C2 -> 1000;
        };
        addCoins(userId, coinsReward, "Level certification: " + certifiedLevel);
        
        log.info("User {} certified level {}", userId, certifiedLevel);
        return convertToDTO(userLevel);
    }
    
    /**
     * Ajouter de l'XP à un utilisateur
     */
    @Transactional
    public UserLevelDTO addXP(Long userId, int xp, String reason) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User level not found"));
        
        EnglishLevel oldLevel = userLevel.getAssessmentLevel();
        userLevel.addXP(xp);
        updateActivity(userLevel);
        userLevel = userLevelRepository.save(userLevel);
        
        // Vérifier si l'utilisateur a monté de niveau
        if (userLevel.getAssessmentLevel() != oldLevel) {
            handleLevelUp(userId, oldLevel, userLevel.getAssessmentLevel());
        }
        
        log.info("Added {} XP to user {} for reason: {}", xp, userId, reason);
        return convertToDTO(userLevel);
    }
    
    /**
     * Ajouter des Jungle Coins
     */
    @Transactional
    public UserLevelDTO addCoins(Long userId, int coins, String reason) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User level not found"));
        
        userLevel.addCoins(coins);
        userLevel = userLevelRepository.save(userLevel);
        
        log.info("Added {} coins to user {} for reason: {}", coins, userId, reason);
        return convertToDTO(userLevel);
    }
    
    /**
     * Dépenser des Jungle Coins
     */
    @Transactional
    public UserLevelDTO spendCoins(Long userId, int coins) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User level not found"));
        
        if (userLevel.getJungleCoins() < coins) {
            throw new RuntimeException("Insufficient coins");
        }
        
        userLevel.spendCoins(coins);
        userLevel = userLevelRepository.save(userLevel);
        
        log.info("User {} spent {} coins", userId, coins);
        return convertToDTO(userLevel);
    }
    
    /**
     * Enregistrer un achat
     */
    @Transactional
    public UserLevelDTO recordPurchase(Long userId, double amount) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User level not found"));
        
        LoyaltyTier oldTier = userLevel.getLoyaltyTier();
        userLevel.addSpending(amount);
        userLevel = userLevelRepository.save(userLevel);
        
        // Vérifier si l'utilisateur a changé de palier
        if (userLevel.getLoyaltyTier() != oldTier) {
            handleTierUpgrade(userId, oldTier, userLevel.getLoyaltyTier());
        }
        
        log.info("Recorded purchase of {}€ for user {}", amount, userId);
        return convertToDTO(userLevel);
    }
    
    /**
     * Attribuer un badge à un utilisateur
     */
    @Transactional
    public BadgeDTO awardBadge(Long userId, String badgeCode) {
        Badge badge = badgeRepository.findByCode(badgeCode)
                .orElseThrow(() -> new RuntimeException("Badge not found: " + badgeCode));
        
        // Vérifier si l'utilisateur a déjà ce badge
        if (userBadgeRepository.existsByUserIdAndBadge(userId, badge)) {
            throw new RuntimeException("User already has this badge");
        }
        
        UserBadge userBadge = UserBadge.builder()
                .userId(userId)
                .badge(badge)
                .isDisplayed(false)
                .isNew(true)
                .build();
        
        userBadge = userBadgeRepository.save(userBadge);
        
        // Ajouter les coins de récompense
        if (badge.getCoinsReward() > 0) {
            addCoins(userId, badge.getCoinsReward(), "Badge reward: " + badge.getName());
        }
        
        log.info("Awarded badge {} to user {}", badgeCode, userId);
        return convertBadgeToDTO(userBadge);
    }
    
    /**
     * Obtenir tous les badges d'un utilisateur
     */
    public List<BadgeDTO> getUserBadges(Long userId) {
        List<UserBadge> userBadges = userBadgeRepository.findByUserIdOrderByEarnedAtDesc(userId);
        return userBadges.stream()
                .map(this::convertBadgeToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtenir les nouveaux badges non vus
     */
    public List<BadgeDTO> getNewBadges(Long userId) {
        List<UserBadge> newBadges = userBadgeRepository.findByUserIdAndIsNewTrue(userId);
        return newBadges.stream()
                .map(this::convertBadgeToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Marquer les badges comme vus
     */
    @Transactional
    public void markBadgesAsSeen(Long userId) {
        List<UserBadge> newBadges = userBadgeRepository.findByUserIdAndIsNewTrue(userId);
        newBadges.forEach(ub -> ub.setIsNew(false));
        userBadgeRepository.saveAll(newBadges);
    }
    
    /**
     * Gérer la montée de niveau
     */
    private void handleLevelUp(Long userId, EnglishLevel oldLevel, EnglishLevel newLevel) {
        log.info("User {} leveled up from {} to {}", userId, oldLevel, newLevel);
        
        // Attribuer le badge de niveau
        awardLevelBadge(userId, newLevel, false);
        
        // Récompenses selon le niveau
        int coinsReward = switch (newLevel) {
            case A2 -> 100;
            case B1 -> 150;
            case B2 -> 200;
            case C1 -> 300;
            case C2 -> 500;
            default -> 50;
        };
        
        addCoins(userId, coinsReward, "Level up to " + newLevel);
        
        // Badge spécial pour montée de niveau
        if (newLevel == EnglishLevel.A2) {
            tryAwardBadge(userId, "RISING_STAR");
        }
    }
    
    /**
     * Gérer le changement de palier de fidélité
     */
    private void handleTierUpgrade(Long userId, LoyaltyTier oldTier, LoyaltyTier newTier) {
        log.info("User {} upgraded from {} to {}", userId, oldTier, newTier);
        
        // Attribuer un badge selon le palier
        String badgeCode = switch (newTier) {
            case SILVER -> "LOYALTY_SILVER";
            case GOLD -> "LOYALTY_GOLD";
            case PLATINUM -> "LOYALTY_PLATINUM";
            default -> null;
        };
        
        if (badgeCode != null) {
            tryAwardBadge(userId, badgeCode);
        }
    }
    
    /**
     * Attribuer un badge de niveau
     */
    private void awardLevelBadge(Long userId, EnglishLevel level, boolean certified) {
        String badgeCode = (certified ? "CERTIFIED_" : "LEVEL_") + level.name();
        tryAwardBadge(userId, badgeCode);
    }
    
    /**
     * Essayer d'attribuer un badge (sans erreur si déjà possédé)
     */
    private void tryAwardBadge(Long userId, String badgeCode) {
        try {
            awardBadge(userId, badgeCode);
        } catch (Exception e) {
            log.debug("Could not award badge {} to user {}: {}", badgeCode, userId, e.getMessage());
        }
    }
    
    /**
     * Mettre à jour l'activité quotidienne
     */
    private void updateActivity(UserLevel userLevel) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastActivity = userLevel.getLastActivityDate();
        
        if (lastActivity != null) {
            long daysBetween = ChronoUnit.DAYS.between(lastActivity.toLocalDate(), now.toLocalDate());
            
            if (daysBetween == 1) {
                // Jour consécutif
                userLevel.setConsecutiveDays(userLevel.getConsecutiveDays() + 1);
                checkStreakBadges(userLevel.getUserId(), userLevel.getConsecutiveDays());
            } else if (daysBetween > 1) {
                // Série interrompue
                userLevel.setConsecutiveDays(1);
            }
        } else {
            userLevel.setConsecutiveDays(1);
        }
        
        userLevel.setLastActivityDate(now);
    }
    
    /**
     * Vérifier les badges de série
     */
    private void checkStreakBadges(Long userId, int consecutiveDays) {
        if (consecutiveDays == 7) {
            tryAwardBadge(userId, "STREAK_7");
        } else if (consecutiveDays == 30) {
            tryAwardBadge(userId, "STREAK_30");
        } else if (consecutiveDays == 100) {
            tryAwardBadge(userId, "STREAK_100");
        }
    }
    
    // ==================== ADMIN METHODS ====================
    
    /**
     * Obtenir tous les niveaux utilisateurs (ADMIN)
     */
    public List<UserLevelDTO> getAllUserLevels() {
        List<UserLevel> userLevels = userLevelRepository.findAll();
        return userLevels.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Mettre à jour le niveau d'évaluation (ADMIN)
     */
    @Transactional
    public UserLevelDTO updateAssessmentLevel(Long userId, EnglishLevel assessedLevel) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User level not found"));
        
        userLevel.setAssessmentLevel(assessedLevel);
        userLevel.setHasCompletedAssessment(true);
        if (userLevel.getAssessmentCompletedAt() == null) {
            userLevel.setAssessmentCompletedAt(LocalDateTime.now());
        }
        
        userLevel = userLevelRepository.save(userLevel);
        
        log.info("Admin updated assessment level for user {} to {}", userId, assessedLevel);
        return convertToDTO(userLevel);
    }
    
    /**
     * Révoquer une certification (ADMIN)
     */
    @Transactional
    public UserLevelDTO revokeCertification(Long userId) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User level not found"));
        
        userLevel.setCertifiedLevel(null);
        userLevel.setCertificationDate(null);
        
        userLevel = userLevelRepository.save(userLevel);
        
        log.info("Admin revoked certification for user {}", userId);
        return convertToDTO(userLevel);
    }
    
    /**
     * Obtenir les statistiques globales (ADMIN)
     */
    public Map<String, Object> getGlobalStats() {
        List<UserLevel> allLevels = userLevelRepository.findAll();
        
        long totalUsers = allLevels.size();
        long assessedUsers = allLevels.stream()
                .filter(UserLevel::getHasCompletedAssessment)
                .count();
        long certifiedUsers = allLevels.stream()
                .filter(ul -> ul.getCertifiedLevel() != null)
                .count();
        
        // Distribution par niveau d'évaluation
        Map<String, Long> assessmentDistribution = allLevels.stream()
                .filter(ul -> ul.getAssessmentLevel() != null)
                .collect(Collectors.groupingBy(
                        ul -> ul.getAssessmentLevel().name(),
                        Collectors.counting()
                ));
        
        // Distribution par niveau certifié
        Map<String, Long> certificationDistribution = allLevels.stream()
                .filter(ul -> ul.getCertifiedLevel() != null)
                .collect(Collectors.groupingBy(
                        ul -> ul.getCertifiedLevel().name(),
                        Collectors.counting()
                ));
        
        // Total XP et Coins
        int totalXP = allLevels.stream()
                .mapToInt(UserLevel::getTotalXP)
                .sum();
        int totalCoins = allLevels.stream()
                .mapToInt(UserLevel::getJungleCoins)
                .sum();
        
        // Total badges attribués
        long totalBadges = userBadgeRepository.count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("assessedUsers", assessedUsers);
        stats.put("certifiedUsers", certifiedUsers);
        stats.put("assessmentRate", totalUsers > 0 ? (assessedUsers * 100.0 / totalUsers) : 0);
        stats.put("certificationRate", totalUsers > 0 ? (certifiedUsers * 100.0 / totalUsers) : 0);
        stats.put("assessmentDistribution", assessmentDistribution);
        stats.put("certificationDistribution", certificationDistribution);
        stats.put("totalXP", totalXP);
        stats.put("totalCoins", totalCoins);
        stats.put("totalBadges", totalBadges);
        
        return stats;
    }
    
    /**
     * Convertir UserLevel en DTO
     */
    private UserLevelDTO convertToDTO(UserLevel userLevel) {
        EnglishLevel assessmentLevel = userLevel.getAssessmentLevel();
        EnglishLevel certifiedLevel = userLevel.getCertifiedLevel();
        
        return UserLevelDTO.builder()
                .userId(userLevel.getUserId())
                .assessmentLevel(assessmentLevel)
                .assessmentLevelIcon(assessmentLevel != null ? assessmentLevel.getIcon() : "❓")
                .assessmentLevelName(assessmentLevel != null ? assessmentLevel.getFullName() : "Not assessed yet")
                .hasCompletedAssessment(userLevel.getHasCompletedAssessment())
                .assessmentCompletedAt(userLevel.getAssessmentCompletedAt() != null ? userLevel.getAssessmentCompletedAt().toString() : null)
                .certifiedLevel(certifiedLevel)
                .certifiedLevelIcon(certifiedLevel != null ? certifiedLevel.getIcon() : null)
                .certifiedLevelName(certifiedLevel != null ? certifiedLevel.getFullName() : null)
                .certificationDate(userLevel.getCertificationDate() != null ? userLevel.getCertificationDate().toString() : null)
                .currentXP(userLevel.getCurrentXP())
                .totalXP(userLevel.getTotalXP())
                .xpForNextLevel(userLevel.getXPForNextLevel())
                .progressPercentage(userLevel.getProgressPercentage())
                .nextLevel(userLevel.getNextLevel())
                .jungleCoins(userLevel.getJungleCoins())
                .loyaltyTier(userLevel.getLoyaltyTier())
                .loyaltyTierIcon(userLevel.getLoyaltyTier().getIcon())
                .loyaltyDiscount(userLevel.getLoyaltyTier().getDiscountPercentage())
                .totalSpent(userLevel.getTotalSpent())
                .consecutiveDays(userLevel.getConsecutiveDays())
                .build();
    }
    
    /**
     * Convertir UserBadge en DTO
     */
    private BadgeDTO convertBadgeToDTO(UserBadge userBadge) {
        Badge badge = userBadge.getBadge();
        
        return BadgeDTO.builder()
                .id(badge.getId())
                .code(badge.getCode())
                .name(badge.getName())
                .description(badge.getDescription())
                .icon(badge.getIcon())
                .type(badge.getType())
                .rarity(badge.getRarity())
                .rarityIcon(badge.getRarity().getIcon())
                .rarityColor(badge.getRarity().getColor())
                .coinsReward(badge.getCoinsReward())
                .isEarned(true)
                .isDisplayed(userBadge.getIsDisplayed())
                .isNew(userBadge.getIsNew())
                .earnedAt(userBadge.getEarnedAt())
                .build();
    }
}
