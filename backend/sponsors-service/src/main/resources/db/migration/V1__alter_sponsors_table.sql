-- Make event_id and event_name nullable in sponsors table
ALTER TABLE sponsors ALTER COLUMN event_id DROP NOT NULL;
ALTER TABLE sponsors ALTER COLUMN event_name DROP NOT NULL;
