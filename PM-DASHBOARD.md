# 📋 PRODUCT MANAGER - COORDINATION DASHBOARD
**Last Updated:** 6 septiembre 2025 - 14:30  
**Role:** Multi-Agent Coordination & Sprint Management  
**Current Sprint:** Sprint 1 - Day 1

---

## 🎯 **QUICK AGENT STATUS**

### **🔧 Backend Agent**
- **Location:** `skillswap-backend/`
- **Current Task:** US-001 (Registration API - 5 pts)
- **Status:** 🟡 Ready to start
- **Last Seen:** Not started yet
- **Expected Progress:** Project setup today
- **How to activate:** Send agent to backend folder, say "revisa y continúa"

### **📱 Frontend Agent**  
- **Location:** `skillswap-frontend/`
- **Current Task:** US-002 (Registration Screen - 8 pts)
- **Status:** 🟡 Ready to start
- **Last Seen:** Not started yet  
- **Expected Progress:** React Native setup today
- **How to activate:** Send agent to frontend folder, say "revisa y continúa"

### **📚 Documentation Agent**
- **Location:** `shared-docs/`
- **Current Task:** API Contract updates & coordination
- **Status:** 🟡 Ready to support
- **Last Seen:** Not started yet
- **Expected Progress:** Monitor other agents, update docs
- **How to activate:** Send agent to shared-docs folder, say "revisa y continúa"

---

## 🚀 **ACTIVATION PROTOCOL**

### **Step 1: Start Backend Agent**
```bash
# Open new VS Code window:
code /Users/mauricio/Proyectos/appMultiplatform/skillswap-backend

# Message to agent:
"Revisa el archivo PENDING-TASKS.md y continúa con US-001 (Registration API). 
Todas las especificaciones están detalladas ahí."
```

### **Step 2: Start Frontend Agent (30 mins later)**
```bash
# Open new VS Code window:
code /Users/mauricio/Proyectos/appMultiplatform/skillswap-frontend

# Message to agent:
"Revisa el archivo PENDING-TASKS.md y continúa con US-002 (Registration Screen). 
Puedes empezar el setup mientras Backend desarrolla la API."
```

### **Step 3: Start Documentation Agent (when needed)**
```bash
# Open new VS Code window:
code /Users/mauricio/Proyectos/appMultiplatform/shared-docs

# Message to agent:
"Revisa PENDING-TASKS.md y continúa monitoreando el progreso de Backend/Frontend. 
Actualiza la documentación según avancen."
```

---

## 📊 **DAILY MONITORING CHECKLIST**

### **Morning Standup (Every Day):**
- [ ] Check git log for overnight commits
- [ ] Update this dashboard with latest status
- [ ] Check if any agent has blockers (@Product-Manager mentions)
- [ ] Update DAILY-COORDINATION.md with today's priorities

### **Midday Check (Every 3 hours):**
- [ ] Review commits from last 3 hours
- [ ] Update burndown progress in PROGRESS-TRACKING.md
- [ ] Check if dependencies are ready (US-001 → US-002)
- [ ] Respond to any @Product-Manager questions

### **Evening Review (End of day):**
- [ ] Update story points progress
- [ ] Move completed tasks to DONE in BACKLOG.md
- [ ] Prepare tomorrow's priorities in DAILY-COORDINATION.md
- [ ] Identify any risks or blockers for tomorrow

---

## 🔄 **COMMUNICATION PROTOCOLS**

### **How Agents Signal Completion:**
```bash
# Backend completes US-001:
feat(US-001): complete registration API with JWT auth ✅
📱 @Frontend-Agent: Registration API ready at /api/v1/auth/register
📋 @Product-Manager: US-001 ready for review

# Frontend needs clarification:
feat(US-002): WIP registration screen - QUESTION for @Product-Manager
❓ Should password confirmation be on same screen or separate?
❓ What should happen after successful registration?

# Your response:
docs(US-002): clarify registration flow requirements ✅
📋 Password confirmation: same screen (better UX)
📋 After registration: navigate to profile completion screen
🔄 Frontend can continue with current screen approach
```

