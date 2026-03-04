-- =====================================================
-- Learning Service - Ebooks Test Data
-- =====================================================
-- Database: englishflow_learning_db
-- =====================================================

-- Clean existing data (optional - uncomment if needed)
-- DELETE FROM ebook_keywords;
-- DELETE FROM ebook_tags;
-- DELETE FROM ebook_review;
-- DELETE FROM ebook_metadata;
-- DELETE FROM ebook;
-- DELETE FROM tag;

-- =====================================================
-- TAGS
-- =====================================================

INSERT INTO tag (name, slug, color) VALUES
('Beginner Friendly', 'beginner-friendly', '#10B981'),
('Advanced', 'advanced', '#EF4444'),
('Business English', 'business-english', '#3B82F6'),
('Grammar', 'grammar', '#8B5CF6'),
('Vocabulary', 'vocabulary', '#F59E0B'),
('TOEFL', 'toefl', '#EC4899'),
('IELTS', 'ielts', '#06B6D4'),
('Conversation', 'conversation', '#14B8A6'),
('Writing', 'writing', '#6366F1'),
('Reading', 'reading', '#84CC16'),
('Listening', 'listening', '#F97316'),
('Pronunciation', 'pronunciation', '#A855F7'),
('Idioms', 'idioms', '#22D3EE'),
('Phrasal Verbs', 'phrasal-verbs', '#FB923C'),
('Cambridge', 'cambridge', '#0EA5E9'),
('Self-Study', 'self-study', '#10B981'),
('Interactive', 'interactive', '#F43F5E'),
('Exercises', 'exercises', '#8B5CF6'),
('Audio Included', 'audio-included', '#06B6D4'),
('Quick Reference', 'quick-reference', '#F59E0B')
ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- EBOOKS - A1 Level (Beginner)
-- =====================================================

INSERT INTO ebook (title, description, file_url, file_size, mime_type, cover_image_url, level, category, is_free, price, pricing_model, download_count, view_count, average_rating, review_count, status, published_at, created_by, created_at, updated_at) VALUES
('English for Absolute Beginners', 'Start your English journey with this comprehensive guide covering basic grammar, essential vocabulary, and simple conversations. Perfect for complete beginners with no prior English knowledge.', '/ebooks/english-absolute-beginners.pdf', 5242880, 'application/pdf', '/covers/beginner-1.jpg', 'A1', 'GENERAL', true, NULL, 'FREE', 1250, 3500, 4.7, 89, 'PUBLISHED', NOW() - INTERVAL '6 months', 1, NOW() - INTERVAL '6 months', NOW()),
('My First English Words', 'Learn 500 essential English words with colorful illustrations and pronunciation guides. Includes everyday vocabulary for home, school, and daily activities.', '/ebooks/first-english-words.pdf', 3145728, 'application/pdf', '/covers/beginner-2.jpg', 'A1', 'VOCABULARY', true, NULL, 'FREE', 980, 2800, 4.5, 67, 'PUBLISHED', NOW() - INTERVAL '5 months', 2, NOW() - INTERVAL '5 months', NOW()),
('Basic English Grammar Made Easy', 'Simple explanations of English grammar rules with lots of examples and exercises. Covers present simple, articles, pronouns, and basic sentence structure.', '/ebooks/basic-grammar-easy.pdf', 4194304, 'application/pdf', '/covers/grammar-1.jpg', 'A1', 'GRAMMAR', true, NULL, 'FREE', 1450, 4200, 4.8, 112, 'PUBLISHED', NOW() - INTERVAL '8 months', 1, NOW() - INTERVAL '8 months', NOW());

-- =====================================================
-- EBOOKS - A2 Level (Elementary)
-- =====================================================

