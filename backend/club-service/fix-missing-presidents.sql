-- Script pour ajouter les créateurs de clubs comme présidents dans la table members
-- Pour les clubs qui n'ont pas encore de président

-- Insérer les créateurs comme présidents pour tous les clubs qui n'ont pas de membres
INSERT INTO members (club_id, user_id, rank, joined_at)
SELECT 
    c.id as club_id,
    c.created_by as user_id,
    'PRESIDENT' as rank,
    c.created_at as joined_at
FROM clubs c
WHERE c.created_by IS NOT NULL
AND NOT EXISTS (
    SELECT 1 
    FROM members m 
    WHERE m.club_id = c.id 
    AND m.user_id = c.created_by
);

-- Vérifier le résultat
SELECT 
    c.id as club_id,
    c.name as club_name,
    c.created_by,
    COUNT(m.id) as member_count
FROM clubs c
LEFT JOIN members m ON c.id = m.club_id
GROUP BY c.id, c.name, c.created_by
ORDER BY c.id;
