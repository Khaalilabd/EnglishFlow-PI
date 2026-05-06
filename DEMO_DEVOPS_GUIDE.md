# 🚀 Guide Complet - Démo DevOps EnglishFlow

## 📋 Table des Matières
1. [Docker Compose - Environnement Local](#1-docker-compose---environnement-local)
2. [Monitoring avec Prometheus & Grafana](#2-monitoring-avec-prometheus--grafana)
3. [SonarCloud - Analyse de Code](#3-sonarcloud---analyse-de-code)
4. [DockerHub - Images](#4-dockerhub---images)
5. [Kubernetes avec kubeadm](#5-kubernetes-avec-kubeadm)

---

## 1. Docker Compose - Environnement Local

### Étape 1.1: Démarrer tous les services
```bash
cd EnglishFlow-PI/devops/docker
docker-compose up -d
```

### Étape 1.2: Vérifier que tous les services sont UP
```bash
docker-compose ps
```

**Résultat attendu**: Tous les services doivent être "Up" (environ 20+ conteneurs)

### Étape 1.3: Accéder aux services
- **Frontend**: http://localhost:4200
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Jaeger**: http://localhost:16686

---

## 2. Monitoring avec Prometheus & Grafana

### Étape 2.1: Vérifier Prometheus
```bash
# Ouvrir le navigateur
http://localhost:9090

# Aller dans Status → Targets
# Vérifier que 15/15 services sont UP
```

### Étape 2.2: Configurer Grafana

#### A. Se connecter à Grafana
```
URL: http://localhost:3000
Username: admin
Password: admin
```

#### B. Ajouter Prometheus comme Data Source
1. Aller dans **Configuration** (⚙️) → **Data Sources**
2. Cliquer **Add data source**
3. Sélectionner **Prometheus**
4. URL: `http://prometheus:9090`
5. Cliquer **Save & Test**

#### C. Importer les dashboards personnalisés

**Dashboard 1: EnglishFlow Basic**
1. Aller dans **Dashboards** (+) → **Import**
2. Cliquer **Upload JSON file**
3. Sélectionner: `EnglishFlow-PI/devops/monitoring/grafana-dashboard-englishflow.json`
4. Sélectionner Prometheus comme data source
5. Cliquer **Import**

**Dashboard 2: EnglishFlow Advanced**
1. Répéter les mêmes étapes
2. Fichier: `EnglishFlow-PI/devops/monitoring/grafana-dashboard-advanced.json`

**Dashboard 3: Spring Boot Statistics (optionnel)**
1. Aller dans **Import**
2. Entrer l'ID: `19004`
3. Cliquer **Load**
4. Sélectionner Prometheus
5. Cliquer **Import**

### Étape 2.3: Démontrer les métriques en temps réel
- **Services Status**: Voir les 15 microservices actifs
- **JVM Memory**: Utilisation mémoire de chaque service
- **HTTP Requests**: Nombre de requêtes par seconde
- **CPU Usage**: Utilisation CPU
- **Database Connections**: Connexions actives

---

## 3. SonarCloud - Analyse de Code

### Étape 3.1: Accéder à SonarCloud
```
URL: https://sonarcloud.io/organizations/khalilab/projects
```

### Étape 3.2: Montrer la structure multi-module
- Cliquer sur le projet **EnglishFlow-PI**
- Aller dans **Code** → Voir les 15 modules:
  - 13 microservices backend
  - 1 eureka-server
  - 1 frontend Angular

### Étape 3.3: Montrer les métriques de qualité
- **Bugs**: Nombre de bugs détectés
- **Vulnerabilities**: Vulnérabilités de sécurité
- **Code Smells**: Problèmes de qualité de code
- **Coverage**: Couverture de tests
- **Duplications**: Code dupliqué

### Étape 3.4: Montrer le workflow GitHub Actions
```bash
# Ouvrir GitHub
https://github.com/VOTRE_USERNAME/EnglishFlow-PI/actions

# Montrer le workflow "Backend CI"
# Montrer les étapes:
# - Build & Test
# - SonarCloud Analysis
# - Coverage Report
```

---

## 4. DockerHub - Images

### Étape 4.1: Accéder à DockerHub
```
URL: https://hub.docker.com/u/khalilab
```

### Étape 4.2: Montrer les 17 images
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

### Étape 4.3: Montrer la traçabilité
- Cliquer sur une image
- Montrer les tags (latest)
- Montrer la date de push
- Montrer la taille de l'image

---

## 5. Kubernetes avec kubeadm

### Étape 5.1: Se connecter à la VM Ubuntu
```bash
# IP de la VM: 192.168.195.131
# Username: khalil
# Password: [votre mot de passe]
```

### Étape 5.2: Vérifier le cluster Kubernetes
```bash
# Vérifier le node
kubectl get nodes

# Vérifier les namespaces
kubectl get namespaces

# Vérifier les pods
kubectl get pods -n englishflow
```

### Étape 5.3: Vérifier les services
```bash
# Lister tous les services
kubectl get svc -n englishflow

# Vérifier les services exposés (NodePort)
kubectl get svc -n englishflow | grep NodePort
```

### Étape 5.4: Exposer les services (si pas déjà fait)
```bash
# Frontend (port 30420)
kubectl patch svc frontend-service -n englishflow -p '{"spec":{"type":"NodePort","ports":[{"port":80,"targetPort":80,"nodePort":30420}]}}'

# API Gateway (port 30080)
kubectl patch svc api-gateway-service -n englishflow -p '{"spec":{"type":"NodePort","ports":[{"port":8080,"targetPort":8080,"nodePort":30080}]}}'

# Eureka (port 30761)
kubectl patch svc eureka-service -n englishflow -p '{"spec":{"type":"NodePort","ports":[{"port":8761,"targetPort":8761,"nodePort":30761}]}}'
```

### Étape 5.5: Accéder aux services depuis Windows
```
Frontend: http://192.168.195.131:30420
API Gateway: http://192.168.195.131:30080
Eureka: http://192.168.195.131:30761
```

### Étape 5.6: Montrer les déploiements
```bash
# Lister les déploiements
kubectl get deployments -n englishflow

# Montrer les détails d'un déploiement
kubectl describe deployment api-gateway -n englishflow

# Montrer les replicas
kubectl get pods -n englishflow -l app=api-gateway
```

### Étape 5.7: Montrer le scaling
```bash
# Scaler un service
kubectl scale deployment api-gateway -n englishflow --replicas=3

# Vérifier
kubectl get pods -n englishflow -l app=api-gateway

# Revenir à 1 replica
kubectl scale deployment api-gateway -n englishflow --replicas=1
```

### Étape 5.8: Montrer les logs
```bash
# Logs d'un pod
kubectl logs -f <nom-du-pod> -n englishflow

# Exemple
kubectl logs -f api-gateway-fb47c56c4-2knqk -n englishflow
```

### Étape 5.9: Montrer les ressources
```bash
# Utilisation des ressources
kubectl top nodes
kubectl top pods -n englishflow
```

---

## 📊 Checklist pour la Démo

### Avant la démo:
- [ ] VM Ubuntu démarrée (IP: 192.168.195.131)
- [ ] Docker Compose UP (`docker-compose ps`)
- [ ] Tous les services Docker sont "Up"
- [ ] Kubernetes cluster UP (`kubectl get nodes`)
- [ ] Tous les pods Kubernetes sont "Running" (`kubectl get pods -n englishflow`)
- [ ] Services exposés via NodePort
- [ ] Grafana configuré avec dashboards
- [ ] SonarCloud accessible
- [ ] DockerHub accessible

### Pendant la démo:
1. **Montrer Docker Compose** (5 min)
   - Démarrer les services
   - Montrer Eureka Dashboard
   - Montrer Prometheus Targets

2. **Montrer Grafana** (5 min)
   - Dashboard Basic: Services, Memory, CPU
   - Dashboard Advanced: Cost estimation, Top endpoints
   - Métriques en temps réel

3. **Montrer SonarCloud** (3 min)
   - Structure multi-module
   - Métriques de qualité
   - GitHub Actions workflow

4. **Montrer DockerHub** (2 min)
   - 17 images publiées
   - Traçabilité

5. **Montrer Kubernetes** (10 min)
   - Cluster kubeadm
   - Pods et services
   - Accès via NodePort
   - Scaling
   - Logs

---

## 🔧 Commandes de Dépannage

### Docker Compose
```bash
# Redémarrer tous les services
docker-compose restart

# Voir les logs d'un service
docker-compose logs -f api-gateway

# Reconstruire et redémarrer
docker-compose up -d --build
```

### Kubernetes
```bash
# Redémarrer un pod
kubectl delete pod <nom-du-pod> -n englishflow

# Vérifier les événements
kubectl get events -n englishflow --sort-by='.lastTimestamp'

# Décrire un pod problématique
kubectl describe pod <nom-du-pod> -n englishflow
```

---

## 📝 Notes Importantes

1. **Docker Compose** est pour le développement local
2. **Kubernetes** est pour la production (démo avec kubeadm)
3. **Prometheus + Grafana** pour le monitoring en temps réel
4. **SonarCloud** pour la qualité de code et collaboration
5. **DockerHub** pour la traçabilité des images

**Temps total de la démo**: ~25 minutes
