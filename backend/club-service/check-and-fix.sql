-- 1. Vérifier tous les membres
SELECT 
    m.id as member_id,
    m.club_id,
    c.name as club_name,
    m.user_id,
    m.rank,
    m.joined_at
FROM members m
JOIN clubs c ON m.club_id = c.id
ORDER BY m.club_id, m.id;

-- 2. Vérifier spécifiquement le membre ID 11
SELECT * FROM members WHERE id = 11;

-- 3. Si le membre 11 a le mauvais rôle, le corriger :
-- UPDATE members SET rank = 'MEMBER' WHERE id = 11;
