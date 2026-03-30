# Jungle in English - English Learning Platform

## Overview

**Developed at Esprit School of Engineering – Tunisia**

This project was developed as part of the PI (Integrated Project) – 4th Year Engineering Program at Esprit School of Engineering (Academic Year 2025-2026).

Jungle in English (EnglishFlow) is a comprehensive microservices-based platform for English language learning, featuring interactive courses, real-time communication, gamification, community forums, and advanced authentication mechanisms.

The platform serves **four main user roles**:
- **Students**: Access courses, participate in forums, join clubs, track progress, and engage with learning materials
- **Tutors**: Create and manage courses, monitor student performance, handle complaints, manage availability
- **Academic Office Affairs**: Handle academic complaints, approve events and clubs, manage exam system, oversee tutor recruitment
- **Administrators**: Oversee platform operations, user management, content moderation, and system configuration

---

## Features

### Authentication & Security
- JWT-based authentication with secure token management (HS512, 15 min expiration)
- OAuth2 integration (Google Sign-In)
- Email verification and account activation with secure tokens
- Password reset functionality with time-limited tokens
- Two-Factor Authentication (2FA/TOTP) support
- Role-based access control (RBAC) - 4 roles: STUDENT, TUTOR, ACADEMIC_OFFICE_AFFAIR, ADMIN
- Rate limiting and brute-force protection (5 attempts per 15 minutes)
- reCAPTCHA v3 protection
- Session management with device tracking and GeoIP localization
- Comprehensive audit logging for security compliance

### User Management
- User registration with email validation
- Profile management with photo upload and thumbnail generation
- Multi-role support with role-specific permissions
- Public user profiles
- Tutor recruitment workflow with application system
- User invitation system
- Session tracking across multiple devices
- Activity audit logs

### Course Management
- Course creation and management by tutors
- Interactive learning modules with chapters and lessons
- Progress tracking and analytics (course, chapter, lesson level)
- Multimedia content support (videos, documents, images, audio)
- Course enrollment system with prerequisites
- Course packs and bundles
- Course categories and filtering
- Tutor availability scheduling with time slots
- File upload with optimization and thumbnails
- Course completion certificates

### Learning Resources
- E-books library with metadata and chapters
- Interactive quizzes with multiple question types
- Reading progress tracking with bookmarks
- Personal collections management
- Resource sharing and recommendations
- Reviews and ratings system
- Annotations and notes
- Tags and categorization
- Quiz attempts tracking with grading

### Exam System (CEFR Levels A1-C2)
- Comprehensive exam creation and management (ACADEMIC_OFFICE_AFFAIR only)
- 8 question types supported:
  - Multiple Choice
  - True/False
  - Fill in the Gap
  - Dropdown Select
  - Word Ordering
  - Matching
  - Open Writing (Essay)
  - Audio Response
- Random exam selection per CEFR level
- Exam attempts tracking with timer
- Auto-save answers functionality
- Automated grading for objective questions
- Manual grading queue for essays
- Detailed exam results with CEFR band recommendation
- Part-by-part breakdown and analytics
- Question review with explanations
- Scheduled attempt expiry

### Communication & Messaging
- Real-time messaging system with WebSocket (STOMP protocol)
- Group conversations with admin roles
- Direct messaging between users
- Message reactions (emojis)
- Read status tracking
- User presence (online/offline)
- Message notifications
- Email notifications with professional Thymeleaf templates
- Rate limiting per user
- Redis session clustering for scalability

### Community Features
- Discussion forums with hierarchical structure (Category → SubCategory → Topic)
- Forum posts with threading and replies
- Reaction system (likes, emojis)
- Voting system
- Forum moderation tools (lock topics, delete posts)
- Full-text search
- Resource attachments (images, documents, videos)
- Trending topics
- Permission-based access control
- Student clubs and groups by English level (Beginner, Intermediate, Advanced)
- Club membership management with roles (President, Vice-President, Member)
- Club tasks and activities
- Club approval workflow
- Club history and audit trail

### Events Management
- Event creation and scheduling (3 types: WORKSHOP, SEMINAR, SOCIAL)
- Event participation and registration with limits
- Event feedback and ratings
- Event approval workflow (ACADEMIC_OFFICE_AFFAIR)
- Public events calendar
- Permission verification via Club Service
- Upcoming events filtering

