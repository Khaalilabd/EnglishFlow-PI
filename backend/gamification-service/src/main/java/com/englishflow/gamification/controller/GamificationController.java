package com.englishflow.gamification.controller;

import com.englishflow.gamification.dto.BadgeDTO;
import com.englishflow.gamification.dto.UserLevelDTO;
import com.englishflow.gamification.entity.EnglishLevel;
import com.englishflow.gamification.service.GamificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gamification")
@RequiredArgsConstructor
@Slf4j
public class GamificationController {
    
    private final GamificationService gamificationService;
    
    /**
     * Initialiser le niveau d'un utilisateur
     * POST /gamification/users/{userId}/initialize
     */
    @PostMapping("/users/{userId}/initialize")
    public ResponseEntity<UserLevelDTO> initializeUserLevel(
            @PathVariable Long userId,
            @RequestParam EnglishLevel initialLevel) {
        log.info("Initializing level for user {} with level {}", userId, initialLevel);
        UserLevelDTO userLevel = gamificationService.initializeUserLevel(userId, initialLevel);
        return ResponseEntity.ok(userLevel);
    }
    
    /**
     * Initialiser le niveau après un test d'évaluation
     * POST /gamification/assessment/complete
     * Endpoint public appelé par le service d'évaluation
     */
    @PostMapping("/assessment/complete")
    public ResponseEntity<UserLevelDTO> completeAssessment(
            @RequestBody com.englishflow.gamification.dto.AssessmentResultRequest request) {
        log.info("Processing assessment result for user {} with level {}", 
                request.getUserId(), request.getAssessedLevel());
        
        EnglishLevel level = EnglishLevel.valueOf(request.getAssessedLevel());
        UserLevelDTO userLevel = gamificationService.initializeUserLevel(request.getUserId(), level);
        
        // Bonus coins pour avoir complété le test
        gamificationService.addCoins(request.getUserId(), 50, "Completed assessment test");
        
        log.info("User {} assessment completed. Level: {}, Score: {}/{}", 
                request.getUserId(), request.getAssessedLevel(), 
                request.getCorrectAnswers(), request.getTotalQuestions());
        
        return ResponseEntity.ok(userLevel);
    }
    
    /**
     * Obtenir le niveau d'un utilisateur
     * GET /gamification/users/{userId}/level
     */
    @GetMapping("/users/{userId}/level")
    public ResponseEntity<UserLevelDTO> getUserLevel(@PathVariable Long userId) {
        log.info("Getting level for user {}", userId);
        UserLevelDTO userLevel = gamificationService.getUserLevel(userId);
        return ResponseEntity.ok(userLevel);
    }
    
    /**
     * Soumettre le résultat du test d'évaluation
     * POST /gamification/users/{userId}/assessment
     */
    @PostMapping("/users/{userId}/assessment")
    public ResponseEntity<UserLevelDTO> submitAssessment(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        String assessedLevelStr = request.get("assessedLevel");
        EnglishLevel assessedLevel = EnglishLevel.valueOf(assessedLevelStr);
        
        log.info("User {} submitting assessment result: {}", userId, assessedLevel);
        UserLevelDTO userLevel = gamificationService.submitAssessmentResult(userId, assessedLevel);
        return ResponseEntity.ok(userLevel);
    }
    
    /**
     * Certifier un niveau après examen payant
     * POST /gamification/users/{userId}/certify
     */
    @PostMapping("/users/{userId}/certify")
    public ResponseEntity<UserLevelDTO> certifyLevel(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        String certifiedLevelStr = request.get("certifiedLevel");
        EnglishLevel certifiedLevel = EnglishLevel.valueOf(certifiedLevelStr);
        
        log.info("Certifying level {} for user {}", certifiedLevel, userId);
        UserLevelDTO userLevel = gamificationService.certifyLevel(userId, certifiedLevel);
        return ResponseEntity.ok(userLevel);
    }
    
    /**
     * Ajouter de l'XP à un utilisateur
     * POST /gamification/users/{userId}/xp
     */
    @PostMapping("/users/{userId}/xp")
    public ResponseEntity<UserLevelDTO> addXP(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> request) {
        int xp = (Integer) request.get("xp");
        String reason = (String) request.getOrDefault("reason", "Activity");
        
        log.info("Adding {} XP to user {} for reason: {}", xp, userId, reason);
        UserLevelDTO userLevel = gamificationService.addXP(userId, xp, reason);
        return ResponseEntity.ok(userLevel);
    }
    
    /**
     * Ajouter des Jungle Coins
     * POST /gamification/users/{userId}/coins
     */
    @PostMapping("/users/{userId}/coins")
    public ResponseEntity<UserLevelDTO> addCoins(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> request) {
        int coins = (Integer) request.get("coins");
        String reason = (String) request.getOrDefault("reason", "Reward");
        
        log.info("Adding {} coins to user {} for reason: {}", coins, userId, reason);
        UserLevelDTO userLevel = gamificationService.addCoins(userId, coins, reason);
        return ResponseEntity.ok(userLevel);
    }
    
    /**
     * Dépenser des Jungle Coins
     * POST /gamification/users/{userId}/coins/spend
     */
    @PostMapping("/users/{userId}/coins/spend")
    public ResponseEntity<UserLevelDTO> spendCoins(
            @PathVariable Long userId,
            @RequestBody Map<String, Integer> request) {
        int coins = request.get("coins");
        
        log.info("User {} spending {} coins", userId, coins);
        UserLevelDTO userLevel = gamificationService.spendCoins(userId, coins);
        return ResponseEntity.ok(userLevel);
    }
    
