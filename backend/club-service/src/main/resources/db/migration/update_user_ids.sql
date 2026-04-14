-- ============================================================
-- FIX: Delete and reinsert members with correct user_ids (1-5)
-- ============================================================

-- Supprimer tous les membres des clubs seedés
DELETE FROM members WHERE club_id IN (SELECT id FROM clubs WHERE name IN (
    'English Conversation Circle',
    'The Book Lovers Club',
    'Drama & Theater Club',
    'Business English Club',
    'Creative Writing Workshop',
    'Pronunciation Masters',
    'Academic English Society',
    'Listening & Media Club'
));

-- English Conversation Circle
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',      (SELECT id FROM clubs WHERE name = 'English Conversation Circle'), 1, NOW() - INTERVAL '89 days', NOW()),
('VICE_PRESIDENT', (SELECT id FROM clubs WHERE name = 'English Conversation Circle'), 2, NOW() - INTERVAL '85 days', NOW()),
('SECRETARY',      (SELECT id FROM clubs WHERE name = 'English Conversation Circle'), 3, NOW() - INTERVAL '85 days', NOW()),
('TREASURER',      (SELECT id FROM clubs WHERE name = 'English Conversation Circle'), 4, NOW() - INTERVAL '84 days', NOW()),
('EVENT_MANAGER',  (SELECT id FROM clubs WHERE name = 'English Conversation Circle'), 5, NOW() - INTERVAL '80 days', NOW());

-- The Book Lovers Club
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',      (SELECT id FROM clubs WHERE name = 'The Book Lovers Club'), 2, NOW() - INTERVAL '79 days', NOW()),
('VICE_PRESIDENT', (SELECT id FROM clubs WHERE name = 'The Book Lovers Club'), 3, NOW() - INTERVAL '75 days', NOW()),
('SECRETARY',      (SELECT id FROM clubs WHERE name = 'The Book Lovers Club'), 4, NOW() - INTERVAL '74 days', NOW()),
('MEMBER',         (SELECT id FROM clubs WHERE name = 'The Book Lovers Club'), 5, NOW() - INTERVAL '70 days', NOW());

-- Drama & Theater Club
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',             (SELECT id FROM clubs WHERE name = 'Drama & Theater Club'), 3, NOW() - INTERVAL '69 days', NOW()),
('VICE_PRESIDENT',        (SELECT id FROM clubs WHERE name = 'Drama & Theater Club'), 4, NOW() - INTERVAL '65 days', NOW()),
('SECRETARY',             (SELECT id FROM clubs WHERE name = 'Drama & Theater Club'), 5, NOW() - INTERVAL '64 days', NOW()),
('COMMUNICATION_MANAGER', (SELECT id FROM clubs WHERE name = 'Drama & Theater Club'), 1, NOW() - INTERVAL '63 days', NOW()),
('MEMBER',                (SELECT id FROM clubs WHERE name = 'Drama & Theater Club'), 2, NOW() - INTERVAL '60 days', NOW());

-- Business English Club
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',           (SELECT id FROM clubs WHERE name = 'Business English Club'), 4, NOW() - INTERVAL '59 days', NOW()),
('VICE_PRESIDENT',      (SELECT id FROM clubs WHERE name = 'Business English Club'), 5, NOW() - INTERVAL '55 days', NOW()),
('SECRETARY',           (SELECT id FROM clubs WHERE name = 'Business English Club'), 1, NOW() - INTERVAL '54 days', NOW()),
('TREASURER',           (SELECT id FROM clubs WHERE name = 'Business English Club'), 2, NOW() - INTERVAL '53 days', NOW()),
('PARTNERSHIP_MANAGER', (SELECT id FROM clubs WHERE name = 'Business English Club'), 3, NOW() - INTERVAL '50 days', NOW());

-- Creative Writing Workshop
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',  (SELECT id FROM clubs WHERE name = 'Creative Writing Workshop'), 5, NOW() - INTERVAL '49 days', NOW()),
('SECRETARY',  (SELECT id FROM clubs WHERE name = 'Creative Writing Workshop'), 1, NOW() - INTERVAL '45 days', NOW()),
('TREASURER',  (SELECT id FROM clubs WHERE name = 'Creative Writing Workshop'), 2, NOW() - INTERVAL '44 days', NOW()),
('MEMBER',     (SELECT id FROM clubs WHERE name = 'Creative Writing Workshop'), 3, NOW() - INTERVAL '40 days', NOW());

-- Pronunciation Masters
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',  (SELECT id FROM clubs WHERE name = 'Pronunciation Masters'), 1, NOW() - INTERVAL '44 days', NOW()),
('SECRETARY',  (SELECT id FROM clubs WHERE name = 'Pronunciation Masters'), 2, NOW() - INTERVAL '40 days', NOW()),
('MEMBER',     (SELECT id FROM clubs WHERE name = 'Pronunciation Masters'), 3, NOW() - INTERVAL '35 days', NOW()),
('MEMBER',     (SELECT id FROM clubs WHERE name = 'Pronunciation Masters'), 4, NOW() - INTERVAL '30 days', NOW());

-- Academic English Society
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',             (SELECT id FROM clubs WHERE name = 'Academic English Society'), 2, NOW() - INTERVAL '39 days', NOW()),
('VICE_PRESIDENT',        (SELECT id FROM clubs WHERE name = 'Academic English Society'), 3, NOW() - INTERVAL '35 days', NOW()),
('SECRETARY',             (SELECT id FROM clubs WHERE name = 'Academic English Society'), 4, NOW() - INTERVAL '34 days', NOW()),
('TREASURER',             (SELECT id FROM clubs WHERE name = 'Academic English Society'), 5, NOW() - INTERVAL '33 days', NOW()),
('COMMUNICATION_MANAGER', (SELECT id FROM clubs WHERE name = 'Academic English Society'), 1, NOW() - INTERVAL '30 days', NOW());

-- Listening & Media Club
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',  (SELECT id FROM clubs WHERE name = 'Listening & Media Club'), 3, NOW() - INTERVAL '34 days', NOW()),
('SECRETARY',  (SELECT id FROM clubs WHERE name = 'Listening & Media Club'), 4, NOW() - INTERVAL '30 days', NOW()),
('MEMBER',     (SELECT id FROM clubs WHERE name = 'Listening & Media Club'), 5, NOW() - INTERVAL '28 days', NOW()),
('MEMBER',     (SELECT id FROM clubs WHERE name = 'Listening & Media Club'), 1, NOW() - INTERVAL '25 days', NOW());

-- ============================================================
-- UPDATE created_by = user_id du PRESIDENT
-- ============================================================
UPDATE clubs SET created_by = (SELECT user_id FROM members WHERE club_id = clubs.id AND rank = 'PRESIDENT')
WHERE name IN (
    'English Conversation Circle',
    'The Book Lovers Club',
    'Drama & Theater Club',
    'Business English Club',
    'Creative Writing Workshop',
    'Pronunciation Masters',
    'Academic English Society',
    'Listening & Media Club'
);
UPDATE clubs SET created_by = 4 WHERE name = 'Vocabulary Builders';
UPDATE clubs SET created_by = 5 WHERE name = 'Public Speaking Club';