### Complaints System
- Complaint submission and tracking
- Real-time conversation threads with SSE notifications
- Multiple complaint categories:
  - PEDAGOGICAL (→ Tutor)
  - BEHAVIORAL (→ Academic Office)
  - TUTOR_BEHAVIOR (→ Academic Office)
  - SCHEDULE (→ Academic Office)
  - CLUB_SUSPENSION (→ Academic Office)
  - TECHNICAL (→ Support)
  - ADMINISTRATIVE (→ Academic Office)
  - OTHER (→ Support)
- Complaint workflow management with status tracking
- Priority system (LOW, MEDIUM, HIGH, URGENT)
- Multi-role complaint handling (Student, Tutor, Academic Office, Admin)
- Target role routing based on category

### Gamification
- Points and rewards system with configurable rules:
  - Login: 10 points
  - Course completion: 100 points
  - Quiz perfect score: 50 points
  - Quiz pass: 30 points
  - Assignment submit: 20 points
  - Streak bonus: 5 points
- User levels and progression (5 levels):
  - Beginner (0-100 points)
  - Intermediate (101-500 points)
  - Advanced (501-1000 points)
  - Expert (1001-5000 points)
  - Master (5001+ points)
- Achievement badges with rarity system (Common, Rare, Epic, Legendary)
- Leaderboards
- Activity tracking
- English level tracking
- Loyalty tier system

---

## Tech Stack

### Frontend
- **Framework**: Angular 18
- **Language**: TypeScript 5.5
- **Styling**: Tailwind CSS 3.4, Bootstrap 5.3
- **State Management**: RxJS 7.8
- **Charts**: ApexCharts 3.45, AmCharts 5.13
- **UI Components**: Angular CDK 18.2, FullCalendar 6.1, SweetAlert2 11.26
- **Rich Text Editors**: TinyMCE 8.3, Quill 2.0, ngx-quill 26.0
- **Maps**: Leaflet 1.9 with Geosearch
- **Security**: ng-recaptcha 13.2
- **Real-time**: WebSocket (SockJS 1.6, STOMP 7.3)
- **PDF**: pdfjs-dist 5.5
- **File Handling**: file-saver 2.0, mammoth 1.11
- **Date Picker**: flatpickr 4.6
- **Carousel**: Swiper 11.2

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Architecture**: Microservices with Spring Cloud 2023.0.0
- **Service Discovery**: Netflix Eureka Server
- **API Gateway**: Spring Cloud Gateway with load balancing
- **Configuration**: Spring Cloud Config Server (externalized)
- **Security**: Spring Security 6, JWT (HS512), OAuth2 (Google)
- **Database**: PostgreSQL 15+ with optimized indexes
- **ORM**: Spring Data JPA with Hibernate optimizations
- **Caching**: Redis 7 (distributed), Caffeine (local, TTL 5 min)
- **Email**: Spring Mail with Thymeleaf templates, async sending
- **WebSocket**: Spring WebSocket with STOMP protocol
- **Documentation**: SpringDoc OpenAPI 3.0 (Swagger UI)
- **Monitoring**: Spring Actuator, Micrometer, Prometheus
- **Logging**: Logback with Logstash encoder (structured JSON)
- **Resilience**: Resilience4j (circuit breaker, retry, rate limiter)
- **HTTP Client**: Spring Cloud OpenFeign
- **DTO Mapping**: MapStruct 1.5.5
- **Validation**: Jakarta Bean Validation
- **Build Tool**: Maven 3.9+
- **Utilities**: Lombok, Hypersistence Utils (JSONB)

---

## Architecture

### Microservices Overview

The platform consists of **13 microservices** with a total of:
- **75+ entities**
- **71 controllers**
- **70+ repositories**
- **87+ services**
- **265+ REST endpoints**

