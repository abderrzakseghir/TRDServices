# ?? MockScoreService - Simulateur API Football

Application complète de simulation de l'API football-data.org avec interface de gestion en temps réel.

## ??? Architecture

```
???????????????????????????
?  MockScoreService.Web   ?
?  (Blazor + MudBlazor)   ? :5001
?  Interface de Gestion   ?
???????????????????????????
            ?
            ?
???????????????????????????
?  MockScoreService.API   ?
?  (ASP.NET Core API)     ? :5000
?  Endpoint /v4/matches   ?
???????????????????????????
            ?
            ?
    ?????????????????
    ? SQLite DB     ?
    ? football.db   ?
    ?????????????????
```

---

## ? Fonctionnalités

### Backend API (Port 5000)
- ? **Endpoint `/v4/matches`** - Compatible avec l'API football-data.org
- ? **SQLite Database** - Base de données embarquée
- ? **Migrations automatiques** - Appliquées au démarrage
- ? **Seed data** - 8 équipes et 3 matchs pré-configurés
- ? **Swagger UI** - Documentation API intégrée

### Frontend Blazor (Port 5001)
- ? **Dashboard** - Vue d'ensemble avec statistiques
- ? **Gestion des Matchs** - Créer, modifier, supprimer des matchs
- ? **Simulation Temps Réel** - Contrôle des scores en direct
- ? **MudBlazor** - Interface moderne et responsive
- ? **Auto-refresh** - Mise à jour automatique toutes les 2 secondes

---

## ?? Démarrage

### Option 1 : Docker Compose (Recommandé)

```bash
# Démarrer tous les services
docker-compose up -d

# Les services MockScore démarrent automatiquement
```

**URLs** :
- **Interface Web** : http://localhost:5001
- **API Swagger** : http://localhost:5000/swagger
- **API Endpoint** : http://localhost:5000/v4/matches

### Option 2 : Local

```bash
# Backend API
cd MockScoreService.API
dotnet run

# Frontend Web (nouveau terminal)
cd MockScoreService.Web
dotnet run
```

---

## ?? Pages Disponibles

### 1. Dashboard (/)
- Statistiques des matchs (Total, À venir, En cours, Terminés)
- Actions rapides
- Vue d'ensemble

### 2. Gestion des Matchs (/matches)
- **Créer un match** : Sélectionner 2 équipes + date/heure
- **Liste des matchs** : Tableau avec toutes les informations
- **Supprimer** : Bouton de suppression pour chaque match
- **Filtres** : Par statut (Scheduled, In Play, Finished)

### 3. Simulation Temps Réel (/simulation)
- **Cards par match** : Affichage en grille responsive
- **Contrôles par équipe** :
  - Boutons `[+]` et `[-]` pour modifier les scores
  - Score affiché en temps réel
- **Actions de match** :
  - `Démarrer` : SCHEDULED ? IN_PLAY
  - `Terminer` : IN_PLAY ? FINISHED
  - `Reset` : Remettre les scores à 0
  - `Rejouer` : FINISHED ? SCHEDULED
- **Auto-refresh** : Mise à jour automatique toutes les 2s

---

## ?? Guide d'Utilisation

### Étape 1 : Créer un Match
1. Allez sur `/matches`
2. Sélectionnez **Équipe Domicile** et **Équipe Extérieur**
3. Choisissez une **date** et une **heure**
4. Cliquez sur **Créer le Match**

### Étape 2 : Simuler un Match
1. Allez sur `/simulation`
2. Trouvez le match créé (statut SCHEDULED)
3. Cliquez sur **Démarrer** 
4. Utilisez les boutons **[+]** et **[-]** pour modifier les scores
5. Les changements sont visibles immédiatement dans l'API `/v4/matches`

### Étape 3 : Terminer le Match
1. Cliquez sur **Terminer** quand le match est fini
2. Le statut passe à FINISHED
3. Les scores sont figés

---

## ?? API Endpoints

### GET /v4/matches
Récupère la liste des matchs (compatible football-data.org)

**Paramètres de requête** :
- `status` : SCHEDULED, IN_PLAY, FINISHED
- `dateFrom` : Date de début (format ISO)
- `dateTo` : Date de fin (format ISO)

**Exemple** :
```bash
curl http://localhost:5000/v4/matches?status=IN_PLAY
```

