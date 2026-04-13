package com.jungle.learning.service;

import com.jungle.learning.client.AuthServiceClient;
import com.jungle.learning.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final AuthServiceClient authServiceClient;

    public UserDTO getUserById(Long userId) {
        log.debug("Fetching user info via Feign for userId: {}", userId);
        return authServiceClient.getUserById(userId);
    }

    public String getUserName(Long userId) {
        UserDTO user = getUserById(userId);
        return user != null ? user.getFullName() : "User " + userId;
    }
}
