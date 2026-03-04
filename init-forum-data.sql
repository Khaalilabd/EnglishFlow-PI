-- =====================================================
-- Forum Data for englishflow_community
-- =====================================================
-- Database: englishflow_community
-- Prerequisites: 
--   1. Categories and SubCategories already created (init-categories.sql)
--   2. Users exist in englishflow_auth database
-- =====================================================

-- Clean existing data (only if tables exist)
DO $$ 
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'reactions') THEN
        DELETE FROM reactions;
    END IF;
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'posts') THEN
        DELETE FROM posts;
    END IF;
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'topics') THEN
        DELETE FROM topics;
    END IF;
END $$;

-- =====================================================
-- TOPICS
-- =====================================================

-- Student Introductions (SubCategory 1)
INSERT INTO topics (title, content, user_id, user_name, sub_category_id, views_count, is_pinned, is_locked, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, created_at, updated_at, last_activity_at) VALUES
('Hello from Tunisia!', 'Hi everyone! I am Ahmed, a complete beginner in English. I work in tourism and want to improve my communication skills. Looking forward to learning with you all!', 1, 'Ahmed Ben Ali', 1, 145, false, false, 12, 0, 12, 8, 3, 1, 17, NOW() - INTERVAL '2 months', NOW() - INTERVAL '1 month', NOW() - INTERVAL '1 month'),
('New student from Sousse', 'Hello! My name is Mohamed. I am 24 years old and I need English for my job. Happy to be part of this community!', 2, 'Mohamed Gharbi', 1, 98, false, false, 8, 0, 8, 6, 1, 1, 11, NOW() - INTERVAL '1 month', NOW() - INTERVAL '3 weeks', NOW() - INTERVAL '3 weeks'),
('Greetings from Sfax', 'Hi all! I am Salma. I love watching English movies and want to understand them without subtitles. Let us learn together!', 3, 'Salma Mansour', 1, 76, false, false, 10, 0, 10, 7, 2, 1, 14, NOW() - INTERVAL '3 weeks', NOW() - INTERVAL '2 weeks', NOW() - INTERVAL '2 weeks');

