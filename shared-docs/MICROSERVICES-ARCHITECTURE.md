# 🏗️ MICROSERVICES ARCHITECTURE - ENTERPRISE TRANSITION
## From Monolith to Microservices (Uber, Airbnb, Spotify Model)

**Migration Timeline:** 12-18 months  
**Target Scale:** 1M+ users, 100+ developers  
**Service Decomposition:** Domain-driven design  
**Technology Stack:** Spring Boot, Kubernetes, Event-driven

---

## 🎯 **MICROSERVICES EVOLUTION ROADMAP**

### **Phase 1: Monolith Optimization (Months 1-3)**
```
Current State: Single Spring Boot application
└── Prepare for decomposition
    ├── Implement domain boundaries
    ├── Extract shared libraries
    ├── Add distributed tracing
    └── Implement event sourcing patterns
```

### **Phase 2: Strategic Decomposition (Months 4-9)**
```
Extract Core Services (Airbnb model)
├── User Service (Identity & Profile)
├── Skill Service (Catalog & Matching)
├── Session Service (Booking & Management)
├── Payment Service (Credits & Transactions)
├── Notification Service (Push & Email)
└── Analytics Service (Metrics & Insights)
```

### **Phase 3: Advanced Services (Months 10-18)**
```
Scale & Optimize (Spotify model)
├── ML Recommendation Service
├── Search & Discovery Service
├── Content Moderation Service
├── Audit & Compliance Service
├── Real-time Communication Service
└── Data Processing Pipeline
```

---

## 🔧 **SERVICE DECOMPOSITION STRATEGY**

### **1. USER SERVICE - Identity & Profile Management**

#### **Service Boundaries:**
```yaml
# user-service responsibilities
domain: User Identity & Profile
bounded_context:
  - User registration/authentication
  - Profile management
  - Privacy settings
  - Account preferences
  - GDPR compliance (data export/deletion)

database: PostgreSQL (isolated schema)
cache: Redis (user sessions)
storage: S3 (profile pictures)

api_endpoints:
  authentication:
    - POST /auth/register
    - POST /auth/login
    - POST /auth/refresh
    - POST /auth/logout
    
  profile:
    - GET /users/{id}
    - PUT /users/{id}
    - DELETE /users/{id}
    - GET /users/{id}/privacy-settings
    
  admin:
    - GET /users (paginated)
    - POST /users/{id}/suspend
    - POST /users/{id}/verify
```

#### **User Service Implementation:**
```java
// UserService.java - Microservice architecture
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    
    private final UserService userService;
    private final EventPublisher eventPublisher;
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        
        // Publish domain event (Event-driven architecture)
        eventPublisher.publish(new UserRegisteredEvent(
            user.getId(), 
            user.getEmail(), 
            user.getRegistrationTime()
        ));
        
        return ResponseEntity.status(CREATED)
            .body(UserResponse.from(user));
    }
    
    @GetMapping("/{userId}")
    @PreAuthorize("hasPermission(#userId, 'USER', 'READ')")
    public ResponseEntity<UserProfileResponse> getProfile(
            @PathVariable UUID userId,
            @RequestHeader("X-Requesting-User") UUID requestingUserId) {
        
        UserProfile profile = userService.getProfile(userId, requestingUserId);
        return ResponseEntity.ok(UserProfileResponse.from(profile));
    }
}

// Event-driven communication
@Component
public class UserEventHandler {
    
    @EventListener
    @Async
    public void handleUserRegistered(UserRegisteredEvent event) {
        // Create analytics profile
        analyticsService.createUserProfile(event.getUserId());
        
        // Send welcome email
        notificationService.sendWelcomeEmail(event.getEmail());
        
        // Initialize recommendation engine
        recommendationService.initializeUserPreferences(event.getUserId());
    }
}
```

---

### **2. SKILL SERVICE - Catalog & Matching Engine**