INSERT INTO ebook (title, description, file_url, file_size, mime_type, cover_image_url, level, category, is_free, price, pricing_model, download_count, view_count, average_rating, review_count, status, published_at, created_by, created_at, updated_at) VALUES
('Elementary English Conversations', 'Practice everyday conversations with dialogues, role-plays, and useful phrases. Topics include shopping, restaurants, travel, and making friends.', '/ebooks/elementary-conversations.pdf', 6291456, 'application/pdf', '/covers/conversation-1.jpg', 'A2', 'GENERAL', true, NULL, 'FREE', 890, 2600, 4.6, 78, 'PUBLISHED', NOW() - INTERVAL '7 months', 3, NOW() - INTERVAL '7 months', NOW()),
('English Vocabulary Builder A2', 'Expand your vocabulary with 1000+ words organized by topics. Includes exercises, collocations, and memory techniques.', '/ebooks/vocab-builder-a2.pdf', 5767168, 'application/pdf', '/covers/vocab-2.jpg', 'A2', 'VOCABULARY', false, 9.99, 'PREMIUM', 450, 1800, 4.9, 95, 'PUBLISHED', NOW() - INTERVAL '4 months', 2, NOW() - INTERVAL '4 months', NOW()),
('Grammar in Use - Elementary', 'Comprehensive grammar reference with clear explanations and practice exercises. Covers past tenses, comparatives, modals, and more.', '/ebooks/grammar-use-elementary.pdf', 7340032, 'application/pdf', '/covers/grammar-2.jpg', 'A2', 'GRAMMAR', false, 12.99, 'PREMIUM', 620, 2100, 4.7, 103, 'PUBLISHED', NOW() - INTERVAL '9 months', 1, NOW() - INTERVAL '9 months', NOW());

-- =====================================================
-- EBOOKS - B1 Level (Intermediate)
-- =====================================================

INSERT INTO ebook (title, description, file_url, file_size, mime_type, cover_image_url, level, category, is_free, price, pricing_model, download_count, view_count, average_rating, review_count, status, published_at, created_by, created_at, updated_at) VALUES
('Intermediate English for Business', 'Master business English with emails, presentations, meetings, and negotiations. Includes real-world scenarios and professional vocabulary.', '/ebooks/business-intermediate.pdf', 8388608, 'application/pdf', '/covers/business-1.jpg', 'B1', 'BUSINESS', false, 15.99, 'PREMIUM', 780, 2900, 4.8, 134, 'PUBLISHED', NOW() - INTERVAL '3 months', 4, NOW() - INTERVAL '3 months', NOW()),
('English Phrasal Verbs in Context', 'Learn 300+ essential phrasal verbs through stories and real-life situations. Includes exercises and audio pronunciation.', '/ebooks/phrasal-verbs-context.pdf', 6815744, 'application/pdf', '/covers/phrasal-1.jpg', 'B1', 'VOCABULARY', true, NULL, 'FREE', 1100, 3800, 4.6, 98, 'PUBLISHED', NOW() - INTERVAL '5 months', 3, NOW() - INTERVAL '5 months', NOW()),
('IELTS Preparation Guide - Intermediate', 'Complete guide to IELTS exam with strategies, practice tests, and tips for all four skills. Ideal for band 5.5-6.5 target.', '/ebooks/ielts-prep-intermediate.pdf', 10485760, 'application/pdf', '/covers/ielts-1.jpg', 'B1', 'EXAM_PREP', false, 19.99, 'PREMIUM', 950, 3200, 4.9, 156, 'PUBLISHED', NOW() - INTERVAL '2 months', 5, NOW() - INTERVAL '2 months', NOW()),
('English Idioms and Expressions', 'Understand and use 500+ common idioms and expressions. Learn the meaning, origin, and usage with examples.', '/ebooks/idioms-expressions.pdf', 5505024, 'application/pdf', '/covers/idioms-1.jpg', 'B1', 'VOCABULARY', true, NULL, 'FREE', 870, 2700, 4.5, 82, 'PUBLISHED', NOW() - INTERVAL '6 months', 2, NOW() - INTERVAL '6 months', NOW());

-- =====================================================
-- EBOOKS - B2 Level (Upper Intermediate)
-- =====================================================

