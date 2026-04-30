#!/bin/bash

# EnglishFlow DevOps Initialization Script
# This script helps you set up the DevOps environment

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

check_command() {
    if command -v $1 &> /dev/null; then
        print_success "$1 is installed"
        return 0
    else
        print_error "$1 is not installed"
        return 1
    fi
}

# Main script
print_header "EnglishFlow DevOps Setup"

echo ""
print_header "Step 1: Checking Prerequisites"

# Check required tools
MISSING_TOOLS=0

check_command "docker" || MISSING_TOOLS=$((MISSING_TOOLS + 1))
check_command "docker-compose" || MISSING_TOOLS=$((MISSING_TOOLS + 1))
check_command "java" || MISSING_TOOLS=$((MISSING_TOOLS + 1))
check_command "mvn" || MISSING_TOOLS=$((MISSING_TOOLS + 1))
check_command "node" || MISSING_TOOLS=$((MISSING_TOOLS + 1))
check_command "npm" || MISSING_TOOLS=$((MISSING_TOOLS + 1))
check_command "git" || MISSING_TOOLS=$((MISSING_TOOLS + 1))

if [[ $MISSING_TOOLS -gt 0 ]]; then
    print_error "$MISSING_TOOLS required tool(s) missing. Please install them first."
    exit 1
fi

echo ""
print_header "Step 2: Setting up Environment Files"

# Copy .env.example files
if [[ ! -f ".env" ]]; then
    cp .env.example .env
    print_success "Created .env file"
else
    print_warning ".env file already exists, skipping"
fi

# Backend services
SERVICES=(
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

for service in "${SERVICES[@]}"; do
    if [[ -f "backend/$service/.env.example" ]]; then
        if [[ ! -f "backend/$service/.env" ]]; then
            cp "backend/$service/.env.example" "backend/$service/.env"
            print_success "Created backend/$service/.env"
        else
            print_warning "backend/$service/.env already exists, skipping"
        fi
    fi
done

# Frontend
if [[ ! -f "frontend/.env" ]]; then
    cp frontend/.env.example frontend/.env
    print_success "Created frontend/.env"
else
    print_warning "frontend/.env already exists, skipping"
fi

echo ""
print_header "Step 3: Generating Secure Secrets"

# Generate JWT secret
JWT_SECRET=$(openssl rand -base64 32)
print_success "Generated JWT_SECRET: $JWT_SECRET"

# Update .env file
if grep -q "JWT_SECRET=your-secure-jwt-secret" .env; then
    sed -i.bak "s|JWT_SECRET=your-secure-jwt-secret.*|JWT_SECRET=$JWT_SECRET|g" .env
    print_success "Updated JWT_SECRET in .env"
fi

echo ""
print_header "Step 4: Creating Required Directories"

# Create upload directories
mkdir -p backend/auth-service/uploads/profile-photos
mkdir -p backend/auth-service/uploads/applications
mkdir -p backend/courses-service/uploads
mkdir -p backend/messaging-service/uploads/group-photos
mkdir -p backend/community-service/uploads
mkdir -p backend/learning-service/uploads

print_success "Created upload directories"

# Create log directories
mkdir -p backend/auth-service/logs
mkdir -p backend/club-service/logs
mkdir -p backend/community-service/logs

print_success "Created log directories"

echo ""
print_header "Step 5: Docker Setup"

# Check if Docker is running
if docker info &> /dev/null; then
    print_success "Docker is running"
else
    print_error "Docker is not running. Please start Docker first."
    exit 1
fi

# Pull base images
echo "Pulling base Docker images..."
docker pull postgres:15-alpine
docker pull redis:7-alpine
docker pull maven:3.9-eclipse-temurin-17-alpine
docker pull eclipse-temurin:17-jre-alpine
docker pull node:20-alpine
docker pull nginx:alpine

print_success "Base images pulled"

echo ""
print_header "Step 6: Database Initialization"

# Check if PostgreSQL is running
if docker ps | grep -q englishflow-postgres; then
    print_warning "PostgreSQL container already running"
else
    echo "Starting PostgreSQL container..."
    cd devops/docker
    docker-compose up -d postgres
    cd ../..
    
    # Wait for PostgreSQL to be ready
    echo "Waiting for PostgreSQL to be ready..."
    sleep 10
    
    print_success "PostgreSQL started"
fi

echo ""
print_header "Step 7: SonarCloud Configuration"

echo ""
echo "To complete SonarCloud setup:"
echo "1. Go to https://sonarcloud.io"
echo "2. Sign in with GitHub"
echo "3. Create a new project for this repository"
echo "4. Get your Organization Key and Project Key"
echo "5. Update sonar-project.properties files with your keys"
echo "6. Generate a token and add it to GitHub Secrets as SONAR_TOKEN"
echo ""
read -p "Press Enter when you've completed SonarCloud setup..."

echo ""
print_header "Step 8: GitHub Secrets Configuration"

echo ""
echo "Add the following secrets to GitHub:"
echo "  - SONAR_TOKEN (from SonarCloud)"
echo "  - JWT_SECRET (generated above: $JWT_SECRET)"
echo "  - MAIL_USERNAME (your Gmail)"
echo "  - MAIL_PASSWORD (Gmail App Password)"
echo "  - GOOGLE_CLIENT_ID (OAuth2)"
echo "  - GOOGLE_CLIENT_SECRET (OAuth2)"
echo "  - RECAPTCHA_SECRET (reCAPTCHA)"
echo ""
echo "Go to: https://github.com/YOUR_USERNAME/YOUR_REPO/settings/secrets/actions"
echo ""
read -p "Press Enter when you've added the secrets..."

echo ""
print_header "Setup Complete!"

echo ""
echo "Next steps:"
echo "1. Edit .env files with your actual credentials"
echo "2. Start the development environment: make dev"
echo "3. Run tests: make test"
echo "4. Access services:"
echo "   - Frontend: http://localhost:4200"
echo "   - API Gateway: http://localhost:8080"
echo "   - Eureka: http://localhost:8761"
echo "   - Grafana: http://localhost:3000"
echo "   - Prometheus: http://localhost:9090"
echo ""
echo "For more information, see DEVOPS_SETUP.md"
echo ""

print_success "DevOps environment is ready!"
