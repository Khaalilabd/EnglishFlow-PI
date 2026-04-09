# Google OAuth2 Setup Guide

## Configuration sur un Nouveau PC

### 1. Vérifier le fichier .env
```env
GOOGLE_USE_OAUTH=true
GOOGLE_OAUTH_CREDENTIALS_FILE=credentials/google-oauth-credentials.json
GOOGLE_TOKENS_DIRECTORY=tokens
```

### 2. Option A: Copier le Token (Rapide - Pour Développement)

Si vous avez déjà le token sur un autre PC:

1. **Sur le PC source** (celui qui fonctionne):
   - Copier le fichier `backend/auth-service/tokens/StoredCredential`
   - Le sauvegarder dans un dossier sécurisé (ex: Bureau/OAuth-Tokens/)

2. **Sur le nouveau PC**:
   - Créer le dossier `backend/auth-service/tokens/` s'il n'existe pas
   - Coller le fichier `StoredCredential` dedans
   - Démarrer le backend: `mvn spring-boot:run`
   - ✅ Ça marche immédiatement!

⚠️ **Important**: 
- Ne JAMAIS commit ce fichier sur Git
- Le garder dans un endroit sécurisé (USB, cloud privé, etc.)
- Partager uniquement entre vos propres PCs

### 3. Option B: Nouvelle Autorisation (Première fois)

Si vous n'avez pas le token:

1. Démarrer le backend: `mvn spring-boot:run`
2. Créer un entretien avec Google Meet
3. Une page Google s'ouvrira pour autoriser l'accès
4. Se connecter avec `jungleinenglish.platform@gmail.com`
5. Accepter les permissions
6. Le token sera sauvegardé dans `tokens/StoredCredential`
7. Sauvegarder ce fichier pour vos autres PCs!

### 4. Ajouter comme Test User (si erreur 403)

1. Aller sur https://console.cloud.google.com
2. Projet "Jungle in English"
3. APIs & Services > OAuth consent screen
4. Ajouter votre email dans "Test users"

## Structure des Fichiers

```
backend/auth-service/
├── credentials/
│   └── google-oauth-credentials.json  ✅ (dans Git)
├── tokens/
│   └── StoredCredential              ❌ (NE PAS commit - copier manuellement)
└── .env                              ❌ (NE PAS commit)
```

## Sauvegarde Recommandée

Créer un dossier sur votre Bureau:
```
Bureau/
└── EnglishFlow-Secrets/
    ├── StoredCredential              (token OAuth2)
    ├── .env                          (variables d'environnement)
    └── google-oauth-credentials.json (backup des credentials)
```

Quand vous changez de PC:
1. Copier tout le dossier `EnglishFlow-Secrets`
2. Coller les fichiers aux bons endroits
3. Démarrer le projet

## Important

- ✅ Le fichier `tokens/StoredCredential` est maintenant dans .gitignore
- ✅ Vous pouvez le copier entre vos PCs sans problème
- ❌ Ne JAMAIS le commit sur Git
- 🔄 Le token est valide jusqu'à révocation (longue durée)
