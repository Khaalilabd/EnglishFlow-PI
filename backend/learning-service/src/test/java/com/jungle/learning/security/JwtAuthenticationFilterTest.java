package com.jungle.learning.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void testDoFilterInternal_NoAuthHeader() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/test");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).validateToken(anyString());
    }

    @Test
    void testDoFilterInternal_InvalidAuthHeader() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");
        when(request.getRequestURI()).thenReturn("/api/test");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).validateToken(anyString());
    }

    @Test
    void testDoFilterInternal_ValidToken() throws Exception {
        // Given
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractEmail(token)).thenReturn("test@example.com");
        when(jwtUtil.extractUserId(token)).thenReturn(1L);
        when(jwtUtil.extractRole(token)).thenReturn("STUDENT");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, times(1)).validateToken(token);
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws Exception {
        // Given
        String token = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(jwtUtil.extractEmail(token)).thenReturn(null);
        when(jwtUtil.extractUserId(token)).thenReturn(null);
        when(jwtUtil.extractRole(token)).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
