package com.englishflow.auth.service;

import com.englishflow.auth.dto.CreateTutorRequest;
import com.englishflow.auth.dto.UserDTO;
import com.englishflow.auth.entity.ActivationToken;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.ActivationTokenRepository;
import com.englishflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ActivationTokenRepository activationTokenRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserDTO.fromEntity(user);
    }

    public List<UserDTO> getUsersByRole(String role) {
        User.Role userRole;
        try {
            userRole = User.Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + role);
        }

        return userRepository.findByRole(userRole).stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update only non-null fields
        if (userDTO.getFirstName() != null) user.setFirstName(userDTO.getFirstName());
        if (userDTO.getLastName() != null) user.setLastName(userDTO.getLastName());
        if (userDTO.getPhone() != null) user.setPhone(userDTO.getPhone());
        if (userDTO.getCin() != null) user.setCin(userDTO.getCin());
        if (userDTO.getProfilePhoto() != null) user.setProfilePhoto(userDTO.getProfilePhoto());
        if (userDTO.getDateOfBirth() != null) user.setDateOfBirth(userDTO.getDateOfBirth());
        if (userDTO.getAddress() != null) user.setAddress(userDTO.getAddress());
        if (userDTO.getCity() != null) user.setCity(userDTO.getCity());
        if (userDTO.getPostalCode() != null) user.setPostalCode(userDTO.getPostalCode());
        if (userDTO.getBio() != null) user.setBio(userDTO.getBio());
        if (userDTO.getEnglishLevel() != null) user.setEnglishLevel(userDTO.getEnglishLevel());
        if (userDTO.getYearsOfExperience() != null) user.setYearsOfExperience(userDTO.getYearsOfExperience());
        user.setActive(userDTO.isActive());
        user.setRegistrationFeePaid(userDTO.isRegistrationFeePaid());

        User updatedUser = userRepository.save(user);
        return UserDTO.fromEntity(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserDTO toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setActive(!user.isActive());
        User updatedUser = userRepository.save(user);
        return UserDTO.fromEntity(updatedUser);
    }

    @Transactional
    public UserDTO createTutor(CreateTutorRequest request) {
        try {
            // Validate request
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                throw new RuntimeException("Email is required");
            }
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                throw new RuntimeException("Password is required");
            }
            
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists: " + request.getEmail());
            }

            // Check if CIN already exists
            if (request.getCin() != null && !request.getCin().isEmpty()) {
                userRepository.findAll().stream()
                    .filter(u -> request.getCin().equals(u.getCin()))
                    .findFirst()
                    .ifPresent(u -> {
                        throw new RuntimeException("CIN already exists: " + request.getCin());
                    });
            }

            // Create new tutor user
            User tutor = new User();
            tutor.setFirstName(request.getFirstName());
            tutor.setLastName(request.getLastName());
            tutor.setEmail(request.getEmail());
            tutor.setPhone(request.getPhone());
            tutor.setCin(request.getCin());
            tutor.setDateOfBirth(request.getDateOfBirth());
            tutor.setAddress(request.getAddress());
            tutor.setCity(request.getCity());
            tutor.setPostalCode(request.getPostalCode());
            tutor.setYearsOfExperience(request.getYearsOfExperience());
            tutor.setBio(request.getBio());
            tutor.setRole(User.Role.TUTOR);
            tutor.setActive(true);
            tutor.setRegistrationFeePaid(false);
            
            // Encode password
            tutor.setPassword(passwordEncoder.encode(request.getPassword()));

            User savedTutor = userRepository.save(tutor);
            return UserDTO.fromEntity(savedTutor);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create tutor: " + e.getMessage(), e);
        }
    }

    @Transactional
    public UserDTO createUser(CreateTutorRequest request) {
        try {
            // Validate request
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                throw new RuntimeException("Email is required");
            }
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                throw new RuntimeException("Password is required");
            }
            if (request.getRole() == null || request.getRole().isEmpty()) {
                throw new RuntimeException("Role is required");
            }
            
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists: " + request.getEmail());
            }

            // Check if CIN already exists
            if (request.getCin() != null && !request.getCin().isEmpty()) {
                userRepository.findAll().stream()
                    .filter(u -> request.getCin().equals(u.getCin()))
                    .findFirst()
                    .ifPresent(u -> {
                        throw new RuntimeException("CIN already exists: " + request.getCin());
                    });
            }

            // Parse role
            User.Role userRole;
            try {
                userRole = User.Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role: " + request.getRole());
            }

            // Create new user
            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setCin(request.getCin());
            user.setDateOfBirth(request.getDateOfBirth());
            user.setAddress(request.getAddress());
            user.setCity(request.getCity());
            user.setPostalCode(request.getPostalCode());
            user.setYearsOfExperience(request.getYearsOfExperience());
            user.setBio(request.getBio());
            user.setEnglishLevel(request.getEnglishLevel());
            user.setRole(userRole);
            user.setActive(false); // Require email activation
            user.setRegistrationFeePaid(false);
            
            // Encode password
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            User savedUser = userRepository.save(user);
            
            // Create activation token
            String activationToken = UUID.randomUUID().toString();
            ActivationToken token = ActivationToken.builder()
                    .token(activationToken)
                    .user(savedUser)
                    .expiryDate(LocalDateTime.now().plusHours(24))
                    .used(false)
                    .build();
            activationTokenRepository.save(token);
            
            // Send activation email
            try {
                emailService.sendActivationEmail(savedUser.getEmail(), savedUser.getFirstName(), activationToken);
                System.out.println("✅ Activation email sent to: " + savedUser.getEmail());
            } catch (Exception e) {
                System.err.println("❌ Failed to send activation email: " + e.getMessage());
                // Continue anyway - admin can activate manually
            }
            
            return UserDTO.fromEntity(savedUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    @Transactional
    public UserDTO activateUser(Long id) {
        System.out.println("📝 UserService.activateUser called with ID: " + id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        System.out.println("👤 Found user: " + user.getEmail() + ", current status: " + user.isActive());
        
        boolean wasInactive = !user.isActive();
        user.setActive(true);
        System.out.println("✏️ Setting user to active...");
        
        User updatedUser = userRepository.save(user);
        System.out.println("💾 User saved, new status: " + updatedUser.isActive());
        
        // Send welcome email if user was previously inactive
        if (wasInactive) {
            try {
                emailService.sendWelcomeEmail(updatedUser.getEmail(), updatedUser.getFirstName());
                System.out.println("✅ Welcome email sent to: " + updatedUser.getEmail());
            } catch (Exception e) {
                System.err.println("❌ Failed to send welcome email: " + e.getMessage());
            }
        }
        
        UserDTO dto = UserDTO.fromEntity(updatedUser);
        System.out.println("📦 DTO created successfully");
        
        return dto;
    }

    @Transactional
    public UserDTO deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setActive(false);
        User updatedUser = userRepository.save(user);
        return UserDTO.fromEntity(updatedUser);
    }
}
