-- Script pour corriger les rôles des membres (PostgreSQL)

-- 1. D'abord, afficher les membres avec des rôles incorrects
SELECT 
    m.id as member_id,
    m.club_id,
    c.name as club_name,
    m.user_id,
    m.rank as current_rank,
    c.created_by as club_creator,
    CASE 
        WHEN m.user_id = c.created_by THEN 'PRESIDENT'
        ELSE 'MEMBER'
    END as expected_rank,
    CASE 
        WHEN m.user_id = c.created_by AND m.rank != 'PRESIDENT' THEN '❌ INCORRECT'
        WHEN m.user_id != c.created_by AND m.rank = 'PRESIDENT' THEN '❌ INCORRECT'
        ELSE '✅ CORRECT'
    END as status
FROM members m
JOIN clubs c ON m.club_id = c.id
ORDER BY m.club_id, m.joined_at;

-- 2. Corriger les créateurs qui ne sont pas présidents
UPDATE members m
SET rank = 'PRESIDENT',
    updated_at = NOW()
FROM clubs c
WHERE m.club_id = c.id
  AND m.user_id = c.created_by 
  AND m.rank != 'PRESIDENT';

-- 3. Corriger les non-créateurs qui sont présidents (sauf s'ils sont les seuls présidents)
UPDATE members m
SET rank = 'MEMBER',
    updated_at = NOW()
FROM clubs c
WHERE m.club_id = c.id
  AND m.user_id != c.created_by 
  AND m.rank = 'PRESIDENT'
  AND EXISTS (
    -- S'assurer qu'il y a au moins un autre président (le créateur)
    SELECT 1 
    FROM members m2 
    WHERE m2.club_id = m.club_id 
      AND m2.user_id = c.created_by 
      AND m2.rank = 'PRESIDENT'
  );

-- 4. Vérifier les résultats après correction
SELECT 
    m.id as member_id,
    m.club_id,
    c.name as club_name,
    m.user_id,
    m.rank as current_rank,
    c.created_by as club_creator,
    CASE 
        WHEN m.user_id = c.created_by THEN 'PRESIDENT'
        ELSE 'MEMBER'
    END as expected_rank,
    CASE 
        WHEN m.user_id = c.created_by AND m.rank != 'PRESIDENT' THEN '❌ INCORRECT'
        WHEN m.user_id != c.created_by AND m.rank = 'PRESIDENT' THEN '❌ INCORRECT'
        ELSE '✅ CORRECT'
    END as status
FROM members m
JOIN clubs c ON m.club_id = c.id
ORDER BY m.club_id, m.joined_at;
