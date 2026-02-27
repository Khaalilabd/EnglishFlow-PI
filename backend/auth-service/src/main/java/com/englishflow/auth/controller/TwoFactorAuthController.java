package com.englishflow.auth.controller;

import com.englishflow.auth.dto.TwoFactorSetupResponse;
import com.englishflow.auth.dto.TwoFactorStatusResponse;
import com.englishflow.auth.dto.TwoFactorVerifyRequest;
import com.englishflow.auth.security.JwtUtil;
import com.englishflow.auth.service.TwoFactorAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth/2fa")
@RequiredArgsConstructor
@Tag(name = "Two-Factor Authentication", description = "2FA management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class TwoFactorAuthController {
    
    private final TwoFactorAuthService twoFactorAuthService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/setup")
    @Operation(summary = "Setup 2FA", description = "Initialize 2FA setup and get QR code")
    public ResponseEntity<TwoFactorSetupResponse> setupTwoFactor(
            @RequestHeader("Authorization") String token) {
        
        Long userId = jwtUtil.extractUserId(token.substring(7));
        TwoFactorSetupResponse response = twoFactorAuthService.setupTwoFactor(userId);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/enable")
    @Operation(summary = "Enable 2FA", description = "Enable 2FA after verifying the code")
    public ResponseEntity<Map<String, String>> enableTwoFactor(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TwoFactorVerifyRequest request) {
        
        Long userId = jwtUtil.extractUserId(token.substring(7));
        twoFactorAuthService.enableTwoFactor(userId, request.getCode());
        
        return ResponseEntity.ok(Map.of(
                "message", "2FA enabled successfully",
                "status", "enabled"
        ));
    }
    
    @PostMapping("/disable")
    @Operation(summary = "Disable 2FA", description = "Disable 2FA with verification code")
    public ResponseEntity<Map<String, String>> disableTwoFactor(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TwoFactorVerifyRequest request) {
        
        Long userId = jwtUtil.extractUserId(token.substring(7));
        twoFactorAuthService.disableTwoFactor(userId, request.getCode());
        
        return ResponseEntity.ok(Map.of(
                "message", "2FA disabled successfully",
                "status", "disabled"
        ));
    }
    
    @PostMapping("/verify")
    @Operation(summary = "Verify 2FA code", description = "Verify a 2FA code during login")
    public ResponseEntity<Map<String, Boolean>> verifyTwoFactorCode(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TwoFactorVerifyRequest request) {
        
        Long userId = jwtUtil.extractUserId(token.substring(7));
        boolean isValid = twoFactorAuthService.verifyTwoFactorCode(userId, request.getCode());
        
        return ResponseEntity.ok(Map.of(
                "valid", isValid,
                "verified", isValid
        ));
    }
    
    @GetMapping("/status")
    @Operation(summary = "Get 2FA status", description = "Get current 2FA status for user")
    public ResponseEntity<TwoFactorStatusResponse> getTwoFactorStatus(
            @RequestHeader("Authorization") String token) {
        
        Long userId = jwtUtil.extractUserId(token.substring(7));
        TwoFactorStatusResponse status = twoFactorAuthService.getTwoFactorStatus(userId);
        
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/backup-codes/regenerate")
    @Operation(summary = "Regenerate backup codes", description = "Generate new backup codes")
    public ResponseEntity<Map<String, Object>> regenerateBackupCodes(
            @RequestHeader("Authorization") String token) {
        
        Long userId = jwtUtil.extractUserId(token.substring(7));
        List<String> backupCodes = twoFactorAuthService.regenerateBackupCodes(userId);
        
        return ResponseEntity.ok(Map.of(
                "backupCodes", backupCodes,
                "message", "New backup codes generated. Save them securely!"
        ));
    }
}
