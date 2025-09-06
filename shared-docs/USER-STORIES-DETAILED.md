# 📋 Detailed User Stories - SkillSwap
## Sprint 1: Foundation & Authentication

**Sprint Duration:** 2 semanas (6-20 septiembre 2025)  
**Story Points Total:** 21  
**Sprint Goal:** Establecer base técnica sólida con autenticación funcional

---

## 🎯 SPRINT 1 USER STORIES

### 🔐 Authentication & User Management

#### US-001: Usuario puede registrarse en la aplicación
**Como** un nuevo usuario  
**Quiero** poder registrarme en SkillSwap  
**Para** acceder a la plataforma de intercambio de habilidades

**Acceptance Criteria:**
- [ ] Formulario de registro con email, contraseña, nombre y apellidos
- [ ] Validación de email único
- [ ] Validación de contraseña (min 8 caracteres, mayúscula, número)
- [ ] Checkbox de aceptación de términos GDPR
- [ ] Confirmación por email (opcional para MVP)
- [ ] Redirección a pantalla de perfil después del registro

**Technical Tasks:**
- [ ] **[BACKEND]** `POST /auth/register` endpoint
- [ ] **[BACKEND]** User entity y validaciones
- [ ] **[BACKEND]** Email uniqueness validation
- [ ] **[FRONTEND]** Pantalla de registro con validaciones
- [ ] **[FRONTEND]** Integración con API de registro
- [ ] **[DOCS]** API documentation actualizada

**Story Points:** 5  
**Priority:** High  
**Agent Responsible:** Backend + Frontend  
**Status:** 🔄 In Progress

---

#### US-002: Usuario puede hacer login
**Como** un usuario registrado  
**Quiero** poder hacer login con mis credenciales  
**Para** acceder a mi cuenta y utilizar la aplicación

**Acceptance Criteria:**
- [ ] Formulario de login con email y contraseña
- [ ] Validación de credenciales
- [ ] Generación y retorno de JWT token
- [ ] Redirección a pantalla principal después del login
- [ ] Manejo de errores (credenciales incorrectas, usuario no encontrado)
- [ ] Opción "Recordar sesión" (opcional)

**Technical Tasks:**
- [ ] **[BACKEND]** `POST /auth/login` endpoint
- [ ] **[BACKEND]** JWT token generation y validation
- [ ] **[BACKEND]** Password hashing y verification
- [ ] **[FRONTEND]** Pantalla de login
- [ ] **[FRONTEND]** Token storage y management
- [ ] **[FRONTEND]** Error handling y user feedback

**Story Points:** 3  
**Priority:** High  
**Agent Responsible:** Backend + Frontend  
**Status:** 🔄 In Progress

---

#### US-003: Usuario puede ver y editar su perfil
**Como** un usuario logueado  
**Quiero** poder ver y editar mi información de perfil  
**Para** mantener mis datos actualizados

**Acceptance Criteria:**
- [ ] Pantalla de perfil con información del usuario
- [ ] Campos editables: nombre, apellidos, biografía, ubicación
- [ ] Foto de perfil (opcional para Sprint 1)
- [ ] Validación de campos obligatorios
- [ ] Confirmación de cambios guardados
- [ ] Logout desde el perfil

**Technical Tasks:**
- [ ] **[BACKEND]** `GET /users/profile` endpoint
- [ ] **[BACKEND]** `PUT /users/profile` endpoint
- [ ] **[BACKEND]** Profile validation rules
- [ ] **[FRONTEND]** Pantalla de visualización de perfil
- [ ] **[FRONTEND]** Pantalla de edición de perfil
- [ ] **[FRONTEND]** Integration con API de perfil

**Story Points:** 4  
**Priority:** Medium  
**Agent Responsible:** Backend + Frontend  
**Status:** 🔄 In Progress

---

### 🛠️ Skills Management Foundation

#### US-004: Usuario puede ver categorías de habilidades
**Como** un usuario  
**Quiero** ver las categorías de habilidades disponibles  
**Para** entender qué tipos de skills puedo agregar

**Acceptance Criteria:**
- [ ] Lista de categorías predefinidas (Tecnología, Idiomas, Arte, etc.)
- [ ] Descripción breve de cada categoría
- [ ] Iconos o imágenes representativas (opcional)
- [ ] Categorías organizadas alfabéticamente
- [ ] Responsive design para móvil

