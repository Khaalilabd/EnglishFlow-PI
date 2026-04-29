package com.englishflow.gamification.service;

import com.englishflow.gamification.dto.BadgeDTO;
import com.englishflow.gamification.dto.UserLevelDTO;
import com.englishflow.gamification.entity.*;
import com.englishflow.gamification.repository.BadgeRepository;
import com.englishflow.gamification.repository.UserBadgeRepository;
import com.englishflow.gamification.repository.UserLevelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationServiceTest {

    @Mock
    private UserLevelRepository userLevelRepository;

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @InjectMocks
    private GamificationService gamificationService;

    private UserLevel userLevel;
    private Badge badge;
    private UserBadge userBadge;

    @BeforeEach
    void setUp() {
        userLevel = UserLevel.builder()
                .id(1L)
                .userId(100L)
                .assessmentLevel(EnglishLevel.A1)
                .currentXP(0)
                .totalXP(0)
                .jungleCoins(100)
                .loyaltyTier(LoyaltyTier.BRONZE)
                .totalSpent(0.0)
                .consecutiveDays(0)
                .hasCompletedAssessment(false)
                .lastActivityDate(LocalDateTime.now())
                .build();

        badge = Badge.builder()
                .id(1L)
                .code("LEVEL_A1")
                .name("A1 Level")
                .description("Reached A1 level")
                .icon("🎯")
                .type(BadgeType.LEVEL)
                .rarity(BadgeRarity.COMMON)
                .coinsReward(50)
                .build();

        userBadge = UserBadge.builder()
                .id(1L)
                .userId(100L)
                .badge(badge)
                .isDisplayed(false)
                .isNew(true)
                .earnedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void initializeUserLevel_WithoutAssessment_ShouldCreateUserLevel() {
        // Arrange
        when(userLevelRepository.existsByUserId(100L)).thenReturn(false);
        when(userLevelRepository.save(any(UserLevel.class))).thenReturn(userLevel);

        // Act
        UserLevelDTO result = gamificationService.initializeUserLevel(100L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(100L);
        verify(userLevelRepository).save(any(UserLevel.class));
    }

    @Test
    void initializeUserLevel_WhenAlreadyExists_ShouldThrowException() {
        // Arrange
        when(userLevelRepository.existsByUserId(100L)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> gamificationService.initializeUserLevel(100L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User level already exists");
    }

    @Test
    void initializeUserLevel_WithInitialLevel_ShouldCreateUserLevelAndAwardBadge() {
        // Arrange
        when(userLevelRepository.existsByUserId(100L)).thenReturn(false);
        when(userLevelRepository.save(any(UserLevel.class))).thenReturn(userLevel);
        when(badgeRepository.findByCode("LEVEL_A1")).thenReturn(Optional.of(badge));
        when(userBadgeRepository.existsByUserIdAndBadge(anyLong(), any(Badge.class))).thenReturn(false);
        when(userBadgeRepository.save(any(UserBadge.class))).thenReturn(userBadge);

        // Act
        UserLevelDTO result = gamificationService.initializeUserLevel(100L, EnglishLevel.A1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(100L);
        verify(userLevelRepository).save(any(UserLevel.class));
    }

    @Test
    void getUserLevel_WhenExists_ShouldReturnUserLevel() {
        // Arrange
        when(userLevelRepository.findByUserId(100L)).thenReturn(Optional.of(userLevel));

        // Act
        UserLevelDTO result = gamificationService.getUserLevel(100L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(100L);
    }

    @Test
    void getUserLevel_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(userLevelRepository.findByUserId(100L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> gamificationService.getUserLevel(100L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User level not found");
    }

    @Test
    void submitAssessmentResult_WhenNotCompleted_ShouldUpdateLevel() {
        // Arrange
        when(userLevelRepository.findByUserId(100L)).thenReturn(Optional.of(userLevel));
        when(userLevelRepository.save(any(UserLevel.class))).thenReturn(userLevel);
        when(badgeRepository.findByCode(anyString())).thenReturn(Optional.of(badge));
        when(userBadgeRepository.existsByUserIdAndBadge(anyLong(), any(Badge.class))).thenReturn(false);
        when(userBadgeRepository.save(any(UserBadge.class))).thenReturn(userBadge);

        // Act
        UserLevelDTO result = gamificationService.submitAssessmentResult(100L, EnglishLevel.A2);

        // Assert
        assertThat(result).isNotNull();
        verify(userLevelRepository, atLeastOnce()).save(any(UserLevel.class));
    }

    @Test
    void submitAssessmentResult_WhenAlreadyCompleted_ShouldThrowException() {
        // Arrange
        userLevel.setHasCompletedAssessment(true);
        when(userLevelRepository.findByUserId(100L)).thenReturn(Optional.of(userLevel));

        // Act & Assert
        assertThatThrownBy(() -> gamificationService.submitAssessmentResult(100L, EnglishLevel.A2))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User has already completed assessment");
    }

    @Test
    void certifyLevel_ShouldUpdateCertification() {
        // Arrange
        when(userLevelRepository.findByUserId(100L)).thenReturn(Optional.of(userLevel));
        when(userLevelRepository.save(any(UserLevel.class))).thenReturn(userLevel);
        when(badgeRepository.findByCode(anyString())).thenReturn(Optional.of(badge));
        when(userBadgeRepository.existsByUserIdAndBadge(anyLong(), any(Badge.class))).thenReturn(false);
        when(userBadgeRepository.save(any(UserBadge.class))).thenReturn(userBadge);

        // Act
        UserLevelDTO result = gamificationService.certifyLevel(100L, EnglishLevel.B1);

        // Assert
        assertThat(result).isNotNull();
        verify(userLevelRepository, atLeastOnce()).save(any(UserLevel.class));
    }

    @Test
    void addXP_ShouldIncreaseXP() {
        // Arrange
        when(userLevelRepository.findByUserId(100L)).thenReturn(Optional.of(userLevel));
        when(userLevelRepository.save(any(UserLevel.class))).thenReturn(userLevel);

        // Act
        UserLevelDTO result = gamificationService.addXP(100L, 50, "Test activity");

        // Assert
        assertThat(result).isNotNull();
        verify(userLevelRepository).save(any(UserLevel.class));
    }

    @Test
    void addCoins_ShouldIncreaseCoins() {
        // Arrange
        when(userLevelRepository.findByUserId(100L)).thenReturn(Optional.of(userLevel));
        when(userLevelRepository.save(any(UserLevel.class))).thenReturn(userLevel);

        // Act
        UserLevelDTO result = gamificationService.addCoins(100L, 100, "Reward");

        // Assert
        assertThat(result).isNotNull();
        verify(userLevelRepository).save(any(UserLevel.class));
    }

    @Test
    void spendCoins_WithSufficientCoins_ShouldDecreaseCoins() {
        // Arrange
        userLevel.setJungleCoins(100);
        when(userLevelRepository.findByUserId(100L)).thenReturn(Optional.of(userLevel));
        when(userLevelRepository.save(any(UserLevel.class))).thenReturn(userLevel);

        // Act
        UserLevelDTO result = gamificationService.spendCoins(100L, 50);

        // Assert
        assertThat(result).isNotNull();
        verify(userLevelRepository).save(any(UserLevel.class));
    }

    @Test
    void spendCoins_WithInsufficientCoins_ShouldThrowException() {
        // Arrange
        userLevel.setJungleCoins(10);
        when(userLevelRepository.findByUserId(100L)).thenReturn(Optional.of(userLevel));

        // Act & Assert
        assertThatThrownBy(() -> gamificationService.spendCoins(100L, 50))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Insufficient coins");
    }

    @Test
    void recordPurchase_ShouldUpdateSpending() {
        // Arrange
        when(userLevelRepository.findByUserId(100L)).thenReturn(Optional.of(userLevel));
        when(userLevelRepository.save(any(UserLevel.class))).thenReturn(userLevel);

        // Act
        UserLevelDTO result = gamificationService.recordPurchase(100L, 50.0);

        // Assert
        assertThat(result).isNotNull();
        verify(userLevelRepository).save(any(UserLevel.class));
    }

    @Test
    void awardBadge_WhenNotOwned_ShouldAwardBadge() {
        // Arrange
        when(badgeRepository.findByCode("LEVEL_A1")).thenReturn(Optional.of(badge));
        when(userBadgeRepository.existsByUserIdAndBadge(100L, badge)).thenReturn(false);
        when(userBadgeRepository.save(any(UserBadge.class))).thenReturn(userBadge);
        when(userLevelRepository.findByUserId(100L)).thenReturn(Optional.of(userLevel));
        when(userLevelRepository.save(any(UserLevel.class))).thenReturn(userLevel);

        // Act
        BadgeDTO result = gamificationService.awardBadge(100L, "LEVEL_A1");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("LEVEL_A1");
        verify(userBadgeRepository).save(any(UserBadge.class));
    }

    @Test
    void awardBadge_WhenAlreadyOwned_ShouldThrowException() {
        // Arrange
        when(badgeRepository.findByCode("LEVEL_A1")).thenReturn(Optional.of(badge));
        when(userBadgeRepository.existsByUserIdAndBadge(100L, badge)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> gamificationService.awardBadge(100L, "LEVEL_A1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User already has this badge");
    }

    @Test
    void awardBadge_WhenBadgeNotFound_ShouldThrowException() {
        // Arrange
        when(badgeRepository.findByCode("INVALID_BADGE")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> gamificationService.awardBadge(100L, "INVALID_BADGE"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Badge not found");
    }

    @Test
    void getUserBadges_ShouldReturnAllBadges() {
        // Arrange
        when(userBadgeRepository.findByUserIdOrderByEarnedAtDesc(100L))
                .thenReturn(List.of(userBadge));

        // Act
        List<BadgeDTO> result = gamificationService.getUserBadges(100L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("LEVEL_A1");
    }

    @Test
    void getNewBadges_ShouldReturnOnlyNewBadges() {
        // Arrange
        when(userBadgeRepository.findByUserIdAndIsNewTrue(100L))
                .thenReturn(List.of(userBadge));

        // Act
        List<BadgeDTO> result = gamificationService.getNewBadges(100L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsNew()).isTrue();
    }

    @Test
    void markBadgesAsSeen_ShouldUpdateBadges() {
        // Arrange
        when(userBadgeRepository.findByUserIdAndIsNewTrue(100L))
                .thenReturn(List.of(userBadge));
        when(userBadgeRepository.saveAll(anyList())).thenReturn(List.of(userBadge));

        // Act
        gamificationService.markBadgesAsSeen(100L);

        // Assert
        verify(userBadgeRepository).saveAll(anyList());
    }

    @Test
    void getAllUserLevels_ShouldReturnAllLevels() {
        // Arrange
        when(userLevelRepository.findAll()).thenReturn(List.of(userLevel));

        // Act
        List<UserLevelDTO> result = gamificationService.getAllUserLevels();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(100L);
    }

    @Test
    void updateAssessmentLevel_ShouldUpdateLevel() {
        // Arrange
        when(userLevelRepository.findByUserId(100L)).thenReturn(Optional.of(userLevel));
        when(userLevelRepository.save(any(UserLevel.class))).thenReturn(userLevel);

        // Act
        UserLevelDTO result = gamificationService.updateAssessmentLevel(100L, EnglishLevel.B1);

        // Assert
        assertThat(result).isNotNull();
        verify(userLevelRepository).save(any(UserLevel.class));
    }

    @Test
    void revokeCertification_ShouldRemoveCertification() {
        // Arrange
        userLevel.setCertifiedLevel(EnglishLevel.B1);
        when(userLevelRepository.findByUserId(100L)).thenReturn(Optional.of(userLevel));
        when(userLevelRepository.save(any(UserLevel.class))).thenReturn(userLevel);

        // Act
        UserLevelDTO result = gamificationService.revokeCertification(100L);

        // Assert
        assertThat(result).isNotNull();
        verify(userLevelRepository).save(any(UserLevel.class));
    }

    @Test
    void getGlobalStats_ShouldReturnStatistics() {
        // Arrange
        when(userLevelRepository.findAll()).thenReturn(List.of(userLevel));
        when(userBadgeRepository.count()).thenReturn(10L);

        // Act
        Map<String, Object> result = gamificationService.getGlobalStats();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).containsKeys("totalUsers", "assessedUsers", "certifiedUsers", "totalBadges");
        assertThat(result.get("totalUsers")).isEqualTo(1L);
    }
}