#### **Service Architecture:**
```yaml
# skill-service responsibilities  
domain: Skills & Matching
bounded_context:
  - Skill catalog management
  - Skill categories & taxonomy
  - User skill profiles
  - Matching algorithm
  - Skill verification

database: PostgreSQL + Elasticsearch
cache: Redis (matching results)
ml_platform: AWS SageMaker (recommendation models)

advanced_features:
  - Real-time skill matching
  - ML-powered recommendations
  - Skill trend analysis
  - Geographic skill mapping
  - Competency assessments
```

#### **Skill Matching Implementation:**
```java
// SkillMatchingService.java - Advanced matching algorithm
@Service
public class SkillMatchingService {
    
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final MatchingAlgorithm matchingAlgorithm;
    private final RecommendationEngine recommendationEngine;
    
    @Cacheable(value = "skill-matches", key = "#userId")
    public List<SkillMatch> findMatches(UUID userId, MatchingCriteria criteria) {
        
        // Get user's skills and preferences
        UserSkillProfile userProfile = userSkillRepository.findByUserId(userId);
        
        // Geographic filtering (Uber-style location matching)
        List<User> nearbyUsers = findNearbyUsers(userProfile.getLocation(), criteria.getMaxDistance());
        
        // Skill compatibility scoring
        List<SkillMatch> potentialMatches = nearbyUsers.stream()
            .map(user -> calculateSkillCompatibility(userProfile, user))
            .filter(match -> match.getCompatibilityScore() > 0.7)
            .sorted(Comparator.comparing(SkillMatch::getCompatibilityScore).reversed())
            .limit(50)
            .collect(toList());
        
        // ML-enhanced ranking (Spotify-style personalization)
        return recommendationEngine.personalizeMatches(userId, potentialMatches);
    }
    
    private SkillMatch calculateSkillCompatibility(UserSkillProfile seeker, User provider) {
        // Multi-factor compatibility algorithm
        double skillScore = calculateSkillOverlap(seeker.getWantedSkills(), provider.getOfferedSkills());
        double ratingScore = provider.getAverageRating() / 5.0;
        double availabilityScore = calculateAvailabilityMatch(seeker.getAvailability(), provider.getAvailability());
        double priceScore = calculatePriceCompatibility(seeker.getBudget(), provider.getHourlyRate());
        
        // Weighted composite score
        double finalScore = (skillScore * 0.4) + (ratingScore * 0.3) + 
                           (availabilityScore * 0.2) + (priceScore * 0.1);
        
        return SkillMatch.builder()
            .seekerId(seeker.getUserId())
            .providerId(provider.getId())
            .compatibilityScore(finalScore)
            .skillOverlap(skillScore)
            .estimatedHourlyRate(provider.getHourlyRate())
            .build();
    }
}
```

---

### **3. SESSION SERVICE - Booking & Session Management**

#### **Service Design:**
```yaml
# session-service responsibilities
domain: Learning Sessions
bounded_context:
  - Session booking/scheduling
  - Session lifecycle management
  - Video call integration
  - Session history & ratings
  - Cancellation policies

integrations:
  video_calls: Zoom/Teams SDK
  calendar: Google Calendar/Outlook
  payments: Stripe/PayPal
  notifications: SNS/FCM

session_states:
  - REQUESTED → ACCEPTED → SCHEDULED → IN_PROGRESS → COMPLETED
  - REQUESTED → DECLINED
  - SCHEDULED → CANCELLED (with policies)
```

