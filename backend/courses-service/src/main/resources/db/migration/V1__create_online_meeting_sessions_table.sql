CREATE TABLE IF NOT EXISTS online_meeting_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lesson_id BIGINT NOT NULL,
    room_id VARCHAR(255) NOT NULL,
    invite_link VARCHAR(500) NOT NULL,
    tutor_id BIGINT NOT NULL,
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_lesson_id (lesson_id),
    INDEX idx_room_id (room_id),
    INDEX idx_is_active (is_active)
);
