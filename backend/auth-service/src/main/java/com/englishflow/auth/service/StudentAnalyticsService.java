package com.englishflow.auth.service;

import com.englishflow.auth.entity.StudentAnalytics;
import com.englishflow.auth.repository.StudentAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentAnalyticsService {

    private final StudentAnalyticsRepository analyticsRepository;

    /**
     * Récupère ou crée les analytics d'un étudiant
     */
    @Transactional
    public StudentAnalytics getOrCreateAnalytics(Long userId) {
        return analyticsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("Création des analytics pour l'étudiant {}", userId);
                    StudentAnalytics analytics = StudentAnalytics.builder()
                            .userId(userId)
                            .firstRegistrationDate(LocalDateTime.now())
                            .build();
                    return analyticsRepository.save(analytics);
                });
    }

    /**
     * Enregistre un clic de l'étudiant
     */
    @Transactional
    public void trackClick(Long userId, int clicks) {
        StudentAnalytics analytics = getOrCreateAnalytics(userId);
        analytics.incrementClicks(clicks);
        analytics.updateMaxClicks(clicks);
        analytics.recalculateAvgClicks(); // Recalculer après ajout des clics
        analyticsRepository.save(analytics);
        log.debug("Clics trackés pour l'étudiant {}: {} clics", userId, clicks);
    }

    /**
     * Enregistre une nouvelle session
     */
    @Transactional
    public void trackSession(Long userId) {
        StudentAnalytics analytics = getOrCreateAnalytics(userId);
        analytics.incrementSession();
        analytics.recalculateAvgClicks(); // Recalculer après ajout de session
        analyticsRepository.save(analytics);
        log.debug("Session trackée pour l'étudiant {}", userId);
    }

    /**
     * Enregistre un score d'évaluation
     */
    @Transactional
    public void trackAssessment(Long userId, double score, String assessmentType) {
        StudentAnalytics analytics = getOrCreateAnalytics(userId);
        analytics.addAssessmentScore(score, assessmentType);
        analyticsRepository.save(analytics);
        log.info("Évaluation trackée pour l'étudiant {}: {} - score: {}", 
                userId, assessmentType, score);
    }

    /**
     * Ajoute des crédits étudiés
     */
    @Transactional
    public void addStudiedCredits(Long userId, int credits) {
        StudentAnalytics analytics = getOrCreateAnalytics(userId);
        analytics.addCredits(credits);
        analyticsRepository.save(analytics);
        log.info("Crédits ajoutés pour l'étudiant {}: {} crédits", userId, credits);
    }

    /**
     * Incrémente le nombre de tentatives
     */
    @Transactional
    public void incrementAttempts(Long userId) {
        StudentAnalytics analytics = getOrCreateAnalytics(userId);
        analytics.incrementAttempts();
        analyticsRepository.save(analytics);
        log.info("Tentative incrémentée pour l'étudiant {}", userId);
    }

    /**
     * Marque un étudiant comme désinscrit
     */
    @Transactional
    public void markAsUnregistered(Long userId) {
        StudentAnalytics analytics = getOrCreateAnalytics(userId);
        analytics.setIsUnregistered(true);
        analyticsRepository.save(analytics);
        log.info("Étudiant {} marqué comme désinscrit", userId);
    }

    /**
     * Track l'ouverture d'une leçon
     */
    @Transactional
    public void trackLessonOpened(Long userId) {
        StudentAnalytics analytics = getOrCreateAnalytics(userId);
        analytics.trackLessonOpened();
        analyticsRepository.save(analytics);
        log.debug("Leçon ouverte trackée pour l'étudiant {}", userId);
    }

    /**
     * Ajoute du temps passé sur une leçon
     */
    @Transactional
    public void addTimeSpent(Long userId, int minutes) {
        StudentAnalytics analytics = getOrCreateAnalytics(userId);
        analytics.addTimeSpent(minutes);
        analyticsRepository.save(analytics);
        log.debug("Temps ajouté pour l'étudiant {}: {} minutes", userId, minutes);
    }

    /**
     * Récupère les analytics d'un étudiant
     */
    public Optional<StudentAnalytics> getAnalytics(Long userId) {
        return analyticsRepository.findByUserId(userId);
    }

    /**
     * Supprime les analytics d'un étudiant
     */
    @Transactional
    public void deleteAnalytics(Long userId) {
        analyticsRepository.deleteByUserId(userId);
        log.info("Analytics supprimées pour l'étudiant {}", userId);
    }
}
