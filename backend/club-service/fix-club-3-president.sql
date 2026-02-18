-- Script pour corriger le club 3 et ajouter le créateur comme président

-- 1. Vérifier le créateur du club 3
SELECT id, name, created_by, status FROM clubs WHERE id = 3;

-- 2. Vérifier les membres actuels du club 3
SELECT * FROM members WHERE club_id = 3;

-- 3. Ajouter l'utilisateur 8 comme président du club 3 (si c'est le créateur)
-- Remplace 8 par le created_by du club 3 si différent
INSERT INTO members (club_id, user_id, rank, joined_at)
SELECT 
    3 as club_id,
    created_by as user_id,
    'PRESIDENT' as rank,
    created_at as joined_at
FROM clubs 
WHERE id = 3 
AND created_by IS NOT NULL
AND NOT EXISTS (
    SELECT 1 FROM members WHERE club_id = 3 AND user_id = clubs.created_by
);

-- 4. Vérifier le résultat
SELECT 
    m.id,
    m.club_id,
    c.name as club_name,
    m.user_id,
    m.rank,
    m.joined_at
FROM members m
JOIN clubs c ON m.club_id = c.id
WHERE m.club_id = 3;
