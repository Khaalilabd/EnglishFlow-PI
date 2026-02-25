package com.englishflow.club.service;

import com.englishflow.club.dto.MemberDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.Member;
import com.englishflow.club.enums.RankType;
import com.englishflow.club.repository.ClubRepository;
import com.englishflow.club.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;
    
    @Transactional(readOnly = true)
    public List<MemberDTO> getMembersByClub(Integer clubId) {
        return memberRepository.findByClubId(clubId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MemberDTO> getMembersByUser(Long userId) {
        try {
            List<Member> members = memberRepository.findByUserId(userId);
            return members.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching members for user " + userId + ": " + e.getMessage(), e);
        }
    }
    
    @Transactional
    public MemberDTO addMemberToClub(Integer clubId, Long userId) {
        // Check if club exists
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + clubId));
        
        // Check if club is full
        if (club.isFull()) {
            throw new RuntimeException("Club is full. Maximum members: " + club.getMaxMembers());
        }
        
        // Check if user is already a member
        if (memberRepository.existsByClubIdAndUserId(clubId, userId)) {
            throw new RuntimeException("User is already a member of this club");
        }
        
        // Create new member with default rank MEMBER
        Member member = Member.builder()
                .club(club)
                .userId(userId)
                .rank(RankType.MEMBER)
                .build();
        
        Member savedMember = memberRepository.save(member);
        return convertToDTO(savedMember);
    }
    
    @Transactional
    public MemberDTO addPresidentToClub(Integer clubId, Long userId) {
        // Check if club exists
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + clubId));
        
        // Check if user is already a member
        if (memberRepository.existsByClubIdAndUserId(clubId, userId)) {
            throw new RuntimeException("User is already a member of this club");
        }
        
        // Create new member with PRESIDENT rank
        Member member = Member.builder()
                .club(club)
                .userId(userId)
                .rank(RankType.PRESIDENT)
                .build();
        
        Member savedMember = memberRepository.save(member);
        return convertToDTO(savedMember);
    }
    
    @Transactional
    public MemberDTO updateMemberRank(Integer memberId, RankType newRank, Long requesterId) {
        System.out.println("📝 MemberService.updateMemberRank - START");
        System.out.println("   memberId: " + memberId);
        System.out.println("   newRank: " + newRank);
        System.out.println("   requesterId: " + requesterId);
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        
        System.out.println("👤 Found member:");
        System.out.println("   member.id: " + member.getId());
        System.out.println("   member.userId: " + member.getUserId());
        System.out.println("   member.currentRank: " + member.getRank());
        System.out.println("   member.clubId: " + member.getClub().getId());
        
        // Check if requester is the president of the club
        boolean isPresidentCheck = isPresident(member.getClub().getId(), requesterId);
        System.out.println("🔐 Authorization check:");
        System.out.println("   requesterId: " + requesterId);
        System.out.println("   clubId: " + member.getClub().getId());
        System.out.println("   isPresident: " + isPresidentCheck);
        
        if (!isPresidentCheck) {
            System.err.println("❌ Authorization failed: Requester is not president");
            throw new RuntimeException("Only the president can change member ranks");
        }
        
        // Prevent changing the rank if this is the only president
        if (member.getRank() == RankType.PRESIDENT && newRank != RankType.PRESIDENT) {
            long presidentCount = memberRepository.findByClubIdAndRank(member.getClub().getId(), RankType.PRESIDENT).size();
            System.out.println("👑 President count check: " + presidentCount);
            if (presidentCount <= 1) {
                System.err.println("❌ Cannot remove last president");
                throw new RuntimeException("Cannot change the rank of the last president. Assign another president first.");
            }
        }
        
        System.out.println("🔄 Updating rank from " + member.getRank() + " to " + newRank);
        member.setRank(newRank);
        Member updatedMember = memberRepository.save(member);
        
        System.out.println("✅ Member rank updated successfully:");
        System.out.println("   member.id: " + updatedMember.getId());
        System.out.println("   member.newRank: " + updatedMember.getRank());
        System.out.println("   member.updatedAt: " + updatedMember.getUpdatedAt());
        
        return convertToDTO(updatedMember);
    }
    
    @Transactional(readOnly = true)
    public boolean isPresident(Integer clubId, Long userId) {
        return memberRepository.findByClubIdAndUserId(clubId, userId)
                .map(member -> member.getRank() == RankType.PRESIDENT)
                .orElse(false);
    }
    
    @Transactional(readOnly = true)
    public boolean isMember(Integer clubId, Long userId) {
        return memberRepository.existsByClubIdAndUserId(clubId, userId);
    }
    
    @Transactional
    public void removeMemberFromClub(Integer memberId, Long requesterId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        
        // Check if requester is the president of the club
        if (!isPresident(member.getClub().getId(), requesterId)) {
            throw new RuntimeException("Only the president can remove members");
        }
        
        // Prevent removing the president
        if (member.getRank() == RankType.PRESIDENT) {
            throw new RuntimeException("Cannot remove the president from the club");
        }
        
        memberRepository.deleteById(memberId);
    }
    
    @Transactional
    public void removeMemberByUserAndClub(Integer clubId, Long userId) {
        Member member = memberRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        memberRepository.delete(member);
    }
    
    @Transactional(readOnly = true)
    public long getClubMemberCount(Integer clubId) {
        return memberRepository.countByClubId(clubId);
    }

    @Transactional(readOnly = true)
    public List<com.englishflow.club.dto.ClubWithRoleDTO> getUserClubsWithStatus(Long userId) {
        List<Member> members = memberRepository.findByUserId(userId);
        return members.stream()
                .map(member -> {
                    Club club = member.getClub();
                    return com.englishflow.club.dto.ClubWithRoleDTO.builder()
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

    
    private MemberDTO convertToDTO(Member member) {
        try {
            return MemberDTO.builder()
                    .id(member.getId())
                    .rank(member.getRank())
                    .clubId(member.getClub() != null ? member.getClub().getId() : null)
                    .userId(member.getUserId())
                    .joinedAt(member.getJoinedAt())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error converting member to DTO: " + e.getMessage(), e);
        }
    }
}
