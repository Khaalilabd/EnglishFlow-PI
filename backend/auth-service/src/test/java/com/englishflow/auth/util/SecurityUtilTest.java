package com.englishflow.auth.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityUtilTest {

    private SecurityUtil securityUtil;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        securityUtil = new SecurityUtil();
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_WhenPrincipalIsLong_ShouldReturnUserId() {
        // Arrange
        Long expectedUserId = 100L;
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            expectedUserId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        Long result = securityUtil.getCurrentUserId();

        // Assert
        assertEquals(expectedUserId, result);
    }

    @Test
    void getCurrentUserId_WhenPrincipalIsString_ShouldParseAndReturnUserId() {
        // Arrange
        String userIdString = "200";
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userIdString, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        Long result = securityUtil.getCurrentUserId();

        // Assert
        assertEquals(200L, result);
    }

    @Test
    void getCurrentUserId_WhenNotAuthenticated_ShouldThrowException() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> securityUtil.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_WhenPrincipalIsInvalidString_ShouldThrowException() {
        // Arrange
        String invalidUserId = "not-a-number";
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            invalidUserId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> securityUtil.getCurrentUserId());
    }

    @Test
    void isAuthenticated_WhenAuthenticated_ShouldReturnTrue() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            100L, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        boolean result = securityUtil.isAuthenticated();

        // Assert
        assertTrue(result);
    }

    @Test
    void isAuthenticated_WhenNotAuthenticated_ShouldReturnFalse() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        boolean result = securityUtil.isAuthenticated();

        // Assert
        assertFalse(result);
    }

    @Test
    void isAuthenticated_WhenAnonymousUser_ShouldReturnFalse() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            "anonymousUser", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        boolean result = securityUtil.isAuthenticated();

        // Assert
        assertFalse(result);
    }
}
