package com.englishflow.club.dto;

import com.englishflow.club.enums.MembershipRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipRequestDTO {
    private Integer id;
    private Integer clubId;
    private String clubName;
    private Double registrationFee;
    private Long userId;
    private String userName;
    private String userEmail;
    private MembershipRequestStatus status;
    private String message;
    private String motivationLetter; // Lettre de motivation
    private String studentSkills; // Compétences de l'étudiant
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
    private Long reviewedBy;
    private String reviewComment;
    private String paymentMethod;
    private String paymentToken;
    private LocalDateTime paymentConfirmedAt;
    private LocalDateTime paymentDeadline;
}
