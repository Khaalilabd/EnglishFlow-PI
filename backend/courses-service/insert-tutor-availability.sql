-- Insert availability for tutor ID 11
-- First, delete any existing availability for this tutor
DELETE FROM tutor_availability WHERE tutor_id = 11;

-- Insert new availability
INSERT INTO tutor_availability (
    tutor_id, 
    tutor_name, 
    available_days, 
    time_slots, 
    categories, 
    levels, 
    hourly_rate, 
    max_students_capacity, 
    current_students_count, 
    status, 
    created_at, 
    updated_at
) VALUES (
    11,
    'Test Tutor',
    ARRAY['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY']::varchar[],
    '[{"startTime":"09:00","endTime":"11:00"},{"startTime":"13:00","endTime":"15:00"},{"startTime":"16:00","endTime":"18:00"}]'::jsonb,
    ARRAY['GENERAL_ENGLISH', 'BUSINESS_ENGLISH']::varchar[],
    ARRAY['BEGINNER', 'INTERMEDIATE', 'ADVANCED']::varchar[],
    30.00,
    15,
    0,
    'AVAILABLE',
    NOW(),
    NOW()
);

-- Verify the insert
SELECT * FROM tutor_availability WHERE tutor_id = 11;
