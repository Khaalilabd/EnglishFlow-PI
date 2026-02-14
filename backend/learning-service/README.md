# Learning Service

Learning service for EnglishFlow platform providing quiz and ebook management.

## Features

- Quiz CRUD operations
- Question management
- Quiz attempt tracking and grading
- Ebook management with file upload
- Ebook access tracking

## Database

PostgreSQL database: `learning_db` on port `5432`

### Tables

- `quiz` - Quiz information
- `question` - Quiz questions
- `quiz_attempt` - Student quiz attempts
- `student_answer` - Student answers for each question
- `ebook` - Ebook information
- `ebook_access` - Student ebook access tracking

## API Endpoints

### Quiz Endpoints
- `GET /api/quizzes` - Get all quizzes
- `GET /api/quizzes/published` - Get published quizzes
- `GET /api/quizzes/{id}` - Get quiz by ID
- `POST /api/quizzes` - Create new quiz
- `PUT /api/quizzes/{id}` - Update quiz
- `DELETE /api/quizzes/{id}` - Delete quiz

### Question Endpoints
- `GET /api/questions/quiz/{quizId}` - Get questions by quiz ID
- `GET /api/questions/{id}` - Get question by ID
- `POST /api/questions` - Create new question
- `PUT /api/questions/{id}` - Update question
- `DELETE /api/questions/{id}` - Delete question

### Attempt Endpoints
- `POST /api/attempts/start?quizId={quizId}&studentId={studentId}` - Start quiz attempt
- `POST /api/attempts/{attemptId}/submit` - Submit quiz attempt
- `GET /api/attempts/student/{studentId}` - Get student attempts
- `GET /api/attempts/{attemptId}/result` - Get attempt result

### Ebook Endpoints
- `GET /api/ebooks` - Get all ebooks
- `GET /api/ebooks/free` - Get free ebooks
- `GET /api/ebooks/level/{level}` - Get ebooks by level
- `GET /api/ebooks/{id}` - Get ebook by ID
- `POST /api/ebooks` - Create new ebook (with file upload)
- `PUT /api/ebooks/{id}` - Update ebook
- `DELETE /api/ebooks/{id}` - Delete ebook
- `POST /api/ebooks/{ebookId}/access?studentId={studentId}` - Track ebook access

## Setup

1. Create PostgreSQL database:
```sql
CREATE DATABASE learning_db;
```

2. Update database credentials in `application.yml` if needed

3. Run the service:
```bash
mvn spring-boot:run
```

The service will run on port `8083`.

## File Upload

Ebooks are stored in `uploads/ebooks/` directory. Make sure this directory exists or it will be created automatically.
