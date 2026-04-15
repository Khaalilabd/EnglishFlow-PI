-- Create tutor_availability table
CREATE TABLE IF NOT EXISTS tutor_availability (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT NOT NULL,
    day_of_week INTEGER NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_tutor_time_slot UNIQUE (tutor_id, day_of_week, start_time)
);

CREATE INDEX idx_tutor_availability_tutor ON tutor_availability(tutor_id);
CREATE INDEX idx_tutor_availability_day ON tutor_availability(day_of_week);

-- Create online_lessons table
CREATE TABLE IF NOT EXISTS online_lessons (
    id BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT NOT NULL UNIQUE,
    tutor_id BIGINT NOT NULL,
    max_students INTEGER DEFAULT 30,
    duration_minutes INTEGER NOT NULL DEFAULT 60,
    meeting_link VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_online_lessons_lesson ON online_lessons(lesson_id);
CREATE INDEX idx_online_lessons_tutor ON online_lessons(tutor_id);

-- Create lesson_time_assignments table
CREATE TABLE IF NOT EXISTS lesson_time_assignments (
    id BIGSERIAL PRIMARY KEY,
    online_lesson_id BIGINT NOT NULL,
    tutor_id BIGINT NOT NULL,
    day_of_week INTEGER NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lesson_time_online_lesson FOREIGN KEY (online_lesson_id) REFERENCES online_lessons(id) ON DELETE CASCADE,
    CONSTRAINT unique_lesson_time_slot UNIQUE (online_lesson_id, day_of_week, start_time)
);

CREATE INDEX idx_lesson_time_assignments_online_lesson ON lesson_time_assignments(online_lesson_id);
CREATE INDEX idx_lesson_time_assignments_tutor ON lesson_time_assignments(tutor_id);
CREATE INDEX idx_lesson_time_assignments_day ON lesson_time_assignments(day_of_week);

-- Create lesson_sessions table
CREATE TABLE IF NOT EXISTS lesson_sessions (
    id BIGSERIAL PRIMARY KEY,
    online_lesson_id BIGINT NOT NULL,
    scheduled_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    meeting_link VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lesson_session_online_lesson FOREIGN KEY (online_lesson_id) REFERENCES online_lessons(id) ON DELETE CASCADE
);

CREATE INDEX idx_lesson_sessions_online_lesson ON lesson_sessions(online_lesson_id);
CREATE INDEX idx_lesson_sessions_date ON lesson_sessions(scheduled_date);
CREATE INDEX idx_lesson_sessions_status ON lesson_sessions(status);

-- Create session_attendance table
CREATE TABLE IF NOT EXISTS session_attendance (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    attended BOOLEAN DEFAULT FALSE,
    joined_at TIMESTAMP,
    left_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_session FOREIGN KEY (session_id) REFERENCES lesson_sessions(id) ON DELETE CASCADE,
    CONSTRAINT unique_session_student UNIQUE (session_id, student_id)
);

CREATE INDEX idx_session_attendance_session ON session_attendance(session_id);
CREATE INDEX idx_session_attendance_student ON session_attendance(student_id);

-- Create online_meeting_sessions table
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

CREATE INDEX idx_online_meeting_sessions_lesson ON online_meeting_sessions(lesson_id);
CREATE INDEX idx_online_meeting_sessions_room ON online_meeting_sessions(room_id);
CREATE INDEX idx_online_meeting_sessions_active ON online_meeting_sessions(is_active);
CREATE INDEX idx_online_meeting_sessions_tutor ON online_meeting_sessions(tutor_id);

-- Create lesson_schedule table (if needed for recurring schedules)
CREATE TABLE IF NOT EXISTS lesson_schedule (
    id BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT NOT NULL,
    day_of_week INTEGER NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_lesson_schedule UNIQUE (lesson_id, day_of_week, start_time)
);

CREATE INDEX idx_lesson_schedule_lesson ON lesson_schedule(lesson_id);
CREATE INDEX idx_lesson_schedule_day ON lesson_schedule(day_of_week);
