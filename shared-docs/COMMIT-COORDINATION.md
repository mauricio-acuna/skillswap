# 🔄 Commit Coordination Protocol
## Sistema de Coordinación para Desarrollo Multi-Agente

**Objetivo:** Evitar conflictos entre agentes trabajando en paralelo  
**Última actualización:** 6 de septiembre de 2025

---

## 📋 COMMIT STANDARDS

### Formato de Commit Messages
```
[AGENT] [SCOPE]: Description

Examples:
[BACKEND] auth: Implement JWT token generation
[FRONTEND] ui: Add login screen with validation
[DOCS] coordination: Update sprint status tracking
```

### Commit Types por Agent
- **[BACKEND]**: Spring Boot, APIs, Database, Server logic
- **[FRONTEND]**: React Native, UI components, Mobile logic  
- **[DOCS]**: Documentation, coordination, specifications

---

## 🎯 COMMIT COORDINATION RULES

### Rule 1: Scope Separation
- **Backend Agent:** Only commits to `skillswap-backend/` directory
- **Frontend Agent:** Only commits to `skillswap-frontend/` directory
- **Docs Agent:** Only commits to `shared-docs/` directory and root coordination files

### Rule 2: Shared Files Protocol
**Files requiring coordination:**
- `shared-docs/API-Contract.md` - Backend updates implementation status
- `shared-docs/SPRINT-STATUS.md` - All agents update their progress
- `shared-docs/Database-Schema.md` - Backend updates schema changes
- `shared-docs/WebSocket-Events.md` - Backend updates event definitions

**Protocol:**
1. Pull latest changes before editing shared files
2. Add your changes without removing others' content
3. Use clear section headers for your agent
4. Commit immediately after updating shared files

### Rule 3: Integration Points
**When Backend changes API:**
1. Backend updates `shared-docs/API-Contract.md`
2. Backend commits with message: `[BACKEND] api: Update endpoint X status`
3. Frontend pulls and adapts integration layer

**When Frontend needs new API:**
1. Frontend updates `shared-docs/API-Contract.md` with request
2. Frontend commits with message: `[FRONTEND] api-request: Need endpoint for X`
3. Backend implements and updates status

---

## 📅 DAILY SYNC PROTOCOL

### Morning Routine (Each Agent)
```bash
# 1. Pull latest changes
git pull origin main

# 2. Check coordination files
cat shared-docs/SPRINT-STATUS.md
cat shared-docs/COMMIT-COORDINATION.md

# 3. Update your daily status
# Edit shared-docs/SPRINT-STATUS.md with your progress
```

### Evening Routine (Each Agent)
```bash
# 1. Commit your daily work
git add .
git commit -m "[YOUR_AGENT] scope: What you accomplished"

# 2. Update sprint status
# Edit shared-docs/SPRINT-STATUS.md

# 3. Push changes
git push origin main
```

---

## 🚨 CONFLICT RESOLUTION

### File Conflicts
1. **Shared documentation conflicts:** Coordinator Agent resolves
2. **Code conflicts:** Should not happen if scope separation is followed
3. **API contract conflicts:** Backend Agent has final say on implementation

### Branch Strategy
- **Main branch:** For stable, integrated code
- **Feature branches:** Optional for complex features
- **Daily integration:** All agents merge to main daily

### Escalation Path
1. **Self-resolve:** Check this coordination doc
2. **Agent discussion:** Update `shared-docs/BLOCKERS.md`
3. **Human intervention:** If agents cannot resolve

---

## 📊 COMMIT TRACKING

### Current Sprint 1 Commits

#### Backend Agent Commits
- 2025-09-06: `[BACKEND] setup: Initial Spring Boot project structure`
- 2025-09-06: `[BACKEND] docker: Add development docker configuration`
- _Next commits will be tracked here_

#### Frontend Agent Commits  
- 2025-09-06: `[FRONTEND] setup: Initial React Native TypeScript project`
- 2025-09-06: `[FRONTEND] nav: Basic navigation structure`
- _Next commits will be tracked here_

#### Docs Agent Commits
- 2025-09-06: `[DOCS] coordination: Establish sprint tracking system`
- 2025-09-06: `[DOCS] coordination: Create commit coordination protocol`
- _Next commits will be tracked here_

---

## 🔧 INTEGRATION CHECKPOINTS

### Daily Integration Check
- [ ] Backend APIs are accessible locally
- [ ] Frontend can connect to backend  
- [ ] No merge conflicts in shared files
- [ ] All agents have latest shared documentation

### Weekly Integration Test
- [ ] End-to-end flow works (register → login → profile)
- [ ] Error handling is consistent
- [ ] API contracts match implementation
- [ ] Performance meets acceptance criteria

---

## 📝 SHARED FILE SECTIONS

### API-Contract.md Sections
```markdown
## Backend Implementation Status
[Updated by Backend Agent]

## Frontend Integration Status  
[Updated by Frontend Agent]

## Testing Status
[Updated by responsible agent]
```

### SPRINT-STATUS.md Sections
```markdown
## Backend Agent Progress
[Daily updates by Backend Agent]

## Frontend Agent Progress
[Daily updates by Frontend Agent]  

## Coordination Progress
[Daily updates by Docs Agent]
```

---

## ⚡ QUICK REFERENCE

### Before Starting Work
```bash
git status
git pull origin main
```

### Before Committing
```bash
git status
# Check no shared files have conflicts
# Update SPRINT-STATUS.md with your progress
```

### Daily Standup Info
Each agent should update their section in `SPRINT-STATUS.md`:
- ✅ Completed yesterday
- 🔄 Working today  
- 🚫 Blockers
- 📝 Notes for other agents

---

*Protocol maintained by: Coordinator Agent*  
*Review frequency: Weekly or when conflicts occur*  
*Next review: End of Sprint 1*
