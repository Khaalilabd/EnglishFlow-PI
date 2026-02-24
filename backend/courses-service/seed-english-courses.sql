-- =====================================================
-- COMPREHENSIVE ENGLISH LEARNING COURSES SEED DATA
-- =====================================================
-- This script deletes all existing courses and creates
-- rich English learning content with videos, images, and text
-- =====================================================

-- Delete existing data (in correct order due to foreign keys)
DELETE FROM lesson_media;
DELETE FROM lesson_progress;
DELETE FROM lessons;
DELETE FROM chapter_objectives;
DELETE FROM chapter_progress;
DELETE FROM chapters;
DELETE FROM course_enrollments;
DELETE FROM courses;

-- Reset sequences
ALTER SEQUENCE courses_id_seq RESTART WITH 1;
ALTER SEQUENCE chapters_id_seq RESTART WITH 1;
ALTER SEQUENCE lessons_id_seq RESTART WITH 1;
ALTER SEQUENCE lesson_media_id_seq RESTART WITH 1;

-- =====================================================
-- COURSE 1: English Grammar Fundamentals
-- =====================================================
INSERT INTO courses (title, description, level, max_students, schedule, duration, tutor_id, file_url, status, created_at, updated_at)
VALUES (
    'English Grammar Fundamentals',
    'Master the essential building blocks of English grammar. Perfect for beginners and intermediate learners who want to strengthen their foundation.',
    'BEGINNER',
    50,
    NOW() + INTERVAL '7 days',
    4800,
    1,
    'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8',
    'PUBLISHED',
    NOW(),
    NOW()
);

-- Chapter 1.1: Parts of Speech
INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
VALUES (
    'Parts of Speech',
    'Understanding the eight parts of speech and their functions in English sentences',
    1,
    180,
    true,
    1,
    NOW(),
    NOW()
);

-- Add objectives for Chapter 1.1
INSERT INTO chapter_objectives (chapter_id, objective) VALUES
(1, 'Identify nouns, verbs, and adjectives'),
(1, 'Understand pronouns and prepositions'),
(1, 'Use adverbs and conjunctions correctly');

-- Lesson 1.1.1: Nouns
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'Nouns - The Building Blocks',
    'Learn about common nouns, proper nouns, and how they form the foundation of English sentences',
    '<h2>What are Nouns?</h2><p>A <strong>noun</strong> is a word that names a person, place, thing, or idea.</p><h3>Types of Nouns:</h3><ul><li><strong>Common Nouns:</strong> dog, city, book, happiness</li><li><strong>Proper Nouns:</strong> London, Shakespeare, Monday</li><li><strong>Abstract Nouns:</strong> love, freedom, courage</li><li><strong>Collective Nouns:</strong> team, family, flock</li></ul><h3>Examples:</h3><p>The <em>teacher</em> gave the <em>students</em> their <em>homework</em>.</p><p><em>Paris</em> is the capital of <em>France</em>.</p>',
    'TEXT',
    1,
    20,
    true,
    true,
    1,
    NOW(),
    NOW()
);

-- Lesson 1.1.2: Verbs
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'Verbs - Action Words',
    'Understand action verbs, linking verbs, and helping verbs',
    '<h2>Understanding Verbs</h2><p>Verbs express <strong>actions</strong>, <strong>states</strong>, or <strong>occurrences</strong>.</p><h3>Types of Verbs:</h3><ol><li><strong>Action Verbs:</strong> run, jump, write, think</li><li><strong>Linking Verbs:</strong> be, seem, appear, become</li><li><strong>Helping Verbs:</strong> can, will, should, must</li></ol><h3>Verb Tenses:</h3><table><tr><th>Tense</th><th>Example</th></tr><tr><td>Present</td><td>I walk</td></tr><tr><td>Past</td><td>I walked</td></tr><tr><td>Future</td><td>I will walk</td></tr></table>',
    'TEXT',
    2,
    25,
    false,
    true,
    1,
    NOW(),
    NOW()
);

