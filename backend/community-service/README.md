# Community Service

Service de gestion de la communauté (forum) pour EnglishFlow.

## 📋 Description

Le Community Service gère toutes les fonctionnalités du forum communautaire incluant:
- Catégories et sous-catégories
- Topics (sujets de discussion)
- Posts (réponses)
- Réactions (likes, helpful, insightful)
- Recherche de contenu
- Statistiques et métriques

## 🚀 Technologies

- **Java 17**
- **Spring Boot 3.2.0**
- **PostgreSQL** - Base de données principale
- **Redis** - Cache distribué
- **Spring Cloud Netflix Eureka** - Service discovery
- **Spring Data JPA** - ORM
- **Lombok** - Réduction du boilerplate
- **SpringDoc OpenAPI** - Documentation API
- **Spring Cache** - Gestion du cache

## ⚙️ Configuration

### Prérequis
- Java 17+
- Maven 3.6+
- PostgreSQL 14+
- Redis 6+ (optionnel, pour le cache)

### Variables d'environnement (.env)
```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=englishflow_community
DB_USERNAME=postgres
DB_PASSWORD=postgres

REDIS_HOST=localhost
REDIS_PORT=6379

EUREKA_SERVER=http://localhost:8761/eureka/
```

### Base de données
```sql
CREATE DATABASE englishflow_community;
```

## 🏃 Démarrage

### Avec Maven
```bash
mvn clean install
mvn spring-boot:run
```

### Avec Docker
```bash
docker build -t community-service .
docker run -p 8082:8082 community-service
```

## 📚 Documentation API

Une fois le service démarré, accédez à:
- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8082/api-docs

## 🔗 Endpoints principaux

### Catégories
- `GET /community/categories` - Liste toutes les catégories
- `GET /community/categories/{id}` - Détails d'une catégorie
- `POST /community/categories` - Créer une catégorie
- `PUT /community/categories/{id}` - Modifier une catégorie
- `DELETE /community/categories/{id}` - Supprimer une catégorie

### Topics
- `GET /community/topics/subcategory/{id}` - Topics par sous-catégorie (paginé)
- `GET /community/topics/{id}` - Détails d'un topic
- `POST /community/topics` - Créer un topic
- `PUT /community/topics/{id}` - Modifier un topic
- `DELETE /community/topics/{id}` - Supprimer un topic
- `PUT /community/topics/{id}/pin` - Épingler un topic
- `PUT /community/topics/{id}/lock` - Verrouiller un topic

### Posts
- `GET /community/posts/topic/{id}` - Posts d'un topic (paginé)
- `POST /community/posts` - Créer un post
- `PUT /community/posts/{id}` - Modifier un post
- `DELETE /community/posts/{id}` - Supprimer un post

### Réactions
- `POST /community/reactions/posts/{id}` - Ajouter une réaction à un post
- `POST /community/reactions/topics/{id}` - Ajouter une réaction à un topic
- `DELETE /community/reactions/posts/{id}` - Retirer une réaction d'un post
- `GET /community/reactions/posts/{id}/count` - Compter les réactions

### Recherche
- `GET /community/search/topics?keyword={keyword}` - Rechercher des topics

## 🏗️ Architecture

```
community-service/
├── config/          # Configuration (Cache, CORS, OpenAPI)
├── controller/      # REST Controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA Entities
├── exception/      # Custom Exceptions & Handler
├── repository/     # JPA Repositories
└── service/        # Business Logic
```

## 🔐 Sécurité

### CORS
Configuré pour accepter les requêtes depuis:
- http://localhost:4200 (Angular)
- http://localhost:3000 (React)

### Gestion des erreurs
- `ResourceNotFoundException` (404)
- `TopicLockedException` (403)
- `UnauthorizedException` (401)
- `DuplicateResourceException` (409)
- Validation errors (400)

## 📊 Monitoring

### Actuator Endpoints
- `/actuator/health` - État du service
- `/actuator/info` - Informations du service
- `/actuator/metrics` - Métriques
- `/actuator/prometheus` - Métriques Prometheus

## 🧪 Tests

```bash
# Exécuter tous les tests
mvn test

# Avec couverture
mvn test jacoco:report
```

## 🚀 Optimisations implémentées

### ✅ Gestion des exceptions personnalisées
- Exceptions typées avec messages clairs
- GlobalExceptionHandler avec gestion détaillée
- Réponses d'erreur structurées

### ✅ Système de réactions
- Likes, Helpful, Insightful
- Compteurs optimisés
- Contraintes d'unicité (1 réaction par user/post)

### ✅ Recherche full-text
- Recherche dans titre et contenu des topics
- Pagination et tri

### ✅ Cache Redis
- Cache des catégories (10 min TTL)
- Invalidation automatique sur modifications
- Amélioration des performances

### ✅ Documentation API
- Swagger UI intégré
- Annotations OpenAPI complètes
- Exemples de requêtes

### ✅ Logging structuré
- Logs SLF4J dans tous les services
- Traçabilité des opérations CRUD

### ✅ Configuration CORS
- Sécurisation des endpoints
- Support multi-origines

## 📝 Données initiales

Le service initialise automatiquement 5 catégories avec 17 sous-catégories:
1. **Général** (3 sous-catégories)
2. **Discussions linguistiques** (4 sous-catégories)
3. **Clubs** (4 sous-catégories)
4. **Événements** (3 sous-catégories)
5. **Ressources et Aide** (2 sous-catégories)

## 🔄 Améliorations futures

- [ ] Système de notifications
- [ ] Modération avancée
- [ ] Upload de médias
- [ ] Badges et gamification
- [ ] Statistiques utilisateur
- [ ] Export de données
- [ ] Tests unitaires et d'intégration
- [ ] Rate limiting
- [ ] Authentification JWT

## 📞 Support

Pour toute question ou problème:
- Email: support@englishflow.com
- Documentation: http://localhost:8082/swagger-ui.html
