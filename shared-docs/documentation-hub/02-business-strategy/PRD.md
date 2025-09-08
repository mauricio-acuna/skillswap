# Product Requirements Document (PRD)
## Aplicación Multiplataforma - Stack Tecnológico y Estrategia de Desarrollo

### 1. RESUMEN EJECUTIVO

**Proyecto:** **SKILLSWAP** - Marketplace de Intercambio de Habilidades P2P
**Objetivo:** Desarrollar una aplicación móvil multiplataforma (Android/iOS) con backend robusto, enfocada en el mercado europeo, siguiendo estándares de la industria y arquitectura escalable.

**Stack Recomendado:** React Native + Spring Boot + H2 → PostgreSQL
**Target Market:** Europa (España, Francia, Alemania, Reino Unido, Italia)

---

### 2. ANÁLISIS DEL MERCADO MÓVIL EUROPEO

#### 2.1 Market Share por País (Q2 2024)

| País | Android | iOS | Versiones Android Dominantes | Versiones iOS Dominantes |
|------|---------|-----|------------------------------|---------------------------|
| **España** | 64.2% | 35.8% | Android 12-14 (78%) | iOS 15-17 (85%) |
| **Francia** | 71.8% | 28.2% | Android 11-14 (82%) | iOS 15-17 (88%) |
| **Alemania** | 74.1% | 25.9% | Android 12-14 (75%) | iOS 16-17 (90%) |
| **Reino Unido** | 51.2% | 48.8% | Android 12-14 (80%) | iOS 15-17 (92%) |
| **Italia** | 76.3% | 23.7% | Android 11-14 (79%) | iOS 15-17 (86%) |

**Europa Promedio:** Android 67.5% | iOS 32.5%

#### 2.2 Dispositivos Más Populares en Europa (2024)

**Android Top Devices:**
1. **Samsung Galaxy A54** (15% market share Android)
2. **Samsung Galaxy S23** (12% market share Android)
3. **Xiaomi Redmi Note 12** (8% market share Android)
4. **Google Pixel 7/8** (6% market share Android)
5. **OnePlus Nord 3** (5% market share Android)

**iOS Devices:**
1. **iPhone 14** (28% market share iOS)
2. **iPhone 13** (24% market share iOS)
3. **iPhone 15** (18% market share iOS)
4. **iPhone 12** (15% market share iOS)
5. **iPhone SE 3rd Gen** (8% market share iOS)

#### 2.3 Características Técnicas Target

**Especificaciones Mínimas (Cobertura 95% del mercado):**

**Android:**
- **OS:** Android 8.0+ (API Level 26+)
- **RAM:** 3GB mínimo, 4GB recomendado
- **Storage:** 32GB mínimo
- **CPU:** Snapdragon 660+ / Exynos 9611+ / MediaTek Helio G85+
- **Screen:** 5.5" - 6.7", 720p mínimo

**iOS:**
- **OS:** iOS 13.0+ (iPhone 6s y posteriores)
- **RAM:** 3GB mínimo (iPhone 6s+)
- **Storage:** 32GB mínimo
- **CPU:** A10 Bionic+ (iPhone 7+)
- **Screen:** 4.7" - 6.7", Retina

#### 2.4 Implicaciones para el Desarrollo

**Estrategia de Compatibilidad:**
- **Android:** Soporte desde Android 8.0 (93% cobertura europea)
- **iOS:** Soporte desde iOS 13.0 (97% cobertura europea)
- **React Native:** Versión 0.72+ (compatible con ambos targets)

**Optimizaciones Específicas:**
- **Performance:** Optimizado para dispositivos con 3-4GB RAM
- **Storage:** App size < 50MB, cache inteligente
- **Battery:** Background tasks optimizados para dispositivos mid-range
- **Network:** Funcionalidad offline para conexiones europeas variables

---

### 3. ANÁLISIS DEL STACK TECNOLÓGICO - SKILLSWAP

#### 3.1 Frontend Móvil - Decisión Final

