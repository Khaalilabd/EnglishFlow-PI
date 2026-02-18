package com.englishflow.club.controller;

import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.Member;
import com.englishflow.club.enums.RankType;
import com.englishflow.club.repository.ClubRepository;
import com.englishflow.club.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    
    @PostMapping("/fix-missing-presidents")
    public ResponseEntity<Map<String, Object>> fixMissingPresidents() {
        List<Club> allClubs = clubRepository.findAll();
        int fixed = 0;
        int alreadyOk = 0;
        
        for (Club club : allClubs) {
            if (club.getCreatedBy() != null) {
                // Check if creator is already a president
                boolean hasPresident = memberRepository.findByClubIdAndUserId(club.getId(), club.getCreatedBy().longValue())
                    .map(member -> member.getRank() == RankType.PRESIDENT)
                    .orElse(false);
                
                if (!hasPresident) {
                    // Add creator as president
                    Member president = Member.builder()
                        .club(club)
                        .userId(club.getCreatedBy().longValue())
                        .rank(RankType.PRESIDENT)
                        .build();
                    
                    // Manually set joinedAt to match club creation
                    president.setJoinedAt(club.getCreatedAt());
                    
                    memberRepository.save(president);
                    fixed++;
                } else {
                    alreadyOk++;
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalClubs", allClubs.size());
        result.put("fixed", fixed);
        result.put("alreadyOk", alreadyOk);
        result.put("message", "Fixed " + fixed + " clubs. " + alreadyOk + " clubs were already OK.");
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/check-club-presidents")
    public ResponseEntity<List<Map<String, Object>>> checkClubPresidents() {
        List<Club> allClubs = clubRepository.findAll();
        
        return ResponseEntity.ok(
            allClubs.stream().map(club -> {
                Map<String, Object> info = new HashMap<>();
                info.put("clubId", club.getId());
                info.put("clubName", club.getName());
                info.put("createdBy", club.getCreatedBy());
                
                if (club.getCreatedBy() != null) {
                    boolean hasPresident = memberRepository.findByClubIdAndUserId(club.getId(), club.getCreatedBy().longValue())
                        .map(member -> member.getRank() == RankType.PRESIDENT)
                        .orElse(false);
                    
                    info.put("hasPresident", hasPresident);
                    info.put("memberCount", memberRepository.countByClubId(club.getId()));
                } else {
                    info.put("hasPresident", false);
                    info.put("memberCount", 0);
                }
                
                return info;
            }).toList()
        );
    }
}
