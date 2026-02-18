package com.englishflow.auth.service;

import com.englishflow.auth.dto.AuthResponse;
import com.englishflow.auth.dto.LoginRequest;
import com.englishflow.auth.dto.RegisterRequest;
import com.englishflow.auth.dto.PasswordResetRequest;
import com.englishflow.auth.dto.PasswordResetConfirm;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.entity.PasswordResetToken;
import com.englishflow.auth.entity.ActivationToken;
import com.englishflow.auth.repository.UserRepository;
import com.englishflow.auth.repository.PasswordResetTokenRepository;
import com.englishflow.auth.repository.ActivationTokenRepository;
import com.englishflow.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
        user.setActive(false); // Account inactive until email verification
        user.setRegistrationFeePaid(false);
        
        // Set CIN if provided
        if (request.getCin() != null && !request.getCin().isEmpty()) {
            user.setCin(request.getCin());
        }
        
        // Map optional fields from RegisterRequest
        if (request.getProfilePhoto() != null && !request.getProfilePhoto().isEmpty()) {
            user.setProfilePhoto(request.getProfilePhoto());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth().toString());
        }
        if (request.getAddress() != null && !request.getAddress().isEmpty()) {
            user.setAddress(request.getAddress());
        }
        if (request.getCity() != null && !request.getCity().isEmpty()) {
            user.setCity(request.getCity());
        }
        if (request.getPostalCode() != null && !request.getPostalCode().isEmpty()) {
            user.setPostalCode(request.getPostalCode());
        }
        if (request.getBio() != null && !request.getBio().isEmpty()) {
            user.setBio(request.getBio());
        }
        if (request.getEnglishLevel() != null && !request.getEnglishLevel().isEmpty()) {
            user.setEnglishLevel(request.getEnglishLevel());
        }
        if (request.getYearsOfExperience() != null) {
            user.setYearsOfExperience(request.getYearsOfExperience());
        }
        
        // Marquer le profil comme complet si phone et CIN sont fournis
        if (request.getPhone() != null && !request.getPhone().isEmpty() && 
            request.getCin() != null && !request.getCin().isEmpty()) {
            user.setProfileCompleted(true);
        } else {
            user.setProfileCompleted(false);
        }

        user = userRepository.save(user);

        // Create activation token
        String activationToken = UUID.randomUUID().toString();
        ActivationToken token = ActivationToken.builder()
                .token(activationToken)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24)) // Valid for 24 hours
                .used(false)
                .build();
        activationTokenRepository.save(token);

        // Send activation email (ne pas bloquer si ça échoue)
        try {
            emailService.sendActivationEmail(user.getEmail(), user.getFirstName(), activationToken);
        } catch (Exception e) {
            System.err.println("Failed to send activation email: " + e.getMessage());
            // Continue anyway - admin can activate manually
        }

        // Return response without JWT (user must activate first)
        return new AuthResponse(
                null, // No token until activation
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getProfilePhoto(),
                user.getPhone()
        );
    }

    @Transactional
    public AuthResponse activateAccount(String token) {
        ActivationToken activationToken = activationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid activation token"));

        if (activationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Activation token has expired");
        }

        if (activationToken.isUsed()) {
            throw new RuntimeException("Activation token has already been used");
        }

        User user = activationToken.getUser();
        user.setActive(true);
        userRepository.save(user);

        activationToken.setUsed(true);
        activationTokenRepository.save(activationToken);

        // Send welcome email
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());

        // Generate JWT token
        String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());

        return new AuthResponse(
                jwtToken,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getProfilePhoto(),
                user.getPhone(),
                user.isProfileCompleted()
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isActive()) {
            throw new RuntimeException("Account not activated. Please check your email.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getProfilePhoto(),
                user.getPhone(),
                user.isProfileCompleted()
        );
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
    public Map<String, Object> checkActivationStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("activated", user.isActive());

        if (user.isActive()) {
            // Generate JWT token for activated user
            String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
            response.put("token", jwtToken);
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("role", user.getRole().name());
            response.put("profilePhoto", user.getProfilePhoto());
            response.put("profileCompleted", user.isProfileCompleted());
            response.put("message", "Account activated successfully!");
        } else {
            response.put("message", "Waiting for account activation...");
        }

        return response;
    }


    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create new reset token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1)) // Token valid for 1 hour
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Send email
        emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), token);
    }

    @Transactional
    public void resetPassword(PasswordResetConfirm request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        if (resetToken.getUsed()) {
            throw new RuntimeException("Reset token has already been used");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
    @Transactional
    public void completeProfile(Long userId, Map<String, String> profileData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update optional fields
        if (profileData.containsKey("phone") && profileData.get("phone") != null) {
            user.setPhone(profileData.get("phone"));
        }
        if (profileData.containsKey("cin") && profileData.get("cin") != null) {
            user.setCin(profileData.get("cin"));
        }
        if (profileData.containsKey("dateOfBirth") && profileData.get("dateOfBirth") != null) {
            user.setDateOfBirth(profileData.get("dateOfBirth"));
        }
        if (profileData.containsKey("address") && profileData.get("address") != null) {
            user.setAddress(profileData.get("address"));
        }
        if (profileData.containsKey("city") && profileData.get("city") != null) {
            user.setCity(profileData.get("city"));
        }
        if (profileData.containsKey("postalCode") && profileData.get("postalCode") != null) {
            user.setPostalCode(profileData.get("postalCode"));
        }
        if (profileData.containsKey("bio") && profileData.get("bio") != null) {
            user.setBio(profileData.get("bio"));
        }
        if (profileData.containsKey("englishLevel") && profileData.get("englishLevel") != null) {
            user.setEnglishLevel(profileData.get("englishLevel"));
        }

        // Mark profile as completed
        user.setProfileCompleted(true);
        userRepository.save(user);
    }

}
