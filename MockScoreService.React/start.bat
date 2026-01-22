@echo off
echo ========================================
echo  Interface React - Mock Football API
echo ========================================
echo.

echo [1/4] Verification de Node.js...
node --version >nul 2>&1
if errorlevel 1 (
    echo ERREUR: Node.js n'est pas installe
    echo Telechargez-le depuis https://nodejs.org/
    pause
    exit /b 1
)
node --version
echo.

echo [2/4] Verification de l'API...
curl -s http://localhost:5000/v4/matches >nul 2>&1
if errorlevel 1 (
    echo ATTENTION: L'API ne repond pas sur http://localhost:5000
    echo Assurez-vous que docker-compose up -d est lance
    echo.
    choice /C YN /M "Continuer quand meme"
    if errorlevel 2 exit /b 1
)
echo API accessible
echo.

echo [3/4] Installation des dependances...
if not exist node_modules (
    echo Installation en cours...
    call npm install
) else (
    echo Dependencies deja installees
)
echo.

echo [4/4] Demarrage du serveur de developpement...
echo.
echo L'interface sera accessible sur: http://localhost:3000
echo Appuyez sur Ctrl+C pour arreter
echo.
call npm run dev
