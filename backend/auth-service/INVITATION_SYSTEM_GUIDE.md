# 📧 Guide du Système d'Invitation

## Vue d'ensemble

Le système d'invitation remplace la création manuelle de comptes pour les rôles **TUTOR** et **ACADEMIC_OFFICE_AFFAIR**. Au lieu de créer un compte avec un mot de passe généré, l'admin envoie une invitation par email, et l'utilisateur crée son propre compte.

---

## 🎯 Avantages

### Avant (Création Manuelle)
- ❌ Admin remplit 15+ champs
- ❌ Mot de passe généré aléatoirement
- ❌ Mot de passe envoyé par email en clair
- ❌ Admin voit le mot de passe
- ❌ Tutor doit changer le mot de passe après
- ❌ Non scalable (10 tutors = 2 heures)

### Après (Système d'Invitation)
- ✅ Admin entre uniquement email + rôle
- ✅ Tutor choisit son propre mot de passe
- ✅ Aucun mot de passe en clair
- ✅ Admin ne voit jamais le mot de passe
- ✅ Compte activé immédiatement
- ✅ Scalable (100 tutors = 5 minutes)

---

## 🔄 Flux Complet

```
┌─────────────┐
│   ADMIN     │
│ Dashboard   │
└──────┬──────┘
       │
       │ 1. Clique "Invite Tutor"
       │    Entre: email + role
       ▼
┌─────────────────┐
│   Backend       │
│ POST /invitations/send
│ - Crée invitation
│ - Génère token UUID
│ - Expiry: 7 jours
└──────┬──────────┘
       │
       │ 2. Envoie email
       ▼
┌─────────────────┐
│   Email         │
│ "You're invited!"
│ [Accept Button] │
└──────┬──────────┘
       │
       │ 3. Tutor clique lien
       │    /accept-invitation?token=xxx
       ▼
┌─────────────────┐
│   Frontend      │
│ Formulaire:     │
│ - First Name    │
│ - Last Name     │
│ - Password      │
│ - Phone, CIN... │
└──────┬──────────┘
       │
       │ 4. Soumet formulaire
       │    POST /invitations/accept
       ▼
┌─────────────────┐
│   Backend       │
│ - Valide token  │
│ - Crée User     │
│ - Active compte │
│ - Marque used   │
│ - Retourne JWT  │
└──────┬──────────┘
       │
       │ 5. Redirection
       ▼
┌─────────────────┐
│   Login/Panel   │
│ Connecté auto   │
└─────────────────┘
```

---

## 🛠️ API Endpoints

### 1. Envoyer une Invitation

**Endpoint:** `POST /invitations/send`

**Headers:**
```
Authorization: Bearer {admin_jwt_token}
Content-Type: application/json
```

**Body:**
```json
{
  "email": "tutor@example.com",
  "role": "TUTOR"
}
```

**Rôles acceptés:**
- `TUTOR`
- `ACADEMIC_OFFICE_AFFAIR`

**Réponse (201 Created):**
```json
{
  "id": 1,
  "email": "tutor@example.com",
  "role": "TUTOR",
  "expiryDate": "2026-02-26T10:30:00",
  "used": false,
  "invitedBy": 1,
  "createdAt": "2026-02-19T10:30:00",
  "usedAt": null
}
```

**Erreurs possibles:**
- `400` - Email déjà utilisé
- `400` - Invitation déjà envoyée à cet email
- `400` - Rôle invalide (seuls TUTOR et ACADEMIC_OFFICE_AFFAIR)

---

### 2. Vérifier une Invitation

**Endpoint:** `GET /invitations/token/{token}`

**Exemple:**
```
GET /invitations/token/a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

**Réponse (200 OK):**
```json
{
  "id": 1,
  "email": "tutor@example.com",
  "role": "TUTOR",
  "expiryDate": "2026-02-26T10:30:00",
  "used": false,
  "invitedBy": 1,
  "createdAt": "2026-02-19T10:30:00",
  "usedAt": null
}
```

**Erreurs possibles:**
- `404` - Token invalide
- `400` - Invitation déjà utilisée
- `400` - Invitation expirée

---

### 3. Accepter une Invitation

**Endpoint:** `POST /invitations/accept`

**Body:**
```json
{
  "token": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "firstName": "John",
  "lastName": "Doe",
  "password": "SecurePassword123!",
  "phone": "0612345678",
  "cin": "AB123456",
  "dateOfBirth": "1990-01-15",
  "address": "123 Main St",
  "city": "Casablanca",
  "postalCode": "20000",
  "bio": "Experienced English tutor with 5 years...",
  "yearsOfExperience": 5
}
```

**Champs obligatoires:**
- `token` ✅
- `firstName` ✅
- `lastName` ✅
- `password` ✅ (minimum 8 caractères)

**Champs optionnels:**
- `phone`, `cin`, `dateOfBirth`, `address`, `city`, `postalCode`, `bio`, `yearsOfExperience`

**Réponse (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 42,
  "email": "tutor@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "TUTOR",
  "profilePhoto": null,
  "phone": "0612345678",
  "profileCompleted": true
}
```

