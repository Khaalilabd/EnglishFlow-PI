package com.englishflow.auth.dto;

import com.englishflow.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String image;

    public static UserDetailsResponse fromEntity(User user) {
        return new UserDetailsResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getProfilePhoto()
        );
    }
}
