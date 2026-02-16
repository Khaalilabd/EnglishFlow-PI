@echo off
echo Creating englishflow_community database...
echo.

REM Créer la base de données
psql -U englishflow -d postgres -c "CREATE DATABASE englishflow_community;"

echo.
echo Database created successfully!
echo.
pause