**SELECCIONADO: React Native 0.72+**

**Justificación para SkillSwap:**
- **Code Sharing:** 85-90% código compartido iOS/Android
- **Performance:** Suficiente para features de video calling y matching
- **Ecosystem:** Librerías maduras para video (react-native-webrtc)
- **European Market:** Excelente soporte para características locales
- **Developer Experience:** Hot reload crucial para iteración rápida

**Consideraciones Específicas Europa:**
- **Multi-language:** i18n nativo para 5+ idiomas europeos
- **GDPR Compliance:** Hooks y componentes para gestión de privacidad
- **Accessibility:** Soporte completo para estándares europeos

#### 3.2 Backend - Spring Boot para SkillSwap

**Componentes Específicos:**
- **Spring Boot 3.1+** con Java 17
- **Spring Security 6** (JWT + OAuth2 + GDPR compliance)
- **Spring Data JPA** con PostgreSQL (mejor para matching algorithms)
- **Spring WebSocket** (real-time matching y notifications)
- **Spring AI** (para skill matching algorithm)
- **Redis** (session management y caching de matches)

**APIs Especializadas para SkillSwap:**
- **Skill Taxonomy API** (custom classification)
- **Matching Algorithm Engine** (ML-based)
- **Credit System** (blockchain-inspired pero centralizado)
- **Video Calling Integration** (Agora.io o Zoom SDK)
- **Calendar Sync** (Google Calendar, Outlook, CalDAV)

#### 3.3 Base de Datos - PostgreSQL para SkillSwap

**Decisión: PostgreSQL desde Producción**
- **Razón:** Algoritmos de matching complejos requieren queries avanzadas
- **JSON Support:** Para skill taxonomies flexibles
- **Full-text Search:** Para búsqueda de habilidades
- **Scaling:** Mejor para relationships complejas user-skill-match

**Esquema Principal:**
```sql
-- Core entities para SkillSwap
Users (id, profile, location, timezone, created_at)
Skills (id, name, category, subcategory, difficulty_level)
UserSkills (user_id, skill_id, level, can_teach, wants_learn, verified)
Matches (id, teacher_id, learner_id, skill_id, status, credits)
Sessions (id, match_id, scheduled_at, duration, completed, rating)
Credits (user_id, balance, earned, spent, pending)
```

**H2 para Desarrollo Local:**
- Setup idéntico a PostgreSQL
- Migración automática en deployment
- Testing rápido sin dependencias externas

---

### 4. ARQUITECTURA TÉCNICA - SKILLSWAP

