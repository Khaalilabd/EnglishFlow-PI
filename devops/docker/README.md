# Guide Docker - EnglishFlow

## Vue d'ensemble

Ce guide explique comment déployer l'application EnglishFlow avec Docker et Docker Compose.

## Architecture

```
EnglishFlow (Docker)
├── Infrastructure
│   ├── PostgreSQL (port 5432) - Base de données
│   └── Redis (port 6379) - Cache & Sessions
├── Service Discovery
│   └── Eureka Server (port 8761)
├── API Gateway (port 8080)
├── Microservices
│   ├── auth-service (port 8081)
│   ├── courses-service (port 8086)
│   ├── exam-service (port 8087)
│   ├── messaging-service (port 8084)
│   ├── community-service (port 8082)
│   └── club-service (port 8085)
└── Frontend (port 4200)
```

## Prérequis

- Docker Desktop 20.10+ ou Docker Engine + Docker Compose
- 8 GB RAM minimum (16 GB recommandé)
- 20 GB d'espace disque libre

### Installation Docker

**Windows:**
- Télécharger Docker Desktop: https://www.docker.com/products/docker-desktop

**macOS:**
```bash
brew install --cask docker
```

**Linux:**
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
```

## Configuration

### 1. Variables d'environnement

Copier le fichier `.env.example` vers `.env`:

```bash
cp .env.example .env
```

Éditer `.env` avec vos valeurs:

```env
JWT_SECRET=your-secret-key-minimum-256-bits
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
GOOGLE_CLIENT_ID=your-client-id
GOOGLE_CLIENT_SECRET=your-client-secret
RECAPTCHA_SECRET=your-recaptcha-secret
```

### 2. Vérifier la configuration

```bash
# Vérifier que Docker est installé
docker --version
docker-compose --version

# Vérifier que les fichiers sont présents
ls -la docker-compose.yml
ls -la .env
```

## Démarrage

### Démarrage complet (tous les services)

```bash
# Build et démarrer tous les services
docker-compose up -d

# Voir les logs
docker-compose logs -f

# Voir les logs d'un service spécifique
docker-compose logs -f auth-service
```

### Démarrage progressif (recommandé pour la première fois)

```bash
# 1. Infrastructure d'abord
docker-compose up -d postgres redis

# Attendre que les services soient prêts (30 secondes)
docker-compose ps

# 2. Eureka Server
docker-compose up -d eureka-server

# Attendre 40 secondes
sleep 40

# 3. API Gateway
docker-compose up -d api-gateway

# Attendre 30 secondes
sleep 30

# 4. Microservices
docker-compose up -d auth-service courses-service exam-service messaging-service community-service club-service

# Attendre 60 secondes
sleep 60

# 5. Frontend
docker-compose up -d frontend
```

### Vérification du démarrage

```bash
# Voir l'état de tous les services
docker-compose ps

# Tous les services doivent être "Up" et "healthy"
```

Accéder aux services:
- **Frontend**: http://localhost:4200
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Auth Service**: http://localhost:8081/actuator/health

## Commandes utiles

### Gestion des services

```bash
# Démarrer tous les services
docker-compose up -d

# Arrêter tous les services
docker-compose down

# Redémarrer un service spécifique
docker-compose restart auth-service

# Voir les logs en temps réel
docker-compose logs -f

# Voir les logs des 100 dernières lignes
docker-compose logs --tail=100

# Voir l'utilisation des ressources
docker stats
```

### Build et rebuild

```bash
# Rebuild tous les services
docker-compose build

# Rebuild un service spécifique
docker-compose build auth-service

# Rebuild et redémarrer
docker-compose up -d --build

# Rebuild sans cache (si problèmes)
docker-compose build --no-cache
```

### Nettoyage

```bash
# Arrêter et supprimer les conteneurs
docker-compose down

# Arrêter et supprimer les conteneurs + volumes
docker-compose down -v

# Supprimer les images
docker-compose down --rmi all

# Nettoyage complet (ATTENTION: supprime tout)
docker-compose down -v --rmi all
docker system prune -a --volumes
```

### Debugging

```bash
# Entrer dans un conteneur
docker-compose exec auth-service sh

