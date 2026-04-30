#!/bin/bash

# EnglishFlow Stop Script
# Usage: ./scripts/stop.sh [environment]

set -e

GREEN='\033[0;32m'
NC='\033[0m'

ENVIRONMENT=${1:-dev}
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE_FILES="-f docker-compose.yml"

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

if [[ "$ENVIRONMENT" = "prod" ]]; then
    COMPOSE_FILES="$COMPOSE_FILES -f docker-compose.prod.yml"
fi

log_info "Stopping EnglishFlow services..."

cd "$PROJECT_DIR"
docker-compose $COMPOSE_FILES down

log_info "All services stopped"
