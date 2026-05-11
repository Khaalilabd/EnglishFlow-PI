package com.jungle.learning.security;

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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - read access
                .requestMatchers(HttpMethod.GET, "/api/quizzes/public/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/ebooks/free/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/learning/ebooks/*/cover").permitAll()
                .requestMatchers(HttpMethod.GET, "/learning/ebooks/*/download").permitAll()
                
                // Actuator endpoints
                .requestMatchers("/actuator/**").permitAll()
                
                // Test endpoints
                .requestMatchers("/api/test/**").permitAll()
                .requestMatchers("/api/db-test/**").permitAll()
                
                // Write operations - require authentication
                .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/**").authenticated()
                
                // Admin-only endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
