# Guide de démarrage des services EnglishFlow

## Ordre de démarrage (IMPORTANT!)

Les services doivent être démarrés dans cet ordre précis:

### 1. Config Server (Port 8888)
```bash
cd config-server
mvn spring-boot:run
```
Attendez le message: "Started ConfigServerApplication"

### 2. Eureka Server (Port 8761)
```bash
cd eureka-server
mvn spring-boot:run
```
Attendez le message: "Started EurekaServerApplication"
Vérifiez: http://localhost:8761

### 3. Services métier (en parallèle)

#### Auth Service (Port 8081)
```bash
cd auth-service
mvn spring-boot:run
```

#### Club Service (Port 8084)
```bash
cd club-service
mvn spring-boot:run
```

#### Learning Service (Port 8083)
```bash
cd learning-service
mvn spring-boot:run
```

Attendez que tous les services s'enregistrent dans Eureka (vérifiez sur http://localhost:8761)

### 4. API Gateway (Port 8080)
```bash
cd api-gateway
mvn spring-boot:run
```
Attendez le message: "Started ApiGatewayApplication"

## Vérification

1. **Eureka Dashboard**: http://localhost:8761
   - Vous devriez voir: AUTH-SERVICE, CLUB-SERVICE, LEARNING-SERVICE

2. **API Gateway**: http://localhost:8080
   - Tous les appels doivent passer par le gateway

3. **Accès aux services via Gateway**:
   - Auth: http://localhost:8080/api/auth/...
   - Club: http://localhost:8080/api/clubs/...
   - Learning: http://localhost:8080/api/learning/...

## Arrêt des services

Arrêtez dans l'ordre inverse:
1. API Gateway
2. Services métier (auth, club, learning)
3. Eureka Server
4. Config Server

## Dépannage

Si un service ne s'enregistre pas dans Eureka:
1. Vérifiez que Eureka est bien démarré
2. Vérifiez les logs du service
3. Redémarrez le service
