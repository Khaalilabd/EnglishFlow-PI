-- Add scheduled publishing support
ALTER TABLE quiz ADD COLUMN IF NOT EXISTS publish_at TIMESTAMP;

-- Add question shuffling support
ALTER TABLE quiz ADD COLUMN IF NOT EXISTS shuffle_questions BOOLEAN DEFAULT false;
ALTER TABLE quiz ADD COLUMN IF NOT EXISTS shuffle_options BOOLEAN DEFAULT false;

-- Add show answers timing control
ALTER TABLE quiz ADD COLUMN IF NOT EXISTS show_answers_timing VARCHAR(20) DEFAULT 'end';

-- Add partial credit support
ALTER TABLE question ADD COLUMN IF NOT EXISTS partial_credit_enabled BOOLEAN DEFAULT false;
ALTER TABLE student_answer ADD COLUMN IF NOT EXISTS partial_points DECIMAL(5,2);

-- Add quiz categories and difficulty
ALTER TABLE quiz ADD COLUMN IF NOT EXISTS category VARCHAR(50);
ALTER TABLE quiz ADD COLUMN IF NOT EXISTS difficulty VARCHAR(20);
ALTER TABLE quiz ADD COLUMN IF NOT EXISTS tags TEXT;

-- Create quiz_tag table for better tag management
CREATE TABLE IF NOT EXISTS quiz_tag (
    id BIGSERIAL PRIMARY KEY,
    quiz_id BIGINT REFERENCES quiz(id) ON DELETE CASCADE,
    tag VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_quiz_tag_quiz_id ON quiz_tag(quiz_id);
CREATE INDEX IF NOT EXISTS idx_quiz_tag_tag ON quiz_tag(tag);
