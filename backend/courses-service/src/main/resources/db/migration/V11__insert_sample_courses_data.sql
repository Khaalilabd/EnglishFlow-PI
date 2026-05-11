-- V11: Insert sample courses with chapters and lessons
-- This migration adds realistic course data with multiple chapters and various lesson types

-- Insert sample courses and get their IDs
DO $$
DECLARE
    course_grammar_id BIGINT;
    course_business_id BIGINT;
    course_pronunciation_id BIGINT;
    course_vocabulary_id BIGINT;
    course_conversation_id BIGINT;
    
    chapter_grammar_1_id BIGINT;
    chapter_grammar_2_id BIGINT;
    chapter_grammar_3_id BIGINT;
    chapter_grammar_4_id BIGINT;
    
    chapter_business_1_id BIGINT;
    chapter_business_2_id BIGINT;
    chapter_business_3_id BIGINT;
    chapter_business_4_id BIGINT;
    
    chapter_pronunciation_1_id BIGINT;
    chapter_pronunciation_2_id BIGINT;
    chapter_pronunciation_3_id BIGINT;
    chapter_pronunciation_4_id BIGINT;
    
    chapter_vocabulary_1_id BIGINT;
    chapter_vocabulary_2_id BIGINT;
    chapter_vocabulary_3_id BIGINT;
    chapter_vocabulary_4_id BIGINT;
    
    chapter_conversation_1_id BIGINT;
    chapter_conversation_2_id BIGINT;
    chapter_conversation_3_id BIGINT;
    chapter_conversation_4_id BIGINT;