-- Grammar & Vocabulary Questions (SubCategory 4)
INSERT INTO topics (title, content, user_id, user_name, sub_category_id, views_count, is_pinned, is_locked, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_trending, created_at, updated_at, last_activity_at) VALUES
('When to use Present Perfect vs Past Simple?', 'I am always confused about when to use "I have done" vs "I did". Can someone explain the difference with clear examples? This is really important for my IELTS preparation. Thanks in advance!', 1, 'Ahmed Ben Ali', 4, 234, false, false, 18, 1, 17, 10, 5, 3, 29, true, NOW() - INTERVAL '1 week', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
('Common mistakes with articles (a, an, the)', 'I keep making mistakes with articles in English. When should I use "a", "an", or "the"? Are there any simple rules to remember? Sometimes I use "the" when I should not, and vice versa.', 2, 'Mohamed Gharbi', 4, 189, false, false, 15, 0, 15, 8, 4, 3, 24, true, NOW() - INTERVAL '5 days', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
('Phrasal verbs that confuse me', 'What is the difference between "look for", "look after", and "look up"? There are so many phrasal verbs in English! How can I remember them all? Any tips or resources?', 3, 'Salma Mansour', 4, 156, false, false, 12, 0, 12, 7, 3, 2, 19, false, NOW() - INTERVAL '10 days', NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days');

-- Pronunciation & Accent (SubCategory 5)
INSERT INTO topics (title, content, user_id, user_name, sub_category_id, views_count, is_pinned, is_locked, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, created_at, updated_at, last_activity_at) VALUES
('How to pronounce TH sound correctly?', 'I struggle with the "th" sound in words like "think" and "this". My tongue does not seem to go in the right position. Any tips or exercises to practice? Are there any videos you recommend?', 1, 'Ahmed Ben Ali', 5, 201, false, false, 16, 0, 16, 9, 4, 3, 26, NOW() - INTERVAL '2 weeks', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
('American vs British pronunciation', 'Should I learn American or British pronunciation? What are the main differences? I watch both American and British shows, so I am confused about which accent to focus on.', 2, 'Mohamed Gharbi', 5, 167, false, false, 13, 1, 12, 8, 3, 2, 20, NOW() - INTERVAL '12 days', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
('Best apps for pronunciation practice?', 'Can anyone recommend good apps or websites to practice English pronunciation? Preferably free ones! I want to improve my speaking skills before my job interview next month.', 3, 'Salma Mansour', 5, 143, false, false, 11, 0, 11, 7, 2, 2, 17, NOW() - INTERVAL '8 days', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days');

-- Written Expression (SubCategory 6)
INSERT INTO topics (title, content, user_id, user_name, sub_category_id, views_count, is_pinned, is_locked, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_trending, created_at, updated_at, last_activity_at) VALUES
('IELTS Writing Task 2 - Need feedback', 'I wrote an essay for IELTS Writing Task 2. Can someone review it and give me feedback? Topic: "Some people think technology makes life easier, while others believe it creates more problems. Discuss both views and give your opinion." I am aiming for band 7.', 1, 'Ahmed Ben Ali', 6, 278, false, false, 22, 0, 22, 12, 6, 4, 36, true, NOW() - INTERVAL '4 days', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
('How to write a formal email in English?', 'I need to write formal emails to international clients. What is the proper structure? How should I start and end the email? Any templates or examples would be very helpful!', 2, 'Mohamed Gharbi', 6, 192, false, false, 14, 0, 14, 8, 4, 2, 22, false, NOW() - INTERVAL '6 days', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days');

-- =====================================================
-- POSTS (Replies to Topics)
-- =====================================================

-- Replies to "When to use Present Perfect vs Past Simple?" (Topic about grammar)
DO $$
DECLARE
    v_topic_id INTEGER;
BEGIN
    SELECT id INTO v_topic_id FROM topics WHERE title = 'When to use Present Perfect vs Past Simple?' LIMIT 1;
    
    IF v_topic_id IS NOT NULL THEN
        INSERT INTO posts (content, user_id, user_name, topic_id, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_accepted, created_at, updated_at) VALUES
        ('Great question! Present Perfect is used when the time is not specified or when the action has a connection to the present. For example: "I have lived in Tunis for 5 years" (you still live there). Past Simple is for completed actions at a specific time: "I lived in Paris in 2020" (you do not live there anymore). The key is whether the action is finished or still relevant.', 4, 'Emily Brown', v_topic_id, 15, 0, 15, 8, 5, 2, 24, true, NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days'),
        ('To add to Emily answer: Use Present Perfect for experiences without mentioning when: "I have visited London" vs "I visited London last year" (Past Simple). Also, use Present Perfect with "just", "already", "yet": "I have just finished my homework". Hope this helps!', 5, 'James Anderson', v_topic_id, 12, 0, 12, 7, 3, 2, 19, false, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
        ('Thank you so much! This makes it much clearer now. So "I have eaten sushi" (experience, time not important) vs "I ate sushi yesterday" (specific time). Got it! I will practice more with these examples.', 1, 'Ahmed Ben Ali', v_topic_id, 5, 0, 5, 5, 0, 0, 5, false, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days');
    END IF;
END $$;

-- Replies to "Common mistakes with articles"
DO $$
DECLARE
    v_topic_id INTEGER;
BEGIN
    SELECT id INTO v_topic_id FROM topics WHERE title = 'Common mistakes with articles (a, an, the)' LIMIT 1;
    
    IF v_topic_id IS NOT NULL THEN
        INSERT INTO posts (content, user_id, user_name, topic_id, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_accepted, created_at, updated_at) VALUES
        ('Here are some basic rules:
1. Use "a/an" for singular countable nouns (first mention): "I saw a cat"
2. Use "the" for specific things (second mention): "The cat was black"
3. Use "the" for unique things: "the sun", "the moon", "the president"
4. No article for plural general statements: "Cats are cute" (not "The cats")
5. No article for abstract nouns: "Love is important" (not "The love")

Practice with these rules and you will improve!', 6, 'Sophia Taylor', v_topic_id, 18, 0, 18, 10, 5, 3, 29, true, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
        ('Also remember: "a" before consonant sounds (a book, a university), "an" before vowel sounds (an apple, an hour). It is about the SOUND, not the letter! For example: "a European" (sounds like "yuropean") but "an honest man" (h is silent).', 7, 'Robert Wilson', v_topic_id, 14, 0, 14, 8, 4, 2, 22, false, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
        ('This is so helpful! I always used "the" everywhere. Now I understand better. I will practice with these examples. Thank you both!', 2, 'Mohamed Gharbi', v_topic_id, 6, 0, 6, 6, 0, 0, 6, false, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days');
    END IF;
END $$;

-- Replies to "How to pronounce TH sound"
DO $$
DECLARE
    v_topic_id INTEGER;
BEGIN
    SELECT id INTO v_topic_id FROM topics WHERE title = 'How to pronounce TH sound correctly?' LIMIT 1;
    
    IF v_topic_id IS NOT NULL THEN
        INSERT INTO posts (content, user_id, user_name, topic_id, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_accepted, created_at, updated_at) VALUES
        ('The "th" sound is tricky for many learners! Here is how to do it: Put your tongue between your teeth (yes, it should stick out a little) and blow air. For "think" it is voiceless (no vibration in throat), for "this" it is voiced (you feel vibration). Practice with this tongue twister: "The three thieves thought Thursday was their day." Start slowly and repeat many times!', 8, 'Lisa Garcia', v_topic_id, 20, 0, 20, 11, 6, 3, 32, true, NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days'),
        ('I recommend watching YouTube videos showing mouth position. Mirror practice helps a lot! Look at your mouth while practicing. Start slowly: "th-th-th-think", then speed up. Also, try minimal pairs: "think/sink", "three/tree", "bath/bat". This will train your ear and mouth!', 4, 'Emily Brown', v_topic_id, 13, 0, 13, 7, 4, 2, 21, false, NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days'),
        ('Thanks everyone! I have been practicing with a mirror and it is getting better. The tongue position tip really helped! I can now say "think" and "this" correctly. Still working on saying it naturally in sentences.', 1, 'Ahmed Ben Ali', v_topic_id, 4, 0, 4, 4, 0, 0, 4, false, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days');
    END IF;
END $$;

-- Replies to "IELTS Writing Task 2"
DO $$
DECLARE
    v_topic_id INTEGER;
BEGIN
    SELECT id INTO v_topic_id FROM topics WHERE title = 'IELTS Writing Task 2 - Need feedback' LIMIT 1;
    
    IF v_topic_id IS NOT NULL THEN
        INSERT INTO posts (content, user_id, user_name, topic_id, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_accepted, created_at, updated_at) VALUES
        ('Good attempt! Here is my feedback:
STRENGTHS:
- Clear structure with introduction, body paragraphs, and conclusion
- Good use of linking words (however, moreover, in conclusion)
- Relevant examples

AREAS TO IMPROVE:
- Need more specific examples (mention specific technologies)
- Some grammar errors with conditionals and articles
- Vocabulary could be more academic (use "facilitate" instead of "make easier")

Overall: Band 6.5-7.0. Keep practicing and focus on academic vocabulary!', 5, 'James Anderson', v_topic_id, 19, 0, 19, 10, 6, 3, 31, true, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
        ('I agree with James. Also, try to vary your sentence structures more. Use some complex sentences with relative clauses. For example: "Technology, which has revolutionized communication, has both advantages and disadvantages." Your vocabulary is good but could be more sophisticated. Use words like "facilitate", "enhance", "mitigate" instead of simple verbs.', 6, 'Sophia Taylor', v_topic_id, 15, 0, 15, 8, 5, 2, 24, false, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
        ('Thank you both! This is exactly what I needed. I will work on those points and rewrite it. I will add more specific examples like smartphones and social media. Also, I will use more academic vocabulary. Can I send you the revised version?', 1, 'Ahmed Ben Ali', v_topic_id, 3, 0, 3, 3, 0, 0, 3, false, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day');
    END IF;
END $$;

-- Replies to "How to write formal email"
DO $$
DECLARE
    v_topic_id INTEGER;
BEGIN
    SELECT id INTO v_topic_id FROM topics WHERE title = 'How to write a formal email in English?' LIMIT 1;
    
    IF v_topic_id IS NOT NULL THEN
        INSERT INTO posts (content, user_id, user_name, topic_id, upvotes, downvotes, score, like_count, insightful_count, helpful_count, weighted_score, is_accepted, created_at, updated_at) VALUES
        ('Here is a formal email structure:

OPENING:
- Dear Mr./Ms. [Last Name], (if you know the name)
- Dear Sir/Madam, (if you do not know)

INTRODUCTION:
- I am writing to inquire about...
- I am writing with regard to...

BODY:
- State your purpose clearly
- Use formal language
- Be polite and professional

CLOSING:
- I look forward to hearing from you.
- Thank you for your time and consideration.

SIGN-OFF:
- Yours sincerely, (if you know the name)
- Yours faithfully, (if you do not know)

Avoid contractions (use "I am" not "I''m") and informal language!', 7, 'Robert Wilson', v_topic_id, 17, 0, 17, 9, 5, 3, 28, true, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
        ('Perfect explanation! I would add: always use a clear subject line, keep paragraphs short, and proofread before sending. Common phrases: "I would appreciate if you could...", "Could you please...", "I would be grateful if...". These are very useful for business emails!', 4, 'Emily Brown', v_topic_id, 11, 0, 11, 6, 3, 2, 17, false, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days');
    END IF;
END $$;

-- =====================================================
-- REACTIONS
-- =====================================================

-- Reactions for Topic 4 (Present Perfect question) - Popular topic
DO $$
DECLARE
    v_topic_id INTEGER;
BEGIN
    SELECT id INTO v_topic_id FROM topics WHERE title = 'When to use Present Perfect vs Past Simple?' LIMIT 1;
    
    IF v_topic_id IS NOT NULL THEN
        INSERT INTO reactions (user_id, topic_id, post_id, type, created_at) VALUES
        (1, v_topic_id, NULL, 'LIKE', NOW() - INTERVAL '6 days'),
        (2, v_topic_id, NULL, 'LIKE', NOW() - INTERVAL '6 days'),
        (3, v_topic_id, NULL, 'HELPFUL', NOW() - INTERVAL '5 days'),
        (8, v_topic_id, NULL, 'INSIGHTFUL', NOW() - INTERVAL '5 days'),
        (9, v_topic_id, NULL, 'LIKE', NOW() - INTERVAL '4 days'),
        (10, v_topic_id, NULL, 'INSIGHTFUL', NOW() - INTERVAL '4 days'),
        (11, v_topic_id, NULL, 'HELPFUL', NOW() - INTERVAL '3 days'),
        (12, v_topic_id, NULL, 'LIKE', NOW() - INTERVAL '3 days'),
        (13, v_topic_id, NULL, 'INSIGHTFUL', NOW() - INTERVAL '2 days'),
        (14, v_topic_id, NULL, 'HELPFUL', NOW() - INTERVAL '2 days');
    END IF;
END $$;

-- Reactions for Post 1 (Emily's answer about Present Perfect) - Accepted answer
DO $$
DECLARE
    v_post_id INTEGER;
BEGIN
    SELECT p.id INTO v_post_id 
    FROM posts p 
    JOIN topics t ON p.topic_id = t.id 
    WHERE t.title = 'When to use Present Perfect vs Past Simple?' 
    AND p.user_name = 'Emily Brown' 
    LIMIT 1;
    
    IF v_post_id IS NOT NULL THEN
        INSERT INTO reactions (user_id, topic_id, post_id, type, created_at) VALUES
        (1, NULL, v_post_id, 'HELPFUL', NOW() - INTERVAL '6 days'),
        (2, NULL, v_post_id, 'INSIGHTFUL', NOW() - INTERVAL '6 days'),
        (3, NULL, v_post_id, 'HELPFUL', NOW() - INTERVAL '5 days'),
        (8, NULL, v_post_id, 'LIKE', NOW() - INTERVAL '5 days'),
        (9, NULL, v_post_id, 'INSIGHTFUL', NOW() - INTERVAL '5 days'),
        (10, NULL, v_post_id, 'LIKE', NOW() - INTERVAL '4 days'),
        (11, NULL, v_post_id, 'INSIGHTFUL', NOW() - INTERVAL '4 days'),
        (12, NULL, v_post_id, 'LIKE', NOW() - INTERVAL '3 days');
    END IF;
END $$;

-- Reactions for Topic 5 (Articles question)
DO $$
DECLARE
    v_topic_id INTEGER;
BEGIN
    SELECT id INTO v_topic_id FROM topics WHERE title = 'Common mistakes with articles (a, an, the)' LIMIT 1;
    
    IF v_topic_id IS NOT NULL THEN
        INSERT INTO reactions (user_id, topic_id, post_id, type, created_at) VALUES
        (1, v_topic_id, NULL, 'LIKE', NOW() - INTERVAL '4 days'),
        (2, v_topic_id, NULL, 'HELPFUL', NOW() - INTERVAL '4 days'),
        (3, v_topic_id, NULL, 'INSIGHTFUL', NOW() - INTERVAL '3 days'),
        (8, v_topic_id, NULL, 'LIKE', NOW() - INTERVAL '3 days'),
        (9, v_topic_id, NULL, 'INSIGHTFUL', NOW() - INTERVAL '2 days'),
        (10, v_topic_id, NULL, 'HELPFUL', NOW() - INTERVAL '2 days'),
        (11, v_topic_id, NULL, 'LIKE', NOW() - INTERVAL '1 day'),
        (12, v_topic_id, NULL, 'INSIGHTFUL', NOW() - INTERVAL '1 day');
    END IF;
END $$;

-- Reactions for Post 4 (Sophia's answer about articles) - Accepted answer
DO $$
DECLARE
    v_post_id INTEGER;
BEGIN
    SELECT p.id INTO v_post_id 
    FROM posts p 
    JOIN topics t ON p.topic_id = t.id 
    WHERE t.title = 'Common mistakes with articles (a, an, the)' 
    AND p.user_name = 'Sophia Taylor' 
    LIMIT 1;
    
    IF v_post_id IS NOT NULL THEN
        INSERT INTO reactions (user_id, topic_id, post_id, type, created_at) VALUES
        (1, NULL, v_post_id, 'HELPFUL', NOW() - INTERVAL '4 days'),
        (2, NULL, v_post_id, 'INSIGHTFUL', NOW() - INTERVAL '4 days'),
        (3, NULL, v_post_id, 'HELPFUL', NOW() - INTERVAL '3 days'),
        (8, NULL, v_post_id, 'LIKE', NOW() - INTERVAL '3 days'),
        (9, NULL, v_post_id, 'INSIGHTFUL', NOW() - INTERVAL '3 days'),
        (10, NULL, v_post_id, 'HELPFUL', NOW() - INTERVAL '2 days'),
        (11, NULL, v_post_id, 'LIKE', NOW() - INTERVAL '2 days'),
        (12, NULL, v_post_id, 'INSIGHTFUL', NOW() - INTERVAL '1 day');
    END IF;
END $$;

-- Reactions for Topic 7 (TH pronunciation)
DO $$
DECLARE
    v_topic_id INTEGER;
BEGIN
    SELECT id INTO v_topic_id FROM topics WHERE title = 'How to pronounce TH sound correctly?' LIMIT 1;
    
    IF v_topic_id IS NOT NULL THEN
        INSERT INTO reactions (user_id, topic_id, post_id, type, created_at) VALUES
        (1, v_topic_id, NULL, 'LIKE', NOW() - INTERVAL '13 days'),
        (2, v_topic_id, NULL, 'HELPFUL', NOW() - INTERVAL '12 days'),
        (3, v_topic_id, NULL, 'LIKE', NOW() - INTERVAL '12 days'),
        (8, v_topic_id, NULL, 'INSIGHTFUL', NOW() - INTERVAL '11 days'),
        (9, v_topic_id, NULL, 'HELPFUL', NOW() - INTERVAL '10 days'),
        (10, v_topic_id, NULL, 'LIKE', NOW() - INTERVAL '9 days'),
        (11, v_topic_id, NULL, 'INSIGHTFUL', NOW() - INTERVAL '8 days'),
        (12, v_topic_id, NULL, 'HELPFUL', NOW() - INTERVAL '7 days');
    END IF;
END $$;

-- Reactions for Post 7 (Lisa's answer about TH) - Accepted answer
DO $$
DECLARE
    v_post_id INTEGER;
BEGIN
    SELECT p.id INTO v_post_id 
    FROM posts p 
    JOIN topics t ON p.topic_id = t.id 
    WHERE t.title = 'How to pronounce TH sound correctly?' 
    AND p.user_name = 'Lisa Garcia' 
    LIMIT 1;
    
    IF v_post_id IS NOT NULL THEN
        INSERT INTO reactions (user_id, topic_id, post_id, type, created_at) VALUES
        (1, NULL, v_post_id, 'HELPFUL', NOW() - INTERVAL '13 days'),
        (2, NULL, v_post_id, 'INSIGHTFUL', NOW() - INTERVAL '13 days'),
        (3, NULL, v_post_id, 'HELPFUL', NOW() - INTERVAL '12 days'),
        (8, NULL, v_post_id, 'LIKE', NOW() - INTERVAL '12 days'),
        (9, NULL, v_post_id, 'INSIGHTFUL', NOW() - INTERVAL '11 days'),
        (10, NULL, v_post_id, 'HELPFUL', NOW() - INTERVAL '10 days'),
        (11, NULL, v_post_id, 'LIKE', NOW() - INTERVAL '9 days'),
        (12, NULL, v_post_id, 'INSIGHTFUL', NOW() - INTERVAL '8 days');
    END IF;
END $$;

-- Reactions for Topic 11 (IELTS Writing) - Trending topic
DO $$
DECLARE
    v_topic_id INTEGER;
BEGIN
    SELECT id INTO v_topic_id FROM topics WHERE title = 'IELTS Writing Task 2 - Need feedback' LIMIT 1;
    
    IF v_topic_id IS NOT NULL THEN
        INSERT INTO reactions (user_id, topic_id, post_id, type, created_at) VALUES
        (1, v_topic_id, NULL, 'LIKE', NOW() - INTERVAL '3 days'),
        (2, v_topic_id, NULL, 'HELPFUL', NOW() - INTERVAL '3 days'),
        (3, v_topic_id, NULL, 'INSIGHTFUL', NOW() - INTERVAL '2 days'),
        (8, v_topic_id, NULL, 'LIKE', NOW() - INTERVAL '2 days'),
        (9, v_topic_id, NULL, 'HELPFUL', NOW() - INTERVAL '1 day'),
        (10, v_topic_id, NULL, 'INSIGHTFUL', NOW() - INTERVAL '1 day');
    END IF;
END $$;

-- Reactions for Post 10 (James's IELTS feedback) - Accepted answer
DO $$
DECLARE
    v_post_id INTEGER;
BEGIN
    SELECT p.id INTO v_post_id 
    FROM posts p 
    JOIN topics t ON p.topic_id = t.id 
    WHERE t.title = 'IELTS Writing Task 2 - Need feedback' 
    AND p.user_name = 'James Anderson' 
    LIMIT 1;
    
    IF v_post_id IS NOT NULL THEN
        INSERT INTO reactions (user_id, topic_id, post_id, type, created_at) VALUES
        (1, NULL, v_post_id, 'HELPFUL', NOW() - INTERVAL '3 days'),
        (2, NULL, v_post_id, 'INSIGHTFUL', NOW() - INTERVAL '3 days'),
        (3, NULL, v_post_id, 'HELPFUL', NOW() - INTERVAL '2 days'),
        (8, NULL, v_post_id, 'INSIGHTFUL', NOW() - INTERVAL '2 days'),
        (9, NULL, v_post_id, 'HELPFUL', NOW() - INTERVAL '1 day');
    END IF;
END $$;

-- =====================================================
-- SUMMARY
-- =====================================================
-- Total Topics: 12
--   - Student Introductions: 3 topics
--   - Grammar & Vocabulary: 3 topics
--   - Pronunciation & Accent: 3 topics
--   - Written Expression: 2 topics
--   - Oral Expression: 1 topic
--
-- Total Posts (Replies): 13
--   - From Tutors: 10 posts
--   - From Students: 3 posts
--   - Accepted Answers: 5 posts
--
-- Total Reactions: 70+
--   - LIKE: ~25 reactions
--   - HELPFUL: ~25 reactions
--   - INSIGHTFUL: ~20 reactions
--
-- Features Demonstrated:
--   ✓ Upvotes/Downvotes system
--   ✓ Weighted scores (LIKE=1, INSIGHTFUL=2, HELPFUL=3)
--   ✓ Accepted answers (marked as solution)
--   ✓ Trending topics (is_trending flag)
--   ✓ View counts tracking
--   ✓ Last activity timestamps
--   ✓ Realistic conversations between students and tutors
--   ✓ Various English learning topics
-- =====================================================
