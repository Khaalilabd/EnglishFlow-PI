-- Initialize all databases for EnglishFlow microservices

-- Create databases (PostgreSQL doesn't support IF NOT EXISTS for CREATE DATABASE)
-- This script should only run once during initial setup

SELECT 'CREATE DATABASE englishflow_identity'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'englishflow_identity')\gexec

SELECT 'CREATE DATABASE englishflow_courses'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'englishflow_courses')\gexec

SELECT 'CREATE DATABASE englishflow_exams'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'englishflow_exams')\gexec

SELECT 'CREATE DATABASE englishflow_messaging_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'englishflow_messaging_db')\gexec

SELECT 'CREATE DATABASE englishflow_community'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'englishflow_community')\gexec

SELECT 'CREATE DATABASE englishflow_jungle_club_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'englishflow_jungle_club_db')\gexec

SELECT 'CREATE DATABASE englishflow_event_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'englishflow_event_db')\gexec

SELECT 'CREATE DATABASE englishflow_gamification'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'englishflow_gamification')\gexec

SELECT 'CREATE DATABASE englishflow_learning_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'englishflow_learning_db')\gexec

SELECT 'CREATE DATABASE englishflow_complaints'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'englishflow_complaints')\gexec

SELECT 'CREATE DATABASE englishflow_payment'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'englishflow_payment')\gexec

SELECT 'CREATE DATABASE englishflow_sponsors_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'englishflow_sponsors_db')\gexec

-- Note: GRANT ALL PRIVILEGES is automatically applied to the database owner (postgres)
