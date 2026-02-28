-- Ensure quiz_id column exists in lessons table
-- Run this in your PostgreSQL database

-- Check if column exists and add it if it doesn't
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'lessons' 
        AND column_name = 'quiz_id'
    ) THEN
        ALTER TABLE lessons ADD COLUMN quiz_id BIGINT;
        RAISE NOTICE 'Column quiz_id added to lessons table';
    ELSE
        RAISE NOTICE 'Column quiz_id already exists in lessons table';
    END IF;
END $$;

-- Verify the column exists
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'lessons' AND column_name = 'quiz_id';
