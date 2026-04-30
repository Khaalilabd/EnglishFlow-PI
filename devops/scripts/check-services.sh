#!/bin/bash

# Script to check health of all services
# Usage: ./check-services.sh [environment]

set -e

ENVIRONMENT=${1:-dev}
BASE_URL="http://localhost"

if [[ "$ENVIRONMENT" = "prod" ]]; then
    BASE_URL="https://englishflow.com"
elif [[ "$ENVIRONMENT" = "staging" ]]; then
    BASE_URL="https://staging.englishflow.com"
fi

echo "🔍 Checking EnglishFlow Services Health..."
echo "Environment: $ENVIRONMENT"
echo "Base URL: $BASE_URL"
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Services to check
declare -A SERVICES=(
    ["Eureka Server"]="8761"
    ["API Gateway"]="8080"
    ["Auth Service"]="8081"
    ["Courses Service"]="8082"
    ["Learning Service"]="8083"
    ["Messaging Service"]="8084"
    ["Event Service"]="8085"
    ["Club Service"]="8086"
    ["Community Service"]="8087"
    ["Gamification Service"]="8088"
    ["Payment Service"]="8089"
    ["Complaints Service"]="8090"
    ["Exam Service"]="8091"
    ["Sponsors Service"]="8092"
    ["WebRTC Signaling"]="3001"
    ["Frontend"]="4200"
    ["Prometheus"]="9090"
    ["Grafana"]="3000"
)

HEALTHY=0
UNHEALTHY=0
TOTAL=0

check_service() {
    local name=$1
    local port=$2
    local url="$BASE_URL:$port/actuator/health"
    
    # Special cases
    if [[ "$name" = "Frontend" ]]; then
        url="$BASE_URL:$port"
    elif [[ "$name" = "WebRTC Signaling" ]]; then
        url="$BASE_URL:$port/health"
    elif [[ "$name" = "Prometheus" ]] || [[ "$name" = "Grafana" ]]; then
        url="$BASE_URL:$port/-/healthy"
    fi
    
    TOTAL=$((TOTAL + 1))
    
    # Try to reach the service
    if curl -sf "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} $name (port $port) - ${GREEN}HEALTHY${NC}"
        HEALTHY=$((HEALTHY + 1))
    else
        echo -e "${RED}✗${NC} $name (port $port) - ${RED}UNHEALTHY${NC}"
        UNHEALTHY=$((UNHEALTHY + 1))
    fi
}

# Check all services
for service in "${!SERVICES[@]}"; do
    check_service "$service" "${SERVICES[$service]}"
done

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Summary:"
echo "  Total Services: $TOTAL"
echo -e "  ${GREEN}Healthy: $HEALTHY${NC}"
echo -e "  ${RED}Unhealthy: $UNHEALTHY${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

if [[ $UNHEALTHY -gt 0 ]]; then
    echo ""
    echo -e "${YELLOW}⚠️  Some services are unhealthy!${NC}"
    exit 1
else
    echo ""
    echo -e "${GREEN}✅ All services are healthy!${NC}"
    exit 0
fi
