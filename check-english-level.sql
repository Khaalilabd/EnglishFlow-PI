-- Check English Level in Database
-- Run this in your PostgreSQL database to verify englishLevel is being saved

-- 1. Check if englishLevel column exists
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'users' AND column_name = 'english_level';

-- 2. Check all students and their English levels
SELECT id, email, first_name, last_name, role, english_level, created_at
FROM users
WHERE role = 'STUDENT'
ORDER BY created_at DESC
LIMIT 10;

-- 3. Check specific user (replace with your email)
SELECT id, email, first_name, last_name, role, english_level, is_active, profile_completed
FROM users
WHERE email = 'raoudhabeltaifa2011@gmail.com';

-- 4. Count students by English level
SELECT 
    english_level,
    COUNT(*) as count
FROM users
WHERE role = 'STUDENT'
GROUP BY english_level
ORDER BY english_level;

-- 5. Find students without English level
SELECT id, email, first_name, last_name, created_at
FROM users
WHERE role = 'STUDENT' AND (english_level IS NULL OR english_level = '')
ORDER BY created_at DESC;
