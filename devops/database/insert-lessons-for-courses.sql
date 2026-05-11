-- Insert Lessons for Courses 54-58 (Tutor: khalilabdelmoumen11@gmail.com, ID: 53)
-- This script adds detailed lessons to all chapters

DO $$
DECLARE
    -- Grammar Course (54) Chapter IDs
    ch_grammar_1 BIGINT;
    ch_grammar_2 BIGINT;
    ch_grammar_3 BIGINT;
    ch_grammar_4 BIGINT;
    
    -- Business Course (55) Chapter IDs
    ch_business_1 BIGINT;
    ch_business_2 BIGINT;
    ch_business_3 BIGINT;
    ch_business_4 BIGINT;
    
    -- Pronunciation Course (56) Chapter IDs
    ch_pronunciation_1 BIGINT;
    ch_pronunciation_2 BIGINT;
    ch_pronunciation_3 BIGINT;
    ch_pronunciation_4 BIGINT;
    
    -- Vocabulary Course (57) Chapter IDs
    ch_vocabulary_1 BIGINT;
    ch_vocabulary_2 BIGINT;
    ch_vocabulary_3 BIGINT;
    ch_vocabulary_4 BIGINT;
    
    -- Conversation Course (58) Chapter IDs
    ch_conversation_1 BIGINT;
    ch_conversation_2 BIGINT;
    ch_conversation_3 BIGINT;
    ch_conversation_4 BIGINT;
