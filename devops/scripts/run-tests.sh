#!/bin/bash

# Script to run all tests
# Usage: ./run-tests.sh [backend|frontend|all]

set -e

TARGET=${1:-all}

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🧪 EnglishFlow Test Runner${NC}"
echo ""

run_backend_tests() {
    echo -e "${YELLOW}📦 Running Backend Tests...${NC}"
    echo ""
    
    cd backend
    
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
    )
    
    PASSED=0
    FAILED=0
    
    for service in "${SERVICES[@]}"; do
        if [ -d "$service" ]; then
            echo -e "${BLUE}Testing $service...${NC}"
            
            cd "$service"
            
            if mvn clean test -B > /dev/null 2>&1; then
                echo -e "${GREEN}✓ $service - PASSED${NC}"
                PASSED=$((PASSED + 1))
            else
                echo -e "${RED}✗ $service - FAILED${NC}"
                FAILED=$((FAILED + 1))
            fi
            
            cd ..
        fi
    done
    
    cd ..
    
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "Backend Test Summary:"
    echo -e "  ${GREEN}Passed: $PASSED${NC}"
    echo -e "  ${RED}Failed: $FAILED${NC}"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    if [ $FAILED -gt 0 ]; then
        return 1
    fi
}

run_frontend_tests() {
    echo -e "${YELLOW}🎨 Running Frontend Tests...${NC}"
    echo ""
    
    cd frontend
    
    if npm test -- --watch=false --browsers=ChromeHeadlessCI; then
        echo -e "${GREEN}✓ Frontend tests - PASSED${NC}"
        cd ..
        return 0
    else
        echo -e "${RED}✗ Frontend tests - FAILED${NC}"
        cd ..
        return 1
    fi
}

run_coverage_report() {
    echo ""
    echo -e "${YELLOW}📊 Generating Coverage Reports...${NC}"
    echo ""
    
    # Backend coverage
    cd backend
    for service in auth-service courses-service community-service messaging-service club-service event-service learning-service complaints-service sponsors-service; do
        if [ -d "$service" ] && [ -f "$service/target/site/jacoco/index.html" ]; then
            echo "  📄 $service: file://$(pwd)/$service/target/site/jacoco/index.html"
        fi
    done
    cd ..
    
    # Frontend coverage
    if [ -f "frontend/coverage/index.html" ]; then
        echo "  📄 Frontend: file://$(pwd)/frontend/coverage/index.html"
    fi
}

# Main execution
BACKEND_RESULT=0
FRONTEND_RESULT=0

if [ "$TARGET" = "backend" ] || [ "$TARGET" = "all" ]; then
    run_backend_tests || BACKEND_RESULT=1
fi

if [ "$TARGET" = "frontend" ] || [ "$TARGET" = "all" ]; then
    run_frontend_tests || FRONTEND_RESULT=1
fi

if [ "$TARGET" = "all" ]; then
    run_coverage_report
fi

echo ""

if [ $BACKEND_RESULT -eq 0 ] && [ $FRONTEND_RESULT -eq 0 ]; then
    echo -e "${GREEN}✅ All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}❌ Some tests failed!${NC}"
    exit 1
fi
