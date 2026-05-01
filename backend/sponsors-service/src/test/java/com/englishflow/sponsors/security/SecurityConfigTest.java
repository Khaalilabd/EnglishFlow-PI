package com.englishflow.sponsors.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private InternalServiceAuthenticationFilter internalServiceAuthenticationFilter;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(jwtAuthenticationFilter, internalServiceAuthenticationFilter);
    }

    @Test
    void securityConfig_ShouldBeInstantiable() {
        // Assert
        assertNotNull(securityConfig);
    }

    @Test
    void securityConfig_ShouldHaveJwtAuthenticationFilter() {
        // Assert
        assertNotNull(jwtAuthenticationFilter);
    }

    @Test
    void securityConfig_ShouldHaveInternalServiceAuthenticationFilter() {
        // Assert
        assertNotNull(internalServiceAuthenticationFilter);
    }

    @Test
    void securityConfig_ShouldAcceptBothFilters() {
        // Arrange & Act
        SecurityConfig config = new SecurityConfig(jwtAuthenticationFilter, internalServiceAuthenticationFilter);

        // Assert
        assertNotNull(config);
    }
}