#### **Session Workflow Implementation:**
```java
// SessionWorkflowService.java - State machine pattern
@Service
public class SessionWorkflowService {
    
    private final SessionRepository sessionRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final VideoCallService videoCallService;
    
    @Transactional
    public Session requestSession(SessionRequest request) {
        // Validate availability
        validateProviderAvailability(request.getProviderId(), request.getScheduledTime());
        
        // Create session with initial state
        Session session = Session.builder()
            .seekerId(request.getSeekerId())
            .providerId(request.getProviderId())
            .skillId(request.getSkillId())
            .scheduledTime(request.getScheduledTime())
            .duration(request.getDuration())
            .status(SessionStatus.REQUESTED)
            .hourlyRate(request.getHourlyRate())
            .totalCredits(calculateTotalCredits(request))
            .build();
        
        sessionRepository.save(session);
        
        // Hold credits (pre-authorization)
        paymentService.holdCredits(request.getSeekerId(), session.getTotalCredits());
        
        // Notify provider
        notificationService.sendSessionRequest(session);
        
        // Publish domain event
        eventPublisher.publish(new SessionRequestedEvent(session));
        
        return session;
    }
    
    @Transactional
    public Session acceptSession(UUID sessionId, UUID providerId) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new SessionNotFoundException(sessionId));
        
        // Validate state transition
        if (session.getStatus() != SessionStatus.REQUESTED) {
            throw new InvalidSessionStateException("Cannot accept session in state: " + session.getStatus());
        }
        
        // Update session
        session.accept(providerId);
        sessionRepository.save(session);
        
        // Create video call room
        VideoCallRoom room = videoCallService.createRoom(session);
        session.setVideoCallRoomId(room.getId());
        
        // Schedule automated reminders
        notificationService.scheduleSessionReminders(session);
        
        // Publish event
        eventPublisher.publish(new SessionAcceptedEvent(session));
        
        return session;
    }
    
    @Transactional
    public Session completeSession(UUID sessionId, SessionFeedback feedback) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new SessionNotFoundException(sessionId));
        
        // Complete session
        session.complete(feedback);
        sessionRepository.save(session);
        
        // Transfer credits to provider
        paymentService.transferCredits(
            session.getSeekerId(), 
            session.getProviderId(), 
            session.getTotalCredits()
        );
        
        // Update user ratings
        updateUserRatings(session, feedback);
        
        // Archive video call
        videoCallService.archiveRecording(session.getVideoCallRoomId());
        
        // Publish completion event
        eventPublisher.publish(new SessionCompletedEvent(session, feedback));
        
        return session;
    }
}
```

---

### **4. PAYMENT SERVICE - Credits & Transactions**

#### **Financial Architecture:**
```yaml
# payment-service responsibilities
domain: Financial Transactions
bounded_context:
  - Credit wallet management
  - Payment processing
  - Transaction history
  - Refunds & disputes
  - Revenue sharing
  - Tax compliance

payment_flows:
  credit_purchase: User → Stripe → Credit Wallet
  session_payment: Seeker Credits → Hold → Provider (on completion)
  withdrawal: Provider Credits → Bank Account (weekly)
  
security:
  - PCI DSS compliance
  - End-to-end encryption
  - Fraud detection
  - Anti-money laundering (AML)
```

