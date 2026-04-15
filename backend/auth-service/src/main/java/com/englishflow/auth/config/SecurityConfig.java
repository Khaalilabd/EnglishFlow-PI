package com.englishflow.auth.config;

import com.englishflow.auth.security.JwtAuthenticationFilter;
import com.englishflow.auth.security.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS est géré par l'API Gateway - désactivé ici pour éviter les doublons
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                // Désactiver X-Frame-Options complètement pour permettre l'affichage dans iframe
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // IMPORTANT: Ajouter le filtre JWT AVANT tout
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // Swagger/OpenAPI endpoints (public access)
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        // Endpoints de recrutement publics (DOIT ÊTRE EN PREMIER pour éviter les conflits)
                        .requestMatchers("/auth/recruitment/apply/**", "/api/auth/recruitment/apply/**").permitAll()
                        // Fichiers uploadés (photos de profil ET documents de candidature)
                        .requestMatchers("/uploads/**").permitAll()
                        // Endpoints publics (avec et sans préfixe /api)
                        .requestMatchers("/auth/login", "/auth/register", "/auth/register-sponsor", "/auth/activate/**", "/auth/activate-api", "/auth/forgot-password", "/auth/reset-password", "/auth/complete-profile/**", "/auth/activation-status/**", "/auth/login/verify-2fa").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/register-sponsor", "/api/auth/activate/**", "/api/auth/activate-api", "/api/auth/forgot-password", "/api/auth/reset-password", "/api/auth/complete-profile/**", "/api/auth/activation-status/**", "/api/auth/login/verify-2fa").permitAll()
                        .requestMatchers("/actuator/**", "/oauth2/**", "/login/oauth2/**", "/public/**", "/activation-pending", "/activation-success", "/activation-error").permitAll()
                        // Endpoint public pour récupérer les infos utilisateur (pour inter-service communication)
                        .requestMatchers("/auth/users/*/public", "/api/auth/users/*/public", "/users/*/public").permitAll()
                        // Endpoint public pour la liste des tuteurs (page d'accueil)
                        .requestMatchers("/auth/users/public/tutors", "/api/auth/users/public/tutors", "/users/public/tutors").permitAll()
                        // Endpoints pour communication inter-service (Feign clients)
                        .requestMatchers("/api/users/**").permitAll()
                        // Endpoints email pour communication inter-service
                        .requestMatchers("/api/email/**").permitAll()
                        // Endpoints password reset
                        .requestMatchers("/auth/password-reset/**", "/api/auth/password-reset/**").permitAll()
                        // Endpoints d'invitation
                        .requestMatchers("/auth/invitations/token/**", "/auth/invitations/accept", "/api/auth/invitations/token/**", "/api/auth/invitations/accept").permitAll()
                        .requestMatchers("/auth/invitations/**", "/api/auth/invitations/**").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                        // Endpoint pour que les tuteurs accèdent à leur propre application
                        .requestMatchers("/auth/recruitment/my-application", "/api/auth/recruitment/my-application").authenticated()
                        // Endpoints de recrutement admin
                        .requestMatchers("/auth/recruitment/**", "/api/auth/recruitment/**").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                        // Endpoints admin
                        .requestMatchers("/auth/admin/**", "/api/auth/admin/**").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                        // Endpoints 2FA (nécessitent authentification)
                        .requestMatchers("/auth/2fa/**", "/api/auth/2fa/**").authenticated()
                        // Endpoints sessions
                        .requestMatchers("/sessions/**", "/api/sessions/**").authenticated()
                        // Endpoints users
                        .requestMatchers("/users/**").authenticated()
                        // Autres endpoints auth nécessitent authentification
                        .requestMatchers("/auth/users/**", "/auth/**", "/api/auth/users/**", "/api/auth/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .formLogin(form -> form.disable());

        return http.build();
    }
}
