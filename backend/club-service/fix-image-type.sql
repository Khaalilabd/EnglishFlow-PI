-- Migration script to change image column type from VARCHAR(500) to TEXT
-- This allows storing Base64 encoded images which are typically much larger

ALTER TABLE clubs 
ALTER COLUMN image TYPE TEXT;

-- Verify the change
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns
WHERE table_name = 'clubs' AND column_name = 'image';
