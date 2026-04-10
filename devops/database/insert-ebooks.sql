-- =====================================================
-- Script d'insertion des ebooks de test
-- Base de données: englishflow_learning_db
-- =====================================================
-- Note: Connectez-vous à la base englishflow_learning_db avant d'exécuter ce script
-- Exemple: psql -U postgres -d englishflow_learning_db -f insert-ebooks.sql

-- Désactiver les contraintes de clés étrangères temporairement (PostgreSQL)
SET session_replication_role = 'replica';

-- =====================================================
-- EBOOKS - Différents niveaux et catégories
-- =====================================================
-- Note: created_by correspond aux IDs des tuteurs (IDs 6-10 selon insert-users.sql)

-- ===== NIVEAU A1 - DÉBUTANT =====

INSERT INTO ebook (title, description, file_url, file_size, mime_type, cover_image_url, thumbnail_url, level, category, is_free, price, pricing_model, download_count, view_count, average_rating, review_count, status, published_at, created_by, created_at, updated_at)
VALUES 
('English Grammar in Use - Elementary', 
'Le guide de référence pour les débutants en grammaire anglaise. Ce livre couvre tous les points essentiels de la grammaire avec des explications claires, des exemples pratiques et des exercices corrigés. Parfait pour les apprenants de niveau A1-A2.',
'https://www.englishgrammar.org/wp-content/uploads/2014/09/Basic-English-Grammar-Book-1.pdf',
5242880, 'application/pdf',
'/uploads/ebooks/covers/grammar-elementary.jpg',
'/uploads/ebooks/thumbnails/grammar-elementary-thumb.jpg',
'A1', 'GRAMMAR',
true, 0.00, 'FREE',
245, 1250, 4.7, 89,
'PUBLISHED', NOW() - INTERVAL '30 days',
6, NOW() - INTERVAL '30 days', NOW()),

('Essential English Vocabulary A1', 
'Vocabulaire essentiel pour débutants avec plus de 1000 mots et expressions de base. Organisé par thèmes (famille, nourriture, voyages, etc.) avec des illustrations et des exercices pratiques. Inclut des fichiers audio pour la prononciation.',
'https://www.englishclub.com/ref/esl/Vocabulary/Topics/',
3145728, 'application/pdf',
'/uploads/ebooks/covers/vocab-a1.jpg',
'/uploads/ebooks/thumbnails/vocab-a1-thumb.jpg',
'A1', 'VOCABULARY',
true, 0.00, 'FREE',
312, 1580, 4.6, 102,
'PUBLISHED', NOW() - INTERVAL '25 days',
7, NOW() - INTERVAL '25 days', NOW()),

('Everyday English Conversations for Beginners', 
'Guide pratique de conversations quotidiennes en anglais. 50 dialogues authentiques avec traductions, vocabulaire clé et exercices de compréhension. Idéal pour développer la confiance à l''oral dès le niveau débutant.',
'https://www.really-learn-english.com/support-files/everyday-english-conversations.pdf',
2621440, 'application/pdf',
'/uploads/ebooks/covers/conversations-a1.jpg',
'/uploads/ebooks/thumbnails/conversations-a1-thumb.jpg',
'A1', 'GENERAL',
true, 0.00, 'FREE',
198, 890, 4.5, 67,
'PUBLISHED', NOW() - INTERVAL '20 days',
8, NOW() - INTERVAL '20 days', NOW());

-- ===== NIVEAU A2 - ÉLÉMENTAIRE =====

INSERT INTO ebook (title, description, file_url, file_size, mime_type, cover_image_url, thumbnail_url, level, category, is_free, price, pricing_model, download_count, view_count, average_rating, review_count, status, published_at, created_by, created_at, updated_at)
VALUES 
('English Grammar Step by Step - A2', 
'Progression méthodique dans la grammaire anglaise pour niveau élémentaire. Couvre les temps du passé, le présent perfect, les modaux et les conditionnels. Chaque chapitre inclut des explications détaillées et des exercices progressifs.',
'https://www.perfect-english-grammar.com/support-files/english_grammar_a2.pdf',
4194304, 'application/pdf',
'/uploads/ebooks/covers/grammar-a2.jpg',
'/uploads/ebooks/thumbnails/grammar-a2-thumb.jpg',
'A2', 'GRAMMAR',
true, 0.00, 'FREE',
287, 1420, 4.8, 95,
'PUBLISHED', NOW() - INTERVAL '28 days',
6, NOW() - INTERVAL '28 days', NOW()),

