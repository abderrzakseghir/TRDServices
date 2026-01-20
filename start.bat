@echo off
REM Script de démarrage pour Docker Compose (Windows)
REM Ce script nettoie, build et démarre tous les services

echo ==========================================
echo   Arret des conteneurs existants...
echo ==========================================
docker-compose down --volumes

echo.
echo ==========================================
echo   Build des nouvelles images...
echo ==========================================
docker-compose build --no-cache

echo.
echo ==========================================
echo   Demarrage des services...
echo ==========================================
docker-compose up -d

echo.
echo ==========================================
echo   Services demarres avec succes !
echo ==========================================
echo.
echo Services disponibles:
echo   - PostgreSQL:          localhost:5432
echo   - RabbitMQ Management: http://localhost:15672 (user/password)
echo   - MatchOdds API:       http://localhost:8080
echo   - MatchOdds Swagger:   http://localhost:8080/swagger
echo.
echo Pour voir les logs: docker-compose logs -f
echo Pour arreter:       docker-compose down
echo.
pause
