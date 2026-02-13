#!/bin/bash

echo "🚀 Démarrage de l'environnement de développement EnglishFlow"
echo ""

# Fonction pour démarrer le frontend
start_frontend() {
    echo "📱 Démarrage du Frontend Angular..."
    cd frontend
    if [ ! -d "node_modules" ]; then
        echo "Installation des dépendances npm..."
        npm install
    fi
    npm start &
    FRONTEND_PID=$!
    cd ..
    echo "✅ Frontend démarré (PID: $FRONTEND_PID)"
}

# Fonction pour démarrer le backend
start_backend() {
    echo "⚙️  Démarrage du Backend Spring Boot..."
    echo "⚠️  Les microservices seront démarrés individuellement"
    # À compléter quand les microservices seront créés
}

# Menu
echo "Que voulez-vous démarrer?"
echo "1) Frontend uniquement"
echo "2) Backend uniquement"
echo "3) Frontend + Backend"
echo "4) Docker Compose (tous les services)"
read -p "Votre choix (1-4): " choice

case $choice in
    1)
        start_frontend
        ;;
    2)
        start_backend
        ;;
    3)
        start_frontend
        start_backend
        ;;
    4)
        echo "🐳 Démarrage avec Docker Compose..."
        docker-compose up -d
        echo "✅ Services démarrés"
        echo "Frontend: http://localhost:4200"
        echo "API Gateway: http://localhost:8080"
        echo "Eureka: http://localhost:8761"
        ;;
    *)
        echo "❌ Choix invalide"
        exit 1
        ;;
esac

echo ""
echo "✨ Environnement prêt!"
echo "Frontend: http://localhost:4200"
echo "Backend API: http://localhost:8080"
