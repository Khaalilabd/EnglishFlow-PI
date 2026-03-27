-- Migration pour ajouter le type de message VOICE
-- Date: 2026-02-22

-- Supprimer l'ancienne contrainte
ALTER TABLE messages DROP CONSTRAINT IF EXISTS messages_message_type_check;

-- Ajouter la nouvelle contrainte avec VOICE
ALTER TABLE messages ADD CONSTRAINT messages_message_type_check 
    CHECK (message_type IN ('TEXT', 'FILE', 'IMAGE', 'EMOJI', 'VOICE'));

-- Ajouter la colonne voice_duration si elle n'existe pas déjà
ALTER TABLE messages ADD COLUMN IF NOT EXISTS voice_duration INTEGER;

-- Commentaire pour la documentation
COMMENT ON COLUMN messages.voice_duration IS 'Durée du message vocal en secondes';
