#!/bin/bash

# EnglishFlow Kubernetes Deployment Script
# Usage: ./deploy.sh [environment]
# Example: ./deploy.sh production

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
NAMESPACE="englishflow"
ENVIRONMENT="${1:-development}"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}EnglishFlow Kubernetes Deployment${NC}"
echo -e "${GREEN}Environment: ${ENVIRONMENT}${NC}"
echo -e "${GREEN}========================================${NC}"

# Check prerequisites
echo -e "\n${YELLOW}Checking prerequisites...${NC}"

if ! command -v kubectl &> /dev/null; then
    echo -e "${RED}kubectl not found. Please install kubectl.${NC}"
    exit 1
fi

if ! kubectl cluster-info &> /dev/null; then
    echo -e "${RED}Cannot connect to Kubernetes cluster. Please check your kubeconfig.${NC}"
    exit 1
fi

echo -e "${GREEN}✓ kubectl found and cluster accessible${NC}"

# Create namespace
echo -e "\n${YELLOW}Creating namespace...${NC}"
kubectl apply -f 00-namespace.yaml
echo -e "${GREEN}✓ Namespace created${NC}"

# Deploy ConfigMaps
echo -e "\n${YELLOW}Deploying ConfigMaps...${NC}"
kubectl apply -f 01-configmaps/
echo -e "${GREEN}✓ ConfigMaps deployed${NC}"

# Check if secrets exist
echo -e "\n${YELLOW}Checking secrets...${NC}"
if ! kubectl get secret app-secrets -n ${NAMESPACE} &> /dev/null; then
    echo -e "${RED}⚠ Secrets not found!${NC}"
    echo -e "${YELLOW}Please create secrets first:${NC}"
    echo -e "kubectl create secret generic app-secrets \\"
    echo -e "  --from-literal=DB_PASSWORD='your-password' \\"
    echo -e "  --from-literal=JWT_SECRET='your-jwt-secret' \\"
    echo -e "  --from-literal=MAIL_PASSWORD='your-mail-password' \\"
    echo -e "  --from-literal=GOOGLE_CLIENT_SECRET='your-google-secret' \\"
    echo -e "  --from-literal=RECAPTCHA_SECRET='your-recaptcha-secret' \\"
    echo -e "  -n ${NAMESPACE}"
    
    read -p "Do you want to use template secrets (NOT for production)? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        kubectl apply -f 02-secrets/
        echo -e "${YELLOW}⚠ Template secrets deployed. CHANGE THEM IN PRODUCTION!${NC}"
    else
        exit 1
    fi
else
    echo -e "${GREEN}✓ Secrets found${NC}"
fi

# Deploy Storage
echo -e "\n${YELLOW}Deploying storage...${NC}"
kubectl apply -f 03-storage/
echo -e "${GREEN}✓ Storage deployed${NC}"

# Wait for PVCs to be bound
echo -e "\n${YELLOW}Waiting for PVCs to be bound...${NC}"
kubectl wait --for=jsonpath='{.status.phase}'=Bound pvc/postgres-pvc -n ${NAMESPACE} --timeout=120s || true
kubectl wait --for=jsonpath='{.status.phase}'=Bound pvc/redis-pvc -n ${NAMESPACE} --timeout=120s || true
echo -e "${GREEN}✓ PVCs bound${NC}"

# Deploy Databases
echo -e "\n${YELLOW}Deploying databases...${NC}"
kubectl apply -f 04-databases/
echo -e "${GREEN}✓ Databases deployed${NC}"

# Wait for databases to be ready
echo -e "\n${YELLOW}Waiting for databases to be ready (this may take a few minutes)...${NC}"
kubectl wait --for=condition=ready pod -l app=postgres -n ${NAMESPACE} --timeout=300s
kubectl wait --for=condition=ready pod -l app=redis -n ${NAMESPACE} --timeout=300s
echo -e "${GREEN}✓ Databases ready${NC}"

