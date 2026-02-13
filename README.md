# Jungle in English - Plateforme d'Apprentissage de l'Anglais

Plateforme complète pour l'apprentissage de l'anglais avec architecture microservices.

## 🚀 Démarrage Rapide sur un Nouveau PC

### Prérequis
- Node.js 18+ et npm
- Java 17
- PostgreSQL 14+
- Maven 3.8+

### 1. Clone le projet
```bash
git clone https://github.com/Khaalilabd/EnglishFlow-PI.git
cd EnglishFlow-PI
```

### 2. Configure le Backend

#### a) Crée le fichier `.env`
```bash
cd backend/auth-service
cp .env.example .env
```

#### b) Remplis le fichier `.env`
Ouvre `backend/auth-service/.env` et ajoute les credentials (demande-les à l'équipe):
```env
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password
FRONTEND_URL=http://localhost:4200
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

#### c) Configure PostgreSQL
```bash
psql postgres
```
```sql
CREATE DATABASE englishflow_identity;
CREATE USER englishflow WITH PASSWORD 'englishflow123';
GRANT ALL PRIVILEGES ON DATABASE englishflow_identity TO englishflow;
\q
```

### 3. Lance les services

#### Option 1: Utilise les scripts (Recommandé)
```bash
# Depuis la racine du projet
./start-services.sh
```

#### Option 2: Lance manuellement
```bash
# Terminal 1 - Config Server
cd backend/config-server
mvn spring-boot:run

# Terminal 2 - Eureka Server
cd backend/eureka-server
mvn spring-boot:run

# Terminal 3 - Auth Service
cd backend/auth-service
mvn spring-boot:run

# Terminal 4 - API Gateway
cd backend/api-gateway
mvn spring-boot:run

# Terminal 5 - Frontend
cd frontend
npm install
npm start
```

### 4. Accède à l'application
- Frontend: http://localhost:4200
- Eureka Dashboard: http://localhost:8761
- Auth Service: http://localhost:8081

## 📁 Structure du Projet

```
EnglishFlow-PI/
├── frontend/          # Application Angular (Backoffice + Frontoffice)
│   ├── src/
│   ├── public/
│   └── README.md
└── backend/           # Microservices Spring Boot
    ├── config-server/     # Configuration centralisée
    ├── eureka-server/     # Service discovery
    ├── api-gateway/       # Point d'entrée API
    ├── auth-service/      # Authentification & autorisation
    ├── user-service/      # Gestion des utilisateurs
    ├── course-service/    # Gestion des cours
    ├── payment-service/   # Gestion des paiements
    └── notification-service/ # Notifications & emails
```

## 🎨 Frontend

Application Angular avec deux interfaces:
- **Frontoffice**: Landing page, inscription, connexion
- **Backoffice**: Dashboard admin/enseignant/étudiant

Technologies: Angular 18, TypeScript, Tailwind CSS, RxJS

## 🔧 Backend

Architecture microservices avec Spring Boot:

### Services disponibles:
- ✅ **Config Server** (8888): Configuration centralisée
- ✅ **Eureka Server** (8761): Service discovery
- ✅ **API Gateway** (8080): Routage et load balancing
- ✅ **Auth Service** (8081): JWT, OAuth2 (Google), activation email
- 🚧 **User Service**: Gestion des profils
- 🚧 **Course Service**: Gestion des cours
- 🚧 **Payment Service**: Gestion des paiements
- 🚧 **Notification Service**: Emails et notifications

Technologies: Spring Boot 3, Spring Cloud, Spring Security, PostgreSQL, JWT

## 🔐 Fonctionnalités d'Authentification

- ✅ Inscription avec validation email
- ✅ Connexion JWT
- ✅ OAuth2 Google
- ✅ Activation de compte par email
- ✅ Réinitialisation de mot de passe
- ✅ Rôles: STUDENT, TEACHER, ADMIN
- ✅ Templates d'emails professionnels

## 📚 Documentation

- [Configuration Auth Service](./backend/auth-service/SETUP.md)
- [Configuration Gmail SMTP](./backend/auth-service/GMAIL_SETUP.md)
- [Configuration OAuth2](./backend/auth-service/OAUTH2_SETUP.md)

## 🔒 Sécurité

- Les credentials sensibles sont dans `.env` (non versionné)
- Utilise `.env.example` comme template
- Ne jamais commit les secrets sur Git

## 🛠️ Scripts Utiles

```bash
# Démarrer tous les services
./start-services.sh

# Arrêter tous les services
./stop-services.sh

# Logs des services
tail -f logs/auth-service.log
tail -f logs/eureka-server.log
```

## 👥 Contribution

Ce projet est développé dans le cadre d'un projet intégré (PI).

### Branches
- `main`: Production
- `User-Authentication-Management`: Développement authentification
- `develop`: Développement général

## 📝 License

Projet académique - Tous droits réservés
