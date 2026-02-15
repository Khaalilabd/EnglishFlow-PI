# Guide de Lancement - Jungle in English

## Prérequis

Avant de lancer les services, assurez-vous que:
- ✅ PostgreSQL est installé et en cours d'exécution
- ✅ Java 17+ est installé
- ✅ Maven est installé
- ✅ Node.js et npm sont installés
- ✅ Angular CLI est installé (`npm install -g @angular/cli`)

## Bases de données à créer

Créez les bases de données PostgreSQL suivantes:
```sql
CREATE DATABASE jungle_auth_db;
CREATE DATABASE jungle_club_db;
```

## Méthode 1: Script Automatique (Recommandé)

### Démarrer tous les services
```powershell
.\start-all-services.ps1
```

Ce script démarre automatiquement dans l'ordre:
1. Eureka Server (port 8761)
2. Config Server (port 8888)
3. Auth Service (port 8081)
4. Club Service (port 8083)
5. API Gateway (port 8080)
6. Frontend Angular (port 4200)

### Arrêter tous les services
```powershell
.\stop-all-services.ps1
```

## Méthode 2: Démarrage Manuel

### Backend (dans l'ordre)

1. **Eureka Server**
```powershell
cd backend/eureka-server
mvn spring-boot:run
```

2. **Config Server** (attendre 30 secondes)
```powershell
cd backend/config-server
mvn spring-boot:run
```

3. **Auth Service** (attendre 20 secondes)
```powershell
cd backend/auth-service
mvn spring-boot:run
```

4. **Club Service** (attendre 20 secondes)
```powershell
cd backend/club-service
mvn spring-boot:run
```

5. **API Gateway** (attendre 20 secondes)
```powershell
cd backend/api-gateway
mvn spring-boot:run
```

### Frontend

```powershell
cd EnglishFlow-PI
ng serve
```

## URLs des Services

| Service | URL | Description |
|---------|-----|-------------|
| Eureka Dashboard | http://localhost:8761 | Découverte de services |
| Config Server | http://localhost:8888 | Configuration centralisée |
| Auth Service | http://localhost:8081 | Authentification |
| Club Service | http://localhost:8083 | Gestion des clubs |
| API Gateway | http://localhost:8080 | Point d'entrée API |
| Frontend | http://localhost:4200 | Application Angular |

## Vérification

1. Ouvrez http://localhost:8761 pour voir tous les services enregistrés dans Eureka
2. Ouvrez http://localhost:4200 pour accéder à l'application

## Dépannage

### Erreur de port déjà utilisé
```powershell
# Trouver le processus utilisant le port
netstat -ano | findstr :8080

# Tuer le processus
taskkill /PID <PID> /F
```

### Erreur de connexion PostgreSQL
- Vérifiez que PostgreSQL est démarré
- Vérifiez les credentials dans les fichiers `.env` ou `application.yml`
- Vérifiez que les bases de données existent

### Services ne démarrent pas
- Vérifiez les logs dans les consoles
- Assurez-vous que Eureka démarre en premier
- Attendez suffisamment entre chaque service
