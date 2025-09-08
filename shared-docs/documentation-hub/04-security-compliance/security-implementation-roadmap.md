# 🎯 Security Implementation Roadmap - SkillSwap

## Executive Summary

Este plan de implementación aborda las **8 vulnerabilidades críticas** identificadas en el análisis de seguridad de SkillSwap, priorizando las correcciones por impacto en la seguridad y facilidad de implementación.

**Estado Actual:** 🔴 ALTO RIESGO  
**Objetivo:** 🟢 PRODUCCIÓN SEGURA  
**Tiempo Total Estimado:** 4-6 semanas  
**Recursos Requeridos:** 2 desarrolladores backend + 1 DevOps + 1 seguridad

---

## 🚨 FASE 1: CRÍTICA - Implementación Inmediata (24-48 horas)

### 🔥 Vulnerabilidades que requieren acción INMEDIATA

#### 1. JWT Secret Hardcoded (CRITICAL)
**Riesgo:** Forja de tokens, escalación de privilegios  
**Tiempo:** 2 horas  
**Desarrollador:** Backend Lead

```bash
# ACCIÓN INMEDIATA:
# 1. Generar secret seguro
openssl rand -base64 32 > jwt-secret.txt

# 2. Configurar variable de entorno
export JWT_SECRET=$(cat jwt-secret.txt)

# 3. Remover default del código
@Value("${JWT_SECRET}") // Sin valor por defecto
private String jwtSecret;

# 4. Validación en startup
@PostConstruct
public void validateJwtSecret() {
    if (jwtSecret == null || jwtSecret.length() < 32) {
        throw new IllegalStateException("JWT_SECRET must be at least 256 bits");
    }
}
```

**Verificación:**
- [ ] Secret generado aleatoriamente (256 bits)
- [ ] Variable de entorno configurada
- [ ] Código default removido
- [ ] Aplicación inicia sin errores
- [ ] Tokens anteriores invalidados

#### 2. CORS Wildcard Vulnerability (CRITICAL)
**Riesgo:** CSRF, acceso no autorizado desde cualquier origen  
**Tiempo:** 1 hora  
**Desarrollador:** Backend Lead

```java
// IMPLEMENTACIÓN INMEDIATA:
@Configuration
public class CorsSecurityConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // REEMPLAZAR: origins = "*" 
        configuration.setAllowedOrigins(Arrays.asList(
            "https://skillswap.app",          // Producción
            "https://staging.skillswap.app",  // Staging
            "http://localhost:3000"           // Desarrollo local
        ));
        
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

// REMOVER de todos los controladores:
// @CrossOrigin(origins = "*", maxAge = 3600) // ELIMINAR ESTA LÍNEA
```

**Verificación:**
- [ ] Anotaciones @CrossOrigin removidas
- [ ] CORS configurado en SecurityConfig
- [ ] Solo orígenes específicos permitidos
- [ ] Requests desde dominios no autorizados bloqueados

#### 3. JWT Token Expiration (CRITICAL)
**Riesgo:** Ventana de ataque extendida (24h actual)  
**Tiempo:** 30 minutos  
**Desarrollador:** Backend Lead

```yaml
# application.yml - CAMBIO INMEDIATO:
app:
  jwt:
    # ANTES: expiration: 86400000  # 24 horas - PELIGROSO
    expiration: 900000           # 15 minutos - SEGURO
    refresh-expiration: 604800000 # 7 días máximo
```

**Verificación:**
- [ ] Access tokens expiran en 15 minutos
- [ ] Refresh tokens expiran en 7 días
- [ ] Frontend maneja refresh automático
- [ ] Usuarios no deslogueados abruptamente

#### 4. Rate Limiting Missing (HIGH)
**Riesgo:** Ataques de fuerza bruta sin restricciones  
**Tiempo:** 4 horas  
**Desarrollador:** Backend + DevOps

```xml
<!-- pom.xml - Añadir dependencia -->
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>7.6.0</version>
</dependency>
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-redis</artifactId>
    <version>7.6.0</version>
</dependency>
```

```java
// RateLimitingFilter.java - IMPLEMENTACIÓN INMEDIATA
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitingFilter implements Filter {
    
    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String clientIp = getClientIp(httpRequest);
        String uri = httpRequest.getRequestURI();
        
        // Rate limit para endpoints de autenticación
        if (isAuthEndpoint(uri)) {
            String key = clientIp + ":auth";
            Bucket bucket = authBuckets.computeIfAbsent(key, this::createAuthBucket);
            
            if (!bucket.tryConsume(1)) {
                httpResponse.setStatus(429); // Too Many Requests
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(
                    "{\"error\":\"Too many authentication attempts\",\"retryAfter\":60}"
                );
                return;
            }
        }
        
        chain.doFilter(request, response);
    }
    
    private Bucket createAuthBucket(String key) {
        // 5 intentos por minuto para auth
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
    
    private boolean isAuthEndpoint(String uri) {
        return uri.contains("/auth/login") || 
               uri.contains("/auth/register") || 
               uri.contains("/auth/forgot-password");
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        return xForwardedFor != null ? xForwardedFor.split(",")[0].trim() : request.getRemoteAddr();
    }
}
```

