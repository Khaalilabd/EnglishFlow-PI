-- =====================================================
-- Script: Create Quizzes in Learning Database
-- Database: englishflow_learning_db
-- =====================================================

-- COURSE 49: English Grammar Fundamentals
DO $$
DECLARE v_quiz_id BIGINT;
BEGIN
    INSERT INTO quiz (title, description, course_id, passing_score, duration_minutes, published, created_at, updated_at)
    VALUES ('Parts of Speech Quiz', 'Test your knowledge of nouns, verbs, adjectives, and adverbs', 49, 70, 15, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    RETURNING id INTO v_quiz_id;
    
    INSERT INTO question (quiz_id, content, type, points, order_index) VALUES
    (v_quiz_id, 'Which word is a NOUN: "The cat sleeps on the mat"?', 'MCQ', 10, 1),
    (v_quiz_id, 'Identify the VERB: "She quickly runs to school"', 'MCQ', 10, 2),
    (v_quiz_id, 'Which word is an ADJECTIVE: "The beautiful garden"?', 'MCQ', 10, 3),
    (v_quiz_id, 'Find the ADVERB: "He speaks English fluently"', 'MCQ', 10, 4),
    (v_quiz_id, 'A noun is a word that describes an action.', 'TRUE_FALSE', 10, 5),
    (v_quiz_id, 'Adjectives describe nouns.', 'TRUE_FALSE', 10, 6),
    (v_quiz_id, 'Adverbs can modify verbs, adjectives, and other adverbs.', 'TRUE_FALSE', 10, 7),
    (v_quiz_id, 'Write a sentence using a noun, a verb, and an adjective.', 'OPEN', 15, 8);
    
    RAISE NOTICE 'Created Quiz ID %: Parts of Speech', v_quiz_id;
END $$;

DO $$
DECLARE v_quiz_id BIGINT;
BEGIN
    INSERT INTO quiz (title, description, course_id, passing_score, duration_minutes, published, created_at, updated_at)
    VALUES ('Present Tenses Quiz', 'Master present simple and present continuous', 49, 70, 20, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    RETURNING id INTO v_quiz_id;
    
    INSERT INTO question (quiz_id, content, type, points, order_index) VALUES
    (v_quiz_id, 'Choose correct form: "She ___ to work every day."', 'MCQ', 10, 1),
    (v_quiz_id, 'Complete: "They ___ (play) football right now."', 'MCQ', 10, 2),
    (v_quiz_id, 'Select present simple: "I ___ coffee every morning."', 'MCQ', 10, 3),
    (v_quiz_id, 'Which is correct: "He is knowing" or "He knows"?', 'MCQ', 10, 4),
    (v_quiz_id, 'Present simple is used for habits and routines.', 'TRUE_FALSE', 10, 5),
    (v_quiz_id, 'Present continuous describes actions happening now.', 'TRUE_FALSE', 10, 6),
    (v_quiz_id, 'Write 3 sentences using present simple tense.', 'OPEN', 15, 7);
    
    RAISE NOTICE 'Created Quiz ID %: Present Tenses', v_quiz_id;
END $$;

-- COURSE 50: Business English Communication
DO $$
DECLARE v_quiz_id BIGINT;
BEGIN
    INSERT INTO quiz (title, description, course_id, passing_score, duration_minutes, published, created_at, updated_at)
    VALUES ('Professional Email Writing Quiz', 'Test your business email skills', 50, 75, 25, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    RETURNING id INTO v_quiz_id;
    
    INSERT INTO question (quiz_id, content, type, points, order_index) VALUES
    (v_quiz_id, 'Which is the most professional email greeting?', 'MCQ', 10, 1),
    (v_quiz_id, 'How should you close a formal business email?', 'MCQ', 10, 2),
    (v_quiz_id, 'Which phrase is appropriate for requesting information?', 'MCQ', 10, 3),
    (v_quiz_id, 'Using "Dear Sir/Madam" is appropriate when you don''t know the recipient''s name.', 'TRUE_FALSE', 10, 4),
    (v_quiz_id, 'It''s professional to use emojis in business emails.', 'TRUE_FALSE', 10, 5),
    (v_quiz_id, 'You should always proofread your email before sending.', 'TRUE_FALSE', 10, 6),
    (v_quiz_id, 'Write a professional email requesting a meeting.', 'OPEN', 20, 7);
    
    RAISE NOTICE 'Created Quiz ID %: Professional Email Writing', v_quiz_id;
END $$;

DO $$
DECLARE v_quiz_id BIGINT;
BEGIN
    INSERT INTO quiz (title, description, course_id, passing_score, duration_minutes, published, created_at, updated_at)
    VALUES ('Business Presentations Quiz', 'Master presentation language', 50, 75, 20, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    RETURNING id INTO v_quiz_id;
    
    INSERT INTO question (quiz_id, content, type, points, order_index) VALUES
    (v_quiz_id, 'Which phrase is best for introducing your presentation?', 'MCQ', 10, 1),
    (v_quiz_id, 'How do you transition to the next slide professionally?', 'MCQ', 10, 2),
    (v_quiz_id, 'Which phrase invites questions from the audience?', 'MCQ', 10, 3),
    (v_quiz_id, 'Eye contact with the audience is important.', 'TRUE_FALSE', 10, 4),
    (v_quiz_id, 'Reading directly from slides is good technique.', 'TRUE_FALSE', 10, 5),
    (v_quiz_id, 'List 5 phrases to introduce a presentation topic.', 'OPEN', 20, 6);
    
    RAISE NOTICE 'Created Quiz ID %: Business Presentations', v_quiz_id;
END $$;

-- COURSE 51: English Pronunciation Mastery
DO $$
DECLARE v_quiz_id BIGINT;
BEGIN
    INSERT INTO quiz (title, description, course_id, passing_score, duration_minutes, published, created_at, updated_at)
    VALUES ('English Phonetics Quiz', 'Test your pronunciation knowledge', 51, 70, 15, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    RETURNING id INTO v_quiz_id;
    
    INSERT INTO question (quiz_id, content, type, points, order_index) VALUES
    (v_quiz_id, 'Which word has different vowel: "cat", "bat", "beat", "hat"?', 'MCQ', 10, 1),
    (v_quiz_id, 'Identify the word with /θ/ sound (think)', 'MCQ', 10, 2),
    (v_quiz_id, 'Which word is stressed on second syllable?', 'MCQ', 10, 3),
    (v_quiz_id, 'English has more vowel sounds than vowel letters.', 'TRUE_FALSE', 10, 4),
    (v_quiz_id, 'Word stress can change the meaning of a word.', 'TRUE_FALSE', 10, 5),
    (v_quiz_id, 'List 5 words with the /ð/ sound (as in "this").', 'OPEN', 20, 6);
    
    RAISE NOTICE 'Created Quiz ID %: Phonetics', v_quiz_id;
END $$;

DO $$
DECLARE v_quiz_id BIGINT;
BEGIN
    INSERT INTO quiz (title, description, course_id, passing_score, duration_minutes, published, created_at, updated_at)
    VALUES ('Intonation Patterns Quiz', 'Master rising and falling intonation', 51, 70, 15, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    RETURNING id INTO v_quiz_id;
    
    INSERT INTO question (quiz_id, content, type, points, order_index) VALUES
    (v_quiz_id, 'Which question uses rising intonation?', 'MCQ', 10, 1),
    (v_quiz_id, 'Falling intonation is typically used for:', 'MCQ', 10, 2),
    (v_quiz_id, 'In a list, which item has falling intonation?', 'MCQ', 10, 3),
    (v_quiz_id, 'Yes/No questions use rising intonation.', 'TRUE_FALSE', 10, 4),
    (v_quiz_id, 'Wh-questions use falling intonation.', 'TRUE_FALSE', 10, 5),
    (v_quiz_id, 'Give 3 examples of questions with rising intonation.', 'OPEN', 20, 6);
    
    RAISE NOTICE 'Created Quiz ID %: Intonation', v_quiz_id;
END $$;

-- COURSE 52: Advanced Vocabulary Builder
DO $$
DECLARE v_quiz_id BIGINT;
BEGIN
    INSERT INTO quiz (title, description, course_id, passing_score, duration_minutes, published, created_at, updated_at)
    VALUES ('English Idioms Quiz', 'Test your idiom knowledge', 52, 75, 20, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    RETURNING id INTO v_quiz_id;
    
    INSERT INTO question (quiz_id, content, type, points, order_index) VALUES
    (v_quiz_id, 'What does "break the ice" mean?', 'MCQ', 10, 1),
    (v_quiz_id, 'If something "costs an arm and a leg", it is:', 'MCQ', 10, 2),
    (v_quiz_id, '"Piece of cake" means:', 'MCQ', 10, 3),
    (v_quiz_id, 'Idioms should be translated word-for-word.', 'TRUE_FALSE', 10, 4),
    (v_quiz_id, 'Native speakers use idioms frequently.', 'TRUE_FALSE', 10, 5),
    (v_quiz_id, 'Write a dialogue using at least 3 idioms.', 'OPEN', 20, 6);
    
    RAISE NOTICE 'Created Quiz ID %: Idioms', v_quiz_id;
END $$;

DO $$
DECLARE v_quiz_id BIGINT;
BEGIN
    INSERT INTO quiz (title, description, course_id, passing_score, duration_minutes, published, created_at, updated_at)
    VALUES ('Academic Vocabulary Quiz', 'Master advanced vocabulary', 52, 75, 25, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    RETURNING id INTO v_quiz_id;
    
    INSERT INTO question (quiz_id, content, type, points, order_index) VALUES
    (v_quiz_id, 'Which word means "to make less severe"?', 'MCQ', 10, 1),
    (v_quiz_id, 'Select synonym for "ubiquitous":', 'MCQ', 10, 2),
    (v_quiz_id, 'What does "paradigm" mean in academic writing?', 'MCQ', 10, 3),
    (v_quiz_id, 'Academic vocabulary is more formal than everyday language.', 'TRUE_FALSE', 10, 4),
    (v_quiz_id, 'Using complex vocabulary always improves writing.', 'TRUE_FALSE', 10, 5),
    (v_quiz_id, 'Write a paragraph using: analyze, significant, demonstrate.', 'OPEN', 20, 6);
    
    RAISE NOTICE 'Created Quiz ID %: Academic Vocabulary', v_quiz_id;
END $$;

-- COURSE 53: Conversational English Practice
DO $$
DECLARE v_quiz_id BIGINT;
BEGIN
    INSERT INTO quiz (title, description, course_id, passing_score, duration_minutes, published, created_at, updated_at)
    VALUES ('Daily Conversations Quiz', 'Practice everyday phrases', 53, 70, 15, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    RETURNING id INTO v_quiz_id;
    
    INSERT INTO question (quiz_id, content, type, points, order_index) VALUES
    (v_quiz_id, 'How do you greet someone in the morning?', 'MCQ', 10, 1),
    (v_quiz_id, 'Which response is appropriate for "How are you?"', 'MCQ', 10, 2),
    (v_quiz_id, 'How do you politely ask someone to repeat?', 'MCQ', 10, 3),
    (v_quiz_id, '"Nice to meet you" is for first meetings.', 'TRUE_FALSE', 10, 4),
    (v_quiz_id, 'Small talk is important in English conversation.', 'TRUE_FALSE', 10, 5),
    (v_quiz_id, 'Write a conversation between two people meeting.', 'OPEN', 20, 6);
    
    RAISE NOTICE 'Created Quiz ID %: Daily Conversations', v_quiz_id;
END $$;

DO $$
DECLARE v_quiz_id BIGINT;
BEGIN
    INSERT INTO quiz (title, description, course_id, passing_score, duration_minutes, published, created_at, updated_at)
    VALUES ('Social Situations Quiz', 'Navigate social contexts', 53, 70, 15, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    RETURNING id INTO v_quiz_id;
    
    INSERT INTO question (quiz_id, content, type, points, order_index) VALUES
    (v_quiz_id, 'How do you politely decline an invitation?', 'MCQ', 10, 1),
    (v_quiz_id, 'Which phrase is appropriate for making an apology?', 'MCQ', 10, 2),
    (v_quiz_id, 'How do you ask for permission politely?', 'MCQ', 10, 3),
    (v_quiz_id, 'Using "please" and "thank you" is important.', 'TRUE_FALSE', 10, 4),
    (v_quiz_id, 'Body language is not important in conversation.', 'TRUE_FALSE', 10, 5),
    (v_quiz_id, 'Write a polite way to disagree with someone.', 'OPEN', 20, 6);
    
    RAISE NOTICE 'Created Quiz ID %: Social Situations', v_quiz_id;
END $$;

DO $$
BEGIN
    RAISE NOTICE '';
    RAISE NOTICE '=== QUIZZES CREATED IN LEARNING DB ===';
    RAISE NOTICE 'Total: 10 quizzes with questions';
    RAISE NOTICE 'Next: Run 2-link-quizzes-to-courses.sql';
END $$;