-- Add video lesson
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'Parts of Speech Overview',
    'Watch this comprehensive video explaining all eight parts of speech with examples',
    'This video covers nouns, pronouns, verbs, adjectives, adverbs, prepositions, conjunctions, and interjections.',
    'VIDEO',
    3,
    15,
    true,
    true,
    1,
    NOW(),
    NOW()
);

-- Add media for video lesson
INSERT INTO lesson_media (url, media_type, title, description, position, lesson_id, created_at, updated_at)
VALUES 
('https://www.youtube.com/watch?v=YkiR1KjIw7k', 'VIDEO', 'Parts of Speech Explained', 'Complete guide to English parts of speech', 0, 3, NOW(), NOW());

-- Chapter 1.2: Sentence Structure
INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
VALUES (
    'Sentence Structure',
    'Learn how to build correct English sentences with proper word order',
    2,
    150,
    true,
    1,
    NOW(),
    NOW()
);

-- Add objectives for Chapter 1.2
INSERT INTO chapter_objectives (chapter_id, objective) VALUES
(2, 'Understand subject-verb-object order'),
(2, 'Create compound sentences'),
(2, 'Use punctuation correctly');

-- Lesson 1.2.1: Simple Sentences
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'Simple Sentences',
    'Master the basic structure of English sentences',
    '<h2>Simple Sentence Structure</h2><p>A simple sentence contains a <strong>subject</strong> and a <strong>verb</strong>.</p><h3>Formula:</h3><p><code>Subject + Verb + Object</code></p><h3>Examples:</h3><ul><li>The cat <strong>sleeps</strong>.</li><li>She <strong>reads</strong> books.</li><li>They <strong>play</strong> football.</li></ul><h3>Practice:</h3><p>Try creating your own simple sentences!</p>',
    'TEXT',
    1,
    20,
    false,
    true,
    2,
    NOW(),
    NOW()
);


-- =====================================================
-- COURSE 2: Business English Communication
-- =====================================================
INSERT INTO courses (title, description, level, max_students, schedule, duration, tutor_id, file_url, status, created_at, updated_at)
VALUES (
    'Business English Communication',
    'Professional English for the workplace. Learn business vocabulary, email writing, presentations, and meeting skills.',
    'INTERMEDIATE',
    40,
    NOW() + INTERVAL '10 days',
    5400,
    2,
    'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40',
    'PUBLISHED',
    NOW(),
    NOW()
);

-- Chapter 2.1: Business Vocabulary
INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
VALUES (
    'Essential Business Vocabulary',
    'Master the key terms and phrases used in professional environments',
    1,
    120,
    true,
    2,
    NOW(),
    NOW()
);

-- Add objectives for Chapter 2.1
INSERT INTO chapter_objectives (chapter_id, objective) VALUES
(3, 'Learn workplace terminology'),
(3, 'Understand business jargon'),
(3, 'Use professional language');

-- Lesson 2.1.1: Office Vocabulary
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'Office and Workplace Terms',
    'Common vocabulary used in office settings',
    '<h2>Office Vocabulary</h2><h3>People:</h3><ul><li><strong>Colleague</strong> - coworker</li><li><strong>Supervisor</strong> - manager</li><li><strong>CEO</strong> - Chief Executive Officer</li><li><strong>HR</strong> - Human Resources</li></ul><h3>Actions:</h3><ul><li><strong>Schedule</strong> - plan a meeting</li><li><strong>Delegate</strong> - assign tasks</li><li><strong>Collaborate</strong> - work together</li><li><strong>Report</strong> - give updates</li></ul><h3>Documents:</h3><ul><li><strong>Agenda</strong> - meeting plan</li><li><strong>Minutes</strong> - meeting notes</li><li><strong>Proposal</strong> - business plan</li><li><strong>Invoice</strong> - bill</li></ul>',
    'TEXT',
    1,
    25,
    true,
    true,
    3,
    NOW(),
    NOW()
);


