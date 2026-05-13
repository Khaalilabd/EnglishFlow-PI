--
-- PostgreSQL database dump
--

\restrict yaN2DvC8WyVpvaqvNx5Lx2podMI9HyvIwLiAm3uUY9MFElfwp4lipnh4GjidoD3

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
-- Data for Name: conversations; Type: TABLE DATA; Schema: public; Owner: postgres
--

SET SESSION AUTHORIZATION DEFAULT;

ALTER TABLE public.conversations DISABLE TRIGGER ALL;

INSERT INTO public.conversations (id, created_at, created_by, description, group_photo, last_message_at, title, type, updated_at) VALUES (1, '2026-04-15 16:36:07.336658', 13, 'azdazdazd', NULL, '2026-05-01 12:05:40.153976', 'Pack: Exam Preparation A1 - Teacher  Khalil', 'GROUP', '2026-05-01 12:05:40.153975');
INSERT INTO public.conversations (id, created_at, created_by, description, group_photo, last_message_at, title, type, updated_at) VALUES (2, '2026-05-01 12:05:07.849641', 1, NULL, NULL, '2026-05-01 12:12:47.228154', NULL, 'DIRECT', '2026-05-01 12:12:47.230153');
INSERT INTO public.conversations (id, created_at, created_by, description, group_photo, last_message_at, title, type, updated_at) VALUES (3, '2026-05-06 00:49:38.980651', 1, NULL, NULL, '2026-05-06 00:50:47.199637', NULL, 'DIRECT', '2026-05-06 00:50:47.201638');
INSERT INTO public.conversations (id, created_at, created_by, description, group_photo, last_message_at, title, type, updated_at) VALUES (5, '2026-05-09 15:53:15.848036', 53, 'Discussion group for Beginner English Complete Package. Ask questions, share progress, and connect with fellow learners!', NULL, '2026-05-09 15:53:15.848036', 'Pack: Beginner English Complete Package - Teacher Khalil', 'GROUP', '2026-05-09 15:53:15.848036');
INSERT INTO public.conversations (id, created_at, created_by, description, group_photo, last_message_at, title, type, updated_at) VALUES (6, '2026-05-09 15:53:15.848036', 53, 'Discussion group for Intermediate Skills Development Bundle. Practice your skills and get feedback from your tutor!', NULL, '2026-05-09 15:53:15.848036', 'Pack: Intermediate Skills Development Bundle - Teacher Khalil', 'GROUP', '2026-05-09 15:53:15.848036');
INSERT INTO public.conversations (id, created_at, created_by, description, group_photo, last_message_at, title, type, updated_at) VALUES (8, '2026-05-09 15:53:15.848036', 53, 'Discussion group for Professional Business English Mastery. Network with professionals and improve your business English!', NULL, '2026-05-09 15:53:15.848036', 'Pack: Professional Business English Mastery - Teacher Khalil', 'GROUP', '2026-05-09 15:53:15.848036');
INSERT INTO public.conversations (id, created_at, created_by, description, group_photo, last_message_at, title, type, updated_at) VALUES (9, '2026-05-09 15:53:15.848036', 53, 'Discussion group for Speaking & Pronunciation Excellence. Practice speaking and get pronunciation tips!', NULL, '2026-05-09 15:53:15.848036', 'Pack: Speaking & Pronunciation Excellence - Teacher Khalil', 'GROUP', '2026-05-09 15:53:15.848036');
INSERT INTO public.conversations (id, created_at, created_by, description, group_photo, last_message_at, title, type, updated_at) VALUES (10, '2026-05-09 15:53:15.848036', 53, 'Discussion group for Complete English Mastery Bundle. Join our comprehensive learning community!', NULL, '2026-05-09 15:53:15.848036', 'Pack: Complete English Mastery - All Courses Bundle - Teacher Khalil', 'GROUP', '2026-05-09 15:53:15.848036');
INSERT INTO public.conversations (id, created_at, created_by, description, group_photo, last_message_at, title, type, updated_at) VALUES (7, '2026-05-09 15:53:15.848036', 53, 'Discussion group for Advanced Fluency & Vocabulary Package. Share advanced vocabulary and practice together!', NULL, '2026-05-09 15:58:30.964816', 'Pack: Advanced Fluency & Vocabulary Package - Teacher Khalil', 'GROUP', '2026-05-09 15:58:30.966816');


ALTER TABLE public.conversations ENABLE TRIGGER ALL;