INSERT INTO ebook (title, description, file_url, file_size, mime_type, cover_image_url, level, category, is_free, price, pricing_model, download_count, view_count, average_rating, review_count, status, published_at, created_by, created_at, updated_at) VALUES
('Advanced Business Communication', 'Professional English for executives and managers. Covers leadership language, strategic communication, and cross-cultural business.', '/ebooks/advanced-business-comm.pdf', 9437184, 'application/pdf', '/covers/business-2.jpg', 'B2', 'BUSINESS', false, 24.99, 'PREMIUM', 560, 1900, 4.9, 87, 'PUBLISHED', NOW() - INTERVAL '4 months', 4, NOW() - INTERVAL '4 months', NOW()),
('TOEFL iBT Complete Guide', 'Comprehensive TOEFL preparation with full-length practice tests, strategies, and detailed answer explanations. Target score 80-100.', '/ebooks/toefl-complete-guide.pdf', 12582912, 'application/pdf', '/covers/toefl-1.jpg', 'B2', 'EXAM_PREP', false, 29.99, 'PREMIUM', 1200, 4100, 4.8, 178, 'PUBLISHED', NOW() - INTERVAL '1 month', 5, NOW() - INTERVAL '1 month', NOW()),
('Academic Writing Skills', 'Master academic writing with essays, research papers, and critical analysis. Includes citation styles and academic vocabulary.', '/ebooks/academic-writing-skills.pdf', 7864320, 'application/pdf', '/covers/writing-1.jpg', 'B2', 'GENERAL', false, 17.99, 'PREMIUM', 690, 2400, 4.7, 102, 'PUBLISHED', NOW() - INTERVAL '3 months', 1, NOW() - INTERVAL '3 months', NOW()),
('English Pronunciation Mastery', 'Perfect your pronunciation with phonetics, stress patterns, and intonation. Includes audio files and mouth diagrams.', '/ebooks/pronunciation-mastery.pdf', 8912896, 'application/pdf', '/covers/pronunciation-1.jpg', 'B2', 'GENERAL', true, NULL, 'FREE', 1050, 3600, 4.6, 119, 'PUBLISHED', NOW() - INTERVAL '7 months', 3, NOW() - INTERVAL '7 months', NOW());

-- =====================================================
-- EBOOKS - C1 Level (Advanced)
-- =====================================================

INSERT INTO ebook (title, description, file_url, file_size, mime_type, cover_image_url, level, category, is_free, price, pricing_model, download_count, view_count, average_rating, review_count, status, published_at, created_by, created_at, updated_at) VALUES
('Cambridge Advanced English (CAE)', 'Complete preparation for Cambridge C1 Advanced exam. Includes 8 practice tests, vocabulary lists, and exam strategies.', '/ebooks/cambridge-cae.pdf', 15728640, 'application/pdf', '/covers/cambridge-1.jpg', 'C1', 'EXAM_PREP', false, 34.99, 'PREMIUM', 420, 1500, 4.9, 76, 'PUBLISHED', NOW() - INTERVAL '2 months', 5, NOW() - INTERVAL '2 months', NOW()),
('Advanced English Grammar in Use', 'Comprehensive grammar reference for advanced learners. Covers complex structures, nuances, and stylistic choices.', '/ebooks/advanced-grammar-use.pdf', 11534336, 'application/pdf', '/covers/grammar-3.jpg', 'C1', 'GRAMMAR', false, 22.99, 'PREMIUM', 580, 2000, 4.8, 94, 'PUBLISHED', NOW() - INTERVAL '5 months', 1, NOW() - INTERVAL '5 months', NOW()),
('Professional English for International Business', 'High-level business English for global professionals. Covers negotiations, contracts, mergers, and international relations.', '/ebooks/professional-intl-business.pdf', 10485760, 'application/pdf', '/covers/business-3.jpg', 'C1', 'BUSINESS', false, 27.99, 'PREMIUM', 340, 1200, 4.7, 58, 'PUBLISHED', NOW() - INTERVAL '3 months', 4, NOW() - INTERVAL '3 months', NOW());

-- =====================================================
-- EBOOK METADATA
-- =====================================================

