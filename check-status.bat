@echo off
REM Script de verification de l'etat des services

echo ==========================================
echo   Verification de l'etat des services
echo ==========================================
echo.

echo [1/5] Etat des conteneurs Docker...
docker-compose ps
echo.

echo [2/5] Verification PostgreSQL...
docker exec postgres pg_isready -U postgres
echo.

echo [3/5] Verification RabbitMQ...
docker exec rabbitmq rabbitmq-diagnostics ping
echo.

echo [4/5] Liste des bases de donnees PostgreSQL...
docker exec postgres psql -U postgres -c "\l"
echo.

echo [5/5] Liste des queues RabbitMQ...
docker exec rabbitmq rabbitmqctl list_queues
echo.

echo ==========================================
echo   Verification terminee !
echo ==========================================
echo.
echo Services disponibles :
echo   - PostgreSQL:         localhost:5432
echo   - RabbitMQ AMQP:      localhost:5672
echo   - RabbitMQ Management: http://localhost:15672 (user/password)
echo   - MatchOdds API:      http://localhost:8080
echo   - MatchOdds Swagger:  http://localhost:8080/swagger
echo.
pause
