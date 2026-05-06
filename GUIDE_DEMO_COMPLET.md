# 🎯 Guide Complet - Démonstration DevOps EnglishFlow

## 📋 Préparation Avant la Démo

### Prérequis
- ✅ VM Ubuntu démarrée (IP: 192.168.195.131)
- ✅ Docker Desktop démarré sur Windows
- ✅ Navigateur ouvert

---

# PARTIE 1: Docker Compose (Environnement Local)

## 1.1 Démarrer l'environnement Docker

```bash
# Ouvrir PowerShell/CMD sur Windows
cd EnglishFlow-PI/devops/docker

# Démarrer tous les services
docker-compose up -d

# Vérifier que tous les services sont UP
docker-compose ps
```

**Résultat attendu**: ~20 conteneurs en état "Up"

## 1.2 Accéder aux services

Ouvrir dans le navigateur:

### Frontend
```
http://localhost:4200
```
**Montrer**: L'interface de l'application EnglishFlow

### Eureka Dashboard (Service Discovery)
```
http://localhost:8761
```
**Montrer**: Les 13 microservices enregistrés

### Prometheus (Monitoring)
```
http://localhost:9090
```
**Actions**:
1. Cliquer sur **Status** → **Targets**
2. **Montrer**: 15/15 services UP (100%)

### Grafana (Dashboards)
```
http://localhost:3000
Username: admin
Password: admin
```

**Actions**:
1. Aller dans **Dashboards**
2. Ouvrir **EnglishFlow Basic Dashboard**
3. **Montrer**:
   - Services Status (15 services actifs)
   - JVM Memory Usage
   - HTTP Requests per second
   - CPU Usage
   - Database Connections

4. Ouvrir **EnglishFlow Advanced Dashboard**
5. **Montrer**:
   - Cost Estimation
   - Top Endpoints
   - Cache Hit Rate
   - Comprehensive Statistics

### Jaeger (Distributed Tracing)
```
http://localhost:16686
```
**Montrer**: Les traces des requêtes entre microservices

## 1.3 Tester l'application

```bash
# Tester l'API Gateway
curl http://localhost:8080/actuator/health

# Voir les logs d'un service
docker-compose logs -f api-gateway
```

## 1.4 Arrêter Docker Compose (optionnel)

```bash
# Si tu veux montrer Kubernetes seul
docker-compose down
```

---

# PARTIE 2: SonarCloud (Qualité de Code)

## 2.1 Accéder à SonarCloud

```
https://sonarcloud.io/organizations/khalilab/projects
```

## 2.2 Montrer le projet

1. Cliquer sur **EnglishFlow-PI**
2. **Montrer** la page principale avec:
   - Bugs
   - Vulnerabilities
   - Code Smells
   - Coverage
   - Duplications

## 2.3 Montrer la structure multi-module

1. Aller dans **Code**
2. **Montrer** les 15 modules:
   - 13 microservices backend
   - 1 eureka-server
   - 1 frontend Angular

**Expliquer**: "Chaque module représente un microservice développé par un membre de l'équipe, ce qui permet de voir les contributions individuelles"

## 2.4 Montrer le workflow GitHub Actions

```
https://github.com/VOTRE_USERNAME/EnglishFlow-PI/actions
```

**Montrer**:
- Workflow "Backend CI"
- Les étapes: Build, Test, SonarCloud Analysis, Coverage

---

# PARTIE 3: DockerHub (Images)

## 3.1 Accéder à DockerHub

```
https://hub.docker.com/u/khalilab
```

## 3.2 Montrer les images

**Montrer** les 17 images publiées:
- `khalilab/englishflow-api-gateway`
- `khalilab/englishflow-auth-service`
- `khalilab/englishflow-club-service`
- `khalilab/englishflow-community-service`
- `khalilab/englishflow-complaints-service`
- `khalilab/englishflow-courses-service`
- `khalilab/englishflow-event-service`
- `khalilab/englishflow-exam-service`
- `khalilab/englishflow-gamification-service`
- `khalilab/englishflow-learning-service`
- `khalilab/englishflow-messaging-service`
- `khalilab/englishflow-payment-service`
- `khalilab/englishflow-sponsors-service`
- `khalilab/englishflow-eureka-server`
- `khalilab/englishflow-config-server`
- `khalilab/englishflow-frontend`
- `khalilab/englishflow-webrtc-signaling`

**Expliquer**: "Ces images sont utilisées pour le déploiement Kubernetes, assurant la traçabilité et la reproductibilité"

---

# PARTIE 4: Kubernetes avec kubeadm (VM Ubuntu)

## 4.1 Se connecter à la VM

```
IP: 192.168.195.131
Username: khalil
Password: [votre mot de passe]
```

## 4.2 Vérifier le cluster Kubernetes

```bash
# Vérifier le node
kubectl get nodes
```
**Résultat attendu**: 1 node "Ready"

```bash
# Vérifier les namespaces
kubectl get namespaces
```
**Montrer**: Le namespace "englishflow"

## 4.3 Vérifier les pods déployés

```bash
# Lister tous les pods
kubectl get pods -n englishflow
```

**Résultat attendu**:
- api-gateway: 1/1 Running
- auth-service: 1/1 Running
- eureka-server: 1/1 Running
- frontend: 1/1 Running
- redis: 1/1 Running

**Expliquer**: "5 services essentiels déployés pour la démo"

## 4.4 Vérifier les services

```bash
# Lister les services
kubectl get svc -n englishflow
```

