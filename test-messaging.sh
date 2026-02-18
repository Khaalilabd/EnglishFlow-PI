#!/bin/bash

# Script de test pour la messagerie EnglishFlow
# Ce script vérifie que tous les services sont opérationnels

echo "🔍 Vérification des services EnglishFlow..."
echo ""

# Couleurs
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Fonction pour vérifier un service
check_service() {
    local name=$1
    local url=$2
    local expected=$3
    
    echo -n "Vérification de $name... "
    
    response=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>/dev/null)
    
    if [ "$response" = "$expected" ]; then
        echo -e "${GREEN}✓ OK${NC} (HTTP $response)"
        return 0
    else
        echo -e "${RED}✗ ERREUR${NC} (HTTP $response, attendu $expected)"
        return 1
    fi
}

# Fonction pour vérifier un port
check_port() {
    local name=$1
    local port=$2
    
    echo -n "Vérification du port $port ($name)... "
    
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${GREEN}✓ Ouvert${NC}"
        return 0
    else
        echo -e "${RED}✗ Fermé${NC}"
        return 1
    fi
}

# Vérifier PostgreSQL
echo "📊 Base de données"
echo "=================="
if command -v psql &> /dev/null; then
    if psql -U postgres -lqt 2>/dev/null | cut -d \| -f 1 | grep -qw messaging_db; then
        echo -e "${GREEN}✓${NC} PostgreSQL est démarré"
        echo -e "${GREEN}✓${NC} Base de données 'messaging_db' existe"
    else
        echo -e "${YELLOW}⚠${NC} PostgreSQL est démarré mais 'messaging_db' n'existe pas"
        echo "  Créez-la avec : psql -U postgres -c 'CREATE DATABASE messaging_db;'"
    fi
else
    echo -e "${RED}✗${NC} PostgreSQL n'est pas installé ou pas dans le PATH"
fi
echo ""

# Vérifier les ports
echo "🔌 Ports"
echo "========"
check_port "Eureka Server" 8761
check_port "API Gateway" 8080
check_port "Messaging Service" 8084
check_port "Auth Service" 8081
check_port "Frontend" 4200
echo ""

# Vérifier les services HTTP
echo "🌐 Services HTTP"
echo "================"
check_service "Eureka Server" "http://localhost:8761" "200"
check_service "API Gateway Health" "http://localhost:8080/actuator/health" "200"
echo ""

# Vérifier Eureka Registry
echo "📋 Services enregistrés dans Eureka"
echo "===================================="
if command -v curl &> /dev/null && command -v jq &> /dev/null; then
    services=$(curl -s http://localhost:8761/eureka/apps | grep -o '<name>[^<]*</name>' | sed 's/<name>//;s/<\/name>//' | sort -u)
    
    if [ -n "$services" ]; then
        while IFS= read -r service; do
            if [ "$service" = "MESSAGING-SERVICE" ] || [ "$service" = "API-GATEWAY" ] || [ "$service" = "AUTH-SERVICE" ]; then
                echo -e "${GREEN}✓${NC} $service"
            else
                echo "  $service"
            fi
        done <<< "$services"
    else
        echo -e "${YELLOW}⚠${NC} Aucun service enregistré ou Eureka non accessible"
    fi
else
    echo -e "${YELLOW}⚠${NC} curl ou jq non disponible, impossible de vérifier les services"
fi
echo ""

# Résumé
echo "📝 Résumé"
echo "========="
echo "Si tous les services sont OK (✓), vous pouvez tester la messagerie :"
echo "1. Ouvrir http://localhost:4200"
echo "2. Se connecter"
echo "3. Ouvrir la messagerie"
echo "4. Créer une conversation"
echo "5. Envoyer des messages"
echo ""
echo "Si des services sont en erreur (✗) :"
echo "- Vérifier les logs dans backend/*/logs/"
echo "- Redémarrer les services avec 'mvn spring-boot:run'"
echo "- Consulter CORRECTIONS_MESSAGERIE.md pour plus de détails"
echo ""
