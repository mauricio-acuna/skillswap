# 📋 USER STORIES DETALLADAS - SkillSwap

## 🎯 **Template de User Story**

```
#### 🆔 **US-XXX: Título Descriptivo**
- **Asignado a:** `Agent Type`
- **Story Points:** X pts
- **Estado:** 🟡 TODO / 🔵 IN PROGRESS / 🟠 REVIEW / 🔴 BLOCKED / ✅ DONE
- **Prioridad:** 🔴 ALTA / 🟠 MEDIA / 🟢 BAJA
- **Dependencies:** US-XXX, US-XXX
- **Epic:** Nombre del Epic

**Como** [tipo de usuario]  
**Quiero** [funcionalidad]  
**Para** [beneficio/objetivo]

**Acceptance Criteria:**
- [ ] Criterio específico y medible
- [ ] Criterio específico y medible
- [ ] Criterio específico y medible

**Technical Notes:**
- Detalles técnicos específicos
- Consideraciones de implementación

**Definition of Done:**
- [ ] Código implementado según acceptance criteria
- [ ] Tests unitarios/integración >= 80% coverage
- [ ] Code review aprobado por PM
- [ ] Documentación API actualizada (Backend)
- [ ] UI tested en iOS y Android (Frontend)
- [ ] No breaking changes sin coordinación
```

---

## 🔐 **SPRINT 1 - DETAILED USER STORIES**

### **Epic: Autenticación Completa**

#### 🔐 **US-001: API de Registro de Usuario** 
- **Asignado a:** `Backend Agent`
- **Story Points:** 5 pts
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🔴 **ALTA**
- **Dependencies:** None
- **Epic:** Autenticación

**Como** desarrollador frontend  
**Quiero** una API robusta de registro  
**Para** permitir que nuevos usuarios se registren de forma segura

**Acceptance Criteria:**
- [ ] **Endpoint:** POST /api/auth/register
- [ ] **Request Body:** `{email, password, firstName, lastName, acceptTerms}`
- [ ] **Response 201:** `{user: {id, email, firstName, lastName}, token, refreshToken}`
- [ ] **Response 400:** Validation errors (email format, password strength, missing fields)
- [ ] **Response 409:** Email already exists
- [ ] **Password:** Min 8 chars, 1 uppercase, 1 number, 1 special char
- [ ] **JWT Token:** 24h expiration + refresh token 30 days
- [ ] **Database:** User stored with hashed password (BCrypt)
- [ ] **Validation:** Email format, unique email, password complexity

**Technical Notes:**
- Spring Security configuration
- JWT implementation con refresh token
- User entity con validaciones JPA
- Password encoding con BCrypt strength 12
- Rate limiting: 5 intentos por IP por hora

**Definition of Done:**
- [ ] Endpoint funcional con todas las validaciones
- [ ] Tests unitarios para service layer
- [ ] Tests de integración para controller
- [ ] Documentación Swagger/OpenAPI
- [ ] Manejo de errores consistente
- [ ] Security audit passed

---

#### 🔑 **US-007: API de Login** 
- **Asignado a:** `Backend Agent`
- **Story Points:** 3 pts
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🔴 **ALTA**
- **Dependencies:** US-001
- **Epic:** Autenticación

**Como** usuario registrado  
**Quiero** hacer login con mis credenciales  
**Para** acceder a mi cuenta y usar la aplicación

**Acceptance Criteria:**
- [ ] **Endpoint:** POST /api/auth/login
- [ ] **Request Body:** `{email, password, rememberMe?}`
- [ ] **Response 200:** `{user: {id, email, firstName, lastName}, token, refreshToken}`
- [ ] **Response 401:** Invalid credentials
- [ ] **Response 429:** Too many attempts (rate limiting)
- [ ] **Brute Force Protection:** Max 5 intentos por email por hora
- [ ] **Remember Me:** Extend refresh token to 90 days if true
- [ ] **Last Login:** Update lastLoginAt timestamp

