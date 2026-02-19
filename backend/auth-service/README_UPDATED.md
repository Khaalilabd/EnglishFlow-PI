# 🔐 Auth Service - EnglishFlow Platform

Service d'authentification et de gestion des utilisateurs pour la plateforme EnglishFlow.

## 🆕 Nouveautés (v2.0.0)

### ✅ Système d'Invitation
- Remplacement de la création manuelle de comptes Tutor/Academic
- Invitations par email avec token sécurisé
- L'utilisateur choisit son propre mot de passe
- Expiration automatique après 7 jours

### 🔒 Sécurité Renforcée
- JWT secret déplacé vers variables d'environnement
- Rate limiting sur login (5 tentatives / 15 minutes)
- Aucun mot de passe en clair dans les emails
- Protection contre brute force

### 📧 Emails Améliorés
- Template d'invitation professionnel
- Email de bienvenue après acceptation
- Support multi-langue (à venir)

---

## 🚀 Démarrage Rapide

### Prérequis

- Java 17+
- PostgreSQL 14+
- Maven 3.8+
- Serveur SMTP (Gmail recommandé)

### Installation

1. **Cloner le repository**
```bash
cd backend/auth-service
```

2. **Créer le fichier .env**
```bash
cp .env.example .env
```

3. **Configurer les variables d'environnement**
```bash
# Générer un JWT secret fort
openssl rand -base64 64

# Éditer .env
nano .env
```

Variables importantes:
```env
# Database
DB_URL=jdbc:postgresql://localhost:5432/englishflow_identity
DB_USERNAME=postgres
DB_PASSWORD=your_password

# JWT (IMPORTANT!)
JWT_SECRET=your-generated-secret-here
JWT_EXPIRATION=86400000

# Email
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# OAuth2
GOOGLE_CLIENT_ID=your-client-id
GOOGLE_CLIENT_SECRET=your-client-secret

# Frontend
FRONTEND_URL=http://localhost:4200
```

4. **Créer la base de données**
```sql
CREATE DATABASE englishflow_identity;
```

5. **Compiler et lancer**
```bash
mvn clean install
mvn spring-boot:run
```

Le service démarre sur `http://localhost:8081`

---

## 📚 Documentation

### Guides Complets

- **[OPTIMIZATIONS.md](./OPTIMIZATIONS.md)** - Détails des optimisations Phase 1
- **[INVITATION_SYSTEM_GUIDE.md](./INVITATION_SYSTEM_GUIDE.md)** - Guide complet du système d'invitation
- **[postman_collection.json](./postman_collection.json)** - Collection Postman pour tests

### Architecture

```
auth-service/
├── src/main/java/com/englishflow/auth/
│   ├── controller/
│   │   ├── AuthController.java          # Login, Register, Activation
│   │   ├── UserController.java          # Profil utilisateur
│   │   ├── AdminUserController.java     # Gestion users (admin)
│   │   └── InvitationController.java    # 🆕 Système d'invitation
│   ├── service/
│   │   ├── AuthService.java             # Logique auth + rate limiting
│   │   ├── UserService.java             # CRUD utilisateurs
│   │   ├── EmailService.java            # Envoi emails
│   │   ├── InvitationService.java       # 🆕 Gestion invitations
│   │   └── RateLimitService.java        # 🆕 Protection brute force
│   ├── entity/
│   │   ├── User.java                    # Entité utilisateur
│   │   ├── ActivationToken.java         # Tokens activation
│   │   ├── PasswordResetToken.java      # Tokens reset password
│   │   └── Invitation.java              # 🆕 Entité invitation
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── ActivationTokenRepository.java
│   │   ├── PasswordResetTokenRepository.java
│   │   └── InvitationRepository.java    # 🆕 Repository invitations
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── AuthResponse.java
│   │   ├── InvitationRequest.java       # 🆕 DTO invitation
│   │   ├── InvitationResponse.java      # 🆕 DTO réponse
│   │   └── AcceptInvitationRequest.java # 🆕 DTO acceptation
│   └── security/
│       ├── JwtUtil.java                 # Génération/validation JWT
│       ├── SecurityConfig.java          # Configuration Spring Security
│       └── OAuth2AuthenticationSuccessHandler.java
└── src/main/resources/
    ├── application.yml                  # Configuration (secrets en env vars)
    ├── templates/
    │   ├── activation-email.html
    │   ├── welcome-email.html
    │   ├── password-reset-email.html
    │   ├── account-created-email.html
    │   └── invitation-email.html        # 🆕 Template invitation
    └── db/migration/
        └── V2__add_invitations_table.sql # 🆕 Migration SQL
```

---

## 🔌 API Endpoints

### Authentification

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| POST | `/auth/register` | Inscription (STUDENT) | Public |
| POST | `/auth/login` | Connexion | Public |
| GET | `/auth/activate` | Activation compte | Public |
| POST | `/auth/password-reset/request` | Demande reset password | Public |
| POST | `/auth/password-reset/confirm` | Confirmer reset password | Public |
| GET | `/auth/validate` | Valider JWT token | Public |

### Invitations (🆕)

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| POST | `/invitations/send` | Envoyer invitation | Admin |
| GET | `/invitations/token/{token}` | Vérifier invitation | Public |
| POST | `/invitations/accept` | Accepter invitation | Public |
| GET | `/invitations` | Liste toutes invitations | Admin |
| GET | `/invitations/pending` | Invitations en attente | Admin |
| POST | `/invitations/{id}/resend` | Renvoyer invitation | Admin |
| DELETE | `/invitations/{id}` | Annuler invitation | Admin |
| POST | `/invitations/cleanup` | Nettoyer expirées | Admin |

