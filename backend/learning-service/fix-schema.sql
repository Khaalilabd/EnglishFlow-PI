-- Fix schema for enum types
-- Run this in pgAdmin or psql to fix the database schema

-- Drop existing tables to recreate with correct types
DROP TABLE IF EXISTS student_answer CASCADE;
DROP TABLE IF EXISTS quiz_attempt CASCADE;
DROP TABLE IF EXISTS ebook_access CASCADE;
DROP TABLE IF EXISTS question CASCADE;
DROP TABLE IF EXISTS quiz CASCADE;
DROP TABLE IF EXISTS ebook CASCADE;

-- Create quiz table
CREATE TABLE quiz (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    course_id BIGINT,
    duration_minutes INTEGER,
    max_score INTEGER,
    passing_score INTEGER,
    published BOOLEAN DEFAULT FALSE,
    due_date TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create question table with enum type
CREATE TABLE question (
    id BIGSERIAL PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    content VARCHAR(2000) NOT NULL,
    type VARCHAR(50) NOT NULL,
    options TEXT,
    correct_answer VARCHAR(255),
    points INTEGER NOT NULL DEFAULT 1,
    order_index INTEGER,
    FOREIGN KEY (quiz_id) REFERENCES quiz(id) ON DELETE CASCADE
);

-- Create quiz_attempt table
CREATE TABLE quiz_attempt (
    id BIGSERIAL PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    score INTEGER,
    started_at TIMESTAMP,
    submitted_at TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (quiz_id) REFERENCES quiz(id) ON DELETE CASCADE
);

-- Create student_answer table
CREATE TABLE student_answer (
    id BIGSERIAL PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer TEXT,
    is_correct BOOLEAN,
    points_earned INTEGER,
    FOREIGN KEY (attempt_id) REFERENCES quiz_attempt(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE
);

-- Create ebook table with enum types
CREATE TABLE ebook (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    file_url VARCHAR(255) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100) DEFAULT 'application/pdf',
    level VARCHAR(50),
    category VARCHAR(50),
    is_free BOOLEAN DEFAULT TRUE,
    download_count INTEGER DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create ebook_access table
CREATE TABLE ebook_access (
    id BIGSERIAL PRIMARY KEY,
    ebook_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    accessed_at TIMESTAMP,
    last_read_at TIMESTAMP,
    progress_percent INTEGER DEFAULT 0,
    FOREIGN KEY (ebook_id) REFERENCES ebook(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_question_quiz_id ON question(quiz_id);
CREATE INDEX idx_quiz_attempt_quiz_id ON quiz_attempt(quiz_id);
CREATE INDEX idx_quiz_attempt_student_id ON quiz_attempt(student_id);
CREATE INDEX idx_student_answer_attempt_id ON student_answer(attempt_id);
CREATE INDEX idx_ebook_access_ebook_id ON ebook_access(ebook_id);
CREATE INDEX idx_ebook_access_student_id ON ebook_access(student_id);
