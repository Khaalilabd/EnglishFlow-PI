#!/bin/bash

# EnglishFlow Deployment Script
# Usage: ./scripts/deploy.sh [environment]
# Environments: dev, staging, prod

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
ENVIRONMENT=${1:-dev}
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE_FILES="-f docker-compose.yml"

# Functions
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_requirements() {
    log_info "Checking requirements..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker is not installed"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose is not installed"
        exit 1
    fi
    
    log_info "Requirements OK"
}

load_env() {
    log_info "Loading environment variables for: $ENVIRONMENT"
    
    if [[ "$ENVIRONMENT" = "prod" ]]; then
        if [[ ! -f "$PROJECT_DIR/.env.prod" ]]; then
            log_error ".env.prod file not found"
            exit 1
        fi
        export $(cat "$PROJECT_DIR/.env.prod" | grep -v '^#' | xargs)
        COMPOSE_FILES="$COMPOSE_FILES -f docker-compose.prod.yml"
    elif [[ "$ENVIRONMENT" = "staging" ]]; then
        if [[ ! -f "$PROJECT_DIR/.env.staging" ]]; then
            log_error ".env.staging file not found"
            exit 1
        fi
        export $(cat "$PROJECT_DIR/.env.staging" | grep -v '^#' | xargs)
    else
        if [[ ! -f "$PROJECT_DIR/.env" ]]; then
            log_warn ".env file not found, using defaults"
        else
            export $(cat "$PROJECT_DIR/.env" | grep -v '^#' | xargs)
        fi
    fi
}

backup_database() {
    if [[ "$ENVIRONMENT" = "prod" ]]; then
        log_info "Creating database backup..."
        
        BACKUP_DIR="$PROJECT_DIR/backups"
        mkdir -p "$BACKUP_DIR"
        
        BACKUP_FILE="$BACKUP_DIR/backup-$(date +%Y%m%d-%H%M%S).sql"
        
        docker-compose $COMPOSE_FILES exec -T postgres pg_dumpall -U postgres > "$BACKUP_FILE"
        gzip "$BACKUP_FILE"
        
        log_info "Backup created: ${BACKUP_FILE}.gz"
        
        # Keep only last 7 backups
        ls -t "$BACKUP_DIR"/backup-*.sql.gz | tail -n +8 | xargs -r rm
    fi
}

pull_images() {
    log_info "Pulling latest images..."
    cd "$PROJECT_DIR"
    docker-compose $COMPOSE_FILES pull
}

build_images() {
    log_info "Building images..."
    cd "$PROJECT_DIR"
    docker-compose $COMPOSE_FILES build --parallel
}

start_services() {
    log_info "Starting services..."
    cd "$PROJECT_DIR"
    
    # Start infrastructure first
    log_info "Starting infrastructure..."
    docker-compose $COMPOSE_FILES up -d postgres redis
    sleep 10
    
    # Start Eureka
    log_info "Starting Eureka Server..."
    docker-compose $COMPOSE_FILES up -d eureka-server
    sleep 30
    
    # Start API Gateway
    log_info "Starting API Gateway..."
    docker-compose $COMPOSE_FILES up -d api-gateway
    sleep 20
    
    # Start microservices
    log_info "Starting microservices..."
    docker-compose $COMPOSE_FILES up -d auth-service courses-service exam-service messaging-service community-service club-service
    sleep 40
    
    # Start frontend
    log_info "Starting frontend..."
    docker-compose $COMPOSE_FILES up -d frontend
}

health_check() {
    log_info "Running health checks..."
    
    SERVICES=(
        "http://localhost:8761/actuator/health:Eureka"
        "http://localhost:8080/actuator/health:Gateway"
        "http://localhost:8081/actuator/health:Auth"
        "http://localhost:8086/actuator/health:Courses"
        "http://localhost:8087/actuator/health:Exam"
        "http://localhost:8084/actuator/health:Messaging"
        "http://localhost:8082/actuator/health:Community"
        "http://localhost:8085/actuator/health:Club"
    )
    
    FAILED=0
    
    for service in "${SERVICES[@]}"; do
        IFS=':' read -r url name <<< "$service"
        
        if curl -f -s "$url" > /dev/null; then
            log_info "$name service: OK"
        else
            log_error "$name service: FAILED"
            FAILED=1
        fi
    done
    
    if [[ $FAILED -eq 1 ]]; then
        log_error "Some services failed health check"
        return 1
    fi
    
    log_info "All services healthy"
}

show_status() {
    log_info "Service status:"
    cd "$PROJECT_DIR"
    docker-compose $COMPOSE_FILES ps
}

cleanup() {
    log_info "Cleaning up..."
    docker system prune -f
}

# Main
main() {
    log_info "Starting deployment for environment: $ENVIRONMENT"
    
    check_requirements
    load_env
    
    if [[ "$ENVIRONMENT" = "prod" ]]; then
        backup_database
    fi
    
    if [[ "$ENVIRONMENT" = "dev" ]]; then
        build_images
    else
        pull_images
    fi
    
    start_services
    
    log_info "Waiting for services to be ready..."
    sleep 20
    
    health_check
    
    show_status
    cleanup
    
    log_info "Deployment completed successfully!"
    log_info "Access the application at:"
    log_info "  - Frontend: http://localhost:4200"
    log_info "  - API Gateway: http://localhost:8080"
    log_info "  - Eureka Dashboard: http://localhost:8761"
}

# Run
main