# Voir les logs d'erreur
docker-compose logs auth-service | grep ERROR

# Inspecter un conteneur
docker inspect englishflow-auth

# Voir les processus dans un conteneur
docker-compose top auth-service

# Vérifier la santé des services
docker-compose ps
```

## Volumes et données

### Volumes persistants

Les données suivantes sont persistées dans des volumes Docker:

- **postgres_data**: Bases de données PostgreSQL
- **redis_data**: Cache Redis
- **auth_uploads**: Photos de profil
- **courses_uploads**: Fichiers de cours
- **messaging_uploads**: Photos de groupe
- **community_uploads**: Fichiers communauté

### Backup des volumes

```bash
# Backup PostgreSQL
docker-compose exec postgres pg_dumpall -U postgres > backup.sql

# Backup d'un volume
docker run --rm -v englishflow_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres-backup.tar.gz /data

# Restore d'un volume
docker run --rm -v englishflow_postgres_data:/data -v $(pwd):/backup alpine tar xzf /backup/postgres-backup.tar.gz -C /
```

## Monitoring

### Health checks

Tous les services ont des health checks configurés:

```bash
# Vérifier la santé de tous les services
docker-compose ps

# Vérifier un service spécifique
curl http://localhost:8081/actuator/health
```

### Métriques

Accéder aux métriques Prometheus:

```bash
# Auth Service
curl http://localhost:8081/actuator/prometheus

# Courses Service
curl http://localhost:8086/actuator/prometheus
```

## Troubleshooting

### Les services ne démarrent pas

```bash
# Vérifier les logs
docker-compose logs

# Vérifier l'espace disque
df -h

# Vérifier la mémoire
docker stats

# Redémarrer Docker Desktop (Windows/Mac)
```

### Erreur "port already in use"

```bash
# Trouver le processus qui utilise le port
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Mac/Linux

# Arrêter le processus ou changer le port dans docker-compose.yml
```

### Services en état "unhealthy"

```bash
# Voir les logs du service
docker-compose logs auth-service

# Vérifier la connexion à la base de données
docker-compose exec auth-service curl http://localhost:8081/actuator/health

# Redémarrer le service
docker-compose restart auth-service
```

### Base de données vide

```bash
# Vérifier que les bases sont créées
docker-compose exec postgres psql -U postgres -c "\l"

# Recréer les bases
docker-compose down -v
docker-compose up -d postgres
# Attendre 30 secondes
docker-compose up -d
```

### Problèmes de cache

```bash
# Vider le cache Redis
docker-compose exec redis redis-cli FLUSHALL

# Rebuild sans cache
docker-compose build --no-cache
docker-compose up -d
```

## Performance

### Optimisation mémoire

Éditer `docker-compose.yml` pour limiter la mémoire:

```yaml
services:
  auth-service:
    deploy:
      resources:
        limits:
          memory: 512M
        reservations:
          memory: 256M
```

### Optimisation build

```bash
# Build en parallèle
docker-compose build --parallel

# Utiliser BuildKit (plus rapide)
DOCKER_BUILDKIT=1 docker-compose build
```

## Production

### Recommandations

1. **Sécurité:**
   - Changer tous les mots de passe par défaut
   - Utiliser des secrets Docker
   - Activer HTTPS avec un reverse proxy (Nginx/Traefik)

2. **Performance:**
   - Augmenter les ressources (CPU/RAM)
   - Utiliser un load balancer
   - Activer le clustering Redis

3. **Monitoring:**
   - Ajouter Prometheus + Grafana
   - Configurer les alertes
   - Centraliser les logs (ELK Stack)

4. **Backup:**
   - Automatiser les backups PostgreSQL
   - Sauvegarder les volumes régulièrement
   - Tester les procédures de restore

### Déploiement production

```bash
# Utiliser le profil production
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Avec secrets
docker-compose --env-file .env.prod up -d
```

## Support

Pour plus d'informations:
- Documentation Docker: https://docs.docker.com
- Documentation Docker Compose: https://docs.docker.com/compose
- Issues GitHub: [lien vers votre repo]

## Changelog

- **v1.0.0** (2026-03-16): Version initiale avec tous les services
