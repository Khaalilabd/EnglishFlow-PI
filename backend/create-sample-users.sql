-- Sample Users for EnglishFlow Application
-- Run this script against the englishflow_identity database
-- Password: root

-- Insert Tutors (IDs 1, 2, 3)
INSERT INTO users (id, email, password, first_name, last_name, role, is_active, registration_fee_paid, created_at, updated_at) VALUES
(1, 'sarah.johnson@englishflow.com', '$2a$10$N.zmdr9k7uH/LQMa/T7/Oe6dVoFI3RgZWvbLrUhQkQiWEA5wR6/Dy', 'Sarah', 'Johnson', 'TUTOR', true, true, NOW(), NOW()),
(2, 'michael.chen@englishflow.com', '$2a$10$N.zmdr9k7uH/LQMa/T7/Oe6dVoFI3RgZWvbLrUhQkQiWEA5wR6/Dy', 'Michael', 'Chen', 'TUTOR', true, true, NOW(), NOW()),
(3, 'emma.rodriguez@englishflow.com', '$2a$10$N.zmdr9k7uH/LQMa/T7/Oe6dVoFI3RgZWvbLrUhQkQiWEA5wR6/Dy', 'Emma', 'Rodriguez', 'TUTOR', true, true, NOW(), NOW());

-- Insert Students (IDs 4, 5, 6, 7, 8)
INSERT INTO users (id, email, password, first_name, last_name, role, is_active, registration_fee_paid, created_at, updated_at) VALUES
(4, 'john.doe@student.com', '$2a$10$N.zmdr9k7uH/LQMa/T7/Oe6dVoFI3RgZWvbLrUhQkQiWEA5wR6/Dy', 'John', 'Doe', 'STUDENT', true, true, NOW(), NOW()),
(5, 'jane.smith@student.com', '$2a$10$N.zmdr9k7uH/LQMa/T7/Oe6dVoFI3RgZWvbLrUhQkQiWEA5wR6/Dy', 'Jane', 'Smith', 'STUDENT', true, true, NOW(), NOW()),
(6, 'carlos.garcia@student.com', '$2a$10$N.zmdr9k7uH/LQMa/T7/Oe6dVoFI3RgZWvbLrUhQkQiWEA5wR6/Dy', 'Carlos', 'Garcia', 'STUDENT', true, true, NOW(), NOW()),
(7, 'maria.lopez@student.com', '$2a$10$N.zmdr9k7uH/LQMa/T7/Oe6dVoFI3RgZWvbLrUhQkQiWEA5wR6/Dy', 'Maria', 'Lopez', 'STUDENT', true, true, NOW(), NOW()),
(8, 'ahmed.hassan@student.com', '$2a$10$N.zmdr9k7uH/LQMa/T7/Oe6dVoFI3RgZWvbLrUhQkQiWEA5wR6/Dy', 'Ahmed', 'Hassan', 'STUDENT', true, true, NOW(), NOW());

-- Reset the sequence to continue from ID 9
SELECT setval('users_id_seq', 8, true);

-- Display created users
SELECT id, email, first_name, last_name, role, is_active FROM users WHERE id <= 8 ORDER BY id;

-- Password for all users is: "password123" (hashed with BCrypt)

/*
TUTORS:
1. Sarah Johnson (sarah.johnson@englishflow.com) - Teaching: English for Beginners, Advanced English Communication
2. Michael Chen (michael.chen@englishflow.com) - Teaching: Intermediate English Mastery, English Conversation Mastery  
3. Emma Rodriguez (emma.rodriguez@englishflow.com) - Teaching: IELTS Exam Preparation

STUDENTS:
4. John Doe (john.doe@student.com)
5. Jane Smith (jane.smith@student.com)
6. Carlos Garcia (carlos.garcia@student.com)
7. Maria Lopez (maria.lopez@student.com)
8. Ahmed Hassan (ahmed.hassan@student.com)

All users have the password: password123
*/