# 🏆 TRD Services - Plateforme de Paris Sportifs Microservices

Architecture microservices polyglotte et complète pour un système de paris sportifs, intégrant **Java Spring Boot**, **.NET 8**, **Python FastAPI**, **Docker**, **Kubernetes**, **PostgreSQL**, **RabbitMQ** et **Keycloak**.

---

## 👥 Équipe de Développement

- 👨‍💻 **BEKKOUCHE Mohamed Baha Eddine**
- 👨‍💻 **SEGHIR Abderrazak**

---

## 📑 Table des Matières

- [Vue d'ensemble](#-vue-densemble)
- [Architecture Globale](#-architecture-globale)
- [Catalogue des Services](#-catalogue-des-services)
- [Démarrage Rapide](#-démarrage-rapide)
- [Fonctionnalités Clés](#-fonctionnalités-clés)
- [Stack Technologique](#-stack-technologique)
- [URLs et Ports](#-urls-et-ports)
- [Bases de Données](#-bases-de-données)
- [Structure des Événements (RabbitMQ)](#-structure-des-événements-rabbitmq)

---

## 🔭 Vue d'ensemble

TRD est une plateforme distribuée conçue pour la **haute disponibilité** et la **scalabilité**.  
Elle sépare les domaines métiers en services autonomes communiquant via des **APIs REST** (synchrones) et un **Bus d'Événements** (asynchrones).

### Points forts

- ✅ Architecture **Hexagonale** pour le cœur métier (Java)
- ✅ Pattern **CQRS** pour la séparation Lecture/Écriture
- ✅ **SAGA Pattern** pour les transactions distribuées (Paris ↔ Wallet)
- ✅ Orchestration via **Gateway BFF** pour l'authentification
- ✅ **IA / ML** pour les recommandations personnalisées
- ✅ Infrastructure complète conteneurisée (IAM, DBs, Broker, Cache)

---

## 🏗 Architecture Globale

Le système est composé de **9 microservices principaux** et **4 composants d'infrastructure**.

```mermaid
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