#### **Payment Service Implementation:**
```java
// PaymentService.java - Financial transactions
@Service
@Transactional
public class PaymentService {
    
    private final CreditWalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final StripeService stripeService;
    private final FraudDetectionService fraudDetectionService;
    
    public CreditPurchaseResult purchaseCredits(UUID userId, CreditPurchaseRequest request) {
        // Fraud detection
        FraudAssessment assessment = fraudDetectionService.assessPurchase(userId, request);
        if (assessment.getRiskLevel() == RiskLevel.HIGH) {
            throw new TransactionBlockedException("Transaction blocked due to fraud risk");
        }
        
        // Process payment via Stripe
        StripePaymentResult stripeResult = stripeService.processPayment(
            request.getPaymentMethodId(),
            request.getAmount(),
            request.getCurrency()
        );
        
        if (!stripeResult.isSuccessful()) {
            throw new PaymentProcessingException("Payment failed: " + stripeResult.getErrorMessage());
        }
        
        // Calculate credits (with bonuses for bulk purchases)
        int creditsToAdd = calculateCredits(request.getAmount(), request.getCurrency());
        
        // Update wallet
        CreditWallet wallet = walletRepository.findByUserId(userId)
            .orElseGet(() -> createWallet(userId));
        
        wallet.addCredits(creditsToAdd);
        walletRepository.save(wallet);
        
        // Record transaction
        Transaction transaction = Transaction.builder()
            .userId(userId)
            .type(TransactionType.CREDIT_PURCHASE)
            .amount(request.getAmount())
            .currency(request.getCurrency())
            .credits(creditsToAdd)
            .stripePaymentIntentId(stripeResult.getPaymentIntentId())
            .status(TransactionStatus.COMPLETED)
            .timestamp(Instant.now())
            .build();
        
        transactionRepository.save(transaction);
        
        // Publish event
        eventPublisher.publish(new CreditsAddedEvent(userId, creditsToAdd, transaction.getId()));
        
        return CreditPurchaseResult.builder()
            .transactionId(transaction.getId())
            .creditsAdded(creditsToAdd)
            .newBalance(wallet.getBalance())
            .build();
    }
    
    public void holdCredits(UUID userId, int amount) {
        CreditWallet wallet = getWallet(userId);
        
        if (wallet.getAvailableBalance() < amount) {
            throw new InsufficientCreditsException("User has insufficient credits");
        }
        
        wallet.holdCredits(amount);
        walletRepository.save(wallet);
        
        // Create hold transaction
        Transaction holdTransaction = Transaction.builder()
            .userId(userId)
            .type(TransactionType.CREDIT_HOLD)
            .credits(-amount)
            .status(TransactionStatus.PENDING)
            .timestamp(Instant.now())
            .build();
        
        transactionRepository.save(holdTransaction);
    }
    
    public void transferCredits(UUID fromUserId, UUID toUserId, int amount) {
        // Release hold and transfer
        CreditWallet fromWallet = getWallet(fromUserId);
        CreditWallet toWallet = getWallet(toUserId);
        
        fromWallet.releaseAndDeduct(amount);
        
        // Platform fee (5% like Airbnb)
        int platformFee = (int) (amount * 0.05);
        int providerAmount = amount - platformFee;
        
        toWallet.addCredits(providerAmount);
        
        walletRepository.saveAll(List.of(fromWallet, toWallet));
        
        // Record transactions
        List<Transaction> transactions = List.of(
            createDebitTransaction(fromUserId, amount),
            createCreditTransaction(toUserId, providerAmount),
            createPlatformFeeTransaction(fromUserId, platformFee)
        );
        
        transactionRepository.saveAll(transactions);
        
        // Publish events
        eventPublisher.publish(new CreditsTransferredEvent(fromUserId, toUserId, amount, platformFee));
    }
}
```

---

## 🔄 **EVENT-DRIVEN COMMUNICATION**

### **Event Sourcing Architecture:**

#### **Domain Events:**
```java
// Domain events for microservices communication
public abstract class DomainEvent {
    private final UUID eventId;
    private final Instant timestamp;
    private final String eventType;
    
    // Base implementation
}

// User domain events
public class UserRegisteredEvent extends DomainEvent {
    private final UUID userId;
    private final String email;
    private final String fullName;
    private final Instant registrationTime;
    private final String registrationSource;
}

public class UserProfileUpdatedEvent extends DomainEvent {
    private final UUID userId;
    private final Map<String, Object> changedFields;
    private final UUID updatedBy;
}

// Session domain events
public class SessionRequestedEvent extends DomainEvent {
    private final UUID sessionId;
    private final UUID seekerId;
    private final UUID providerId;
    private final UUID skillId;
    private final Instant scheduledTime;
    private final BigDecimal hourlyRate;
}

public class SessionCompletedEvent extends DomainEvent {
    private final UUID sessionId;
    private final SessionFeedback feedback;
    private final Duration actualDuration;
    private final BigDecimal finalAmount;
}
```

