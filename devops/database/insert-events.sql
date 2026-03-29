-- =====================================================
-- Script d'insertion des événements de test
-- Base de données: englishflow_event_db
-- =====================================================
-- Note: Connectez-vous à la base englishflow_event_db avant d'exécuter ce script
-- Exemple: psql -U postgres -d englishflow_event_db -f insert-events.sql

-- Désactiver les contraintes de clés étrangères temporairement (PostgreSQL)
SET session_replication_role = 'replica';

-- =====================================================
-- EVENTS - Différents types (WORKSHOP, SEMINAR, SOCIAL)
-- =====================================================
-- Note: creator_id correspond aux IDs des utilisateurs
-- club_id correspond aux IDs des clubs (1-16 selon insert-clubs.sql)

INSERT INTO events (title, type, description, location, latitude, longitude, start_date, end_date, max_participants, current_participants, status, creator_id, club_id, club_name, image, created_at, updated_at)
VALUES 
-- WORKSHOPS APPROUVÉS
('Mastering English Pronunciation', 'WORKSHOP',
'Atelier intensif de 3 heures sur la prononciation anglaise. Travail sur les sons difficiles pour francophones, l''intonation et le rythme. Exercices pratiques avec enregistrements et feedback personnalisé. Matériel fourni.',
'45 Boulevard Zerktouni, Casablanca', 33.5731, -7.6298,
'2026-04-05 14:00:00', '2026-04-05 17:00:00',
20, 0, 'APPROVED', 6, 10, 'Accent Improvement Lab', NULL, NOW(), NOW()),

('Business Email Writing Workshop', 'WORKSHOP',
'Atelier pratique sur la rédaction d''emails professionnels en anglais. Structure, formules de politesse, ton approprié, gestion des situations délicates. Exercices basés sur des cas réels.',
'156 Boulevard Anfa, Casablanca', 33.5883, -7.6311,
'2026-04-08 10:00:00', '2026-04-08 13:00:00',
25, 0, 'APPROVED', 7, 5, 'Business English Professionals', NULL, NOW(), NOW()),

('Creative Writing Sprint', 'WORKSHOP',
'Session intensive d''écriture créative en anglais. Exercices d''écriture rapide, techniques narratives, développement de personnages. Partage et feedback en groupe. Tous niveaux bienvenus.',
'89 Rue Abdelmoumen, Casablanca', 33.5892, -7.6356,
'2026-04-12 15:00:00', '2026-04-12 18:00:00',
15, 0, 'APPROVED', 19, 7, 'Creative Writing Circle', NULL, NOW(), NOW()),

('Grammar Bootcamp: Tenses Mastery', 'WORKSHOP',
'Bootcamp intensif sur les temps anglais. Révision complète des 12 temps principaux avec exercices pratiques, quiz interactifs et astuces de mémorisation. Support de cours inclus.',
'78 Avenue Hassan II, Rabat', 34.0209, -6.8416,
'2026-04-15 09:00:00', '2026-04-15 13:00:00',
30, 0, 'APPROVED', 9, 8, 'Grammar Masters', NULL, NOW(), NOW()),

('Theater Improvisation Workshop', 'WORKSHOP',
'Atelier d''improvisation théâtrale en anglais. Jeux de rôle, scènes spontanées, travail sur l''expression corporelle et vocale. Ambiance ludique pour vaincre la timidité.',
'67 Rue Allal Ben Abdellah, Fès', 34.0331, -5.0003,
'2026-04-18 16:00:00', '2026-04-18 19:00:00',
12, 0, 'APPROVED', 14, 6, 'English Theater Workshop', NULL, NOW(), NOW()),

-- SEMINARS APPROUVÉS
('IELTS Preparation Strategies', 'SEMINAR',
'Séminaire complet sur la préparation à l''examen IELTS. Présentation des 4 sections, stratégies de réussite, gestion du temps, erreurs courantes à éviter. Session Q&A avec un examinateur certifié.',
'12 Rue Abdelmoumen, Casablanca', 33.5892, -7.6356,
'2026-04-10 14:00:00', '2026-04-10 17:00:00',
50, 0, 'APPROVED', 6, 11, 'Academic English Success', NULL, NOW(), NOW()),

('English for Job Interviews', 'SEMINAR',
'Séminaire sur les entretiens d''embauche en anglais. Questions fréquentes, techniques de réponse STAR, vocabulaire professionnel, langage corporel. Simulations d''entretiens.',
'156 Boulevard Anfa, Casablanca', 33.5883, -7.6311,
'2026-04-20 10:00:00', '2026-04-20 13:00:00',
40, 0, 'APPROVED', 7, 5, 'Business English Professionals', NULL, NOW(), NOW()),

('Understanding British vs American English', 'SEMINAR',
'Séminaire comparatif sur les différences entre anglais britannique et américain. Vocabulaire, prononciation, orthographe, expressions idiomatiques. Exemples audio et vidéo.',
'34 Avenue des FAR, Rabat', 34.0209, -6.8416,
'2026-04-22 15:00:00', '2026-04-22 18:00:00',
60, 0, 'APPROVED', 9, 1, 'English Coffee Chat', NULL, NOW(), NOW()),

('Literature Analysis: Shakespeare Today', 'SEMINAR',
'Séminaire sur l''actualité de Shakespeare. Analyse de thèmes universels, adaptations modernes, techniques d''analyse littéraire. Discussion interactive avec extraits de pièces.',
'89 Rue Abou Bakr Seddik, Casablanca', 33.5731, -7.6298,
'2026-04-25 14:00:00', '2026-04-25 17:30:00',
35, 0, 'APPROVED', 19, 3, 'Classic Literature Circle', NULL, NOW(), NOW()),

