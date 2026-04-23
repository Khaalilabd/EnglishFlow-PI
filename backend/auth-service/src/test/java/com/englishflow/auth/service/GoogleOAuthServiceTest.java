package com.englishflow.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class GoogleOAuthServiceTest {

    private GoogleOAuthService googleOAuthService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        googleOAuthService = new GoogleOAuthService();
    }

    @Test
    void hasValidTokens_WhenTokensDirNotExists_ShouldReturnFalse() {
        // Given
        String nonExistentPath = tempDir.resolve("non-existent").toString();
        ReflectionTestUtils.setField(googleOAuthService, "tokensDirectoryPath", nonExistentPath);

        // When
        boolean result = googleOAuthService.hasValidTokens();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void hasValidTokens_WhenTokensDirExistsButNoStoredCredential_ShouldReturnFalse() throws IOException {
        // Given
        Path tokensDir = tempDir.resolve("tokens");
        Files.createDirectories(tokensDir);
        ReflectionTestUtils.setField(googleOAuthService, "tokensDirectoryPath", tokensDir.toString());

        // When
        boolean result = googleOAuthService.hasValidTokens();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void hasValidTokens_WhenStoredCredentialExists_ShouldReturnTrue() throws IOException {
        // Given
        Path tokensDir = tempDir.resolve("tokens");
        Files.createDirectories(tokensDir);
        Path storedCredential = tokensDir.resolve("StoredCredential");
        Files.createFile(storedCredential);
        ReflectionTestUtils.setField(googleOAuthService, "tokensDirectoryPath", tokensDir.toString());

        // When
        boolean result = googleOAuthService.hasValidTokens();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void clearTokens_WhenTokensDirExists_ShouldDeleteAllFiles() throws IOException {
        // Given
        Path tokensDir = tempDir.resolve("tokens");
        Files.createDirectories(tokensDir);
        Path file1 = tokensDir.resolve("file1.txt");
        Path file2 = tokensDir.resolve("file2.txt");
        Files.createFile(file1);
        Files.createFile(file2);
        ReflectionTestUtils.setField(googleOAuthService, "tokensDirectoryPath", tokensDir.toString());

        // When
        googleOAuthService.clearTokens();

        // Then
        assertThat(tokensDir).doesNotExist();
    }

    @Test
    void clearTokens_WhenTokensDirNotExists_ShouldNotThrowException() {
        // Given
        String nonExistentPath = tempDir.resolve("non-existent").toString();
        ReflectionTestUtils.setField(googleOAuthService, "tokensDirectoryPath", nonExistentPath);

        // When & Then - should not throw exception
        googleOAuthService.clearTokens();
    }

    @Test
    void getCredentials_WhenCredentialsFileNotExists_ShouldThrowIOException() {
        // Given
        String nonExistentFile = tempDir.resolve("non-existent.json").toString();
        ReflectionTestUtils.setField(googleOAuthService, "oauthCredentialsFilePath", nonExistentFile);

        // When & Then
        assertThatThrownBy(() -> googleOAuthService.getCredentials(null))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("OAuth credentials file not found");
    }
}
