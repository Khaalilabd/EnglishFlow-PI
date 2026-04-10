# Learning Service - EnglishFlow Platform

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Role of Eureka and Gateway](#role-of-eureka-and-gateway)
3. [Learning Service Details](#learning-service-details)
4. [Angular ↔ API Connection](#angular--api-connection)

---

## Architecture Overview

### Microservices Architecture
EnglishFlow utilizes a **microservices architecture** where the application is decomposed into independent, loosely-coupled services. Each service handles a specific business domain.

```
┌─────────────────────────────────────────────────────────────┐
│                     Angular Frontend                         │
│                    (Port 4200/4201)                          │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (Port 8080)                   │
│              Routes requests to microservices                │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              Eureka Server (Port 8761)                       │
│           Service Discovery & Registration                   │
└────────────────────────┬────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┬──────────────┐
        ▼                ▼                ▼              ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Auth Service │  │Learning Svc  │  │Courses Svc   │  │Other Services│
│  (Port 8081) │  │ (Port 8086)  │  │ (Port 8082)  │  │              │
└──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘
        │                │                │
        ▼                ▼                ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  PostgreSQL  │  │  PostgreSQL  │  │  PostgreSQL  │
│   (Auth DB)  │  │(Learning DB) │  │ (Courses DB) │
└──────────────┘  └──────────────┘  └──────────────┘
```

### Key Architectural Principles

1. **Service Independence**: Each microservice can be developed, deployed, and scaled independently
2. **Database per Service**: Each service has its own database (Database per Service pattern)
3. **API Gateway Pattern**: Single entry point for all client requests
4. **Service Discovery**: Dynamic service registration and discovery using Eureka
5. **Load Balancing**: Client-side load balancing through Spring Cloud LoadBalancer

---

## Role of Eureka and Gateway

### Eureka Server (Service Discovery)

**Port**: 8761  
**Purpose**: Service registry for microservices discovery

#### How Eureka Works:

1. **Service Registration**:
   - When a microservice starts, it registers itself with Eureka
   - Sends heartbeat signals every 30 seconds to indicate it's alive
   - Provides metadata: service name, host, port, health check URL

2. **Service Discovery**:
   - Services query Eureka to find other services
   - No need for hardcoded URLs or IP addresses
   - Dynamic discovery enables horizontal scaling

3. **Health Monitoring**:
   - Tracks which service instances are healthy
   - Removes unhealthy instances from registry
   - Enables automatic failover

#### Configuration in Learning Service:
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${random.value}
```

### API Gateway (Spring Cloud Gateway)

**Port**: 8080  
**Purpose**: Single entry point, routing, and cross-cutting concerns

#### Gateway Responsibilities:

1. **Request Routing**:
   ```
   http://localhost:8080/api/learning/** → Learning Service (8086)
   http://localhost:8080/api/auth/**     → Auth Service (8081)
   http://localhost:8080/api/courses/**  → Courses Service (8082)
   ```

2. **Load Balancing**:
   - Distributes requests across multiple service instances
   - Uses Eureka for service discovery
   - Implements client-side load balancing

3. **Cross-Cutting Concerns**:
   - Authentication/Authorization (JWT validation)
   - CORS configuration
   - Rate limiting
   - Request/Response logging
   - Circuit breaking

4. **Service Abstraction**:
   - Frontend only knows Gateway URL
   - Backend services can change ports/hosts without affecting frontend
   - Enables zero-downtime deployments

#### Gateway Route Configuration Example:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: learning-service
          uri: lb://LEARNING-SERVICE  # lb = load balanced via Eureka
          predicates:
            - Path=/api/learning/**
          filters:
            - StripPrefix=2  # Remove /api/learning from path
```

---

## Learning Service Details

### Service Overview

**Service Name**: `LEARNING-SERVICE`  
**Port**: 8086  
**Database**: PostgreSQL (learning_db)  
**Main Package**: `com.jungle.learning`

### Business Domain

The Learning Service manages educational content and assessments:
- **Quizzes**: Quiz creation, management, and publishing
- **Questions**: Multiple choice, true/false, and open-ended questions
- **Quiz Attempts**: Student quiz submissions and grading
- **Ebooks**: Digital learning materials with approval workflow

### Database Schema

#### Main Tables:

1. **quiz**
   - Stores quiz metadata (title, description, duration, passing score)
   - Publishing settings (published, publishAt, showAnswersTiming)
   - Categorization (category, difficulty, tags)

2. **question**
   - Quiz questions with different types (MCQ, TRUE_FALSE, OPEN)
   - Correct answers and point values
   - Order index for question sequencing

3. **quiz_attempt**
   - Student quiz attempts tracking
   - Status (IN_PROGRESS, COMPLETED, ABANDONED)
   - Scores and timestamps

4. **student_answer**
   - Individual answers for each question in an attempt
   - Correctness and points earned

5. **ebook**
   - Digital learning materials
   - Approval workflow (PENDING, PUBLISHED, REJECTED)
   - File storage and metadata

### Core Entities

#### Quiz Entity
```java
@Entity
@Table(name = "quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    private Long courseId;  // Reference to course in courses-service
    private Integer durationMinutes;
    private Integer maxScore;
    private Integer passingScore;
    private Boolean published;
    private LocalDateTime publishAt;
    
    // Relationships
    @OneToMany(mappedBy = "quiz")
    private List<Question> questions;
    
    @OneToMany(mappedBy = "quiz")
    private List<QuizAttempt> attempts;
}
```

#### QuizAttempt Entity
```java
@Entity
@Table(name = "quiz_attempt")
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    
    private Long studentId;  // Reference to user in auth-service
    private Integer score;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    
    @Enumerated(EnumType.STRING)
    private AttemptStatus status;
    
    @OneToMany(mappedBy = "attempt")
    private List<StudentAnswer> answers;
}
```

### REST API Endpoints

#### Quiz Management Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/learning/quizzes` | Get all quizzes | All |
| GET | `/api/learning/quizzes/{id}` | Get quiz by ID | All |
| POST | `/api/learning/quizzes` | Create new quiz | Tutor |
| PUT | `/api/learning/quizzes/{id}` | Update quiz | Tutor |
| DELETE | `/api/learning/quizzes/{id}` | Delete quiz | Tutor |
| PUT | `/api/learning/quizzes/{id}/publish` | Publish quiz | Tutor |

#### Question Management Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/learning/quizzes/{quizId}/questions` | Get quiz questions | All |
| POST | `/api/learning/questions` | Create question | Tutor |
| PUT | `/api/learning/questions/{id}` | Update question | Tutor |
| DELETE | `/api/learning/questions/{id}` | Delete question | Tutor |

#### Quiz Attempt Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/learning/attempts/start` | Start quiz attempt | Student |
| POST | `/api/learning/attempts/{id}/submit` | Submit quiz answers | Student |
| GET | `/api/learning/attempts/student/{studentId}` | Get student attempts | Student |
| GET | `/api/learning/attempts/{id}/result` | Get attempt result | Student |

#### Ebook Management Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/learning/ebooks` | Get all ebooks | All |
| POST | `/api/learning/ebooks` | Upload ebook | Tutor |
| PUT | `/api/learning/ebooks/{id}/approve` | Approve ebook | Admin |
| PUT | `/api/learning/ebooks/{id}/reject` | Reject ebook | Admin |
| GET | `/api/learning/ebooks/pending` | Get pending ebooks | Admin |

### Service Layer Architecture

```
Controller Layer (REST API)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Database (PostgreSQL)
```

#### Key Services:

1. **QuizService**
   - Quiz CRUD operations
   - Publishing logic
   - Validation

2. **QuestionService**
   - Question management
   - Order index handling

3. **QuizAttemptService**
   - Attempt lifecycle management
   - Score calculation
   - Result generation

4. **GradingService**
   - Answer validation
   - Point calculation
   - Grading logic

5. **EbookService**
   - File upload/download
   - Approval workflow
   - Status management

### Configuration Files

#### application.yml
```yaml
spring:
  application:
    name: LEARNING-SERVICE
  
  datasource:
    url: jdbc:postgresql://localhost:5432/learning_db
    username: ${DB_USERNAME}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8086

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

#### .env (Environment Variables)
```properties
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
```

### Dependencies (pom.xml)

Key dependencies:
- **Spring Boot Starter Web**: REST API development
- **Spring Boot Starter Data JPA**: Database access
- **PostgreSQL Driver**: Database connectivity
- **Spring Cloud Netflix Eureka Client**: Service discovery
- **Lombok**: Reduce boilerplate code
- **Spring Boot Starter Validation**: Input validation

---

## Angular ↔ API Connection

### Frontend Architecture

```
Angular Components
    ↓
Services (HTTP Clients)
    ↓
API Gateway (http://localhost:8080)
    ↓
Learning Service (via Eureka)
```

### Angular Service Example

#### quiz.service.ts
```typescript
@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private apiUrl = 'http://localhost:8080/api/learning';

  constructor(private http: HttpClient) {}

  // Get all quizzes
  getAllQuizzes(): Observable<Quiz[]> {
    return this.http.get<Quiz[]>(`${this.apiUrl}/quizzes`);
  }

  // Get quiz by ID
  getQuizById(id: number): Observable<Quiz> {
    return this.http.get<Quiz>(`${this.apiUrl}/quizzes/${id}`);
  }

  // Start quiz attempt
  startAttempt(quizId: number, studentId: number): Observable<QuizAttempt> {
    return this.http.post<QuizAttempt>(
      `${this.apiUrl}/attempts/start`,
      { quizId, studentId }
    );
  }

  // Submit quiz answers
  submitAttempt(attemptId: number, request: AttemptRequest): Observable<AttemptResult> {
    return this.http.post<AttemptResult>(
      `${this.apiUrl}/attempts/${attemptId}/submit`,
      request
    );
  }
}
```

### Request Flow Example

#### Taking a Quiz:

1. **Frontend Request**:
   ```typescript
   // Component
   this.quizService.startAttempt(quizId, studentId).subscribe(
     attempt => console.log('Attempt started:', attempt)
   );
   ```

2. **HTTP Request**:
   ```
   POST http://localhost:8080/api/learning/attempts/start
   Headers: {
     Authorization: Bearer <JWT_TOKEN>
     Content-Type: application/json
   }
   Body: {
     quizId: 1,
     studentId: 5
   }
   ```

3. **Gateway Processing**:
   - Validates JWT token
   - Checks CORS
   - Routes to LEARNING-SERVICE via Eureka
   - Strips `/api/learning` prefix

4. **Learning Service Processing**:
   ```
   POST http://localhost:8086/attempts/start
   ↓
   QuizAttemptController.startAttempt()
   ↓
   QuizAttemptService.startAttempt()
   ↓
   QuizAttemptRepository.save()
   ↓
   PostgreSQL Database
   ```

5. **Response Flow**:
   ```
   Database → Repository → Service → Controller
   ↓
   JSON Response
   ↓
   Gateway (adds CORS headers)
   ↓
   Angular Service
   ↓
   Component (updates UI)
   ```

### CORS Configuration

The Gateway handles CORS to allow Angular frontend to communicate:

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedOrigin("http://localhost:4201");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = 
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
}
```

### Authentication Flow

1. **Login** (Auth Service):
   ```
   POST /api/auth/login
   → Returns JWT token
   ```

2. **Authenticated Requests**:
   ```typescript
   // Angular Interceptor adds token to all requests
   @Injectable()
   export class AuthInterceptor implements HttpInterceptor {
     intercept(req: HttpRequest<any>, next: HttpHandler) {
       const token = localStorage.getItem('token');
       if (token) {
         req = req.clone({
           setHeaders: { Authorization: `Bearer ${token}` }
         });
       }
       return next.handle(req);
     }
   }
   ```

3. **Gateway Validation**:
   - Extracts JWT from Authorization header
   - Validates signature and expiration
   - Extracts user info (ID, role)
   - Forwards to microservice

4. **Service Authorization**:
   - Uses user info from JWT
   - Applies role-based access control
   - Returns appropriate response

### Data Models Synchronization

Frontend and backend models must match:

#### Backend (Java):
```java
public class QuizDTO {
    private Long id;
    private String title;
    private String description;
    private Integer durationMin;
    private Integer maxScore;
    private Integer passingScore;
    private Boolean published;
}
```

#### Frontend (TypeScript):
```typescript
export interface Quiz {
  id?: number;
  title: string;
  description: string;
  durationMin: number;
  maxScore: number;
  passingScore: number;
  published: boolean;
}
```

### Error Handling

#### Backend Exception Handling:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
        ResourceNotFoundException ex
    ) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }
}
```

#### Frontend Error Handling:
```typescript
this.quizService.getQuizById(id).subscribe({
  next: (quiz) => this.quiz = quiz,
  error: (error) => {
    if (error.status === 404) {
      this.showError('Quiz not found');
    } else {
      this.showError('An error occurred');
    }
  }
});
```

---

## Running the Service

### Prerequisites
- Java 17+
- PostgreSQL 12+
- Maven 3.6+

### Setup Steps

1. **Create Database**:
   ```sql
   CREATE DATABASE learning_db;
   ```

2. **Configure Environment**:
   ```bash
   # Create .env file
   DB_USERNAME=postgres
   DB_PASSWORD=your_password
   JWT_SECRET=your_secret_key
   ```

3. **Start Eureka Server**:
   ```bash
   cd backend/eureka-server
   mvn spring-boot:run
   ```

4. **Start API Gateway**:
   ```bash
   cd backend/api-gateway
   mvn spring-boot:run
   ```

5. **Start Learning Service**:
   ```bash
   cd backend/learning-service
   mvn spring-boot:run
   ```

6. **Verify Registration**:
   - Open http://localhost:8761
   - Check LEARNING-SERVICE is registered

### Testing Endpoints

```bash
# Get all quizzes
curl http://localhost:8080/api/learning/quizzes

# Get quiz by ID
curl http://localhost:8080/api/learning/quizzes/1

# Create quiz (with JWT token)
curl -X POST http://localhost:8080/api/learning/quizzes \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Quiz","maxScore":100}'
```

---

## Key Takeaways

1. **Microservices Architecture**: Independent, scalable services with dedicated databases
2. **Eureka**: Dynamic service discovery eliminates hardcoded URLs
3. **Gateway**: Single entry point providing routing, security, and load balancing
4. **Learning Service**: Manages quizzes, questions, attempts, and ebooks
5. **Angular Integration**: HTTP services communicate through Gateway to microservices
6. **Security**: JWT-based authentication validated at Gateway level
7. **Scalability**: Services can be scaled independently based on load

---

## Additional Resources

- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Eureka Server Guide](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Microservices Patterns](https://microservices.io/patterns/index.html)
