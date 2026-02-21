package com.englishflow.auth.security;

import com.englishflow.auth.entity.ActivationToken;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.ActivationTokenRepository;
import com.englishflow.auth.repository.UserRepository;
import com.englishflow.auth.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final com.englishflow.auth.service.UserSessionService userSessionService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        log.info("OAuth2 authentication successful for user: {}", oAuth2User.getAttributes());
        
        // Extract user info based on provider
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String registrationId = extractRegistrationId(request);
        
        UserInfo userInfo = extractUserInfo(attributes, registrationId);
        
        if (userInfo.email == null || userInfo.email.isEmpty()) {
            // Redirect to frontend with error - email required
            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/login")
                    .queryParam("error", "email_required")
                    .queryParam("provider", registrationId)
                    .build().toUriString();
            
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
            return;
        }
        
        // Find or create user
        User user = userRepository.findByEmail(userInfo.email)
                .orElseGet(() -> createOAuth2User(userInfo));
        
        // Check if user is active
        if (!user.isActive()) {
            // Pour les STUDENTS OAuth2: rediriger vers la page HTML backend avec animation
            // Cette page détecte automatiquement l'activation et redirige ensuite
            String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8081/activation-pending")
                    .queryParam("email", userInfo.email)
                    .queryParam("firstName", user.getFirstName())
                    .build().toUriString();
            
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
            return;
        }
        
        // Check if profile is not completed - redirect to complete profile
        if (!user.isProfileCompleted()) {
            // Generate temporary JWT token for profile completion
            String tempToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
            
            // Redirect to complete-profile page
            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/complete-profile")
                    .queryParam("token", tempToken)
                    .queryParam("userId", user.getId())
                    .queryParam("email", user.getEmail())
                    .queryParam("firstName", user.getFirstName())
                    .queryParam("lastName", user.getLastName())
                    .build().toUriString();
            
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
            return;
        }
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        
        // Create user session for OAuth2 login
        try {
            log.info("Attempting to create session for OAuth2 user: {} (ID: {})", user.getEmail(), user.getId());
            userSessionService.createSession(user.getId(), request);
            log.info("Session created successfully for OAuth2 user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to create session for OAuth2 user: {} - Error: {}", user.getEmail(), e.getMessage(), e);
            // Continue anyway - session tracking is not critical for login
        }
        
        // Redirect to frontend with token and role
        // Use encode() to properly encode the URL and avoid Safari issues
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/callback")
                .queryParam("token", token)
                .queryParam("id", user.getId())
                .queryParam("email", user.getEmail())
                .queryParam("firstName", user.getFirstName())
                .queryParam("lastName", user.getLastName())
                .queryParam("role", user.getRole().name())
                .queryParam("profileCompleted", user.isProfileCompleted())
                .build()
                .encode() // Encode the URL to avoid special characters issues
                .toUriString();
        
        log.info("Redirecting OAuth2 user {} to: {}", user.getEmail(), targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String extractRegistrationId(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        // Extract from URI like /login/oauth2/code/google or /login/oauth2/code/github
        String[] parts = requestUri.split("/");
        return parts[parts.length - 1];
    }

    private UserInfo extractUserInfo(Map<String, Object> attributes, String provider) {
        UserInfo info = new UserInfo();
        
        switch (provider.toLowerCase()) {
            case "google":
                info.email = (String) attributes.get("email");
                info.firstName = (String) attributes.get("given_name");
                info.lastName = (String) attributes.get("family_name");
                info.picture = (String) attributes.get("picture");
                break;
                
            case "github":
                info.email = (String) attributes.get("email");
                String name = (String) attributes.get("name");
                if (name != null && name.contains(" ")) {
                    String[] nameParts = name.split(" ", 2);
                    info.firstName = nameParts[0];
                    info.lastName = nameParts[1];
                } else {
                    info.firstName = name != null ? name : (String) attributes.get("login");
                    info.lastName = "";
                }
                info.picture = (String) attributes.get("avatar_url");
                break;
                
            default:
                log.warn("Unknown OAuth2 provider: {}", provider);
                info.email = (String) attributes.get("email");
                info.firstName = "";
                info.lastName = "";
                info.picture = null;
        }
        
        return info;
    }

    private User createOAuth2User(UserInfo userInfo) {
        User user = new User();
        user.setEmail(userInfo.email);
        user.setFirstName(userInfo.firstName != null ? userInfo.firstName : "");
        user.setLastName(userInfo.lastName != null ? userInfo.lastName : "");
        user.setProfilePhoto(userInfo.picture);
        user.setRole(User.Role.STUDENT); // Default role for OAuth2 users
        user.setActive(false); // Will be activated after profile completion
        user.setRegistrationFeePaid(false);
        user.setProfileCompleted(false); // OAuth2 users need to complete profile
        user.setPassword(""); // No password for OAuth2 users
        
        User savedUser = userRepository.save(user);
        
        // Create activation token
        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(UUID.randomUUID().toString());
        activationToken.setUser(savedUser);
        activationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        activationTokenRepository.save(activationToken);
        
        // Send activation email
        try {
            emailService.sendActivationEmail(savedUser.getEmail(), 
                                            savedUser.getFirstName(), 
                                            activationToken.getToken());
            log.info("Activation email sent to OAuth2 user: {}", userInfo.email);
        } catch (Exception e) {
            log.error("Failed to send activation email to OAuth2 user: {}", userInfo.email, e);
        }
        
        log.info("OAuth2 user created: {}", userInfo.email);
        
        return savedUser;
    }

    // Inner class to hold user info from different providers
    private static class UserInfo {
        String email;
        String firstName;
        String lastName;
        String picture;
    }
}