**Verificación:**
- [ ] Rate limiting activo en endpoints auth
- [ ] 5 intentos por minuto por IP
- [ ] Error 429 retornado al exceder límite
- [ ] Buckets se resetean después de 1 minuto

---

## ⚡ FASE 2: ALTA PRIORIDAD - Semana 1

### 🔧 Mejoras Fundamentales de Seguridad

#### 5. HTTPS/TLS Implementation (HIGH)
**Tiempo:** 1 día  
**Desarrollador:** DevOps + Backend

```bash
# Generar certificado SSL para desarrollo
keytool -genkeypair -alias skillswap -keyalg RSA -keysize 2048 \
        -storetype PKCS12 -keystore skillswap-keystore.p12 \
        -validity 3650 -ext SAN=dns:localhost,dns:skillswap.local
```

```yaml
# application-production.yml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:skillswap-keystore.p12
    key-store-type: PKCS12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    protocol: TLS
    enabled-protocols: TLSv1.2,TLSv1.3
```

```nginx
# nginx.conf - HTTPS redirect y headers de seguridad
server {
    listen 80;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    
    ssl_certificate /etc/ssl/certs/skillswap.crt;
    ssl_certificate_key /etc/ssl/private/skillswap.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    
    # Security headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Frame-Options "DENY" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    
    location / {
        proxy_pass https://localhost:8443;
    }
}
```

#### 6. Enhanced Input Validation (HIGH)
**Tiempo:** 2 días  
**Desarrollador:** Backend Lead + Junior

```xml
<!-- Añadir dependencias de validación -->
<dependency>
    <groupId>org.owasp.antisamy</groupId>
    <artifactId>antisamy</artifactId>
    <version>1.7.4</version>
</dependency>
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.17.2</version>
</dependency>
```

```java
// InputSanitizer.java
@Component
public class InputSanitizer {
    
    private final Policy antiSamyPolicy;
    
    public InputSanitizer() throws PolicyException {
        this.antiSamyPolicy = Policy.getInstance(
            getClass().getResourceAsStream("/antisamy-ebay.xml")
        );
    }
    
    public String sanitizeHtml(String input) {
        if (input == null) return null;
        
        try {
            AntiSamy antiSamy = new AntiSamy();
            CleanResults results = antiSamy.scan(input, antiSamyPolicy);
            return results.getCleanHTML();
        } catch (Exception e) {
            logger.warn("HTML sanitization failed", e);
            return Jsoup.clean(input, Safelist.none());
        }
    }
    
    public String sanitizeString(String input) {
        if (input == null) return null;
        
        return input
            .replaceAll("[<>\"'&;]", "") // Remove dangerous characters
            .replaceAll("javascript:", "")
            .replaceAll("data:", "")
            .trim();
    }
}

// Enhanced DTOs con validación robusta
public class UserRegistrationRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email too long")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must contain uppercase, lowercase, digit and special character"
    )
    private String password;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]{2,50}$", message = "Invalid characters in name")
    private String firstName;
    
    // Setters con sanitización automática
    public void setFirstName(String firstName) {
        this.firstName = inputSanitizer.sanitizeString(firstName);
    }
}
```

#### 7. Security Audit Logging (HIGH)
**Tiempo:** 1 día  
**Desarrollador:** Backend Lead

```java
// SecurityAuditService.java
@Service
public class SecurityAuditService {
    
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        UserPrincipal user = (UserPrincipal) event.getAuthentication().getPrincipal();
        
        SecurityAuditEvent auditEvent = SecurityAuditEvent.builder()
            .eventType("AUTH_SUCCESS")
            .userId(user.getId())
            .username(user.getEmail())
            .clientIp(getCurrentClientIp())
            .userAgent(getCurrentUserAgent())
            .timestamp(Instant.now())
            .build();
            
        logSecurityEvent(auditEvent);
    }
    
    @EventListener
    public void handleAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        
        SecurityAuditEvent auditEvent = SecurityAuditEvent.builder()
            .eventType("AUTH_FAILURE")
            .username(username)
            .clientIp(getCurrentClientIp())
            .failureReason(event.getException().getMessage())
            .timestamp(Instant.now())
            .build();
            
        logSecurityEvent(auditEvent);
        checkBruteForceAttempts(getCurrentClientIp(), username);
    }
    
    private void logSecurityEvent(SecurityAuditEvent event) {
        securityLogger.info("SECURITY_EVENT: {}", event.toJson());
        
        // Persist to database for analysis
        auditEventRepository.save(event);
        
        // Send to security monitoring system
        if (event.isCritical()) {
            alertingService.sendSecurityAlert(event);
        }
    }
}

// logback-spring.xml - Configuración de logs de seguridad
<configuration>
    <appender name="SECURITY" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/security.log</file>
        <encoder>
            <pattern>%d{ISO8601} [%thread] %-5level [%X{traceId}] %logger{36} - %msg%n</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/security.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>90</maxHistory>
        </rollingPolicy>
    </appender>
    
    <logger name="SECURITY" level="INFO" additivity="false">
        <appender-ref ref="SECURITY"/>
    </logger>
</configuration>
```

