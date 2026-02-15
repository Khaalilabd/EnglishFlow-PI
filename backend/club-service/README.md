# Club Service - Jungle in English

Microservice de gestion des clubs pour la plateforme Jungle in English.

## Description

Ce service gère les clubs d'apprentissage de l'anglais, permettant aux étudiants de rejoindre des groupes selon leur niveau.

## Fonctionnalités

- Création et gestion des clubs
- Filtrage par niveau (Beginner, Intermediate, Advanced)
- Gestion des membres
- Planification des sessions
- Attribution des instructeurs

## Technologies

- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL
- Spring Cloud Netflix Eureka Client
- Lombok

## Configuration

1. Copier `.env.example` vers `.env`
2. Configurer les variables d'environnement
3. Créer la base de données PostgreSQL: `jungle_club_db`

## Endpoints API

### GET /api/clubs
Récupérer tous les clubs

### GET /api/clubs/active
Récupérer les clubs actifs

### GET /api/clubs/level/{level}
Récupérer les clubs par niveau (Beginner, Intermediate, Advanced)

### GET /api/clubs/{id}
Récupérer un club par ID

### POST /api/clubs
Créer un nouveau club

### PUT /api/clubs/{id}
Mettre à jour un club

### DELETE /api/clubs/{id}
Supprimer un club

## Lancement

```bash
mvn spring-boot:run
```

Le service démarre sur le port 8083 et s'enregistre automatiquement auprès d'Eureka Server.
