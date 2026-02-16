@echo off
echo ========================================
echo Testing PostgreSQL Connection
echo ========================================
echo.

echo Testing connection with user 'root'...
psql -U root -h localhost -c "SELECT version();"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ Connection successful with user 'root'
    echo.
    echo Current databases:
    psql -U root -h localhost -c "\l"
) else (
    echo.
    echo ✗ Connection failed with user 'root'
    echo.
    echo Trying with user 'postgres'...
    psql -U postgres -h localhost -c "SELECT version();"
    
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo ✓ Connection successful with user 'postgres'
        echo.
        echo Please update application.yml to use 'postgres' instead of 'root'
    ) else (
        echo.
        echo ✗ Connection failed
        echo.
        echo Please check:
        echo 1. PostgreSQL is running
        echo 2. Username and password are correct
        echo 3. PostgreSQL is listening on port 5432
    )
)

echo.
pause
