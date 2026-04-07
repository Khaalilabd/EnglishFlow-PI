-- Create membership_requests table for PostgreSQL
CREATE TABLE IF NOT EXISTS membership_requests (
    id SERIAL PRIMARY KEY,
    club_id INTEGER NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    message VARCHAR(500),
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL,
    reviewed_by BIGINT NULL,
    review_comment VARCHAR(500),
    CONSTRAINT fk_membership_request_club FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE,
    CONSTRAINT uk_membership_request_club_user UNIQUE (club_id, user_id)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_membership_request_club_id ON membership_requests(club_id);
CREATE INDEX IF NOT EXISTS idx_membership_request_user_id ON membership_requests(user_id);
CREATE INDEX IF NOT EXISTS idx_membership_request_status ON membership_requests(status);
