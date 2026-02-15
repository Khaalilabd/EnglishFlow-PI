@echo off
echo Starting Learning Service...
echo Make sure PostgreSQL is running with learning_db database
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo Maven is not installed. Please install Maven first.
    exit /b 1
)

REM Run the service
mvn spring-boot:run
