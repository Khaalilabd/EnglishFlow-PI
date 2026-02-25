# Event Service

Service de gestion des événements pour Jungle in English.

## Description

Le Event Service gère tous les événements (workshops, séminaires, événements sociaux) et les inscriptions des étudiants.

## Fonctionnalités

- Création, modification et suppression d'événements
- Inscription et désinscription aux événements
- Gestion des participants
- Limitation du nombre de participants
- Filtrage par type d'événement
- Liste des événements à venir

## Types d'événements

- **WORKSHOP** : Ateliers pratiques
- **SEMINAR** : Séminaires éducatifs
- **SOCIAL** : Événements sociaux

## API Endpoints

### Events

- `GET /api/events` - Liste tous les événements
- `GET /api/events/{id}` - Récupère un événement par ID
- `GET /api/events/type/{type}` - Filtre par type (WORKSHOP, SEMINAR, SOCIAL)
- `GET /api/events/upcoming` - Liste des événements à venir
- `POST /api/events` - Créer un événement
- `PUT /api/events/{id}` - Mettre à jour un événement
- `DELETE /api/events/{id}` - Supprimer un événement

### Participants

- `POST /api/events/{eventId}/join` - S'inscrire à un événement
- `DELETE /api/events/{eventId}/leave/{userId}` - Se désinscrire
- `GET /api/events/{eventId}/participants` - Liste des participants
- `GET /api/events/user/{userId}` - Événements d'un utilisateur
- `GET /api/events/{eventId}/is-participant/{userId}` - Vérifier l'inscription

## Configuration

### Base de données

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/event_db
    username: postgres
    password: nadhem
```

### Port

Le service tourne sur le port **8086**

## Démarrage

```bash
cd backend/event-service
mvn spring-boot:run
```

## Technologies

- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL
- Eureka Client
- Lombok
- Validation