**Erreurs possibles:**
- `400` - Token invalide/expiré/utilisé
- `400` - Email déjà utilisé
- `400` - Validation échouée (password trop court, etc.)

---

### 4. Lister les Invitations en Attente

**Endpoint:** `GET /invitations/pending`

**Headers:**
```
Authorization: Bearer {admin_jwt_token}
```

**Réponse (200 OK):**
```json
[
  {
    "id": 1,
    "email": "tutor1@example.com",
    "role": "TUTOR",
    "expiryDate": "2026-02-26T10:30:00",
    "used": false,
    "invitedBy": 1,
    "createdAt": "2026-02-19T10:30:00",
    "usedAt": null
  },
  {
    "id": 2,
    "email": "academic@example.com",
    "role": "ACADEMIC_OFFICE_AFFAIR",
    "expiryDate": "2026-02-25T14:20:00",
    "used": false,
    "invitedBy": 1,
    "createdAt": "2026-02-18T14:20:00",
    "usedAt": null
  }
]
```

---

### 5. Renvoyer une Invitation

**Endpoint:** `POST /invitations/{id}/resend`

**Headers:**
```
Authorization: Bearer {admin_jwt_token}
```

**Exemple:**
```
POST /invitations/1/resend
```

**Réponse (200 OK):**
```json
{
  "id": 1,
  "email": "tutor@example.com",
  "role": "TUTOR",
  "expiryDate": "2026-02-26T15:45:00",
  "used": false,
  "invitedBy": 1,
  "createdAt": "2026-02-19T10:30:00",
  "usedAt": null
}
```

**Comportement:**
- Prolonge l'expiration de 7 jours supplémentaires
- Renvoie l'email d'invitation
- Ne peut pas renvoyer une invitation déjà utilisée

---

### 6. Annuler une Invitation

**Endpoint:** `DELETE /invitations/{id}`

**Headers:**
```
Authorization: Bearer {admin_jwt_token}
```

**Exemple:**
```
DELETE /invitations/1
```

**Réponse (200 OK):**
```json
{
  "message": "Invitation cancelled successfully"
}
```

**Erreurs possibles:**
- `404` - Invitation non trouvée
- `400` - Impossible d'annuler une invitation déjà utilisée

---

### 7. Nettoyer les Invitations Expirées

**Endpoint:** `POST /invitations/cleanup`

**Headers:**
```
Authorization: Bearer {admin_jwt_token}
```

**Réponse (200 OK):**
```json
{
  "message": "Expired invitations cleaned up successfully"
}
```

**Comportement:**
- Supprime toutes les invitations expirées et non utilisées
- Peut être appelé manuellement ou via un cron job

---

## 📧 Template Email

L'email d'invitation contient:

- **Sujet:** "You're Invited to Join Jungle in English! 🎉"
- **Contenu:**
  - Message de bienvenue
  - Rôle assigné (TUTOR ou ACADEMIC_OFFICE_AFFAIR)
  - Bouton "Accept Invitation"
  - Lien direct vers `/accept-invitation?token=xxx`
  - Note d'expiration (7 jours)
  - Lien de secours si le bouton ne fonctionne pas

**Template:** `backend/auth-service/src/main/resources/templates/invitation-email.html`

---

## 🔐 Sécurité

