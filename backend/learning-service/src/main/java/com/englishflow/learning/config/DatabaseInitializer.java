package com.englishflow.learning.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Configuration
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource(DataSourceProperties properties) {
        // Créer la base de données si elle n'existe pas
        createDatabaseIfNotExists(properties);
        
        // Créer et retourner le DataSource
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    private void createDatabaseIfNotExists(DataSourceProperties properties) {
        String datasourceUrl = properties.getUrl();
        String username = properties.getUsername();
        String password = properties.getPassword();
        
        if (datasourceUrl == null) {
            return;
        }
        
        try {
            String databaseName = extractDatabaseName(datasourceUrl);
            String postgresUrl = datasourceUrl.substring(0, datasourceUrl.lastIndexOf("/")) + "/postgres";
            
            logger.info("Vérification de l'existence de la base de données: {}", databaseName);
            
            try (Connection conn = DriverManager.getConnection(postgresUrl, username, password);
                 Statement stmt = conn.createStatement()) {
                
                String checkQuery = String.format(
                    "SELECT 1 FROM pg_database WHERE datname = '%s'", databaseName
                );
                
                ResultSet rs = stmt.executeQuery(checkQuery);
                
                if (!rs.next()) {
                    logger.warn("Base de données '{}' introuvable. Création en cours...", databaseName);
                    
                    String createQuery = String.format("CREATE DATABASE %s", databaseName);
                    stmt.executeUpdate(createQuery);
                    
                    logger.info("✅ Base de données '{}' créée avec succès!", databaseName);
                } else {
                    logger.info("✅ Base de données '{}' existe déjà", databaseName);
                }
                
                rs.close();
            }
            
        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'initialisation de la base de données: {}", e.getMessage());
        }
    }

    private String extractDatabaseName(String url) {
        int lastSlash = url.lastIndexOf("/");
        int questionMark = url.indexOf("?", lastSlash);
        
        if (questionMark > 0) {
            return url.substring(lastSlash + 1, questionMark);
        } else {
            return url.substring(lastSlash + 1);
        }
    }
}
