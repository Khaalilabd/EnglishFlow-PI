package com.englishflow.auth.service;

import com.englishflow.auth.dto.AcceptInvitationRequest;
import com.englishflow.auth.dto.InvitationRequest;
import com.englishflow.auth.dto.InvitationResponse;
import com.englishflow.auth.entity.Invitation;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.InvitationRepository;
import com.englishflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public InvitationResponse sendInvitation(InvitationRequest request, Long invitedBy) {
        // Validate role
        User.Role role;
        try {
            role = User.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + request.getRole());
        }

        // Only TUTOR and ACADEMIC_OFFICE_AFFAIR can be invited
        if (role != User.Role.TUTOR && role != User.Role.ACADEMIC_OFFICE_AFFAIR) {
            throw new RuntimeException("Only TUTOR and ACADEMIC_OFFICE_AFFAIR roles can be invited");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        // Check if there's already a pending invitation
        if (invitationRepository.existsByEmailAndUsedFalse(request.getEmail())) {
            throw new RuntimeException("An invitation has already been sent to this email");
        }

        // Create invitation
        String token = UUID.randomUUID().toString();
        Invitation invitation = Invitation.builder()
                .email(request.getEmail())
                .token(token)
                .role(role)
                .expiryDate(LocalDateTime.now().plusDays(7)) // 7 days to accept
                .used(false)
                .invitedBy(invitedBy)
                .build();

        Invitation savedInvitation = invitationRepository.save(invitation);

        // Send invitation email
        try {
            emailService.sendInvitationEmail(
                    request.getEmail(),
                    role.name(),
                    token
            );
            log.info("Invitation email sent to: {}", request.getEmail());
        } catch (Exception e) {
            log.error("Failed to send invitation email to: {}", request.getEmail(), e);
            throw new RuntimeException("Failed to send invitation email", e);
        }

        return InvitationResponse.fromEntity(savedInvitation);
    }

    @Transactional(readOnly = true)
    public InvitationResponse getInvitationByToken(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        if (invitation.isUsed()) {
            throw new RuntimeException("This invitation has already been used");
        }

        if (invitation.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This invitation has expired");
        }

        return InvitationResponse.fromEntity(invitation);
    }

    @Transactional
    public User acceptInvitation(AcceptInvitationRequest request) {
        // Find invitation
        Invitation invitation = invitationRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        // Validate invitation
        if (invitation.isUsed()) {
            throw new RuntimeException("This invitation has already been used");
        }

        if (invitation.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This invitation has expired");
        }

        // Check if email already exists (double check)
        if (userRepository.existsByEmail(invitation.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        // Create user
        User user = new User();
        user.setEmail(invitation.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setCin(request.getCin());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setPostalCode(request.getPostalCode());
        user.setBio(request.getBio());
        user.setYearsOfExperience(request.getYearsOfExperience());
        user.setRole(invitation.getRole());
        user.setActive(false); // Require admin activation
        user.setRegistrationFeePaid(false);
        user.setProfileCompleted(true);

        User savedUser = userRepository.save(user);

        // Mark invitation as used
        invitation.setUsed(true);
        invitation.setUsedAt(LocalDateTime.now());
        invitationRepository.save(invitation);

        // Send welcome email
        try {
            emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());
            log.info("Welcome email sent to: {}", savedUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", savedUser.getEmail(), e);
        }

        return savedUser;
    }

    @Transactional(readOnly = true)
    public List<InvitationResponse> getAllInvitations() {
        return invitationRepository.findAll().stream()
                .map(InvitationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvitationResponse> getPendingInvitations() {
        return invitationRepository.findByUsedFalse().stream()
                .map(InvitationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        if (invitation.isUsed()) {
            throw new RuntimeException("Cannot cancel an invitation that has already been used");
        }

        invitationRepository.delete(invitation);
    }

    @Transactional
    public InvitationResponse resendInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        if (invitation.isUsed()) {
            throw new RuntimeException("Cannot resend an invitation that has already been used");
        }

        // Extend expiry date
        invitation.setExpiryDate(LocalDateTime.now().plusDays(7));
        Invitation updatedInvitation = invitationRepository.save(invitation);

        // Resend email
        try {
            emailService.sendInvitationEmail(
                    invitation.getEmail(),
                    invitation.getRole().name(),
                    invitation.getToken()
            );
            log.info("Invitation email resent to: {}", invitation.getEmail());
        } catch (Exception e) {
            log.error("Failed to resend invitation email to: {}", invitation.getEmail(), e);
            throw new RuntimeException("Failed to resend invitation email", e);
        }

        return InvitationResponse.fromEntity(updatedInvitation);
    }

    @Transactional
    public void cleanupExpiredInvitations() {
        List<Invitation> expiredInvitations = invitationRepository
                .findByExpiryDateBeforeAndUsedFalse(LocalDateTime.now());
        
        if (!expiredInvitations.isEmpty()) {
            invitationRepository.deleteAll(expiredInvitations);
            log.info("Cleaned up {} expired invitations", expiredInvitations.size());
        }
    }
}
