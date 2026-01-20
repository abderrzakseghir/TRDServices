# Commandes Pratiques - Docker Compose

## ?? Démarrage

### Démarrer tous les services
```bash
docker-compose up -d
```

### Démarrer avec rebuild complet
```bash
docker-compose up -d --build
```

### Démarrer sans cache
```bash
docker-compose build --no-cache
docker-compose up -d
```

---

## ?? Gestion des Services

### Redémarrer un service spécifique
```bash
docker-compose restart matchodds-service
docker-compose restart score-service
docker-compose restart bet-result-service
```

### Rebuild et redémarrer un service
```bash
docker-compose build matchodds-service
docker-compose up -d matchodds-service
```

### Arrêter tous les services
```bash
docker-compose stop
```

### Arrêter et supprimer les conteneurs
```bash
docker-compose down
```

### Tout supprimer (volumes inclus)
```bash
docker-compose down --volumes
```

---

## ?? Surveillance

### Voir l'état de tous les services
```bash
docker-compose ps
```

### Voir les logs en temps réel
```bash
docker-compose logs -f
```

### Logs d'un service spécifique
```bash
docker-compose logs -f matchodds-service
docker-compose logs -f score-service
docker-compose logs -f bet-result-service
docker-compose logs -f postgres
docker-compose logs -f rabbitmq
```

### Derniers logs (tail)
```bash
docker-compose logs --tail=50 matchodds-service
```

---

## ??? PostgreSQL

### Connexion à PostgreSQL
```bash
docker exec -it postgres psql -U postgres
```

### Lister les bases de données
```sql
\l
```

### Se connecter à une base
```sql
\c MatchOddsDb
```

### Lister les tables
```sql
\dt
```

### Voir les données d'une table
```sql
SELECT * FROM "Teams";
SELECT * FROM "Matches";
SELECT * FROM "Odds";
```

### Vérifier l'état de PostgreSQL
```bash
docker exec postgres pg_isready -U postgres
```

### Lister toutes les bases depuis la ligne de commande
```bash
docker exec postgres psql -U postgres -c "\l"
```

---

## ?? RabbitMQ

### Vérifier l'état de RabbitMQ
```bash
docker exec rabbitmq rabbitmq-diagnostics ping
```

### Lister les queues
```bash
docker exec rabbitmq rabbitmqctl list_queues
```

### Lister les exchanges
```bash
docker exec rabbitmq rabbitmqctl list_exchanges
```

### Lister les bindings
```bash
docker exec rabbitmq rabbitmqctl list_bindings
```

### Voir les connexions actives
```bash
docker exec rabbitmq rabbitmqctl list_connections
```

### Voir les canaux (channels)
```bash
docker exec rabbitmq rabbitmqctl list_channels
```

### Purger une queue
```bash
docker exec rabbitmq rabbitmqctl purge_queue q.bet-result.new-bets
```

### Interface Web Management
```
http://localhost:15672
user / password
```

---

## ?? Tests API

### Vérifier que l'API répond
```powershell
curl http://localhost:8080/swagger -UseBasicParsing
```

### Tester un endpoint (GET Teams)
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/Teams" -Method GET
```

### Créer une équipe (POST)
```powershell
$team = @{
    name = "Paris SG"
    code = "PSG"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/Teams" `
    -Method POST `
    -Body $team `
    -ContentType "application/json"
```

### Ouvrir Swagger dans le navigateur
```powershell
Start-Process "http://localhost:8080/swagger"
```

---

## ?? Debugging

### Entrer dans un conteneur
```bash
docker exec -it matchodds-service /bin/sh
docker exec -it postgres /bin/bash
docker exec -it rabbitmq /bin/sh
```

### Inspecter un conteneur
```bash
docker inspect matchodds-service
docker inspect postgres
docker inspect rabbitmq
```

### Voir les processus dans un conteneur
```bash
docker top matchodds-service
```

### Statistiques d'utilisation
```bash
docker stats
```

### Vérifier les volumes
```bash
docker volume ls
docker volume inspect trdservices_postgres_data
docker volume inspect trdservices_rabbitmq_data
```

---

## ?? Nettoyage

### Supprimer les images non utilisées
```bash
docker image prune -a
```

### Supprimer les volumes non utilisés
```bash
docker volume prune
```

### Nettoyage complet du système
```bash
docker system prune -a --volumes
```

### Supprimer tout ce qui concerne le projet
```bash
docker-compose down --volumes --rmi all
```

---

## ?? Rebuild Complet

### Méthode 1 : Rebuild propre
```bash
docker-compose down --volumes
docker-compose build --no-cache
docker-compose up -d
```

### Méthode 2 : Avec nettoyage Docker
```bash
docker-compose down --volumes
docker system prune -a -f --volumes
docker-compose build --no-cache
docker-compose up -d
```

### Méthode 3 : Utiliser le script
```bash
# Windows
start.bat

# Linux/Mac
./start.sh
```

---

## ?? Monitoring en Continu

### Surveiller tous les logs en temps réel
```bash
docker-compose logs -f
```

### Surveiller l'état des services (boucle)
```bash
watch docker-compose ps
```

### Surveiller les ressources
```bash
docker stats
```

---

## ?? Dépannage Rapide

### Le service ne démarre pas
```bash
# Voir les erreurs
docker-compose logs matchodds-service

# Redémarrer
docker-compose restart matchodds-service

# Rebuild complet
docker-compose build matchodds-service
docker-compose up -d matchodds-service
```

### Problème de connexion à PostgreSQL
```bash
# Vérifier PostgreSQL
docker exec postgres pg_isready -U postgres

# Voir les logs
docker-compose logs postgres

# Redémarrer
docker-compose restart postgres
```

### Problème avec RabbitMQ
```bash
# Vérifier RabbitMQ
docker exec rabbitmq rabbitmq-diagnostics ping

# Voir les logs
docker-compose logs rabbitmq

# Redémarrer
docker-compose restart rabbitmq
```

### Port déjà utilisé
```bash
# Voir qui utilise le port 8080
netstat -ano | findstr :8080

# Arrêter Docker Compose
docker-compose down

# Redémarrer
docker-compose up -d
```

---

## ?? Export/Import de Données

### Backup PostgreSQL
```bash
docker exec postgres pg_dump -U postgres MatchOddsDb > backup.sql
```

### Restore PostgreSQL
```bash
cat backup.sql | docker exec -i postgres psql -U postgres MatchOddsDb
```

### Export d'une table
```bash
docker exec postgres psql -U postgres MatchOddsDb -c "COPY \"Teams\" TO STDOUT CSV HEADER" > teams.csv
```

---

## ?? Scripts Disponibles

### Windows
- `start.bat` - Démarrer tous les services (rebuild complet)
- `check-status.bat` - Vérifier l'état de tous les services

### Linux/Mac
- `start.sh` - Démarrer tous les services (rebuild complet)
- `check-status.sh` - Vérifier l'état de tous les services

---

## ?? URLs Importantes

| Service | URL | Credentials |
|---------|-----|-------------|
| Swagger UI | http://localhost:8080 | - |
| MatchOdds API | http://localhost:8080 | - |
| RabbitMQ Management | http://localhost:15672 | user / password |
| PostgreSQL | localhost:5432 | postgres / password |
| RabbitMQ AMQP | localhost:5672 | user / password |


---

**?? Astuce** : Gardez ce fichier ouvert pendant le développement pour un accès rapide aux commandes !
