# Guide des Rôles et Navigation Dashboard

## Valeurs des Rôles dans la Base de Données

Les rôles doivent avoir **EXACTEMENT** ces valeurs dans la colonne `role` de la table `users`:

- `ADMIN`
- `ACADEMIC_OFFICE_AFFAIR`
- `TUTOR`
- `STUDENT`

⚠️ **IMPORTANT**: Les valeurs sont sensibles à la casse (majuscules/minuscules)!

---

## Navigation par Rôle

### 1. Rôle: `ADMIN`

**Sections visibles dans le sidebar:**

#### 📊 OVERVIEW
- Dashboard (`/dashboard`)
- Calendar (`/dashboard/calendar`)

#### 👥 USER MANAGEMENT
- Students (`/dashboard/users/students`)
- Tutors (`/dashboard/users/tutors`)
- Academic Affairs (`/dashboard/users/academic-affairs`)

#### 📈 STATISTICS
- View Statistics (`/dashboard/statistics`)

#### ⚙️ SYSTEM
- Profile (`/dashboard/profile`)
- Settings (`/dashboard/settings`)

---

### 2. Rôle: `ACADEMIC_OFFICE_AFFAIR`

**Sections visibles dans le sidebar:**

#### 📊 OVERVIEW
- Dashboard (`/dashboard`)
- Calendar (`/dashboard/calendar`)

#### 📅 SCHEDULE
- Manage Schedules (`/dashboard/schedules/manage`)
- View Schedules (`/dashboard/schedules`)

#### 💰 FINANCIAL
- Manage Refunds (`/dashboard/refunds`)
- Manage Payments (`/dashboard/payments`)
- Manage Subscriptions (`/dashboard/subscriptions`)

#### 🎯 CLUBS & EVENTS
- Manage Clubs (`/dashboard/clubs/manage`)
- Manage Events (`/dashboard/events/manage`)
- Club Requests (`/dashboard/clubs/requests`)

#### 📝 FEEDBACK
- Manage Complaints (`/dashboard/complaints`)
- Manage Feedbacks (`/dashboard/feedbacks`)

#### ⚙️ SYSTEM
- Profile (`/dashboard/profile`)
- Settings (`/dashboard/settings`)

---

### 3. Rôle: `TUTOR`

Les tutors utilisent le **Tutor Panel** (`/tutor-panel`) et non le dashboard admin.

---

### 4. Rôle: `STUDENT`

Les students utilisent le **User Panel** (`/user-panel`) et non le dashboard admin.

---

## Comment Modifier le Rôle d'un Utilisateur

### Option 1: Via SQL Direct

```sql
-- Changer un utilisateur en ADMIN
UPDATE users SET role = 'ADMIN' WHERE email = 'user@example.com';

-- Changer un utilisateur en ACADEMIC_OFFICE_AFFAIR
UPDATE users SET role = 'ACADEMIC_OFFICE_AFFAIR' WHERE email = 'user@example.com';

-- Changer un utilisateur en TUTOR
UPDATE users SET role = 'TUTOR' WHERE email = 'user@example.com';

-- Changer un utilisateur en STUDENT
UPDATE users SET role = 'STUDENT' WHERE email = 'user@example.com';
```

### Option 2: Via l'Interface Admin (à développer)

Une interface de gestion des utilisateurs sera disponible dans:
- `/dashboard/users/students` - Gérer les étudiants
- `/dashboard/users/tutors` - Gérer les tuteurs
- `/dashboard/users/academic-affairs` - Gérer les Academic Affairs

---

## Vérification du Rôle

Pour vérifier le rôle actuel d'un utilisateur:

```sql
SELECT id, email, firstName, lastName, role 
FROM users 
WHERE email = 'user@example.com';
```

---

## Dépannage

### Problème: La navbar ne change pas après modification du rôle

**Solution:**
1. Déconnectez-vous de l'application
2. Reconnectez-vous avec le compte modifié
3. Le nouveau rôle sera chargé depuis la base de données

### Problème: Toutes les sections s'affichent

**Cause:** Le rôle par défaut est `ADMIN` si aucun rôle n'est trouvé.

**Solution:** Vérifiez que le rôle est bien défini dans la base de données.

### Problème: Aucune section ne s'affiche

**Cause:** Le rôle dans la base de données ne correspond pas exactement aux valeurs attendues.

**Solution:** 
- Vérifiez l'orthographe exacte (majuscules)
- Utilisez une des 4 valeurs exactes: `ADMIN`, `ACADEMIC_OFFICE_AFFAIR`, `TUTOR`, `STUDENT`

---

## Exemple de Test

```sql
-- Créer un utilisateur ADMIN
INSERT INTO users (email, password, firstName, lastName, role, isActive, registrationFeePaid)
VALUES ('admin@test.com', '$2a$10$...', 'Admin', 'User', 'ADMIN', true, true);

-- Créer un utilisateur ACADEMIC_OFFICE_AFFAIR
INSERT INTO users (email, password, firstName, lastName, role, isActive, registrationFeePaid)
VALUES ('academic@test.com', '$2a$10$...', 'Academic', 'Officer', 'ACADEMIC_OFFICE_AFFAIR', true, true);
```

---

## Notes Importantes

1. **Sensibilité à la casse**: `ADMIN` ≠ `admin` ≠ `Admin`
2. **Pas d'espaces**: `ACADEMIC_OFFICE_AFFAIR` (avec underscore, pas d'espace)
3. **Déconnexion requise**: Après modification du rôle, l'utilisateur doit se reconnecter
4. **Token JWT**: Le rôle est stocké dans le token JWT, donc une nouvelle connexion est nécessaire

---

## Structure du Code

### Backend (Java)
```java
// EnglishFlow-PI/backend/auth-service/src/main/java/com/englishflow/auth/entity/User.java
public enum Role {
    ADMIN,
    TUTOR,
    STUDENT,
    ACADEMIC_OFFICE_AFFAIR
}
```

### Frontend (TypeScript)
```typescript
// EnglishFlow-PI/frontend/src/app/core/models/user.model.ts
export enum UserRole {
  ADMIN = 'ADMIN',
  TUTOR = 'TUTOR',
  TEACHER = 'TEACHER',
  STUDENT = 'STUDENT',
  ACADEMIC_OFFICE_AFFAIR = 'ACADEMIC_OFFICE_AFFAIR'
}
```

### Sidebar Component
```typescript
// EnglishFlow-PI/frontend/src/app/shared/layout/app-sidebar/app-sidebar.component.ts
ngOnInit() {
  const currentUser = this.authService.currentUserValue;
  if (currentUser && currentUser.role) {
    this.currentUserRole = currentUser.role; // 'ADMIN' ou 'ACADEMIC_OFFICE_AFFAIR'
  }
}
```

### HTML Template
```html
<!-- Affiche uniquement si le rôle est ADMIN -->
@if (currentUserRole === 'ADMIN') {
  <!-- Sections ADMIN -->
}

<!-- Affiche uniquement si le rôle est ACADEMIC_OFFICE_AFFAIR -->
@if (currentUserRole === 'ACADEMIC_OFFICE_AFFAIR') {
  <!-- Sections ACADEMIC_OFFICE_AFFAIR -->
}
```