INSERT INTO ebook_metadata (ebook_id, author, publisher, isbn, total_pages, estimated_read_time_minutes, language, edition, publication_date, table_of_contents) VALUES
(1, 'Dr. Sarah Johnson', 'EnglishFlow Publishing', '978-1-234567-89-0', 180, 240, 'English', '1st Edition', '2023-06-15', 'Chapter 1: The Alphabet\nChapter 2: Basic Greetings\nChapter 3: Numbers and Colors\nChapter 4: Family and Friends\nChapter 5: Daily Routines'),
(2, 'Emily Brown', 'Language Learning Press', '978-1-234567-90-6', 120, 180, 'English', '2nd Edition', '2023-07-20', 'Part 1: Home Vocabulary\nPart 2: School Words\nPart 3: Food and Drinks\nPart 4: Animals and Nature\nPart 5: Actions and Verbs'),
(3, 'Prof. Michael Chen', 'Grammar Masters', '978-1-234567-91-3', 200, 300, 'English', '3rd Edition', '2023-04-10', 'Unit 1: Present Simple\nUnit 2: Articles\nUnit 3: Pronouns\nUnit 4: Basic Questions\nUnit 5: Sentence Structure'),
(4, 'James Anderson', 'Conversation Hub', '978-1-234567-92-0', 150, 210, 'English', '1st Edition', '2023-05-25', 'Section 1: Meeting People\nSection 2: Shopping\nSection 3: At the Restaurant\nSection 4: Travel and Transport\nSection 5: Making Plans'),
(5, 'Sophia Taylor', 'Vocabulary Plus', '978-1-234567-93-7', 250, 360, 'English', '1st Edition', '2023-08-15', 'Chapter 1: Work and Career\nChapter 2: Health and Fitness\nChapter 3: Technology\nChapter 4: Entertainment\nChapter 5: Environment'),
(6, 'Robert Wilson', 'Grammar Experts Ltd', '978-1-234567-94-4', 280, 420, 'English', '4th Edition', '2023-03-05', 'Part 1: Past Tenses\nPart 2: Comparatives and Superlatives\nPart 3: Modal Verbs\nPart 4: Conditionals\nPart 5: Passive Voice'),
(7, 'Lisa Garcia', 'Business English Pro', '978-1-234567-95-1', 220, 330, 'English', '2nd Edition', '2023-09-10', 'Module 1: Email Writing\nModule 2: Presentations\nModule 3: Meetings\nModule 4: Negotiations\nModule 5: Reports'),
(8, 'David Martinez', 'Phrasal Verb Academy', '978-1-234567-96-8', 190, 270, 'English', '1st Edition', '2023-07-01', 'Unit 1: Common Phrasal Verbs\nUnit 2: Business Phrasal Verbs\nUnit 3: Informal Phrasal Verbs\nUnit 4: Separable vs Inseparable\nUnit 5: Practice Exercises'),
(9, 'Emma Williams', 'IELTS Success', '978-1-234567-97-5', 350, 600, 'English', '5th Edition', '2023-10-20', 'Part 1: Listening Strategies\nPart 2: Reading Techniques\nPart 3: Writing Task 1 & 2\nPart 4: Speaking Practice\nPart 5: Full Practice Tests'),
(10, 'Ahmed Ben Ali', 'Idiom Masters', '978-1-234567-98-2', 170, 240, 'English', '1st Edition', '2023-06-30', 'Chapter 1: Common Idioms\nChapter 2: Business Idioms\nChapter 3: American vs British\nChapter 4: Slang Expressions\nChapter 5: Cultural Context');

INSERT INTO ebook_metadata (ebook_id, author, publisher, isbn, total_pages, estimated_read_time_minutes, language, edition, publication_date, table_of_contents) VALUES
(11, 'Jennifer Smith', 'Executive English', '978-1-234567-99-9', 260, 390, 'English', '1st Edition', '2023-08-25', 'Chapter 1: Leadership Language\nChapter 2: Strategic Communication\nChapter 3: Cross-Cultural Business\nChapter 4: Crisis Management\nChapter 5: Executive Presence'),
(12, 'Dr. Thomas Brown', 'TOEFL Masters', '978-1-234568-00-2', 400, 720, 'English', '6th Edition', '2023-11-05', 'Section 1: Reading Comprehension\nSection 2: Listening Skills\nSection 3: Speaking Tasks\nSection 4: Integrated Writing\nSection 5: Practice Tests'),
(13, 'Prof. Rachel Green', 'Academic Press', '978-1-234568-01-9', 240, 360, 'English', '2nd Edition', '2023-09-15', 'Part 1: Essay Structure\nPart 2: Research Papers\nPart 3: Critical Analysis\nPart 4: Citation Styles\nPart 5: Academic Vocabulary'),
(14, 'Mark Johnson', 'Pronunciation Pro', '978-1-234568-02-6', 210, 315, 'English', '1st Edition', '2023-05-10', 'Unit 1: Phonetics Basics\nUnit 2: Vowel Sounds\nUnit 3: Consonants\nUnit 4: Stress and Intonation\nUnit 5: Connected Speech'),
(15, 'Dr. Catherine White', 'Cambridge Press', '978-1-234568-03-3', 450, 900, 'English', '7th Edition', '2023-10-01', 'Test 1-8: Full Practice Tests\nAppendix A: Vocabulary Lists\nAppendix B: Grammar Reference\nAppendix C: Exam Strategies\nAppendix D: Answer Keys'),
(16, 'Prof. Martin Taylor', 'Advanced Grammar Ltd', '978-1-234568-04-0', 320, 480, 'English', '5th Edition', '2023-07-15', 'Chapter 1: Complex Sentences\nChapter 2: Subjunctive Mood\nChapter 3: Inversion\nChapter 4: Cleft Sentences\nChapter 5: Advanced Tenses'),
(17, 'Susan Anderson', 'Global Business English', '978-1-234568-05-7', 290, 435, 'English', '1st Edition', '2023-09-20', 'Module 1: International Negotiations\nModule 2: Contract Language\nModule 3: Mergers & Acquisitions\nModule 4: Global Marketing\nModule 5: Cultural Intelligence');

