# ? MockScoreService - Installation et Démarrage Complet

## ?? Félicitations ! Le simulateur est opérationnel

Le **MockScoreService** a été créé avec succès et est maintenant intégré à votre stack Docker.

---

## ?? URLs d'Accès

### Services MockScore
- **Interface Web Blazor** : http://localhost:5001
- **API REST** : http://localhost:5000/v4/matches
- **Swagger API** : http://localhost:5000/swagger

### Autres Services
- **MatchOdds API** : http://localhost:8080
- **RabbitMQ Management** : http://localhost:15672 (user/password)
- **PostgreSQL** : localhost:5432 (postgres/password)

---

## ?? Démarrage Rapide

### Option 1 : Tous les services
```bash
docker-compose up -d
```

### Option 2 : Seulement MockScore
```bash
docker-compose up -d mock-football-api mock-football-web
```

---

## ?? Utilisation de l'Interface Web

### 1. Ouvrez l'interface
```bash
# Windows
start http://localhost:5001

# Ou manuellement dans votre navigateur
```

### 2. Navigation
- **Dashboard (/)** : Vue d'ensemble et statistiques
- **Matches (/matches)** : Créer et gérer les matchs
- **Simulation (/simulation)** : Contrôler les scores en temps réel

### 3. Créer un Match
1. Allez sur **"Matches"** dans le menu
2. Sélectionnez **Équipe Domicile** (ex: PSG)
3. Sélectionnez **Équipe Extérieur** (ex: Marseille)
4. Choisissez une **Date** et **Heure**
5. Cliquez sur **"Créer le Match"**

### 4. Simuler un Match en Temps Réel
1. Allez sur **"Simulation Temps Réel"**
2. Trouvez le match créé (statut: SCHEDULED)
3. Cliquez sur **"Démarrer"** ? Le statut passe à IN_PLAY
4. Utilisez les boutons **[+]** et **[-]** pour modifier les scores
5. Les changements sont **immédiatement visibles** dans l'API
6. Cliquez sur **"Terminer"** quand le match est fini

---

## ?? Tester l'API

### Récupérer tous les matchs
```bash
curl http://localhost:5000/v4/matches
```

### Filtrer par statut
```bash
# Matchs en cours
curl http://localhost:5000/v4/matches?status=IN_PLAY

# Matchs terminés
curl http://localhost:5000/v4/matches?status=FINISHED

# Matchs programmés
curl http://localhost:5000/v4/matches?status=SCHEDULED
```

### PowerShell
```powershell
Invoke-RestMethod -Uri "http://localhost:5000/v4/matches" | ConvertTo-Json -Depth 10
```

---

## ?? Intégration avec ScoreService

Le **ScoreService** a été automatiquement configuré pour utiliser le MockScoreService au lieu de l'API officielle :

```json
{
  "ApiSettings": {
    "BaseUrl": "http://mock-football-api:5000/",
    "Token": "mock_token_not_needed"
  }
}
```

### Avantages
? Pas besoin de token API externe  
? Contrôle total sur les données  
? Tests sans rate-limit  
? Simulation temps réel  

---

## ?? Données Pré-chargées

### 8 Équipes Françaises
- Paris Saint-Germain FC (PSG)
- Olympique de Marseille (MAR)
- Olympique Lyonnais (LYO)
- AS Monaco FC (MON)
- LOSC Lille (LIL)
- OGC Nice (NIC)
- Stade Rennais FC (REN)
- RC Lens (LEN)

### 3 Matchs d'Exemple
- PSG vs Marseille (demain 20h)
- Lyon vs Monaco (demain 23h)
- Lille vs Nice (après-demain)

---

## ?? Scénario de Test Complet

### Étape 1 : Vérifier les matchs existants
```bash
curl http://localhost:5000/v4/matches
```

### Étape 2 : Démarrer un match via l'interface
1. Ouvrir http://localhost:5001/simulation
2. Cliquer sur "Démarrer" pour le match PSG vs Marseille
3. Le statut passe de SCHEDULED à IN_PLAY

### Étape 3 : Modifier les scores
1. Cliquer sur [+] pour PSG ? Score: 1-0
2. Cliquer sur [+] pour Marseille ? Score: 1-1
3. Cliquer encore sur [+] pour PSG ? Score: 2-1

### Étape 4 : Vérifier via l'API
```bash
curl http://localhost:5000/v4/matches?status=IN_PLAY
```

**Résultat attendu** : Le match PSG-Marseille avec score 2-1

### Étape 5 : Terminer le match
1. Cliquer sur "Terminer" dans l'interface
2. Le statut passe à FINISHED
3. Les scores sont figés

### Étape 6 : Vérifier ScoreService
Le ScoreService récupère automatiquement les matchs terminés et publie un événement dans RabbitMQ !

---

## ??? Base de Données

### SQLite Embarquée
- Fichier : `football.db`
- Location : Conteneur Docker (volume automatique)
- Migrations : Appliquées automatiquement au démarrage

### Tables
- **Teams** : Équipes
- **Competitions** : Compétitions (Ligue 1)
- **Seasons** : Saisons
- **Matches** : Matchs créés

