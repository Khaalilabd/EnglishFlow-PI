# Debug - Liste des Tutors Vide

## Problème
La liste des tutors affiche 0 données alors qu'un compte tutor existe.

## Vérifications à Faire

### 1. Vérifier que le Backend est Démarré
```bash
# Vérifier si le service auth tourne sur le port 8081
curl http://localhost:8081/actuator/health

# Ou vérifier les processus
lsof -i :8081
```

### 2. Tester l'API Directement

#### Test 1 : Récupérer tous les utilisateurs
```bash
curl -X GET http://localhost:8081/auth/users
```

#### Test 2 : Récupérer les tutors
```bash
curl -X GET http://localhost:8081/auth/users/role/TUTOR
```

#### Test 3 : Avec authentification (si nécessaire)
```bash
# Remplacer YOUR_TOKEN par le token JWT de votre session admin
curl -X GET http://localhost:8081/auth/users/role/TUTOR \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. Vérifier la Base de Données

Exécutez ce SQL dans votre base de données :

```sql
-- Voir tous les utilisateurs
SELECT id, email, firstName, lastName, role, isActive 
FROM users;

-- Compter par rôle
SELECT role, COUNT(*) 
FROM users 
GROUP BY role;

-- Voir les tutors spécifiquement
SELECT * FROM users WHERE role = 'TUTOR';
```

### 4. Vérifier la Console du Navigateur

1. Ouvrir les DevTools (F12)
2. Aller dans l'onglet **Console**
3. Aller dans l'onglet **Network**
4. Recharger la page `/dashboard/users/tutors`
5. Chercher la requête vers `auth/users/role/TUTOR`
6. Vérifier :
   - Le statut HTTP (200, 401, 403, 404, 500 ?)
   - La réponse (Response tab)
   - Les headers (Headers tab)

### 5. Problèmes Possibles et Solutions

#### Problème 1 : Backend non démarré
**Symptôme** : Erreur réseau dans la console, "ERR_CONNECTION_REFUSED"

**Solution** :
```bash
cd backend/auth-service
./mvnw spring-boot:run
```

#### Problème 2 : Pas de tutors dans la DB
**Symptôme** : API retourne `[]` (tableau vide)

**Solution** : Créer un tutor via l'interface ou SQL :
```sql
INSERT INTO users (
  email, password, firstName, lastName, role, 
  isActive, registrationFeePaid, createdAt, updatedAt
) VALUES (
  'tutor@test.com',
  '$2a$10$abcdefghijklmnopqrstuvwxyz', -- Mot de passe encodé
  'Test',
  'Tutor',
  'TUTOR',
  true,
  false,
  NOW(),
  NOW()
);
```

#### Problème 3 : Erreur 401 (Non autorisé)
**Symptôme** : Status 401 dans Network tab

**Solution** : Le endpoint nécessite peut-être une authentification. Vérifier que vous êtes connecté en tant qu'ADMIN.

#### Problème 4 : Erreur 403 (Interdit)
**Symptôme** : Status 403 dans Network tab

**Solution** : Votre compte n'a pas les permissions. Vérifier le rôle :
```sql
SELECT role FROM users WHERE email = 'votre@email.com';
-- Doit retourner 'ADMIN'
```

#### Problème 5 : CORS Error
**Symptôme** : "CORS policy" dans la console

**Solution** : Vérifier que le backend autorise `http://localhost:4200` (déjà configuré dans SecurityConfig)

#### Problème 6 : Mauvaise URL
**Symptôme** : 404 Not Found

**Solution** : Vérifier que l'URL est correcte :
- Frontend appelle : `http://localhost:8081/auth/users/role/TUTOR`
- Backend expose : `/auth/users/role/{role}` dans AdminUserController

### 6. Test Rapide avec Console du Navigateur

Ouvrez la console (F12) sur la page tutors et exécutez :

```javascript
// Test 1 : Vérifier le service
fetch('http://localhost:8081/auth/users/role/TUTOR')
  .then(r => r.json())
  .then(data => console.log('Tutors:', data))
  .catch(err => console.error('Error:', err));

// Test 2 : Vérifier tous les users
fetch('http://localhost:8081/auth/users')
  .then(r => r.json())
  .then(data => console.log('All users:', data))
  .catch(err => console.error('Error:', err));
```

### 7. Vérifier les Logs Backend

Dans le terminal où tourne le backend, cherchez :
- Les requêtes entrantes
- Les erreurs SQL
- Les exceptions

### 8. Solution Temporaire : Mock Data

Si vous voulez tester l'interface en attendant, ajoutez des données de test dans `tutors.component.ts` :

```typescript
loadTutors(): void {
  this.loading = true;
  this.userService.getUsersByRole('TUTOR').subscribe({
    next: (data) => {
      console.log('Tutors received:', data); // DEBUG
      this.users = data;
      this.applyFilters();
      this.loading = false;
    },
    error: (error) => {
      console.error('Error loading tutors:', error);
      
      // MOCK DATA pour test
      this.users = [
        {
          id: 1,
          email: 'tutor1@test.com',
          firstName: 'John',
          lastName: 'Doe',
          role: 'TUTOR',
          isActive: true,
          registrationFeePaid: true,
          phone: '+216 20 123 456',
          cin: 'AB123456',
          yearsOfExperience: 5,
          bio: 'Experienced English tutor'
        }
      ];
      this.applyFilters();
      this.loading = false;
    }
  });
}
```

## Checklist de Debug

- [ ] Backend démarré sur port 8081
- [ ] Base de données accessible
- [ ] Au moins 1 tutor existe dans la DB
- [ ] Pas d'erreur dans la console navigateur
- [ ] Requête HTTP visible dans Network tab
- [ ] Status HTTP = 200
- [ ] Réponse contient des données
- [ ] Connecté en tant qu'ADMIN (si nécessaire)

## Commandes Utiles

```bash
# Démarrer le backend
cd backend/auth-service
./mvnw spring-boot:run

# Vérifier les ports utilisés
lsof -i :8081
lsof -i :4200

# Tester l'API
curl http://localhost:8081/auth/users/role/TUTOR

# Voir les logs en temps réel
tail -f backend/auth-service/logs/application.log
```

## Contact

Si le problème persiste après ces vérifications, fournissez :
1. Le message d'erreur exact de la console
2. Le status HTTP de la requête
3. Le résultat de la requête SQL
4. Les logs du backend
