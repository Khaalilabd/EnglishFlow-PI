-- Initialize all databases for EnglishFlow microservices

-- Create databases
CREATE DATABASE IF NOT EXISTS englishflow_identity;
CREATE DATABASE IF NOT EXISTS englishflow_courses;
CREATE DATABASE IF NOT EXISTS englishflow_exams;
CREATE DATABASE IF NOT EXISTS englishflow_messaging_db;
CREATE DATABASE IF NOT EXISTS englishflow_community;
CREATE DATABASE IF NOT EXISTS englishflow_jungle_club_db;
CREATE DATABASE IF NOT EXISTS englishflow_events;
CREATE DATABASE IF NOT EXISTS englishflow_gamification;
CREATE DATABASE IF NOT EXISTS englishflow_learning;
CREATE DATABASE IF NOT EXISTS englishflow_notifications;
CREATE DATABASE IF NOT EXISTS englishflow_complaints;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE englishflow_identity TO postgres;
GRANT ALL PRIVILEGES ON DATABASE englishflow_courses TO postgres;
GRANT ALL PRIVILEGES ON DATABASE englishflow_exams TO postgres;
GRANT ALL PRIVILEGES ON DATABASE englishflow_messaging_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE englishflow_community TO postgres;
GRANT ALL PRIVILEGES ON DATABASE englishflow_jungle_club_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE englishflow_events TO postgres;
GRANT ALL PRIVILEGES ON DATABASE englishflow_gamification TO postgres;
GRANT ALL PRIVILEGES ON DATABASE englishflow_learning TO postgres;
GRANT ALL PRIVILEGES ON DATABASE englishflow_notifications TO postgres;
GRANT ALL PRIVILEGES ON DATABASE englishflow_complaints TO postgres;
