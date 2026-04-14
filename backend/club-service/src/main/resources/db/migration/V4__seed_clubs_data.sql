-- ============================================================
-- SEED DATA - Clubs (IDs start at 8)
-- ============================================================

-- Reset sequence to start at 8
SELECT setval('clubs_id_seq', 7, true);

-- ============================================================
-- CLUBS
-- ============================================================
INSERT INTO clubs (name, description, objective, category, max_members, registration_fee, image, status, created_by, reviewed_by, review_comment, created_at, updated_at) VALUES

('English Conversation Circle',
 'Un espace convivial pour pratiquer l''anglais oral à travers des discussions libres, des débats et des jeux de rôle.',
 'Améliorer la fluidité et la confiance en communication orale en anglais.',
 'CONVERSATION', 30, 15.00,
 'https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=400',
 'APPROVED', 1, 1, 'Club bien structuré, approuvé.',
 NOW() - INTERVAL '90 days', NOW() - INTERVAL '89 days'),

('The Book Lovers Club',
 'Club dédié à la lecture de romans, nouvelles et essais en anglais. Chaque mois un nouveau livre est analysé et discuté.',
 'Développer la compréhension écrite et enrichir le vocabulaire à travers la littérature anglophone.',
 'BOOK', 25, 10.00,
 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400',
 'APPROVED', 2, 1, 'Excellent projet culturel.',
 NOW() - INTERVAL '80 days', NOW() - INTERVAL '79 days'),

('Drama & Theater Club',
 'Club de théâtre en anglais : jeux de rôle, pièces de Shakespeare, improvisation et performances scéniques.',
 'Renforcer l''expression orale, la prononciation et la confiance en soi à travers l''art dramatique.',
 'DRAMA', 20, 20.00,
 'https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?w=400',
 'APPROVED', 3, 1, 'Initiative créative très appréciée.',
 NOW() - INTERVAL '70 days', NOW() - INTERVAL '69 days'),

('Business English Club',
 'Club orienté vers l''anglais professionnel : rédaction d''emails, présentations, négociations et entretiens.',
 'Préparer les étudiants au monde professionnel anglophone.',
 'BUSINESS', 35, 25.00,
 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=400',
 'APPROVED', 4, 1, 'Très pertinent pour l''insertion professionnelle.',
 NOW() - INTERVAL '60 days', NOW() - INTERVAL '59 days'),

('Creative Writing Workshop',
 'Atelier d''écriture créative en anglais : nouvelles, poésie, scripts et journaux personnels.',
 'Développer la créativité et la maîtrise de l''écrit en anglais.',
 'WRITING', 20, 12.00,
 'https://images.unsplash.com/photo-1455390582262-044cdead277a?w=400',
 'APPROVED', 5, 1, 'Projet original et enrichissant.',
 NOW() - INTERVAL '50 days', NOW() - INTERVAL '49 days'),

('Pronunciation Masters',
 'Club spécialisé dans la phonétique anglaise, l''accent britannique et américain, et les techniques de diction.',
 'Corriger les erreurs de prononciation et acquérir un accent naturel.',
 'PRONUNCIATION', 15, 10.00,
 'https://images.unsplash.com/photo-1516321497487-e288fb19713f?w=400',
 'APPROVED', 1, 1, 'Besoin réel identifié chez les étudiants.',
 NOW() - INTERVAL '45 days', NOW() - INTERVAL '44 days'),

('Academic English Society',
 'Club axé sur l''anglais académique : rédaction de dissertations, résumés, rapports et préparation aux examens IELTS/TOEFL.',
 'Maîtriser l''anglais académique pour réussir les certifications internationales.',
 'ACADEMIC', 40, 30.00,
 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400',
 'APPROVED', 2, 1, 'Club stratégique pour les certifications.',
 NOW() - INTERVAL '40 days', NOW() - INTERVAL '39 days'),

