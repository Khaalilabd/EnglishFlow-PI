-- =====================================================
-- Script d'insertion des données community
-- Base de données: englishflow_community
-- =====================================================
-- Note: Connectez-vous à la base englishflow_community avant d'exécuter ce script
-- Exemple: psql -U postgres -d englishflow_community -f insert-community.sql

-- Désactiver les contraintes de clés étrangères temporairement (PostgreSQL)
SET session_replication_role = 'replica';

-- =====================================================
-- CATEGORIES
-- =====================================================

INSERT INTO categories (name, description, icon, color, is_locked, created_at, updated_at)
VALUES 
('General', 'General discussions, announcements and community updates', '💬', '#3B82F6', false, NOW(), NOW()),
('Language Learning', 'Grammar, vocabulary, pronunciation and language tips', '📚', '#10B981', false, NOW(), NOW()),
('Clubs & Events', 'Join our thematic clubs and community events', '🎭', '#8B5CF6', false, NOW(), NOW()),
('Resources', 'Share and discover learning materials and resources', '📖', '#F59E0B', false, NOW(), NOW()),
('Q&A', 'Ask questions and get answers from the community', '❓', '#EF4444', false, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- SUB-CATEGORIES
-- =====================================================

INSERT INTO sub_categories (name, description, requires_club_membership, requires_admin_role, is_locked, category_id, created_at, updated_at)
VALUES 
-- General
('Announcements', 'Official announcements from EnglishFlow team', false, true, false, 1, NOW(), NOW()),
('Introductions', 'Introduce yourself to the community', false, false, false, 1, NOW(), NOW()),
('Off-Topic', 'Casual conversations and non-English topics', false, false, false, 1, NOW(), NOW()),

-- Language Learning
('Grammar Help', 'Get help with English grammar questions', false, false, false, 2, NOW(), NOW()),
('Vocabulary Building', 'Expand your vocabulary and learn new words', false, false, false, 2, NOW(), NOW()),
('Pronunciation Tips', 'Improve your pronunciation and accent', false, false, false, 2, NOW(), NOW()),
('Writing Practice', 'Share your writing and get feedback', false, false, false, 2, NOW(), NOW()),
('Speaking Practice', 'Find speaking partners and practice conversations', false, false, false, 2, NOW(), NOW()),

-- Clubs & Events
('Book Club', 'Discuss English books and literature', true, false, false, 3, NOW(), NOW()),
('Movie Club', 'Watch and discuss English movies and series', true, false, false, 3, NOW(), NOW()),
('Debate Club', 'Participate in structured debates in English', true, false, false, 3, NOW(), NOW()),
('Event Announcements', 'Upcoming workshops, webinars and events', false, false, false, 3, NOW(), NOW()),

-- Resources
('Study Materials', 'Share textbooks, worksheets and study guides', false, false, false, 4, NOW(), NOW()),
('Useful Websites', 'Recommend helpful websites and online tools', false, false, false, 4, NOW(), NOW()),
('Apps & Software', 'Discuss language learning apps and software', false, false, false, 4, NOW(), NOW()),
('Videos & Podcasts', 'Share educational videos and podcasts', false, false, false, 4, NOW(), NOW()),

-- Q&A
('Grammar Questions', 'Ask specific grammar questions', false, false, false, 5, NOW(), NOW()),
('Translation Help', 'Get help with translations', false, false, false, 5, NOW(), NOW()),
('Exam Preparation', 'IELTS, TOEFL, Cambridge exam questions', false, false, false, 5, NOW(), NOW()),
('Career Advice', 'English for professional development', false, false, false, 5, NOW(), NOW());

-- =====================================================
-- TOPICS - Variété de discussions
-- =====================================================

-- Topics dans Announcements (sub_category_id = 1)
INSERT INTO topics (title, content, user_id, user_name, sub_category_id, views_count, is_pinned, is_locked, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_trending, created_at, updated_at, last_activity_at)
VALUES 
('Welcome to EnglishFlow Community! 🎉', 
'Welcome to our vibrant English learning community! This is a space where learners from all levels can connect, share experiences, and grow together. Please read our community guidelines and introduce yourself in the Introductions section. Happy learning!',
1, 'Admin System', 1, 1250, true, false, 145, 2, 143, 89, 34, 22, 223, false, NOW() - INTERVAL '60 days', NOW(), NOW() - INTERVAL '1 day'),

('New Feature: AI Writing Assistant Now Available', 
'We''re excited to announce our new AI-powered writing assistant! Get instant feedback on your essays, emails, and creative writing. Check it out in the Writing Practice section. Let us know what you think!',
1, 'Admin System', 1, 890, true, false, 98, 1, 97, 67, 21, 10, 129, false, NOW() - INTERVAL '15 days', NOW(), NOW() - INTERVAL '2 days');

-- Topics dans Introductions (sub_category_id = 2)
INSERT INTO topics (title, content, user_id, user_name, sub_category_id, views_count, is_pinned, is_locked, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_trending, created_at, updated_at, last_activity_at)
VALUES 
('Hello from Morocco! 🇲🇦', 
'Hi everyone! I''m Youssef from Casablanca. I''m a B1 level learner working towards B2. I love reading English novels and watching movies. Looking forward to connecting with you all!',
11, 'Youssef Idrissi', 2, 234, false, false, 23, 0, 23, 18, 3, 2, 30, false, NOW() - INTERVAL '45 days', NOW(), NOW() - INTERVAL '10 days'),

('Greetings from France! 🇫🇷', 
'Bonjour! I''m Sophie, currently living in Paris. I''m preparing for my IELTS exam and would love to find study partners. My goal is to score 7.5+. Anyone else preparing for IELTS?',
12, 'Sophie Martin', 2, 189, false, false, 19, 0, 19, 15, 2, 2, 25, false, NOW() - INTERVAL '30 days', NOW(), NOW() - INTERVAL '5 days'),

('New member from Egypt! 🇪🇬', 
'Hello everyone! I''m Ahmed from Cairo. I''m an English teacher looking to improve my own skills and learn new teaching methods. Excited to be part of this community!',
13, 'Ahmed Hassan', 2, 156, false, false, 16, 0, 16, 12, 2, 2, 22, false, NOW() - INTERVAL '20 days', NOW(), NOW() - INTERVAL '3 days');

-- Topics dans Grammar Help (sub_category_id = 4)
INSERT INTO topics (title, content, user_id, user_name, sub_category_id, views_count, is_pinned, is_locked, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_trending, has_accepted_answer, created_at, updated_at, last_activity_at)
VALUES 
('When to use Present Perfect vs Simple Past?', 
'I''m always confused about when to use present perfect and when to use simple past. Can someone explain the difference with examples? For instance: "I have seen that movie" vs "I saw that movie yesterday".',
14, 'Amina Chakir', 4, 567, false, false, 45, 2, 43, 28, 12, 5, 68, true, true, NOW() - INTERVAL '25 days', NOW(), NOW() - INTERVAL '1 hour'),

('Help with Conditional Sentences', 
'I need help understanding the different types of conditional sentences (zero, first, second, third). When do I use each one? Any tips for remembering them?',
15, 'Omar Fassi', 4, 423, false, false, 38, 1, 37, 24, 10, 4, 58, false, true, NOW() - INTERVAL '18 days', NOW(), NOW() - INTERVAL '3 hours'),

('Gerund vs Infinitive - The Ultimate Confusion!', 
'Why do we say "I enjoy swimming" but "I want to swim"? Is there a rule for when to use gerund (-ing) vs infinitive (to + verb)? This is driving me crazy!',
16, 'Leila Mansouri', 4, 789, false, false, 67, 3, 64, 42, 18, 7, 113, true, true, NOW() - INTERVAL '12 days', NOW(), NOW() - INTERVAL '30 minutes'),

('Prepositions: In, On, At for Time', 
'Can someone explain the difference between in, on, and at when talking about time? Like "in the morning", "on Monday", "at 3 PM". Are there any tricks to remember?',
17, 'Hamza Ziani', 4, 345, false, false, 29, 1, 28, 19, 7, 3, 42, false, true, NOW() - INTERVAL '8 days', NOW(), NOW() - INTERVAL '2 hours');

-- Topics dans Vocabulary Building (sub_category_id = 5)
INSERT INTO topics (title, content, user_id, user_name, sub_category_id, views_count, is_pinned, is_locked, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_trending, created_at, updated_at, last_activity_at)
VALUES 
('50 Essential Business English Phrases', 
'Here''s a list of 50 must-know phrases for business English: 1. "Let''s touch base" - Let''s communicate/meet 2. "Circle back" - Return to discuss later 3. "Think outside the box" - Be creative... [Share your favorites!]',
18, 'Sara Kadiri', 5, 892, false, false, 78, 2, 76, 52, 18, 8, 122, true, false, NOW() - INTERVAL '10 days', NOW(), NOW() - INTERVAL '45 minutes'),

('Idioms with Animals 🐱🐶🐘', 
'Let''s learn idioms with animals! I''ll start: "It''s raining cats and dogs" = raining heavily. "Let the cat out of the bag" = reveal a secret. Add yours!',
19, 'Zineb Alaoui', 5, 456, false, false, 41, 1, 40, 28, 9, 4, 59, false, false, NOW() - INTERVAL '7 days', NOW(), NOW() - INTERVAL '1 hour'),

('Confusing Word Pairs: Affect vs Effect', 
'Let''s discuss commonly confused words! Today: AFFECT (verb) vs EFFECT (noun). Affect = to influence. Effect = result/consequence. Example: "The weather affects my mood" vs "The effect was immediate". What other pairs confuse you?',
20, 'Adam Berrada', 5, 678, false, false, 56, 2, 54, 38, 12, 6, 86, false, false, NOW() - INTERVAL '5 days', NOW(), NOW() - INTERVAL '2 hours');

-- Topics dans Writing Practice (sub_category_id = 7)
INSERT INTO topics (title, content, user_id, user_name, sub_category_id, views_count, is_pinned, is_locked, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_trending, created_at, updated_at, last_activity_at)
VALUES 
('My First Essay: Technology and Education', 
'Here''s my essay on how technology has changed education. Please give me feedback on grammar, structure, and ideas! [Essay content: Technology has revolutionized the way we learn. Online platforms, educational apps, and virtual classrooms have made education more accessible...]',
21, 'Meryem Senhaji', 7, 234, false, false, 21, 0, 21, 14, 5, 2, 33, false, false, NOW() - INTERVAL '6 days', NOW(), NOW() - INTERVAL '4 hours'),

('Daily Writing Challenge: Describe Your Dream Job', 
'Let''s practice descriptive writing! Describe your dream job in 150-200 words. Focus on using vivid adjectives and varied sentence structures. I''ll go first: My dream job would be working as a travel writer...',
22, 'Tarik Filali', 7, 345, false, false, 31, 1, 30, 21, 7, 3, 49, false, false, NOW() - INTERVAL '3 days', NOW(), NOW() - INTERVAL '6 hours');

-- Topics dans Book Club (sub_category_id = 9)
INSERT INTO topics (title, content, user_id, user_name, sub_category_id, views_count, is_pinned, is_locked, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_trending, created_at, updated_at, last_activity_at)
VALUES 
('Currently Reading: "To Kill a Mockingbird"', 
'Who else is reading this classic? Let''s discuss the themes of racial injustice and moral growth. What do you think about Atticus Finch as a character? No spoilers for those who haven''t finished!',
23, 'Hicham Amrani', 9, 567, false, false, 48, 1, 47, 32, 12, 5, 81, false, false, NOW() - INTERVAL '14 days', NOW(), NOW() - INTERVAL '8 hours'),

('Book Recommendation: "The Alchemist" by Paulo Coelho', 
'Just finished this beautiful book! It''s perfect for intermediate learners - simple language but profound messages. The story follows a shepherd boy on his journey to find treasure. Highly recommend!',
24, 'Samira El Idrissi', 9, 423, false, false, 39, 0, 39, 27, 9, 4, 63, false, false, NOW() - INTERVAL '9 days', NOW(), NOW() - INTERVAL '12 hours');

-- Topics dans Study Materials (sub_category_id = 13)
INSERT INTO topics (title, content, user_id, user_name, sub_category_id, views_count, is_pinned, is_locked, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, resource_type, resource_link, is_trending, created_at, updated_at, last_activity_at)
VALUES 
('Free Grammar Workbook PDF', 
'I found this excellent grammar workbook with exercises and answer keys. Perfect for self-study! Covers all levels from A1 to C1.',
25, 'Bilal Tazi', 13, 1234, false, false, 112, 3, 109, 76, 28, 14, 174, true, 'PDF', 'https://www.perfect-english-grammar.com/grammar-book.pdf', true, NOW() - INTERVAL '20 days', NOW(), NOW() - INTERVAL '30 minutes'),

('IELTS Writing Task 2 Sample Essays', 
'Collection of 50 high-scoring IELTS Writing Task 2 essays with examiner comments. Great for understanding what examiners look for!',
26, 'Khadija Bennani', 13, 890, false, false, 87, 2, 85, 59, 21, 11, 143, false, 'LINK', 'https://www.ielts-exam.net/sample-essays/', false, NOW() - INTERVAL '15 days', NOW(), NOW() - INTERVAL '1 hour');

-- Topics dans Exam Preparation (sub_category_id = 18)
INSERT INTO topics (title, content, user_id, user_name, sub_category_id, views_count, is_pinned, is_locked, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_trending, has_accepted_answer, created_at, updated_at, last_activity_at)
VALUES 
('IELTS Speaking Part 2: Describe a Person You Admire', 
'I have my IELTS exam next week and I''m nervous about Speaking Part 2. Can someone share tips for the "Describe a person you admire" topic? How do I structure my answer?',
27, 'Ayoub Mouhib', 18, 678, false, false, 58, 2, 56, 39, 14, 7, 102, true, true, NOW() - INTERVAL '4 days', NOW(), NOW() - INTERVAL '2 hours'),

('TOEFL Reading: Time Management Strategies', 
'How do you manage time in TOEFL Reading section? I always run out of time on the last passage. Any tips for reading faster while maintaining comprehension?',
28, 'Dounia Lahlou', 18, 456, false, false, 42, 1, 41, 29, 10, 5, 74, false, true, NOW() - INTERVAL '6 days', NOW(), NOW() - INTERVAL '5 hours');

-- =====================================================
-- POSTS - Réponses aux topics
-- =====================================================

-- Réponses au topic "When to use Present Perfect vs Simple Past?" (topic_id = 4)
INSERT INTO posts (content, user_id, user_name, topic_id, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_accepted, created_at, updated_at)
VALUES 
('Great question! The key difference is: Present Perfect connects past to present, Simple Past is finished. Use Present Perfect when: 1) Time is not specified ("I have visited Paris") 2) Action affects now ("I have lost my keys" - still lost). Use Simple Past when time is specified ("I visited Paris in 2020").',
6, 'James Wilson', 4, 42, 1, 41, 28, 11, 5, 72, true, NOW() - INTERVAL '25 days', NOW()),

('To add to the previous answer: Present Perfect often uses "already", "yet", "just", "ever", "never". Simple Past uses "yesterday", "last week", "in 2020", "ago". Example: "Have you ever been to London?" vs "Did you go to London last year?"',
7, 'Emily Brown', 4, 35, 0, 35, 24, 8, 4, 56, false, NOW() - INTERVAL '24 days', NOW()),

('Think of it this way: Present Perfect = bridge between past and present. Simple Past = completely in the past, no connection to now. This helped me a lot!',
11, 'Youssef Idrissi', 4, 18, 0, 18, 14, 3, 2, 29, false, NOW() - INTERVAL '23 days', NOW());

-- Réponses au topic "Gerund vs Infinitive" (topic_id = 6)
INSERT INTO posts (content, user_id, user_name, topic_id, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_accepted, created_at, updated_at)
VALUES 
('There are some patterns: Verbs of LIKING use gerund (enjoy, love, hate, prefer + -ing). Verbs of WANTING use infinitive (want, need, hope, plan + to). Some verbs can use both but meaning changes: "I stopped smoking" (quit) vs "I stopped to smoke" (paused to smoke).',
8, 'Karim Idrissi', 6, 64, 2, 62, 41, 17, 8, 115, true, NOW() - INTERVAL '12 days', NOW()),

('Here''s a helpful list: GERUND: enjoy, finish, mind, avoid, suggest, practice. INFINITIVE: want, need, decide, hope, plan, promise, agree. BOTH: like, love, hate, prefer, start, begin, continue.',
9, 'Sophia Martin', 6, 52, 1, 51, 35, 13, 6, 93, false, NOW() - INTERVAL '11 days', NOW());

-- Réponses au topic "50 Essential Business English Phrases" (topic_id = 10)
INSERT INTO posts (content, user_id, user_name, topic_id, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, created_at, updated_at)
VALUES 
('Great list! I''d add: "Keep me in the loop" = Keep me informed, "On the same page" = In agreement, "Move the needle" = Make progress, "Low-hanging fruit" = Easy wins',
15, 'Omar Fassi', 10, 38, 0, 38, 26, 9, 4, 65, NOW() - INTERVAL '10 days', NOW()),

('These are gold! Also useful: "Ballpark figure" = Rough estimate, "Get the ball rolling" = Start something, "Back to the drawing board" = Start over, "Win-win situation" = Good for everyone',
18, 'Sara Kadiri', 10, 31, 0, 31, 22, 7, 3, 50, NOW() - INTERVAL '9 days', NOW());

-- Réponses au topic "IELTS Speaking Part 2" (topic_id = 19)
INSERT INTO posts (content, user_id, user_name, topic_id, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_accepted, created_at, updated_at)
VALUES 
('Structure your answer like this: 1) WHO (introduce the person) 2) WHY you admire them (qualities, achievements) 3) HOW they influenced you 4) Conclusion. Use past tense for stories, present for current qualities. Aim for 2 minutes. Practice with a timer!',
6, 'James Wilson', 19, 56, 1, 55, 38, 13, 7, 103, true, NOW() - INTERVAL '4 days', NOW()),

('Add specific examples and details! Don''t just say "She is kind" - say "She volunteers at a local shelter every weekend and has helped over 100 families". Examiners love specific details!',
7, 'Emily Brown', 19, 41, 0, 41, 29, 9, 5, 74, false, NOW() - INTERVAL '3 days', NOW());

-- Réactiver les contraintes de clés étrangères (PostgreSQL)
SET session_replication_role = 'origin';

-- =====================================================
-- Résumé des insertions
-- =====================================================
-- Total insertions:
-- - 5 Categories
-- - 20 Sub-categories
-- - 20 Topics (variés: questions, discussions, ressources)
-- - 10 Posts (réponses aux topics)
-- 
-- Statistiques:
-- - Topics avec réponses acceptées: 6
-- - Topics trending: 4
-- - Topics avec ressources: 2
-- - Vues totales: ~10,000+
-- - Engagement élevé avec upvotes/reactions
-- =====================================================

SELECT 'Données community insérées avec succès!' AS message;
SELECT 'Categories:' as type, COUNT(*) as count FROM categories
UNION ALL
SELECT 'Sub-categories:', COUNT(*) FROM sub_categories
UNION ALL
SELECT 'Topics:', COUNT(*) FROM topics
UNION ALL
SELECT 'Posts:', COUNT(*) FROM posts;

-- Statistiques des topics
SELECT 
    'Topics Statistics' as info,
    COUNT(*) as total_topics,
    SUM(views_count) as total_views,
    AVG(views_count)::int as avg_views,
    SUM(upvotes) as total_upvotes,
    COUNT(CASE WHEN is_trending THEN 1 END) as trending_topics,
    COUNT(CASE WHEN has_accepted_answer THEN 1 END) as solved_questions
FROM topics;
