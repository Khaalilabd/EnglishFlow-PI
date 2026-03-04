@echo off
echo ========================================
echo Testing Lock Endpoint
echo ========================================
echo.

echo Test 1: Direct call to Community Service (port 8082)
echo URL: http://localhost:8082/community/lock/subcategory/1?userId=1
curl -X POST "http://localhost:8082/community/lock/subcategory/1?userId=1"
echo.
echo.

echo Test 2: Via API Gateway (port 8080)
echo URL: http://localhost:8080/api/community/lock/subcategory/1?userId=1
curl -X POST "http://localhost:8080/api/community/lock/subcategory/1?userId=1"
echo.
echo.

echo ========================================
echo Tests completed
echo ========================================
pause
