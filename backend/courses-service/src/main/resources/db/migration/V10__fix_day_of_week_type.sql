-- Fix day_of_week column type in lesson_time_assignments
-- Change from INTEGER to VARCHAR(20) to match the Java enum with @Enumerated(EnumType.STRING)

DO $$
BEGIN
    -- Check if the column exists and is INTEGER type
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'lesson_time_assignments'
        AND column_name = 'day_of_week'
        AND data_type = 'integer'
    ) THEN
        -- Drop the unique constraint first if it exists
        IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'unique_lesson_time_slot') THEN
            ALTER TABLE lesson_time_assignments DROP CONSTRAINT unique_lesson_time_slot;
        END IF;
        
        -- Change the column type from INTEGER to VARCHAR(20)
        ALTER TABLE lesson_time_assignments ALTER COLUMN day_of_week TYPE VARCHAR(20);
        
        -- Recreate the unique constraint
        ALTER TABLE lesson_time_assignments ADD CONSTRAINT unique_lesson_time_slot UNIQUE (tutor_id, day_of_week, start_time);
    END IF;
END $$;
