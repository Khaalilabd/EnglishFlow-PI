# Installation de la fonctionnalité Tasks

## Étape 1 : Créer la table dans PostgreSQL

Ouvrez pgAdmin et exécutez le script SQL sur la base de données `club_db` :

```sql
-- Copiez le contenu du fichier: backend/club-service/create-tasks-table.sql
-- Ou exécutez directement :

CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    club_id INTEGER NOT NULL,
    created_by INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_club FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE,
    CONSTRAINT chk_task_status CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE'))
);

CREATE INDEX idx_tasks_club_id ON tasks(club_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_club_status ON tasks(club_id, status);
```

## Étape 2 : Redémarrer le service club-service

1. Arrêtez le service actuel (Ctrl+C dans la fenêtre PowerShell du club-service)
2. Redémarrez-le :
   ```powershell
   cd backend/club-service
   mvn spring-boot:run
   ```

## Étape 3 : Tester

1. Ouvrez l'application frontend (http://localhost:4200)
2. Connectez-vous
3. Allez dans "My Clubs"
4. Cliquez sur "View Details" d'un club
5. Scrollez jusqu'à la section "Club Tasks"
6. Ajoutez une tâche et changez son statut !

## Fonctionnalités disponibles

✅ Créer une tâche
✅ Changer le statut (À faire → En cours → Terminé)
✅ Supprimer une tâche
✅ Compteurs par statut
✅ Persistance en base de données
✅ Chaque club a ses propres tâches

## Statuts disponibles

- **À faire** (TODO) - Tâches non commencées
- **En cours** (IN_PROGRESS) - Tâches en cours
- **Terminé** (DONE) - Tâches complétées

## API Endpoints créés

- `GET /api/tasks/club/{clubId}` - Récupérer toutes les tâches d'un club
- `POST /api/tasks` - Créer une tâche
- `PUT /api/tasks/{id}` - Mettre à jour une tâche
- `DELETE /api/tasks/{id}` - Supprimer une tâche
- `GET /api/tasks/club/{clubId}/status/{status}` - Filtrer par statut
- `GET /api/tasks/club/{clubId}/count/{status}` - Compter par statut
