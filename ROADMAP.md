# 🗺️ EnglishFlow - Roadmap de Développement

## 📊 État Actuel du Projet

### ✅ Fonctionnalités Complétées

#### 1. **Système d'Authentification** ✅
- Login/Register avec OAuth2 (Google, Facebook)
- Gestion des rôles (STUDENT, TUTOR, ACADEMIC, ADMIN)
- Activation de compte par email
- Reset password
- JWT Token management
- Profile management avec photo upload

#### 2. **Gestion des Cours (Courses-Service)** ✅
- CRUD complet des cours par les tutors
- Système de catégories dynamiques (base de données)
- Niveaux CEFR (A1-C2)
- Upload de thumbnails et fichiers
- Gestion des chapitres (Chapters)
- Gestion des leçons (Lessons) avec types:
  - VIDEO (upload direct ou URL YouTube/Vimeo)
  - TEXT (contenu formaté)
  - DOCUMENT (PDF, DOC, PPT, etc.)
  - QUIZ, ASSIGNMENT, INTERACTIVE
- Upload de vidéos (max 500MB)
- Upload de documents (max 50MB)
- Interface moderne pour tutors:
  - Course List avec filtres
  - Course Create (wizard 4 étapes)
  - Course Edit
  - Course View avec chapitres/leçons publiés
  - Chapter Management
  - Lesson Management

#### 3. **Système de Packs** ✅
- Création de packs par les academics
- Pack contient plusieurs cours d'un tuteur spécifique
- Gestion des enrollments aux packs
- Pack Management (academic-panel)
- Pack Catalog (student-panel)
- My Packs (student-panel)
- **Home Page avec affichage des packs** ✅
- **Page Pack Details complète** ✅

#### 4. **Clubs** ✅
- Création et gestion des clubs
- Inscription aux clubs
- Page publique des clubs

#### 5. **Services Techniques** ✅
- API Gateway (port 8080)
- Config Server
- Eureka Service Discovery
- Auth Service (port 8081)
- Courses Service (port 8086)
- CORS configuré uniquement dans API Gateway
- Base de données PostgreSQL pour chaque service

---

## 🚧 Fonctionnalités à Développer

### 🎯 PRIORITÉ HAUTE

#### 1. **Système d'Enrollment aux Packs** 🔴
**Objectif**: Permettre aux étudiants de s'inscrire aux packs depuis la page Pack Details

**Backend (Pack-Service)**:
- ✅ Endpoint POST `/pack-enrollments` existe déjà
- ✅ Endpoint GET `/pack-enrollments/student/{studentId}` existe
- ✅ Endpoint GET `/pack-enrollments/pack/{packId}` existe
- ⚠️ À vérifier: Logique de décrémentation des `availableSlots`
- ⚠️ À vérifier: Validation du nombre max d'étudiants

**Frontend**:
- [ ] Modifier `pack-details.component.ts`:
  - Créer méthode `enrollInPack()` qui appelle le service
  - Gérer les cas d'erreur (pack full, déjà inscrit, etc.)
  - Afficher message de succès/erreur
  - Rediriger vers "My Packs" après inscription
- [ ] Créer `PackEnrollmentService` si nécessaire
- [ ] Ajouter modal de confirmation avant enrollment
- [ ] Afficher le statut d'enrollment (déjà inscrit ou non)

**Fichiers à modifier**:
- `frontend/src/app/pages/pack-details/pack-details.component.ts`
- `frontend/src/app/pages/pack-details/pack-details.component.html`
- `frontend/src/app/core/services/pack-enrollment.service.ts` (à créer)

---

#### 2. **Système de Progression des Cours** 🔴
**Objectif**: Tracker la progression des étudiants dans les cours

**Backend (Courses-Service)**:
- ✅ Entities existent: `ChapterProgress`, `LessonProgress`
- ✅ Controllers existent: `ChapterProgressController`, `LessonProgressController`
- ⚠️ À vérifier: Logique de calcul de progression globale

**Frontend**:
- [ ] Créer `CourseProgressService`
- [ ] Créer composant `course-learning` (student-panel):
  - Afficher la liste des chapitres
  - Afficher la progression par chapitre
  - Marquer les leçons comme complétées
  - Barre de progression globale
