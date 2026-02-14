-- Run this in pgAdmin to check current schema
-- Connect to learning_db database first

-- Check if tables exist
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;

-- Check quiz table structure
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'quiz'
ORDER BY ordinal_position;

-- Check question table structure
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'question'
ORDER BY ordinal_position;

-- Check ebook table structure
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'ebook'
ORDER BY ordinal_position;