('Phrasal Verbs Made Easy', 
'Maîtrisez les phrasal verbs essentiels de l''anglais. Plus de 200 phrasal verbs courants expliqués avec des exemples contextuels, des illustrations et des exercices pratiques. Indispensable pour progresser en anglais.',
'https://www.englishpage.com/prepositions/phrasaldictionary.pdf',
3670016, 'application/pdf',
'/uploads/ebooks/covers/phrasal-verbs.jpg',
'/uploads/ebooks/thumbnails/phrasal-verbs-thumb.jpg',
'A2', 'VOCABULARY',
true, 0.00, 'FREE',
356, 1680, 4.7, 118,
'PUBLISHED', NOW() - INTERVAL '22 days',
7, NOW() - INTERVAL '22 days', NOW()),

('Short Stories for English Learners A2', 
'Collection de 20 histoires courtes adaptées au niveau A2. Chaque histoire est suivie de questions de compréhension, vocabulaire clé et activités. Parfait pour améliorer la lecture et enrichir son vocabulaire de manière ludique.',
'https://www.englishe-books.com/short-stories-a2.pdf',
5767168, 'application/pdf',
'/uploads/ebooks/covers/stories-a2.jpg',
'/uploads/ebooks/thumbnails/stories-a2-thumb.jpg',
'A2', 'GENERAL',
true, 0.00, 'FREE',
423, 2100, 4.9, 145,
'PUBLISHED', NOW() - INTERVAL '18 days',
9, NOW() - INTERVAL '18 days', NOW());

-- ===== NIVEAU B1 - INTERMÉDIAIRE =====

INSERT INTO ebook (title, description, file_url, file_size, mime_type, cover_image_url, thumbnail_url, level, category, is_free, price, pricing_model, download_count, view_count, average_rating, review_count, status, published_at, created_by, created_at, updated_at)
VALUES 
('Intermediate English Grammar in Context', 
'Grammaire anglaise intermédiaire avec approche contextuelle. Couvre les structures complexes, le discours indirect, les temps narratifs et les nuances grammaticales. Exercices basés sur des textes authentiques.',
'https://www.cambridge.org/files/3514/6173/1619/intermediate-grammar.pdf',
6291456, 'application/pdf',
'/uploads/ebooks/covers/grammar-b1.jpg',
'/uploads/ebooks/thumbnails/grammar-b1-thumb.jpg',
'B1', 'GRAMMAR',
true, 0.00, 'FREE',
512, 2450, 4.8, 167,
'PUBLISHED', NOW() - INTERVAL '35 days',
6, NOW() - INTERVAL '35 days', NOW()),

('Business English Essentials', 
'Anglais des affaires pour professionnels. Vocabulaire commercial, rédaction d''emails, présentations, négociations et réunions. Inclut des modèles de documents et des études de cas réels du monde professionnel.',
'https://www.businessenglishpod.com/wp-content/uploads/2019/03/Business-English-Essentials.pdf',
7340032, 'application/pdf',
'/uploads/ebooks/covers/business-english.jpg',
'/uploads/ebooks/thumbnails/business-english-thumb.jpg',
'B1', 'BUSINESS',
false, 19.99, 'PREMIUM',
189, 980, 4.9, 78,
'PUBLISHED', NOW() - INTERVAL '15 days',
7, NOW() - INTERVAL '15 days', NOW()),

