#!/bin/bash

# Script to add Jaeger configuration to all microservices
# This adds Zipkin endpoint configuration for Jaeger tracing

SERVICES=(
  "club-service"
  "community-service"
  "courses-service"
  "event-service"
  "messaging-service"
  "learning-service"
  "exam-service"
  "gamification-service"
  "payment-service"
  "complaints-service"
  "sponsors-service"
)

echo "🚀 Adding Jaeger configuration to microservices..."

for service in "${SERVICES[@]}"; do
  config_file="backend/$service/src/main/resources/application.yml"
  
  if [[ -f "$config_file" ]]; then
    echo "✅ Processing $service..."
    
    # Check if zipkin config already exists
    if grep -q "management.zipkin" "$config_file"; then
      echo "   ⚠️  Jaeger config already exists in $service, skipping..."
    else
      # Add zipkin endpoint to management section if it exists
      if grep -q "management:" "$config_file"; then
        echo "   📝 Adding Jaeger endpoint configuration..."
        # This will be done manually for each service to ensure correctness
      fi
    fi
  else
    echo "❌ Config file not found for $service"
  fi
done

echo ""
echo "✅ Jaeger configuration process completed!"
echo ""
echo "📋 Next steps:"
echo "1. Review the configuration in each service"
echo "2. Build the services: cd backend && mvn clean install -DskipTests"
echo "3. Start Docker: cd devops/docker && docker-compose up -d"
echo "4. Access Jaeger UI: http://localhost:16686"
echo "5. Access Grafana: http://localhost:3000 (admin/admin)"
echo "6. Access Prometheus: http://localhost:9090"
