package com.englishflow.auth.controller;

import com.englishflow.auth.dto.UserLevelDTO;
import com.englishflow.auth.service.GamificationIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gamification")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class GamificationController {
    
    private final GamificationIntegrationService gamificationIntegrationService;
    
    /**
     * Obtenir le niveau de gamification d'un utilisateur
     * GET /gamification/users/{userId}/level
     */
    @GetMapping("/users/{userId}/level")
    public ResponseEntity<UserLevelDTO> getUserLevel(@PathVariable Long userId) {
        log.info("Fetching gamification level for user {}", userId);
        UserLevelDTO level = gamificationIntegrationService.getUserLevel(userId);
        return ResponseEntity.ok(level);
    }
    
    /**
     * Initialiser le niveau d'un utilisateur (admin only)
     * POST /gamification/users/{userId}/initialize
     */
    @PostMapping("/users/{userId}/initialize")
    public ResponseEntity<UserLevelDTO> initializeUserLevel(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "A1") String englishLevel) {
        log.info("Initializing gamification level for user {} with level {}", userId, englishLevel);
        UserLevelDTO level = gamificationIntegrationService.initializeUserLevel(userId, englishLevel);
        return ResponseEntity.ok(level);
    }
}