| Service | Port | Database | Description | Swagger UI |
|---------|------|----------|-------------|------------|
| Config Server | 8888 | N/A | Centralized configuration management | N/A |
| Eureka Server | 8761 | N/A | Service registry and discovery | N/A |
| API Gateway | 8080 | N/A | API routing, load balancing, CORS | N/A |
| Auth Service | 8081 | englishflow_identity | Authentication, user management, sessions, 2FA, OAuth2 | ✅ |
| Community Service | 8082 | englishflow_community | Forums, topics, posts, reactions, moderation | ✅ |
| Learning Service | 8083 | englishflow_learning_db | E-books, quizzes, reading progress, collections | ❌ |
| Messaging Service | 8084 | englishflow_messaging_db | Real-time messaging, WebSocket, conversations | ✅ |
| Club Service | 8085 | englishflow_jungle_club_db | Student clubs, members, tasks, approval workflow | ✅ |
| Courses Service | 8086 | englishflow_courses | Courses, chapters, lessons, enrollment, progress | ❌ |
| Exam Service | 8087 | englishflow_exams | CEFR exams, questions, attempts, grading, results | ❌ |
| Event Service | 8088 | englishflow_event_db | Events, participants, feedback, approval | ✅ |
| Complaints Service | 8089 | englishflow_complaints | Complaints, messages, workflow, notifications | ❌ |
| Gamification Service | 8090 | englishflow_gamification | Points, levels, badges, leaderboards | ❌ |

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                   Angular Frontend (4200)                    │
│         TypeScript 5.5 + Tailwind CSS + Bootstrap           │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/WebSocket
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              API Gateway (8080)                              │
│    Spring Cloud Gateway - Load Balancing & Routing          │
│    JWT Validation - CORS - Rate Limiting                    │
└────────────────────────┬────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┬─────────────┐
        │                │                │             │
        ▼                ▼                ▼             ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Auth Service │  │  Community   │  │  Learning    │  │  Messaging   │
│   (8081)     │  │   Service    │  │   Service    │  │   Service    │
│              │  │   (8082)     │  │   (8083)     │  │   (8084)     │
│ JWT + OAuth2 │  │   Forums     │  │   E-books    │  │  WebSocket   │
│ 2FA + Email  │  │  Reactions   │  │   Quizzes    │  │  Real-time   │
└──────┬───────┘  └──────────────┘  └──────────────┘  └──────────────┘
       │                │                │                    │
       │                ▼                ▼                    ▼
       │         ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
       │         │  Club        │  │  Courses     │  │  Exam        │
       │         │  Service     │  │  Service     │  │  Service     │
       │         │   (8085)     │  │   (8086)     │  │   (8087)     │
       │         │              │  │              │  │              │
       │         │  Clubs       │  │  Chapters    │  │  CEFR A1-C2  │
       │         │  Members     │  │  Lessons     │  │  Grading     │
       │         └──────────────┘  └──────────────┘  └──────────────┘
       │                │                │                    │
       │                ▼                ▼                    ▼
       │         ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
       │         │  Event       │  │ Complaints   │  │Gamification  │
       │         │  Service     │  │  Service     │  │  Service     │
       │         │   (8088)     │  │   (8089)     │  │   (8090)     │
       │         │              │  │              │  │              │
       │         │  Workshops   │  │  Workflow    │  │  Points      │
       │         │  Seminars    │  │  SSE         │  │  Badges      │
       │         └──────────────┘  └──────────────┘  └──────────────┘
       │                │                │                    │
       └────────────────┴────────────────┴────────────────────┘
                         │                │
        ┌────────────────┴────────────────┘
        │                                  │
        ▼                                  ▼
┌──────────────────┐            ┌──────────────────┐
│  Eureka Server   │            │  Config Server   │
│     (8761)       │            │      (8888)      │
│Service Discovery │            │  Centralized     │
│  Health Checks   │            │  Configuration   │
└──────────────────┘            └──────────────────┘
        │                                  │
        └──────────────┬───────────────────┘
                       │
        ┌──────────────┴──────────────┐
        │                             │
        ▼                             ▼
