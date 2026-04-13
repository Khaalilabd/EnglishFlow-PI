-- Augmenter la taille de la colonne resource_link pour supporter les images base64
ALTER TABLE topics ALTER COLUMN resource_link TYPE TEXT;
