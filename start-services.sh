#!/bin/bash

# Script pour lancer les microservices nécessaires pour l'authentification
# Microservices requis: Config Server, Eureka Server, Auth Service

echo "=========================================="
echo "Starting Jungle in English Microservices"
echo "=========================================="
echo ""

# Forcer l'utilisation de Java 17
export JAVA_HOME=/Users/apple/Library/Java/JavaVirtualMachines/jbr-17.0.12/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

echo "Using Java: $JAVA_HOME"
java -version
echo ""

# Couleurs pour les messages
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Fonction pour vérifier si un port est utilisé
check_port() {
    lsof -i :$1 > /dev/null 2>&1
    return $?
}

# Fonction pour attendre qu'un service soit prêt
wait_for_service() {
    local port=$1
    local service_name=$2
    local max_attempts=30
    local attempt=0
    
    echo -e "${YELLOW}Waiting for $service_name to start on port $port...${NC}"
    
    while [ $attempt -lt $max_attempts ]; do
        if check_port $port; then
            echo -e "${GREEN}✓ $service_name is ready!${NC}"
            return 0
        fi
        attempt=$((attempt + 1))
        sleep 2
        echo -n "."
    done
    
    echo -e "${RED}✗ $service_name failed to start${NC}"
    return 1
}

# 1. Démarrer Config Server (Port 8888)
echo -e "${BLUE}[1/3] Starting Config Server...${NC}"
if check_port 8888; then
    echo -e "${YELLOW}Config Server is already running on port 8888${NC}"
else
    cd backend/config-server
    mvn spring-boot:run > ../../logs/config-server.log 2>&1 &
    CONFIG_PID=$!
    echo "Config Server PID: $CONFIG_PID"
    cd ../..
    wait_for_service 8888 "Config Server"
fi
echo ""

# 2. Démarrer Eureka Server (Port 8761)
echo -e "${BLUE}[2/3] Starting Eureka Server...${NC}"
if check_port 8761; then
    echo -e "${YELLOW}Eureka Server is already running on port 8761${NC}"
else
    cd backend/eureka-server
    mvn spring-boot:run > ../../logs/eureka-server.log 2>&1 &
    EUREKA_PID=$!
    echo "Eureka Server PID: $EUREKA_PID"
    cd ../..
    wait_for_service 8761 "Eureka Server"
fi
echo ""

# 3. Démarrer Auth Service (Port 8081)
echo -e "${BLUE}[3/3] Starting Auth Service...${NC}"
if check_port 8081; then
    echo -e "${YELLOW}Auth Service is already running on port 8081${NC}"
else
    cd backend/auth-service
    mvn spring-boot:run > ../../logs/auth-service.log 2>&1 &
    AUTH_PID=$!
    echo "Auth Service PID: $AUTH_PID"
    cd ../..
    wait_for_service 8081 "Auth Service"
fi
echo ""

echo "=========================================="
echo -e "${GREEN}All services are running!${NC}"
echo "=========================================="
echo ""
echo "Service URLs:"
echo "  - Config Server:  http://localhost:8888"
echo "  - Eureka Server:  http://localhost:8761"
echo "  - Auth Service:   http://localhost:8081"
echo ""
echo "Logs are available in the 'logs' directory"
echo ""
echo "To stop all services, run: ./stop-services.sh"
echo "=========================================="