-- Lesson 2.1.2: Business Email Writing
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'Professional Email Writing',
    'Learn to write clear, professional business emails',
    '<h2>Email Structure</h2><h3>1. Subject Line</h3><p>Be clear and specific: <em>"Meeting Request: Q4 Budget Review"</em></p><h3>2. Greeting</h3><ul><li>Formal: Dear Mr./Ms. [Last Name]</li><li>Semi-formal: Hello [First Name]</li></ul><h3>3. Body</h3><p><strong>Opening:</strong> State your purpose<br><strong>Middle:</strong> Provide details<br><strong>Closing:</strong> Call to action</p><h3>4. Sign-off</h3><ul><li>Best regards,</li><li>Sincerely,</li><li>Kind regards,</li></ul><h3>Example:</h3><blockquote><p><strong>Subject:</strong> Project Update - Marketing Campaign</p><p>Dear Sarah,</p><p>I hope this email finds you well. I wanted to update you on the progress of our marketing campaign.</p><p>We have completed the first phase and are ready to move forward with phase two. Could we schedule a meeting next week to discuss the next steps?</p><p>Best regards,<br>John</p></blockquote>',
    'TEXT',
    2,
    30,
    false,
    true,
    3,
    NOW(),
    NOW()
);

-- Add video lesson for business communication
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'Business English Conversation',
    'Watch real business conversations and learn professional communication',
    'Learn how to communicate effectively in business settings',
    'VIDEO',
    3,
    20,
    true,
    true,
    3,
    NOW(),
    NOW()
);

INSERT INTO lesson_media (url, media_type, title, description, position, lesson_id, created_at, updated_at)
VALUES 
('https://www.youtube.com/watch?v=Pfo-4lAKyHo', 'VIDEO', 'Business English Conversations', 'Professional workplace dialogues', 0, 6, NOW(), NOW());


-- =====================================================
-- COURSE 3: English Conversation Practice
-- =====================================================
INSERT INTO courses (title, description, level, max_students, schedule, duration, tutor_id, file_url, status, created_at, updated_at)
VALUES (
    'English Conversation Practice',
    'Improve your speaking skills through real-life scenarios, dialogues, and pronunciation practice.',
    'INTERMEDIATE',
    35,
    NOW() + INTERVAL '5 days',
    3600,
    1,
    'https://images.unsplash.com/photo-1573164713714-d95e436ab8d6',
    'PUBLISHED',
    NOW(),
    NOW()
);

-- Chapter 3.1: Daily Conversations
INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
VALUES (
    'Everyday Conversations',
    'Practice common daily interactions in English',
    1,
    180,
    true,
    3,
    NOW(),
    NOW()
);

-- Add objectives for Chapter 3.1
INSERT INTO chapter_objectives (chapter_id, objective) VALUES
(4, 'Order food at restaurants'),
(4, 'Make appointments'),
(4, 'Ask for directions'),
(4, 'Shop for groceries');

-- Lesson 3.1.1: At the Restaurant
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'Ordering Food at a Restaurant',
    'Learn essential phrases for dining out',
    '<h2>Restaurant Vocabulary</h2><h3>Arriving:</h3><ul><li>"Table for two, please."</li><li>"Do you have a reservation?"</li><li>"Can we sit by the window?"</li></ul><h3>Ordering:</h3><ul><li>"I''d like to order..."</li><li>"Could I have the..."</li><li>"What do you recommend?"</li><li>"I''ll have the..."</li></ul><h3>During the Meal:</h3><ul><li>"Could we have some water, please?"</li><li>"This is delicious!"</li><li>"Excuse me, could I get..."</li></ul><h3>Paying:</h3><ul><li>"Could we have the bill, please?"</li><li>"Is service included?"</li><li>"Can I pay by card?"</li></ul><h3>Sample Dialogue:</h3><blockquote><p><strong>Waiter:</strong> Good evening! Do you have a reservation?<br><strong>You:</strong> Yes, table for two under Smith.<br><strong>Waiter:</strong> Perfect! Right this way. Here''s your menu.<br><strong>You:</strong> Thank you. Could we have some water, please?<br><strong>Waiter:</strong> Of course! Are you ready to order?<br><strong>You:</strong> Yes, I''d like the grilled salmon, please.</p></blockquote>',
    'TEXT',
    1,
    25,
    true,
    true,
    4,
    NOW(),
    NOW()
);


