-- Add PENDING and REJECTED to the ebook status check constraint
-- Drop the old constraint
ALTER TABLE ebook DROP CONSTRAINT IF EXISTS ebook_status_check;

-- Add new constraint with PENDING and REJECTED
ALTER TABLE ebook ADD CONSTRAINT ebook_status_check 
    CHECK (status IN ('DRAFT', 'PENDING', 'SCHEDULED', 'PUBLISHED', 'ARCHIVED', 'REJECTED'));
