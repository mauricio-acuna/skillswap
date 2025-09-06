# 🚀 SkillSwap - Instrucciones para VS Code Multi-Agente
## Configuración de Editores por Agente

### 📋 REPOSITORIO CONFIGURADO

✅ **Repositorio:** https://github.com/mauricio-acuna/skillswap.git
✅ **Branch principal:** main
✅ **Primer commit:** Documentación completa y estructura del proyecto

---

## 📁 **ESTRUCTURA FINAL DEL PROYECTO**

```
/Users/mauricio/Proyectos/appMultiplatform/
├── 📚 shared-docs/              # 📖 COORDINADOR AGENT
│   ├── PRD.md                   # PRD general del proyecto
│   ├── MercadoObjetivo.md       # Análisis de mercado
│   ├── SkillSwap-TechnicalSpecs.md # Especificaciones técnicas
│   ├── API-Contract.md          # Contrato Backend ↔ Frontend
│   ├── Multi-Agent-Coordination.md # Guía de coordinación
│   └── README.md                # Documentación compartida
├── 🔧 skillswap-backend/        # 🔧 BACKEND AGENT
│   └── README.md                # PRD específico backend
├── 📱 skillswap-frontend/       # 📱 FRONTEND AGENT
│   └── README.md                # PRD específico frontend
├── .gitignore                   # Configuración Git
└── README.md                    # README principal
```

---

## 🎯 **CONFIGURACIÓN DE VS CODE POR AGENTE**

### **🔧 BACKEND AGENT**

#### **Folder a abrir en VS Code:**
```bash
/Users/mauricio/Proyectos/appMultiplatform/skillswap-backend
```

#### **Comando para abrir:**
```bash
code /Users/mauricio/Proyectos/appMultiplatform/skillswap-backend
```

#### **Workspace focus:**
- ✅ **Responsabilidad:** Spring Boot API + Database + Security
- ✅ **Archivos principales:** 
  - `README.md` (PRD específico backend)
  - `../shared-docs/API-Contract.md` (APIs a implementar)
  - `../shared-docs/Database-Schema.md` (cuando se cree)
- ✅ **Tecnologías:** Java 17, Spring Boot 3.1+, PostgreSQL, Redis

#### **Primera tarea:**
1. Leer `README.md` completo
2. Revisar `../shared-docs/API-Contract.md`
3. Setup inicial Spring Boot con Maven
4. Implementar endpoints de authentication

---

### **📱 FRONTEND AGENT**

#### **Folder a abrir en VS Code:**
```bash
/Users/mauricio/Proyectos/appMultiplatform/skillswap-frontend
```

#### **Comando para abrir:**
```bash
code /Users/mauricio/Proyectos/appMultiplatform/skillswap-frontend
```

#### **Workspace focus:**
- ✅ **Responsabilidad:** React Native App + UI/UX + Mobile features
- ✅ **Archivos principales:**
  - `README.md` (PRD específico frontend)
  - `../shared-docs/API-Contract.md` (APIs disponibles)
  - `../shared-docs/SkillSwap-TechnicalSpecs.md` (device compatibility)
- ✅ **Tecnologías:** React Native 0.72+, TypeScript, Redux Toolkit

#### **Primera tarea:**
1. Leer `README.md` completo
2. Revisar `../shared-docs/API-Contract.md`
3. Setup inicial React Native con TypeScript
4. Implementar navegación y pantallas de auth

---

### **📚 COORDINADOR AGENT**

#### **Folder a abrir en VS Code:**
```bash
/Users/mauricio/Proyectos/appMultiplatform/shared-docs
```

#### **Comando para abrir:**
```bash
code /Users/mauricio/Proyectos/appMultiplatform/shared-docs
```

#### **Workspace focus:**
- ✅ **Responsabilidad:** Documentación + Coordinación + API contracts
- ✅ **Archivos principales:**
  - `Multi-Agent-Coordination.md` (guía de coordinación)
  - `API-Contract.md` (mantener actualizado)
  - `PRD.md` (documentación general)
- ✅ **Rol:** Facilitar comunicación, resolver conflicts, mantener docs

#### **Primera tarea:**
1. Leer `Multi-Agent-Coordination.md`
2. Monitorear progreso de otros agentes
3. Mantener `API-Contract.md` actualizado
4. Facilitar resolución de blockers

---

## 🔄 **PROTOCOLO DE TRABAJO DIARIO**

### **Cada Agente debe:**

#### **🌅 Al comenzar el día:**
```bash
cd /Users/mauricio/Proyectos/appMultiplatform
git pull origin main  # Obtener últimos cambios
```

#### **💻 Durante el desarrollo:**
- Trabajar en su workspace asignado
- Hacer commits frecuentes con mensajes claros
- Actualizar su `README.md` con progreso diario

