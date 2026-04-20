-- =====================================================
-- Script: Insert Multiple Complaints for User ID 1
-- Database: englishflow_complaints
-- Description: Creates realistic complaints for testing
-- =====================================================

-- Complaint 1: Problème pédagogique - Difficulté avec le cours de grammaire
INSERT INTO complaints (
    user_id, target_role, category, subject, description, 
    status, priority, risk_score, requires_intervention, 
    course_type, difficulty, issue_type, created_at, updated_at
) VALUES (
    1,
    'TUTOR',
    'PEDAGOGICAL',
    'Difficulté à comprendre les temps du passé',
    'Bonjour, je suis actuellement le cours "English Grammar Fundamentals" et j''ai beaucoup de mal à comprendre la différence entre le past simple et le present perfect. Les explications dans les leçons ne sont pas assez claires pour moi. Pourriez-vous m''aider ou me fournir des ressources supplémentaires? J''ai déjà regardé les vidéos plusieurs fois mais je reste confus. Merci d''avance.',
    'OPEN',
    'MEDIUM',
    35,
    false,
    'Grammar',
    'Intermediate',
    'Understanding',
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
);

-- Complaint 2: Problème technique - Vidéo ne se charge pas
INSERT INTO complaints (
    user_id, target_role, category, subject, description,
    status, priority, risk_score, requires_intervention,
    issue_type, created_at, updated_at
) VALUES (
    1,
    'SUPPORT',
    'TECHNICAL',
    'Les vidéos du cours ne se chargent pas',
    'Depuis hier, je n''arrive plus à regarder les vidéos du cours "Business English Communication". La page se charge mais la vidéo reste bloquée sur l''écran de chargement. J''ai essayé avec Chrome et Firefox, même problème. Ma connexion internet fonctionne bien pour les autres sites. Pouvez-vous résoudre ce problème rapidement car j''ai un examen la semaine prochaine?',
    'SUBMITTED',
    'HIGH',
    60,
    true,
    'Video Playback',
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
);

-- Complaint 3: Problème d'horaire - Tuteur absent
INSERT INTO complaints (
    user_id, target_role, category, subject, description,
    status, priority, risk_score, requires_intervention,
    session_count, issue_type, created_at, updated_at
) VALUES (
    1,
    'ACADEMIC_OFFICE_AFFAIR',
    'SCHEDULE',
    'Mon tuteur était absent à la dernière séance',
    'J''avais une séance programmée hier à 14h avec mon tuteur pour le cours de prononciation. J''ai attendu 20 minutes mais il n''est jamais venu. Je n''ai reçu aucune notification d''annulation. C''est la deuxième fois que cela arrive ce mois-ci. Je perds du temps et cela affecte ma progression. Pouvez-vous intervenir?',
    'IN_PROGRESS',
    'HIGH',
    70,
    true,
    2,
    'Tutor Absence',
    CURRENT_TIMESTAMP - INTERVAL '3 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
);

-- Complaint 4: Problème administratif - Paiement non reconnu
INSERT INTO complaints (
    user_id, target_role, category, subject, description,
    status, priority, risk_score, requires_intervention,
    issue_type, created_at, updated_at
) VALUES (
    1,
    'ACADEMIC_OFFICE_AFFAIR',
    'ADMINISTRATIVE',
    'Mon paiement n''a pas été pris en compte',
    'J''ai effectué le paiement pour mon abonnement mensuel il y a 5 jours (référence: PAY-2024-001234) mais mon compte indique toujours que je dois payer. J''ai la preuve de paiement de ma banque. Pouvez-vous vérifier et mettre à jour mon statut? Je ne peux pas accéder à certains cours à cause de cela.',
    'ANALYZED',
    'CRITICAL',
    85,
    true,
    'Payment Issue',
    CURRENT_TIMESTAMP - INTERVAL '5 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
);

-- Complaint 5: Problème comportemental du tuteur
INSERT INTO complaints (
    user_id, target_role, category, subject, description,
    status, priority, risk_score, requires_intervention,
    issue_type, created_at, updated_at
) VALUES (
    1,
    'ACADEMIC_OFFICE_AFFAIR',
    'TUTOR_BEHAVIOR',
    'Comportement inapproprié du tuteur pendant la séance',
    'Lors de ma dernière séance avec le tuteur Khalil (ID: 13), j''ai trouvé son comportement inapproprié. Il était souvent distrait, répondait à son téléphone pendant la séance, et semblait impatient quand je posais des questions. Ce n''est pas professionnel et cela ne m''aide pas à apprendre. Je souhaiterais changer de tuteur ou au moins que cette situation soit adressée.',
    'SUBMITTED',
    'HIGH',
    75,
    true,
    'Unprofessional Behavior',
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
);

-- Complaint 6: Problème pédagogique - Quiz trop difficile
INSERT INTO complaints (
    user_id, target_role, category, subject, description,
    status, priority, risk_score, requires_intervention,
    course_type, difficulty, issue_type, created_at, updated_at
) VALUES (
    1,
    'TUTOR',
    'PEDAGOGICAL',
    'Le quiz "Academic Vocabulary" est trop difficile',
    'J''ai essayé de passer le quiz "Academic Vocabulary Quiz" trois fois et je n''arrive pas à obtenir la note de passage. Les questions portent sur des mots que nous n''avons pas étudiés dans les leçons. Il y a un décalage entre le contenu du cours et le quiz. Pourriez-vous revoir le quiz ou ajouter plus de contenu préparatoire?',
    'OPEN',
    'MEDIUM',
    40,
    false,
    'Vocabulary',
    'Advanced',
    'Quiz Difficulty',
    CURRENT_TIMESTAMP - INTERVAL '4 hours',
    CURRENT_TIMESTAMP - INTERVAL '4 hours'
);

