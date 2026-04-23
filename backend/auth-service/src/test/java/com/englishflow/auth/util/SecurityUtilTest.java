package com.englishflow.auth.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SecurityUtil
 */
class SecurityUtilTest {

    @Test
    void testGetCurrentUserId_WithAuthentication() {
        // Given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "123",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When
        Long userId = SecurityUtil.getCurrentUserId();

        // Then
        assertNotNull(userId);
        assertEquals(123L, userId);

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentUserId_WithoutAuthentication() {
        // Given
        SecurityContextHolder.clearContext();

        // When
        Long userId = SecurityUtil.getCurrentUserId();

        // Then
        assertNull(userId);
    }

    @Test
    void testGetCurrentUserId_WithInvalidFormat() {
        // Given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "invalid",
                null,
                Collections.emptyList()
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When
        Long userId = SecurityUtil.getCurrentUserId();

        // Then
        assertNull(userId);

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentUserId_WithNullPrincipal() {
        // Given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                null,
                null,
                Collections.emptyList()
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When
        Long userId = SecurityUtil.getCurrentUserId();

        // Then
        assertNull(userId);

        // Cleanup
        SecurityContextHolder.clearContext();
    }
}