### Gestion Utilisateurs (Admin)

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| GET | `/admin/users` | Liste tous users | Admin |
| GET | `/admin/users/role/{role}` | Users par rôle | Admin |
| GET | `/admin/users/{id}` | Détails user | Admin |
| POST | `/admin/users` | Créer user | Admin |
| PUT | `/admin/users/{id}` | Modifier user | Admin |
| DELETE | `/admin/users/{id}` | Supprimer user | Admin |
| PUT | `/admin/users/{id}/activate` | Activer user | Admin |
| PUT | `/admin/users/{id}/deactivate` | Désactiver user | Admin |

### Profil Utilisateur

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| GET | `/users/me` | Mon profil | User |
| PUT | `/users/me` | Modifier profil | User |
| POST | `/users/me/upload-photo` | Upload photo | User |

---

## 🧪 Tests

### Avec Postman

1. Importer `postman_collection.json`
2. Configurer variables:
   - `base_url`: `http://localhost:8081`
   - `admin_token`: (auto-rempli après login)
3. Exécuter les requêtes dans l'ordre

### Avec cURL

Voir [INVITATION_SYSTEM_GUIDE.md](./INVITATION_SYSTEM_GUIDE.md) section "Tests"

### Tests Unitaires

```bash
mvn test
```

---

## 🔒 Sécurité

### Rate Limiting

**Login:** 5 tentatives max / 15 minutes par email

```java
// Après 5 échecs
{
  "message": "Too many failed login attempts. Please try again in 15 minutes."
}
```

### JWT

- Algorithme: HS256
- Expiration: 24 heures (configurable)
- Claims: email, role, userId
- Secret: Variable d'environnement (minimum 256 bits)

### Passwords

- Encodage: BCrypt (strength 10)
- Minimum: 8 caractères
- Validation côté backend et frontend

### OAuth2

- Providers: Google, GitHub
- Comptes créés inactifs par défaut
- Activation manuelle par admin

---

## 📊 Base de Données

### Tables Principales

**users**
- Informations utilisateur
- Rôles: ADMIN, TUTOR, STUDENT, ACADEMIC_OFFICE_AFFAIR
- Status: active/inactive

**invitations** (🆕)
- Invitations en attente
- Token UUID unique
- Expiration 7 jours
- Tracking: invitedBy, usedAt

**activation_tokens**
- Tokens activation email
- Expiration 24 heures

**password_reset_tokens**
- Tokens reset password
- Expiration 1 heure

### Migrations

```bash
# Flyway migrations (auto-exécutées au démarrage)
src/main/resources/db/migration/
├── V1__initial_schema.sql
└── V2__add_invitations_table.sql
```

---

## 🌐 Intégration Frontend

### Flux d'Invitation

**1. Admin envoie invitation**
```typescript
// Angular service
sendInvitation(email: string, role: string) {
  return this.http.post('/invitations/send', { email, role });
}
```

**2. Page d'acceptation**
```typescript
// Route: /accept-invitation?token=xxx
acceptInvitation(data: AcceptInvitationRequest) {
  return this.http.post('/invitations/accept', data);
}
```

**3. Redirection après succès**
```typescript
// Rediriger vers login ou auto-login avec JWT retourné
this.router.navigate(['/login']);
```

Voir [INVITATION_SYSTEM_GUIDE.md](./INVITATION_SYSTEM_GUIDE.md) section "Frontend à Implémenter"

---

## 🐛 Troubleshooting

### Erreur: "JWT secret not configured"

**Solution:**
```bash
# Vérifier .env
cat .env | grep JWT_SECRET

# Générer nouveau secret
openssl rand -base64 64
```

### Erreur: "Failed to send email"

**Causes:**
- SMTP mal configuré
- App password Gmail invalide
- Firewall bloque port 587

**Solution:**
```yaml
# Vérifier application.yml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
```

### Erreur: "Too many failed login attempts"

**Solution:**
```bash
# Attendre 15 minutes
# Ou débloquer manuellement (TODO: endpoint admin)
```

---

## 📈 Monitoring

### Actuator Endpoints

```bash
# Health check
curl http://localhost:8081/actuator/health

# Info
curl http://localhost:8081/actuator/info
```

### Logs

```bash
# Logs application
tail -f logs/auth-service.log

# Logs invitations
grep "Invitation" logs/auth-service.log
```

---

## 🔄 Roadmap

### Phase 2 (En cours)
- [ ] Frontend: Page acceptation invitation
- [ ] Frontend: Interface admin invitations
- [ ] Audit trail complet
- [ ] Refresh tokens

### Phase 3 (Futur)
- [ ] 2FA (TOTP)
- [ ] Gestion de sessions
- [ ] Permissions granulaires
- [ ] SSO (SAML)
- [ ] Biométrie (WebAuthn)

---

## 🤝 Contribution

### Workflow

1. Créer une branche: `git checkout -b feature/ma-feature`
2. Commit: `git commit -m "feat: description"`
3. Push: `git push origin feature/ma-feature`
4. Créer Pull Request

### Conventions

- **Commits:** Conventional Commits (feat, fix, docs, etc.)
- **Code:** Google Java Style Guide
- **Tests:** Minimum 80% coverage

---

## 📄 License

Propriétaire - EnglishFlow Platform © 2026

---

## 📞 Support

- **Email:** support@englishflow.com
- **Docs:** [INVITATION_SYSTEM_GUIDE.md](./INVITATION_SYSTEM_GUIDE.md)
- **Issues:** GitHub Issues

---

**Version:** 2.0.0  
**Date:** 2026-02-19  
**Status:** ✅ Production Ready
