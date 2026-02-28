# Course Dropdown in Quiz Creation - Implementation

## What Was Added

Added a course dropdown in the quiz creation/edit form so tutors can select which course the quiz belongs to.

## Changes Made

### Frontend - Quiz Management Component

**File:** `frontend/src/app/pages/tutor-panel/quiz-management/quiz-management.component.ts`

1. **Added imports:**
   - `CourseService` from `'../../../core/services/course.service'`
   - `Course` model from `'../../../core/models/course.model'`

2. **Added property:**
   ```typescript
   courses: Course[] = [];
   ```

3. **Injected CourseService:**
   ```typescript
   constructor(
     private quizService: QuizService,
     private courseService: CourseService
   ) {}
   ```

4. **Added loadCourses method:**
   ```typescript
   loadCourses() {
     this.courseService.getAllCourses().subscribe({
       next: (courses) => {
         this.courses = courses;
       },
       error: (error) => {
         console.error('Error loading courses:', error);
       }
     });
   }
   ```

5. **Called loadCourses in ngOnInit:**
   ```typescript
   ngOnInit() {
     this.loadQuizzes();
     this.loadCourses();
   }
   ```

### Frontend - Quiz Management Template

**File:** `frontend/src/app/pages/tutor-panel/quiz-management/quiz-management.component.html`

Added course dropdown in Step 1 (Basic Info) of the wizard:

```html
<div>
  <label class="block text-gray-700 text-sm font-bold mb-2">Course (Optional)</label>
  <select 
    [(ngModel)]="newQuiz.courseId"
    class="w-full px-4 py-3 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-[#2D5F5D]">
    <option [ngValue]="undefined">No course assigned</option>
    <option *ngFor="let course of courses" [ngValue]="course.id">{{ course.title }}</option>
  </select>
  <p class="text-xs text-gray-500 mt-1">Select a course to link this quiz to specific course content</p>
</div>
```

## How It Works

1. **On Component Load:**
   - Fetches all available courses from the API
   - Populates the courses dropdown

2. **When Creating/Editing Quiz:**
   - Tutor sees a dropdown with all available courses
   - Can select a course or leave it as "No course assigned"
   - The selected `courseId` is saved with the quiz

3. **When Assigning Quiz to Chapter:**
   - The chapter management component filters quizzes by `courseId`
   - Only shows quizzes that belong to the current course
   - This ensures tutors only see relevant quizzes when assigning to chapters

## User Flow

1. **Create a Course** (if not already created)
   - Go to Course Management
   - Create a new course

2. **Create a Quiz**
   - Go to Quiz Management
   - Click "Create Quiz"
   - Fill in title and description
   - **Select the course from the dropdown** ← NEW FEATURE
   - Continue with settings and questions
   - Save the quiz

3. **Assign Quiz to Chapter**
   - Go to Course Management → Manage Content
   - Add or edit a chapter
   - In the "Assign Quiz" dropdown, you'll see only quizzes for this course
   - Select the quiz and save

## Testing

1. Restart frontend: `ng serve`
2. Login as a tutor
3. Go to Quiz Management
4. Click "Create Quiz"
5. Verify you see the "Course (Optional)" dropdown in Step 1
6. Verify the dropdown shows all your courses
7. Select a course and create the quiz
8. Go to Chapter Management for that course
9. Verify the quiz appears in the chapter's quiz dropdown

## API Endpoints Used

- `GET /api/courses` - Fetches all courses for the dropdown
- `POST /api/learning/quizzes` - Creates quiz with courseId
- `GET /api/learning/quizzes/course/{courseId}` - Fetches quizzes for a specific course (used in chapter management)

## Notes

- The course field is optional - quizzes can exist without being linked to a course
- When editing a quiz, the previously selected course will be pre-selected
- The Quiz model already had the `courseId` field, so no backend changes were needed
- This completes the full workflow: Course → Quiz → Chapter → Quiz Assignment