┌──────────────────┐        ┌──────────────────┐
│   PostgreSQL     │        │   Redis Cache    │
│   (10 databases) │        │   + Sessions     │
└──────────────────┘        └──────────────────┘
```

---

## Contributors

### Development Team - Class 4SAE1

- **Khalil Abdelmoumen**
- **Kenza Baccar**
- **Nadhem Hmida**
- **Ismail Ismail**
- **Mohamed Aziz Louati**

### Academic Supervision

- **Academic Supervisor**: Monsieur Khaled Hamrouni

---

## Academic Context

**Developed at Esprit School of Engineering – Tunisia**

- **Program**: Software Engineering
- **Project Type**: PI (Integrated Project)
- **Academic Year**: 2025-2026
- **Class**: 4SAE1

---

## User Roles & Permissions

### 1. STUDENT
**Capabilities:**
- Access and enroll in courses
- Take exams and quizzes
- Participate in forums and discussions
- Join clubs and events
- Send and receive messages
- Track learning progress
- Submit complaints
- Earn points and badges
- Read e-books and resources

**Restrictions:**
- Cannot create courses or exams
- Cannot moderate forums
- Cannot approve clubs or events
- Cannot access admin panels

### 2. TUTOR
**Capabilities:**
- All STUDENT capabilities
- Create and manage courses
- Create chapters and lessons
- Upload course materials
- Manage course enrollments
- View student progress
- Handle pedagogical complaints
- Set availability schedule
- Grade assignments

**Restrictions:**
- Cannot approve clubs or events
- Cannot manage users
- Cannot access system configuration

### 3. ACADEMIC_OFFICE_AFFAIR
**Capabilities:**
- Create and manage CEFR exams (A1-C2)
- Approve/reject clubs
- Approve/reject events
- Handle academic complaints:
  - Behavioral issues
  - Tutor behavior
  - Schedule conflicts
  - Club suspensions
  - Administrative matters
- Manage tutor recruitment
- Grade essay questions
- View analytics and reports

**Restrictions:**
- Cannot create courses (Tutor role)
- Cannot access system configuration
- Cannot manage users directly

### 4. ADMIN
**Capabilities:**
- All system access
- User management (create, update, delete, assign roles)
- Content moderation
- System configuration
- View audit logs
- Manage all complaints
- Access all analytics
- Override permissions

---

## Getting Started

### Prerequisites

- **Node.js** 18+ and npm
- **Java** 17 (JDK)
- **PostgreSQL** 14+
- **Maven** 3.8+
- **Git**
- **Redis** (optional, for caching)

### Installation

#### 1. Clone the Repository

```bash
git clone https://github.com/Khaalilabd/EnglishFlow-PI.git
cd EnglishFlow-PI
```

#### 2. Database Setup

Create PostgreSQL databases:

```sql
-- Create databases
CREATE DATABASE englishflow_identity;
CREATE DATABASE englishflow_community;
CREATE DATABASE englishflow_learning_db;
CREATE DATABASE englishflow_messaging_db;
CREATE DATABASE englishflow_jungle_club_db;
CREATE DATABASE englishflow_courses;
CREATE DATABASE englishflow_exams;
CREATE DATABASE englishflow_event_db;
CREATE DATABASE englishflow_complaints;
CREATE DATABASE englishflow_gamification;

-- Create user (optional)
CREATE USER englishflow WITH PASSWORD 'englishflow123';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE englishflow_identity TO englishflow;
GRANT ALL PRIVILEGES ON DATABASE englishflow_community TO englishflow;
GRANT ALL PRIVILEGES ON DATABASE englishflow_learning_db TO englishflow;
GRANT ALL PRIVILEGES ON DATABASE englishflow_messaging_db TO englishflow;
GRANT ALL PRIVILEGES ON DATABASE englishflow_jungle_club_db TO englishflow;
GRANT ALL PRIVILEGES ON DATABASE englishflow_courses TO englishflow;
GRANT ALL PRIVILEGES ON DATABASE englishflow_exams TO englishflow;
GRANT ALL PRIVILEGES ON DATABASE englishflow_event_db TO englishflow;
GRANT ALL PRIVILEGES ON DATABASE englishflow_complaints TO englishflow;
GRANT ALL PRIVILEGES ON DATABASE englishflow_gamification TO englishflow;
```

#### 3. Backend Configuration

Configure environment variables in `backend/auth-service/.env`:

```env
# Google OAuth2
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# Frontend URL
FRONTEND_URL=http://localhost:4200

# Database
DB_URL=jdbc:postgresql://localhost:5432/englishflow_identity
DB_USERNAME=postgres
DB_PASSWORD=postgres

# JWT
JWT_SECRET=your-secure-jwt-secret-key-minimum-256-bits
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

# Email (Gmail)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password

# reCAPTCHA
RECAPTCHA_SECRET_KEY=your-recaptcha-secret-key

# Redis (optional)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
```

#### 4. Start Backend Services

Start services in order:

```bash
# 1. Config Server (8888) - Optional if using local config
cd backend/config-server
mvn spring-boot:run

# 2. Eureka Server (8761) - Service Discovery
cd backend/eureka-server
mvn spring-boot:run

# Wait for Eureka to start (check http://localhost:8761)

# 3. API Gateway (8080)
cd backend/api-gateway
mvn spring-boot:run

# 4. Auth Service (8081) - Must start before others
cd backend/auth-service
mvn spring-boot:run

