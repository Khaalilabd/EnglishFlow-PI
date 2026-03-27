-- Add createdBy column to ebook table
ALTER TABLE ebook ADD COLUMN IF NOT EXISTS created_by BIGINT;

-- Update existing ebooks to have PUBLISHED status if they don't have PENDING/REJECTED
UPDATE ebook 
SET status = 'PUBLISHED' 
WHERE status NOT IN ('PENDING', 'REJECTED') 
  AND status IS NOT NULL;
