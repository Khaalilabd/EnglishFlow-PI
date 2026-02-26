@echo off
echo Setting up PostgreSQL databases for all services...
echo.

set PGPASSWORD=root

echo Creating englishflow_courses database...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -c "CREATE DATABASE englishflow_courses;" 2>nul
if %errorlevel% equ 0 (
    echo englishflow_courses created successfully!
) else (
    echo englishflow_courses already exists or error occurred
)

echo Creating englishflow_identity database...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -c "CREATE DATABASE englishflow_identity;" 2>nul
if %errorlevel% equ 0 (
    echo englishflow_identity created successfully!
) else (
    echo englishflow_identity already exists or error occurred
)

echo Creating learning_db database...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -c "CREATE DATABASE learning_db;" 2>nul
if %errorlevel% equ 0 (
    echo learning_db created successfully!
) else (
    echo learning_db already exists or error occurred
)

echo Creating jungle_club_db database...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -c "CREATE DATABASE jungle_club_db;" 2>nul
if %errorlevel% equ 0 (
    echo jungle_club_db created successfully!
) else (
    echo jungle_club_db already exists or error occurred
)

echo Creating englishflow_community database...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -c "CREATE DATABASE englishflow_community;" 2>nul
if %errorlevel% equ 0 (
    echo englishflow_community created successfully!
) else (
    echo englishflow_community already exists or error occurred
)

echo Creating englishflow_complaints database...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -c "CREATE DATABASE englishflow_complaints;" 2>nul
if %errorlevel% equ 0 (
    echo englishflow_complaints created successfully!
) else (
    echo englishflow_complaints already exists or error occurred
)

echo Creating messaging_db database...
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -c "CREATE DATABASE messaging_db;" 2>nul
if %errorlevel% equ 0 (
    echo messaging_db created successfully!
) else (
    echo messaging_db already exists or error occurred
)

echo.
echo All databases created successfully!
echo.
pause
