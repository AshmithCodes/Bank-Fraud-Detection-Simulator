@echo off
echo ================================
echo  Card Fraud Detection - Launcher
echo ================================

:: Check if java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Java is NOT installed on this machine.
    echo.
    echo Please install Java from:
    echo   https://www.oracle.com/java/technologies/downloads/
    echo   OR  https://adoptium.net/  (free, recommended)
    echo.
    echo After installing, re-run this script.
    pause
    exit /b 1
)

:: Check if javac (compiler) is available
javac -version >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Java Runtime is installed but the JDK (compiler) is missing.
    echo You need the JDK, not just the JRE.
    echo.
    echo Download the JDK from:
    echo   https://adoptium.net/
    pause
    exit /b 1
)

echo [OK] Java found.

:: Check if FraudDetection.java exists in the same folder
if not exist "%~dp0FraudDetection.java" (
    echo.
    echo [ERROR] FraudDetection.java not found.
    echo Make sure this script is in the same folder as FraudDetection.java
    pause
    exit /b 1
)

:: Compile
echo Compiling FraudDetection.java...
javac "%~dp0FraudDetection.java" -d "%~dp0"
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed. Check FraudDetection.java for errors.
    pause
    exit /b 1
)

echo [OK] Compiled successfully.
echo Launching...
echo.

:: Run
java -cp "%~dp0" FraudDetection
pause