--
-- Data for Name: conversation_participants; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.conversation_participants DISABLE TRIGGER ALL;

INSERT INTO public.conversation_participants (id, is_active, joined_at, last_read_at, participant_role, user_avatar, user_email, user_id, user_name, user_role, conversation_id) VALUES (1, true, '2026-04-15 16:36:08.392649', NULL, 'ADMIN', NULL, 'khalilabdelmoumen11@gmail.com', 13, 'Teacher  Khalil', 'TUTOR', 1);
INSERT INTO public.conversation_participants (id, is_active, joined_at, last_read_at, participant_role, user_avatar, user_email, user_id, user_name, user_role, conversation_id) VALUES (2, true, '2026-04-15 16:36:35.207304', '2026-05-01 12:05:36.652219', 'MEMBER', 'https://lh3.googleusercontent.com/a/ACg8ocJg8e74WoQ0cfeNa2I0VQV54kO-abaLpqwt2RUIg7FX2enUKegX=s96-c', 'aabdelmoumen4@gmail.com', 7, 'aziz abdelmoumen', 'STUDENT', 1);
INSERT INTO public.conversation_participants (id, is_active, joined_at, last_read_at, participant_role, user_avatar, user_email, user_id, user_name, user_role, conversation_id) VALUES (4, true, '2026-05-01 12:05:07.905349', '2026-05-01 12:12:41.627557', 'MEMBER', 'https://lh3.googleusercontent.com/a/ACg8ocJg8e74WoQ0cfeNa2I0VQV54kO-abaLpqwt2RUIg7FX2enUKegX=s96-c', 'aabdelmoumen4@gmail.com', 7, 'aziz abdelmoumen', 'STUDENT', 2);
INSERT INTO public.conversation_participants (id, is_active, joined_at, last_read_at, participant_role, user_avatar, user_email, user_id, user_name, user_role, conversation_id) VALUES (3, true, '2026-05-01 12:05:07.877419', '2026-05-06 00:49:09.07775', 'MEMBER', '/uploads/profile-photos/80e23873-51c2-425f-9d58-694205a531d0.jpg', 'khalilabdelmoumen7@gmail.com', 1, 'Khalil Abdelmoumen', 'ADMIN', 2);
INSERT INTO public.conversation_participants (id, is_active, joined_at, last_read_at, participant_role, user_avatar, user_email, user_id, user_name, user_role, conversation_id) VALUES (5, true, '2026-05-06 00:49:39.000651', '2026-05-06 00:49:39.855098', 'MEMBER', '/uploads/profile-photos/548791b3-c534-4bd0-9e42-b5034686369e.jpg', 'khalilabdelmoumen7@gmail.com', 1, 'Khalil Abdelmoumen', 'STUDENT', 3);
INSERT INTO public.conversation_participants (id, is_active, joined_at, last_read_at, participant_role, user_avatar, user_email, user_id, user_name, user_role, conversation_id) VALUES (6, true, '2026-05-06 00:49:39.024651', '2026-05-06 00:50:02.945716', 'MEMBER', NULL, 'khalilabdelmoumen11@gmail.com', 13, 'Teacher  Khalil', 'STUDENT', 3);
INSERT INTO public.conversation_participants (id, is_active, joined_at, last_read_at, participant_role, user_avatar, user_email, user_id, user_name, user_role, conversation_id) VALUES (7, true, '2026-05-09 15:53:15.848036', NULL, 'ADMIN', NULL, 'khalilabdelmoumen11@gmail.com', 53, 'Mr Amazing', 'TUTOR', 5);
INSERT INTO public.conversation_participants (id, is_active, joined_at, last_read_at, participant_role, user_avatar, user_email, user_id, user_name, user_role, conversation_id) VALUES (8, true, '2026-05-09 15:53:15.848036', NULL, 'ADMIN', NULL, 'khalilabdelmoumen11@gmail.com', 53, 'Mr Amazing', 'TUTOR', 6);
INSERT INTO public.conversation_participants (id, is_active, joined_at, last_read_at, participant_role, user_avatar, user_email, user_id, user_name, user_role, conversation_id) VALUES (10, true, '2026-05-09 15:53:15.848036', NULL, 'ADMIN', NULL, 'khalilabdelmoumen11@gmail.com', 53, 'Mr Amazing', 'TUTOR', 8);
INSERT INTO public.conversation_participants (id, is_active, joined_at, last_read_at, participant_role, user_avatar, user_email, user_id, user_name, user_role, conversation_id) VALUES (11, true, '2026-05-09 15:53:15.848036', NULL, 'ADMIN', NULL, 'khalilabdelmoumen11@gmail.com', 53, 'Mr Amazing', 'TUTOR', 9);
INSERT INTO public.conversation_participants (id, is_active, joined_at, last_read_at, participant_role, user_avatar, user_email, user_id, user_name, user_role, conversation_id) VALUES (12, true, '2026-05-09 15:53:15.848036', NULL, 'ADMIN', NULL, 'khalilabdelmoumen11@gmail.com', 53, 'Mr Amazing', 'TUTOR', 10);
INSERT INTO public.conversation_participants (id, is_active, joined_at, last_read_at, participant_role, user_avatar, user_email, user_id, user_name, user_role, conversation_id) VALUES (13, true, '2026-05-09 15:56:54.562788', '2026-05-09 15:57:47.791888', 'MEMBER', '/uploads/profile-photos/548791b3-c534-4bd0-9e42-b5034686369e.jpg', 'khalilabdelmoumen7@gmail.com', 1, 'Khalil Abdelmoumen', 'STUDENT', 7);
INSERT INTO public.conversation_participants (id, is_active, joined_at, last_read_at, participant_role, user_avatar, user_email, user_id, user_name, user_role, conversation_id) VALUES (9, true, '2026-05-09 15:53:15.848036', '2026-05-09 15:58:11.971578', 'ADMIN', NULL, 'khalilabdelmoumen11@gmail.com', 53, 'Mr Amazing', 'TUTOR', 7);