### **How You Coordinate Dependencies:**
```bash
# When US-001 is done:
docs(US-001): approve registration API - ready for integration ✅
🔄 Moving US-001 to DONE in backlog
📱 @Frontend-Agent: US-002 can now integrate with real API
🔧 @Backend-Agent: US-007 (Login API) is now top priority

# When blocking issues arise:
docs(sprint): adjust priorities due to integration issues ✅
🚨 Backend API changes needed for mobile compatibility
🔄 Frontend: continue with mockups while Backend adjusts
📊 Sprint velocity may be impacted, monitoring closely
```

---

## 📈 **PROGRESS TRACKING SHORTCUTS**

### **Quick Status Update Commands:**
```bash
# Daily progress update:
git pull origin main
git log --oneline --since="24 hours ago" --grep="US-00"
# Count completed story points, update PROGRESS-TRACKING.md

# Check current burndown:
# Expected: 52 - (day_number * 4) story points remaining
# Day 1: 52 pts, Day 2: 48 pts, Day 3: 44 pts, etc.
```

### **Story Point Tracking:**
```markdown
# Update this daily in PROGRESS-TRACKING.md:
Day 1: 52 pts remaining (0 completed)
Day 2: __ pts remaining (__ completed)  
Day 3: __ pts remaining (__ completed)

# When US-001 completes: -5 pts
# When US-002 completes: -8 pts
# When US-007 completes: -3 pts
# etc.
```

---

## 🚨 **RISK MANAGEMENT**

### **Common Issues & Solutions:**

#### **🔴 Agent Gets Stuck:**
**Problem:** Agent commits "WIP" but no progress for 4+ hours
**Solution:** 
1. Check their PENDING-TASKS.md for clarity
2. Update with more specific next steps
3. Send message: "revisa y continúa - he actualizado tus tareas"

#### **🟡 Integration Problems:**
**Problem:** Backend/Frontend can't integrate properly
**Solution:**
1. Activate Documentation Agent to mediate
2. Update API-Contract.md with real specs
3. Create integration testing guide

#### **🟠 Scope Creep:**
**Problem:** Agent adds features not in acceptance criteria
**Solution:**
1. Redirect to original user story
2. Create new user story for additional features
3. Maintain Sprint 1 scope discipline

---

## 🎯 **SUCCESS METRICS TO TRACK**

### **Daily KPIs:**
- **Commits per agent:** Target 2-3 meaningful commits/day
- **Story points completed:** Target 4 pts/day total
- **Blockers resolved:** Target < 4 hours resolution time
- **Integration readiness:** Dependencies completed on time

### **Sprint 1 Success Criteria:**
- [ ] All 10 user stories completed (52 pts)
- [ ] End-to-end registration flow working
- [ ] Profile management functional
- [ ] Basic search and contact working
- [ ] No critical bugs in core flows

---

## 🔮 **NEXT ACTIONS**

### **Immediate (Next 30 minutes):**
1. **Activate Backend Agent** - US-001 is critical path
2. **Update daily coordination** - Set today's expectations
3. **Prepare monitoring routine** - Check commits every 2 hours

### **This Week:**
1. **Monitor US-001 → US-002 handoff** (Day 2-3)
2. **Coordinate first integration testing** (Day 3-4)  
3. **Prepare Sprint 2 planning** (Day 12-14)

---

## 📞 **QUICK REFERENCE**

### **File Locations:**
- **Backlog Management:** `/BACKLOG.md`
- **Progress Tracking:** `/PROGRESS-TRACKING.md`
- **Daily Coordination:** `/DAILY-COORDINATION.md`
- **Backend Tasks:** `/skillswap-backend/PENDING-TASKS.md`
- **Frontend Tasks:** `/skillswap-frontend/PENDING-TASKS.md`
- **Documentation Tasks:** `/shared-docs/PENDING-TASKS.md`

### **Git Commands:**
```bash
# Check recent activity:
git log --oneline --since="6 hours ago"

# Update and commit coordination changes:
git add PROGRESS-TRACKING.md DAILY-COORDINATION.md
git commit -m "docs(pm): update daily progress and coordination"
git push origin main
```

---

**🎯 READY TO COORDINATE! Your agents are prepared with detailed PENDING-TASKS.md files.**

**🚀 NEXT STEP:** Start Backend Agent first - open skillswap-backend/ folder and say "revisa y continúa"**
