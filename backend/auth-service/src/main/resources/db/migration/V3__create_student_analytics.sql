-- Suppression de la table si elle existe (pour recréer avec la bonne syntaxe PostgreSQL)
DROP TABLE IF EXISTS student_analytics CASCADE;

-- Création de la table student_analytics pour tracker les données ML (PostgreSQL)
CREATE TABLE student_analytics (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    
    -- Données d'interaction
    total_clicks INTEGER NOT NULL DEFAULT 0,
    total_sessions INTEGER NOT NULL DEFAULT 0,
    avg_clicks_per_session INTEGER NOT NULL DEFAULT 0,
    max_clicks_in_session INTEGER NOT NULL DEFAULT 0,
    
    -- Données d'évaluation
    avg_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    min_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    max_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    total_assessments INTEGER NOT NULL DEFAULT 0,
    completed_tma INTEGER NOT NULL DEFAULT 0,
    completed_cma INTEGER NOT NULL DEFAULT 0,
    completed_exams INTEGER NOT NULL DEFAULT 0,
    
    -- Données d'engagement
    previous_attempts INTEGER NOT NULL DEFAULT 0,
    studied_credits INTEGER NOT NULL DEFAULT 0,
    last_activity_at TIMESTAMP NULL,
    first_registration_date TIMESTAMP NULL,
    is_unregistered BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Métadonnées
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Contraintes
    CONSTRAINT fk_student_analytics_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index pour améliorer les performances
CREATE INDEX idx_student_analytics_user_id ON student_analytics(user_id);
CREATE INDEX idx_student_analytics_last_activity ON student_analytics(last_activity_at);

-- Fonction pour mettre à jour automatiquement updated_at
CREATE OR REPLACE FUNCTION update_student_analytics_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger pour mettre à jour updated_at automatiquement
CREATE TRIGGER trigger_update_student_analytics_updated_at
    BEFORE UPDATE ON student_analytics
    FOR EACH ROW
    EXECUTE FUNCTION update_student_analytics_updated_at();

-- Initialiser les analytics pour les étudiants existants
INSERT INTO student_analytics (user_id, first_registration_date, created_at, updated_at)
SELECT 
    id,
    created_at,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users
WHERE role = 'STUDENT'
ON CONFLICT (user_id) DO NOTHING;
