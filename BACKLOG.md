# 📋 SKILLSWAP - Product Backlog

## 🎯 Sprint 1 - MVP Core Features
**Sprint Goal:** Desarrollar las funcionalidades básicas para registro, perfil y matching inicial

### 📊 **Sprint Stats**
- **Duración:** 2 semanas  
- **Story Points Total:** 52 pts
- **Capacidad Backend:** 23 pts (US-001, US-003, US-005, US-007, US-009)
- **Capacidad Frontend:** 29 pts (US-002, US-004, US-006, US-008, US-010)

---

## 🔥 **READY FOR DEVELOPMENT**

### 🔐 **Epic: Autenticación y Registro**

#### 🆔 **US-001: Registro de Usuario** 
- **Asignado a:** `Backend Agent`
- **Story Points:** 5
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🔴 **ALTA**

**Como** nuevo usuario  
**Quiero** registrarme en la aplicación  
**Para** poder acceder a las funcionalidades de intercambio de habilidades

**Acceptance Criteria:**
- [ ] API POST /api/auth/register acepta email, password, nombre, apellido
- [ ] Validación de email único en base de datos
- [ ] Password hash con BCrypt
- [ ] Respuesta con JWT token
- [ ] Validación de campos obligatorios
- [ ] Tests unitarios de endpoints

**Technical Notes:**
- Usar Spring Security
- JWT con expiración 24h
- H2 database para desarrollo

---

#### 📱 **US-002: Pantalla de Registro Mobile**
- **Asignado a:** `Frontend Agent`
- **Story Points:** 8
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🔴 **ALTA**

**Como** nuevo usuario  
**Quiero** una interfaz intuitiva para registrarme  
**Para** crear mi cuenta fácilmente

**Acceptance Criteria:**
- [ ] Pantalla con formulario: email, password, confirmar password, nombre, apellido
- [ ] Validación en tiempo real de campos
- [ ] Loading state durante registro
- [ ] Manejo de errores del API
- [ ] Navegación a pantalla principal tras registro exitoso
- [ ] Diseño responsive para iOS y Android

**Technical Notes:**
- React Hook Form para validaciones
- Redux para manejo de estado auth
- Integrar con US-001 API

---

### 👤 **Epic: Gestión de Perfil**

#### 🛠️ **US-003: API de Perfil de Usuario**
- **Asignado a:** `Backend Agent`
- **Story Points:** 3
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🟠 **MEDIA**

**Como** usuario registrado  
**Quiero** que el sistema guarde mi información de perfil  
**Para** poder mostrar mis habilidades a otros usuarios

**Acceptance Criteria:**
- [ ] Endpoint GET /api/users/profile retorna perfil completo
- [ ] Endpoint PUT /api/users/profile actualiza perfil
- [ ] Campos: bio, ubicación, habilidades, intereses
- [ ] Validación de datos de entrada
- [ ] Tests de integración

---

#### 📱 **US-004: Pantalla de Perfil Usuario**
- **Asignado a:** `Frontend Agent`
- **Story Points:** 5
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🟠 **MEDIA**

**Como** usuario  
**Quiero** completar y editar mi perfil  
**Para** presentarme mejor a otros usuarios

**Acceptance Criteria:**
- [ ] Formulario de perfil con todos los campos
- [ ] Subida de foto de perfil
- [ ] Lista de habilidades con tags
- [ ] Selector de ubicación
- [ ] Preview del perfil como lo ven otros

---

### 🔍 **Epic: Búsqueda y Matching**

#### 🔍 **US-005: API de Búsqueda de Usuarios**
- **Asignado a:** `Backend Agent`  
- **Story Points:** 7
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🟠 **MEDIA**

**Como** usuario  
**Quiero** buscar otros usuarios por habilidades  
**Para** encontrar personas con quien intercambiar conocimientos

**Acceptance Criteria:**
- [ ] Endpoint GET /api/users/search con filtros
- [ ] Filtros por: habilidad, ubicación, disponibilidad
- [ ] Paginación de resultados
- [ ] Algoritmo de scoring por relevancia
- [ ] Exclusión de usuarios ya contactados

---

#### 📱 **US-006: Pantalla de Búsqueda**
- **Asignado a:** `Frontend Agent`
- **Story Points:** 6
- **Estado:** 🟡 **TODO**  
- **Prioridad:** 🟠 **MEDIA**

**Como** usuario  
**Quiero** una interfaz para buscar otros usuarios  
**Para** encontrar intercambios relevantes

**Acceptance Criteria:**
- [ ] Barra de búsqueda por habilidades
- [ ] Filtros avanzados (ubicación, disponibilidad)
- [ ] Lista de resultados con cards de usuario
- [ ] Infinite scroll para cargar más resultados
- [ ] Botón "Contactar" en cada card

---

### � **Epic: Login y Autenticación Completa**

#### 🔑 **US-007: API de Login**
- **Asignado a:** `Backend Agent`
- **Story Points:** 3
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🔴 **ALTA**

**Como** usuario registrado  
**Quiero** hacer login con mis credenciales  
**Para** acceder a mi cuenta existente

**Acceptance Criteria:**
- [ ] Endpoint POST /api/auth/login acepta email y password
- [ ] Validación de credenciales contra base de datos
- [ ] Generación de JWT token válido
- [ ] Manejo de credenciales incorrectas
- [ ] Rate limiting para prevenir ataques de fuerza bruta
- [ ] Tests de seguridad y edge cases

