--
-- PostgreSQL database dump
--

\restrict dSQeB9Stb5MRw1z19aqE7L8WMaNkL98xrb4VKnk9CHReUFViH4dPWyPt9c07xhh

-- Dumped from database version 16.12
-- Dumped by pg_dump version 16.12

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: ebook; Type: TABLE DATA; Schema: public; Owner: postgres
--

SET SESSION AUTHORIZATION DEFAULT;

ALTER TABLE public.ebook DISABLE TRIGGER ALL;

INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (1, 5, 'GENERAL', 'ba86ae9f-f553-457e-a086-310a38b687e3_cat-cute-cat.gif', '2026-04-14 17:24:44.279172', 1, 'AAAAA

__METADATA__
{"chapters":[],"totalPages":4,"estimatedReadTime":9,"pricing":"free","price":0,"release":"immediate","scheduledDate":"","audience":[],"accessCode":"","hasCoverImage":true}', 0, 220225, '3c3ad060-ef76-4e3c-8eb5-6f0cdb0d5422_Grille_App Web Distribuées_4eme(1).pdf', true, 'A1', 'application/pdf', NULL, 'FREE', '2026-04-14 17:25:01.500482', 1, NULL, 'PUBLISHED', NULL, 'Livre 1 ', '2026-04-14 17:25:20.668441', 0);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (2, 4.7, 'GRAMMAR', '/uploads/ebooks/covers/grammar-elementary.jpg', '2026-03-18 14:03:40.445777', 6, 'Le guide de référence pour les débutants en grammaire anglaise. Ce livre couvre tous les points essentiels de la grammaire avec des explications claires, des exemples pratiques et des exercices corrigés. Parfait pour les apprenants de niveau A1-A2.', 245, 5242880, 'https://www.englishgrammar.org/wp-content/uploads/2014/09/Basic-English-Grammar-Book-1.pdf', true, 'A1', 'application/pdf', 0.00, 'FREE', '2026-03-18 14:03:40.445777', 89, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/grammar-elementary-thumb.jpg', 'English Grammar in Use - Elementary', '2026-04-17 14:03:40.445777', 1250);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (3, 4.6, 'VOCABULARY', '/uploads/ebooks/covers/vocab-a1.jpg', '2026-03-23 14:03:40.445777', 7, 'Vocabulaire essentiel pour débutants avec plus de 1000 mots et expressions de base. Organisé par thèmes (famille, nourriture, voyages, etc.) avec des illustrations et des exercices pratiques. Inclut des fichiers audio pour la prononciation.', 312, 3145728, 'https://www.englishclub.com/ref/esl/Vocabulary/Topics/', true, 'A1', 'application/pdf', 0.00, 'FREE', '2026-03-23 14:03:40.445777', 102, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/vocab-a1-thumb.jpg', 'Essential English Vocabulary A1', '2026-04-17 14:03:40.445777', 1580);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (4, 4.5, 'GENERAL', '/uploads/ebooks/covers/conversations-a1.jpg', '2026-03-28 14:03:40.445777', 8, 'Guide pratique de conversations quotidiennes en anglais. 50 dialogues authentiques avec traductions, vocabulaire clé et exercices de compréhension. Idéal pour développer la confiance à l''oral dès le niveau débutant.', 198, 2621440, 'https://www.really-learn-english.com/support-files/everyday-english-conversations.pdf', true, 'A1', 'application/pdf', 0.00, 'FREE', '2026-03-28 14:03:40.445777', 67, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/conversations-a1-thumb.jpg', 'Everyday English Conversations for Beginners', '2026-04-17 14:03:40.445777', 890);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (5, 4.8, 'GRAMMAR', '/uploads/ebooks/covers/grammar-a2.jpg', '2026-03-20 14:03:40.445777', 6, 'Progression méthodique dans la grammaire anglaise pour niveau élémentaire. Couvre les temps du passé, le présent perfect, les modaux et les conditionnels. Chaque chapitre inclut des explications détaillées et des exercices progressifs.', 287, 4194304, 'https://www.perfect-english-grammar.com/support-files/english_grammar_a2.pdf', true, 'A2', 'application/pdf', 0.00, 'FREE', '2026-03-20 14:03:40.445777', 95, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/grammar-a2-thumb.jpg', 'English Grammar Step by Step - A2', '2026-04-17 14:03:40.445777', 1420);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (6, 4.7, 'VOCABULARY', '/uploads/ebooks/covers/phrasal-verbs.jpg', '2026-03-26 14:03:40.445777', 7, 'Maîtrisez les phrasal verbs essentiels de l''anglais. Plus de 200 phrasal verbs courants expliqués avec des exemples contextuels, des illustrations et des exercices pratiques. Indispensable pour progresser en anglais.', 356, 3670016, 'https://www.englishpage.com/prepositions/phrasaldictionary.pdf', true, 'A2', 'application/pdf', 0.00, 'FREE', '2026-03-26 14:03:40.445777', 118, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/phrasal-verbs-thumb.jpg', 'Phrasal Verbs Made Easy', '2026-04-17 14:03:40.445777', 1680);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (7, 4.9, 'GENERAL', '/uploads/ebooks/covers/stories-a2.jpg', '2026-03-30 14:03:40.445777', 9, 'Collection de 20 histoires courtes adaptées au niveau A2. Chaque histoire est suivie de questions de compréhension, vocabulaire clé et activités. Parfait pour améliorer la lecture et enrichir son vocabulaire de manière ludique.', 423, 5767168, 'https://www.englishe-books.com/short-stories-a2.pdf', true, 'A2', 'application/pdf', 0.00, 'FREE', '2026-03-30 14:03:40.445777', 145, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/stories-a2-thumb.jpg', 'Short Stories for English Learners A2', '2026-04-17 14:03:40.445777', 2100);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (8, 4.8, 'GRAMMAR', '/uploads/ebooks/covers/grammar-b1.jpg', '2026-03-13 14:03:40.445777', 6, 'Grammaire anglaise intermédiaire avec approche contextuelle. Couvre les structures complexes, le discours indirect, les temps narratifs et les nuances grammaticales. Exercices basés sur des textes authentiques.', 512, 6291456, 'https://www.cambridge.org/files/3514/6173/1619/intermediate-grammar.pdf', true, 'B1', 'application/pdf', 0.00, 'FREE', '2026-03-13 14:03:40.445777', 167, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/grammar-b1-thumb.jpg', 'Intermediate English Grammar in Context', '2026-04-17 14:03:40.445777', 2450);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (9, 4.9, 'BUSINESS', '/uploads/ebooks/covers/business-english.jpg', '2026-04-02 14:03:40.445777', 7, 'Anglais des affaires pour professionnels. Vocabulaire commercial, rédaction d''emails, présentations, négociations et réunions. Inclut des modèles de documents et des études de cas réels du monde professionnel.', 189, 7340032, 'https://www.businessenglishpod.com/wp-content/uploads/2019/03/Business-English-Essentials.pdf', false, 'B1', 'application/pdf', 19.99, 'PREMIUM', '2026-04-02 14:03:40.445777', 78, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/business-english-thumb.jpg', 'Business English Essentials', '2026-04-17 14:03:40.445777', 980);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (10, 4.6, 'GENERAL', '/uploads/ebooks/covers/academic-writing.jpg', '2026-04-05 14:03:40.445777', 8, 'Guide complet de rédaction académique en anglais. Structure d''essais, argumentation, citations, paraphrases et style académique. Exemples d''essais annotés et exercices de rédaction progressive.', 298, 5505024, 'https://www.uefap.com/writing/writfram.htm', true, 'B1', 'application/pdf', 0.00, 'FREE', '2026-04-05 14:03:40.445777', 92, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/academic-writing-thumb.jpg', 'Academic Writing Skills B1-B2', '2026-04-17 14:03:40.445777', 1560);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (11, 4.7, 'VOCABULARY', '/uploads/ebooks/covers/idioms-b1.jpg', '2026-04-07 14:03:40.445777', 9, 'Plus de 500 expressions idiomatiques anglaises expliquées. Origine, signification, usage et exemples en contexte. Organisé par thèmes avec exercices de mise en pratique et quiz interactifs.', 445, 4718592, 'https://www.englishclub.com/ref/Idioms/', true, 'B1', 'application/pdf', 0.00, 'FREE', '2026-04-07 14:03:40.445777', 134, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/idioms-b1-thumb.jpg', 'Idioms and Expressions Intermediate', '2026-04-17 14:03:40.445777', 2200);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (12, 4.9, 'GRAMMAR', '/uploads/ebooks/covers/grammar-b2.jpg', '2026-03-08 14:03:40.445777', 6, 'Grammaire anglaise avancée pour niveau B2-C1. Structures sophistiquées, nuances grammaticales, style formel vs informel. 100 unités avec explications détaillées et exercices corrigés. Référence indispensable.', 234, 8388608, 'https://www.cambridge.org/files/8914/6173/1620/advanced-grammar.pdf', false, 'B2', 'application/pdf', 24.99, 'PREMIUM', '2026-03-08 14:03:40.445777', 98, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/grammar-b2-thumb.jpg', 'Advanced Grammar in Use', '2026-04-17 14:03:40.445777', 1340);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (13, 4.8, 'EXAM_PREP', '/uploads/ebooks/covers/ielts-prep.jpg', '2026-03-03 14:03:40.445777', 7, 'Préparation complète à l''examen IELTS. Stratégies pour les 4 sections (Listening, Reading, Writing, Speaking), tests blancs complets, conseils d''examinateurs et techniques de gestion du temps. Inclut 6 tests pratiques.', 567, 12582912, 'https://www.ielts.org/for-test-takers/sample-test-questions', false, 'B2', 'application/pdf', 29.99, 'PREMIUM', '2026-03-03 14:03:40.445777', 189, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/ielts-prep-thumb.jpg', 'IELTS Preparation Complete Guide', '2026-04-17 14:03:40.445777', 3200);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (14, 4.7, 'BUSINESS', '/uploads/ebooks/covers/email-writing.jpg', '2026-04-09 14:03:40.445777', 8, 'Maîtrisez la rédaction d''emails professionnels en anglais. Formules de politesse, structure, ton approprié, gestion des situations délicates. Plus de 100 modèles d''emails pour toutes situations professionnelles.', 389, 3932160, 'https://www.businesswritingblog.com/email-writing-guide.pdf', true, 'B2', 'application/pdf', 0.00, 'FREE', '2026-04-09 14:03:40.445777', 112, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/email-writing-thumb.jpg', 'Professional Email Writing Masterclass', '2026-04-17 14:03:40.445777', 1890);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (15, 4.6, 'GENERAL', '/uploads/ebooks/covers/literature-classics.jpg', '2026-04-11 14:03:40.445777', 9, 'Introduction à la littérature anglaise classique. Résumés et analyses de 20 œuvres majeures (Shakespeare, Dickens, Austen, etc.) avec vocabulaire clé et contexte historique. Parfait pour découvrir les classiques.', 276, 9437184, 'https://www.gutenberg.org/files/literature-guide.pdf', true, 'B2', 'application/pdf', 0.00, 'FREE', '2026-04-11 14:03:40.445777', 87, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/literature-classics-thumb.jpg', 'English Literature Classics Simplified', '2026-04-17 14:03:40.445777', 1450);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (16, 4.9, 'VOCABULARY', '/uploads/ebooks/covers/collocations-c1.jpg', '2026-02-26 14:03:40.445777', 6, 'Guide complet des collocations anglaises pour niveau avancé. Plus de 2000 collocations courantes classées par catégories avec exemples authentiques. Essentiel pour atteindre un niveau de langue naturel et fluide.', 198, 6815744, 'https://www.onestopenglish.com/collocations-advanced.pdf', false, 'C1', 'application/pdf', 22.99, 'PREMIUM', '2026-02-26 14:03:40.445777', 76, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/collocations-c1-thumb.jpg', 'Mastering English Collocations', '2026-04-17 14:03:40.445777', 1120);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (17, 4.8, 'EXAM_PREP', '/uploads/ebooks/covers/cambridge-c1.jpg', '2026-02-21 14:03:40.445777', 7, 'Préparation intensive au Cambridge C1 Advanced (CAE). Stratégies d''examen, techniques de réponse, 8 tests complets avec corrections détaillées. Conseils d''examinateurs Cambridge pour maximiser votre score.', 423, 15728640, 'https://www.cambridgeenglish.org/exams-and-tests/advanced/preparation/', false, 'C1', 'application/pdf', 34.99, 'PREMIUM', '2026-02-21 14:03:40.445777', 145, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/cambridge-c1-thumb.jpg', 'Cambridge C1 Advanced Exam Preparation', '2026-04-17 14:03:40.445777', 2340);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (20, 0, 'EXAM_PREP', '/uploads/ebooks/covers/toefl-prep.jpg', '2026-04-16 14:03:40.445777', 7, 'Guide stratégique complet pour le TOEFL iBT. Techniques pour chaque section, gestion du temps, 4 tests complets, vocabulaire académique essentiel. Préparation intensive pour obtenir un score élevé.', 0, 11534336, 'https://www.ets.org/toefl/test-takers/ibt/prepare/', false, 'B2', 'application/pdf', 32.99, 'PREMIUM', NULL, 0, NULL, 'PENDING', '/uploads/ebooks/thumbnails/toefl-prep-thumb.jpg', 'TOEFL iBT Complete Strategy Guide', '2026-04-17 14:03:40.445777', 78);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (22, 0, 'GENERAL', 'd738ee79-6501-4dc2-80a2-d1e4fc48a989_images (1).jpg', '2026-04-17 14:03:40.445777', 9, 'Guide de l''écriture créative en anglais. Techniques narratives, développement de personnages, dialogues, descriptions, styles littéraires. Exercices d''écriture et analyse d''extraits d''auteurs célèbres.

__METADATA__
{"chapters":[],"totalPages":0,"estimatedReadTime":0,"pricing":"free","price":0,"release":"immediate","scheduledDate":"","audience":[],"accessCode":"","hasCoverImage":true}', 0, 6291456, 'https://www.creativewriting.org/english-guide.pdf', true, 'C1', 'application/pdf', 0.00, 'FREE', NULL, 0, '2026-05-01 14:03:40.445777', 'SCHEDULED', '/uploads/ebooks/thumbnails/creative-writing-thumb.jpg', 'Creative Writing in English', '2026-05-09 16:36:15.605662', 0);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (18, 4.7, 'BUSINESS', 'd536d23e-352d-473a-a453-5edbaba91868_71-qX0L+F1L._AC_UF1000,1000_QL80_.jpg', '2026-04-12 14:03:40.445777', 8, 'Techniques avancées de négociation en anglais des affaires. Stratégies de persuasion, gestion des conflits, langage diplomatique, négociations interculturelles. Études de cas réels et simulations.

__METADATA__
{"chapters":[],"totalPages":0,"estimatedReadTime":0,"pricing":"free","price":0,"release":"immediate","scheduledDate":"","audience":[],"accessCode":"","hasCoverImage":true}', 167, 5242880, 'https://www.negotiationskills.com/advanced-business-english.pdf', true, 'C1', 'application/pdf', 27.99, 'PREMIUM', '2026-04-12 14:03:40.445777', 54, NULL, 'PUBLISHED', '/uploads/ebooks/thumbnails/negotiations-thumb.jpg', 'Advanced Business Negotiations', '2026-05-09 16:35:47.486296', 890);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (21, 0, 'BUSINESS', 'e6acedfe-0805-47a3-acaa-f61dfa4e3ee4_images (2).jpg', '2026-04-17 14:03:40.445777', 8, 'Anglais médical pour professionnels de santé. Terminologie médicale, communication patient-médecin, rédaction de rapports médicaux, présentations de cas cliniques. Vocabulaire spécialisé par spécialité.

__METADATA__
{"chapters":[],"totalPages":0,"estimatedReadTime":0,"pricing":"free","price":0,"release":"immediate","scheduledDate":"","audience":[],"accessCode":"","hasCoverImage":true}', 0, 7864320, 'https://www.medical-english.com/professional-guide.pdf', true, 'B2', 'application/pdf', 26.99, 'PREMIUM', NULL, 0, '2026-04-24 14:03:40.445777', 'SCHEDULED', '/uploads/ebooks/thumbnails/medical-english-thumb.jpg', 'English for Medical Professionals', '2026-05-09 16:36:44.154172', 0);
INSERT INTO public.ebook (id, average_rating, category, cover_image_url, created_at, created_by, description, download_count, file_size, file_url, is_free, level, mime_type, price, pricing_model, published_at, review_count, scheduled_for, status, thumbnail_url, title, updated_at, view_count) VALUES (23, 5, 'VOCABULARY', '54147ea7-46a7-4988-95e8-81833cbdcc6b_rs=w_1280.png', '2026-05-09 16:22:19.69279', 1, 'Atelier complet de prononciation anglaise. Phonétique, intonation, rythme et accentuation. Exercices audio, diagrammes articulatoires et techniques de correction. Idéal pour réduire son accent et améliorer sa clarté.

__METADATA__
{"chapters":[],"totalPages":45,"estimatedReadTime":92,"pricing":"free","price":0,"release":"immediate","scheduledDate":"","audience":[],"accessCode":"","hasCoverImage":true}', 0, 2296289, 'ba134a65-9dde-4d07-8980-eb5c6e6a01f5_Metodo_SITE.pdf', true, 'B1', 'application/pdf', NULL, 'FREE', '2026-05-09 16:22:46.870617', 1, NULL, 'PUBLISHED', NULL, 'English Pronunciation Workshop', '2026-05-09 16:38:14.88462', 0);


ALTER TABLE public.ebook ENABLE TRIGGER ALL;

--
-- Data for Name: reading_progress; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.reading_progress DISABLE TRIGGER ALL;



ALTER TABLE public.reading_progress ENABLE TRIGGER ALL;

--
-- Data for Name: bookmark; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.bookmark DISABLE TRIGGER ALL;



ALTER TABLE public.bookmark ENABLE TRIGGER ALL;

--
-- Data for Name: ebook_collection; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.ebook_collection DISABLE TRIGGER ALL;



ALTER TABLE public.ebook_collection ENABLE TRIGGER ALL;

--
-- Data for Name: collection_ebooks; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.collection_ebooks DISABLE TRIGGER ALL;



ALTER TABLE public.collection_ebooks ENABLE TRIGGER ALL;

--
-- Data for Name: ebook_access; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.ebook_access DISABLE TRIGGER ALL;



ALTER TABLE public.ebook_access ENABLE TRIGGER ALL;

--
-- Data for Name: ebook_chapter; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.ebook_chapter DISABLE TRIGGER ALL;



ALTER TABLE public.ebook_chapter ENABLE TRIGGER ALL;

--
-- Data for Name: ebook_metadata; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.ebook_metadata DISABLE TRIGGER ALL;



ALTER TABLE public.ebook_metadata ENABLE TRIGGER ALL;

--
-- Data for Name: ebook_keywords; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.ebook_keywords DISABLE TRIGGER ALL;



ALTER TABLE public.ebook_keywords ENABLE TRIGGER ALL;

--
-- Data for Name: ebook_note; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.ebook_note DISABLE TRIGGER ALL;



ALTER TABLE public.ebook_note ENABLE TRIGGER ALL;

--
-- Data for Name: ebook_review; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.ebook_review DISABLE TRIGGER ALL;

INSERT INTO public.ebook_review (id, comment, created_at, helpful_count, is_verified, rating, updated_at, user_id, ebook_id) VALUES (1, 'TEST', '2026-04-14 17:25:20.63744', 0, false, 5, '2026-04-14 17:25:20.63744', 1, 1);
INSERT INTO public.ebook_review (id, comment, created_at, helpful_count, is_verified, rating, updated_at, user_id, ebook_id) VALUES (2, 'I liked this book ', '2026-05-09 16:38:14.820621', 0, false, 5, '2026-05-09 16:38:14.820621', 1, 23);


ALTER TABLE public.ebook_review ENABLE TRIGGER ALL;

--
-- Data for Name: tag; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.tag DISABLE TRIGGER ALL;



ALTER TABLE public.tag ENABLE TRIGGER ALL;

--
-- Data for Name: ebook_tags; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.ebook_tags DISABLE TRIGGER ALL;



ALTER TABLE public.ebook_tags ENABLE TRIGGER ALL;

--
-- Data for Name: quiz; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.quiz DISABLE TRIGGER ALL;

INSERT INTO public.quiz (id, category, course_id, created_at, description, difficulty, due_date, duration_minutes, max_score, passing_score, publish_at, published, show_answers_timing, shuffle_options, shuffle_questions, tags, title, updated_at) VALUES (1, NULL, 49, '2026-04-17 14:23:30.530359', 'Test your knowledge of nouns, verbs, adjectives, and adverbs', NULL, NULL, 15, NULL, 70, NULL, true, NULL, NULL, NULL, NULL, 'Parts of Speech Quiz', '2026-04-17 14:23:30.530359');
INSERT INTO public.quiz (id, category, course_id, created_at, description, difficulty, due_date, duration_minutes, max_score, passing_score, publish_at, published, show_answers_timing, shuffle_options, shuffle_questions, tags, title, updated_at) VALUES (2, NULL, 49, '2026-04-17 14:23:30.530359', 'Master present simple and present continuous', NULL, NULL, 20, NULL, 70, NULL, true, NULL, NULL, NULL, NULL, 'Present Tenses Quiz', '2026-04-17 14:23:30.530359');
INSERT INTO public.quiz (id, category, course_id, created_at, description, difficulty, due_date, duration_minutes, max_score, passing_score, publish_at, published, show_answers_timing, shuffle_options, shuffle_questions, tags, title, updated_at) VALUES (3, NULL, 50, '2026-04-17 14:23:30.530359', 'Test your business email skills', NULL, NULL, 25, NULL, 75, NULL, true, NULL, NULL, NULL, NULL, 'Professional Email Writing Quiz', '2026-04-17 14:23:30.530359');
INSERT INTO public.quiz (id, category, course_id, created_at, description, difficulty, due_date, duration_minutes, max_score, passing_score, publish_at, published, show_answers_timing, shuffle_options, shuffle_questions, tags, title, updated_at) VALUES (4, NULL, 50, '2026-04-17 14:23:30.530359', 'Master presentation language', NULL, NULL, 20, NULL, 75, NULL, true, NULL, NULL, NULL, NULL, 'Business Presentations Quiz', '2026-04-17 14:23:30.530359');
INSERT INTO public.quiz (id, category, course_id, created_at, description, difficulty, due_date, duration_minutes, max_score, passing_score, publish_at, published, show_answers_timing, shuffle_options, shuffle_questions, tags, title, updated_at) VALUES (5, NULL, 51, '2026-04-17 14:23:30.530359', 'Test your pronunciation knowledge', NULL, NULL, 15, NULL, 70, NULL, true, NULL, NULL, NULL, NULL, 'English Phonetics Quiz', '2026-04-17 14:23:30.530359');
INSERT INTO public.quiz (id, category, course_id, created_at, description, difficulty, due_date, duration_minutes, max_score, passing_score, publish_at, published, show_answers_timing, shuffle_options, shuffle_questions, tags, title, updated_at) VALUES (6, NULL, 51, '2026-04-17 14:23:30.530359', 'Master rising and falling intonation', NULL, NULL, 15, NULL, 70, NULL, true, NULL, NULL, NULL, NULL, 'Intonation Patterns Quiz', '2026-04-17 14:23:30.530359');
INSERT INTO public.quiz (id, category, course_id, created_at, description, difficulty, due_date, duration_minutes, max_score, passing_score, publish_at, published, show_answers_timing, shuffle_options, shuffle_questions, tags, title, updated_at) VALUES (7, NULL, 52, '2026-04-17 14:23:30.530359', 'Test your idiom knowledge', NULL, NULL, 20, NULL, 75, NULL, true, NULL, NULL, NULL, NULL, 'English Idioms Quiz', '2026-04-17 14:23:30.530359');
INSERT INTO public.quiz (id, category, course_id, created_at, description, difficulty, due_date, duration_minutes, max_score, passing_score, publish_at, published, show_answers_timing, shuffle_options, shuffle_questions, tags, title, updated_at) VALUES (8, NULL, 52, '2026-04-17 14:23:30.530359', 'Master advanced vocabulary', NULL, NULL, 25, NULL, 75, NULL, true, NULL, NULL, NULL, NULL, 'Academic Vocabulary Quiz', '2026-04-17 14:23:30.530359');
INSERT INTO public.quiz (id, category, course_id, created_at, description, difficulty, due_date, duration_minutes, max_score, passing_score, publish_at, published, show_answers_timing, shuffle_options, shuffle_questions, tags, title, updated_at) VALUES (9, NULL, 53, '2026-04-17 14:23:30.530359', 'Practice everyday phrases', NULL, NULL, 15, NULL, 70, NULL, true, NULL, NULL, NULL, NULL, 'Daily Conversations Quiz', '2026-04-17 14:23:30.530359');
INSERT INTO public.quiz (id, category, course_id, created_at, description, difficulty, due_date, duration_minutes, max_score, passing_score, publish_at, published, show_answers_timing, shuffle_options, shuffle_questions, tags, title, updated_at) VALUES (10, NULL, 53, '2026-04-17 14:23:30.530359', 'Navigate social contexts', NULL, NULL, 15, NULL, 70, NULL, true, NULL, NULL, NULL, NULL, 'Social Situations Quiz', '2026-04-17 14:23:30.530359');
INSERT INTO public.quiz (id, category, course_id, created_at, description, difficulty, due_date, duration_minutes, max_score, passing_score, publish_at, published, show_answers_timing, shuffle_options, shuffle_questions, tags, title, updated_at) VALUES (11, 'grammar', 57, '2026-05-09 16:06:55.49417', 'Quiz Test ', 'medium', NULL, 30, 100, 60, NULL, true, 'end', false, false, '', 'Quiz Test ', '2026-05-09 16:06:55.49417');
INSERT INTO public.quiz (id, category, course_id, created_at, description, difficulty, due_date, duration_minutes, max_score, passing_score, publish_at, published, show_answers_timing, shuffle_options, shuffle_questions, tags, title, updated_at) VALUES (12, 'grammar', 54, '2026-05-13 11:51:22.916699', 'kajzdkazdazd', 'medium', NULL, 30, 100, 60, NULL, true, 'end', false, false, '', 'Ceci est Un quiz de TESt ', '2026-05-13 11:54:58.826416');
INSERT INTO public.quiz (id, category, course_id, created_at, description, difficulty, due_date, duration_minutes, max_score, passing_score, publish_at, published, show_answers_timing, shuffle_options, shuffle_questions, tags, title, updated_at) VALUES (13, 'grammar', 54, '2026-05-13 12:05:57.511062', 'ML ML ML ML ', 'medium', NULL, 30, 100, 60, NULL, true, 'end', false, false, '', 'ML ', '2026-05-13 12:13:38.29044');


ALTER TABLE public.quiz ENABLE TRIGGER ALL;

--
-- Data for Name: question; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.question DISABLE TRIGGER ALL;

INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (1, 'Which word is a NOUN: "The cat sleeps on the mat"?', NULL, NULL, 1, NULL, 10, 'MCQ', 1);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (2, 'Identify the VERB: "She quickly runs to school"', NULL, NULL, 2, NULL, 10, 'MCQ', 1);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (3, 'Which word is an ADJECTIVE: "The beautiful garden"?', NULL, NULL, 3, NULL, 10, 'MCQ', 1);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (4, 'Find the ADVERB: "He speaks English fluently"', NULL, NULL, 4, NULL, 10, 'MCQ', 1);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (5, 'A noun is a word that describes an action.', NULL, NULL, 5, NULL, 10, 'TRUE_FALSE', 1);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (6, 'Adjectives describe nouns.', NULL, NULL, 6, NULL, 10, 'TRUE_FALSE', 1);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (7, 'Adverbs can modify verbs, adjectives, and other adverbs.', NULL, NULL, 7, NULL, 10, 'TRUE_FALSE', 1);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (8, 'Write a sentence using a noun, a verb, and an adjective.', NULL, NULL, 8, NULL, 15, 'OPEN', 1);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (9, 'Choose correct form: "She ___ to work every day."', NULL, NULL, 1, NULL, 10, 'MCQ', 2);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (10, 'Complete: "They ___ (play) football right now."', NULL, NULL, 2, NULL, 10, 'MCQ', 2);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (11, 'Select present simple: "I ___ coffee every morning."', NULL, NULL, 3, NULL, 10, 'MCQ', 2);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (12, 'Which is correct: "He is knowing" or "He knows"?', NULL, NULL, 4, NULL, 10, 'MCQ', 2);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (13, 'Present simple is used for habits and routines.', NULL, NULL, 5, NULL, 10, 'TRUE_FALSE', 2);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (14, 'Present continuous describes actions happening now.', NULL, NULL, 6, NULL, 10, 'TRUE_FALSE', 2);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (15, 'Write 3 sentences using present simple tense.', NULL, NULL, 7, NULL, 15, 'OPEN', 2);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (16, 'Which is the most professional email greeting?', NULL, NULL, 1, NULL, 10, 'MCQ', 3);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (17, 'How should you close a formal business email?', NULL, NULL, 2, NULL, 10, 'MCQ', 3);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (18, 'Which phrase is appropriate for requesting information?', NULL, NULL, 3, NULL, 10, 'MCQ', 3);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (19, 'Using "Dear Sir/Madam" is appropriate when you don''t know the recipient''s name.', NULL, NULL, 4, NULL, 10, 'TRUE_FALSE', 3);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (20, 'It''s professional to use emojis in business emails.', NULL, NULL, 5, NULL, 10, 'TRUE_FALSE', 3);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (21, 'You should always proofread your email before sending.', NULL, NULL, 6, NULL, 10, 'TRUE_FALSE', 3);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (22, 'Write a professional email requesting a meeting.', NULL, NULL, 7, NULL, 20, 'OPEN', 3);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (23, 'Which phrase is best for introducing your presentation?', NULL, NULL, 1, NULL, 10, 'MCQ', 4);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (24, 'How do you transition to the next slide professionally?', NULL, NULL, 2, NULL, 10, 'MCQ', 4);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (25, 'Which phrase invites questions from the audience?', NULL, NULL, 3, NULL, 10, 'MCQ', 4);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (26, 'Eye contact with the audience is important.', NULL, NULL, 4, NULL, 10, 'TRUE_FALSE', 4);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (27, 'Reading directly from slides is good technique.', NULL, NULL, 5, NULL, 10, 'TRUE_FALSE', 4);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (28, 'List 5 phrases to introduce a presentation topic.', NULL, NULL, 6, NULL, 20, 'OPEN', 4);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (29, 'Which word has different vowel: "cat", "bat", "beat", "hat"?', NULL, NULL, 1, NULL, 10, 'MCQ', 5);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (30, 'Identify the word with /θ/ sound (think)', NULL, NULL, 2, NULL, 10, 'MCQ', 5);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (31, 'Which word is stressed on second syllable?', NULL, NULL, 3, NULL, 10, 'MCQ', 5);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (32, 'English has more vowel sounds than vowel letters.', NULL, NULL, 4, NULL, 10, 'TRUE_FALSE', 5);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (33, 'Word stress can change the meaning of a word.', NULL, NULL, 5, NULL, 10, 'TRUE_FALSE', 5);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (34, 'List 5 words with the /ð/ sound (as in "this").', NULL, NULL, 6, NULL, 20, 'OPEN', 5);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (35, 'Which question uses rising intonation?', NULL, NULL, 1, NULL, 10, 'MCQ', 6);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (36, 'Falling intonation is typically used for:', NULL, NULL, 2, NULL, 10, 'MCQ', 6);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (37, 'In a list, which item has falling intonation?', NULL, NULL, 3, NULL, 10, 'MCQ', 6);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (38, 'Yes/No questions use rising intonation.', NULL, NULL, 4, NULL, 10, 'TRUE_FALSE', 6);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (39, 'Wh-questions use falling intonation.', NULL, NULL, 5, NULL, 10, 'TRUE_FALSE', 6);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (40, 'Give 3 examples of questions with rising intonation.', NULL, NULL, 6, NULL, 20, 'OPEN', 6);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (41, 'What does "break the ice" mean?', NULL, NULL, 1, NULL, 10, 'MCQ', 7);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (42, 'If something "costs an arm and a leg", it is:', NULL, NULL, 2, NULL, 10, 'MCQ', 7);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (43, '"Piece of cake" means:', NULL, NULL, 3, NULL, 10, 'MCQ', 7);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (44, 'Idioms should be translated word-for-word.', NULL, NULL, 4, NULL, 10, 'TRUE_FALSE', 7);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (45, 'Native speakers use idioms frequently.', NULL, NULL, 5, NULL, 10, 'TRUE_FALSE', 7);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (46, 'Write a dialogue using at least 3 idioms.', NULL, NULL, 6, NULL, 20, 'OPEN', 7);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (47, 'Which word means "to make less severe"?', NULL, NULL, 1, NULL, 10, 'MCQ', 8);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (48, 'Select synonym for "ubiquitous":', NULL, NULL, 2, NULL, 10, 'MCQ', 8);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (49, 'What does "paradigm" mean in academic writing?', NULL, NULL, 3, NULL, 10, 'MCQ', 8);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (50, 'Academic vocabulary is more formal than everyday language.', NULL, NULL, 4, NULL, 10, 'TRUE_FALSE', 8);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (51, 'Using complex vocabulary always improves writing.', NULL, NULL, 5, NULL, 10, 'TRUE_FALSE', 8);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (52, 'Write a paragraph using: analyze, significant, demonstrate.', NULL, NULL, 6, NULL, 20, 'OPEN', 8);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (53, 'How do you greet someone in the morning?', NULL, NULL, 1, NULL, 10, 'MCQ', 9);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (54, 'Which response is appropriate for "How are you?"', NULL, NULL, 2, NULL, 10, 'MCQ', 9);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (55, 'How do you politely ask someone to repeat?', NULL, NULL, 3, NULL, 10, 'MCQ', 9);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (56, '"Nice to meet you" is for first meetings.', NULL, NULL, 4, NULL, 10, 'TRUE_FALSE', 9);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (57, 'Small talk is important in English conversation.', NULL, NULL, 5, NULL, 10, 'TRUE_FALSE', 9);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (58, 'Write a conversation between two people meeting.', NULL, NULL, 6, NULL, 20, 'OPEN', 9);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (59, 'How do you politely decline an invitation?', NULL, NULL, 1, NULL, 10, 'MCQ', 10);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (60, 'Which phrase is appropriate for making an apology?', NULL, NULL, 2, NULL, 10, 'MCQ', 10);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (61, 'How do you ask for permission politely?', NULL, NULL, 3, NULL, 10, 'MCQ', 10);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (62, 'Using "please" and "thank you" is important.', NULL, NULL, 4, NULL, 10, 'TRUE_FALSE', 10);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (63, 'Body language is not important in conversation.', NULL, NULL, 5, NULL, 10, 'TRUE_FALSE', 10);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (64, 'Write a polite way to disagree with someone.', NULL, NULL, 6, NULL, 20, 'OPEN', 10);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (65, 'Test ', 'False', '', 0, false, 10, 'TRUE_FALSE', 11);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (67, 'Je suis beau ? ', 'True', '', 0, false, 10, 'TRUE_FALSE', 12);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (66, 'Bhim ?', 'False', '', 1, false, 10, 'TRUE_FALSE', 12);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (68, 'TESTSTSTS', 'A', 'A | B ', 2, false, 10, 'MCQ', 12);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (69, 'ML ML ML ML ', 'False', '', 0, false, 10, 'TRUE_FALSE', 13);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (70, 'ML ML ML ', 'B ', 'A | B | C ', 2, false, 10, 'MCQ', 13);
INSERT INTO public.question (id, content, correct_answer, options, order_index, partial_credit_enabled, points, type, quiz_id) VALUES (71, 'ML ML ML ML ML ', 'True', '', 1, false, 10, 'TRUE_FALSE', 13);


ALTER TABLE public.question ENABLE TRIGGER ALL;

--
-- Data for Name: quiz_attempt; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.quiz_attempt DISABLE TRIGGER ALL;

INSERT INTO public.quiz_attempt (id, score, started_at, status, student_id, submitted_at, quiz_id) VALUES (8, 10, '2026-05-13 13:26:16.300887', 'COMPLETED', 1, '2026-05-13 13:26:16.473405', 13);


ALTER TABLE public.quiz_attempt ENABLE TRIGGER ALL;

--
-- Data for Name: student_answer; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.student_answer DISABLE TRIGGER ALL;

INSERT INTO public.student_answer (id, answer, is_correct, partial_points, points_earned, question_id, attempt_id) VALUES (19, 'True', false, NULL, 0, 69, 8);
INSERT INTO public.student_answer (id, answer, is_correct, partial_points, points_earned, question_id, attempt_id) VALUES (20, 'True', true, NULL, 10, 71, 8);
INSERT INTO public.student_answer (id, answer, is_correct, partial_points, points_earned, question_id, attempt_id) VALUES (21, ' C ', false, NULL, 0, 70, 8);


ALTER TABLE public.student_answer ENABLE TRIGGER ALL;

--
-- Name: bookmark_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.bookmark_id_seq', 1, false);


--
-- Name: ebook_access_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ebook_access_id_seq', 1, false);


--
-- Name: ebook_chapter_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ebook_chapter_id_seq', 1, false);


--
-- Name: ebook_collection_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ebook_collection_id_seq', 1, false);


--
-- Name: ebook_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ebook_id_seq', 23, true);


--
-- Name: ebook_metadata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ebook_metadata_id_seq', 1, false);


--
-- Name: ebook_note_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ebook_note_id_seq', 1, false);


--
-- Name: ebook_review_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.ebook_review_id_seq', 2, true);


--
-- Name: question_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.question_id_seq', 71, true);


--
-- Name: quiz_attempt_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.quiz_attempt_id_seq', 8, true);


--
-- Name: quiz_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.quiz_id_seq', 13, true);


--
-- Name: reading_progress_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.reading_progress_id_seq', 1, false);


--
-- Name: student_answer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.student_answer_id_seq', 21, true);


--
-- Name: tag_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tag_id_seq', 1, false);


--
-- PostgreSQL database dump complete
--

\unrestrict dSQeB9Stb5MRw1z19aqE7L8WMaNkL98xrb4VKnk9CHReUFViH4dPWyPt9c07xhh

