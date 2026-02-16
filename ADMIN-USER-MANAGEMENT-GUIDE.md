# Guide de Gestion des Utilisateurs pour ADMIN

## Vue d'ensemble

Ce système permet aux administrateurs de gérer complètement tous les types d'utilisateurs dans l'application EnglishFlow :
- **Students** (Étudiants)
- **Tutors** (Tuteurs)
- **Academic Affairs** (Personnel académique)

## Architecture

### Backend (Spring Boot)

#### Contrôleur Principal
**`AdminUserController.java`** - `/auth/users`

Endpoints disponibles :
- `GET /auth/users` - Liste tous les utilisateurs
- `GET /auth/users/role/{role}` - Filtre par rôle (STUDENT, TUTOR, ACADEMIC_OFFICE_AFFAIR)
- `GET /auth/users/{id}` - Détails d'un utilisateur
- `POST /auth/users` - Créer un nouvel utilisateur
- `PUT /auth/users/{id}` - Modifier un utilisateur
- `DELETE /auth/users/{id}` - Supprimer un utilisateur
- `PATCH /auth/users/{id}/activate` - Activer un utilisateur
- `PATCH /auth/users/{id}/deactivate` - Désactiver un utilisateur
- `PATCH /auth/users/{id}/toggle-status` - Basculer le statut actif/inactif

#### Service
**`UserService.java`**

Méthodes principales :
- `getAllUsers()` - Récupère tous les utilisateurs
- `getUserById(Long id)` - Récupère un utilisateur par ID
- `getUsersByRole(String role)` - Filtre par rôle
- `createUser(CreateTutorRequest)` - Crée un utilisateur (tous rôles)
- `updateUser(Long id, UserDTO)` - Met à jour un utilisateur
- `deleteUser(Long id)` - Supprime un utilisateur
- `activateUser(Long id)` - Active un compte
- `deactivateUser(Long id)` - Désactive un compte
- `toggleUserStatus(Long id)` - Bascule le statut

#### DTOs
- **`CreateTutorRequest`** - Pour la création (avec password)
- **`UserDTO`** - Pour les réponses (sans password)

### Frontend (Angular)

#### Services
**`UserService`** - `frontend/src/app/core/services/user.service.ts`

Méthodes HTTP :
```typescript
getAllUsers(): Observable<User[]>
getUsersByRole(role: string): Observable<User[]>
getUserById(id: number): Observable<User>
createUser(user: CreateUserRequest): Observable<User>
updateUser(id: number, user: UpdateUserRequest): Observable<User>
deleteUser(id: number): Observable<void>
activateUser(id: number): Observable<User>
deactivateUser(id: number): Observable<User>
```

#### Composants de Gestion

1. **Students Component** - `/dashboard/users/students`
   - Liste paginée des étudiants
   - Recherche et filtres (statut)
   - Modal de création avec formulaire complet
   - Modal d'édition
   - Modal de visualisation détaillée
   - Actions : Activer, Désactiver, Modifier, Supprimer

2. **Tutors Component** - `/dashboard/users/tutors`
   - Liste paginée des tuteurs
   - Recherche et filtres
   - Création via formulaire wizard (3 étapes)
   - Génération automatique de mot de passe
   - Actions : Activer, Désactiver, Modifier, Supprimer

3. **Academic Affairs Component** - `/dashboard/users/academic-affairs`
   - Liste paginée du personnel académique
   - Recherche et filtres
   - Modal de création
   - Modal d'édition
   - Modal de visualisation
   - Actions : Activer, Désactiver, Modifier, Supprimer

## Fonctionnalités CRUD Complètes

### CREATE (Créer)

#### Pour Students
```typescript
// Champs requis
- firstName *
- lastName *
- email *
- password * (min 6 caractères)

// Champs optionnels
- phone
- cin
- dateOfBirth
- address
- city
- postalCode
- englishLevel (BEGINNER, ELEMENTARY, INTERMEDIATE, UPPER_INTERMEDIATE, ADVANCED, PROFICIENT)
```

#### Pour Tutors
```typescript
// Étape 1 : Informations de base
- firstName *
- lastName *
- email *
- phone *
- cin *
- dateOfBirth *

// Étape 2 : Adresse
- address *
- city *
- postalCode *

// Étape 3 : Informations professionnelles
- yearsOfExperience * (0-50)
- bio * (50-500 caractères)

// Auto-généré
- password (12 caractères aléatoires)
```

#### Pour Academic Affairs
```typescript
// Champs requis
- firstName *
- lastName *
- email *
- password * (min 6 caractères)

// Champs optionnels
- phone
- cin
- dateOfBirth
- address
- city
- postalCode
```

### READ (Lire)

#### Liste avec Filtres
- **Recherche** : Par nom, email, ou CIN
- **Filtre de statut** : Tous / Actifs / Inactifs
- **Pagination** : 10 éléments par page (configurable)

#### Statistiques Affichées
- Total d'utilisateurs
- Nombre d'actifs
- Nombre d'inactifs

