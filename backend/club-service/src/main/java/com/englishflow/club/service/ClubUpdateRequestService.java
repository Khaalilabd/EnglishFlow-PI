package com.englishflow.club.service;

import com.englishflow.club.dto.ClubDTO;
import com.englishflow.club.dto.ClubUpdateRequestDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.ClubUpdateRequest;
import com.englishflow.club.enums.RankType;
import com.englishflow.club.enums.UpdateRequestStatus;
import com.englishflow.club.repository.ClubRepository;
import com.englishflow.club.repository.ClubUpdateRequestRepository;
import com.englishflow.club.repository.MemberRepository;
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
public class ClubUpdateRequestService {
    
    private final ClubUpdateRequestRepository updateRequestRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    
    /**
     * Créer une demande de modification de club (par le président)
     */
    @Transactional
    public ClubUpdateRequestDTO createUpdateRequest(Integer clubId, ClubDTO clubDTO, Long requesterId) {
        log.info("Creating update request for club {} by user {}", clubId, requesterId);
        
        // Vérifier que le club existe
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + clubId));
        
        // Vérifier que le requester est le président
        boolean isPresident = memberService.isPresident(clubId, requesterId);
        if (!isPresident) {
            throw new RuntimeException("Seul le président peut créer une demande de modification");
        }
        
        // Vérifier qu'il n'y a pas déjà une demande en attente
        updateRequestRepository.findFirstByClubIdAndStatusOrderByCreatedAtDesc(clubId, UpdateRequestStatus.PENDING)
                .ifPresent(existing -> {
                    throw new RuntimeException("Une demande de modification est déjà en attente pour ce club");
                });
        
        // Créer la demande
        ClubUpdateRequest request = ClubUpdateRequest.builder()
                .club(club)
                .requestedBy(requesterId)
                .name(clubDTO.getName())
                .description(clubDTO.getDescription())
                .objective(clubDTO.getObjective())
                .category(clubDTO.getCategory())
                .maxMembers(clubDTO.getMaxMembers())
                .image(clubDTO.getImage())
                .status(UpdateRequestStatus.PENDING)
                .vicePresidentApproved(false)
                .secretaryApproved(false)
                .build();
        
        ClubUpdateRequest savedRequest = updateRequestRepository.save(request);
        log.info("Update request created with id: {}", savedRequest.getId());
        
        return convertToDTO(savedRequest);
    }
    
    /**
     * Approuver une demande de modification
     */
    @Transactional
    public ClubUpdateRequestDTO approveUpdateRequest(Integer requestId, Long approverId) {
        log.info("Approving update request {} by user {}", requestId, approverId);
        
        ClubUpdateRequest request = updateRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Update request not found with id: " + requestId));
        
        if (request.getStatus() != UpdateRequestStatus.PENDING) {
            throw new RuntimeException("Cette demande n'est plus en attente");
        }
        
        // Vérifier le rôle de l'approbateur
        RankType approverRank = memberRepository.findByClubIdAndUserId(request.getClub().getId(), approverId)
                .map(member -> member.getRank())
                .orElseThrow(() -> new RuntimeException("Vous n'êtes pas membre de ce club"));
        
        // Marquer l'approbation selon le rôle
        if (approverRank == RankType.VICE_PRESIDENT) {
            if (request.getVicePresidentApproved()) {
                throw new RuntimeException("Vous avez déjà approuvé cette demande");
            }
            request.setVicePresidentApproved(true);
            log.info("Vice-président a approuvé la demande {}", requestId);
        } else if (approverRank == RankType.SECRETARY) {
            if (request.getSecretaryApproved()) {
                throw new RuntimeException("Vous avez déjà approuvé cette demande");
            }
            request.setSecretaryApproved(true);
            log.info("Secrétaire a approuvé la demande {}", requestId);
        } else {
            throw new RuntimeException("Seuls le vice-président et le secrétaire peuvent approuver les modifications");
        }
        
        // Si les deux ont approuvé, appliquer les modifications
        if (request.getVicePresidentApproved() && request.getSecretaryApproved()) {
            applyUpdateRequest(request);
        }
        
        ClubUpdateRequest savedRequest = updateRequestRepository.save(request);
        return convertToDTO(savedRequest);
    }
    
    /**
     * Rejeter une demande de modification
     */
    @Transactional
    public ClubUpdateRequestDTO rejectUpdateRequest(Integer requestId, Long rejecterId) {
        log.info("Rejecting update request {} by user {}", requestId, rejecterId);
        
        ClubUpdateRequest request = updateRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Update request not found with id: " + requestId));
        
        if (request.getStatus() != UpdateRequestStatus.PENDING) {
            throw new RuntimeException("Cette demande n'est plus en attente");
        }
        
        // Vérifier le rôle du rejeteur
        RankType rejecterRank = memberRepository.findByClubIdAndUserId(request.getClub().getId(), rejecterId)
                .map(member -> member.getRank())
                .orElseThrow(() -> new RuntimeException("Vous n'êtes pas membre de ce club"));
        
        if (rejecterRank != RankType.VICE_PRESIDENT && rejecterRank != RankType.SECRETARY) {
            throw new RuntimeException("Seuls le vice-président et le secrétaire peuvent rejeter les modifications");
        }
        
        request.setStatus(UpdateRequestStatus.REJECTED);
        ClubUpdateRequest savedRequest = updateRequestRepository.save(request);
        
        log.info("Update request {} rejected", requestId);
        return convertToDTO(savedRequest);
    }
    
    /**
     * Appliquer les modifications au club
     */
    private void applyUpdateRequest(ClubUpdateRequest request) {
        log.info("Applying update request {} to club {}", request.getId(), request.getClub().getId());
        
        Club club = request.getClub();
        club.setName(request.getName());
        club.setDescription(request.getDescription());
        club.setObjective(request.getObjective());
        club.setCategory(request.getCategory());
        club.setMaxMembers(request.getMaxMembers());
        
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            club.setImage(request.getImage());
        }
        
        clubRepository.save(club);
        
        request.setStatus(UpdateRequestStatus.APPROVED);
        request.setAppliedAt(LocalDateTime.now());
        
        log.info("Update request {} applied successfully", request.getId());
    }
    
    /**
     * Récupérer les demandes en attente pour un club
     */
    @Transactional(readOnly = true)
    public List<ClubUpdateRequestDTO> getPendingRequestsForClub(Integer clubId) {
        return updateRequestRepository.findByClubIdAndStatus(clubId, UpdateRequestStatus.PENDING).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer toutes les demandes pour un club
     */
    @Transactional(readOnly = true)
    public List<ClubUpdateRequestDTO> getAllRequestsForClub(Integer clubId) {
        return updateRequestRepository.findByClubId(clubId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer une demande par ID
     */
    @Transactional(readOnly = true)
    public ClubUpdateRequestDTO getRequestById(Integer requestId) {
        ClubUpdateRequest request = updateRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Update request not found with id: " + requestId));
        return convertToDTO(request);
    }
    
    private ClubUpdateRequestDTO convertToDTO(ClubUpdateRequest request) {
        return ClubUpdateRequestDTO.builder()
                .id(request.getId())
                .clubId(request.getClub().getId())
                .requestedBy(request.getRequestedBy())
                .name(request.getName())
                .description(request.getDescription())
                .objective(request.getObjective())
                .category(request.getCategory())
                .maxMembers(request.getMaxMembers())
                .image(request.getImage())
                .status(request.getStatus())
                .vicePresidentApproved(request.getVicePresidentApproved())
                .secretaryApproved(request.getSecretaryApproved())
                .createdAt(request.getCreatedAt())
                .appliedAt(request.getAppliedAt())
                .build();
    }
}