('Listening & Media Club',
 'Club centré sur l''écoute active : podcasts, films, séries, TED Talks et actualités en anglais.',
 'Améliorer la compréhension orale à travers des médias authentiques.',
 'LISTENING', 25, 8.00,
 'https://images.unsplash.com/photo-1478737270239-2f02b77fc618?w=400',
 'APPROVED', 3, 1, 'Approche moderne et efficace.',
 NOW() - INTERVAL '35 days', NOW() - INTERVAL '34 days'),

('Vocabulary Builders',
 'Club dédié à l''enrichissement du vocabulaire anglais via des jeux, flashcards, étymologie et contextes réels.',
 'Acquérir un vocabulaire riche et varié pour s''exprimer avec précision.',
 'VOCABULARY', 30, 8.00,
 'https://images.unsplash.com/photo-1546521343-4eb2c01aa44b?w=400',
 'PENDING', 4, NULL, NULL,
 NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),

('Public Speaking Club',
 'Club de prise de parole en public : discours, débats formels, présentations et techniques de persuasion.',
 'Vaincre le trac et devenir un orateur confiant en anglais.',
 'SPEAKING', 20, 15.00,
 'https://images.unsplash.com/photo-1475721027785-f74eccf877e2?w=400',
 'PENDING', 5, NULL, NULL,
 NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days');

-- ============================================================
-- MEMBERS (using subqueries to avoid hardcoded IDs)
-- ============================================================

-- Club: English Conversation Circle
-- user 1 = PRESIDENT, les autres tournent sur 2-5
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',      (SELECT id FROM clubs WHERE name = 'English Conversation Circle'), 1, NOW() - INTERVAL '89 days', NOW()),
('VICE_PRESIDENT', (SELECT id FROM clubs WHERE name = 'English Conversation Circle'), 2, NOW() - INTERVAL '85 days', NOW()),
('SECRETARY',      (SELECT id FROM clubs WHERE name = 'English Conversation Circle'), 3, NOW() - INTERVAL '85 days', NOW()),
('TREASURER',      (SELECT id FROM clubs WHERE name = 'English Conversation Circle'), 4, NOW() - INTERVAL '84 days', NOW()),
('EVENT_MANAGER',  (SELECT id FROM clubs WHERE name = 'English Conversation Circle'), 5, NOW() - INTERVAL '80 days', NOW());

-- Club: The Book Lovers Club
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',      (SELECT id FROM clubs WHERE name = 'The Book Lovers Club'), 2, NOW() - INTERVAL '79 days', NOW()),
('VICE_PRESIDENT', (SELECT id FROM clubs WHERE name = 'The Book Lovers Club'), 3, NOW() - INTERVAL '75 days', NOW()),
('SECRETARY',      (SELECT id FROM clubs WHERE name = 'The Book Lovers Club'), 4, NOW() - INTERVAL '74 days', NOW()),
('MEMBER',         (SELECT id FROM clubs WHERE name = 'The Book Lovers Club'), 5, NOW() - INTERVAL '70 days', NOW());

-- Club: Drama & Theater Club
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',             (SELECT id FROM clubs WHERE name = 'Drama & Theater Club'), 3, NOW() - INTERVAL '69 days', NOW()),
('VICE_PRESIDENT',        (SELECT id FROM clubs WHERE name = 'Drama & Theater Club'), 4, NOW() - INTERVAL '65 days', NOW()),
('SECRETARY',             (SELECT id FROM clubs WHERE name = 'Drama & Theater Club'), 5, NOW() - INTERVAL '64 days', NOW()),
('COMMUNICATION_MANAGER', (SELECT id FROM clubs WHERE name = 'Drama & Theater Club'), 1, NOW() - INTERVAL '63 days', NOW()),
('MEMBER',                (SELECT id FROM clubs WHERE name = 'Drama & Theater Club'), 2, NOW() - INTERVAL '60 days', NOW());

-- Club: Business English Club
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',           (SELECT id FROM clubs WHERE name = 'Business English Club'), 4, NOW() - INTERVAL '59 days', NOW()),
('VICE_PRESIDENT',      (SELECT id FROM clubs WHERE name = 'Business English Club'), 5, NOW() - INTERVAL '55 days', NOW()),
('SECRETARY',           (SELECT id FROM clubs WHERE name = 'Business English Club'), 1, NOW() - INTERVAL '54 days', NOW()),
('TREASURER',           (SELECT id FROM clubs WHERE name = 'Business English Club'), 2, NOW() - INTERVAL '53 days', NOW()),
('PARTNERSHIP_MANAGER', (SELECT id FROM clubs WHERE name = 'Business English Club'), 3, NOW() - INTERVAL '50 days', NOW());

-- Club: Creative Writing Workshop
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',  (SELECT id FROM clubs WHERE name = 'Creative Writing Workshop'), 5, NOW() - INTERVAL '49 days', NOW()),
('SECRETARY',  (SELECT id FROM clubs WHERE name = 'Creative Writing Workshop'), 1, NOW() - INTERVAL '45 days', NOW()),
('TREASURER',  (SELECT id FROM clubs WHERE name = 'Creative Writing Workshop'), 2, NOW() - INTERVAL '44 days', NOW()),
('MEMBER',     (SELECT id FROM clubs WHERE name = 'Creative Writing Workshop'), 3, NOW() - INTERVAL '40 days', NOW());

-- Club: Pronunciation Masters
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',  (SELECT id FROM clubs WHERE name = 'Pronunciation Masters'), 1, NOW() - INTERVAL '44 days', NOW()),
('SECRETARY',  (SELECT id FROM clubs WHERE name = 'Pronunciation Masters'), 2, NOW() - INTERVAL '40 days', NOW()),
('MEMBER',     (SELECT id FROM clubs WHERE name = 'Pronunciation Masters'), 3, NOW() - INTERVAL '35 days', NOW()),
('MEMBER',     (SELECT id FROM clubs WHERE name = 'Pronunciation Masters'), 4, NOW() - INTERVAL '30 days', NOW());

-- Club: Academic English Society
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',             (SELECT id FROM clubs WHERE name = 'Academic English Society'), 2, NOW() - INTERVAL '39 days', NOW()),
('VICE_PRESIDENT',        (SELECT id FROM clubs WHERE name = 'Academic English Society'), 3, NOW() - INTERVAL '35 days', NOW()),
('SECRETARY',             (SELECT id FROM clubs WHERE name = 'Academic English Society'), 4, NOW() - INTERVAL '34 days', NOW()),
('TREASURER',             (SELECT id FROM clubs WHERE name = 'Academic English Society'), 5, NOW() - INTERVAL '33 days', NOW()),
('COMMUNICATION_MANAGER', (SELECT id FROM clubs WHERE name = 'Academic English Society'), 1, NOW() - INTERVAL '30 days', NOW());

-- Club: Listening & Media Club
INSERT INTO members (rank, club_id, user_id, joined_at, updated_at) VALUES
('PRESIDENT',  (SELECT id FROM clubs WHERE name = 'Listening & Media Club'), 3, NOW() - INTERVAL '34 days', NOW()),
('SECRETARY',  (SELECT id FROM clubs WHERE name = 'Listening & Media Club'), 4, NOW() - INTERVAL '30 days', NOW()),
('MEMBER',     (SELECT id FROM clubs WHERE name = 'Listening & Media Club'), 5, NOW() - INTERVAL '28 days', NOW()),
('MEMBER',     (SELECT id FROM clubs WHERE name = 'Listening & Media Club'), 1, NOW() - INTERVAL '25 days', NOW());

-- ============================================================
-- SKILLS
-- ============================================================
INSERT INTO skills (name, description, club_id, created_at) VALUES
('Active Listening',    'Écouter attentivement et répondre de manière pertinente',    (SELECT id FROM clubs WHERE name = 'English Conversation Circle'), NOW() - INTERVAL '88 days'),
('Debate Skills',       'Argumenter et défendre un point de vue en anglais',          (SELECT id FROM clubs WHERE name = 'English Conversation Circle'), NOW() - INTERVAL '88 days'),
('Storytelling',        'Raconter des histoires de façon captivante',                 (SELECT id FROM clubs WHERE name = 'English Conversation Circle'), NOW() - INTERVAL '87 days'),

('Literary Analysis',   'Analyser les thèmes, personnages et structure d''un texte',  (SELECT id FROM clubs WHERE name = 'The Book Lovers Club'), NOW() - INTERVAL '78 days'),
('Critical Thinking',   'Évaluer et critiquer des œuvres littéraires',               (SELECT id FROM clubs WHERE name = 'The Book Lovers Club'), NOW() - INTERVAL '78 days'),
('Speed Reading',       'Lire rapidement tout en retenant l''essentiel',              (SELECT id FROM clubs WHERE name = 'The Book Lovers Club'), NOW() - INTERVAL '77 days'),

('Stage Presence',      'Maîtriser sa présence sur scène',                            (SELECT id FROM clubs WHERE name = 'Drama & Theater Club'), NOW() - INTERVAL '68 days'),
('Voice Projection',    'Projeter sa voix clairement et avec expression',             (SELECT id FROM clubs WHERE name = 'Drama & Theater Club'), NOW() - INTERVAL '68 days'),
('Improvisation',       'Réagir spontanément dans des situations imprévues',          (SELECT id FROM clubs WHERE name = 'Drama & Theater Club'), NOW() - INTERVAL '67 days'),

('Email Writing',       'Rédiger des emails professionnels formels',                  (SELECT id FROM clubs WHERE name = 'Business English Club'), NOW() - INTERVAL '58 days'),
('Negotiation',         'Techniques de négociation en anglais des affaires',          (SELECT id FROM clubs WHERE name = 'Business English Club'), NOW() - INTERVAL '58 days'),
('Presentation Skills', 'Créer et délivrer des présentations professionnelles',       (SELECT id FROM clubs WHERE name = 'Business English Club'), NOW() - INTERVAL '57 days'),

('Creative Writing',    'Écrire des textes créatifs variés',                          (SELECT id FROM clubs WHERE name = 'Creative Writing Workshop'), NOW() - INTERVAL '48 days'),
('Poetry',              'Composer des poèmes en anglais',                             (SELECT id FROM clubs WHERE name = 'Creative Writing Workshop'), NOW() - INTERVAL '48 days'),

('IPA Phonetics',       'Maîtriser l''alphabet phonétique international',             (SELECT id FROM clubs WHERE name = 'Pronunciation Masters'), NOW() - INTERVAL '43 days'),
('Accent Reduction',    'Réduire l''accent natif pour un anglais plus naturel',       (SELECT id FROM clubs WHERE name = 'Pronunciation Masters'), NOW() - INTERVAL '43 days'),

('Essay Writing',       'Rédiger des dissertations académiques structurées',          (SELECT id FROM clubs WHERE name = 'Academic English Society'), NOW() - INTERVAL '38 days'),
('Research Skills',     'Rechercher et citer des sources académiques',                (SELECT id FROM clubs WHERE name = 'Academic English Society'), NOW() - INTERVAL '38 days'),
('IELTS Preparation',   'Se préparer aux épreuves de l''examen IELTS',               (SELECT id FROM clubs WHERE name = 'Academic English Society'), NOW() - INTERVAL '37 days'),

('Note-Taking',         'Prendre des notes efficaces lors d''écoutes',                (SELECT id FROM clubs WHERE name = 'Listening & Media Club'), NOW() - INTERVAL '33 days'),
('Accent Recognition',  'Reconnaître différents accents anglophones',                 (SELECT id FROM clubs WHERE name = 'Listening & Media Club'), NOW() - INTERVAL '33 days');
