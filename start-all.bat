@echo off
echo ========================================
echo   EnglishFlow - Demarrage complet
echo ========================================
echo.

REM Verification de Maven
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERREUR] Maven n'est pas installe. Veuillez installer Maven.
    pause
    exit /b 1
)

REM Verification de Node.js
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERREUR] Node.js n'est pas installe. Veuillez installer Node.js.
    pause
    exit /b 1
)

echo [INFO] Demarrage des services backend...
echo.

REM 1. Config Server
echo [1/6] Demarrage Config Server (port 8888)...
start "Config Server" cmd /k "cd backend\config-server && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

REM 2. Eureka Server
echo [2/6] Demarrage Eureka Server (port 8761)...
start "Eureka Server" cmd /k "cd backend\eureka-server && mvn spring-boot:run"
timeout /t 20 /nobreak >nul

REM 3. Auth Service
echo [3/6] Demarrage Auth Service (port 8081)...
start "Auth Service" cmd /k "cd backend\auth-service && mvn spring-boot:run"
timeout /t 5 /nobreak >nul

REM 4. Club Service
echo [4/6] Demarrage Club Service (port 8084)...
start "Club Service" cmd /k "cd backend\club-service && mvn spring-boot:run"
timeout /t 5 /nobreak >nul



REM 6. API Gateway
echo [6/6] Demarrage API Gateway (port 8080)...
start "API Gateway" cmd /k "cd backend\api-gateway && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo.
echo [INFO] Demarrage du frontend Angular...
start "Frontend Angular" cmd /k "cd frontend && npm start"

echo.
echo ========================================
echo   Tous les services sont en cours de demarrage!
echo ========================================
echo.
echo Services backend:
echo   - Config Server:    http://localhost:8888
echo   - Eureka Server:    http://localhost:8761
echo   - Auth Service:     http://localhost:8081
echo   - Club Service:     http://localhost:8084
echo   - Learning Service: http://localhost:8083
echo   - API Gateway:      http://localhost:8080
echo.
echo Frontend:
echo   - Angular App:      http://localhost:4200
echo.
echo Patientez 1-2 minutes pour que tous les services soient prets.
echo Verifiez Eureka Dashboard: http://localhost:8761
echo.
pause
