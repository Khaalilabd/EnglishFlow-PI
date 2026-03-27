-- Create course_categories table
CREATE TABLE IF NOT EXISTS course_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    icon VARCHAR(50),
    color VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL
);

-- Insert default categories
INSERT INTO course_categories (name, description, icon, color, active, display_order, created_by) VALUES
('Grammar', 'Master English grammar rules and structures', '📚', '#3B82F6', TRUE, 1, 1),
('Vocabulary', 'Expand your English vocabulary', '📖', '#10B981', TRUE, 2, 1),
('Pronunciation', 'Improve your English pronunciation and accent', '🗣️', '#F59E0B', TRUE, 3, 1),
('Business English', 'Professional English for business contexts', '💼', '#EF4444', TRUE, 4, 1),
('Conversation', 'Practice everyday English conversations', '💬', '#8B5CF6', TRUE, 5, 1),
('Writing', 'Develop your English writing skills', '✍️', '#EC4899', TRUE, 6, 1),
('Reading', 'Enhance your English reading comprehension', '📕', '#14B8A6', TRUE, 7, 1),
('Listening', 'Improve your English listening skills', '👂', '#F97316', TRUE, 8, 1),
('Exam Preparation', 'Prepare for English proficiency exams', '🎯', '#6366F1', TRUE, 9, 1),
('Culture & Idioms', 'Learn English idioms and cultural expressions', '🌟', '#84CC16', TRUE, 10, 1);

-- Create index for faster queries
CREATE INDEX idx_course_categories_active ON course_categories(active);
CREATE INDEX idx_course_categories_display_order ON course_categories(display_order);
