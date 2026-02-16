-- Update existing topics to English
-- Note: This script translates common French topics to English
-- You may need to adjust based on your actual topic titles and content

-- Example translations for common topics
-- Replace the French titles with their English equivalents

-- Update topics with French titles to English
UPDATE topics SET 
    title = 'How to improve my pronunciation?',
    content = 'Hello everyone! I''m looking for tips and resources to improve my English pronunciation. What methods have worked for you?'
WHERE title = 'Comment améliorer ma prononciation ?';

UPDATE topics SET 
    title = 'Grammar question: Present Perfect vs Simple Past',
    content = 'I''m having trouble understanding when to use Present Perfect versus Simple Past. Can someone explain the difference with examples?'
WHERE title = 'Question de grammaire : Present Perfect vs Passé Simple';

UPDATE topics SET 
    title = 'Book recommendations for beginners',
    content = 'I''m looking for book recommendations suitable for beginners. What books helped you improve your English?'
WHERE title = 'Recommandations de livres pour débutants';

UPDATE topics SET 
    title = 'Conversation practice partners',
    content = 'Looking for conversation partners to practice speaking English. Anyone interested in regular practice sessions?'
WHERE title = 'Partenaires de pratique de conversation';

UPDATE topics SET 
    title = 'Best podcasts for learning English',
    content = 'What are your favorite podcasts for learning English? I''m looking for recommendations at intermediate level.'
WHERE title = 'Meilleurs podcasts pour apprendre l''anglais';

UPDATE topics SET 
    title = 'Introduction - New student here!',
    content = 'Hello everyone! I''m new to this school and excited to start learning. Looking forward to meeting you all!'
WHERE title = 'Présentation - Nouvel étudiant ici !';

UPDATE topics SET 
    title = 'Vocabulary building techniques',
    content = 'What techniques do you use to memorize new vocabulary? I''m looking for effective methods to expand my vocabulary.'
WHERE title = 'Techniques pour enrichir son vocabulaire';

UPDATE topics SET 
    title = 'Understanding idioms and expressions',
    content = 'English idioms are confusing! Can we share common idioms and their meanings? What are the most useful ones to know?'
WHERE title = 'Comprendre les expressions idiomatiques';

UPDATE topics SET 
    title = 'Movie recommendations for language learning',
    content = 'What movies do you recommend for improving English? Looking for films with clear dialogue and subtitles.'
WHERE title = 'Recommandations de films pour apprendre';

UPDATE topics SET 
    title = 'Writing practice and feedback',
    content = 'I''d like to practice my writing skills. Is anyone interested in exchanging texts for feedback and corrections?'
WHERE title = 'Pratique de l''écriture et retours';

-- Update posts (replies) to English
UPDATE posts SET 
    content = 'Great question! I recommend watching English movies with subtitles and repeating phrases out loud.'
WHERE content LIKE '%Je recommande de regarder des films%';

UPDATE posts SET 
    content = 'Thanks for sharing! This is very helpful.'
WHERE content LIKE '%Merci pour le partage%';

UPDATE posts SET 
    content = 'I agree with you. Practice is the key to improvement.'
WHERE content LIKE '%Je suis d''accord%pratique%';

UPDATE posts SET 
    content = 'Welcome to the community! Feel free to ask any questions.'
WHERE content LIKE '%Bienvenue%communauté%';

-- Note: The above are examples. You should customize this script based on your actual data.
-- To see all your topics, run: SELECT id, title, content FROM topics;
-- Then update this script with the actual French titles and their English translations.
