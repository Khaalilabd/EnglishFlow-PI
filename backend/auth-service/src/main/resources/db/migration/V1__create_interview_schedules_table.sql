-- Table pour stocker les rendez-vous d'entretien
CREATE TABLE IF NOT EXISTS interview_schedules (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL,
    interviewer_id BIGINT NOT NULL,
    scheduled_start TIMESTAMP NOT NULL,
    scheduled_end TIMESTAMP NOT NULL,
    duration_minutes INTEGER NOT NULL,
    meeting_link VARCHAR(500) NOT NULL,
    google_event_id VARCHAR(255),
    meeting_platform VARCHAR(50),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    cancellation_reason TEXT,
    
    CONSTRAINT fk_interview_schedule_application 
        FOREIGN KEY (application_id) 
        REFERENCES tutor_applications(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT fk_interview_schedule_interviewer 
        FOREIGN KEY (interviewer_id) 
        REFERENCES users(id) 
        ON DELETE RESTRICT
);

-- Index pour améliorer les performances des requêtes
CREATE INDEX idx_interview_schedules_application_id ON interview_schedules(application_id);
CREATE INDEX idx_interview_schedules_interviewer_id ON interview_schedules(interviewer_id);
CREATE INDEX idx_interview_schedules_scheduled_start ON interview_schedules(scheduled_start);
CREATE INDEX idx_interview_schedules_status ON interview_schedules(status);
CREATE INDEX idx_interview_schedules_google_event_id ON interview_schedules(google_event_id);

-- Index composite pour les requêtes de disponibilité
CREATE INDEX idx_interview_schedules_interviewer_date_status 
    ON interview_schedules(interviewer_id, scheduled_start, status);

-- Commentaires
COMMENT ON TABLE interview_schedules IS 'Stocke les rendez-vous d''entretien programmés avec les candidats';
COMMENT ON COLUMN interview_schedules.google_event_id IS 'ID de l''événement dans Google Calendar pour synchronisation';
COMMENT ON COLUMN interview_schedules.status IS 'SCHEDULED, COMPLETED, CANCELLED, NO_SHOW';