-- Add video for restaurant conversation
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'Restaurant Conversation Video',
    'Watch a complete restaurant interaction from start to finish',
    'Real-life restaurant dialogue with native speakers',
    'VIDEO',
    2,
    15,
    true,
    true,
    4,
    NOW(),
    NOW()
);

INSERT INTO lesson_media (url, media_type, title, description, position, lesson_id, created_at, updated_at)
VALUES 
('https://www.youtube.com/watch?v=CxokBJEt-8s', 'VIDEO', 'Ordering Food in English', 'Restaurant conversation practice', 0, 8, NOW(), NOW());

-- Lesson 3.1.2: Shopping
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'Shopping and Making Purchases',
    'Essential phrases for shopping in English',
    '<h2>Shopping Phrases</h2><h3>Asking for Help:</h3><ul><li>"Excuse me, where can I find..."</li><li>"Do you have this in a different size/color?"</li><li>"How much does this cost?"</li><li>"Is this on sale?"</li></ul><h3>Trying Things On:</h3><ul><li>"Can I try this on?"</li><li>"Where are the fitting rooms?"</li><li>"Do you have a mirror?"</li><li>"This is too big/small."</li></ul><h3>Making a Purchase:</h3><ul><li>"I''ll take this one."</li><li>"Do you accept credit cards?"</li><li>"Can I get a receipt?"</li><li>"Is there a return policy?"</li></ul><h3>Useful Vocabulary:</h3><table><tr><th>Word</th><th>Meaning</th></tr><tr><td>Discount</td><td>Price reduction</td></tr><tr><td>Receipt</td><td>Proof of purchase</td></tr><tr><td>Refund</td><td>Money back</td></tr><tr><td>Exchange</td><td>Swap for different item</td></tr></table>',
    'TEXT',
    3,
    20,
    false,
    true,
    4,
    NOW(),
    NOW()
);


-- =====================================================
-- COURSE 4: IELTS Exam Preparation
-- =====================================================
INSERT INTO courses (title, description, level, max_students, schedule, duration, tutor_id, file_url, status, created_at, updated_at)
VALUES (
    'IELTS Exam Preparation',
    'Comprehensive preparation for all four sections of the IELTS exam: Listening, Reading, Writing, and Speaking.',
    'UPPER_INTERMEDIATE',
    30,
    NOW() + INTERVAL '15 days',
    7200,
    3,
    'https://images.unsplash.com/photo-1434030216411-0b793f4b4173',
    'PUBLISHED',
    NOW(),
    NOW()
);

-- Chapter 4.1: IELTS Writing
INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
VALUES (
    'IELTS Writing Task 1 & 2',
    'Master both writing tasks with strategies, templates, and practice',
    1,
    240,
    true,
    4,
    NOW(),
    NOW()
);

-- Add objectives for Chapter 4.1
INSERT INTO chapter_objectives (chapter_id, objective) VALUES
(5, 'Understand task requirements'),
(5, 'Learn essay structure'),
(5, 'Improve vocabulary and grammar'),
(5, 'Practice time management');

-- Lesson 4.1.1: Writing Task 1
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'IELTS Writing Task 1 - Academic',
    'Learn to describe graphs, charts, and diagrams effectively',
    '<h2>Writing Task 1 Overview</h2><p><strong>Time:</strong> 20 minutes<br><strong>Words:</strong> Minimum 150 words<br><strong>Task:</strong> Describe visual information</p><h3>Types of Visuals:</h3><ul><li>Line graphs</li><li>Bar charts</li><li>Pie charts</li><li>Tables</li><li>Diagrams</li><li>Maps</li></ul><h3>Structure:</h3><ol><li><strong>Introduction:</strong> Paraphrase the question</li><li><strong>Overview:</strong> Main trends/features</li><li><strong>Body 1:</strong> Detailed description</li><li><strong>Body 2:</strong> More details/comparisons</li></ol><h3>Useful Phrases:</h3><p><strong>Introduction:</strong></p><ul><li>"The graph illustrates..."</li><li>"The chart shows..."</li><li>"The diagram depicts..."</li></ul><p><strong>Trends:</strong></p><ul><li>"There was a significant increase..."</li><li>"The figure remained stable..."</li><li>"A dramatic decline occurred..."</li></ul><p><strong>Comparisons:</strong></p><ul><li>"In comparison to..."</li><li>"While X increased, Y decreased..."</li><li>"The highest/lowest figure was..."</li></ul>',
    'TEXT',
    1,
    35,
    true,
    true,
    5,
    NOW(),
    NOW()
);


