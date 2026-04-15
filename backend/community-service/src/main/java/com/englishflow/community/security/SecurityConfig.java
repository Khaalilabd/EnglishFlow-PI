package com.englishflow.community.security;

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
                .requestMatchers(HttpMethod.GET, "/community/posts/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/community/topics/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/community/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/community/search/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/community/trending/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/community/permissions/**").permitAll()
                
                // Public file serving - uploaded images, PDFs, videos
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                
                // Actuator endpoints
                .requestMatchers("/actuator/**").permitAll()
                
                // Write operations - require authentication (any authenticated user can write)
                .requestMatchers(HttpMethod.POST, "/community/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/community/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/community/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/community/**").authenticated()
                
                // Admin-only endpoints
                .requestMatchers("/community/admin/**").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(internalServiceAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
