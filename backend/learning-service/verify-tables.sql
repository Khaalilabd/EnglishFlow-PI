-- Run this in pgAdmin to verify tables exist
-- Connect to learning_db database first

-- List all tables
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;

-- Check quiz table columns
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'quiz'
ORDER BY ordinal_position;

-- Count records
SELECT 'quiz' as table_name, COUNT(*) as count FROM quiz
UNION ALL
SELECT 'question', COUNT(*) FROM question
UNION ALL
SELECT 'ebook', COUNT(*) FROM ebook;