#### 8. Token Blacklisting (HIGH)
**Tiempo:** 1 día  
**Desarrollador:** Backend Lead

```java
// TokenBlacklistService.java
@Service
public class TokenBlacklistService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void blacklistToken(String token) {
        try {
            Claims claims = jwtUtils.getClaimsFromToken(token);
            String jti = claims.getId(); // JWT ID
            Date expiration = claims.getExpiration();
            
            if (jti != null && expiration.after(new Date())) {
                long ttl = expiration.getTime() - System.currentTimeMillis();
                
                redisTemplate.opsForValue().set(
                    "blacklist:" + jti,
                    true,
                    Duration.ofMillis(ttl)
                );
                
                logger.info("Token blacklisted: jti={}, ttl={}ms", jti, ttl);
            }
        } catch (Exception e) {
            logger.error("Failed to blacklist token", e);
        }
    }
    
    public boolean isTokenBlacklisted(String token) {
        try {
            Claims claims = jwtUtils.getClaimsFromToken(token);
            String jti = claims.getId();
            
            return jti != null && Boolean.TRUE.equals(
                redisTemplate.opsForValue().get("blacklist:" + jti)
            );
        } catch (Exception e) {
            logger.error("Failed to check token blacklist", e);
            return false; // Fail open for availability
        }
    }
    
    public void blacklistAllUserTokens(Long userId) {
        // Blacklist all active tokens for user (in case of account compromise)
        Set<String> activeTokens = getActiveTokensForUser(userId);
        
        for (String token : activeTokens) {
            blacklistToken(token);
        }
        
        logger.warn("All tokens blacklisted for user: {}", userId);
    }
}

// Logout endpoint con blacklisting
@PostMapping("/logout")
public ResponseEntity<?> logoutUser(HttpServletRequest request) {
    String token = jwtUtils.getTokenFromRequest(request);
    
    if (token != null) {
        // Blacklist the current token
        tokenBlacklistService.blacklistToken(token);
        
        UserPrincipal userPrincipal = jwtUtils.getUserPrincipalFromToken(token);
        auditService.logLogout(userPrincipal.getId());
    }
    
    return ResponseEntity.ok(new ApiResponse(true, "Logged out successfully"));
}

// Enhanced JWT Filter con blacklist check
@Override
protected void doFilterInternal(HttpServletRequest request, 
                               HttpServletResponse response, 
                               FilterChain filterChain) throws ServletException, IOException {
    
    String token = getTokenFromRequest(request);
    
    if (token != null && jwtUtils.validateToken(token)) {
        // Check if token is blacklisted
        if (tokenBlacklistService.isTokenBlacklisted(token)) {
            logger.warn("Blacklisted token attempted: {}", token.substring(0, 20) + "...");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        
        UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    
    filterChain.doFilter(request, response);
}
```

---

## 🔧 FASE 3: MEDIA PRIORIDAD - Semana 2-3

### 🛡️ Características Avanzadas de Seguridad

#### 9. Multi-Factor Authentication (MFA)
**Tiempo:** 3 días  
**Desarrollador:** Backend Lead + Frontend

```xml
<!-- Dependencias MFA -->
<dependency>
    <groupId>de.taimos</groupId>
    <artifactId>totp</artifactId>
    <version>1.0</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.2</version>
</dependency>
```

