package com.englishflow.club.controller;

import com.englishflow.club.dto.MemberDTO;
import com.englishflow.club.enums.RankType;
import com.englishflow.club.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberService memberService;
    
    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<MemberDTO>> getMembersByClub(@PathVariable Integer clubId) {
        return ResponseEntity.ok(memberService.getMembersByClub(clubId));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MemberDTO>> getMembersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(memberService.getMembersByUser(userId));
    }
    
    @PostMapping("/club/{clubId}/user/{userId}")
    public ResponseEntity<MemberDTO> addMemberToClub(
            @PathVariable Integer clubId,
            @PathVariable Long userId) {
        MemberDTO member = memberService.addMemberToClub(clubId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }
    
    @PutMapping("/{memberId}/rank")
    public ResponseEntity<MemberDTO> updateMemberRank(
            @PathVariable Integer memberId,
            @RequestParam RankType rank) {
        MemberDTO updatedMember = memberService.updateMemberRank(memberId, rank);
        return ResponseEntity.ok(updatedMember);
    }
    
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable Integer memberId) {
        memberService.removeMemberFromClub(memberId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/club/{clubId}/user/{userId}")
    public ResponseEntity<Void> removeMemberByUserAndClub(
            @PathVariable Integer clubId,
            @PathVariable Long userId) {
        memberService.removeMemberByUserAndClub(clubId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/club/{clubId}/count")
    public ResponseEntity<Long> getClubMemberCount(@PathVariable Integer clubId) {
        return ResponseEntity.ok(memberService.getClubMemberCount(clubId));
    }
}
