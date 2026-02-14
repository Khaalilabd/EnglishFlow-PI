# Learning Service Setup Guide

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 15+

## Database Setup

### Option 1: Local PostgreSQL

1. Create the database:
```sql
CREATE DATABASE learning_db;
CREATE USER englishflow WITH PASSWORD 'englishflow123';
GRANT ALL PRIVILEGES ON DATABASE learning_db TO englishflow;
```

2. The tables will be created automatically by Hibernate when you run the application.

### Option 2: Docker PostgreSQL

Use the docker-compose.yml in the root directory:
```bash
docker-compose up postgres-learning -d
```

This will create a PostgreSQL instance on port 5433 with the learning_db database.

If using Docker, update `application.yml`:
```yaml
datasource:
  url: jdbc:postgresql://localhost:5433/learning_db
```

## Running the Service

### Development Mode

```bash
cd backend/learning-service
mvn spring-boot:run
```

The service will start on port 8083.

### Production Build

```bash
mvn clean package
java -jar target/learning-service-1.0.0.jar
```

### Docker

```bash
docker build -t learning-service .
docker run -p 8083:8083 learning-service
```

## Testing the API

### Create a Quiz

```bash
curl -X POST http://localhost:8083/api/quizzes \
  -H "Content-Type: application/json" \
  -d '{
    "title": "English Grammar Quiz",
    "description": "Test your grammar knowledge",
    "durationMin": 30,
    "maxScore": 100,
    "passingScore": 70,
    "published": true
  }'
```

### Add Questions

```bash
curl -X POST http://localhost:8083/api/questions \
  -H "Content-Type: application/json" \
  -d '{
    "quizId": 1,
    "content": "What is the past tense of 'go'?",
    "type": "multiple_choice",
    "options": "went,goed,gone,goes",
    "correctAnswer": "went",
    "points": 10,
    "orderIndex": 1
  }'
```

### Upload an Ebook

```bash
curl -X POST http://localhost:8083/api/ebooks \
  -F 'ebook={"title":"English Grammar Guide","description":"Complete grammar guide","level":"intermediate","category":"grammar","free":true};type=application/json' \
  -F 'file=@/path/to/book.pdf'
```

## File Upload Directory

Ebooks are stored in `uploads/ebooks/`. This directory is created automatically if it doesn't exist.

## Health Check

```bash
curl http://localhost:8083/actuator/health
```

## Troubleshooting

### Database Connection Issues

- Verify PostgreSQL is running
- Check database credentials in `application.yml`
- Ensure the database `learning_db` exists

### Port Already in Use

If port 8083 is already in use, change it in `application.yml`:
```yaml
server:
  port: 8084
```

### File Upload Issues

- Check write permissions for `uploads/ebooks/` directory
- Verify `app.upload.dir` in `application.yml`
- Check file size limits in `application.yml` (default: 50MB)
