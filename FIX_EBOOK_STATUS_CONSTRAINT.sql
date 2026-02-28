-- Fix ebook status constraint to allow PENDING and REJECTED values
-- Run this SQL script directly on your PostgreSQL database

-- Drop the old constraint
ALTER TABLE ebook DROP CONSTRAINT IF EXISTS ebook_status_check;

-- Add new constraint with all status values including PENDING and REJECTED
ALTER TABLE ebook ADD CONSTRAINT ebook_status_check 
    CHECK (status IN ('DRAFT', 'PENDING', 'SCHEDULED', 'PUBLISHED', 'ARCHIVED', 'REJECTED'));

-- Verify the constraint was added
SELECT conname, pg_get_constraintdef(oid) 
FROM pg_constraint 
WHERE conname = 'ebook_status_check';
