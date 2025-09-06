# 🤝 SkillSwap - Guía de Coordinación Multi-Agente
## Estrategia para Desarrollo Paralelo

### 📋 OVERVIEW DE LA ESTRATEGIA

Esta guía facilita la colaboración entre **múltiples agentes Sonnet** trabajando en paralelo en diferentes partes del proyecto SkillSwap:

- **Agent Backend:** VS Code apuntando a `/skillswap-backend`
- **Agent Frontend:** VS Code apuntando a `/skillswap-frontend`  
- **Agent Coordinador:** VS Code apuntando a `/shared-docs` (este agente)

---

## 📁 ESTRUCTURA OPTIMIZADA PARA MULTI-AGENTE

```
/Users/mauricio/Proyectos/appMultiplatform/
├── shared-docs/                    # 📚 DOCUMENTACIÓN COMPARTIDA
│   ├── PRD.md                      # PRD general del proyecto
│   ├── MercadoObjetivo.md          # Análisis de mercado
│   ├── SkillSwap-TechnicalSpecs.md # Especificaciones técnicas
│   ├── API-Contract.md             # Contrato de API (Backend ↔ Frontend)
│   ├── Database-Schema.md          # Esquema de base de datos
│   ├── WebSocket-Events.md         # Eventos en tiempo real
│   ├── GDPR-Compliance.md          # Requerimientos GDPR
│   ├── Testing-Strategy.md         # Estrategia de testing
│   └── Deployment-Guide.md         # Guía de deployment
├── skillswap-backend/              # 🔧 BACKEND (Spring Boot)
│   ├── README.md                   # PRD específico backend
│   ├── src/main/java/              # Código fuente Java
│   ├── src/main/resources/         # Configuraciones
│   ├── src/test/                   # Tests del backend
│   ├── docs/                       # Documentación backend
│   ├── docker/                     # Docker configs
│   └── pom.xml                     # Maven dependencies
├── skillswap-frontend/             # 📱 FRONTEND (React Native)
│   ├── README.md                   # PRD específico frontend
│   ├── src/                        # Código fuente TypeScript
│   ├── android/                    # Configuración Android
│   ├── ios/                        # Configuración iOS
│   ├── __tests__/                  # Tests del frontend
│   ├── docs/                       # Documentación frontend
│   └── package.json                # npm dependencies
└── README.md                       # README principal del proyecto
```

---

## 🎯 RESPONSABILIDADES POR AGENTE

### 🔧 **AGENT BACKEND** (skillswap-backend/)
**Workspace:** `/Users/mauricio/Proyectos/appMultiplatform/skillswap-backend`

**Responsabilidades Principales:**
- ✅ Spring Boot application setup
- ✅ Database design y migrations (PostgreSQL/H2)
- ✅ REST API endpoints implementation
- ✅ JWT Authentication & Security
- ✅ Business logic (matching algorithm, credit system)
- ✅ WebSocket real-time features
- ✅ External integrations (Agora, Calendar, Email)
- ✅ GDPR compliance backend
- ✅ Testing (Unit + Integration)
- ✅ API documentation (Swagger)

**Archivos Clave a Entregar:**
- `src/main/java/com/skillswap/` - Código Java
- `src/main/resources/db/migration/` - Migrations
- `src/main/resources/application.yml` - Configuración
- `docs/api/` - Documentación de APIs
- `docker/docker-compose.yml` - Setup local

### 📱 **AGENT FRONTEND** (skillswap-frontend/)
**Workspace:** `/Users/mauricio/Proyectos/appMultiplatform/skillswap-frontend`

**Responsabilidades Principales:**
- ✅ React Native setup (iOS + Android)
- ✅ Navigation implementation
- ✅ UI/UX components library
- ✅ State management (Redux Toolkit)
- ✅ API integration con backend
- ✅ Video calling implementation (WebRTC)
- ✅ Real-time features (WebSocket client)
- ✅ Push notifications (FCM)
- ✅ Multi-language support (i18n)
- ✅ GDPR compliance UI
- ✅ Testing (Component + E2E)
- ✅ Performance optimization

**Archivos Clave a Entregar:**
- `src/` - Código TypeScript/React Native
- `android/` y `ios/` - Configuraciones nativas
- `src/services/api/` - Integración con backend
- `src/components/` - Component library
- `src/navigation/` - Navigation setup

### 📚 **AGENT COORDINADOR** (shared-docs/)
**Workspace:** `/Users/mauricio/Proyectos/appMultiplatform/shared-docs`

**Responsabilidades Principales:**
- ✅ Mantener documentación actualizada
- ✅ Definir contratos de API
- ✅ Coordinar cambios entre equipos
- ✅ Gestionar requirements y especificaciones
- ✅ Mantener roadmap y milestones
- ✅ Facilitar comunicación entre agentes
- ✅ Resolver conflicts de integración
- ✅ Mantener deployment guides

---

## 🔄 PROTOCOLO DE COMUNICACIÓN

