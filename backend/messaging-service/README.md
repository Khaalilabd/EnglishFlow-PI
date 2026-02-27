# Messaging Service - Jungle in English

Service de messagerie en temps réel pour la plateforme Jungle in English avec support WebSocket et Redis.

## 🚀 Fonctionnalités

- ✅ Messagerie en temps réel via WebSocket
- ✅ Conversations directes et de groupe
- ✅ Indicateurs de frappe (typing indicators)
- ✅ Statuts de lecture des messages
- ✅ Réactions aux messages (emojis)
- ✅ Support multi-types: texte, fichiers, images, emojis, messages vocaux
- ✅ Cache Redis pour haute performance
- ✅ Session clustering pour scalabilité horizontale
- ✅ Rate limiting pour protection anti-spam
- ✅ Monitoring avec Prometheus
- ✅ Documentation API avec Swagger

## 🛠 Technologies

- Spring Boot 3.2.0
- Spring WebSocket avec STOMP
- Spring Data JPA avec optimisations
- PostgreSQL avec index optimisés
- Redis pour cache et session clustering
- Spring Security + JWT
- MapStruct pour mapping DTO/Entity
- Micrometer pour métriques
- Logstash pour logging structuré
- Swagger/OpenAPI pour documentation

## 📦 Optimisations Implémentées

### Performance
- ✅ Redis cache pour conversations et messages
- ✅ Index DB sur toutes les colonnes critiques
- ✅ Batch processing Hibernate
- ✅ Connection pooling optimisé (HikariCP: 20 max, 10 min)
- ✅ MapStruct pour mapping performant
- ✅ Compression HTTP/2 activée

### Scalabilité
- ✅ Redis session clustering pour WebSocket multi-instance
- ✅ Stateless architecture
- ✅ Cache distribué
- ✅ Support 10,000+ connexions simultanées

### Monitoring & Observabilité
- ✅ Métriques Prometheus (HTTP + WebSocket)
- ✅ Health checks (liveness & readiness)
- ✅ Distributed tracing
- ✅ Logging structuré JSON
- ✅ Logs rotatifs (30 jours, 1GB max)

### Sécurité
- ✅ JWT authentication
- ✅ Rate limiting (60 messages/minute par défaut)
- ✅ WebSocket authentication
- ✅ CORS configuré

## 🔧 Prérequis

- Java 17+
- PostgreSQL 12+
- Redis 6+
- Maven 3.6+

## 📝 Configuration

### 1. Créer les bases de données

```bash
# PostgreSQL
psql -U postgres
CREATE DATABASE messaging_db;
\q

# Redis (si pas déjà installé)
# Windows: https://github.com/microsoftarchive/redis/releases
# Linux: sudo apt-get install redis-server
# Mac: brew install redis
```

### 2. Configurer les variables d'environnement

Copier `.env.example` vers `.env` et ajuster:

```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=messaging_db
DB_USERNAME=postgres
DB_PASSWORD=your_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT
JWT_SECRET=your-secret-key-minimum-256-bits

# WebSocket
WEBSOCKET_ALLOWED_ORIGINS=http://localhost:4200

# Rate Limiting
RATE_LIMIT_MESSAGES=60
RATE_LIMIT_ENABLED=true
```

### 3. Lancer le service

```bash
cd backend/messaging-service
mvn clean install
mvn spring-boot:run
```

Le service démarre sur le port **8084**.

## 📡 API Endpoints

### REST API

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/messaging/conversations` | Liste des conversations |
| POST | `/api/messaging/conversations` | Créer une conversation |
| GET | `/api/messaging/conversations/{id}` | Détails d'une conversation |
| GET | `/api/messaging/conversations/{id}/messages` | Messages d'une conversation (paginés) |
| POST | `/api/messaging/conversations/{id}/messages` | Envoyer un message |
| POST | `/api/messaging/conversations/{id}/mark-read` | Marquer comme lu |
| GET | `/api/messaging/unread-count` | Nombre de messages non lus |
| POST | `/api/messaging/messages/{id}/reactions` | Ajouter une réaction |
| DELETE | `/api/messaging/messages/{id}/reactions/{emoji}` | Retirer une réaction |

### WebSocket

**Endpoint de connexion:** `ws://localhost:8084/ws`

