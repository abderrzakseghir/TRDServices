🏆 TRD Services - Plateforme de Paris Sportifs Microservices

Architecture microservices polyglotte et complète pour un système de paris sportifs, intégrant Java Spring Boot, .NET 8, Python FastAPI, Docker, Kubernetes, PostgreSQL, RabbitMQ et Keycloak.

📑 Table des Matières

Vue d'ensemble

Architecture Globale

Catalogue des Services

Démarrage Rapide

Fonctionnalités Clés

Stack Technologique

URLs et Ports

Bases de Données

Structure des Événements (RabbitMQ)

🔭 Vue d'ensemble

TRD est une plateforme distribuée conçue pour la haute disponibilité et la scalabilité. Elle sépare les domaines métiers en services autonomes communiquant via des APIs REST (synchrones) et un Bus d'Événements (asynchrones).

Points forts :

✅ Architecture Hexagonale pour le cœur métier (Java).

✅ Pattern CQRS pour la séparation Lecture/Écriture.

✅ SAGA Pattern pour les transactions distribuées (Paris ↔ Wallet).

✅ Orchestration via Gateway BFF pour l'authentification.

✅ IA/ML pour les recommandations personnalisées.

✅ Infrastructure complète (IAM, DBs, Broker, Cache) conteneurisée.

🏗 Architecture Globale

Le système est composé de 9 Microservices principaux et 4 Composants d'Infrastructure.

graph TD
    User((Utilisateur)) -->|HTTPS| GW[Gateway Orchestrator]
    
    subgraph "Infrastructure"
        KC[Keycloak IAM]
        RMQ((RabbitMQ))
        PG[(PostgreSQL)]
        RD[(Redis)]
    end

    subgraph "Domaine Utilisateur & Finance (Java)"
        GW --> ACC[Account Service]
        GW --> WAL[Wallet Service]
        GW --> BET[Bet Lifecycle Service]
        ACC --> PG
        WAL --> PG
        BET --> PG
    end

    subgraph "Domaine Sport & Data (.NET)"
        ODDS[MatchOdds Service]
        SCORE[Score Service]
        RES[Bet Result Service]
        MOCK[Mock Football API]
        
        GW --> ODDS
        SCORE --> MOCK
        ODDS --> PG
        SCORE --> PG
        RES --> PG
    end

    subgraph "Domaine Intelligence (Python)"
        GW --> REC[Recommendation Engine]
        REC --> RD
    end

    %% Communication Asynchrone
    ACC -.->|Events| RMQ
    BET -.->|Events| RMQ
    WAL -.->|Events| RMQ
    SCORE -.->|Events| RMQ
    
    RMQ -.->|Consomme| WAL
    RMQ -.->|Consomme| BET
    RMQ -.->|Consomme| RES
    RMQ -.->|Consomme| REC


📦 Catalogue des Services

🟢 Domaine Core (Java Spring Boot)

Service

Port

Rôle

Gateway Orchestrator

:8088

BFF (Backend for Frontend). Point d'entrée unique. Gère l'orchestration de l'inscription (Keycloak + Account) et le routage.

Account Service

:8081

Gestion des profils utilisateurs et historique des paris.

Bet Lifecycle Service

:8082

Gestion de la prise de pari, validation des règles (cotes, mises) et coordination SAGA.

Wallet Service

:8083

"La Banque". Gestion des soldes, dépôts, retraits et verrouillage des fonds (Optimistic Locking).

🔵 Domaine Data & Sport (.NET 8)

Service

Port

Rôle

MatchOdds Service

:8085

Gestion du catalogue des matchs, équipes et cotes.

Score Service

:8086

Worker background. Synchronise les scores en temps réel et publie les fins de match.

Bet Result Service

:8087

Worker background. Calcule les résultats des paris (Gagné/Perdu) et déclenche les paiements.

Mock Football API

:5000

Simulation de l'API externe de football pour le développement et les tests.

🟡 Domaine Intelligence (Python)

Service

Port

Rôle

Recommendation Engine

:8084

Moteur hybride (Contenu + Collaboratif) suggérant des matchs basés sur l'historique et les tendances.

🚀 Démarrage Rapide

Prérequis

Docker Desktop (avec support Compose V2)

Git

Installation et Lancement

Cloner le repository

