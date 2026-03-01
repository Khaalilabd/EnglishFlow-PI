package com.englishflow.auth.service;

import com.englishflow.auth.client.GamificationClient;
import com.englishflow.auth.dto.UserLevelDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamificationIntegrationService {
    
    private final GamificationClient gamificationClient;
    private final ObjectMapper objectMapper;
    
    /**
     * Initialiser le niveau d'un utilisateur lors de l'inscription
     */
    public UserLevelDTO initializeUserLevel(Long userId, String englishLevel) {
        try {
            log.info("Initializing gamification level for user {} with level {}", userId, englishLevel);
            
            // Utiliser le niveau d'anglais fourni ou A1 par défaut
            String initialLevel = (englishLevel != null && !englishLevel.isEmpty()) ? englishLevel : "A1";
            
            Map<String, Object> response = gamificationClient.initializeUserLevel(userId, initialLevel);
            return convertToUserLevelDTO(response);
        } catch (Exception e) {
            log.error("Failed to initialize gamification level for user {}: {}", userId, e.getMessage());
            // Retourner un niveau par défaut en cas d'erreur
            return createDefaultLevel(userId);
        }
    }
    
    /**
     * Récupérer le niveau d'un utilisateur
     * Retourne null si l'utilisateur n'a pas encore passé le test d'évaluation
     */
    public UserLevelDTO getUserLevel(Long userId) {
        try {
            log.debug("Fetching gamification level for user {}", userId);
            Map<String, Object> response = gamificationClient.getUserLevel(userId);
            return convertToUserLevelDTO(response);
        } catch (Exception e) {
            log.debug("User {} has not completed assessment test yet", userId);
            return null; // Retourner null si le niveau n'existe pas encore
        }
    }
    
    /**
     * Ajouter de l'XP à un utilisateur
     */
    public void addXP(Long userId, int xp, String reason) {
        try {
            log.info("Adding {} XP to user {} for reason: {}", xp, userId, reason);
            gamificationClient.addXP(userId, Map.of("xp", xp, "reason", reason));
        } catch (Exception e) {
            log.error("Failed to add XP to user {}: {}", userId, e.getMessage());
        }
    }
    
    /**
     * Ajouter des Jungle Coins
     */
    public void addCoins(Long userId, int coins, String reason) {
        try {
            log.info("Adding {} coins to user {} for reason: {}", coins, userId, reason);
            gamificationClient.addCoins(userId, Map.of("coins", coins, "reason", reason));
        } catch (Exception e) {
            log.error("Failed to add coins to user {}: {}", userId, e.getMessage());
        }
    }
    
    /**
     * Enregistrer un achat
     */
    public void recordPurchase(Long userId, double amount) {
        try {
            log.info("Recording purchase of {}€ for user {}", amount, userId);
            gamificationClient.recordPurchase(userId, Map.of("amount", amount));
        } catch (Exception e) {
            log.error("Failed to record purchase for user {}: {}", userId, e.getMessage());
        }
    }
    
    /**
     * Convertir la réponse en UserLevelDTO
     */
    private UserLevelDTO convertToUserLevelDTO(Map<String, Object> response) {
        try {
            return objectMapper.convertValue(response, UserLevelDTO.class);
        } catch (Exception e) {
            log.error("Failed to convert response to UserLevelDTO: {}", e.getMessage());
            return createDefaultLevel(null);
        }
    }
    
    /**
     * Créer un niveau par défaut en cas d'erreur
     */
    private UserLevelDTO createDefaultLevel(Long userId) {
        return UserLevelDTO.builder()
                .userId(userId)
                .assessmentLevel("A1")
                .assessmentLevelIcon("🌱")
                .assessmentLevelName("A1 - Beginner")
                .currentXP(0)
                .totalXP(0)
                .xpForNextLevel(1000)
                .progressPercentage(0)
                .nextLevel("A2")
                .jungleCoins(0)
                .loyaltyTier("BRONZE")
                .loyaltyTierIcon("🥉")
                .loyaltyDiscount(5)
                .totalSpent(0.0)
                .consecutiveDays(0)
                .build();
    }
}
