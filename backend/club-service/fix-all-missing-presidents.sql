-- Script pour corriger TOUS les clubs et ajouter les créateurs comme présidents

-- 1. Afficher tous les clubs qui ont un problème
SELECT 
    c.id as club_id,
    c.name as club_name,
    c.created_by as creator_user_id,
    COUNT(m.id) as member_count,
    MAX(CASE WHEN m.user_id = c.created_by AND m.rank = 'PRESIDENT' THEN 1 ELSE 0 END) as has_president
FROM clubs c
LEFT JOIN members m ON c.id = m.club_id
WHERE c.created_by IS NOT NULL
GROUP BY c.id, c.name, c.created_by
HAVING has_president = 0;

-- 2. Corriger tous les clubs en ajoutant les créateurs comme présidents
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
    AND m.rank = 'PRESIDENT'
);

-- 3. Vérifier le résultat final
SELECT 
    c.id as club_id,
    c.name as club_name,
    c.created_by as creator_user_id,
    COUNT(m.id) as total_members,
    SUM(CASE WHEN m.rank = 'PRESIDENT' THEN 1 ELSE 0 END) as president_count
FROM clubs c
LEFT JOIN members m ON c.id = m.club_id
GROUP BY c.id, c.name, c.created_by
ORDER BY c.id;
