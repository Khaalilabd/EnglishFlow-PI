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

    // CORS est géré par l'API Gateway - pas besoin de configuration ici
    // pour éviter les conflits de headers "Access-Control-Allow-Origin cannot contain more than one origin"

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/login", "/auth/register", "/auth/activate/**", "/auth/forgot-password", "/auth/reset-password", "/auth/complete-profile/**", "/actuator/**", "/oauth2/**", "/login/oauth2/**", "/public/**", "/activation-pending", "/activation-success", "/activation-error", "/uploads/**").permitAll()
                        .requestMatchers("/auth/invitations/token/**", "/auth/invitations/accept").permitAll()  // Public endpoints for invitation acceptance
                        .requestMatchers("/auth/invitations/**").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")  // Admin endpoints for invitation management
                        .requestMatchers("/auth/admin/**").hasAnyRole("ADMIN", "ACADEMIC_OFFICE_AFFAIR")  // Admin endpoints
                        .requestMatchers("/auth/users/**", "/auth/**").authenticated()  // User endpoints and other auth endpoints
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
