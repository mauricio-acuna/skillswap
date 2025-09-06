# 🔄 TASK COORDINATION SYSTEM
## Sistema de Coordinación Multi-Agente

### 📋 **Cómo Funciona el Flujo**

#### **1. Product Manager (Este IDE)**
```bash
# Workspace: /Users/mauricio/Proyectos/appMultiplatform/
# Archivos principales:
- BACKLOG.md          # 📋 User stories y tareas
- TASK-ASSIGNMENTS.md # 📊 Asignaciones actuales  
- PROGRESS-TRACKING.md # 📈 Seguimiento progreso
```

#### **2. Backend Agent** 
```bash
# Workspace: skillswap-backend/
# Al abrir el workspace, revisar:
- ../BACKLOG.md (filtrar por "Backend Agent")
- Tomar tareas en estado "TODO" con prioridad ALTA
- Implementar según acceptance criteria
- Commit con formato: feat(US-XXX): descripción
```

#### **3. Frontend Agent**
```bash  
# Workspace: skillswap-frontend/
# Al abrir el workspace, revisar:
- ../BACKLOG.md (filtrar por "Frontend Agent") 
- Verificar dependencias Backend completadas
- Implementar según acceptance criteria
- Commit con formato: feat(US-XXX): descripción
```

#### **4. Coordinator Agent**
```bash
# Workspace: shared-docs/
# Responsabilidades:
- Actualizar documentación técnica
- Mantener API contracts actualizados
- Resolver conflicts entre agentes
```

---

## 🔄 **Protocolo de Trabajo Diario**

### **Inicio de Sesión (Cada Agente)**
1. `git pull origin main` - Obtener últimos cambios
2. Revisar `BACKLOG.md` para tareas asignadas
3. Verificar `TASK-ASSIGNMENTS.md` para dependencias
4. Seleccionar tarea con mayor prioridad disponible

### **Durante Desarrollo**
1. Crear branch: `git checkout -b feature/US-XXX-descripcion`
2. Implementar según acceptance criteria
3. Hacer commits frecuentes con formato estándar
4. Actualizar progreso en `PROGRESS-TRACKING.md`

### **Al Completar Tarea**
1. `git push origin feature/US-XXX-descripcion`
2. Crear PR hacia main
3. Notificar al PM (comentario en commit)
4. Mover tarea a "DONE" en BACKLOG.md

---

## 📊 **Estados de Tareas**

| Estado | Emoji | Significado |
|--------|-------|-------------|
| **TODO** | 🟡 | Lista para desarrollar |
| **IN PROGRESS** | 🔵 | En desarrollo activo |
| **REVIEW** | 🟠 | Esperando review del PM |
| **BLOCKED** | 🔴 | Bloqueada por dependencia |
| **DONE** | ✅ | Completada y aprobada |

---

## 🎯 **Asignaciones Actuales (Sprint 1)**

### **Backend Agent - Tareas Disponibles:**
- [ ] 🔴 **US-001** (5 pts) - API Registro Usuario  
- [ ] 🟠 **US-003** (3 pts) - API Perfil Usuario
- [ ] 🟠 **US-005** (7 pts) - API Búsqueda

### **Frontend Agent - Tareas Disponibles:**
- [ ] 🔴 **US-002** (8 pts) - Pantalla Registro
- [ ] 🟠 **US-004** (5 pts) - Pantalla Perfil  
- [ ] 🟠 **US-006** (6 pts) - Pantalla Búsqueda

### **Dependencias:**
- US-002 requiere US-001 (API registro)
- US-004 requiere US-003 (API perfil)
- US-006 requiere US-005 (API búsqueda)

---

## 📢 **Comunicación Entre Agentes**

### **Formato de Commits**
```bash
feat(US-001): add user registration endpoint with JWT auth
fix(US-002): resolve form validation for email field
docs(US-003): update API documentation for profile endpoints
test(US-001): add integration tests for auth flow
```

### **Notificaciones de Progreso**
```bash
# Al completar una tarea:
git commit -m "feat(US-001): complete user registration API ✅

✅ Endpoint POST /api/auth/register implemented
✅ JWT token generation working  
✅ Password hashing with BCrypt
✅ Email uniqueness validation
✅ Unit tests added (95% coverage)

📱 @Frontend-Agent: US-002 can now start development
📋 @Product-Manager: Ready for review"
```

### **Solicitud de Clarificaciones**
```bash
# Si necesitas clarificación:
git commit -m "feat(US-001): WIP user registration - NEED CLARIFICATION

❓ Question for @Product-Manager:
- Should password have minimum length requirement?
- Do we need email verification for MVP?
- JWT expiration time preference?

🔄 Will continue with current assumptions until clarified"
```

---

## 🚀 **Quick Start para Otros Agentes**

### **Backend Agent - Empezar Ahora:**
```bash
cd /Users/mauricio/Proyectos/appMultiplatform/skillswap-backend
git pull origin main
# Revisar BACKLOG.md - tomar US-001 (registro API)
# Implementar según acceptance criteria
```

### **Frontend Agent - Empezar Ahora:**
```bash  
cd /Users/mauricio/Proyectos/appMultiplatform/skillswap-frontend
git pull origin main
# Revisar BACKLOG.md - preparar US-002 (mientras Backend hace US-001)
# Crear mockups y estructura de componentes
```

**¡El sistema está listo para coordinación multi-agente! 🎯**
