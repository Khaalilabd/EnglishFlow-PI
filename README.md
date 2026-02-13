# EnglishFlow - Plateforme d'Apprentissage de l'Anglais

Plateforme complète pour l'apprentissage de l'anglais avec architecture microservices.

## Structure du Projet

```
EnglishFlow-PI/
├── frontend/          # Application Angular (Backoffice + Frontoffice)
│   ├── src/
│   ├── public/
│   └── README.md
└── backend/           # Microservices Spring Boot
    ├── api-gateway/
    ├── auth-service/
    ├── user-service/
    └── README.md
```

## Frontend

Application Angular avec deux interfaces:
- **Backoffice**: Interface d'administration
- **Frontoffice**: Interface utilisateur (étudiants/enseignants)

Technologies: Angular 18, TypeScript, Tailwind CSS

[Voir le README du frontend](./frontend/README.md)

## Backend

Architecture microservices avec Spring Boot:
- API Gateway
- Service d'authentification
- Service utilisateurs
- Service cours
- Service paiements
- Service notifications

Technologies: Spring Boot 3, Spring Cloud, PostgreSQL, Redis, Docker

[Voir le README du backend](./backend/README.md)

## Démarrage Rapide

### Frontend
```bash
cd frontend
npm install
npm start
```

### Backend
```bash
cd backend
# Instructions à venir pour chaque microservice
```

## Contribution

Ce projet est développé dans le cadre d'un projet intégré (PI).