ALTER TABLE public.conversation_participants ENABLE TRIGGER ALL;

--
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.messages DISABLE TRIGGER ALL;

INSERT INTO public.messages (id, content, created_at, emoji_code, file_name, file_size, file_url, is_edited, message_type, sender_avatar, sender_id, sender_name, updated_at, voice_duration, conversation_id) VALUES (1, '🎉 aziz abdelmoumen a rejoint le pack!', '2026-04-15 16:36:35.347303', NULL, NULL, NULL, NULL, false, 'TEXT', NULL, 0, 'Système', '2026-04-15 16:36:35.347303', NULL, 1);
INSERT INTO public.messages (id, content, created_at, emoji_code, file_name, file_size, file_url, is_edited, message_type, sender_avatar, sender_id, sender_name, updated_at, voice_duration, conversation_id) VALUES (2, 'Bonjour Aziz', '2026-05-01 12:05:12.063613', NULL, NULL, NULL, NULL, false, 'TEXT', '/uploads/profile-photos/80e23873-51c2-425f-9d58-694205a531d0.jpg', 1, 'Khalil Abdelmoumen', '2026-05-01 12:05:12.063613', NULL, 2);
INSERT INTO public.messages (id, content, created_at, emoji_code, file_name, file_size, file_url, is_edited, message_type, sender_avatar, sender_id, sender_name, updated_at, voice_duration, conversation_id) VALUES (3, 'Hello', '2026-05-01 12:05:40.153975', NULL, NULL, NULL, NULL, false, 'TEXT', 'https://lh3.googleusercontent.com/a/ACg8ocJg8e74WoQ0cfeNa2I0VQV54kO-abaLpqwt2RUIg7FX2enUKegX=s96-c', 7, 'aziz abdelmoumen', '2026-05-01 12:05:40.153975', NULL, 1);
INSERT INTO public.messages (id, content, created_at, emoji_code, file_name, file_size, file_url, is_edited, message_type, sender_avatar, sender_id, sender_name, updated_at, voice_duration, conversation_id) VALUES (4, 'Bonjour Khaill', '2026-05-01 12:12:47.200157', NULL, NULL, NULL, NULL, false, 'TEXT', 'https://lh3.googleusercontent.com/a/ACg8ocJg8e74WoQ0cfeNa2I0VQV54kO-abaLpqwt2RUIg7FX2enUKegX=s96-c', 7, 'aziz abdelmoumen', '2026-05-01 12:12:47.200157', NULL, 2);
INSERT INTO public.messages (id, content, created_at, emoji_code, file_name, file_size, file_url, is_edited, message_type, sender_avatar, sender_id, sender_name, updated_at, voice_duration, conversation_id) VALUES (5, 'Bonjour Comment cava ?', '2026-05-06 00:49:54.962595', NULL, NULL, NULL, NULL, false, 'TEXT', '/uploads/profile-photos/548791b3-c534-4bd0-9e42-b5034686369e.jpg', 1, 'Khalil Abdelmoumen', '2026-05-06 00:49:54.962595', NULL, 3);
INSERT INTO public.messages (id, content, created_at, emoji_code, file_name, file_size, file_url, is_edited, message_type, sender_avatar, sender_id, sender_name, updated_at, voice_duration, conversation_id) VALUES (6, 'Très bien et vous ?', '2026-05-06 00:50:47.197635', NULL, NULL, NULL, NULL, false, 'TEXT', NULL, 13, 'Teacher  Khalil', '2026-05-06 00:50:47.197635', NULL, 3);
INSERT INTO public.messages (id, content, created_at, emoji_code, file_name, file_size, file_url, is_edited, message_type, sender_avatar, sender_id, sender_name, updated_at, voice_duration, conversation_id) VALUES (7, '🎉 Khalil Abdelmoumen a rejoint le pack!', '2026-05-09 15:56:54.96234', NULL, NULL, NULL, NULL, false, 'TEXT', NULL, 0, 'Système', '2026-05-09 15:56:54.96234', NULL, 7);
INSERT INTO public.messages (id, content, created_at, emoji_code, file_name, file_size, file_url, is_edited, message_type, sender_avatar, sender_id, sender_name, updated_at, voice_duration, conversation_id) VALUES (8, '❤️', '2026-05-09 15:58:20.232865', 'U+2764', NULL, NULL, NULL, false, 'EMOJI', '/uploads/profile-photos/61acc877-124e-4f9a-9ca8-e4b705d2502e.jpg', 53, 'Mr Amazing', '2026-05-09 15:58:20.232865', NULL, 7);
INSERT INTO public.messages (id, content, created_at, emoji_code, file_name, file_size, file_url, is_edited, message_type, sender_avatar, sender_id, sender_name, updated_at, voice_duration, conversation_id) VALUES (9, '❤️', '2026-05-09 15:58:22.85172', 'U+2764', NULL, NULL, NULL, false, 'EMOJI', '/uploads/profile-photos/61acc877-124e-4f9a-9ca8-e4b705d2502e.jpg', 53, 'Mr Amazing', '2026-05-09 15:58:22.85172', NULL, 7);
INSERT INTO public.messages (id, content, created_at, emoji_code, file_name, file_size, file_url, is_edited, message_type, sender_avatar, sender_id, sender_name, updated_at, voice_duration, conversation_id) VALUES (10, 'Welcome To you Khalil', '2026-05-09 15:58:24.172121', NULL, NULL, NULL, NULL, false, 'TEXT', '/uploads/profile-photos/61acc877-124e-4f9a-9ca8-e4b705d2502e.jpg', 53, 'Mr Amazing', '2026-05-09 15:58:24.172121', NULL, 7);
INSERT INTO public.messages (id, content, created_at, emoji_code, file_name, file_size, file_url, is_edited, message_type, sender_avatar, sender_id, sender_name, updated_at, voice_duration, conversation_id) VALUES (11, 'Thanks :)', '2026-05-09 15:58:30.961815', NULL, NULL, NULL, NULL, false, 'TEXT', '/uploads/profile-photos/548791b3-c534-4bd0-9e42-b5034686369e.jpg', 1, 'Khalil Abdelmoumen', '2026-05-09 15:58:30.961815', NULL, 7);


ALTER TABLE public.messages ENABLE TRIGGER ALL;

--
-- Data for Name: message_reactions; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.message_reactions DISABLE TRIGGER ALL;

INSERT INTO public.message_reactions (id, created_at, emoji, user_id, user_name, message_id) VALUES (1, '2026-05-06 00:50:11.44525', '❤️', 13, 'Teacher  Khalil', 5);


ALTER TABLE public.message_reactions ENABLE TRIGGER ALL;

--
-- Data for Name: message_read_status; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.message_read_status DISABLE TRIGGER ALL;



ALTER TABLE public.message_read_status ENABLE TRIGGER ALL;

--
-- Name: conversation_participants_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.conversation_participants_id_seq', 13, true);


--
-- Name: conversations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.conversations_id_seq', 10, true);


--
-- Name: message_reactions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.message_reactions_id_seq', 1, true);


--
-- Name: message_read_status_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.message_read_status_id_seq', 1, false);


--
-- Name: messages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.messages_id_seq', 11, true);


--
-- PostgreSQL database dump complete
--

\unrestrict yaN2DvC8WyVpvaqvNx5Lx2podMI9HyvIwLiAm3uUY9MFElfwp4lipnh4GjidoD3

