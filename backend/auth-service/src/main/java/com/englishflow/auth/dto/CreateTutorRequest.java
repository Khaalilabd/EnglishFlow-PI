package com.englishflow.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTutorRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String cin;
    private String dateOfBirth;
    private String address;
    private String city;
    private String postalCode;
    private Integer yearsOfExperience;
    private String bio;
    private String password;
    private String role;
    private String englishLevel;
}
