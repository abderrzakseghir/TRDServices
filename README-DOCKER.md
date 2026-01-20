# ?? Configuration Docker Complète - Projet TRD Services

## ? TOUT EST MAINTENANT FONCTIONNEL !

Tous les services microservices sont configurés, déployés et testés avec succès.

---

## ?? Démarrage Rapide

### Option 1 : Script automatique (Recommandé)
```bash
# Windows
start.bat

# Linux/Mac
chmod +x start.sh
./start.sh
```

### Option 2 : Commande manuelle
```bash
docker-compose up -d
```

---

## ?? URLs des Services

| Service | URL | Identifiants |
|---------|-----|--------------|
| **Swagger UI** ? | **http://localhost:8080** | - |
| RabbitMQ Management | http://localhost:15672 | user / password |
| PostgreSQL | localhost:5432 | postgres / password |

---

## ?? État des Services

```
? PostgreSQL          - 3 bases de données créées automatiquement
? RabbitMQ            - Queues et exchanges pré-configurés
? MatchOdds API       - API REST complète + Swagger UI
? Score Service       - Producer RabbitMQ actif
? Bet Result Service  - Consumer RabbitMQ actif
```

---

## ?? Documentation Disponible

### Pour Commencer
1. **SWAGGER-CORRECTION-FINALE.md** ? - Guide de démarrage rapide
2. **GUIDE-TEST-SWAGGER.md** - Tests complets de l'API

### Référence
3. **COMMANDES-PRATIQUES.md** - Commandes Docker quotidiennes
4. **DOCKER-README.md** - Documentation complète
5. **ETAT_DES_SERVICES.md** - État détaillé de tous les services

### Scripts
- **start.bat / start.sh** - Démarrer tous les services
- **check-status.bat / check-status.sh** - Vérifier l'état

---

## ?? Test Rapide

### Tester Swagger UI
```powershell
Start-Process "http://localhost:8080"
```

### Créer une équipe via API
```powershell
$team = @{
    name = "Paris Saint-Germain"
    code = "PSG"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/Teams" `
    -Method POST `
    -Body $team `
    -ContentType "application/json"
```

### Lister les équipes
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/Teams"
```

---

## ?? Gestion des Services

### Voir l'état
```bash
docker-compose ps
```

### Voir les logs en temps réel
```bash
docker-compose logs -f
```

### Redémarrer un service
```bash
docker-compose restart matchodds-service
```

### Tout arrêter
```bash
docker-compose down
```

### Tout nettoyer (volumes inclus)
```bash
docker-compose down --volumes
```

---

## ??? Bases de Données PostgreSQL

Trois bases créées automatiquement au démarrage :
- **betresult** - BetResultService
- **ScoreServiceDb** - ScoreService  
- **MatchOddsDb** - MatchOddsService

Les migrations Entity Framework sont appliquées automatiquement avec retry logic.

---

## ?? RabbitMQ

### Queues pré-configurées
- `q.bet-result.new-bets` (routing: `bet.placed`)
- `q.bet-result.match-scores` (routing: `match.finished`)

### Exchange
- `sportsbook.topic` (type: topic)

### Management UI
http://localhost:15672 (user / password)

---

## ? Fonctionnalités Clés

### ? Configuration Automatique
- Bases de données créées automatiquement via `init-db.sql`
- RabbitMQ configuré via `rabbitmq-definitions.json`
- Migrations EF appliquées automatiquement avec retry

### ? Healthchecks
- PostgreSQL vérifié avant démarrage des services
- RabbitMQ vérifié avant démarrage des consumers
- Restart automatique en cas d'échec

### ? Retry Logic
- 10 tentatives de migration avec 3s de délai
- Logs détaillés pour debugging

### ? Images Docker Propres
- Build multi-stage optimisé
- Images Alpine pour taille minimale
- Aucune dépendance externe manuelle

---

## ?? Endpoints API Disponibles

### Teams (Équipes)
- `GET /api/Teams` - Liste
- `POST /api/Teams` - Créer
- `GET /api/Teams/{id}` - Détail
- `PUT /api/Teams/{id}` - Modifier
- `DELETE /api/Teams/{id}` - Supprimer

### Matches (Matchs)
- `GET /api/Matches` - Liste
- `POST /api/Matches` - Créer
- `GET /api/Matches/{id}` - Détail
- `PATCH /api/Matches/{id}` - Modifier
- `DELETE /api/Matches/{id}` - Supprimer

### Odds (Cotes)
- `GET /api/Odds` - Liste
- `POST /api/Odds` - Créer
- `GET /api/Odds/{id}` - Détail
- `PATCH /api/Odds/{id}` - Modifier
- `DELETE /api/Odds/{id}` - Supprimer

**Testez-les tous dans Swagger UI** : http://localhost:8080

---

## ?? Dépannage

### Service ne démarre pas
```bash
docker-compose logs <service-name>
docker-compose restart <service-name>
```

### Rebuild complet
```bash
docker-compose down --volumes
docker-compose build --no-cache
docker-compose up -d
```

### Vérifier PostgreSQL
```bash
docker exec postgres pg_isready -U postgres
```

### Vérifier RabbitMQ
```bash
docker exec rabbitmq rabbitmq-diagnostics ping
docker exec rabbitmq rabbitmqctl list_queues
```

---

## ?? Notes Importantes

### ?? Configuration ScoreService
Le ScoreService nécessite un token API Football-Data valide.
Pour configurer : Éditez `ScoreService/appsettings.json` ? `ApiSettings:Token`

### ?? URL Swagger Mise à Jour
L'URL Swagger est maintenant : **http://localhost:8080** (sans `/swagger`)

---

## ?? Architecture

```
???????????????????
?  MatchOdds API  ? :8080 (Swagger UI)
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

---

## ? Checklist de Validation

- [x] PostgreSQL démarré et healthy
- [x] RabbitMQ démarré avec queues configurées
- [x] MatchOdds API accessible
- [x] Swagger UI fonctionnel à http://localhost:8080
- [x] Migrations automatiques appliquées
- [x] Test de création d'équipe réussi
- [x] ScoreService connecté à RabbitMQ
- [x] BetResultService consommant les messages

---

## ?? Résultat Final

**Tous les services sont opérationnels !**

?? **Commencez par ouvrir** : http://localhost:8080

?? **Consultez** : `GUIDE-TEST-SWAGGER.md` pour un tutoriel complet

?? **Référence** : `COMMANDES-PRATIQUES.md` pour les commandes quotidiennes

---

**Fait avec ?? - Configuration Docker complète et testée**
