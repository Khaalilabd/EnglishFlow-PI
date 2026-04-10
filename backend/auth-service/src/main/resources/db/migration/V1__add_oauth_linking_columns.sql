-- Add OAuth linking columns to users table

-- Add google_id column
ALTER TABLE users ADD COLUMN IF NOT EXISTS google_id VARCHAR(255);

-- Add linkedin_id column
ALTER TABLE users ADD COLUMN IF NOT EXISTS linkedin_id VARCHAR(255);

-- Add auth_provider column with default value
ALTER TABLE users ADD COLUMN IF NOT EXISTS auth_provider VARCHAR(50) NOT NULL DEFAULT 'LOCAL';

-- Create indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_google_id ON users(google_id);
CREATE INDEX IF NOT EXISTS idx_users_linkedin_id ON users(linkedin_id);
CREATE INDEX IF NOT EXISTS idx_users_auth_provider ON users(auth_provider);

-- Update existing users to have LOCAL as auth_provider if not set
UPDATE users SET auth_provider = 'LOCAL' WHERE auth_provider IS NULL;
