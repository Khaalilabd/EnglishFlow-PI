# EnglishFlow Auth Service - API Documentation

## 📚 Table des Matières

1. [Vue d'ensemble](#vue-densemble)
2. [Accès à la documentation](#accès-à-la-documentation)
3. [Authentification](#authentification)
4. [Endpoints principaux](#endpoints-principaux)
5. [Codes d'erreur](#codes-derreur)
6. [Exemples d'utilisation](#exemples-dutilisation)

---

## 🎯 Vue d'ensemble

L'Auth Service est le service d'authentification et d'autorisation centralisé pour la plateforme EnglishFlow.

### Fonctionnalités

- ✅ Authentification Email/Password
- ✅ OAuth2 (Google)
- ✅ JWT avec refresh tokens
- ✅ Vérification email
- ✅ Réinitialisation mot de passe
- ✅ Gestion des sessions
- ✅ Audit logging
- ✅ Rate limiting
- ✅ Système d'invitations

### Technologies

- Spring Boot 3.2.0
- Spring Security 6
- JWT (jjwt 0.11.5)
- PostgreSQL
- Swagger/OpenAPI 3.0

---

## 📖 Accès à la documentation

### Swagger UI (Interface Interactive)

```
http://localhost:8081/swagger-ui.html
```

### OpenAPI JSON

```
http://localhost:8081/api-docs
```

### Via API Gateway

```
http://localhost:8080/api/swagger-ui.html
```

---

## 🔐 Authentification

### Obtenir un token JWT

**Endpoint:** `POST /auth/login`

**Request:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "recaptchaToken": "your-recaptcha-token"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "expiresIn": 900,
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "STUDENT"
  }
}
```

### Utiliser le token

Incluez le token dans le header Authorization:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

### Rafraîchir le token

**Endpoint:** `POST /auth/refresh-token`

**Request:**
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

## 🛣️ Endpoints principaux

### Authentification publique

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/auth/register` | Inscription utilisateur |
| POST | `/auth/login` | Connexion |
| GET | `/auth/activate` | Activation compte (email) |
| POST | `/auth/forgot-password` | Demande reset password |
| POST | `/auth/reset-password` | Reset password |
| POST | `/auth/refresh-token` | Rafraîchir JWT |

### Gestion utilisateur (Authentifié)

| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| GET | `/auth/users/me` | Profil utilisateur | Tous |
| PUT | `/auth/users/me` | Modifier profil | Tous |
| POST | `/auth/users/me/change-password` | Changer password | Tous |
| POST | `/auth/users/me/upload-photo` | Upload photo | Tous |

### Administration (Admin/Staff)

| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| GET | `/auth/admin/users` | Liste utilisateurs | ADMIN, ACADEMIC_OFFICE_AFFAIR |
| GET | `/auth/admin/users/{id}` | Détails utilisateur | ADMIN, ACADEMIC_OFFICE_AFFAIR |
| PUT | `/auth/admin/users/{id}` | Modifier utilisateur | ADMIN |
| DELETE | `/auth/admin/users/{id}` | Supprimer utilisateur | ADMIN |
| POST | `/auth/admin/users/{id}/activate` | Activer compte | ADMIN |
| POST | `/auth/admin/users/{id}/deactivate` | Désactiver compte | ADMIN |

### Invitations (Admin/Staff)

| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| POST | `/auth/invitations` | Envoyer invitation | ADMIN, ACADEMIC_OFFICE_AFFAIR |
| GET | `/auth/invitations` | Liste invitations | ADMIN, ACADEMIC_OFFICE_AFFAIR |
| GET | `/auth/invitations/token/{token}` | Vérifier invitation | Public |
| POST | `/auth/invitations/accept` | Accepter invitation | Public |
| DELETE | `/auth/invitations/{id}` | Annuler invitation | ADMIN, ACADEMIC_OFFICE_AFFAIR |

### Sessions

| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| GET | `/sessions/my-sessions` | Mes sessions actives | Tous |
| DELETE | `/sessions/my-sessions/{id}` | Terminer session | Tous |
| DELETE | `/sessions/my-sessions/others` | Terminer autres sessions | Tous |
| POST | `/sessions/admin/search` | Rechercher sessions | ADMIN |
| DELETE | `/sessions/admin/{id}` | Terminer session (admin) | ADMIN |
| GET | `/sessions/admin/statistics` | Statistiques sessions | ADMIN |

### Audit Logs (Admin)

| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| POST | `/audit/search` | Rechercher logs | ADMIN |
| GET | `/audit/user/{userId}` | Logs utilisateur | ADMIN |
| GET | `/audit/statistics` | Statistiques audit | ADMIN |
| GET | `/audit/security-events` | Événements sécurité | ADMIN |

---

## ⚠️ Codes d'erreur

### HTTP Status Codes

| Code | Signification | Description |
|------|---------------|-------------|
| 200 | OK | Requête réussie |
| 201 | Created | Ressource créée |
| 400 | Bad Request | Données invalides |
| 401 | Unauthorized | Non authentifié |
| 403 | Forbidden | Accès refusé |
| 404 | Not Found | Ressource introuvable |
| 409 | Conflict | Conflit (email existe déjà) |
| 410 | Gone | Ressource expirée (invitation) |
| 429 | Too Many Requests | Rate limit dépassé |
| 500 | Internal Server Error | Erreur serveur |

### Format de réponse d'erreur

```json
{
  "timestamp": "2024-02-20T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already exists: user@example.com",
  "path": "/auth/register",
  "validationErrors": [
    {
      "field": "email",
      "message": "Email already in use",
      "rejectedValue": "user@example.com"
    }
  ]
}
```

### Erreurs spécifiques

| Erreur | Code | Message |
|--------|------|---------|
| UserNotFoundException | 404 | User not found with ID: {id} |
| InvalidTokenException | 401 | Invalid JWT token |
| TokenExpiredException | 401 | Token has expired |
| AccountNotActivatedException | 403 | Account not activated |
| RateLimitExceededException | 429 | Rate limit exceeded |
| InvitationExpiredException | 410 | Invitation has expired |
| EmailAlreadyExistsException | 409 | Email already exists |
| InvalidCredentialsException | 401 | Invalid email or password |

---

## 💡 Exemples d'utilisation

### 1. Inscription complète

```bash
# 1. S'inscrire
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@example.com",
    "password": "SecurePass123!",
    "confirmPassword": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+33612345678",
    "cin": "AB123456",
    "dateOfBirth": "2000-01-15",
    "role": "STUDENT",
    "englishLevel": "Intermediate",
    "recaptchaToken": "test-token"
  }'

# 2. Activer le compte (cliquer sur le lien dans l'email)
# GET http://localhost:8081/auth/activate?token=<activation-token>

# 3. Se connecter
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@example.com",
    "password": "SecurePass123!",
    "recaptchaToken": "test-token"
  }'
```

### 2. Inviter un tuteur

```bash
# 1. Admin envoie une invitation
curl -X POST http://localhost:8081/auth/invitations \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "tutor@example.com",
    "role": "TUTOR"
  }'

# 2. Tuteur vérifie l'invitation
curl -X GET "http://localhost:8081/auth/invitations/token/<invitation-token>"

# 3. Tuteur accepte l'invitation
curl -X POST http://localhost:8081/auth/invitations/accept \
  -H "Content-Type: application/json" \
  -d '{
    "token": "<invitation-token>",
    "password": "SecurePass123!",
    "confirmPassword": "SecurePass123!",
    "firstName": "Jane",
    "lastName": "Smith",
    "phone": "+33698765432",
    "cin": "CD789012",
    "dateOfBirth": "1990-05-20",
    "yearsOfExperience": 5,
    "bio": "Experienced English teacher"
  }'
```

### 3. Gérer les sessions

```bash
# Voir mes sessions actives
curl -X GET http://localhost:8081/sessions/my-sessions \
  -H "Authorization: Bearer <token>"

# Terminer une session spécifique
curl -X DELETE http://localhost:8081/sessions/my-sessions/123 \
  -H "Authorization: Bearer <token>"

# Terminer toutes les autres sessions
curl -X DELETE "http://localhost:8081/sessions/my-sessions/others?currentSessionToken=<current-token>" \
  -H "Authorization: Bearer <token>"
```

### 4. Rechercher dans les audit logs (Admin)

```bash
curl -X POST http://localhost:8081/audit/search \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "action": "LOGIN_SUCCESS",
    "status": "SUCCESS",
    "startDate": "2024-02-01T00:00:00",
    "endDate": "2024-02-20T23:59:59",
    "page": 0,
    "size": 20,
    "sortBy": "createdAt",
    "sortDirection": "DESC"
  }'
```

### 5. Reset password

```bash
# 1. Demander reset
curl -X POST http://localhost:8081/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com"
  }'

# 2. Reset avec le token reçu par email
curl -X POST http://localhost:8081/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "<reset-token>",
    "newPassword": "NewSecurePass123!",
    "confirmPassword": "NewSecurePass123!"
  }'
```

---

## 🔒 Sécurité

### Rate Limiting

- **Login:** 5 tentatives par 15 minutes
- **Register:** 3 tentatives par heure
- **Password Reset:** 3 tentatives par heure

### Token Expiration

- **JWT Access Token:** 15 minutes
- **Refresh Token:** 7 jours
- **Activation Token:** 24 heures
- **Password Reset Token:** 1 heure
- **Invitation Token:** 7 jours

### Sessions

- **Max concurrent sessions:** 5 par utilisateur
- **Session inactivity timeout:** 30 minutes
- **Session cleanup:** Automatique (scheduled)

---

## 📞 Support

Pour toute question ou problème:

- **Email:** support@englishflow.com
- **Documentation:** http://localhost:8081/swagger-ui.html
- **GitHub:** https://github.com/englishflow/auth-service

---

**Version:** 1.0.0  
**Dernière mise à jour:** 20 Février 2024
