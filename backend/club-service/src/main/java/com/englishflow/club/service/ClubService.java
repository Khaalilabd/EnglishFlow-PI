package com.englishflow.club.service;

import com.englishflow.club.client.AuthServiceClient;
import com.englishflow.club.client.CommunityServiceClient;
import com.englishflow.club.dto.ClubDTO;
import com.englishflow.club.dto.ClubWithRoleDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.Member;
import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.enums.ClubStatus;
import com.englishflow.club.exception.ClubNotFoundException;
import com.englishflow.club.exception.UnauthorizedException;
import com.englishflow.club.mapper.ClubMapper;
import com.englishflow.club.repository.ClubRepository;
import com.englishflow.club.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubService {
    
    private final ClubRepository clubRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ClubUpdateRequestService updateRequestService;
    private final ClubMapper clubMapper;
    private final WebSocketNotificationService wsNotificationService; // ← Ajout WebSocket
    private final SkillService skillService;
    private final CommunityServiceClient communityServiceClient;
    private final AuthServiceClient authServiceClient;
    
    @Cacheable(value = "clubs", key = "'all'")
    @Transactional(readOnly = true)
    public List<ClubDTO> getAllClubs() {
        log.debug("Fetching all clubs from database");
        return clubRepository.findAll().stream()
                .map(clubMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "clubsByCategory", key = "#category")
    @Transactional(readOnly = true)
    public List<ClubDTO> getClubsByCategory(ClubCategory category) {
        log.debug("Fetching clubs by category: {}", category);
        return clubRepository.findByCategory(category).stream()
                .map(clubMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ClubDTO> searchClubsByName(String name) {
        log.debug("Searching clubs by name: {}", name);
        return clubRepository.findByNameContainingIgnoreCase(name).stream()
                .map(clubMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "clubById", key = "#id")
    @Transactional(readOnly = true)
    public ClubDTO getClubById(Integer id) {
        log.debug("Fetching club by id: {}", id);
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ClubNotFoundException(id));
        return clubMapper.toDTO(club);
    }
    
    @Caching(evict = {
        @CacheEvict(value = "clubs", key = "'all'"),
        @CacheEvict(value = "clubsByCategory", allEntries = true)
    })
    @Transactional
    public ClubDTO createClub(ClubDTO clubDTO) {
        log.info("Creating new club: {}", clubDTO.getName());
        Club club = clubMapper.toEntity(clubDTO);
        Club savedClub = clubRepository.save(club);
        
        // Ajouter les skills si présentes
        if (clubDTO.getSkills() != null && !clubDTO.getSkills().isEmpty()) {
            skillService.updateClubSkills(savedClub.getId(), clubDTO.getSkills());
        }
        
        // Automatically add the creator as PRESIDENT
        if (clubDTO.getCreatedBy() != null) {
            memberService.addPresidentToClub(savedClub.getId(), clubDTO.getCreatedBy().longValue());
        }
        
        // 🔔 Envoyer notification WebSocket
        wsNotificationService.notifyClubCreated(savedClub.getId().longValue(), savedClub.getName());
        
        log.info("Club created successfully with id: {}", savedClub.getId());
        return clubMapper.toDTO(savedClub);
    }
    
    /**
     * Crée automatiquement une annonce dans la sous-catégorie "Official Announcements"
     * lors de l'approbation d'un nouveau club
     */
    private void createOfficialAnnouncement(Club club, Integer creatorId) {
        log.info("🚀 [ANNOUNCEMENT] Starting official announcement creation for club: {} (ID: {})", club.getName(), club.getId());
        try {
            // Récupérer toutes les sous-catégories
            log.info("📋 [ANNOUNCEMENT] Fetching all subcategories from community-service...");
            List<com.englishflow.club.dto.SubCategoryDTO> subCategories = communityServiceClient.getAllSubCategories();
            log.info("✅ [ANNOUNCEMENT] Retrieved {} subcategories", subCategories.size());
            
            // Trouver la sous-catégorie "Official Announcements"
            log.info("🔍 [ANNOUNCEMENT] Searching for 'Official Announcements' subcategory...");
            com.englishflow.club.dto.SubCategoryDTO officialAnnouncementsSubCategory = subCategories.stream()
                    .filter(sc -> "Official Announcements".equalsIgnoreCase(sc.getName()))
                    .findFirst()
                    .orElse(null);
            
            if (officialAnnouncementsSubCategory == null) {
                log.warn("⚠️ [ANNOUNCEMENT] Could not find 'Official Announcements' subcategory. Skipping announcement creation.");
                return;
            }
            log.info("✅ [ANNOUNCEMENT] Found 'Official Announcements' subcategory (ID: {})", officialAnnouncementsSubCategory.getId());
            
            // Utiliser le créateur du club (président) comme auteur de l'annonce
            Long clubPresidentUserId = club.getCreatedBy() != null ? club.getCreatedBy().longValue() : null;
            log.info("👤 [ANNOUNCEMENT] Club president user ID: {}", clubPresidentUserId);
            
            if (clubPresidentUserId == null) {
                log.warn("⚠️ [ANNOUNCEMENT] No creator found for club '{}'. Skipping announcement creation.", club.getName());
                return;
            }
            
            // Récupérer les informations du président du club
            log.info("📞 [ANNOUNCEMENT] Fetching president info from auth-service...");
            com.englishflow.club.dto.UserInfoDTO clubPresident = authServiceClient.getUserInfo(clubPresidentUserId);
            String authorName = clubPresident != null ? clubPresident.getFirstName() + " " + clubPresident.getLastName() : "Club President";
            log.info("✅ [ANNOUNCEMENT] President name: {}", authorName);
            
            // Créer le contenu de l'annonce
            String title = "🎉 New Club created: " + club.getName() + " 🌟";
            String content = buildAnnouncementContent(club);
            log.info("📝 [ANNOUNCEMENT] Announcement title: {}", title);
            
            // Créer la requête de topic avec isAutoGenerated = true
            com.englishflow.club.dto.CreateTopicRequest topicRequest = com.englishflow.club.dto.CreateTopicRequest.builder()
                    .title(title)
                    .content(content)
                    .userId(clubPresidentUserId)
                    .userName(authorName)
                    .subCategoryId(officialAnnouncementsSubCategory.getId())
                    .isAutoGenerated(true)
                    .resourceType("IMAGE")
                    .resourceLink(club.getImage()) // Stocker l'URL de l'image du club
                    .build();
            
            log.info("📤 [ANNOUNCEMENT] Sending POST request to community-service to create topic...");
            log.info("📤 [ANNOUNCEMENT] Request details - SubCategoryId: {}, UserId: {}, UserName: {}", 
                    officialAnnouncementsSubCategory.getId(), clubPresidentUserId, authorName);
            
            // Créer le topic via le community service
            com.englishflow.club.dto.TopicDTO createdTopic = communityServiceClient.createTopic(topicRequest);
            log.info("✅ [ANNOUNCEMENT] Topic created successfully with ID: {}", createdTopic.getId());
            
            // Sauvegarder l'ID du topic dans le club
            log.info("💾 [ANNOUNCEMENT] Saving topic ID {} to club record...", createdTopic.getId());
            club.setAnnouncementTopicId(createdTopic.getId().intValue());
            clubRepository.save(club);
            
            log.info("🎊 [ANNOUNCEMENT] ✅ Official announcement created successfully for club '{}' (Topic ID: {}) by president: {}", 
                    club.getName(), createdTopic.getId(), authorName);
            
        } catch (Exception e) {
            // Ne pas bloquer l'approbation du club si l'annonce échoue
            log.error("💥 [ANNOUNCEMENT] ❌ Failed to create official announcement for club '{}': {}", 
                    club.getName(), e.getMessage());
            log.error("💥 [ANNOUNCEMENT] Exception type: {}", e.getClass().getName());
            log.error("💥 [ANNOUNCEMENT] Stack trace:", e);
        }
    }
    
    /**
     * Construit le contenu de l'annonce pour un nouveau club approuvé
     * Note: L'image du club est stockée dans resourceLink et affichée dans le détail du topic
     */
    private String buildAnnouncementContent(Club club) {
        StringBuilder content = new StringBuilder();
        
        // Description du club
        content.append("<div style='background: white; padding: 20px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); border-left: 4px solid #F6BD60; margin-bottom: 16px;'>");
        content.append("<h3 style='color: #1a4d4d; margin: 0 0 12px 0; font-size: 20px; font-weight: 700;'>📋 About ").append(club.getName()).append("</h3>");
        
        if (club.getDescription() != null && !club.getDescription().isEmpty()) {
            content.append("<p style='color: #495057; margin: 0 0 12px 0; font-size: 14px; line-height: 1.6;'>").append(club.getDescription()).append("</p>");
        }
        
        // Badges (Category, Members, Fee)
        content.append("<div style='display: flex; flex-wrap: wrap; gap: 8px; margin-top: 12px;'>");
        content.append("<span style='background: linear-gradient(135deg, #1a4d4d, #2d7a7a); color: white; padding: 8px 14px; border-radius: 8px; font-size: 13px; font-weight: 600; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>🎯 ").append(formatCategory(club.getCategory())).append("</span>");
        if (club.getMaxMembers() != null) {
            content.append("<span style='background: linear-gradient(135deg, #F6BD60, #FFA500); color: white; padding: 8px 14px; border-radius: 8px; font-size: 13px; font-weight: 600; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>👥 Max ").append(club.getMaxMembers()).append(" members</span>");
        }
        content.append("<span style='background: ");
        content.append(club.getRegistrationFee() != null && club.getRegistrationFee() > 0 ? 
            "linear-gradient(135deg, #FFA500, #FF8C00)" : "linear-gradient(135deg, #2d7a7a, #1a4d4d)");
        content.append("; color: white; padding: 8px 14px; border-radius: 8px; font-size: 13px; font-weight: 600; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>💰 ");
        if (club.getRegistrationFee() != null && club.getRegistrationFee() > 0) {
            content.append(club.getRegistrationFee()).append(" TND");
        } else {
            content.append("FREE");
        }
        content.append("</span>");
        content.append("</div></div>");
        
        // Objectifs du club
        if (club.getObjective() != null && !club.getObjective().isEmpty()) {
            content.append("<div style='background: linear-gradient(135deg, #e8f5f5, #f0f9f9); padding: 20px; border-radius: 12px; border: 2px solid #2d7a7a; margin-bottom: 16px;'>");
            content.append("<h4 style='color: #1a4d4d; margin: 0 0 12px 0; font-size: 18px; font-weight: 700; display: flex; align-items: center; gap: 8px;'>");
            content.append("<span style='background: linear-gradient(135deg, #1a4d4d, #2d7a7a); color: white; width: 32px; height: 32px; border-radius: 8px; display: inline-flex; align-items: center; justify-content: center; font-size: 16px;'>🎯</span>");
            content.append("Our Objectives</h4>");
            content.append("<p style='color: #2c5f5f; margin: 0; font-size: 14px; line-height: 1.7; white-space: pre-wrap;'>").append(club.getObjective()).append("</p>");
            content.append("</div>");
        }
        
        // Skills du club
        List<com.englishflow.club.dto.SkillDTO> skills = skillService.getSkillsByClubId(club.getId());
        if (skills != null && !skills.isEmpty()) {
            content.append("<div style='background: linear-gradient(135deg, #fff8e8, #fffbf0); padding: 20px; border-radius: 12px; border: 2px solid #F6BD60; margin-bottom: 16px;'>");
            content.append("<h4 style='color: #1a4d4d; margin: 0 0 12px 0; font-size: 18px; font-weight: 700; display: flex; align-items: center; gap: 8px;'>");
            content.append("<span style='background: linear-gradient(135deg, #F6BD60, #FFA500); color: white; width: 32px; height: 32px; border-radius: 8px; display: inline-flex; align-items: center; justify-content: center; font-size: 16px;'>⭐</span>");
            content.append("Skills You'll Develop</h4>");
            content.append("<div style='display: flex; flex-wrap: wrap; gap: 10px;'>");
            for (com.englishflow.club.dto.SkillDTO skill : skills) {
                content.append("<span style='background: white; color: #d97706; padding: 10px 16px; border-radius: 8px; font-size: 13px; font-weight: 600; border: 2px solid #F6BD60; box-shadow: 0 2px 4px rgba(0,0,0,0.08);'>");
                content.append("✨ ").append(skill.getName());
                content.append("</span>");
            }
            content.append("</div></div>");
        }
        
        // How to Join
        content.append("<div style='background: white; padding: 20px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); border-left: 4px solid #2d7a7a; margin-bottom: 16px;'>");
        content.append("<h4 style='color: #1a4d4d; margin: 0 0 12px 0; font-size: 18px; font-weight: 700; display: flex; align-items: center; gap: 8px;'>");
        content.append("<span style='background: linear-gradient(135deg, #1a4d4d, #2d7a7a); color: white; width: 32px; height: 32px; border-radius: 8px; display: inline-flex; align-items: center; justify-content: center; font-size: 16px;'>🚀</span>");
        content.append("How to Join</h4>");
        content.append("<p style='margin: 0; color: #495057; font-size: 14px; line-height: 1.6;'>Go to <strong style='color: #1a4d4d;'>Clubs</strong> → Find <strong style='color: #1a4d4d;'>").append(club.getName()).append("</strong> → Click <strong style='color: #F6BD60;'>Submit Request</strong></p>");
        content.append("</div>");
        
        // Welcome message
        content.append("<div style='background: linear-gradient(135deg, #F6BD60, #FFA500); padding: 20px; border-radius: 12px; text-align: center; box-shadow: 0 4px 12px rgba(246, 189, 96, 0.3);'>");
        content.append("<p style='margin: 0; color: white; font-size: 18px; font-weight: 700; text-shadow: 0 2px 4px rgba(0,0,0,0.1);'>🌟 Welcome to ").append(club.getName()).append("! 🌟</p>");
        content.append("</div>");
        
        return content.toString();
    }
    
    /**
     * Formate le nom de la catégorie pour l'affichage
     */
    private String formatCategory(ClubCategory category) {
        if (category == null) return "N/A";
        
        switch (category) {
            case CONVERSATION:
                return "💬 Conversation Club";
            case BOOK:
                return "📚 Book Club";
            case DRAMA:
                return "🎭 Drama Club";
            case WRITING:
                return "✍️ Writing Club";
            default:
                return category.toString();
        }
    }
    
    @CacheEvict(value = "clubById", key = "#id")
    @Transactional
    public ClubDTO updateClub(Integer id, ClubDTO clubDTO, Long requesterId) {
        log.info("Updating club id: {} by user: {}", id, requesterId);
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ClubNotFoundException(id));
        
        // Verify that the requester is the president of the club
        boolean isPresident = memberService.isPresident(id, requesterId);
        if (!isPresident) {
            log.warn("Unauthorized update attempt on club {} by user {}", id, requesterId);
            throw new UnauthorizedException("Only the president can update club information");
        }
        
        // Créer une demande de modification au lieu de modifier directement
        updateRequestService.createUpdateRequest(id, clubDTO, requesterId);
        
        log.info("Update request created for club: {}", id);
        // Retourner le club actuel (non modifié)
        return clubMapper.toDTO(club);
    }
    
    @Caching(evict = {
        @CacheEvict(value = "clubs", key = "'all'"),
        @CacheEvict(value = "clubsByCategory", allEntries = true),
        @CacheEvict(value = "clubById", key = "#id")
    })
    @Transactional
    public void deleteClub(Integer id) {
        log.info("Deleting club id: {}", id);
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ClubNotFoundException(id));
        
        String clubName = club.getName();
        Integer announcementTopicId = club.getAnnouncementTopicId();
        
        // Delete the club announcement topic if it exists
        if (announcementTopicId != null) {
            try {
                // Use a system user ID (0) for automated deletions
                communityServiceClient.deleteTopic(announcementTopicId.longValue(), 0L);
                log.info("✅ Deleted announcement topic {} for club '{}'", announcementTopicId, clubName);
            } catch (Exception e) {
                log.error("❌ Failed to delete announcement topic {} for club '{}': {}", 
                        announcementTopicId, clubName, e.getMessage());
                // Continue with club deletion even if topic deletion fails
            }
        }
        
        clubRepository.deleteById(id);
        
        // 🔔 Envoyer notification WebSocket
        wsNotificationService.sendGlobalClubNotification(
            com.englishflow.club.dto.ClubNotificationDTO.builder()
                .type("CLUB_DELETED")
                .clubId(id.longValue())
                .clubName(clubName)
                .message("Club '" + clubName + "' has been deleted")
                .timestamp(java.time.LocalDateTime.now())
                .build()
        );
        
        log.info("Club deleted successfully: {}", id);
    }
    
    @Transactional(readOnly = true)
    public List<ClubDTO> getPendingClubs() {
        log.debug("Fetching pending clubs");
        return clubRepository.findByStatus(com.englishflow.club.enums.ClubStatus.PENDING).stream()
                .map(clubMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ClubDTO> getApprovedClubs() {
        log.debug("Fetching approved and suspended clubs");
        List<ClubStatus> statuses = List.of(ClubStatus.APPROVED, ClubStatus.SUSPENDED);
        return clubRepository.findByStatusIn(statuses).stream()
                .map(club -> {
                    ClubDTO dto = clubMapper.toDTO(club);
                    dto.setCurrentMembersCount((int) memberRepository.countByClubId(club.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ClubDTO> getClubsByUser(Integer userId) {
        log.debug("Fetching clubs created by user: {}", userId);
        return clubRepository.findByCreatedBy(userId).stream()
                .map(clubMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ClubWithRoleDTO> getClubsWithRoleByUser(Long userId) {
        log.debug("Fetching clubs with role for user: {}", userId);
        List<Member> memberships = memberRepository.findByUserId(userId);
        
        return memberships.stream()
                .map(member -> clubMapper.toClubWithRoleDTO(member.getClub(), member))
                .collect(Collectors.toList());
    }
    
    @Caching(evict = {
        @CacheEvict(value = "clubs", key = "'all'"),
        @CacheEvict(value = "clubById", key = "#id")
    })
    @Transactional
    public ClubDTO approveClub(Integer id, Integer reviewerId, String comment) {
        log.info("Approving club id: {} by reviewer: {}", id, reviewerId);
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ClubNotFoundException(id));
        
        club.setStatus(com.englishflow.club.enums.ClubStatus.APPROVED);
        club.setReviewedBy(reviewerId);
        club.setReviewComment(comment);
        
        Club updatedClub = clubRepository.save(club);
        
        // 📢 Créer automatiquement une annonce dans "Official Announcements" après approbation
        createOfficialAnnouncement(updatedClub, club.getCreatedBy());
        
        // 🔔 Envoyer notification WebSocket
        wsNotificationService.sendClubNotification(
            id.longValue(),
            com.englishflow.club.dto.ClubNotificationDTO.builder()
                .type("CLUB_APPROVED")
                .clubId(id.longValue())
                .clubName(club.getName())
                .message("Club '" + club.getName() + "' has been approved")
                .timestamp(java.time.LocalDateTime.now())
                .build()
        );
        
        log.info("Club approved successfully: {}", id);
        return clubMapper.toDTO(updatedClub);
    }
    
    @Caching(evict = {
        @CacheEvict(value = "clubs", key = "'all'"),
        @CacheEvict(value = "clubById", key = "#id")
    })
    @Transactional
    public ClubDTO rejectClub(Integer id, Integer reviewerId, String comment) {
        log.info("Rejecting club id: {} by reviewer: {}", id, reviewerId);
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ClubNotFoundException(id));
        
        club.setStatus(com.englishflow.club.enums.ClubStatus.REJECTED);
        club.setReviewedBy(reviewerId);
        club.setReviewComment(comment);
        
        Club updatedClub = clubRepository.save(club);
        log.info("Club rejected: {}", id);
        return clubMapper.toDTO(updatedClub);
    }
    
    @Caching(evict = {
        @CacheEvict(value = "clubs", key = "'all'"),
        @CacheEvict(value = "clubById", key = "#id")
    })
    @Transactional
    public ClubDTO suspendClub(Integer id, Integer managerId, String reason) {
        log.info("Suspending club id: {} by manager: {}", id, managerId);
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ClubNotFoundException(id));
        
        if (club.getStatus() != com.englishflow.club.enums.ClubStatus.APPROVED) {
            throw new IllegalStateException("Only approved clubs can be suspended");
        }
        
        String clubName = club.getName();
        Integer announcementTopicId = club.getAnnouncementTopicId();
        
        // Delete the club announcement topic if it exists
        if (announcementTopicId != null) {
            try {
                // Use a system user ID (0) for automated deletions
                communityServiceClient.deleteTopic(announcementTopicId.longValue(), 0L);
                log.info("✅ Deleted announcement topic {} for suspended club '{}'", announcementTopicId, clubName);
            } catch (Exception e) {
                log.error("❌ Failed to delete announcement topic {} for club '{}': {}", 
                        announcementTopicId, clubName, e.getMessage());
                // Continue with club suspension even if topic deletion fails
            }
        }
        
        club.setStatus(com.englishflow.club.enums.ClubStatus.SUSPENDED);
        club.setSuspendedBy(managerId);
        club.setSuspensionReason(reason);
        club.setSuspendedAt(java.time.LocalDateTime.now());
        
        Club updatedClub = clubRepository.save(club);
        log.info("Club suspended successfully: {}", id);
        return clubMapper.toDTO(updatedClub);
    }
    
    @Caching(evict = {
        @CacheEvict(value = "clubs", key = "'all'"),
        @CacheEvict(value = "clubById", key = "#id")
    })
    @Transactional
    public ClubDTO activateClub(Integer id, Integer managerId) {
        log.info("Activating club id: {} by manager: {}", id, managerId);
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ClubNotFoundException(id));
        
        if (club.getStatus() != com.englishflow.club.enums.ClubStatus.SUSPENDED) {
            throw new IllegalStateException("Only suspended clubs can be activated");
        }
        
        club.setStatus(com.englishflow.club.enums.ClubStatus.APPROVED);
        club.setSuspendedBy(null);
        club.setSuspensionReason(null);
        club.setSuspendedAt(null);
        
        Club updatedClub = clubRepository.save(club);
        
        // 📢 Recreate the announcement when club is reactivated
        createOfficialAnnouncement(updatedClub, club.getCreatedBy());
        
        log.info("Club activated successfully: {}", id);
        return clubMapper.toDTO(updatedClub);
    }
}