#### **Event Bus Implementation:**
```java
// EventBus using AWS EventBridge + SQS
@Service
public class EventBridgeEventBus implements EventBus {
    
    private final EventBridgeClient eventBridgeClient;
    private final ObjectMapper objectMapper;
    
    @Override
    @Async
    public void publish(DomainEvent event) {
        try {
            PutEventsRequest request = PutEventsRequest.builder()
                .entries(PutEventsRequestEntry.builder()
                    .source("skillswap.microservices")
                    .detailType(event.getEventType())
                    .detail(objectMapper.writeValueAsString(event))
                    .resources(event.getRelatedResourceArns())
                    .build())
                .build();
            
            eventBridgeClient.putEvents(request);
            
        } catch (Exception e) {
            log.error("Failed to publish event: {}", event, e);
            // Dead letter queue for failed events
            deadLetterService.send(event, e.getMessage());
        }
    }
}

// Event handlers in each microservice
@Component
public class UserEventHandlers {
    
    @SqsListener("user-events-queue")
    public void handleUserRegistered(UserRegisteredEvent event) {
        // Create analytics profile
        analyticsService.createUserProfile(event.getUserId());
        
        // Initialize empty skill profile
        skillService.initializeUserSkillProfile(event.getUserId());
        
        // Create credit wallet
        paymentService.createWallet(event.getUserId());
    }
    
    @SqsListener("user-events-queue")
    @RetryableTopic(attempts = "3", delay = "5s")
    public void handleUserProfileUpdated(UserProfileUpdatedEvent event) {
        // Update search index
        searchService.updateUserIndex(event.getUserId(), event.getChangedFields());
        
        // Refresh recommendations if skills changed
        if (event.getChangedFields().containsKey("skills")) {
            recommendationService.refreshUserRecommendations(event.getUserId());
        }
    }
}
```

---

## 🚦 **API GATEWAY & SERVICE MESH**

### **Kong API Gateway Configuration:**
```yaml
# kong-gateway.yaml - Centralized API management
apiVersion: configuration.konghq.com/v1
kind: KongIngress
metadata:
  name: skillswap-api-gateway
proxy:
  protocol: http
  path: /api/v1
  connect_timeout: 10000
  write_timeout: 10000
  read_timeout: 10000
upstream:
  algorithm: round-robin
  health_checks:
    active:
      healthy:
        interval: 10
        successes: 3
      unhealthy:
        interval: 10
        tcp_failures: 3
        http_failures: 3

---
# Service routing rules
apiVersion: configuration.konghq.com/v1
kind: KongPlugin
metadata:
  name: rate-limiting
config:
  minute: 1000
  hour: 10000
  policy: local
plugin: rate-limiting

---
# Authentication plugin
apiVersion: configuration.konghq.com/v1
kind: KongPlugin
metadata:
  name: jwt-auth
config:
  secret_is_base64: false
  run_on_preflight: true
plugin: jwt

---
# Service definitions
services:
  user-service:
    url: http://user-service:8080
    routes:
      - name: user-auth
        paths: ["/auth", "/users"]
        plugins: [jwt-auth, rate-limiting]
        
  skill-service:
    url: http://skill-service:8080
    routes:
      - name: skills
        paths: ["/skills", "/matches"]
        plugins: [jwt-auth, rate-limiting]
        
  session-service:
    url: http://session-service:8080
    routes:
      - name: sessions
        paths: ["/sessions", "/bookings"]
        plugins: [jwt-auth, rate-limiting]
        
  payment-service:
    url: http://payment-service:8080
    routes:
      - name: payments
        paths: ["/payments", "/credits", "/transactions"]
        plugins: [jwt-auth, rate-limiting, request-validator]
```