```java
// MfaService.java
@Service
public class MfaService {
    
    @Autowired
    private TotpGenerator totpGenerator;
    
    public MfaSetupResponse initiateMfaSetup(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        
        // Generate TOTP secret
        String secret = totpGenerator.generateSecret();
        
        // Store encrypted secret
        user.setMfaSecret(encryptionService.encrypt(secret));
        user.setMfaEnabled(false); // Will be enabled after verification
        userRepository.save(user);
        
        // Generate QR code URL for authenticator apps
        String qrCodeUrl = totpGenerator.getQrCodeUrl(
            user.getEmail(),
            "SkillSwap",
            secret
        );
        
        return MfaSetupResponse.builder()
            .secret(secret)
            .qrCodeUrl(qrCodeUrl)
            .backupCodes(generateBackupCodes(userId))
            .build();
    }
    
    public boolean verifyMfaCode(Long userId, String code) {
        User user = userRepository.findById(userId).orElseThrow();
        
        if (!user.isMfaEnabled()) {
            return false;
        }
        
        String secret = encryptionService.decrypt(user.getMfaSecret());
        return totpGenerator.verifyCode(secret, code);
    }
    
    public void enableMfa(Long userId, String verificationCode) {
        if (verifyMfaCode(userId, verificationCode)) {
            User user = userRepository.findById(userId).orElseThrow();
            user.setMfaEnabled(true);
            userRepository.save(user);
            
            auditService.logMfaEnabled(userId);
        } else {
            throw new InvalidMfaCodeException("Invalid verification code");
        }
    }
}

// Enhanced Login con MFA
@PostMapping("/login")
public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    
    // Primary authentication
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(),
            loginRequest.getPassword()
        )
    );
    
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    User user = userRepository.findById(userPrincipal.getId()).orElseThrow();
    
    // Check if MFA is enabled
    if (user.isMfaEnabled()) {
        // Generate temporary MFA token (valid for 5 minutes)
        String mfaToken = jwtUtils.generateMfaToken(user.getId());
        
        return ResponseEntity.ok(MfaRequiredResponse.builder()
            .mfaRequired(true)
            .mfaToken(mfaToken)
            .availableMethods(Arrays.asList("TOTP", "BACKUP_CODE"))
            .build());
    }
    
    // Generate access tokens (no MFA required)
    TokenResponse tokenResponse = jwtUtils.generateTokenResponse(authentication);
    return ResponseEntity.ok(tokenResponse);
}

@PostMapping("/verify-mfa")
public ResponseEntity<?> verifyMfa(@Valid @RequestBody MfaVerificationRequest request) {
    
    if (!jwtUtils.validateMfaToken(request.getMfaToken())) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Invalid or expired MFA token"));
    }
    
    Long userId = jwtUtils.getUserIdFromMfaToken(request.getMfaToken());
    
    boolean mfaValid = false;
    if ("TOTP".equals(request.getMethod())) {
        mfaValid = mfaService.verifyMfaCode(userId, request.getCode());
    } else if ("BACKUP_CODE".equals(request.getMethod())) {
        mfaValid = mfaService.verifyBackupCode(userId, request.getCode());
    }
    
    if (!mfaValid) {
        auditService.logMfaFailure(userId, request.getMethod());
        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Invalid MFA code"));
    }
    
    // Generate full access tokens
    User user = userRepository.findById(userId).orElseThrow();
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        user.getEmail(), null, user.getAuthorities()
    );
    
    TokenResponse tokenResponse = jwtUtils.generateTokenResponse(authentication);
    auditService.logMfaSuccess(userId, request.getMethod());
    
    return ResponseEntity.ok(tokenResponse);
}
```

#### 10. Anomaly Detection System
**Tiempo:** 2 días  
**Desarrollador:** Backend + Data Science

```java
// AnomalyDetectionService.java
@Service
public class AnomalyDetectionService {
    
    @Autowired
    private UserBehaviorRepository behaviorRepository;
    
    public void analyzeUserActivity(Long userId, String activity, HttpServletRequest request) {
        UserActivityEvent event = UserActivityEvent.builder()
            .userId(userId)
            .activity(activity)
            .clientIp(getClientIp(request))
            .userAgent(request.getHeader("User-Agent"))
            .timestamp(Instant.now())
            .location(geoLocationService.getLocation(getClientIp(request)))
            .build();
            
        // Store activity
        behaviorRepository.save(event);
        
        // Analyze for anomalies
        AnomalyScore score = calculateAnomalyScore(userId, event);
        
        if (score.isAnomalous()) {
            handleAnomalousActivity(userId, event, score);
        }
    }
    
    private AnomalyScore calculateAnomalyScore(Long userId, UserActivityEvent currentEvent) {
        // Get user's behavior baseline (last 30 days)
        List<UserActivityEvent> historicalEvents = 
            behaviorRepository.findByUserIdAndTimestampAfter(
                userId, 
                Instant.now().minus(30, ChronoUnit.DAYS)
            );
            
        if (historicalEvents.size() < 10) {
            return AnomalyScore.normal(); // Insufficient data
        }
        
        double timeAnomalyScore = analyzeTimePattern(currentEvent, historicalEvents);
        double locationAnomalyScore = analyzeLocationPattern(currentEvent, historicalEvents);
        double deviceAnomalyScore = analyzeDevicePattern(currentEvent, historicalEvents);
        
        double overallScore = (timeAnomalyScore * 0.3) + 
                             (locationAnomalyScore * 0.4) + 
                             (deviceAnomalyScore * 0.3);
                             
        return AnomalyScore.builder()
            .score(overallScore)
            .isAnomalous(overallScore > 0.7)
            .timeScore(timeAnomalyScore)
            .locationScore(locationAnomalyScore)
            .deviceScore(deviceAnomalyScore)
            .build();
    }
    
    private void handleAnomalousActivity(Long userId, UserActivityEvent event, AnomalyScore score) {
        SecurityIncident incident = SecurityIncident.builder()
            .type("BEHAVIORAL_ANOMALY")
            .severity(score.getSeverity())
            .userId(userId)
            .description(score.getDescription())
            .clientIp(event.getClientIp())
            .timestamp(Instant.now())
            .build();
            
        // Log incident
        securityIncidentRepository.save(incident);
        
        // Take action based on severity
        if (score.isCritical()) {
            // Force re-authentication
            tokenBlacklistService.blacklistAllUserTokens(userId);
            notificationService.sendSecurityAlert(userId, 
                "Unusual activity detected on your account. Please log in again.");
        } else if (score.isHigh()) {
            // Send notification but don't force logout
            notificationService.sendSecurityNotification(userId,
                "We noticed a login from a new location or device.");
        }
        
        // Alert security team for manual review
        alertingService.sendAnomalyAlert(incident);
    }
}

// Scheduled cleanup y analysis
@Component
public class SecurityMaintenanceService {
    
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void performSecurityMaintenance() {
        // Clean up old audit logs (keep 90 days)
        auditService.cleanupOldLogs(Duration.ofDays(90));
        
        // Analyze security trends
        securityAnalyticsService.generateDailySecurityReport();
        
        // Update threat intelligence
        threatIntelligenceService.updateThreatDatabase();
        
        // Check for compromised credentials
        compromisedCredentialService.checkRecentPasswords();
    }
}
```

