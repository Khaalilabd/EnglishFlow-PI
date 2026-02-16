-- Initialize Forum Categories and SubCategories

-- Insert Categories
INSERT INTO categories (name, description, icon, color, created_at, updated_at) VALUES
('General', 'General discussions and announcements', 'fa-home', 'primary', NOW(), NOW()),
('Language Discussions', 'Grammar, vocabulary, pronunciation and expression', 'fa-book-open', 'accent-navy', NOW(), NOW()),
('Clubs', 'Join our thematic clubs', 'fa-users', 'accent-orange', NOW(), NOW()),
('Events', 'Workshops, competitions and meetups', 'fa-calendar-alt', 'accent-red', NOW(), NOW()),
('Resources and Help', 'Share and find resources', 'fa-lightbulb', 'secondary', NOW(), NOW());

-- Insert SubCategories for General
INSERT INTO sub_categories (name, description, category_id, created_at, updated_at) VALUES
('Student Introductions', 'Introduce yourself, talk about your level and goals', 1, NOW(), NOW()),
('School Announcements', 'Important news, schedule changes, reminders', 1, NOW(), NOW()),
('General Questions', 'For everything that doesn''t fit in another category', 1, NOW(), NOW());

-- Insert SubCategories for Language Discussions
INSERT INTO sub_categories (name, description, category_id, created_at, updated_at) VALUES
('Grammar & Vocabulary', 'Ask questions about grammar, learn new words', 2, NOW(), NOW()),
('Pronunciation & Accent', 'Tips, tricks, recordings to improve pronunciation', 2, NOW(), NOW()),
('Written Expression', 'Share texts, essays to receive feedback', 2, NOW(), NOW()),
('Oral Expression', 'Organize discussion sessions with other students', 2, NOW(), NOW());

-- Insert SubCategories for Clubs
INSERT INTO sub_categories (name, description, category_id, created_at, updated_at) VALUES
('Reading Club', 'Discussion about books in English, recommendations', 3, NOW(), NOW()),
('Movie / Series Club', 'Talk about movies and series in original version, analyze dialogues and vocabulary', 3, NOW(), NOW()),
('Conversation Club', 'Informal conversation groups to practice English', 3, NOW(), NOW()),
('Culture & Travel Club', 'Discover the culture of English-speaking countries, share your experiences', 3, NOW(), NOW());

-- Insert SubCategories for Events
INSERT INTO sub_categories (name, description, category_id, created_at, updated_at) VALUES
('Workshops and Conferences', 'Discussions about school workshops, note sharing', 4, NOW(), NOW()),
('Competitions & Challenges', 'Vocabulary contests, quizzes, language games', 4, NOW(), NOW()),
('Outings & Meetups', 'Event planning, cultural visits, language cafes', 4, NOW(), NOW());

-- Insert SubCategories for Resources and Help
INSERT INTO sub_categories (name, description, category_id, created_at, updated_at) VALUES
('Resource Sharing', 'Links, videos, books, podcasts in English', 5, NOW(), NOW()),
('Student Help', 'Tutoring, group reviews, Q&A', 5, NOW(), NOW());
