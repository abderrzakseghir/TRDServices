# Guide de Test - API MatchOdds via Swagger

## ? Swagger est maintenant fonctionnel !

**?? URL MISE À JOUR** : **http://localhost:8080** (sans /swagger)

**URL Swagger UI** : http://localhost:8080

**URL OpenAPI JSON** : http://localhost:8080/swagger/v1/swagger.json


---

## ?? Endpoints Disponibles

### **Teams (Équipes)**
- `GET /api/Teams` - Liste toutes les équipes
- `POST /api/Teams` - Créer une nouvelle équipe
- `GET /api/Teams/{id}` - Obtenir une équipe par ID
- `PUT /api/Teams/{id}` - Mettre à jour une équipe
- `DELETE /api/Teams/{id}` - Supprimer une équipe

### **Matches (Matchs)**
- `GET /api/Matches` - Liste tous les matchs
- `POST /api/Matches` - Créer un nouveau match
- `GET /api/Matches/{id}` - Obtenir un match par ID
- `PATCH /api/Matches/{id}` - Mettre à jour partiellement un match
- `DELETE /api/Matches/{id}` - Supprimer un match

### **Odds (Cotes)**
- `POST /api/Odds` - Créer des cotes pour un match
- `GET /api/Odds/{id}` - Obtenir des cotes par ID
- `PATCH /api/Odds/{id}` - Mettre à jour des cotes
- `DELETE /api/Odds/{id}` - Supprimer des cotes

---

## ?? Scénario de Test Complet

### **Étape 1 : Créer deux équipes**

**Requête** : `POST /api/Teams`

**Team 1** (PSG) :
```json
{
  "name": "Paris Saint-Germain",
  "code": "PSG"
}
```

**Team 2** (OM) :
```json
{
  "name": "Olympique de Marseille",
  "code": "OM"
}
```

**Réponse attendue** : Status 201 avec les IDs générés (ex: id: 1 et 2)

---

### **Étape 2 : Vérifier les équipes créées**

**Requête** : `GET /api/Teams`

**Réponse attendue** :
```json
[
  {
    "id": 1,
    "name": "Paris Saint-Germain",
    "code": "PSG"
  },
  {
    "id": 2,
    "name": "Olympique de Marseille",
    "code": "OM"
  }
]
```

---

### **Étape 3 : Créer un match**

**Requête** : `POST /api/Matches`

```json
{
  "matchDate": "2026-02-15T20:00:00Z",
  "status": "SCHEDULED",
  "homeTeamId": 1,
  "awayTeamId": 2
}
```

**Réponse attendue** : Status 201 avec l'ID du match (ex: id: 1)

---

### **Étape 4 : Créer des cotes pour le match**

**Requête** : `POST /api/Odds`

```json
{
  "homeWin": 1.85,
  "draw": 3.40,
  "awayWin": 4.20,
  "matchId": 1
}
```

**Réponse attendue** : Status 201 avec l'ID des cotes

---

### **Étape 5 : Récupérer tous les matchs avec détails**

**Requête** : `GET /api/Matches`

**Réponse attendue** :
```json
[
  {
    "id": 1,
    "matchDate": "2026-02-15T20:00:00Z",
    "status": "SCHEDULED",
    "homeTeamId": 1,
    "awayTeamId": 2,
    "homeTeam": {
      "id": 1,
      "name": "Paris Saint-Germain",
      "code": "PSG"
    },
    "awayTeam": {
      "id": 2,
      "name": "Olympique de Marseille",
      "code": "OM"
    },
    "odds": {
      "id": 1,
      "homeWin": 1.85,
      "draw": 3.40,
      "awayWin": 4.20,
      "matchId": 1
    }
  }
]
```

---

### **Étape 6 : Mettre à jour le statut du match**

**Requête** : `PATCH /api/Matches/1`

```json
{
  "status": "FINISHED"
}
```

**Réponse attendue** : Status 204 (No Content)

---

### **Étape 7 : Mettre à jour les cotes**

**Requête** : `PATCH /api/Odds/1`

```json
{
  "homeWin": 1.75,
  "draw": 3.50,
  "awayWin": 4.50
}
```

---

## ?? Tests via Swagger UI

1. Ouvrez votre navigateur : **http://localhost:8080**

2. Vous verrez l'interface Swagger UI avec tous les endpoints


3. Pour tester un endpoint :
   - Cliquez sur l'endpoint (ex: POST /api/Teams)
   - Cliquez sur "Try it out"
   - Remplissez le corps de la requête
   - Cliquez sur "Execute"
   - Consultez la réponse

---

## ?? Tests via cURL (PowerShell)

### Créer une équipe
```powershell
$body = @{
    name = "Paris Saint-Germain"
    code = "PSG"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/Teams" `
    -Method POST `
    -Body $body `
    -ContentType "application/json"
```

### Lister les équipes
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/Teams" -Method GET
```

### Créer un match
```powershell
$match = @{
    matchDate = "2026-02-15T20:00:00Z"
    status = "SCHEDULED"
    homeTeamId = 1
    awayTeamId = 2
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/Matches" `
    -Method POST `
    -Body $match `
    -ContentType "application/json"
```

### Créer des cotes
```powershell
$odds = @{
    homeWin = 1.85
    draw = 3.40
    awayWin = 4.20
    matchId = 1
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/Odds" `
    -Method POST `
    -Body $odds `
    -ContentType "application/json"
```

---

## ?? Tester l'intégration RabbitMQ

### Vérifier les queues RabbitMQ
```bash
docker exec rabbitmq rabbitmqctl list_queues
```

### Surveiller les messages dans RabbitMQ Management UI
1. Ouvrez : **http://localhost:15672**
2. Connectez-vous : `user` / `password`
3. Allez dans l'onglet "Queues"
4. Cliquez sur une queue pour voir les messages

### Publier un événement de test (ScoreService)
Le ScoreService publie des événements `match.finished` vers RabbitMQ.
Le BetResultService consomme ces événements depuis la queue `q.bet-result.match-scores`.

---

## ? Validation Complète

### Services fonctionnels
- [x] PostgreSQL - 3 bases de données
- [x] RabbitMQ - Queues et exchanges configurés
- [x] MatchOdds API - CRUD complet
- [x] Swagger UI - Documentation interactive
- [x] Migrations automatiques

### Points de vérification
```powershell
# Vérifier l'état de tous les conteneurs
docker-compose ps

# Tester Swagger
Start-Process "http://localhost:8080/swagger"

# Tester RabbitMQ Management
Start-Process "http://localhost:15672"

# Vérifier les logs
docker-compose logs -f
```

---

## ?? Dépannage

### Swagger ne charge pas
```bash
# Redémarrer le service
docker-compose restart matchodds-service

# Voir les logs
docker-compose logs matchodds-service
```

### L'API ne répond pas
```bash
# Vérifier que le conteneur est running
docker-compose ps matchodds-service

# Vérifier les ports
netstat -an | findstr :8080
```

### Erreur de connexion à la base de données
```bash
# Vérifier PostgreSQL
docker exec postgres pg_isready -U postgres

# Voir les logs de migration
docker-compose logs matchodds-service | findstr "Migration"
```

---

**?? L'API est maintenant complètement fonctionnelle avec Swagger !**

Vous pouvez maintenant tester toutes les fonctionnalités directement depuis :
?? **http://localhost:8080**

?? **Note** : L'URL a changé - c'est maintenant à la racine (sans `/swagger`)

