# Jungle in English - English Learning Platform

> **Developed at Esprit School of Engineering – Tunisia**  
> Academic Year: 2025-2026  
> Integrated Project (PI) - Class 3A/4A

A comprehensive microservices-based platform for English language learning, featuring interactive courses, real-time communication, and advanced authentication mechanisms.

---

## 📋 Overview

Jungle in English (EnglishFlow) is an innovative e-learning platform designed to facilitate English language acquisition through a modern, scalable architecture. Built with Angular 18 and Spring Boot 3, the platform leverages microservices architecture to provide a robust, maintainable, and feature-rich learning environment.

The platform serves three main user roles:
- **Students**: Access courses, track progress, and engage with learning materials
- **Teachers**: Create and manage courses, monitor student performance
- **Administrators**: Oversee platform operations and user management

---

## ✨ Features

### Authentication & Security
- ✅ JWT-based authentication with secure token management
- ✅ OAuth2 integration (Google Sign-In)
- ✅ Email verification and account activation
- ✅ Password reset functionality with secure tokens
- ✅ Two-Factor Authentication (2FA) support
- ✅ Role-based access control (RBAC)
- ✅ Rate limiting and brute-force protection

### User Management
- ✅ User registration with email validation
- ✅ Profile management with photo upload
- ✅ Multi-role support (Student, Teacher, Admin)
- 🚧 Social profile integration

### Course Management
- 🚧 Course creation and management
- 🚧 Interactive learning modules
- 🚧 Progress tracking and analytics
- 🚧 Multimedia content support

### Communication
- 🚧 Real-time notifications
- 🚧 Email notifications with professional templates
- 🚧 In-app messaging system

### Payment Integration
- 🚧 Secure payment processing
- 🚧 Subscription management
- 🚧 Invoice generation

### Community Features
- 🚧 Discussion forums
- 🚧 Student clubs and groups
- 🚧 Complaint management system

**Legend**: ✅ Implemented | 🚧 In Development

---

## 🛠️ Tech Stack

### Frontend
- **Framework**: Angular 18
- **Language**: TypeScript 5.5
- **Styling**: Tailwind CSS, Bootstrap 5
- **State Management**: RxJS
- **Charts**: ApexCharts, AmCharts 5
- **UI Components**: Angular CDK, FullCalendar, SweetAlert2
- **Security**: ng-recaptcha

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Architecture**: Microservices with Spring Cloud
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Configuration**: Spring Cloud Config Server
- **Security**: Spring Security, JWT (jjwt 0.11.5), OAuth2
- **Database**: PostgreSQL 14+
- **ORM**: Spring Data JPA
- **Email**: Spring Mail with Thymeleaf templates
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Monitoring**: Spring Actuator, Micrometer, Prometheus
- **Logging**: Logback with Logstash encoder
- **Build Tool**: Maven 3.8+

### DevOps & Tools
- **Version Control**: Git, GitHub
- **Containerization**: Docker
- **API Testing**: Postman

---

## 🏗️ Architecture

### Microservices Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Angular Frontend                        │
│                    (Port 4200)                              │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                        │
│              Load Balancing & Routing                        │
└────────────────────────┬────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
        ▼                ▼                ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Auth Service │  │ User Service │  │Course Service│
│   (8081)     │  │   (8082)     │  │   (8083)     │
└──────────────┘  └──────────────┘  └──────────────┘
        │                │                │
        ▼                ▼                ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  Payment     │  │ Notification │  │  Community   │
│  Service     │  │   Service    │  │   Service    │
│   (8084)     │  │   (8085)     │  │   (8086)     │
└──────────────┘  └──────────────┘  └──────────────┘
        │                │                │
        └────────────────┴────────────────┘
                         │
        ┌────────────────┴────────────────┐
        │                                  │
        ▼                                  ▼
┌──────────────────┐            ┌──────────────────┐
│  Eureka Server   │            │  Config Server   │
│     (8761)       │            │      (8888)      │
│Service Discovery │            │  Centralized     │
└──────────────────┘            │  Configuration   │
                                └──────────────────┘
