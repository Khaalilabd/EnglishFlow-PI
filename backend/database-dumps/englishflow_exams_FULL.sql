--
-- PostgreSQL database dump
--

\restrict UuEmCacKru5qpLSUwRK4thIfSY7XjfRzEwKT4BkuTEgI9t7U5bh45p0Ns8qA4P0

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
-- Data for Name: correct_answers; Type: TABLE DATA; Schema: public; Owner: postgres
--

SET SESSION AUTHORIZATION DEFAULT;

ALTER TABLE public.correct_answers DISABLE TRIGGER ALL;



ALTER TABLE public.correct_answers ENABLE TRIGGER ALL;

--
-- Data for Name: exams; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.exams DISABLE TRIGGER ALL;



ALTER TABLE public.exams ENABLE TRIGGER ALL;

--
-- Data for Name: exam_parts; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.exam_parts DISABLE TRIGGER ALL;



ALTER TABLE public.exam_parts ENABLE TRIGGER ALL;

--
-- Data for Name: exam_results; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.exam_results DISABLE TRIGGER ALL;



ALTER TABLE public.exam_results ENABLE TRIGGER ALL;

--
-- Data for Name: questions; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.questions DISABLE TRIGGER ALL;



ALTER TABLE public.questions ENABLE TRIGGER ALL;

--
-- Data for Name: question_options; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.question_options DISABLE TRIGGER ALL;



ALTER TABLE public.question_options ENABLE TRIGGER ALL;

--
-- Data for Name: student_exam_attempts; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.student_exam_attempts DISABLE TRIGGER ALL;



ALTER TABLE public.student_exam_attempts ENABLE TRIGGER ALL;

--
-- Data for Name: student_answers; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.student_answers DISABLE TRIGGER ALL;



ALTER TABLE public.student_answers ENABLE TRIGGER ALL;

--
-- PostgreSQL database dump complete
--

\unrestrict UuEmCacKru5qpLSUwRK4thIfSY7XjfRzEwKT4BkuTEgI9t7U5bh45p0Ns8qA4P0