### 📝 **Documentos de Coordinación (shared-docs/)**

#### 1. **API-Contract.md** - Contrato Backend ↔ Frontend
```markdown
# API Contract - SkillSwap

## Base URL
- Development: http://localhost:8080/api/v1
- Production: https://api.skillswap.com/api/v1

## Authentication
- Header: Authorization: Bearer {jwt_token}
- Token expires: 15 minutes
- Refresh token expires: 7 days

## Endpoints Status
✅ = Implemented | 🔄 = In Progress | ❌ = Not Started

### Authentication
- ✅ POST /auth/login
- ✅ POST /auth/register  
- 🔄 POST /auth/refresh
- ❌ POST /auth/forgot-password

### Users
- 🔄 GET /users/profile
- ❌ PUT /users/profile
- ❌ DELETE /users/profile

### Skills
- ❌ GET /skills
- ❌ POST /skills/user-skills
- ❌ GET /skills/user-skills
```

#### 2. **Database-Schema.md** - Esquema de Base de Datos
```sql
-- Core tables for SkillSwap
-- Updated: 2025-09-06

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    -- ... (schema details)
);

-- Status: ✅ Implemented ✅ Tested ❌ Needs Migration
```

#### 3. **WebSocket-Events.md** - Eventos en Tiempo Real
```markdown
# WebSocket Events - SkillSwap

## Client → Server Events
- join_room: { roomId, userId }
- leave_room: { roomId, userId }
- send_match_request: { targetUserId, skillId, message }

## Server → Client Events
- match_request_received: { fromUser, skill, message }
- session_starting: { sessionId, startsIn }
- credit_balance_updated: { newBalance, transaction }
```

### 🔗 **Sincronización de Cambios**

#### **Cuando Backend cambia API:**
1. **Backend Agent** actualiza `shared-docs/API-Contract.md`
2. **Backend Agent** documenta cambios en commit message
3. **Frontend Agent** lee cambios y adapta cliente
4. **Coordinador** valida que la integración funcione

#### **Cuando Frontend necesita nueva API:**
1. **Frontend Agent** actualiza `shared-docs/API-Contract.md` con request
2. **Backend Agent** implementa endpoint solicitado
3. **Backend Agent** actualiza documentación con implementación
4. **Frontend Agent** integra nuevo endpoint

#### **Para cambios de Database:**
1. **Backend Agent** actualiza `shared-docs/Database-Schema.md`
2. **Backend Agent** crea migration en `src/main/resources/db/migration/`
3. **Coordinador** valida que el schema sea consistente

---

## 📋 WORKFLOW DE DESARROLLO

### 🎯 **Sprint Planning Multi-Agente**

#### **Cada Sprint (2 semanas):**

**Week 1:**
- **Lunes:** Planning meeting (definir objetivos del sprint)
- **Martes-Viernes:** Desarrollo independiente
- **Viernes:** Sync meeting (revisar progreso, resolver blockers)

**Week 2:**
- **Lunes-Jueves:** Desarrollo + Integration
- **Viernes:** Sprint review + Integration testing

#### **Daily Sync Protocol:**
1. Cada agente actualiza su README.md con progreso diario
2. Commits frecuentes con mensajes descriptivos
3. Updates en shared-docs/ cuando hay cambios de contrato

### 🔄 **Integration Points**

#### **Backend → Frontend Integration:**
- **API Client Testing:** Frontend debe poder connectar a backend local
- **Data Contracts:** Estructuras JSON deben coincidir
- **Error Handling:** Códigos de error consistentes
- **Authentication:** JWT flow debe funcionar end-to-end

#### **Real-time Integration:**
- **WebSocket Connection:** Cliente React Native → Server Spring Boot
- **Event Handling:** Eventos deben ser consistentes en ambos lados
- **Reconnection Logic:** Manejar pérdidas de conexión

---

## ⚡ QUICK START POR AGENTE

### 🔧 **Para Backend Agent:**

```bash
# Setup workspace
cd /Users/mauricio/Proyectos/appMultiplatform/skillswap-backend

# Leer documentación específica
cat README.md

# Referencias importantes
cat ../shared-docs/API-Contract.md
cat ../shared-docs/Database-Schema.md

# Start development
./mvnw spring-boot:run
```

**Archivos más importantes para Backend:**
- `README.md` - PRD específico backend
- `../shared-docs/API-Contract.md` - APIs a implementar
- `../shared-docs/Database-Schema.md` - Schema de DB
- `../shared-docs/GDPR-Compliance.md` - Requirements GDPR

### 📱 **Para Frontend Agent:**

```bash
# Setup workspace  
cd /Users/mauricio/Proyectos/appMultiplatform/skillswap-frontend

# Leer documentación específica
cat README.md

# Referencias importantes
cat ../shared-docs/API-Contract.md
cat ../shared-docs/WebSocket-Events.md

# Start development
npm install
npm run ios # o npm run android
```