- [ ] Créer composant `lesson-viewer` (student-panel):
  - Lecteur vidéo pour VIDEO lessons
  - Affichage du contenu pour TEXT lessons
  - Viewer PDF pour DOCUMENT lessons
  - Bouton "Mark as Complete"
  - Navigation Previous/Next lesson
- [ ] Intégrer dans "My Packs" → Courses → Learning

**Fichiers à créer**:
- `frontend/src/app/pages/student-panel/course-learning/`
- `frontend/src/app/pages/student-panel/lesson-viewer/`
- `frontend/src/app/core/services/course-progress.service.ts`

---

#### 3. **Dashboard Analytics** 🔴
**Objectif**: Afficher des statistiques pour chaque rôle

**Pour TUTOR**:
- [ ] Nombre total de cours créés
- [ ] Nombre d'étudiants inscrits (via packs)
- [ ] Cours les plus populaires
- [ ] Revenus générés (si système de paiement)
- [ ] Graphiques de progression

**Pour STUDENT**:
- [ ] Nombre de packs inscrits
- [ ] Progression globale
- [ ] Cours en cours
- [ ] Cours complétés
- [ ] Temps d'apprentissage

**Pour ACADEMIC**:
- [ ] Nombre de packs créés
- [ ] Nombre d'enrollments
- [ ] Packs les plus populaires
- [ ] Statistiques par tuteur

**Fichiers à créer**:
- `frontend/src/app/pages/tutor-panel/dashboard/`
- `frontend/src/app/pages/student-panel/dashboard/`
- `frontend/src/app/pages/academic-panel/dashboard/`

---

### 🎯 PRIORITÉ MOYENNE

#### 4. **Système de Quiz et Assignments** 🟡
**Objectif**: Permettre aux tutors de créer des quiz et assignments

**Backend**:
- [ ] Créer entities: `Quiz`, `Question`, `Answer`, `QuizAttempt`
- [ ] Créer entities: `Assignment`, `AssignmentSubmission`
- [ ] Controllers pour CRUD
- [ ] Logique de correction automatique pour quiz
- [ ] Upload de fichiers pour assignments

**Frontend**:
- [ ] Interface de création de quiz (tutor-panel)
- [ ] Interface de création d'assignments (tutor-panel)
- [ ] Interface de passage de quiz (student-panel)
- [ ] Interface de soumission d'assignments (student-panel)
- [ ] Affichage des résultats et corrections

---

#### 5. **Système de Notifications** 🟡
**Objectif**: Notifier les utilisateurs des événements importants

**Backend**:
- [ ] Service de notifications (WebSocket ou SSE)
- [ ] Notifications pour:
  - Nouveau cours ajouté à un pack
  - Deadline d'assignment
  - Nouveau message dans un club
  - Enrollment accepté/refusé

**Frontend**:
- ✅ Composant `frontoffice-notification-dropdown` existe
- [ ] Intégrer avec le backend
- [ ] Afficher notifications en temps réel
- [ ] Marquer comme lu
- [ ] Filtrer par type

---

#### 6. **Système de Messagerie** 🟡
**Objectif**: Communication entre étudiants et tutors

**Backend**:
- [ ] Service de messagerie
- [ ] Entities: `Conversation`, `Message`
- [ ] WebSocket pour temps réel

**Frontend**:
- ✅ Composant `messages` existe dans student-panel
- [ ] Interface de chat
- [ ] Liste des conversations
- [ ] Notifications de nouveaux messages

---

#### 7. **Système de Reviews et Ratings** 🟡
**Objectif**: Permettre aux étudiants de noter les cours et tutors

**Backend**:
- [ ] Entities: `CourseReview`, `TutorReview`
- [ ] Calcul de rating moyen
- [ ] Validation (1 review par étudiant par cours)

**Frontend**:
- [ ] Interface de soumission de review
- [ ] Affichage des reviews sur pack-details
- [ ] Affichage des reviews sur tutor profile
- [ ] Système d'étoiles (1-5)

---

### 🎯 PRIORITÉ BASSE

#### 8. **Système de Paiement** 🟢
**Objectif**: Intégrer un système de paiement (Stripe/PayPal)

**Backend**:
- [ ] Intégration Stripe API
- [ ] Entities: `Payment`, `Transaction`
- [ ] Webhooks pour confirmation de paiement
- [ ] Gestion des remboursements