-- SOCIAL EVENTS APPROUVÉS
('English Coffee Morning', 'SOCIAL',
'Rencontre conviviale autour d''un café pour pratiquer l''anglais dans une ambiance décontractée. Discussions libres sur des sujets variés. Tous niveaux bienvenus. Café et pâtisseries offerts.',
'Café Maure, Jardin Majorelle, Marrakech', 31.6417, -8.0033,
'2026-04-06 10:00:00', '2026-04-06 12:00:00',
30, 0, 'APPROVED', 11, 1, 'English Coffee Chat', NULL, NOW(), NOW()),

('Movie Night: Classic Hollywood', 'SOCIAL',
'Soirée cinéma en anglais avec projection d''un classique hollywoodien (Casablanca, 1942). Discussion après le film sur les thèmes, le contexte historique et les expressions idiomatiques. Popcorn inclus!',
'Cinéma Rialto, Casablanca', 33.5731, -7.6298,
'2026-04-11 19:00:00', '2026-04-11 22:00:00',
40, 0, 'APPROVED', 18, 12, 'Podcast & Audio Club', NULL, NOW(), NOW()),

('Book Club Meetup: Agatha Christie', 'SOCIAL',
'Rencontre du club de lecture pour discuter de "Murder on the Orient Express". Discussion approfondie sur l''intrigue, les personnages et le style d''Agatha Christie. Thé anglais servi.',
'Librairie Carrefour des Livres, Rabat', 34.0209, -6.8416,
'2026-04-13 16:00:00', '2026-04-13 18:30:00',
25, 0, 'APPROVED', 19, 3, 'Classic Literature Circle', NULL, NOW(), NOW()),

('English Pub Quiz Night', 'SOCIAL',
'Soirée quiz en anglais sur la culture anglophone. Questions sur l''histoire, la géographie, le cinéma, la musique et la littérature. Équipes de 4-6 personnes. Prix pour les gagnants!',
'Café Restaurant Le Dhow, Casablanca', 33.5731, -7.6298,
'2026-04-17 20:00:00', '2026-04-17 23:00:00',
50, 0, 'APPROVED', 15, 2, 'Debate & Discussion Club', NULL, NOW(), NOW()),

('Picnic & Conversation Practice', 'SOCIAL',
'Pique-nique en plein air avec jeux et conversations en anglais. Activités ludiques: charades, storytelling, word games. Ambiance familiale et détendue. Apportez votre panier!',
'Parc de la Ligue Arabe, Casablanca', 33.5731, -7.6298,
'2026-04-19 11:00:00', '2026-04-19 15:00:00',
40, 0, 'APPROVED', 21, 13, 'Fluency Builders', NULL, NOW(), NOW()),

('International Food & Language Exchange', 'SOCIAL',
'Soirée d''échange linguistique et culinaire. Chacun apporte un plat de son pays et partage en anglais. Découverte culturelle et pratique linguistique dans une ambiance festive.',
'Centre Culturel, Marrakech', 31.6295, -7.9811,
'2026-04-24 18:00:00', '2026-04-24 21:00:00',
35, 0, 'APPROVED', 22, 1, 'English Coffee Chat', NULL, NOW(), NOW()),

-- EVENTS EN ATTENTE D'APPROBATION
('Advanced Debate Workshop', 'WORKSHOP',
'Atelier de débat avancé en anglais. Techniques d''argumentation, rhétorique, gestion des contre-arguments. Débats sur des sujets d''actualité avec feedback détaillé.',
'23 Rue Mohammed V, Rabat', 34.0209, -6.8416,
'2026-04-28 14:00:00', '2026-04-28 17:00:00',
20, 0, 'PENDING', 15, 2, 'Debate & Discussion Club', NULL, NOW(), NOW()),

('Vocabulary Building Techniques', 'SEMINAR',
'Séminaire sur les méthodes efficaces d''apprentissage du vocabulaire. Mnémotechniques, cartes mentales, applications recommandées, techniques de mémorisation espacée.',
'45 Boulevard Zerktouni, Casablanca', 33.5731, -7.6298,
'2026-04-30 10:00:00', '2026-04-30 12:30:00',
45, 0, 'PENDING', 16, 9, 'Word Power Club', NULL, NOW(), NOW()),

('Karaoke Night in English', 'SOCIAL',
'Soirée karaoké en anglais! Chantez vos chansons préférées, travaillez votre prononciation en s''amusant. Ambiance conviviale, tous niveaux acceptés. Liste de chansons variées disponible.',
'Café Culturel, Tanger', 35.7595, -5.8340,
'2026-05-02 20:00:00', '2026-05-02 23:00:00',
30, 0, 'PENDING', 12, 10, 'Accent Improvement Lab', NULL, NOW(), NOW());

-- Réactiver les contraintes de clés étrangères (PostgreSQL)
SET session_replication_role = 'origin';

-- =====================================================
-- Résumé des insertions
-- =====================================================
-- Total: 19 événements
-- - 16 événements APPROVED (actifs)
-- - 3 événements PENDING (en attente d'approbation)
-- 
-- Types d'événements:
-- - WORKSHOP: 6 événements (5 approuvés, 1 en attente)
-- - SEMINAR: 5 événements (4 approuvés, 1 en attente)
-- - SOCIAL: 8 événements (7 approuvés, 1 en attente)
-- 
-- Dates: Avril-Mai 2026
-- Lieux: Casablanca, Rabat, Marrakech, Fès, Tanger
-- =====================================================

SELECT 'Événements insérés avec succès!' AS message;
SELECT status, COUNT(*) as count FROM events GROUP BY status;
SELECT type, COUNT(*) as count FROM events GROUP BY type ORDER BY type;