-- =====================================================
-- EBOOK KEYWORDS
-- =====================================================

INSERT INTO ebook_keywords (metadata_id, keyword) VALUES
(1, 'beginner'), (1, 'alphabet'), (1, 'basic grammar'), (1, 'greetings'), (1, 'numbers'),
(2, 'vocabulary'), (2, 'illustrations'), (2, 'pronunciation'), (2, 'everyday words'), (2, 'beginner'),
(3, 'grammar'), (3, 'present simple'), (3, 'articles'), (3, 'exercises'), (3, 'beginner'),
(4, 'conversation'), (4, 'dialogues'), (4, 'shopping'), (4, 'travel'), (4, 'elementary'),
(5, 'vocabulary'), (5, 'intermediate'), (5, 'collocations'), (5, 'exercises'), (5, 'topics'),
(6, 'grammar'), (6, 'past tenses'), (6, 'modals'), (6, 'conditionals'), (6, 'elementary'),
(7, 'business'), (7, 'emails'), (7, 'presentations'), (7, 'meetings'), (7, 'intermediate'),
(8, 'phrasal verbs'), (8, 'context'), (8, 'exercises'), (8, 'audio'), (8, 'intermediate'),
(9, 'IELTS'), (9, 'exam prep'), (9, 'practice tests'), (9, 'strategies'), (9, 'intermediate'),
(10, 'idioms'), (10, 'expressions'), (10, 'meaning'), (10, 'usage'), (10, 'intermediate'),
(11, 'business'), (11, 'executive'), (11, 'leadership'), (11, 'communication'), (11, 'advanced'),
(12, 'TOEFL'), (12, 'exam prep'), (12, 'practice tests'), (12, 'strategies'), (12, 'advanced'),
(13, 'academic writing'), (13, 'essays'), (13, 'research'), (13, 'citation'), (13, 'advanced'),
(14, 'pronunciation'), (14, 'phonetics'), (14, 'intonation'), (14, 'audio'), (14, 'advanced'),
(15, 'Cambridge'), (15, 'CAE'), (15, 'C1'), (15, 'exam prep'), (15, 'practice tests'),
(16, 'grammar'), (16, 'advanced'), (16, 'complex structures'), (16, 'reference'), (16, 'C1'),
(17, 'business'), (17, 'international'), (17, 'negotiations'), (17, 'contracts'), (17, 'advanced');

-- =====================================================
-- EBOOK TAGS (Many-to-Many)
-- =====================================================

