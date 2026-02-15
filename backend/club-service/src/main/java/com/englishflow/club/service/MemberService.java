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
        return memberRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
    public MemberDTO updateMemberRank(Integer memberId, RankType newRank) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        
        member.setRank(newRank);
        Member updatedMember = memberRepository.save(member);
        return convertToDTO(updatedMember);
    }
    
    @Transactional
    public void removeMemberFromClub(Integer memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new RuntimeException("Member not found with id: " + memberId);
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
    
    private MemberDTO convertToDTO(Member member) {
        return MemberDTO.builder()
                .id(member.getId())
                .rank(member.getRank())
                .clubId(member.getClub().getId())
                .userId(member.getUserId())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
