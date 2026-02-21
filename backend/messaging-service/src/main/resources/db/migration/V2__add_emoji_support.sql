-- Migration pour ajouter le support des emojis
-- Date: 2026-02-21

-- Ajouter la colonne emoji_code à la table messages
ALTER TABLE messages 
ADD COLUMN emoji_code VARCHAR(50) NULL 
COMMENT 'Code emoji (ex: U+1F600 ou emoji natif UTF-8)';

-- Créer un index pour les recherches d'emojis si nécessaire
CREATE INDEX idx_message_emoji ON messages(emoji_code) WHERE emoji_code IS NOT NULL;

-- Mettre à jour l'enum message_type pour inclure EMOJI (si nécessaire selon votre SGBD)
-- Pour PostgreSQL, vous devrez peut-être utiliser:
-- ALTER TYPE message_type ADD VALUE IF NOT EXISTS 'EMOJI';

-- Pour MySQL, l'enum sera automatiquement mis à jour par JPA/Hibernate