### **Istio Service Mesh (Advanced):**
```yaml
# istio-config.yaml - Traffic management & security
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: skillswap-routing
spec:
  hosts:
  - api.skillswap.com
  gateways:
  - skillswap-gateway
  http:
  # Canary deployments (Netflix-style)
  - match:
    - headers:
        canary:
          exact: "true"
    route:
    - destination:
        host: user-service
        subset: canary
      weight: 100
  
  # Production traffic
  - route:
    - destination:
        host: user-service
        subset: stable
      weight: 90
    - destination:
        host: user-service
        subset: canary
      weight: 10
    
  # Circuit breaker configuration
  - fault:
      delay:
        percentage:
          value: 0.1
        fixedDelay: 5s
    route:
    - destination:
        host: skill-service

---
# Security policies
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: skillswap-authz
spec:
  rules:
  - from:
    - source:
        principals: ["cluster.local/ns/skillswap/sa/user-service"]
    to:
    - operation:
        methods: ["GET", "POST"]
        paths: ["/api/v1/skills/*"]
```

---

## 📊 **MONITORING & OBSERVABILITY (Microservices)**

### **Distributed Tracing with Jaeger:**
```java
// Distributed tracing implementation
@RestController
public class SessionController {
    
    @GetMapping("/sessions/{id}")
    @Traced(value = "get-session-details", kind = SpanKind.SERVER)
    public ResponseEntity<SessionResponse> getSession(@PathVariable UUID id) {
        
        // Create child span for database call
        try (Scope scope = tracer.nextSpan()
                .name("database-session-lookup")
                .tag("session.id", id.toString())
                .start()
                .makeCurrent()) {
            
            Session session = sessionService.findById(id);
            
            // Another service call with distributed tracing
            UserProfile user = userServiceClient.getUser(session.getSeekerId());
            SkillDetails skill = skillServiceClient.getSkill(session.getSkillId());
            
            return ResponseEntity.ok(SessionResponse.from(session, user, skill));
        }
    }
}

// Custom metrics for business KPIs
@Component
public class BusinessMetrics {
    
    private final MeterRegistry meterRegistry;
    
    @EventListener
    public void trackSessionCompletion(SessionCompletedEvent event) {
        // Track session success rate by skill category
        meterRegistry.counter("sessions.completed",
            "skill_category", event.getSkillCategory(),
            "provider_rating", String.valueOf(event.getProviderRating())).increment();
        
        // Track revenue metrics
        meterRegistry.gauge("revenue.session.amount", event.getSessionAmount().doubleValue());
        
        // Track user engagement
        meterRegistry.timer("sessions.duration").record(event.getDuration());
    }
}
```

### **Health Checks & Circuit Breakers:**
```java
// Resilient service communication
@Service
public class SkillServiceClient {
    
    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    
    @CircuitBreaker(name = "skill-service", fallbackMethod = "getSkillFallback")
    @Retry(name = "skill-service")
    @TimeLimiter(name = "skill-service")
    public CompletableFuture<SkillDetails> getSkill(UUID skillId) {
        return webClient
            .get()
            .uri("/skills/{id}", skillId)
            .retrieve()
            .bodyToMono(SkillDetails.class)
            .timeout(Duration.ofMillis(500))
            .toFuture();
    }
    
    public CompletableFuture<SkillDetails> getSkillFallback(UUID skillId, Exception ex) {
        log.warn("Skill service unavailable for skillId: {}, using fallback", skillId);
        
        // Return cached data or basic info
        return CompletableFuture.completedFuture(
            SkillDetails.builder()
                .id(skillId)
                .name("Skill temporarily unavailable")
                .description("Please try again later")
                .category("UNKNOWN")
                .build()
        );
    }
}
```

---

## 🔧 **MIGRATION STRATEGY (Monolith → Microservices)**

### **Strangler Fig Pattern Implementation:**

