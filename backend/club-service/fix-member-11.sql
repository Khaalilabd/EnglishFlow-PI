-- Vérifier le membre avec ID 11
SELECT 
    m.id as member_id,
    m.club_id,
    c.name as club_name,
    m.user_id,
    m.rank,
    m.joined_at
FROM members m
JOIN clubs c ON m.club_id = c.id
WHERE m.id = 11;

-- Si le résultat montre que le rank n'est pas correct, exécutez cette commande :
-- UPDATE members SET rank = 'MEMBER' WHERE id = 11;
