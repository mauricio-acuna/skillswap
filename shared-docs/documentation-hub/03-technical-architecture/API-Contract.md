# 📡 API Contract - SkillSwap
## Backend ↔ Frontend Integration Specifications

### 🌐 BASE CONFIGURATION

**Base URLs:**
- **Development:** `http://localhost:8080/api/v1`
- **Production:** `https://api.skillswap.com/api/v1`

**Authentication:**
- **Header:** `Authorization: Bearer {jwt_token}`
- **Token Expiry:** 15 minutes
- **Refresh Token Expiry:** 7 days

**Response Format:**
```json
{
  "success": true,
  "data": { /* response data */ },
  "message": "Success message",
  "timestamp": "2025-09-06T10:30:00Z"
}
```

**Error Format:**
```json
{
  "success": false,
  "error": {
    "code": "USER_NOT_FOUND",
    "message": "User with email not found",
    "details": { /* additional error info */ }
  },
  "timestamp": "2025-09-06T10:30:00Z"
}
```

---

## 🔐 AUTHENTICATION ENDPOINTS

### POST /auth/register
**Status:** ✅ Ready for Implementation

**Request:**
```json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "firstName": "John",
  "lastName": "Doe",
  "language": "en",
  "gdprConsent": true,
  "marketingConsent": false
}
```

**Response 201:**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": 123,
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "profileImageUrl": null,
      "emailVerified": false,
      "role": "USER",
      "createdAt": "2025-09-06T10:30:00Z"
    },
    "tokens": {
      "accessToken": "eyJhbGciOiJIUzI1NiIs...",
      "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
      "expiresIn": 900
    }
  }
}
```

**Error 400:**
```json
{
  "success": false,
  "error": {
    "code": "EMAIL_ALREADY_EXISTS",
    "message": "User with this email already exists"
  }
}
```

### POST /auth/login
**Status:** ✅ Ready for Implementation

**Request:**
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response 200:** (Same as register response)

**Error 401:**
```json
{
  "success": false,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "Invalid email or password"
  }
}
```

### POST /auth/refresh
**Status:** 🔄 Sprint 2

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response 200:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 900
  }
}
```

### POST /auth/logout
**Status:** 🔄 Sprint 2

**Request:** (Empty body, requires Authorization header)

**Response 200:**
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

---

## 👤 USER MANAGEMENT ENDPOINTS

### GET /users/profile
**Status:** 🔄 Sprint 1

**Headers:** `Authorization: Bearer {token}`

**Response 200:**
```json
{
  "success": true,
  "data": {
    "id": 123,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "bio": "Passionate developer and language learner",
    "profileImageUrl": "https://cdn.skillswap.com/profiles/123.jpg",
    "location": "Madrid, Spain",
    "timezone": "Europe/Madrid",
    "languagePreference": "es",
    "emailVerified": true,
    "role": "USER",
    "createdAt": "2025-09-06T10:30:00Z",
    "skills": [
      {
        "id": 456,
        "name": "JavaScript",
        "category": "Programming",
        "canTeach": true,
        "wantsToLearn": false,
        "level": "ADVANCED",
        "verified": true
      }
    ],
    "creditBalance": 5,
    "completedSessions": 12,
    "rating": 4.8
  }
}
```

### PUT /users/profile
**Status:** ❌ Sprint 2