-- Complaint 7: Problème technique - Impossible de soumettre le quiz
INSERT INTO complaints (
    user_id, target_role, category, subject, description,
    status, priority, risk_score, requires_intervention,
    issue_type, created_at, updated_at
) VALUES (
    1,
    'SUPPORT',
    'TECHNICAL',
    'Erreur lors de la soumission du quiz',
    'J''ai passé 30 minutes à répondre au quiz "Parts of Speech Quiz" et quand j''ai cliqué sur "Submit", j''ai reçu une erreur "500 Internal Server Error". Toutes mes réponses ont été perdues. C''est très frustrant! Pouvez-vous corriger ce bug et me permettre de repasser le quiz sans que cela compte comme une tentative?',
    'OPEN',
    'HIGH',
    65,
    true,
    'Quiz Submission Error',
    CURRENT_TIMESTAMP - INTERVAL '6 hours',
    CURRENT_TIMESTAMP - INTERVAL '6 hours'
);

-- Complaint 8: Problème d'horaire - Changement de planning
INSERT INTO complaints (
    user_id, target_role, category, subject, description,
    status, priority, risk_score, requires_intervention,
    session_count, issue_type, created_at, updated_at
) VALUES (
    1,
    'ACADEMIC_OFFICE_AFFAIR',
    'SCHEDULE',
    'Demande de changement d''horaire de séance',
    'En raison d''un changement dans mon emploi du temps professionnel, je ne peux plus assister aux séances du mardi à 14h. Serait-il possible de déplacer mes séances au jeudi à 16h? Je suis flexible sur l''horaire du jeudi. Merci de me confirmer si c''est possible.',
    'PENDING_STUDENT_CONFIRMATION',
    'LOW',
    15,
    false,
    1,
    'Schedule Change Request',
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
);

-- Complaint 9: Problème pédagogique - Besoin de ressources supplémentaires
INSERT INTO complaints (
    user_id, target_role, category, subject, description,
    status, priority, risk_score, requires_intervention,
    course_type, difficulty, issue_type, response, responder_id, responder_role, student_confirmed, created_at, updated_at
) VALUES (
    1,
    'TUTOR',
    'PEDAGOGICAL',
    'Besoin de plus d''exercices pratiques',
    'Le cours "Business English Communication" est excellent mais je trouve qu''il manque d''exercices pratiques. Après avoir appris la théorie sur les emails professionnels, j''aimerais avoir plus d''exemples et d''exercices pour m''entraîner. Avez-vous des ressources supplémentaires à me recommander?',
    'RESOLVED',
    'LOW',
    20,
    false,
    'Business English',
    'Intermediate',
    'Need More Practice',
    'Bonjour, merci pour votre retour. J''ai ajouté 5 nouveaux exercices pratiques dans le chapitre "Professional Email Writing". Vous trouverez également des templates d''emails dans les ressources du cours. N''hésitez pas si vous avez besoin d''aide supplémentaire.',
    13,
    'TUTOR',
    true,
    CURRENT_TIMESTAMP - INTERVAL '7 days',
    CURRENT_TIMESTAMP - INTERVAL '5 days'
);

-- Complaint 10: Problème technique - Certificat non généré
INSERT INTO complaints (
    user_id, target_role, category, subject, description,
    status, priority, risk_score, requires_intervention,
    issue_type, created_at, updated_at
) VALUES (
    1,
    'SUPPORT',
    'TECHNICAL',
    'Mon certificat de fin de cours n''a pas été généré',
    'J''ai terminé le cours "English Grammar Fundamentals" il y a 3 jours avec un score de 85%. Le système indique que le cours est complété mais je n''ai toujours pas reçu mon certificat. Pouvez-vous vérifier et me l''envoyer? J''en ai besoin pour mon dossier professionnel.',
    'SUBMITTED',
    'MEDIUM',
    45,
    false,
    'Certificate Generation',
    CURRENT_TIMESTAMP - INTERVAL '3 days',
    CURRENT_TIMESTAMP - INTERVAL '3 days'
);

-- Summary
DO $$
BEGIN
    RAISE NOTICE '';
    RAISE NOTICE '=== COMPLAINTS INSERTED SUCCESSFULLY ===';
    RAISE NOTICE 'Total: 10 complaints created for user ID 1';
    RAISE NOTICE 'Categories: PEDAGOGICAL (3), TECHNICAL (3), SCHEDULE (2), ADMINISTRATIVE (1), TUTOR_BEHAVIOR (1)';
    RAISE NOTICE 'Statuses: OPEN (3), SUBMITTED (3), IN_PROGRESS (1), ANALYZED (1), RESOLVED (1), PENDING_STUDENT_CONFIRMATION (1)';
    RAISE NOTICE 'Priorities: LOW (2), MEDIUM (3), HIGH (4), CRITICAL (1)';
END $$;