**Technical Notes:**
- Usar misma estrategia JWT que US-001
- Implementar intentos fallidos (max 5 por hora)

---

#### 📱 **US-008: Pantalla de Login**
- **Asignado a:** `Frontend Agent`
- **Story Points:** 4
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🔴 **ALTA**

**Como** usuario registrado  
**Quiero** una pantalla de login intuitiva  
**Para** acceder rápidamente a mi cuenta

**Acceptance Criteria:**
- [ ] Formulario con email y password
- [ ] Validación de campos en tiempo real
- [ ] Loading state durante autenticación
- [ ] Manejo de errores (credenciales incorrectas, network)
- [ ] Opción "Recordar sesión"
- [ ] Link a "¿Olvidaste tu contraseña?"
- [ ] Navegación a registro si no tiene cuenta

---

### 💬 **Epic: Sistema de Contacto Básico**

#### 📧 **US-009: API de Solicitudes de Contacto**
- **Asignado a:** `Backend Agent`
- **Story Points:** 5
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🟠 **MEDIA**

**Como** usuario  
**Quiero** enviar solicitudes de contacto a otros usuarios  
**Para** iniciar un intercambio de habilidades

**Acceptance Criteria:**
- [ ] Endpoint POST /api/contact-requests crear solicitud
- [ ] Endpoint GET /api/contact-requests/received obtener recibidas
- [ ] Endpoint GET /api/contact-requests/sent obtener enviadas
- [ ] Endpoint PUT /api/contact-requests/{id}/accept aceptar solicitud
- [ ] Endpoint PUT /api/contact-requests/{id}/decline rechazar solicitud
- [ ] Estados: pending, accepted, declined
- [ ] Prevenir solicitudes duplicadas

---

#### 📱 **US-010: Pantalla de Solicitudes**
- **Asignado a:** `Frontend Agent`
- **Story Points:** 6
- **Estado:** 🟡 **TODO**
- **Prioridad:** 🟠 **MEDIA**

**Como** usuario  
**Quiero** gestionar mis solicitudes de contacto  
**Para** organizar mis intercambios potenciales

**Acceptance Criteria:**
- [ ] Tab "Recibidas" con solicitudes pendientes
- [ ] Tab "Enviadas" con mis solicitudes
- [ ] Botones Aceptar/Rechazar en solicitudes recibidas
- [ ] Estados visuales (pendiente, aceptada, rechazada)
- [ ] Pull-to-refresh para actualizar
- [ ] Badge con contador de solicitudes pendientes

---

## �🔄 **IN PROGRESS**
*Tareas que están siendo desarrolladas actualmente*

## ✅ **DONE**
*Tareas completadas y aprobadas*

---

## 📝 **BACKLOG (Sprint 2 - Comunicación Avanzada)**

### 💬 **Epic: Sistema de Mensajería**

#### **US-011: Chat API (Backend)**
- **Story Points:** 8
- **Prioridad:** 🔴 ALTA
- **Features:** WebSocket real-time, persistencia mensajes, encriptación

#### **US-012: Pantalla de Chat (Frontend)**  
- **Story Points:** 10
- **Prioridad:** 🔴 ALTA
- **Features:** UI chat, typing indicators, mensaje status

#### **US-013: Notificaciones Push**
- **Story Points:** 6
- **Prioridad:** 🟠 MEDIA
- **Features:** FCM integration, mensaje notifications

---

## 📝 **BACKLOG (Sprint 3 - Intercambios)**

### 📅 **Epic: Sistema de Intercambios**

#### **US-014: API de Intercambios**
- **Story Points:** 12
- **Features:** Crear intercambio, gestionar sesiones, sistema de créditos

#### **US-015: Calendario de Intercambios**
- **Story Points:** 8  
- **Features:** Calendar view, booking system, disponibilidad

#### **US-016: Sistema de Créditos**
- **Story Points:** 10
- **Features:** Wallet virtual, transacciones, historial

---

## 📝 **BACKLOG (Sprint 4 - Gamificación)**

### 🏆 **Epic: Gamificación y Reconocimiento**

#### **US-017: Sistema de Puntos y Badges**
- **Story Points:** 6
- **Features:** XP system, achievement badges, niveles

#### **US-018: Rankings y Leaderboards**
- **Story Points:** 4
- **Features:** Top teachers, best students, categorías

#### **US-019: Reviews y Ratings**
- **Story Points:** 8
- **Features:** Star rating, comentarios, moderación

---

## 🔧 **NOTAS TÉCNICAS**

### **Convenciones de Commits**
```
feat(US-001): add user registration endpoint
fix(US-002): resolve form validation issue  
docs(US-003): update API documentation
test(US-001): add unit tests for auth
```

### **Definition of Done**
- [ ] Código implementado según acceptance criteria
- [ ] Tests unitarios/integración pasando
- [ ] Documentación actualizada
- [ ] Code review aprobado por PM
- [ ] Deploy en ambiente de desarrollo

### **Comunicación Entre Agentes**
1. **Backend completa US → Commit con tag US-XXX**
2. **Frontend puede empezar integración**
3. **PM revisa y mueve a DONE**
4. **Siguiente tarea disponible**
