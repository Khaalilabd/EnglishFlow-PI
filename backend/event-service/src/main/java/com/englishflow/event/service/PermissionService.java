package com.englishflow.event.service;

import com.englishflow.event.client.ClubServiceClient;
import com.englishflow.event.dto.MemberDTO;
import com.englishflow.event.enums.RankType;
import com.englishflow.event.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {
    
    private final ClubServiceClient clubServiceClient;
    
    private static final List<RankType> ALLOWED_RANKS = Arrays.asList(
        RankType.PRESIDENT,
        RankType.VICE_PRESIDENT,
        RankType.EVENT_MANAGER
    );
    
    /**
     * Vérifie si l'utilisateur a la permission de créer un événement
     * Seuls les présidents, vice-présidents et event managers peuvent créer des événements
     */
    public void checkEventCreationPermission(Long userId) {
        log.info("Checking event creation permission for user: {}", userId);
        
        try {
            List<MemberDTO> memberships = clubServiceClient.getMembersByUserId(userId);
            
            if (memberships == null || memberships.isEmpty()) {
                log.warn("User {} is not a member of any club", userId);
                throw new UnauthorizedException(
                    "Vous devez être membre d'un club pour créer un événement"
                );
            }
            
            log.info("User {} has {} memberships", userId, memberships.size());
            
            boolean hasPermission = memberships.stream()
                .anyMatch(member -> {
                    RankType rank = member.getRank();
                    log.info("User {} has rank {} in club {}", userId, rank, member.getClubId());
                    return rank != null && ALLOWED_RANKS.contains(rank);
                });
            
            if (!hasPermission) {
                log.warn("User {} does not have permission to create events", userId);
                throw new UnauthorizedException(
                    "Seuls les présidents, vice-présidents et event managers peuvent créer des événements"
                );
            }
            
            log.info("User {} has permission to create events", userId);
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error checking permissions for user {}", userId, e);
            throw new UnauthorizedException(
                "Impossible de vérifier vos permissions. Veuillez réessayer."
            );
        }
    }
}
