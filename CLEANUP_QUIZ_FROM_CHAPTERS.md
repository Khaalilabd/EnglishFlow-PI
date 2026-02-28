# Cleanup: Remove Quiz from Chapters

## Summary

Removed all quiz-related code from chapters since quizzes are now assigned to lessons instead.

## Changes Made

### Frontend

#### 1. Chapter Model (`frontend/src/app/core/models/chapter.model.ts`)
- ✅ Removed `quizId?: number` from `Chapter` interface
- ✅ Removed `quizId?: number` from `CreateChapterRequest` interface
- ✅ Removed `quizId?: number` from `UpdateChapterRequest` interface

#### 2. Chapter Management Component (`frontend/src/app/pages/tutor-panel/chapter-management/`)

**TypeScript:**
- ✅ Removed `quizzes: Quiz[] = []` property
- ✅ Removed `QuizService` import and injection
- ✅ Removed `loadQuizzes()` method
- ✅ Removed `getQuizTitle()` helper method
- ✅ Removed `quizId` from `openEditModal()`
- ✅ Removed `quizId` from `updateChapter()`
- ✅ Removed `quizId` from `togglePublish()`
- ✅ Removed call to `loadQuizzes()` in `ngOnInit()`

**HTML:**
- ✅ Removed quiz indicator from chapter list view

### Backend

#### 1. Chapter Entity (`backend/courses-service/src/main/java/com/englishflow/courses/entity/Chapter.java`)
- ✅ Removed `quizId` field

#### 2. ChapterDTO (`backend/courses-service/src/main/java/com/englishflow/courses/dto/ChapterDTO.java`)
- ✅ Removed `quizId` field

#### 3. ChapterService (`backend/courses-service/src/main/java/com/englishflow/courses/service/ChapterService.java`)
- ✅ Removed `quizId` from `mapToDTO()`
- ✅ Removed `quizId` from `mapToEntity()`
- ✅ Removed `quizId` from `updateChapter()`

### Database

**SQL Migration:** `ADD_QUIZ_TO_LESSON.sql`
```sql
-- Add quiz_id to lessons
ALTER TABLE lessons ADD COLUMN IF NOT EXISTS quiz_id BIGINT;

-- Remove quiz_id from chapters
ALTER TABLE chapters DROP COLUMN IF EXISTS quiz_id;
```

## Complete SQL to Run

```sql
-- Step 1: Add quiz_id to lessons table
ALTER TABLE lessons ADD COLUMN IF NOT EXISTS quiz_id BIGINT;
COMMENT ON COLUMN lessons.quiz_id IS 'Quiz ID for lessons of type QUIZ - links to quiz in learning service';

-- Step 2: Remove quiz_id from chapters table
ALTER TABLE chapters DROP COLUMN IF EXISTS quiz_id;

-- Step 3: Verify the changes
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'lessons' AND column_name = 'quiz_id';

SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'chapters' AND column_name = 'quiz_id';
```

The second query should return no results (confirming quiz_id is removed from chapters).

## Files Modified

### Backend
- `backend/courses-service/src/main/java/com/englishflow/courses/entity/Chapter.java`
- `backend/courses-service/src/main/java/com/englishflow/courses/dto/ChapterDTO.java`
- `backend/courses-service/src/main/java/com/englishflow/courses/service/ChapterService.java`

### Frontend
- `frontend/src/app/core/models/chapter.model.ts`
- `frontend/src/app/pages/tutor-panel/chapter-management/chapter-management.component.ts`
- `frontend/src/app/pages/tutor-panel/chapter-management/chapter-management.component.html`

### Database
- `ADD_QUIZ_TO_LESSON.sql` (updated to include DROP COLUMN)

## Testing

After running the SQL and restarting services:

1. ✅ Chapter creation should work without quiz field
2. ✅ Chapter editing should work without quiz field
3. ✅ Chapter list should not show quiz indicator
4. ✅ Lesson creation with type QUIZ should show quiz dropdown
5. ✅ No errors in browser console or backend logs

## Status

✅ All quiz-related code removed from chapters
✅ All code compiles without errors
✅ Database migration ready to run
✅ Clean separation: Chapters manage structure, Lessons manage content (including quizzes)
