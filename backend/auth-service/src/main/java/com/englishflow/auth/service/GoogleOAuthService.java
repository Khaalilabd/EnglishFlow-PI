package com.englishflow.auth.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class GoogleOAuthService {

    private static final String APPLICATION_NAME = "EnglishFlow Recruitment";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/calendar",
            "https://www.googleapis.com/auth/calendar.events"
    );

    @Value("${meeting.google.oauth-credentials-file:credentials/google-oauth-credentials.json}")
    private String oauthCredentialsFilePath;

    @Value("${meeting.google.tokens-directory:tokens}")
    private String tokensDirectoryPath;

    /**
     * Obtient les credentials OAuth2 pour l'utilisateur
     * Si les tokens n'existent pas, lance le flux d'autorisation
     */
    public Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Charger les secrets client
        File credentialsFile = new File(oauthCredentialsFilePath);
        if (!credentialsFile.exists()) {
            log.error("OAuth credentials file not found: {}", oauthCredentialsFilePath);
            throw new IOException("OAuth credentials file not found. Please create: " + oauthCredentialsFilePath);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY,
                new InputStreamReader(new FileInputStream(credentialsFile))
        );

        // Créer le flux d'autorisation
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(tokensDirectoryPath)))
                .setAccessType("offline")
                .build();

        // Vérifier si les tokens existent déjà
        Credential credential = flow.loadCredential("user");
        if (credential != null && credential.getRefreshToken() != null) {
            log.info("✅ Using existing OAuth2 tokens from: {}", tokensDirectoryPath);
            return credential;
        }

        // Si pas de tokens, lancer le flux d'autorisation
        log.warn("⚠️ No valid tokens found. Starting OAuth2 authorization flow...");
        
        // Créer le receiver local pour recevoir le code d'autorisation
        // Utiliser le port 15000 (port libre vérifié - loin des microservices)
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(15000).build();

        // Autoriser et obtenir les credentials
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Vérifie si les tokens OAuth2 existent
     */
    public boolean hasValidTokens() {
        File tokensDir = new File(tokensDirectoryPath);
        if (!tokensDir.exists()) {
            return false;
        }

        File storedCredential = new File(tokensDir, "StoredCredential");
        return storedCredential.exists();
    }

    /**
     * Supprime les tokens OAuth2 stockés (pour forcer une nouvelle autorisation)
     */
    public void clearTokens() {
        File tokensDir = new File(tokensDirectoryPath);
        if (tokensDir.exists()) {
            File[] files = tokensDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            tokensDir.delete();
            log.info("OAuth tokens cleared successfully");
        }
    }
}
