package com.englishflow.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String profilePhoto;
    private String bio;
    private String englishLevel;
    private Integer yearsOfExperience;
    private String role;
    private boolean isActive;
}