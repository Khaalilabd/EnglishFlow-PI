package com.englishflow.courses.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads/courses}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get absolute path
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadLocation = "file:" + uploadPath.toString() + "/";
        
        // Serve uploaded files at /uploads/courses/**
        registry.addResourceHandler("/uploads/courses/**")
                .addResourceLocations(uploadLocation);
                
        System.out.println("📁 Serving files from: " + uploadLocation);
        System.out.println("📁 Accessible at: /uploads/courses/**");
    }
}
