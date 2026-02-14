# Learning Service - Project Structure

## Overview
The learning-service is a Spring Boot microservice for managing quizzes, questions, quiz attempts, and ebooks for the EnglishFlow platform.

## Technology Stack
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL 15
- Lombok
- Maven

## Project Structure

```
learning-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/jungle/learning/
│   │   │       ├── LearningServiceApplication.java    # Main application class
│   │   │       │
│   │   │       ├── config/
│   │   │       │   └── WebConfig.java                 # CORS configuration
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── QuizController.java            # Quiz CRUD endpoints
│   │   │       │   ├── QuestionController.java        # Question CRUD endpoints
│   │   │       │   ├── AttemptController.java         # Quiz attempt endpoints
│   │   │       │   └── EbookController.java           # Ebook CRUD endpoints
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── QuizService.java               # Quiz business logic
│   │   │       │   ├── QuestionService.java           # Question business logic
│   │   │       │   ├── QuizAttemptService.java        # Attempt & grading logic
│   │   │       │   ├── GradingService.java            # Answer grading logic
│   │   │       │   └── EbookService.java              # Ebook & file upload logic
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   ├── QuizRepository.java
│   │   │       │   ├── QuestionRepository.java
│   │   │       │   ├── QuizAttemptRepository.java
│   │   │       │   ├── StudentAnswerRepository.java
│   │   │       │   ├── EbookRepository.java
│   │   │       │   └── EbookAccessRepository.java
│   │   │       │
│   │   │       ├── model/
│   │   │       │   ├── Quiz.java                      # Quiz entity
│   │   │       │   ├── Question.java                  # Question entity
│   │   │       │   ├── QuizAttempt.java               # Quiz attempt entity
│   │   │       │   ├── StudentAnswer.java             # Student answer entity
│   │   │       │   ├── Ebook.java                     # Ebook entity
│   │   │       │   └── EbookAccess.java               # Ebook access tracking
│   │   │       │
│   │   │       ├── dto/
│   │   │       │   ├── QuizDTO.java
│   │   │       │   ├── QuestionDTO.java
│   │   │       │   ├── AttemptRequestDTO.java
│   │   │       │   ├── AttemptResultDTO.java
│   │   │       │   └── EbookDTO.java
│   │   │       │
│   │   │       └── exception/
│   │   │           ├── ResourceNotFoundException.java
│   │   │           ├── InvalidQuizAttemptException.java
│   │   │           └── GlobalExceptionHandler.java    # Global error handler
│   │   │
│   │   └── resources/
│   │       ├── application.yml                        # Application configuration
│   │       └── static/
│   │           └── ebooks/                            # Local PDF storage
│   │
│   └── test/
│       └── java/com/jungle/learning/
│           └── LearningServiceApplicationTests.java
│
├── uploads/                                           # External uploads folder
│   └── ebooks/
│
├── Dockerfile                                         # Docker build file
├── pom.xml                                            # Maven dependencies
├── .gitignore
├── .editorconfig
├── README.md                                          # Service overview
├── SETUP.md                                           # Setup instructions
├── API.md                                             # API documentation
├── init-db.sql                                        # Database init script
├── run.sh                                             # Linux/Mac run script
└── run.bat                                            # Windows run script
```

## Database Schema

### Tables

1. **quiz**
   - id (PK)
   - title
   - description
   - course_id (FK - to be added later)
   - duration_min
   - max_score
   - passing_scr
   - published
   - due_date
   - created_at
   - updated_at

2. **question**
   - id (PK)
   - quiz_id (FK)
   - content
   - type (multiple_choice, true_false, short_answer, essay)
   - options (comma-separated)
   - correct_ans
   - points
   - order_index

3. **quiz_attempt**
   - id (PK)
   - quiz_id (FK)
   - student_id (FK - to be added later)
   - score
   - started_at
   - submitted_at
   - status (IN_PROGRESS, COMPLETED)

4. **student_answer**
   - id (PK)
   - attempt_id (FK)
   - question_id (FK)
   - answer
   - is_correct
   - points_earnd

5. **ebook**
   - id (PK)
   - title
   - description
   - file_url
   - file_size
   - mime_type
   - level (beginner, intermediate, advanced)
   - category
   - free
   - download_cnt
   - created_at

6. **ebook_access**
   - id (PK)
   - ebook_id (FK)
   - student_id (FK - to be added later)
   - accessed_at
   - progress_pct

## Key Features

### Quiz Management
- Create, read, update, delete quizzes
- Publish/unpublish quizzes
- Set passing scores and time limits
- Filter by published status

### Question Management
- Add questions to quizzes
- Support multiple question types
- Order questions within a quiz
- Assign points to questions

### Quiz Attempts
- Start quiz attempts
- Submit answers
- Automatic grading
- Track attempt history
- View detailed results

### Ebook Management
- Upload PDF files
- Store ebook metadata
- Track downloads
- Filter by level and category
- Support free and paid ebooks

### Grading System
- Automatic answer checking
- Points calculation
- Pass/fail determination
- Detailed feedback

## API Endpoints

### Quiz: `/api/quizzes`
- GET / - List all quizzes
- GET /published - List published quizzes
- GET /{id} - Get quiz details
- POST / - Create quiz
- PUT /{id} - Update quiz
- DELETE /{id} - Delete quiz

### Question: `/api/questions`
- GET /quiz/{quizId} - List questions
- GET /{id} - Get question
- POST / - Create question
- PUT /{id} - Update question
- DELETE /{id} - Delete question

### Attempt: `/api/attempts`
- POST /start - Start attempt
- POST /{id}/submit - Submit attempt
- GET /student/{studentId} - Student attempts
- GET /{id}/result - Attempt result

### Ebook: `/api/ebooks`
- GET / - List all ebooks
- GET /free - List free ebooks
- GET /level/{level} - Filter by level
- GET /{id} - Get ebook
- POST / - Create ebook (with file)
- PUT /{id} - Update ebook
- DELETE /{id} - Delete ebook
- POST /{id}/access - Track access

## Configuration

### Server
- Port: 8083

### Database
- Type: PostgreSQL
- Database: learning_db
- Port: 5432 (local) or 5433 (docker)
- User: englishflow
- Password: englishflow123

### File Upload
- Max file size: 50MB
- Upload directory: uploads/ebooks/
- Supported formats: PDF

## Future Enhancements

1. Add Course entity and relationships
2. Add Student entity and relationships
3. Implement authentication/authorization
4. Add quiz timer functionality
5. Support more question types
6. Add quiz analytics
7. Implement ebook reader
8. Add progress tracking
9. Support multiple file formats
10. Add search functionality

## Notes

- Tables are created automatically by Hibernate (ddl-auto: update)
- CORS is enabled for localhost:4200 and localhost:8080
- Eureka client is disabled by default
- File uploads are stored locally (consider cloud storage for production)
- Course and Student references use Long IDs (to be linked later)