BEGIN
    -- Insert Course 1: English Grammar Fundamentals
    INSERT INTO courses (title, description, category, level, max_students, duration, tutor_id, price, thumbnail_url, objectives, prerequisites, is_featured, status, created_at, updated_at)
    VALUES ('English Grammar Fundamentals', 
            'Master the basics of English grammar with clear explanations and practical exercises. Perfect for beginners starting their English learning journey.',
            'Grammar', 'A1', 30, 40, 53, 99.99,
            '/uploads/courses/thumbnails/grammar-fundamentals.jpg',
            'Understand basic sentence structure, Learn present and past tenses, Master articles and prepositions, Build confidence in grammar usage',
            'No prior English knowledge required',
            true, 'PUBLISHED', NOW(), NOW())
    RETURNING id INTO course_grammar_id;
    
    -- Insert Course 2: Business English Communication
    INSERT INTO courses (title, description, category, level, max_students, duration, tutor_id, price, thumbnail_url, objectives, prerequisites, is_featured, status, created_at, updated_at)
    VALUES ('Business English Communication',
            'Enhance your professional English skills for the workplace. Learn business vocabulary, email writing, presentations, and meeting etiquette.',
            'Business English', 'B2', 25, 50, 53, 149.99,
            '/uploads/courses/thumbnails/business-english.jpg',
            'Write professional emails, Conduct business meetings, Deliver presentations, Negotiate effectively',
            'Intermediate English level (B1 or higher)',
            true, 'PUBLISHED', NOW(), NOW())
    RETURNING id INTO course_business_id;
    
    -- Insert Course 3: English Pronunciation Mastery
    INSERT INTO courses (title, description, category, level, max_students, duration, tutor_id, price, thumbnail_url, objectives, prerequisites, is_featured, status, created_at, updated_at)
    VALUES ('English Pronunciation Mastery',
            'Improve your English pronunciation and accent. Learn phonetics, stress patterns, and intonation to speak more clearly and confidently.',
            'Pronunciation', 'B1', 20, 30, 53, 79.99,
            '/uploads/courses/thumbnails/pronunciation-mastery.jpg',
            'Master English phonetics, Improve accent and clarity, Learn stress and intonation, Practice with native speakers',
            'Basic English speaking ability',
            false, 'PUBLISHED', NOW(), NOW())
    RETURNING id INTO course_pronunciation_id;
    
    -- Insert Course 4: Advanced Vocabulary Builder
    INSERT INTO courses (title, description, category, level, max_students, duration, tutor_id, price, thumbnail_url, objectives, prerequisites, is_featured, status, created_at, updated_at)
    VALUES ('Advanced Vocabulary Builder',
            'Expand your English vocabulary with advanced words, idioms, and expressions. Perfect for those aiming for fluency.',
            'Vocabulary', 'C1', 30, 35, 53, 119.99,
            '/uploads/courses/thumbnails/vocabulary-builder.jpg',
            'Learn 1000+ advanced words, Master idioms and phrasal verbs, Understand context and usage, Improve reading comprehension',
            'Upper-intermediate English (B2)',
            true, 'PUBLISHED', NOW(), NOW())
    RETURNING id INTO course_vocabulary_id;
    
    -- Insert Course 5: Conversational English Practice
    INSERT INTO courses (title, description, category, level, max_students, duration, tutor_id, price, thumbnail_url, objectives, prerequisites, is_featured, status, created_at, updated_at)
    VALUES ('Conversational English Practice',
            'Practice everyday English conversations in real-life situations. Build confidence speaking English naturally.',
            'Conversation', 'A2', 15, 25, 53, 89.99,
            '/uploads/courses/thumbnails/conversational-english.jpg',
            'Speak confidently in daily situations, Understand native speakers, Build conversational vocabulary, Practice listening skills',
            'Basic English knowledge (A1)',
            false, 'PUBLISHED', NOW(), NOW())
    RETURNING id INTO course_conversation_id;
    
    -- Insert chapters for Grammar course
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Introduction to English Grammar', 
            'Learn the fundamental building blocks of English grammar including parts of speech and sentence structure.',
            0, 180, true, course_grammar_id, NOW(), NOW())
    RETURNING id INTO chapter_grammar_1_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Present Tenses', 
            'Master the present simple and present continuous tenses with practical examples and exercises.',
            1, 240, true, course_grammar_id, NOW(), NOW())
    RETURNING id INTO chapter_grammar_2_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Past Tenses', 
            'Learn how to talk about past events using past simple and past continuous tenses.',
            2, 240, true, course_grammar_id, NOW(), NOW())
    RETURNING id INTO chapter_grammar_3_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Articles and Determiners', 
            'Understand when and how to use articles (a, an, the) and other determiners in English.',
            3, 180, true, course_grammar_id, NOW(), NOW())
    RETURNING id INTO chapter_grammar_4_id;
    
    -- Insert chapters for Business English course
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Professional Email Writing', 
            'Learn to write clear, professional emails for various business situations.',
            0, 300, true, course_business_id, NOW(), NOW())
    RETURNING id INTO chapter_business_1_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Business Meetings and Presentations', 
            'Master the language and skills needed for effective meetings and presentations.',
            1, 360, true, course_business_id, NOW(), NOW())
    RETURNING id INTO chapter_business_2_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Negotiations and Persuasion', 
            'Develop negotiation skills and learn persuasive language for business contexts.',
            2, 300, true, course_business_id, NOW(), NOW())
    RETURNING id INTO chapter_business_3_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Business Networking', 
            'Build your professional network with effective communication strategies.',
            3, 240, true, course_business_id, NOW(), NOW())
    RETURNING id INTO chapter_business_4_id;
    
    -- Insert chapters for Pronunciation course
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('English Phonetics Basics', 
            'Introduction to English sounds, the phonetic alphabet, and pronunciation fundamentals.',
            0, 180, true, course_pronunciation_id, NOW(), NOW())
    RETURNING id INTO chapter_pronunciation_1_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Vowel Sounds', 
            'Master all English vowel sounds including short, long, and diphthongs.',
            1, 240, true, course_pronunciation_id, NOW(), NOW())
    RETURNING id INTO chapter_pronunciation_2_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Consonant Sounds', 
            'Learn to produce all English consonant sounds accurately.',
            2, 240, true, course_pronunciation_id, NOW(), NOW())
    RETURNING id INTO chapter_pronunciation_3_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Stress and Intonation', 
            'Understand word stress, sentence stress, and intonation patterns in English.',
            3, 180, true, course_pronunciation_id, NOW(), NOW())
    RETURNING id INTO chapter_pronunciation_4_id;
    
    -- Insert chapters for Vocabulary course
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Academic Vocabulary', 
            'Learn essential academic words and expressions for formal contexts.',
            0, 240, true, course_vocabulary_id, NOW(), NOW())
    RETURNING id INTO chapter_vocabulary_1_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Idioms and Expressions', 
            'Master common English idioms and colloquial expressions.',
            1, 300, true, course_vocabulary_id, NOW(), NOW())
    RETURNING id INTO chapter_vocabulary_2_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Phrasal Verbs', 
            'Learn the most important phrasal verbs and how to use them correctly.',
            2, 300, true, course_vocabulary_id, NOW(), NOW())
    RETURNING id INTO chapter_vocabulary_3_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Advanced Collocations', 
            'Discover word combinations that native speakers use naturally.',
            3, 240, true, course_vocabulary_id, NOW(), NOW())
    RETURNING id INTO chapter_vocabulary_4_id;
    
    -- Insert chapters for Conversation course
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Everyday Conversations', 
            'Practice common daily conversations and small talk.',
            0, 180, true, course_conversation_id, NOW(), NOW())
    RETURNING id INTO chapter_conversation_1_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Shopping and Services', 
            'Learn English for shopping, restaurants, and using services.',
            1, 180, true, course_conversation_id, NOW(), NOW())
    RETURNING id INTO chapter_conversation_2_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Travel and Transportation', 
            'Master English for traveling, asking directions, and using transportation.',
            2, 180, true, course_conversation_id, NOW(), NOW())
    RETURNING id INTO chapter_conversation_3_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Social Situations', 
            'Learn to navigate social events, make friends, and socialize in English.',
            3, 180, true, course_conversation_id, NOW(), NOW())
    RETURNING id INTO chapter_conversation_4_id;
    
    -- Insert chapter objectives
    INSERT INTO chapter_objectives (chapter_id, objective) VALUES
    (chapter_grammar_1_id, 'Identify parts of speech'),
    (chapter_grammar_1_id, 'Understand sentence components'),
    (chapter_grammar_1_id, 'Recognize basic grammar patterns'),
    (chapter_grammar_2_id, 'Use present simple correctly'),
    (chapter_grammar_2_id, 'Apply present continuous'),
    (chapter_grammar_2_id, 'Distinguish between tenses'),
    (chapter_grammar_3_id, 'Form past simple sentences'),
    (chapter_grammar_3_id, 'Use past continuous'),
    (chapter_grammar_3_id, 'Tell stories in the past'),
    (chapter_grammar_4_id, 'Use articles correctly'),
    (chapter_grammar_4_id, 'Master determiners'),
    (chapter_grammar_4_id, 'Avoid common mistakes'),
    
    (chapter_business_1_id, 'Write formal emails'),
    (chapter_business_1_id, 'Use appropriate tone'),
    (chapter_business_1_id, 'Structure business correspondence'),
    (chapter_business_2_id, 'Lead meetings effectively'),
    (chapter_business_2_id, 'Deliver presentations'),
    (chapter_business_2_id, 'Use business vocabulary'),
    (chapter_business_3_id, 'Negotiate successfully'),
    (chapter_business_3_id, 'Use persuasive techniques'),
    (chapter_business_3_id, 'Handle objections'),
    (chapter_business_4_id, 'Make small talk'),
    (chapter_business_4_id, 'Network professionally'),
    (chapter_business_4_id, 'Build relationships'),
    
    (chapter_pronunciation_1_id, 'Read phonetic symbols'),
    (chapter_pronunciation_1_id, 'Produce English sounds'),
    (chapter_pronunciation_1_id, 'Understand sound differences'),
    (chapter_pronunciation_2_id, 'Pronounce vowels correctly'),
    (chapter_pronunciation_2_id, 'Distinguish similar sounds'),
    (chapter_pronunciation_2_id, 'Practice with words'),
    (chapter_pronunciation_3_id, 'Master consonants'),
    (chapter_pronunciation_3_id, 'Handle difficult sounds'),
    (chapter_pronunciation_3_id, 'Improve clarity'),
    (chapter_pronunciation_4_id, 'Apply word stress'),
    (chapter_pronunciation_4_id, 'Use correct intonation'),
    (chapter_pronunciation_4_id, 'Sound more natural'),
    
    (chapter_vocabulary_1_id, 'Use academic language'),
    (chapter_vocabulary_1_id, 'Understand formal texts'),
    (chapter_vocabulary_1_id, 'Write academically'),
    (chapter_vocabulary_2_id, 'Understand idioms'),
    (chapter_vocabulary_2_id, 'Use expressions naturally'),
    (chapter_vocabulary_2_id, 'Recognize figurative language'),
    (chapter_vocabulary_3_id, 'Master phrasal verbs'),
    (chapter_vocabulary_3_id, 'Use them in context'),
    (chapter_vocabulary_3_id, 'Understand meanings'),
    (chapter_vocabulary_4_id, 'Use collocations'),
    (chapter_vocabulary_4_id, 'Sound more natural'),
    (chapter_vocabulary_4_id, 'Expand vocabulary'),
    
    (chapter_conversation_1_id, 'Make small talk'),
    (chapter_conversation_1_id, 'Have casual conversations'),
    (chapter_conversation_1_id, 'Build confidence'),
    (chapter_conversation_2_id, 'Order in restaurants'),
    (chapter_conversation_2_id, 'Shop confidently'),
    (chapter_conversation_2_id, 'Ask for help'),
    (chapter_conversation_3_id, 'Ask for directions'),
    (chapter_conversation_3_id, 'Book travel'),
    (chapter_conversation_3_id, 'Handle travel situations'),
    (chapter_conversation_4_id, 'Socialize confidently'),
    (chapter_conversation_4_id, 'Make invitations'),
    (chapter_conversation_4_id, 'Express opinions');
    
    -- Insert lessons for Grammar Chapter 1
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Welcome to English Grammar', 'An introduction to the course and what you will learn about English grammar.', 'Welcome to English Grammar Fundamentals! In this course, you will learn the essential building blocks of English grammar.', '', 'VIDEO', 0, 15, true, true, chapter_grammar_1_id, NOW(), NOW()),
    ('Parts of Speech Overview', 'Learn about the eight parts of speech in English.', 'The eight parts of speech are the categories that words belong to based on their function in a sentence.', '', 'VIDEO', 1, 25, false, true, chapter_grammar_1_id, NOW(), NOW()),
    ('Nouns and Pronouns', 'Understand nouns and pronouns.', 'Nouns are words that name people, places, things, or ideas. Pronouns are words that take the place of nouns.', '', 'DOCUMENT', 2, 20, false, true, chapter_grammar_1_id, NOW(), NOW()),
    ('Verbs and Tenses Introduction', 'Introduction to verbs and the concept of tenses in English.', 'Verbs are action words or state-of-being words.', '', 'VIDEO', 3, 30, false, true, chapter_grammar_1_id, NOW(), NOW()),
    ('Parts of Speech Quiz', 'Test your understanding of parts of speech.', 'Quiz covering nouns, pronouns, verbs, adjectives, and adverbs.', '', 'QUIZ', 4, 15, false, true, chapter_grammar_1_id, NOW(), NOW());
    
    -- Insert lessons for Grammar Chapter 2
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Present Simple Tense', 'Learn how to form and use the present simple tense.', 'The present simple tense is used for habits, routines, facts, and general truths.', '', 'VIDEO', 0, 30, false, true, chapter_grammar_2_id, NOW(), NOW()),
    ('Present Simple Practice', 'Interactive exercises to practice present simple tense.', 'Practice forming present simple sentences.', '', 'INTERACTIVE', 1, 25, false, true, chapter_grammar_2_id, NOW(), NOW()),
    ('Present Continuous Tense', 'Master the present continuous tense.', 'The present continuous describes actions happening right now.', '', 'VIDEO', 2, 30, false, true, chapter_grammar_2_id, NOW(), NOW()),
    ('Present Simple vs Continuous', 'Learn when to use present simple versus present continuous.', 'Understanding the difference between these two tenses is crucial.', '', 'DOCUMENT', 3, 20, false, true, chapter_grammar_2_id, NOW(), NOW()),
    ('Present Tenses Assignment', 'Complete exercises using both present tenses.', 'Write sentences using present simple and present continuous correctly.', '', 'ASSIGNMENT', 4, 30, false, true, chapter_grammar_2_id, NOW(), NOW());
    
    -- Insert lessons for Business Chapter 1
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Email Structure and Format', 'Learn the proper structure of professional business emails.', 'A professional email has a clear structure.', '', 'VIDEO', 0, 20, true, true, chapter_business_1_id, NOW(), NOW()),
    ('Formal vs Informal Tone', 'Understand when to use formal or informal language in emails.', 'The tone should match your relationship with the recipient.', '', 'VIDEO', 1, 25, false, true, chapter_business_1_id, NOW(), NOW()),
    ('Common Email Phrases', 'Learn useful phrases for different email situations.', 'Master phrases for opening, requesting, and closing emails.', '', 'DOCUMENT', 2, 15, false, true, chapter_business_1_id, NOW(), NOW()),
    ('Writing Practice: Request Email', 'Practice writing a professional request email.', 'Write an email requesting information or assistance.', '', 'ASSIGNMENT', 3, 30, false, true, chapter_business_1_id, NOW(), NOW()),
    ('Email Writing Quiz', 'Test your knowledge of professional email writing.', 'Quiz on email structure and tone.', '', 'QUIZ', 4, 15, false, true, chapter_business_1_id, NOW(), NOW());
    
    -- Insert lessons for Pronunciation Chapter 1
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Introduction to Phonetics', 'Learn what phonetics is and why it matters.', 'Phonetics is the study of speech sounds.', '', 'VIDEO', 0, 20, true, true, chapter_pronunciation_1_id, NOW(), NOW()),
    ('The International Phonetic Alphabet', 'Learn to read and use the IPA.', 'The IPA is a system of symbols that represent speech sounds.', '', 'VIDEO', 1, 30, false, true, chapter_pronunciation_1_id, NOW(), NOW()),
    ('English Sound System Overview', 'Overview of all English sounds.', 'English has approximately 44 sounds.', '', 'DOCUMENT', 2, 25, false, true, chapter_pronunciation_1_id, NOW(), NOW()),
    ('Pronunciation Practice Session', 'Live online session to practice sounds.', 'Interactive session with feedback.', '', 'ONLINE', 3, 45, false, true, chapter_pronunciation_1_id, NOW(), NOW());
    
    -- Insert lessons for Vocabulary Chapter 2
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('What Are Idioms?', 'Introduction to idiomatic expressions in English.', 'Idioms are expressions whose meaning cannot be understood from the individual words.', '', 'VIDEO', 0, 20, false, true, chapter_vocabulary_2_id, NOW(), NOW()),
    ('Common Everyday Idioms', 'Learn 50 frequently used idioms.', 'Master idioms like "piece of cake" and "break the ice".', '', 'VIDEO', 1, 35, false, true, chapter_vocabulary_2_id, NOW(), NOW()),
    ('Idioms in Context', 'See how idioms are used in real conversations.', 'Read dialogues that use idioms naturally.', '', 'TEXT', 2, 25, false, true, chapter_vocabulary_2_id, NOW(), NOW()),
    ('Idiom Practice Exercises', 'Interactive exercises to practice using idioms.', 'Complete sentences and match idioms to meanings.', '', 'INTERACTIVE', 3, 30, false, true, chapter_vocabulary_2_id, NOW(), NOW()),
    ('Idioms Quiz', 'Test your knowledge of English idioms.', 'Quiz covering meanings and usage.', '', 'QUIZ', 4, 20, false, true, chapter_vocabulary_2_id, NOW(), NOW());
    
    -- Insert lessons for Conversation Chapter 2
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Shopping Vocabulary', 'Learn essential vocabulary for shopping.', 'Master words and phrases for shopping.', '', 'VIDEO', 0, 20, false, true, chapter_conversation_2_id, NOW(), NOW()),
    ('At the Restaurant', 'Learn how to order food and drinks.', 'Practice making reservations and ordering.', '', 'VIDEO', 1, 25, false, true, chapter_conversation_2_id, NOW(), NOW()),
    ('Restaurant Role-Play', 'Practice restaurant conversations.', 'Interactive online session with role-play.', '', 'ONLINE', 2, 45, false, true, chapter_conversation_2_id, NOW(), NOW()),
    ('Using Services', 'Learn English for banks and post offices.', 'Practice asking for help and filling out forms.', '', 'DOCUMENT', 3, 20, false, true, chapter_conversation_2_id, NOW(), NOW()),
    ('Shopping Dialogue Assignment', 'Create and record a shopping dialogue.', 'Write a conversation between a customer and shop assistant.', '', 'ASSIGNMENT', 4, 30, false, true, chapter_conversation_2_id, NOW(), NOW());
    
    -- Insert course enrollments
    INSERT INTO course_enrollments (student_id, course_id, enrolled_at, completed_at, is_active, total_lessons, last_accessed_at) VALUES
    (10, course_grammar_id, NOW() - INTERVAL '7 days', NULL, true, 10, NOW() - INTERVAL '1 day'),
    (11, course_grammar_id, NOW() - INTERVAL '3 days', NULL, true, 10, NOW()),
    (12, course_business_id, NOW() - INTERVAL '10 days', NULL, true, 5, NOW() - INTERVAL '2 days'),
    (13, course_pronunciation_id, NOW() - INTERVAL '5 days', NULL, true, 4, NOW() - INTERVAL '1 day'),
    (14, course_vocabulary_id, NOW() - INTERVAL '15 days', NULL, true, 5, NOW() - INTERVAL '3 days'),
    (15, course_conversation_id, NOW() - INTERVAL '8 days', NULL, true, 5, NOW());
    
END $$;

COMMENT ON TABLE courses IS 'Main courses table with hierarchical structure: Course -> Chapter -> Lesson';
COMMENT ON TABLE chapters IS 'Course chapters for organizing lessons into logical sections';
COMMENT ON TABLE lessons IS 'Individual lessons with various types: VIDEO, TEXT, QUIZ, ASSIGNMENT, DOCUMENT, INTERACTIVE, ONLINE';
