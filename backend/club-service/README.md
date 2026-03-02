# Club Service - Jungle in English

Microservice de gestion des clubs pour la plateforme Jungle in English.

## Description

Ce service gère les clubs d'apprentissage de l'anglais, permettant aux étudiants de rejoindre des groupes selon leur niveau.

## Fonctionnalités

- Création et gestion des clubs
- Filtrage par niveau (Beginner, Intermediate, Advanced)
- Gestion des membres et des rôles (President, Vice-President, Member)
- Planification des sessions
- Attribution des instructeurs
- Workflow d'approbation des clubs
- Système de tâches pour les clubs
- Cache distribué pour optimiser les performances
- Monitoring et métriques avec Prometheus
- Documentation API interactive avec Swagger

## Technologies

- Spring Boot 3.2.0
- Spring Data JPA avec optimisations (batch processing, query optimization)
- PostgreSQL avec index optimisés
- Spring Cloud Netflix Eureka Client
- Lombok
- MapStruct pour le mapping DTO/Entity
- Caffeine Cache pour la mise en cache
- Micrometer pour les métriques
- Logstash pour le logging structuré
- Swagger/OpenAPI pour la documentation

## Optimisations Implémentées

### Performance
- ✅ Cache Caffeine sur les requêtes fréquentes (clubs, membres)
- ✅ Index sur les colonnes fréquemment recherchées
- ✅ Batch processing Hibernate (batch_size: 20)
- ✅ Connection pooling optimisé (HikariCP)
- ✅ Transactions read-only pour les lectures
- ✅ MapStruct pour mapping performant

### Monitoring & Observabilité
- ✅ Métriques Prometheus exposées sur /actuator/prometheus
- ✅ Health checks (liveness & readiness)
- ✅ Distributed tracing avec Micrometer
- ✅ Logging structuré JSON avec Logstash
- ✅ Logs rotatifs (30 jours, 1GB max)

### Qualité du Code
- ✅ Exceptions custom typées
- ✅ Gestion d'erreurs centralisée
- ✅ Logging SLF4J avec niveaux appropriés
- ✅ Tests unitaires avec JUnit 5 et Mockito
- ✅ Validation des DTOs avec Bean Validation

### DevOps
- ✅ Dockerfile multi-stage optimisé
- ✅ Health checks dans le container
- ✅ Configuration externalisée (.env)
- ✅ Compression HTTP/2 activée

## Configuration

1. Copier `.env.example` vers `.env`
2. Configurer les variables d'environnement
3. Créer la base de données PostgreSQL: `jungle_club_db`

### Variables d'environnement

```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=jungle_club_db
DB_USERNAME=postgres
DB_PASSWORD=your_password

# Server
SERVER_PORT=8085

# Eureka
EUREKA_SERVER_URL=http://localhost:8761/eureka/

# Logging (optional)
LOG_LEVEL_ROOT=INFO
LOG_LEVEL_APP=INFO
LOG_LEVEL_SQL=WARN

# JPA (optional)
JPA_DDL_AUTO=update
JPA_SHOW_SQL=false
```

## Endpoints API

### Clubs

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/clubs` | Récupérer tous les clubs |
| GET | `/clubs/{id}` | Récupérer un club par ID |
| GET | `/clubs/category/{category}` | Récupérer les clubs par catégorie |
| GET | `/clubs/search?name={name}` | Rechercher des clubs par nom |
| GET | `/clubs/pending` | Récupérer les clubs en attente d'approbation |
| GET | `/clubs/approved` | Récupérer les clubs approuvés |
| GET | `/clubs/user/{userId}` | Récupérer les clubs créés par un utilisateur |
| GET | `/clubs/user/{userId}/with-role` | Récupérer les clubs avec le rôle de l'utilisateur |
| POST | `/clubs` | Créer un nouveau club |
| PUT | `/clubs/{id}?requesterId={id}` | Mettre à jour un club |
| DELETE | `/clubs/{id}` | Supprimer un club |
| POST | `/clubs/{id}/approve` | Approuver un club |
| POST | `/clubs/{id}/reject` | Rejeter un club |

### Membres

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/members/club/{clubId}` | Récupérer les membres d'un club |
| GET | `/members/user/{userId}` | Récupérer les clubs d'un utilisateur |
| POST | `/members/club/{clubId}/user/{userId}` | Ajouter un membre à un club |
| PUT | `/members/{memberId}/rank` | Modifier le rôle d'un membre |
| DELETE | `/members/{memberId}` | Retirer un membre d'un club |

### Tâches

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/tasks/club/{clubId}` | Récupérer les tâches d'un club |
| POST | `/tasks` | Créer une nouvelle tâche |
| PUT | `/tasks/{taskId}` | Mettre à jour une tâche |
| DELETE | `/tasks/{taskId}` | Supprimer une tâche |

### Monitoring

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Health check |
| `/actuator/metrics` | Métriques applicatives |
| `/actuator/prometheus` | Métriques Prometheus |
| `/swagger-ui.html` | Documentation API interactive |
| `/api-docs` | Spécification OpenAPI JSON |

## Lancement

### Développement local

```bash
mvn spring-boot:run
```

### Avec Docker

```bash
# Build
docker build -t club-service:latest .

# Run
docker run -p 8085:8085 \
  -e DB_HOST=host.docker.internal \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=your_password \
  club-service:latest
```

Le service démarre sur le port 8085 et s'enregistre automatiquement auprès d'Eureka Server.

## Tests

```bash
# Exécuter tous les tests
mvn test

# Exécuter les tests avec couverture
mvn test jacoco:report
```

## Monitoring

### Prometheus Metrics

Les métriques sont exposées sur `http://localhost:8085/actuator/prometheus`

Métriques disponibles:
- `http_server_requests_seconds` - Latence des requêtes HTTP
- `jvm_memory_used_bytes` - Utilisation mémoire JVM
- `hikaricp_connections_active` - Connexions DB actives
- `cache_gets_total` - Statistiques du cache

### Logs

Les logs sont disponibles dans le dossier `logs/`:
- `club-service.log` - Logs texte
- `club-service.json` - Logs structurés JSON
- Rotation automatique quotidienne avec compression

## Architecture

```
club-service/
├── config/          # Configuration (Cache, OpenAPI)
├── controller/      # REST Controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA Entities (avec index)
├── enums/           # Enumerations
├── exception/       # Custom Exceptions & Handler
├── mapper/          # MapStruct Mappers
├── repository/      # Spring Data Repositories
└── service/         # Business Logic (avec cache)
```

## Sécurité

- Validation des entrées avec Bean Validation
- Gestion des erreurs sans exposition de stack traces
- Health checks pour Kubernetes/Docker
- Logs structurés sans données sensibles

## Performance

- Cache TTL: 5 minutes
- Connection pool: 10 max, 5 min idle
- Batch size: 20 pour les insertions
- HTTP/2 et compression activés

## Contribution

1. Créer une branche feature
2. Ajouter des tests pour les nouvelles fonctionnalités
3. Vérifier que tous les tests passent
4. Créer une Pull Request

## License

MIT License
