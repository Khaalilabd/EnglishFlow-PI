@echo off
echo ========================================
echo Verifying Community Service Database
echo ========================================
echo.

echo Checking if database exists...
psql -U root -h localhost -c "\l" | findstr englishflow_community

echo.
echo Checking tables...
psql -U root -d englishflow_community -c "\dt"

echo.
echo Checking categories...
psql -U root -d englishflow_community -c "SELECT COUNT(*) as category_count FROM categories;"

echo.
echo Checking sub_categories...
psql -U root -d englishflow_community -c "SELECT COUNT(*) as subcategory_count FROM sub_categories;"

echo.
pause
