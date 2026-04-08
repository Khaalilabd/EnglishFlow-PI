# 🚀 Guide de Démarrage Rapide - Docker

## Prérequis

✅ Docker Desktop est installé et lancé
✅ 8 GB RAM minimum disponible
✅ 20 GB d'espace disque libre

## Option 1: Démarrage Automatique (Recommandé)

### Windows (PowerShell)
```powershell
cd devops/docker
.\start-dev.ps1
```

### Mac/Linux (Bash)
```bash
cd devops/docker
chmod +x start-dev.sh
./start-dev.sh
```

Le script va démarrer tous les services dans le bon ordre et attendre qu'ils soient prêts.

## Option 2: Démarrage Manuel

### 1. Créer le fichier .env
```bash
cd devops/docker
cp .env.example .env
# Éditer .env si nécessaire (optionnel pour le dev)
```

### 2. Démarrer l'infrastructure
```bash
docker-compose up -d postgres redis
```
⏳ Attendre 30 secondes

### 3. Démarrer Eureka
```bash
docker-compose up -d eureka-server
```
⏳ Attendre 40 secondes

### 4. Démarrer l'API Gateway
```bash
docker-compose up -d api-gateway
```
⏳ Attendre 30 secondes

### 5. Démarrer les microservices
```bash
docker-compose up -d auth-service courses-service exam-service messaging-service community-service club-service
```
⏳ Attendre 60 secondes

### 6. Démarrer le frontend
```bash
docker-compose up -d frontend
```

## Vérification

### Voir l'état des services
```bash
docker-compose ps
```

Tous les services doivent être "Up" et "healthy" (sain).

### Accéder aux services

- **Frontend**: http://localhost:4200
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Auth Service Health**: http://localhost:8081/actuator/health

### Voir les logs

```bash
# Tous les services
docker-compose logs -f

# Un service spécifique
docker-compose logs -f auth-service

# Les 100 dernières lignes
docker-compose logs --tail=100
```

## Commandes Utiles

### Arrêter tous les services
```bash
docker-compose down
```

### Redémarrer un service
```bash
docker-compose restart auth-service
```

### Rebuild un service après modification
```bash
docker-compose build auth-service
docker-compose up -d auth-service
```

### Voir l'utilisation des ressources
```bash
docker stats
```

### Nettoyer complètement (⚠️ supprime les données)
```bash
docker-compose down -v
```

## Problèmes Courants

### "Port already in use"
Un service local utilise déjà le port. Arrêtez-le ou changez le port dans `docker-compose.yml`.

### Service "unhealthy"
```bash
# Voir les logs du service
docker-compose logs auth-service

# Redémarrer le service
docker-compose restart auth-service
```

### Manque de mémoire
Augmentez la RAM allouée à Docker Desktop dans les paramètres (minimum 8 GB).

### Build échoue
```bash
# Rebuild sans cache
docker-compose build --no-cache
```

## Temps de Démarrage

- Infrastructure (PostgreSQL + Redis): ~30 secondes
- Eureka Server: ~40 secondes
- API Gateway: ~30 secondes
- Microservices: ~60 secondes
- Frontend: ~10 secondes

**Total: ~3 minutes** pour un démarrage complet

## Architecture des Services

```
┌─────────────────────────────────────────────────────────┐
│                    Frontend (4200)                      │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                API Gateway (8080)                       │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│              Eureka Server (8761)                       │
└────────────────────┬────────────────────────────────────┘
                     │
        ┌────────────┴────────────┬──────────────┐
        │                         │              │
┌───────▼────────┐   ┌───────────▼──┐   ┌──────▼────────┐
│ Auth Service   │   │ Courses      │   │ Community     │
│    (8081)      │   │ Service      │   │ Service       │
└───────┬────────┘   │  (8086)      │   │  (8082)       │
        │            └───────┬──────┘   └──────┬────────┘
        │                    │                  │
┌───────▼────────────────────▼──────────────────▼────────┐
│              PostgreSQL (5432)                          │
└─────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────┐
│                Redis (6379)                             │
└─────────────────────────────────────────────────────────┘
```

## Prochaines Étapes

1. ✅ Démarrer tous les services
2. 🌐 Ouvrir http://localhost:4200
3. 🔐 Tester l'inscription/connexion
4. 📊 Vérifier Eureka Dashboard: http://localhost:8761
5. 📝 Consulter les logs si problème

## Support

Pour plus de détails, consultez le [README.md](README.md) complet.
