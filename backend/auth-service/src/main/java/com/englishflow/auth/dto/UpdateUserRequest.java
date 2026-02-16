package com.englishflow.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String profilePhoto;
    private String dateOfBirth;
    private String address;
    private String city;
    private String postalCode;
    private String bio;
}