**Topics:**
- `/app/chat/{conversationId}` - Envoyer un message
- `/app/typing/{conversationId}` - Indicateur de frappe
- `/topic/conversation/{conversationId}` - Recevoir messages
- `/topic/typing/{conversationId}` - Recevoir indicateurs de frappe

**Exemple de connexion (JavaScript):**
```javascript
const socket = new SockJS('http://localhost:8084/ws');
const stompClient = Stomp.over(socket);

stompClient.connect(
  { Authorization: `Bearer ${token}` },
  () => {
    // Souscrire aux messages
    stompClient.subscribe('/topic/conversation/123', (message) => {
      console.log('New message:', JSON.parse(message.body));
    });
    
    // Envoyer un message
    stompClient.send('/app/chat/123', {}, JSON.stringify({
      content: 'Hello!',
      messageType: 'TEXT'
    }));
  }
);
```

### Monitoring

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Health check |
| `/actuator/metrics` | Métriques applicatives |
| `/actuator/prometheus` | Métriques Prometheus |
| `/swagger-ui.html` | Documentation API interactive |
| `/api-docs` | Spécification OpenAPI JSON |

## 🔐 Authentification

Tous les endpoints nécessitent un token JWT dans le header:
```
Authorization: Bearer {token}
```

## 🐳 Docker

### Build

```bash
docker build -t messaging-service:latest .
```

### Run

```bash
docker run -p 8084:8084 \
  -e DB_HOST=host.docker.internal \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=your_password \
  -e REDIS_HOST=host.docker.internal \
  -e JWT_SECRET=your-secret-key \
  messaging-service:latest
```

### Docker Compose (recommandé)

```yaml
version: '3.8'
services:
  messaging-service:
    build: .
    ports:
      - "8084:8084"
    environment:
      - DB_HOST=postgres
      - REDIS_HOST=redis
    depends_on:
      - postgres
      - redis
  
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: messaging_db
      POSTGRES_PASSWORD: postgres
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
```

## 📊 Base de Données

Tables créées automatiquement avec index optimisés:
- `conversations` - Index: type, last_message_at
- `conversation_participants` - Index: user_id, conversation_id, is_active
- `messages` - Index: conversation_id, sender_id, created_at
- `message_read_status` - Index: message_id, user_id
- `message_reactions` - Index: message_id, user_id

## 🚀 Performance

- Cache TTL: 5 minutes (conversations), 2 minutes (unread counts)
- Connection pool: 20 max, 10 min idle
- Batch size: 20 pour les insertions
- Rate limit: 60 messages/minute par utilisateur
- WebSocket: Support 10,000+ connexions simultanées

## 📈 Monitoring

### Métriques Prometheus

Exposées sur `http://localhost:8084/actuator/prometheus`

Métriques disponibles:
- `http_server_requests_seconds` - Latence HTTP
- `websocket_sessions` - Sessions WebSocket actives
- `jvm_memory_used_bytes` - Mémoire JVM
- `hikaricp_connections_active` - Connexions DB
- `cache_gets_total` - Statistiques cache Redis

### Logs

Les logs sont disponibles dans le dossier `logs/`:
- `messaging-service.log` - Logs texte
- `messaging-service.json` - Logs structurés JSON
- Rotation automatique quotidienne avec compression

## 🏗 Architecture

```
messaging-service/
├── client/          # Feign Clients (AuthServiceClient)
├── config/          # Configuration (Redis, WebSocket, Security, OpenAPI, RateLimit)
├── constants/       # Constantes
├── controller/      # REST Controllers + WebSocket Controller
├── dto/             # Data Transfer Objects
├── exception/       # Custom Exceptions & Handler
├── mapper/          # MapStruct Mappers
├── model/           # JPA Entities (avec index)
├── repository/      # Spring Data Repositories
└── service/         # Business Logic (avec cache)
```

## 🔒 Sécurité

- JWT authentication sur tous les endpoints
- WebSocket authentication via interceptor
- Rate limiting pour prévenir le spam
- CORS configuré
- Validation des entrées
- Logs d'audit

## 📚 Documentation

- Swagger UI: http://localhost:8084/swagger-ui.html
- OpenAPI JSON: http://localhost:8084/api-docs
- Health Check: http://localhost:8084/actuator/health

## 🤝 Intégration

Le service s'intègre avec:
- **auth-service** - Validation JWT et informations utilisateur
- **Eureka Server** - Service discovery
- **API Gateway** - Routing et load balancing

## License

MIT License
