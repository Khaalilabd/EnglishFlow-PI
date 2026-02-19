-- Migration V2: Add Invitations System
-- Date: 2026-02-19
-- Description: Create invitations table for secure user onboarding

CREATE TABLE IF NOT EXISTS invitations (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    invited_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP,
    
    CONSTRAINT fk_invited_by FOREIGN KEY (invited_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_invitations_email ON invitations(email);
CREATE INDEX idx_invitations_token ON invitations(token);
CREATE INDEX idx_invitations_used ON invitations(used);
CREATE INDEX idx_invitations_expiry_date ON invitations(expiry_date);

-- Add comments for documentation
COMMENT ON TABLE invitations IS 'Stores invitation tokens for TUTOR and ACADEMIC_OFFICE_AFFAIR roles';
COMMENT ON COLUMN invitations.email IS 'Email address of the invited person';
COMMENT ON COLUMN invitations.token IS 'Unique UUID token for invitation link';
COMMENT ON COLUMN invitations.role IS 'Role to be assigned (TUTOR or ACADEMIC_OFFICE_AFFAIR)';
COMMENT ON COLUMN invitations.expiry_date IS 'Invitation expiration date (7 days from creation)';
COMMENT ON COLUMN invitations.used IS 'Whether the invitation has been accepted';
COMMENT ON COLUMN invitations.invited_by IS 'ID of the admin who sent the invitation';
COMMENT ON COLUMN invitations.created_at IS 'Timestamp when invitation was created';
COMMENT ON COLUMN invitations.used_at IS 'Timestamp when invitation was accepted';