INSERT INTO ebook_tags (ebook_id, tag_id) VALUES
-- English for Absolute Beginners
(1, 1), (1, 4), (1, 16),
-- My First English Words
(2, 1), (2, 5), (2, 10), (2, 19),
-- Basic English Grammar Made Easy
(3, 1), (3, 4), (3, 18), (3, 16),
-- Elementary English Conversations
(4, 1), (4, 8), (4, 16),
-- English Vocabulary Builder A2
(5, 5), (5, 18), (5, 16),
-- Grammar in Use - Elementary
(6, 4), (6, 18), (6, 16),
-- Intermediate English for Business
(7, 3), (7, 9), (7, 18),
-- English Phrasal Verbs in Context
(8, 14), (8, 5), (8, 19), (8, 16),
-- IELTS Preparation Guide
(9, 7), (9, 18), (9, 16),
-- English Idioms and Expressions
(10, 13), (10, 5), (10, 16),
-- Advanced Business Communication
(11, 3), (11, 2), (11, 9),
-- TOEFL iBT Complete Guide
(12, 6), (12, 18), (12, 16),
-- Academic Writing Skills
(13, 9), (13, 2), (13, 18),
-- English Pronunciation Mastery
(14, 12), (14, 19), (14, 16),
-- Cambridge Advanced English
(15, 15), (15, 2), (15, 18),
-- Advanced English Grammar in Use
(16, 4), (16, 2), (16, 20),
-- Professional English for International Business
(17, 3), (17, 2), (17, 9);

-- =====================================================
-- REVIEWS
-- =====================================================

-- Reviews for Ebook 1 (English for Absolute Beginners)
INSERT INTO ebook_review (ebook_id, user_id, rating, comment, is_verified, helpful_count, created_at, updated_at) VALUES
(1, 8, 5, 'Perfect for complete beginners! The explanations are clear and easy to follow. I learned so much in just a few weeks.', true, 45, NOW() - INTERVAL '3 months', NOW() - INTERVAL '3 months'),
(1, 9, 5, 'This book changed my life! I can now have basic conversations in English. Highly recommended!', true, 38, NOW() - INTERVAL '2 months', NOW() - INTERVAL '2 months'),
(1, 10, 4, 'Good book for starters. Some chapters could use more examples, but overall very helpful.', true, 22, NOW() - INTERVAL '1 month', NOW() - INTERVAL '1 month'),
(1, 11, 5, 'Excellent resource! The step-by-step approach makes learning English less intimidating.', false, 15, NOW() - INTERVAL '2 weeks', NOW() - INTERVAL '2 weeks');

-- Reviews for Ebook 3 (Basic English Grammar Made Easy)
INSERT INTO ebook_review (ebook_id, user_id, rating, comment, is_verified, helpful_count, created_at, updated_at) VALUES
(3, 8, 5, 'Finally, a grammar book that makes sense! The exercises are really helpful.', true, 52, NOW() - INTERVAL '4 months', NOW() - INTERVAL '4 months'),
(3, 12, 5, 'Best grammar book for beginners. Clear explanations with lots of practice.', true, 41, NOW() - INTERVAL '3 months', NOW() - INTERVAL '3 months'),
(3, 13, 4, 'Very good book. Helped me understand the basics of English grammar.', true, 28, NOW() - INTERVAL '1 month', NOW() - INTERVAL '1 month');

-- Reviews for Ebook 5 (English Vocabulary Builder A2)
INSERT INTO ebook_review (ebook_id, user_id, rating, comment, is_verified, helpful_count, created_at, updated_at) VALUES
(5, 14, 5, 'Worth every penny! My vocabulary has expanded significantly. The memory techniques really work.', true, 67, NOW() - INTERVAL '2 months', NOW() - INTERVAL '2 months'),
(5, 15, 5, 'Excellent organization by topics. Makes it easy to learn words related to specific areas.', true, 54, NOW() - INTERVAL '1 month', NOW() - INTERVAL '1 month'),
(5, 16, 4, 'Great book with useful vocabulary. Would love to see more audio examples.', true, 31, NOW() - INTERVAL '3 weeks', NOW() - INTERVAL '3 weeks');

-- Reviews for Ebook 9 (IELTS Preparation Guide)
INSERT INTO ebook_review (ebook_id, user_id, rating, comment, is_verified, helpful_count, created_at, updated_at) VALUES
(9, 17, 5, 'Got band 7 thanks to this book! The strategies are practical and the practice tests are realistic.', true, 89, NOW() - INTERVAL '1 month', NOW() - INTERVAL '1 month'),
(9, 18, 5, 'Comprehensive guide with everything you need for IELTS. Highly recommend!', true, 76, NOW() - INTERVAL '3 weeks', NOW() - INTERVAL '3 weeks'),
(9, 19, 5, 'Best IELTS book I have used. Clear explanations and useful tips.', true, 62, NOW() - INTERVAL '2 weeks', NOW() - INTERVAL '2 weeks'),
(9, 20, 4, 'Very helpful for IELTS preparation. Would be perfect with more speaking practice.', true, 44, NOW() - INTERVAL '1 week', NOW() - INTERVAL '1 week');