git clone [https://github.com/votre-repo/TRD-Platform.git](https://github.com/votre-repo/TRD-Platform.git)
cd TRD-Platform


Lancer la stack complète

docker-compose up -d


Cela va construire les images locales (Java/Python) et puller les images distantes (.NET/Infra).

Vérifier l'état

docker-compose ps


Accéder aux interfaces

Swagger Gateway : http://localhost:8088/swagger-ui.html (Si activé)

RabbitMQ Manager : http://localhost:15672 (guest/guest)

Keycloak Admin : http://localhost:8080 (admin/admin)

Configuration Initiale (Obligatoire pour la première exécution)

La base de données est initialisée automatiquement via init-db.sql, mais Keycloak nécessite une configuration manuelle du Royaume si le volume est vide :

Aller sur http://localhost:8080/admin

Créer le Realm trd-realm.

Créer le client gateway-orchestrator (Confidential, Service Accounts Enabled).

Copier le Secret et mettre à jour le docker-compose.yml si nécessaire (variable APP_KEYCLOAK_ADMIN_CLIENT_SECRET).

✨ Fonctionnalités Clés

🔐 Identité & Sécurité

Authentification OIDC standardisée via Keycloak.

Inscription "One-Click" orchestrée par la Gateway (création Keycloak + Profil Métier).

Split Horizon Security : Configuration réseau avancée pour gérer la validation des tokens entre Docker et l'Hôte.

💰 Finance & Paris

Transaction SAGA : La prise de pari est une transaction distribuée (Bet Service ⇄ Wallet Service) via RabbitMQ.

Cotes Fixes : Les cotes sont figées au moment du pari (Snapshot).

Paris Combinés : Support des accumulateurs avec multiplication des cotes.

🧠 Intelligence Artificielle

Cold Start : Recommandations basées sur la popularité globale (Redis).

Personnalisation : Algorithme de similarité cosinus basé sur l'historique des tags (Équipes, Ligues).

🛠 Stack Technologique

Catégorie

Technologies

Backend Core

Java 21, Spring Boot 3.2, Hexagonal Architecture

Backend Data

.NET 8, ASP.NET Core, Entity Framework

Backend AI

Python 3.11, FastAPI, Scikit-learn, NumPy

Bases de Données

PostgreSQL 16 (Multi-DB), Redis 7 (Cache/PubSub)

Messaging

RabbitMQ 3.12 (AMQP)

Sécurité

Keycloak 23 (OAuth2/OIDC)

DevOps

Docker, Docker Compose, Kubernetes (Minikube)

🔗 URLs et Ports

Service

Base URL

Documentation / UI

Gateway

http://localhost:8088

/auth/login, /auth/sign-up

Account

http://localhost:8081

/api/v1/accounts

Bet Lifecycle

http://localhost:8082

/api/v1/bets

Wallet

http://localhost:8083

/api/v1/wallets

Recommendation

http://localhost:8084

/api/v1/recommendations

Match Odds

http://localhost:8085

/api/matches

Keycloak

http://localhost:8080

/admin (Console)

RabbitMQ

http://localhost:15672

/ (Console)

🗄 Bases de Données

Un seul conteneur PostgreSQL héberge plusieurs bases logiques pour l'isolation des données :

Base de Données

Service Propriétaire

Contenu

account_db

Account Service

Profils, Logs d'activité

wallet_db

Wallet Service

Soldes, Transactions (Ledger)

bet_lifecycle_db

Bet Service

Tickets, Sélections, Statuts

match_odds_db

MatchOdds Service

Équipes, Matchs, Cotes

score_db

Score Service

Scores temps réel

bet_result_db

Bet Result Service

Résultats calculés

keycloak_db

Keycloak

Données IAM

🐰 Structure des Événements (RabbitMQ)

Le système repose sur l'Exchange principal : betting.events.exchange.

Routing Key

Événement

Producteur

Consommateurs

account.registered

Nouvel utilisateur

Account

Wallet (Création), Notification

betting.lifecycle.placed

Pari placé (Pending)

Bet Lifecycle

Wallet (Débit), Rec Engine (Apprentissage)

wallet.transaction.reserved

Fonds réservés (Succès/Échec)

Wallet

Bet Lifecycle (Confirmation/Rejet)

match.finished

Fin du match

Score Service

Bet Result, Bet Lifecycle

betting.settlement.settled

Pari réglé (Gain calculé)

Bet Result

Wallet (Crédit gain), Account (Historique)

🧪 Tests Rapides (cURL)

1. Inscription (Gateway)

curl -X POST http://localhost:8088/auth/sign-up \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@trd.com",
    "password": "123",
    "firstName": "Test",
    "lastName": "User",
    "phone": "+33600000000"
  }'


2. Dépôt d'argent (Wallet)

curl -X POST http://localhost:8083/api/v1/wallets/deposit \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{ "amount": 100.00, "paymentReference": "ref_123" }'


3. Placer un Pari (Bet Lifecycle)

curl -X POST http://localhost:8082/api/v1/bets \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "stake": 10.00,
    "selections": [
      { "matchId": "m1", "marketName": "1x2", "selectionName": "Home", "odd": 1.50 }
    ]
  }'