**Frontend**:
- [ ] Page de checkout
- [ ] Historique des paiements
- [ ] Factures téléchargeables

---

#### 9. **Système de Certificats** 🟢
**Objectif**: Générer des certificats de complétion

**Backend**:
- [ ] Service de génération de PDF
- [ ] Template de certificat
- [ ] Signature numérique

**Frontend**:
- [ ] Affichage des certificats obtenus
- [ ] Téléchargement en PDF
- [ ] Partage sur réseaux sociaux

---

#### 10. **Système de Gamification** 🟢
**Objectif**: Badges, points, leaderboard

**Backend**:
- [ ] Entities: `Badge`, `UserBadge`, `Points`
- [ ] Logique d'attribution de badges
- [ ] Calcul de leaderboard

**Frontend**:
- [ ] Affichage des badges
- [ ] Barre de progression XP
- [ ] Leaderboard global

---

#### 11. **Système de Calendrier** 🟢
**Objectif**: Planification des sessions live

**Backend**:
- [ ] Entities: `LiveSession`, `SessionBooking`
- [ ] Intégration avec Google Calendar
- [ ] Notifications de rappel

**Frontend**:
- ✅ Composant `calender` existe
- [ ] Intégrer avec le backend
- [ ] Réservation de sessions
- [ ] Affichage des sessions à venir

---

#### 12. **Forum/Community** 🟢
**Objectif**: Espace de discussion pour les étudiants

**Backend**:
- [ ] Service community
- [ ] Entities: `ForumPost`, `Comment`, `Like`
- [ ] Modération

**Frontend**:
- [ ] Liste des posts
- [ ] Création de post
- [ ] Commentaires
- [ ] Système de likes/votes

---

## 📋 Tâches Techniques

### Backend

#### 1. **Tests Unitaires et Intégration** 🔴
- [ ] Tests pour Auth Service
- [ ] Tests pour Courses Service
- [ ] Tests pour Pack Service
- [ ] Tests pour Club Service
- [ ] Coverage minimum 70%

#### 2. **Documentation API** 🟡
- [ ] Swagger/OpenAPI pour tous les services
- [ ] Documentation des endpoints
- [ ] Exemples de requêtes/réponses

#### 3. **Sécurité** 🔴
- [ ] Rate limiting
- [ ] Input validation
- [ ] SQL injection prevention
- [ ] XSS protection
- [ ] CSRF tokens

#### 4. **Performance** 🟡
- [ ] Caching (Redis)
- [ ] Database indexing
- [ ] Query optimization
- [ ] Lazy loading

#### 5. **Monitoring** 🟡
- [ ] Logging centralisé (ELK Stack)
- [ ] Métriques (Prometheus + Grafana)
- [ ] Health checks
- [ ] Alerting

---

### Frontend

#### 1. **Tests** 🔴
- [ ] Tests unitaires (Jasmine/Karma)
- [ ] Tests E2E (Cypress)
- [ ] Coverage minimum 60%

#### 2. **Optimisation** 🟡
- [ ] Lazy loading des modules
- [ ] Image optimization
- [ ] Bundle size reduction
- [ ] PWA (Progressive Web App)

#### 3. **Accessibilité** 🟡
- [ ] ARIA labels
- [ ] Keyboard navigation
- [ ] Screen reader support
- [ ] Contrast ratios

#### 4. **Internationalisation** 🟢
- [ ] Support multi-langues (FR, EN, AR)
- [ ] ngx-translate
- [ ] Date/Number formatting

---

## 🎨 Améliorations UI/UX

### 1. **Design System** 🟡
- [ ] Créer une bibliothèque de composants réutilisables
- [ ] Définir les couleurs, typographies, espacements
- [ ] Créer un style guide

### 2. **Animations** 🟢
- [ ] Transitions entre pages
- [ ] Loading skeletons
- [ ] Micro-interactions
- [ ] Animations de succès/erreur

### 3. **Responsive Design** 🟡
- [ ] Tester sur tous les breakpoints
- [ ] Optimiser pour mobile
- [ ] Touch gestures

### 4. **Dark Mode** 🟢
- [ ] Implémenter le thème sombre
- [ ] Toggle dans les settings
- [ ] Sauvegarder la préférence

---

## 📱 Mobile App (Optionnel)

