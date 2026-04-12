-- Fix English Level for Students
-- This script sets englishLevel to NULL for students who have empty string or whitespace

-- Update students with empty string to NULL
UPDATE users 
SET english_level = NULL 
WHERE role = 'STUDENT' 
  AND (english_level = '' OR english_level IS NULL OR TRIM(english_level) = '');

-- Verify the changes
SELECT id, email, first_name, last_name, role, english_level 
FROM users 
WHERE role = 'STUDENT'
ORDER BY created_at DESC
LIMIT 20;
