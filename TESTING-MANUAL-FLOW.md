# ?? Guide de Test Manuel - Flux Complet de l'Application TRDServices

Ce guide détaille les étapes pour tester manuellement le flux complet de bout en bout de l'application de paris sportifs.

## ?? Prérequis

- Docker et Docker Compose installés
- Un outil pour les requêtes HTTP (curl, Postman, ou l'extension REST Client VSCode)
- Accès au RabbitMQ Management UI

---

## ??? Architecture du Flux

```
???????????????????      ?????????????????      ????????????????????
? MockScoreService???????? ScoreService  ????????    RabbitMQ      ?
?   (Matchs Mock) ?      ?(Sync & Store) ?      ? (match.finished) ?
???????????????????      ?????????????????      ????????????????????
                                                         ?
???????????????????      ?????????????????               ?
? MatchOddsService?      ?BetResultService????????????????
?   (Cotes/Odds)  ?      ?(Calcul gains) ?????? bet.placed
???????????????????      ?????????????????
```

---

## ?? Étape 0 : Démarrage de l'Infrastructure

### 0.1 Lancer tous les services

```bash
cd C:\Users\SEGHIR Aderrazak\source\repos\TRDServices
docker-compose up -d --build
```

### 0.2 Vérifier que tous les services sont en cours d'exécution

```bash
docker-compose ps
```

**Résultat attendu** : Tous les services sont `Up` et `healthy`

| Service | Port | URL |
|---------|------|-----|
| PostgreSQL | 5432 | - |
| RabbitMQ | 5672 / 15672 | http://localhost:15672 |
| MockScoreService API | 5000 | http://localhost:5000 |
| MockScoreService React | 3000 | http://localhost:3000 |
| MatchOddsService | 8080 | http://localhost:8080 |
| ScoreService | - | (Background Worker) |
| BetResultService | - | (Background Worker) |

### 0.3 Accéder à RabbitMQ Management

- **URL** : http://localhost:15672
- **Utilisateur** : `user`
- **Mot de passe** : `password`

Vérifier que les queues et l'exchange sont créés :
- Exchange : `sportsbook.topic`
- Queues : `q.bet-result.new-bets` et `q.bet-result.match-scores`

---

## ?? Étape 1 : Création des Matchs Mock

### 1.1 Consulter les équipes disponibles

```bash
curl -X GET http://localhost:5000/v4/teams
```

**Réponse attendue** : Liste des équipes avec leurs IDs

### 1.2 Créer un nouveau match

```bash
curl -X POST http://localhost:5000/v4/matches \
  -H "Content-Type: application/json" \
  -d '{
    "homeTeamId": 1,
    "awayTeamId": 2,
    "utcDate": "2025-01-26T20:00:00Z",
    "matchday": 1,
    "stage": "REGULAR_SEASON"
  }'
```

**Réponse attendue** : 
```json
{
  "id": <MATCH_ID>,
  "message": "Match created successfully"
}
```

> ?? **IMPORTANT** : Notez le `MATCH_ID` retourné, vous en aurez besoin pour les étapes suivantes.

### 1.3 Vérifier le match créé

```bash
curl -X GET http://localhost:5000/v4/matches
```

---

## ?? Étape 2 : Le ScoreService Récupère et Stocke les Matchs

Le `ScoreService` est un worker en arrière-plan qui synchronise périodiquement les matchs depuis le MockScoreService.

### 2.1 Consulter les logs du ScoreService

```bash
docker logs -f score-service
```

**Logs attendus** :
```
Récupération des matchs depuis l'API...
Nouveau match ajouté: [HomeTeam] vs [AwayTeam]
```

### 2.2 Vérifier la base de données (optionnel)

```bash
docker exec -it postgres psql -U postgres -d ScoreServiceDb -c "SELECT * FROM \"Matches\";"
```

---

## ?? Étape 3 : Création des Cotes avec MatchOddsService

### 3.1 Créer ou synchroniser le match dans MatchOddsService

D'abord, récupérer ou créer les équipes si nécessaire, puis créer le match :

```bash
curl -X POST http://localhost:8080/api/matches \
  -H "Content-Type: application/json" \
  -d '{
    "homeTeamId": 1,
    "awayTeamId": 2,
    "matchDate": "2025-01-26T20:00:00Z"
  }'
```

### 3.2 Créer les cotes pour le match

```bash
curl -X POST http://localhost:8080/api/odds \
  -H "Content-Type: application/json" \
  -d '{
    "matchId": <MATCH_ID>,
    "homeWin": 1.85,
    "awayWin": 4.20,
    "draw": 3.50
  }'
```

**Réponse attendue** :
```json
{
  "id": 1,
  "matchId": <MATCH_ID>,
  "homeWin": 1.85,
  "awayWin": 4.20,
  "draw": 3.50
}
```

### 3.3 Vérifier les matchs avec leurs cotes

```bash
curl -X GET http://localhost:8080/api/matches
```

---

## ?? Étape 4 : Simulation du Service Collègue - Injection du Message "bet.placed"

Cette étape simule le service de votre collègue qui envoie une confirmation de pari.

### 4.1 Accéder à RabbitMQ Management UI

1. Ouvrir http://localhost:15672
2. Se connecter avec `user` / `password`
3. Aller dans l'onglet **"Exchanges"**
4. Cliquer sur l'exchange **`sportsbook.topic`**

### 4.2 Publier le message "bet.placed"

Dans la section **"Publish message"** :

**Routing key** :
```
bet.placed
```

**Properties** :
```
content_type=application/json
```

**Payload** (message JSON) :

```json
{
  "betId": 123456,
  "accountId": 1001,
  "amount": 50.00,
  "selections": [
    {
      "matchId": "<MATCH_ID>",
      "marketName": "1X2",
      "selectionName": "<NOM_EQUIPE_ATTENDUE>",
      "odd": 1.85
    }
  ]
}
```

> ?? **IMPORTANT** :
> - Remplacez `<MATCH_ID>` par l'ID du match créé à l'étape 1
> - Remplacez `<NOM_EQUIPE_ATTENDUE>` par le **nom exact** de l'équipe sur laquelle vous pariez (ex: "Paris Saint-Germain")
> - Le `selectionName` doit correspondre EXACTEMENT au nom de l'équipe dans le système

### 4.3 Exemple concret de payload

Si vous pariez sur l'équipe "Paris Saint-Germain" (équipe à domicile) pour le match ID 1 :

```json
{
  "betId": 123456,
  "accountId": 1001,
  "amount": 50.00,
  "selections": [
    {
      "matchId": "1",
      "marketName": "1X2",
      "selectionName": "Paris Saint-Germain",
      "odd": 1.85
    }
  ]
}
```

### 4.4 Vérifier la réception du pari

Consulter les logs du BetResultService :

```bash
docker logs -f bet-result-service
```

**Logs attendus** :
```
Message reçu [bet.placed]
Nouveau pari reçu: ID 123456 pour 50.00€
```

### 4.5 Vérifier le pari en base de données

```bash
docker exec -it postgres psql -U postgres -d betresult -c "SELECT * FROM \"Bets\";"
docker exec -it postgres psql -U postgres -d betresult -c "SELECT * FROM \"BetSelections\";"
```

**Résultat attendu** : Le pari est en statut `PENDING`

---

## ?? Étape 5 : Démarrage et Simulation du Match

### 5.1 Passer le match en statut "IN_PLAY"

```bash
curl -X PATCH http://localhost:5000/v4/matches/<MATCH_ID>/status \
  -H "Content-Type: application/json" \
  -d '{"status": "IN_PLAY"}'
```

### 5.2 Mettre à jour le score

```bash
curl -X PATCH http://localhost:5000/v4/matches/<MATCH_ID>/score \
  -H "Content-Type: application/json" \
  -d '{"homeScore": 2, "awayScore": 1}'
```

---

## ?? Étape 6 : Terminer le Match et Déclencher le Calcul des Gains

### 6.1 Passer le match en statut "FINISHED"

```bash
curl -X PATCH http://localhost:5000/v4/matches/<MATCH_ID>/status \
  -H "Content-Type: application/json" \
  -d '{"status": "FINISHED"}'
```

### 6.2 Attendre la synchronisation du ScoreService

Le ScoreService va détecter que le match est terminé et publier un événement `match.finished`.

Consulter les logs :

```bash
docker logs -f score-service
```

**Logs attendus** :
```
Mise à jour match [HomeTeam]-[AwayTeam]: 2-1
[RabbitMQ] Événement publié : Match <MATCH_ID> terminé.
```

### 6.3 Vérifier le message dans RabbitMQ

Dans RabbitMQ Management UI, vérifier la queue `q.bet-result.match-scores` :
- Le message `match.finished` doit avoir été consommé par le BetResultService

### 6.4 Vérifier le calcul du résultat du pari

Consulter les logs du BetResultService :

```bash
docker logs -f bet-result-service
```

**Logs attendus (si le pari est gagnant)** :
```
Message reçu [match.finished]
[DEBUG] Traitement résultat match <MATCH_ID>: Paris Saint-Germain 2 - 1 Olympique Marseille
[DEBUG] Paris PENDING trouvés pour match <MATCH_ID>: 1
Pari 123456 GAGNÉ ! Gain: 92.50€
```

**Logs attendus (si le pari est perdant)** :
```
Pari 123456 PERDU (Match <MATCH_ID>)
```

### 6.5 Vérifier le statut final du pari en base de données

```bash
docker exec -it postgres psql -U postgres -d betresult -c "SELECT \"ExternalBetId\", \"Status\", \"Amount\", \"Payout\" FROM \"Bets\";"
```

**Résultat attendu** :
| ExternalBetId | Status | Amount | Payout |
|---------------|--------|--------|--------|
| 123456 | WON | 50.00 | 92.50 |

---

## ?? Étape 7 : Scénarios de Test Complémentaires

### 7.1 Tester un pari perdant

Répéter les étapes 4-6 avec un `selectionName` différent du vainqueur.

Exemple : Parier sur "Olympique Marseille" alors que le score final est 2-1 pour Paris Saint-Germain.

```json
{
  "betId": 789012,
  "accountId": 1002,
  "amount": 25.00,
  "selections": [
    {
      "matchId": "1",
      "marketName": "1X2",
      "selectionName": "Olympique Marseille",
      "odd": 4.20
    }
  ]
}
```

### 7.2 Tester un pari combiné (plusieurs sélections)

```json
{
  "betId": 111222,
  "accountId": 1003,
  "amount": 10.00,
  "selections": [
    {
      "matchId": "1",
      "marketName": "1X2",
      "selectionName": "Paris Saint-Germain",
      "odd": 1.85
    },
    {
      "matchId": "2",
      "marketName": "1X2",
      "selectionName": "Lyon",
      "odd": 2.10
    }
  ]
}
```

> Note : Un pari combiné est gagné seulement si TOUTES les sélections sont gagnantes.

### 7.3 Tester un match nul

1. Créer un pari avec `selectionName` = "DRAW"
2. Terminer le match avec un score égal (ex: 1-1)
3. Vérifier que le pari est marqué comme gagné

---

## ?? Dépannage

### Les messages ne sont pas consommés

```bash
# Vérifier les connexions RabbitMQ
docker logs bet-result-service | grep -i "rabbit"
docker logs score-service | grep -i "rabbit"
```

### Le pari n'est pas traité

1. Vérifier que le `matchId` dans le message correspond à l'ID en base
2. Vérifier que le `selectionName` correspond exactement au nom de l'équipe

### Réinitialiser l'environnement

```bash
docker-compose down -v
docker-compose up -d --build
```

---

## ?? Récapitulatif des Endpoints

| Service | Méthode | Endpoint | Description |
|---------|---------|----------|-------------|
| MockScoreService | GET | `/v4/matches` | Liste des matchs |
| MockScoreService | POST | `/v4/matches` | Créer un match |
| MockScoreService | PATCH | `/v4/matches/{id}/status` | Modifier le statut |
| MockScoreService | PATCH | `/v4/matches/{id}/score` | Modifier le score |
| MockScoreService | GET | `/v4/teams` | Liste des équipes |
| MatchOddsService | GET | `/api/matches` | Liste des matchs avec cotes |
| MatchOddsService | POST | `/api/odds` | Créer des cotes |

---

## ?? Format des Messages RabbitMQ

### Message `bet.placed` (routing key: `bet.placed`)

```json
{
  "betId": 123456,
  "accountId": 1001,
  "amount": 50.00,
  "selections": [
    {
      "matchId": "1",
      "marketName": "1X2",
      "selectionName": "Paris Saint-Germain",
      "odd": 1.85
    }
  ]
}
```

### Message `match.finished` (routing key: `match.finished`)

```json
{
  "matchId": 1,
  "homeTeam": "Paris Saint-Germain",
  "awayTeam": "Olympique Marseille",
  "homeScore": 2,
  "awayScore": 1,
  "status": "FINISHED",
  "occurredAt": "2025-01-26T22:00:00Z"
}
```

---

## ? Checklist de Validation

- [ ] Infrastructure démarrée et healthy
- [ ] Match créé dans MockScoreService
- [ ] Match synchronisé dans ScoreService
- [ ] Cotes créées dans MatchOddsService
- [ ] Message `bet.placed` injecté via RabbitMQ
- [ ] Pari enregistré en statut PENDING
- [ ] Match passé en IN_PLAY puis FINISHED
- [ ] Message `match.finished` publié par ScoreService
- [ ] BetResultService a calculé le résultat
- [ ] Statut du pari mis à jour (WON/LOST) avec payout correct

---

## ?? Notes Importantes

1. **Correspondance des noms d'équipes** : Le `selectionName` doit correspondre **exactement** au nom de l'équipe dans la base (sensible à la casse et aux espaces).

2. **Timing de synchronisation** : Le ScoreService synchronise périodiquement. Il peut y avoir un délai entre la mise à jour du match et la publication de l'événement.

3. **Logs de debug** : Le BetResultService inclut des logs `[DEBUG]` pour faciliter le diagnostic.
