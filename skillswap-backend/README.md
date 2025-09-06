# SkillSwap Backend - PRD Específico
## Spring Boot API & Database Architecture

### 🎯 SCOPE DEL BACKEND

Este documento es específico para el desarrollo del **backend de SkillSwap**. Para contexto completo, consultar:
- `../shared-docs/PRD.md` - PRD general del proyecto
- `../shared-docs/SkillSwap-TechnicalSpecs.md` - Especificaciones técnicas
- `../shared-docs/MercadoObjetivo.md` - Análisis de mercado

---

## 📋 RESPONSABILIDADES DEL BACKEND

### Core Responsibilities
1. **Authentication & Authorization** (JWT + OAuth2 + GDPR)
2. **User Management** (profiles, skills, preferences)
3. **Skill Taxonomy** (categorization, verification)
4. **Matching Algorithm** (P2P skill matching with ML)
5. **Credit System** (earn/spend credits, transactions)
6. **Session Management** (scheduling, history, ratings)
7. **Real-time Features** (notifications, live updates)
8. **Video Call Integration** (Agora.io SDK integration)
9. **European Compliance** (GDPR, multi-language)
10. **Analytics & Monitoring** (metrics, performance)

---

## 🏗️ ARQUITECTURA BACKEND

### Tech Stack
```
┌─────────────────────────────────────────────────────────────┐
│                     SKILLSWAP BACKEND                       │
├─────────────────────────────────────────────────────────────┤
│  Spring Boot 3.1+ (Java 17)                               │
│  ├── Spring Security 6 (JWT + OAuth2)                     │
│  ├── Spring Data JPA (PostgreSQL)                         │
│  ├── Spring WebSocket (Real-time)                         │
│  ├── Spring Cache (Redis)                                 │
│  ├── Spring Validation (Input validation)                 │
│  └── Spring Actuator (Monitoring)                         │
├─────────────────────────────────────────────────────────────┤
│  Database Layer                                            │
│  ├── PostgreSQL 15+ (Production)                          │
│  ├── H2 Database (Development)                            │
│  ├── Redis 7+ (Session cache)                             │
│  └── Flyway (Database migrations)                         │
├─────────────────────────────────────────────────────────────┤
│  External Integrations                                     │
│  ├── Agora.io (Video calling)                            │
│  ├── Google Calendar API                                  │
│  ├── SendGrid (Email notifications)                       │
│  ├── Firebase (Push notifications)                        │
│  └── Stripe (Payment processing)                          │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 ESTRUCTURA DE CARPETAS BACKEND

```
skillswap-backend/
├── src/main/java/com/skillswap/
│   ├── SkillswapApplication.java           # Main application
│   ├── config/                             # Configuration classes
│   │   ├── SecurityConfig.java             # JWT + OAuth2 setup
│   │   ├── DatabaseConfig.java             # DB configuration
│   │   ├── RedisConfig.java                # Cache configuration
│   │   ├── WebSocketConfig.java            # Real-time config
│   │   ├── CorsConfig.java                 # CORS for mobile apps
│   │   └── SwaggerConfig.java              # API documentation
│   ├── controller/                         # REST Controllers
│   │   ├── AuthController.java             # Authentication endpoints
│   │   ├── UserController.java             # User management
│   │   ├── SkillController.java            # Skill operations
│   │   ├── MatchController.java            # Matching system
│   │   ├── SessionController.java          # Session management
│   │   ├── CreditController.java           # Credit transactions
│   │   ├── NotificationController.java     # Push notifications
│   │   └── AdminController.java            # Admin operations
│   ├── service/                            # Business Logic
│   │   ├── impl/                           # Service implementations
│   │   ├── AuthService.java                # Authentication logic
│   │   ├── UserService.java                # User operations
│   │   ├── SkillService.java               # Skill management
│   │   ├── MatchingService.java            # Core matching algorithm
│   │   ├── CreditService.java              # Credit system
│   │   ├── SessionService.java             # Session lifecycle
│   │   ├── NotificationService.java        # Push notifications
│   │   ├── EmailService.java               # Email notifications
│   │   ├── VideoCallService.java           # Agora integration
│   │   ├── CalendarService.java            # Calendar sync
│   │   ├── GdprService.java                # GDPR compliance
│   │   └── AnalyticsService.java           # Metrics & analytics
│   ├── repository/                         # Data Access Layer
│   │   ├── UserRepository.java             # User CRUD + queries
│   │   ├── SkillRepository.java            # Skill operations
│   │   ├── UserSkillRepository.java        # User-Skill relationships
│   │   ├── MatchRepository.java            # Matching data
│   │   ├── SessionRepository.java          # Session history
│   │   ├── CreditTransactionRepository.java # Credit history
│   │   ├── NotificationRepository.java     # Notification queue
│   │   └── AnalyticsRepository.java        # Analytics queries
│   ├── model/                              # JPA Entities
│   │   ├── entity/                         # Database entities
│   │   │   ├── User.java                   # User entity
│   │   │   ├── Skill.java                  # Skill entity
│   │   │   ├── UserSkill.java              # User-Skill mapping
│   │   │   ├── Match.java                  # Match entity
│   │   │   ├── Session.java                # Session entity
│   │   │   ├── CreditTransaction.java      # Credit history
│   │   │   ├── Notification.java           # Notification entity
│   │   │   └── BaseEntity.java             # Common fields
│   │   ├── dto/                            # Data Transfer Objects
│   │   │   ├── request/                    # Request DTOs
│   │   │   │   ├── AuthRequest.java
│   │   │   │   ├── UserProfileRequest.java
│   │   │   │   ├── SkillRequest.java
│   │   │   │   ├── MatchRequest.java
│   │   │   │   └── SessionRequest.java
│   │   │   ├── response/                   # Response DTOs
│   │   │   │   ├── AuthResponse.java
│   │   │   │   ├── UserProfileResponse.java
│   │   │   │   ├── SkillResponse.java
│   │   │   │   ├── MatchResponse.java
│   │   │   │   └── SessionResponse.java
│   │   │   └── common/                     # Common DTOs
│   │   │       ├── ApiResponse.java
│   │   │       ├── ErrorResponse.java
│   │   │       └── PaginatedResponse.java
│   │   └── enums/                          # Enumerations
│   │       ├── SkillCategory.java          # Skill categories
│   │       ├── SkillLevel.java             # Beginner/Intermediate/Advanced
│   │       ├── MatchStatus.java            # Pending/Accepted/Completed
│   │       ├── SessionStatus.java          # Scheduled/InProgress/Completed
│   │       └── UserRole.java               # User/Premium/Admin
│   ├── security/                           # Security components
│   │   ├── JwtAuthenticationEntryPoint.java # JWT entry point
│   │   ├── JwtAuthenticationFilter.java    # JWT filter
│   │   ├── JwtTokenProvider.java           # JWT utilities
│   │   ├── CustomUserDetailsService.java  # User details
│   │   ├── OAuth2UserService.java          # OAuth2 integration
│   │   └── GdprComplianceFilter.java       # GDPR headers
│   ├── exception/                          # Exception handling
│   │   ├── GlobalExceptionHandler.java     # Global error handler
│   │   ├── custom/                         # Custom exceptions
│   │   │   ├── UserNotFoundException.java
│   │   │   ├── SkillNotFoundException.java
│   │   │   ├── InvalidMatchException.java
│   │   │   ├── InsufficientCreditsException.java
│   │   │   └── GdprViolationException.java
│   │   └── ErrorCode.java                  # Error code constants
│   ├── utils/                              # Utility classes
│   │   ├── DateUtils.java                  # Date/time utilities
│   │   ├── ValidationUtils.java            # Custom validations
│   │   ├── EncryptionUtils.java            # Data encryption
│   │   ├── LocalizationUtils.java          # i18n utilities
│   │   └── MatchingAlgorithmUtils.java     # Matching helpers
│   ├── websocket/                          # WebSocket handlers
│   │   ├── NotificationWebSocketHandler.java # Real-time notifications
│   │   ├── MatchingWebSocketHandler.java   # Live matching updates
│   │   └── SessionWebSocketHandler.java    # Session real-time
│   └── algorithm/                          # ML & Algorithms
│       ├── MatchingAlgorithm.java          # Core matching logic
│       ├── SkillCompatibilityCalculator.java # Skill matching
│       ├── UserRecommendationEngine.java   # User recommendations
│       └── CreditCalculator.java           # Credit calculations
├── src/main/resources/
│   ├── application.yml                     # Main configuration
│   ├── application-dev.yml                 # Development config (H2)
│   ├── application-prod.yml                # Production config (PostgreSQL)
│   ├── application-test.yml                # Test configuration
│   ├── db/migration/                       # Flyway migrations
│   │   ├── V1__Create_users_table.sql
│   │   ├── V2__Create_skills_table.sql
│   │   ├── V3__Create_user_skills_table.sql
│   │   ├── V4__Create_matches_table.sql
│   │   ├── V5__Create_sessions_table.sql
│   │   ├── V6__Create_credit_transactions_table.sql
│   │   └── V7__Create_notifications_table.sql
│   ├── static/                             # Static resources
│   ├── templates/                          # Email templates
│   │   ├── welcome-email.html
│   │   ├── match-notification.html
│   │   ├── session-reminder.html
│   │   └── gdpr-data-export.html
│   ├── i18n/                              # Internationalization
│   │   ├── messages_en.properties
│   │   ├── messages_es.properties
│   │   ├── messages_fr.properties
│   │   ├── messages_de.properties
│   │   └── messages_it.properties
│   └── skills/                            # Skill taxonomy data
│       ├── skill-categories.json
│       ├── skill-subcategories.json
│       └── default-skills.json
├── src/test/java/com/skillswap/            # Test classes
│   ├── controller/                         # Controller tests
│   ├── service/                            # Service tests
│   ├── repository/                         # Repository tests
│   ├── integration/                        # Integration tests
│   ├── TestSkillswapApplication.java       # Test application
│   └── config/                             # Test configurations
├── docker/                                 # Docker configuration
│   ├── Dockerfile                          # Application container
│   ├── docker-compose.yml                  # Local development
│   ├── docker-compose.prod.yml             # Production setup
│   └── init-db.sql                         # Database initialization
├── docs/                                   # Backend documentation
│   ├── api/                                # API documentation
│   │   ├── authentication.md
│   │   ├── user-management.md
│   │   ├── skill-management.md
│   │   ├── matching-system.md
│   │   ├── session-management.md
│   │   └── credit-system.md
│   ├── database/                           # Database documentation
│   │   ├── schema.md
│   │   ├── migrations.md
│   │   └── queries.md
│   └── deployment/                         # Deployment guides
│       ├── local-setup.md
│       ├── production-deployment.md
│       └── monitoring.md
├── pom.xml                                 # Maven configuration
├── README.md                               # Backend-specific README
├── .gitignore                              # Git ignore rules
├── .env.example                            # Environment variables template
└── postman/                                # API testing
    ├── SkillSwap-API.postman_collection.json
    └── SkillSwap-ENV.postman_environment.json
