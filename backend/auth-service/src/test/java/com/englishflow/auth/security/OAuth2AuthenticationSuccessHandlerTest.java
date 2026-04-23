package com.englishflow.auth.security;

import com.englishflow.auth.entity.User;
import com.englishflow.auth.enums.Role;
import com.englishflow.auth.repository.UserRepository;
import com.englishflow.auth.service.UserSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSessionService userSessionService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OAuth2AuthenticationSuccessHandler successHandler;

    private User testUser;
    private OAuth2User oauth2User;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(Role.STUDENT);
        testUser.setActive(true);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "test@example.com");
        attributes.put("given_name", "John");
        attributes.put("family_name", "Doe");
        attributes.put("picture", "http://example.com/photo.jpg");

        oauth2User = new DefaultOAuth2User(
                null,
                attributes,
                "email"
        );
    }

    @Test
    void testOnAuthenticationSuccess_ExistingUser() throws IOException {
        // Given
        when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("jwt-token");
        doNothing().when(userSessionService).createSession(anyLong(), any(HttpServletRequest.class), anyString());

        // When
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(jwtUtil, times(1)).generateToken(anyLong(), anyString());
        verify(userSessionService, times(1)).createSession(anyLong(), any(HttpServletRequest.class), anyString());
        verify(response, times(1)).sendRedirect(anyString());
    }

    @Test
    void testOnAuthenticationSuccess_NewUser() throws IOException {
        // Given
        when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("jwt-token");
        doNothing().when(userSessionService).createSession(anyLong(), any(HttpServletRequest.class), anyString());

        // When
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Then
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtUtil, times(1)).generateToken(anyLong(), anyString());
        verify(userSessionService, times(1)).createSession(anyLong(), any(HttpServletRequest.class), anyString());
        verify(response, times(1)).sendRedirect(anyString());
    }
}
