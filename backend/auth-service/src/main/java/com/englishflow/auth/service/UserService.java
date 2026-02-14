package com.englishflow.auth.service;

import com.englishflow.auth.dto.CreateTutorRequest;
import com.englishflow.auth.dto.UserDTO;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
    }
}