**Request:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "bio": "Updated bio",
  "location": "Barcelona, Spain",
  "timezone": "Europe/Madrid",
  "languagePreference": "en"
}
```

### DELETE /users/profile
**Status:** ❌ Sprint 3 (GDPR)

**Request:** (Empty body, requires password confirmation)

**Response 200:**
```json
{
  "success": true,
  "message": "Account deleted successfully"
}
```

---

## 🎯 SKILLS MANAGEMENT ENDPOINTS

### GET /skills
**Status:** 🔄 Sprint 1

**Query Parameters:**
- `category` (optional): Filter by category
- `search` (optional): Search by name
- `limit` (default: 50): Results per page
- `offset` (default: 0): Pagination offset

**Response 200:**
```json
{
  "success": true,
  "data": {
    "skills": [
      {
        "id": 1,
        "name": "JavaScript",
        "category": "Programming",
        "subcategory": "Web Development",
        "description": "Modern JavaScript programming language",
        "difficultyLevel": "INTERMEDIATE"
      },
      {
        "id": 2,
        "name": "Spanish",
        "category": "Languages",
        "subcategory": "Romance Languages",
        "description": "Spanish language conversation and grammar",
        "difficultyLevel": "BEGINNER"
      }
    ],
    "total": 150,
    "hasMore": true
  }
}
```

### GET /skills/categories
**Status:** 🔄 Sprint 1

**Response 200:**
```json
{
  "success": true,
  "data": {
    "categories": [
      {
        "name": "Programming",
        "subcategories": ["Web Development", "Mobile Development", "Data Science"],
        "skillCount": 45
      },
      {
        "name": "Languages",
        "subcategories": ["Romance Languages", "Germanic Languages", "Asian Languages"],
        "skillCount": 32
      },
      {
        "name": "Music",
        "subcategories": ["Instruments", "Theory", "Production"],
        "skillCount": 28
      }
    ]
  }
}
```

### POST /skills/user-skills
**Status:** ❌ Sprint 2

**Request:**
```json
{
  "skillId": 1,
  "canTeach": true,
  "wantsToLearn": false,
  "level": "ADVANCED",
  "yearsExperience": 5
}
```

**Response 201:**
```json
{
  "success": true,
  "data": {
    "userSkill": {
      "id": 789,
      "skillId": 1,
      "skillName": "JavaScript",
      "canTeach": true,
      "wantsToLearn": false,
      "level": "ADVANCED",
      "yearsExperience": 5,
      "verified": false,
      "createdAt": "2025-09-06T10:30:00Z"
    }
  }
}
```

### GET /skills/user-skills
**Status:** ❌ Sprint 2

**Response 200:**
```json
{
  "success": true,
  "data": {
    "userSkills": [
      {
        "id": 789,
        "skill": {
          "id": 1,
          "name": "JavaScript",
          "category": "Programming"
        },
        "canTeach": true,
        "wantsToLearn": false,
        "level": "ADVANCED",
        "verified": true
      }
    ]
  }
}
```

---

## 🤝 MATCHING SYSTEM ENDPOINTS

### GET /matches/suggestions
**Status:** ❌ Sprint 3

**Query Parameters:**
- `skillId` (optional): Filter by specific skill
- `radius` (optional): Distance in km (default: 50)
- `limit` (default: 20): Results per page

**Response 200:**
```json
{
  "success": true,
  "data": {
    "suggestions": [
      {
        "id": "match_456",
        "user": {
          "id": 789,
          "firstName": "Maria",
          "lastName": "G.",
          "profileImageUrl": "https://cdn.skillswap.com/profiles/789.jpg",
          "location": "Madrid, Spain",
          "rating": 4.9,
          "completedSessions": 25
        },
        "skill": {
          "id": 2,
          "name": "Spanish",
          "category": "Languages"
        },
        "teacherSkill": {
          "level": "NATIVE",
          "verified": true,
          "yearsExperience": 10
        },
        "learnerSkill": {
          "level": "BEGINNER",
          "wantsToLearn": true
        },
        "matchScore": 0.92,
        "distance": 2.5,
        "commonInterests": ["Travel", "Technology"],
        "lastActive": "2025-09-06T09:15:00Z"
      }
    ]
  }
}
```

### POST /matches/request
**Status:** ❌ Sprint 3

**Request:**
```json
{
  "targetUserId": 789,
  "skillId": 2,
  "message": "Hi! I'd love to practice Spanish with you and can help you with JavaScript in return.",
  "proposedSessionDuration": 60
}
```

**Response 201:**
```json
{
  "success": true,
  "data": {
    "matchRequest": {
      "id": "req_123",
      "targetUser": {
        "id": 789,
        "firstName": "Maria",
        "lastName": "G."
      },
      "skill": {
        "id": 2,
        "name": "Spanish"
      },
      "message": "Hi! I'd love to practice Spanish...",
      "status": "PENDING",
      "createdAt": "2025-09-06T10:30:00Z",
      "expiresAt": "2025-09-13T10:30:00Z"
    }
  }
}
```

### GET /matches
**Status:** ❌ Sprint 3

**Query Parameters:**
- `status` (optional): PENDING, ACCEPTED, DECLINED, COMPLETED
- `type` (optional): SENT, RECEIVED

**Response 200:**
```json
{
  "success": true,
  "data": {
    "matches": [
      {
        "id": "match_123",
        "otherUser": {
          "id": 789,
          "firstName": "Maria",
          "lastName": "G.",
          "profileImageUrl": "https://cdn.skillswap.com/profiles/789.jpg"
        },
        "skill": {
          "id": 2,
          "name": "Spanish"
        },
        "status": "ACCEPTED",
        "message": "Looking forward to our session!",
        "createdAt": "2025-09-06T10:30:00Z",
        "lastActivity": "2025-09-06T11:15:00Z"
      }
    ]
  }
}
```

---

## 📅 SESSION MANAGEMENT ENDPOINTS

### POST /sessions
**Status:** ❌ Sprint 4

**Request:**
```json
{
  "matchId": "match_123",
  "scheduledAt": "2025-09-07T15:00:00Z",
  "durationMinutes": 60,
  "notes": "Let's focus on conversational Spanish"
}
```

**Response 201:**
```json
{
  "success": true,
  "data": {
    "session": {
      "id": "session_456",
      "match": {
        "id": "match_123",
        "otherUser": {
          "id": 789,
          "firstName": "Maria",
          "lastName": "G."
        },
        "skill": {
          "id": 2,
          "name": "Spanish"
        }
      },
      "scheduledAt": "2025-09-07T15:00:00Z",
      "durationMinutes": 60,
      "status": "SCHEDULED",
      "videoRoomId": "room_abc123",
      "notes": "Let's focus on conversational Spanish",
      "createdAt": "2025-09-06T10:30:00Z"
    }
  }
}
```

### GET /sessions
**Status:** ❌ Sprint 4

**Query Parameters:**
- `status` (optional): SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
- `from` (optional): Start date filter
- `to` (optional): End date filter

**Response 200:**
```json
{
  "success": true,
  "data": {
    "sessions": [
      {
        "id": "session_456",
        "otherUser": {
          "id": 789,
          "firstName": "Maria",
          "lastName": "G.",
          "profileImageUrl": "https://cdn.skillswap.com/profiles/789.jpg"
        },
        "skill": {
          "id": 2,
          "name": "Spanish"
        },
        "scheduledAt": "2025-09-07T15:00:00Z",
        "durationMinutes": 60,
        "status": "SCHEDULED",
        "videoRoomId": "room_abc123"
      }
    ]
  }
}
```

### POST /sessions/{sessionId}/join
**Status:** ❌ Sprint 5 (Video Integration)

**Response 200:**
```json
{
  "success": true,
  "data": {
    "session": {
      "id": "session_456",
      "status": "IN_PROGRESS",
      "videoRoomId": "room_abc123",
      "agoraToken": "agora_temp_token_here",
      "startedAt": "2025-09-07T15:00:00Z"
    }
  }
}
```

---

## 💰 CREDITS SYSTEM ENDPOINTS

### GET /credits/balance
**Status:** ❌ Sprint 4

**Response 200:**
```json
{
  "success": true,
  "data": {
    "balance": 12,
    "earned": 15,
    "spent": 3,
    "pending": 2
  }
}
```

### GET /credits/transactions
**Status:** ❌ Sprint 4

**Query Parameters:**
- `limit` (default: 20): Results per page
- `offset` (default: 0): Pagination offset
- `type` (optional): EARNED, SPENT, BONUS, PENALTY

**Response 200:**
```json
{
  "success": true,
  "data": {
    "transactions": [
      {
        "id": "tx_789",
        "amount": 1,
        "type": "EARNED",
        "description": "Completed session with Maria G.",
        "sessionId": "session_456",
        "createdAt": "2025-09-06T10:30:00Z"
      },
      {
        "id": "tx_790",
        "amount": -1,
        "type": "SPENT",
        "description": "Session with John D.",
        "sessionId": "session_457",
        "createdAt": "2025-09-05T14:00:00Z"
      }
    ],
    "total": 50,
    "hasMore": true
  }
}
```

---

## 📱 NOTIFICATIONS ENDPOINTS

### GET /notifications
**Status:** ❌ Sprint 5

**Query Parameters:**
- `read` (optional): true/false filter
- `limit` (default: 20): Results per page

**Response 200:**
```json
{
  "success": true,
  "data": {
    "notifications": [
      {
        "id": "notif_123",
        "type": "MATCH_REQUEST",
        "title": "New match request",
        "message": "Maria G. wants to practice Spanish with you",
        "read": false,
        "data": {
          "matchRequestId": "req_123",
          "userId": 789
        },
        "createdAt": "2025-09-06T10:30:00Z"
      }
    ],
    "unreadCount": 3
  }
}
```

### PUT /notifications/{notificationId}/read
**Status:** ❌ Sprint 5

**Response 200:**
```json
{
  "success": true,
  "message": "Notification marked as read"
}
```

---

## 🔍 SEARCH & DISCOVERY ENDPOINTS

### GET /search/users
**Status:** ❌ Sprint 6

**Query Parameters:**
- `skill` (optional): Skill name or ID
- `location` (optional): City or region
- `radius` (optional): Distance in km
- `canTeach` (optional): true/false
- `wantsToLearn` (optional): true/false

**Response 200:**
```json
{
  "success": true,
  "data": {
    "users": [
      {
        "id": 789,
        "firstName": "Maria",
        "lastName": "G.",
        "profileImageUrl": "https://cdn.skillswap.com/profiles/789.jpg",
        "location": "Madrid, Spain",
        "skills": [
          {
            "id": 2,
            "name": "Spanish",
            "canTeach": true,
            "level": "NATIVE"
          }
        ],
        "rating": 4.9,
        "completedSessions": 25,
        "distance": 2.5
      }
    ]
  }
}
```

---

## 📊 ANALYTICS ENDPOINTS

### GET /analytics/dashboard
**Status:** ❌ Sprint 7

**Response 200:**
```json
{
  "success": true,
  "data": {
    "stats": {
      "totalSessions": 12,
      "totalCreditsEarned": 15,
      "totalCreditsSpent": 3,
      "averageRating": 4.8,
      "skillsTeaching": 3,
      "skillsLearning": 2,
      "monthlyGrowth": {
        "sessions": 2,
        "credits": 5
      }
    }
  }
}
```

---

## 🚨 ERROR CODES

### Authentication Errors
- `INVALID_CREDENTIALS` - Wrong email/password
- `EMAIL_ALREADY_EXISTS` - Registration with existing email
- `TOKEN_EXPIRED` - JWT token expired
- `TOKEN_INVALID` - Malformed JWT token
- `REFRESH_TOKEN_INVALID` - Invalid refresh token

### User Errors
- `USER_NOT_FOUND` - User ID doesn't exist
- `EMAIL_NOT_VERIFIED` - Action requires verified email
- `INSUFFICIENT_PERMISSIONS` - User lacks required permissions

### Skill Errors
- `SKILL_NOT_FOUND` - Skill ID doesn't exist
- `SKILL_ALREADY_ADDED` - User already has this skill
- `INVALID_SKILL_LEVEL` - Invalid skill level value

### Match Errors
- `MATCH_NOT_FOUND` - Match ID doesn't exist
- `MATCH_ALREADY_EXISTS` - Match request already sent
- `CANNOT_MATCH_SELF` - User trying to match with themselves
- `INSUFFICIENT_CREDITS` - Not enough credits for action

### Session Errors
- `SESSION_NOT_FOUND` - Session ID doesn't exist
- `SESSION_ALREADY_STARTED` - Cannot modify active session
- `SESSION_CONFLICT` - Time slot conflict with existing session
- `VIDEO_ROOM_UNAVAILABLE` - Cannot create video room

### General Errors
- `VALIDATION_ERROR` - Request validation failed
- `RATE_LIMIT_EXCEEDED` - Too many requests
- `SERVER_ERROR` - Internal server error
- `SERVICE_UNAVAILABLE` - External service unavailable

---

## 📝 IMPLEMENTATION STATUS

### Sprint 1 (Current) - Updated 6 Sep 2025
- 🔄 **POST /auth/register** - Backend Agent implementing
- 🔄 **POST /auth/login** - Backend Agent implementing
- 🔄 **GET /users/profile** - Backend Agent implementing
- 🔄 **GET /skills** - Backend Agent implementing  
- 🔄 **GET /skills/categories** - Backend Agent implementing

### Backend Implementation Progress
- ✅ **Spring Boot Setup** - Completed by Backend Agent
- ✅ **Project Structure** - Completed by Backend Agent
- ✅ **Docker Configuration** - Completed by Backend Agent
- 🔄 **JWT Authentication** - In progress by Backend Agent
- 🔄 **User Entity & Repository** - In progress by Backend Agent
- 🔄 **Skills Entity & Repository** - In progress by Backend Agent

### Frontend Implementation Progress  
- ✅ **React Native Setup** - Completed by Frontend Agent
- ✅ **TypeScript Configuration** - Completed by Frontend Agent
- ✅ **Navigation Structure** - Completed by Frontend Agent
- 🔄 **Authentication Screens** - In progress by Frontend Agent
- 🔄 **API Integration Layer** - Pending backend completion

### Sprint 2
- ❌ **POST /auth/refresh** - Not started
- ❌ **POST /auth/logout** - Not started
- ❌ **PUT /users/profile** - Not started
- ❌ **POST /skills/user-skills** - Not started
- ❌ **GET /skills/user-skills** - Not started

### Sprint 3
- ❌ **GET /matches/suggestions** - Not started
- ❌ **POST /matches/request** - Not started
- ❌ **GET /matches** - Not started

### Sprint 4+
- ❌ **Session Management** - Not started
- ❌ **Credits System** - Not started
- ❌ **Notifications** - Not started

---

## 🔄 CHANGELOG

### 2025-09-06
- ✅ Initial API contract definition
- ✅ Authentication endpoints specified
- ✅ User management endpoints defined
- ✅ Skills management endpoints outlined
- ✅ Error codes standardized

### Future Updates
- Updates will be logged here as implementation progresses
- Both Backend and Frontend agents should update this section

---

*API Contract maintained by: Coordinator Agent*
*Last updated: 6 de septiembre de 2025*
*Next review: After Sprint 1 completion*
