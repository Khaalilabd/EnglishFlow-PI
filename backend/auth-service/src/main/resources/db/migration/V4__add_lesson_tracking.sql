-- Add lesson tracking columns to student_analytics
ALTER TABLE student_analytics
ADD COLUMN IF NOT EXISTS total_lessons_opened INTEGER NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS total_time_spent_minutes INTEGER NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS avg_time_per_lesson INTEGER NOT NULL DEFAULT 0,
ADD COLUMN IF NOT EXISTS last_lesson_opened_at TIMESTAMP NULL;

-- Add index for performance
CREATE INDEX IF NOT EXISTS idx_student_analytics_last_lesson ON student_analytics(last_lesson_opened_at);

-- Add comment
COMMENT ON COLUMN student_analytics.total_lessons_opened IS 'Total number of lessons opened by student';
COMMENT ON COLUMN student_analytics.total_time_spent_minutes IS 'Total time spent in minutes across all lessons';
COMMENT ON COLUMN student_analytics.avg_time_per_lesson IS 'Average time spent per lesson in minutes';
COMMENT ON COLUMN student_analytics.last_lesson_opened_at IS 'Timestamp of last lesson opened';
