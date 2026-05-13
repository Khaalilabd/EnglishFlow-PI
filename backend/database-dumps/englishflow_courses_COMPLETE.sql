--
-- PostgreSQL database dump
--

\restrict db1pXChhBsBz10NalzgEDjGYFrdtuk1vs33svC0vtbImymgbHzUyVKyGX2EBeH4

-- Dumped from database version 16.12
-- Dumped by pg_dump version 16.12

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: availability_modification_request; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.availability_modification_request (id, tutor_id, tutor_name, tutor_email, reason, status, requested_at, reviewed_at, reviewer_id, reviewer_name, review_comment, proposed_availability) VALUES (3, 13, 'Teacher  Khalil', 'khalilabdelmoumen11@gmail.com', 'okokokoko', 'REJECTED', '2026-04-15 17:12:36.744157', '2026-04-15 17:12:45.736596', 1, 'Khalil Abdelmoumen', '', NULL);
INSERT INTO public.availability_modification_request (id, tutor_id, tutor_name, tutor_email, reason, status, requested_at, reviewed_at, reviewer_id, reviewer_name, review_comment, proposed_availability) VALUES (2, 13, 'Teacher  Khalil', 'khalilabdelmoumen11@gmail.com', 'sasasas', 'REJECTED', '2026-04-15 17:08:44.405474', '2026-04-15 17:12:48.210369', 1, 'Khalil Abdelmoumen', '', NULL);
INSERT INTO public.availability_modification_request (id, tutor_id, tutor_name, tutor_email, reason, status, requested_at, reviewed_at, reviewer_id, reviewer_name, review_comment, proposed_availability) VALUES (1, 13, 'Teacher  Khalil', 'khalilabdelmoumen11@gmail.com', 'trghrtyrtgr', 'APPROVED', '2026-04-15 17:05:25.287059', '2026-04-15 17:12:50.64166', 1, 'Khalil Abdelmoumen', '', NULL);
INSERT INTO public.availability_modification_request (id, tutor_id, tutor_name, tutor_email, reason, status, requested_at, reviewed_at, reviewer_id, reviewer_name, review_comment, proposed_availability) VALUES (4, 13, 'Teacher  Khalil', 'khalilabdelmoumen11@gmail.com', 'TESTETESTEST', 'APPROVED', '2026-04-15 17:20:43.389861', '2026-04-15 17:20:53.393789', 1, 'Khalil Abdelmoumen', '', '{"id":2,"tutorId":13,"tutorName":"Teacher  Khalil","availableDays":["TUESDAY","MONDAY"],"timeSlots":[{"id":3,"startTime":"10:00:00","endTime":"12:00:00"},{"id":4,"startTime":"15:00:00","endTime":"16:30:00"}],"maxStudentsCapacity":30,"currentStudentsCount":0,"availableCapacity":30,"capacityPercentage":0,"categories":["Business English","General English","Conversation"],"levels":["A1","B2","A2","C1"],"status":"AVAILABLE","lastUpdated":"2026-04-15T16:23:08.343487","createdAt":"2026-04-15T16:23:08.343487"}');


--
-- Data for Name: courses; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.courses (id, category, created_at, description, duration, file_url, is_featured, level, max_students, objectives, prerequisites, price, schedule, status, thumbnail_url, title, tutor_id, updated_at) VALUES (54, 'Grammar', '2026-05-09 15:33:39.381542', 'Master the basics of English grammar with clear explanations and practical exercises. This comprehensive course covers essential grammar topics including parts of speech, verb tenses, articles, and sentence structure. Perfect for beginners starting their English learning journey.', 40, NULL, true, 'A1', 30, 'Understand basic sentence structure, Learn present and past tenses, Master articles and prepositions, Build confidence in grammar usage, Recognize and use parts of speech correctly', 'No prior English knowledge required. Basic literacy skills needed.', 99.99, NULL, 'PUBLISHED', '/uploads/courses/thumbnails/grammar-fundamentals.jpg', 'English Grammar Fundamentals', 53, '2026-05-09 15:33:39.381542');
INSERT INTO public.courses (id, category, created_at, description, duration, file_url, is_featured, level, max_students, objectives, prerequisites, price, schedule, status, thumbnail_url, title, tutor_id, updated_at) VALUES (55, 'Business English', '2026-05-09 15:33:39.381542', 'Enhance your professional English skills for the workplace. This course focuses on practical business communication including email writing, presentations, meetings, and negotiations. Learn the vocabulary and expressions used in modern business environments.', 50, NULL, true, 'B2', 25, 'Write professional emails and reports, Conduct effective business meetings, Deliver confident presentations, Negotiate successfully, Use business vocabulary appropriately', 'Intermediate English level (B1 or higher). Basic understanding of business concepts helpful but not required.', 149.99, NULL, 'PUBLISHED', '/uploads/courses/thumbnails/business-english.jpg', 'Business English Communication', 53, '2026-05-09 15:33:39.381542');
INSERT INTO public.courses (id, category, created_at, description, duration, file_url, is_featured, level, max_students, objectives, prerequisites, price, schedule, status, thumbnail_url, title, tutor_id, updated_at) VALUES (1, 'General English', '2026-04-14 17:00:20.623271', 'Ceci est un test ', 10, NULL, false, 'A1', 30, '9f5f80a2-8a99f5f80a2-8a99f5f80a2-8a99f5f80a2-8a9', 'zefzefzefzef', 29.98, NULL, 'PUBLISHED', '/uploads/courses/thumbnails/076eb234-d380-4328-b2a8-fcdda4fa83e2.jpg', 'TEST Valdiation', 2, '2026-04-14 17:00:23.889977');
INSERT INTO public.courses (id, category, created_at, description, duration, file_url, is_featured, level, max_students, objectives, prerequisites, price, schedule, status, thumbnail_url, title, tutor_id, updated_at) VALUES (56, 'Pronunciation', '2026-05-09 15:33:39.381542', 'Improve your English pronunciation and accent with systematic phonetics training. Learn the International Phonetic Alphabet (IPA), master vowel and consonant sounds, and develop natural stress and intonation patterns. Includes live practice sessions with feedback.', 30, NULL, false, 'B1', 20, 'Master English phonetics and IPA, Produce all English sounds correctly, Improve accent and clarity, Learn stress and intonation patterns, Practice with native speaker feedback', 'Basic English speaking ability (A2 level). Willingness to practice speaking regularly.', 79.99, NULL, 'PUBLISHED', '/uploads/courses/thumbnails/pronunciation-mastery.jpg', 'English Pronunciation Mastery', 53, '2026-05-09 15:33:39.381542');
INSERT INTO public.courses (id, category, created_at, description, duration, file_url, is_featured, level, max_students, objectives, prerequisites, price, schedule, status, thumbnail_url, title, tutor_id, updated_at) VALUES (57, 'Vocabulary', '2026-05-09 15:33:39.381542', 'Expand your English vocabulary to advanced levels with 1000+ words, idioms, phrasal verbs, and collocations. This course focuses on academic vocabulary, idiomatic expressions, and natural word combinations that native speakers use. Perfect for those aiming for fluency and C1/C2 proficiency.', 35, NULL, true, 'C1', 30, 'Learn 1000+ advanced words and expressions, Master idioms and phrasal verbs, Understand context and usage nuances, Improve reading comprehension, Use collocations naturally', 'Upper-intermediate English (B2). Strong foundation in basic grammar and vocabulary.', 119.99, NULL, 'PUBLISHED', '/uploads/courses/thumbnails/vocabulary-builder.jpg', 'Advanced Vocabulary Builder', 53, '2026-05-09 15:33:39.381542');
INSERT INTO public.courses (id, category, created_at, description, duration, file_url, is_featured, level, max_students, objectives, prerequisites, price, schedule, status, thumbnail_url, title, tutor_id, updated_at) VALUES (58, 'Conversation', '2026-05-09 15:33:39.381542', 'Practice everyday English conversations in real-life situations. This interactive course covers common scenarios like shopping, dining, traveling, and socializing. Build confidence speaking English naturally through role-plays and live practice sessions.', 25, NULL, false, 'A2', 15, 'Speak confidently in daily situations, Understand native speakers better, Build conversational vocabulary, Practice listening skills, Handle common social interactions', 'Basic English knowledge (A1). Ability to form simple sentences.', 89.99, NULL, 'PUBLISHED', '/uploads/courses/thumbnails/conversational-english.jpg', 'Conversational English Practice', 53, '2026-05-09 15:33:39.381542');
INSERT INTO public.courses (id, category, created_at, description, duration, file_url, is_featured, level, max_students, objectives, prerequisites, price, schedule, status, thumbnail_url, title, tutor_id, updated_at) VALUES (33, 'Conversation', '2026-04-17 13:03:36.136605', 'Practice everyday English conversations in real-life situations. Build confidence speaking English naturally.', 25, NULL, false, 'A2', 15, 'Speak confidently in daily situations, Understand native speakers, Build conversational vocabulary, Practice listening skills', 'Basic English knowledge (A1)', 89.99, NULL, 'PUBLISHED', '/uploads/courses/thumbnails/conversational-english.jpg', 'Conversational English Practice', 1, '2026-04-17 13:03:36.136605');
INSERT INTO public.courses (id, category, created_at, description, duration, file_url, is_featured, level, max_students, objectives, prerequisites, price, schedule, status, thumbnail_url, title, tutor_id, updated_at) VALUES (49, 'Grammar', '2026-04-17 13:16:18.244243', 'Master the basics of English grammar with clear explanations and practical exercises. This comprehensive course covers essential grammar topics including parts of speech, verb tenses, articles, and sentence structure. Perfect for beginners starting their English learning journey.', 40, NULL, true, 'A1', 30, 'Understand basic sentence structure, Learn present and past tenses, Master articles and prepositions, Build confidence in grammar usage, Recognize and use parts of speech correctly', 'No prior English knowledge required. Basic literacy skills needed.', 99.99, NULL, 'PUBLISHED', '/uploads/courses/thumbnails/grammar-fundamentals.jpg', 'English Grammar Fundamentals', 13, '2026-04-17 13:16:18.244243');
INSERT INTO public.courses (id, category, created_at, description, duration, file_url, is_featured, level, max_students, objectives, prerequisites, price, schedule, status, thumbnail_url, title, tutor_id, updated_at) VALUES (50, 'Business English', '2026-04-17 13:16:18.244243', 'Enhance your professional English skills for the workplace. This course focuses on practical business communication including email writing, presentations, meetings, and negotiations. Learn the vocabulary and expressions used in modern business environments.', 50, NULL, true, 'B2', 25, 'Write professional emails and reports, Conduct effective business meetings, Deliver confident presentations, Negotiate successfully, Use business vocabulary appropriately', 'Intermediate English level (B1 or higher). Basic understanding of business concepts helpful but not required.', 149.99, NULL, 'PUBLISHED', '/uploads/courses/thumbnails/business-english.jpg', 'Business English Communication', 13, '2026-04-17 13:16:18.244243');
INSERT INTO public.courses (id, category, created_at, description, duration, file_url, is_featured, level, max_students, objectives, prerequisites, price, schedule, status, thumbnail_url, title, tutor_id, updated_at) VALUES (51, 'Pronunciation', '2026-04-17 13:16:18.244243', 'Improve your English pronunciation and accent with systematic phonetics training. Learn the International Phonetic Alphabet (IPA), master vowel and consonant sounds, and develop natural stress and intonation patterns. Includes live practice sessions with feedback.', 30, NULL, false, 'B1', 20, 'Master English phonetics and IPA, Produce all English sounds correctly, Improve accent and clarity, Learn stress and intonation patterns, Practice with native speaker feedback', 'Basic English speaking ability (A2 level). Willingness to practice speaking regularly.', 79.99, NULL, 'PUBLISHED', '/uploads/courses/thumbnails/pronunciation-mastery.jpg', 'English Pronunciation Mastery', 13, '2026-04-17 13:16:18.244243');
INSERT INTO public.courses (id, category, created_at, description, duration, file_url, is_featured, level, max_students, objectives, prerequisites, price, schedule, status, thumbnail_url, title, tutor_id, updated_at) VALUES (52, 'Vocabulary', '2026-04-17 13:16:18.244243', 'Expand your English vocabulary to advanced levels with 1000+ words, idioms, phrasal verbs, and collocations. This course focuses on academic vocabulary, idiomatic expressions, and natural word combinations that native speakers use. Perfect for those aiming for fluency and C1/C2 proficiency.', 35, NULL, true, 'C1', 30, 'Learn 1000+ advanced words and expressions, Master idioms and phrasal verbs, Understand context and usage nuances, Improve reading comprehension, Use collocations naturally', 'Upper-intermediate English (B2). Strong foundation in basic grammar and vocabulary.', 119.99, NULL, 'PUBLISHED', '/uploads/courses/thumbnails/vocabulary-builder.jpg', 'Advanced Vocabulary Builder', 13, '2026-04-17 13:16:18.244243');
INSERT INTO public.courses (id, category, created_at, description, duration, file_url, is_featured, level, max_students, objectives, prerequisites, price, schedule, status, thumbnail_url, title, tutor_id, updated_at) VALUES (53, 'Conversation', '2026-04-17 13:16:18.244243', 'Practice everyday English conversations in real-life situations. This interactive course covers common scenarios like shopping, dining, traveling, and socializing. Build confidence speaking English naturally through role-plays and live practice sessions.', 25, NULL, false, 'A2', 15, 'Speak confidently in daily situations, Understand native speakers better, Build conversational vocabulary, Practice listening skills, Handle common social interactions', 'Basic English knowledge (A1). Ability to form simple sentences.', 89.99, NULL, 'PUBLISHED', '/uploads/courses/thumbnails/conversational-english.jpg', 'Conversational English Practice', 13, '2026-04-17 13:16:18.244243');


--
-- Data for Name: chapters; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (1, '2026-04-14 17:15:32.37849', 'azdazdazd', 0, true, 0, 'TEST QUIZ ', '2026-04-14 17:15:32.37849', 1);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (2, '2026-04-15 00:09:43.0494', 'sdfdsfsdfsdf', 0, true, 1, 'Chapitre 2 ', '2026-04-15 00:09:43.0494', 1);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (161, '2026-05-09 15:33:39.381542', 'Learn the fundamental building blocks of English grammar including parts of speech and sentence structure. This chapter introduces you to the basic concepts that form the foundation of English grammar.', 180, true, 0, 'Introduction to English Grammar', '2026-05-09 15:33:39.381542', 54);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (162, '2026-05-09 15:33:39.381542', 'Master the present simple and present continuous tenses with practical examples and exercises. Learn when to use each tense and how to form them correctly.', 240, true, 1, 'Present Tenses', '2026-05-09 15:33:39.381542', 54);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (163, '2026-05-09 15:33:39.381542', 'Learn how to talk about past events using past simple and past continuous tenses. Practice telling stories and describing past experiences.', 240, true, 2, 'Past Tenses', '2026-05-09 15:33:39.381542', 54);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (164, '2026-05-09 15:33:39.381542', 'Understand when and how to use articles (a, an, the) and other determiners in English. Master one of the most challenging aspects of English grammar.', 180, true, 3, 'Articles and Determiners', '2026-05-09 15:33:39.381542', 54);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (165, '2026-05-09 15:33:39.381542', 'Learn to write clear, professional emails for various business situations. Master email structure, tone, and common phrases used in business correspondence.', 300, true, 0, 'Professional Email Writing', '2026-05-09 15:33:39.381542', 55);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (166, '2026-05-09 15:33:39.381542', 'Master the language and skills needed for effective meetings and presentations. Learn how to participate actively, lead discussions, and deliver compelling presentations.', 360, true, 1, 'Business Meetings and Presentations', '2026-05-09 15:33:39.381542', 55);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (167, '2026-05-09 15:33:39.381542', 'Develop negotiation skills and learn persuasive language for business contexts. Practice techniques for reaching agreements and handling objections.', 300, true, 2, 'Negotiations and Persuasion', '2026-05-09 15:33:39.381542', 55);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (168, '2026-05-09 15:33:39.381542', 'Build your professional network with effective communication strategies. Learn small talk, networking phrases, and relationship-building techniques.', 240, true, 3, 'Business Networking', '2026-05-09 15:33:39.381542', 55);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (169, '2026-05-09 15:33:39.381542', 'Introduction to English sounds, the phonetic alphabet, and pronunciation fundamentals. Learn the IPA and understand how English sounds are produced.', 180, true, 0, 'English Phonetics Basics', '2026-05-09 15:33:39.381542', 56);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (170, '2026-05-09 15:33:39.381542', 'Master all English vowel sounds including short vowels, long vowels, and diphthongs. Practice distinguishing between similar sounds.', 240, true, 1, 'Vowel Sounds', '2026-05-09 15:33:39.381542', 56);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (171, '2026-05-09 15:33:39.381542', 'Learn to produce all English consonant sounds accurately. Focus on challenging sounds for non-native speakers.', 240, true, 2, 'Consonant Sounds', '2026-05-09 15:33:39.381542', 56);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (172, '2026-05-09 15:33:39.381542', 'Understand word stress, sentence stress, and intonation patterns in English. Learn to sound more natural and native-like.', 180, true, 3, 'Stress and Intonation', '2026-05-09 15:33:39.381542', 56);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (173, '2026-05-09 15:33:39.381542', 'Learn essential academic words and expressions for formal contexts. Master the Academic Word List and formal register.', 240, true, 0, 'Academic Vocabulary', '2026-05-09 15:33:39.381542', 57);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (174, '2026-05-09 15:33:39.381542', 'Master common English idioms and colloquial expressions. Learn figurative language that native speakers use daily.', 300, true, 1, 'Idioms and Expressions', '2026-05-09 15:33:39.381542', 57);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (175, '2026-05-09 15:33:39.381542', 'Learn the most important phrasal verbs and how to use them correctly. Master this essential aspect of natural English.', 300, true, 2, 'Phrasal Verbs', '2026-05-09 15:33:39.381542', 57);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (176, '2026-05-09 15:33:39.381542', 'Discover word combinations that native speakers use naturally. Learn which words go together in English.', 240, true, 3, 'Advanced Collocations', '2026-05-09 15:33:39.381542', 57);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (77, '2026-04-17 13:03:36.136605', 'Practice common daily conversations and small talk.', 180, true, 0, 'Everyday Conversations', '2026-04-17 13:03:36.136605', 33);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (78, '2026-04-17 13:03:36.136605', 'Learn English for shopping, restaurants, and using services.', 180, true, 1, 'Shopping and Services', '2026-04-17 13:03:36.136605', 33);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (79, '2026-04-17 13:03:36.136605', 'Master English for traveling, asking directions, and using transportation.', 180, true, 2, 'Travel and Transportation', '2026-04-17 13:03:36.136605', 33);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (80, '2026-04-17 13:03:36.136605', 'Learn to navigate social events, make friends, and socialize in English.', 180, true, 3, 'Social Situations', '2026-04-17 13:03:36.136605', 33);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (177, '2026-05-09 15:33:39.381542', 'Practice common daily conversations and small talk. Learn greetings, introductions, and casual conversation topics.', 180, true, 0, 'Everyday Conversations', '2026-05-09 15:33:39.381542', 58);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (178, '2026-05-09 15:33:39.381542', 'Learn English for shopping, restaurants, and using services. Practice real-world scenarios you encounter daily.', 180, true, 1, 'Shopping and Services', '2026-05-09 15:33:39.381542', 58);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (179, '2026-05-09 15:33:39.381542', 'Master English for traveling, asking directions, and using transportation. Essential phrases for tourists and travelers.', 180, true, 2, 'Travel and Transportation', '2026-05-09 15:33:39.381542', 58);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (180, '2026-05-09 15:33:39.381542', 'Learn to navigate social events, make friends, and socialize in English. Build confidence in informal settings.', 180, true, 3, 'Social Situations', '2026-05-09 15:33:39.381542', 58);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (181, '2026-05-09 16:07:48.126336', '', 0, true, 4, 'Quiz Part', '2026-05-09 16:07:48.126336', 57);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (183, '2026-05-13 12:04:06.795642', 'Ceci est un TESt Ceci est un TESt Ceci est un TESt ', 0, true, 4, 'Ceci est un TESt ', '2026-05-13 12:04:06.795642', 54);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (141, '2026-04-17 13:16:18.244243', 'Learn the fundamental building blocks of English grammar including parts of speech and sentence structure. This chapter introduces you to the basic concepts that form the foundation of English grammar.', 180, true, 0, 'Introduction to English Grammar', '2026-04-17 13:16:18.244243', 49);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (142, '2026-04-17 13:16:18.244243', 'Master the present simple and present continuous tenses with practical examples and exercises. Learn when to use each tense and how to form them correctly.', 240, true, 1, 'Present Tenses', '2026-04-17 13:16:18.244243', 49);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (143, '2026-04-17 13:16:18.244243', 'Learn how to talk about past events using past simple and past continuous tenses. Practice telling stories and describing past experiences.', 240, true, 2, 'Past Tenses', '2026-04-17 13:16:18.244243', 49);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (144, '2026-04-17 13:16:18.244243', 'Understand when and how to use articles (a, an, the) and other determiners in English. Master one of the most challenging aspects of English grammar.', 180, true, 3, 'Articles and Determiners', '2026-04-17 13:16:18.244243', 49);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (145, '2026-04-17 13:16:18.244243', 'Learn to write clear, professional emails for various business situations. Master email structure, tone, and common phrases used in business correspondence.', 300, true, 0, 'Professional Email Writing', '2026-04-17 13:16:18.244243', 50);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (146, '2026-04-17 13:16:18.244243', 'Master the language and skills needed for effective meetings and presentations. Learn how to participate actively, lead discussions, and deliver compelling presentations.', 360, true, 1, 'Business Meetings and Presentations', '2026-04-17 13:16:18.244243', 50);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (147, '2026-04-17 13:16:18.244243', 'Develop negotiation skills and learn persuasive language for business contexts. Practice techniques for reaching agreements and handling objections.', 300, true, 2, 'Negotiations and Persuasion', '2026-04-17 13:16:18.244243', 50);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (148, '2026-04-17 13:16:18.244243', 'Build your professional network with effective communication strategies. Learn small talk, networking phrases, and relationship-building techniques.', 240, true, 3, 'Business Networking', '2026-04-17 13:16:18.244243', 50);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (149, '2026-04-17 13:16:18.244243', 'Introduction to English sounds, the phonetic alphabet, and pronunciation fundamentals. Learn the IPA and understand how English sounds are produced.', 180, true, 0, 'English Phonetics Basics', '2026-04-17 13:16:18.244243', 51);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (150, '2026-04-17 13:16:18.244243', 'Master all English vowel sounds including short vowels, long vowels, and diphthongs. Practice distinguishing between similar sounds.', 240, true, 1, 'Vowel Sounds', '2026-04-17 13:16:18.244243', 51);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (151, '2026-04-17 13:16:18.244243', 'Learn to produce all English consonant sounds accurately. Focus on challenging sounds for non-native speakers.', 240, true, 2, 'Consonant Sounds', '2026-04-17 13:16:18.244243', 51);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (152, '2026-04-17 13:16:18.244243', 'Understand word stress, sentence stress, and intonation patterns in English. Learn to sound more natural and native-like.', 180, true, 3, 'Stress and Intonation', '2026-04-17 13:16:18.244243', 51);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (153, '2026-04-17 13:16:18.244243', 'Learn essential academic words and expressions for formal contexts. Master the Academic Word List and formal register.', 240, true, 0, 'Academic Vocabulary', '2026-04-17 13:16:18.244243', 52);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (154, '2026-04-17 13:16:18.244243', 'Master common English idioms and colloquial expressions. Learn figurative language that native speakers use daily.', 300, true, 1, 'Idioms and Expressions', '2026-04-17 13:16:18.244243', 52);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (155, '2026-04-17 13:16:18.244243', 'Learn the most important phrasal verbs and how to use them correctly. Master this essential aspect of natural English.', 300, true, 2, 'Phrasal Verbs', '2026-04-17 13:16:18.244243', 52);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (156, '2026-04-17 13:16:18.244243', 'Discover word combinations that native speakers use naturally. Learn which words go together in English.', 240, true, 3, 'Advanced Collocations', '2026-04-17 13:16:18.244243', 52);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (157, '2026-04-17 13:16:18.244243', 'Practice common daily conversations and small talk. Learn greetings, introductions, and casual conversation topics.', 180, true, 0, 'Everyday Conversations', '2026-04-17 13:16:18.244243', 53);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (158, '2026-04-17 13:16:18.244243', 'Learn English for shopping, restaurants, and using services. Practice real-world scenarios you encounter daily.', 180, true, 1, 'Shopping and Services', '2026-04-17 13:16:18.244243', 53);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (159, '2026-04-17 13:16:18.244243', 'Master English for traveling, asking directions, and using transportation. Essential phrases for tourists and travelers.', 180, true, 2, 'Travel and Transportation', '2026-04-17 13:16:18.244243', 53);
INSERT INTO public.chapters (id, created_at, description, estimated_duration, is_published, order_index, title, updated_at, course_id) VALUES (160, '2026-04-17 13:16:18.244243', 'Learn to navigate social events, make friends, and socialize in English. Build confidence in informal settings.', 180, true, 3, 'Social Situations', '2026-04-17 13:16:18.244243', 53);


--
-- Data for Name: chapter_objectives; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (1, 'ada');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (2, 'AAAA');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (161, 'Identify all eight parts of speech');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (161, 'Understand sentence components (subject, verb, object)');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (161, 'Recognize basic grammar patterns');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (162, 'Use present simple correctly for habits and facts');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (162, 'Apply present continuous for ongoing actions');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (162, 'Distinguish between the two present tenses');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (163, 'Form past simple sentences correctly');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (163, 'Use past continuous for interrupted actions');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (163, 'Tell stories in the past tense');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (164, 'Use articles (a, an, the) correctly');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (164, 'Master determiners (this, that, some, any)');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (164, 'Avoid common article mistakes');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (165, 'Write formal business emails');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (165, 'Use appropriate professional tone');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (165, 'Structure business correspondence effectively');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (166, 'Lead meetings effectively in English');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (166, 'Deliver confident presentations');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (166, 'Use business vocabulary appropriately');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (167, 'Negotiate successfully in English');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (167, 'Use persuasive techniques');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (167, 'Handle objections professionally');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (168, 'Make effective small talk');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (168, 'Network professionally');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (168, 'Build business relationships');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (169, 'Read and use phonetic symbols (IPA)');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (169, 'Produce English sounds correctly');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (169, 'Understand sound differences');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (170, 'Pronounce all vowel sounds correctly');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (170, 'Distinguish similar vowel sounds');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (170, 'Practice vowels in context');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (171, 'Master all consonant sounds');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (171, 'Handle difficult consonant clusters');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (171, 'Improve overall clarity');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (172, 'Apply correct word stress');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (172, 'Use appropriate intonation');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (172, 'Sound more natural');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (173, 'Use academic language appropriately');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (173, 'Understand formal texts');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (173, 'Write academically');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (174, 'Understand common idioms');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (174, 'Use expressions naturally');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (174, 'Recognize figurative language');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (175, 'Master essential phrasal verbs');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (175, 'Use them in appropriate contexts');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (175, 'Understand multiple meanings');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (176, 'Use natural collocations');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (176, 'Sound more native-like');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (176, 'Expand active vocabulary');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (77, 'Make small talk');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (77, 'Have casual conversations');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (77, 'Build confidence');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (78, 'Order in restaurants');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (78, 'Shop confidently');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (78, 'Ask for help');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (79, 'Ask for directions');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (79, 'Book travel');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (79, 'Handle travel situations');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (80, 'Socialize confidently');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (80, 'Make invitations');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (80, 'Express opinions');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (177, 'Make effective small talk');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (177, 'Have casual conversations');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (177, 'Build speaking confidence');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (178, 'Order in restaurants confidently');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (178, 'Shop in English');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (178, 'Ask for help appropriately');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (179, 'Ask for and give directions');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (179, 'Book travel arrangements');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (179, 'Handle travel situations');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (180, 'Socialize confidently');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (180, 'Make and respond to invitations');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (180, 'Express opinions politely');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (141, 'Identify all eight parts of speech');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (141, 'Understand sentence components (subject, verb, object)');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (141, 'Recognize basic grammar patterns');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (142, 'Use present simple correctly for habits and facts');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (142, 'Apply present continuous for ongoing actions');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (142, 'Distinguish between the two present tenses');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (143, 'Form past simple sentences correctly');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (143, 'Use past continuous for interrupted actions');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (143, 'Tell stories in the past tense');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (144, 'Use articles (a, an, the) correctly');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (144, 'Master determiners (this, that, some, any)');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (144, 'Avoid common article mistakes');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (145, 'Write formal business emails');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (145, 'Use appropriate professional tone');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (145, 'Structure business correspondence effectively');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (146, 'Lead meetings effectively in English');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (146, 'Deliver confident presentations');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (146, 'Use business vocabulary appropriately');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (147, 'Negotiate successfully in English');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (147, 'Use persuasive techniques');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (147, 'Handle objections professionally');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (148, 'Make effective small talk');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (148, 'Network professionally');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (148, 'Build business relationships');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (149, 'Read and use phonetic symbols (IPA)');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (149, 'Produce English sounds correctly');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (149, 'Understand sound differences');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (150, 'Pronounce all vowel sounds correctly');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (150, 'Distinguish similar vowel sounds');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (150, 'Practice vowels in context');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (151, 'Master all consonant sounds');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (151, 'Handle difficult consonant clusters');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (151, 'Improve overall clarity');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (152, 'Apply correct word stress');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (152, 'Use appropriate intonation');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (152, 'Sound more natural');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (153, 'Use academic language appropriately');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (153, 'Understand formal texts');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (153, 'Write academically');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (154, 'Understand common idioms');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (154, 'Use expressions naturally');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (154, 'Recognize figurative language');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (155, 'Master essential phrasal verbs');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (155, 'Use them in appropriate contexts');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (155, 'Understand multiple meanings');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (156, 'Use natural collocations');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (156, 'Sound more native-like');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (156, 'Expand active vocabulary');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (157, 'Make effective small talk');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (157, 'Have casual conversations');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (157, 'Build speaking confidence');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (158, 'Order in restaurants confidently');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (158, 'Shop in English');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (158, 'Ask for help appropriately');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (159, 'Ask for and give directions');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (159, 'Book travel arrangements');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (159, 'Handle travel situations');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (160, 'Socialize confidently');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (160, 'Make and respond to invitations');
INSERT INTO public.chapter_objectives (chapter_id, objective) VALUES (160, 'Express opinions politely');