**Technical Notes:**
- Reusar JWT service de US-001
- Implementar rate limiting por email + IP
- Cache de intentos fallidos (Redis o in-memory)
- Audit log de logins exitosos y fallidos

---

#### 📱 **US-002: Pantalla de Registro Mobile**
- **Asignado a:** `Frontend Agent`
- **Story Points:** 8 pts
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🔴 **ALTA**
- **Dependencies:** US-001 (API)
- **Epic:** Autenticación

**Como** nuevo usuario  
**Quiero** una pantalla de registro intuitiva y atractiva  
**Para** crear mi cuenta fácilmente desde mi móvil

**Acceptance Criteria:**
- [ ] **Campos:** Email, Password, Confirm Password, First Name, Last Name
- [ ] **Validation Real-time:** Email format, password strength indicator
- [ ] **Password Strength:** Visual indicator (weak/medium/strong)
- [ ] **Terms & Conditions:** Checkbox obligatorio con link a términos
- [ ] **Loading States:** Spinner durante llamada API
- [ ] **Error Handling:** Mostrar errores específicos del backend
- [ ] **Success Flow:** Navegar a onboarding tras registro exitoso
- [ ] **Responsive:** Funcional en iPhone SE (375px) hasta iPad Pro
- [ ] **Accessibility:** Screen reader support, focus management

**Technical Notes:**
- React Hook Form para validaciones
- Redux Toolkit para estado auth
- React Navigation para flow
- Async Storage para token persistence
- Error boundary para crashes

**UI/UX Specifications:**
- Diseño card-based con rounded corners
- Primary color: #007AFF (iOS blue)
- Typography: SF Pro (iOS) / Roboto (Android)
- Input fields con floating labels
- Micro-animations para feedback

---

#### 📱 **US-008: Pantalla de Login**
- **Asignado a:** `Frontend Agent`
- **Story Points:** 4 pts
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🔴 **ALTA**
- **Dependencies:** US-007 (API)
- **Epic:** Autenticación

**Como** usuario registrado  
**Quiero** una pantalla de login rápida y eficiente  
**Para** acceder a mi cuenta sin fricciones

**Acceptance Criteria:**
- [ ] **Campos:** Email, Password
- [ ] **Remember Me:** Toggle para sesión extendida
- [ ] **Auto-fill:** Soporte para password managers
- [ ] **Biometric Login:** Face ID / Touch ID si está habilitado
- [ ] **Forgot Password:** Link a recovery flow
- [ ] **Quick Actions:** Auto-complete email de últimos logins
- [ ] **Error States:** Mensajes específicos (invalid credentials, network, etc.)
- [ ] **Loading State:** Disable form durante autenticación
- [ ] **Deep Linking:** Soporte para login desde links externos

**Technical Notes:**
- Integrar con Keychain (iOS) / Keystore (Android)
- React Native Biometrics para auth biométrica
- Deep linking con React Navigation

---

### **Epic: Gestión de Perfil**

#### 🛠️ **US-003: API de Perfil de Usuario**
- **Asignado a:** `Backend Agent`
- **Story Points:** 3 pts
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🟠 **MEDIA**
- **Dependencies:** US-001
- **Epic:** Perfil

**Como** usuario autenticado  
**Quiero** gestionar mi información de perfil  
**Para** presentarme profesionalmente a otros usuarios

**Acceptance Criteria:**
- [ ] **GET /api/users/profile:** Obtener perfil completo
- [ ] **PUT /api/users/profile:** Actualizar perfil
- [ ] **POST /api/users/profile/avatar:** Subir foto de perfil
- [ ] **Campos Perfil:** bio, location, skills[], interests[], availability
- [ ] **Skills Management:** CRUD operations en lista de habilidades
- [ ] **Image Upload:** Soporte para JPG/PNG, max 5MB, auto-resize
- [ ] **Validation:** Bio max 500 chars, location geocoding validation

**Technical Notes:**
- File upload con Spring Boot
- Image processing con ImageIO
- S3 o storage local para avatars
- Validation con Bean Validation

