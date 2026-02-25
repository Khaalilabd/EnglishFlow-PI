-- Table pour les demandes de modification de club
CREATE TABLE IF NOT EXISTS club_update_requests (
    id SERIAL PRIMARY KEY,
    club_id INTEGER NOT NULL,
    requested_by BIGINT NOT NULL,
    name VARCHAR(255),
    description TEXT,
    objective TEXT,
    category VARCHAR(50),
    max_members INTEGER,
    image TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    vice_president_approved BOOLEAN DEFAULT FALSE,
    secretary_approved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    applied_at TIMESTAMP,
    CONSTRAINT fk_club_update_request_club FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE
);

-- Index pour améliorer les performances des requêtes
CREATE INDEX idx_club_update_requests_club_id ON club_update_requests(club_id);
CREATE INDEX idx_club_update_requests_status ON club_update_requests(status);
CREATE INDEX idx_club_update_requests_club_status ON club_update_requests(club_id, status);
