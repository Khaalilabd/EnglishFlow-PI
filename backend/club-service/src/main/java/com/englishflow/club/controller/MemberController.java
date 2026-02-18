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
            @RequestParam RankType rank,
            @RequestParam Long requesterId) {
        MemberDTO updatedMember = memberService.updateMemberRank(memberId, rank, requesterId);
        return ResponseEntity.ok(updatedMember);
    }
    
    @PatchMapping("/{memberId}/role")
    public ResponseEntity<MemberDTO> updateMemberRole(
            @PathVariable Integer memberId,
            @RequestBody UpdateRoleRequest request,
            @RequestParam(required = true) Long requesterId) {
        try {
            System.out.println("🔧 Controller: Updating role for memberId: " + memberId + ", newRank: " + request.getRank() + ", requesterId: " + requesterId);
            
            if (request.getRank() == null || request.getRank().trim().isEmpty()) {
                System.err.println("❌ Rank is null or empty");
                throw new IllegalArgumentException("Rank cannot be null or empty");
            }
            
            RankType rank = RankType.valueOf(request.getRank().toUpperCase());
            System.out.println("✅ Rank parsed successfully: " + rank);
            
            MemberDTO updatedMember = memberService.updateMemberRank(memberId, rank, requesterId);
            System.out.println("✅ Role updated successfully for memberId: " + memberId);
            return ResponseEntity.ok(updatedMember);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Invalid rank type: " + request.getRank());
            System.err.println("❌ Error details: " + e.getMessage());
            throw new IllegalArgumentException("Invalid rank type: " + request.getRank() + ". Valid values are: " + 
                String.join(", ", java.util.Arrays.stream(RankType.values()).map(Enum::name).toArray(String[]::new)));
        } catch (Exception e) {
            System.err.println("❌ Error updating role: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // Inner class for request body
    @lombok.Data
    public static class UpdateRoleRequest {
        private String rank;
    }
    
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Integer memberId,
            @RequestParam Long requesterId) {
        memberService.removeMemberFromClub(memberId, requesterId);
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
    
    @GetMapping("/club/{clubId}/user/{userId}/is-president")
    public ResponseEntity<Boolean> isPresident(
            @PathVariable Integer clubId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(memberService.isPresident(clubId, userId));
    }
    
    @GetMapping("/club/{clubId}/user/{userId}/is-member")
    public ResponseEntity<Boolean> isMember(
            @PathVariable Integer clubId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(memberService.isMember(clubId, userId));
    }
}
