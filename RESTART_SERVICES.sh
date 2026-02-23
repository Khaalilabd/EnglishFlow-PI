#!/bin/bash

echo "🛑 Arrêt de tous les services Java..."
pkill -9 -f "ApiGatewayApplication"
pkill -9 -f "LearningServiceApplication"
sleep 2

echo "✅ Services arrêtés"
echo ""
echo "📋 Pour redémarrer les services, ouvre 2 terminaux:"
echo ""
echo "Terminal 1 - API Gateway:"
echo "cd backend/api-gateway && mvn spring-boot:run"
echo ""
echo "Terminal 2 - Learning Service:"
echo "cd backend/learning-service && mvn spring-boot:run"
echo ""
echo "⏳ Attends que les deux services affichent 'Started' avant de tester"
echo ""
echo "🧪 Pour tester que tout fonctionne:"
echo "curl http://localhost:8080/api/reviews"
echo ""
