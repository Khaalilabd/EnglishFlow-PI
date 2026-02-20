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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // Endpoints publics (avec et sans préfixe /api)
                        .requestMatchers("/auth/login", "/auth/register", "/auth/activate/**", "/auth/activate-api", "/auth/forgot-password", "/auth/reset-password", "/auth/complete-profile/**", "/auth/activation-status/**").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/activate/**", "/api/auth/activate-api", "/api/auth/forgot-password", "/api/auth/reset-password", "/api/auth/complete-profile/**", "/api/auth/activation-status/**").permitAll()
                        .requestMatchers("/actuator/**", "/oauth2/**", "/login/oauth2/**", "/public/**", "/activation-pending", "/activation-success", "/activation-error", "/uploads/**").permitAll()
                        // Endpoints d'invitation
                        .requestMatchers("/auth/invitations/token/**", "/auth/invitations/accept", "/api/auth/invitations/token/**", "/api/auth/invitations/accept").permitAll()
                        .requestMatchers("/auth/invitations/**", "/api/auth/invitations/**").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                        // Endpoints admin
                        .requestMatchers("/auth/admin/**", "/api/auth/admin/**").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                        // Autres endpoints auth nécessitent authentification
                        .requestMatchers("/auth/users/**", "/auth/**", "/api/auth/users/**", "/api/auth/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable());

        return http.build();
    }
}
