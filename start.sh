#!/bin/bash

# Script de démarrage pour Docker Compose
# Ce script nettoie, build et démarre tous les services

echo "=========================================="
echo "  Arrêt des conteneurs existants..."
echo "=========================================="
docker-compose down --volumes

echo ""
echo "=========================================="
echo "  Build des nouvelles images..."
echo "=========================================="
docker-compose build --no-cache

echo ""
echo "=========================================="
echo "  Démarrage des services..."
echo "=========================================="
docker-compose up -d

echo ""
echo "=========================================="
echo "  Services démarrés avec succès !"
echo "=========================================="
echo ""
echo "Services disponibles:"
echo "  - PostgreSQL:        localhost:5432"
echo "  - RabbitMQ Management: http://localhost:15672 (user/password)"
echo "  - MatchOdds API:     http://localhost:8080"
echo "  - MatchOdds Swagger: http://localhost:8080/swagger"
echo ""
echo "Pour voir les logs: docker-compose logs -f"
echo "Pour arrêter:       docker-compose down"
echo ""