# Deploy Infrastructure
echo -e "\n${YELLOW}Deploying infrastructure services...${NC}"
kubectl apply -f 05-infrastructure/
echo -e "${GREEN}✓ Infrastructure deployed${NC}"

# Wait for Eureka to be ready
echo -e "\n${YELLOW}Waiting for Eureka Server to be ready...${NC}"
kubectl wait --for=condition=ready pod -l app=eureka-server -n ${NAMESPACE} --timeout=300s
echo -e "${GREEN}✓ Eureka Server ready${NC}"

# Deploy Microservices
echo -e "\n${YELLOW}Deploying microservices...${NC}"
kubectl apply -f 06-microservices/
echo -e "${GREEN}✓ Microservices deployed${NC}"

# Deploy Frontend
echo -e "\n${YELLOW}Deploying frontend...${NC}"
kubectl apply -f 07-frontend/
echo -e "${GREEN}✓ Frontend deployed${NC}"

# Deploy Ingress
echo -e "\n${YELLOW}Deploying ingress...${NC}"
kubectl apply -f 08-ingress/
echo -e "${GREEN}✓ Ingress deployed${NC}"

# Deploy Monitoring
echo -e "\n${YELLOW}Deploying monitoring...${NC}"
kubectl apply -f 09-monitoring/
echo -e "${GREEN}✓ Monitoring deployed${NC}"

# Deploy Autoscaling
echo -e "\n${YELLOW}Deploying autoscaling...${NC}"
kubectl apply -f 10-autoscaling/
echo -e "${GREEN}✓ Autoscaling deployed${NC}"

# Show deployment status
echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}Deployment Summary${NC}"
echo -e "${GREEN}========================================${NC}"

echo -e "\n${YELLOW}Pods:${NC}"
kubectl get pods -n ${NAMESPACE}

echo -e "\n${YELLOW}Services:${NC}"
kubectl get svc -n ${NAMESPACE}

echo -e "\n${YELLOW}Ingress:${NC}"
kubectl get ingress -n ${NAMESPACE}

echo -e "\n${YELLOW}HPA:${NC}"
kubectl get hpa -n ${NAMESPACE}

# Get external IPs
echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}Access Information${NC}"
echo -e "${GREEN}========================================${NC}"

API_GATEWAY_IP=$(kubectl get svc api-gateway-service -n ${NAMESPACE} -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "pending")
INGRESS_IP=$(kubectl get ingress englishflow-ingress -n ${NAMESPACE} -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "pending")

echo -e "\n${YELLOW}API Gateway:${NC} http://${API_GATEWAY_IP}"
echo -e "${YELLOW}Ingress:${NC} http://${INGRESS_IP}"

echo -e "\n${YELLOW}To access Grafana:${NC}"
echo -e "kubectl port-forward svc/grafana-service 3000:3000 -n ${NAMESPACE}"
echo -e "Then open: http://localhost:3000 (admin/admin123)"

echo -e "\n${YELLOW}To access Prometheus:${NC}"
echo -e "kubectl port-forward svc/prometheus-service 9090:9090 -n ${NAMESPACE}"
echo -e "Then open: http://localhost:9090"

echo -e "\n${YELLOW}To access Eureka Dashboard:${NC}"
echo -e "kubectl port-forward svc/eureka-service 8761:8761 -n ${NAMESPACE}"
echo -e "Then open: http://localhost:8761"

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}Deployment completed successfully!${NC}"
echo -e "${GREEN}========================================${NC}"

echo -e "\n${YELLOW}Useful commands:${NC}"
echo -e "  Watch pods: kubectl get pods -n ${NAMESPACE} -w"
echo -e "  View logs: kubectl logs -f deployment/<service-name> -n ${NAMESPACE}"
echo -e "  Check HPA: kubectl get hpa -n ${NAMESPACE} -w"
echo -e "  Port forward: kubectl port-forward svc/<service-name> <local-port>:<service-port> -n ${NAMESPACE}"
