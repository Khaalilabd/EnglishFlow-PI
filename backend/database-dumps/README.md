# Database Dumps - EnglishFlow

## 📦 Fichiers Disponibles

### ✅ Fichiers Complets (À Utiliser)
- `englishflow_identity_FULL.sql` - **TOUTES** les données de la base d'authentification
- `englishflow_courses_FULL.sql` - **TOUTES** les données de la base de cours

### Tables Incluses

**englishflow_identity_FULL.sql:**
- ✅ users (37 utilisateurs)
- ✅ student_analytics (métriques ML)
- ✅ activation_tokens
- ✅ refresh_tokens
- ✅ user_sessions
- ✅ tutor_applications
- ✅ application_documents
- ✅ application_status_history
- ✅ interview_schedules

**englishflow_courses_FULL.sql:**
- ✅ courses (tous les cours)
- ✅ chapters (chapitres)
- ✅ lessons (leçons)
- ✅ packs (bundles de cours)
- ✅ pack_courses (relation pack-cours)
- ✅ course_enrollments (inscriptions aux cours)
- ✅ pack_enrollments (inscriptions aux packs)
- ✅ lesson_progress (progression des leçons)
- ✅ course_categories
- ✅ tutor_availability
- ✅ tutor_available_days
- ✅ tutor_categories
- ✅ time_slots
- ✅ lesson_time_assignments
- ✅ online_meeting_sessions
- ✅ chapter_objectives
- ✅ availability_modification_request

## 🚀 Comment Importer dans pgAdmin

### Étape 1: Créer les Bases de Données

1. Ouvrir pgAdmin
2. Créer deux bases de données:
   - `englishflow_identity`
   - `englishflow_courses`

### Étape 2: Exécuter les Migrations Flyway

**Important:** Avant d'importer les données, vous devez d'abord créer les tables en démarrant les services backend:

```bash
# Démarrer auth-service (créera les tables dans englishflow_identity)
cd backend/auth-service
mvn spring-boot:run

# Démarrer courses-service (créera les tables dans englishflow_courses)
cd backend/courses-service
mvn spring-boot:run
```

Les migrations Flyway créeront automatiquement toutes les tables nécessaires.

### Étape 3: Importer les Données

#### Option 1: Via pgAdmin (Interface Graphique)

1. **Pour englishflow_identity:**
   - Clic droit sur la base `englishflow_identity`
   - Sélectionner `Restore...`
   - Choisir le fichier `englishflow_identity_data.sql`
   - Cliquer sur `Restore`

2. **Pour englishflow_courses:**
   - Clic droit sur la base `englishflow_courses`
   - Sélectionner `Restore...`
   - Choisir le fichier `englishflow_courses_data.sql`
   - Cliquer sur `Restore`

#### Option 2: Via Query Tool

1. Ouvrir pgAdmin
2. Sélectionner la base `englishflow_identity`
3. Ouvrir `Query Tool` (Tools → Query Tool)
4. Ouvrir le fichier `englishflow_identity_data.sql`
5. Exécuter le script (F5)
6. Répéter pour `englishflow_courses` avec `englishflow_courses_data.sql`

#### Option 3: Via Ligne de Commande

```bash
# Importer englishflow_identity
psql -U postgres -d englishflow_identity -f englishflow_identity_data.sql

# Importer englishflow_courses
psql -U postgres -d englishflow_courses -f englishflow_courses_data.sql
```

## 📊 Données Incluses

### Base englishflow_identity
- **Users** : Étudiants, tuteurs, administrateurs
- **Student Analytics** : Métriques de performance et d'engagement
- **Roles & Permissions**

### Base englishflow_courses
- **Courses** : Cours d'anglais avec chapitres et leçons
- **Packs** : Bundles de cours
- **Enrollments** : Inscriptions des étudiants
- **Lesson Progress** : Progression dans les leçons
- **Quizzes** : Quiz et questions

## 👥 Comptes de Test Disponibles

Après l'import, vous aurez accès à ces comptes:

### Administrateur
- Email: `admin@englishflow.com`
- Password: `admin123`

### Tuteur (Khalil Abdelmoumen)
- Email: `khalil.abdelmoumen@gmail.com`
- Password: (connexion via Google OAuth2)
- Tutor ID: 53

### Étudiants de Test
1. **Sarah Johnson** (ID: 57) - High Performer (88%)
2. **Michael Chen** (ID: 58) - Good Student (75%)
3. **Emma Garcia** (ID: 59) - Average (62%)
4. **David Martinez** (ID: 60) - At Risk (45%)
5. **Lisa Anderson** (ID: 61) - High Risk (32%)

## ⚠️ Notes Importantes

1. **Ordre d'Import**: Toujours importer `englishflow_identity` avant `englishflow_courses` (dépendances)
2. **Migrations Flyway**: Les tables doivent exister avant l'import des données
3. **Mot de Passe PostgreSQL**: Par défaut `postgres` (à changer si différent)
4. **Port PostgreSQL**: Par défaut `5432`

## 🔧 En Cas de Problème

### Erreur: "relation does not exist"
→ Les tables n'ont pas été créées. Démarrez les services backend pour exécuter les migrations Flyway.

### Erreur: "duplicate key value"
→ Les données existent déjà. Supprimez les données existantes ou recréez les bases.

### Erreur: "permission denied"
→ Vérifiez que l'utilisateur PostgreSQL a les droits nécessaires.

## 📝 Commandes Utiles

```sql
-- Vérifier les données importées
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM student_analytics;
SELECT COUNT(*) FROM courses;
SELECT COUNT(*) FROM packs;

-- Réinitialiser une base (ATTENTION: supprime toutes les données)
DROP DATABASE englishflow_identity;
CREATE DATABASE englishflow_identity;
```

---

**Dernière Mise à Jour**: Mai 2026  
**Version**: 1.0.0
