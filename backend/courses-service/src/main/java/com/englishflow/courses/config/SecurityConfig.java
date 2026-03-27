package com.englishflow.courses.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(AbstractHttpConfigurer::disable) // CORS handled by API Gateway
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())  // Allow iframe embedding
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/courses/**").permitAll()
                .requestMatchers("/api/chapters/**").permitAll()
                .requestMatchers("/api/lessons/**").permitAll()
                .requestMatchers("/api/files/**").permitAll()
                .requestMatchers("/api/lesson-media/**").permitAll()
                .requestMatchers("/api/**").permitAll()
                .anyRequest().permitAll()  // Changed to permitAll for debugging
            );
        
        return http.build();
    }
}
