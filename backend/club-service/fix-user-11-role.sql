-- Vérifier d'abord les données actuelles
SELECT m.id as member_id, m.club_id, m.user_id, m.rank 
FROM members m 
WHERE m.user_id IN (8, 11)
ORDER BY m.club_id, m.user_id;

-- Corriger le rôle de l'utilisateur 11 pour qu'il soit MEMBER
UPDATE members 
SET rank = 'MEMBER' 
WHERE user_id = 11 AND rank != 'MEMBER';

-- Vérifier après la correction
SELECT m.id as member_id, m.club_id, m.user_id, m.rank 
FROM members m 
WHERE m.user_id IN (8, 11)
ORDER BY m.club_id, m.user_id;