---

## 📊 FASE 4: MONITOREO Y COMPLIANCE - Semana 4

### 📈 Monitoring, Alerting y Compliance

#### 11. Security Monitoring Dashboard
**Tiempo:** 2 días  
**Desarrollador:** Full Stack + DevOps

```yaml
# prometheus.yml - Métricas de seguridad
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'skillswap-security'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 30s
```

```java
// SecurityMetricsService.java
@Component
public class SecurityMetricsService {
    
    private final Counter authenticationFailures = Counter.build()
        .name("authentication_failures_total")
        .help("Total number of authentication failures")
        .labelNames("reason", "client_ip")
        .register();
        
    private final Counter tokenValidationFailures = Counter.build()
        .name("token_validation_failures_total")
        .help("Total number of token validation failures")
        .labelNames("reason")
        .register();
        
    private final Histogram responseTime = Histogram.build()
        .name("security_operation_duration_seconds")
        .help("Time spent on security operations")
        .labelNames("operation")
        .register();
        
    private final Gauge blockedIPs = Gauge.build()
        .name("blocked_ips_current")
        .help("Current number of blocked IP addresses")
        .register();
    
    @EventListener
    public void recordAuthFailure(AuthenticationFailureEvent event) {
        authenticationFailures.labels(
            event.getReason(),
            event.getClientIp()
        ).inc();
    }
    
    @EventListener
    public void recordTokenFailure(TokenValidationFailureEvent event) {
        tokenValidationFailures.labels(event.getReason()).inc();
    }
    
    public void recordSecurityOperation(String operation, long durationMs) {
        responseTime.labels(operation).observe(durationMs / 1000.0);
    }
}

// Grafana Dashboard config (JSON)
{
  "dashboard": {
    "title": "SkillSwap Security Dashboard",
    "panels": [
      {
        "title": "Authentication Failures (Last 24h)",
        "type": "stat",
        "targets": [
          {
            "expr": "increase(authentication_failures_total[24h])"
          }
        ],
        "thresholds": [
          {"color": "green", "value": 0},
          {"color": "yellow", "value": 10},
          {"color": "red", "value": 50}
        ]
      },
      {
        "title": "Failed Login Attempts by IP",
        "type": "table",
        "targets": [
          {
            "expr": "topk(10, sum by (client_ip) (rate(authentication_failures_total[1h])))"
          }
        ]
      },
      {
        "title": "Token Validation Errors",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(token_validation_failures_total[5m])"
          }
        ]
      }
    ]
  }
}
```

#### 12. GDPR Compliance Implementation
**Tiempo:** 2 días  
**Desarrollador:** Backend + Legal Review

