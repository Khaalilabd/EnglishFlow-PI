# 🚀 Configuration du Auth Service

## Prérequis
- Java 17
- PostgreSQL 14+
- Maven 3.8+

## 📝 Configuration initiale sur un nouveau PC

### 1. Clone le projet
```bash
git clone https://github.com/Khaalilabd/EnglishFlow-PI.git
cd EnglishFlow-PI/backend/auth-service
```

### 2. Crée le fichier `.env`
```bash
cp .env.example .env
```

### 3. Remplis le fichier `.env` avec les credentials

Ouvre `.env` et ajoute les valeurs suivantes (demande les vraies valeurs à l'équipe):

```env
# Gmail SMTP Configuration
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password

# Frontend URL
FRONTEND_URL=http://localhost:4200

# Google OAuth2 Configuration
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

⚠️ **Important:** Ces credentials sont partagés entre tous les membres de l'équipe. Ne les modifie pas sauf si tu crées de nouvelles applications OAuth2.

### 4. Configure PostgreSQL

Crée la base de données:
```bash
psql postgres
```

```sql
CREATE DATABASE englishflow_identity;
CREATE USER englishflow WITH PASSWORD 'englishflow123';
GRANT ALL PRIVILEGES ON DATABASE englishflow_identity TO englishflow;
\q
```

Vérifie la connexion:
```bash
psql -U englishflow -d englishflow_identity -h localhost
```

### 5. Lance le service

```bash
mvn spring-boot:run
```

Le service sera disponible sur `http://localhost:8081`

## 🔐 Sécurité

- ❌ **NE JAMAIS** commit le fichier `.env` sur Git
- ✅ Le fichier `.env` est déjà dans `.gitignore`
- ✅ Utilise `.env.example` comme template
- ✅ Partage les credentials de manière sécurisée (Slack, email chiffré, etc.)

## 📚 Documentation supplémentaire

- [Configuration Gmail SMTP](./GMAIL_SETUP.md)
- [Configuration OAuth2](./OAUTH2_SETUP.md)

## 🆘 Problèmes courants

### Erreur: "Could not connect to database"
- Vérifie que PostgreSQL est lancé: `brew services list` (macOS) ou `systemctl status postgresql` (Linux)
- Vérifie les credentials dans `.env`

### Erreur: "Failed to send email"
- Vérifie que le mot de passe Gmail est correct dans `.env`
- Vérifie que l'authentification à 2 facteurs est activée sur Gmail
- Vérifie que tu utilises un "App Password" et non ton mot de passe Gmail normal

### Erreur OAuth2: "redirect_uri_mismatch"
- Vérifie que `http://localhost:8081/login/oauth2/code/google` est bien dans les URIs autorisées sur Google Cloud Console
