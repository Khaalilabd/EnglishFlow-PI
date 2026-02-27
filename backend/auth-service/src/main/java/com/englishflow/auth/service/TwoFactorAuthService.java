package com.englishflow.auth.service;

import com.englishflow.auth.dto.TwoFactorSetupResponse;
import com.englishflow.auth.dto.TwoFactorStatusResponse;
import com.englishflow.auth.entity.TwoFactorAuth;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.exception.InvalidTokenException;
import com.englishflow.auth.exception.UserNotFoundException;
import com.englishflow.auth.repository.TwoFactorAuthRepository;
import com.englishflow.auth.repository.UserRepository;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwoFactorAuthService {
    
    private final TwoFactorAuthRepository twoFactorAuthRepository;
    private final UserRepository userRepository;
    
    @Value("${app.name:EnglishFlow}")
    private String appName;
    
    private static final int BACKUP_CODES_COUNT = 10;
    private static final int BACKUP_CODE_LENGTH = 8;
    
    @Transactional
    public TwoFactorSetupResponse setupTwoFactor(Long userId) {
        log.info("Setting up 2FA for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Generate secret
        String secret = new DefaultSecretGenerator().generate();
        
        // Generate backup codes
        List<String> backupCodes = generateBackupCodes();
        
        // Create or update 2FA entity
        TwoFactorAuth twoFactorAuth = twoFactorAuthRepository.findByUserId(userId)
                .orElse(TwoFactorAuth.builder()
                        .user(user)
                        .build());
        
        twoFactorAuth.setSecret(secret);
        twoFactorAuth.setBackupCodes(backupCodes);
        twoFactorAuth.setEnabled(false); // Will be enabled after verification
        
        twoFactorAuthRepository.save(twoFactorAuth);
        
        // Generate QR code
        String qrCodeUrl = generateQrCodeUrl(user.getEmail(), secret);
        
        log.info("2FA setup completed for user: {}", userId);
        
        return TwoFactorSetupResponse.builder()
                .secret(secret)
                .qrCodeUrl(qrCodeUrl)
                .backupCodes(backupCodes)
                .message("Scan the QR code with your authenticator app and verify with a code")
                .build();
    }
    
    @Transactional
    public void enableTwoFactor(Long userId, String code) {
        log.info("Enabling 2FA for user: {}", userId);
        
        TwoFactorAuth twoFactorAuth = twoFactorAuthRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidTokenException("2FA not set up. Please setup first."));
        
        if (twoFactorAuth.getEnabled()) {
            throw new IllegalStateException("2FA is already enabled");
        }
        
        // Verify the code
        if (!verifyCode(twoFactorAuth.getSecret(), code)) {
            log.warn("Invalid 2FA code provided during enable for user: {}", userId);
            throw new InvalidTokenException("Invalid verification code");
        }
        
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setEnabledAt(LocalDateTime.now());
        twoFactorAuthRepository.save(twoFactorAuth);
        
        log.info("2FA enabled successfully for user: {}", userId);
    }
    
    @Transactional
    public void disableTwoFactor(Long userId, String code) {
        log.info("Disabling 2FA for user: {}", userId);
        
        TwoFactorAuth twoFactorAuth = twoFactorAuthRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidTokenException("2FA not found"));
        
        // Verify code or backup code
        if (!verifyCode(twoFactorAuth.getSecret(), code) && !verifyBackupCode(twoFactorAuth, code)) {
            log.warn("Invalid code provided during 2FA disable for user: {}", userId);
            throw new InvalidTokenException("Invalid verification code");
        }
        
        twoFactorAuthRepository.delete(twoFactorAuth);
        
        log.info("2FA disabled successfully for user: {}", userId);
    }
    
    @Transactional
    public boolean verifyTwoFactorCode(Long userId, String code) {
        TwoFactorAuth twoFactorAuth = twoFactorAuthRepository.findByUserId(userId)
                .orElse(null);
        
        if (twoFactorAuth == null || !twoFactorAuth.getEnabled()) {
            return false;
        }
        
        boolean isValid = verifyCode(twoFactorAuth.getSecret(), code) || 
                         verifyBackupCode(twoFactorAuth, code);
        
        if (isValid) {
            twoFactorAuth.setLastUsedAt(LocalDateTime.now());
            twoFactorAuthRepository.save(twoFactorAuth);
            
            log.info("2FA verified successfully for user: {}", userId);
        } else {
            log.warn("Invalid 2FA code attempt for user: {}", userId);
        }
        
        return isValid;
    }
    
    @Transactional(readOnly = true)
    public boolean isTwoFactorEnabled(Long userId) {
        return twoFactorAuthRepository.findByUserId(userId)
                .map(TwoFactorAuth::getEnabled)
                .orElse(false);
    }
    
    @Transactional(readOnly = true)
    public TwoFactorStatusResponse getTwoFactorStatus(Long userId) {
        TwoFactorAuth twoFactorAuth = twoFactorAuthRepository.findByUserId(userId)
                .orElse(null);
        
        if (twoFactorAuth == null) {
            return TwoFactorStatusResponse.builder()
                    .enabled(false)
                    .backupCodesRemaining(0)
                    .build();
        }
        
        return TwoFactorStatusResponse.builder()
                .enabled(twoFactorAuth.getEnabled())
                .enabledAt(twoFactorAuth.getEnabledAt())
                .lastUsedAt(twoFactorAuth.getLastUsedAt())
                .backupCodesRemaining(twoFactorAuth.getBackupCodes().size())
                .build();
    }
    
    @Transactional
    public List<String> regenerateBackupCodes(Long userId) {
        log.info("Regenerating backup codes for user: {}", userId);
        
        TwoFactorAuth twoFactorAuth = twoFactorAuthRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidTokenException("2FA not found"));
        
        List<String> newBackupCodes = generateBackupCodes();
        twoFactorAuth.setBackupCodes(newBackupCodes);
        twoFactorAuthRepository.save(twoFactorAuth);
        
        log.info("Backup codes regenerated for user: {}", userId);
        
        return newBackupCodes;
    }
    
    private boolean verifyCode(String secret, String code) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        
        // DefaultCodeVerifier with default discrepancy of 1 (allows 30 seconds before/after)
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        
        // Try with default window first
        boolean isValid = verifier.isValidCode(secret, code);
        
        if (!isValid) {
            try {
                // Try with extended time window manually (2 periods = 60 seconds before/after)
                long currentBucket = timeProvider.getTime() / 30;
                
                for (int i = -2; i <= 2; i++) {
                    String expectedCode = codeGenerator.generate(secret, currentBucket + i);
                    if (expectedCode.equals(code)) {
                        log.debug("Code verified with time offset: {} periods", i);
                        return true;
                    }
                }
                
                // Log for debugging
                String expectedCode = codeGenerator.generate(secret, currentBucket);
                log.debug("Code verification failed. Expected: {}, Received: {}, Time bucket: {}", 
                         expectedCode, code, currentBucket);
            } catch (Exception e) {
                log.error("Error during extended code verification", e);
            }
        }
        
        return isValid;
    }
    
    private boolean verifyBackupCode(TwoFactorAuth twoFactorAuth, String code) {
        List<String> backupCodes = twoFactorAuth.getBackupCodes();
        
        if (backupCodes.contains(code)) {
            // Remove used backup code
            backupCodes.remove(code);
            twoFactorAuth.setBackupCodes(backupCodes);
            twoFactorAuthRepository.save(twoFactorAuth);
            
            log.info("Backup code used for user: {}", twoFactorAuth.getUser().getId());
            return true;
        }
        
        return false;
    }
    
    private List<String> generateBackupCodes() {
        SecureRandom random = new SecureRandom();
        List<String> codes = new ArrayList<>();
        
        for (int i = 0; i < BACKUP_CODES_COUNT; i++) {
            StringBuilder code = new StringBuilder();
            for (int j = 0; j < BACKUP_CODE_LENGTH; j++) {
                code.append(random.nextInt(10));
            }
            codes.add(code.toString());
        }
        
        return codes;
    }
    
    private String generateQrCodeUrl(String email, String secret) {
        QrData data = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer(appName)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();
        
        QrGenerator generator = new ZxingPngQrGenerator();
        try {
            byte[] imageData = generator.generate(data);
            // Convert to Base64 data URI
            String base64Image = java.util.Base64.getEncoder().encodeToString(imageData);
            return "data:image/png;base64," + base64Image;
        } catch (QrGenerationException e) {
            log.error("Error generating QR code", e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
}
