package com.englishflow.sponsors.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternalServiceAuthenticationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private InternalServiceAuthenticationFilter filter;

    private final String internalServiceKey = "test-internal-service-key-12345";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        ReflectionTestUtils.setField(filter, "internalServiceKey", internalServiceKey);
    }

    @Test
    void doFilterInternal_WithValidInternalKey_ShouldSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-Internal-Service-Key")).thenReturn(internalServiceKey);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/sponsors");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo("internal-service");
        assertThat(authentication.getAuthorities()).hasSize(1);
        assertThat(authentication.getAuthorities().iterator().next().getAuthority())
                .isEqualTo("ROLE_INTERNAL_SERVICE");
    }

    @Test
    void doFilterInternal_WithInvalidInternalKey_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-Internal-Service-Key")).thenReturn("invalid-key");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();
    }

    @Test
    void doFilterInternal_WithNoInternalKey_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-Internal-Service-Key")).thenReturn(null);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();
    }

    @Test
    void doFilterInternal_WithEmptyInternalKey_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-Internal-Service-Key")).thenReturn("");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();
    }

    @Test
    void doFilterInternal_WithValidKey_ShouldLogRequestDetails() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("X-Internal-Service-Key")).thenReturn(internalServiceKey);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/sponsors/create");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(request).getMethod();
        verify(request).getRequestURI();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_AlwaysCallsFilterChain() throws ServletException, IOException {
        // Arrange - test with valid key
        when(request.getHeader("X-Internal-Service-Key")).thenReturn(internalServiceKey);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        
        // Reset and test with invalid key
        SecurityContextHolder.clearContext();
        reset(filterChain);
        when(request.getHeader("X-Internal-Service-Key")).thenReturn("wrong-key");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }
}
