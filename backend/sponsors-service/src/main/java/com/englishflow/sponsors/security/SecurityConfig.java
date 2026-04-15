package com.englishflow.sponsors.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final InternalServiceAuthenticationFilter internalServiceAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - read access
                .requestMatchers(HttpMethod.GET, "/sponsors").permitAll()
                .requestMatchers(HttpMethod.GET, "/sponsors/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/sponsors/approved").permitAll()
                
                // Actuator endpoints
                .requestMatchers("/actuator/**").permitAll()
                
                // WebSocket endpoints
                .requestMatchers("/ws/**").permitAll()
                
                // Admin-only endpoints (approval workflow)
                .requestMatchers(HttpMethod.GET, "/sponsors/pending").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                .requestMatchers(HttpMethod.POST, "/sponsors/{id}/approve").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                .requestMatchers(HttpMethod.POST, "/sponsors/{id}/reject").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                .requestMatchers(HttpMethod.POST, "/sponsors/club-requests/{id}/approve").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                .requestMatchers(HttpMethod.POST, "/sponsors/club-requests/{id}/reject").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                
                // Public endpoint - anyone can apply to be a sponsor
                .requestMatchers(HttpMethod.POST, "/sponsors").permitAll()
                
                // Write operations - require authentication
                .requestMatchers(HttpMethod.PUT, "/sponsors/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/sponsors/**").authenticated()
                
                // Club request operations - require authentication
                .requestMatchers("/sponsors/club-requests/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(internalServiceAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
