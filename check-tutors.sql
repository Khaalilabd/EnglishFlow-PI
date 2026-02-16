-- Vérifier tous les utilisateurs
SELECT id, email, firstName, lastName, role, isActive 
FROM users 
ORDER BY role, id;

-- Compter par rôle
SELECT role, COUNT(*) as count 
FROM users 
GROUP BY role;

-- Vérifier spécifiquement les tutors
SELECT * 
FROM users 
WHERE role = 'TUTOR';

-- Vérifier si le compte tutor existe
SELECT id, email, firstName, lastName, role, isActive, createdAt
FROM users 
WHERE role = 'TUTOR'
ORDER BY createdAt DESC;
