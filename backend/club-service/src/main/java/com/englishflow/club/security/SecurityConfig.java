package com.englishflow.club.security;

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
                .requestMatchers(HttpMethod.GET, "/clubs").permitAll()
                .requestMatchers(HttpMethod.GET, "/clubs/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/clubs/category/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/clubs/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/clubs/approved").permitAll()
                
                // Actuator endpoints
                .requestMatchers("/actuator/**").permitAll()
                
                // WebSocket endpoints
                .requestMatchers("/ws/**").permitAll()
                
                // Admin-only endpoints (approval workflow)
                .requestMatchers(HttpMethod.GET, "/clubs/pending").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                .requestMatchers(HttpMethod.POST, "/clubs/{id}/approve").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                .requestMatchers(HttpMethod.POST, "/clubs/{id}/reject").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                .requestMatchers(HttpMethod.POST, "/clubs/{id}/suspend").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                .requestMatchers(HttpMethod.POST, "/clubs/{id}/activate").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")
                
                // Write operations - require authentication
                .requestMatchers(HttpMethod.POST, "/clubs").authenticated()
                .requestMatchers(HttpMethod.PUT, "/clubs/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/clubs/**").authenticated()
                
                // Member operations - require authentication
                .requestMatchers("/members/**").authenticated()
                .requestMatchers("/membership-requests/**").authenticated()
                .requestMatchers("/expenses/**").authenticated()
                .requestMatchers("/tasks/**").authenticated()
                .requestMatchers("/skills/**").authenticated()
                .requestMatchers("/update-requests/**").authenticated()
                .requestMatchers("/history/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(internalServiceAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