-- Lesson 4.1.2: Writing Task 2
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'IELTS Writing Task 2 - Essay Writing',
    'Master the art of writing academic essays for IELTS',
    '<h2>Writing Task 2 Overview</h2><p><strong>Time:</strong> 40 minutes<br><strong>Words:</strong> Minimum 250 words<br><strong>Task:</strong> Write an essay responding to a point of view, argument, or problem</p><h3>Essay Types:</h3><ol><li><strong>Opinion:</strong> Do you agree or disagree?</li><li><strong>Discussion:</strong> Discuss both views</li><li><strong>Problem/Solution:</strong> Causes and solutions</li><li><strong>Two-part:</strong> Answer two questions</li></ol><h3>Essay Structure:</h3><p><strong>Introduction (2-3 sentences):</strong></p><ul><li>Paraphrase the question</li><li>State your position</li></ul><p><strong>Body Paragraph 1:</strong></p><ul><li>Topic sentence</li><li>Explanation</li><li>Example</li></ul><p><strong>Body Paragraph 2:</strong></p><ul><li>Topic sentence</li><li>Explanation</li><li>Example</li></ul><p><strong>Conclusion (2 sentences):</strong></p><ul><li>Summarize main points</li><li>Restate position</li></ul><h3>Linking Words:</h3><table><tr><th>Purpose</th><th>Words/Phrases</th></tr><tr><td>Adding</td><td>Furthermore, Moreover, In addition</td></tr><tr><td>Contrasting</td><td>However, Nevertheless, On the other hand</td></tr><tr><td>Giving examples</td><td>For instance, For example, Such as</td></tr><tr><td>Concluding</td><td>In conclusion, To sum up, Overall</td></tr></table>',
    'TEXT',
    2,
    40,
    false,
    true,
    5,
    NOW(),
    NOW()
);

-- Add IELTS video lesson
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'IELTS Writing Tips and Strategies',
    'Expert tips for achieving a high band score in IELTS Writing',
    'Learn proven strategies from IELTS examiners',
    'VIDEO',
    3,
    25,
    true,
    true,
    5,
    NOW(),
    NOW()
);

INSERT INTO lesson_media (url, media_type, title, description, position, lesson_id, created_at, updated_at)
VALUES 
('https://www.youtube.com/watch?v=0oE4OOl-4GI', 'VIDEO', 'IELTS Writing Task 2 Tips', 'How to get Band 7+ in IELTS Writing', 0, 12, NOW(), NOW());


-- =====================================================
-- COURSE 5: Advanced English Idioms and Expressions
-- =====================================================
INSERT INTO courses (title, description, level, max_students, schedule, duration, tutor_id, file_url, status, created_at, updated_at)
VALUES (
    'Advanced English Idioms and Expressions',
    'Sound like a native speaker! Learn common idioms, phrasal verbs, and colloquial expressions used in everyday English.',
    'ADVANCED',
    25,
    NOW() + INTERVAL '12 days',
    4200,
    2,
    'https://images.unsplash.com/photo-1503676260728-1c00da094a0b',
    'PUBLISHED',
    NOW(),
    NOW()
);

-- Chapter 5.1: Common Idioms
INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
VALUES (
    'Everyday Idioms',
    'Master the most frequently used English idioms',
    1,
    150,
    true,
    5,
    NOW(),
    NOW()
);

-- Add objectives for Chapter 5.1
INSERT INTO chapter_objectives (chapter_id, objective) VALUES
(6, 'Understand idiom meanings'),
(6, 'Use idioms in context'),
(6, 'Recognize cultural references');

