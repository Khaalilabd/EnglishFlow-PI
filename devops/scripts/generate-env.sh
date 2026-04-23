#!/bin/bash

# Script to generate .env files from .env.example
# Usage: ./generate-env.sh

set -e

echo "🔧 Generating .env files from .env.example..."
echo ""

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

copy_env_file() {
    local source=$1
    local target=$2
    
    if [ -f "$source" ]; then
        if [ -f "$target" ]; then
            echo -e "${YELLOW}⚠️  $target already exists, skipping...${NC}"
        else
            cp "$source" "$target"
            echo -e "${GREEN}✓${NC} Created $target"
        fi
    else
        echo -e "${YELLOW}⚠️  $source not found, skipping...${NC}"
    fi
}

# Root .env
copy_env_file ".env.example" ".env"

# Backend services
BACKEND_SERVICES=(
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
    "eureka-server"
    "api-gateway"
    "config-server"
    "webrtc-signaling"
)

for service in "${BACKEND_SERVICES[@]}"; do
    copy_env_file "backend/$service/.env.example" "backend/$service/.env"
done

# Frontend
copy_env_file "frontend/.env.example" "frontend/.env"

echo ""
echo -e "${GREEN}✅ Done!${NC}"
echo ""
echo "⚠️  Important: Edit the .env files with your actual values!"
echo ""
echo "Required values to configure:"
echo "  - JWT_SECRET (generate with: openssl rand -base64 32)"
echo "  - MAIL_USERNAME and MAIL_PASSWORD (Gmail App Password)"
echo "  - GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET"
echo "  - RECAPTCHA_SECRET"
echo "  - Database passwords"
echo ""
