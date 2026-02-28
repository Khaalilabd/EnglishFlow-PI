-- Add quiz_id column to chapters table
-- This allows tutors to assign quizzes to chapters

ALTER TABLE chapters ADD COLUMN IF NOT EXISTS quiz_id BIGINT;

-- Add comment to explain the column
COMMENT ON COLUMN chapters.quiz_id IS 'Optional quiz ID that students can access after completing the chapter';

-- Verify the change
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'chapters' AND column_name = 'quiz_id';