#### 4.1 Estructura del Proyecto SkillSwap
```
skillswap-app/
├── mobile/                     # React Native App
│   ├── android/               # Android-specific config
│   ├── ios/                   # iOS-specific config  
│   ├── src/
│   │   ├── components/        # Reusable UI components
│   │   │   ├── SkillCard/     # Skill display component
│   │   │   ├── MatchCard/     # Match display component
│   │   │   ├── VideoCall/     # Video calling component
│   │   │   └── Calendar/      # Calendar integration
│   │   ├── screens/           # App screens
│   │   │   ├── Auth/          # Login/Register
│   │   │   ├── Profile/       # User profile management
│   │   │   ├── Skills/        # Skill management
│   │   │   ├── Matches/       # Browse/manage matches
│   │   │   ├── Sessions/      # Schedule/join sessions
│   │   │   └── Credits/       # Credit management
│   │   ├── services/          # API communication
│   │   │   ├── AuthService.ts
│   │   │   ├── SkillService.ts
│   │   │   ├── MatchService.ts
│   │   │   └── VideoService.ts
│   │   ├── utils/             # Utilities
│   │   │   ├── skillTaxonomy.ts
│   │   │   ├── matchingAlgorithm.ts
│   │   │   └── dateUtils.ts
│   │   └── navigation/        # Navigation setup
│   └── package.json
├── backend/                   # Spring Boot API
│   ├── src/main/java/com/skillswap/
│   │   ├── controller/        # REST Controllers
│   │   │   ├── AuthController.java
│   │   │   ├── SkillController.java
│   │   │   ├── MatchController.java
│   │   │   ├── SessionController.java
│   │   │   └── CreditController.java
│   │   ├── service/           # Business Logic
│   │   │   ├── MatchingService.java
│   │   │   ├── CreditService.java
│   │   │   ├── SkillVerificationService.java
│   │   │   └── NotificationService.java
│   │   ├── repository/        # Data Access
│   │   │   ├── UserRepository.java
│   │   │   ├── SkillRepository.java
│   │   │   ├── MatchRepository.java
│   │   │   └── SessionRepository.java
│   │   ├── model/             # JPA Entities
│   │   │   ├── User.java
│   │   │   ├── Skill.java
│   │   │   ├── UserSkill.java
│   │   │   ├── Match.java
│   │   │   ├── Session.java
│   │   │   └── Credit.java
│   │   ├── config/            # Configuration
│   │   │   ├── SecurityConfig.java
│   │   │   ├── WebSocketConfig.java
│   │   │   └── RedisConfig.java
│   │   └── security/          # Security & GDPR
│   │       ├── JwtAuthFilter.java
│   │       ├── GdprService.java
│   │       └── DataProtectionUtils.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml (H2)
│   │   └── application-prod.yml (PostgreSQL)
│   └── pom.xml
├── docs/                      # Documentation
│   ├── api/                   # API documentation
│   ├── mobile/                # Mobile app documentation
│   └── deployment/            # Deployment guides
└── docker/                   # Containerization
    ├── docker-compose.dev.yml
    ├── docker-compose.prod.yml
    └── Dockerfile.backend
```

#### 4.2 Stack Tecnológico Detallado - SkillSwap

**Frontend (React Native):**
- **React Native 0.72+** (Android 8+ / iOS 13+ compatibility)
- **TypeScript 5.0+** (type safety crítica para matching algorithms)
- **React Navigation 6** (stack + tab navigation)
- **Redux Toolkit + RTK Query** (state management + API caching)
- **React Hook Form** (formularios complejos de skills)
- **React Native Vector Icons** (iconografía consistente)
- **React Native Paper** (Material Design + iOS adaptive)

**Video Calling & Real-time:**
- **React Native WebRTC** (video calls P2P)
- **Socket.io Client** (real-time notifications)
- **React Native Calendar Kit** (scheduling integration)
- **React Native Permissions** (camera, microphone)

**Specific to European Market:**
- **React Native Localize** (multi-language support)
- **React Native Privacy Snapshot** (iOS privacy screens)
- **React Native Cookie Manager** (GDPR compliance)

**Backend (Spring Boot):**
- **Java 17 LTS**
- **Spring Boot 3.1.4** (latest stable)
- **Spring Security 6** (JWT + OAuth2 + GDPR)
- **Spring Data JPA** (with PostgreSQL)
- **Spring WebSocket** (real-time features)
- **Spring Cache** (Redis integration)
- **Spring Validation** (input validation)
- **Spring Actuator** (monitoring & health checks)

**Database & Caching:**
- **PostgreSQL 15+** (primary database)
- **H2 Database** (development & testing)
- **Redis 7+** (session cache + real-time data)
- **Flyway** (database migrations)

**External Integrations:**
- **Agora.io SDK** (video calling infrastructure)
- **Google Calendar API** (calendar integration)
- **Microsoft Graph API** (Outlook calendar)
- **SendGrid** (transactional emails)
- **Firebase Cloud Messaging** (push notifications)

**DevOps & Monitoring:**
- **Docker & Docker Compose**
- **GitHub Actions** (CI/CD)
- **Nginx** (reverse proxy)
- **Let's Encrypt** (SSL certificates)
- **Prometheus + Grafana** (monitoring)
- **Sentry** (error tracking)

---

### 5. ROADMAP DE DESARROLLO - SKILLSWAP

