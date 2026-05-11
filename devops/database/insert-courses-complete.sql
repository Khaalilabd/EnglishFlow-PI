-- Complete Course Data Insertion Script
-- This script inserts realistic course data with detailed content for all lesson types
-- All courses belong to tutor_id = 53 (khalilabdelmoumen11@gmail.com)

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
    -- ========================================
    -- COURSE 1: English Grammar Fundamentals (A1)
    -- ========================================
    INSERT INTO courses (title, description, category, level, max_students, duration, tutor_id, price, thumbnail_url, objectives, prerequisites, is_featured, status, created_at, updated_at)
    VALUES ('English Grammar Fundamentals', 
            'Master the basics of English grammar with clear explanations and practical exercises. This comprehensive course covers essential grammar topics including parts of speech, verb tenses, articles, and sentence structure. Perfect for beginners starting their English learning journey.',
            'Grammar', 'A1', 30, 40, 53, 99.99,
            '/uploads/courses/thumbnails/grammar-fundamentals.jpg',
            'Understand basic sentence structure, Learn present and past tenses, Master articles and prepositions, Build confidence in grammar usage, Recognize and use parts of speech correctly',
            'No prior English knowledge required. Basic literacy skills needed.',
            true, 'PUBLISHED', NOW(), NOW())
    RETURNING id INTO course_grammar_id;
    
    -- ========================================
    -- COURSE 2: Business English Communication (B2)
    -- ========================================
    INSERT INTO courses (title, description, category, level, max_students, duration, tutor_id, price, thumbnail_url, objectives, prerequisites, is_featured, status, created_at, updated_at)
    VALUES ('Business English Communication',
            'Enhance your professional English skills for the workplace. This course focuses on practical business communication including email writing, presentations, meetings, and negotiations. Learn the vocabulary and expressions used in modern business environments.',
            'Business English', 'B2', 25, 50, 53, 149.99,
            '/uploads/courses/thumbnails/business-english.jpg',
            'Write professional emails and reports, Conduct effective business meetings, Deliver confident presentations, Negotiate successfully, Use business vocabulary appropriately',
            'Intermediate English level (B1 or higher). Basic understanding of business concepts helpful but not required.',
            true, 'PUBLISHED', NOW(), NOW())
    RETURNING id INTO course_business_id;
    
    -- ========================================
    -- COURSE 3: English Pronunciation Mastery (B1)
    -- ========================================
    INSERT INTO courses (title, description, category, level, max_students, duration, tutor_id, price, thumbnail_url, objectives, prerequisites, is_featured, status, created_at, updated_at)
    VALUES ('English Pronunciation Mastery',
            'Improve your English pronunciation and accent with systematic phonetics training. Learn the International Phonetic Alphabet (IPA), master vowel and consonant sounds, and develop natural stress and intonation patterns. Includes live practice sessions with feedback.',
            'Pronunciation', 'B1', 20, 30, 53, 79.99,
            '/uploads/courses/thumbnails/pronunciation-mastery.jpg',
            'Master English phonetics and IPA, Produce all English sounds correctly, Improve accent and clarity, Learn stress and intonation patterns, Practice with native speaker feedback',
            'Basic English speaking ability (A2 level). Willingness to practice speaking regularly.',
            false, 'PUBLISHED', NOW(), NOW())
    RETURNING id INTO course_pronunciation_id;
    
    -- ========================================
    -- COURSE 4: Advanced Vocabulary Builder (C1)
    -- ========================================
    INSERT INTO courses (title, description, category, level, max_students, duration, tutor_id, price, thumbnail_url, objectives, prerequisites, is_featured, status, created_at, updated_at)
    VALUES ('Advanced Vocabulary Builder',
            'Expand your English vocabulary to advanced levels with 1000+ words, idioms, phrasal verbs, and collocations. This course focuses on academic vocabulary, idiomatic expressions, and natural word combinations that native speakers use. Perfect for those aiming for fluency and C1/C2 proficiency.',
            'Vocabulary', 'C1', 30, 35, 53, 119.99,
            '/uploads/courses/thumbnails/vocabulary-builder.jpg',
            'Learn 1000+ advanced words and expressions, Master idioms and phrasal verbs, Understand context and usage nuances, Improve reading comprehension, Use collocations naturally',
            'Upper-intermediate English (B2). Strong foundation in basic grammar and vocabulary.',
            true, 'PUBLISHED', NOW(), NOW())
    RETURNING id INTO course_vocabulary_id;
    
    -- ========================================
    -- COURSE 5: Conversational English Practice (A2)
    -- ========================================
    INSERT INTO courses (title, description, category, level, max_students, duration, tutor_id, price, thumbnail_url, objectives, prerequisites, is_featured, status, created_at, updated_at)
    VALUES ('Conversational English Practice',
            'Practice everyday English conversations in real-life situations. This interactive course covers common scenarios like shopping, dining, traveling, and socializing. Build confidence speaking English naturally through role-plays and live practice sessions.',
            'Conversation', 'A2', 15, 25, 53, 89.99,
            '/uploads/courses/thumbnails/conversational-english.jpg',
            'Speak confidently in daily situations, Understand native speakers better, Build conversational vocabulary, Practice listening skills, Handle common social interactions',
            'Basic English knowledge (A1). Ability to form simple sentences.',
            false, 'PUBLISHED', NOW(), NOW())
    RETURNING id INTO course_conversation_id;
    
    -- ========================================
    -- CHAPTERS FOR GRAMMAR COURSE
    -- ========================================
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Introduction to English Grammar', 
            'Learn the fundamental building blocks of English grammar including parts of speech and sentence structure. This chapter introduces you to the basic concepts that form the foundation of English grammar.',
            0, 180, true, course_grammar_id, NOW(), NOW())
    RETURNING id INTO chapter_grammar_1_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Present Tenses', 
            'Master the present simple and present continuous tenses with practical examples and exercises. Learn when to use each tense and how to form them correctly.',
            1, 240, true, course_grammar_id, NOW(), NOW())
    RETURNING id INTO chapter_grammar_2_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Past Tenses', 
            'Learn how to talk about past events using past simple and past continuous tenses. Practice telling stories and describing past experiences.',
            2, 240, true, course_grammar_id, NOW(), NOW())
    RETURNING id INTO chapter_grammar_3_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Articles and Determiners', 
            'Understand when and how to use articles (a, an, the) and other determiners in English. Master one of the most challenging aspects of English grammar.',
            3, 180, true, course_grammar_id, NOW(), NOW())
    RETURNING id INTO chapter_grammar_4_id;
    
    -- Chapter objectives for Grammar
    INSERT INTO chapter_objectives (chapter_id, objective) VALUES
    (chapter_grammar_1_id, 'Identify all eight parts of speech'),
    (chapter_grammar_1_id, 'Understand sentence components (subject, verb, object)'),
    (chapter_grammar_1_id, 'Recognize basic grammar patterns'),
    (chapter_grammar_2_id, 'Use present simple correctly for habits and facts'),
    (chapter_grammar_2_id, 'Apply present continuous for ongoing actions'),
    (chapter_grammar_2_id, 'Distinguish between the two present tenses'),
    (chapter_grammar_3_id, 'Form past simple sentences correctly'),
    (chapter_grammar_3_id, 'Use past continuous for interrupted actions'),
    (chapter_grammar_3_id, 'Tell stories in the past tense'),
    (chapter_grammar_4_id, 'Use articles (a, an, the) correctly'),
    (chapter_grammar_4_id, 'Master determiners (this, that, some, any)'),
    (chapter_grammar_4_id, 'Avoid common article mistakes');
    
    -- ========================================
    -- CHAPTERS FOR BUSINESS ENGLISH COURSE
    -- ========================================
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Professional Email Writing', 
            'Learn to write clear, professional emails for various business situations. Master email structure, tone, and common phrases used in business correspondence.',
            0, 300, true, course_business_id, NOW(), NOW())
    RETURNING id INTO chapter_business_1_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Business Meetings and Presentations', 
            'Master the language and skills needed for effective meetings and presentations. Learn how to participate actively, lead discussions, and deliver compelling presentations.',
            1, 360, true, course_business_id, NOW(), NOW())
    RETURNING id INTO chapter_business_2_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Negotiations and Persuasion', 
            'Develop negotiation skills and learn persuasive language for business contexts. Practice techniques for reaching agreements and handling objections.',
            2, 300, true, course_business_id, NOW(), NOW())
    RETURNING id INTO chapter_business_3_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Business Networking', 
            'Build your professional network with effective communication strategies. Learn small talk, networking phrases, and relationship-building techniques.',
            3, 240, true, course_business_id, NOW(), NOW())
    RETURNING id INTO chapter_business_4_id;
    
    -- Chapter objectives for Business English
    INSERT INTO chapter_objectives (chapter_id, objective) VALUES
    (chapter_business_1_id, 'Write formal business emails'),
    (chapter_business_1_id, 'Use appropriate professional tone'),
    (chapter_business_1_id, 'Structure business correspondence effectively'),
    (chapter_business_2_id, 'Lead meetings effectively in English'),
    (chapter_business_2_id, 'Deliver confident presentations'),
    (chapter_business_2_id, 'Use business vocabulary appropriately'),
    (chapter_business_3_id, 'Negotiate successfully in English'),
    (chapter_business_3_id, 'Use persuasive techniques'),
    (chapter_business_3_id, 'Handle objections professionally'),
    (chapter_business_4_id, 'Make effective small talk'),
    (chapter_business_4_id, 'Network professionally'),
    (chapter_business_4_id, 'Build business relationships');
    
    -- ========================================
    -- CHAPTERS FOR PRONUNCIATION COURSE
    -- ========================================
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('English Phonetics Basics', 
            'Introduction to English sounds, the phonetic alphabet, and pronunciation fundamentals. Learn the IPA and understand how English sounds are produced.',
            0, 180, true, course_pronunciation_id, NOW(), NOW())
    RETURNING id INTO chapter_pronunciation_1_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Vowel Sounds', 
            'Master all English vowel sounds including short vowels, long vowels, and diphthongs. Practice distinguishing between similar sounds.',
            1, 240, true, course_pronunciation_id, NOW(), NOW())
    RETURNING id INTO chapter_pronunciation_2_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Consonant Sounds', 
            'Learn to produce all English consonant sounds accurately. Focus on challenging sounds for non-native speakers.',
            2, 240, true, course_pronunciation_id, NOW(), NOW())
    RETURNING id INTO chapter_pronunciation_3_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Stress and Intonation', 
            'Understand word stress, sentence stress, and intonation patterns in English. Learn to sound more natural and native-like.',
            3, 180, true, course_pronunciation_id, NOW(), NOW())
    RETURNING id INTO chapter_pronunciation_4_id;
    
    -- Chapter objectives for Pronunciation
    INSERT INTO chapter_objectives (chapter_id, objective) VALUES
    (chapter_pronunciation_1_id, 'Read and use phonetic symbols (IPA)'),
    (chapter_pronunciation_1_id, 'Produce English sounds correctly'),
    (chapter_pronunciation_1_id, 'Understand sound differences'),
    (chapter_pronunciation_2_id, 'Pronounce all vowel sounds correctly'),
    (chapter_pronunciation_2_id, 'Distinguish similar vowel sounds'),
    (chapter_pronunciation_2_id, 'Practice vowels in context'),
    (chapter_pronunciation_3_id, 'Master all consonant sounds'),
    (chapter_pronunciation_3_id, 'Handle difficult consonant clusters'),
    (chapter_pronunciation_3_id, 'Improve overall clarity'),
    (chapter_pronunciation_4_id, 'Apply correct word stress'),
    (chapter_pronunciation_4_id, 'Use appropriate intonation'),
    (chapter_pronunciation_4_id, 'Sound more natural');
    
    -- ========================================
    -- CHAPTERS FOR VOCABULARY COURSE
    -- ========================================
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Academic Vocabulary', 
            'Learn essential academic words and expressions for formal contexts. Master the Academic Word List and formal register.',
            0, 240, true, course_vocabulary_id, NOW(), NOW())
    RETURNING id INTO chapter_vocabulary_1_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Idioms and Expressions', 
            'Master common English idioms and colloquial expressions. Learn figurative language that native speakers use daily.',
            1, 300, true, course_vocabulary_id, NOW(), NOW())
    RETURNING id INTO chapter_vocabulary_2_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Phrasal Verbs', 
            'Learn the most important phrasal verbs and how to use them correctly. Master this essential aspect of natural English.',
            2, 300, true, course_vocabulary_id, NOW(), NOW())
    RETURNING id INTO chapter_vocabulary_3_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Advanced Collocations', 
            'Discover word combinations that native speakers use naturally. Learn which words go together in English.',
            3, 240, true, course_vocabulary_id, NOW(), NOW())
    RETURNING id INTO chapter_vocabulary_4_id;
    
    -- Chapter objectives for Vocabulary
    INSERT INTO chapter_objectives (chapter_id, objective) VALUES
    (chapter_vocabulary_1_id, 'Use academic language appropriately'),
    (chapter_vocabulary_1_id, 'Understand formal texts'),
    (chapter_vocabulary_1_id, 'Write academically'),
    (chapter_vocabulary_2_id, 'Understand common idioms'),
    (chapter_vocabulary_2_id, 'Use expressions naturally'),
    (chapter_vocabulary_2_id, 'Recognize figurative language'),
    (chapter_vocabulary_3_id, 'Master essential phrasal verbs'),
    (chapter_vocabulary_3_id, 'Use them in appropriate contexts'),
    (chapter_vocabulary_3_id, 'Understand multiple meanings'),
    (chapter_vocabulary_4_id, 'Use natural collocations'),
    (chapter_vocabulary_4_id, 'Sound more native-like'),
    (chapter_vocabulary_4_id, 'Expand active vocabulary');
    
    -- ========================================
    -- CHAPTERS FOR CONVERSATION COURSE
    -- ========================================
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Everyday Conversations', 
            'Practice common daily conversations and small talk. Learn greetings, introductions, and casual conversation topics.',
            0, 180, true, course_conversation_id, NOW(), NOW())
    RETURNING id INTO chapter_conversation_1_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Shopping and Services', 
            'Learn English for shopping, restaurants, and using services. Practice real-world scenarios you encounter daily.',
            1, 180, true, course_conversation_id, NOW(), NOW())
    RETURNING id INTO chapter_conversation_2_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Travel and Transportation', 
            'Master English for traveling, asking directions, and using transportation. Essential phrases for tourists and travelers.',
            2, 180, true, course_conversation_id, NOW(), NOW())
    RETURNING id INTO chapter_conversation_3_id;
    
    INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
    VALUES ('Social Situations', 
            'Learn to navigate social events, make friends, and socialize in English. Build confidence in informal settings.',
            3, 180, true, course_conversation_id, NOW(), NOW())
    RETURNING id INTO chapter_conversation_4_id;
    
    -- Chapter objectives for Conversation
    INSERT INTO chapter_objectives (chapter_id, objective) VALUES
    (chapter_conversation_1_id, 'Make effective small talk'),
    (chapter_conversation_1_id, 'Have casual conversations'),
    (chapter_conversation_1_id, 'Build speaking confidence'),
    (chapter_conversation_2_id, 'Order in restaurants confidently'),
    (chapter_conversation_2_id, 'Shop in English'),
    (chapter_conversation_2_id, 'Ask for help appropriately'),
    (chapter_conversation_3_id, 'Ask for and give directions'),
    (chapter_conversation_3_id, 'Book travel arrangements'),
    (chapter_conversation_3_id, 'Handle travel situations'),
    (chapter_conversation_4_id, 'Socialize confidently'),
    (chapter_conversation_4_id, 'Make and respond to invitations'),
    (chapter_conversation_4_id, 'Express opinions politely');
    
    RAISE NOTICE 'Courses and chapters created successfully';
    RAISE NOTICE 'Grammar Course ID: %', course_grammar_id;
    RAISE NOTICE 'Business Course ID: %', course_business_id;
    RAISE NOTICE 'Pronunciation Course ID: %', course_pronunciation_id;
    RAISE NOTICE 'Vocabulary Course ID: %', course_vocabulary_id;
    RAISE NOTICE 'Conversation Course ID: %', course_conversation_id;
    
END $$;
