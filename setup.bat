@echo off
REM ============================================
REM University ERP - One-Click Setup
REM ============================================
REM Complete automated setup - just run this!
REM ============================================

echo ========================================
echo University ERP - One-Click Setup
echo ========================================
echo.

REM Get MySQL password
set /p MYSQL_PASS="Enter MySQL root password: "
if "%MYSQL_PASS%"=="" (
    echo ERROR: Password required
    pause
    exit /b 1
)
echo.

echo [1/4] Dropping old databases (if exist)...
mysql -u root -p%MYSQL_PASS% -e "DROP DATABASE IF EXISTS univ_auth; DROP DATABASE IF EXISTS univ_erp;" 2>nul
echo ✓ Old databases removed
echo.

echo [2/4] Creating fresh databases...
mysql -u root -p%MYSQL_PASS% -e "CREATE DATABASE univ_auth; CREATE DATABASE univ_erp;" 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Failed to create databases
    pause
    exit /b 1
)
echo ✓ Databases created
echo.

echo [3/4] Loading all schemas and data...
echo   - Auth schema...
type "%~dp0sql\auth_schema.sql" | mysql -u root -p%MYSQL_PASS% univ_auth 2>nul
echo   - Auth data...
type "%~dp0sql\auth_seed.sql" | mysql -u root -p%MYSQL_PASS% univ_auth 2>nul
echo   - ERP schema...
type "%~dp0sql\erp_schema.sql" | mysql -u root -p%MYSQL_PASS% univ_erp 2>nul
echo   - ERP data...
type "%~dp0sql\erp_seed.sql" | mysql -u root -p%MYSQL_PASS% univ_erp 2>nul
echo   - Constraints...
type "%~dp0sql\add_constraints.sql" | mysql -u root -p%MYSQL_PASS% univ_erp 2>nul
echo   - Indexes...
type "%~dp0sql\add_indexes.sql" | mysql -u root -p%MYSQL_PASS% univ_erp 2>nul
echo ✓ All data loaded
echo.

echo [4/4] Verifying setup...
mysql -u root -p%MYSQL_PASS% -e "SELECT COUNT(*) as Users FROM univ_auth.users_auth; SELECT COUNT(*) as Courses FROM univ_erp.courses; SELECT COUNT(*) as Sections FROM univ_erp.sections;" 2>nul
echo.

echo ========================================
echo ✓ Setup Complete!
echo ========================================
echo.
echo Starting application...
echo.
echo Login Credentials:
echo   Admin:      admin / password123
echo   Instructor: john.doe / password123
echo   Student:    alice.smith / password123
echo.
echo ========================================
echo.

REM Start the application
call mvn clean compile exec:java

pause
