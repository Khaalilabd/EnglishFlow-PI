package com.englishflow.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads/profile-photos}")
    private String uploadDir;

    @Value("${file.application-upload-dir:uploads/applications}")
    private String applicationUploadDir;

    // CORS est géré par l'API Gateway - pas de configuration CORS ici pour éviter les doublons

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir les photos de profil
        Path uploadPath = Paths.get(uploadDir);
        String uploadPathString = uploadPath.toFile().getAbsolutePath();
        
        registry.addResourceHandler("/uploads/profile-photos/**")
                .addResourceLocations("file:" + uploadPathString + "/")
                .addResourceLocations("file:uploads/profile-photos/");
        
        // Servir les documents de candidature
        Path applicationPath = Paths.get(applicationUploadDir);
        String applicationPathString = applicationPath.toFile().getAbsolutePath();
        
        registry.addResourceHandler("/uploads/applications/**")
                .addResourceLocations("file:" + applicationPathString + "/")
                .addResourceLocations("file:uploads/applications/");
    }
}
