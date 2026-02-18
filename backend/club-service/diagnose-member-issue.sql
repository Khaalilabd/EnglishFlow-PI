-- Script de diagnostic pour le problème des rôles des membres

-- 1. Afficher tous les membres avec leurs rôles
SELECT 
    m.id as member_id,
    m.club_id,
    c.name as club_name,
    m.user_id,
    m.rank,
    m.joined_at,
    m.updated_at
FROM members m
JOIN clubs c ON m.club_id = c.id
ORDER BY m.club_id, m.joined_at;

-- 2. Compter les présidents par club
SELECT 
    club_id,
    COUNT(*) as president_count
FROM members
WHERE rank = 'PRESIDENT'
GROUP BY club_id
HAVING COUNT(*) > 1;

-- 3. Trouver les membres qui ont rejoint récemment avec le mauvais rôle
SELECT 
    m.id as member_id,
    m.club_id,
    c.name as club_name,
    m.user_id,
    m.rank,
    c.created_by as club_creator,
    CASE 
        WHEN m.user_id = c.created_by THEN 'Creator (should be PRESIDENT)'
        ELSE 'Regular member (should be MEMBER)'
    END as expected_role,
    m.joined_at
FROM members m
JOIN clubs c ON m.club_id = c.id
WHERE (m.user_id = c.created_by AND m.rank != 'PRESIDENT')
   OR (m.user_id != c.created_by AND m.rank = 'PRESIDENT')
ORDER BY m.joined_at DESC;