('Academic Writing Skills B1-B2', 
'Guide complet de rédaction académique en anglais. Structure d''essais, argumentation, citations, paraphrases et style académique. Exemples d''essais annotés et exercices de rédaction progressive.',
'https://www.uefap.com/writing/writfram.htm',
5505024, 'application/pdf',
'/uploads/ebooks/covers/academic-writing.jpg',
'/uploads/ebooks/thumbnails/academic-writing-thumb.jpg',
'B1', 'GENERAL',
true, 0.00, 'FREE',
298, 1560, 4.6, 92,
'PUBLISHED', NOW() - INTERVAL '12 days',
8, NOW() - INTERVAL '12 days', NOW()),

('Idioms and Expressions Intermediate', 
'Plus de 500 expressions idiomatiques anglaises expliquées. Origine, signification, usage et exemples en contexte. Organisé par thèmes avec exercices de mise en pratique et quiz interactifs.',
'https://www.englishclub.com/ref/Idioms/',
4718592, 'application/pdf',
'/uploads/ebooks/covers/idioms-b1.jpg',
'/uploads/ebooks/thumbnails/idioms-b1-thumb.jpg',
'B1', 'VOCABULARY',
true, 0.00, 'FREE',
445, 2200, 4.7, 134,
'PUBLISHED', NOW() - INTERVAL '10 days',
9, NOW() - INTERVAL '10 days', NOW());

-- ===== NIVEAU B2 - INTERMÉDIAIRE SUPÉRIEUR =====

INSERT INTO ebook (title, description, file_url, file_size, mime_type, cover_image_url, thumbnail_url, level, category, is_free, price, pricing_model, download_count, view_count, average_rating, review_count, status, published_at, created_by, created_at, updated_at)
VALUES 
('Advanced Grammar in Use', 
'Grammaire anglaise avancée pour niveau B2-C1. Structures sophistiquées, nuances grammaticales, style formel vs informel. 100 unités avec explications détaillées et exercices corrigés. Référence indispensable.',
'https://www.cambridge.org/files/8914/6173/1620/advanced-grammar.pdf',
8388608, 'application/pdf',
'/uploads/ebooks/covers/grammar-b2.jpg',
'/uploads/ebooks/thumbnails/grammar-b2-thumb.jpg',
'B2', 'GRAMMAR',
false, 24.99, 'PREMIUM',
234, 1340, 4.9, 98,
'PUBLISHED', NOW() - INTERVAL '40 days',
6, NOW() - INTERVAL '40 days', NOW()),

('IELTS Preparation Complete Guide', 
'Préparation complète à l''examen IELTS. Stratégies pour les 4 sections (Listening, Reading, Writing, Speaking), tests blancs complets, conseils d''examinateurs et techniques de gestion du temps. Inclut 6 tests pratiques.',
'https://www.ielts.org/for-test-takers/sample-test-questions',
12582912, 'application/pdf',
'/uploads/ebooks/covers/ielts-prep.jpg',
'/uploads/ebooks/thumbnails/ielts-prep-thumb.jpg',
'B2', 'EXAM_PREP',
false, 29.99, 'PREMIUM',
567, 3200, 4.8, 189,
'PUBLISHED', NOW() - INTERVAL '45 days',
7, NOW() - INTERVAL '45 days', NOW()),

('Professional Email Writing Masterclass', 
'Maîtrisez la rédaction d''emails professionnels en anglais. Formules de politesse, structure, ton approprié, gestion des situations délicates. Plus de 100 modèles d''emails pour toutes situations professionnelles.',
'https://www.businesswritingblog.com/email-writing-guide.pdf',
3932160, 'application/pdf',
'/uploads/ebooks/covers/email-writing.jpg',
'/uploads/ebooks/thumbnails/email-writing-thumb.jpg',
'B2', 'BUSINESS',
true, 0.00, 'FREE',
389, 1890, 4.7, 112,
'PUBLISHED', NOW() - INTERVAL '8 days',
8, NOW() - INTERVAL '8 days', NOW()),

