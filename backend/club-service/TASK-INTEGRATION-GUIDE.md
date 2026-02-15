# Task Feature Integration Guide

## Status: Ready for Deployment

The task management feature has been fully implemented in both backend and frontend. Follow these steps to complete the integration.

## What's Been Done

### Backend (✅ Complete)
- Created `TaskStatus` enum (TODO, IN_PROGRESS, DONE)
- Created `Task` entity with relationship to Club
- Created `TaskDTO` for data transfer
- Created `TaskRepository` with custom queries
- Created `TaskService` with full CRUD operations
- Created `TaskController` with REST endpoints
- Created SQL migration script

### Frontend (✅ Complete)
- Created `task.model.ts` with TypeScript interfaces
- Created `task.service.ts` with API integration
- Updated `clubs.component.ts` with task management methods
- Updated `clubs.component.html` with task UI in details modal
- Added status selector (À faire, En cours, Terminé)
- Added status badges with color coding
- Added task counters by status

## Deployment Steps

### Step 1: Execute SQL Migration

Open pgAdmin and connect to your `club_db` database, then execute:

```sql
-- Create tasks table
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

-- Create indexes
CREATE INDEX idx_tasks_club_id ON tasks(club_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_club_status ON tasks(club_id, status);
```

Or simply run the file: `EnglishFlow-PI/backend/club-service/create-tasks-table.sql`

### Step 2: Restart Backend Services

Stop and restart the club-service to load the new Task entity and endpoints:

```powershell
# From EnglishFlow-PI directory
.\stop-all-services.ps1
.\start-all-services.ps1
```

Or restart just the club-service:
```powershell
cd backend/club-service
mvn spring-boot:run
```

### Step 3: Verify Backend

Check that the Task endpoints are available:
- GET http://localhost:8080/api/tasks/club/{clubId}
- POST http://localhost:8080/api/tasks
- PUT http://localhost:8080/api/tasks/{id}
- DELETE http://localhost:8080/api/tasks/{id}

### Step 4: Test Frontend

1. Start the frontend (if not already running):
   ```powershell
   cd frontend
   npm start
   ```

2. Navigate to the student dashboard
3. Open any club's details modal
4. Test the task features:
   - Add a new task
   - Change task status using the dropdown
   - Delete a task
   - Verify status counters update correctly

## API Endpoints

### Task Endpoints
- `GET /api/tasks/club/{clubId}` - Get all tasks for a club
- `GET /api/tasks/club/{clubId}/status/{status}` - Get tasks by status
- `GET /api/tasks/{id}` - Get task by ID
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `GET /api/tasks/club/{clubId}/count/{status}` - Count tasks by status

### Request/Response Examples

**Create Task:**
```json
POST /api/tasks
{
  "text": "Prepare presentation",
  "status": "TODO",
  "clubId": 1,
  "createdBy": 123
}
```

**Update Task Status:**
```json
PUT /api/tasks/1
{
  "status": "IN_PROGRESS"
}
```

## Features

### Task Management
- ✅ Add tasks to any club
- ✅ Update task status (À faire, En cours, Terminé)
- ✅ Delete tasks
- ✅ View task counts by status
- ✅ Color-coded status badges
- ✅ Persistent storage in PostgreSQL

### UI Features
- Status dropdown selector with French labels
- Real-time status counters
- Smooth animations and transitions
- Responsive design
- Empty state messaging

## Troubleshooting

### Backend Issues

**Error: Table 'tasks' doesn't exist**
- Solution: Execute the SQL migration script in pgAdmin

**Error: 404 on /api/tasks endpoints**
- Solution: Restart the club-service to load the new controller

**Error: Foreign key constraint fails**
- Solution: Ensure the clubs table exists and has data

### Frontend Issues

**Error: Cannot find module 'environment'**
- Solution: Already fixed - using hardcoded URL like other services

**Tasks not loading**
- Check browser console for API errors
- Verify backend is running on http://localhost:8080
- Check that SQL migration was executed

**Status not updating**
- Verify the PUT endpoint is working
- Check network tab for API response
- Ensure task ID is being passed correctly

## Files Modified/Created

### Backend
- `src/main/java/com/englishflow/club/entity/Task.java`
- `src/main/java/com/englishflow/club/dto/TaskDTO.java`
- `src/main/java/com/englishflow/club/repository/TaskRepository.java`
- `src/main/java/com/englishflow/club/service/TaskService.java`
- `src/main/java/com/englishflow/club/controller/TaskController.java`
- `src/main/java/com/englishflow/club/enums/TaskStatus.java`
- `create-tasks-table.sql`

### Frontend
- `src/app/core/models/task.model.ts`
- `src/app/core/services/task.service.ts`
- `src/app/pages/student-panel/clubs/clubs.component.ts`
- `src/app/pages/student-panel/clubs/clubs.component.html`

## Next Steps

After successful deployment:
1. Test with multiple clubs
2. Test with multiple users
3. Verify task persistence across sessions
4. Consider adding task assignment to specific members
5. Consider adding task due dates
6. Consider adding task priorities