#### **Phase 1: Extract User Service (Month 1-3)**
```java
// Legacy adapter pattern
@Component
public class UserServiceAdapter {
    
    private final LegacyUserRepository legacyUserRepo;
    private final NewUserServiceClient newUserService;
    private final FeatureToggleService featureToggle;
    
    public User getUser(UUID userId) {
        // Feature toggle to gradually migrate traffic
        if (featureToggle.isEnabled("new-user-service", userId)) {
            try {
                return newUserService.getUser(userId);
            } catch (Exception e) {
                log.warn("New user service failed, falling back to legacy", e);
                return legacyUserRepo.findById(userId);
            }
        } else {
            return legacyUserRepo.findById(userId);
        }
    }
    
    @Transactional
    public User updateUser(UUID userId, UserUpdateRequest request) {
        User updatedUser;
        
        if (featureToggle.isEnabled("new-user-service", userId)) {
            // Update in new service
            updatedUser = newUserService.updateUser(userId, request);
            
            // Sync back to legacy for gradual migration
            syncToLegacy(updatedUser);
        } else {
            // Update in legacy system
            updatedUser = legacyUserRepo.update(userId, request);
            
            // Async sync to new service for preparation
            asyncSyncToNewService(updatedUser);
        }
        
        return updatedUser;
    }
}
```

#### **Database Migration Strategy:**
```sql
-- Dual-write pattern for gradual data migration
-- Phase 1: Legacy system writes to both old and new schemas

-- Legacy user table (existing)
CREATE TABLE users_legacy (
    id UUID PRIMARY KEY,
    email VARCHAR(255),
    full_name VARCHAR(255),
    created_at TIMESTAMP,
    -- other legacy fields
);

-- New microservice user table
CREATE TABLE user_service.users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    profile_picture_url TEXT,
    privacy_settings JSONB,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    version INTEGER DEFAULT 1  -- Optimistic locking
);

-- Data consistency checks
CREATE OR REPLACE FUNCTION check_user_data_consistency()
RETURNS TABLE(user_id UUID, legacy_email VARCHAR, new_email VARCHAR, status VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        l.id,
        l.email,
        n.email,
        CASE 
            WHEN l.email = n.email THEN 'CONSISTENT'
            WHEN n.email IS NULL THEN 'MISSING_IN_NEW'
            WHEN l.email IS NULL THEN 'MISSING_IN_LEGACY'
            ELSE 'INCONSISTENT'
        END as status
    FROM users_legacy l
    FULL OUTER JOIN user_service.users n ON l.id = n.id
    WHERE l.email != n.email OR l.email IS NULL OR n.email IS NULL;
END;
$$ LANGUAGE plpgsql;
```

---

## 🚀 **DEPLOYMENT & ROLLOUT STRATEGY**

### **Blue-Green Microservices Deployment:**
```yaml
# blue-green-deployment.yaml
apiVersion: argoproj.io/v1alpha1
kind: Rollout
metadata:
  name: user-service-rollout
spec:
  replicas: 5
  strategy:
    blueGreen:
      # Traffic routing
      activeService: user-service-active
      previewService: user-service-preview
      
      # Automated testing before promotion
      prePromotionAnalysis:
        templates:
        - templateName: success-rate-check
        args:
        - name: service-name
          value: user-service-preview
          
      # Promotion strategy
      autoPromotionEnabled: false  # Manual approval required
      scaleDownDelaySeconds: 300   # Keep blue for 5 minutes
      
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
    spec:
      containers:
      - name: user-service
        image: skillswap/user-service:v1.2.0
        
        # Health checks for zero-downtime deployment
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          
        # Resource optimization
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"

---
# Analysis template for automated testing
apiVersion: argoproj.io/v1alpha1
kind: AnalysisTemplate
metadata:
  name: success-rate-check
spec:
  args:
  - name: service-name
  metrics:
  - name: success-rate
    successCondition: result >= 0.99
    interval: 30s
    count: 5
    provider:
      prometheus:
        address: http://prometheus.monitoring.svc.cluster.local:9090
        query: |
          sum(rate(http_requests_total{service="{{args.service-name}}",status!~"5.."}[5m])) /
          sum(rate(http_requests_total{service="{{args.service-name}}"}[5m]))
```

**🎯 Result: Production-ready microservices architecture supporting millions of users with enterprise-grade reliability and scalability!**