--
-- Data for Name: chapter_progress; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: course_categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.course_categories (id, active, color, created_at, created_by, description, display_order, icon, name, updated_at) VALUES (1, true, '#3B82F6', '2026-04-14 16:29:41.702973', 1, 'Comprehensive English language learning for all levels', 1, '📚', 'General English', '2026-04-14 16:29:41.702973');
INSERT INTO public.course_categories (id, active, color, created_at, created_by, description, display_order, icon, name, updated_at) VALUES (2, true, '#10B981', '2026-04-14 16:29:41.729493', 1, 'Professional English for workplace communication', 2, '💼', 'Business English', '2026-04-14 16:29:41.729493');
INSERT INTO public.course_categories (id, active, color, created_at, created_by, description, display_order, icon, name, updated_at) VALUES (3, true, '#F59E0B', '2026-04-14 16:29:41.731494', 1, 'Master English grammar rules and structures', 3, '📝', 'Grammar', '2026-04-14 16:29:41.731494');
INSERT INTO public.course_categories (id, active, color, created_at, created_by, description, display_order, icon, name, updated_at) VALUES (4, true, '#8B5CF6', '2026-04-14 16:29:41.734493', 1, 'Improve speaking and listening skills', 4, '💬', 'Conversation', '2026-04-14 16:29:41.734493');
INSERT INTO public.course_categories (id, active, color, created_at, created_by, description, display_order, icon, name, updated_at) VALUES (5, true, '#EC4899', '2026-04-14 16:29:41.737493', 1, 'Develop writing skills for various purposes', 5, '✍️', 'Writing', '2026-04-14 16:29:41.737493');
INSERT INTO public.course_categories (id, active, color, created_at, created_by, description, display_order, icon, name, updated_at) VALUES (6, true, '#EF4444', '2026-04-14 16:29:41.741018', 1, 'Prepare for IELTS, TOEFL, and other exams', 6, '🎯', 'Exam Preparation', '2026-04-14 16:29:41.741018');
INSERT INTO public.course_categories (id, active, color, created_at, created_by, description, display_order, icon, name, updated_at) VALUES (7, true, '#06B6D4', '2026-04-14 16:29:41.74601', 1, 'Expand your English vocabulary', 7, '📖', 'Vocabulary', '2026-04-14 16:29:41.74601');
INSERT INTO public.course_categories (id, active, color, created_at, created_by, description, display_order, icon, name, updated_at) VALUES (8, true, '#F97316', '2026-04-14 16:29:41.748009', 1, 'Perfect your English pronunciation', 8, '🗣️', 'Pronunciation', '2026-04-14 16:29:41.748009');
INSERT INTO public.course_categories (id, active, color, created_at, created_by, description, display_order, icon, name, updated_at) VALUES (9, true, '#84CC16', '2026-04-14 16:29:41.751009', 1, 'Fun English learning for children', 9, '🎨', 'Kids English', '2026-04-14 16:29:41.751009');
INSERT INTO public.course_categories (id, active, color, created_at, created_by, description, display_order, icon, name, updated_at) VALUES (10, true, '#6366F1', '2026-04-14 16:29:41.753009', 1, 'English for academic and research purposes', 10, '🎓', 'Academic English', '2026-04-14 16:29:41.753009');


--
-- Data for Name: course_enrollments; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (1, '2026-04-14 17:22:30.590498', '2026-04-14 17:22:12.302842', true, '2026-04-14 17:22:30.590498', 3, 1, 1);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (2, NULL, '2026-04-15 02:12:11.126038', true, '2026-04-15 02:12:11.126038', 1, 2, 1);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (10, NULL, '2026-04-09 13:03:36.136605', true, '2026-04-17 13:03:36.136605', 15, 5, 33);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (13, NULL, '2026-05-09 15:56:55.055343', true, '2026-05-09 15:56:55.055343', 1, 17, 57);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (14, NULL, '2026-05-09 15:56:55.079343', true, '2026-05-09 15:56:55.079343', 1, 16, 56);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (11, '2026-05-13 13:25:14.460343', '2026-05-09 15:45:44.983718', true, '2026-05-13 13:25:14.966921', 1, 19, 54);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (12, '2026-05-13 14:14:21.221233', '2026-05-09 15:45:45.00772', true, '2026-05-13 14:14:21.222234', 1, 17, 58);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (15, NULL, '2026-05-13 15:46:06.738835', true, NULL, 57, 16, 56);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (16, NULL, '2026-05-13 15:46:06.738835', true, NULL, 57, 17, 57);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (17, NULL, '2026-05-13 15:46:06.738835', true, NULL, 58, 16, 56);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (18, NULL, '2026-05-13 15:46:06.738835', true, NULL, 58, 17, 57);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (19, NULL, '2026-05-13 15:46:06.738835', true, NULL, 59, 16, 56);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (20, NULL, '2026-05-13 15:46:06.738835', true, NULL, 59, 17, 57);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (21, NULL, '2026-05-13 15:46:06.738835', true, NULL, 60, 16, 56);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (22, NULL, '2026-05-13 15:46:06.738835', true, NULL, 60, 17, 57);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (23, NULL, '2026-05-13 15:46:06.738835', true, NULL, 61, 16, 56);
INSERT INTO public.course_enrollments (id, completed_at, enrolled_at, is_active, last_accessed_at, student_id, total_lessons, course_id) VALUES (24, NULL, '2026-05-13 15:46:06.738835', true, NULL, 61, 17, 57);


