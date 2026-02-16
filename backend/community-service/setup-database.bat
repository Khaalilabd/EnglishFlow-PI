@echo off
echo ========================================
echo Community Service - Database Setup
echo ========================================
echo.

echo Step 1: Creating database...
psql -U root -h localhost -c "CREATE DATABASE englishflow_community;"

if %ERRORLEVEL% EQU 0 (
    echo Database created successfully!
) else (
    echo Database might already exist or there was an error.
)

echo.
echo Step 2: Verifying database...
psql -U root -h localhost -c "\l" | findstr englishflow_community

echo.
echo ========================================
echo Setup complete!
echo ========================================
echo.
echo Next steps:
echo 1. Start the service: run.bat
echo 2. Wait for tables to be created
echo 3. Initialize categories: psql -U root -d englishflow_community -f init-categories.sql
echo.
pause
