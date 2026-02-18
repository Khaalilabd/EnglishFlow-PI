package com.englishflow.club.service;

import com.englishflow.club.dto.ClubDTO;
import com.englishflow.club.dto.ClubWithRoleDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.Member;
import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.repository.ClubRepository;
import com.englishflow.club.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubService {
    
    private final ClubRepository clubRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    
    @Transactional(readOnly = true)
    public List<ClubDTO> getAllClubs() {
        return clubRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ClubDTO> getClubsByCategory(ClubCategory category) {
        return clubRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ClubDTO> searchClubsByName(String name) {
        return clubRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ClubDTO getClubById(Integer id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + id));
        return convertToDTO(club);
    }
    
    @Transactional
    public ClubDTO createClub(ClubDTO clubDTO) {
        Club club = convertToEntity(clubDTO);
        Club savedClub = clubRepository.save(club);
        
        // Automatically add the creator as PRESIDENT
        if (clubDTO.getCreatedBy() != null) {
            memberService.addPresidentToClub(savedClub.getId(), clubDTO.getCreatedBy().longValue());
        }
        
        return convertToDTO(savedClub);
    }
    
    @Transactional
    public ClubDTO updateClub(Integer id, ClubDTO clubDTO) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + id));
        
        club.setName(clubDTO.getName());
        club.setDescription(clubDTO.getDescription());
        club.setObjective(clubDTO.getObjective());
        club.setCategory(clubDTO.getCategory());
        club.setMaxMembers(clubDTO.getMaxMembers());
        
        // Update image if provided
        if (clubDTO.getImage() != null && !clubDTO.getImage().isEmpty()) {
            club.setImage(clubDTO.getImage());
        }
        
        Club updatedClub = clubRepository.save(club);
        return convertToDTO(updatedClub);
    }
    
    @Transactional
    public void deleteClub(Integer id) {
        if (!clubRepository.existsById(id)) {
            throw new RuntimeException("Club not found with id: " + id);
        }
        clubRepository.deleteById(id);
    }
    
    // Méthodes pour le workflow d'approbation
    @Transactional(readOnly = true)
    public List<ClubDTO> getPendingClubs() {
        return clubRepository.findByStatus(com.englishflow.club.enums.ClubStatus.PENDING).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ClubDTO> getApprovedClubs() {
        return clubRepository.findByStatus(com.englishflow.club.enums.ClubStatus.APPROVED).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ClubDTO> getClubsByUser(Integer userId) {
        return clubRepository.findByCreatedBy(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ClubWithRoleDTO> getClubsWithRoleByUser(Long userId) {
        // Get all memberships for the user
        List<Member> memberships = memberRepository.findByUserId(userId);
        
        // Convert to ClubWithRoleDTO
        return memberships.stream()
                .map(member -> {
                    Club club = member.getClub();
                    return ClubWithRoleDTO.builder()
                            .id(club.getId())
                            .name(club.getName())
                            .description(club.getDescription())
                            .objective(club.getObjective())
                            .category(club.getCategory())
                            .maxMembers(club.getMaxMembers())
                            .image(club.getImage())
                            .status(club.getStatus())
                            .createdBy(club.getCreatedBy())
                            .reviewedBy(club.getReviewedBy())
                            .reviewComment(club.getReviewComment())
                            .createdAt(club.getCreatedAt())
                            .updatedAt(club.getUpdatedAt())
                            .userRole(member.getRank())
                            .joinedAt(member.getJoinedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ClubDTO approveClub(Integer id, Integer reviewerId, String comment) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + id));
        
        club.setStatus(com.englishflow.club.enums.ClubStatus.APPROVED);
        club.setReviewedBy(reviewerId);
        club.setReviewComment(comment);
        
        Club updatedClub = clubRepository.save(club);
        return convertToDTO(updatedClub);
    }
    
    @Transactional
    public ClubDTO rejectClub(Integer id, Integer reviewerId, String comment) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + id));
        
        club.setStatus(com.englishflow.club.enums.ClubStatus.REJECTED);
        club.setReviewedBy(reviewerId);
        club.setReviewComment(comment);
        
        Club updatedClub = clubRepository.save(club);
        return convertToDTO(updatedClub);
    }
    
    private ClubDTO convertToDTO(Club club) {
        return ClubDTO.builder()
                .id(club.getId())
                .name(club.getName())
                .description(club.getDescription())
                .objective(club.getObjective())
                .category(club.getCategory())
                .maxMembers(club.getMaxMembers())
                .image(club.getImage())
                .status(club.getStatus())
                .createdBy(club.getCreatedBy())
                .reviewedBy(club.getReviewedBy())
                .reviewComment(club.getReviewComment())
                .createdAt(club.getCreatedAt())
                .updatedAt(club.getUpdatedAt())
                .build();
    }
    
    private Club convertToEntity(ClubDTO clubDTO) {
        return Club.builder()
                .name(clubDTO.getName())
                .description(clubDTO.getDescription())
                .objective(clubDTO.getObjective())
                .category(clubDTO.getCategory())
                .maxMembers(clubDTO.getMaxMembers())
                .image(clubDTO.getImage())
                .createdBy(clubDTO.getCreatedBy())
                .build();
    }
}
