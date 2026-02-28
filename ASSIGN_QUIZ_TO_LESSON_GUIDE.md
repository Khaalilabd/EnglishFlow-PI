# How to Assign a Quiz to a Lesson

## Problem
You're seeing "Coming Soon" message when clicking on a quiz lesson because the lesson doesn't have a quiz assigned to it yet.

## Solution: Assign a Quiz to the Lesson

### Step 1: Create a Quiz (if you haven't already)
1. Go to **Tutor Panel** > **Quiz Management**
2. Click **Create Quiz**
3. Fill in quiz details:
   - Title
   - Description
   - Select the course
   - Set passing score
   - Add questions
4. Save the quiz

### Step 2: Assign the Quiz to a Lesson
1. Go to **Tutor Panel** > **Courses**
2. Click on your course
3. Navigate to **Chapters** > Select the chapter
4. Click on **Lessons** or **Manage Lessons**
5. Find the lesson with type "QUIZ" (the one showing "sqd" in your screenshot)
6. Click **Edit** on that lesson
7. In the lesson form, you should see a **Quiz** dropdown (it appears when lesson type is QUIZ)
8. Select the quiz you created from the dropdown
9. Click **Save**

### Step 3: Test as Student
1. Login as a student
2. Navigate to the course
3. Click on the quiz lesson
4. You should now see the **Start Quiz** button instead of "Coming Soon"

## Troubleshooting

### If you don't see the Quiz dropdown:
- Make sure the lesson type is set to "QUIZ"
- The dropdown only appears when lesson type is QUIZ

### If the dropdown is empty:
- Make sure you've created at least one quiz
- Make sure the quiz is assigned to the same course

### If you still see "Coming Soon":
1. Open browser console (F12)
2. Look for the console logs:
   ```
   📚 Loaded lesson: {...}
   📝 Lesson type: QUIZ
   🎯 Quiz ID: undefined (or a number)
   ```
3. If Quiz ID is `undefined` or `null`, the quiz wasn't saved properly
4. Try editing and saving the lesson again

### Backend Check:
If the quiz ID is still not saving, check the backend:
1. Make sure the `quiz_id` column exists in the `lessons` table
2. Run this SQL if needed:
   ```sql
   ALTER TABLE lessons ADD COLUMN IF NOT EXISTS quiz_id BIGINT;
   ```
3. Restart the courses-service

## Expected Behavior

Once a quiz is properly assigned:
1. Student clicks on quiz lesson
2. Sees quiz start screen with:
   - Quiz title and description
   - Number of questions
   - Time limit
   - Passing score
   - **Start Quiz** button (green, full width)
3. Clicks "Start Quiz"
4. Takes the quiz question by question
5. Submits and sees results
6. Automatically moves to next lesson
