-- Update existing categories and subcategories to English

-- Update Categories
UPDATE categories SET name = 'General', description = 'General discussions and announcements' WHERE name = 'Général';
UPDATE categories SET name = 'Language Discussions', description = 'Grammar, vocabulary, pronunciation and expression' WHERE name = 'Discussions linguistiques';
UPDATE categories SET name = 'Clubs', description = 'Join our thematic clubs' WHERE name = 'Clubs';
UPDATE categories SET name = 'Events', description = 'Workshops, competitions and meetups' WHERE name = 'Événements';
UPDATE categories SET name = 'Resources and Help', description = 'Share and find resources' WHERE name = 'Ressources et Aide';

-- Update SubCategories for General (category_id = 1)
UPDATE sub_categories SET name = 'Student Introductions', description = 'Introduce yourself, talk about your level and goals' WHERE name = 'Présentation des étudiants';
UPDATE sub_categories SET name = 'School Announcements', description = 'Important news, schedule changes, reminders' WHERE name = 'Annonces de l''école';
UPDATE sub_categories SET name = 'General Questions', description = 'For everything that doesn''t fit in another category' WHERE name = 'Questions générales';

-- Update SubCategories for Language Discussions (category_id = 2)
UPDATE sub_categories SET name = 'Grammar & Vocabulary', description = 'Ask questions about grammar, learn new words' WHERE name = 'Grammaire & Vocabulaire';
UPDATE sub_categories SET name = 'Pronunciation & Accent', description = 'Tips, tricks, recordings to improve pronunciation' WHERE name = 'Prononciation & Accent';
UPDATE sub_categories SET name = 'Written Expression', description = 'Share texts, essays to receive feedback' WHERE name = 'Expression écrite';
UPDATE sub_categories SET name = 'Oral Expression', description = 'Organize discussion sessions with other students' WHERE name = 'Expression orale';

-- Update SubCategories for Clubs (category_id = 3)
UPDATE sub_categories SET name = 'Reading Club', description = 'Discussion about books in English, recommendations' WHERE name = 'Club Lecture';
UPDATE sub_categories SET name = 'Movie / Series Club', description = 'Talk about movies and series in original version, analyze dialogues and vocabulary' WHERE name = 'Club Cinéma / Séries';
UPDATE sub_categories SET name = 'Conversation Club', description = 'Informal conversation groups to practice English' WHERE name = 'Club Conversation';
UPDATE sub_categories SET name = 'Culture & Travel Club', description = 'Discover the culture of English-speaking countries, share your experiences' WHERE name = 'Club Culture & Voyages';

-- Update SubCategories for Events (category_id = 4)
UPDATE sub_categories SET name = 'Workshops and Conferences', description = 'Discussions about school workshops, note sharing' WHERE name = 'Ateliers et conférences';
UPDATE sub_categories SET name = 'Competitions & Challenges', description = 'Vocabulary contests, quizzes, language games' WHERE name = 'Compétitions & Challenges';
UPDATE sub_categories SET name = 'Outings & Meetups', description = 'Event planning, cultural visits, language cafes' WHERE name = 'Sorties & Rencontres';

-- Update SubCategories for Resources and Help (category_id = 5)
UPDATE sub_categories SET name = 'Resource Sharing', description = 'Links, videos, books, podcasts in English' WHERE name = 'Partage de ressources';
UPDATE sub_categories SET name = 'Student Help', description = 'Tutoring, group reviews, Q&A' WHERE name = 'Aide entre étudiants';
