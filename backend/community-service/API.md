# Community Service API Documentation

## Base URL
```
http://localhost:8082/api/community
```

## Endpoints

### Categories

#### Get All Categories
```http
GET /categories
```

Response:
```json
[
  {
    "id": 1,
    "name": "Général",
    "description": "Discussions générales et annonces",
    "icon": "fa-home",
    "color": "primary",
    "subCategories": [
      {
        "id": 1,
        "name": "Présentation des étudiants",
        "description": "Présentez-vous...",
        "categoryId": 1
      }
    ]
  }
]
```

#### Get Category by ID
```http
GET /categories/{id}
```

---

### Topics

#### Create Topic
```http
POST /topics
Content-Type: application/json

{
  "subCategoryId": 1,
  "title": "Mon premier sujet",
  "content": "Contenu du sujet...",
  "userId": 123,
  "userName": "John Doe"
}
```

#### Get Topics by SubCategory
```http
GET /topics/subcategory/{subCategoryId}?page=0&size=20&sortBy=createdAt&sortDir=DESC
```

Parameters:
- `page`: Page number (default: 0)
- `size`: Page size (default: 20)
- `sortBy`: Sort field (default: createdAt)
- `sortDir`: Sort direction ASC/DESC (default: DESC)

#### Get Topic by ID
```http
GET /topics/{id}
```

#### Update Topic
```http
PUT /topics/{id}
Content-Type: application/json

{
  "subCategoryId": 1,
  "title": "Titre modifié",
  "content": "Contenu modifié...",
  "userId": 123,
  "userName": "John Doe"
}
```

#### Delete Topic
```http
DELETE /topics/{id}
```

---

### Posts

#### Create Post
```http
POST /posts
Content-Type: application/json

{
  "topicId": 1,
  "content": "Ma réponse...",
  "userId": 123,
  "userName": "John Doe"
}
```

#### Get Posts by Topic
```http
GET /posts/topic/{topicId}?page=0&size=20
```

Parameters:
- `page`: Page number (default: 0)
- `size`: Page size (default: 20)

#### Update Post
```http
PUT /posts/{id}
Content-Type: application/json

{
  "topicId": 1,
  "content": "Contenu modifié...",
  "userId": 123,
  "userName": "John Doe"
}
```

#### Delete Post
```http
DELETE /posts/{id}
```

---

## Database Schema

### Tables

#### categories
- id (BIGINT, PK)
- name (VARCHAR, UNIQUE)
- description (TEXT)
- icon (VARCHAR)
- color (VARCHAR)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

#### sub_categories
- id (BIGINT, PK)
- name (VARCHAR)
- description (TEXT)
- category_id (BIGINT, FK)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

#### topics
- id (BIGINT, PK)
- title (VARCHAR)
- content (TEXT)
- user_id (BIGINT)
- user_name (VARCHAR)
- sub_category_id (BIGINT, FK)
- views_count (INT)
- is_pinned (BOOLEAN)
- is_locked (BOOLEAN)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

#### posts
- id (BIGINT, PK)
- content (TEXT)
- user_id (BIGINT)
- user_name (VARCHAR)
- topic_id (BIGINT, FK)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

---

## Setup

1. Create database:
```sql
CREATE DATABASE englishflow_community;
```

2. Run initialization script:
```bash
psql -U englishflow -d englishflow_community -f init-categories.sql
```

3. Start service:
```bash
mvn spring-boot:run
```

or

```bash
./run.bat
```
