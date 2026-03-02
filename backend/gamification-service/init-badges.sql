-- Create database if not exists
CREATE DATABASE IF NOT EXISTS englishflow_gamification;

-- Connect to the database
\c englishflow_gamification;

-- Insert default badges
INSERT INTO badges (id, name, description, icon, category, points_required, created_at, updated_at) VALUES
-- Achievement Badges
(1, 'First Steps', 'Complete your first lesson', '🎯', 'ACHIEVEMENT', 0, NOW(), NOW()),
(2, 'Quick Learner', 'Complete 5 lessons in one day', '⚡', 'ACHIEVEMENT', 50, NOW(), NOW()),
(3, 'Dedicated Student', 'Complete 10 lessons', '📚', 'ACHIEVEMENT', 100, NOW(), NOW()),
(4, 'Course Master', 'Complete your first course', '🎓', 'ACHIEVEMENT', 200, NOW(), NOW()),
(5, 'Overachiever', 'Complete 5 courses', '🏆', 'ACHIEVEMENT', 500, NOW(), NOW()),

-- Streak Badges
(6, 'Consistent', 'Maintain a 3-day streak', '🔥', 'STREAK', 30, NOW(), NOW()),
(7, 'Committed', 'Maintain a 7-day streak', '💪', 'STREAK', 70, NOW(), NOW()),
(8, 'Unstoppable', 'Maintain a 30-day streak', '⭐', 'STREAK', 300, NOW(), NOW()),
(9, 'Legend', 'Maintain a 100-day streak', '👑', 'STREAK', 1000, NOW(), NOW()),

-- Quiz Badges
(10, 'Quiz Novice', 'Complete your first quiz', '📝', 'QUIZ', 10, NOW(), NOW()),
(11, 'Perfect Score', 'Get 100% on a quiz', '💯', 'QUIZ', 50, NOW(), NOW()),
(12, 'Quiz Master', 'Complete 10 quizzes with 80%+', '🎯', 'QUIZ', 200, NOW(), NOW()),

-- Social Badges
(13, 'Team Player', 'Join your first club', '👥', 'SOCIAL', 20, NOW(), NOW()),
(14, 'Social Butterfly', 'Join 3 clubs', '🦋', 'SOCIAL', 60, NOW(), NOW()),
(15, 'Event Enthusiast', 'Attend your first event', '🎉', 'SOCIAL', 30, NOW(), NOW()),

-- Special Badges
(16, 'Early Bird', 'Login before 8 AM', '🌅', 'SPECIAL', 15, NOW(), NOW()),
(17, 'Night Owl', 'Login after 10 PM', '🦉', 'SPECIAL', 15, NOW(), NOW()),
(18, 'Weekend Warrior', 'Study on weekend', '⚔️', 'SPECIAL', 25, NOW(), NOW()),
(19, 'Feedback Champion', 'Submit 5 feedback forms', '💬', 'SPECIAL', 50, NOW(), NOW()),
(20, 'Helper', 'Help 3 other students', '🤝', 'SPECIAL', 75, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Reset sequence
SELECT setval('badges_id_seq', (SELECT MAX(id) FROM badges));