# 5. Other services (can start in parallel)
cd backend/community-service && mvn spring-boot:run &
cd backend/learning-service && mvn spring-boot:run &
cd backend/messaging-service && mvn spring-boot:run &
cd backend/club-service && mvn spring-boot:run &
cd backend/courses-service && mvn spring-boot:run &
cd backend/exam-service && mvn spring-boot:run &
cd backend/event-service && mvn spring-boot:run &
cd backend/complaints-service && mvn spring-boot:run &
cd backend/gamification-service && mvn spring-boot:run &
```

#### 5. Start Frontend

```bash
cd frontend
npm install --legacy-peer-deps
npm start
```

#### 6. Access Application

- **Frontend**: http://localhost:4200
- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Auth Service Swagger**: http://localhost:8081/swagger-ui.html

### Service Endpoints

| Service | Port | Swagger UI | Health Check | Actuator |
|---------|------|------------|--------------|----------|
| Config Server | 8888 | N/A | http://localhost:8888/actuator/health | ✅ |
| Eureka Server | 8761 | N/A | http://localhost:8761 | ✅ |
| API Gateway | 8080 | N/A | http://localhost:8080/actuator/health | ✅ |
| Auth Service | 8081 | http://localhost:8081/swagger-ui.html | http://localhost:8081/actuator/health | ✅ |
| Community Service | 8082 | http://localhost:8082/swagger-ui.html | http://localhost:8082/actuator/health | ✅ |
| Learning Service | 8083 | N/A | http://localhost:8083/actuator/health | ✅ |
| Messaging Service | 8084 | http://localhost:8084/swagger-ui.html | http://localhost:8084/actuator/health | ✅ |
| Club Service | 8085 | http://localhost:8085/swagger-ui.html | http://localhost:8085/actuator/health | ✅ |
| Courses Service | 8086 | N/A | http://localhost:8086/actuator/health | ✅ |
| Exam Service | 8087 | N/A | http://localhost:8087/actuator/health | ✅ |
| Event Service | 8088 | http://localhost:8088/swagger-ui.html | http://localhost:8088/actuator/health | ✅ |
| Complaints Service | 8089 | N/A | http://localhost:8089/actuator/health | ✅ |
| Gamification Service | 8090 | N/A | N/A | ❌ |

---

## Documentation

### Service Documentation
- [Auth Service Setup Guide](./backend/auth-service/docs/README.md)
- [Gmail SMTP Configuration](./backend/auth-service/docs/GMAIL_SETUP.md)
- [OAuth2 Configuration](./backend/auth-service/docs/OAUTH2_SETUP.md)
- [2FA Implementation](./backend/auth-service/docs/2FA_IMPLEMENTATION.md)
- [API Documentation](./backend/auth-service/docs/API_DOCUMENTATION.md)
- [Monitoring Guide](./backend/auth-service/docs/MONITORING_GUIDE.md)
- [Club Service README](./backend/club-service/README.md)
- [Event Service README](./backend/event-service/README.md)
- [Exam Service README](./backend/exam-service/README.md)

### API Documentation
Swagger/OpenAPI documentation available at: `http://localhost:<port>/swagger-ui.html`

### Postman Collection
- `backend/auth-service/postman_collection.json`

### CI/CD Documentation
- [CI/CD Corrections Guide](./CORRECTIONS_CI_CD.md)
- [Configuration Explanation](./EXPLICATION_CONFIGURATION.md)
- [GitHub Actions Launch Guide](./GUIDE_LANCEMENT_TESTS_GITHUB.md)

---

## Project Statistics

- **Total Lines of Code**: 100,000+
- **Backend Services**: 13 microservices
- **Entities**: 75+
- **Controllers**: 71
- **Repositories**: 70+
- **Services**: 87+
- **REST Endpoints**: 265+
- **Databases**: 10 PostgreSQL databases
- **Frontend Components**: 150+
- **Test Coverage**: 70%+

---

## Acknowledgments

### Special Thanks

- **Esprit School of Engineering** for providing the academic framework and resources
- **Monsieur Khaled Hamrouni** for his guidance and mentorship throughout the project
- **Spring Boot & Angular Communities** for excellent documentation and support
- **Open Source Contributors** for the libraries and tools used in this project

### Resources & References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Angular Documentation](https://angular.io/docs)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Microservices Patterns](https://microservices.io/patterns/)
- [CEFR Framework](https://www.coe.int/en/web/common-european-framework-reference-languages)

---

**Made with ❤️ by 4SAE1 Team - Esprit School of Engineering**

---

## Topics

`esprit-school-of-engineering` `academic-project` `esprit-pi` `2025-2026` `angular` `spring-boot` `microservices` `java` `typescript` `postgresql` `english-learning` `e-learning-platform` `jwt-authentication` `oauth2` `websocket` `real-time-messaging` `gamification` `docker` `kubernetes` `redis` `prometheus` `cefr` `spring-cloud` `eureka` `api-gateway` `microservices-architecture` `mapstruct` `resilience4j` `feign-client`
