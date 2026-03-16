#!/bin/bash

# EnglishFlow Logs Script
# Usage: ./scripts/logs.sh [service] [lines]

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SERVICE=${1:-}
LINES=${2:-100}

cd "$PROJECT_DIR"

if [ -z "$SERVICE" ]; then
    echo "Following all services logs..."
    docker-compose logs -f --tail=$LINES
else
    echo "Following $SERVICE logs..."
    docker-compose logs -f --tail=$LINES $SERVICE
fi
