package com.englishflow.club.service;

import com.englishflow.club.client.AuthServiceClient;
import com.englishflow.club.dto.ClubHistoryDTO;
import com.englishflow.club.dto.UserInfoDTO;
import com.englishflow.club.entity.ClubHistory;
import com.englishflow.club.enums.ClubHistoryType;
import com.englishflow.club.mapper.ClubHistoryMapper;
import com.englishflow.club.repository.ClubHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClubHistoryService {
    
    private final ClubHistoryRepository historyRepository;
    private final ClubHistoryMapper historyMapper;
    private final AuthServiceClient authServiceClient;
    
    /**
     * Créer une entrée d'historique
     */
    @Transactional
    public ClubHistoryDTO createHistory(ClubHistoryDTO historyDTO) {
        log.info("Creating club history entry: {}", historyDTO);
        ClubHistory history = historyMapper.toEntity(historyDTO);
        ClubHistory saved = historyRepository.save(history);
        return historyMapper.toDTO(saved);
    }
    
    /**
     * Créer une entrée d'historique (méthode helper)
     */
    @Transactional
    public void logHistory(Long clubId, Long userId, ClubHistoryType type, String action, 
                          String description, String oldValue, String newValue, Long performedBy) {
        log.info("📝 Creating history entry: clubId={}, userId={}, type={}, action='{}'", 
            clubId, userId, type, action);
        log.info("   Description: {}", description);
        log.info("   Change: '{}' -> '{}'", oldValue, newValue);
        
        ClubHistory history = new ClubHistory();
        history.setClubId(clubId);
        history.setUserId(userId);
        history.setType(type);
        history.setAction(action);
        history.setDescription(description);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setPerformedBy(performedBy);
        
        ClubHistory saved = historyRepository.save(history);
        log.info("✅ History entry saved with ID: {}, createdAt: {}", saved.getId(), saved.getCreatedAt());
    }
    
    /**
     * Obtenir l'historique complet d'un club
     */
    public List<ClubHistoryDTO> getClubHistory(Long clubId) {
        log.info("🔍 Fetching history for club: {}", clubId);
        List<ClubHistory> historyEntities = historyRepository.findByClubIdOrderByCreatedAtDesc(clubId);
        log.info("📊 Found {} history entries in database", historyEntities.size());
        
        if (!historyEntities.isEmpty()) {
            log.info("📋 First entry: id={}, type={}, action='{}', createdAt={}", 
                historyEntities.get(0).getId(), 
                historyEntities.get(0).getType(), 
                historyEntities.get(0).getAction(),
                historyEntities.get(0).getCreatedAt());
        }
        
        List<ClubHistoryDTO> dtos = historyEntities.stream()
                .map(historyMapper::toDTO)
                .collect(Collectors.toList());

        // Enrich with user names
        dtos.forEach(dto -> {
            if (dto.getPerformedBy() != null) {
                try {
                    UserInfoDTO user = authServiceClient.getUserInfo(dto.getPerformedBy());
                    if (user != null) {
                        dto.setPerformedByName(user.getFirstName() + " " + user.getLastName());
                    }
                } catch (Exception e) {
                    dto.setPerformedByName("User #" + dto.getPerformedBy());
                }
            }
        });

        log.info("✅ Returning {} history DTOs", dtos.size());
        return dtos;
    }
    
    /**
     * Obtenir l'historique d'un utilisateur dans un club
     */
    public List<ClubHistoryDTO> getUserHistoryInClub(Long clubId, Long userId) {
        log.info("Fetching history for user {} in club {}", userId, clubId);
        return historyRepository.findByClubIdAndUserIdOrderByCreatedAtDesc(clubId, userId)
                .stream()
                .map(historyMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtenir l'historique d'un utilisateur (tous les clubs)
     */
    public List<ClubHistoryDTO> getUserHistory(Long userId) {
        log.info("Fetching all history for user: {}", userId);
        return historyRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(historyMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtenir l'historique par type
     */
    public List<ClubHistoryDTO> getHistoryByType(Long clubId, ClubHistoryType type) {
        log.info("Fetching history of type {} for club {}", type, clubId);
        return historyRepository.findByClubIdAndTypeOrderByCreatedAtDesc(clubId, type)
                .stream()
                .map(historyMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtenir l'historique récent (derniers X jours)
     */
    public List<ClubHistoryDTO> getRecentHistory(Long clubId, int days) {
        log.info("Fetching recent history ({} days) for club {}", days, clubId);
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return historyRepository.findRecentHistory(clubId, since)
                .stream()
                .map(historyMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Compter les entrées d'historique
     */
    public long countClubHistory(Long clubId) {
        return historyRepository.countByClubId(clubId);
    }
    
    /**
     * Compter les entrées d'historique pour un utilisateur
     */
    public long countUserHistoryInClub(Long clubId, Long userId) {
        return historyRepository.countByClubIdAndUserId(clubId, userId);
    }
    
    /**
     * Supprimer l'historique d'un club (utilisé lors de la suppression du club)
     */
    @Transactional
    public void deleteClubHistory(Long clubId) {
        log.info("Deleting all history for club: {}", clubId);
        List<ClubHistory> history = historyRepository.findByClubIdOrderByCreatedAtDesc(clubId);
        historyRepository.deleteAll(history);
    }
}
