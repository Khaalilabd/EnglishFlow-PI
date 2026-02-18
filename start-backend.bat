@echo off
echo ========================================
echo Starting EnglishFlow Backend Services
echo ========================================
echo.

echo Checking if services are already running...
echo.

REM Check if ports are in use
netstat -ano | findstr ":8761" >nul
if %errorlevel% equ 0 (
    echo [OK] Eureka Server is running on port 8761
) else (
    echo [WARNING] Eureka Server is NOT running on port 8761
)

netstat -ano | findstr ":8888" >nul
if %errorlevel% equ 0 (
    echo [OK] Config Server is running on port 8888
) else (
    echo [WARNING] Config Server is NOT running on port 8888
)

netstat -ano | findstr ":8088" >nul
if %errorlevel% equ 0 (
    echo [OK] API Gateway is running on port 8088
) else (
    echo [WARNING] API Gateway is NOT running on port 8088
)

netstat -ano | findstr ":8082" >nul
if %errorlevel% equ 0 (
    echo [OK] Courses Service is running on port 8082
) else (
    echo [WARNING] Courses Service is NOT running on port 8082
)

echo.
echo ========================================
echo Testing API Gateway
echo ========================================
echo.

curl -s http://localhost:8088/api/courses >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] API Gateway is accessible and responding
    echo.
    echo Fetching courses...
    curl http://localhost:8088/api/courses
) else (
    echo [ERROR] API Gateway is NOT accessible
    echo.
    echo Please start the services in this order:
    echo 1. Eureka Server: cd backend\eureka-server ^&^& mvn spring-boot:run
    echo 2. Config Server: cd backend\config-server ^&^& mvn spring-boot:run
    echo 3. API Gateway: cd backend\api-gateway ^&^& mvn spring-boot:run
    echo 4. Courses Service: cd backend\courses-service ^&^& mvn spring-boot:run
)

echo.
pause
