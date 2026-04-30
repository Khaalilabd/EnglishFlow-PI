-- =====================================================
-- Script d'insertion des utilisateurs de test
-- Base de données: englishflow_identity
-- =====================================================
-- ⚠️ IMPORTANT: Ce fichier contient des données de TEST uniquement
-- Les passwords bcrypt sont pour le développement local
-- Password par défaut pour tous les comptes: "password123"
-- NE PAS utiliser en production !
-- =====================================================
-- NOSONAR - This file contains test data only, not real credentials
-- sonar.issue.ignore.allfile=true
-- =====================================================
-- Note: Connectez-vous à la base englishflow_identity avant d'exécuter ce script
-- Exemple: psql -U postgres -d englishflow_identity -f insert-users.sql

-- Désactiver les contraintes de clés étrangères temporairement (PostgreSQL)
SET session_replication_role = 'replica';

-- =====================================================
-- 1. ADMIN
-- =====================================================
INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, application_id, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at)
VALUES 
('admin@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Youssef', 'Bennani', '+212661234567', 'AB234567', NULL, '1985-03-15', '45 Boulevard Zerktouni', 'Casablanca', '20000', 'Directeur général d''EnglishFlow avec 15 ans d''expérience dans l''éducation', NULL, NULL, NULL, 'ADMIN', true, false, true, NOW(), NOW()),
('admin.tech@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Sarah', 'El Amrani', '+212662345678', 'AB234568', NULL, '1988-07-22', '12 Rue Abdelmoumen', 'Casablanca', '20100', 'Responsable technique et innovation pédagogique', NULL, NULL, NULL, 'ADMIN', true, false, true, NOW(), NOW());

-- =====================================================
-- 2. ACADEMIC OFFICE AFFAIR
-- =====================================================
INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, application_id, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at)
VALUES 
('academic@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Fatima', 'Alaoui', '+212663456789', 'AB234569', NULL, '1990-05-10', '78 Avenue Hassan II', 'Rabat', '10000', 'Coordinatrice académique, gestion des programmes et suivi pédagogique', NULL, NULL, NULL, 'ACADEMIC_OFFICE_AFFAIR', true, false, true, NOW(), NOW()),
('academic.support@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Mehdi', 'Tazi', '+212664567890', 'AB234570', NULL, '1992-11-18', '23 Rue Mohammed V', 'Rabat', '10010', 'Assistant académique, organisation des examens et certifications', NULL, NULL, NULL, 'ACADEMIC_OFFICE_AFFAIR', true, false, true, NOW(), NOW());

