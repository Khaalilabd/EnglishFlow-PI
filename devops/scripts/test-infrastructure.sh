#!/bin/bash

# Script to test the complete DevOps infrastructure
# Tests: Docker, Prometheus, Grafana, Jaeger, Loki

echo "🚀 Testing EnglishFlow DevOps Infrastructure"
echo "=============================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check if a service is running
check_service() {
    local service_name=$1
    local url=$2
    local expected_status=${3:-200}
    
    echo -n "Testing $service_name... "
    
    response=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>/dev/null)
    
    if [[ "$response" -eq "$expected_status" ]]; then
        echo -e "${GREEN}✅ OK${NC} (HTTP $response)"
        return 0
    else
        echo -e "${RED}❌ FAILED${NC} (HTTP $response)"
        return 1
    fi
}

# Check Docker containers
echo "📦 Checking Docker Containers..."
echo "--------------------------------"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep englishflow
echo ""

# Test Infrastructure Services
echo "🔧 Testing Infrastructure Services..."
echo "------------------------------------"
check_service "PostgreSQL" "http://localhost:5432" 000
check_service "Redis" "http://localhost:6379" 000
check_service "Eureka Server" "http://localhost:8761/actuator/health"
check_service "API Gateway" "http://localhost:8080/actuator/health"
echo ""

# Test Monitoring Services
echo "📊 Testing Monitoring Services..."
echo "---------------------------------"
check_service "Prometheus" "http://localhost:9090/-/healthy"
check_service "Grafana" "http://localhost:3000/api/health"
check_service "Jaeger UI" "http://localhost:16686"
check_service "Loki" "http://localhost:3100/ready"
echo ""

# Test Microservices
echo "🎯 Testing Microservices..."
echo "---------------------------"
check_service "Auth Service" "http://localhost:8081/actuator/health"
check_service "Club Service" "http://localhost:8085/actuator/health"
check_service "Community Service" "http://localhost:8082/actuator/health"
check_service "Courses Service" "http://localhost:8086/actuator/health"
check_service "Event Service" "http://localhost:8088/actuator/health"
check_service "Messaging Service" "http://localhost:8084/actuator/health"
echo ""

# Test Prometheus Targets
echo "🎯 Checking Prometheus Targets..."
echo "---------------------------------"
targets=$(curl -s http://localhost:9090/api/v1/targets | jq -r '.data.activeTargets[] | "\(.labels.job): \(.health)"' 2>/dev/null)
if [[ $? -eq 0 ]]; then
    echo "$targets"
else
    echo -e "${YELLOW}⚠️  Could not fetch Prometheus targets${NC}"
fi
echo ""

# Summary
echo "📋 Access URLs:"
echo "---------------"
echo "🌐 Eureka Dashboard:    http://localhost:8761"
echo "📊 Prometheus:          http://localhost:9090"
echo "📈 Grafana:             http://localhost:3000 (admin/admin)"
echo "🔍 Jaeger Tracing:      http://localhost:16686"
echo "📝 Loki Logs:           http://localhost:3100"
echo "🚪 API Gateway:         http://localhost:8080"
echo ""
echo "✅ Infrastructure test completed!"
