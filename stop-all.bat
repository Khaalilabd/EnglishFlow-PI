@echo off
echo ========================================
echo   EnglishFlow - Arret de tous les services
echo ========================================
echo.

echo Arret des processus Java (Spring Boot)...
taskkill /F /FI "WINDOWTITLE eq Config Server*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Eureka Server*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Auth Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Club Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq Learning Service*" 2>nul
taskkill /F /FI "WINDOWTITLE eq API Gateway*" 2>nul

echo Arret du frontend Angular...
taskkill /F /FI "WINDOWTITLE eq Frontend Angular*" 2>nul

echo.
echo Tous les services ont ete arretes.
echo.
pause