--
-- Data for Name: lessons; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (1, '', '1', '2026-04-14 17:15:39.196353', 'azdazdazd', 0, false, true, 'QUIZ', 0, 'azdazdazd', '2026-04-14 17:15:39.196353', 1, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (117, 'Complete this quiz to assess your understanding of the basic parts of speech.', NULL, '2026-04-17 14:24:44.830428', 'Test your knowledge of nouns, verbs, adjectives, and adverbs', 15, false, true, 'QUIZ', 99, 'Parts of Speech Quiz', '2026-04-17 14:24:44.830428', 141, 1);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (118, 'Test your understanding of present tenses.', NULL, '2026-04-17 14:24:44.830428', 'Master present simple and present continuous', 20, false, true, 'QUIZ', 99, 'Present Tenses Quiz', '2026-04-17 14:24:44.830428', 142, 2);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (119, 'Assess your business email writing abilities.', NULL, '2026-04-17 14:24:44.830428', 'Test your business email skills', 25, false, true, 'QUIZ', 99, 'Professional Email Writing Quiz', '2026-04-17 14:24:44.830428', 145, 3);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (120, 'Test your presentation skills.', NULL, '2026-04-17 14:24:44.830428', 'Master presentation language', 20, false, true, 'QUIZ', 99, 'Business Presentations Quiz', '2026-04-17 14:24:44.830428', 146, 4);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (121, 'Assess your phonetics understanding.', NULL, '2026-04-17 14:24:44.830428', 'Test your pronunciation knowledge', 15, false, true, 'QUIZ', 99, 'English Phonetics Quiz', '2026-04-17 14:24:44.830428', 149, 5);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (122, 'Test your intonation knowledge.', NULL, '2026-04-17 14:24:44.830428', 'Master rising and falling intonation', 15, false, true, 'QUIZ', 99, 'Intonation Patterns Quiz', '2026-04-17 14:24:44.830428', 152, 6);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (123, 'Assess your understanding of idioms.', NULL, '2026-04-17 14:24:44.830428', 'Test your idiom knowledge', 20, false, true, 'QUIZ', 99, 'English Idioms Quiz', '2026-04-17 14:24:44.830428', 154, 7);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (124, 'Test your academic vocabulary.', NULL, '2026-04-17 14:24:44.830428', 'Master advanced vocabulary', 25, false, true, 'QUIZ', 99, 'Academic Vocabulary Quiz', '2026-04-17 14:24:44.830428', 153, 8);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (11, '', '', '2026-04-15 02:09:30.135127', 'EN LIGNR 1', 0, false, true, 'ONLINE', 0, 'EN LIGNR 1', '2026-04-15 02:09:30.135127', 2, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (125, 'Test your daily conversation skills.', NULL, '2026-04-17 14:24:44.830428', 'Practice everyday phrases', 15, false, true, 'QUIZ', 99, 'Daily Conversations Quiz', '2026-04-17 14:24:44.830428', 157, 9);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (126, 'Assess your social situation handling.', NULL, '2026-04-17 14:24:44.830428', 'Navigate social contexts', 15, false, true, 'QUIZ', 99, 'Social Situations Quiz', '2026-04-17 14:24:44.830428', 160, 10);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (127, 'Welcome to English Grammar Fundamentals! In this course, you will learn the essential building blocks of English grammar. We will cover parts of speech, sentence structure, verb tenses, and much more.', '', '2026-05-09 15:41:15.86456', 'An introduction to the course and what you will learn about English grammar.', 15, true, true, 'VIDEO', 0, 'Welcome to English Grammar', '2026-05-09 15:41:15.86456', 161, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (128, 'The eight parts of speech are the categories that words belong to based on their function in a sentence: nouns, pronouns, verbs, adjectives, adverbs, prepositions, conjunctions, and interjections.', '', '2026-05-09 15:41:15.86456', 'Learn about the eight parts of speech in English.', 25, false, true, 'VIDEO', 1, 'Parts of Speech Overview', '2026-05-09 15:41:15.86456', 161, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (129, 'Nouns are words that name people, places, things, or ideas. Pronouns are words that take the place of nouns to avoid repetition. Examples: he, she, it, they, we.', '', '2026-05-09 15:41:15.86456', 'Understand nouns and pronouns in detail.', 20, false, true, 'DOCUMENT', 2, 'Nouns and Pronouns', '2026-05-09 15:41:15.86456', 161, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (130, 'Verbs are action words or state-of-being words. English has three main tenses: past, present, and future. Each tense has different forms.', '', '2026-05-09 15:41:15.86456', 'Introduction to verbs and the concept of tenses in English.', 30, false, true, 'VIDEO', 3, 'Verbs and Tenses Introduction', '2026-05-09 15:41:15.86456', 161, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (131, 'Quiz covering nouns, pronouns, verbs, adjectives, and adverbs.', '', '2026-05-09 15:41:15.86456', 'Test your understanding of parts of speech.', 15, false, true, 'QUIZ', 4, 'Parts of Speech Quiz', '2026-05-09 15:41:15.86456', 161, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (132, 'The present simple tense is used for habits, routines, facts, and general truths. Form: Subject + base verb (add -s/-es for third person singular).', '', '2026-05-09 15:41:15.86456', 'Learn how to form and use the present simple tense.', 30, false, true, 'VIDEO', 0, 'Present Simple Tense', '2026-05-09 15:41:15.86456', 162, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (133, 'Practice forming present simple sentences with various subjects and verbs.', '', '2026-05-09 15:41:15.86456', 'Interactive exercises to practice present simple tense.', 25, false, true, 'INTERACTIVE', 1, 'Present Simple Practice', '2026-05-09 15:41:15.86456', 162, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (134, 'The present continuous describes actions happening right now or temporary situations. Form: Subject + am/is/are + verb-ing.', '', '2026-05-09 15:41:15.86456', 'Master the present continuous tense.', 30, false, true, 'VIDEO', 2, 'Present Continuous Tense', '2026-05-09 15:41:15.86456', 162, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (135, 'Understanding the difference between these two tenses is crucial for accurate communication.', '', '2026-05-09 15:41:15.86456', 'Learn when to use present simple versus present continuous.', 20, false, true, 'DOCUMENT', 3, 'Present Simple vs Continuous', '2026-05-09 15:41:15.86456', 162, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (136, 'Write 10 sentences using present simple and 10 using present continuous correctly.', '', '2026-05-09 15:41:15.86456', 'Complete exercises using both present tenses.', 30, false, true, 'ASSIGNMENT', 4, 'Present Tenses Assignment', '2026-05-09 15:41:15.86456', 162, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (137, 'The past simple is used for completed actions in the past. Regular verbs add -ed, irregular verbs have special forms.', '', '2026-05-09 15:41:15.86456', 'Learn how to form and use the past simple tense.', 30, false, true, 'VIDEO', 0, 'Past Simple Tense', '2026-05-09 15:41:15.86456', 163, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (138, 'Learn the past forms of common irregular verbs like go-went, eat-ate, see-saw.', '', '2026-05-09 15:41:15.86456', 'Master common irregular past tense verbs.', 25, false, true, 'DOCUMENT', 1, 'Irregular Verbs', '2026-05-09 15:41:15.86456', 163, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (139, 'The past continuous describes actions in progress at a specific time in the past. Form: Subject + was/were + verb-ing.', '', '2026-05-09 15:41:15.86456', 'Understand the past continuous tense.', 30, false, true, 'VIDEO', 2, 'Past Continuous Tense', '2026-05-09 15:41:15.86456', 163, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (140, 'Combine past simple and past continuous to tell engaging stories.', '', '2026-05-09 15:41:15.86456', 'Learn to narrate past events effectively.', 25, false, true, 'VIDEO', 3, 'Telling Stories in the Past', '2026-05-09 15:41:15.86456', 163, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (141, 'Quiz covering past simple and past continuous usage.', '', '2026-05-09 15:41:15.86456', 'Test your knowledge of past tenses.', 20, false, true, 'QUIZ', 4, 'Past Tenses Quiz', '2026-05-09 15:41:15.86456', 163, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (142, 'Articles are words that define nouns. A/an are indefinite (any one), the is definite (specific one).', '', '2026-05-09 15:41:15.86456', 'Learn when to use a, an, and the.', 25, false, true, 'VIDEO', 0, 'Definite and Indefinite Articles', '2026-05-09 15:41:15.86456', 164, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (143, 'Some nouns do not need articles in certain contexts, like plural general statements and abstract nouns.', '', '2026-05-09 15:41:15.86456', 'Understand when NOT to use articles.', 20, false, true, 'DOCUMENT', 1, 'Zero Article', '2026-05-09 15:41:15.86456', 164, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (144, 'Determiners specify which or how many nouns we are talking about.', '', '2026-05-09 15:41:15.86456', 'Learn other determiners like this, that, some, any.', 25, false, true, 'VIDEO', 2, 'Demonstratives and Quantifiers', '2026-05-09 15:41:15.86456', 164, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (145, 'Fill in the blanks with the correct article or leave blank if no article is needed.', '', '2026-05-09 15:41:15.86456', 'Interactive exercises on article usage.', 30, false, true, 'INTERACTIVE', 3, 'Articles Practice Exercises', '2026-05-09 15:41:15.86456', 164, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (146, 'A professional email has: subject line, greeting, opening, body, closing, and signature.', '', '2026-05-09 15:41:15.86456', 'Learn the proper structure of professional business emails.', 20, true, true, 'VIDEO', 0, 'Email Structure and Format', '2026-05-09 15:41:15.86456', 165, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (147, 'The tone should match your relationship with the recipient and the context.', '', '2026-05-09 15:41:15.86456', 'Understand when to use formal or informal language in emails.', 25, false, true, 'VIDEO', 1, 'Formal vs Informal Tone', '2026-05-09 15:41:15.86456', 165, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (148, 'Master phrases for opening, requesting, apologizing, and closing emails professionally.', '', '2026-05-09 15:41:15.86456', 'Learn useful phrases for different email situations.', 15, false, true, 'DOCUMENT', 2, 'Common Email Phrases', '2026-05-09 15:41:15.86456', 165, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (149, 'Write an email requesting information or assistance from a colleague or client.', '', '2026-05-09 15:41:15.86456', 'Practice writing a professional request email.', 30, false, true, 'ASSIGNMENT', 3, 'Writing Practice: Request Email', '2026-05-09 15:41:15.86456', 165, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (150, 'Quiz on email structure, tone, and appropriate phrases.', '', '2026-05-09 15:41:15.86456', 'Test your knowledge of professional email writing.', 15, false, true, 'QUIZ', 4, 'Email Writing Quiz', '2026-05-09 15:41:15.86456', 165, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (48, 'Master words and phrases for shopping.', '', '2026-04-17 13:03:36.136605', 'Learn essential vocabulary for shopping.', 20, false, true, 'VIDEO', 0, 'Shopping Vocabulary', '2026-04-17 13:03:36.136605', 78, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (49, 'Practice making reservations and ordering.', '', '2026-04-17 13:03:36.136605', 'Learn how to order food and drinks.', 25, false, true, 'VIDEO', 1, 'At the Restaurant', '2026-04-17 13:03:36.136605', 78, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (50, 'Interactive online session with role-play.', '', '2026-04-17 13:03:36.136605', 'Practice restaurant conversations.', 45, false, true, 'ONLINE', 2, 'Restaurant Role-Play', '2026-04-17 13:03:36.136605', 78, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (51, 'Practice asking for help and filling out forms.', '', '2026-04-17 13:03:36.136605', 'Learn English for banks and post offices.', 20, false, true, 'DOCUMENT', 3, 'Using Services', '2026-04-17 13:03:36.136605', 78, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (52, 'Write a conversation between a customer and shop assistant.', '', '2026-04-17 13:03:36.136605', 'Create and record a shopping dialogue.', 30, false, true, 'ASSIGNMENT', 4, 'Shopping Dialogue Assignment', '2026-04-17 13:03:36.136605', 78, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (151, 'Learn phrases for opening meetings, giving opinions, agreeing/disagreeing, and closing.', '', '2026-05-09 15:41:15.86456', 'Essential vocabulary for business meetings.', 25, false, true, 'VIDEO', 0, 'Meeting Vocabulary and Phrases', '2026-05-09 15:41:15.86456', 166, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (152, 'Introduction, main points, transitions, conclusion, and Q&A.', '', '2026-05-09 15:41:15.86456', 'How to structure an effective business presentation.', 30, false, true, 'VIDEO', 1, 'Presentation Structure', '2026-05-09 15:41:15.86456', 166, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (153, 'Signposting language, emphasizing points, referring to visuals.', '', '2026-05-09 15:41:15.86456', 'Key phrases for delivering presentations.', 20, false, true, 'DOCUMENT', 2, 'Presentation Language', '2026-05-09 15:41:15.86456', 166, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (154, 'Create a 5-minute presentation on a business topic of your choice.', '', '2026-05-09 15:41:15.86456', 'Prepare and deliver a short presentation.', 45, false, true, 'ASSIGNMENT', 3, 'Practice Presentation', '2026-05-09 15:41:15.86456', 166, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (155, 'Learn the principles of win-win negotiation and key strategies.', '', '2026-05-09 15:41:15.86456', 'Introduction to business negotiation techniques.', 25, false, true, 'VIDEO', 0, 'Negotiation Basics', '2026-05-09 15:41:15.86456', 167, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (156, 'Modal verbs, conditional sentences, and rhetorical questions.', '', '2026-05-09 15:41:15.86456', 'Language techniques for persuasion.', 25, false, true, 'VIDEO', 1, 'Persuasive Language', '2026-05-09 15:41:15.86456', 167, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (157, 'Techniques for addressing concerns and finding common ground.', '', '2026-05-09 15:41:15.86456', 'How to respond to objections professionally.', 20, false, true, 'DOCUMENT', 2, 'Handling Objections', '2026-05-09 15:41:15.86456', 167, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (158, 'Participate in a negotiation role-play exercise.', '', '2026-05-09 15:41:15.86456', 'Practice negotiation in a simulated scenario.', 45, false, true, 'ONLINE', 3, 'Negotiation Role-Play', '2026-05-09 15:41:15.86456', 167, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (159, 'Topics, questions, and phrases for networking situations.', '', '2026-05-09 15:41:15.86456', 'Master the art of professional small talk.', 20, false, true, 'VIDEO', 0, 'Small Talk in Business', '2026-05-09 15:41:15.86456', 168, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (160, 'How to introduce yourself and make connections at business events.', '', '2026-05-09 15:41:15.86456', 'Professional introduction techniques.', 20, false, true, 'VIDEO', 1, 'Introducing Yourself and Others', '2026-05-09 15:41:15.86456', 168, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (161, 'Email templates and strategies for following up after meetings.', '', '2026-05-09 15:41:15.86456', 'How to maintain professional relationships.', 15, false, true, 'DOCUMENT', 2, 'Following Up After Networking', '2026-05-09 15:41:15.86456', 168, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (162, 'Phonetics is the study of speech sounds and how they are produced.', '', '2026-05-09 15:41:15.86456', 'Learn what phonetics is and why it matters.', 20, true, true, 'VIDEO', 0, 'Introduction to Phonetics', '2026-05-09 15:41:15.86456', 169, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (163, 'The IPA is a system of symbols that represent speech sounds universally.', '', '2026-05-09 15:41:15.86456', 'Learn to read and use the IPA.', 30, false, true, 'VIDEO', 1, 'The International Phonetic Alphabet', '2026-05-09 15:41:15.86456', 169, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (164, 'English has approximately 44 sounds: vowels, diphthongs, and consonants.', '', '2026-05-09 15:41:15.86456', 'Overview of all English sounds.', 25, false, true, 'DOCUMENT', 2, 'English Sound System Overview', '2026-05-09 15:41:15.86456', 169, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (165, 'Interactive session with feedback on your pronunciation.', '', '2026-05-09 15:41:15.86456', 'Live online session to practice sounds.', 45, false, true, 'ONLINE', 3, 'Pronunciation Practice Session', '2026-05-09 15:41:15.86456', 169, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (166, 'Practice sounds like /?/, /e/, /?/, /?/, /?/.', '', '2026-05-09 15:41:15.86456', 'Master the short vowel sounds in English.', 30, false, true, 'VIDEO', 0, 'Short Vowel Sounds', '2026-05-09 15:41:15.86456', 170, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (167, 'Practice sounds like /i?/, /??/, /??/, /u?/, /??/.', '', '2026-05-09 15:41:15.86456', 'Learn the long vowel sounds.', 30, false, true, 'VIDEO', 1, 'Long Vowel Sounds', '2026-05-09 15:41:15.86456', 170, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (168, 'Two vowel sounds combined: /e?/, /a?/, /??/, /a?/, /??/.', '', '2026-05-09 15:41:15.86456', 'Master English diphthongs.', 25, false, true, 'VIDEO', 2, 'Diphthongs', '2026-05-09 15:41:15.86456', 170, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (169, 'Listen and repeat exercises for all vowel sounds.', '', '2026-05-09 15:41:15.86456', 'Interactive vowel pronunciation practice.', 30, false, true, 'INTERACTIVE', 3, 'Vowel Practice Exercises', '2026-05-09 15:41:15.86456', 170, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (170, 'Learn pairs like /p/-/b/, /t/-/d/, /k/-/g/, /f/-/v/, /s/-/z/.', '', '2026-05-09 15:41:15.86456', 'Understand the difference between voiced and voiceless sounds.', 25, false, true, 'VIDEO', 0, 'Voiced and Voiceless Consonants', '2026-05-09 15:41:15.86456', 171, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (171, 'Focus on /?/, /?/, /r/, /l/, /w/, /v/.', '', '2026-05-09 15:41:15.86456', 'Master challenging consonants for non-native speakers.', 30, false, true, 'VIDEO', 1, 'Difficult Consonant Sounds', '2026-05-09 15:41:15.86456', 171, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (172, 'Practice clusters like str-, spr-, -nts, -ths.', '', '2026-05-09 15:41:15.86456', 'Learn to pronounce consonant combinations.', 20, false, true, 'DOCUMENT', 2, 'Consonant Clusters', '2026-05-09 15:41:15.86456', 171, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (173, 'Interactive session focusing on your specific pronunciation challenges.', '', '2026-05-09 15:41:15.86456', 'Live practice with feedback.', 45, false, true, 'ONLINE', 3, 'Consonant Practice Session', '2026-05-09 15:41:15.86456', 171, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (174, 'Rules and patterns for word stress in English.', '', '2026-05-09 15:41:15.86456', 'Learn where to stress syllables in words.', 25, false, true, 'VIDEO', 0, 'Word Stress Patterns', '2026-05-09 15:41:15.86456', 172, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (175, 'Content words vs function words, and how stress affects meaning.', '', '2026-05-09 15:41:15.86456', 'Understand which words to emphasize in sentences.', 25, false, true, 'VIDEO', 1, 'Sentence Stress', '2026-05-09 15:41:15.86456', 172, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (176, 'How intonation changes meaning in questions, statements, and emotions.', '', '2026-05-09 15:41:15.86456', 'Master rising and falling intonation.', 25, false, true, 'VIDEO', 2, 'Intonation Patterns', '2026-05-09 15:41:15.86456', 172, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (177, 'Linking, elision, and weak forms in natural English.', '', '2026-05-09 15:41:15.86456', 'Sound more natural with connected speech.', 20, false, true, 'DOCUMENT', 3, 'Rhythm and Connected Speech', '2026-05-09 15:41:15.86456', 172, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (178, 'Learn the most common words used in academic texts across disciplines.', '', '2026-05-09 15:41:15.86456', 'Introduction to high-frequency academic words.', 20, false, true, 'VIDEO', 0, 'Academic Word List Introduction', '2026-05-09 15:41:15.86456', 173, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (179, 'Verbs like analyze, evaluate, demonstrate, illustrate, synthesize.', '', '2026-05-09 15:41:15.86456', 'Master essential academic verbs.', 30, false, true, 'VIDEO', 1, 'Academic Verbs', '2026-05-09 15:41:15.86456', 173, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (180, 'Common academic nouns and adjectives for formal writing.', '', '2026-05-09 15:41:15.86456', 'Build your academic vocabulary.', 25, false, true, 'DOCUMENT', 2, 'Academic Nouns and Adjectives', '2026-05-09 15:41:15.86456', 173, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (181, 'Write a short academic paragraph using new vocabulary.', '', '2026-05-09 15:41:15.86456', 'Apply academic vocabulary in writing.', 35, false, true, 'ASSIGNMENT', 3, 'Academic Writing Practice', '2026-05-09 15:41:15.86456', 173, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (182, 'Idioms are expressions whose meaning cannot be understood from the individual words.', '', '2026-05-09 15:41:15.86456', 'Introduction to idiomatic expressions in English.', 20, false, true, 'VIDEO', 0, 'What Are Idioms?', '2026-05-09 15:41:15.86456', 174, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (183, 'Master idioms like "piece of cake", "break the ice", "hit the nail on the head".', '', '2026-05-09 15:41:15.86456', 'Learn 50 frequently used idioms.', 35, false, true, 'VIDEO', 1, 'Common Everyday Idioms', '2026-05-09 15:41:15.86456', 174, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (184, 'Read dialogues and watch videos that use idioms naturally.', '', '2026-05-09 15:41:15.86456', 'See how idioms are used in real conversations.', 25, false, true, 'TEXT', 2, 'Idioms in Context', '2026-05-09 15:41:15.86456', 174, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (185, 'Complete sentences and match idioms to meanings.', '', '2026-05-09 15:41:15.86456', 'Interactive exercises to practice using idioms.', 30, false, true, 'INTERACTIVE', 3, 'Idiom Practice Exercises', '2026-05-09 15:41:15.86456', 174, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (186, 'Quiz covering meanings and usage of common idioms.', '', '2026-05-09 15:41:15.86456', 'Test your knowledge of English idioms.', 20, false, true, 'QUIZ', 4, 'Idioms Quiz', '2026-05-09 15:41:15.86456', 174, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (187, 'Phrasal verbs are verbs combined with prepositions or adverbs that create new meanings.', '', '2026-05-09 15:41:15.86456', 'What are phrasal verbs and why are they important?', 20, false, true, 'VIDEO', 0, 'Introduction to Phrasal Verbs', '2026-05-09 15:41:15.86456', 175, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (188, 'Some phrasal verbs can be separated, others cannot.', '', '2026-05-09 15:41:15.86456', 'Learn the grammar rules for phrasal verbs.', 25, false, true, 'VIDEO', 1, 'Separable vs Inseparable Phrasal Verbs', '2026-05-09 15:41:15.86456', 175, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (189, 'Learn phrasal verbs organized by topic: work, relationships, communication.', '', '2026-05-09 15:41:15.86456', 'Master 100 essential phrasal verbs.', 40, false, true, 'DOCUMENT', 2, 'Common Phrasal Verbs', '2026-05-09 15:41:15.86456', 175, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (190, 'Fill in the blanks and choose the correct phrasal verb.', '', '2026-05-09 15:41:15.86456', 'Interactive exercises with phrasal verbs.', 30, false, true, 'INTERACTIVE', 3, 'Phrasal Verb Practice', '2026-05-09 15:41:15.86456', 175, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (191, 'Collocations are words that naturally go together in English.', '', '2026-05-09 15:41:15.86456', 'Introduction to word combinations.', 20, false, true, 'VIDEO', 0, 'What Are Collocations?', '2026-05-09 15:41:15.86456', 176, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (192, 'Examples: make a decision, take a break, do homework, have a meeting.', '', '2026-05-09 15:41:15.86456', 'Common verb-noun combinations.', 30, false, true, 'VIDEO', 1, 'Verb + Noun Collocations', '2026-05-09 15:41:15.86456', 176, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (193, 'Examples: strong coffee, heavy rain, deep sleep, bright future.', '', '2026-05-09 15:41:15.86456', 'Natural adjective-noun pairs.', 25, false, true, 'DOCUMENT', 2, 'Adjective + Noun Collocations', '2026-05-09 15:41:15.86456', 176, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (194, 'Exercises to help you remember and use collocations correctly.', '', '2026-05-09 15:41:15.86456', 'Practice using collocations naturally.', 30, false, true, 'INTERACTIVE', 3, 'Collocation Practice', '2026-05-09 15:41:15.86456', 176, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (195, 'Formal and informal greetings, asking and answering "How are you?".', '', '2026-05-09 15:41:15.86456', 'How to greet people and introduce yourself.', 20, false, true, 'VIDEO', 0, 'Greetings and Introductions', '2026-05-09 15:41:15.86456', 177, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (196, 'Safe topics: weather, hobbies, weekend plans, current events.', '', '2026-05-09 15:41:15.86456', 'Learn to make small talk confidently.', 25, false, true, 'VIDEO', 1, 'Small Talk Topics', '2026-05-09 15:41:15.86456', 177, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (197, 'Open and closed questions, follow-up questions, showing interest.', '', '2026-05-09 15:41:15.86456', 'Practice question forms in conversation.', 20, false, true, 'DOCUMENT', 2, 'Asking and Answering Questions', '2026-05-09 15:41:15.86456', 177, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (198, 'Interactive online session for real conversation practice.', '', '2026-05-09 15:41:15.86456', 'Live practice with other students.', 45, false, true, 'ONLINE', 3, 'Conversation Practice Session', '2026-05-09 15:41:15.86456', 177, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (199, 'Words and phrases for clothes shopping, grocery shopping, asking for help.', '', '2026-05-09 15:41:15.86456', 'Learn essential vocabulary for shopping.', 20, false, true, 'VIDEO', 0, 'Shopping Vocabulary', '2026-05-09 15:41:15.86456', 178, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (200, 'Making reservations, ordering, asking about ingredients, paying the bill.', '', '2026-05-09 15:41:15.86456', 'Learn how to order food and drinks.', 25, false, true, 'VIDEO', 1, 'At the Restaurant', '2026-05-09 15:41:15.86456', 178, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (201, 'Interactive online session with role-play scenarios.', '', '2026-05-09 15:41:15.86456', 'Practice restaurant conversations.', 45, false, true, 'ONLINE', 2, 'Restaurant Role-Play', '2026-05-09 15:41:15.86456', 178, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (202, 'Practice asking for help, filling out forms, making complaints.', '', '2026-05-09 15:41:15.86456', 'Learn English for banks, post offices, and services.', 20, false, true, 'DOCUMENT', 3, 'Using Services', '2026-05-09 15:41:15.86456', 178, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (203, 'Write a conversation between a customer and shop assistant, then record it.', '', '2026-05-09 15:41:15.86456', 'Create and record a shopping dialogue.', 30, false, true, 'ASSIGNMENT', 4, 'Shopping Dialogue Assignment', '2026-05-09 15:41:15.86456', 178, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (204, 'Check-in, security, boarding, asking for directions in airports.', '', '2026-05-09 15:41:15.86456', 'Essential English for air travel.', 25, false, true, 'VIDEO', 0, 'At the Airport', '2026-05-09 15:41:15.86456', 179, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (205, 'Prepositions of place, landmarks, understanding directions.', '', '2026-05-09 15:41:15.86456', 'How to ask for and give directions.', 20, false, true, 'VIDEO', 1, 'Asking for Directions', '2026-05-09 15:41:15.86456', 179, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (206, 'Buying tickets, asking about schedules, understanding announcements.', '', '2026-05-09 15:41:15.86456', 'Using buses, trains, and taxis.', 20, false, true, 'DOCUMENT', 2, 'Public Transportation', '2026-05-09 15:41:15.86456', 179, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (207, 'Making reservations, checking in, requesting services, complaints.', '', '2026-05-09 15:41:15.86456', 'English for hotel stays.', 25, false, true, 'VIDEO', 3, 'Hotel Check-in and Check-out', '2026-05-09 15:41:15.86456', 179, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (208, 'Conversation starters, showing interest, making plans.', '', '2026-05-09 15:41:15.86456', 'How to start and maintain friendships.', 20, false, true, 'VIDEO', 0, 'Making Friends', '2026-05-09 15:41:15.86456', 180, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (209, 'Phrases for inviting, accepting, declining politely.', '', '2026-05-09 15:41:15.86456', 'How to invite people and make suggestions.', 20, false, true, 'VIDEO', 1, 'Invitations and Suggestions', '2026-05-09 15:41:15.86456', 180, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (210, 'Agreeing, disagreeing politely, giving reasons.', '', '2026-05-09 15:41:15.86456', 'How to share your thoughts and ideas.', 20, false, true, 'DOCUMENT', 2, 'Expressing Opinions', '2026-05-09 15:41:15.86456', 180, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (211, 'Live online session with various social scenarios.', '', '2026-05-09 15:41:15.86456', 'Practice social English in real situations.', 45, false, true, 'ONLINE', 3, 'Social Conversation Practice', '2026-05-09 15:41:15.86456', 180, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (212, '', '', '2026-05-09 16:08:04.651946', 'Quiz 2 ', 0, false, true, 'QUIZ', 0, 'Quiz 1 ', '2026-05-09 16:08:04.651946', 181, 11);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (73, 'Welcome to English Grammar Fundamentals!

In this comprehensive course, you will learn the essential building blocks of English grammar. Grammar is the foundation of language learning, and mastering it will help you communicate more effectively and confidently.

What you will learn:
• The eight parts of speech and their functions
• How to construct correct sentences
• Present and past tenses
• Articles and determiners
• Common grammar patterns

This course is designed for beginners (A1 level) and requires no prior knowledge of English grammar. Each lesson includes clear explanations, examples, and practice exercises.

Let''s begin your journey to mastering English grammar!', '', '2026-04-17 13:41:32.944434', 'An introduction to the course and what you will learn about English grammar.', 15, true, true, 'VIDEO', 0, 'Welcome to English Grammar', '2026-04-17 13:41:32.944434', 141, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (74, 'The Eight Parts of Speech

Every word in English belongs to one of eight categories called "parts of speech." Understanding these categories is essential for building correct sentences.

1. NOUNS - Words that name people, places, things, or ideas
   Examples: teacher, London, book, happiness
   
2. PRONOUNS - Words that replace nouns
   Examples: I, you, he, she, it, they, we
   
3. VERBS - Action words or state-of-being words
   Examples: run, eat, is, have, think
   
4. ADJECTIVES - Words that describe nouns
   Examples: beautiful, tall, red, happy
   
5. ADVERBS - Words that describe verbs, adjectives, or other adverbs
   Examples: quickly, very, well, often
   
6. PREPOSITIONS - Words that show relationships between nouns
   Examples: in, on, at, under, between
   
7. CONJUNCTIONS - Words that connect other words or sentences
   Examples: and, but, or, because, although
   
8. INTERJECTIONS - Words that express emotion
   Examples: Oh! Wow! Ouch! Hey!

Practice identifying parts of speech in sentences to improve your grammar skills.', '', '2026-04-17 13:41:32.944434', 'Learn about the eight parts of speech in English: nouns, verbs, adjectives, adverbs, pronouns, prepositions, conjunctions, and interjections.', 25, false, true, 'VIDEO', 1, 'Parts of Speech Overview', '2026-04-17 13:41:32.944434', 141, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (75, 'Nouns and Pronouns in Detail

NOUNS
Nouns are words that name people, places, things, or ideas. There are several types:

1. Common Nouns - General names (not capitalized)
   Examples: dog, city, car, teacher
   
2. Proper Nouns - Specific names (always capitalized)
   Examples: London, John, Microsoft, Monday
   
3. Concrete Nouns - Things you can see or touch
   Examples: table, apple, computer
   
4. Abstract Nouns - Ideas or concepts
   Examples: love, freedom, happiness, time
   
5. Countable Nouns - Can be counted (have plural forms)
   Examples: book/books, cat/cats, idea/ideas
   
6. Uncountable Nouns - Cannot be counted (no plural form)
   Examples: water, rice, information, advice

PRONOUNS
Pronouns replace nouns to avoid repetition.

Subject Pronouns: I, you, he, she, it, we, they
Object Pronouns: me, you, him, her, it, us, them
Possessive Pronouns: mine, yours, his, hers, ours, theirs
Possessive Adjectives: my, your, his, her, its, our, their

Example:
"John loves his dog. He walks it every day."
(He = John, it = dog)

Practice using nouns and pronouns correctly in your writing and speaking.', '', '2026-04-17 13:41:32.944434', 'Understand nouns (people, places, things) and pronouns (words that replace nouns).', 20, false, true, 'DOCUMENT', 2, 'Nouns and Pronouns', '2026-04-17 13:41:32.944434', 141, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (76, 'Understanding Verbs and Tenses

WHAT ARE VERBS?
Verbs are the most important part of a sentence. They express actions or states of being.

Action Verbs: run, eat, write, think, speak
State Verbs: be, have, know, like, want

VERB TENSES
English has three main time frames, each with four aspects:

PRESENT TENSES:
• Present Simple: I work
• Present Continuous: I am working
• Present Perfect: I have worked
• Present Perfect Continuous: I have been working

PAST TENSES:
• Past Simple: I worked
• Past Continuous: I was working
• Past Perfect: I had worked
• Past Perfect Continuous: I had been working

FUTURE TENSES:
• Future Simple: I will work
• Future Continuous: I will be working
• Future Perfect: I will have worked
• Future Perfect Continuous: I will have been working

In this course, we will focus on the most common tenses:
- Present Simple and Present Continuous
- Past Simple and Past Continuous

Understanding when to use each tense is key to speaking and writing English correctly.

Example:
"I work in an office." (Present Simple - routine)
"I am working on a project." (Present Continuous - happening now)
"I worked yesterday." (Past Simple - completed action)
"I was working when you called." (Past Continuous - interrupted action)', '', '2026-04-17 13:41:32.944434', 'Introduction to verbs and the concept of tenses in English.', 30, false, true, 'VIDEO', 3, 'Verbs and Tenses Introduction', '2026-04-17 13:41:32.944434', 141, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (77, 'Present Simple Tense - Complete Guide

WHEN TO USE:
1. Habits and routines
   "I drink coffee every morning."
   
2. Facts and general truths
   "The sun rises in the east."
   "Water boils at 100°C."
   
3. Permanent situations
   "She lives in Paris."
   "He works as a teacher."
   
4. Scheduled events
   "The train leaves at 6 PM."

HOW TO FORM:

Positive:
I/You/We/They + base verb
He/She/It + base verb + s/es

Examples:
"I work in an office."
"She works in a hospital."
"They play football."

Negative:
I/You/We/They + do not (don''t) + base verb
He/She/It + does not (doesn''t) + base verb

Examples:
"I don''t like coffee."
"He doesn''t speak French."

Questions:
Do + I/you/we/they + base verb?
Does + he/she/it + base verb?

Examples:
"Do you like pizza?"
"Does she work here?"

SPELLING RULES FOR THIRD PERSON (he/she/it):
• Most verbs: add -s (work → works, play → plays)
• Verbs ending in -s, -sh, -ch, -x, -o: add -es (watch → watches, go → goes)
• Verbs ending in consonant + y: change y to i and add -es (study → studies)
• Irregular: have → has

TIME EXPRESSIONS:
always, usually, often, sometimes, rarely, never
every day/week/month/year
on Mondays, in the morning, at night

Practice forming present simple sentences with different subjects!', '', '2026-04-17 13:41:32.944434', 'Learn how to form and use the present simple tense for habits, facts, and routines.', 30, false, true, 'VIDEO', 0, 'Present Simple Tense', '2026-04-17 13:41:32.944434', 142, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (78, 'Present Simple Practice Exercises

Complete these exercises to master the present simple tense:

EXERCISE 1: Fill in the blanks with the correct form of the verb
1. She _____ (work) in a bank.
2. They _____ (not/like) spicy food.
3. _____ you _____ (speak) English?
4. He _____ (watch) TV every evening.
5. We _____ (not/have) a car.

EXERCISE 2: Correct the mistakes
1. He don''t like coffee. → _____
2. Does they live here? → _____
3. She work in an office. → _____
4. I doesn''t understand. → _____
5. Do she speak French? → _____

EXERCISE 3: Make questions
1. You like pizza. → _____?
2. She works here. → _____?
3. They have a dog. → _____?
4. He speaks Spanish. → _____?
5. You know the answer. → _____?

EXERCISE 4: Write about your daily routine
Use present simple to describe what you do every day. Include:
- What time you wake up
- What you eat for breakfast
- Where you work or study
- What you do in the evening
- What time you go to bed

Example:
"I wake up at 7 AM every day. I eat cereal for breakfast. I work in an office from 9 to 5. In the evening, I watch TV or read a book. I go to bed at 11 PM."

Practice makes perfect! Complete these exercises and check your answers.', '', '2026-04-17 13:41:32.944434', 'Interactive exercises to practice present simple tense.', 25, false, true, 'INTERACTIVE', 1, 'Present Simple Practice', '2026-04-17 13:41:32.944434', 142, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (79, 'Present Continuous Tense - Complete Guide

WHEN TO USE:
1. Actions happening right now
   "I am writing an email." (right now)
   
2. Temporary situations
   "She is living in London this year." (temporary)
   
3. Future arrangements
   "We are meeting tomorrow at 3 PM." (planned future)
   
4. Changing situations
   "The weather is getting colder." (gradual change)
   
5. Annoying habits (with "always")
   "He is always complaining!" (criticism)

HOW TO FORM:

Positive:
Subject + am/is/are + verb-ing

Examples:
"I am working."
"She is studying."
"They are playing."

Negative:
Subject + am not/isn''t/aren''t + verb-ing

Examples:
"I am not working."
"He isn''t studying."
"They aren''t playing."

Questions:
Am/Is/Are + subject + verb-ing?

Examples:
"Are you working?"
"Is she studying?"
"Are they playing?"

SPELLING RULES FOR -ING:
• Most verbs: add -ing (work → working, play → playing)
• Verbs ending in -e: remove e, add -ing (make → making, write → writing)
• One syllable verbs ending in consonant-vowel-consonant: double the last consonant (run → running, sit → sitting)
• Verbs ending in -ie: change ie to y (lie → lying, die → dying)

TIME EXPRESSIONS:
now, right now, at the moment, currently
today, this week, this month, this year
Look! Listen! (to draw attention)

STATIVE VERBS (NOT used in continuous):
know, understand, believe, like, love, hate, want, need, prefer, remember, forget, seem, belong

Wrong: "I am knowing the answer."
Right: "I know the answer."

Practice using present continuous for actions happening now!', '', '2026-04-17 13:41:32.944434', 'Master the present continuous tense for actions happening now.', 30, false, true, 'VIDEO', 2, 'Present Continuous Tense', '2026-04-17 13:41:32.944434', 142, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (80, 'Present Simple vs Present Continuous - Key Differences

Understanding when to use each tense is crucial for accurate communication.

PRESENT SIMPLE:
✓ Permanent situations
✓ Habits and routines
✓ Facts and general truths
✓ Scheduled events

Examples:
"I live in New York." (permanent)
"She works every day." (routine)
"The Earth orbits the Sun." (fact)
"The train leaves at 6 PM." (schedule)

PRESENT CONTINUOUS:
✓ Actions happening now
✓ Temporary situations
✓ Future arrangements
✓ Changing situations

Examples:
"I am living in a hotel this week." (temporary)
"She is working on a project." (happening now)
"We are meeting tomorrow." (future plan)
"Prices are rising." (change)

COMPARISON EXAMPLES:

1. Permanent vs Temporary:
   "I work in London." (permanent job)
   "I am working in London this month." (temporary assignment)

2. Routine vs Now:
   "He reads books." (general habit)
   "He is reading a book." (right now)

3. General vs Specific:
   "She teaches English." (her profession)
   "She is teaching a class." (at this moment)

4. Always true vs Currently true:
   "Water boils at 100°C." (always)
   "The water is boiling." (right now)

COMMON MISTAKES:

Wrong: "I am understanding English."
Right: "I understand English."
(understand is a stative verb)

Wrong: "She is having a car."
Right: "She has a car."
(have for possession is stative)

Wrong: "What do you do now?"
Right: "What are you doing now?"
(asking about current action)

PRACTICE:
Choose the correct tense:
1. I _____ (work/am working) in an office. [permanent]
2. She _____ (works/is working) on a report now. [current]
3. They _____ (live/are living) in Paris. [permanent]
4. We _____ (have/are having) dinner at 7 PM tonight. [future plan]
5. He _____ (studies/is studying) English every day. [routine]

Master these differences to speak English more naturally!', '', '2026-04-17 13:41:32.944434', 'Learn when to use present simple versus present continuous.', 20, false, true, 'DOCUMENT', 3, 'Present Simple vs Continuous', '2026-04-17 13:41:32.944434', 142, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (81, 'Present Tenses Assignment

Complete this assignment to demonstrate your understanding of present simple and present continuous tenses.

PART 1: Fill in the blanks (10 points)
Choose present simple or present continuous:

1. Right now, I _____ (sit) in a café and _____ (drink) coffee.
2. My sister _____ (work) as a nurse. She _____ (love) her job.
3. Look! It _____ (rain) outside.
4. We usually _____ (go) to the gym on Mondays, but today we _____ (stay) home.
5. _____ you _____ (understand) the lesson?
6. The children _____ (play) in the garden at the moment.
7. She _____ (not/eat) meat. She _____ (be) vegetarian.
8. What _____ you _____ (do) right now?
9. The sun _____ (rise) in the east and _____ (set) in the west.
10. This week, I _____ (study) for my exams.

PART 2: Correct the mistakes (10 points)
Find and correct the errors:

1. I am knowing the answer.
2. She is having a beautiful house.
3. Do you understanding me?
4. They are live in London.
5. He works on a project now.
6. I am not believing you.
7. Are you want some coffee?
8. She is teach English.
9. We are needing help.
10. He is have lunch now.

PART 3: Translation (10 points)
Translate these sentences to English:

1. Je travaille dans un bureau. (permanent)
2. Je travaille sur un projet maintenant. (current)
3. Elle étudie l''anglais tous les jours. (routine)
4. Ils regardent la télévision en ce moment. (now)
5. Nous habitons à Paris. (permanent)

PART 4: Writing (20 points)
Write two short paragraphs (50-75 words each):

Paragraph 1: Describe your typical day using present simple
Include: your routine, habits, what you usually do

Paragraph 2: Describe what you are doing right now using present continuous
Include: current actions, temporary situations, what''s happening around you

SUBMISSION:
Complete all parts and submit your assignment for review. You will receive feedback within 48 hours.

Total Points: 50
Passing Score: 35/50 (70%)

Good luck!', '', '2026-04-17 13:41:32.944434', 'Complete exercises using both present tenses.', 30, false, true, 'ASSIGNMENT', 4, 'Present Tenses Assignment', '2026-04-17 13:41:32.944434', 142, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (82, 'Professional Email Structure

A well-structured email makes a professional impression and ensures clear communication.

ESSENTIAL COMPONENTS:

1. SUBJECT LINE
   • Clear and specific
   • Summarizes the email purpose
   • Examples:
     ✓ "Meeting Request: Q4 Budget Review"
     ✓ "Follow-up: Project Proposal Discussion"
     ✗ "Hi" or "Question" (too vague)

2. GREETING/SALUTATION
   Formal:
   • Dear Mr./Ms. [Last Name],
   • Dear Sir/Madam, (if name unknown)
   
   Semi-formal:
   • Hello [First Name],
   • Hi [First Name],
   
   Note: Use comma (,) in American English, colon (:) in British English

3. OPENING LINE
   • State your purpose immediately
   • Examples:
     "I am writing to inquire about..."
     "Thank you for your email regarding..."
     "I would like to request..."

4. BODY
   • Keep paragraphs short (2-3 sentences)
   • Use bullet points for lists
   • One main idea per paragraph
   • Be clear and concise

5. CLOSING LINE
   • Summarize or call to action
   • Examples:
     "I look forward to hearing from you."
     "Please let me know if you need any further information."
     "Thank you for your time and consideration."

6. SIGN-OFF
   Formal:
   • Yours sincerely, (if you know the name)
   • Yours faithfully, (if you don''t know the name)
   • Best regards,
   • Kind regards,
   
   Semi-formal:
   • Best,
   • Thanks,
   • Regards,

7. SIGNATURE
   [Your Full Name]
   [Your Position]
   [Company Name]
   [Contact Information]

EXAMPLE EMAIL:

Subject: Meeting Request: Marketing Strategy Discussion

Dear Ms. Johnson,

I hope this email finds you well. I am writing to request a meeting to discuss our Q4 marketing strategy.

I would like to propose the following:
• Date: Next Tuesday, October 15th
• Time: 2:00 PM - 3:00 PM
• Location: Conference Room B

Please let me know if this time works for you, or suggest an alternative that suits your schedule.

I look forward to hearing from you.

Best regards,

John Smith
Marketing Manager
ABC Corporation
john.smith@abc.com
+1 (555) 123-4567

FORMATTING TIPS:
• Use a professional font (Arial, Calibri, Times New Roman)
• Font size: 10-12 pt
• Avoid colors, emojis, or fancy formatting
• Proofread before sending
• Keep it concise (aim for 3-5 short paragraphs)

Practice writing emails with this structure!', '', '2026-04-17 13:41:32.944434', 'Learn the proper structure of professional business emails.', 20, true, true, 'VIDEO', 0, 'Email Structure and Format', '2026-04-17 13:41:32.944434', 145, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (92, 'Welcome to English Grammar Fundamentals!

In this comprehensive course, you will learn the essential building blocks of English grammar. Grammar is the foundation of language learning, and mastering it will help you communicate more effectively and confidently.

What you will learn:
• The eight parts of speech and their functions
• How to construct correct sentences
• Present and past tenses
• Articles and determiners
• Common grammar patterns

This course is designed for beginners (A1 level) and requires no prior knowledge of English grammar. Each lesson includes clear explanations, examples, and practice exercises.

Let''s begin your journey to mastering English grammar!', '', '2026-04-17 13:44:45.285944', 'An introduction to the course and what you will learn about English grammar.', 15, true, true, 'VIDEO', 0, 'Welcome to English Grammar', '2026-04-17 13:44:45.285944', 141, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (216, '', '', '2026-05-13 12:06:23.252549', 'adazdazdazdazda', 0, false, true, 'QUIZ', 0, 'qsdazdazdazd', '2026-05-13 12:06:23.252549', 183, 13);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (83, 'Email Tone: Formal vs Informal

Choosing the right tone is crucial for effective business communication.

WHEN TO USE FORMAL TONE:

1. First contact with someone
2. Writing to senior management
3. Official requests or complaints
4. Legal or contractual matters
5. External clients or partners
6. Job applications

FORMAL LANGUAGE CHARACTERISTICS:
• Complete sentences
• No contractions (write "I am" not "I''m")
• Polite, respectful language
• Professional vocabulary
• Proper grammar and punctuation

FORMAL PHRASES:
Opening:
• I am writing to...
• I would like to inquire about...
• Thank you for your email dated...
• Further to our conversation...

Requesting:
• I would appreciate it if you could...
• Would it be possible to...
• I would be grateful if you could...
• Could you please...

Closing:
• I look forward to hearing from you.
• Thank you for your time and consideration.
• Please do not hesitate to contact me.
• I remain at your disposal.

WHEN TO USE INFORMAL TONE:

1. Colleagues you know well
2. Internal team communications
3. Follow-up emails
4. Casual updates
5. After establishing a relationship

INFORMAL LANGUAGE CHARACTERISTICS:
• Contractions allowed (I''m, you''re, we''ll)
• Shorter sentences
• Friendly, conversational tone
• Simple vocabulary
• Can be more direct

INFORMAL PHRASES:
Opening:
• Thanks for your email.
• Just wanted to let you know...
• Quick question about...
• Hope you''re doing well.

Requesting:
• Can you...?
• Could you...?
• Would you mind...?
• Let me know if...

Closing:
• Talk soon!
• Thanks!
• Cheers,
• Have a great day!

COMPARISON EXAMPLES:

FORMAL:
"Dear Mr. Brown,

I am writing to request your assistance with the Johnson account. Would it be possible for you to provide the quarterly reports by Friday?

I would greatly appreciate your help with this matter.

Yours sincerely,
Sarah"

INFORMAL:
"Hi Tom,

Hope you''re well! Can you send me the Johnson account reports by Friday? I need them for the meeting.

Thanks!
Sarah"

SEMI-FORMAL (Most Common):
"Hello Tom,

I hope this email finds you well. Could you please send me the Johnson account reports by Friday? I need them for the upcoming meeting.

Thank you for your help.

Best regards,
Sarah"

TONE MISTAKES TO AVOID:

Too Formal (sounds stiff):
"I hereby request that you forward the aforementioned documents at your earliest convenience."

Better:
"Could you please send the documents when you have a chance?"

Too Informal (unprofessional):
"Hey! Send me those docs ASAP!!!"

Better:
"Hi! Could you send me those documents soon? Thanks!"

TIPS FOR CHOOSING THE RIGHT TONE:

1. Consider your relationship with the recipient
2. Think about the company culture
3. Match the tone of previous emails
4. When in doubt, err on the side of formal
5. Adjust your tone as the relationship develops

Practice writing emails in different tones!', '', '2026-04-17 13:41:32.944434', 'Understand when to use formal or informal language in emails.', 25, false, true, 'VIDEO', 1, 'Formal vs Informal Tone', '2026-04-17 13:41:32.944434', 145, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (84, 'Essential Business Email Phrases

Master these phrases for professional email communication.

OPENING PHRASES:

Acknowledging Receipt:
• Thank you for your email.
• I acknowledge receipt of your email dated...
• Thank you for getting in touch.
• I have received your email regarding...

Referring to Previous Contact:
• Further to our conversation...
• Following our meeting yesterday...
• As discussed in our phone call...
• With reference to your email...

Introducing Yourself:
• My name is [Name] and I am...
• I am writing on behalf of...
• I am the [position] at [company]...
• Allow me to introduce myself...

MAKING REQUESTS:

Polite Requests:
• I would appreciate it if you could...
• Would it be possible to...?
• I would be grateful if you could...
• Could you please...?
• Would you mind...?

Urgent Requests:
• I would appreciate your prompt attention to this matter.
• This is quite urgent, so I would appreciate...
• Due to the urgency of this matter...
• I would be grateful for a quick response.

PROVIDING INFORMATION:

• I am writing to inform you that...
• I would like to let you know that...
• Please be advised that...
• I am pleased to inform you that...
• I regret to inform you that...

ASKING FOR INFORMATION:

• I would like to inquire about...
• Could you please provide information on...?
• I am interested in learning more about...
• I would appreciate more details about...
• Could you clarify...?

MAKING SUGGESTIONS:

• I would suggest that...
• May I suggest...?
• Perhaps we could...
• How about...?
• Would it be possible to...?

APOLOGIZING:

• I apologize for...
• Please accept my apologies for...
• I am sorry for any inconvenience caused.
• I regret that...
• Unfortunately, I must apologize for...

ATTACHING DOCUMENTS:

• Please find attached...
• I have attached...
• Attached you will find...
• I am sending you...
• Please see the attached file.

CONFIRMING:

• I can confirm that...
• I would like to confirm...
• This is to confirm...
• I am writing to confirm...
• As confirmed in our meeting...

OFFERING HELP:

• Please let me know if you need any further information.
• If you have any questions, please don''t hesitate to contact me.
• I am happy to provide additional details.
• Please feel free to contact me if you need assistance.
• I remain at your disposal.

CLOSING PHRASES:

Looking Forward:
• I look forward to hearing from you.
• I look forward to your reply.
• I look forward to meeting you.
• I await your response.

Thanking:
• Thank you for your time and consideration.
• Thank you in advance for your help.
• I appreciate your assistance with this matter.
• Thank you for your attention to this matter.

Availability:
• Please let me know if you have any questions.
• Feel free to contact me if you need more information.
• I am available for a call if you would like to discuss further.
• Don''t hesitate to reach out if you need anything.

EXAMPLE EMAIL USING THESE PHRASES:

Subject: Request for Project Update

Dear Ms. Anderson,

Thank you for your email dated March 15th. I am writing to inquire about the status of the website redesign project.

Could you please provide an update on the following:
• Current progress
• Expected completion date
• Any challenges or concerns

I would appreciate it if you could send this information by the end of the week. Please find attached the latest design mockups for your review.

If you have any questions or need any further information, please don''t hesitate to contact me.

I look forward to hearing from you.

Best regards,
Michael Chen

Practice using these phrases in your emails!', '', '2026-04-17 13:41:32.944434', 'Learn useful phrases for different email situations.', 15, false, true, 'DOCUMENT', 2, 'Common Email Phrases', '2026-04-17 13:41:32.944434', 145, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (85, 'Email Writing Assignment: Request Email

SCENARIO:
You work for TechCorp Inc. as a Project Coordinator. You need to request a meeting with your manager, Sarah Johnson, to discuss the budget for the upcoming Q4 marketing campaign. You want to meet next week and need to present your budget proposal.

ASSIGNMENT REQUIREMENTS:

Write a professional email that includes:

1. SUBJECT LINE (5 points)
   • Clear and specific
   • Indicates the purpose

2. GREETING (5 points)
   • Appropriate level of formality
   • Correct format

3. OPENING (10 points)
   • State your purpose clearly
   • Provide context if needed

4. BODY (20 points)
   • Request the meeting
   • Suggest specific dates/times
   • Mention what you want to discuss
   • Keep it concise and organized

5. CLOSING (10 points)
   • Appropriate closing phrase
   • Call to action

6. SIGN-OFF (5 points)
   • Professional sign-off
   • Complete signature block

7. TONE AND LANGUAGE (15 points)
   • Appropriate formality
   • Professional vocabulary
   • Polite and respectful

8. GRAMMAR AND SPELLING (15 points)
   • No grammatical errors
   • Correct spelling
   • Proper punctuation

9. FORMAT (10 points)
   • Proper email structure
   • Good paragraph organization
   • Easy to read

10. OVERALL EFFECTIVENESS (5 points)
    • Clear communication
    • Achieves the purpose

TOTAL: 100 points
PASSING SCORE: 70 points

TIPS:
• Use formal or semi-formal tone
• Be specific about dates and times
• Keep it brief (150-200 words)
• Proofread carefully
• Use phrases from the lesson

EXAMPLE STRUCTURE:

Subject: [Your subject line]

Dear [Greeting],

[Opening - state purpose]

[Body - make request with details]

[Closing - call to action]

[Sign-off]
[Your signature]

SUBMISSION:
Write your email and submit it for review. You will receive detailed feedback within 48 hours.

Good luck!', '', '2026-04-17 13:41:32.944434', 'Practice writing a professional request email.', 30, false, true, 'ASSIGNMENT', 3, 'Writing Practice: Request Email', '2026-04-17 13:41:32.944434', 145, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (86, 'What is Phonetics?

Phonetics is the scientific study of speech sounds. Understanding phonetics will dramatically improve your English pronunciation and help you speak more clearly and confidently.

WHY PHONETICS MATTERS:

1. ACCURATE PRONUNCIATION
   • Learn to produce sounds correctly
   • Reduce your accent
   • Be understood by native speakers

2. LISTENING COMPREHENSION
   • Recognize sounds in fast speech
   • Understand different accents
   • Improve your listening skills

3. SPELLING VS PRONUNCIATION
   English spelling doesn''t always match pronunciation:
   • "though" /θoʊ/
   • "through" /θruː/
   • "tough" /tʌf/
   • "cough" /kɔːf/
   
   All spelled similarly but pronounced differently!

THE INTERNATIONAL PHONETIC ALPHABET (IPA):

The IPA is a system of symbols where each symbol represents ONE sound. This is crucial because:
• English has 26 letters but 44 sounds
• One letter can make different sounds (e.g., "a" in "cat" vs "cake")
• Multiple letters can make one sound (e.g., "sh" in "ship")

ENGLISH SOUND SYSTEM:

Approximately 44 sounds in English:
• 20 vowel sounds (including diphthongs)
• 24 consonant sounds

VOWELS vs CONSONANTS:

Vowels:
• Air flows freely through the mouth
• Voice is always used
• Examples: /iː/ (see), /æ/ (cat), /ʌ/ (cup)

Consonants:
• Air is blocked or restricted
• May be voiced or voiceless
• Examples: /p/ (pen), /b/ (bed), /s/ (see)

VOICED vs VOICELESS SOUNDS:

Voiced: Vocal cords vibrate
• /b/, /d/, /g/, /v/, /z/
• Put your hand on your throat - you feel vibration

Voiceless: No vocal cord vibration
• /p/, /t/, /k/, /f/, /s/
• No vibration when you touch your throat

PRACTICE TIP:
Start by learning the IPA symbols. Use a dictionary with phonetic transcriptions to check pronunciation of new words.

In the next lessons, we will study each sound in detail and practice producing them correctly.', '', '2026-04-17 13:41:38.056123', 'Learn what phonetics is and why it matters for pronunciation.', 20, true, true, 'VIDEO', 0, 'Introduction to Phonetics', '2026-04-17 13:41:38.056123', 149, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (87, 'The International Phonetic Alphabet (IPA) - Your Pronunciation Guide

The IPA is your key to perfect English pronunciation. Once you learn it, you can pronounce any English word correctly!

WHY USE THE IPA?

1. ONE SYMBOL = ONE SOUND
   Unlike English spelling, each IPA symbol always represents the same sound.

2. UNIVERSAL SYSTEM
   Used in dictionaries worldwide
   Understood by language learners everywhere

3. PRECISE PRONUNCIATION
   Shows exactly how to pronounce words
   No guessing needed

ENGLISH VOWEL SOUNDS (20 total):

SHORT VOWELS:
/ɪ/ - bit, sit, give
/e/ - bed, said, head
/æ/ - cat, bad, man
/ʌ/ - cup, love, money
/ʊ/ - book, good, put
/ɒ/ - hot, dog, want (British)
/ə/ - about, sofa, banana (schwa - most common sound!)

LONG VOWELS:
/iː/ - see, eat, key
/ɑː/ - car, father, start
/ɔː/ - door, saw, bought
/uː/ - food, blue, through
/ɜː/ - bird, work, learn

DIPHTHONGS (two vowel sounds gliding together):
/eɪ/ - day, make, great
/aɪ/ - my, like, high
/ɔɪ/ - boy, coin, voice
/aʊ/ - now, house, loud
/əʊ/ - go, home, know (British)
/oʊ/ - go, home, know (American)
/ɪə/ - here, ear, beer
/eə/ - hair, care, there
/ʊə/ - tour, pure, sure

ENGLISH CONSONANT SOUNDS (24 total):

STOPS (air completely blocked):
/p/ - pen, happy, stop
/b/ - bed, rabbit, cab
/t/ - tea, better, cat
/d/ - dog, ladder, bad
/k/ - cat, school, back
/g/ - go, bigger, bag

FRICATIVES (air forced through narrow gap):
/f/ - fish, coffee, laugh
/v/ - very, over, love
/θ/ - think, author, bath (voiceless th)
/ð/ - this, mother, bathe (voiced th)
/s/ - see, lesson, bus
/z/ - zoo, easy, has
/ʃ/ - ship, nation, wash
/ʒ/ - measure, vision, beige
/h/ - hot, behind, who

AFFRICATES (stop + fricative):
/tʃ/ - church, teacher, watch
/dʒ/ - judge, magic, age

NASALS (air through nose):
/m/ - man, summer, come
/n/ - no, dinner, sun
/ŋ/ - sing, thinking, long

LIQUIDS:
/l/ - leg, yellow, call
/r/ - red, sorry, car

GLIDES:
/w/ - we, away, quick
/j/ - yes, onion, use

READING IPA TRANSCRIPTIONS:

Word: "cat"
IPA: /kæt/
Breakdown: /k/ + /æ/ + /t/

Word: "through"
IPA: /θruː/
Breakdown: /θ/ + /r/ + /uː/

Word: "beautiful"
IPA: /ˈbjuːtɪfl/
Breakdown: /b/ + /j/ + /uː/ + /t/ + /ɪ/ + /f/ + /l/
(The ˈ mark shows primary stress)

STRESS MARKS:
ˈ = primary stress (before the stressed syllable)
ˌ = secondary stress

Example: "understand"
/ˌʌndəˈstænd/
Secondary stress on "un", primary stress on "stand"

PRACTICE EXERCISES:

1. Read these IPA transcriptions:
   /kæt/ = cat
   /dɒg/ = dog
   /haʊs/ = house
   /ˈtiːtʃə/ = teacher

2. Write these words in IPA:
   - book
   - phone
   - water
   - computer

TIPS FOR LEARNING IPA:

1. Start with sounds you know
2. Practice a few symbols each day
3. Use IPA in your dictionary
4. Write new words in IPA
5. Compare similar sounds

In the next lessons, we will practice each sound group in detail!', '', '2026-04-17 13:41:38.056123', 'Learn to read and use the IPA for English pronunciation.', 30, false, true, 'VIDEO', 1, 'The International Phonetic Alphabet', '2026-04-17 13:41:38.056123', 149, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (88, 'Complete English Sound System

This comprehensive guide covers all 44 sounds in English with examples and pronunciation tips.

PART 1: VOWEL SOUNDS

Vowels are the core of every syllable. English has more vowel sounds than most languages!

SHORT VOWELS (7 sounds):

1. /ɪ/ - Short I
   Words: bit, sit, give, women
   Tip: Shorter and more relaxed than /iː/
   
2. /e/ - Short E
   Words: bed, said, head, many
   Tip: Mouth slightly open, tongue mid-high
   
3. /æ/ - Short A (cat vowel)
   Words: cat, bad, man, have
   Tip: Mouth wide open, tongue low
   
4. /ʌ/ - Short U (cup vowel)
   Words: cup, love, money, blood
   Tip: Mouth slightly open, tongue mid-low
   
5. /ʊ/ - Short OO (book vowel)
   Words: book, good, put, could
   Tip: Lips slightly rounded
   
6. /ɒ/ - Short O (British)
   Words: hot, dog, want, what
   Tip: Mouth wide open, lips rounded
   
7. /ə/ - Schwa (the most common sound!)
   Words: about, sofa, banana, the
   Tip: Relaxed, neutral sound in unstressed syllables

LONG VOWELS (5 sounds):

1. /iː/ - Long E
   Words: see, eat, key, people
   Tip: Smile, stretch lips, tense tongue
   
2. /ɑː/ - Long A
   Words: car, father, start, heart
   Tip: Mouth wide open, tongue low and back
   
3. /ɔː/ - Long O
   Words: door, saw, bought, thought
   Tip: Lips rounded, tongue back
   
4. /uː/ - Long OO
   Words: food, blue, through, shoe
   Tip: Lips very rounded, tongue high and back
   
5. /ɜː/ - ER sound
   Words: bird, work, learn, her
   Tip: Lips slightly rounded, tongue mid-central

DIPHTHONGS (8 sounds - vowels that glide):

1. /eɪ/ - Long A (day)
   Words: day, make, great, they
   Glides from /e/ to /ɪ/
   
2. /aɪ/ - Long I (my)
   Words: my, like, high, buy
   Glides from /a/ to /ɪ/
   
3. /ɔɪ/ - OY sound
   Words: boy, coin, voice, toy
   Glides from /ɔ/ to /ɪ/
   
4. /aʊ/ - OW sound (now)
   Words: now, house, loud, how
   Glides from /a/ to /ʊ/
   
5. /əʊ/ or /oʊ/ - Long O (go)
   Words: go, home, know, though
   Glides from /ə/ or /o/ to /ʊ/
   
6. /ɪə/ - EAR sound
   Words: here, ear, beer, idea
   Glides from /ɪ/ to /ə/
   
7. /eə/ - AIR sound
   Words: hair, care, there, bear
   Glides from /e/ to /ə/
   
8. /ʊə/ - OOR sound
   Words: tour, pure, sure, poor
   Glides from /ʊ/ to /ə/

PART 2: CONSONANT SOUNDS

STOPS (6 sounds):
Air is completely blocked then released

Voiceless: /p/ /t/ /k/
Voiced: /b/ /d/ /g/

Pairs:
/p/ - /b/: pen - ben, cap - cab
/t/ - /d/: ten - den, bat - bad
/k/ - /g/: came - game, back - bag

FRICATIVES (9 sounds):
Air forced through narrow gap

Voiceless: /f/ /θ/ /s/ /ʃ/ /h/
Voiced: /v/ /ð/ /z/ /ʒ/

Pairs:
/f/ - /v/: fan - van, leaf - leave
/θ/ - /ð/: think - this, bath - bathe
/s/ - /z/: sue - zoo, bus - buzz
/ʃ/ - /ʒ/: sh - measure

AFFRICATES (2 sounds):
Combination of stop + fricative

/tʃ/: church, teacher, watch
/dʒ/: judge, magic, age

NASALS (3 sounds):
Air flows through nose

/m/: man, summer, come
/n/: no, dinner, sun
/ŋ/: sing, thinking, long

LIQUIDS (2 sounds):
/l/: leg, yellow, call
/r/: red, sorry, car

GLIDES (2 sounds):
/w/: we, away, quick
/j/: yes, onion, use

COMMON PRONUNCIATION CHALLENGES:

1. /θ/ and /ð/ (th sounds)
   Not found in many languages
   Practice: think, this, three, the

2. /v/ and /w/
   Different sounds!
   /v/: teeth touch bottom lip (very, have)
   /w/: lips rounded (we, away)

3. /l/ and /r/
   Very different in English
   /l/: tongue touches roof of mouth
   /r/: tongue doesn''t touch anything

4. /ŋ/ (ng sound)
   One sound, not two!
   sing /sɪŋ/ NOT /sɪng/

PRACTICE TIPS:

1. Record yourself
2. Compare with native speakers
3. Practice minimal pairs (words that differ by one sound)
4. Focus on difficult sounds
5. Practice every day

Remember: Perfect pronunciation takes time and practice. Be patient with yourself!', '', '2026-04-17 13:41:38.056123', 'Overview of all English sounds: vowels, consonants, and diphthongs.', 25, false, true, 'DOCUMENT', 2, 'English Sound System Overview', '2026-04-17 13:41:38.056123', 149, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (89, 'Live Pronunciation Practice Session

This is a live online session where you will practice English sounds with your tutor and receive personalized feedback.

SESSION STRUCTURE (45 minutes):

PART 1: WARM-UP (5 minutes)
• Introduction and goals
• Quick review of IPA symbols
• Vocal warm-up exercises

PART 2: VOWEL PRACTICE (15 minutes)
• Practice short vs long vowels
• Minimal pairs exercises
• Diphthong practice
• Individual pronunciation check

PART 3: CONSONANT PRACTICE (15 minutes)
• Difficult consonants (/θ/, /ð/, /r/, /l/)
• Voiced vs voiceless pairs
• Consonant clusters
• Individual pronunciation check

PART 4: CONNECTED SPEECH (10 minutes)
• Sentences and phrases
• Natural rhythm and flow
• Common reductions
• Real-life practice

WHAT TO PREPARE:

1. Review IPA symbols
2. Identify your difficult sounds
3. Prepare questions
4. Have a mirror ready (to watch your mouth)
5. Quiet environment with good internet

WHAT YOU WILL PRACTICE:

Minimal Pairs:
• ship - sheep
• bit - beat
• cat - cut
• pen - pan
• think - sink

Difficult Sounds:
• /θ/ - think, three, bath
• /ð/ - this, that, mother
• /r/ - red, very, car
• /l/ - light, yellow, call
• /v/ - very, have, love

Sentences:
• "She sells seashells by the seashore."
• "How much wood would a woodchuck chuck?"
• "The thirty-three thieves thought they thrilled the throne."

FEEDBACK YOU WILL RECEIVE:

• Specific sounds to improve
• Mouth position corrections
• Practice exercises for your needs
• Resources for continued practice

AFTER THE SESSION:

• Recording of the session (if permitted)
• Personalized practice plan
• Follow-up exercises
• Progress tracking

TECHNICAL REQUIREMENTS:

• Stable internet connection
• Microphone and camera
• Quiet environment
• Zoom/Google Meet link (provided before session)

BOOKING:
Sessions are scheduled weekly. Check your course calendar for available times.

This interactive session is crucial for improving your pronunciation. Active participation is key!', '', '2026-04-17 13:41:38.056123', 'Live online session to practice sounds with a tutor.', 45, false, true, 'ONLINE', 3, 'Pronunciation Practice Session', '2026-04-17 13:41:38.056123', 149, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (90, 'Understanding English Idioms

Idioms are one of the most fascinating and challenging aspects of English. Mastering them will make you sound more natural and help you understand native speakers better.

WHAT IS AN IDIOM?

An idiom is a phrase or expression whose meaning cannot be understood from the individual words. The meaning is figurative, not literal.

Example:
"It''s raining cats and dogs"
Literal meaning: Cats and dogs are falling from the sky (impossible!)
Idiomatic meaning: It''s raining very heavily

WHY LEARN IDIOMS?

1. SOUND MORE NATURAL
   Native speakers use idioms constantly in everyday conversation.

2. UNDERSTAND NATIVE SPEAKERS
   Movies, TV shows, books, and conversations are full of idioms.

3. EXPRESS IDEAS CREATIVELY
   Idioms add color and personality to your English.

4. CULTURAL UNDERSTANDING
   Many idioms reflect cultural values and history.

TYPES OF IDIOMS:

1. BODY IDIOMS
   • "Keep your chin up" = Stay positive
   • "Cost an arm and a leg" = Very expensive
   • "Give someone a hand" = Help someone

2. ANIMAL IDIOMS
   • "Let the cat out of the bag" = Reveal a secret
   • "Kill two birds with one stone" = Accomplish two things at once
   • "When pigs fly" = Never/impossible

3. FOOD IDIOMS
   • "Piece of cake" = Very easy
   • "Spill the beans" = Reveal a secret
   • "Butter someone up" = Flatter someone

4. COLOR IDIOMS
   • "Out of the blue" = Unexpectedly
   • "Green with envy" = Very jealous
   • "See red" = Become very angry

5. WEATHER IDIOMS
   • "Under the weather" = Feeling sick
   • "Break the ice" = Make people comfortable
   • "Storm in a teacup" = Big fuss about nothing

COMMON IDIOMS FOR BEGINNERS:

EASY TASKS:
• "Piece of cake" - very easy
• "A walk in the park" - very easy
• "Child''s play" - very easy

DIFFICULT TASKS:
• "Not rocket science" - not very difficult
• "Easier said than done" - difficult to do
• "Uphill battle" - very difficult

UNDERSTANDING:
• "Get the picture" - understand
• "Crystal clear" - very clear
• "Greek to me" - don''t understand at all

SECRETS:
• "Spill the beans" - reveal a secret
• "Let the cat out of the bag" - reveal a secret
• "Keep it under wraps" - keep it secret

MONEY:
• "Cost an arm and a leg" - very expensive
• "Break the bank" - very expensive
• "Dirt cheap" - very inexpensive

HOW TO LEARN IDIOMS:

1. LEARN IN CONTEXT
   Don''t just memorize - understand when to use them.
   
   Example:
   "This exam was a piece of cake!"
   (Use after completing something easy)

2. LEARN IN GROUPS
   Group idioms by theme (body, animals, food, etc.)

3. USE THEM
   Practice using idioms in your speaking and writing.

4. WATCH AND LISTEN
   Pay attention to idioms in movies, TV shows, and conversations.

5. KEEP A NOTEBOOK
   Write down new idioms with examples.

IDIOM MISTAKES TO AVOID:

1. WRONG CONTEXT
   Wrong: "I''m feeling under the weather today!" (when happy)
   Right: "I''m feeling under the weather today." (when sick)

2. MIXING IDIOMS
   Wrong: "Let''s kill two cats with one stone."
   Right: "Let''s kill two birds with one stone."

3. LITERAL TRANSLATION
   Don''t translate idioms from your language word-for-word.

4. OVERUSING
   Don''t use too many idioms in formal writing or professional contexts.

PRACTICE:

Match the idiom to its meaning:
1. "Break a leg" → Good luck
2. "Hit the books" → Study hard
3. "Call it a day" → Stop working
4. "On cloud nine" → Very happy
5. "Bite the bullet" → Do something difficult

In the next lessons, we''ll explore 50+ common idioms in detail with examples and practice exercises!', '', '2026-04-17 13:41:38.056123', 'Introduction to idiomatic expressions in English.', 20, false, true, 'VIDEO', 0, 'What Are Idioms?', '2026-04-17 13:41:38.056123', 154, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (91, '50 Essential English Idioms

Master these commonly used idioms to sound more natural in English conversations.

CATEGORY 1: EMOTIONS & FEELINGS

1. "On cloud nine" = Extremely happy
   Example: "She was on cloud nine after getting the job."

2. "Down in the dumps" = Sad, depressed
   Example: "He''s been down in the dumps since his team lost."

3. "Over the moon" = Very happy
   Example: "They were over the moon about the baby news."

4. "Butterflies in my stomach" = Nervous
   Example: "I have butterflies in my stomach before the presentation."

5. "See red" = Become very angry
   Example: "He saw red when someone scratched his car."

CATEGORY 2: DIFFICULTY & EASE

6. "Piece of cake" = Very easy
   Example: "The test was a piece of cake!"

7. "Walk in the park" = Very easy
   Example: "This project is a walk in the park compared to the last one."

8. "Uphill battle" = Very difficult
   Example: "Losing weight is an uphill battle for me."

9. "Back to square one" = Start over
   Example: "The plan failed, so we''re back to square one."

10. "Learn the ropes" = Learn how to do something
    Example: "It takes time to learn the ropes at a new job."

CATEGORY 3: TIME

11. "In the nick of time" = Just in time
    Example: "We arrived at the airport in the nick of time."

12. "Better late than never" = It''s better to do something late than not at all
    Example: "You finally finished the report - better late than never!"

13. "Time flies" = Time passes quickly
    Example: "Time flies when you''re having fun!"

14. "Around the clock" = 24 hours a day
    Example: "The hospital is open around the clock."

15. "Call it a day" = Stop working for the day
    Example: "It''s 6 PM. Let''s call it a day."

CATEGORY 4: COMMUNICATION

16. "Break the ice" = Make people feel comfortable
    Example: "He told a joke to break the ice at the meeting."

17. "Spill the beans" = Reveal a secret
    Example: "Don''t spill the beans about the surprise party!"

18. "Let the cat out of the bag" = Reveal a secret accidentally
    Example: "He let the cat out of the bag about their engagement."

19. "Beat around the bush" = Avoid saying something directly
    Example: "Stop beating around the bush and tell me the truth!"

20. "Get straight to the point" = Say something directly
    Example: "I don''t have much time, so get straight to the point."

CATEGORY 5: MONEY

21. "Cost an arm and a leg" = Very expensive
    Example: "That designer bag costs an arm and a leg!"

22. "Break the bank" = Very expensive
    Example: "This vacation won''t break the bank."

23. "Dirt cheap" = Very inexpensive
    Example: "I bought this shirt for $5 - it was dirt cheap!"

24. "Pay through the nose" = Pay too much
    Example: "We paid through the nose for those concert tickets."

25. "Make ends meet" = Have enough money to live
    Example: "It''s hard to make ends meet with these prices."

CATEGORY 6: SUCCESS & FAILURE

26. "Hit the nail on the head" = Be exactly right
    Example: "You hit the nail on the head with that analysis!"

27. "Miss the boat" = Miss an opportunity
    Example: "I missed the boat on buying that house."

28. "Back to the drawing board" = Start planning again
    Example: "The idea didn''t work, so it''s back to the drawing board."

29. "The ball is in your court" = It''s your turn to act
    Example: "I''ve made my offer. The ball is in your court now."

30. "Throw in the towel" = Give up
    Example: "After trying for hours, he threw in the towel."

CATEGORY 7: UNDERSTANDING

31. "Get the picture" = Understand
    Example: "Do you get the picture now?"

32. "Crystal clear" = Very clear
    Example: "Your instructions were crystal clear."

33. "It''s Greek to me" = I don''t understand at all
    Example: "This math problem is Greek to me!"

34. "Ring a bell" = Sound familiar
    Example: "Does the name John Smith ring a bell?"

35. "Put two and two together" = Understand by connecting facts
    Example: "I put two and two together and realized the truth."

CATEGORY 8: ADVICE & WISDOM

36. "Don''t cry over spilled milk" = Don''t worry about past mistakes
    Example: "You failed the test, but don''t cry over spilled milk."

37. "Actions speak louder than words" = What you do is more important than what you say
    Example: "He says he''ll help, but actions speak louder than words."

38. "The early bird catches the worm" = Success comes to those who start early
    Example: "I always arrive early - the early bird catches the worm!"

39. "Don''t put all your eggs in one basket" = Don''t risk everything on one thing
    Example: "Apply to multiple jobs - don''t put all your eggs in one basket."

40. "When it rains, it pours" = Problems come all at once
    Example: "First my car broke down, then I got sick - when it rains, it pours!"

CATEGORY 9: WORK & EFFORT

41. "Burn the midnight oil" = Work late into the night
    Example: "I''m burning the midnight oil to finish this project."

42. "Go the extra mile" = Make extra effort
    Example: "She always goes the extra mile for her customers."

43. "Pull your weight" = Do your fair share of work
    Example: "Everyone needs to pull their weight on this team."

44. "Cut corners" = Do something poorly to save time/money
    Example: "Don''t cut corners on this project - do it right!"

45. "Get the ball rolling" = Start something
    Example: "Let''s get the ball rolling on this new initiative."

CATEGORY 10: MISCELLANEOUS

46. "Under the weather" = Feeling sick
    Example: "I''m feeling a bit under the weather today."

47. "Once in a blue moon" = Very rarely
    Example: "I only eat fast food once in a blue moon."

48. "The best of both worlds" = All the advantages
    Example: "Working from home gives me the best of both worlds."

49. "Bite off more than you can chew" = Take on too much
    Example: "I bit off more than I could chew with three projects."

50. "It takes two to tango" = Both people are responsible
    Example: "The argument wasn''t just his fault - it takes two to tango."

PRACTICE EXERCISES:

1. Use 5 idioms in sentences about your life
2. Find idioms in a movie or TV show
3. Create dialogues using idioms
4. Teach an idiom to a friend

Remember: Use idioms naturally, don''t force them into every sentence!', '', '2026-04-17 13:41:38.056123', 'Learn 50 frequently used idioms in everyday English.', 35, false, true, 'VIDEO', 1, 'Common Everyday Idioms', '2026-04-17 13:41:38.056123', 154, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (217, '', '', '2026-05-13 12:30:57.13114', 'Quiz V2', 0, false, true, 'QUIZ', 1, 'QUIZ V2', '2026-05-13 12:30:57.13114', 183, 12);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (93, 'The Eight Parts of Speech

Every word in English belongs to one of eight categories called "parts of speech." Understanding these categories is essential for building correct sentences.

1. NOUNS - Words that name people, places, things, or ideas
   Examples: teacher, London, book, happiness
   
2. PRONOUNS - Words that replace nouns
   Examples: I, you, he, she, it, they, we
   
3. VERBS - Action words or state-of-being words
   Examples: run, eat, is, have, think
   
4. ADJECTIVES - Words that describe nouns
   Examples: beautiful, tall, red, happy
   
5. ADVERBS - Words that describe verbs, adjectives, or other adverbs
   Examples: quickly, very, well, often
   
6. PREPOSITIONS - Words that show relationships between nouns
   Examples: in, on, at, under, between
   
7. CONJUNCTIONS - Words that connect other words or sentences
   Examples: and, but, or, because, although
   
8. INTERJECTIONS - Words that express emotion
   Examples: Oh! Wow! Ouch! Hey!

Practice identifying parts of speech in sentences to improve your grammar skills.', '', '2026-04-17 13:44:45.285944', 'Learn about the eight parts of speech in English: nouns, verbs, adjectives, adverbs, pronouns, prepositions, conjunctions, and interjections.', 25, false, true, 'VIDEO', 1, 'Parts of Speech Overview', '2026-04-17 13:44:45.285944', 141, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (94, 'Nouns and Pronouns in Detail

NOUNS
Nouns are words that name people, places, things, or ideas. There are several types:

1. Common Nouns - General names (not capitalized)
   Examples: dog, city, car, teacher
   
2. Proper Nouns - Specific names (always capitalized)
   Examples: London, John, Microsoft, Monday
   
3. Concrete Nouns - Things you can see or touch
   Examples: table, apple, computer
   
4. Abstract Nouns - Ideas or concepts
   Examples: love, freedom, happiness, time
   
5. Countable Nouns - Can be counted (have plural forms)
   Examples: book/books, cat/cats, idea/ideas
   
6. Uncountable Nouns - Cannot be counted (no plural form)
   Examples: water, rice, information, advice

PRONOUNS
Pronouns replace nouns to avoid repetition.

Subject Pronouns: I, you, he, she, it, we, they
Object Pronouns: me, you, him, her, it, us, them
Possessive Pronouns: mine, yours, his, hers, ours, theirs
Possessive Adjectives: my, your, his, her, its, our, their

Example:
"John loves his dog. He walks it every day."
(He = John, it = dog)

Practice using nouns and pronouns correctly in your writing and speaking.', '', '2026-04-17 13:44:45.285944', 'Understand nouns (people, places, things) and pronouns (words that replace nouns).', 20, false, true, 'DOCUMENT', 2, 'Nouns and Pronouns', '2026-04-17 13:44:45.285944', 141, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (95, 'Understanding Verbs and Tenses

WHAT ARE VERBS?
Verbs are the most important part of a sentence. They express actions or states of being.

Action Verbs: run, eat, write, think, speak
State Verbs: be, have, know, like, want

VERB TENSES
English has three main time frames, each with four aspects:

PRESENT TENSES:
• Present Simple: I work
• Present Continuous: I am working
• Present Perfect: I have worked
• Present Perfect Continuous: I have been working

PAST TENSES:
• Past Simple: I worked
• Past Continuous: I was working
• Past Perfect: I had worked
• Past Perfect Continuous: I had been working

FUTURE TENSES:
• Future Simple: I will work
• Future Continuous: I will be working
• Future Perfect: I will have worked
• Future Perfect Continuous: I will have been working

In this course, we will focus on the most common tenses:
- Present Simple and Present Continuous
- Past Simple and Past Continuous

Understanding when to use each tense is key to speaking and writing English correctly.

Example:
"I work in an office." (Present Simple - routine)
"I am working on a project." (Present Continuous - happening now)
"I worked yesterday." (Past Simple - completed action)
"I was working when you called." (Past Continuous - interrupted action)', '', '2026-04-17 13:44:45.285944', 'Introduction to verbs and the concept of tenses in English.', 30, false, true, 'VIDEO', 3, 'Verbs and Tenses Introduction', '2026-04-17 13:44:45.285944', 141, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (96, 'Present Simple Tense - Complete Guide

WHEN TO USE:
1. Habits and routines
   "I drink coffee every morning."
   
2. Facts and general truths
   "The sun rises in the east."
   "Water boils at 100°C."
   
3. Permanent situations
   "She lives in Paris."
   "He works as a teacher."
   
4. Scheduled events
   "The train leaves at 6 PM."

HOW TO FORM:

Positive:
I/You/We/They + base verb
He/She/It + base verb + s/es

Examples:
"I work in an office."
"She works in a hospital."
"They play football."

Negative:
I/You/We/They + do not (don''t) + base verb
He/She/It + does not (doesn''t) + base verb

Examples:
"I don''t like coffee."
"He doesn''t speak French."

Questions:
Do + I/you/we/they + base verb?
Does + he/she/it + base verb?

Examples:
"Do you like pizza?"
"Does she work here?"

SPELLING RULES FOR THIRD PERSON (he/she/it):
• Most verbs: add -s (work → works, play → plays)
• Verbs ending in -s, -sh, -ch, -x, -o: add -es (watch → watches, go → goes)
• Verbs ending in consonant + y: change y to i and add -es (study → studies)
• Irregular: have → has

TIME EXPRESSIONS:
always, usually, often, sometimes, rarely, never
every day/week/month/year
on Mondays, in the morning, at night

Practice forming present simple sentences with different subjects!', '', '2026-04-17 13:44:45.285944', 'Learn how to form and use the present simple tense for habits, facts, and routines.', 30, false, true, 'VIDEO', 0, 'Present Simple Tense', '2026-04-17 13:44:45.285944', 142, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (97, 'Present Simple Practice Exercises

Complete these exercises to master the present simple tense:

EXERCISE 1: Fill in the blanks with the correct form of the verb
1. She _____ (work) in a bank.
2. They _____ (not/like) spicy food.
3. _____ you _____ (speak) English?
4. He _____ (watch) TV every evening.
5. We _____ (not/have) a car.

EXERCISE 2: Correct the mistakes
1. He don''t like coffee. → _____
2. Does they live here? → _____
3. She work in an office. → _____
4. I doesn''t understand. → _____
5. Do she speak French? → _____

EXERCISE 3: Make questions
1. You like pizza. → _____?
2. She works here. → _____?
3. They have a dog. → _____?
4. He speaks Spanish. → _____?
5. You know the answer. → _____?

EXERCISE 4: Write about your daily routine
Use present simple to describe what you do every day. Include:
- What time you wake up
- What you eat for breakfast
- Where you work or study
- What you do in the evening
- What time you go to bed

Example:
"I wake up at 7 AM every day. I eat cereal for breakfast. I work in an office from 9 to 5. In the evening, I watch TV or read a book. I go to bed at 11 PM."

Practice makes perfect! Complete these exercises and check your answers.', '', '2026-04-17 13:44:45.285944', 'Interactive exercises to practice present simple tense.', 25, false, true, 'INTERACTIVE', 1, 'Present Simple Practice', '2026-04-17 13:44:45.285944', 142, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (98, 'Present Continuous Tense - Complete Guide

WHEN TO USE:
1. Actions happening right now
   "I am writing an email." (right now)
   
2. Temporary situations
   "She is living in London this year." (temporary)
   
3. Future arrangements
   "We are meeting tomorrow at 3 PM." (planned future)
   
4. Changing situations
   "The weather is getting colder." (gradual change)
   
5. Annoying habits (with "always")
   "He is always complaining!" (criticism)

HOW TO FORM:

Positive:
Subject + am/is/are + verb-ing

Examples:
"I am working."
"She is studying."
"They are playing."

Negative:
Subject + am not/isn''t/aren''t + verb-ing

Examples:
"I am not working."
"He isn''t studying."
"They aren''t playing."

Questions:
Am/Is/Are + subject + verb-ing?

Examples:
"Are you working?"
"Is she studying?"
"Are they playing?"

SPELLING RULES FOR -ING:
• Most verbs: add -ing (work → working, play → playing)
• Verbs ending in -e: remove e, add -ing (make → making, write → writing)
• One syllable verbs ending in consonant-vowel-consonant: double the last consonant (run → running, sit → sitting)
• Verbs ending in -ie: change ie to y (lie → lying, die → dying)

TIME EXPRESSIONS:
now, right now, at the moment, currently
today, this week, this month, this year
Look! Listen! (to draw attention)

STATIVE VERBS (NOT used in continuous):
know, understand, believe, like, love, hate, want, need, prefer, remember, forget, seem, belong

Wrong: "I am knowing the answer."
Right: "I know the answer."

Practice using present continuous for actions happening now!', '', '2026-04-17 13:44:45.285944', 'Master the present continuous tense for actions happening now.', 30, false, true, 'VIDEO', 2, 'Present Continuous Tense', '2026-04-17 13:44:45.285944', 142, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (99, 'Present Simple vs Present Continuous - Key Differences

Understanding when to use each tense is crucial for accurate communication.

PRESENT SIMPLE:
✓ Permanent situations
✓ Habits and routines
✓ Facts and general truths
✓ Scheduled events

Examples:
"I live in New York." (permanent)
"She works every day." (routine)
"The Earth orbits the Sun." (fact)
"The train leaves at 6 PM." (schedule)

PRESENT CONTINUOUS:
✓ Actions happening now
✓ Temporary situations
✓ Future arrangements
✓ Changing situations

Examples:
"I am living in a hotel this week." (temporary)
"She is working on a project." (happening now)
"We are meeting tomorrow." (future plan)
"Prices are rising." (change)

COMPARISON EXAMPLES:

1. Permanent vs Temporary:
   "I work in London." (permanent job)
   "I am working in London this month." (temporary assignment)

2. Routine vs Now:
   "He reads books." (general habit)
   "He is reading a book." (right now)

3. General vs Specific:
   "She teaches English." (her profession)
   "She is teaching a class." (at this moment)

4. Always true vs Currently true:
   "Water boils at 100°C." (always)
   "The water is boiling." (right now)

COMMON MISTAKES:

Wrong: "I am understanding English."
Right: "I understand English."
(understand is a stative verb)

Wrong: "She is having a car."
Right: "She has a car."
(have for possession is stative)

Wrong: "What do you do now?"
Right: "What are you doing now?"
(asking about current action)

PRACTICE:
Choose the correct tense:
1. I _____ (work/am working) in an office. [permanent]
2. She _____ (works/is working) on a report now. [current]
3. They _____ (live/are living) in Paris. [permanent]
4. We _____ (have/are having) dinner at 7 PM tonight. [future plan]
5. He _____ (studies/is studying) English every day. [routine]

Master these differences to speak English more naturally!', '', '2026-04-17 13:44:45.285944', 'Learn when to use present simple versus present continuous.', 20, false, true, 'DOCUMENT', 3, 'Present Simple vs Continuous', '2026-04-17 13:44:45.285944', 142, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (100, 'Present Tenses Assignment

Complete this assignment to demonstrate your understanding of present simple and present continuous tenses.

PART 1: Fill in the blanks (10 points)
Choose present simple or present continuous:

1. Right now, I _____ (sit) in a café and _____ (drink) coffee.
2. My sister _____ (work) as a nurse. She _____ (love) her job.
3. Look! It _____ (rain) outside.
4. We usually _____ (go) to the gym on Mondays, but today we _____ (stay) home.
5. _____ you _____ (understand) the lesson?
6. The children _____ (play) in the garden at the moment.
7. She _____ (not/eat) meat. She _____ (be) vegetarian.
8. What _____ you _____ (do) right now?
9. The sun _____ (rise) in the east and _____ (set) in the west.
10. This week, I _____ (study) for my exams.

PART 2: Correct the mistakes (10 points)
Find and correct the errors:

1. I am knowing the answer.
2. She is having a beautiful house.
3. Do you understanding me?
4. They are live in London.
5. He works on a project now.
6. I am not believing you.
7. Are you want some coffee?
8. She is teach English.
9. We are needing help.
10. He is have lunch now.

PART 3: Translation (10 points)
Translate these sentences to English:

1. Je travaille dans un bureau. (permanent)
2. Je travaille sur un projet maintenant. (current)
3. Elle étudie l''anglais tous les jours. (routine)
4. Ils regardent la télévision en ce moment. (now)
5. Nous habitons à Paris. (permanent)

PART 4: Writing (20 points)
Write two short paragraphs (50-75 words each):

Paragraph 1: Describe your typical day using present simple
Include: your routine, habits, what you usually do

Paragraph 2: Describe what you are doing right now using present continuous
Include: current actions, temporary situations, what''s happening around you

SUBMISSION:
Complete all parts and submit your assignment for review. You will receive feedback within 48 hours.

Total Points: 50
Passing Score: 35/50 (70%)

Good luck!', '', '2026-04-17 13:44:45.285944', 'Complete exercises using both present tenses.', 30, false, true, 'ASSIGNMENT', 4, 'Present Tenses Assignment', '2026-04-17 13:44:45.285944', 142, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (101, 'Professional Email Structure

A well-structured email makes a professional impression and ensures clear communication.

ESSENTIAL COMPONENTS:

1. SUBJECT LINE
   • Clear and specific
   • Summarizes the email purpose
   • Examples:
     ✓ "Meeting Request: Q4 Budget Review"
     ✓ "Follow-up: Project Proposal Discussion"
     ✗ "Hi" or "Question" (too vague)

2. GREETING/SALUTATION
   Formal:
   • Dear Mr./Ms. [Last Name],
   • Dear Sir/Madam, (if name unknown)
   
   Semi-formal:
   • Hello [First Name],
   • Hi [First Name],
   
   Note: Use comma (,) in American English, colon (:) in British English

3. OPENING LINE
   • State your purpose immediately
   • Examples:
     "I am writing to inquire about..."
     "Thank you for your email regarding..."
     "I would like to request..."

4. BODY
   • Keep paragraphs short (2-3 sentences)
   • Use bullet points for lists
   • One main idea per paragraph
   • Be clear and concise

5. CLOSING LINE
   • Summarize or call to action
   • Examples:
     "I look forward to hearing from you."
     "Please let me know if you need any further information."
     "Thank you for your time and consideration."

6. SIGN-OFF
   Formal:
   • Yours sincerely, (if you know the name)
   • Yours faithfully, (if you don''t know the name)
   • Best regards,
   • Kind regards,
   
   Semi-formal:
   • Best,
   • Thanks,
   • Regards,

7. SIGNATURE
   [Your Full Name]
   [Your Position]
   [Company Name]
   [Contact Information]

EXAMPLE EMAIL:

Subject: Meeting Request: Marketing Strategy Discussion

Dear Ms. Johnson,

I hope this email finds you well. I am writing to request a meeting to discuss our Q4 marketing strategy.

I would like to propose the following:
• Date: Next Tuesday, October 15th
• Time: 2:00 PM - 3:00 PM
• Location: Conference Room B

Please let me know if this time works for you, or suggest an alternative that suits your schedule.

I look forward to hearing from you.

Best regards,

John Smith
Marketing Manager
ABC Corporation
john.smith@abc.com
+1 (555) 123-4567

FORMATTING TIPS:
• Use a professional font (Arial, Calibri, Times New Roman)
• Font size: 10-12 pt
• Avoid colors, emojis, or fancy formatting
• Proofread before sending
• Keep it concise (aim for 3-5 short paragraphs)

Practice writing emails with this structure!', '', '2026-04-17 13:44:45.285944', 'Learn the proper structure of professional business emails.', 20, true, true, 'VIDEO', 0, 'Email Structure and Format', '2026-04-17 13:44:45.285944', 145, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (102, 'Email Tone: Formal vs Informal

Choosing the right tone is crucial for effective business communication.

WHEN TO USE FORMAL TONE:

1. First contact with someone
2. Writing to senior management
3. Official requests or complaints
4. Legal or contractual matters
5. External clients or partners
6. Job applications

FORMAL LANGUAGE CHARACTERISTICS:
• Complete sentences
• No contractions (write "I am" not "I''m")
• Polite, respectful language
• Professional vocabulary
• Proper grammar and punctuation

FORMAL PHRASES:
Opening:
• I am writing to...
• I would like to inquire about...
• Thank you for your email dated...
• Further to our conversation...

Requesting:
• I would appreciate it if you could...
• Would it be possible to...
• I would be grateful if you could...
• Could you please...

Closing:
• I look forward to hearing from you.
• Thank you for your time and consideration.
• Please do not hesitate to contact me.
• I remain at your disposal.

WHEN TO USE INFORMAL TONE:

1. Colleagues you know well
2. Internal team communications
3. Follow-up emails
4. Casual updates
5. After establishing a relationship

INFORMAL LANGUAGE CHARACTERISTICS:
• Contractions allowed (I''m, you''re, we''ll)
• Shorter sentences
• Friendly, conversational tone
• Simple vocabulary
• Can be more direct

INFORMAL PHRASES:
Opening:
• Thanks for your email.
• Just wanted to let you know...
• Quick question about...
• Hope you''re doing well.

Requesting:
• Can you...?
• Could you...?
• Would you mind...?
• Let me know if...

Closing:
• Talk soon!
• Thanks!
• Cheers,
• Have a great day!

COMPARISON EXAMPLES:

FORMAL:
"Dear Mr. Brown,

I am writing to request your assistance with the Johnson account. Would it be possible for you to provide the quarterly reports by Friday?

I would greatly appreciate your help with this matter.

Yours sincerely,
Sarah"

INFORMAL:
"Hi Tom,

Hope you''re well! Can you send me the Johnson account reports by Friday? I need them for the meeting.

Thanks!
Sarah"

SEMI-FORMAL (Most Common):
"Hello Tom,

I hope this email finds you well. Could you please send me the Johnson account reports by Friday? I need them for the upcoming meeting.

Thank you for your help.

Best regards,
Sarah"

TONE MISTAKES TO AVOID:

Too Formal (sounds stiff):
"I hereby request that you forward the aforementioned documents at your earliest convenience."

Better:
"Could you please send the documents when you have a chance?"

Too Informal (unprofessional):
"Hey! Send me those docs ASAP!!!"

Better:
"Hi! Could you send me those documents soon? Thanks!"

TIPS FOR CHOOSING THE RIGHT TONE:

1. Consider your relationship with the recipient
2. Think about the company culture
3. Match the tone of previous emails
4. When in doubt, err on the side of formal
5. Adjust your tone as the relationship develops

Practice writing emails in different tones!', '', '2026-04-17 13:44:45.285944', 'Understand when to use formal or informal language in emails.', 25, false, true, 'VIDEO', 1, 'Formal vs Informal Tone', '2026-04-17 13:44:45.285944', 145, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (103, 'Essential Business Email Phrases

Master these phrases for professional email communication.

OPENING PHRASES:

Acknowledging Receipt:
• Thank you for your email.
• I acknowledge receipt of your email dated...
• Thank you for getting in touch.
• I have received your email regarding...

Referring to Previous Contact:
• Further to our conversation...
• Following our meeting yesterday...
• As discussed in our phone call...
• With reference to your email...

Introducing Yourself:
• My name is [Name] and I am...
• I am writing on behalf of...
• I am the [position] at [company]...
• Allow me to introduce myself...

MAKING REQUESTS:

Polite Requests:
• I would appreciate it if you could...
• Would it be possible to...?
• I would be grateful if you could...
• Could you please...?
• Would you mind...?

Urgent Requests:
• I would appreciate your prompt attention to this matter.
• This is quite urgent, so I would appreciate...
• Due to the urgency of this matter...
• I would be grateful for a quick response.

PROVIDING INFORMATION:

• I am writing to inform you that...
• I would like to let you know that...
• Please be advised that...
• I am pleased to inform you that...
• I regret to inform you that...

ASKING FOR INFORMATION:

• I would like to inquire about...
• Could you please provide information on...?
• I am interested in learning more about...
• I would appreciate more details about...
• Could you clarify...?

MAKING SUGGESTIONS:

• I would suggest that...
• May I suggest...?
• Perhaps we could...
• How about...?
• Would it be possible to...?

APOLOGIZING:

• I apologize for...
• Please accept my apologies for...
• I am sorry for any inconvenience caused.
• I regret that...
• Unfortunately, I must apologize for...

ATTACHING DOCUMENTS:

• Please find attached...
• I have attached...
• Attached you will find...
• I am sending you...
• Please see the attached file.

CONFIRMING:

• I can confirm that...
• I would like to confirm...
• This is to confirm...
• I am writing to confirm...
• As confirmed in our meeting...

OFFERING HELP:

• Please let me know if you need any further information.
• If you have any questions, please don''t hesitate to contact me.
• I am happy to provide additional details.
• Please feel free to contact me if you need assistance.
• I remain at your disposal.

CLOSING PHRASES:

Looking Forward:
• I look forward to hearing from you.
• I look forward to your reply.
• I look forward to meeting you.
• I await your response.

Thanking:
• Thank you for your time and consideration.
• Thank you in advance for your help.
• I appreciate your assistance with this matter.
• Thank you for your attention to this matter.

Availability:
• Please let me know if you have any questions.
• Feel free to contact me if you need more information.
• I am available for a call if you would like to discuss further.
• Don''t hesitate to reach out if you need anything.

EXAMPLE EMAIL USING THESE PHRASES:

Subject: Request for Project Update

Dear Ms. Anderson,

Thank you for your email dated March 15th. I am writing to inquire about the status of the website redesign project.

Could you please provide an update on the following:
• Current progress
• Expected completion date
• Any challenges or concerns

I would appreciate it if you could send this information by the end of the week. Please find attached the latest design mockups for your review.

If you have any questions or need any further information, please don''t hesitate to contact me.

I look forward to hearing from you.

Best regards,
Michael Chen

Practice using these phrases in your emails!', '', '2026-04-17 13:44:45.285944', 'Learn useful phrases for different email situations.', 15, false, true, 'DOCUMENT', 2, 'Common Email Phrases', '2026-04-17 13:44:45.285944', 145, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (104, 'Email Writing Assignment: Request Email

SCENARIO:
You work for TechCorp Inc. as a Project Coordinator. You need to request a meeting with your manager, Sarah Johnson, to discuss the budget for the upcoming Q4 marketing campaign. You want to meet next week and need to present your budget proposal.

ASSIGNMENT REQUIREMENTS:

Write a professional email that includes:

1. SUBJECT LINE (5 points)
   • Clear and specific
   • Indicates the purpose

2. GREETING (5 points)
   • Appropriate level of formality
   • Correct format

3. OPENING (10 points)
   • State your purpose clearly
   • Provide context if needed

4. BODY (20 points)
   • Request the meeting
   • Suggest specific dates/times
   • Mention what you want to discuss
   • Keep it concise and organized

5. CLOSING (10 points)
   • Appropriate closing phrase
   • Call to action

6. SIGN-OFF (5 points)
   • Professional sign-off
   • Complete signature block

7. TONE AND LANGUAGE (15 points)
   • Appropriate formality
   • Professional vocabulary
   • Polite and respectful

8. GRAMMAR AND SPELLING (15 points)
   • No grammatical errors
   • Correct spelling
   • Proper punctuation

9. FORMAT (10 points)
   • Proper email structure
   • Good paragraph organization
   • Easy to read

10. OVERALL EFFECTIVENESS (5 points)
    • Clear communication
    • Achieves the purpose

TOTAL: 100 points
PASSING SCORE: 70 points

TIPS:
• Use formal or semi-formal tone
• Be specific about dates and times
• Keep it brief (150-200 words)
• Proofread carefully
• Use phrases from the lesson

EXAMPLE STRUCTURE:

Subject: [Your subject line]

Dear [Greeting],

[Opening - state purpose]

[Body - make request with details]

[Closing - call to action]

[Sign-off]
[Your signature]

SUBMISSION:
Write your email and submit it for review. You will receive detailed feedback within 48 hours.

Good luck!', '', '2026-04-17 13:44:45.285944', 'Practice writing a professional request email.', 30, false, true, 'ASSIGNMENT', 3, 'Writing Practice: Request Email', '2026-04-17 13:44:45.285944', 145, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (105, 'What is Phonetics?

Phonetics is the scientific study of speech sounds. Understanding phonetics will dramatically improve your English pronunciation and help you speak more clearly and confidently.

WHY PHONETICS MATTERS:

1. ACCURATE PRONUNCIATION
   • Learn to produce sounds correctly
   • Reduce your accent
   • Be understood by native speakers

2. LISTENING COMPREHENSION
   • Recognize sounds in fast speech
   • Understand different accents
   • Improve your listening skills

3. SPELLING VS PRONUNCIATION
   English spelling doesn''t always match pronunciation:
   • "though" /θoʊ/
   • "through" /θruː/
   • "tough" /tʌf/
   • "cough" /kɔːf/
   
   All spelled similarly but pronounced differently!

THE INTERNATIONAL PHONETIC ALPHABET (IPA):

The IPA is a system of symbols where each symbol represents ONE sound. This is crucial because:
• English has 26 letters but 44 sounds
• One letter can make different sounds (e.g., "a" in "cat" vs "cake")
• Multiple letters can make one sound (e.g., "sh" in "ship")

ENGLISH SOUND SYSTEM:

Approximately 44 sounds in English:
• 20 vowel sounds (including diphthongs)
• 24 consonant sounds

VOWELS vs CONSONANTS:

Vowels:
• Air flows freely through the mouth
• Voice is always used
• Examples: /iː/ (see), /æ/ (cat), /ʌ/ (cup)

Consonants:
• Air is blocked or restricted
• May be voiced or voiceless
• Examples: /p/ (pen), /b/ (bed), /s/ (see)

VOICED vs VOICELESS SOUNDS:

Voiced: Vocal cords vibrate
• /b/, /d/, /g/, /v/, /z/
• Put your hand on your throat - you feel vibration

Voiceless: No vocal cord vibration
• /p/, /t/, /k/, /f/, /s/
• No vibration when you touch your throat

PRACTICE TIP:
Start by learning the IPA symbols. Use a dictionary with phonetic transcriptions to check pronunciation of new words.

In the next lessons, we will study each sound in detail and practice producing them correctly.', '', '2026-04-17 13:44:54.175209', 'Learn what phonetics is and why it matters for pronunciation.', 20, true, true, 'VIDEO', 0, 'Introduction to Phonetics', '2026-04-17 13:44:54.175209', 149, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (106, 'The International Phonetic Alphabet (IPA) - Your Pronunciation Guide

The IPA is your key to perfect English pronunciation. Once you learn it, you can pronounce any English word correctly!

WHY USE THE IPA?

1. ONE SYMBOL = ONE SOUND
   Unlike English spelling, each IPA symbol always represents the same sound.

2. UNIVERSAL SYSTEM
   Used in dictionaries worldwide
   Understood by language learners everywhere

3. PRECISE PRONUNCIATION
   Shows exactly how to pronounce words
   No guessing needed

ENGLISH VOWEL SOUNDS (20 total):

SHORT VOWELS:
/ɪ/ - bit, sit, give
/e/ - bed, said, head
/æ/ - cat, bad, man
/ʌ/ - cup, love, money
/ʊ/ - book, good, put
/ɒ/ - hot, dog, want (British)
/ə/ - about, sofa, banana (schwa - most common sound!)

LONG VOWELS:
/iː/ - see, eat, key
/ɑː/ - car, father, start
/ɔː/ - door, saw, bought
/uː/ - food, blue, through
/ɜː/ - bird, work, learn

DIPHTHONGS (two vowel sounds gliding together):
/eɪ/ - day, make, great
/aɪ/ - my, like, high
/ɔɪ/ - boy, coin, voice
/aʊ/ - now, house, loud
/əʊ/ - go, home, know (British)
/oʊ/ - go, home, know (American)
/ɪə/ - here, ear, beer
/eə/ - hair, care, there
/ʊə/ - tour, pure, sure

ENGLISH CONSONANT SOUNDS (24 total):

STOPS (air completely blocked):
/p/ - pen, happy, stop
/b/ - bed, rabbit, cab
/t/ - tea, better, cat
/d/ - dog, ladder, bad
/k/ - cat, school, back
/g/ - go, bigger, bag

FRICATIVES (air forced through narrow gap):
/f/ - fish, coffee, laugh
/v/ - very, over, love
/θ/ - think, author, bath (voiceless th)
/ð/ - this, mother, bathe (voiced th)
/s/ - see, lesson, bus
/z/ - zoo, easy, has
/ʃ/ - ship, nation, wash
/ʒ/ - measure, vision, beige
/h/ - hot, behind, who

AFFRICATES (stop + fricative):
/tʃ/ - church, teacher, watch
/dʒ/ - judge, magic, age

NASALS (air through nose):
/m/ - man, summer, come
/n/ - no, dinner, sun
/ŋ/ - sing, thinking, long

LIQUIDS:
/l/ - leg, yellow, call
/r/ - red, sorry, car

GLIDES:
/w/ - we, away, quick
/j/ - yes, onion, use

READING IPA TRANSCRIPTIONS:

Word: "cat"
IPA: /kæt/
Breakdown: /k/ + /æ/ + /t/

Word: "through"
IPA: /θruː/
Breakdown: /θ/ + /r/ + /uː/

Word: "beautiful"
IPA: /ˈbjuːtɪfl/
Breakdown: /b/ + /j/ + /uː/ + /t/ + /ɪ/ + /f/ + /l/
(The ˈ mark shows primary stress)

STRESS MARKS:
ˈ = primary stress (before the stressed syllable)
ˌ = secondary stress

Example: "understand"
/ˌʌndəˈstænd/
Secondary stress on "un", primary stress on "stand"

PRACTICE EXERCISES:

1. Read these IPA transcriptions:
   /kæt/ = cat
   /dɒg/ = dog
   /haʊs/ = house
   /ˈtiːtʃə/ = teacher

2. Write these words in IPA:
   - book
   - phone
   - water
   - computer

TIPS FOR LEARNING IPA:

1. Start with sounds you know
2. Practice a few symbols each day
3. Use IPA in your dictionary
4. Write new words in IPA
5. Compare similar sounds

In the next lessons, we will practice each sound group in detail!', '', '2026-04-17 13:44:54.175209', 'Learn to read and use the IPA for English pronunciation.', 30, false, true, 'VIDEO', 1, 'The International Phonetic Alphabet', '2026-04-17 13:44:54.175209', 149, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (107, 'Complete English Sound System

This comprehensive guide covers all 44 sounds in English with examples and pronunciation tips.

PART 1: VOWEL SOUNDS

Vowels are the core of every syllable. English has more vowel sounds than most languages!

SHORT VOWELS (7 sounds):

1. /ɪ/ - Short I
   Words: bit, sit, give, women
   Tip: Shorter and more relaxed than /iː/
   
2. /e/ - Short E
   Words: bed, said, head, many
   Tip: Mouth slightly open, tongue mid-high
   
3. /æ/ - Short A (cat vowel)
   Words: cat, bad, man, have
   Tip: Mouth wide open, tongue low
   
4. /ʌ/ - Short U (cup vowel)
   Words: cup, love, money, blood
   Tip: Mouth slightly open, tongue mid-low
   
5. /ʊ/ - Short OO (book vowel)
   Words: book, good, put, could
   Tip: Lips slightly rounded
   
6. /ɒ/ - Short O (British)
   Words: hot, dog, want, what
   Tip: Mouth wide open, lips rounded
   
7. /ə/ - Schwa (the most common sound!)
   Words: about, sofa, banana, the
   Tip: Relaxed, neutral sound in unstressed syllables

LONG VOWELS (5 sounds):

1. /iː/ - Long E
   Words: see, eat, key, people
   Tip: Smile, stretch lips, tense tongue
   
2. /ɑː/ - Long A
   Words: car, father, start, heart
   Tip: Mouth wide open, tongue low and back
   
3. /ɔː/ - Long O
   Words: door, saw, bought, thought
   Tip: Lips rounded, tongue back
   
4. /uː/ - Long OO
   Words: food, blue, through, shoe
   Tip: Lips very rounded, tongue high and back
   
5. /ɜː/ - ER sound
   Words: bird, work, learn, her
   Tip: Lips slightly rounded, tongue mid-central

DIPHTHONGS (8 sounds - vowels that glide):

1. /eɪ/ - Long A (day)
   Words: day, make, great, they
   Glides from /e/ to /ɪ/
   
2. /aɪ/ - Long I (my)
   Words: my, like, high, buy
   Glides from /a/ to /ɪ/
   
3. /ɔɪ/ - OY sound
   Words: boy, coin, voice, toy
   Glides from /ɔ/ to /ɪ/
   
4. /aʊ/ - OW sound (now)
   Words: now, house, loud, how
   Glides from /a/ to /ʊ/
   
5. /əʊ/ or /oʊ/ - Long O (go)
   Words: go, home, know, though
   Glides from /ə/ or /o/ to /ʊ/
   
6. /ɪə/ - EAR sound
   Words: here, ear, beer, idea
   Glides from /ɪ/ to /ə/
   
7. /eə/ - AIR sound
   Words: hair, care, there, bear
   Glides from /e/ to /ə/
   
8. /ʊə/ - OOR sound
   Words: tour, pure, sure, poor
   Glides from /ʊ/ to /ə/

PART 2: CONSONANT SOUNDS

STOPS (6 sounds):
Air is completely blocked then released

Voiceless: /p/ /t/ /k/
Voiced: /b/ /d/ /g/

Pairs:
/p/ - /b/: pen - ben, cap - cab
/t/ - /d/: ten - den, bat - bad
/k/ - /g/: came - game, back - bag

FRICATIVES (9 sounds):
Air forced through narrow gap

Voiceless: /f/ /θ/ /s/ /ʃ/ /h/
Voiced: /v/ /ð/ /z/ /ʒ/

Pairs:
/f/ - /v/: fan - van, leaf - leave
/θ/ - /ð/: think - this, bath - bathe
/s/ - /z/: sue - zoo, bus - buzz
/ʃ/ - /ʒ/: sh - measure

AFFRICATES (2 sounds):
Combination of stop + fricative

/tʃ/: church, teacher, watch
/dʒ/: judge, magic, age

NASALS (3 sounds):
Air flows through nose

/m/: man, summer, come
/n/: no, dinner, sun
/ŋ/: sing, thinking, long

LIQUIDS (2 sounds):
/l/: leg, yellow, call
/r/: red, sorry, car

GLIDES (2 sounds):
/w/: we, away, quick
/j/: yes, onion, use

COMMON PRONUNCIATION CHALLENGES:

1. /θ/ and /ð/ (th sounds)
   Not found in many languages
   Practice: think, this, three, the

2. /v/ and /w/
   Different sounds!
   /v/: teeth touch bottom lip (very, have)
   /w/: lips rounded (we, away)

3. /l/ and /r/
   Very different in English
   /l/: tongue touches roof of mouth
   /r/: tongue doesn''t touch anything

4. /ŋ/ (ng sound)
   One sound, not two!
   sing /sɪŋ/ NOT /sɪng/

PRACTICE TIPS:

1. Record yourself
2. Compare with native speakers
3. Practice minimal pairs (words that differ by one sound)
4. Focus on difficult sounds
5. Practice every day

Remember: Perfect pronunciation takes time and practice. Be patient with yourself!', '', '2026-04-17 13:44:54.175209', 'Overview of all English sounds: vowels, consonants, and diphthongs.', 25, false, true, 'DOCUMENT', 2, 'English Sound System Overview', '2026-04-17 13:44:54.175209', 149, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (108, 'Live Pronunciation Practice Session

This is a live online session where you will practice English sounds with your tutor and receive personalized feedback.

SESSION STRUCTURE (45 minutes):

PART 1: WARM-UP (5 minutes)
• Introduction and goals
• Quick review of IPA symbols
• Vocal warm-up exercises

PART 2: VOWEL PRACTICE (15 minutes)
• Practice short vs long vowels
• Minimal pairs exercises
• Diphthong practice
• Individual pronunciation check

PART 3: CONSONANT PRACTICE (15 minutes)
• Difficult consonants (/θ/, /ð/, /r/, /l/)
• Voiced vs voiceless pairs
• Consonant clusters
• Individual pronunciation check

PART 4: CONNECTED SPEECH (10 minutes)
• Sentences and phrases
• Natural rhythm and flow
• Common reductions
• Real-life practice

WHAT TO PREPARE:

1. Review IPA symbols
2. Identify your difficult sounds
3. Prepare questions
4. Have a mirror ready (to watch your mouth)
5. Quiet environment with good internet

WHAT YOU WILL PRACTICE:

Minimal Pairs:
• ship - sheep
• bit - beat
• cat - cut
• pen - pan
• think - sink

Difficult Sounds:
• /θ/ - think, three, bath
• /ð/ - this, that, mother
• /r/ - red, very, car
• /l/ - light, yellow, call
• /v/ - very, have, love

Sentences:
• "She sells seashells by the seashore."
• "How much wood would a woodchuck chuck?"
• "The thirty-three thieves thought they thrilled the throne."

FEEDBACK YOU WILL RECEIVE:

• Specific sounds to improve
• Mouth position corrections
• Practice exercises for your needs
• Resources for continued practice

AFTER THE SESSION:

• Recording of the session (if permitted)
• Personalized practice plan
• Follow-up exercises
• Progress tracking

TECHNICAL REQUIREMENTS:

• Stable internet connection
• Microphone and camera
• Quiet environment
• Zoom/Google Meet link (provided before session)

BOOKING:
Sessions are scheduled weekly. Check your course calendar for available times.

This interactive session is crucial for improving your pronunciation. Active participation is key!', '', '2026-04-17 13:44:54.175209', 'Live online session to practice sounds with a tutor.', 45, false, true, 'ONLINE', 3, 'Pronunciation Practice Session', '2026-04-17 13:44:54.175209', 149, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (109, 'Understanding English Idioms

Idioms are one of the most fascinating and challenging aspects of English. Mastering them will make you sound more natural and help you understand native speakers better.

WHAT IS AN IDIOM?

An idiom is a phrase or expression whose meaning cannot be understood from the individual words. The meaning is figurative, not literal.

Example:
"It''s raining cats and dogs"
Literal meaning: Cats and dogs are falling from the sky (impossible!)
Idiomatic meaning: It''s raining very heavily

WHY LEARN IDIOMS?

1. SOUND MORE NATURAL
   Native speakers use idioms constantly in everyday conversation.

2. UNDERSTAND NATIVE SPEAKERS
   Movies, TV shows, books, and conversations are full of idioms.

3. EXPRESS IDEAS CREATIVELY
   Idioms add color and personality to your English.

4. CULTURAL UNDERSTANDING
   Many idioms reflect cultural values and history.

TYPES OF IDIOMS:

1. BODY IDIOMS
   • "Keep your chin up" = Stay positive
   • "Cost an arm and a leg" = Very expensive
   • "Give someone a hand" = Help someone

2. ANIMAL IDIOMS
   • "Let the cat out of the bag" = Reveal a secret
   • "Kill two birds with one stone" = Accomplish two things at once
   • "When pigs fly" = Never/impossible

3. FOOD IDIOMS
   • "Piece of cake" = Very easy
   • "Spill the beans" = Reveal a secret
   • "Butter someone up" = Flatter someone

4. COLOR IDIOMS
   • "Out of the blue" = Unexpectedly
   • "Green with envy" = Very jealous
   • "See red" = Become very angry

5. WEATHER IDIOMS
   • "Under the weather" = Feeling sick
   • "Break the ice" = Make people comfortable
   • "Storm in a teacup" = Big fuss about nothing

COMMON IDIOMS FOR BEGINNERS:

EASY TASKS:
• "Piece of cake" - very easy
• "A walk in the park" - very easy
• "Child''s play" - very easy

DIFFICULT TASKS:
• "Not rocket science" - not very difficult
• "Easier said than done" - difficult to do
• "Uphill battle" - very difficult

UNDERSTANDING:
• "Get the picture" - understand
• "Crystal clear" - very clear
• "Greek to me" - don''t understand at all

SECRETS:
• "Spill the beans" - reveal a secret
• "Let the cat out of the bag" - reveal a secret
• "Keep it under wraps" - keep it secret

MONEY:
• "Cost an arm and a leg" - very expensive
• "Break the bank" - very expensive
• "Dirt cheap" - very inexpensive

HOW TO LEARN IDIOMS:

1. LEARN IN CONTEXT
   Don''t just memorize - understand when to use them.
   
   Example:
   "This exam was a piece of cake!"
   (Use after completing something easy)

2. LEARN IN GROUPS
   Group idioms by theme (body, animals, food, etc.)

3. USE THEM
   Practice using idioms in your speaking and writing.

4. WATCH AND LISTEN
   Pay attention to idioms in movies, TV shows, and conversations.

5. KEEP A NOTEBOOK
   Write down new idioms with examples.

IDIOM MISTAKES TO AVOID:

1. WRONG CONTEXT
   Wrong: "I''m feeling under the weather today!" (when happy)
   Right: "I''m feeling under the weather today." (when sick)

2. MIXING IDIOMS
   Wrong: "Let''s kill two cats with one stone."
   Right: "Let''s kill two birds with one stone."

3. LITERAL TRANSLATION
   Don''t translate idioms from your language word-for-word.

4. OVERUSING
   Don''t use too many idioms in formal writing or professional contexts.

PRACTICE:

Match the idiom to its meaning:
1. "Break a leg" → Good luck
2. "Hit the books" → Study hard
3. "Call it a day" → Stop working
4. "On cloud nine" → Very happy
5. "Bite the bullet" → Do something difficult

In the next lessons, we''ll explore 50+ common idioms in detail with examples and practice exercises!', '', '2026-04-17 13:44:54.175209', 'Introduction to idiomatic expressions in English.', 20, false, true, 'VIDEO', 0, 'What Are Idioms?', '2026-04-17 13:44:54.175209', 154, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (110, '50 Essential English Idioms

Master these commonly used idioms to sound more natural in English conversations.

CATEGORY 1: EMOTIONS & FEELINGS

1. "On cloud nine" = Extremely happy
   Example: "She was on cloud nine after getting the job."

2. "Down in the dumps" = Sad, depressed
   Example: "He''s been down in the dumps since his team lost."

3. "Over the moon" = Very happy
   Example: "They were over the moon about the baby news."

4. "Butterflies in my stomach" = Nervous
   Example: "I have butterflies in my stomach before the presentation."

5. "See red" = Become very angry
   Example: "He saw red when someone scratched his car."

CATEGORY 2: DIFFICULTY & EASE

6. "Piece of cake" = Very easy
   Example: "The test was a piece of cake!"

7. "Walk in the park" = Very easy
   Example: "This project is a walk in the park compared to the last one."

8. "Uphill battle" = Very difficult
   Example: "Losing weight is an uphill battle for me."

9. "Back to square one" = Start over
   Example: "The plan failed, so we''re back to square one."

10. "Learn the ropes" = Learn how to do something
    Example: "It takes time to learn the ropes at a new job."

CATEGORY 3: TIME

11. "In the nick of time" = Just in time
    Example: "We arrived at the airport in the nick of time."

12. "Better late than never" = It''s better to do something late than not at all
    Example: "You finally finished the report - better late than never!"

13. "Time flies" = Time passes quickly
    Example: "Time flies when you''re having fun!"

14. "Around the clock" = 24 hours a day
    Example: "The hospital is open around the clock."

15. "Call it a day" = Stop working for the day
    Example: "It''s 6 PM. Let''s call it a day."

CATEGORY 4: COMMUNICATION

16. "Break the ice" = Make people feel comfortable
    Example: "He told a joke to break the ice at the meeting."

17. "Spill the beans" = Reveal a secret
    Example: "Don''t spill the beans about the surprise party!"

18. "Let the cat out of the bag" = Reveal a secret accidentally
    Example: "He let the cat out of the bag about their engagement."

19. "Beat around the bush" = Avoid saying something directly
    Example: "Stop beating around the bush and tell me the truth!"

20. "Get straight to the point" = Say something directly
    Example: "I don''t have much time, so get straight to the point."

CATEGORY 5: MONEY

21. "Cost an arm and a leg" = Very expensive
    Example: "That designer bag costs an arm and a leg!"

22. "Break the bank" = Very expensive
    Example: "This vacation won''t break the bank."

23. "Dirt cheap" = Very inexpensive
    Example: "I bought this shirt for $5 - it was dirt cheap!"

24. "Pay through the nose" = Pay too much
    Example: "We paid through the nose for those concert tickets."

25. "Make ends meet" = Have enough money to live
    Example: "It''s hard to make ends meet with these prices."

CATEGORY 6: SUCCESS & FAILURE

26. "Hit the nail on the head" = Be exactly right
    Example: "You hit the nail on the head with that analysis!"

27. "Miss the boat" = Miss an opportunity
    Example: "I missed the boat on buying that house."

28. "Back to the drawing board" = Start planning again
    Example: "The idea didn''t work, so it''s back to the drawing board."

29. "The ball is in your court" = It''s your turn to act
    Example: "I''ve made my offer. The ball is in your court now."

30. "Throw in the towel" = Give up
    Example: "After trying for hours, he threw in the towel."

CATEGORY 7: UNDERSTANDING

31. "Get the picture" = Understand
    Example: "Do you get the picture now?"

32. "Crystal clear" = Very clear
    Example: "Your instructions were crystal clear."

33. "It''s Greek to me" = I don''t understand at all
    Example: "This math problem is Greek to me!"

34. "Ring a bell" = Sound familiar
    Example: "Does the name John Smith ring a bell?"

35. "Put two and two together" = Understand by connecting facts
    Example: "I put two and two together and realized the truth."

CATEGORY 8: ADVICE & WISDOM

36. "Don''t cry over spilled milk" = Don''t worry about past mistakes
    Example: "You failed the test, but don''t cry over spilled milk."

37. "Actions speak louder than words" = What you do is more important than what you say
    Example: "He says he''ll help, but actions speak louder than words."

38. "The early bird catches the worm" = Success comes to those who start early
    Example: "I always arrive early - the early bird catches the worm!"

39. "Don''t put all your eggs in one basket" = Don''t risk everything on one thing
    Example: "Apply to multiple jobs - don''t put all your eggs in one basket."

40. "When it rains, it pours" = Problems come all at once
    Example: "First my car broke down, then I got sick - when it rains, it pours!"

CATEGORY 9: WORK & EFFORT

41. "Burn the midnight oil" = Work late into the night
    Example: "I''m burning the midnight oil to finish this project."

42. "Go the extra mile" = Make extra effort
    Example: "She always goes the extra mile for her customers."

43. "Pull your weight" = Do your fair share of work
    Example: "Everyone needs to pull their weight on this team."

44. "Cut corners" = Do something poorly to save time/money
    Example: "Don''t cut corners on this project - do it right!"

45. "Get the ball rolling" = Start something
    Example: "Let''s get the ball rolling on this new initiative."

CATEGORY 10: MISCELLANEOUS

46. "Under the weather" = Feeling sick
    Example: "I''m feeling a bit under the weather today."

47. "Once in a blue moon" = Very rarely
    Example: "I only eat fast food once in a blue moon."

48. "The best of both worlds" = All the advantages
    Example: "Working from home gives me the best of both worlds."

49. "Bite off more than you can chew" = Take on too much
    Example: "I bit off more than I could chew with three projects."

50. "It takes two to tango" = Both people are responsible
    Example: "The argument wasn''t just his fault - it takes two to tango."

PRACTICE EXERCISES:

1. Use 5 idioms in sentences about your life
2. Find idioms in a movie or TV show
3. Create dialogues using idioms
4. Teach an idiom to a friend

Remember: Use idioms naturally, don''t force them into every sentence!', '', '2026-04-17 13:44:54.175209', 'Learn 50 frequently used idioms in everyday English.', 35, false, true, 'VIDEO', 1, 'Common Everyday Idioms', '2026-04-17 13:44:54.175209', 154, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (111, 'What is Phonetics?

Phonetics is the scientific study of speech sounds. Understanding phonetics will dramatically improve your English pronunciation and help you speak more clearly and confidently.

WHY PHONETICS MATTERS:

1. ACCURATE PRONUNCIATION
   • Learn to produce sounds correctly
   • Reduce your accent
   • Be understood by native speakers

2. LISTENING COMPREHENSION
   • Recognize sounds in fast speech
   • Understand different accents
   • Improve your listening skills

3. SPELLING VS PRONUNCIATION
   English spelling doesn''t always match pronunciation:
   • "though" /θoʊ/
   • "through" /θruː/
   • "tough" /tʌf/
   • "cough" /kɔːf/
   
   All spelled similarly but pronounced differently!

THE INTERNATIONAL PHONETIC ALPHABET (IPA):

The IPA is a system of symbols where each symbol represents ONE sound. This is crucial because:
• English has 26 letters but 44 sounds
• One letter can make different sounds (e.g., "a" in "cat" vs "cake")
• Multiple letters can make one sound (e.g., "sh" in "ship")

ENGLISH SOUND SYSTEM:

Approximately 44 sounds in English:
• 20 vowel sounds (including diphthongs)
• 24 consonant sounds

VOWELS vs CONSONANTS:

Vowels:
• Air flows freely through the mouth
• Voice is always used
• Examples: /iː/ (see), /æ/ (cat), /ʌ/ (cup)

Consonants:
• Air is blocked or restricted
• May be voiced or voiceless
• Examples: /p/ (pen), /b/ (bed), /s/ (see)

VOICED vs VOICELESS SOUNDS:

Voiced: Vocal cords vibrate
• /b/, /d/, /g/, /v/, /z/
• Put your hand on your throat - you feel vibration

Voiceless: No vocal cord vibration
• /p/, /t/, /k/, /f/, /s/
• No vibration when you touch your throat

PRACTICE TIP:
Start by learning the IPA symbols. Use a dictionary with phonetic transcriptions to check pronunciation of new words.

In the next lessons, we will study each sound in detail and practice producing them correctly.', '', '2026-04-17 13:47:07.190267', 'Learn what phonetics is and why it matters for pronunciation.', 20, true, true, 'VIDEO', 0, 'Introduction to Phonetics', '2026-04-17 13:47:07.190267', 149, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (112, 'The International Phonetic Alphabet (IPA) - Your Pronunciation Guide

The IPA is your key to perfect English pronunciation. Once you learn it, you can pronounce any English word correctly!

WHY USE THE IPA?

1. ONE SYMBOL = ONE SOUND
   Unlike English spelling, each IPA symbol always represents the same sound.

2. UNIVERSAL SYSTEM
   Used in dictionaries worldwide
   Understood by language learners everywhere

3. PRECISE PRONUNCIATION
   Shows exactly how to pronounce words
   No guessing needed

ENGLISH VOWEL SOUNDS (20 total):

SHORT VOWELS:
/ɪ/ - bit, sit, give
/e/ - bed, said, head
/æ/ - cat, bad, man
/ʌ/ - cup, love, money
/ʊ/ - book, good, put
/ɒ/ - hot, dog, want (British)
/ə/ - about, sofa, banana (schwa - most common sound!)

LONG VOWELS:
/iː/ - see, eat, key
/ɑː/ - car, father, start
/ɔː/ - door, saw, bought
/uː/ - food, blue, through
/ɜː/ - bird, work, learn

DIPHTHONGS (two vowel sounds gliding together):
/eɪ/ - day, make, great
/aɪ/ - my, like, high
/ɔɪ/ - boy, coin, voice
/aʊ/ - now, house, loud
/əʊ/ - go, home, know (British)
/oʊ/ - go, home, know (American)
/ɪə/ - here, ear, beer
/eə/ - hair, care, there
/ʊə/ - tour, pure, sure

ENGLISH CONSONANT SOUNDS (24 total):

STOPS (air completely blocked):
/p/ - pen, happy, stop
/b/ - bed, rabbit, cab
/t/ - tea, better, cat
/d/ - dog, ladder, bad
/k/ - cat, school, back
/g/ - go, bigger, bag

FRICATIVES (air forced through narrow gap):
/f/ - fish, coffee, laugh
/v/ - very, over, love
/θ/ - think, author, bath (voiceless th)
/ð/ - this, mother, bathe (voiced th)
/s/ - see, lesson, bus
/z/ - zoo, easy, has
/ʃ/ - ship, nation, wash
/ʒ/ - measure, vision, beige
/h/ - hot, behind, who

AFFRICATES (stop + fricative):
/tʃ/ - church, teacher, watch
/dʒ/ - judge, magic, age

NASALS (air through nose):
/m/ - man, summer, come
/n/ - no, dinner, sun
/ŋ/ - sing, thinking, long

LIQUIDS:
/l/ - leg, yellow, call
/r/ - red, sorry, car

GLIDES:
/w/ - we, away, quick
/j/ - yes, onion, use

READING IPA TRANSCRIPTIONS:

Word: "cat"
IPA: /kæt/
Breakdown: /k/ + /æ/ + /t/

Word: "through"
IPA: /θruː/
Breakdown: /θ/ + /r/ + /uː/

Word: "beautiful"
IPA: /ˈbjuːtɪfl/
Breakdown: /b/ + /j/ + /uː/ + /t/ + /ɪ/ + /f/ + /l/
(The ˈ mark shows primary stress)

STRESS MARKS:
ˈ = primary stress (before the stressed syllable)
ˌ = secondary stress

Example: "understand"
/ˌʌndəˈstænd/
Secondary stress on "un", primary stress on "stand"

PRACTICE EXERCISES:

1. Read these IPA transcriptions:
   /kæt/ = cat
   /dɒg/ = dog
   /haʊs/ = house
   /ˈtiːtʃə/ = teacher

2. Write these words in IPA:
   - book
   - phone
   - water
   - computer

TIPS FOR LEARNING IPA:

1. Start with sounds you know
2. Practice a few symbols each day
3. Use IPA in your dictionary
4. Write new words in IPA
5. Compare similar sounds

In the next lessons, we will practice each sound group in detail!', '', '2026-04-17 13:47:07.190267', 'Learn to read and use the IPA for English pronunciation.', 30, false, true, 'VIDEO', 1, 'The International Phonetic Alphabet', '2026-04-17 13:47:07.190267', 149, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (113, 'Complete English Sound System

This comprehensive guide covers all 44 sounds in English with examples and pronunciation tips.

PART 1: VOWEL SOUNDS

Vowels are the core of every syllable. English has more vowel sounds than most languages!

SHORT VOWELS (7 sounds):

1. /ɪ/ - Short I
   Words: bit, sit, give, women
   Tip: Shorter and more relaxed than /iː/
   
2. /e/ - Short E
   Words: bed, said, head, many
   Tip: Mouth slightly open, tongue mid-high
   
3. /æ/ - Short A (cat vowel)
   Words: cat, bad, man, have
   Tip: Mouth wide open, tongue low
   
4. /ʌ/ - Short U (cup vowel)
   Words: cup, love, money, blood
   Tip: Mouth slightly open, tongue mid-low
   
5. /ʊ/ - Short OO (book vowel)
   Words: book, good, put, could
   Tip: Lips slightly rounded
   
6. /ɒ/ - Short O (British)
   Words: hot, dog, want, what
   Tip: Mouth wide open, lips rounded
   
7. /ə/ - Schwa (the most common sound!)
   Words: about, sofa, banana, the
   Tip: Relaxed, neutral sound in unstressed syllables

LONG VOWELS (5 sounds):

1. /iː/ - Long E
   Words: see, eat, key, people
   Tip: Smile, stretch lips, tense tongue
   
2. /ɑː/ - Long A
   Words: car, father, start, heart
   Tip: Mouth wide open, tongue low and back
   
3. /ɔː/ - Long O
   Words: door, saw, bought, thought
   Tip: Lips rounded, tongue back
   
4. /uː/ - Long OO
   Words: food, blue, through, shoe
   Tip: Lips very rounded, tongue high and back
   
5. /ɜː/ - ER sound
   Words: bird, work, learn, her
   Tip: Lips slightly rounded, tongue mid-central

DIPHTHONGS (8 sounds - vowels that glide):

1. /eɪ/ - Long A (day)
   Words: day, make, great, they
   Glides from /e/ to /ɪ/
   
2. /aɪ/ - Long I (my)
   Words: my, like, high, buy
   Glides from /a/ to /ɪ/
   
3. /ɔɪ/ - OY sound
   Words: boy, coin, voice, toy
   Glides from /ɔ/ to /ɪ/
   
4. /aʊ/ - OW sound (now)
   Words: now, house, loud, how
   Glides from /a/ to /ʊ/
   
5. /əʊ/ or /oʊ/ - Long O (go)
   Words: go, home, know, though
   Glides from /ə/ or /o/ to /ʊ/
   
6. /ɪə/ - EAR sound
   Words: here, ear, beer, idea
   Glides from /ɪ/ to /ə/
   
7. /eə/ - AIR sound
   Words: hair, care, there, bear
   Glides from /e/ to /ə/
   
8. /ʊə/ - OOR sound
   Words: tour, pure, sure, poor
   Glides from /ʊ/ to /ə/

PART 2: CONSONANT SOUNDS

STOPS (6 sounds):
Air is completely blocked then released

Voiceless: /p/ /t/ /k/
Voiced: /b/ /d/ /g/

Pairs:
/p/ - /b/: pen - ben, cap - cab
/t/ - /d/: ten - den, bat - bad
/k/ - /g/: came - game, back - bag

FRICATIVES (9 sounds):
Air forced through narrow gap

Voiceless: /f/ /θ/ /s/ /ʃ/ /h/
Voiced: /v/ /ð/ /z/ /ʒ/

Pairs:
/f/ - /v/: fan - van, leaf - leave
/θ/ - /ð/: think - this, bath - bathe
/s/ - /z/: sue - zoo, bus - buzz
/ʃ/ - /ʒ/: sh - measure

AFFRICATES (2 sounds):
Combination of stop + fricative

/tʃ/: church, teacher, watch
/dʒ/: judge, magic, age

NASALS (3 sounds):
Air flows through nose

/m/: man, summer, come
/n/: no, dinner, sun
/ŋ/: sing, thinking, long

LIQUIDS (2 sounds):
/l/: leg, yellow, call
/r/: red, sorry, car

GLIDES (2 sounds):
/w/: we, away, quick
/j/: yes, onion, use

COMMON PRONUNCIATION CHALLENGES:

1. /θ/ and /ð/ (th sounds)
   Not found in many languages
   Practice: think, this, three, the

2. /v/ and /w/
   Different sounds!
   /v/: teeth touch bottom lip (very, have)
   /w/: lips rounded (we, away)

3. /l/ and /r/
   Very different in English
   /l/: tongue touches roof of mouth
   /r/: tongue doesn''t touch anything

4. /ŋ/ (ng sound)
   One sound, not two!
   sing /sɪŋ/ NOT /sɪng/

PRACTICE TIPS:

1. Record yourself
2. Compare with native speakers
3. Practice minimal pairs (words that differ by one sound)
4. Focus on difficult sounds
5. Practice every day

Remember: Perfect pronunciation takes time and practice. Be patient with yourself!', '', '2026-04-17 13:47:07.190267', 'Overview of all English sounds: vowels, consonants, and diphthongs.', 25, false, true, 'DOCUMENT', 2, 'English Sound System Overview', '2026-04-17 13:47:07.190267', 149, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (114, 'Live Pronunciation Practice Session

This is a live online session where you will practice English sounds with your tutor and receive personalized feedback.

SESSION STRUCTURE (45 minutes):

PART 1: WARM-UP (5 minutes)
• Introduction and goals
• Quick review of IPA symbols
• Vocal warm-up exercises

PART 2: VOWEL PRACTICE (15 minutes)
• Practice short vs long vowels
• Minimal pairs exercises
• Diphthong practice
• Individual pronunciation check

PART 3: CONSONANT PRACTICE (15 minutes)
• Difficult consonants (/θ/, /ð/, /r/, /l/)
• Voiced vs voiceless pairs
• Consonant clusters
• Individual pronunciation check

PART 4: CONNECTED SPEECH (10 minutes)
• Sentences and phrases
• Natural rhythm and flow
• Common reductions
• Real-life practice

WHAT TO PREPARE:

1. Review IPA symbols
2. Identify your difficult sounds
3. Prepare questions
4. Have a mirror ready (to watch your mouth)
5. Quiet environment with good internet

WHAT YOU WILL PRACTICE:

Minimal Pairs:
• ship - sheep
• bit - beat
• cat - cut
• pen - pan
• think - sink

Difficult Sounds:
• /θ/ - think, three, bath
• /ð/ - this, that, mother
• /r/ - red, very, car
• /l/ - light, yellow, call
• /v/ - very, have, love

Sentences:
• "She sells seashells by the seashore."
• "How much wood would a woodchuck chuck?"
• "The thirty-three thieves thought they thrilled the throne."

FEEDBACK YOU WILL RECEIVE:

• Specific sounds to improve
• Mouth position corrections
• Practice exercises for your needs
• Resources for continued practice

AFTER THE SESSION:

• Recording of the session (if permitted)
• Personalized practice plan
• Follow-up exercises
• Progress tracking

TECHNICAL REQUIREMENTS:

• Stable internet connection
• Microphone and camera
• Quiet environment
• Zoom/Google Meet link (provided before session)

BOOKING:
Sessions are scheduled weekly. Check your course calendar for available times.

This interactive session is crucial for improving your pronunciation. Active participation is key!', '', '2026-04-17 13:47:07.190267', 'Live online session to practice sounds with a tutor.', 45, false, true, 'ONLINE', 3, 'Pronunciation Practice Session', '2026-04-17 13:47:07.190267', 149, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (115, 'Understanding English Idioms

Idioms are one of the most fascinating and challenging aspects of English. Mastering them will make you sound more natural and help you understand native speakers better.

WHAT IS AN IDIOM?

An idiom is a phrase or expression whose meaning cannot be understood from the individual words. The meaning is figurative, not literal.

Example:
"It''s raining cats and dogs"
Literal meaning: Cats and dogs are falling from the sky (impossible!)
Idiomatic meaning: It''s raining very heavily

WHY LEARN IDIOMS?

1. SOUND MORE NATURAL
   Native speakers use idioms constantly in everyday conversation.

2. UNDERSTAND NATIVE SPEAKERS
   Movies, TV shows, books, and conversations are full of idioms.

3. EXPRESS IDEAS CREATIVELY
   Idioms add color and personality to your English.

4. CULTURAL UNDERSTANDING
   Many idioms reflect cultural values and history.

TYPES OF IDIOMS:

1. BODY IDIOMS
   • "Keep your chin up" = Stay positive
   • "Cost an arm and a leg" = Very expensive
   • "Give someone a hand" = Help someone

2. ANIMAL IDIOMS
   • "Let the cat out of the bag" = Reveal a secret
   • "Kill two birds with one stone" = Accomplish two things at once
   • "When pigs fly" = Never/impossible

3. FOOD IDIOMS
   • "Piece of cake" = Very easy
   • "Spill the beans" = Reveal a secret
   • "Butter someone up" = Flatter someone

4. COLOR IDIOMS
   • "Out of the blue" = Unexpectedly
   • "Green with envy" = Very jealous
   • "See red" = Become very angry

5. WEATHER IDIOMS
   • "Under the weather" = Feeling sick
   • "Break the ice" = Make people comfortable
   • "Storm in a teacup" = Big fuss about nothing

COMMON IDIOMS FOR BEGINNERS:

EASY TASKS:
• "Piece of cake" - very easy
• "A walk in the park" - very easy
• "Child''s play" - very easy

DIFFICULT TASKS:
• "Not rocket science" - not very difficult
• "Easier said than done" - difficult to do
• "Uphill battle" - very difficult

UNDERSTANDING:
• "Get the picture" - understand
• "Crystal clear" - very clear
• "Greek to me" - don''t understand at all

SECRETS:
• "Spill the beans" - reveal a secret
• "Let the cat out of the bag" - reveal a secret
• "Keep it under wraps" - keep it secret

MONEY:
• "Cost an arm and a leg" - very expensive
• "Break the bank" - very expensive
• "Dirt cheap" - very inexpensive

HOW TO LEARN IDIOMS:

1. LEARN IN CONTEXT
   Don''t just memorize - understand when to use them.
   
   Example:
   "This exam was a piece of cake!"
   (Use after completing something easy)

2. LEARN IN GROUPS
   Group idioms by theme (body, animals, food, etc.)

3. USE THEM
   Practice using idioms in your speaking and writing.

4. WATCH AND LISTEN
   Pay attention to idioms in movies, TV shows, and conversations.

5. KEEP A NOTEBOOK
   Write down new idioms with examples.

IDIOM MISTAKES TO AVOID:

1. WRONG CONTEXT
   Wrong: "I''m feeling under the weather today!" (when happy)
   Right: "I''m feeling under the weather today." (when sick)

2. MIXING IDIOMS
   Wrong: "Let''s kill two cats with one stone."
   Right: "Let''s kill two birds with one stone."

3. LITERAL TRANSLATION
   Don''t translate idioms from your language word-for-word.

4. OVERUSING
   Don''t use too many idioms in formal writing or professional contexts.

PRACTICE:

Match the idiom to its meaning:
1. "Break a leg" → Good luck
2. "Hit the books" → Study hard
3. "Call it a day" → Stop working
4. "On cloud nine" → Very happy
5. "Bite the bullet" → Do something difficult

In the next lessons, we''ll explore 50+ common idioms in detail with examples and practice exercises!', '', '2026-04-17 13:47:07.190267', 'Introduction to idiomatic expressions in English.', 20, false, true, 'VIDEO', 0, 'What Are Idioms?', '2026-04-17 13:47:07.190267', 154, NULL);
INSERT INTO public.lessons (id, content, content_url, created_at, description, duration, is_preview, is_published, lesson_type, order_index, title, updated_at, chapter_id, quiz_id) VALUES (116, '50 Essential English Idioms

Master these commonly used idioms to sound more natural in English conversations.

CATEGORY 1: EMOTIONS & FEELINGS

1. "On cloud nine" = Extremely happy
   Example: "She was on cloud nine after getting the job."

2. "Down in the dumps" = Sad, depressed
   Example: "He''s been down in the dumps since his team lost."

3. "Over the moon" = Very happy
   Example: "They were over the moon about the baby news."

4. "Butterflies in my stomach" = Nervous
   Example: "I have butterflies in my stomach before the presentation."

5. "See red" = Become very angry
   Example: "He saw red when someone scratched his car."

CATEGORY 2: DIFFICULTY & EASE

6. "Piece of cake" = Very easy
   Example: "The test was a piece of cake!"

7. "Walk in the park" = Very easy
   Example: "This project is a walk in the park compared to the last one."

8. "Uphill battle" = Very difficult
   Example: "Losing weight is an uphill battle for me."

9. "Back to square one" = Start over
   Example: "The plan failed, so we''re back to square one."

10. "Learn the ropes" = Learn how to do something
    Example: "It takes time to learn the ropes at a new job."

CATEGORY 3: TIME

11. "In the nick of time" = Just in time
    Example: "We arrived at the airport in the nick of time."

12. "Better late than never" = It''s better to do something late than not at all
    Example: "You finally finished the report - better late than never!"

13. "Time flies" = Time passes quickly
    Example: "Time flies when you''re having fun!"

14. "Around the clock" = 24 hours a day
    Example: "The hospital is open around the clock."

15. "Call it a day" = Stop working for the day
    Example: "It''s 6 PM. Let''s call it a day."

CATEGORY 4: COMMUNICATION

16. "Break the ice" = Make people feel comfortable
    Example: "He told a joke to break the ice at the meeting."

17. "Spill the beans" = Reveal a secret
    Example: "Don''t spill the beans about the surprise party!"

18. "Let the cat out of the bag" = Reveal a secret accidentally
    Example: "He let the cat out of the bag about their engagement."

19. "Beat around the bush" = Avoid saying something directly
    Example: "Stop beating around the bush and tell me the truth!"

20. "Get straight to the point" = Say something directly
    Example: "I don''t have much time, so get straight to the point."

CATEGORY 5: MONEY

21. "Cost an arm and a leg" = Very expensive
    Example: "That designer bag costs an arm and a leg!"

22. "Break the bank" = Very expensive
    Example: "This vacation won''t break the bank."

23. "Dirt cheap" = Very inexpensive
    Example: "I bought this shirt for $5 - it was dirt cheap!"

24. "Pay through the nose" = Pay too much
    Example: "We paid through the nose for those concert tickets."

25. "Make ends meet" = Have enough money to live
    Example: "It''s hard to make ends meet with these prices."

CATEGORY 6: SUCCESS & FAILURE

26. "Hit the nail on the head" = Be exactly right
    Example: "You hit the nail on the head with that analysis!"

27. "Miss the boat" = Miss an opportunity
    Example: "I missed the boat on buying that house."

28. "Back to the drawing board" = Start planning again
    Example: "The idea didn''t work, so it''s back to the drawing board."

29. "The ball is in your court" = It''s your turn to act
    Example: "I''ve made my offer. The ball is in your court now."

30. "Throw in the towel" = Give up
    Example: "After trying for hours, he threw in the towel."

CATEGORY 7: UNDERSTANDING

31. "Get the picture" = Understand
    Example: "Do you get the picture now?"

32. "Crystal clear" = Very clear
    Example: "Your instructions were crystal clear."

33. "It''s Greek to me" = I don''t understand at all
    Example: "This math problem is Greek to me!"

34. "Ring a bell" = Sound familiar
    Example: "Does the name John Smith ring a bell?"

35. "Put two and two together" = Understand by connecting facts
    Example: "I put two and two together and realized the truth."

CATEGORY 8: ADVICE & WISDOM

36. "Don''t cry over spilled milk" = Don''t worry about past mistakes
    Example: "You failed the test, but don''t cry over spilled milk."

37. "Actions speak louder than words" = What you do is more important than what you say
    Example: "He says he''ll help, but actions speak louder than words."

38. "The early bird catches the worm" = Success comes to those who start early
    Example: "I always arrive early - the early bird catches the worm!"

39. "Don''t put all your eggs in one basket" = Don''t risk everything on one thing
    Example: "Apply to multiple jobs - don''t put all your eggs in one basket."

40. "When it rains, it pours" = Problems come all at once
    Example: "First my car broke down, then I got sick - when it rains, it pours!"

CATEGORY 9: WORK & EFFORT

41. "Burn the midnight oil" = Work late into the night
    Example: "I''m burning the midnight oil to finish this project."

42. "Go the extra mile" = Make extra effort
    Example: "She always goes the extra mile for her customers."

43. "Pull your weight" = Do your fair share of work
    Example: "Everyone needs to pull their weight on this team."

44. "Cut corners" = Do something poorly to save time/money
    Example: "Don''t cut corners on this project - do it right!"

45. "Get the ball rolling" = Start something
    Example: "Let''s get the ball rolling on this new initiative."

CATEGORY 10: MISCELLANEOUS

46. "Under the weather" = Feeling sick
    Example: "I''m feeling a bit under the weather today."

47. "Once in a blue moon" = Very rarely
    Example: "I only eat fast food once in a blue moon."

48. "The best of both worlds" = All the advantages
    Example: "Working from home gives me the best of both worlds."

49. "Bite off more than you can chew" = Take on too much
    Example: "I bit off more than I could chew with three projects."

50. "It takes two to tango" = Both people are responsible
    Example: "The argument wasn''t just his fault - it takes two to tango."

PRACTICE EXERCISES:

1. Use 5 idioms in sentences about your life
2. Find idioms in a movie or TV show
3. Create dialogues using idioms
4. Teach an idiom to a friend

Remember: Use idioms naturally, don''t force them into every sentence!', '', '2026-04-17 13:47:07.190267', 'Learn 50 frequently used idioms in everyday English.', 35, false, true, 'VIDEO', 1, 'Common Everyday Idioms', '2026-04-17 13:47:07.190267', 154, NULL);


--
-- Data for Name: lesson_media; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: lesson_progress; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (1, '2026-04-14 17:22:30.572496', 1, '2026-04-14 17:22:30.575499', true, '2026-04-14 17:22:30.572496', 1, 3, NULL, '2026-04-14 17:22:30.575499');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (2, '2026-04-15 02:53:55.941473', 1, '2026-04-15 02:53:55.949471', true, '2026-04-15 02:53:55.941473', 1, 1, NULL, '2026-04-15 02:53:55.949471');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (67, '2026-05-13 14:14:21.214232', 58, '2026-05-13 14:14:21.215235', true, '2026-05-13 14:14:21.214232', 211, 1, NULL, '2026-05-13 14:14:21.215235');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (68, '2026-05-08 15:47:19.799858', 56, NULL, true, '2026-05-08 15:47:19.799858', 162, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (11, '2026-05-09 15:57:08.6971', 57, '2026-05-09 15:57:08.762098', true, '2026-05-09 15:57:08.6971', 178, 1, NULL, '2026-05-09 15:57:08.762098');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (12, '2026-05-09 15:57:11.086904', 57, '2026-05-09 15:57:11.087903', true, '2026-05-09 15:57:11.086904', 179, 1, NULL, '2026-05-09 15:57:11.087903');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (13, '2026-05-09 15:57:13.06931', 57, '2026-05-09 15:57:13.069309', true, '2026-05-09 15:57:13.06931', 180, 1, NULL, '2026-05-09 15:57:13.069309');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (14, '2026-05-09 15:57:15.01651', 57, '2026-05-09 15:57:15.016509', true, '2026-05-09 15:57:15.01651', 181, 1, NULL, '2026-05-09 15:57:15.016509');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (15, '2026-05-09 15:57:17.959187', 57, '2026-05-09 15:57:17.959187', true, '2026-05-09 15:57:17.959187', 182, 1, NULL, '2026-05-09 15:57:17.959187');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (16, '2026-05-09 15:57:19.708473', 57, '2026-05-09 15:57:19.708472', true, '2026-05-09 15:57:19.708473', 183, 1, NULL, '2026-05-09 15:57:19.708472');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (17, '2026-05-09 15:57:22.173069', 57, '2026-05-09 15:57:22.17407', true, '2026-05-09 15:57:22.173069', 184, 1, NULL, '2026-05-09 15:57:22.17407');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (18, '2026-05-09 15:57:24.108687', 57, '2026-05-09 15:57:24.108687', true, '2026-05-09 15:57:24.108687', 185, 1, NULL, '2026-05-09 15:57:24.108687');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (19, '2026-05-09 15:57:35.83262', 56, '2026-05-09 15:57:35.83262', true, '2026-05-09 15:57:35.83262', 164, 1, NULL, '2026-05-09 15:57:35.83262');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (20, '2026-05-09 15:57:38.260751', 56, '2026-05-09 15:57:38.26075', true, '2026-05-09 15:57:38.260751', 165, 1, NULL, '2026-05-09 15:57:38.26075');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (21, '2026-05-09 15:57:42.621471', 56, '2026-05-09 15:57:42.62247', true, '2026-05-09 15:57:42.621471', 166, 1, NULL, '2026-05-09 15:57:42.62247');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (22, '2026-05-09 16:08:59.773956', 57, '2026-05-09 16:08:59.773955', true, '2026-05-09 16:08:59.773956', 212, 1, NULL, '2026-05-09 16:08:59.773955');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (23, '2026-05-13 11:25:10.137831', 56, '2026-05-13 11:25:12.305822', true, '2026-05-13 11:25:13.916143', 162, 1, NULL, '2026-05-13 11:25:13.960143');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (26, '2026-05-13 11:27:50.031283', 56, '2026-05-13 11:27:50.031282', true, '2026-05-13 11:27:50.031283', 163, 1, NULL, '2026-05-13 11:27:50.031282');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (27, '2026-05-13 11:27:58.384278', 56, '2026-05-13 11:27:58.384278', true, '2026-05-13 11:27:58.384278', 167, 1, NULL, '2026-05-13 11:27:58.384278');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (28, '2026-05-13 11:32:35.005256', 54, '2026-05-13 11:32:35.006286', true, '2026-05-13 11:32:35.005256', 127, 1, NULL, '2026-05-13 11:32:35.006286');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (29, '2026-05-13 11:32:37.441283', 54, '2026-05-13 11:32:37.442284', true, '2026-05-13 11:32:37.441283', 128, 1, NULL, '2026-05-13 11:32:37.442284');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (30, '2026-05-13 11:32:40.082171', 54, '2026-05-13 11:32:40.08217', true, '2026-05-13 11:32:40.082171', 129, 1, NULL, '2026-05-13 11:32:40.08217');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (31, '2026-05-13 11:33:13.379979', 54, '2026-05-13 11:33:13.397974', true, '2026-05-13 11:33:13.379979', 130, 1, NULL, '2026-05-13 11:33:13.398976');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (32, '2026-05-13 11:46:58.998549', 54, '2026-05-13 11:46:58.998549', true, '2026-05-13 11:46:58.998549', 137, 1, NULL, '2026-05-13 11:46:58.998549');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (33, '2026-05-13 11:47:01.046626', 54, '2026-05-13 11:47:01.046625', true, '2026-05-13 11:47:01.046626', 138, 1, NULL, '2026-05-13 11:47:01.046625');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (34, '2026-05-13 11:47:31.033565', 54, '2026-05-13 11:47:31.033565', true, '2026-05-13 11:47:31.033565', 139, 1, NULL, '2026-05-13 11:47:31.033565');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (35, '2026-05-13 11:52:24.515477', 54, '2026-05-13 11:52:24.516478', true, '2026-05-13 11:52:24.515477', 213, 1, NULL, '2026-05-13 11:52:24.516478');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (36, '2026-05-13 12:05:00.233841', 54, '2026-05-13 12:05:00.907681', true, '2026-05-13 12:05:00.233841', 215, 1, NULL, '2026-05-13 12:05:00.907681');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (37, '2026-05-13 12:31:05.055797', 54, '2026-05-13 12:31:05.315829', true, '2026-05-13 12:31:05.055797', 216, 1, NULL, '2026-05-13 12:31:05.332829');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (38, '2026-05-13 12:31:09.835178', 54, '2026-05-13 12:31:09.853177', true, '2026-05-13 12:31:09.835178', 217, 1, NULL, '2026-05-13 12:31:09.853177');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (39, '2026-05-13 13:24:55.030197', 54, '2026-05-13 13:24:55.073198', true, '2026-05-13 13:24:55.030197', 131, 1, NULL, '2026-05-13 13:24:55.073198');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (40, '2026-05-13 13:24:57.260677', 54, '2026-05-13 13:24:57.261677', true, '2026-05-13 13:24:57.260677', 132, 1, NULL, '2026-05-13 13:24:57.261677');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (41, '2026-05-13 13:24:59.045007', 54, '2026-05-13 13:24:59.045007', true, '2026-05-13 13:24:59.045007', 133, 1, NULL, '2026-05-13 13:24:59.045007');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (42, '2026-05-13 13:25:01.296248', 54, '2026-05-13 13:25:01.297248', true, '2026-05-13 13:25:01.296248', 134, 1, NULL, '2026-05-13 13:25:01.297248');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (43, '2026-05-13 13:25:03.230201', 54, '2026-05-13 13:25:03.2302', true, '2026-05-13 13:25:03.230201', 135, 1, NULL, '2026-05-13 13:25:03.2302');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (44, '2026-05-13 13:25:05.296743', 54, '2026-05-13 13:25:05.296743', true, '2026-05-13 13:25:05.296743', 136, 1, NULL, '2026-05-13 13:25:05.296743');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (45, '2026-05-13 13:25:11.543454', 54, '2026-05-13 13:25:11.543454', true, '2026-05-13 13:25:11.543454', 140, 1, NULL, '2026-05-13 13:25:11.543454');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (46, '2026-05-13 13:25:14.430344', 54, '2026-05-13 13:25:14.431343', true, '2026-05-13 13:25:14.430344', 141, 1, NULL, '2026-05-13 13:25:14.431343');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (47, '2026-05-13 13:25:30.100126', 54, '2026-05-13 13:25:30.101126', true, '2026-05-13 13:25:30.100126', 142, 1, NULL, '2026-05-13 13:25:30.101126');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (48, '2026-05-13 13:25:31.922571', 54, '2026-05-13 13:25:31.92357', true, '2026-05-13 13:25:31.922571', 143, 1, NULL, '2026-05-13 13:25:31.92357');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (49, '2026-05-13 13:25:34.645333', 54, '2026-05-13 13:25:34.645332', true, '2026-05-13 13:25:34.645333', 144, 1, NULL, '2026-05-13 13:25:34.645332');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (50, '2026-05-13 13:25:36.547416', 54, '2026-05-13 13:25:36.547416', true, '2026-05-13 13:25:36.547416', 145, 1, NULL, '2026-05-13 13:25:36.547416');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (51, '2026-05-13 13:33:23.225436', 58, '2026-05-13 13:33:23.226438', true, '2026-05-13 13:33:23.225436', 195, 1, NULL, '2026-05-13 13:33:23.226438');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (52, '2026-05-13 13:33:25.589512', 58, '2026-05-13 13:33:25.589511', true, '2026-05-13 13:33:25.589512', 196, 1, NULL, '2026-05-13 13:33:25.589511');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (53, '2026-05-13 13:33:27.458108', 58, '2026-05-13 13:33:27.458108', true, '2026-05-13 13:33:27.458108', 197, 1, NULL, '2026-05-13 13:33:27.458108');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (54, '2026-05-13 14:13:55.028378', 58, '2026-05-13 14:13:55.030377', true, '2026-05-13 14:13:55.028378', 198, 1, NULL, '2026-05-13 14:13:55.030377');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (55, '2026-05-13 14:13:57.865883', 58, '2026-05-13 14:13:57.865883', true, '2026-05-13 14:13:57.865883', 199, 1, NULL, '2026-05-13 14:13:57.865883');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (56, '2026-05-13 14:13:59.66197', 58, '2026-05-13 14:13:59.66197', true, '2026-05-13 14:13:59.66197', 200, 1, NULL, '2026-05-13 14:13:59.66197');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (57, '2026-05-13 14:14:01.556027', 58, '2026-05-13 14:14:01.556027', true, '2026-05-13 14:14:01.556027', 201, 1, NULL, '2026-05-13 14:14:01.556027');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (58, '2026-05-13 14:14:03.327326', 58, '2026-05-13 14:14:03.327325', true, '2026-05-13 14:14:03.327326', 202, 1, NULL, '2026-05-13 14:14:03.327325');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (59, '2026-05-13 14:14:05.01301', 58, '2026-05-13 14:14:05.014039', true, '2026-05-13 14:14:05.01301', 203, 1, NULL, '2026-05-13 14:14:05.014039');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (60, '2026-05-13 14:14:07.488995', 58, '2026-05-13 14:14:07.488994', true, '2026-05-13 14:14:07.488995', 204, 1, NULL, '2026-05-13 14:14:07.488994');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (61, '2026-05-13 14:14:09.429185', 58, '2026-05-13 14:14:09.429185', true, '2026-05-13 14:14:09.429185', 205, 1, NULL, '2026-05-13 14:14:09.429185');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (62, '2026-05-13 14:14:11.269496', 58, '2026-05-13 14:14:11.269496', true, '2026-05-13 14:14:11.269496', 206, 1, NULL, '2026-05-13 14:14:11.269496');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (63, '2026-05-13 14:14:13.373012', 58, '2026-05-13 14:14:13.373012', true, '2026-05-13 14:14:13.373012', 207, 1, NULL, '2026-05-13 14:14:13.373012');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (64, '2026-05-13 14:14:15.329751', 58, '2026-05-13 14:14:15.330781', true, '2026-05-13 14:14:15.329751', 208, 1, NULL, '2026-05-13 14:14:15.330781');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (65, '2026-05-13 14:14:17.538545', 58, '2026-05-13 14:14:17.538545', true, '2026-05-13 14:14:17.538545', 209, 1, NULL, '2026-05-13 14:14:17.538545');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (66, '2026-05-13 14:14:19.45162', 58, '2026-05-13 14:14:19.45162', true, '2026-05-13 14:14:19.45162', 210, 1, NULL, '2026-05-13 14:14:19.45162');
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (69, '2026-05-08 15:47:19.799858', 56, NULL, true, '2026-05-08 15:47:19.799858', 166, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (70, '2026-05-08 15:47:19.799858', 56, NULL, true, '2026-05-08 15:47:19.799858', 170, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (71, '2026-05-08 15:47:19.799858', 56, NULL, true, '2026-05-08 15:47:19.799858', 174, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (72, '2026-05-08 15:47:19.799858', 56, NULL, true, '2026-05-08 15:47:19.799858', 167, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (73, '2026-05-08 15:47:19.799858', 56, NULL, true, '2026-05-08 15:47:19.799858', 171, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (74, '2026-05-08 15:47:19.799858', 56, NULL, true, '2026-05-08 15:47:19.799858', 175, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (75, '2026-05-08 15:47:19.799858', 56, NULL, true, '2026-05-08 15:47:19.799858', 163, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (76, '2026-05-08 15:47:19.799858', 56, NULL, true, '2026-05-08 15:47:19.799858', 172, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (77, '2026-05-08 15:47:19.799858', 56, NULL, true, '2026-05-08 15:47:19.799858', 168, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (78, '2026-05-08 15:47:19.799858', 56, NULL, true, '2026-05-08 15:47:19.799858', 176, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (79, '2026-05-08 15:47:19.799858', 56, NULL, true, '2026-05-08 15:47:19.799858', 164, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (80, '2026-05-08 15:47:19.799858', 56, NULL, true, '2026-05-08 15:47:19.799858', 177, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (81, '2026-05-08 15:47:19.799858', 56, NULL, true, '2026-05-08 15:47:19.799858', 169, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (82, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 212, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (83, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 182, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (84, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 187, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (85, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 191, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (86, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 178, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (87, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 183, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (88, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 188, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (89, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 179, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (90, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 192, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (91, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 193, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (92, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 189, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (93, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 184, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (94, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 180, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (95, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 194, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (96, '2026-05-08 15:47:19.799858', 57, NULL, true, '2026-05-08 15:47:19.799858', 190, 57, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (97, '2026-05-06 15:47:19.799858', 56, NULL, true, '2026-05-06 15:47:19.799858', 162, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (98, '2026-05-06 15:47:19.799858', 56, NULL, true, '2026-05-06 15:47:19.799858', 166, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (99, '2026-05-06 15:47:19.799858', 56, NULL, true, '2026-05-06 15:47:19.799858', 170, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (100, '2026-05-06 15:47:19.799858', 56, NULL, true, '2026-05-06 15:47:19.799858', 174, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (101, '2026-05-06 15:47:19.799858', 56, NULL, true, '2026-05-06 15:47:19.799858', 167, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (102, '2026-05-06 15:47:19.799858', 56, NULL, true, '2026-05-06 15:47:19.799858', 171, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (103, '2026-05-06 15:47:19.799858', 56, NULL, true, '2026-05-06 15:47:19.799858', 175, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (104, '2026-05-06 15:47:19.799858', 56, NULL, true, '2026-05-06 15:47:19.799858', 163, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (105, '2026-05-06 15:47:19.799858', 56, NULL, true, '2026-05-06 15:47:19.799858', 172, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (106, '2026-05-06 15:47:19.799858', 56, NULL, true, '2026-05-06 15:47:19.799858', 168, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (107, '2026-05-06 15:47:19.799858', 56, NULL, true, '2026-05-06 15:47:19.799858', 176, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (108, '2026-05-06 15:47:19.799858', 56, NULL, true, '2026-05-06 15:47:19.799858', 164, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (109, '2026-05-06 15:47:19.799858', 57, NULL, true, '2026-05-06 15:47:19.799858', 212, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (110, '2026-05-06 15:47:19.799858', 57, NULL, true, '2026-05-06 15:47:19.799858', 182, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (111, '2026-05-06 15:47:19.799858', 57, NULL, true, '2026-05-06 15:47:19.799858', 187, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (112, '2026-05-06 15:47:19.799858', 57, NULL, true, '2026-05-06 15:47:19.799858', 191, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (113, '2026-05-06 15:47:19.799858', 57, NULL, true, '2026-05-06 15:47:19.799858', 178, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (114, '2026-05-06 15:47:19.799858', 57, NULL, true, '2026-05-06 15:47:19.799858', 183, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (115, '2026-05-06 15:47:19.799858', 57, NULL, true, '2026-05-06 15:47:19.799858', 188, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (116, '2026-05-06 15:47:19.799858', 57, NULL, true, '2026-05-06 15:47:19.799858', 179, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (117, '2026-05-06 15:47:19.799858', 57, NULL, true, '2026-05-06 15:47:19.799858', 192, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (118, '2026-05-06 15:47:19.799858', 57, NULL, true, '2026-05-06 15:47:19.799858', 193, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (119, '2026-05-06 15:47:19.799858', 57, NULL, true, '2026-05-06 15:47:19.799858', 189, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (120, '2026-05-06 15:47:19.799858', 57, NULL, true, '2026-05-06 15:47:19.799858', 184, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (121, '2026-05-06 15:47:19.799858', 57, NULL, true, '2026-05-06 15:47:19.799858', 180, 58, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (122, '2026-05-03 15:47:19.799858', 56, NULL, true, '2026-05-03 15:47:19.799858', 162, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (123, '2026-05-03 15:47:19.799858', 56, NULL, true, '2026-05-03 15:47:19.799858', 166, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (124, '2026-05-03 15:47:19.799858', 56, NULL, true, '2026-05-03 15:47:19.799858', 170, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (125, '2026-05-03 15:47:19.799858', 56, NULL, true, '2026-05-03 15:47:19.799858', 174, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (126, '2026-05-03 15:47:19.799858', 56, NULL, true, '2026-05-03 15:47:19.799858', 167, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (127, '2026-05-03 15:47:19.799858', 56, NULL, true, '2026-05-03 15:47:19.799858', 171, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (128, '2026-05-03 15:47:19.799858', 56, NULL, true, '2026-05-03 15:47:19.799858', 175, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (129, '2026-05-03 15:47:19.799858', 56, NULL, true, '2026-05-03 15:47:19.799858', 163, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (130, '2026-05-03 15:47:19.799858', 56, NULL, true, '2026-05-03 15:47:19.799858', 172, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (131, '2026-05-03 15:47:19.799858', 56, NULL, true, '2026-05-03 15:47:19.799858', 168, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (132, '2026-05-03 15:47:19.799858', 57, NULL, true, '2026-05-03 15:47:19.799858', 212, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (133, '2026-05-03 15:47:19.799858', 57, NULL, true, '2026-05-03 15:47:19.799858', 182, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (134, '2026-05-03 15:47:19.799858', 57, NULL, true, '2026-05-03 15:47:19.799858', 187, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (135, '2026-05-03 15:47:19.799858', 57, NULL, true, '2026-05-03 15:47:19.799858', 191, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (136, '2026-05-03 15:47:19.799858', 57, NULL, true, '2026-05-03 15:47:19.799858', 178, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (137, '2026-05-03 15:47:19.799858', 57, NULL, true, '2026-05-03 15:47:19.799858', 183, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (138, '2026-05-03 15:47:19.799858', 57, NULL, true, '2026-05-03 15:47:19.799858', 188, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (139, '2026-05-03 15:47:19.799858', 57, NULL, true, '2026-05-03 15:47:19.799858', 179, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (140, '2026-05-03 15:47:19.799858', 57, NULL, true, '2026-05-03 15:47:19.799858', 192, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (141, '2026-05-03 15:47:19.799858', 57, NULL, true, '2026-05-03 15:47:19.799858', 193, 59, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (142, '2026-04-28 15:47:19.799858', 56, NULL, true, '2026-04-28 15:47:19.799858', 162, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (143, '2026-04-28 15:47:19.799858', 56, NULL, true, '2026-04-28 15:47:19.799858', 166, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (144, '2026-04-28 15:47:19.799858', 56, NULL, true, '2026-04-28 15:47:19.799858', 170, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (145, '2026-04-28 15:47:19.799858', 56, NULL, true, '2026-04-28 15:47:19.799858', 174, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (146, '2026-04-28 15:47:19.799858', 56, NULL, true, '2026-04-28 15:47:19.799858', 167, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (147, '2026-04-28 15:47:19.799858', 56, NULL, true, '2026-04-28 15:47:19.799858', 171, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (148, '2026-04-28 15:47:19.799858', 56, NULL, true, '2026-04-28 15:47:19.799858', 175, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (149, '2026-04-28 15:47:19.799858', 57, NULL, true, '2026-04-28 15:47:19.799858', 212, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (150, '2026-04-28 15:47:19.799858', 57, NULL, true, '2026-04-28 15:47:19.799858', 182, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (151, '2026-04-28 15:47:19.799858', 57, NULL, true, '2026-04-28 15:47:19.799858', 187, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (152, '2026-04-28 15:47:19.799858', 57, NULL, true, '2026-04-28 15:47:19.799858', 191, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (153, '2026-04-28 15:47:19.799858', 57, NULL, true, '2026-04-28 15:47:19.799858', 178, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (154, '2026-04-28 15:47:19.799858', 57, NULL, true, '2026-04-28 15:47:19.799858', 183, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (155, '2026-04-28 15:47:19.799858', 57, NULL, true, '2026-04-28 15:47:19.799858', 188, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (156, '2026-04-28 15:47:19.799858', 57, NULL, true, '2026-04-28 15:47:19.799858', 179, 60, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (157, '2026-04-23 15:47:19.799858', 56, NULL, true, '2026-04-23 15:47:19.799858', 162, 61, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (158, '2026-04-23 15:47:19.799858', 56, NULL, true, '2026-04-23 15:47:19.799858', 166, 61, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (159, '2026-04-23 15:47:19.799858', 56, NULL, true, '2026-04-23 15:47:19.799858', 170, 61, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (160, '2026-04-23 15:47:19.799858', 56, NULL, true, '2026-04-23 15:47:19.799858', 174, 61, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (161, '2026-04-23 15:47:19.799858', 56, NULL, true, '2026-04-23 15:47:19.799858', 167, 61, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (162, '2026-04-23 15:47:19.799858', 57, NULL, true, '2026-04-23 15:47:19.799858', 212, 61, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (163, '2026-04-23 15:47:19.799858', 57, NULL, true, '2026-04-23 15:47:19.799858', 182, 61, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (164, '2026-04-23 15:47:19.799858', 57, NULL, true, '2026-04-23 15:47:19.799858', 187, 61, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (165, '2026-04-23 15:47:19.799858', 57, NULL, true, '2026-04-23 15:47:19.799858', 191, 61, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (166, '2026-04-23 15:47:19.799858', 57, NULL, true, '2026-04-23 15:47:19.799858', 178, 61, NULL, NULL);
INSERT INTO public.lesson_progress (id, completed_at, course_id, created_at, is_completed, last_accessed_at, lesson_id, student_id, time_spent, updated_at) VALUES (167, '2026-04-23 15:47:19.799858', 57, NULL, true, '2026-04-23 15:47:19.799858', 183, 61, NULL, NULL);


--
-- Data for Name: online_lessons; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: lesson_schedule; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: lesson_schedules; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: lesson_sessions; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: lesson_time_assignments; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.lesson_time_assignments (id, lesson_id, tutor_id, day_of_week, start_time, end_time, created_at) VALUES (1, 11, 2, 'WEDNESDAY', '02:13:02.762303', '04:23:02.762303', '2026-04-15 02:09:30.211124');


--
-- Data for Name: online_meeting_sessions; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.online_meeting_sessions (id, lesson_id, room_id, invite_link, tutor_id, started_at, ended_at, is_active) VALUES (1, 11, 'lesson-11-1776213716276', 'http://localhost:4200/join/lesson-11-1776213716276', 2, '2026-04-15 02:52:01.708049', '2026-04-15 02:52:20.845983', false);
INSERT INTO public.online_meeting_sessions (id, lesson_id, room_id, invite_link, tutor_id, started_at, ended_at, is_active) VALUES (2, 11, 'lesson-11-1776214361992', 'http://localhost:4200/join/lesson-11-1776214361992', 2, '2026-04-15 02:52:42.941998', '2026-04-15 03:03:09.821483', false);
INSERT INTO public.online_meeting_sessions (id, lesson_id, room_id, invite_link, tutor_id, started_at, ended_at, is_active) VALUES (3, 11, 'lesson-11', 'http://localhost:4200/join/lesson-11', 2, '2026-04-15 03:03:17.706636', '2026-04-15 03:03:36.219722', false);
INSERT INTO public.online_meeting_sessions (id, lesson_id, room_id, invite_link, tutor_id, started_at, ended_at, is_active) VALUES (4, 15, 'lesson-15', 'http://localhost:4200/join/lesson-15', 13, '2026-04-15 16:41:19.290673', '2026-04-15 16:41:48.059715', false);
INSERT INTO public.online_meeting_sessions (id, lesson_id, room_id, invite_link, tutor_id, started_at, ended_at, is_active) VALUES (5, 17, 'lesson-17', 'http://localhost:4200/join/lesson-17', 13, '2026-04-15 16:43:23.454478', '2026-04-15 16:44:25.051415', false);
INSERT INTO public.online_meeting_sessions (id, lesson_id, room_id, invite_link, tutor_id, started_at, ended_at, is_active) VALUES (6, 23, 'lesson-23', 'http://localhost:4200/join/lesson-23', 13, '2026-04-15 20:35:04.610388', NULL, true);


--
-- Data for Name: packs; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.packs (id, category, conversation_id, created_at, created_by, current_enrolled_students, description, enrollment_end_date, enrollment_start_date, estimated_duration, level, max_students, name, price, status, tutor_id, tutor_name, tutor_rating, updated_at) VALUES (5, 'Vocabulary', 7, '2026-05-09 15:43:45.787147', 53, 1, 'Achieve near-native fluency! This advanced package focuses on expanding your vocabulary to C1 level with 1000+ words, idioms, phrasal verbs, and collocations. Includes pronunciation refinement to sound more natural. Perfect for advanced learners aiming for mastery.', '2026-08-07 15:43:45.787147', '2026-05-09 15:43:45.787147', 65, 'C1', 30, 'Advanced Fluency & Vocabulary Package', 179.99, 'ACTIVE', 53, 'Khalil Abdelmoumen', 4.8, '2026-05-09 15:56:55.113343');
INSERT INTO public.packs (id, category, conversation_id, created_at, created_by, current_enrolled_students, description, enrollment_end_date, enrollment_start_date, estimated_duration, level, max_students, name, price, status, tutor_id, tutor_name, tutor_rating, updated_at) VALUES (2, 'Exam Preparation', 1, '2026-04-15 16:35:55.863045', 5, 1, 'azdazdazd', '2026-04-30 16:35:00', '2026-04-01 16:35:00', 0, 'A1', 30, 'Exam Preparation A1 - Teacher  Khalil', 10.00, 'ACTIVE', 13, 'Teacher  Khalil', 0, '2026-04-15 16:36:35.934585');
INSERT INTO public.packs (id, category, conversation_id, created_at, created_by, current_enrolled_students, description, enrollment_end_date, enrollment_start_date, estimated_duration, level, max_students, name, price, status, tutor_id, tutor_name, tutor_rating, updated_at) VALUES (3, 'Grammar', 5, '2026-05-09 15:43:45.787147', 53, 1, 'Perfect for absolute beginners! This comprehensive package includes Grammar Fundamentals and Conversational English Practice. Master the basics of English grammar while building confidence in everyday conversations. Ideal for A1-A2 level learners starting their English journey.', '2026-08-07 15:43:45.787147', '2026-05-09 15:43:45.787147', 65, 'A1', 50, 'Beginner English Complete Package', 169.99, 'ACTIVE', 53, 'Khalil Abdelmoumen', 4.8, '2026-05-09 15:45:45.27972');
INSERT INTO public.packs (id, category, conversation_id, created_at, created_by, current_enrolled_students, description, enrollment_end_date, enrollment_start_date, estimated_duration, level, max_students, name, price, status, tutor_id, tutor_name, tutor_rating, updated_at) VALUES (4, 'Business English', 6, '2026-05-09 15:43:45.787147', 53, 0, 'Take your English to the next level! This bundle combines Pronunciation Mastery and Business English Communication. Perfect your accent and pronunciation while learning professional business communication skills. Designed for B1-B2 level learners.', '2026-08-07 15:43:45.787147', '2026-05-09 15:43:45.787147', 80, 'B2', 40, 'Intermediate Skills Development Bundle', 199.99, 'ACTIVE', 53, 'Khalil Abdelmoumen', 4.8, '2026-05-09 15:43:45.787147');
INSERT INTO public.packs (id, category, conversation_id, created_at, created_by, current_enrolled_students, description, enrollment_end_date, enrollment_start_date, estimated_duration, level, max_students, name, price, status, tutor_id, tutor_name, tutor_rating, updated_at) VALUES (6, 'Business English', 8, '2026-05-09 15:43:45.787147', 53, 0, 'Excel in your professional career! This specialized package combines Business English Communication with Advanced Vocabulary Builder. Master business communication, presentations, negotiations, and expand your professional vocabulary. Essential for career advancement.', '2026-08-07 15:43:45.787147', '2026-05-09 15:43:45.787147', 85, 'B2', 35, 'Professional Business English Mastery', 239.99, 'ACTIVE', 53, 'Khalil Abdelmoumen', 4.8, '2026-05-09 15:43:45.787147');
INSERT INTO public.packs (id, category, conversation_id, created_at, created_by, current_enrolled_students, description, enrollment_end_date, enrollment_start_date, estimated_duration, level, max_students, name, price, status, tutor_id, tutor_name, tutor_rating, updated_at) VALUES (7, 'Pronunciation', 9, '2026-05-09 15:43:45.787147', 53, 0, 'Speak English confidently and clearly! This package combines Conversational English Practice with Pronunciation Mastery. Perfect your pronunciation, master phonetics, and practice real-life conversations. Ideal for learners who want to improve their speaking skills.', '2026-08-07 15:43:45.787147', '2026-05-09 15:43:45.787147', 55, 'B1', 45, 'Speaking & Pronunciation Excellence', 149.99, 'ACTIVE', 53, 'Khalil Abdelmoumen', 4.8, '2026-05-09 15:43:45.787147');
INSERT INTO public.packs (id, category, conversation_id, created_at, created_by, current_enrolled_students, description, enrollment_end_date, enrollment_start_date, estimated_duration, level, max_students, name, price, status, tutor_id, tutor_name, tutor_rating, updated_at) VALUES (8, 'Grammar', 10, '2026-05-09 15:43:45.787147', 53, 0, 'The ultimate English learning package! Get ALL 5 courses at an incredible discount. From beginner grammar to advanced vocabulary, business communication to perfect pronunciation. This comprehensive bundle takes you from A1 to C1 level. Best value for serious learners!', '2026-11-05 15:43:45.787147', '2026-05-09 15:43:45.787147', 180, 'A1', 25, 'Complete English Mastery - All Courses Bundle', 449.99, 'ACTIVE', 53, 'Khalil Abdelmoumen', 4.8, '2026-05-09 15:43:45.787147');


--
-- Data for Name: pack_courses; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.pack_courses (pack_id, course_id) VALUES (2, 3);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (2, 2);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (3, 54);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (3, 58);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (4, 56);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (4, 55);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (5, 57);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (5, 56);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (6, 55);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (6, 57);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (7, 58);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (7, 56);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (8, 54);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (8, 55);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (8, 56);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (8, 57);
INSERT INTO public.pack_courses (pack_id, course_id) VALUES (8, 58);


--
-- Data for Name: pack_enrollments; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.pack_enrollments (id, completed_at, enrolled_at, is_active, pack_category, pack_id, pack_level, pack_name, status, student_id, student_name, total_courses, tutor_id, tutor_name) VALUES (3, NULL, '2026-04-15 16:36:34.916435', true, 'Exam Preparation', 2, 'A1', 'Exam Preparation A1 - Teacher  Khalil', 'ACTIVE', 7, 'aziz abdelmoumen', 2, 13, 'Teacher  Khalil');
INSERT INTO public.pack_enrollments (id, completed_at, enrolled_at, is_active, pack_category, pack_id, pack_level, pack_name, status, student_id, student_name, total_courses, tutor_id, tutor_name) VALUES (4, NULL, '2026-05-09 15:45:43.122457', true, 'Grammar', 3, 'A1', 'Beginner English Complete Package', 'ACTIVE', 1, 'Khalil Abdelmoumen', 2, 53, 'Khalil Abdelmoumen');
INSERT INTO public.pack_enrollments (id, completed_at, enrolled_at, is_active, pack_category, pack_id, pack_level, pack_name, status, student_id, student_name, total_courses, tutor_id, tutor_name) VALUES (5, NULL, '2026-05-09 15:56:53.361911', true, 'Vocabulary', 5, 'C1', 'Advanced Fluency & Vocabulary Package', 'ACTIVE', 1, 'Khalil Abdelmoumen', 2, 53, 'Khalil Abdelmoumen');
INSERT INTO public.pack_enrollments (id, completed_at, enrolled_at, is_active, pack_category, pack_id, pack_level, pack_name, status, student_id, student_name, total_courses, tutor_id, tutor_name) VALUES (6, NULL, '2026-02-13 15:20:34.990134', true, 'Vocabulary', 5, 'C1', 'Advanced Fluency & Vocabulary Package', 'ACTIVE', 57, 'Sarah Johnson', 3, 53, 'Khalil Abdelmoumen');
INSERT INTO public.pack_enrollments (id, completed_at, enrolled_at, is_active, pack_category, pack_id, pack_level, pack_name, status, student_id, student_name, total_courses, tutor_id, tutor_name) VALUES (7, NULL, '2026-03-13 15:20:34.990134', true, 'Vocabulary', 5, 'C1', 'Advanced Fluency & Vocabulary Package', 'ACTIVE', 58, 'Michael Chen', 3, 53, 'Khalil Abdelmoumen');
INSERT INTO public.pack_enrollments (id, completed_at, enrolled_at, is_active, pack_category, pack_id, pack_level, pack_name, status, student_id, student_name, total_courses, tutor_id, tutor_name) VALUES (8, NULL, '2026-04-13 15:20:34.990134', true, 'Vocabulary', 5, 'C1', 'Advanced Fluency & Vocabulary Package', 'ACTIVE', 59, 'Emma Garcia', 3, 53, 'Khalil Abdelmoumen');
INSERT INTO public.pack_enrollments (id, completed_at, enrolled_at, is_active, pack_category, pack_id, pack_level, pack_name, status, student_id, student_name, total_courses, tutor_id, tutor_name) VALUES (9, NULL, '2026-04-13 15:20:34.990134', true, 'Vocabulary', 5, 'C1', 'Advanced Fluency & Vocabulary Package', 'ACTIVE', 60, 'David Martinez', 3, 53, 'Khalil Abdelmoumen');
INSERT INTO public.pack_enrollments (id, completed_at, enrolled_at, is_active, pack_category, pack_id, pack_level, pack_name, status, student_id, student_name, total_courses, tutor_id, tutor_name) VALUES (10, NULL, '2026-03-13 15:20:34.990134', true, 'Vocabulary', 5, 'C1', 'Advanced Fluency & Vocabulary Package', 'ACTIVE', 61, 'Lisa Anderson', 3, 53, 'Khalil Abdelmoumen');


--
-- Data for Name: session_attendance; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: tutor_availability; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.tutor_availability (id, created_at, current_students_count, last_updated, max_students_capacity, status, tutor_id, tutor_name, day_of_week, start_time, end_time, is_available, updated_at, locked) VALUES (1, '2026-04-14 16:59:19.815582', 0, '2026-04-14 16:59:19.815582', 30, 'AVAILABLE', 2, 'Teacher  For Test', 1, '09:00:00', '17:00:00', true, '2026-04-15 02:02:58.901997', false);
INSERT INTO public.tutor_availability (id, created_at, current_students_count, last_updated, max_students_capacity, status, tutor_id, tutor_name, day_of_week, start_time, end_time, is_available, updated_at, locked) VALUES (2, '2026-04-15 16:23:08.343487', 0, '2026-04-15 16:23:08.343487', 30, 'AVAILABLE', 13, 'Teacher  Khalil', 1, '09:00:00', '17:00:00', true, '2026-04-15 16:23:08.299379', false);
INSERT INTO public.tutor_availability (id, created_at, current_students_count, last_updated, max_students_capacity, status, tutor_id, tutor_name, day_of_week, start_time, end_time, is_available, updated_at, locked) VALUES (3, '2026-05-09 16:41:31.815627', 0, '2026-05-09 16:41:31.815627', 15, 'AVAILABLE', 53, 'Mr Amazing', 1, '09:00:00', '17:00:00', true, '2026-05-09 16:41:31.735055', false);


--
-- Data for Name: time_slots; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.time_slots (id, end_time, start_time, tutor_availability_id) VALUES (1, '12:00:00', '09:00:00', 1);
INSERT INTO public.time_slots (id, end_time, start_time, tutor_availability_id) VALUES (2, '17:00:00', '14:00:00', 1);
INSERT INTO public.time_slots (id, end_time, start_time, tutor_availability_id) VALUES (3, '12:00:00', '10:00:00', 2);
INSERT INTO public.time_slots (id, end_time, start_time, tutor_availability_id) VALUES (4, '16:30:00', '15:00:00', 2);
INSERT INTO public.time_slots (id, end_time, start_time, tutor_availability_id) VALUES (5, '12:00:00', '09:00:00', 3);
INSERT INTO public.time_slots (id, end_time, start_time, tutor_availability_id) VALUES (6, '17:00:00', '14:00:00', 3);


--
-- Data for Name: tutor_availability_days; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: tutor_available_days; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.tutor_available_days (availability_id, day) VALUES (1, 'SUNDAY');
INSERT INTO public.tutor_available_days (availability_id, day) VALUES (1, 'THURSDAY');
INSERT INTO public.tutor_available_days (availability_id, day) VALUES (1, 'MONDAY');
INSERT INTO public.tutor_available_days (availability_id, day) VALUES (1, 'FRIDAY');
INSERT INTO public.tutor_available_days (availability_id, day) VALUES (1, 'TUESDAY');
INSERT INTO public.tutor_available_days (availability_id, day) VALUES (1, 'SATURDAY');
INSERT INTO public.tutor_available_days (availability_id, day) VALUES (1, 'WEDNESDAY');
INSERT INTO public.tutor_available_days (availability_id, day) VALUES (2, 'MONDAY');
INSERT INTO public.tutor_available_days (availability_id, day) VALUES (2, 'TUESDAY');
INSERT INTO public.tutor_available_days (availability_id, day) VALUES (3, 'MONDAY');
INSERT INTO public.tutor_available_days (availability_id, day) VALUES (3, 'TUESDAY');
INSERT INTO public.tutor_available_days (availability_id, day) VALUES (3, 'FRIDAY');
INSERT INTO public.tutor_available_days (availability_id, day) VALUES (3, 'SATURDAY');


--
-- Data for Name: tutor_categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.tutor_categories (availability_id, category) VALUES (1, 'Kids English');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (1, 'Vocabulary');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (1, 'Exam Preparation');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (1, 'Business English');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (1, 'Pronunciation');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (1, 'General English');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (1, 'Writing');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (1, 'Academic English');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (1, 'Conversation');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (1, 'Grammar');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (2, 'Business English');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (2, 'General English');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (2, 'Conversation');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (3, 'Business English');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (3, 'General English');
INSERT INTO public.tutor_categories (availability_id, category) VALUES (3, 'Conversation');


--
-- Data for Name: tutor_levels; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.tutor_levels (availability_id, level) VALUES (1, 'A1');
INSERT INTO public.tutor_levels (availability_id, level) VALUES (1, 'B2');
INSERT INTO public.tutor_levels (availability_id, level) VALUES (1, 'A2');
INSERT INTO public.tutor_levels (availability_id, level) VALUES (1, 'C1');
INSERT INTO public.tutor_levels (availability_id, level) VALUES (1, 'B1');
INSERT INTO public.tutor_levels (availability_id, level) VALUES (1, 'C2');
INSERT INTO public.tutor_levels (availability_id, level) VALUES (2, 'A1');
INSERT INTO public.tutor_levels (availability_id, level) VALUES (2, 'B2');
INSERT INTO public.tutor_levels (availability_id, level) VALUES (2, 'A2');
INSERT INTO public.tutor_levels (availability_id, level) VALUES (2, 'C1');
INSERT INTO public.tutor_levels (availability_id, level) VALUES (3, 'A1');
INSERT INTO public.tutor_levels (availability_id, level) VALUES (3, 'A2');
INSERT INTO public.tutor_levels (availability_id, level) VALUES (3, 'B1');


--
-- Name: availability_modification_request_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.availability_modification_request_id_seq', 4, true);


--
-- Name: chapter_progress_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.chapter_progress_id_seq', 1, false);


--
-- Name: chapters_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.chapters_id_seq', 183, true);


--
-- Name: course_categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.course_categories_id_seq', 10, true);


--
-- Name: course_enrollments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.course_enrollments_id_seq', 24, true);


--
-- Name: courses_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.courses_id_seq', 58, true);


--
-- Name: lesson_media_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lesson_media_id_seq', 1, false);


--
-- Name: lesson_progress_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lesson_progress_id_seq', 167, true);


--
-- Name: lesson_schedule_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lesson_schedule_id_seq', 1, false);


--
-- Name: lesson_schedules_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lesson_schedules_id_seq', 1, false);


--
-- Name: lesson_sessions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lesson_sessions_id_seq', 1, false);


--
-- Name: lesson_time_assignments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lesson_time_assignments_id_seq', 4, true);


--
-- Name: lessons_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lessons_id_seq', 217, true);


--
-- Name: online_lessons_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.online_lessons_id_seq', 1, false);


--
-- Name: online_meeting_sessions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.online_meeting_sessions_id_seq', 6, true);


--
-- Name: pack_enrollments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pack_enrollments_id_seq', 10, true);


--
-- Name: packs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.packs_id_seq', 8, true);


--
-- Name: session_attendance_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.session_attendance_id_seq', 1, false);


--
-- Name: time_slots_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.time_slots_id_seq', 6, true);


--
-- Name: tutor_availability_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tutor_availability_id_seq', 3, true);


--
-- PostgreSQL database dump complete
--

\unrestrict db1pXChhBsBz10NalzgEDjGYFrdtuk1vs33svC0vtbImymgbHzUyVKyGX2EBeH4

