-- Add missing columns to tutor_availability if they don't exist
DO $$
BEGIN
    -- Add day_of_week column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='tutor_availability' AND column_name='day_of_week') THEN
        ALTER TABLE tutor_availability ADD COLUMN day_of_week INTEGER NOT NULL DEFAULT 1;
    END IF;

    -- Add start_time column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='tutor_availability' AND column_name='start_time') THEN
        ALTER TABLE tutor_availability ADD COLUMN start_time TIME NOT NULL DEFAULT '09:00:00';
    END IF;

    -- Add end_time column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='tutor_availability' AND column_name='end_time') THEN
        ALTER TABLE tutor_availability ADD COLUMN end_time TIME NOT NULL DEFAULT '17:00:00';
    END IF;

    -- Add is_available column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='tutor_availability' AND column_name='is_available') THEN
        ALTER TABLE tutor_availability ADD COLUMN is_available BOOLEAN NOT NULL DEFAULT TRUE;
    END IF;

    -- Add created_at column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='tutor_availability' AND column_name='created_at') THEN
        ALTER TABLE tutor_availability ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;

    -- Add updated_at column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='tutor_availability' AND column_name='updated_at') THEN
        ALTER TABLE tutor_availability ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;
END $$;

-- Create unique constraint if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'unique_tutor_time_slot') THEN
        ALTER TABLE tutor_availability ADD CONSTRAINT unique_tutor_time_slot UNIQUE (tutor_id, day_of_week, start_time);
    END IF;
END $$;

-- Create indexes if they don't exist
CREATE INDEX IF NOT EXISTS idx_tutor_availability_tutor ON tutor_availability(tutor_id);
CREATE INDEX IF NOT EXISTS idx_tutor_availability_day ON tutor_availability(day_of_week);

-- Add missing columns to online_lessons if they don't exist
DO $$
BEGIN
    -- Add duration_minutes column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='online_lessons' AND column_name='duration_minutes') THEN
        ALTER TABLE online_lessons ADD COLUMN duration_minutes INTEGER NOT NULL DEFAULT 60;
    END IF;

    -- Add timezone column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='online_lessons' AND column_name='timezone') THEN
        ALTER TABLE online_lessons ADD COLUMN timezone VARCHAR(50) NOT NULL DEFAULT 'UTC';
    END IF;

    -- Add start_date column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='online_lessons' AND column_name='start_date') THEN
        ALTER TABLE online_lessons ADD COLUMN start_date DATE NOT NULL DEFAULT CURRENT_DATE;
    END IF;

    -- Add end_date column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name='online_lessons' AND column_name='end_date') THEN
        ALTER TABLE online_lessons ADD COLUMN end_date DATE;
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS lesson_time_assignments (
    id BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT NOT NULL,
    tutor_id BIGINT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_lesson_time_slot UNIQUE (tutor_id, day_of_week, start_time)
);

-- Add foreign key only if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_lesson_time_lesson') THEN
        ALTER TABLE lesson_time_assignments
        ADD CONSTRAINT fk_lesson_time_lesson
        FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_lesson_time_assignments_lesson ON lesson_time_assignments(lesson_id);
CREATE INDEX IF NOT EXISTS idx_lesson_time_assignments_tutor ON lesson_time_assignments(tutor_id);
CREATE INDEX IF NOT EXISTS idx_lesson_time_assignments_day ON lesson_time_assignments(day_of_week);

CREATE TABLE IF NOT EXISTS lesson_sessions (
    id BIGSERIAL PRIMARY KEY,
    online_lesson_id BIGINT NOT NULL,
    session_date DATE NOT NULL,
    session_time TIME NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'scheduled',
    meeting_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_lesson_session_online_lesson') THEN
        ALTER TABLE lesson_sessions
        ADD CONSTRAINT fk_lesson_session_online_lesson
        FOREIGN KEY (online_lesson_id) REFERENCES online_lessons(id) ON DELETE CASCADE;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_lesson_sessions_online_lesson ON lesson_sessions(online_lesson_id);
CREATE INDEX IF NOT EXISTS idx_lesson_sessions_date ON lesson_sessions(session_date);
CREATE INDEX IF NOT EXISTS idx_lesson_sessions_status ON lesson_sessions(status);

CREATE TABLE IF NOT EXISTS session_attendance (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    joined_at TIMESTAMP,
    left_at TIMESTAMP,
    attendance_percentage DECIMAL(5,2),
    attendance_status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_session_student UNIQUE (session_id, student_id)
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_attendance_session') THEN
        ALTER TABLE session_attendance
        ADD CONSTRAINT fk_attendance_session
        FOREIGN KEY (session_id) REFERENCES lesson_sessions(id) ON DELETE CASCADE;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_session_attendance_session ON session_attendance(session_id);
CREATE INDEX IF NOT EXISTS idx_session_attendance_student ON session_attendance(student_id);

CREATE TABLE IF NOT EXISTS online_meeting_sessions (
    id BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT NOT NULL,
    room_id VARCHAR(255) NOT NULL,
    invite_link VARCHAR(500) NOT NULL,
    tutor_id BIGINT NOT NULL,
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_online_meeting_sessions_lesson ON online_meeting_sessions(lesson_id);
CREATE INDEX IF NOT EXISTS idx_online_meeting_sessions_room ON online_meeting_sessions(room_id);
CREATE INDEX IF NOT EXISTS idx_online_meeting_sessions_active ON online_meeting_sessions(is_active);
CREATE INDEX IF NOT EXISTS idx_online_meeting_sessions_tutor ON online_meeting_sessions(tutor_id);

CREATE TABLE IF NOT EXISTS lesson_schedule (
    id BIGSERIAL PRIMARY KEY,
    online_lesson_id BIGINT NOT NULL,
    day_of_week INTEGER NOT NULL,
    time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_lesson_schedule UNIQUE (online_lesson_id, day_of_week, time)
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_lesson_schedule_online_lesson') THEN
        ALTER TABLE lesson_schedule
        ADD CONSTRAINT fk_lesson_schedule_online_lesson
        FOREIGN KEY (online_lesson_id) REFERENCES online_lessons(id) ON DELETE CASCADE;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_lesson_schedule_online_lesson ON lesson_schedule(online_lesson_id);
CREATE INDEX IF NOT EXISTS idx_lesson_schedule_day ON lesson_schedule(day_of_week);
