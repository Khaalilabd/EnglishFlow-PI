# Fonctionnalité Task (Todo Liste) pour les Clubs

## Vue d'ensemble
Cette fonctionnalité permet d'ajouter des tâches à chaque club avec 3 statuts :
- **TODO** (À faire) - Tâches non commencées
- **IN_PROGRESS** (En cours) - Tâches en cours de réalisation
- **DONE** (Terminé) - Tâches complétées

## Installation Backend

### 1. Créer la table dans PostgreSQL

Exécutez le script SQL dans pgAdmin sur la base de données `club_db` :

```sql
-- Ouvrez le fichier: create-tasks-table.sql
-- Ou copiez-collez cette commande dans pgAdmin Query Tool
```

Ou utilisez psql :
```bash
psql -h localhost -p 5432 -U postgres -d club_db -f create-tasks-table.sql
```

### 2. Redémarrer le service club-service

Après avoir créé la table, redémarrez le service :
```powershell
# Arrêtez le service actuel (Ctrl+C dans la fenêtre PowerShell)
# Puis redémarrez
cd backend/club-service
mvn spring-boot:run
```

## Fichiers créés

### Backend
- `enums/TaskStatus.java` - Enum pour les statuts
- `entity/Task.java` - Entité JPA
- `dto/TaskDTO.java` - Data Transfer Object
- `repository/TaskRepository.java` - Repository JPA
- `service/TaskService.java` - Logique métier
- `controller/TaskController.java` - API REST endpoints

### Frontend
- `models/task.model.ts` - Modèle TypeScript
- `services/task.service.ts` - Service HTTP

## API Endpoints

### GET /api/tasks/club/{clubId}
Récupère toutes les tâches d'un club

### GET /api/tasks/club/{clubId}/status/{status}
Récupère les tâches d'un club par statut

### POST /api/tasks
Crée une nouvelle tâche
```json
{
  "text": "Préparer la présentation",
  "status": "TODO",
  "clubId": 1,
  "createdBy": 123
}
```

### PUT /api/tasks/{id}
Met à jour une tâche
```json
{
  "text": "Préparer la présentation (modifié)",
  "status": "IN_PROGRESS"
}
```

### DELETE /api/tasks/{id}
Supprime une tâche

### GET /api/tasks/club/{clubId}/count/{status}
Compte les tâches par statut

## Utilisation Frontend

Le composant `clubs.component.ts` a été mis à jour pour utiliser localStorage temporairement.
Pour utiliser le backend, remplacez les méthodes par des appels au `TaskService`.

## Prochaines étapes

1. Exécuter le script SQL dans pgAdmin
2. Redémarrer le service club-service
3. Tester les endpoints avec Postman ou directement depuis l'interface
4. Intégrer le TaskService dans le composant clubs (optionnel)

## Notes
- Les tâches sont liées à un club via `club_id`
- La suppression d'un club supprime automatiquement ses tâches (CASCADE)
- Les index sont créés pour optimiser les performances
