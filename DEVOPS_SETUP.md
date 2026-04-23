# 🚀 Guide de Configuration DevOps - EnglishFlow

Ce guide vous accompagne dans la mise en place complète de l'infrastructure DevOps pour le projet EnglishFlow.

## 📋 Table des Matières

1. [Prérequis](#prérequis)
2. [Configuration SonarCloud](#configuration-sonarcloud)
3. [Configuration des Secrets GitHub](#configuration-des-secrets-github)
4. [Configuration Docker](#configuration-docker)
5. [Configuration Kubernetes](#configuration-kubernetes)
6. [Monitoring](#monitoring)
7. [Déploiement](#déploiement)

---

## 🔧 Prérequis

### Outils Requis

- **Docker** 24.0+ et Docker Compose 2.0+
- **Java** 17 (JDK)
- **Maven** 3.9+
- **Node.js** 20+ et npm
- **Git** 2.40+
- **kubectl** (pour Kubernetes)
- **PostgreSQL** 15+ (ou via Docker)
- **Redis** 7+ (ou via Docker)

### Comptes Nécessaires

- [ ] Compte GitHub (pour CI/CD)
- [ ] Compte SonarCloud (pour qualité du code)
- [ ] Compte Docker Hub ou GitHub Container Registry
- [ ] Serveur de déploiement (VPS, AWS, Azure, etc.)

---

## 🔍 Configuration SonarCloud

### Étape 1 : Créer un compte SonarCloud

1. Allez sur [sonarcloud.io](https://sonarcloud.io)
2. Connectez-vous avec votre compte GitHub
3. Cliquez sur **"+"** → **"Analyze new project"**
4. Sélectionnez votre repository **EnglishFlow**

### Étape 2 : Récupérer les informations

Après création du projet, notez :
- **Organization Key** : `your-org-name`
- **Project Key** : `englishflow-backend`
- **Token** : Générez un token dans **My Account** → **Security**

### Étape 3 : Mettre à jour les fichiers de configuration

Éditez les fichiers suivants avec vos valeurs :

**`sonar-project.properties`** (racine du projet) :
```properties
sonar.organization=VOTRE_ORG_NAME
sonar.projectKey=englishflow-backend
```

**`backend/sonar-project.properties`** :
```properties
sonar.organization=VOTRE_ORG_NAME
sonar.projectKey=englishflow-backend
```

### Étape 4 : Ajouter le token dans GitHub Secrets

1. Allez dans **Settings** → **Secrets and variables** → **Actions**
2. Cliquez sur **New repository secret**
3. Nom : `SONAR_TOKEN`
4. Valeur : Collez votre token SonarCloud
5. Cliquez sur **Add secret**

### Étape 5 : Tester l'analyse

```bash
# Localement (optionnel)
cd backend
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=englishflow-backend \
  -Dsonar.organization=your-org-name \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.token=YOUR_TOKEN
```

---

## 🔐 Configuration des Secrets GitHub

### Secrets Requis

Allez dans **Settings** → **Secrets and variables** → **Actions** et ajoutez :

| Secret Name | Description | Exemple |
|-------------|-------------|---------|
| `SONAR_TOKEN` | Token SonarCloud | `sqp_xxxxxxxxxxxxx` |
| `JWT_SECRET` | Clé JWT (256 bits) | Générer avec `openssl rand -base64 32` |
| `DB_PASSWORD` | Mot de passe PostgreSQL | `postgres123` |
| `MAIL_USERNAME` | Email Gmail | `your-email@gmail.com` |
| `MAIL_PASSWORD` | App Password Gmail | `xxxx xxxx xxxx xxxx` |
| `GOOGLE_CLIENT_ID` | OAuth2 Google Client ID | `xxx.apps.googleusercontent.com` |
| `GOOGLE_CLIENT_SECRET` | OAuth2 Google Secret | `GOCSPX-xxxxx` |
| `RECAPTCHA_SECRET` | reCAPTCHA Secret Key | `6Lxxxxxxxxxxxxxx` |
| `STAGING_SSH_KEY` | Clé SSH pour staging | Contenu de `~/.ssh/id_rsa` |
| `STAGING_HOST` | IP serveur staging | `192.168.1.100` |
| `STAGING_USER` | User SSH staging | `ubuntu` |
| `PRODUCTION_SSH_KEY` | Clé SSH pour production | Contenu de `~/.ssh/id_rsa` |
| `PRODUCTION_HOST` | IP serveur production | `prod.englishflow.com` |
| `PRODUCTION_USER` | User SSH production | `ubuntu` |
| `SLACK_WEBHOOK_URL` | Webhook Slack (optionnel) | `https://hooks.slack.com/...` |

### Générer un JWT Secret sécurisé

```bash
openssl rand -base64 32
```

### Configurer Gmail App Password

1. Allez sur [myaccount.google.com/security](https://myaccount.google.com/security)
2. Activez la **vérification en 2 étapes**
3. Allez dans **Mots de passe des applications**
4. Générez un mot de passe pour "Mail"
5. Utilisez ce mot de passe (format : `xxxx xxxx xxxx xxxx`)

---

## 🐳 Configuration Docker

### Étape 1 : Copier les fichiers d'environnement

```bash
# Racine du projet
cp .env.example .env

# Backend services
for service in auth-service courses-service community-service messaging-service club-service event-service learning-service complaints-service gamification-service exam-service payment-service sponsors-service eureka-server api-gateway config-server; do
  cp backend/$service/.env.example backend/$service/.env
done

# Frontend
cp frontend/.env.example frontend/.env

# WebRTC
cp backend/webrtc-signaling/.env.example backend/webrtc-signaling/.env
```

### Étape 2 : Éditer les fichiers .env

Éditez chaque fichier `.env` avec vos valeurs réelles.

**Important** : Le fichier `.env` à la racine est utilisé par `docker-compose.yml`

### Étape 3 : Lancer l'environnement de développement

```bash
# Avec le Makefile
make dev

# Ou directement avec docker-compose
cd devops/docker
docker-compose up -d
```

### Étape 4 : Vérifier les services

```bash
# Voir les logs
make logs

# Vérifier le statut
docker-compose ps

# Accéder aux services
# - Eureka: http://localhost:8761
# - API Gateway: http://localhost:8080
# - Frontend: http://localhost:4200
# - Grafana: http://localhost:3000
# - Prometheus: http://localhost:9090
```

---

## ☸️ Configuration Kubernetes

### Étape 1 : Créer le namespace

```bash
kubectl apply -f devops/kubernetes/00-namespace.yaml
```

### Étape 2 : Configurer les secrets

```bash
# Créer les secrets Kubernetes
kubectl create secret generic db-credentials \
  --from-literal=username=postgres \
  --from-literal=password=postgres123 \
  -n englishflow

kubectl create secret generic jwt-secret \
  --from-literal=secret=$(openssl rand -base64 32) \
  -n englishflow

kubectl create secret generic mail-credentials \
  --from-literal=username=your-email@gmail.com \
  --from-literal=password=your-app-password \
  -n englishflow
```

### Étape 3 : Déployer l'infrastructure

```bash
cd devops/kubernetes

# Déployer dans l'ordre
kubectl apply -f 01-configmaps/
kubectl apply -f 02-secrets/
kubectl apply -f 03-storage/
kubectl apply -f 04-databases/
kubectl apply -f 05-infrastructure/
kubectl apply -f 06-microservices/
kubectl apply -f 07-frontend/
kubectl apply -f 08-ingress/
kubectl apply -f 09-monitoring/
kubectl apply -f 10-autoscaling/
```

### Étape 4 : Vérifier le déploiement

```bash
# Voir tous les pods
kubectl get pods -n englishflow

# Voir les services
kubectl get svc -n englishflow

# Voir les logs d'un service
kubectl logs -f deployment/auth-service -n englishflow
```

---

## 📊 Monitoring

### Accès aux Dashboards

- **Prometheus** : http://localhost:9090
- **Grafana** : http://localhost:3000
  - Username : `admin`
  - Password : `admin`

### Dashboards Grafana Pré-configurés

1. **EnglishFlow Overview** : Vue d'ensemble de tous les services
2. **JVM Metrics** : Métriques Java (heap, threads, GC)
3. **Database Metrics** : Connexions PostgreSQL, queries
4. **API Gateway** : Requêtes, latence, erreurs
5. **Redis Metrics** : Cache hit/miss, mémoire

### Alertes Prometheus

Les alertes sont configurées dans `devops/monitoring/alert-rules.yml` :

- Service Down (> 5 min)
- High CPU Usage (> 80%)
- High Memory Usage (> 85%)
- High Error Rate (> 5%)
- Database Connection Pool Exhausted

---

## 🚀 Déploiement

### Déploiement Automatique (CI/CD)

Le déploiement est automatique via GitHub Actions :

1. **Push sur `main`** → Build + Tests + Deploy Staging
2. **Tag `v*`** → Build + Tests + Deploy Production

### Déploiement Manuel

#### Staging

```bash
# Via Makefile
make staging

# Ou via script
./devops/scripts/deploy.sh staging
```

#### Production

```bash
# Via Makefile
make prod

# Ou via script
./devops/scripts/deploy.sh prod
```

### Rollback

```bash
# Rollback automatique en cas d'échec du health check
# Rollback manuel
ssh user@production-server
cd /opt/englishflow
docker-compose down
docker-compose up -d
```

---

## 🧪 Tests

### Tests Unitaires

```bash
# Backend (tous les services)
cd backend
for service in */; do
  cd $service
  mvn test
  cd ..
done

# Frontend
cd frontend
npm test
```

### Tests d'Intégration

```bash
# Avec Docker Compose
docker-compose -f docker-compose.test.yml up --abort-on-container-exit
```

### Couverture de Code

```bash
# Backend (avec JaCoCo)
cd backend/auth-service
mvn clean test jacoco:report
open target/site/jacoco/index.html

# Frontend (avec Karma)
cd frontend
npm run test -- --code-coverage
open coverage/index.html
```

---

## 📝 Checklist de Déploiement

### Avant le Premier Déploiement

- [ ] Tous les fichiers `.env` sont configurés
- [ ] Les secrets GitHub sont ajoutés
- [ ] SonarCloud est configuré
- [ ] Les bases de données sont créées
- [ ] Les certificats SSL sont configurés (production)
- [ ] Les DNS pointent vers les serveurs
- [ ] Les backups automatiques sont configurés

### Avant Chaque Déploiement

- [ ] Les tests passent localement
- [ ] Le pipeline CI est vert
- [ ] SonarCloud Quality Gate est OK
- [ ] Les migrations de base de données sont prêtes
- [ ] Un backup récent existe
- [ ] L'équipe est notifiée

### Après le Déploiement

- [ ] Health checks sont OK
- [ ] Les logs ne montrent pas d'erreurs
- [ ] Les métriques sont normales
- [ ] Les fonctionnalités critiques fonctionnent
- [ ] L'équipe est notifiée du succès

---

## 🆘 Dépannage

### Les services ne démarrent pas

```bash
# Vérifier les logs
docker-compose logs service-name

# Vérifier la connectivité réseau
docker network inspect englishflow-network

# Redémarrer un service
docker-compose restart service-name
```

### Problèmes de base de données

```bash
# Se connecter à PostgreSQL
docker-compose exec postgres psql -U postgres

# Lister les bases de données
\l

# Vérifier les connexions
SELECT * FROM pg_stat_activity;
```

### Problèmes de mémoire

```bash
# Voir l'utilisation mémoire
docker stats

# Augmenter la mémoire pour un service (docker-compose.yml)
services:
  auth-service:
    deploy:
      resources:
        limits:
          memory: 1G
```

---

## 📚 Ressources

- [Documentation Spring Boot](https://spring.io/projects/spring-boot)
- [Documentation Docker](https://docs.docker.com/)
- [Documentation Kubernetes](https://kubernetes.io/docs/)
- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)

---

## 👥 Support

Pour toute question ou problème :
- Créez une issue sur GitHub
- Contactez l'équipe DevOps
- Consultez la documentation interne

---

**Dernière mise à jour** : Avril 2026
**Version** : 1.0.0
