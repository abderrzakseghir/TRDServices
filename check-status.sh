#!/bin/bash

# Script de verification de l'etat des services

echo "=========================================="
echo "  Vérification de l'état des services"
echo "=========================================="
echo ""

echo "[1/5] État des conteneurs Docker..."
docker-compose ps
echo ""

echo "[2/5] Vérification PostgreSQL..."
docker exec postgres pg_isready -U postgres
echo ""

echo "[3/5] Vérification RabbitMQ..."
docker exec rabbitmq rabbitmq-diagnostics ping
echo ""

echo "[4/5] Liste des bases de données PostgreSQL..."
docker exec postgres psql -U postgres -c "\l"
echo ""

echo "[5/5] Liste des queues RabbitMQ..."
docker exec rabbitmq rabbitmqctl list_queues
echo ""

echo "=========================================="
echo "  Vérification terminée !"
echo "=========================================="
echo ""
echo "Services disponibles :"
echo "  - PostgreSQL:          localhost:5432"
echo "  - RabbitMQ AMQP:       localhost:5672"
echo "  - RabbitMQ Management: http://localhost:15672 (user/password)"
echo "  - MatchOdds API:       http://localhost:8080"
echo "  - MatchOdds Swagger:   http://localhost:8080/swagger"
echo ""
