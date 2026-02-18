-- Script pour vérifier les données des membres et leurs rôles

-- 1. Afficher tous les clubs avec leurs créateurs
SELECT 
    c.id as club_id,
    c.name as club_name,
    c.created_by as creator_user_id,
    c.status
FROM clubs c
ORDER BY c.id;

-- 2. Afficher tous les membres avec leurs rôles
SELECT 
    m.id as member_id,
    m.club_id,
    c.name as club_name,
    m.user_id,
    m.rank,
    m.joined_at
FROM members m
JOIN clubs c ON m.club_id = c.id
ORDER BY m.club_id, m.rank DESC;

-- 3. Vérifier si les créateurs sont bien présidents
SELECT 
    c.id as club_id,
    c.name as club_name,
    c.created_by as creator_user_id,
    m.user_id as member_user_id,
    m.rank as member_rank,
    CASE 
        WHEN m.rank = 'PRESIDENT' THEN '✓ OK'
        WHEN m.rank IS NULL THEN '✗ MISSING - Creator not in members table'
        ELSE '✗ WRONG RANK - Should be PRESIDENT'
    END as status
FROM clubs c
LEFT JOIN members m ON c.id = m.club_id AND c.created_by = m.user_id
ORDER BY c.id;

-- 4. Trouver les clubs où le créateur n'est pas président
SELECT 
    c.id as club_id,
    c.name as club_name,
    c.created_by as creator_user_id
FROM clubs c
WHERE c.created_by IS NOT NULL
AND NOT EXISTS (
    SELECT 1 
    FROM members m 
    WHERE m.club_id = c.id 
    AND m.user_id = c.created_by 
    AND m.rank = 'PRESIDENT'
);

-- 5. Corriger les données - Supprimer les mauvaises entrées et ajouter les bonnes
-- ATTENTION: Exécuter ces commandes une par une après vérification

-- Supprimer les entrées où le créateur n'est pas président
DELETE FROM members 
WHERE (club_id, user_id) IN (
    SELECT c.id, c.created_by
    FROM clubs c
    JOIN members m ON c.id = m.club_id AND c.created_by = m.user_id
    WHERE m.rank != 'PRESIDENT'
);

-- Ajouter les créateurs comme présidents s'ils manquent
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
