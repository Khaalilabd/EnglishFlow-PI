#!/bin/bash

# Script pour arrêter tous les microservices

echo "=========================================="
echo "Stopping Jungle in English Microservices"
echo "=========================================="
echo ""

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Fonction pour arrêter un service sur un port
stop_service() {
    local port=$1
    local service_name=$2
    
    echo -e "${YELLOW}Stopping $service_name on port $port...${NC}"
    
    # Trouver le PID du processus utilisant le port
    PID=$(lsof -ti :$port)
    
    if [ -z "$PID" ]; then
        echo -e "${YELLOW}No process found on port $port${NC}"
    else
        kill -9 $PID
        echo -e "${GREEN}✓ $service_name stopped (PID: $PID)${NC}"
    fi
}

# Arrêter les services dans l'ordre inverse
stop_service 8081 "Auth Service"
stop_service 8761 "Eureka Server"
stop_service 8888 "Config Server"

echo ""
echo "=========================================="
echo -e "${GREEN}All services stopped!${NC}"
echo "=========================================="
