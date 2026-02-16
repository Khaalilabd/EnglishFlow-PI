-- Create database for Community Service
-- Run this script as postgres superuser or with appropriate privileges

-- Create database
CREATE DATABASE englishflow_community
    WITH 
    OWNER = root
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Connect to the database
\c englishflow_community

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE englishflow_community TO root;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO root;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO root;

-- Verify connection
SELECT 'Database englishflow_community created successfully!' as status;
