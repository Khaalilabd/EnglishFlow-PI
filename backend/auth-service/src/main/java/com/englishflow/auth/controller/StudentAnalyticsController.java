package com.englishflow.auth.controller;

import com.englishflow.auth.dto.StudentAnalyticsDTO;
import com.englishflow.auth.entity.StudentAnalytics;
import com.englishflow.auth.service.StudentAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Slf4j
public class StudentAnalyticsController {

    private final StudentAnalyticsService analyticsService;

    /**
     * Récupère les analytics d'un étudiant
     */
    @GetMapping("/student/{userId}")
    public ResponseEntity<StudentAnalyticsDTO> getStudentAnalytics(@PathVariable Long userId) {
        log.info("Récupération des analytics pour l'étudiant {}", userId);
        
        return analyticsService.getAnalytics(userId)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    // Créer des analytics par défaut si elles n'existent pas
                    StudentAnalytics analytics = analyticsService.getOrCreateAnalytics(userId);
                    return ResponseEntity.ok(toDTO(analytics));
                });
    }

    /**
     * Track un clic
     */
    @PostMapping("/student/{userId}/click")
    public ResponseEntity<Void> trackClick(
            @PathVariable Long userId,
            @RequestBody(required = false) Map<String, Integer> body) {
        
        int clicks = body != null && body.containsKey("clicks") ? body.get("clicks") : 1;
        analyticsService.trackClick(userId, clicks);
        return ResponseEntity.ok().build();
    }

    /**
     * Track une session
     */
    @PostMapping("/student/{userId}/session")
    public ResponseEntity<Void> trackSession(@PathVariable Long userId) {
        analyticsService.trackSession(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Track une évaluation
     */
    @PostMapping("/student/{userId}/assessment")
    public ResponseEntity<Void> trackAssessment(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> body) {
        
        double score = ((Number) body.get("score")).doubleValue();
        String type = (String) body.getOrDefault("type", "TMA");
        
        analyticsService.trackAssessment(userId, score, type);
        return ResponseEntity.ok().build();
    }

    /**
     * Ajoute des crédits
     */
    @PostMapping("/student/{userId}/credits")
    public ResponseEntity<Void> addCredits(
            @PathVariable Long userId,
            @RequestBody Map<String, Integer> body) {
        
        int credits = body.get("credits");
        analyticsService.addStudiedCredits(userId, credits);
        return ResponseEntity.ok().build();
    }

    /**
     * Incrémente les tentatives
     */
    @PostMapping("/student/{userId}/attempt")
    public ResponseEntity<Void> incrementAttempts(@PathVariable Long userId) {
        analyticsService.incrementAttempts(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Marque comme désinscrit
     */
    @PostMapping("/student/{userId}/unregister")
    public ResponseEntity<Void> markAsUnregistered(@PathVariable Long userId) {
        analyticsService.markAsUnregistered(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Track l'ouverture d'une leçon
     */
    @PostMapping("/student/{userId}/lesson-opened")
    public ResponseEntity<Void> trackLessonOpened(@PathVariable Long userId) {
        analyticsService.trackLessonOpened(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Ajoute du temps passé
     */
    @PostMapping("/student/{userId}/time-spent")
    public ResponseEntity<Void> addTimeSpent(
            @PathVariable Long userId,
            @RequestBody Map<String, Integer> body) {
        
        int minutes = body.get("minutes");
        analyticsService.addTimeSpent(userId, minutes);
        return ResponseEntity.ok().build();
    }

    /**
     * 🧪 ENDPOINT DE TEST - Force le tracking de données de test
     */
    @PostMapping("/student/{userId}/test-tracking")
    public ResponseEntity<StudentAnalyticsDTO> testTracking(@PathVariable Long userId) {
        log.info("🧪 TEST: Forcing tracking for student {}", userId);
        
        // Track 50 clicks
        analyticsService.trackClick(userId, 50);
        
        // Track 3 sessions
        for (int i = 0; i < 3; i++) {
            analyticsService.trackSession(userId);
        }
        
        // Track 2 assessments
        analyticsService.trackAssessment(userId, 75.0, "TMA");
        analyticsService.trackAssessment(userId, 85.0, "CMA");
        
        // Add credits
        analyticsService.addStudiedCredits(userId, 10);
        
        // Get updated analytics
        StudentAnalytics analytics = analyticsService.getOrCreateAnalytics(userId);
        
        log.info("🧪 TEST: Analytics after tracking: {}", analytics);
        
        return ResponseEntity.ok(toDTO(analytics));
    }

    /**
     * 🔍 ENDPOINT DE DEBUG - Récupère les analytics avec logs détaillés
     */
    @GetMapping("/student/{userId}/debug")
    public ResponseEntity<StudentAnalyticsDTO> debugAnalytics(@PathVariable Long userId) {
        log.info("🔍 DEBUG: Fetching analytics for student {}", userId);
        
        return analyticsService.getAnalytics(userId)
                .map(analytics -> {
                    log.info("🔍 DEBUG: Found analytics: {}", analytics);
                    return ResponseEntity.ok(toDTO(analytics));
                })
                .orElseGet(() -> {
                    log.warn("🔍 DEBUG: No analytics found for student {}, creating new one", userId);
                    StudentAnalytics analytics = analyticsService.getOrCreateAnalytics(userId);
                    log.info("🔍 DEBUG: Created analytics: {}", analytics);
                    return ResponseEntity.ok(toDTO(analytics));
                });
    }

    // Helper method
    private StudentAnalyticsDTO toDTO(StudentAnalytics analytics) {
        return StudentAnalyticsDTO.builder()
                .userId(analytics.getUserId())
                .totalClicks(analytics.getTotalClicks())
                .totalSessions(analytics.getTotalSessions())
                .avgClicksPerSession(analytics.getAvgClicksPerSession())
                .maxClicksInSession(analytics.getMaxClicksInSession())
                .avgScore(analytics.getAvgScore())
                .minScore(analytics.getMinScore())
                .maxScore(analytics.getMaxScore())
                .totalAssessments(analytics.getTotalAssessments())
                .completedTMA(analytics.getCompletedTMA())
                .completedCMA(analytics.getCompletedCMA())
                .completedExams(analytics.getCompletedExams())
                .previousAttempts(analytics.getPreviousAttempts())
                .studiedCredits(analytics.getStudiedCredits())
                .lastActivityAt(analytics.getLastActivityAt())
                .firstRegistrationDate(analytics.getFirstRegistrationDate())
                .isUnregistered(analytics.getIsUnregistered())
                .totalLessonsOpened(analytics.getTotalLessonsOpened())
                .totalTimeSpentMinutes(analytics.getTotalTimeSpentMinutes())
                .avgTimePerLesson(analytics.getAvgTimePerLesson())
                .lastLessonOpenedAt(analytics.getLastLessonOpenedAt())
                .build();
    }
}
