package com.jungle.learning.client;

import com.jungle.learning.dto.UserDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", fallback = AuthServiceClientFallback.class)
public interface AuthServiceClient {
    
    @GetMapping("/users/{userId}/public")
    @Cacheable(value = "users", key = "#userId")
    UserDTO getUserById(@PathVariable("userId") Long userId);
}
