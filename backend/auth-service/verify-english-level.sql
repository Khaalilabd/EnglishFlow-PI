-- Verify english_level column and data
SELECT 
    id, 
    email, 
    first_name, 
    last_name, 
    role,
    english_level,
    LENGTH(english_level) as level_length,
    CASE 
        WHEN english_level IS NULL THEN 'NULL'
        WHEN english_level = '' THEN 'EMPTY STRING'
        WHEN TRIM(english_level) = '' THEN 'WHITESPACE'
        ELSE 'HAS VALUE'
    END as level_status
FROM users 
WHERE email = 'raoudhabeltaifa2011@gmail.com';

-- If the value is NULL or empty, update it to B1
UPDATE users 
SET english_level = 'B1' 
WHERE email = 'raoudhabeltaifa2011@gmail.com' 
  AND (english_level IS NULL OR english_level = '' OR TRIM(english_level) = '');

-- Verify the update
SELECT 
    id, 
    email, 
    first_name, 
    last_name, 
    role,
    english_level
FROM users 
WHERE email = 'raoudhabeltaifa2011@gmail.com';