**Réponse** :
```json
{
  "filters": {
    "status": "IN_PLAY",
    "season": "2024"
  },
  "resultSet": {
    "count": 1,
    "first": "2026-01-21",
    "last": "2026-01-21",
    "played": 0
  },
  "competition": {
    "id": 1,
    "name": "Ligue 1",
    "code": "FL1",
    "type": "LEAGUE"
  },
  "matches": [
    {
      "id": 1,
      "utcDate": "2026-01-21T20:00:00Z",
      "status": "IN_PLAY",
      "homeTeam": {
        "id": 1,
        "name": "Paris Saint-Germain FC",
        "shortName": "Paris SG",
        "tla": "PSG"
      },
      "awayTeam": {
        "id": 2,
        "name": "Olympique de Marseille",
        "shortName": "Marseille",
        "tla": "MAR"
      },
      "score": {
        "winner": null,
        "duration": "REGULAR",
        "fullTime": {
          "home": 2,
          "away": 1
        }
      }
    }
  ]
}
```

---

## ??? Base de Données

### Tables
- **Teams** : 8 équipes françaises pré-configurées
- **Competitions** : Ligue 1
- **Seasons** : Saison 2024-2025
- **Matches** : Matchs créés manuellement

### Seed Data
Les équipes suivantes sont pré-chargées :
- Paris Saint-Germain FC (PSG)
- Olympique de Marseille (MAR)
- Olympique Lyonnais (LYO)
- AS Monaco FC (MON)
- LOSC Lille (LIL)
- OGC Nice (NIC)
- Stade Rennais FC (REN)
- RC Lens (LEN)

---

## ?? Intégration avec ScoreService

Le **ScoreService** a été modifié pour pointer vers cette API :

```json
{
  "ApiSettings": {
    "BaseUrl": "http://mock-football-api:5000/",
    "Token": "mock_token_not_needed"
  }
}
```

**Avantages** :
- ? Pas besoin de token API externe
- ? Contrôle total sur les données
- ? Simulation de matchs en temps réel
- ? Tests sans limitations de rate-limit

---

## ??? Développement

### Ajouter une Équipe
Modifiez `FootballDbContext.cs` dans la méthode `SeedData()` :

```csharp
new Team { 
    Id = 9, 
    Name = "Nouvelle Équipe", 
    ShortName = "Nouveau", 
    Tla = "NOU", 
    Crest = "url_crest" 
}
```

### Ajouter des Endpoints
Créez de nouveaux controllers dans `MockScoreService.API/Controllers/`

### Personnaliser l'Interface
Modifiez les pages Razor dans `MockScoreService.Web/Components/Pages/`

---

## ?? Structure du Projet

```
MockScoreService/
??? MockScoreService.API/
?   ??? Controllers/
?   ?   ??? MatchesController.cs
?   ??? Data/
?   ?   ??? FootballDbContext.cs
?   ??? DTOs/
?   ?   ??? ApiResponses.cs
?   ??? Models/
?   ?   ??? FootballModels.cs
?   ??? Migrations/
?   ??? Program.cs
?   ??? Dockerfile
?
??? MockScoreService.Web/
    ??? Components/
    ?   ??? Layout/
    ?   ?   ??? MainLayout.razor
    ?   ??? Pages/
    ?   ?   ??? Home.razor
    ?   ?   ??? Matches.razor
    ?   ?   ??? Simulation.razor
    ?   ??? App.razor
    ??? Program.cs
    ??? Dockerfile
```

---

## ?? Interface MudBlazor

### Composants Utilisés
- **MudCard** : Affichage des matchs
- **MudButton** : Actions (Démarrer, Terminer, etc.)
- **MudIconButton** : Contrôles +/-
- **MudTable** : Liste des matchs
- **MudSelect** : Sélection d'équipes
- **MudDatePicker/TimePicker** : Choix de date/heure
- **MudChip** : Badges de statut
- **MudSnackbar** : Notifications

### Thème
- Thème par défaut MudBlazor
- Responsive design (xs, sm, md, lg)
- Navigation drawer

---

## ?? Dépannage

### L'API ne répond pas
```bash
# Vérifier que le service tourne
docker ps | grep mock-football-api

# Voir les logs
docker logs mock-football-api
```

### L'interface ne charge pas
```bash
# Vérifier que le service tourne
docker ps | grep mock-football-web

# Voir les logs
docker logs mock-football-web
```

### La base de données est vide
```bash
# Supprimer le conteneur et relancer
docker-compose down
docker-compose up -d

# Les migrations et seed data s'appliquent automatiquement
```

---

## ?? Améliorations Futures

- [ ] Ajouter plus d'équipes et de compétitions
- [ ] Gestion des joueurs
- [ ] Statistiques détaillées (tirs, passes, etc.)
- [ ] Export de données (CSV, JSON)
- [ ] API pour créer des matchs depuis l'extérieur
- [ ] WebSocket pour le temps réel (au lieu du polling)
- [ ] Authentification/Autorisation
- [ ] Tests unitaires et d'intégration

---

## ?? License

MIT License - Libre d'utilisation et de modification

---

**? Profitez de votre simulateur API Football ! ?**
