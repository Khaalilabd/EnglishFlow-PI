package com.jungle.learning.client;

import com.jungle.learning.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthServiceClientFallback implements AuthServiceClient {
    
    @Override
    public UserDTO getUserById(Long userId) {
        log.warn("Fallback: Auth service unavailable for userId: {}", userId);
        UserDTO fallback = new UserDTO();
        fallback.setId(userId);
        fallback.setFirstName("User");
        fallback.setLastName(String.valueOf(userId));
        return fallback;
    }
}
