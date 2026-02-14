-- Create database
CREATE DATABASE learning_db;

-- Connect to the database
\c learning_db;

-- Tables will be created automatically by Hibernate with ddl-auto: update
-- This script is just for reference

-- Quiz table
-- CREATE TABLE quiz (
--     id BIGSERIAL PRIMARY KEY,
--     title VARCHAR(255) NOT NULL,
--     description TEXT,
--     course_id BIGINT,
--     duration_min INTEGER,
--     max_score INTEGER,
--     passing_scr INTEGER,
--     published BOOLEAN NOT NULL DEFAULT FALSE,
--     due_date TIMESTAMP,
--     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
-- );

-- Question table
-- CREATE TABLE question (
--     id BIGSERIAL PRIMARY KEY,
--     quiz_id BIGINT NOT NULL REFERENCES quiz(id) ON DELETE CASCADE,
--     content TEXT NOT NULL,
--     type VARCHAR(50) NOT NULL,
--     options TEXT,
--     correct_ans TEXT,
--     points INTEGER NOT NULL DEFAULT 1,
--     order_index INTEGER
-- );

-- Quiz attempt table
-- CREATE TABLE quiz_attempt (
--     id BIGSERIAL PRIMARY KEY,
--     quiz_id BIGINT NOT NULL,
--     student_id BIGINT NOT NULL,
--     score INTEGER,
--     started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     submitted_at TIMESTAMP,
--     status VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS'
-- );

-- Student answer table
-- CREATE TABLE student_answer (
--     id BIGSERIAL PRIMARY KEY,
--     attempt_id BIGINT NOT NULL REFERENCES quiz_attempt(id) ON DELETE CASCADE,
--     question_id BIGINT NOT NULL,
--     answer TEXT,
--     is_correct BOOLEAN,
--     points_earnd INTEGER
-- );

-- Ebook table
-- CREATE TABLE ebook (
--     id BIGSERIAL PRIMARY KEY,
--     title VARCHAR(255) NOT NULL,
--     description TEXT,
--     file_url VARCHAR(500) NOT NULL,
--     file_size BIGINT,
--     mime_type VARCHAR(100),
--     level VARCHAR(50),
--     category VARCHAR(100),
--     free BOOLEAN NOT NULL DEFAULT TRUE,
--     download_cnt INTEGER DEFAULT 0,
--     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
-- );

-- Ebook access table
-- CREATE TABLE ebook_access (
--     id BIGSERIAL PRIMARY KEY,
--     ebook_id BIGINT NOT NULL,
--     student_id BIGINT NOT NULL,
--     accessed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     progress_pct INTEGER DEFAULT 0
-- );
