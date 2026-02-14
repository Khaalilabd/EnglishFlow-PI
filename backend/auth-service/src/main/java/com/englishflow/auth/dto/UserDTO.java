package com.englishflow.auth.dto;

import com.englishflow.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String cin;
    private String profilePhoto;
    private String dateOfBirth;
    private String address;
    private String city;
    private String postalCode;
    private String bio;
    private String englishLevel;
    private Integer yearsOfExperience;
    private String role;
    private boolean isActive;
    private boolean registrationFeePaid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setCin(user.getCin());
        dto.setProfilePhoto(user.getProfilePhoto());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setAddress(user.getAddress());
        dto.setCity(user.getCity());
        dto.setPostalCode(user.getPostalCode());
        dto.setBio(user.getBio());
        dto.setEnglishLevel(user.getEnglishLevel());
        dto.setYearsOfExperience(user.getYearsOfExperience());
        dto.setRole(user.getRole().name());
        dto.setActive(user.isActive());
        dto.setRegistrationFeePaid(user.isRegistrationFeePaid());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