**Archivos más importantes para Frontend:**
- `README.md` - PRD específico frontend
- `../shared-docs/API-Contract.md` - APIs disponibles
- `../shared-docs/WebSocket-Events.md` - Eventos real-time
- `../shared-docs/SkillSwap-TechnicalSpecs.md` - Specs técnicas

### 📚 **Para Coordinador Agent:**

```bash
# Setup workspace
cd /Users/mauricio/Proyectos/appMultiplatform/shared-docs

# Monitorear cambios en ambos proyectos
git log --oneline skillswap-backend/ skillswap-frontend/

# Mantener documentación actualizada
git status
```

---

## 🚨 RESOLUCIÓN DE CONFLICTOS

### **Conflict Types & Solutions:**

#### **API Contract Mismatch:**
- **Problema:** Frontend espera campo que Backend no envía
- **Solución:** Coordinador media, se decide si Backend agrega campo o Frontend se adapta
- **Documentar en:** `shared-docs/API-Contract.md`

#### **Database Schema Changes:**
- **Problema:** Backend necesita cambiar schema existente
- **Solución:** Backend crea migration, Coordinador valida que no rompa Frontend
- **Documentar en:** `shared-docs/Database-Schema.md`

#### **WebSocket Event Changes:**
- **Problema:** Cambio en estructura de eventos real-time
- **Solución:** Ambos agentes se coordinan para cambio simultáneo
- **Documentar en:** `shared-docs/WebSocket-Events.md`

### **Escalation Path:**
1. **Self-resolve:** Agente intenta resolver con documentación
2. **Peer-resolve:** Coordinador media entre agentes
3. **Escalate:** Si no se puede resolver, se consulta al human developer

---

## 📊 METRICS & TRACKING

### **Daily Standup Info:**
Cada agente actualiza diariamente:

```markdown
## Daily Update - [Agent Name] - [Date]

### ✅ Completed Yesterday:
- [ ] Task 1
- [ ] Task 2

### 🔄 Working Today:
- [ ] Task 3
- [ ] Task 4

### 🚫 Blockers:
- Waiting for API X from Backend
- Need clarification on requirement Y

### 📝 Notes:
- Updated API contract for user profile
- Found issue with WebSocket connection
```

### **Integration Health Check:**
Semanal verification:
- [ ] Backend APIs respond correctly
- [ ] Frontend can authenticate
- [ ] WebSocket connection works
- [ ] Database migrations run successfully
- [ ] Tests pass on both sides

---

## 🎯 SUCCESS CRITERIA

### **Sprint Success = All Green:**
- ✅ Backend APIs implemented and tested
- ✅ Frontend UI implemented and connected
- ✅ Integration tests pass
- ✅ Documentation updated
- ✅ No critical blockers for next sprint

### **Daily Success = Sync Maintained:**
- ✅ Code committed with clear messages
- ✅ README.md updated with progress
- ✅ Shared docs updated if contracts changed
- ✅ No unresolved blockers > 24h

---

## 🔧 HERRAMIENTAS DE COORDINACIÓN

### **Git Strategy:**
```bash
# Branches per agent
main                    # Stable integration branch
backend/feature-name    # Backend features
frontend/feature-name   # Frontend features
docs/update-name        # Documentation updates

# Integration workflow
1. Each agent works in their branch
2. Daily commits to their branch
3. Weekly merge to main after integration test
```

### **Communication Files:**
- `shared-docs/BLOCKERS.md` - Current blockers
- `shared-docs/INTEGRATION-STATUS.md` - Integration health
- `shared-docs/SPRINT-PROGRESS.md` - Sprint tracking
- `shared-docs/DECISIONS.md` - Technical decisions log

---

## 🚀 GETTING STARTED

### **First Time Setup:**

1. **Clone and organize:**
```bash
cd /Users/mauricio/Proyectos/appMultiplatform
git status  # Verify structure is correct
```

2. **Agent assignment:**
   - **Backend Agent:** `code skillswap-backend/`
   - **Frontend Agent:** `code skillswap-frontend/`
   - **Coordinator Agent:** `code shared-docs/`

3. **Read your PRD:**
   - Backend: `skillswap-backend/README.md`
   - Frontend: `skillswap-frontend/README.md`
   - Coordinator: All docs in `shared-docs/`

4. **Start first sprint:**
   - Review `shared-docs/PRD.md` for context
   - Check `shared-docs/API-Contract.md` for current state
   - Begin Sprint 1 tasks according to roadmap

### **Daily Workflow:**
```bash
# Morning: Check shared docs for updates
cd shared-docs && git pull

# Development: Work in your workspace
cd ../skillswap-[backend|frontend]

# Evening: Update progress and commit
git add . && git commit -m "Clear message about what was done"
git push

# Update your README.md with daily progress
```

---

*Guía creada para facilitar desarrollo paralelo multi-agente*
*Última actualización: 6 de septiembre de 2025*