```java
// GdprComplianceService.java
@Service
public class GdprComplianceService {
    
    public UserDataExport exportUserData(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        
        return UserDataExport.builder()
            .personalData(mapPersonalData(user))
            .profileData(profileService.getUserProfile(userId))
            .skillsData(skillService.getUserSkills(userId))
            .sessionsData(sessionService.getUserSessions(userId))
            .activityData(activityService.getUserActivity(userId))
            .paymentsData(paymentService.getUserPayments(userId))
            .exportTimestamp(Instant.now())
            .exportFormat("JSON")
            .build();
    }
    
    @Transactional
    public void deleteUserData(Long userId, DeletionReason reason) {
        User user = userRepository.findById(userId).orElseThrow();
        
        // 1. Anonymize user record (keep for analytics)
        user.setEmail("deleted-" + UUID.randomUUID() + "@deleted.local");
        user.setFirstName("Deleted");
        user.setLastName("User");
        user.setPhoneNumber(null);
        user.setDeleted(true);
        user.setDeletionReason(reason.toString());
        user.setDeletedAt(Instant.now());
        userRepository.save(user);
        
        // 2. Delete related personal data
        profileService.deleteUserProfile(userId);
        paymentService.anonymizeUserPayments(userId);
        sessionService.deleteUserSessions(userId);
        
        // 3. Keep anonymized activity data for business analytics
        activityService.anonymizeUserActivity(userId);
        
        // 4. Log deletion for audit trail
        gdprAuditService.logDataDeletion(userId, reason);
        
        logger.info("User data deleted/anonymized: userId={}, reason={}", userId, reason);
    }
    
    public ConsentStatus getUserConsent(Long userId) {
        return consentRepository.findByUserId(userId)
            .map(consent -> ConsentStatus.builder()
                .marketingConsent(consent.isMarketingConsent())
                .analyticsConsent(consent.isAnalyticsConsent())
                .necessaryConsent(true) // Always true for app functionality
                .consentDate(consent.getUpdatedAt())
                .build())
            .orElse(ConsentStatus.defaultConsent());
    }
    
    public void updateUserConsent(Long userId, ConsentUpdateRequest request) {
        UserConsent consent = consentRepository.findByUserId(userId)
            .orElse(new UserConsent(userId));
            
        consent.setMarketingConsent(request.isMarketingConsent());
        consent.setAnalyticsConsent(request.isAnalyticsConsent());
        consent.setUpdatedAt(Instant.now());
        
        consentRepository.save(consent);
        
        // Log consent change
        gdprAuditService.logConsentChange(userId, request);
    }
}

// Data Retention Policy
@Component
@Scheduled(cron = "0 0 3 * * ?") // Daily at 3 AM
public class DataRetentionService {
    
    public void enforceRetentionPolicies() {
        // Delete inactive user accounts after 3 years
        LocalDateTime threeYearsAgo = LocalDateTime.now().minusYears(3);
        List<User> inactiveUsers = userRepository.findInactiveUsersSince(threeYearsAgo);
        
        for (User user : inactiveUsers) {
            if (!user.hasActiveSubscription() && !user.hasRecentActivity()) {
                gdprService.deleteUserData(user.getId(), DeletionReason.RETENTION_POLICY);
            }
        }
        
        // Delete audit logs older than 7 years (legal requirement)
        auditLogRepository.deleteOldLogs(LocalDateTime.now().minusYears(7));
        
        // Anonymize payment data older than 7 years
        paymentRepository.anonymizeOldPayments(LocalDateTime.now().minusYears(7));
        
        logger.info("Data retention policies enforced: deleted {} inactive accounts", 
                   inactiveUsers.size());
    }
}

// GDPR API Endpoints
@RestController
@RequestMapping("/api/user/gdpr")
public class GdprController {
    
    @GetMapping("/export")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDataExport> exportMyData(Authentication auth) {
        Long userId = getCurrentUserId(auth);
        UserDataExport export = gdprService.exportUserData(userId);
        
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=my-data.json")
            .body(export);
    }
    
    @PostMapping("/delete-account")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteMyAccount(
            @RequestBody AccountDeletionRequest request,
            Authentication auth) {
        
        Long userId = getCurrentUserId(auth);
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), getCurrentUser(auth).getPassword())) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Invalid password"));
        }
        
        // Schedule account deletion (30-day grace period)
        accountDeletionService.scheduleAccountDeletion(userId, request.getReason());
        
        return ResponseEntity.ok(new ApiResponse(true, 
            "Account deletion scheduled. You have 30 days to cancel."));
    }
    
    @GetMapping("/consent")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ConsentStatus> getMyConsent(Authentication auth) {
        Long userId = getCurrentUserId(auth);
        ConsentStatus consent = gdprService.getUserConsent(userId);
        return ResponseEntity.ok(consent);
    }
    
    @PutMapping("/consent")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateMyConsent(
            @RequestBody ConsentUpdateRequest request,
            Authentication auth) {
        
        Long userId = getCurrentUserId(auth);
        gdprService.updateUserConsent(userId, request);
        
        return ResponseEntity.ok(new ApiResponse(true, "Consent preferences updated"));
    }
}
```

---

## 🧪 Testing & Validation Plan

### 🔒 Security Testing Strategy

#### Phase 1: Automated Security Testing
```bash
#!/bin/bash
# security-tests.sh

echo "🔐 Running Security Test Suite..."

# 1. OWASP ZAP Baseline Scan
docker run -t owasp/zap2docker-stable zap-baseline.py \
    -t https://api.skillswap.com \
    -r zap-report.html

# 2. JWT Security Tests
echo "Testing JWT vulnerabilities..."
python3 jwt-security-tests.py

# 3. Rate Limiting Tests
echo "Testing rate limiting..."
for i in {1..10}; do
    curl -X POST https://api.skillswap.com/api/auth/login \
         -H "Content-Type: application/json" \
         -d '{"email":"test@test.com","password":"wrong"}' \
         -w "%{http_code}\n"
done

# 4. CORS Testing
echo "Testing CORS configuration..."
curl -H "Origin: https://malicious-site.com" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: X-Requested-With" \
     -X OPTIONS https://api.skillswap.com/api/auth/login

# 5. Input Validation Tests
echo "Testing input validation..."
python3 input-validation-tests.py

echo "✅ Security tests completed. Check reports for vulnerabilities."
```