#### 5.1 Metodología: Agile con enfoque Lean Startup

**Sprints de 2 semanas** con validación continua de mercado

#### 5.2 Roadmap Detallado SkillSwap

**SPRINT 1-2: Foundation & Auth (4 semanas)**
- [ ] Setup React Native con TypeScript
- [ ] Configuración Spring Boot + PostgreSQL local
- [ ] Sistema de autenticación (JWT + OAuth2)
- [ ] Registro/Login con validación GDPR
- [ ] Basic user profile creation
- [ ] CI/CD pipeline setup
- [ ] **Milestone:** Usuario puede registrarse y crear perfil básico

**SPRINT 3-4: Skill Management (4 semanas)**
- [ ] Skill taxonomy system (categorías predefinidas)
- [ ] Add/remove skills to user profile
- [ ] Skill level assessment (self-reported)
- [ ] Basic skill search functionality
- [ ] **Demo:** 100 skills disponibles, usuarios pueden agregar
- [ ] **Milestone:** Usuarios pueden gestionar su inventario de skills

**SPRINT 5-6: Matching Engine MVP (4 semanas)**
- [ ] Basic matching algorithm (skill complementarity)
- [ ] Match suggestions UI
- [ ] Send/accept match requests
- [ ] Basic credit system (earn 1, spend 1)
- [ ] **Demo:** Matching funcional entre 50 beta users
- [ ] **Milestone:** Matching P2P funcional

**SPRINT 7-8: Session Management (4 semanas)**
- [ ] Calendar integration (Google Calendar)
- [ ] Session scheduling interface
- [ ] Session management (upcoming, completed)
- [ ] Basic session rating system
- [ ] Session history and analytics
- [ ] **Demo:** End-to-end session booking
- [ ] **Milestone:** Complete session lifecycle

**SPRINT 9-10: Video Integration (4 semanas)**
- [ ] React Native WebRTC integration
- [ ] In-app video calling
- [ ] Session recording capabilities (optional)
- [ ] Screen sharing for teaching
- [ ] **Beta Test:** 25 sessions completadas con video
- [ ] **Milestone:** Video calling integrado

**SPRINT 11-12: Advanced Features (4 semanas)**
- [ ] Advanced matching algorithm (ML-based)
- [ ] Skill verification system (peer reviews)
- [ ] Group sessions support
- [ ] Learning paths creation
- [ ] **Market Test:** Launch to 500 beta users
- [ ] **Milestone:** Advanced features validadas

**SPRINT 13-14: European Localization (4 semanas)**
- [ ] Multi-language support (ES, FR, DE, EN, IT)
- [ ] GDPR compliance features
- [ ] European time zones support
- [ ] Local payment methods integration
- [ ] **Pre-launch:** App store submissions
- [ ] **Milestone:** Ready for European launch

**SPRINT 15-16: Launch & Optimization (4 semanas)**
- [ ] App Store & Google Play launch
- [ ] Performance monitoring and optimization
- [ ] User feedback integration
- [ ] Marketing automation setup
- [ ] **Go-live:** Public launch in 3 countries
- [ ] **Milestone:** Live in production

#### 5.3 Success Metrics por Sprint

| Sprint | Key Metric | Target |
|--------|------------|--------|
| 1-2 | User registrations | 50 beta users |
| 3-4 | Skills added per user | 5+ skills average |
| 5-6 | Successful matches | 20+ matches created |
| 7-8 | Sessions scheduled | 10+ sessions/week |
| 9-10 | Video sessions completed | 80%+ completion rate |
| 11-12 | User retention (week 2) | 40%+ retention |
| 13-14 | Multi-language adoption | 30%+ non-English |
| 15-16 | Public user acquisition | 1000+ organic users |

#### 4.3 Entorno de Desarrollo

**Requisitos del Sistema:**
- **macOS** con Xcode (para iOS)
- **Android Studio** (para Android)
- **Node.js 18+** y **npm/yarn**
- **Java 17+** y **Maven**
- **Git** y **Docker**

