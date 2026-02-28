-- Add quiz_id column to lessons table
-- This allows tutors to assign quizzes to lessons (when lesson type is QUIZ)

ALTER TABLE lessons ADD COLUMN IF NOT EXISTS quiz_id BIGINT;

-- Add comment to explain the column
COMMENT ON COLUMN lessons.quiz_id IS 'Quiz ID for lessons of type QUIZ - links to quiz in learning service';

-- Remove quiz_id from chapters table (moving it to lessons)
ALTER TABLE chapters DROP COLUMN IF EXISTS quiz_id;

-- Verify the changes
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'lessons' AND column_name = 'quiz_id';

SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'chapters' AND column_name = 'quiz_id';
