@echo off
echo ========================================
echo Setting up Sample Data for EnglishFlow
echo ========================================

echo.
echo 1. Creating sample users in englishflow_identity database...
psql -U postgres -d englishflow_identity -f backend/create-sample-users.sql

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to create sample users. Make sure PostgreSQL is running and englishflow_identity database exists.
    pause
    exit /b 1
)

echo.
echo 2. Sample users created successfully!
echo.
echo 3. Now restart the backend services to initialize course data...
echo    The DataInitializer will create courses with proper tutor assignments.
echo.
echo USERS CREATED:
echo ===============
echo TUTORS:
echo   1. Sarah Johnson (sarah.johnson@englishflow.com) - password123
echo   2. Michael Chen (michael.chen@englishflow.com) - password123  
echo   3. Emma Rodriguez (emma.rodriguez@englishflow.com) - password123
echo.
echo STUDENTS:
echo   4. John Doe (john.doe@student.com) - password123
echo   5. Jane Smith (jane.smith@student.com) - password123
echo   6. Carlos Garcia (carlos.garcia@student.com) - password123
echo   7. Maria Lopez (maria.lopez@student.com) - password123
echo   8. Ahmed Hassan (ahmed.hassan@student.com) - password123
echo.
echo COURSE ASSIGNMENTS:
echo ===================
echo   - Course 1 & 3: Tutor ID 1 (Sarah Johnson)
echo   - Course 2 & 5: Tutor ID 2 (Michael Chen)
echo   - Course 4: Tutor ID 3 (Emma Rodriguez)
echo.
echo 4. Ready to test! You can now:
echo    - Use test-progress-api.html to test progress tracking
echo    - Login with any of the above credentials
echo    - Enroll students in courses and track their progress
echo.
pause