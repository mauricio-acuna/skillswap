# 🔧 BACKEND AGENT - PENDING TASKS
**Last Updated:** 6 septiembre 2025 - 14:30  
**Status:** 🟡 READY TO START  
**Current Priority:** US-001 (Registration API)

---

## 🎯 **CURRENT FOCUS**

### **📋 US-001: User Registration API** 
**Priority:** 🔴 CRITICAL  
**Story Points:** 5  
**Dependencies:** None  
**Deadline:** 8 septiembre (Day 3)

**📍 EXACTLY WHAT TO DO NEXT:**

1. **Setup Spring Boot Project Structure**
   ```bash
   # In this directory (skillswap-backend/):
   ./gradlew init --type java-application
   # OR maven equivalent
   ```

2. **Create User Entity**
   ```java
   // File: src/main/java/com/skillswap/entity/User.java
   @Entity
   @Table(name = "users")
   public class User {
       @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       
       @Column(unique = true, nullable = false)
       @Email
       private String email;
       
       @Column(nullable = false)
       private String password; // Will be hashed
       
       // Add firstName, lastName, createdAt, etc.
   }
   ```

3. **Implement Registration Endpoint**
   ```java
   // File: src/main/java/com/skillswap/controller/AuthController.java
   @RestController
   @RequestMapping("/api/v1/auth")
   public class AuthController {
       
       @PostMapping("/register")
       public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
           // Validate email uniqueness
           // Hash password with BCrypt
           // Save user to database
           // Generate JWT token
           // Return response with token
       }
   }
   ```

---

## 📋 **ACCEPTANCE CRITERIA CHECKLIST**

### **Must Complete Today:**
- [ ] Spring Boot project starts without errors
- [ ] User entity with proper validations
- [ ] Basic registration endpoint structure
- [ ] JWT token generation working

### **Must Complete Tomorrow:**
- [ ] Registration endpoint fully functional
- [ ] Password hashing with BCrypt
- [ ] Email uniqueness validation
- [ ] Error handling for bad requests
- [ ] Basic integration tests

---

## 🔗 **API SPECIFICATION TO IMPLEMENT**

**Endpoint:** `POST /api/v1/auth/register`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "acceptTerms": true
}
```

**Success Response (201):**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": 1,
      "email": "user@example.com", 
      "firstName": "John",
      "lastName": "Doe"
    },
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "refresh_token_here"
  },
  "message": "User registered successfully"
}
```

**Error Responses:**
- **400:** Validation errors (email format, password strength)
- **409:** Email already exists
- **500:** Server error

---

## 🛠️ **TECHNICAL REQUIREMENTS**

### **Dependencies to Add:**
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    runtimeOnly 'com.h2database:h2' // Development
    runtimeOnly 'org.postgresql:postgresql' // Production
}
```

### **Configuration Needed:**
```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:h2:mem:skillswap
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    
jwt:
  secret: your-secret-key-here
  expiration: 86400000 # 24 hours
```

---

## 📊 **PROGRESS TRACKING**

### **Daily Commits Expected:**
```bash
# Day 1 (Today):
feat(US-001): setup spring boot project structure ✅
feat(US-001): add user entity with validations

# Day 2:
feat(US-001): implement registration endpoint
feat(US-001): add JWT token generation  
test(US-001): add basic integration tests

# Day 3:
feat(US-001): complete registration API with error handling ✅
docs(US-001): update API documentation
```

---

## 🔄 **NEXT TASKS AFTER US-001**

### **US-007: Login API (3 pts)**
- Endpoint: `POST /api/v1/auth/login`
- Validate credentials
- Generate JWT tokens
- Rate limiting for security

### **US-003: Profile API (3 pts)**  
- Endpoint: `GET/PUT /api/v1/users/profile`
- Profile management
- File upload for avatars

---

## 🚨 **BLOCKERS & QUESTIONS**

### **No Current Blockers** ✅

### **If You Have Questions:**
1. **Technical:** Comment in commit message with `@Product-Manager`
2. **Requirements:** Ask specific questions about acceptance criteria  
3. **Architecture:** Reference the main API contract in `../shared-docs/API-Contract.md`

---

## 🎯 **SUCCESS DEFINITION**

**US-001 is DONE when:**
- [ ] Frontend Agent can successfully call `/api/v1/auth/register`
- [ ] New users are stored in database with hashed passwords
- [ ] JWT tokens are generated and valid
- [ ] All acceptance criteria are met
- [ ] Integration tests pass
- [ ] Code review approved by Product Manager

---

**🔄 WHEN STUCK:** Just write "revisa y continúa" and I'll analyze your progress + give next specific steps!

**📞 COMMUNICATION:** Commit messages with progress updates expected every 2-3 hours during development.

**🎊 Ready to start? Let's build this! 🚀**
