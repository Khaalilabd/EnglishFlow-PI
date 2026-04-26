package com.englishflow.auth.security;

import com.englishflow.auth.entity.ActivationToken;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.ActivationTokenRepository;
import com.englishflow.auth.repository.UserRepository;
import com.englishflow.auth.service.EmailService;
import com.englishflow.auth.service.TwoFactorAuthService;
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
import org.springframework.security.web.RedirectStrategy;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivationTokenRepository activationTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserSessionService userSessionService;

    @Mock
    private TwoFactorAuthService twoFactorAuthService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private RedirectStrategy redirectStrategy;

    @InjectMocks
    private OAuth2AuthenticationSuccessHandler handler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(handler, "frontendUrl", "http://localhost:3000");
        ReflectionTestUtils.setField(handler, "redirectStrategy", redirectStrategy);
    }

    @Test
    void onAuthenticationSuccess_WithGoogleProvider_ShouldRedirectToCallback() throws Exception {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "test@example.com");
        attributes.put("given_name", "John");
        attributes.put("family_name", "Doe");
        attributes.put("picture", "http://example.com/photo.jpg");

        OAuth2User oAuth2User = new DefaultOAuth2User(
                java.util.Collections.emptyList(),
                attributes,
                "email"
        );

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(request.getRequestURI()).thenReturn("/login/oauth2/code/google");

        User user = createActiveUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("jwt-token");
        when(twoFactorAuthService.isTwoFactorEnabled(anyLong())).thenReturn(false);

        // Act
        handler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(userSessionService).createSession(eq(user.getId()), eq(request));
        verify(redirectStrategy).sendRedirect(eq(request), eq(response), contains("oauth2/callback"));
        verify(redirectStrategy).sendRedirect(eq(request), eq(response), contains("token=jwt-token"));
    }

    @Test
    void onAuthenticationSuccess_WithGithubProvider_ShouldExtractUserInfo() throws Exception {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "github@example.com");
        attributes.put("name", "Jane Smith");
        attributes.put("avatar_url", "http://example.com/avatar.jpg");

        OAuth2User oAuth2User = new DefaultOAuth2User(
                java.util.Collections.emptyList(),
                attributes,
                "email"
        );

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(request.getRequestURI()).thenReturn("/login/oauth2/code/github");

        User user = createActiveUser();
        user.setEmail("github@example.com");
        when(userRepository.findByEmail("github@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("jwt-token");
        when(twoFactorAuthService.isTwoFactorEnabled(anyLong())).thenReturn(false);

        // Act
        handler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(userRepository).findByEmail("github@example.com");
        verify(redirectStrategy).sendRedirect(eq(request), eq(response), contains("oauth2/callback"));
    }

    @Test
    void onAuthenticationSuccess_WithNoEmail_ShouldRedirectToError() throws Exception {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "12345"); // Add a required attribute for DefaultOAuth2User
        // No email attribute

        OAuth2User oAuth2User = new DefaultOAuth2User(
                java.util.Collections.emptyList(),
                attributes,
                "sub"
        );

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(request.getRequestURI()).thenReturn("/login/oauth2/code/google");

        // Act
        handler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(redirectStrategy).sendRedirect(eq(request), eq(response), contains("error=email_required"));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void onAuthenticationSuccess_WithInactiveUser_ShouldRedirectToActivationPending() throws Exception {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "inactive@example.com");
        attributes.put("given_name", "Inactive");
        attributes.put("family_name", "User");

        OAuth2User oAuth2User = new DefaultOAuth2User(
                java.util.Collections.emptyList(),
                attributes,
                "email"
        );

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(request.getRequestURI()).thenReturn("/login/oauth2/code/google");

        User user = createInactiveUser();
        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(user));

        // Act
        handler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(redirectStrategy).sendRedirect(eq(request), eq(response), contains("activation-pending"));
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyLong());
    }

    @Test
    void onAuthenticationSuccess_WithIncompleteProfile_ShouldRedirectToCompleteProfile() throws Exception {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "incomplete@example.com");
        attributes.put("given_name", "Incomplete");
        attributes.put("family_name", "User");

        OAuth2User oAuth2User = new DefaultOAuth2User(
                java.util.Collections.emptyList(),
                attributes,
                "email"
        );

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(request.getRequestURI()).thenReturn("/login/oauth2/code/google");

        User user = createActiveUser();
        user.setEmail("incomplete@example.com");
        user.setProfileCompleted(false);
        when(userRepository.findByEmail("incomplete@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("temp-token");

        // Act
        handler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(redirectStrategy).sendRedirect(eq(request), eq(response), contains("complete-profile"));
        verify(redirectStrategy).sendRedirect(eq(request), eq(response), contains("token=temp-token"));
    }

    @Test
    void onAuthenticationSuccess_With2FAEnabled_ShouldRedirectTo2FAVerification() throws Exception {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "2fa@example.com");
        attributes.put("given_name", "TwoFA");
        attributes.put("family_name", "User");

        OAuth2User oAuth2User = new DefaultOAuth2User(
                java.util.Collections.emptyList(),
                attributes,
                "email"
        );

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(request.getRequestURI()).thenReturn("/login/oauth2/code/google");

        User user = createActiveUser();
        user.setEmail("2fa@example.com");
        when(userRepository.findByEmail("2fa@example.com")).thenReturn(Optional.of(user));
        when(twoFactorAuthService.isTwoFactorEnabled(user.getId())).thenReturn(true);
        when(jwtUtil.generateTempToken(anyString(), anyLong())).thenReturn("temp-token");

        // Act
        handler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(twoFactorAuthService).isTwoFactorEnabled(user.getId());
        verify(jwtUtil).generateTempToken("2fa@example.com", user.getId());
        verify(redirectStrategy).sendRedirect(eq(request), eq(response), contains("verify-2fa"));
        verify(redirectStrategy).sendRedirect(eq(request), eq(response), contains("tempToken=temp-token"));
    }

    @Test
    void onAuthenticationSuccess_WithNewUser_ShouldCreateUserAndSendActivationEmail() throws Exception {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "newuser@example.com");
        attributes.put("given_name", "New");
        attributes.put("family_name", "User");
        attributes.put("picture", "http://example.com/photo.jpg");

        OAuth2User oAuth2User = new DefaultOAuth2User(
                java.util.Collections.emptyList(),
                attributes,
                "email"
        );

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(request.getRequestURI()).thenReturn("/login/oauth2/code/google");
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());

        User newUser = new User();
        newUser.setId(3L);
        newUser.setEmail("newuser@example.com");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setActive(false);
        newUser.setProfileCompleted(false);
        newUser.setRole(User.Role.STUDENT);
        
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(activationTokenRepository.save(any(ActivationToken.class))).thenReturn(new ActivationToken());

        // Act
        handler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(userRepository).save(any(User.class));
        verify(activationTokenRepository).save(any(ActivationToken.class));
        verify(emailService).sendActivationEmail(eq("newuser@example.com"), anyString(), anyString());
        verify(redirectStrategy).sendRedirect(eq(request), eq(response), contains("activation-pending"));
    }

    @Test
    void onAuthenticationSuccess_WithSessionCreationFailure_ShouldContinueAnyway() throws Exception {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "test@example.com");
        attributes.put("given_name", "John");
        attributes.put("family_name", "Doe");

        OAuth2User oAuth2User = new DefaultOAuth2User(
                java.util.Collections.emptyList(),
                attributes,
                "email"
        );

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(request.getRequestURI()).thenReturn("/login/oauth2/code/google");

        User user = createActiveUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("jwt-token");
        when(twoFactorAuthService.isTwoFactorEnabled(anyLong())).thenReturn(false);
        doThrow(new RuntimeException("Session creation failed")).when(userSessionService).createSession(anyLong(), any());

        // Act
        handler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(redirectStrategy).sendRedirect(eq(request), eq(response), contains("oauth2/callback"));
    }

    private User createActiveUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);
        user.setProfileCompleted(true);
        user.setRole(User.Role.STUDENT);
        return user;
    }

    private User createInactiveUser() {
        User user = new User();
        user.setId(2L);
        user.setEmail("inactive@example.com");
        user.setFirstName("Inactive");
        user.setLastName("User");
        user.setActive(false);
        user.setProfileCompleted(false);
        user.setRole(User.Role.STUDENT);
        return user;
    }
}