---

## 🔍 **Epic: Búsqueda y Discovery**

#### 🔍 **US-005: API de Búsqueda Avanzada**
- **Asignado a:** `Backend Agent`
- **Story Points:** 7 pts
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🟠 **MEDIA**
- **Dependencies:** US-003
- **Epic:** Búsqueda

**Como** usuario  
**Quiero** buscar otros usuarios con filtros avanzados  
**Para** encontrar intercambios relevantes y de calidad

**Acceptance Criteria:**
- [ ] **GET /api/users/search:** Búsqueda con múltiples filtros
- [ ] **Filtros:** skills[], location (radius), availability, rating_min
- [ ] **Paginación:** page, size, sort (relevance, distance, rating)
- [ ] **Scoring Algorithm:** Relevancia basada en skills match + proximidad
- [ ] **Exclude:** Usuarios ya contactados, bloqueados, inactivos
- [ ] **Performance:** Response time < 500ms para 10k usuarios
- [ ] **Analytics:** Track popular searches para recommendations

**Technical Notes:**
- Elasticsearch o Postgres full-text search
- Geospatial queries para location
- Caching con Redis para queries comunes
- Pagination con cursor-based o offset

---

## 💬 **Epic: Sistema de Contacto**

#### 📧 **US-009: API de Solicitudes de Contacto**
- **Asignado a:** `Backend Agent`
- **Story Points:** 5 pts
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🟠 **MEDIA**
- **Dependencies:** US-005
- **Epic:** Contacto

**Como** usuario  
**Quiero** enviar y gestionar solicitudes de contacto  
**Para** iniciar intercambios de habilidades organizadamente

**Acceptance Criteria:**
- [ ] **POST /api/contact-requests:** Crear solicitud con mensaje personalizado
- [ ] **GET /api/contact-requests:** Listar recibidas y enviadas
- [ ] **PUT /api/contact-requests/{id}/respond:** Aceptar/rechazar con mensaje
- [ ] **Estados:** pending, accepted, declined, expired (7 días)
- [ ] **Validations:** No duplicados, no auto-solicitudes
- [ ] **Notifications:** Trigger para push notifications
- [ ] **Limits:** Max 5 solicitudes pendientes por usuario

**Technical Notes:**
- Scheduled job para expired requests
- Event system para notifications
- Validation rules con database constraints

---

## 📊 **SPRINT METRICS & TRACKING**

### **Story Points Distribution:**
- **Backend Total:** 23 pts (US-001: 5, US-007: 3, US-003: 3, US-005: 7, US-009: 5)
- **Frontend Total:** 29 pts (US-002: 8, US-008: 4, US-004: 5, US-006: 6, US-010: 6)
- **Total Sprint 1:** 52 pts

### **Critical Path:**
1. US-001 (Backend) → US-002 (Frontend) → Login Flow
2. US-003 (Backend) → US-004 (Frontend) → Profile
3. US-005 (Backend) → US-006 (Frontend) → Search
4. US-009 (Backend) → US-010 (Frontend) → Contact

### **Risk Mitigation:**
- Backend debe completar US-001 y US-007 en semana 1
- Frontend puede trabajar en mocks mientras espera APIs
- Daily sync via git commits con @mentions
- Integration testing cada 2 días

---

## 🎯 **SUCCESS CRITERIA SPRINT 1**

### **Functional:**
- [ ] User registration + login flow completo
- [ ] Profile creation y editing funcional
- [ ] Search y contact requests working end-to-end
- [ ] App navegable sin crashes

### **Technical:**
- [ ] APIs deployed y documentadas
- [ ] Mobile app builds sin errores
- [ ] Test coverage > 70% backend, > 60% frontend
- [ ] Performance targets met (API < 500ms, UI 60fps)

### **Business:**
- [ ] Demo-ready para stakeholders
- [ ] Core user journey completable
- [ ] Data model escalable para próximos sprints
