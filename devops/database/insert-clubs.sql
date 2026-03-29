-- =====================================================
-- Script d'insertion des clubs de test
-- Base de données: englishflow_jungle_club_db
-- =====================================================
-- Note: Connectez-vous à la base englishflow_jungle_club_db avant d'exécuter ce script
-- Exemple: psql -U postgres -d englishflow_jungle_club_db -f insert-clubs.sql

-- Désactiver les contraintes de clés étrangères temporairement (PostgreSQL)
SET session_replication_role = 'replica';

-- =====================================================
-- CLUBS - Différentes catégories
-- =====================================================
-- Note: created_by correspond aux IDs des utilisateurs de la table users
-- Tutors: IDs 6-10, Students: IDs 11-26 (selon insert-users.sql)

INSERT INTO clubs (name, category, description, objective, max_members, status, created_by, created_at, updated_at, image, review_comment, reviewed_by, suspended_at, suspended_by, suspension_reason)
VALUES 
-- CONVERSATION CLUBS
('English Coffee Chat', 'CONVERSATION', 
'Un club convivial pour pratiquer l''anglais autour d''un café virtuel. Discussions informelles sur des sujets variés du quotidien, actualités, culture et expériences personnelles. Ambiance détendue et bienveillante pour tous les niveaux.',
'Améliorer la fluidité et la confiance en conversation anglaise dans un cadre décontracté. Développer l''écoute active et enrichir son vocabulaire quotidien.',
25, 'APPROVED', 11, NOW(), NOW(), NULL, 'Excellent concept de club, très engageant', 3, NULL, NULL, NULL),

('Debate & Discussion Club', 'CONVERSATION',
'Club de débats structurés en anglais sur des sujets d''actualité, société, technologie et culture. Format organisé avec modérateur, arguments préparés et temps de parole équitable. Niveau intermédiaire à avancé recommandé.',
'Développer l''argumentation en anglais, apprendre à défendre ses idées avec clarté et respecter les opinions divergentes. Perfectionner l''expression orale formelle.',
20, 'APPROVED', 15, NOW(), NOW(), NULL, 'Club bien structuré avec des débats de qualité', 3, NULL, NULL, NULL),

-- BOOK CLUBS
('Classic Literature Circle', 'BOOK',
'Club de lecture dédié aux classiques de la littérature anglaise et américaine. Nous lisons un livre par mois (Shakespeare, Dickens, Austen, Hemingway...) et organisons des discussions approfondies sur les thèmes, personnages et contexte historique.',
'Découvrir les grands classiques de la littérature anglophone, enrichir son vocabulaire littéraire et développer l''analyse critique en anglais.',
30, 'APPROVED', 19, NOW(), NOW(), NULL, 'Excellente initiative pour les amoureux de littérature', 3, NULL, NULL, NULL),

('Modern Fiction Readers', 'BOOK',
'Club de lecture contemporaine explorant les best-sellers et romans modernes en anglais. Romans policiers, science-fiction, romance, thrillers... Discussions détendues et partage de recommandations entre passionnés.',
'Maintenir une pratique régulière de lecture en anglais, découvrir de nouveaux auteurs et partager le plaisir de lire dans un groupe dynamique.',
25, 'APPROVED', 22, NOW(), NOW(), NULL, 'Club actif avec de bonnes discussions', 3, NULL, NULL, NULL),

-- BUSINESS ENGLISH
('Business English Professionals', 'BUSINESS',
'Club pour professionnels souhaitant perfectionner leur anglais des affaires. Simulations de réunions, présentations, négociations, emails professionnels et networking. Vocabulaire spécialisé et situations réelles du monde du travail.',
'Maîtriser l''anglais professionnel pour réussir dans un environnement international. Gagner en confiance pour les présentations et communications d''entreprise.',
20, 'APPROVED', 17, NOW(), NOW(), NULL, 'Très utile pour les professionnels', 3, NULL, NULL, NULL),

-- DRAMA CLUB
('English Theater Workshop', 'DRAMA',
'Atelier de théâtre en anglais pour tous niveaux. Improvisation, lecture de pièces, jeux de rôle et mise en scène de courtes scènes. Travail sur la prononciation, l''intonation et l''expression corporelle à travers le jeu théâtral.',
'Améliorer la prononciation et l''aisance orale par le théâtre. Développer la créativité et vaincre la timidité en s''exprimant en anglais.',
15, 'APPROVED', 14, NOW(), NOW(), NULL, 'Approche créative et efficace', 3, NULL, NULL, NULL),

-- WRITING CLUB
('Creative Writing Circle', 'WRITING',
'Club d''écriture créative en anglais. Nouvelles, poésie, essais personnels... Partage de textes, feedback constructif et exercices d''écriture hebdomadaires. Tous styles et niveaux bienvenus dans une atmosphère encourageante.',
'Développer ses compétences en écriture anglaise, recevoir des retours bienveillants et progresser dans l''expression écrite créative.',
20, 'APPROVED', 19, NOW(), NOW(), NULL, 'Excellent pour développer l''écriture', 3, NULL, NULL, NULL),

-- GRAMMAR CLUB
('Grammar Masters', 'GRAMMAR',
'Club focalisé sur la maîtrise de la grammaire anglaise. Révision systématique des temps, structures complexes, conditionnels, voix passive... Exercices pratiques, quiz et explications claires avec exemples concrets.',
'Consolider les bases grammaticales et maîtriser les structures avancées. Comprendre la logique de la grammaire anglaise pour mieux l''appliquer.',
25, 'APPROVED', 13, NOW(), NOW(), NULL, 'Très structuré et pédagogique', 3, NULL, NULL, NULL),

