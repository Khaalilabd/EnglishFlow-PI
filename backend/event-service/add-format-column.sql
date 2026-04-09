-- Add format column to events table
ALTER TABLE events 
ADD COLUMN IF NOT EXISTS format VARCHAR(20) NOT NULL DEFAULT 'IN_PERSON';

-- Update existing records to have a default format
UPDATE events 
SET format = 'IN_PERSON' 
WHERE format IS NULL;

-- Add comment to the column
COMMENT ON COLUMN events.format IS 'Event format: ONLINE or IN_PERSON';
