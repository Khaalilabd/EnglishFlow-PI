package com.englishflow.community.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternalServiceAuthenticationFilter extends OncePerRequestFilter {

    @Value("${internal.service.key}")
    private String internalServiceKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String internalKey = request.getHeader("X-Internal-Service-Key");
        
        // Si le header est présent et valide, authentifier comme service interne
        if (internalKey != null && internalKey.equals(internalServiceKey)) {
            log.info("✅ Internal service authentication successful for request: {} {}", 
                    request.getMethod(), request.getRequestURI());
            
            // Créer une authentification pour le service interne
            // Le constructeur avec 3 paramètres marque automatiquement le token comme "authenticated"
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    "internal-service",
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("✅ Authentication set in SecurityContext with authorities: {}", 
                    authentication.getAuthorities());
        }
        
        filterChain.doFilter(request, response);
    }
}
