# Student Quiz Visibility - Issue and Solution

## The Issue

Students are seeing "No Courses Found" and no quizzes because:

1. **Students haven't enrolled in any packs/courses yet**
2. **Quizzes page shows ALL published quizzes** (should only show quizzes for enrolled courses)

## Root Cause

Looking at your screenshots:
- **Tutor Panel**: Shows 3 published courses with quizzes
- **Student Panel**: Shows "No Courses Found - You haven't enrolled in any packs yet"

This means the student account hasn't enrolled in any packs/courses.

## Solution 1: Enroll Students in Packs/Courses

### For Students to See Courses and Quizzes:

1. **Student needs to browse and enroll in packs:**
   - Click "Browse Packs" button (shown in the screenshot)
   - Find available packs/courses
   - Enroll in a pack
   - Then courses and quizzes will appear

2. **Or Tutor can enroll students:**
   - Tutor goes to "My Students" section
   - Manually enrolls students in packs

### How Enrollment Works:

```
Pack (contains multiple courses)
  ↓
Student enrolls in Pack
  ↓
Student gets access to all courses in that pack
  ↓
Student can see quizzes for those courses
```

## Solution 2: Filter Quizzes by Enrolled Courses (Optional Enhancement)

Currently, the quizzes page shows ALL published quizzes. You might want to filter them to only show quizzes for courses the student is enrolled in.

### Current Behavior:
```typescript
// Shows ALL published quizzes
this.quizService.getAllQuizzes().subscribe({
  next: (data) => {
    this.quizzes = data.filter(quiz => quiz.published);
  }
});
```

### Recommended Behavior:
```typescript
// Show only quizzes for enrolled courses
1. Get student's enrolled packs
2. Get courses from those packs
3. Get course IDs
4. Filter quizzes by those course IDs
```

## Quick Test Steps

### To verify the system works:

1. **Login as Student**
2. **Click "Browse Packs"** (button visible in your screenshot)
3. **Find a pack and enroll**
4. **Go back to "My Courses"** - you should now see courses
5. **Go to "Quizzes"** - you should now see quizzes

## Why This Happens

Your system uses a **Pack-based enrollment model**:
- Tutors create **Packs** (bundles of courses)
- Students **enroll in Packs**
- Students get access to all courses in that pack
- Quizzes are linked to courses
- Students see quizzes for their enrolled courses

## Verification Checklist

- [ ] Student has enrolled in at least one pack
- [ ] Pack contains courses
- [ ] Courses have quizzes assigned
- [ ] Quizzes are published
- [ ] Quizzes have courseId set

## Database Check

Run this SQL to verify data:

```sql
-- Check if student is enrolled in any packs
SELECT * FROM pack_enrollments WHERE student_id = [STUDENT_ID];

-- Check packs and their courses
SELECT p.*, pc.course_id 
FROM packs p
LEFT JOIN pack_courses pc ON p.id = pc.pack_id;

-- Check quizzes and their courses
SELECT id, title, course_id, published 
FROM quiz 
WHERE published = true;

-- Check if courses have quizzes
SELECT c.id, c.title, q.id as quiz_id, q.title as quiz_title
FROM courses c
LEFT JOIN quiz q ON q.course_id = c.id
WHERE q.published = true;
```

## Next Steps

### Immediate Fix:
1. Have the student enroll in a pack
2. Verify the pack contains courses
3. Verify courses have published quizzes

### Optional Enhancement:
If you want to filter quizzes by enrolled courses, I can update the `quizzes.component.ts` to:
1. Fetch student's enrolled packs
2. Get course IDs from those packs
3. Filter quizzes to only show those with matching courseIds

Would you like me to implement the optional enhancement?

## Summary

**The student needs to enroll in packs first.** Once enrolled:
- "My Courses" will show the courses from enrolled packs
- "Quizzes" will show quizzes for those courses
- Everything will work as expected

The system is working correctly - it's just that the student account hasn't enrolled in any packs yet!
