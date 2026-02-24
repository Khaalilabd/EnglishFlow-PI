-- Convert Pack category and level from enum to string
ALTER TABLE packs 
ALTER COLUMN category TYPE VARCHAR(100),
ALTER COLUMN level TYPE VARCHAR(10);

-- Convert TutorAvailability categories and levels from enum to string
-- The ElementCollection tables will be recreated by Hibernate with the new type
-- No need to manually alter them as they will be dropped and recreated

-- Add index for category-based searches
CREATE INDEX IF NOT EXISTS idx_packs_category ON packs(category);
CREATE INDEX IF NOT EXISTS idx_packs_level ON packs(level);
CREATE INDEX IF NOT EXISTS idx_packs_category_level ON packs(category, level);
