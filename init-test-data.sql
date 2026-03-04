-- =====================================================
-- Auth Service Test Data
-- =====================================================
-- Password for all users: Test123!
-- Hashed with BCrypt (strength 10)
-- =====================================================

-- Clean existing data (optional - uncomment if needed)
-- DELETE FROM two_factor_backup_codes;
-- DELETE FROM two_factor_auth;
-- DELETE FROM users;

-- =====================================================
-- ADMIN USERS
-- =====================================================

INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at) VALUES
('admin@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Sarah', 'Johnson', '+216 98 765 432', '12345678', NULL, '1985-03-15', '123 Admin Street', 'Tunis', '1000', 'System Administrator with 10 years of experience in educational management.', NULL, 10, 'ADMIN', true, true, true, NOW() - INTERVAL '2 years', NOW()),
('admin.tech@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Michael', 'Chen', '+216 97 654 321', '23456789', NULL, '1988-07-22', '456 Tech Avenue', 'Ariana', '2080', 'Technical Administrator specializing in platform management.', NULL, 8, 'ADMIN', true, true, true, NOW() - INTERVAL '18 months', NOW());

-- =====================================================
-- ACADEMIC OFFICE AFFAIRS
-- =====================================================

INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at) VALUES
('academic@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Emma', 'Williams', '+216 96 543 210', '34567890', NULL, '1990-05-10', '789 Academic Road', 'Sousse', '4000', 'Academic Affairs Coordinator managing student records and schedules.', NULL, 5, 'ACADEMIC_OFFICE_AFFAIR', true, true, true, NOW() - INTERVAL '1 year', NOW()),
('registrar@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'David', 'Martinez', '+216 95 432 109', '45678901', NULL, '1987-11-28', '321 Registry Lane', 'Sfax', '3000', 'Registrar handling student enrollment and academic records.', NULL, 7, 'ACADEMIC_OFFICE_AFFAIR', true, true, true, NOW() - INTERVAL '14 months', NOW());

-- =====================================================
-- TUTORS
-- =====================================================

INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at) VALUES
('tutor.emily@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Emily', 'Brown', '+216 94 321 098', '56789012', NULL, '1992-02-14', '555 Teaching Street', 'Tunis', '1001', 'Experienced English tutor specializing in Business English and TOEFL preparation. Native speaker from the UK.', 'C2', 6, 'TUTOR', true, true, true, NOW() - INTERVAL '10 months', NOW()),
('tutor.james@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'James', 'Anderson', '+216 93 210 987', '67890123', NULL, '1989-09-05', '777 Education Avenue', 'La Marsa', '2070', 'IELTS and Cambridge exam specialist with a passion for conversational English.', 'C2', 8, 'TUTOR', true, true, true, NOW() - INTERVAL '15 months', NOW()),
('tutor.sophia@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Sophia', 'Taylor', '+216 92 109 876', '78901234', NULL, '1994-06-18', '999 Language Road', 'Monastir', '5000', 'Young and dynamic tutor focusing on interactive learning and modern teaching methods.', 'C2', 3, 'TUTOR', true, true, true, NOW() - INTERVAL '8 months', NOW()),
('tutor.robert@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Robert', 'Wilson', '+216 91 098 765', '89012345', NULL, '1986-12-30', '111 Grammar Street', 'Nabeul', '8000', 'Grammar and writing specialist with extensive experience in academic English.', 'C2', 10, 'TUTOR', true, true, true, NOW() - INTERVAL '2 years', NOW()),
('tutor.lisa@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Lisa', 'Garcia', '+216 90 987 654', '90123456', NULL, '1991-04-25', '222 Pronunciation Plaza', 'Bizerte', '7000', 'Pronunciation and accent reduction expert. Helps students sound more natural.', 'C2', 5, 'TUTOR', true, true, true, NOW() - INTERVAL '11 months', NOW());

-- =====================================================
-- STUDENTS - Various Levels
-- =====================================================

-- A1 Level Students (Beginners)
INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at) VALUES
('student.ahmed@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ahmed', 'Ben Ali', '+216 20 111 222', '01234567', NULL, '2000-01-15', '10 Student Street', 'Tunis', '1002', 'Complete beginner eager to learn English for career opportunities.', 'A1', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '3 months', NOW()),
('student.fatma@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Fatma', 'Trabelsi', '+216 20 222 333', '11234567', NULL, '2001-03-20', '20 Learning Lane', 'Ariana', '2081', 'Starting my English journey to study abroad.', 'A1', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '2 months', NOW());