-- Reviews for Ebook 12 (TOEFL iBT Complete Guide)
INSERT INTO ebook_review (ebook_id, user_id, rating, comment, is_verified, helpful_count, created_at, updated_at) VALUES
(12, 21, 5, 'Scored 95 on TOEFL! This book is comprehensive and well-structured.', true, 98, NOW() - INTERVAL '2 weeks', NOW() - INTERVAL '2 weeks'),
(12, 22, 5, 'The best TOEFL preparation material. Practice tests are very similar to the real exam.', true, 87, NOW() - INTERVAL '1 week', NOW() - INTERVAL '1 week'),
(12, 23, 4, 'Excellent resource. Helped me understand the test format and improve my score.', true, 65, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days');

-- Reviews for Ebook 7 (Intermediate English for Business)
INSERT INTO ebook_review (ebook_id, user_id, rating, comment, is_verified, helpful_count, created_at, updated_at) VALUES
(7, 17, 5, 'Perfect for my job! I can now write professional emails and participate in meetings confidently.', true, 71, NOW() - INTERVAL '1 month', NOW() - INTERVAL '1 month'),
(7, 19, 5, 'Practical and useful. The real-world scenarios are very helpful.', true, 58, NOW() - INTERVAL '3 weeks', NOW() - INTERVAL '3 weeks'),
(7, 20, 4, 'Good business English resource. Would like more negotiation examples.', true, 42, NOW() - INTERVAL '2 weeks', NOW() - INTERVAL '2 weeks');

-- Reviews for Ebook 11 (Advanced Business Communication)
INSERT INTO ebook_review (ebook_id, user_id, rating, comment, is_verified, helpful_count, created_at, updated_at) VALUES
(11, 24, 5, 'Essential for executives. The leadership language section is outstanding.', true, 54, NOW() - INTERVAL '2 months', NOW() - INTERVAL '2 months'),
(11, 25, 5, 'High-level content for professionals. Exactly what I needed for my role.', true, 47, NOW() - INTERVAL '1 month', NOW() - INTERVAL '1 month'),
(11, 26, 4, 'Very professional and well-written. Great for senior management.', true, 33, NOW() - INTERVAL '3 weeks', NOW() - INTERVAL '3 weeks');

-- Reviews for Ebook 15 (Cambridge Advanced English)
INSERT INTO ebook_review (ebook_id, user_id, rating, comment, is_verified, helpful_count, created_at, updated_at) VALUES
(15, 27, 5, 'Passed CAE with grade A! This book is comprehensive and the practice tests are excellent.', true, 68, NOW() - INTERVAL '1 month', NOW() - INTERVAL '1 month'),
(15, 28, 5, 'Best Cambridge preparation book. Very thorough and well-organized.', true, 59, NOW() - INTERVAL '3 weeks', NOW() - INTERVAL '3 weeks'),
(15, 29, 4, 'Great resource for CAE. The vocabulary lists are particularly useful.', true, 41, NOW() - INTERVAL '2 weeks', NOW() - INTERVAL '2 weeks');

-- =====================================================
-- Summary
-- =====================================================
-- Total Ebooks: 17
-- - A1 Level: 3 ebooks
-- - A2 Level: 3 ebooks
-- - B1 Level: 4 ebooks
-- - B2 Level: 4 ebooks
-- - C1 Level: 3 ebooks
-- 
-- Categories:
-- - GENERAL: 6 ebooks
-- - VOCABULARY: 3 ebooks
-- - GRAMMAR: 3 ebooks
-- - BUSINESS: 3 ebooks
-- - EXAM_PREP: 2 ebooks
--
-- Pricing:
-- - FREE: 8 ebooks
-- - PREMIUM: 9 ebooks
--
-- Total Reviews: 30+ reviews
-- Total Tags: 20 tags
-- =====================================================