---

## ??? Commandes Utiles

### Voir les logs
```bash
docker-compose logs -f mock-football-api
docker-compose logs -f mock-football-web
```

### Restart les services
```bash
docker-compose restart mock-football-api mock-football-web
```

### Rebuild en cas de modification
```bash
docker-compose build mock-football-api mock-football-web
docker-compose up -d mock-football-api mock-football-web
```

### Accéder à la base de données
```bash
docker exec -it mock-football-web sh
ls -la  # Trouver football.db
```

---

## ?? Fonctionnalités de l'Interface

### Dashboard
- Nombre total de matchs
- Matchs à venir (SCHEDULED)
- Matchs en cours (IN_PLAY)
- Matchs terminés (FINISHED)
- Actions rapides

### Gestion des Matchs
- Créer un nouveau match
- Sélection d'équipes via dropdown
- Date/Heure picker
- Liste de tous les matchs
- Suppression de matchs

### Simulation Temps Réel
- Cards visuelles pour chaque match
- Contrôles +/- pour les scores
- Boutons d'action (Démarrer, Terminer, Reset, Rejouer)
- Auto-refresh toutes les 2 secondes
- Statut visuel avec couleurs (Scheduled=Bleu, In Play=Vert, Finished=Gris)

---

## ?? Architecture Technique

### Backend API (Port 5000)
- ASP.NET Core Web API (.NET 8)
- Entity Framework Core + SQLite
- Swagger UI intégré
- Migrations automatiques
- CORS activé

### Frontend Web (Port 5001)
- Blazor Server (.NET 8)
- MudBlazor pour l'UI
- Même base SQLite que l'API
- Composants interactifs
- Real-time updates

### Communication
```
[Blazor Web] ?? [SQLite] ?? [API]
                    ?
            [ScoreService]
                    ?
              [RabbitMQ]
```

---

## ?? Workflow Complet

```
1. Créer Match (Web UI)
      ?
2. Match stocké dans SQLite
      ?
3. API expose le match via /v4/matches
      ?
4. Démarrer Match (Web UI)
      ?
5. Statut ? IN_PLAY
      ?
6. Modifier scores (Web UI +/-)
      ?
7. Scores mis à jour en temps réel
      ?
8. ScoreService récupère les données
      ?
9. Terminer Match (Web UI)
      ?
10. Statut ? FINISHED
      ?
11. ScoreService publie événement
      ?
12. RabbitMQ ? BetResultService
```

---

## ?? Cas d'Usage

### 1. Développement Local
- Tester l'intégration sans API externe
- Contrôler les données de test
- Simuler différents scénarios

### 2. Tests Automatisés
- Données prévisibles
- Pas de rate-limit
- Environnement isolé

### 3. Démonstrations
- Interface visuelle attrayante
- Contrôle temps réel
- Scénarios scriptables

### 4. Formation
- Comprendre les flux de données
- Tester RabbitMQ
- Apprendre l'architecture microservices

---

## ?? Dépannage

### L'API ne répond pas
```bash
# Vérifier le statut
docker ps | grep mock-football-api

# Voir les logs
docker logs mock-football-api

# Restart
docker-compose restart mock-football-api
```

### L'interface ne charge pas
```bash
# Vérifier le statut
docker ps | grep mock-football-web

# Voir les logs
docker logs mock-football-web

# Restart
docker-compose restart mock-football-web
```

### Base de données vide
```bash
# Les migrations s'appliquent automatiquement
# Si problème, rebuild :
docker-compose down
docker-compose build mock-football-api mock-football-web --no-cache
docker-compose up -d
```

### Erreur de connexion ScoreService
```bash
# Vérifier que mock-football-api tourne
docker ps | grep mock-football-api

# Tester l'API
curl http://localhost:5000/v4/matches
```

---

## ?? Documentation Complète

Pour plus de détails, consultez :
- **[MOCKSCORESERVICE-README.md](./MOCKSCORESERVICE-README.md)** - Documentation complète
- **[docker-compose.yml](./docker-compose.yml)** - Configuration Docker

---

## ? Checklist de Validation

- [x] MockScoreService.API buildé et démarré
- [x] MockScoreService.Web buildé et démarré
- [x] API accessible sur port 5000
- [x] Interface Web accessible sur port 5001
- [x] Migrations appliquées automatiquement
- [x] Seed data chargées (8 équipes, 3 matchs)
- [x] Endpoint /v4/matches fonctionnel
- [x] ScoreService configuré pour utiliser MockScore
- [x] Interface Blazor responsive et fonctionnelle
- [x] Simulation temps réel opérationnelle

---

## ?? C'EST PRÊT !

Vous avez maintenant un simulateur complet d'API Football avec :
- ? Interface graphique moderne (MudBlazor)
- ? Contrôle temps réel des scores
- ? API compatible football-data.org
- ? Intégration transparente avec ScoreService
- ? Zéro configuration externe

**Ouvrez http://localhost:5001 et commencez à simuler ! ???**
