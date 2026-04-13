package com.englishflow.complaints.client;

import com.englishflow.complaints.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthServiceClientFallback implements AuthServiceClient {
    
    @Override
    public UserDTO getUserById(Long userId) {
        log.error("Fallback: Unable to fetch user with id: {}", userId);
        return UserDTO.builder()
                .id(userId)
                .email("unavailable@system.com")
                .firstName("User")
                .lastName("Unavailable")
                .role("UNKNOWN")
                .build();
    }
}