    /**
     * Enregistrer un achat
     * POST /gamification/users/{userId}/purchase
     */
    @PostMapping("/users/{userId}/purchase")
    public ResponseEntity<UserLevelDTO> recordPurchase(
            @PathVariable Long userId,
            @RequestBody Map<String, Double> request) {
        double amount = request.get("amount");
        
        log.info("Recording purchase of {}€ for user {}", amount, userId);
        UserLevelDTO userLevel = gamificationService.recordPurchase(userId, amount);
        return ResponseEntity.ok(userLevel);
    }
    
    /**
     * Attribuer un badge à un utilisateur
     * POST /gamification/users/{userId}/badges
     */
    @PostMapping("/users/{userId}/badges")
    public ResponseEntity<BadgeDTO> awardBadge(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        String badgeCode = request.get("badgeCode");
        
        log.info("Awarding badge {} to user {}", badgeCode, userId);
        BadgeDTO badge = gamificationService.awardBadge(userId, badgeCode);
        return ResponseEntity.ok(badge);
    }
    
    /**
     * Obtenir tous les badges d'un utilisateur
     * GET /gamification/users/{userId}/badges
     */
    @GetMapping("/users/{userId}/badges")
    public ResponseEntity<List<BadgeDTO>> getUserBadges(@PathVariable Long userId) {
        log.info("Getting badges for user {}", userId);
        List<BadgeDTO> badges = gamificationService.getUserBadges(userId);
        return ResponseEntity.ok(badges);
    }
    
    /**
     * Obtenir les nouveaux badges non vus
     * GET /gamification/users/{userId}/badges/new
     */
    @GetMapping("/users/{userId}/badges/new")
    public ResponseEntity<List<BadgeDTO>> getNewBadges(@PathVariable Long userId) {
        log.info("Getting new badges for user {}", userId);
        List<BadgeDTO> badges = gamificationService.getNewBadges(userId);
        return ResponseEntity.ok(badges);
    }
    
    /**
     * Marquer les badges comme vus
     * POST /gamification/users/{userId}/badges/mark-seen
     */
    @PostMapping("/users/{userId}/badges/mark-seen")
    public ResponseEntity<Void> markBadgesAsSeen(@PathVariable Long userId) {
        log.info("Marking badges as seen for user {}", userId);
        gamificationService.markBadgesAsSeen(userId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Health check
     * GET /gamification/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "gamification-service"
        ));
    }
    
    // ==================== ADMIN ENDPOINTS ====================
    
    /**
     * Obtenir tous les niveaux utilisateurs (ADMIN)
     * GET /gamification/admin/users/levels
     */
    @GetMapping("/admin/users/levels")
    public ResponseEntity<List<UserLevelDTO>> getAllUserLevels() {
        log.info("Admin: Getting all user levels");
        List<UserLevelDTO> levels = gamificationService.getAllUserLevels();
        return ResponseEntity.ok(levels);
    }
    
    /**
     * Mettre à jour le niveau d'évaluation d'un utilisateur (ADMIN)
     * PUT /gamification/admin/users/{userId}/assessment
     */
    @PutMapping("/admin/users/{userId}/assessment")
    public ResponseEntity<UserLevelDTO> updateAssessmentLevel(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        String assessedLevelStr = request.get("assessedLevel");
        EnglishLevel assessedLevel = EnglishLevel.valueOf(assessedLevelStr);
        
        log.info("Admin: Updating assessment level for user {} to {}", userId, assessedLevel);
        UserLevelDTO userLevel = gamificationService.updateAssessmentLevel(userId, assessedLevel);
        return ResponseEntity.ok(userLevel);
    }
    
    /**
     * Certifier manuellement un niveau (ADMIN)
     * PUT /gamification/admin/users/{userId}/certify
     */
    @PutMapping("/admin/users/{userId}/certify")
    public ResponseEntity<UserLevelDTO> adminCertifyLevel(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        String certifiedLevelStr = request.get("certifiedLevel");
        EnglishLevel certifiedLevel = EnglishLevel.valueOf(certifiedLevelStr);
        
        log.info("Admin: Certifying level {} for user {}", certifiedLevel, userId);
        UserLevelDTO userLevel = gamificationService.certifyLevel(userId, certifiedLevel);
        return ResponseEntity.ok(userLevel);
    }
    
    /**
     * Révoquer une certification (ADMIN)
     * DELETE /gamification/admin/users/{userId}/certification
     */
    @DeleteMapping("/admin/users/{userId}/certification")
    public ResponseEntity<UserLevelDTO> revokeCertification(@PathVariable Long userId) {
        log.info("Admin: Revoking certification for user {}", userId);
        UserLevelDTO userLevel = gamificationService.revokeCertification(userId);
        return ResponseEntity.ok(userLevel);
    }
    
    /**
     * Obtenir les statistiques globales (ADMIN)
     * GET /gamification/admin/stats
     */
    @GetMapping("/admin/stats")
    public ResponseEntity<Map<String, Object>> getGlobalStats() {
        log.info("Admin: Getting global statistics");
        Map<String, Object> stats = gamificationService.getGlobalStats();
        return ResponseEntity.ok(stats);
    }
}