**Montrer**: Les services exposés en NodePort:
- frontend-service: NodePort 30420
- api-gateway-service: NodePort 30080
- eureka-service: NodePort 30761

## 4.5 Accéder aux services depuis Windows

Ouvrir dans le navigateur Windows:

### Frontend Kubernetes
```
http://192.168.195.131:30420
```
**Montrer**: L'application fonctionne via Kubernetes

### API Gateway Kubernetes
```
http://192.168.195.131:30080
```
**Tester**:
```bash
curl http://192.168.195.131:30080/actuator/health
```

### Eureka Dashboard Kubernetes
```
http://192.168.195.131:30761
```
**Montrer**: Les services enregistrés dans Eureka

## 4.6 Montrer les déploiements

```bash
# Lister les déploiements
kubectl get deployments -n englishflow
```

**Montrer**: Les déploiements avec leurs replicas

```bash
# Voir les détails d'un déploiement
kubectl describe deployment api-gateway -n englishflow
```

**Montrer**: La configuration du déploiement (image, replicas, ressources)

## 4.7 Démontrer le scaling

```bash
# Scaler le frontend à 2 replicas
kubectl scale deployment frontend -n englishflow --replicas=2

# Vérifier
kubectl get pods -n englishflow | grep frontend
```

**Montrer**: 2 pods frontend en cours d'exécution

```bash
# Revenir à 1 replica
kubectl scale deployment frontend -n englishflow --replicas=1
```

## 4.8 Montrer les logs

```bash
# Voir les logs de l'API Gateway
kubectl logs -f $(kubectl get pods -n englishflow | grep api-gateway | awk '{print $1}') -n englishflow --tail=50
```

**Montrer**: Les logs en temps réel (Ctrl+C pour arrêter)

## 4.9 Montrer le storage

```bash
# Voir les PersistentVolumes
kubectl get pv

# Voir les PersistentVolumeClaims
kubectl get pvc -n englishflow
```

**Expliquer**: "Utilisation de volumes persistants pour les bases de données"

## 4.10 Montrer les ressources

```bash
# Utilisation des ressources du node
kubectl top node

# Utilisation des ressources des pods
kubectl top pods -n englishflow
```

**Montrer**: L'utilisation CPU et mémoire en temps réel

---

# 📊 Points Clés à Mentionner

## Architecture
- **Microservices**: 13 services backend indépendants
- **Service Discovery**: Eureka pour l'enregistrement automatique
- **API Gateway**: Point d'entrée unique avec routing
- **Monitoring**: Prometheus + Grafana pour métriques temps réel
- **Tracing**: Jaeger pour traçabilité des requêtes
- **Logging**: Loki pour centralisation des logs

## DevOps
- **CI/CD**: GitHub Actions pour build, test, analyse
- **Quality**: SonarCloud pour qualité de code multi-module
- **Containerization**: Docker pour isolation et portabilité
- **Registry**: DockerHub pour traçabilité des images
- **Orchestration**: Kubernetes avec kubeadm pour production

## Kubernetes
- **Cluster**: kubeadm sur VM Ubuntu (production-like)
- **Namespace**: Isolation des ressources
- **Deployments**: Gestion des replicas et rolling updates
- **Services**: Exposition via NodePort
- **Storage**: PersistentVolumes pour données persistantes
- **Scaling**: Horizontal scaling des pods

---

# 🔧 Commandes de Dépannage (Si Besoin)

## Docker Compose

```bash
# Redémarrer un service
docker-compose restart api-gateway

# Voir les logs
docker-compose logs -f api-gateway

# Reconstruire
docker-compose up -d --build
```

## Kubernetes

```bash
# Redémarrer un pod
kubectl delete pod <nom-du-pod> -n englishflow

# Voir les événements
kubectl get events -n englishflow --sort-by='.lastTimestamp' | tail -20

# Décrire un pod
kubectl describe pod <nom-du-pod> -n englishflow

# Vérifier la configuration
kubectl get configmap -n englishflow
kubectl get secret -n englishflow
```

---

# ⏱️ Timing de la Démo (25 minutes)

1. **Docker Compose** (8 min)
   - Démarrage: 2 min
   - Eureka + Prometheus: 2 min
   - Grafana dashboards: 4 min

2. **SonarCloud** (3 min)
   - Structure multi-module: 2 min
   - Métriques qualité: 1 min

3. **DockerHub** (2 min)
   - Images publiées: 1 min
   - Traçabilité: 1 min

4. **Kubernetes** (12 min)
   - Cluster + Pods: 3 min
   - Services exposés: 2 min
   - Accès via IP: 3 min
   - Scaling + Logs: 2 min
   - Storage + Ressources: 2 min

---

# ✅ Checklist Finale

Avant la démo:
- [ ] VM Ubuntu démarrée
- [ ] Docker Desktop démarré
- [ ] Tous les services Docker UP
- [ ] Tous les pods Kubernetes Running
- [ ] Services exposés via NodePort
- [ ] Navigateur avec onglets préparés
- [ ] SonarCloud accessible
- [ ] DockerHub accessible

Pendant la démo:
- [ ] Montrer Docker Compose local
- [ ] Montrer Grafana dashboards
- [ ] Montrer SonarCloud multi-module
- [ ] Montrer DockerHub images
- [ ] Montrer Kubernetes cluster
- [ ] Montrer accès via IP VM
- [ ] Montrer scaling
- [ ] Montrer logs et monitoring

---

**Bonne chance pour ta démo! 🚀**