**Technical Tasks:**
- [ ] **[BACKEND]** Skills categories data model
- [ ] **[BACKEND]** `GET /skills/categories` endpoint
- [ ] **[BACKEND]** Seed data para categorías iniciales
- [ ] **[FRONTEND]** Pantalla de categorías de skills
- [ ] **[FRONTEND]** UI components para mostrar categorías
- [ ] **[DOCS]** Skills taxonomy documentation

**Story Points:** 2  
**Priority:** Medium  
**Agent Responsible:** Backend + Frontend  
**Status:** 🔄 In Progress

---

#### US-005: Usuario puede ver skills disponibles
**Como** un usuario  
**Quiero** ver las habilidades disponibles en cada categoría  
**Para** seleccionar las que domino o quiero aprender

**Acceptance Criteria:**
- [ ] Lista de skills por categoría
- [ ] Búsqueda básica de skills por nombre
- [ ] Filtro por categoría
- [ ] Paginación o scroll infinito
- [ ] Skills mostradas con nombre y descripción breve

**Technical Tasks:**
- [ ] **[BACKEND]** Skills data model
- [ ] **[BACKEND]** `GET /skills` endpoint con filtros
- [ ] **[BACKEND]** Seed data para skills iniciales (100+ skills)
- [ ] **[FRONTEND]** Pantalla de exploración de skills
- [ ] **[FRONTEND]** Componente de búsqueda
- [ ] **[FRONTEND]** Filtros y paginación

**Story Points:** 3  
**Priority:** Medium  
**Agent Responsible:** Backend + Frontend  
**Status:** 🔄 In Progress

---

### 🏗️ Technical Foundation

#### US-006: Sistema tiene setup de desarrollo robusto
**Como** desarrollador  
**Quiero** un setup de desarrollo fácil y reproducible  
**Para** poder trabajar eficientemente en el proyecto

**Acceptance Criteria:**
- [ ] Backend se levanta con un comando
- [ ] Frontend se levanta con un comando
- [ ] Base de datos local configurada automáticamente
- [ ] Variables de entorno documentadas
- [ ] Docker setup funcional (opcional)
- [ ] README con instrucciones claras

**Technical Tasks:**
- [ ] **[BACKEND]** Spring Boot con configuración óptima
- [ ] **[BACKEND]** H2 database para desarrollo
- [ ] **[BACKEND]** Maven/Gradle build optimizado
- [ ] **[FRONTEND]** React Native con TypeScript
- [ ] **[FRONTEND]** Metro bundler optimizado
- [ ] **[DOCS]** Setup documentation completa

**Story Points:** 4  
**Priority:** High  
**Agent Responsible:** Backend + Frontend + Coordinator  
**Status:** ✅ Completed

---

## 📊 SPRINT 1 SUMMARY

### Story Points Distribution
- **Authentication:** 12 points (US-001, US-002, US-003)
- **Skills Foundation:** 5 points (US-004, US-005)
- **Technical Setup:** 4 points (US-006)
- **Total:** 21 points

### Dependencies
- US-002 depends on US-001 (login requires registration)
- US-003 depends on US-002 (profile requires authentication)
- US-004 and US-005 can be developed in parallel
- US-006 is foundational for all others

### Risk Assessment
- **High Risk:** JWT implementation complexity
- **Medium Risk:** React Native platform-specific issues
- **Low Risk:** Skills data modeling

---

## 📋 DEFINITION OF DONE

### Backend Stories
- [ ] Endpoint implemented and tested
- [ ] Unit tests written (minimum 80% coverage)
- [ ] API documentation updated
- [ ] Database migrations created (if needed)
- [ ] Error handling implemented
- [ ] Logs configured appropriately

### Frontend Stories
- [ ] UI implemented according to designs
- [ ] Integration with backend APIs working
- [ ] Loading states and error handling
- [ ] Responsive design for different screen sizes
- [ ] Basic navigation flows working
- [ ] No console errors or warnings

### Cross-functional
- [ ] Code reviewed and approved
- [ ] Integration testing passed
- [ ] Performance acceptable
- [ ] GDPR compliance considered
- [ ] Documentation updated

---

## 🔄 NEXT SPRINT PREVIEW

### Sprint 2: Enhanced Skills & Profile
- User can add/remove skills to their profile
- Skill level assessment (beginner, intermediate, expert)
- Enhanced profile with skills showcase
- Basic skill matching algorithm foundation
- JWT refresh token implementation

---

*Última actualización: 6 de septiembre de 2025*  
*Maintained by: Coordinator Agent*  
*Review frequency: Weekly*