#### Phase 2: Manual Penetration Testing
```python
# jwt-security-tests.py
import jwt
import requests
import base64

def test_jwt_security():
    """Test JWT implementation for common vulnerabilities"""
    
    # Test 1: Try to forge token with known secret
    print("🔍 Testing JWT secret strength...")
    
    weak_secrets = [
        "secret", "skillswap", "password", "123456",
        "skillswap-secret-key-that-should-be-changed-in-production"
    ]
    
    for secret in weak_secrets:
        try:
            payload = {"sub": "admin@skillswap.com", "exp": 9999999999}
            forged_token = jwt.encode(payload, secret, algorithm="HS256")
            
            response = requests.get(
                "https://api.skillswap.com/api/user/profile",
                headers={"Authorization": f"Bearer {forged_token}"}
            )
            
            if response.status_code == 200:
                print(f"🚨 CRITICAL: JWT forged with weak secret: {secret}")
                return False
                
        except Exception as e:
            continue
    
    print("✅ JWT secret appears strong")
    
    # Test 2: Algorithm confusion attack
    print("🔍 Testing algorithm confusion...")
    
    # Try to change algorithm to 'none'
    try:
        payload = {"sub": "admin@skillswap.com", "exp": 9999999999}
        none_token = jwt.encode(payload, "", algorithm="none")
        
        response = requests.get(
            "https://api.skillswap.com/api/user/profile",
            headers={"Authorization": f"Bearer {none_token}"}
        )
        
        if response.status_code == 200:
            print("🚨 CRITICAL: Algorithm confusion vulnerability found")
            return False
            
    except Exception as e:
        pass
    
    print("✅ Algorithm confusion protection working")
    
    # Test 3: Token reuse detection
    print("🔍 Testing token reuse detection...")
    
    # Login and get token
    login_response = requests.post(
        "https://api.skillswap.com/api/auth/login",
        json={"email": "test@skillswap.com", "password": "TestPassword123!"}
    )
    
    if login_response.status_code == 200:
        token = login_response.json()["accessToken"]
        
        # Use token from multiple IPs (simulate)
        headers = {"Authorization": f"Bearer {token}"}
        
        # First request
        response1 = requests.get(
            "https://api.skillswap.com/api/user/profile",
            headers={**headers, "X-Forwarded-For": "1.1.1.1"}
        )
        
        # Second request from different IP
        response2 = requests.get(
            "https://api.skillswap.com/api/user/profile", 
            headers={**headers, "X-Forwarded-For": "2.2.2.2"}
        )
        
        if response1.status_code == 200 and response2.status_code == 401:
            print("✅ Token reuse detection working")
        else:
            print("⚠️  Token reuse detection may need improvement")
    
    return True

if __name__ == "__main__":
    test_jwt_security()
```

#### Phase 3: Load Testing Security
```python
# load-test-security.py
import asyncio
import aiohttp
import time

async def test_rate_limiting():
    """Test rate limiting under load"""
    
    async with aiohttp.ClientSession() as session:
        tasks = []
        
        # Create 100 concurrent login attempts
        for i in range(100):
            task = asyncio.create_task(
                attempt_login(session, f"user{i}@test.com", "wrongpassword")
            )
            tasks.append(task)
        
        results = await asyncio.gather(*tasks)
        
        # Count responses
        success_count = sum(1 for r in results if r == 200)
        rate_limited_count = sum(1 for r in results if r == 429)
        
        print(f"Successful requests: {success_count}")
        print(f"Rate limited requests: {rate_limited_count}")
        
        if rate_limited_count > 90:
            print("✅ Rate limiting working effectively")
        else:
            print("🚨 Rate limiting may be bypassed under load")

async def attempt_login(session, email, password):
    try:
        async with session.post(
            "https://api.skillswap.com/api/auth/login",
            json={"email": email, "password": password},
            timeout=5
        ) as response:
            return response.status
    except:
        return 500

if __name__ == "__main__":
    asyncio.run(test_rate_limiting())
```

---

## 📋 Compliance Verification Checklist

### ✅ Pre-Production Security Checklist

#### Critical Security Controls
- [ ] **JWT Secret:** 256-bit random key, environment variable only
- [ ] **CORS:** Specific origins configured, no wildcards
- [ ] **Token Expiration:** Access tokens ≤ 15 minutes
- [ ] **Rate Limiting:** 5 attempts/minute on auth endpoints
- [ ] **HTTPS:** TLS 1.2+ enforced, HTTP redirects to HTTPS
- [ ] **Input Validation:** All user inputs sanitized and validated
- [ ] **Error Handling:** No sensitive information in error responses
- [ ] **Token Blacklisting:** Logout invalidates tokens

#### Monitoring & Alerting
- [ ] **Security Logs:** All auth events logged with IP/timestamp
- [ ] **Failed Login Alerts:** >5 failures trigger alert
- [ ] **Anomaly Detection:** Unusual patterns detected and flagged
- [ ] **Intrusion Detection:** Real-time monitoring active
- [ ] **Dashboards:** Security metrics visible to team
- [ ] **Incident Response:** Procedures documented and tested

