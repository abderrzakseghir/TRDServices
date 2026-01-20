-- Script d'initialisation PostgreSQL
-- Crée automatiquement toutes les bases de données nécessaires

-- Créer la base de données pour BetResultService
CREATE DATABASE betresult;

-- Créer la base de données pour ScoreService
CREATE DATABASE "ScoreServiceDb";

-- Créer la base de données pour MatchOddsService
CREATE DATABASE "MatchOddsDb";

-- Accorder tous les privilèges à l'utilisateur postgres
GRANT ALL PRIVILEGES ON DATABASE betresult TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "ScoreServiceDb" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "MatchOddsDb" TO postgres;