-- Lesson 5.1.1: Body Idioms
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'Idioms Using Body Parts',
    'Learn idioms that reference parts of the body',
    '<h2>Body Part Idioms</h2><h3>Head:</h3><ul><li><strong>Keep your head above water</strong> - Manage to survive financially<br><em>Example: "With two jobs, I can barely keep my head above water."</em></li><li><strong>Head over heels</strong> - Deeply in love<br><em>Example: "She''s head over heels for him."</em></li><li><strong>Use your head</strong> - Think carefully<br><em>Example: "Use your head before making that decision."</em></li></ul><h3>Hand:</h3><ul><li><strong>Give someone a hand</strong> - Help someone<br><em>Example: "Can you give me a hand with this box?"</em></li><li><strong>Get out of hand</strong> - Become uncontrollable<br><em>Example: "The party got out of hand."</em></li><li><strong>Hands down</strong> - Without question<br><em>Example: "She''s hands down the best player."</em></li></ul><h3>Eye:</h3><ul><li><strong>See eye to eye</strong> - Agree completely<br><em>Example: "We don''t always see eye to eye."</em></li><li><strong>Turn a blind eye</strong> - Ignore something<br><em>Example: "The teacher turned a blind eye to the cheating."</em></li><li><strong>Keep an eye on</strong> - Watch carefully<br><em>Example: "Can you keep an eye on my bag?"</em></li></ul><h3>Foot:</h3><ul><li><strong>Put your foot down</strong> - Be firm<br><em>Example: "I had to put my foot down about the curfew."</em></li><li><strong>Get cold feet</strong> - Become nervous<br><em>Example: "He got cold feet before the wedding."</em></li></ul>',
    'TEXT',
    1,
    30,
    true,
    true,
    6,
    NOW(),
    NOW()
);


-- Lesson 5.1.2: Common Expressions
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'Popular English Expressions',
    'The most common idioms you''ll hear in daily conversation',
    '<h2>Essential Idioms</h2><h3>About Difficulty:</h3><ul><li><strong>Piece of cake</strong> - Very easy<br><em>"The test was a piece of cake!"</em></li><li><strong>It''s not rocket science</strong> - Not difficult<br><em>"Just follow the instructions, it''s not rocket science."</em></li><li><strong>Uphill battle</strong> - Difficult challenge<br><em>"Losing weight is an uphill battle."</em></li></ul><h3>About Money:</h3><ul><li><strong>Cost an arm and a leg</strong> - Very expensive<br><em>"That car costs an arm and a leg."</em></li><li><strong>Break the bank</strong> - Too expensive<br><em>"This vacation won''t break the bank."</em></li><li><strong>On a shoestring</strong> - With very little money<br><em>"We traveled Europe on a shoestring."</em></li></ul><h3>About Time:</h3><ul><li><strong>In the nick of time</strong> - Just in time<br><em>"We arrived in the nick of time."</em></li><li><strong>Better late than never</strong> - It''s good you came even if late<br><em>"You''re two hours late, but better late than never!"</em></li><li><strong>Time flies</strong> - Time passes quickly<br><em>"Time flies when you''re having fun."</em></li></ul><h3>About Communication:</h3><ul><li><strong>Break the ice</strong> - Start a conversation<br><em>"Let''s play a game to break the ice."</em></li><li><strong>Spill the beans</strong> - Reveal a secret<br><em>"Don''t spill the beans about the surprise party!"</em></li><li><strong>Beat around the bush</strong> - Avoid saying something directly<br><em>"Stop beating around the bush and tell me!"</em></li></ul>',
    'TEXT',
    2,
    25,
    false,
    true,
    6,
    NOW(),
    NOW()
);

-- Add idioms video
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'English Idioms in Context',
    'Watch native speakers use idioms in real conversations',
    'Learn how to use idioms naturally in everyday speech',
    'VIDEO',
    3,
    18,
    true,
    true,
    6,
    NOW(),
    NOW()
);

INSERT INTO lesson_media (url, media_type, title, description, position, lesson_id, created_at, updated_at)
VALUES 
('https://www.youtube.com/watch?v=jShAS-UrJsM', 'VIDEO', '25 Common English Idioms', 'Learn idioms with examples', 0, 15, NOW(), NOW());