#### Vue Détaillée
Modal affichant toutes les informations :
- Informations personnelles
- Coordonnées
- Statut du compte
- Frais d'inscription
- Date de création

### UPDATE (Modifier)

#### Champs Modifiables
```typescript
- firstName
- lastName
- email
- phone
- cin
- dateOfBirth
- address
- city
- postalCode
- englishLevel (pour students)
- yearsOfExperience (pour tutors)
- bio (pour tutors)
- isActive (checkbox)
- registrationFeePaid (checkbox)
```

**Note** : Le mot de passe ne peut pas être modifié via cette interface (utiliser la réinitialisation de mot de passe)

### DELETE (Supprimer)

- Confirmation requise avant suppression
- Suppression définitive de la base de données
- Message d'alerte : "This action cannot be undone"

### Actions Supplémentaires

#### Activer un Compte
- Change `isActive` à `true`
- L'utilisateur peut se connecter

#### Désactiver un Compte
- Change `isActive` à `false`
- L'utilisateur ne peut plus se connecter
- Confirmation requise

## Sécurité et Permissions

### Accès Réservé aux ADMIN
Seuls les utilisateurs avec le rôle `ADMIN` peuvent :
- Voir les pages de gestion des utilisateurs
- Créer de nouveaux utilisateurs
- Modifier les informations des utilisateurs
- Activer/Désactiver des comptes
- Supprimer des utilisateurs

### Validation Backend
- Email unique (vérification avant création)
- CIN unique (si fourni)
- Mot de passe encodé avec BCrypt
- Validation des rôles (ADMIN, STUDENT, TUTOR, ACADEMIC_OFFICE_AFFAIR)

### Validation Frontend
- Formulaires réactifs avec validation en temps réel
- Messages d'erreur clairs
- Champs requis marqués avec *
- Validation d'email
- Longueur minimale du mot de passe

## Navigation

### Menu Admin (Sidebar)
```
USER MANAGEMENT
├── Students (/dashboard/users/students)
├── Tutors (/dashboard/users/tutors)
└── Academic Affairs (/dashboard/users/academic-affairs)
```

## Utilisation

### Créer un Étudiant
1. Aller sur `/dashboard/users/students`
2. Cliquer sur "Add New Student"
3. Remplir le formulaire
4. Cliquer sur "Create Student"
5. L'étudiant apparaît dans la liste

### Modifier un Utilisateur
1. Trouver l'utilisateur dans la liste
2. Cliquer sur l'icône "Edit" (crayon)
3. Modifier les champs souhaités
4. Cliquer sur "Update"

### Désactiver un Compte
1. Trouver l'utilisateur actif
2. Cliquer sur l'icône "Deactivate" (cercle barré)
3. Confirmer l'action
4. Le statut passe à "Inactive"

### Supprimer un Utilisateur
1. Trouver l'utilisateur
2. Cliquer sur l'icône "Delete" (poubelle)
3. Confirmer la suppression
4. L'utilisateur est supprimé définitivement

## Messages de Confirmation

### Succès
- "Student created successfully!"
- "Student updated successfully!"
- "Student activated successfully!"
- "Student deactivated successfully!"
- "Student deleted successfully!"

### Erreurs
- "Email already exists"
- "CIN already exists"
- "Failed to create student. Please try again."
- "Failed to update student. Please try again."

## Responsive Design

- **Desktop** : Tableau complet avec toutes les colonnes
- **Tablet** : Colonnes essentielles visibles
- **Mobile** : Vue adaptée avec actions accessibles

## Dark Mode

Tous les composants supportent le mode sombre :
- Fond : `dark:bg-gray-800`
- Texte : `dark:text-white`
- Bordures : `dark:border-gray-700`

## Prochaines Améliorations Possibles

1. **Export de données** : CSV, Excel
2. **Import en masse** : Upload de fichiers
3. **Historique des modifications** : Audit trail
4. **Filtres avancés** : Par date, par ville, etc.
5. **Envoi d'emails** : Notifications aux utilisateurs
6. **Réinitialisation de mot de passe** : Par l'admin
7. **Gestion des permissions** : Rôles personnalisés
8. **Statistiques avancées** : Graphiques et rapports

## Support

Pour toute question ou problème :
1. Vérifier les logs du backend (console Spring Boot)
2. Vérifier la console du navigateur (F12)
3. Vérifier que le backend est démarré sur le port 8081
4. Vérifier que l'utilisateur connecté a le rôle ADMIN

## Commandes Utiles

### Backend
```bash
# Démarrer le service auth
cd backend/auth-service
./mvnw spring-boot:run
```

### Frontend
```bash
# Démarrer l'application Angular
cd frontend
npm start
```

### Base de Données
```sql
-- Vérifier les utilisateurs
SELECT id, email, firstName, lastName, role, isActive FROM users;

-- Compter par rôle
SELECT role, COUNT(*) FROM users GROUP BY role;

-- Trouver les admins
SELECT * FROM users WHERE role = 'ADMIN';
```
