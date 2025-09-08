# 🎯 COORDINATION INSTRUCTIONS FOR AGENTS
**Updated:** 8 septiembre 2025  
**Purpose:** Clear instructions for agent coordination and progress tracking

---

## 📋 **MANDATORY AGENT WORKFLOW**

### **🔄 WHEN YOU START WORKING:**
1. **Read this file first** - check for updates from PM
2. **Update your HOW-TO-CONTINUE.md** - fill "Current Session Update" section
3. **Update AGENTS-PROGRESS-TRACKING.md** - mark tasks as "IN PROGRESS"
4. **Check for blockers** - see if other agents completed dependencies

### **🔄 DURING YOUR WORK:**
1. **Mark progress** - update percentage completion in tracking file
2. **Report blockers immediately** - if you can't proceed, update status
3. **Document new files/features** - add to your HOW-TO-CONTINUE.md

### **✅ WHEN YOU COMPLETE TASKS:**
1. **Mark as COMPLETED** - with date in AGENTS-PROGRESS-TRACKING.md
2. **Update next priorities** - what should be done next
3. **Test your work** - ensure it actually works before marking complete
4. **Document for next agent** - explain what you did and how to continue

---

## 🚨 **CRITICAL COORDINATION POINTS**

### **Backend ↔ Frontend Dependencies:**
- **Backend MUST complete MatchingService.java** before Frontend can integrate APIs
- **Frontend needs API endpoints** documented to create proper service calls
- **Both need to agree on data structures** (DTOs, request/response formats)

### **All Agents ↔ Documentation:**
- **Documentation agent tracks all progress** for funding proposals
- **Technical achievements** should be reported to Documentation agent
- **Market-ready features** help with investor materials

---

## 📊 **PROGRESS REPORTING FORMAT**

### **Use this exact format in AGENTS-PROGRESS-TRACKING.md:**

```markdown
## [AGENT TYPE] UPDATE - [DATE]
**Completed Today:**
- ✅ [Specific task] - [What exactly was accomplished]

**In Progress:**
- 🔄 [Task name] - [X%] - [Expected completion: X days]

**Next Session Focus:**
- [Exactly what you'll work on next time]

**Blockers/Dependencies:**
- [What's stopping you or what you need from other agents]

**Files Modified/Created:**
- [List of specific files you changed]
```

### **Example Update:**
```markdown
## BACKEND UPDATE - 8 septiembre 2025
**Completed Today:**
- ✅ MatchingService.java - Implemented findMatchCandidates() method with algorithm

**In Progress:**
- 🔄 NotificationService.java - 60% - Expected completion: 2 days

**Next Session Focus:**
- Complete notification endpoints and WebSocket integration

**Blockers/Dependencies:**
- None currently

**Files Modified/Created:**
- src/main/java/com/skillswap/backend/service/MatchingService.java
- src/test/java/com/skillswap/backend/service/MatchingServiceTest.java
```

---

## 🎯 **CURRENT SPRINT PRIORITIES**

### **🔥 Week 1 Focus (Sept 8-15):**
1. **Backend**: Complete MatchingService.java (CRITICAL BLOCKER)
2. **Frontend**: Complete Redux API integration 
3. **Documentation**: Develop funding business case

### **⚡ Week 2 Focus (Sept 16-22):**
1. **Backend**: NotificationService + VideoSession features
2. **Frontend**: Main app screens (Dashboard, Skills, Matching)
3. **Documentation**: Complete investor pitch deck

### **🚀 Week 3 Focus (Sept 23-29):**
1. **Backend**: Enterprise monitoring + performance optimization
2. **Frontend**: Advanced UI components + testing
3. **Documentation**: Finalize complete funding package

---

## 📞 **COORDINATION PROTOCOL**

### **Daily Check-ins:**
- Each agent updates their progress in tracking file
- PM reviews all progress and identifies blockers
- Agents check for new priorities or instructions

### **Weekly Planning:**
- PM updates sprint priorities based on progress
- Agents estimate completion times for upcoming tasks
- Dependencies and blockers are resolved

### **Emergency Coordination:**
- If CRITICAL blocker encountered, update status immediately
- PM will coordinate solution with other agents
- Don't wait for next session if you're completely blocked

---

## 🏁 **SUCCESS METRICS**

### **MVP Ready Criteria:**
- ✅ Users can register/login (Backend + Frontend)
- ✅ Users can find skill matches (Backend MatchingService + Frontend UI)
- ✅ Users can communicate (Backend notifications + Frontend messaging)
- ✅ Basic video sessions work (Backend + Frontend integration)

### **Beta Ready Criteria:**
- ✅ All MVP features + testing
- ✅ Performance optimization
- ✅ Security hardening
- ✅ User documentation complete

### **Launch Ready Criteria:**
- ✅ All Beta features + monitoring
- ✅ Funding package complete
- ✅ Marketing materials ready
- ✅ App store submissions prepared

---

**REMEMBER:** The goal is MVP in 2-3 weeks. Every task should contribute to this timeline. If something doesn't help reach MVP, it can wait.