-- Chapter 5.2: Phrasal Verbs
INSERT INTO chapters (title, description, order_index, estimated_duration, is_published, course_id, created_at, updated_at)
VALUES (
    'Essential Phrasal Verbs',
    'Master the most important phrasal verbs in English',
    2,
    180,
    true,
    5,
    NOW(),
    NOW()
);

-- Add objectives for Chapter 5.2
INSERT INTO chapter_objectives (chapter_id, objective) VALUES
(7, 'Understand phrasal verb meanings'),
(7, 'Distinguish separable vs inseparable'),
(7, 'Use phrasal verbs correctly');

-- Lesson 5.2.1: Common Phrasal Verbs
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'Top 50 Phrasal Verbs',
    'The most frequently used phrasal verbs in English',
    '<h2>Essential Phrasal Verbs</h2><h3>With "Get":</h3><ul><li><strong>Get up</strong> - Wake up and leave bed<br><em>"I get up at 7 AM every day."</em></li><li><strong>Get along</strong> - Have a good relationship<br><em>"I get along well with my coworkers."</em></li><li><strong>Get over</strong> - Recover from<br><em>"It took me weeks to get over the flu."</em></li><li><strong>Get through</strong> - Complete successfully<br><em>"We got through the difficult project."</em></li></ul><h3>With "Look":</h3><ul><li><strong>Look after</strong> - Take care of<br><em>"Can you look after my dog?"</em></li><li><strong>Look forward to</strong> - Anticipate with pleasure<br><em>"I''m looking forward to the weekend."</em></li><li><strong>Look up</strong> - Search for information<br><em>"Look up the word in the dictionary."</em></li><li><strong>Look into</strong> - Investigate<br><em>"The police are looking into the case."</em></li></ul><h3>With "Take":</h3><ul><li><strong>Take off</strong> - Remove / Leave the ground<br><em>"Take off your shoes." / "The plane takes off at 6 PM."</em></li><li><strong>Take after</strong> - Resemble<br><em>"She takes after her mother."</em></li><li><strong>Take up</strong> - Start a hobby<br><em>"I took up yoga last year."</em></li><li><strong>Take care of</strong> - Look after<br><em>"I''ll take care of the arrangements."</em></li></ul><h3>With "Put":</h3><ul><li><strong>Put off</strong> - Postpone<br><em>"Don''t put off until tomorrow what you can do today."</em></li><li><strong>Put up with</strong> - Tolerate<br><em>"I can''t put up with this noise anymore."</em></li><li><strong>Put on</strong> - Wear<br><em>"Put on your coat, it''s cold."</em></li></ul>',
    'TEXT',
    1,
    35,
    false,
    true,
    7,
    NOW(),
    NOW()
);

-- Add phrasal verbs video
INSERT INTO lessons (title, description, content, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at)
VALUES (
    'Phrasal Verbs Explained',
    'Comprehensive video guide to understanding and using phrasal verbs',
    'Master phrasal verbs with clear explanations and examples',
    'VIDEO',
    2,
    22,
    false,
    true,
    7,
    NOW(),
    NOW()
);

INSERT INTO lesson_media (url, media_type, title, description, position, lesson_id, created_at, updated_at)
VALUES 
('https://www.youtube.com/watch?v=GNgHYMPQh-w', 'VIDEO', 'Phrasal Verbs in English', 'Learn the most common phrasal verbs', 0, 17, NOW(), NOW());

-- =====================================================
-- COMPLETION MESSAGE
-- =====================================================
-- Display success message
DO $$
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE 'SEED DATA LOADED SUCCESSFULLY!';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Created 5 comprehensive English courses:';
    RAISE NOTICE '1. English Grammar Fundamentals (Beginner)';
    RAISE NOTICE '2. Business English Communication (Intermediate)';
    RAISE NOTICE '3. English Conversation Practice (Intermediate)';
    RAISE NOTICE '4. IELTS Exam Preparation (Upper-Intermediate)';
    RAISE NOTICE '5. Advanced English Idioms (Advanced)';
    RAISE NOTICE '';
    RAISE NOTICE 'Total: 7 chapters, 17 lessons, 6 video lessons';
    RAISE NOTICE '========================================';
END $$;
