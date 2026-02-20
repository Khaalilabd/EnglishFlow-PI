package com.englishflow.auth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Custom health checks for auth-service
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class HealthCheckConfig {

    private final DataSource dataSource;
    private final JavaMailSender mailSender;

    /**
     * Database health check
     */
    @Bean
    public HealthIndicator databaseHealthIndicator() {
        return () -> {
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(2)) {
                    return Health.up()
                            .withDetail("database", "PostgreSQL")
                            .withDetail("status", "Connected")
                            .build();
                } else {
                    return Health.down()
                            .withDetail("database", "PostgreSQL")
                            .withDetail("status", "Connection invalid")
                            .build();
                }
            } catch (Exception e) {
                log.error("Database health check failed", e);
                return Health.down()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }

    /**
     * Email service health check
     */
    @Bean
    public HealthIndicator emailHealthIndicator() {
        return () -> {
            try {
                // Test mail sender configuration
                mailSender.createMimeMessage();
                return Health.up()
                        .withDetail("email", "SMTP")
                        .withDetail("status", "Configured")
                        .build();
            } catch (Exception e) {
                log.error("Email health check failed", e);
                return Health.down()
                        .withDetail("email", "SMTP")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }

    /**
     * Application health check
     */
    @Bean
    public HealthIndicator applicationHealthIndicator() {
        return () -> {
            // Check memory usage
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            double memoryUsagePercent = (double) usedMemory / maxMemory * 100;

            Health.Builder builder = Health.up()
                    .withDetail("application", "auth-service")
                    .withDetail("memory.max", formatBytes(maxMemory))
                    .withDetail("memory.total", formatBytes(totalMemory))
                    .withDetail("memory.used", formatBytes(usedMemory))
                    .withDetail("memory.free", formatBytes(freeMemory))
                    .withDetail("memory.usage", String.format("%.2f%%", memoryUsagePercent));

            // Warn if memory usage is high
            if (memoryUsagePercent > 90) {
                builder.down().withDetail("warning", "High memory usage");
            } else if (memoryUsagePercent > 75) {
                builder.withDetail("warning", "Memory usage above 75%");
            }

            return builder.build();
        };
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
