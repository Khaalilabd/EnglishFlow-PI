package com.englishflow.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Initialise automatiquement la base de données si elle n'existe pas
 * S'exécute AVANT l'initialisation de JPA
 */
@Component
public class DatabaseInitializer implements BeanFactoryPostProcessor, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String datasourceUrl = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");
        
        if (datasourceUrl == null || username == null) {
            logger.warn("Configuration de base de données non trouvée, initialisation ignorée");
            return;
        }
        
        try {
            String databaseName = extractDatabaseName(datasourceUrl);
            
            // Construire l'URL pour se connecter à la base postgres par défaut
            String postgresUrl = datasourceUrl.substring(0, datasourceUrl.lastIndexOf("/")) + "/postgres";
            
            logger.info("Vérification de l'existence de la base de données: {}", databaseName);
            
            // Se connecter à la base postgres
            try (Connection conn = DriverManager.getConnection(postgresUrl, username, password);
                 Statement stmt = conn.createStatement()) {
                
                // Vérifier si la base existe
                String checkQuery = String.format(
                    "SELECT 1 FROM pg_database WHERE datname = '%s'", databaseName
                );
                
                ResultSet rs = stmt.executeQuery(checkQuery);
                
                if (!rs.next()) {
                    // La base n'existe pas, la créer
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
            // Ne pas bloquer le démarrage, laisser Spring gérer l'erreur
        }
    }

    /**
     * Extrait le nom de la base de données de l'URL JDBC
     */
    private String extractDatabaseName(String url) {
        // Format: jdbc:postgresql://host:port/database_name
        int lastSlash = url.lastIndexOf("/");
        int questionMark = url.indexOf("?", lastSlash);
        
        if (questionMark > 0) {
            return url.substring(lastSlash + 1, questionMark);
        } else {
            return url.substring(lastSlash + 1);
        }
    }
}
