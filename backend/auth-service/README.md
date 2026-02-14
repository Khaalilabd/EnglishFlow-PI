# Auth Service - Jungle in English

Service d'authentification et d'autorisation pour la plateforme Jungle in English.

## 🚀 Fonctionnalités

- ✅ Authentification JWT
- ✅ OAuth2 (Google, GitHub)
- ✅ Activation de compte par email
- ✅ Réinitialisation de mot de passe
- ✅ Emails HTML professionnels
- ✅ Google reCAPTCHA v2 (protection anti-bot)
- ✅ Rôles: STUDENT, TEACHER, ADMIN
- ✅ PostgreSQL (englishflow_identity)

## 📋 Prérequis

- Java 17
- PostgreSQL 14+
- Maven 3.8+
- Compte Gmail pour SMTP

## ⚙️ Configuration

### 1. Base de données PostgreSQL

```bash
psql postgres
```

```sql
CREATE DATABASE englishflow_identity;
CREATE USER englishflow WITH PASSWORD 'englishflow123';
GRANT ALL PRIVILEGES ON DATABASE englishflow_identity TO englishflow;
\q
```

### 2. Variables d'environnement

Crée le fichier `.env` à la racine du service:

```bash
cp .env.example .env
```

Remplis avec les vraies valeurs:

```env
# Gmail SMTP
MAIL_USERNAME=jungleinenglish.platform@gmail.com
MAIL_PASSWORD=ton-app-password

# Frontend URL
FRONTEND_URL=http://localhost:4200

# Google OAuth2
GOOGLE_CLIENT_ID=ton-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=ton-client-secret

# GitHub OAuth2 (optionnel)
GITHUB_CLIENT_ID=ton-github-client-id
GITHUB_CLIENT_SECRET=ton-github-client-secret
```

## 🏃 Démarrage

### Sur votre machine de développement:

1. Configure les variables d'environnement dans ton terminal:

```bash
export JAVA_HOME=/path/to/java17
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD="your-app-password"
export GOOGLE_CLIENT_ID=your-google-client-id
export GOOGLE_CLIENT_SECRET=your-google-client-secret
export FRONTEND_URL=http://localhost:4200
```

2. Lance le service:

```bash
cd backend/auth-service
mvn spring-boot:run
```

### Sur un autre PC:

1. Clone le projet
2. Crée le fichier `.env` avec les credentials (demande-les à l'équipe)
3. Configure JAVA_HOME et exporte les variables du `.env`
4. Lance `mvn spring-boot:run`

Le service sera disponible sur `http://localhost:8081`

## 📡 Endpoints API

### Authentification

```
POST   /auth/register          - Inscription
POST   /auth/login             - Connexion
POST   /auth/validate          - Validation JWT
GET    /auth/activate          - Activation compte
POST   /auth/password-reset/request  - Demande reset password
POST   /auth/password-reset/confirm  - Confirmer reset password
```

### OAuth2

```
GET    /oauth2/authorization/google  - Login Google
GET    /oauth2/authorization/github  - Login GitHub
```

## 🔐 Configuration OAuth2

### Google OAuth2

1. Va sur https://console.cloud.google.com/
2. Crée un projet "Jungle in English"
3. Active Google+ API
4. Configure l'écran de consentement OAuth
5. Crée des credentials OAuth 2.0:
   - Type: Web application
   - Authorized redirect URIs: `http://localhost:8081/login/oauth2/code/google`
6. Copie Client ID et Client Secret dans `.env`

### GitHub OAuth2

1. Va sur https://github.com/settings/developers
2. Crée une OAuth App:
   - Homepage URL: `http://localhost:4200`
   - Authorization callback URL: `http://localhost:8081/login/oauth2/code/github`
3. Copie Client ID et Client Secret dans `.env`

## 📧 Configuration Email

Le service utilise Gmail SMTP pour envoyer les emails.

### Générer un App Password Gmail:

1. Active l'authentification à 2 facteurs sur Gmail
2. Va sur https://myaccount.google.com/apppasswords
3. Génère un mot de passe pour "Mail"
4. Copie le mot de passe dans `.env`

### Templates d'emails:

- `activation-email.html` - Email d'activation (24h)
- `password-reset-email.html` - Reset password (1h)
- `welcome-email.html` - Email de bienvenue

## 🗄️ Base de données

### Tables principales:

- `users` - Utilisateurs
- `activation_tokens` - Tokens d'activation (24h)
- `password_reset_tokens` - Tokens de reset (1h)

### Schéma User:

```java
- id (Long)
- email (String, unique)
- password (String, nullable pour OAuth2)
- firstName (String)
- lastName (String)
- phone (String)
- cin (String)
- role (STUDENT, TEACHER, ADMIN)
- isActive (Boolean)
- registrationFeePaid (Boolean)
- profilePhoto (String)
- dateOfBirth (LocalDate)
- address, city, postalCode (String)
- bio (String)
- englishLevel (String)
- yearsOfExperience (Integer)
- createdAt, updatedAt (LocalDateTime)
```

## 🔒 Sécurité

- JWT avec expiration 24h
- Passwords hashés avec BCrypt
- OAuth2 users nécessitent activation email
- CORS configuré pour localhost:4200
- Tokens d'activation/reset avec expiration

## 🧪 Tests

```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn verify
```

## 📝 Logs

Les logs sont disponibles dans `logs/auth-service.log`

## 🐛 Problèmes courants

### Erreur: "Could not connect to database"
- Vérifie que PostgreSQL est lancé
- Vérifie les credentials dans `.env`

### Erreur: "Failed to send email"
- Vérifie le mot de passe Gmail dans `.env`
- Vérifie que l'authentification à 2 facteurs est activée

### Erreur OAuth2: "redirect_uri_mismatch"
- Vérifie les URIs autorisées dans Google Cloud Console
- Doit être exactement: `http://localhost:8081/login/oauth2/code/google`

## 📚 Technologies

- Spring Boot 3.2.0
- Spring Security 6
- Spring Data JPA
- PostgreSQL
- JWT (jjwt 0.12.3)
- Spring Mail
- Thymeleaf (templates email)
- Lombok

## 🔄 Prochaines étapes

- [ ] Refresh tokens
- [ ] Remember me
- [ ] LinkedIn OAuth2
- [ ] Discord OAuth2
- [ ] Microsoft OAuth2
- [ ] Rate limiting
- [ ] Audit logs
