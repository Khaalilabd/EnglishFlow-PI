#!/bin/bash

echo "=========================================="
echo "Démarrage de tous les services EnglishFlow"
echo "=========================================="
echo ""

# Fonction pour attendre qu'un service soit prêt
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=0
    
    echo "⏳ Attente du démarrage de $service_name..."
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -s "$url" > /dev/null 2>&1; then
            echo "✅ $service_name est prêt!"
            return 0
        fi
        attempt=$((attempt + 1))
        sleep 2
        echo -n "."
    done
    
    echo ""
    echo "⚠️  $service_name n'a pas démarré dans le temps imparti"
    return 1
}

# 1. Démarrer Eureka Server
echo ""
echo "1️⃣  Démarrage d'Eureka Server (Port 8761)..."
cd backend/eureka-server
mvn spring-boot:run > /dev/null 2>&1 &
EUREKA_PID=$!
cd ../..

wait_for_service "http://localhost:8761" "Eureka Server"

# 2. Démarrer Auth Service
echo ""
echo "2️⃣  Démarrage d'Auth Service (Port 8081)..."
cd backend/auth-service
mvn spring-boot:run > /dev/null 2>&1 &
AUTH_PID=$!
cd ../..

wait_for_service "http://localhost:8081/actuator/health" "Auth Service"

# 3. Démarrer API Gateway
echo ""
echo "3️⃣  Démarrage d'API Gateway (Port 8080)..."
cd backend/api-gateway
mvn spring-boot:run > /dev/null 2>&1 &
GATEWAY_PID=$!
cd ../..

wait_for_service "http://localhost:8080/actuator/health" "API Gateway"

# 4. Démarrer Messaging Service
echo ""
echo "4️⃣  Démarrage de Messaging Service (Port 8084)..."
cd backend/messaging-service
mvn spring-boot:run > /dev/null 2>&1 &
MESSAGING_PID=$!
cd ../..

wait_for_service "http://localhost:8084/actuator/health" "Messaging Service"

# Résumé
echo ""
echo "=========================================="
echo "✅ Tous les services sont démarrés!"
echo "=========================================="
echo ""
echo "Services en cours d'exécution:"
echo "  • Eureka Server:      http://localhost:8761"
echo "  • Auth Service:       http://localhost:8081"
echo "  • API Gateway:        http://localhost:8080"
echo "  • Messaging Service:  http://localhost:8084"
echo ""
echo "PIDs des processus:"
echo "  • Eureka:    $EUREKA_PID"
echo "  • Auth:      $AUTH_PID"
echo "  • Gateway:   $GATEWAY_PID"
echo "  • Messaging: $MESSAGING_PID"
echo ""
echo "Pour arrêter tous les services:"
echo "  kill $EUREKA_PID $AUTH_PID $GATEWAY_PID $MESSAGING_PID"
echo ""
echo "Vous pouvez maintenant démarrer le frontend:"
echo "  cd frontend && npm start"
echo ""
