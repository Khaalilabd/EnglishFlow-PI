-- Create databases for all microservices
-- Run this with: psql -U postgres -f create-databases.sql

-- Create database for auth service
CREATE DATABASE englishflow_identity;

-- Create database for learning service
CREATE DATABASE learning_db;

-- Create database for courses service
CREATE DATABASE englishflow_courses;

-- Grant privileges (optional, if needed)
-- GRANT ALL PRIVILEGES ON DATABASE englishflow_identity TO postgres;
-- GRANT ALL PRIVILEGES ON DATABASE learning_db TO postgres;
-- GRANT ALL PRIVILEGES ON DATABASE englishflow_courses TO postgres;