```

---

## 🔌 API ENDPOINTS PRINCIPALES

### Authentication
```
POST   /api/auth/register           # User registration
POST   /api/auth/login              # User login
POST   /api/auth/refresh            # Refresh JWT token
POST   /api/auth/logout             # User logout
POST   /api/auth/forgot-password    # Password reset
GET    /api/auth/oauth2/{provider}  # OAuth2 login
```

### User Management
```
GET    /api/users/profile           # Get user profile
PUT    /api/users/profile           # Update user profile
GET    /api/users/{id}              # Get user by ID
DELETE /api/users/profile           # Delete account (GDPR)
POST   /api/users/export-data       # Export user data (GDPR)
```

### Skill Management
```
GET    /api/skills                  # Get all skills
GET    /api/skills/categories       # Get skill categories
POST   /api/skills/user-skills      # Add skill to user
DELETE /api/skills/user-skills/{id} # Remove user skill
PUT    /api/skills/user-skills/{id} # Update skill level
GET    /api/skills/user-skills      # Get user skills
```

### Matching System
```
GET    /api/matches/suggestions     # Get match suggestions
POST   /api/matches/request         # Send match request
PUT    /api/matches/{id}/accept     # Accept match
PUT    /api/matches/{id}/decline    # Decline match
GET    /api/matches                 # Get user matches
```

### Session Management
```
POST   /api/sessions                # Create session
GET    /api/sessions                # Get user sessions
PUT    /api/sessions/{id}           # Update session
DELETE /api/sessions/{id}           # Cancel session
POST   /api/sessions/{id}/join      # Join video session
POST   /api/sessions/{id}/rate      # Rate completed session
```

### Credit System
```
GET    /api/credits/balance         # Get credit balance
GET    /api/credits/transactions    # Get credit history
POST   /api/credits/transfer        # Transfer credits
```

---

## 🗄️ ESQUEMA DE BASE DE DATOS

### Core Tables
```sql
-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    profile_image_url VARCHAR(500),
    bio TEXT,
    location VARCHAR(255),
    timezone VARCHAR(50),
    language_preference VARCHAR(10) DEFAULT 'en',
    role VARCHAR(20) DEFAULT 'USER',
    email_verified BOOLEAN DEFAULT FALSE,
    gdpr_consent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Skills table
CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    subcategory VARCHAR(100),
    description TEXT,
    difficulty_level VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Skills mapping
CREATE TABLE user_skills (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    skill_id BIGINT REFERENCES skills(id) ON DELETE CASCADE,
    can_teach BOOLEAN DEFAULT FALSE,
    wants_to_learn BOOLEAN DEFAULT FALSE,
    skill_level VARCHAR(20) NOT NULL, -- BEGINNER, INTERMEDIATE, ADVANCED
    years_experience INTEGER,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, skill_id)
);

-- Matches table
CREATE TABLE matches (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    learner_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    skill_id BIGINT REFERENCES skills(id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, ACCEPTED, DECLINED, COMPLETED
    match_score DECIMAL(3,2), -- 0.00 to 1.00
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sessions table
CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT REFERENCES matches(id) ON DELETE CASCADE,
    scheduled_at TIMESTAMP NOT NULL,
    duration_minutes INTEGER DEFAULT 60,
    status VARCHAR(20) DEFAULT 'SCHEDULED', -- SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    video_room_id VARCHAR(255),
    teacher_rating INTEGER CHECK (teacher_rating >= 1 AND teacher_rating <= 5),
    learner_rating INTEGER CHECK (learner_rating >= 1 AND learner_rating <= 5),
    teacher_feedback TEXT,
    learner_feedback TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Credit Transactions table
CREATE TABLE credit_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    amount INTEGER NOT NULL, -- Positive for earned, negative for spent
    transaction_type VARCHAR(20) NOT NULL, -- EARNED, SPENT, BONUS, PENALTY
    session_id BIGINT REFERENCES sessions(id),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🔧 CONFIGURACIÓN Y SETUP

### Dependencies (pom.xml principales)
```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Migration -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Documentation -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.1.0</version>
    </dependency>
</dependencies>
```

---

## 🚀 ROADMAP BACKEND (8 Sprints)

### Sprint 1-2: Foundation (4 semanas)
- [x] Project setup con Spring Boot 3.1+
- [ ] Database configuration (H2 + PostgreSQL profiles)
- [ ] Basic security setup (JWT + CORS)
- [ ] User entity y authentication endpoints
- [ ] Basic CRUD operations para usuarios
- [x] Swagger documentation setup
- [x] Docker configuration

### Sprint 3-4: Core Entities (4 semanas)
- [ ] Skill management system
- [ ] User-Skill relationships
- [ ] Basic matching algorithm
- [ ] Credit system implementation
- [ ] Session management
- [ ] Email notification service
- [ ] Unit testing setup

### Sprint 5-6: Advanced Features (4 semanas)
- [ ] Advanced matching algorithm con ML
- [ ] Real-time WebSocket implementation
- [ ] Video call integration (Agora.io)
- [ ] Calendar integration (Google Calendar)
- [ ] Push notification service
- [ ] GDPR compliance features
- [ ] Integration testing

### Sprint 7-8: Production Ready (4 semanas)
- [ ] Performance optimization
- [ ] Security hardening
- [ ] Monitoring y analytics
- [ ] Production database migration
- [ ] Load testing
- [ ] API documentation completa
- [ ] Production deployment

---

## 🔒 SECURITY & COMPLIANCE

### JWT Implementation
- **Access Token:** 15 minutos de duración
- **Refresh Token:** 7 días de duración
- **Algoritmo:** HS256 con secret de 256 bits
- **Claims:** user_id, email, role, permissions

### GDPR Compliance
- **Data Minimization:** Solo datos necesarios
- **Consent Management:** Granular consent tracking
- **Right to Erasure:** Complete data deletion
- **Data Portability:** Export en formato JSON
- **Audit Trail:** Log de todas las operaciones con datos personales

### European Data Protection
- **Data Encryption:** AES-256 para datos sensibles
- **Database Encryption:** PostgreSQL TDE en producción
- **API Rate Limiting:** 1000 requests/hour por usuario
- **Session Security:** Redis con TTL automático

---

## 📊 MONITORING & ANALYTICS

### Health Checks
```
GET /actuator/health           # Application health
GET /actuator/metrics          # Application metrics
GET /actuator/prometheus       # Prometheus metrics
GET /actuator/info             # Application info
```

### Key Metrics to Track
- **User Registrations** por día/semana
- **Match Success Rate** (requests → accepted matches)
- **Session Completion Rate** (scheduled → completed)
- **API Response Times** por endpoint
- **Database Query Performance**
- **Cache Hit Rates** (Redis)
- **Error Rates** por endpoint
- **Credit Transaction Volume**

---

## 🤝 INTEGRACIÓN CON FRONTEND

### API Contract
- **Base URL:** `https://api.skillswap.com/api/v1`
- **Authentication:** Bearer token en header
- **Response Format:** JSON con estructura consistente
- **Error Handling:** HTTP status codes + error details
- **Pagination:** Cursor-based para performance
- **Versioning:** URL path versioning (`/api/v1`, `/api/v2`)

### WebSocket Events
```javascript
// Real-time events que el frontend debe manejar
'match_request_received'    // Nueva solicitud de match
'match_accepted'           // Match aceptado
'session_scheduled'        // Nueva sesión programada
'session_starting'         // Sesión por empezar (5 min)
'credit_transaction'       // Cambio en balance de créditos
'notification_received'    // Nueva notificación general
```

---

## 📝 TESTING STRATEGY

### Unit Testing
- **JUnit 5** para lógica de negocio
- **Mockito** para mocking dependencies
- **TestContainers** para integration testing con DB
- **Coverage mínimo:** 80% para services y controllers

### Integration Testing
- **SpringBootTest** para context loading
- **WebMvcTest** para controller testing
- **DataJpaTest** para repository testing
- **TestContainers PostgreSQL** para DB integration

### Performance Testing
- **JMeter** para load testing
- **Target:** 1000 concurrent users
- **Response time:** < 200ms para 95% de requests
- **Throughput:** 10,000 requests/minute

---

*Documento específico para desarrollo del Backend de SkillSwap*
*Para coordinarse con Frontend, consultar: `../shared-docs/`*
*Última actualización: 6 de septiembre de 2025*
