# Learning Service API Documentation

Base URL: `http://localhost:8083`

## Quiz Management

### Get All Quizzes
```
GET /api/quizzes
```

### Get Published Quizzes Only
```
GET /api/quizzes/published
```

### Get Quiz by ID
```
GET /api/quizzes/{id}
```

### Create Quiz
```
POST /api/quizzes
Content-Type: application/json

{
  "title": "English Grammar Quiz",
  "description": "Test your grammar knowledge",
  "courseId": 1,
  "durationMin": 30,
  "maxScore": 100,
  "passingScore": 70,
  "published": true,
  "dueDate": "2026-03-01T23:59:59"
}
```

### Update Quiz
```
PUT /api/quizzes/{id}
Content-Type: application/json

{
  "title": "Updated Quiz Title",
  "description": "Updated description",
  "published": true
}
```

### Delete Quiz
```
DELETE /api/quizzes/{id}
```

## Question Management

### Get Questions by Quiz ID
```
GET /api/questions/quiz/{quizId}
```

### Get Question by ID
```
GET /api/questions/{id}
```

### Create Question
```
POST /api/questions
Content-Type: application/json

{
  "quizId": 1,
  "content": "What is the past tense of 'go'?",
  "type": "multiple_choice",
  "options": "went,goed,gone,goes",
  "correctAnswer": "went",
  "points": 10,
  "orderIndex": 1
}
```

Question Types:
- `multiple_choice` - Multiple choice question
- `true_false` - True/False question
- `short_answer` - Short text answer
- `essay` - Long text answer

### Update Question
```
PUT /api/questions/{id}
Content-Type: application/json

{
  "content": "Updated question content",
  "correctAnswer": "updated answer"
}
```

### Delete Question
```
DELETE /api/questions/{id}
```

## Quiz Attempts

### Start Quiz Attempt
```
POST /api/attempts/start?quizId=1&studentId=123
```

Response:
```json
{
  "id": 1,
  "quizId": 1,
  "studentId": 123,
  "status": "IN_PROGRESS",
  "startedAt": "2026-02-14T10:00:00"
}
```

### Submit Quiz Attempt
```
POST /api/attempts/{attemptId}/submit
Content-Type: application/json

{
  "quizId": 1,
  "studentId": 123,
  "answers": {
    "1": "went",
    "2": "true",
    "3": "present perfect"
  }
}
```

Response:
```json
{
  "attemptId": 1,
  "quizId": 1,
  "studentId": 123,
  "score": 80,
  "maxScore": 100,
  "passed": true,
  "startedAt": "2026-02-14T10:00:00",
  "submittedAt": "2026-02-14T10:30:00",
  "status": "COMPLETED",
  "answerDetails": {
    "1": {
      "studentAnswer": "went",
      "correctAnswer": "went",
      "isCorrect": true,
      "pointsEarned": 10
    }
  }
}
```

### Get Student Attempts
```
GET /api/attempts/student/{studentId}
```

### Get Attempt Result
```
GET /api/attempts/{attemptId}/result
```

## Ebook Management

### Get All Ebooks
```
GET /api/ebooks
```

### Get Free Ebooks
```
GET /api/ebooks/free
```

### Get Ebooks by Level
```
GET /api/ebooks/level/{level}
```

Levels: `beginner`, `intermediate`, `advanced`

### Get Ebook by ID
```
GET /api/ebooks/{id}
```

### Create Ebook (with file upload)
```
POST /api/ebooks
Content-Type: multipart/form-data

ebook: {
  "title": "English Grammar Guide",
  "description": "Complete grammar guide for beginners",
  "level": "beginner",
  "category": "grammar",
  "free": true
}
file: [PDF file]
```

### Create Ebook (without file upload)
```
POST /api/ebooks
Content-Type: multipart/form-data

ebook: {
  "title": "External Ebook",
  "description": "Link to external resource",
  "fileUrl": "https://example.com/book.pdf",
  "level": "intermediate",
  "category": "vocabulary",
  "free": false
}
```

### Update Ebook
```
PUT /api/ebooks/{id}
Content-Type: multipart/form-data

ebook: {
  "title": "Updated Title",
  "description": "Updated description"
}
file: [Optional new PDF file]
```

### Delete Ebook
```
DELETE /api/ebooks/{id}
```

### Track Ebook Access
```
POST /api/ebooks/{ebookId}/access?studentId={studentId}
```

This endpoint tracks when a student accesses an ebook and increments the download count.

## Error Responses

### 404 Not Found
```json
{
  "timestamp": "2026-02-14T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Quiz not found with id: 1"
}
```

### 400 Bad Request
```json
{
  "timestamp": "2026-02-14T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Quiz is not published yet"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2026-02-14T10:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```
