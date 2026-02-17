-- Script de création des bases de données pour EnglishFlow
-- Exécuter ce script en tant que superuser PostgreSQL

-- Créer l'utilisateur si nécessaire
DO
$$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'englishflow') THEN
      CREATE USER englishflow WITH PASSWORD 'englishflow123';
   END IF;
END
$$;

-- Créer les bases de données
CREATE DATABASE englishflow_identity;
CREATE DATABASE englishflow_community;
CREATE DATABASE englishflow_learning;
CREATE DATABASE englishflow_club;

-- Donner les privilèges à l'utilisateur
GRANT ALL PRIVILEGES ON DATABASE englishflow_identity TO englishflow;
GRANT ALL PRIVILEGES ON DATABASE englishflow_community TO englishflow;
GRANT ALL PRIVILEGES ON DATABASE englishflow_learning TO englishflow;
GRANT ALL PRIVILEGES ON DATABASE englishflow_club TO englishflow;

-- Se connecter à chaque base de données et donner les privilèges sur le schéma public
\c englishflow_identity
GRANT ALL ON SCHEMA public TO englishflow;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO englishflow;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO englishflow;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO englishflow;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO englishflow;

\c englishflow_community
GRANT ALL ON SCHEMA public TO englishflow;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO englishflow;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO englishflow;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO englishflow;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO englishflow;

\c englishflow_learning
GRANT ALL ON SCHEMA public TO englishflow;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO englishflow;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO englishflow;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO englishflow;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO englishflow;

\c englishflow_club
GRANT ALL ON SCHEMA public TO englishflow;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO englishflow;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO englishflow;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO englishflow;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO englishflow;

-- Afficher les bases de données créées
\l englishflow*
