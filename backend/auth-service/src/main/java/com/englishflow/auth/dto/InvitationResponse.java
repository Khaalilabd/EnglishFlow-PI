package com.englishflow.auth.dto;

import com.englishflow.auth.entity.Invitation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationResponse {
    
    private Long id;
    private String email;
    private String token;
    private String role;
    private LocalDateTime expiryDate;
    private boolean used;
    private Long invitedBy;
    private LocalDateTime createdAt;
    private LocalDateTime usedAt;
    
    public static InvitationResponse fromEntity(Invitation invitation) {
        return InvitationResponse.builder()
                .id(invitation.getId())
                .email(invitation.getEmail())
                .token(invitation.getToken())
                .role(invitation.getRole().name())
                .expiryDate(invitation.getExpiryDate())
                .used(invitation.isUsed())
                .invitedBy(invitation.getInvitedBy())
                .createdAt(invitation.getCreatedAt())
                .usedAt(invitation.getUsedAt())
                .build();
    }
}
