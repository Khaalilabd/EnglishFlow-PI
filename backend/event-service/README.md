# Event Service - Jungle in English

Service de gestion des événements pour Jungle in English.

## Description

Le Event Service gère tous les événements (workshops, séminaires, événements sociaux) et les inscriptions des étudiants avec un système de permissions basé sur les rôles des clubs.

## Fonctionnalités

- Création, modification et suppression d'événements
- Inscription et désinscription aux événements
- Gestion des participants avec limite
- Filtrage par type d'événement
- Liste des événements à venir
- Workflow d'approbation des événements
- Vérification des permissions via Feign Client
- Cache distribué pour optimiser les performances
- Monitoring et métriques avec Prometheus

## Technologies

- Spring Boot 3.2.0
- Spring Data JPA avec optimisations
- PostgreSQL avec index optimisés
- Spring Cloud Netflix Eureka Client
- Spring Cloud OpenFeign
- Lombok
- MapStruct pour le mapping DTO/Entity
- Caffeine Cache pour la mise en cache
- Micrometer pour les métriques
- Logstash pour le logging structuré
- Swagger/OpenAPI pour la documentation

## Optimisations Implémentées

### Performance
- ✅ Cache Caffeine sur les requêtes fréquentes
- ✅ Index sur les colonnes fréquemment recherchées
- ✅ Batch processing Hibernate
- ✅ Connection pooling optimisé (HikariCP)
- ✅ Transactions read-only pour les lectures
- ✅ MapStruct pour mapping performant

### Monitoring & Observabilité
- ✅ Métriques Prometheus
- ✅ Health checks (liveness & readiness)
- ✅ Distributed tracing
- ✅ Logging structuré JSON
- ✅ Logs rotatifs (30 jours, 1GB max)

### Qualité du Code
- ✅ Exceptions custom typées
- ✅ Gestion d'erreurs centralisée
- ✅ Logging SLF4J avec contexte
- ✅ Validation des DTOs

## Types d'événements

- **WORKSHOP** : Ateliers pratiques
- **SEMINAR** : Séminaires éducatifs
- **SOCIAL** : Événements sociaux

## Configuration

### Variables d'environnement

```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=event_db
DB_USERNAME=postgres
DB_PASSWORD=your_password

# Server
SERVER_PORT=8088

# Eureka
EUREKA_SERVER_URL=http://localhost:8761/eureka/

# Logging (optional)
LOG_LEVEL_ROOT=INFO
LOG_LEVEL_APP=INFO
LOG_LEVEL_CLIENT=INFO
LOG_LEVEL_FEIGN=INFO
LOG_LEVEL_SQL=WARN

# JPA (optional)
JPA_DDL_AUTO=update
JPA_SHOW_SQL=false
```

## API Endpoints

### Events

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/events` | Liste tous les événements |
| GET | `/api/events/{id}` | Récupère un événement par ID |
| GET | `/api/events/type/{type}` | Filtre par type (WORKSHOP, SEMINAR, SOCIAL) |
| GET | `/api/events/upcoming` | Liste des événements à venir |
| GET | `/api/events/creator/{creatorId}` | Événements créés par un utilisateur |
| POST | `/api/events` | Créer un événement |
| PUT | `/api/events/{id}` | Mettre à jour un événement |
| DELETE | `/api/events/{id}` | Supprimer un événement |
| POST | `/api/events/{id}/approve` | Approuver un événement |
| POST | `/api/events/{id}/reject` | Rejeter un événement |

### Participants

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/events/{eventId}/join` | S'inscrire à un événement |
| DELETE | `/api/events/{eventId}/leave/{userId}` | Se désinscrire |
| GET | `/api/events/{eventId}/participants` | Liste des participants |
| GET | `/api/events/user/{userId}` | Événements d'un utilisateur |
| GET | `/api/events/{eventId}/is-participant/{userId}` | Vérifier l'inscription |

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
docker build -t event-service:latest .

# Run
docker run -p 8088:8088 \
  -e DB_HOST=host.docker.internal \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=your_password \
  event-service:latest
```

Le service démarre sur le port 8088 et s'enregistre automatiquement auprès d'Eureka Server.

## Architecture

```
event-service/
├── client/          # Feign Clients (ClubServiceClient)
├── config/          # Configuration (Cache, OpenAPI, Feign)
├── controller/      # REST Controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA Entities (avec index)
├── enums/           # Enumerations
├── exception/       # Custom Exceptions & Handler
├── mapper/          # MapStruct Mappers
├── repository/      # Spring Data Repositories
└── service/         # Business Logic (avec cache)
```

## Permissions

Le service utilise un système de permissions basé sur les rôles des clubs:
- Seuls les présidents de clubs peuvent créer des événements
- Vérification via Feign Client vers le club-service

## Performance

- Cache TTL: 5 minutes
- Connection pool: 10 max, 5 min idle
- Batch size: 20 pour les insertions
- HTTP/2 et compression activés

## Monitoring

### Prometheus Metrics

Métriques exposées sur `http://localhost:8088/actuator/prometheus`

### Logs

Les logs sont disponibles dans le dossier `logs/`:
- `event-service.log` - Logs texte
- `event-service.json` - Logs structurés JSON
- Rotation automatique quotidienne avec compression

## License

MIT License
