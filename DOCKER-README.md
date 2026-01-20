# Configuration Docker pour Microservices

Ce projet utilise Docker Compose pour orchestrer tous les services nécessaires.

## Prérequis

- Docker Desktop installé et démarré
- Docker Compose (inclus avec Docker Desktop)

## Démarrage Rapide

### Windows
```bash
start.bat
```

### Linux/Mac
```bash
chmod +x start.sh
./start.sh
```

### Commande manuelle
```bash
docker-compose up -d --build
```

## Services Disponibles

| Service | Port | Description |
|---------|------|-------------|
| PostgreSQL | 5432 | Base de données (postgres/password) |
| RabbitMQ | 5672 | Message broker |
| RabbitMQ Management | 15672 | Interface web (user/password) |
| MatchOdds API | 8080 | API REST |
| MatchOdds Swagger | 8080/swagger | Documentation API |

## Bases de Données

Trois bases de données sont automatiquement créées au démarrage :
- `betresult` - BetResultService
- `ScoreServiceDb` - ScoreService
- `MatchOddsDb` - MatchOddsService

Les migrations Entity Framework sont appliquées automatiquement au démarrage de chaque service.

## RabbitMQ

RabbitMQ est pré-configuré avec :
- Exchange : `sportsbook.topic` (type: topic)
- Queue : `q.bet-result.new-bets` (binding: bet.placed)
- Queue : `q.bet-result.match-scores` (binding: match.finished)

Accédez à l'interface de gestion : http://localhost:15672
- Utilisateur : `user`
- Mot de passe : `password`

## Commandes Utiles

### Voir les logs de tous les services
```bash
docker-compose logs -f
```

### Voir les logs d'un service spécifique
```bash
docker-compose logs -f matchodds-service
docker-compose logs -f score-service
docker-compose logs -f bet-result-service
```

### Arrêter tous les services
```bash
docker-compose down
```

### Arrêter et supprimer les volumes
```bash
docker-compose down --volumes
```

### Rebuild un service spécifique
```bash
docker-compose up -d --build matchodds-service
```

### Redémarrer un service
```bash
docker-compose restart matchodds-service
```

## Résolution de Problèmes

### Les migrations ne s'appliquent pas
Les services réessaient automatiquement 10 fois avec un délai de 3 secondes. Vérifiez les logs :
```bash
docker-compose logs -f [nom-du-service]
```

### RabbitMQ ne démarre pas
Attendez 30 secondes - RabbitMQ prend du temps à initialiser. Vérifiez le healthcheck :
```bash
docker ps
```

### PostgreSQL refuse les connexions
Vérifiez que le healthcheck est OK :
```bash
docker ps
```

### Nettoyer complètement Docker
```bash
docker-compose down --volumes
docker system prune -a -f --volumes
```

## Architecture

```
???????????????????
?  MatchOdds API  ? :8080
???????????????????
         ?
    ???????????
    ?         ?
????????? ???????????
? Score ? ?   Bet   ?
?Service? ? Result  ?
????????? ???????????
    ?          ?
    ????????????
         ?
    ????????????
    ? RabbitMQ ? :5672, :15672
    ????????????
         ?
    ?????????????
    ?PostgreSQL ? :5432
    ?????????????
```

## Configuration

Les variables d'environnement sont définies dans `docker-compose.yml`.
Les valeurs par défaut dans `appsettings.json` sont écrasées par les variables d'environnement Docker.

### Modifier les credentials PostgreSQL
Éditez `docker-compose.yml` et `init-db.sql` :
```yaml
environment:
  POSTGRES_PASSWORD: votre-mot-de-passe
```

### Modifier les credentials RabbitMQ
Éditez `docker-compose.yml` et `rabbitmq-definitions.json` :
```yaml
environment:
  RABBITMQ_DEFAULT_USER: votre-user
  RABBITMQ_DEFAULT_PASS: votre-password
```

## Développement

Pour développer localement sans Docker, modifiez les `appsettings.json` :
- PostgreSQL : `Host=localhost;Port=5432;...`
- RabbitMQ : `HostName=localhost`

Les healthchecks garantissent que :
1. PostgreSQL est prêt avant de démarrer les services
2. RabbitMQ est prêt avant de démarrer les consumers
3. Les migrations sont appliquées avec retry automatique
