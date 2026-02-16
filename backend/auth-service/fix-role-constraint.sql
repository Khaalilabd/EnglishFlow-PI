-- Fix role constraint to include ACADEMIC_OFFICE_AFFAIR
-- Drop the existing constraint
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

-- Add the new constraint with all roles
ALTER TABLE users ADD CONSTRAINT users_role_check 
CHECK (role IN ('ADMIN', 'TUTOR', 'STUDENT', 'ACADEMIC_OFFICE_AFFAIR'));

-- Verify the constraint
SELECT conname, pg_get_constraintdef(oid) 
FROM pg_constraint 
WHERE conrelid = 'users'::regclass AND conname = 'users_role_check';