### Token
- UUID v4 aléatoire (128 bits d'entropie)
- Unique dans la base de données
- Expire après 7 jours
- Usage unique (marqué `used=true` après acceptation)

### Validation
- Email vérifié (format + unicité)
- Rôle validé (uniquement TUTOR et ACADEMIC_OFFICE_AFFAIR)
- Password minimum 8 caractères
- Token vérifié à chaque étape

### Rate Limiting
- Pas de rate limiting sur `/invitations/accept` (token unique)
- Rate limiting sur `/invitations/send` recommandé (TODO)

---

## 🧪 Tests

### Test Manuel avec cURL

```bash
# 1. Envoyer invitation
curl -X POST http://localhost:8081/invitations/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -d '{
    "email": "test.tutor@example.com",
    "role": "TUTOR"
  }'

# 2. Vérifier email (copier le token)

# 3. Vérifier invitation
curl http://localhost:8081/invitations/token/TOKEN_FROM_EMAIL

# 4. Accepter invitation
curl -X POST http://localhost:8081/invitations/accept \
  -H "Content-Type: application/json" \
  -d '{
    "token": "TOKEN_FROM_EMAIL",
    "firstName": "Test",
    "lastName": "Tutor",
    "password": "SecurePass123!",
    "phone": "0612345678",
    "cin": "AB123456"
  }'

# 5. Login avec le nouveau compte
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test.tutor@example.com",
    "password": "SecurePass123!",
    "recaptchaToken": "test"
  }'
```

---

## 🎨 Frontend à Implémenter

### 1. Page Admin - Invitations

**Route:** `/dashboard/invitations`

**Composants:**
- Bouton "Invite Tutor"
- Bouton "Invite Academic Affairs"
- Tableau des invitations en attente
- Actions: Resend, Cancel
- Filtres: All, Pending, Expired, Used

### 2. Page Acceptation

**Route:** `/accept-invitation`

**Query Params:** `?token=xxx`

**Composants:**
- Formulaire multi-step (comme create-tutor actuel)
- Step 1: Informations personnelles (firstName, lastName, password)
- Step 2: Contact (phone, address, city)
- Step 3: Professionnel (bio, yearsOfExperience)
- Validation en temps réel
- Message d'erreur si token invalide/expiré

### 3. Remplacer Composants Existants

**À modifier:**
- `create-tutor.component.ts` → Utiliser système d'invitation
- `academic-affairs.component.ts` → Utiliser système d'invitation

**À supprimer:**
- `password-modal.component.ts` (plus nécessaire)

---

## 📊 Monitoring

### Métriques à Suivre

- Nombre d'invitations envoyées par jour
- Taux d'acceptation (invitations acceptées / envoyées)
- Temps moyen entre envoi et acceptation
- Nombre d'invitations expirées
- Nombre d'invitations renvoyées

### Logs

```
✅ Invitation email sent to: tutor@example.com
✅ Invitation accepted by: tutor@example.com
⚠️ Invitation expired for: old@example.com
🔄 Invitation resent to: tutor@example.com
```

---

## 🐛 Troubleshooting

### Problème: Email non reçu

**Solutions:**
1. Vérifier configuration SMTP dans `application.yml`
2. Vérifier spam/junk folder
3. Vérifier logs backend: `Failed to send invitation email`
4. Utiliser endpoint `/invitations/{id}/resend`

### Problème: Token invalide

**Causes possibles:**
- Token expiré (> 7 jours)
- Token déjà utilisé
- Token incorrect (copié partiellement)

**Solution:**
- Demander à l'admin de renvoyer l'invitation

### Problème: Email déjà utilisé

**Cause:**
- Un compte existe déjà avec cet email

**Solution:**
- Utiliser un autre email
- Ou supprimer le compte existant (si test)

---

## 🔄 Migration depuis l'Ancien Système

### Comptes Existants

Les comptes créés avec l'ancien système (création manuelle) continuent de fonctionner normalement. Aucune migration nécessaire.

### Transition

1. **Phase 1 (Actuelle):** Les deux systèmes coexistent
2. **Phase 2:** Désactiver création manuelle dans le frontend
3. **Phase 3:** Supprimer endpoints de création manuelle

---

## 📝 TODO

- [ ] Ajouter rate limiting sur `/invitations/send` (max 10/heure par admin)
- [ ] Créer cron job pour cleanup automatique des invitations expirées
- [ ] Ajouter notification admin quand invitation acceptée
- [ ] Ajouter statistiques d'invitations dans dashboard admin
- [ ] Permettre personnalisation du message d'invitation
- [ ] Ajouter support multi-langue pour emails

---

**Version:** 1.0.0  
**Date:** 2026-02-19  
**Auteur:** EnglishFlow Team
