# Messaging Service - Jungle in English

Service de messagerie en temps réel pour la plateforme Jungle in English.

## 🚀 Démarrage

### Prérequis
- Java 17+
- PostgreSQL
- Maven

### 1. Créer la base de données
```bash
psql -U postgres
CREATE DATABASE messaging_db;
\q
```

### 2. Configurer application.yml
Modifier `src/main/resources/application.yml` si nécessaire (mot de passe DB, etc.)

### 3. Lancer le service
```bash
cd backend/messaging-service
mvn clean install
mvn spring-boot:run
```

Le service démarre sur le port **8084**.

## 📡 Endpoints

### REST API
- `GET /api/messaging/conversations` - Liste des conversations
- `POST /api/messaging/conversations` - Créer une conversation
- `GET /api/messaging/conversations/{id}/messages` - Messages d'une conversation
- `POST /api/messaging/conversations/{id}/messages` - Envoyer un message
- `POST /api/messaging/conversations/{id}/mark-read` - Marquer comme lu
- `GET /api/messaging/unread-count` - Nombre de messages non lus

### WebSocket
- Endpoint: `/ws`
- Envoyer message: `/app/chat/{conversationId}`
- Indicateur de frappe: `/app/typing/{conversationId}`
- Recevoir messages: `/topic/conversation/{conversationId}`

## 🔐 Authentification

Tous les endpoints nécessitent un token JWT dans le header:
```
Authorization: Bearer {token}
```

## 📊 Base de Données

Tables créées automatiquement par Hibernate:
- `conversations`
- `conversation_participants`
- `messages`
- `message_read_status`
