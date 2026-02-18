# Système de Rôles et Permissions - Club Service

## Rôles des Membres

### PRESIDENT
- Créé automatiquement lors de la création du club
- Le créateur du club devient automatiquement président
- Un seul président par club
- Ne peut pas être supprimé du club
- Son rôle ne peut pas être changé

### SECRETARY
- Rôle intermédiaire (peut être ajouté par le président)
- Membre avec des responsabilités spéciales

### MEMBER
- Rôle par défaut lors du join d'un club
- Membre standard du club

## Permissions

### Création de Club
```
POST /api/clubs
```
- Le créateur (createdBy) est automatiquement ajouté comme PRESIDENT dans la table members

### Gestion des Membres (Réservé au PRESIDENT)

#### Changer le rôle d'un membre
```
PUT /api/members/{memberId}/rank?rank=SECRETARY&requesterId={userId}
```
- Seul le président peut changer les rôles
- Le rôle du président ne peut pas être changé
- Paramètres:
  - `memberId`: ID du membre à modifier
  - `rank`: Nouveau rôle (MEMBER, SECRETARY)
  - `requesterId`: ID de l'utilisateur qui fait la demande (doit être président)

#### Supprimer un membre
```
DELETE /api/members/{memberId}?requesterId={userId}
```
- Seul le président peut supprimer des membres
- Le président ne peut pas être supprimé
- Paramètres:
  - `memberId`: ID du membre à supprimer
  - `requesterId`: ID de l'utilisateur qui fait la demande (doit être président)

### Gestion des Tâches (Todo Liste)

#### Voir les tâches
```
GET /api/tasks/club/{clubId}?userId={userId}
```
- Tous les membres du club peuvent voir les tâches
- Seuls les membres peuvent accéder aux tâches

#### Créer une tâche
```
POST /api/tasks?userId={userId}
Body: {
  "text": "Description de la tâche",
  "status": "TODO",
  "clubId": 1,
  "createdBy": 123
}
```
- Seul le président peut créer des tâches

#### Modifier une tâche
```
PUT /api/tasks/{id}?userId={userId}
Body: {
  "text": "Nouvelle description",
  "status": "IN_PROGRESS"
}
```
- Seul le président peut modifier des tâches

#### Supprimer une tâche
```
DELETE /api/tasks/{id}?userId={userId}
```
- Seul le président peut supprimer des tâches

### Vérification des Permissions

#### Vérifier si un utilisateur est président
```
GET /api/members/club/{clubId}/user/{userId}/is-president
```
Retourne: `true` ou `false`

#### Vérifier si un utilisateur est membre
```
GET /api/members/club/{clubId}/user/{userId}/is-member
```
Retourne: `true` ou `false`

## Workflow Complet

### 1. Création d'un Club
1. Un étudiant crée un club via `POST /api/clubs`
2. Le système crée automatiquement une entrée dans la table `members` avec:
   - `userId`: ID du créateur
   - `clubId`: ID du club créé
   - `rank`: PRESIDENT
   - `joinedAt`: Date actuelle

### 2. Rejoindre un Club
1. Un étudiant rejoint un club via `POST /api/members/club/{clubId}/user/{userId}`
2. Le système crée une entrée dans la table `members` avec:
   - `userId`: ID de l'étudiant
   - `clubId`: ID du club
   - `rank`: MEMBER (par défaut)
   - `joinedAt`: Date actuelle

### 3. Afficher les Clubs avec Rôles
Pour afficher tous les clubs qu'un étudiant a rejoint avec son rôle:
```
GET /api/clubs/user/{userId}/with-role
```

Retourne une liste de clubs avec les informations suivantes:
```json
[
  {
    "id": 1,
    "name": "English Conversation Club",
    "description": "Practice English speaking",
    "objective": "Improve fluency",
    "category": "LANGUAGE",
    "maxMembers": 20,
    "image": "base64...",
    "status": "APPROVED",
    "createdBy": 123,
    "reviewedBy": 456,
    "reviewComment": "Approved",
    "createdAt": "2024-01-15T10:00:00",
    "updatedAt": "2024-01-15T10:00:00",
    "userRole": "PRESIDENT",
    "joinedAt": "2024-01-15T10:00:00"
  },
  {
    "id": 2,
    "name": "Reading Club",
    "description": "Read and discuss books",
    "objective": "Improve reading skills",
    "category": "READING",
    "maxMembers": 15,
    "image": "base64...",
    "status": "APPROVED",
    "createdBy": 789,
    "reviewedBy": 456,
    "reviewComment": "Approved",
    "createdAt": "2024-01-20T14:00:00",
    "updatedAt": "2024-01-20T14:00:00",
    "userRole": "MEMBER",
    "joinedAt": "2024-01-22T09:30:00"
  }
]
```

Les champs `userRole` et `joinedAt` indiquent:
- `userRole`: Le rôle de l'utilisateur dans ce club (PRESIDENT, SECRETARY, ou MEMBER)
- `joinedAt`: La date à laquelle l'utilisateur a rejoint le club

### 4. Gestion par le Président
Le président peut:
- Promouvoir un membre en secrétaire
- Rétrograder un secrétaire en membre
- Supprimer des membres du club
- Créer, modifier et supprimer des tâches
- Voir toutes les tâches du club

### 5. Membres Standards
Les membres peuvent:
- Voir les tâches du club
- Participer aux activités du club

## Messages d'Erreur

- `"Only the president can change member ranks"` - Tentative de changement de rôle par un non-président
- `"Cannot change the president's rank"` - Tentative de changement du rôle du président
- `"Only the president can remove members"` - Tentative de suppression par un non-président
- `"Cannot remove the president from the club"` - Tentative de suppression du président
- `"Only the president can create tasks"` - Tentative de création de tâche par un non-président
- `"Only the president can update tasks"` - Tentative de modification de tâche par un non-président
- `"Only the president can delete tasks"` - Tentative de suppression de tâche par un non-président
- `"Only club members can view tasks"` - Tentative d'accès aux tâches par un non-membre