```

### Project Structure

```
EnglishFlow-PI/
├── frontend/                    # Angular Application
│   ├── src/
│   │   ├── app/
│   │   │   ├── backoffice/     # Admin/Teacher/Student dashboards
│   │   │   ├── frontoffice/    # Public pages (landing, auth)
│   │   │   ├── core/           # Services, guards, interceptors
│   │   │   └── shared/         # Shared components, pipes, directives
│   │   ├── assets/             # Static resources
│   │   └── environments/       # Environment configurations
│   └── package.json
│
└── backend/                     # Spring Boot Microservices
    ├── config-server/          # Port 8888 - Centralized configuration
    ├── eureka-server/          # Port 8761 - Service registry
    ├── api-gateway/            # Port 8080 - API routing
    ├── auth-service/           # Port 8081 - Authentication & authorization
    ├── user-service/           # Port 8082 - User management
    ├── courses-service/        # Port 8083 - Course management
    ├── payment-service/        # Port 8084 - Payment processing
    ├── notification-service/   # Port 8085 - Email & notifications
    ├── community-service/      # Port 8086 - Forums & discussions
    ├── club-service/           # Port 8087 - Student clubs
    └── complaints-service/     # Port 8088 - Complaint management
```

---

## � Contributors

This project is developed by a team of students at Esprit School of Engineering as part of the Integrated Project (PI) curriculum.

### Development Team
- **Project Lead**: [Name]
- **Frontend Developers**: [Names]
- **Backend Developers**: [Names]
- **DevOps Engineer**: [Name]

### Academic Supervision
- **Academic Supervisor**: [Professor Name]
- **Technical Mentor**: [Mentor Name]

---

## 🎓 Academic Context

### Project Information
- **Institution**: Esprit School of Engineering, Tunisia
- **Program**: Software Engineering
- **Project Type**: Integrated Project (PI)
- **Academic Year**: 2025-2026
- **Class**: 3A/4A
- **Duration**: [Start Date] - [End Date]

### Learning Objectives
- Master microservices architecture design and implementation
- Develop full-stack applications with modern frameworks
- Implement secure authentication and authorization mechanisms
- Apply DevOps practices and CI/CD pipelines
- Work collaboratively using Agile methodologies
- Deliver production-ready software solutions

### Technologies Learned
- Spring Boot ecosystem and Spring Cloud
- Angular framework and TypeScript
- RESTful API design and implementation
- JWT and OAuth2 authentication
- PostgreSQL database design
- Docker containerization
- Git version control and collaboration

---

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed on your system:
- **Node.js** 18+ and npm
- **Java** 17 (JDK)
- **PostgreSQL** 14+
- **Maven** 3.8+
- **Git**

### Installation Steps

#### 1. Clone the Repository

```bash
git clone https://github.com/Khaalilabd/EnglishFlow-PI.git
cd EnglishFlow-PI
```

#### 2. Database Setup

Create the PostgreSQL database:

```bash
psql -U postgres
```

```sql
CREATE DATABASE englishflow_identity;
CREATE USER englishflow WITH PASSWORD 'englishflow123';
GRANT ALL PRIVILEGES ON DATABASE englishflow_identity TO englishflow;
\q
```

#### 3. Backend Configuration

Navigate to the auth-service and create environment file:

```bash
cd backend/auth-service
cp .env.example .env
```

Edit the `.env` file with your credentials:

```env
# Google OAuth Configuration
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-google-client-secret

# Frontend URL
FRONTEND_URL=http://localhost:4200

# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/englishflow_identity
DB_USERNAME=englishflow
DB_PASSWORD=englishflow123

# JWT Configuration
JWT_SECRET=your-secure-jwt-secret-key-minimum-256-bits
JWT_EXPIRATION=86400000

# Email Configuration (Gmail)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password

# reCAPTCHA Configuration
RECAPTCHA_SECRET_KEY=your-recaptcha-secret-key
```

**Important**: Never commit the `.env` file to version control. It contains sensitive credentials.

#### 4. Start Backend Services

**Option A: Using Scripts (Recommended)**

```bash
# From project root
./start-services.sh
```

**Option B: Manual Start**

Start each service in a separate terminal:

```bash
# Terminal 1 - Config Server
cd backend/config-server
mvn spring-boot:run

# Terminal 2 - Eureka Server
cd backend/eureka-server
mvn spring-boot:run

# Terminal 3 - Auth Service
cd backend/auth-service
mvn spring-boot:run