('English Literature Classics Simplified', 
'Introduction à la littérature anglaise classique. Résumés et analyses de 20 œuvres majeures (Shakespeare, Dickens, Austen, etc.) avec vocabulaire clé et contexte historique. Parfait pour découvrir les classiques.',
'https://www.gutenberg.org/files/literature-guide.pdf',
9437184, 'application/pdf',
'/uploads/ebooks/covers/literature-classics.jpg',
'/uploads/ebooks/thumbnails/literature-classics-thumb.jpg',
'B2', 'GENERAL',
true, 0.00, 'FREE',
276, 1450, 4.6, 87,
'PUBLISHED', NOW() - INTERVAL '6 days',
9, NOW() - INTERVAL '6 days', NOW());

-- ===== NIVEAU C1 - AVANCÉ =====

INSERT INTO ebook (title, description, file_url, file_size, mime_type, cover_image_url, thumbnail_url, level, category, is_free, price, pricing_model, download_count, view_count, average_rating, review_count, status, published_at, created_by, created_at, updated_at)
VALUES 
('Mastering English Collocations', 
'Guide complet des collocations anglaises pour niveau avancé. Plus de 2000 collocations courantes classées par catégories avec exemples authentiques. Essentiel pour atteindre un niveau de langue naturel et fluide.',
'https://www.onestopenglish.com/collocations-advanced.pdf',
6815744, 'application/pdf',
'/uploads/ebooks/covers/collocations-c1.jpg',
'/uploads/ebooks/thumbnails/collocations-c1-thumb.jpg',
'C1', 'VOCABULARY',
false, 22.99, 'PREMIUM',
198, 1120, 4.9, 76,
'PUBLISHED', NOW() - INTERVAL '50 days',
6, NOW() - INTERVAL '50 days', NOW()),

('Cambridge C1 Advanced Exam Preparation', 
'Préparation intensive au Cambridge C1 Advanced (CAE). Stratégies d''examen, techniques de réponse, 8 tests complets avec corrections détaillées. Conseils d''examinateurs Cambridge pour maximiser votre score.',
'https://www.cambridgeenglish.org/exams-and-tests/advanced/preparation/',
15728640, 'application/pdf',
'/uploads/ebooks/covers/cambridge-c1.jpg',
'/uploads/ebooks/thumbnails/cambridge-c1-thumb.jpg',
'C1', 'EXAM_PREP',
false, 34.99, 'PREMIUM',
423, 2340, 4.8, 145,
'PUBLISHED', NOW() - INTERVAL '55 days',
7, NOW() - INTERVAL '55 days', NOW()),

('Advanced Business Negotiations', 
'Techniques avancées de négociation en anglais des affaires. Stratégies de persuasion, gestion des conflits, langage diplomatique, négociations interculturelles. Études de cas réels et simulations.',
'https://www.negotiationskills.com/advanced-business-english.pdf',
5242880, 'application/pdf',
'/uploads/ebooks/covers/negotiations.jpg',
'/uploads/ebooks/thumbnails/negotiations-thumb.jpg',
'C1', 'BUSINESS',
false, 27.99, 'PREMIUM',
167, 890, 4.7, 54,
'PUBLISHED', NOW() - INTERVAL '5 days',
8, NOW() - INTERVAL '5 days', NOW());

-- ===== EBOOKS EN ATTENTE D'APPROBATION =====

INSERT INTO ebook (title, description, file_url, file_size, mime_type, cover_image_url, thumbnail_url, level, category, is_free, price, pricing_model, download_count, view_count, average_rating, review_count, status, created_by, created_at, updated_at)
VALUES 
('English Pronunciation Workshop', 
'Atelier complet de prononciation anglaise. Phonétique, intonation, rythme et accentuation. Exercices audio, diagrammes articulatoires et techniques de correction. Idéal pour réduire son accent et améliorer sa clarté.',
'https://www.pronunciationworkshop.com/guide.pdf',
4194304, 'application/pdf',
'/uploads/ebooks/covers/pronunciation.jpg',
'/uploads/ebooks/thumbnails/pronunciation-thumb.jpg',
'B1', 'GENERAL',
true, 0.00, 'FREE',
0, 45, 0.0, 0,
'PENDING',
9, NOW() - INTERVAL '2 days', NOW()),

