# Complaint Handling Center - Guide de Transformation

## Vue d'ensemble

Le module Complaints a été transformé en un véritable **Complaint Handling Center** pour la gestion académique des réclamations étudiantes.

## Modifications Principales

### 1. Interface en Cartes (Cards)

**Avant** : Tableau classique avec lignes
**Après** : Cartes visuelles avec informations clés

Chaque carte affiche :
- Nom et email de l'étudiant
- Sujet et description (tronquée)
- Catégorie (PEDAGOGICAL, TUTOR_BEHAVIOR, TECHNICAL, ADMINISTRATIVE)
- Priorité avec badge coloré
- Statut actuel
- Date de soumission
- **Badge "Severity Indicator"** : Indicateur visuel de gravité
  - LOW → Gris
  - MEDIUM → Jaune
  - HIGH → Orange
  - URGENT → Rouge
- **Badge "Days Open"** : Temps d'attente
  - < 2 jours → Bleu "X days waiting"
  - 2-4 jours → Orange "X days - Delayed"
  - ≥ 5 jours → Rouge "X days - Overdue"
- Score de risque

### 2. Page de Détail Complète

**Route** : `/dashboard/complaints/:id`

#### A) Complaint Information
- Subject, description complète
- Informations étudiant
- Date, catégorie, priorité, statut
- Badges de sévérité et délai

#### B) Complaint Conversation
- Système de messages type chat
- Admin et étudiant peuvent échanger
- Chaque message affiche :
  - Auteur et rôle
  - Date/heure
  - Contenu
- Input pour nouveau message

#### C) Complaint History (Timeline)
- Timeline verticale des actions
- Affiche :
  - Submitted
  - Assigned to tutor
  - Tutor responded
  - Admin intervened
  - Marked resolved
- Format chronologique avec icônes

### 3. Système d'Actions "Take Action"

Remplace les simples icônes par un modal d'actions :

**Actions disponibles** :
1. **Request More Information** : Demander des détails à l'étudiant
2. **Forward to Tutor** : Transférer au tuteur responsable
3. **Escalate Priority** : Augmenter l'urgence
4. **Mark as In Progress** : Marquer en cours de traitement
5. **Resolve Complaint** : Résoudre (passe en PENDING_STUDENT_CONFIRMATION)
6. **Reject Complaint** : Rejeter avec motif obligatoire

Chaque action enregistre une entrée dans l'historique.

### 4. Système d'Escalade Automatique

**Comportement visuel** :
- Complaint OPEN > 2 jours → Badge orange "Delayed"
- Complaint OPEN > 5 jours → Badge rouge "Overdue"
- Les complaints overdue remontent automatiquement en haut de la liste

**Tri automatique** :
1. Overdue en premier
2. Puis par nombre de jours d'attente (décroissant)

### 5. Confirmation Étudiant

**Nouveau statut** : `PENDING_STUDENT_CONFIRMATION`

Quand l'admin clique "Resolve Complaint" :
- Statut → PENDING_STUDENT_CONFIRMATION
- L'étudiant reçoit : "Is your problem solved?"
- Boutons côté étudiant :
  - YES → RESOLVED
  - NO → REOPENED

### 6. Filtres Intelligents

**Nouveaux filtres rapides** :
- **Waiting for Admin** : OPEN ou IN_PROGRESS
- **Waiting for Student** : PENDING_STUDENT_CONFIRMATION
- **Waiting for Tutor** : PEDAGOGICAL + OPEN
- **Overdue** : isOverdue = true
- **High Priority** : HIGH ou URGENT

### 7. Interface Tuteur

**Route** : `/tutor-panel/complaints/:id`

Le tuteur voit uniquement les complaints **PEDAGOGICAL**.

**Actions limitées** :
- Répondre (Send Solution)
- Marquer "Handled by Tutor"
- **NE PEUT PAS** clôturer la réclamation (réservé à l'admin)

### 8. Statistiques de Performance

**Nouveaux indicateurs dans le header** :
- **Average Resolution Time** : Temps moyen de résolution (en jours)
- **Number of Overdue Complaints** : Nombre de réclamations en retard
- **Resolution Rate %** : Taux de résolution

## Structure des Fichiers

```
frontend/src/app/pages/
├── dashboard/
│   └── complaints/
│       ├── complaints.component.ts          # Liste des complaints (cartes)
│       ├── complaints.component.html
│       ├── complaints.component.css
│       └── complaint-detail/
│           ├── complaint-detail.component.ts    # Page détail admin
│           ├── complaint-detail.component.html
│           └── complaint-detail.component.css
└── tutor-panel/
    └── complaints/
        ├── complaints.component.ts          # Liste tuteur (PEDAGOGICAL only)
        ├── complaints.component.html
        ├── complaints.component.css
        └── complaint-detail-tutor/
            ├── complaint-detail-tutor.component.ts  # Page détail tuteur
            ├── complaint-detail-tutor.component.html
            └── complaint-detail-tutor.component.css
```

## Routes Ajoutées

```typescript
// Admin
'/dashboard/complaints/:id' → ComplaintDetailComponent

// Tutor
'/tutor-panel/complaints/:id' → ComplaintDetailTutorComponent
```

## Nouveaux Statuts

- `OPEN` : Ouvert
- `IN_PROGRESS` : En cours de traitement
- `PENDING_STUDENT_CONFIRMATION` : En attente de confirmation étudiant
- `RESOLVED` : Résolu
- `REJECTED` : Rejeté

## Fonctionnalités à Implémenter (Backend)

### 1. Messages/Conversation
- Endpoint pour sauvegarder les messages
- Endpoint pour récupérer les messages d'une complaint
- Notification en temps réel (WebSocket optionnel)

### 2. Confirmation Étudiant
- Endpoint pour que l'étudiant confirme/rejette la résolution
- Notification email à l'étudiant quand statut = PENDING_STUDENT_CONFIRMATION

### 3. Statistiques
- Calcul du temps moyen de résolution
- Calcul du taux de résolution
- Endpoint pour récupérer ces métriques

### 4. Escalade Automatique
- Job/Scheduler pour vérifier les complaints > 2 jours et > 5 jours
- Mise à jour automatique des badges/flags

## Utilisation

### Pour l'Admin

1. Accéder à `/dashboard/complaints`
2. Voir les cartes de complaints avec indicateurs visuels
3. Utiliser les filtres intelligents pour trier
4. Cliquer sur une carte pour voir les détails
5. Utiliser "Take Action" pour gérer la complaint
6. Échanger avec l'étudiant via la conversation
7. Marquer comme résolu → étudiant doit confirmer

### Pour le Tuteur

1. Accéder à `/tutor-panel/complaints`
2. Voir uniquement les complaints PEDAGOGICAL
3. Cliquer sur une carte pour voir les détails
4. Répondre avec une solution
5. Marquer "Handled by Tutor" quand terminé
6. L'admin peut ensuite clôturer

### Pour l'Étudiant

1. Soumettre une complaint via `/user-panel/complaints`
2. Recevoir des messages de l'admin/tuteur
3. Quand résolu, recevoir demande de confirmation
4. Confirmer (YES) ou rejeter (NO) la résolution

## Améliorations Futures

- [ ] Système de notifications push
- [ ] Export des complaints en PDF/Excel
- [ ] Graphiques de tendances
- [ ] Templates de réponses rapides
- [ ] Assignation automatique aux tuteurs
- [ ] SLA (Service Level Agreement) tracking
- [ ] Satisfaction survey après résolution
