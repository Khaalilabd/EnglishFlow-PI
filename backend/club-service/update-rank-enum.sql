-- Script pour mettre à jour l'enum RankType dans PostgreSQL
-- Ce script ajoute les nouveaux rôles à l'enum existant

-- Ajouter les nouveaux rôles à l'enum
ALTER TYPE rank_type ADD VALUE IF NOT EXISTS 'VICE_PRESIDENT';
ALTER TYPE rank_type ADD VALUE IF NOT EXISTS 'TREASURER';
ALTER TYPE rank_type ADD VALUE IF NOT EXISTS 'COMMUNICATION_MANAGER';
ALTER TYPE rank_type ADD VALUE IF NOT EXISTS 'EVENT_MANAGER';
ALTER TYPE rank_type ADD VALUE IF NOT EXISTS 'PARTNERSHIP_MANAGER';

-- Vérifier les valeurs de l'enum
SELECT enumlabel 
FROM pg_enum 
WHERE enumtypid = 'rank_type'::regtype 
ORDER BY enumsortorder;