### React Native ou Flutter
- [ ] Authentification
- [ ] Navigation
- [ ] Lecture de vidéos offline
- [ ] Push notifications
- [ ] App Store / Play Store deployment

---

## 🚀 Déploiement

### 1. **Infrastructure** 🔴
- [ ] Docker containers pour tous les services
- [ ] Docker Compose pour dev
- [ ] Kubernetes pour production
- [ ] CI/CD Pipeline (GitHub Actions / GitLab CI)

### 2. **Environnements** 🔴
- [ ] Development
- [ ] Staging
- [ ] Production

### 3. **Base de Données** 🔴
- [ ] Backups automatiques
- [ ] Réplication
- [ ] Migration scripts

### 4. **CDN** 🟡
- [ ] CloudFlare ou AWS CloudFront
- [ ] Optimisation des assets
- [ ] Caching

---

## 📊 Métriques de Succès

### KPIs à Tracker
- [ ] Nombre d'utilisateurs actifs (DAU/MAU)
- [ ] Taux de conversion (visiteurs → inscrits)
- [ ] Taux de complétion des cours
- [ ] Temps moyen passé sur la plateforme
- [ ] Taux de rétention (7 jours, 30 jours)
- [ ] NPS (Net Promoter Score)

---

## 🗓️ Planning Suggéré

### Phase 1 (2-3 semaines) - MVP Complet
1. ✅ Système d'enrollment aux packs
2. ✅ Système de progression des cours
3. ✅ Dashboard analytics basique
4. ✅ Tests et corrections de bugs

### Phase 2 (2-3 semaines) - Engagement
1. Système de quiz et assignments
2. Système de notifications
3. Système de messagerie
4. Reviews et ratings

### Phase 3 (2-3 semaines) - Monétisation
1. Système de paiement
2. Certificats
3. Gamification
4. Calendrier et sessions live

### Phase 4 (2-3 semaines) - Scaling
1. Forum/Community
2. Mobile app
3. Optimisations performance
4. Internationalisation

### Phase 5 (Continu) - Maintenance
1. Monitoring et alerting
2. Bug fixes
3. Nouvelles fonctionnalités
4. Support utilisateurs

---

## 📝 Notes Importantes

### Architecture Actuelle
- **Microservices**: Auth, Courses, Clubs (+ futurs services)
- **API Gateway**: Port 8080 (point d'entrée unique)
- **Service Discovery**: Eureka
- **Config Server**: Configuration centralisée
- **Base de données**: PostgreSQL par service
- **Frontend**: Angular 18 standalone components

### Conventions de Code
- **Backend**: 
  - Pas de préfixe `/api` dans les controllers (géré par Gateway)
  - CORS uniquement dans API Gateway
  - DTOs pour toutes les réponses
  - Validation avec `@Valid`
  
- **Frontend**:
  - Standalone components
  - Nouvelle syntaxe `@if`, `@for` au lieu de `*ngIf`, `*ngFor`
  - Services injectés avec `providedIn: 'root'`
  - Lazy loading des routes

### Ports Utilisés
- 8080: API Gateway
- 8081: Auth Service
- 8082: Club Service
- 8086: Courses Service
- 8761: Eureka Server
- 8888: Config Server
- 4200: Angular Frontend

---

## 🎯 Prochaines Étapes Immédiates

1. **Tester l'application actuelle**:
   - Vérifier que tous les services démarrent
   - Tester la création de cours
   - Tester l'affichage des packs
   - Tester la navigation

2. **Implémenter l'enrollment aux packs**:
   - C'est la fonctionnalité la plus critique
   - Permet aux étudiants de commencer à utiliser la plateforme

3. **Créer le système de progression**:
   - Permet aux étudiants de suivre leurs cours
   - Marquer les leçons comme complétées

4. **Ajouter les dashboards**:
   - Donne une vue d'ensemble à chaque utilisateur
   - Améliore l'engagement

---

## 📞 Support et Documentation

### Ressources
- Documentation Spring Boot: https://spring.io/projects/spring-boot
- Documentation Angular: https://angular.io/docs
- Documentation PostgreSQL: https://www.postgresql.org/docs/

### Contacts
- Email: jungleinenglish.platform@gmail.com

---

**Dernière mise à jour**: 24 Février 2026
**Version**: 1.0
**Statut**: En développement actif 🚀
