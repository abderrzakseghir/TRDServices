# ?? TRD Services - Microservices de Paris Sportifs

Architecture microservices complète pour un système de paris sportifs avec Docker, PostgreSQL, RabbitMQ et Swagger UI.

[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![.NET 8.0](https://img.shields.io/badge/.NET-8.0-purple.svg)](https://dotnet.microsoft.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.13-orange.svg)](https://www.rabbitmq.com/)

---

## ?? Table des Matières

- [Vue d'ensemble](#-vue-densemble)
- [Architecture](#-architecture)
- [Services](#-services)
- [Démarrage Rapide](#-démarrage-rapide)
- [Documentation](#-documentation)
- [Fonctionnalités](#-fonctionnalités)
- [Technologies](#-technologies)
- [URLs des Services](#-urls-des-services)
- [Contribution](#-contribution)

---

## ?? Vue d'ensemble

Système de microservices pour gérer les matchs, cotes et paris sportifs avec :
- ? **Configuration Docker complète** (zero-config)
- ? **Migrations automatiques** des bases de données
- ? **RabbitMQ pré-configuré** avec queues et exchanges
- ? **Swagger UI intégré** pour tester l'API
- ? **Healthchecks** et retry logic robustes

---

## ??? Architecture

```
???????????????????
?  MatchOdds API  ? :8080 (REST + Swagger)
?   (.NET 8.0)    ?
???????????????????
         ?
    ???????????????????????
    ?                     ?
????????????      ??????????????
?  Score   ?      ?    Bet     ?
? Service  ?      ?   Result   ?
? (Worker) ?      ?  (Worker)  ?
????????????      ??????????????
    ?                    ?
    ?    ?????????????   ?
    ?????? RabbitMQ  ?????
         ?  (AMQP)   ?
         ?????????????
               ?
         ??????????????
         ? PostgreSQL ?
         ?  (3 DBs)   ?
         ??????????????
```

---

## ?? Services

### 1?? **MatchOdds Service** (API REST)
- Gestion des équipes, matchs et cotes
- API REST complète avec Swagger UI
- CRUD pour Teams, Matches et Odds
- **Port** : 8080

### 2?? **Score Service** (Worker)
- Récupération des scores depuis Football-Data API
- Publication des événements `match.finished` vers RabbitMQ
- Mise à jour automatique des scores

### 3?? **BetResult Service** (Consumer)
- Consommation des événements de paris et scores
- Calcul automatique des résultats
- Traitement asynchrone via RabbitMQ

### 4?? **PostgreSQL**
- 3 bases de données séparées
- Créées automatiquement au démarrage
- Migrations EF Core appliquées automatiquement

### 5?? **RabbitMQ**
- Exchanges et queues pré-configurés
- Interface de management Web
- Bindings automatiques

---

## ?? Démarrage Rapide

### Prérequis
- **Docker Desktop** installé et démarré
- **Git** (pour cloner le repo)

### Installation

```bash
# 1. Cloner le repository
git clone https://github.com/abderrzakseghir/TRDServices.git
cd TRDServices

# 2. Démarrer tous les services
docker-compose up -d

# 3. Attendre que tous les services soient prêts (~30 secondes)

# 4. Ouvrir Swagger UI
# Windows
start http://localhost:8080

# Linux/Mac
open http://localhost:8080
```

### Scripts de Démarrage

**Windows** :
```bash
start.bat
```

**Linux/Mac** :
```bash
chmod +x start.sh
./start.sh
```

---

## ?? Documentation

| Fichier | Description |
|---------|-------------|
| **[README-DOCKER.md](./README-DOCKER.md)** | ?? Guide principal Docker |
| **[GUIDE-TEST-SWAGGER.md](./GUIDE-TEST-SWAGGER.md)** | ?? Guide de test complet de l'API |
| **[COMMANDES-PRATIQUES.md](./COMMANDES-PRATIQUES.md)** | ?? Commandes Docker quotidiennes |
| **[ETAT_DES_SERVICES.md](./ETAT_DES_SERVICES.md)** | ?? État et configuration des services |
| **[SWAGGER-CORRECTION-FINALE.md](./SWAGGER-CORRECTION-FINALE.md)** | ?? Notes sur la configuration Swagger |

---

## ? Fonctionnalités

### Configuration Automatique
- ? **Zero-config** : `docker-compose up` suffit
- ? Bases de données créées automatiquement
- ? RabbitMQ configuré avec définitions JSON
- ? Migrations EF appliquées automatiquement (10 retry)
- ? Healthchecks sur PostgreSQL et RabbitMQ

### Gestion des Données
- ? **3 bases PostgreSQL** séparées par service
- ? Migrations Entity Framework Core
- ? Retry logic robuste (10 tentatives, 3s délai)
- ? Volumes Docker pour persistance

### Messaging
- ? RabbitMQ avec exchanges et queues pré-configurés
- ? Topic exchange `sportsbook.topic`
- ? Queues : `bet.placed`, `match.finished`
- ? Interface Management UI accessible

### API REST
- ? Swagger UI complet et fonctionnel
- ? CRUD pour Teams, Matches, Odds
- ? CORS configuré
- ? Documentation OpenAPI intégrée

---

## ??? Technologies

### Backend
- **.NET 8.0** - Framework applicatif
- **ASP.NET Core** - API REST
- **Entity Framework Core** - ORM
- **Npgsql** - Driver PostgreSQL

### Infrastructure
- **Docker** & **Docker Compose** - Containerisation
- **PostgreSQL 16** - Base de données relationnelle
- **RabbitMQ 3.13** - Message broker AMQP

### API & Documentation
- **Swagger UI** - Documentation interactive
- **Swashbuckle** - Génération OpenAPI

---

## ?? URLs des Services

| Service | URL | Identifiants |
|---------|-----|--------------|
| **Swagger UI** | http://localhost:8080 | - |
| **RabbitMQ Management** | http://localhost:15672 | user / password |
| **PostgreSQL** | localhost:5432 | postgres / password |

---

## ?? Tests Rapides

### Créer une équipe
```bash
curl -X POST http://localhost:8080/api/Teams \
  -H "Content-Type: application/json" \
  -d '{"name":"Paris Saint-Germain","code":"PSG"}'
```

### Lister les équipes
```bash
curl http://localhost:8080/api/Teams
```

### PowerShell
```powershell
$team = @{ name = "PSG"; code = "PSG" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/Teams" -Method POST -Body $team -ContentType "application/json"
```

---

## ?? Commandes Utiles

```bash
# Voir l'état de tous les services
docker-compose ps

# Voir les logs en temps réel
docker-compose logs -f

# Redémarrer un service
docker-compose restart matchodds-service

# Tout arrêter
docker-compose down

# Tout nettoyer (volumes inclus)
docker-compose down --volumes

# Vérifier les queues RabbitMQ
docker exec rabbitmq rabbitmqctl list_queues

# Connexion PostgreSQL
docker exec -it postgres psql -U postgres
```

---

## ??? Bases de Données

Trois bases PostgreSQL créées automatiquement :

| Base de Données | Service | Tables |
|----------------|---------|--------|
| **MatchOddsDb** | MatchOdds API | Teams, Matches, Odds |
| **ScoreServiceDb** | Score Service | MatchEntity |
| **betresult** | BetResult Service | Bets, Selections |

---

## ?? RabbitMQ

### Exchanges
- `sportsbook.topic` (type: topic, durable)

### Queues
- `q.bet-result.new-bets` ? routing: `bet.placed`
- `q.bet-result.match-scores` ? routing: `match.finished`

### Management UI
?? http://localhost:15672 (user / password)

---

## ?? Endpoints API

### Teams
- `GET /api/Teams` - Liste
- `POST /api/Teams` - Créer
- `GET /api/Teams/{id}` - Détail
- `PUT /api/Teams/{id}` - Modifier
- `DELETE /api/Teams/{id}` - Supprimer

### Matches
- `GET /api/Matches` - Liste
- `POST /api/Matches` - Créer
- `GET /api/Matches/{id}` - Détail
- `PATCH /api/Matches/{id}` - Modifier
- `DELETE /api/Matches/{id}` - Supprimer

### Odds
- `GET /api/Odds` - Liste
- `POST /api/Odds` - Créer
- `GET /api/Odds/{id}` - Détail
- `PATCH /api/Odds/{id}` - Modifier
- `DELETE /api/Odds/{id}` - Supprimer

?? **Testez-les dans Swagger** : http://localhost:8080

---

## ?? Dépannage

### Les services ne démarrent pas
```bash
docker-compose down --volumes
docker-compose build --no-cache
docker-compose up -d
```

### Voir les erreurs
```bash
docker-compose logs <service-name>
```

### Vérifier PostgreSQL
```bash
docker exec postgres pg_isready -U postgres
```

### Vérifier RabbitMQ
```bash
docker exec rabbitmq rabbitmq-diagnostics ping
```

---

## ?? Notes Importantes

### Configuration ScoreService
Le ScoreService nécessite un **token API Football-Data** valide.

Pour configurer :
```json
// ScoreService/appsettings.json
{
  "ApiSettings": {
    "Token": "VOTRE_TOKEN_ICI"
  }
}
```

Obtenez votre token sur : https://www.football-data.org/

---

## ?? Contribution

Les contributions sont les bienvenues !

1. Fork le projet
2. Créez une branche (`git checkout -b feature/AmazingFeature`)
3. Commit vos changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

---

## ?? Licence

Ce projet est sous licence MIT.

---

## ?? Auteur

**Abderrazak Seghir**
- GitHub: [@abderrzakseghir](https://github.com/abderrzakseghir)

---

## ?? Remerciements

- Football-Data.org pour l'API de données sportives
- Communauté .NET et Docker

---

## ?? Roadmap

- [ ] Ajouter des tests unitaires et d'intégration
- [ ] Implémenter authentification JWT
- [ ] Ajouter monitoring avec Prometheus/Grafana
- [ ] CI/CD avec GitHub Actions
- [ ] Documentation API complète
- [ ] Support Kubernetes

---

**? Si ce projet vous aide, n'oubliez pas de lui donner une étoile !**

---

*Dernière mise à jour : Janvier 2026*