#### Data Protection & Privacy
- [ ] **Data Encryption:** Sensitive data encrypted at rest
- [ ] **PII Handling:** Personal data properly protected
- [ ] **GDPR Compliance:** Data export/deletion implemented
- [ ] **Consent Management:** User preferences respected
- [ ] **Data Retention:** Automatic cleanup policies active
- [ ] **Backup Security:** Encrypted backups, access controlled

#### Code Security
- [ ] **Dependency Scanning:** No critical vulnerabilities
- [ ] **SAST Analysis:** Static analysis passed
- [ ] **Secret Scanning:** No hardcoded secrets in code
- [ ] **Code Review:** Security-focused peer review complete
- [ ] **Penetration Testing:** External security audit passed

### 🚀 Deployment Security Checklist

#### Infrastructure Security
- [ ] **Firewall Rules:** Only required ports open
- [ ] **Database Security:** Access restricted, encrypted connections
- [ ] **API Gateway:** Rate limiting and filtering active
- [ ] **Load Balancer:** DDoS protection enabled
- [ ] **Network Segmentation:** Services isolated properly
- [ ] **Backup Strategy:** Encrypted, tested restore procedures

#### Application Security
- [ ] **Environment Variables:** All secrets in environment
- [ ] **Security Headers:** HSTS, CSP, CSRF protection active
- [ ] **Session Management:** Secure session configuration
- [ ] **File Upload Security:** Size limits, type validation
- [ ] **API Versioning:** Deprecated versions secured/removed
- [ ] **Admin Interface:** Multi-factor authentication required

#### Monitoring & Response
- [ ] **SIEM Integration:** Security events centralized
- [ ] **Alerting Rules:** Critical events trigger immediate alerts
- [ ] **Log Retention:** Security logs kept for compliance period
- [ ] **Incident Runbooks:** Response procedures documented
- [ ] **Security Team Access:** 24/7 monitoring capability
- [ ] **Communication Plan:** Stakeholder notification procedures

---

## 📊 Success Metrics & KPIs

### 🎯 Security Performance Indicators

| **Metric** | **Baseline** | **Target** | **Critical** |
|------------|-------------|------------|-------------|
| **Failed Login Rate** | Unknown | <1% | >5% |
| **Token Validation Failures** | Unknown | <0.1% | >1% |
| **Security Incidents** | Unknown | 0/month | >1/month |
| **Mean Time to Detection** | Unknown | <5 min | >30 min |
| **Mean Time to Response** | Unknown | <15 min | >2 hours |
| **False Positive Rate** | Unknown | <5% | >20% |
| **Security Test Coverage** | 0% | 95% | <80% |
| **Vulnerability Resolution** | Unknown | <24h | >7 days |

### 📈 Monthly Security Review

#### Week 1: Implementation Status
- [ ] Critical vulnerabilities fixed
- [ ] High priority features deployed
- [ ] Security tests passing
- [ ] Monitoring systems active

#### Week 2: Performance Analysis  
- [ ] Security metrics reviewed
- [ ] False positive analysis
- [ ] Incident post-mortems completed
- [ ] Performance impact assessed

#### Week 3: Threat Intelligence
- [ ] New threats identified
- [ ] Vulnerability scanning completed
- [ ] Dependency updates applied
- [ ] Security training completed

#### Week 4: Compliance & Planning
- [ ] Compliance requirements verified
- [ ] Next month priorities set
- [ ] Budget and resources planned
- [ ] Stakeholder reporting completed

---

## 🔄 Continuous Security Improvement

### Automated Security Pipeline

```yaml
# .github/workflows/security.yml
name: Security Checks

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  security-scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Run OWASP Dependency Check
        uses: dependency-check/Dependency-Check_Action@main
        with:
          project: 'SkillSwap'
          path: '.'
          format: 'ALL'
          
      - name: Run CodeQL Analysis
        uses: github/codeql-action/analyze@v2
        with:
          languages: java
          
      - name: Run Semgrep Security Scan
        uses: returntocorp/semgrep-action@v1
        with:
          config: >-
            p/security-audit
            p/secrets
            p/owasp-top-ten
            
      - name: Upload Security Reports
        uses: actions/upload-artifact@v3
        with:
          name: security-reports
          path: reports/
```

### Monthly Security Tasks

1. **Week 1:** Vulnerability Assessment
   - Dependency scanning
   - Infrastructure security review
   - Penetration testing

2. **Week 2:** Incident Analysis
   - Security event review
   - False positive analysis
   - Process improvements

3. **Week 3:** Training & Documentation
   - Security awareness training
   - Documentation updates
   - Playbook reviews

4. **Week 4:** Planning & Compliance
   - Threat landscape analysis
   - Compliance audits
   - Next quarter planning

---

*Document Version: 1.0*  
*Created: 6 September 2025*  
*Next Review: 6 October 2025*  
*Owner: Security Engineering Team*
