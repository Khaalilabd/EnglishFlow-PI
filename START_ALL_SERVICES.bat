@echo off
echo ==========================================
echo Demarrage de tous les services EnglishFlow
echo ==========================================
echo.

echo 1. Demarrage d'Eureka Server (Port 8761)...
start "Eureka Server" cmd /k "cd backend\eureka-server && mvn spring-boot:run"
timeout /t 30 /nobreak

echo.
echo 2. Demarrage d'Auth Service (Port 8081)...
start "Auth Service" cmd /k "cd backend\auth-service && mvn spring-boot:run"
timeout /t 15 /nobreak

echo.
echo 3. Demarrage d'API Gateway (Port 8080)...
start "API Gateway" cmd /k "cd backend\api-gateway && mvn spring-boot:run"
timeout /t 15 /nobreak

echo.
echo 4. Demarrage de Messaging Service (Port 8084)...
start "Messaging Service" cmd /k "cd backend\messaging-service && mvn spring-boot:run"
timeout /t 10 /nobreak

echo.
echo ==========================================
echo Tous les services sont en cours de demarrage!
echo ==========================================
echo.
echo Services:
echo   - Eureka Server:      http://localhost:8761
echo   - Auth Service:       http://localhost:8081
echo   - API Gateway:        http://localhost:8080
echo   - Messaging Service:  http://localhost:8084
echo.
echo Verifiez Eureka Dashboard: http://localhost:8761
echo.
echo Pour demarrer le frontend:
echo   cd frontend ^&^& npm start
echo.
pause
