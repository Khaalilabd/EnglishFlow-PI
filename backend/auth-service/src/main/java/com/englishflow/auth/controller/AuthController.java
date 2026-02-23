package com.englishflow.auth.controller;

import com.englishflow.auth.dto.AuthResponse;
import com.englishflow.auth.dto.LoginRequest;
import com.englishflow.auth.dto.RegisterRequest;
import com.englishflow.auth.dto.PasswordResetRequest;
import com.englishflow.auth.dto.PasswordResetConfirm;
import com.englishflow.auth.service.AuthService;
import com.englishflow.auth.service.RecaptchaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RecaptchaService recaptchaService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        // Verify reCAPTCHA - COMMENTED OUT FOR DEVELOPMENT
        // if (!recaptchaService.verifyRecaptcha(request.getRecaptchaToken())) {
        //     return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        //             .body(Map.of("message", "reCAPTCHA verification failed. Please try again."));
        // }
        
        authService.register(request);
        return ResponseEntity.ok(Map.of("message", "Registration successful! Please check your email to activate your account."));
    }

    @GetMapping("/activate")
    public ResponseEntity<AuthResponse> activateAccount(@RequestParam String token) {
        return ResponseEntity.ok(authService.activateAccount(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Verify reCAPTCHA - COMMENTED OUT FOR DEVELOPMENT
        // if (!recaptchaService.verifyRecaptcha(request.getRecaptchaToken())) {
        //     return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        //             .body(null);
        // }
        
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<Map<String, String>> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        authService.requestPasswordReset(request);
        return ResponseEntity.ok(Map.of("message", "Password reset email sent"));
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Map<String, String>> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirm request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }
}