# Terminal 4 - API Gateway
cd backend/api-gateway
mvn spring-boot:run
```

Wait for each service to fully start before launching the next one.

#### 5. Start Frontend Application

```bash
cd frontend
npm install
npm start
```

#### 6. Access the Application

- **Frontend**: http://localhost:4200
- **Eureka Dashboard**: http://localhost:8761
- **Auth Service API**: http://localhost:8081
- **API Gateway**: http://localhost:8080
- **Swagger Documentation**: http://localhost:8081/swagger-ui.html

### Configuration Files

- **Frontend Environment**: `frontend/src/environments/`
- **Backend Properties**: `backend/*/src/main/resources/application.yml`
- **Centralized Config**: `backend/config-server/src/main/resources/`

---

## 📚 Documentation

### Service-Specific Documentation
- [Auth Service Setup Guide](./backend/auth-service/docs/README.md)
- [Gmail SMTP Configuration](./backend/auth-service/docs/GMAIL_SETUP.md)
- [OAuth2 Configuration](./backend/auth-service/docs/OAUTH2_SETUP.md)
- [2FA Implementation](./backend/auth-service/docs/2FA_IMPLEMENTATION.md)
- [API Documentation](./backend/auth-service/docs/API_DOCUMENTATION.md)
- [Monitoring Guide](./backend/auth-service/docs/MONITORING_GUIDE.md)

### API Documentation
Each microservice exposes Swagger/OpenAPI documentation at:
- `http://localhost:<port>/swagger-ui.html`
- `http://localhost:<port>/v3/api-docs`

### Postman Collection
Import the Postman collection for API testing:
- `backend/auth-service/postman_collection.json`

---

## 🔒 Security

### Best Practices Implemented
- Environment variables for sensitive data (never committed)
- JWT token-based authentication with secure secret keys
- Password hashing with BCrypt
- HTTPS enforcement in production
- CORS configuration for frontend-backend communication
- Rate limiting to prevent brute-force attacks
- Input validation and sanitization
- SQL injection prevention through JPA
- XSS protection headers

### Security Checklist
- ✅ All secrets stored in `.env` files (gitignored)
- ✅ Strong JWT secret keys (256+ bits)
- ✅ Password complexity requirements enforced
- ✅ Email verification for new accounts
- ✅ OAuth2 integration for social login
- ✅ Role-based access control (RBAC)
- ✅ API rate limiting enabled

---

## 🧪 Testing

### Running Tests

**Backend Tests**:
```bash
cd backend/auth-service
mvn test
```

**Frontend Tests**:
```bash
cd frontend
npm test
```

### Test Coverage
- Unit tests for service layer
- Integration tests for API endpoints
- Security tests for authentication flows

---

## 🚢 Deployment

### Recommended Hosting Platforms

#### Frontend Deployment
- **Vercel**: https://vercel.com (Recommended for Angular)
- **Netlify**: https://netlify.com
- **GitHub Pages**: https://pages.github.com
- **Firebase Hosting**: https://firebase.google.com/docs/hosting

#### Backend Deployment
- **Railway**: https://railway.app (Easy microservices deployment)
- **Render**: https://render.com (Free tier available)
- **DigitalOcean App Platform**: https://www.digitalocean.com/products/app-platform
- **Heroku**: https://heroku.com
- **AWS**: Amazon Web Services (for production-grade deployment)

### Docker Deployment

Build and run with Docker:

```bash
# Build Docker images
docker build -t englishflow-frontend ./frontend
docker build -t englishflow-auth ./backend/auth-service

# Run containers
docker-compose up -d
```

---

## 🤝 Contributing

This is an academic project. Contributions are limited to team members.

### Development Workflow
1. Create a feature branch from `develop`
2. Implement your feature with proper commits
3. Write tests for new functionality
4. Submit a pull request for code review
5. Merge to `develop` after approval

### Branch Strategy
- `main`: Production-ready code
- `develop`: Development integration branch
- `feature/*`: Feature development branches
- `bugfix/*`: Bug fix branches

### Commit Convention
Follow conventional commits:
```
feat: Add user profile photo upload
fix: Resolve JWT token expiration issue
docs: Update API documentation
refactor: Improve authentication service structure
test: Add unit tests for user service
```

---

## 📝 License

This project is developed for academic purposes at Esprit School of Engineering.  
All rights reserved © 2025-2026

---

## 🙏 Acknowledgments

### Special Thanks
- **Esprit School of Engineering** for providing the academic framework and resources
- **Project Supervisors** for their guidance and mentorship
- **Spring Boot & Angular Communities** for excellent documentation and support
- **Open Source Contributors** for the libraries and tools used in this project

### Resources & References
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Angular Documentation](https://angular.io/docs)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

---

## 📞 Contact & Support

For questions or support regarding this project:

- **GitHub Issues**: [Create an issue](https://github.com/Khaalilabd/EnglishFlow-PI/issues)
- **Project Repository**: https://github.com/Khaalilabd/EnglishFlow-PI
- **Institution**: Esprit School of Engineering, Tunisia

---

**Made with ❤️ by Esprit Engineering Students**
