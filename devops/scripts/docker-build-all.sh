#!/bin/bash

# Script to build all Docker images
# Usage: ./docker-build-all.sh [tag]

set -e

TAG=${1:-latest}
REGISTRY="ghcr.io/khaalilabd/esprit-pidev-4sae1-2026-jungleinenglish"

echo "🐳 Building all Docker images..."
echo "Registry: $REGISTRY"
echo "Tag: $TAG"
echo ""

# Color codes
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

build_service() {
    local service=$1
    local context=$2
    
    echo -e "${BLUE}Building $service...${NC}"
    
    docker build -t "$REGISTRY-$service:$TAG" "$context"
    
    echo -e "${GREEN}✓ $service built successfully${NC}"
    echo ""
}

# Backend services
BACKEND_SERVICES=(
    "eureka-server"
    "api-gateway"
    "config-server"
    "auth-service"
    "courses-service"
    "community-service"
    "messaging-service"
    "club-service"
    "event-service"
    "learning-service"
    "complaints-service"
    "gamification-service"
    "exam-service"
    "payment-service"
    "sponsors-service"
    "webrtc-signaling"
)

for service in "${BACKEND_SERVICES[@]}"; do
    if [[ -f "backend/$service/Dockerfile" ]]; then
        build_service "$service" "backend/$service"
    fi
done

# Frontend
if [[ -f "frontend/Dockerfile" ]]; then
    build_service "frontend" "frontend"
fi

echo ""
echo -e "${GREEN}✅ All images built successfully!${NC}"
echo ""
echo "To push to registry:"
echo "  docker tag englishflow-SERVICE:$TAG your-registry/englishflow-SERVICE:$TAG"
echo "  docker push your-registry/englishflow-SERVICE:$TAG"
echo ""
