# 📚 Documentation Auth Service

Bienvenue dans la documentation complète du service d'authentification EnglishFlow!

## 📖 Table des Matières

### 1. [API Documentation](./API_DOCUMENTATION.md)
Guide complet de l'API avec tous les endpoints, exemples de requêtes/réponses, et codes d'erreur.

**Contenu:**
- Vue d'ensemble du service
- Accès à Swagger UI
- Guide d'authentification JWT
- Liste complète des endpoints
- Codes d'erreur HTTP
- Exemples d'utilisation (curl)

### 2. [Exceptions Guide](./EXCEPTIONS_GUIDE.md)
Guide des 13 exceptions personnalisées et comment les utiliser.

**Contenu:**
- Liste des exceptions avec codes HTTP
- Exemples d'utilisation
- Migration du code existant
- Format de réponse d'erreur

### 3. [Testing Guide](./TESTING_GUIDE.md)
Guide pour écrire et exécuter les tests unitaires et d'intégration.

**Contenu:**
- Structure des tests
- Objectifs de couverture
- Commandes Maven
- Exemples de tests
- Tests d'intégration

### 4. [Changelog Improvements](./CHANGELOG_IMPROVEMENTS.md)
Historique détaillé des améliorations apportées au service.

**Contenu:**
- Nouvelles fonctionnalités
- Métriques d'amélioration
- Impact sur la qualité
- Prochaines étapes

---

## 🚀 Démarrage Rapide

### 1. Accéder à Swagger UI

```bash
# Démarrer le service
cd backend/auth-service
mvn spring-boot:run

# Ouvrir Swagger dans le navigateur
open http://localhost:8081/swagger-ui.html
```

### 2. Tester un endpoint

```bash
# S'inscrire
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe",
    "role": "STUDENT",
    "cin": "AB123456",
    "recaptchaToken": "test-token"
  }'
```

### 3. Exécuter les tests

```bash
# Tous les tests
mvn test

# Avec rapport de couverture
mvn test jacoco:report
open target/site/jacoco/index.html
```

---

## 📊 Statistiques du Service

### Fonctionnalités Implémentées

- ✅ 8 Controllers
- ✅ 11 Services
- ✅ 7 Repositories
- ✅ 13 Exceptions personnalisées
- ✅ 20+ DTOs
- ✅ 7 Entities
- ✅ Swagger/OpenAPI documentation
- ✅ JWT Authentication
- ✅ OAuth2 (Google)
- ✅ Session Management
- ✅ Audit Logging
- ✅ Rate Limiting
- ✅ Email Verification
- ✅ Password Reset
- ✅ Invitation System

### Endpoints Disponibles

- **Publics:** 8 endpoints (register, login, activate, etc.)
- **Authentifiés:** 15+ endpoints (profile, sessions, etc.)
- **Admin:** 20+ endpoints (user management, audit, etc.)

---

## 🔐 Sécurité

### Authentification
- JWT avec HS512 (15 min expiration)
- Refresh tokens (7 jours)
- OAuth2 Google
- reCAPTCHA protection

### Autorisation
- Role-based access control (RBAC)
- 4 rôles: STUDENT, TUTOR, ACADEMIC_OFFICE_AFFAIR, ADMIN

### Protection
- Rate limiting (5 tentatives/15 min sur login)
- Session tracking avec device info
- Audit logging de toutes les actions
- Email verification obligatoire

---

## 🛠️ Technologies

- **Framework:** Spring Boot 3.2.0
- **Security:** Spring Security 6
- **Database:** PostgreSQL
- **JWT:** jjwt 0.11.5
- **Documentation:** Swagger/OpenAPI 3.0
- **Email:** Spring Mail + Thymeleaf
- **Testing:** JUnit 5 + Mockito
- **Build:** Maven

---

## 📞 Support

### Documentation Interactive
- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/api-docs

### Fichiers de Documentation
- [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) - Guide complet de l'API
- [EXCEPTIONS_GUIDE.md](./EXCEPTIONS_GUIDE.md) - Guide des exceptions
- [TESTING_GUIDE.md](./TESTING_GUIDE.md) - Guide des tests
- [CHANGELOG_IMPROVEMENTS.md](./CHANGELOG_IMPROVEMENTS.md) - Historique des améliorations

### Contact
- Email: support@englishflow.com
- GitHub: https://github.com/englishflow/auth-service

---

## 🎯 Prochaines Améliorations

### Priorité Haute
1. ⏳ Compléter les tests unitaires (80%+ couverture)
2. ⏳ Migrer RuntimeException vers exceptions custom
3. ⏳ Ajouter annotations Swagger sur controllers

### Priorité Moyenne
4. ⏳ Implémenter 2FA/TOTP
5. ⏳ Ajouter monitoring Prometheus
6. ⏳ Implémenter token blacklisting (Redis)

### Priorité Basse
7. ⏳ Load testing avec JMeter
8. ⏳ Database encryption
9. ⏳ Advanced audit analytics

---

**Version:** 1.1.0  
**Dernière mise à jour:** 20 Février 2024  
**Auteur:** EnglishFlow Team
