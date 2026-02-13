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

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        log.info("OAuth2 authentication successful for user: {}", oAuth2User.getAttributes());
        
        // Extract user info from OAuth2User
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String firstName = (String) attributes.get("given_name");
        String lastName = (String) attributes.get("family_name");
        String picture = (String) attributes.get("picture");
        
        // Find or create user
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createOAuth2User(email, firstName, lastName, picture));
        
        // Check if user is active
        if (!user.getIsActive()) {
            // Redirect to frontend with error - account not activated
            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/login")
                    .queryParam("error", "account_not_activated")
                    .queryParam("email", email)
                    .build().toUriString();
            
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
            return;
        }
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        
        // Redirect to frontend with token and role
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/callback")
                .queryParam("token", token)
                .queryParam("email", user.getEmail())
                .queryParam("firstName", user.getFirstName())
                .queryParam("lastName", user.getLastName())
                .queryParam("role", user.getRole().name())
                .build().toUriString();
        
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private User createOAuth2User(String email, String firstName, String lastName, String picture) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName != null ? firstName : "");
        user.setLastName(lastName != null ? lastName : "");
        user.setProfilePhoto(picture);
        user.setRole(User.Role.STUDENT); // Default role for OAuth2 users
        user.setIsActive(false); // OAuth2 users need activation
        user.setRegistrationFeePaid(false);
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
            log.info("Activation email sent to OAuth2 user: {}", email);
        } catch (Exception e) {
            log.error("Failed to send activation email to OAuth2 user: {}", email, e);
        }
        
        return savedUser;
    }
}