-- =====================================================
-- 3. TUTORS
-- =====================================================
INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, application_id, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at)
VALUES 
('james.wilson@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'James', 'Wilson', '+212665678901', 'AB234571', NULL, '1985-02-14', '156 Boulevard Anfa', 'Casablanca', '20050', 'Professeur natif britannique, spécialiste IELTS et Cambridge. Passionné par l''enseignement communicatif', 'C2', 12, NULL, 'TUTOR', true, false, true, NOW(), NOW()),
('emily.brown@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Emily', 'Brown', '+212666789012', 'AB234572', NULL, '1990-08-25', '89 Rue de la Liberté', 'Casablanca', '20070', 'Enseignante américaine certifiée TEFL, experte en Business English et préparation TOEFL', 'C2', 8, NULL, 'TUTOR', true, false, true, NOW(), NOW()),
('karim.idrissi@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Karim', 'Idrissi', '+212667890123', 'AB234573', NULL, '1987-12-05', '34 Avenue des FAR', 'Rabat', '10020', 'Professeur bilingue marocain, master en linguistique appliquée. Spécialiste grammaire et phonétique', 'C2', 10, NULL, 'TUTOR', true, false, true, NOW(), NOW()),
('sophia.martin@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Sophia', 'Martin', '+212668901234', 'AB234574', NULL, '1993-04-30', '67 Rue Allal Ben Abdellah', 'Fès', '30000', 'Enseignante canadienne, spécialisée en anglais pour enfants et adolescents. Méthodes ludiques', 'C2', 6, NULL, 'TUTOR', true, false, true, NOW(), NOW()),
('omar.fassi@englishflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Omar', 'Fassi', '+212669012345', 'AB234575', NULL, '1989-09-12', '145 Boulevard Mohammed VI', 'Marrakech', '40000', 'Professeur expérimenté en anglais académique et préparation aux études supérieures', 'C2', 9, NULL, 'TUTOR', true, false, true, NOW(), NOW());

-- =====================================================
-- 4. STUDENTS - Niveau Débutant (A1-A2)
-- =====================================================
INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, application_id, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at)
VALUES 
('amine.benali@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Amine', 'Benali', '+212670123456', 'AB234576', NULL, '2002-01-20', '12 Rue Ibn Khaldoun', 'Casablanca', '20200', 'Étudiant en commerce international, souhaite améliorer mon anglais pour ma carrière', 'A2', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('salma.chakir@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Salma', 'Chakir', '+212671234567', 'AB234577', NULL, '2003-05-15', '28 Avenue Mers Sultan', 'Casablanca', '20250', 'Passionnée par les langues, je débute en anglais et j''adore la culture anglophone', 'A1', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('youssef.moussaoui@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Youssef', 'Moussaoui', '+212672345678', 'AB234578', NULL, '2001-11-08', '56 Rue Oued Sebou', 'Rabat', '10030', 'Étudiant en informatique, besoin d''anglais pour lire la documentation technique', 'A2', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('nadia.el-fassi@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Nadia', 'El Fassi', '+212673456789', 'AB234579', NULL, '2004-03-22', '91 Quartier Agdal', 'Rabat', '10040', 'Lycéenne motivée, je prépare mon bac et veux renforcer mes bases en anglais', 'A1', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW());

-- =====================================================
-- 5. STUDENTS - Niveau Intermédiaire (B1-B2)
-- =====================================================
INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, application_id, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at)
VALUES 
('imane.lahlou@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Imane', 'Lahlou', '+212674567890', 'AB234580', NULL, '2000-07-14', '43 Rue Patrice Lumumba', 'Casablanca', '20300', 'Étudiante en marketing digital, je veux perfectionner mon anglais professionnel', 'B2', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('rachid.benjelloun@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Rachid', 'Benjelloun', '+212675678901', 'AB234581', NULL, '1999-12-30', '78 Boulevard Bir Anzarane', 'Casablanca', '20350', 'Ingénieur en télécommunications, l''anglais est essentiel dans mon domaine', 'B1', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('leila.mansouri@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Leila', 'Mansouri', '+212676789012', 'AB234582', NULL, '2001-04-18', '125 Avenue Moulay Youssef', 'Marrakech', '40100', 'Étudiante en tourisme et hôtellerie, l''anglais est indispensable pour mon métier', 'B2', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('hamza.ziani@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Hamza', 'Ziani', '+212677890123', 'AB234583', NULL, '2002-09-05', '34 Rue de Fès', 'Meknès', '50000', 'Étudiant en finance, je prépare des certifications internationales en anglais', 'B1', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('sara.kadiri@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Sara', 'Kadiri', '+212678901234', 'AB234584', NULL, '2000-06-28', '67 Avenue Hassan II', 'Agadir', '80000', 'Étudiante en relations internationales, passionnée par les débats et discussions en anglais', 'B2', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW());

-- =====================================================
-- 6. STUDENTS - Niveau Avancé (C1-C2)
-- =====================================================
INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, application_id, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at)
VALUES 
('zineb.alaoui@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Zineb', 'Alaoui', '+212679012345', 'AB234585', NULL, '1998-02-11', '89 Rue Abou Bakr Seddik', 'Casablanca', '20400', 'Traductrice freelance, je perfectionne mon anglais pour des traductions littéraires', 'C1', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('adam.berrada@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Adam', 'Berrada', '+212680123456', 'AB234586', NULL, '1999-10-25', '156 Boulevard Zerktouni', 'Casablanca', '20450', 'Doctorant en sciences politiques, je rédige ma thèse en anglais', 'C2', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('meryem.senhaji@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Meryem', 'Senhaji', '+212681234567', 'AB234587', NULL, '2000-08-16', '23 Rue Taha Hussein', 'Rabat', '10050', 'Étudiante en littérature anglaise, passionnée par Shakespeare et la poésie', 'C1', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('tarik.filali@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Tarik', 'Filali', '+212682345678', 'AB234588', NULL, '1997-12-03', '45 Avenue Mohammed V', 'Tanger', '90000', 'Consultant en stratégie d''entreprise, l''anglais est ma langue de travail quotidienne', 'C2', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW());

-- =====================================================
-- 7. STUDENTS SUPPLÉMENTAIRES - Mix de niveaux
-- =====================================================
INSERT INTO users (email, password, first_name, last_name, phone, cin, profile_photo, date_of_birth, address, city, postal_code, bio, english_level, years_of_experience, application_id, role, is_active, registration_fee_paid, profile_completed, created_at, updated_at)
VALUES 
('hicham.amrani@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Hicham', 'Amrani', '+212683456789', 'AB234589', NULL, '2001-05-09', '78 Rue Al Massira', 'Casablanca', '20500', 'Étudiant en architecture, besoin d''anglais pour les logiciels et la documentation', 'B1', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('samira.el-idrissi@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Samira', 'El Idrissi', '+212684567890', 'AB234590', NULL, '2003-11-21', '12 Quartier Palmier', 'Casablanca', '20550', 'Lycéenne passionnée de cinéma américain, je veux comprendre les films en VO', 'A2', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('bilal.tazi@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Bilal', 'Tazi', '+212685678901', 'AB234591', NULL, '2000-03-17', '56 Avenue Lalla Yacout', 'Rabat', '10060', 'Étudiant en médecine, l''anglais médical est crucial pour ma formation', 'B2', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('khadija.bennani@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Khadija', 'Bennani', '+212686789012', 'AB234592', NULL, '1999-07-29', '89 Rue Ibn Batouta', 'Fès', '30100', 'Professeure de français, j''apprends l''anglais pour enseigner les deux langues', 'B1', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('ayoub.mouhib@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Ayoub', 'Mouhib', '+212687890123', 'AB234593', NULL, '2002-01-14', '34 Boulevard Moulay Ismail', 'Meknès', '50100', 'Développeur web, je veux améliorer mon anglais technique pour collaborer à l''international', 'B2', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW()),
('dounia.lahlou@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'Dounia', 'Lahlou', '+212688901234', 'AB234594', NULL, '2001-09-07', '67 Avenue Prince Héritier', 'Marrakech', '40200', 'Étudiante en design graphique, l''anglais m''ouvre des opportunités créatives', 'C1', NULL, NULL, 'STUDENT', true, false, true, NOW(), NOW());

-- Réactiver les contraintes de clés étrangères (PostgreSQL)
SET session_replication_role = 'origin';

-- =====================================================
-- Résumé des insertions
-- =====================================================
-- Total: 25 utilisateurs
-- - 2 Admins
-- - 2 Academic Office Affairs
-- - 5 Tutors (natifs et bilingues)
-- - 16 Students répartis par niveau:
--   * 4 Débutants (A1-A2)
--   * 5 Intermédiaires (B1-B2)
--   * 4 Avancés (C1-C2)
--   * 3 Mix supplémentaires
-- 
-- Mot de passe pour tous: "password123"
-- (Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO)
-- =====================================================

SELECT 'Utilisateurs insérés avec succès!' AS message;
SELECT role, COUNT(*) as count FROM users GROUP BY role ORDER BY role;