-- VOCABULARY CLUB
('Word Power Club', 'VOCABULARY',
'Club dédié à l''enrichissement du vocabulaire anglais. Thèmes hebdomadaires (émotions, nature, technologie...), idiomes, phrasal verbs, expressions courantes. Techniques de mémorisation et utilisation en contexte.',
'Élargir significativement son vocabulaire anglais et apprendre à utiliser les mots nouveaux dans des phrases naturelles.',
30, 'APPROVED', 16, NOW(), NOW(), NULL, 'Approche systématique du vocabulaire', 3, NULL, NULL, NULL),

-- PRONUNCIATION CLUB
('Accent Improvement Lab', 'PRONUNCIATION',
'Atelier de prononciation et réduction d''accent. Travail sur les sons difficiles, l''intonation, le rythme et l''accentuation. Exercices de phonétique, enregistrements et feedback personnalisé pour une prononciation claire.',
'Améliorer la clarté de sa prononciation anglaise et gagner en confiance à l''oral. Comprendre les subtilités phonétiques de l''anglais.',
15, 'APPROVED', 12, NOW(), NOW(), NULL, 'Très utile pour la prononciation', 3, NULL, NULL, NULL),

-- ACADEMIC ENGLISH
('Academic English Success', 'ACADEMIC',
'Club pour étudiants préparant des études supérieures en anglais. Rédaction académique, essais, résumés, présentations universitaires, vocabulaire spécialisé. Préparation IELTS Academic et TOEFL.',
'Maîtriser l''anglais académique pour réussir ses études supérieures. Développer les compétences en rédaction et présentation universitaire.',
20, 'APPROVED', 20, NOW(), NOW(), NULL, 'Essentiel pour les étudiants', 3, NULL, NULL, NULL),

-- LISTENING CLUB
('Podcast & Audio Club', 'LISTENING',
'Club de compréhension orale à travers podcasts, séries, films et documentaires en anglais. Écoute active, discussions sur le contenu et exercices de compréhension. Tous genres: actualités, culture, science, divertissement.',
'Améliorer la compréhension orale en anglais authentique. S''habituer aux différents accents et débits de parole.',
25, 'APPROVED', 18, NOW(), NOW(), NULL, 'Excellente ressource pour l''écoute', 3, NULL, NULL, NULL),

-- SPEAKING CLUB
('Fluency Builders', 'SPEAKING',
'Club intensif de pratique orale. Sessions de conversation guidée, storytelling, descriptions, opinions... Focus sur la fluidité plutôt que la perfection. Environnement sûr pour pratiquer sans jugement.',
'Gagner en fluidité et spontanéité à l''oral. Réduire les hésitations et développer des automatismes en conversation.',
20, 'APPROVED', 21, NOW(), NOW(), NULL, 'Très efficace pour la fluidité', 3, NULL, NULL, NULL),

-- READING CLUB
('Speed Reading Challenge', 'READING',
'Club de lecture rapide et compréhension écrite. Articles, nouvelles, extraits de romans... Techniques de lecture efficace, enrichissement du vocabulaire et discussions sur les textes. Progression adaptée au niveau.',
'Améliorer la vitesse et la compréhension de lecture en anglais. Développer des stratégies de lecture efficaces.',
30, 'APPROVED', 23, NOW(), NOW(), NULL, 'Bon pour développer la lecture', 3, NULL, NULL, NULL);

-- =====================================================
-- CLUBS EN ATTENTE D'APPROBATION
-- =====================================================
INSERT INTO clubs (name, category, description, objective, max_members, status, created_by, created_at, updated_at, image, review_comment, reviewed_by, suspended_at, suspended_by, suspension_reason)
VALUES 
('Movie Night Club', 'LISTENING',
'Club de cinéma en anglais. Visionnage de films classiques et récents suivis de discussions. Analyse des dialogues, expressions idiomatiques et références culturelles. Une soirée cinéma par semaine.',
'Améliorer la compréhension orale tout en découvrant la culture anglophone à travers le cinéma.',
20, 'PENDING', 24, NOW(), NOW(), NULL, NULL, NULL, NULL, NULL, NULL),

('Travel English Prep', 'CONVERSATION',
'Club pour préparer ses voyages en pays anglophones. Vocabulaire du voyage, situations pratiques (aéroport, hôtel, restaurant, urgences), jeux de rôle et conseils culturels.',
'Être autonome en anglais lors de voyages et gérer toutes les situations courantes avec confiance.',
25, 'PENDING', 25, NOW(), NOW(), NULL, NULL, NULL, NULL, NULL, NULL);

-- Réactiver les contraintes de clés étrangères (PostgreSQL)
SET session_replication_role = 'origin';

-- =====================================================
-- Résumé des insertions
-- =====================================================
-- Total: 16 clubs
-- - 14 clubs APPROVED (actifs)
-- - 2 clubs PENDING (en attente d'approbation)
-- 
-- Catégories couvertes:
-- - CONVERSATION (3 clubs)
-- - BOOK (2 clubs)
-- - BUSINESS (1 club)
-- - DRAMA (1 club)
-- - WRITING (1 club)
-- - GRAMMAR (1 club)
-- - VOCABULARY (1 club)
-- - PRONUNCIATION (1 club)
-- - ACADEMIC (1 club)
-- - LISTENING (2 clubs)
-- - SPEAKING (1 club)
-- - READING (1 club)
-- =====================================================

SELECT 'Clubs insérés avec succès!' AS message;
SELECT status, COUNT(*) as count FROM clubs GROUP BY status;
SELECT category, COUNT(*) as count FROM clubs GROUP BY category ORDER BY category;