**IDEs Recomendados:**
- **VS Code** (React Native + extensiones)
- **IntelliJ IDEA** (Spring Boot)
- **Android Studio** (debugging Android)
- **Xcode** (debugging iOS)

#### 4.4 Testing Strategy

**Frontend:**
- **Jest** (unit testing)
- **React Native Testing Library** (component testing)
- **Detox** (E2E testing)

**Backend:**
- **JUnit 5** (unit testing)
- **Mockito** (mocking)
- **TestContainers** (integration testing)
- **Spring Boot Test** (context testing)

---

### 5. CONSIDERACIONES DE SEGURIDAD

#### 5.1 Autenticación y Autorización
- **JWT Tokens** con refresh tokens
- **OAuth2** para login social
- **Hashing** seguro de contraseñas (BCrypt)
- **Rate limiting** en APIs

#### 5.2 Protección de Datos
- **HTTPS** obligatorio
- **Encriptación** de datos sensibles
- **Validación** de entrada en cliente y servidor
- **Logs** de seguridad y auditoría

---

### 6. ESCALABILIDAD Y PERFORMANCE

#### 6.1 Frontend
- **Lazy loading** de componentes
- **Optimización** de imágenes
- **Caching** inteligente
- **Bundle splitting**

#### 6.2 Backend
- **Connection pooling**
- **Caching** con Redis (futuro)
- **Pagination** en listados
- **Índices** de base de datos optimizados

---

### 7. TIMELINE Y RECURSOS

**Duración Estimada:** 10 sprints (20 semanas / 5 meses)

**Equipo Recomendado:**
- 1 Desarrollador Full-Stack (React Native + Spring Boot)
- 1 UI/UX Designer (part-time)
- 1 QA Tester (part-time en sprints finales)

**Hitos Principales:**
- **Semana 4:** MVP funcional
- **Semana 8:** Beta testing
- **Semana 16:** Release candidate
- **Semana 20:** Lanzamiento en stores

---

### 8. RIESGOS Y MITIGACIONES

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| Problemas de compatibilidad iOS/Android | Media | Alto | Testing continuo en dispositivos reales |
| Performance en dispositivos antiguos | Media | Medio | Optimización progresiva y testing |
| Cambios en policies de stores | Baja | Alto | Seguimiento de guidelines y actualizaciones |
| Escalabilidad del backend | Baja | Alto | Arquitectura modular y preparada para escalar |

---

### 9. CRITERIOS DE ÉXITO - SKILLSWAP EUROPA

**Técnicos:**
- [ ] App funciona en iOS 13+ (97% cobertura) y Android 8+ (93% cobertura)
- [ ] Tiempo de carga < 2 segundos en dispositivos mid-range europeos
- [ ] Video calling funciona en 90%+ de sesiones
- [ ] 99.5% uptime del backend
- [ ] Soporte completo para 5 idiomas europeos
- [ ] Cumplimiento GDPR al 100%

**Performance Específico Europa:**
- [ ] < 100ms latencia para matching en misma ciudad
- [ ] < 3MB data usage por sesión de video de 30 min
- [ ] App size < 45MB (importante para mercados con data limitada)
- [ ] Funciona offline para features básicas (perfil, skills)

**Negocio:**
- [ ] Publicación exitosa en App Store y Google Play (5 países)
- [ ] 1000+ usuarios activos en 6 meses
- [ ] 60%+ matches resultan en sesión programada
- [ ] 40%+ retention rate (semana 2)
- [ ] Rating promedio > 4.2 estrellas en ambas stores
- [ ] 15%+ conversión a premium en 3 meses

**Market Specific:**
- [ ] 25%+ usuarios usando idioma local (no inglés)
- [ ] 30%+ sesiones cross-country (español-francés, etc.)
- [ ] 20%+ usuarios en horarios europeos prime (18-22h CET)
- [ ] Soporte customer service en 3+ idiomas

---

*Documento creado el 6 de septiembre de 2025*
*Versión 1.0*
