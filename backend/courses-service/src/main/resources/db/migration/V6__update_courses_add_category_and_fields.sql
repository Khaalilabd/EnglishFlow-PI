-- V6: Update courses table to add category and optimize fields
-- Add new columns to courses table
ALTER TABLE courses ADD COLUMN IF NOT EXISTS category VARCHAR(255);
ALTER TABLE courses ADD COLUMN IF NOT EXISTS price DECIMAL(10, 2);
ALTER TABLE courses ADD COLUMN IF NOT EXISTS thumbnail_url VARCHAR(500);
ALTER TABLE courses ADD COLUMN IF NOT EXISTS objectives TEXT;
ALTER TABLE courses ADD COLUMN IF NOT EXISTS prerequisites TEXT;
ALTER TABLE courses ADD COLUMN IF NOT EXISTS is_featured BOOLEAN DEFAULT FALSE;

-- Update level column to use string format (A1, A2, B1, B2, C1, C2) instead of enum
-- First, add a temporary column
ALTER TABLE courses ADD COLUMN IF NOT EXISTS level_new VARCHAR(10);

-- Migrate existing data from enum to string format
UPDATE courses SET level_new = 
    CASE 
        WHEN level = 'BEGINNER' THEN 'A1'
        WHEN level = 'ELEMENTARY' THEN 'A2'
        WHEN level = 'INTERMEDIATE' THEN 'B1'
        WHEN level = 'UPPER_INTERMEDIATE' THEN 'B2'
        WHEN level = 'ADVANCED' THEN 'C1'
        ELSE 'A1'
    END
WHERE level_new IS NULL;

-- Drop old level column and rename new one
ALTER TABLE courses DROP COLUMN IF EXISTS level;
ALTER TABLE courses RENAME COLUMN level_new TO level;

-- Add index on category for better query performance
CREATE INDEX IF NOT EXISTS idx_courses_category ON courses(category);
CREATE INDEX IF NOT EXISTS idx_courses_level ON courses(level);
CREATE INDEX IF NOT EXISTS idx_courses_tutor_id ON courses(tutor_id);
CREATE INDEX IF NOT EXISTS idx_courses_status ON courses(status);

-- Add foreign key constraint to ensure category exists (optional, can be removed if causing issues)
-- ALTER TABLE courses ADD CONSTRAINT fk_courses_category 
--     FOREIGN KEY (category) REFERENCES course_categories(name) ON DELETE SET NULL;

COMMENT ON COLUMN courses.category IS 'Dynamic category name from course_categories table';
COMMENT ON COLUMN courses.level IS 'CEFR level: A1, A2, B1, B2, C1, C2';
COMMENT ON COLUMN courses.price IS 'Course price (can be null if included in pack only)';
COMMENT ON COLUMN courses.thumbnail_url IS 'URL to course thumbnail image';
COMMENT ON COLUMN courses.objectives IS 'Learning objectives (JSON or text)';
COMMENT ON COLUMN courses.prerequisites IS 'Course prerequisites';