('TOEFL iBT Complete Strategy Guide', 
'Guide stratégique complet pour le TOEFL iBT. Techniques pour chaque section, gestion du temps, 4 tests complets, vocabulaire académique essentiel. Préparation intensive pour obtenir un score élevé.',
'https://www.ets.org/toefl/test-takers/ibt/prepare/',
11534336, 'application/pdf',
'/uploads/ebooks/covers/toefl-prep.jpg',
'/uploads/ebooks/thumbnails/toefl-prep-thumb.jpg',
'B2', 'EXAM_PREP',
false, 32.99, 'PREMIUM',
0, 78, 0.0, 0,
'PENDING',
7, NOW() - INTERVAL '1 day', NOW());

-- ===== EBOOKS PROGRAMMÉS =====

INSERT INTO ebook (title, description, file_url, file_size, mime_type, cover_image_url, thumbnail_url, level, category, is_free, price, pricing_model, download_count, view_count, average_rating, review_count, status, scheduled_for, created_by, created_at, updated_at)
VALUES 
('English for Medical Professionals', 
'Anglais médical pour professionnels de santé. Terminologie médicale, communication patient-médecin, rédaction de rapports médicaux, présentations de cas cliniques. Vocabulaire spécialisé par spécialité.',
'https://www.medical-english.com/professional-guide.pdf',
7864320, 'application/pdf',
'/uploads/ebooks/covers/medical-english.jpg',
'/uploads/ebooks/thumbnails/medical-english-thumb.jpg',
'B2', 'BUSINESS',
false, 26.99, 'PREMIUM',
0, 0, 0.0, 0,
'SCHEDULED', NOW() + INTERVAL '7 days',
8, NOW(), NOW()),

('Creative Writing in English', 
'Guide de l''écriture créative en anglais. Techniques narratives, développement de personnages, dialogues, descriptions, styles littéraires. Exercices d''écriture et analyse d''extraits d''auteurs célèbres.',
'https://www.creativewriting.org/english-guide.pdf',
6291456, 'application/pdf',
'/uploads/ebooks/covers/creative-writing.jpg',
'/uploads/ebooks/thumbnails/creative-writing-thumb.jpg',
'C1', 'GENERAL',
true, 0.00, 'FREE',
0, 0, 0.0, 0,
'SCHEDULED', NOW() + INTERVAL '14 days',
9, NOW(), NOW());

-- Réactiver les contraintes de clés étrangères (PostgreSQL)
SET session_replication_role = 'origin';

-- =====================================================
-- Résumé des insertions
-- =====================================================
-- Total: 22 ebooks
-- - 18 ebooks PUBLISHED (actifs)
-- - 2 ebooks PENDING (en attente d'approbation)
-- - 2 ebooks SCHEDULED (programmés)
-- 
-- Répartition par niveau:
-- - A1: 3 ebooks
-- - A2: 3 ebooks
-- - B1: 4 ebooks
-- - B2: 4 ebooks
-- - C1: 3 ebooks
-- - Pending/Scheduled: 3 ebooks
--
-- Répartition par catégorie:
-- - GRAMMAR: 4 ebooks
-- - VOCABULARY: 4 ebooks
-- - BUSINESS: 5 ebooks
-- - EXAM_PREP: 3 ebooks
-- - GENERAL: 6 ebooks
--
-- Modèles de prix:
-- - FREE: 14 ebooks
-- - PREMIUM: 8 ebooks
-- =====================================================

SELECT 'Ebooks insérés avec succès!' AS message;
SELECT status, COUNT(*) as count FROM ebook GROUP BY status;
SELECT level, COUNT(*) as count FROM ebook WHERE status = 'PUBLISHED' GROUP BY level ORDER BY level;
SELECT category, COUNT(*) as count FROM ebook WHERE status = 'PUBLISHED' GROUP BY category ORDER BY category;
