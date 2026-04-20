-- =====================================================
-- Script: Link Quizzes to Course Lessons
-- Database: englishflow_courses
-- Description: Create QUIZ lessons that link to quizzes
-- =====================================================

-- Link quizzes to lessons (quiz IDs 1-10 from learning_db)
INSERT INTO lessons (title, description, content, lesson_type, chapter_id, order_index, duration, is_preview, is_published, quiz_id, created_at, updated_at)
VALUES 
-- Course 49: Grammar
('Parts of Speech Quiz', 'Test your knowledge of nouns, verbs, adjectives, and adverbs', 'Complete this quiz to assess your understanding of the basic parts of speech.', 'QUIZ', 141, 99, 15, false, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Present Tenses Quiz', 'Master present simple and present continuous', 'Test your understanding of present tenses.', 'QUIZ', 142, 99, 20, false, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Course 50: Business
('Professional Email Writing Quiz', 'Test your business email skills', 'Assess your business email writing abilities.', 'QUIZ', 145, 99, 25, false, true, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Business Presentations Quiz', 'Master presentation language', 'Test your presentation skills.', 'QUIZ', 146, 99, 20, false, true, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Course 51: Pronunciation
('English Phonetics Quiz', 'Test your pronunciation knowledge', 'Assess your phonetics understanding.', 'QUIZ', 149, 99, 15, false, true, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Intonation Patterns Quiz', 'Master rising and falling intonation', 'Test your intonation knowledge.', 'QUIZ', 152, 99, 15, false, true, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Course 52: Vocabulary
('English Idioms Quiz', 'Test your idiom knowledge', 'Assess your understanding of idioms.', 'QUIZ', 154, 99, 20, false, true, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Academic Vocabulary Quiz', 'Master advanced vocabulary', 'Test your academic vocabulary.', 'QUIZ', 153, 99, 25, false, true, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Course 53: Conversation
('Daily Conversations Quiz', 'Practice everyday phrases', 'Test your daily conversation skills.', 'QUIZ', 157, 99, 15, false, true, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Social Situations Quiz', 'Navigate social contexts', 'Assess your social situation handling.', 'QUIZ', 160, 99, 15, false, true, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Summary
DO $$
BEGIN
    RAISE NOTICE '';
    RAISE NOTICE '=== QUIZ LESSONS LINKED ===';
    RAISE NOTICE 'Created 10 quiz lessons in courses';
    RAISE NOTICE 'All quizzes are now visible to tutor ID 13';
    RAISE NOTICE 'Setup complete!';
END $$;
