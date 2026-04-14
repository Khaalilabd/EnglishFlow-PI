-- Create lesson_time_assignments table if it doesn't exist
CREATE TABLE IF NOT EXISTS lesson_time_assignments (
    id BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT NOT NULL,
    tutor_id BIGINT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE,
    CONSTRAINT unique_tutor_slot UNIQUE (tutor_id, day_of_week, start_time)
);

-- Create index for faster queries
CREATE INDEX IF NOT EXISTS idx_lesson_time_assignment_lesson ON lesson_time_assignments(lesson_id);
CREATE INDEX IF NOT EXISTS idx_lesson_time_assignment_tutor ON lesson_time_assignments(tutor_id);