-- A2 Level Students (Elementary)
INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at) VALUES
('student.mohamed@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Mohamed', 'Gharbi', '+216 20 333 444', '21234567', NULL, '1999-05-10', '30 Progress Road', 'Sousse', '4001', 'Improving my English to work in tourism industry.', 'A2', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '5 months', NOW()),
('student.salma@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Salma', 'Mansour', '+216 20 444 555', '31234567', NULL, '2002-07-08', '40 Education Street', 'Sfax', '3001', 'Love watching English movies and want to understand them better.', 'A2', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '4 months', NOW()),
('student.youssef@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Youssef', 'Hamdi', '+216 20 555 666', '41234567', NULL, '2000-09-12', '50 Study Avenue', 'Monastir', '5001', 'Engineering student needing English for technical documentation.', 'A2', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '6 months', NOW());

-- B1 Level Students (Intermediate)
INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at) VALUES
('student.ines@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ines', 'Kacem', '+216 20 666 777', '51234567', NULL, '1998-11-25', '60 Intermediate Lane', 'Tunis', '1003', 'Business student preparing for international internships.', 'B1', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '8 months', NOW()),
('student.karim@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Karim', 'Bouaziz', '+216 20 777 888', '61234567', NULL, '2001-02-14', '70 Communication Road', 'La Marsa', '2071', 'Passionate about English literature and creative writing.', 'B1', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '7 months', NOW()),
('student.mariem@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Mariem', 'Jebali', '+216 20 888 999', '71234567', NULL, '1999-04-30', '80 Language Plaza', 'Nabeul', '8001', 'Medical student needing English for research papers.', 'B1', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '9 months', NOW());

-- B2 Level Students (Upper Intermediate)
INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at) VALUES
('student.amir@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Amir', 'Sassi', '+216 21 111 222', '81234567', NULL, '1997-06-18', '90 Advanced Street', 'Bizerte', '7001', 'Computer science graduate preparing for job interviews abroad.', 'B2', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '1 year', NOW()),
('student.nour@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nour', 'Mejri', '+216 21 222 333', '91234567', NULL, '2000-08-22', '100 Fluency Avenue', 'Sousse', '4002', 'Aiming for C1 level to study master degree in Canada.', 'B2', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '10 months', NOW()),
('student.rami@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Rami', 'Chouchane', '+216 21 333 444', '02345678', NULL, '1998-10-05', '110 Excellence Road', 'Sfax', '3002', 'Finance professional working with international clients.', 'B2', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '11 months', NOW());

-- C1 Level Students (Advanced)
INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at) VALUES
('student.sarra@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Sarra', 'Dridi', '+216 21 444 555', '12345670', NULL, '1996-12-10', '120 Mastery Lane', 'Tunis', '1004', 'PhD candidate in linguistics, perfecting academic English.', 'C1', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '1.5 years', NOW()),
('student.mehdi@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Mehdi', 'Ayari', '+216 21 555 666', '22345678', NULL, '1997-03-28', '130 Professional Plaza', 'Ariana', '2082', 'Software engineer working remotely for US company.', 'C1', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '1.2 years', NOW());

-- C2 Level Students (Proficient)
INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at) VALUES
('student.leila@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Leila', 'Khelifi', '+216 21 666 777', '32345678', NULL, '1995-05-15', '140 Expert Street', 'La Marsa', '2072', 'Translator and interpreter, maintaining near-native proficiency.', 'C2', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '2 years', NOW()),
('student.omar@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Omar', 'Belhadj', '+216 21 777 888', '42345678', NULL, '1996-07-20', '150 Bilingual Avenue', 'Monastir', '5002', 'International business consultant, native-like English speaker.', 'C2', NULL, 'STUDENT', true, true, true, NOW() - INTERVAL '2.5 years', NOW());

-- =====================================================
-- INACTIVE/PENDING STUDENTS (for testing different states)
-- =====================================================

INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at) VALUES
('student.pending@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Pending', 'User', '+216 22 111 222', '52345678', NULL, '2001-01-01', '160 Waiting Street', 'Tunis', '1005', 'Account pending activation.', 'A1', NULL, 'STUDENT', false, false, false, NOW() - INTERVAL '1 week', NOW()),
('student.nofee@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'NoFee', 'Student', '+216 22 222 333', '62345678', NULL, '2000-06-15', '170 Payment Lane', 'Ariana', '2083', 'Registration fee not paid yet.', 'B1', NULL, 'STUDENT', true, false, true, NOW() - INTERVAL '2 weeks', NOW()),
('student.incomplete@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Incomplete', 'Profile', '+216 22 333 444', '72345678', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'STUDENT', true, true, false, NOW() - INTERVAL '3 days', NOW());

-- =====================================================
-- Summary
-- =====================================================
-- Total Users: 30
-- - Admins: 2
-- - Academic Affairs: 2
-- - Tutors: 5
-- - Students: 21 (various levels A1-C2)
--   * A1: 2 students
--   * A2: 3 students
--   * B1: 3 students
--   * B2: 3 students
--   * C1: 2 students
--   * C2: 2 students
--   * Pending/Incomplete: 3 students
-- =====================================================