BEGIN
    -- Get Chapter IDs for Grammar Course (54)
    SELECT id INTO ch_grammar_1 FROM chapters WHERE course_id = 54 AND order_index = 0;
    SELECT id INTO ch_grammar_2 FROM chapters WHERE course_id = 54 AND order_index = 1;
    SELECT id INTO ch_grammar_3 FROM chapters WHERE course_id = 54 AND order_index = 2;
    SELECT id INTO ch_grammar_4 FROM chapters WHERE course_id = 54 AND order_index = 3;
    
    -- Get Chapter IDs for Business Course (55)
    SELECT id INTO ch_business_1 FROM chapters WHERE course_id = 55 AND order_index = 0;
    SELECT id INTO ch_business_2 FROM chapters WHERE course_id = 55 AND order_index = 1;
    SELECT id INTO ch_business_3 FROM chapters WHERE course_id = 55 AND order_index = 2;
    SELECT id INTO ch_business_4 FROM chapters WHERE course_id = 55 AND order_index = 3;
    
    -- Get Chapter IDs for Pronunciation Course (56)
    SELECT id INTO ch_pronunciation_1 FROM chapters WHERE course_id = 56 AND order_index = 0;
    SELECT id INTO ch_pronunciation_2 FROM chapters WHERE course_id = 56 AND order_index = 1;
    SELECT id INTO ch_pronunciation_3 FROM chapters WHERE course_id = 56 AND order_index = 2;
    SELECT id INTO ch_pronunciation_4 FROM chapters WHERE course_id = 56 AND order_index = 3;
    
    -- Get Chapter IDs for Vocabulary Course (57)
    SELECT id INTO ch_vocabulary_1 FROM chapters WHERE course_id = 57 AND order_index = 0;
    SELECT id INTO ch_vocabulary_2 FROM chapters WHERE course_id = 57 AND order_index = 1;
    SELECT id INTO ch_vocabulary_3 FROM chapters WHERE course_id = 57 AND order_index = 2;
    SELECT id INTO ch_vocabulary_4 FROM chapters WHERE course_id = 57 AND order_index = 3;
    
    -- Get Chapter IDs for Conversation Course (58)
    SELECT id INTO ch_conversation_1 FROM chapters WHERE course_id = 58 AND order_index = 0;
    SELECT id INTO ch_conversation_2 FROM chapters WHERE course_id = 58 AND order_index = 1;
    SELECT id INTO ch_conversation_3 FROM chapters WHERE course_id = 58 AND order_index = 2;
    SELECT id INTO ch_conversation_4 FROM chapters WHERE course_id = 58 AND order_index = 3;
    
    -- ========================================
    -- GRAMMAR COURSE LESSONS
    -- ========================================
    
    -- Grammar Chapter 1: Introduction to English Grammar
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Welcome to English Grammar', 'An introduction to the course and what you will learn about English grammar.', 'Welcome to English Grammar Fundamentals! In this course, you will learn the essential building blocks of English grammar. We will cover parts of speech, sentence structure, verb tenses, and much more.', '', 'VIDEO', 0, 15, true, true, ch_grammar_1, NOW(), NOW()),
    ('Parts of Speech Overview', 'Learn about the eight parts of speech in English.', 'The eight parts of speech are the categories that words belong to based on their function in a sentence: nouns, pronouns, verbs, adjectives, adverbs, prepositions, conjunctions, and interjections.', '', 'VIDEO', 1, 25, false, true, ch_grammar_1, NOW(), NOW()),
    ('Nouns and Pronouns', 'Understand nouns and pronouns in detail.', 'Nouns are words that name people, places, things, or ideas. Pronouns are words that take the place of nouns to avoid repetition. Examples: he, she, it, they, we.', '', 'DOCUMENT', 2, 20, false, true, ch_grammar_1, NOW(), NOW()),
    ('Verbs and Tenses Introduction', 'Introduction to verbs and the concept of tenses in English.', 'Verbs are action words or state-of-being words. English has three main tenses: past, present, and future. Each tense has different forms.', '', 'VIDEO', 3, 30, false, true, ch_grammar_1, NOW(), NOW()),
    ('Parts of Speech Quiz', 'Test your understanding of parts of speech.', 'Quiz covering nouns, pronouns, verbs, adjectives, and adverbs.', '', 'QUIZ', 4, 15, false, true, ch_grammar_1, NOW(), NOW());
    
    -- Grammar Chapter 2: Present Tenses
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Present Simple Tense', 'Learn how to form and use the present simple tense.', 'The present simple tense is used for habits, routines, facts, and general truths. Form: Subject + base verb (add -s/-es for third person singular).', '', 'VIDEO', 0, 30, false, true, ch_grammar_2, NOW(), NOW()),
    ('Present Simple Practice', 'Interactive exercises to practice present simple tense.', 'Practice forming present simple sentences with various subjects and verbs.', '', 'INTERACTIVE', 1, 25, false, true, ch_grammar_2, NOW(), NOW()),
    ('Present Continuous Tense', 'Master the present continuous tense.', 'The present continuous describes actions happening right now or temporary situations. Form: Subject + am/is/are + verb-ing.', '', 'VIDEO', 2, 30, false, true, ch_grammar_2, NOW(), NOW()),
    ('Present Simple vs Continuous', 'Learn when to use present simple versus present continuous.', 'Understanding the difference between these two tenses is crucial for accurate communication.', '', 'DOCUMENT', 3, 20, false, true, ch_grammar_2, NOW(), NOW()),
    ('Present Tenses Assignment', 'Complete exercises using both present tenses.', 'Write 10 sentences using present simple and 10 using present continuous correctly.', '', 'ASSIGNMENT', 4, 30, false, true, ch_grammar_2, NOW(), NOW());
    
    -- Grammar Chapter 3: Past Tenses
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Past Simple Tense', 'Learn how to form and use the past simple tense.', 'The past simple is used for completed actions in the past. Regular verbs add -ed, irregular verbs have special forms.', '', 'VIDEO', 0, 30, false, true, ch_grammar_3, NOW(), NOW()),
    ('Irregular Verbs', 'Master common irregular past tense verbs.', 'Learn the past forms of common irregular verbs like go-went, eat-ate, see-saw.', '', 'DOCUMENT', 1, 25, false, true, ch_grammar_3, NOW(), NOW()),
    ('Past Continuous Tense', 'Understand the past continuous tense.', 'The past continuous describes actions in progress at a specific time in the past. Form: Subject + was/were + verb-ing.', '', 'VIDEO', 2, 30, false, true, ch_grammar_3, NOW(), NOW()),
    ('Telling Stories in the Past', 'Learn to narrate past events effectively.', 'Combine past simple and past continuous to tell engaging stories.', '', 'VIDEO', 3, 25, false, true, ch_grammar_3, NOW(), NOW()),
    ('Past Tenses Quiz', 'Test your knowledge of past tenses.', 'Quiz covering past simple and past continuous usage.', '', 'QUIZ', 4, 20, false, true, ch_grammar_3, NOW(), NOW());
    
    -- Grammar Chapter 4: Articles and Determiners
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Definite and Indefinite Articles', 'Learn when to use a, an, and the.', 'Articles are words that define nouns. A/an are indefinite (any one), the is definite (specific one).', '', 'VIDEO', 0, 25, false, true, ch_grammar_4, NOW(), NOW()),
    ('Zero Article', 'Understand when NOT to use articles.', 'Some nouns do not need articles in certain contexts, like plural general statements and abstract nouns.', '', 'DOCUMENT', 1, 20, false, true, ch_grammar_4, NOW(), NOW()),
    ('Demonstratives and Quantifiers', 'Learn other determiners like this, that, some, any.', 'Determiners specify which or how many nouns we are talking about.', '', 'VIDEO', 2, 25, false, true, ch_grammar_4, NOW(), NOW()),
    ('Articles Practice Exercises', 'Interactive exercises on article usage.', 'Fill in the blanks with the correct article or leave blank if no article is needed.', '', 'INTERACTIVE', 3, 30, false, true, ch_grammar_4, NOW(), NOW());
    
    -- ========================================
    -- BUSINESS ENGLISH COURSE LESSONS
    -- ========================================
    
    -- Business Chapter 1: Professional Email Writing
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Email Structure and Format', 'Learn the proper structure of professional business emails.', 'A professional email has: subject line, greeting, opening, body, closing, and signature.', '', 'VIDEO', 0, 20, true, true, ch_business_1, NOW(), NOW()),
    ('Formal vs Informal Tone', 'Understand when to use formal or informal language in emails.', 'The tone should match your relationship with the recipient and the context.', '', 'VIDEO', 1, 25, false, true, ch_business_1, NOW(), NOW()),
    ('Common Email Phrases', 'Learn useful phrases for different email situations.', 'Master phrases for opening, requesting, apologizing, and closing emails professionally.', '', 'DOCUMENT', 2, 15, false, true, ch_business_1, NOW(), NOW()),
    ('Writing Practice: Request Email', 'Practice writing a professional request email.', 'Write an email requesting information or assistance from a colleague or client.', '', 'ASSIGNMENT', 3, 30, false, true, ch_business_1, NOW(), NOW()),
    ('Email Writing Quiz', 'Test your knowledge of professional email writing.', 'Quiz on email structure, tone, and appropriate phrases.', '', 'QUIZ', 4, 15, false, true, ch_business_1, NOW(), NOW());
    
    -- Business Chapter 2: Business Meetings and Presentations
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Meeting Vocabulary and Phrases', 'Essential vocabulary for business meetings.', 'Learn phrases for opening meetings, giving opinions, agreeing/disagreeing, and closing.', '', 'VIDEO', 0, 25, false, true, ch_business_2, NOW(), NOW()),
    ('Presentation Structure', 'How to structure an effective business presentation.', 'Introduction, main points, transitions, conclusion, and Q&A.', '', 'VIDEO', 1, 30, false, true, ch_business_2, NOW(), NOW()),
    ('Presentation Language', 'Key phrases for delivering presentations.', 'Signposting language, emphasizing points, referring to visuals.', '', 'DOCUMENT', 2, 20, false, true, ch_business_2, NOW(), NOW()),
    ('Practice Presentation', 'Prepare and deliver a short presentation.', 'Create a 5-minute presentation on a business topic of your choice.', '', 'ASSIGNMENT', 3, 45, false, true, ch_business_2, NOW(), NOW());
    
    -- Business Chapter 3: Negotiations and Persuasion
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Negotiation Basics', 'Introduction to business negotiation techniques.', 'Learn the principles of win-win negotiation and key strategies.', '', 'VIDEO', 0, 25, false, true, ch_business_3, NOW(), NOW()),
    ('Persuasive Language', 'Language techniques for persuasion.', 'Modal verbs, conditional sentences, and rhetorical questions.', '', 'VIDEO', 1, 25, false, true, ch_business_3, NOW(), NOW()),
    ('Handling Objections', 'How to respond to objections professionally.', 'Techniques for addressing concerns and finding common ground.', '', 'DOCUMENT', 2, 20, false, true, ch_business_3, NOW(), NOW()),
    ('Negotiation Role-Play', 'Practice negotiation in a simulated scenario.', 'Participate in a negotiation role-play exercise.', '', 'ONLINE', 3, 45, false, true, ch_business_3, NOW(), NOW());
    
    -- Business Chapter 4: Business Networking
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Small Talk in Business', 'Master the art of professional small talk.', 'Topics, questions, and phrases for networking situations.', '', 'VIDEO', 0, 20, false, true, ch_business_4, NOW(), NOW()),
    ('Introducing Yourself and Others', 'Professional introduction techniques.', 'How to introduce yourself and make connections at business events.', '', 'VIDEO', 1, 20, false, true, ch_business_4, NOW(), NOW()),
    ('Following Up After Networking', 'How to maintain professional relationships.', 'Email templates and strategies for following up after meetings.', '', 'DOCUMENT', 2, 15, false, true, ch_business_4, NOW(), NOW());
    
    -- ========================================
    -- PRONUNCIATION COURSE LESSONS
    -- ========================================
    
    -- Pronunciation Chapter 1: English Phonetics Basics
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Introduction to Phonetics', 'Learn what phonetics is and why it matters.', 'Phonetics is the study of speech sounds and how they are produced.', '', 'VIDEO', 0, 20, true, true, ch_pronunciation_1, NOW(), NOW()),
    ('The International Phonetic Alphabet', 'Learn to read and use the IPA.', 'The IPA is a system of symbols that represent speech sounds universally.', '', 'VIDEO', 1, 30, false, true, ch_pronunciation_1, NOW(), NOW()),
    ('English Sound System Overview', 'Overview of all English sounds.', 'English has approximately 44 sounds: vowels, diphthongs, and consonants.', '', 'DOCUMENT', 2, 25, false, true, ch_pronunciation_1, NOW(), NOW()),
    ('Pronunciation Practice Session', 'Live online session to practice sounds.', 'Interactive session with feedback on your pronunciation.', '', 'ONLINE', 3, 45, false, true, ch_pronunciation_1, NOW(), NOW());
    
    -- Pronunciation Chapter 2: Vowel Sounds
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Short Vowel Sounds', 'Master the short vowel sounds in English.', 'Practice sounds like /ɪ/, /e/, /æ/, /ʌ/, /ʊ/.', '', 'VIDEO', 0, 30, false, true, ch_pronunciation_2, NOW(), NOW()),
    ('Long Vowel Sounds', 'Learn the long vowel sounds.', 'Practice sounds like /iː/, /ɑː/, /ɔː/, /uː/, /ɜː/.', '', 'VIDEO', 1, 30, false, true, ch_pronunciation_2, NOW(), NOW()),
    ('Diphthongs', 'Master English diphthongs.', 'Two vowel sounds combined: /eɪ/, /aɪ/, /ɔɪ/, /aʊ/, /əʊ/.', '', 'VIDEO', 2, 25, false, true, ch_pronunciation_2, NOW(), NOW()),
    ('Vowel Practice Exercises', 'Interactive vowel pronunciation practice.', 'Listen and repeat exercises for all vowel sounds.', '', 'INTERACTIVE', 3, 30, false, true, ch_pronunciation_2, NOW(), NOW());
    
    -- Pronunciation Chapter 3: Consonant Sounds
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Voiced and Voiceless Consonants', 'Understand the difference between voiced and voiceless sounds.', 'Learn pairs like /p/-/b/, /t/-/d/, /k/-/g/, /f/-/v/, /s/-/z/.', '', 'VIDEO', 0, 25, false, true, ch_pronunciation_3, NOW(), NOW()),
    ('Difficult Consonant Sounds', 'Master challenging consonants for non-native speakers.', 'Focus on /θ/, /ð/, /r/, /l/, /w/, /v/.', '', 'VIDEO', 1, 30, false, true, ch_pronunciation_3, NOW(), NOW()),
    ('Consonant Clusters', 'Learn to pronounce consonant combinations.', 'Practice clusters like str-, spr-, -nts, -ths.', '', 'DOCUMENT', 2, 20, false, true, ch_pronunciation_3, NOW(), NOW()),
    ('Consonant Practice Session', 'Live practice with feedback.', 'Interactive session focusing on your specific pronunciation challenges.', '', 'ONLINE', 3, 45, false, true, ch_pronunciation_3, NOW(), NOW());
    
    -- Pronunciation Chapter 4: Stress and Intonation
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Word Stress Patterns', 'Learn where to stress syllables in words.', 'Rules and patterns for word stress in English.', '', 'VIDEO', 0, 25, false, true, ch_pronunciation_4, NOW(), NOW()),
    ('Sentence Stress', 'Understand which words to emphasize in sentences.', 'Content words vs function words, and how stress affects meaning.', '', 'VIDEO', 1, 25, false, true, ch_pronunciation_4, NOW(), NOW()),
    ('Intonation Patterns', 'Master rising and falling intonation.', 'How intonation changes meaning in questions, statements, and emotions.', '', 'VIDEO', 2, 25, false, true, ch_pronunciation_4, NOW(), NOW()),
    ('Rhythm and Connected Speech', 'Sound more natural with connected speech.', 'Linking, elision, and weak forms in natural English.', '', 'DOCUMENT', 3, 20, false, true, ch_pronunciation_4, NOW(), NOW());
    
    -- ========================================
    -- VOCABULARY COURSE LESSONS
    -- ========================================
    
    -- Vocabulary Chapter 1: Academic Vocabulary
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Academic Word List Introduction', 'Introduction to high-frequency academic words.', 'Learn the most common words used in academic texts across disciplines.', '', 'VIDEO', 0, 20, false, true, ch_vocabulary_1, NOW(), NOW()),
    ('Academic Verbs', 'Master essential academic verbs.', 'Verbs like analyze, evaluate, demonstrate, illustrate, synthesize.', '', 'VIDEO', 1, 30, false, true, ch_vocabulary_1, NOW(), NOW()),
    ('Academic Nouns and Adjectives', 'Build your academic vocabulary.', 'Common academic nouns and adjectives for formal writing.', '', 'DOCUMENT', 2, 25, false, true, ch_vocabulary_1, NOW(), NOW()),
    ('Academic Writing Practice', 'Apply academic vocabulary in writing.', 'Write a short academic paragraph using new vocabulary.', '', 'ASSIGNMENT', 3, 35, false, true, ch_vocabulary_1, NOW(), NOW());
    
    -- Vocabulary Chapter 2: Idioms and Expressions
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('What Are Idioms?', 'Introduction to idiomatic expressions in English.', 'Idioms are expressions whose meaning cannot be understood from the individual words.', '', 'VIDEO', 0, 20, false, true, ch_vocabulary_2, NOW(), NOW()),
    ('Common Everyday Idioms', 'Learn 50 frequently used idioms.', 'Master idioms like "piece of cake", "break the ice", "hit the nail on the head".', '', 'VIDEO', 1, 35, false, true, ch_vocabulary_2, NOW(), NOW()),
    ('Idioms in Context', 'See how idioms are used in real conversations.', 'Read dialogues and watch videos that use idioms naturally.', '', 'TEXT', 2, 25, false, true, ch_vocabulary_2, NOW(), NOW()),
    ('Idiom Practice Exercises', 'Interactive exercises to practice using idioms.', 'Complete sentences and match idioms to meanings.', '', 'INTERACTIVE', 3, 30, false, true, ch_vocabulary_2, NOW(), NOW()),
    ('Idioms Quiz', 'Test your knowledge of English idioms.', 'Quiz covering meanings and usage of common idioms.', '', 'QUIZ', 4, 20, false, true, ch_vocabulary_2, NOW(), NOW());
    
    -- Vocabulary Chapter 3: Phrasal Verbs
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Introduction to Phrasal Verbs', 'What are phrasal verbs and why are they important?', 'Phrasal verbs are verbs combined with prepositions or adverbs that create new meanings.', '', 'VIDEO', 0, 20, false, true, ch_vocabulary_3, NOW(), NOW()),
    ('Separable vs Inseparable Phrasal Verbs', 'Learn the grammar rules for phrasal verbs.', 'Some phrasal verbs can be separated, others cannot.', '', 'VIDEO', 1, 25, false, true, ch_vocabulary_3, NOW(), NOW()),
    ('Common Phrasal Verbs', 'Master 100 essential phrasal verbs.', 'Learn phrasal verbs organized by topic: work, relationships, communication.', '', 'DOCUMENT', 2, 40, false, true, ch_vocabulary_3, NOW(), NOW()),
    ('Phrasal Verb Practice', 'Interactive exercises with phrasal verbs.', 'Fill in the blanks and choose the correct phrasal verb.', '', 'INTERACTIVE', 3, 30, false, true, ch_vocabulary_3, NOW(), NOW());
    
    -- Vocabulary Chapter 4: Advanced Collocations
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('What Are Collocations?', 'Introduction to word combinations.', 'Collocations are words that naturally go together in English.', '', 'VIDEO', 0, 20, false, true, ch_vocabulary_4, NOW(), NOW()),
    ('Verb + Noun Collocations', 'Common verb-noun combinations.', 'Examples: make a decision, take a break, do homework, have a meeting.', '', 'VIDEO', 1, 30, false, true, ch_vocabulary_4, NOW(), NOW()),
    ('Adjective + Noun Collocations', 'Natural adjective-noun pairs.', 'Examples: strong coffee, heavy rain, deep sleep, bright future.', '', 'DOCUMENT', 2, 25, false, true, ch_vocabulary_4, NOW(), NOW()),
    ('Collocation Practice', 'Practice using collocations naturally.', 'Exercises to help you remember and use collocations correctly.', '', 'INTERACTIVE', 3, 30, false, true, ch_vocabulary_4, NOW(), NOW());
    
    -- ========================================
    -- CONVERSATION COURSE LESSONS
    -- ========================================
    
    -- Conversation Chapter 1: Everyday Conversations
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Greetings and Introductions', 'How to greet people and introduce yourself.', 'Formal and informal greetings, asking and answering "How are you?".', '', 'VIDEO', 0, 20, false, true, ch_conversation_1, NOW(), NOW()),
    ('Small Talk Topics', 'Learn to make small talk confidently.', 'Safe topics: weather, hobbies, weekend plans, current events.', '', 'VIDEO', 1, 25, false, true, ch_conversation_1, NOW(), NOW()),
    ('Asking and Answering Questions', 'Practice question forms in conversation.', 'Open and closed questions, follow-up questions, showing interest.', '', 'DOCUMENT', 2, 20, false, true, ch_conversation_1, NOW(), NOW()),
    ('Conversation Practice Session', 'Live practice with other students.', 'Interactive online session for real conversation practice.', '', 'ONLINE', 3, 45, false, true, ch_conversation_1, NOW(), NOW());
    
    -- Conversation Chapter 2: Shopping and Services
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Shopping Vocabulary', 'Learn essential vocabulary for shopping.', 'Words and phrases for clothes shopping, grocery shopping, asking for help.', '', 'VIDEO', 0, 20, false, true, ch_conversation_2, NOW(), NOW()),
    ('At the Restaurant', 'Learn how to order food and drinks.', 'Making reservations, ordering, asking about ingredients, paying the bill.', '', 'VIDEO', 1, 25, false, true, ch_conversation_2, NOW(), NOW()),
    ('Restaurant Role-Play', 'Practice restaurant conversations.', 'Interactive online session with role-play scenarios.', '', 'ONLINE', 2, 45, false, true, ch_conversation_2, NOW(), NOW()),
    ('Using Services', 'Learn English for banks, post offices, and services.', 'Practice asking for help, filling out forms, making complaints.', '', 'DOCUMENT', 3, 20, false, true, ch_conversation_2, NOW(), NOW()),
    ('Shopping Dialogue Assignment', 'Create and record a shopping dialogue.', 'Write a conversation between a customer and shop assistant, then record it.', '', 'ASSIGNMENT', 4, 30, false, true, ch_conversation_2, NOW(), NOW());
    
    -- Conversation Chapter 3: Travel and Transportation
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('At the Airport', 'Essential English for air travel.', 'Check-in, security, boarding, asking for directions in airports.', '', 'VIDEO', 0, 25, false, true, ch_conversation_3, NOW(), NOW()),
    ('Asking for Directions', 'How to ask for and give directions.', 'Prepositions of place, landmarks, understanding directions.', '', 'VIDEO', 1, 20, false, true, ch_conversation_3, NOW(), NOW()),
    ('Public Transportation', 'Using buses, trains, and taxis.', 'Buying tickets, asking about schedules, understanding announcements.', '', 'DOCUMENT', 2, 20, false, true, ch_conversation_3, NOW(), NOW()),
    ('Hotel Check-in and Check-out', 'English for hotel stays.', 'Making reservations, checking in, requesting services, complaints.', '', 'VIDEO', 3, 25, false, true, ch_conversation_3, NOW(), NOW());
    
    -- Conversation Chapter 4: Social Situations
    INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
    ('Making Friends', 'How to start and maintain friendships.', 'Conversation starters, showing interest, making plans.', '', 'VIDEO', 0, 20, false, true, ch_conversation_4, NOW(), NOW()),
    ('Invitations and Suggestions', 'How to invite people and make suggestions.', 'Phrases for inviting, accepting, declining politely.', '', 'VIDEO', 1, 20, false, true, ch_conversation_4, NOW(), NOW()),
    ('Expressing Opinions', 'How to share your thoughts and ideas.', 'Agreeing, disagreeing politely, giving reasons.', '', 'DOCUMENT', 2, 20, false, true, ch_conversation_4, NOW(), NOW()),
    ('Social Conversation Practice', 'Practice social English in real situations.', 'Live online session with various social scenarios.', '', 'ONLINE', 3, 45, false, true, ch_conversation_4, NOW(), NOW());
    
    RAISE NOTICE 'All lessons inserted successfully!';
    RAISE NOTICE 'Total lessons created: Grammar (19), Business (14), Pronunciation (16), Vocabulary (16), Conversation (17)';
    RAISE NOTICE 'Grand Total: 82 lessons';
END $$;
