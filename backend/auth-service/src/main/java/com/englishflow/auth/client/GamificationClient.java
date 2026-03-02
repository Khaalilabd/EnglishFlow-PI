package com.englishflow.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "gamification-service", path = "/gamification")
public interface GamificationClient {
    
    @PostMapping("/users/{userId}/initialize")
    Map<String, Object> initializeUserLevel(
            @PathVariable("userId") Long userId,
            @RequestParam("initialLevel") String initialLevel
    );
    
    @GetMapping("/users/{userId}/level")
    Map<String, Object> getUserLevel(@PathVariable("userId") Long userId);
    
    @PostMapping("/users/{userId}/xp")
    Map<String, Object> addXP(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, Object> request
    );
    
    @PostMapping("/users/{userId}/coins")
    Map<String, Object> addCoins(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, Object> request
    );
    
    @PostMapping("/users/{userId}/purchase")
    Map<String, Object> recordPurchase(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, Double> request
    );
    
    @GetMapping("/users/{userId}/badges")
    Object getUserBadges(@PathVariable("userId") Long userId);
    
    @GetMapping("/users/{userId}/badges/new")
    Object getNewBadges(@PathVariable("userId") Long userId);
}