#### **🌙 Al finalizar el día:**
```bash
# En tu workspace específico
git add .
git commit -m "Clear description of what was done"
git push origin main
```

#### **📝 Comunicación:**
- **Backend changes:** Actualizar `shared-docs/API-Contract.md`
- **Frontend needs:** Documentar en `shared-docs/API-Contract.md`
- **Blockers:** Documentar en tu `README.md`

---

## 🚀 **COMANDOS DE SETUP RÁPIDO**

### **Para clonar en una nueva máquina:**
```bash
# Clonar repositorio
git clone https://github.com/mauricio-acuna/skillswap.git
cd skillswap

# Abrir editores según rol
code skillswap-backend/    # Backend Agent
code skillswap-frontend/   # Frontend Agent  
code shared-docs/          # Coordinador Agent
```

### **Para verificar estructura:**
```bash
cd /Users/mauricio/Proyectos/appMultiplatform
tree -L 2
# Debería mostrar la estructura con las 3 carpetas principales
```

---

## 🎯 **OBJETIVOS SPRINT 1 (2 semanas)**

### **🔧 Backend Agent (skillswap-backend/):**
- [ ] Setup Spring Boot 3.1+ con Maven
- [ ] Configuración H2 (dev) + PostgreSQL (prod)
- [ ] Implementar User entity y repository
- [ ] Endpoints: POST /auth/register, POST /auth/login
- [ ] JWT authentication setup
- [ ] Basic security configuration

### **📱 Frontend Agent (skillswap-frontend/):**
- [ ] Setup React Native 0.72+ con TypeScript
- [ ] Configuración de navegación (Stack + Tab)
- [ ] Pantallas: Welcome, Login, Register
- [ ] Setup Redux Toolkit + RTK Query
- [ ] Integración con API de authentication
- [ ] Configuración básica iOS + Android

### **📚 Coordinador Agent (shared-docs/):**
- [ ] Mantener API-Contract.md actualizado
- [ ] Crear Database-Schema.md
- [ ] Monitorear integración Backend ↔ Frontend
- [ ] Documentar decisiones técnicas
- [ ] Facilitar resolución de blockers
- [ ] Preparar Sprint 2 planning

---

## 📊 **MÉTRICAS DE ÉXITO SPRINT 1**

### **Integration Success:**
- ✅ Frontend puede hacer login/register contra Backend
- ✅ JWT tokens funcionan correctamente
- ✅ No hay errores de CORS
- ✅ Ambas apps corren sin errores

### **Code Quality:**
- ✅ Commits con mensajes claros
- ✅ README.md actualizado diariamente
- ✅ No código hardcodeado
- ✅ Estructura de carpetas consistente

### **Documentation:**
- ✅ API-Contract.md actualizado
- ✅ Setup instructions funcionan
- ✅ Blockers documentados y resueltos
- ✅ Sprint 2 planning preparado

---

## 🔗 **LINKS IMPORTANTES**

### **Repositorio:**
- 🔗 **GitHub:** https://github.com/mauricio-acuna/skillswap.git
- 🔗 **Clone:** `git clone https://github.com/mauricio-acuna/skillswap.git`

### **Documentación:**
- 📖 **PRD General:** `shared-docs/PRD.md`
- 📖 **API Contract:** `shared-docs/API-Contract.md`
- 📖 **Coordinación:** `shared-docs/Multi-Agent-Coordination.md`
- 📖 **Backend PRD:** `skillswap-backend/README.md`
- 📖 **Frontend PRD:** `skillswap-frontend/README.md`

### **Workspace Commands:**
```bash
# Backend Agent
code /Users/mauricio/Proyectos/appMultiplatform/skillswap-backend

# Frontend Agent  
code /Users/mauricio/Proyectos/appMultiplatform/skillswap-frontend

# Coordinador Agent
code /Users/mauricio/Proyectos/appMultiplatform/shared-docs
```

---

## 🎉 **¡READY TO START!**

### **Status actual:**
✅ **Repositorio configurado:** https://github.com/mauricio-acuna/skillswap.git
✅ **Documentación completa:** PRD, API contracts, coordinación
✅ **Estructura optimizada:** 3 workspaces independientes
✅ **Sprint 1 definido:** Objetivos claros para cada agente

### **Próximos pasos:**
1. **Cada agente abre su workspace** en VS Code
2. **Lee su README.md específico** para entender responsabilidades
3. **Comienza desarrollo Sprint 1** según su roadmap
4. **Coordina através de shared-docs/** para mantener sincronización

**¡Let's build SkillSwap! 🚀**

---

*Configuración completada: 6 de septiembre de 2025*
*Repository: https://github.com/mauricio-acuna/skillswap.git*
*Sprint 1 starts: NOW! 🎯*
