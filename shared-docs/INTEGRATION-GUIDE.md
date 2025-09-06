# 🔗 Integration Guide - Backend ↔ Frontend
## SkillSwap Development Coordination

**Última actualización:** 6 de septiembre de 2025  
**Status:** 🟢 Ready for Implementation  
**Target:** Sprint 1 Backend-Frontend Integration

---

## 🎯 Integration Overview

### **Objective**
Seamless integration between SkillSwap Backend (Spring Boot) and Frontend (React Native) ensuring data consistency, error handling, and optimal user experience.

### **Integration Architecture**
```
┌─────────────────┐    HTTP/HTTPS    ┌─────────────────┐
│  React Native   │ ◄──────────────► │   Spring Boot   │
│   Frontend      │     REST API     │     Backend     │
│                 │                  │                 │
│ • State Mgmt    │                  │ • JWT Auth      │
│ • API Client    │                  │ • PostgreSQL    │
│ • Error Handling│                  │ • Validation    │
└─────────────────┘                  └─────────────────┘
```

---

## 🔌 API Integration Points

### **1. Authentication Flow**

#### **Backend Implementation (US-001)**
```java
// UserController.java
@PostMapping("/auth/register")
public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    // Implementation by Backend Agent
}

@PostMapping("/auth/login") 
public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    // Implementation by Backend Agent
}
```

#### **Frontend Integration (US-002)**
```typescript
// authService.ts
export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface AuthResponse {
  token: string;
  user: User;
  expiresIn: number;
}

export const authAPI = {
  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
    
    if (!response.ok) {
      throw new APIError(await response.json());
    }
    
    return response.json();
  },
  
  login: async (email: string, password: string): Promise<AuthResponse> => {
    // Similar implementation
  }
};
```

### **2. Error Handling Integration**

#### **Backend Error Response Format**
```java
// ErrorResponse.java
public class ErrorResponse {
    private String message;
    private String code;
    private Map<String, String> fieldErrors;
    private LocalDateTime timestamp;
    // getters/setters
}

// Global Exception Handler
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        return ResponseEntity.badRequest().body(
            new ErrorResponse("VALIDATION_ERROR", ex.getMessage(), ex.getFieldErrors())
        );
    }
}
```

#### **Frontend Error Handling**
```typescript
// errorHandler.ts
export class APIError extends Error {
  constructor(
    public message: string,
    public code: string,
    public fieldErrors?: Record<string, string>,
    public status?: number
  ) {
    super(message);
  }
}

export const handleAPIError = (error: APIError): string => {
  switch (error.code) {
    case 'VALIDATION_ERROR':
      return 'Please check your input and try again';
    case 'EMAIL_ALREADY_EXISTS':
      return 'This email is already registered';
    case 'INVALID_CREDENTIALS':
      return 'Invalid email or password';
    default:
      return 'An unexpected error occurred';
  }
};
```

### **3. State Management Integration**

#### **Frontend State Management**
```typescript
// userSlice.ts (Redux Toolkit)
export const userSlice = createSlice({
  name: 'user',
  initialState: {
    currentUser: null,
    token: null,
    isAuthenticated: false,
    loading: false,
    error: null,
  },
  reducers: {
    loginStart: (state) => {
      state.loading = true;
      state.error = null;
    },
    loginSuccess: (state, action) => {
      state.currentUser = action.payload.user;
      state.token = action.payload.token;
      state.isAuthenticated = true;
      state.loading = false;
    },
    loginFailure: (state, action) => {
      state.error = action.payload;
      state.loading = false;
    },
  },
});

// Async thunk for login
export const loginUser = createAsyncThunk(
  'user/login',
  async ({ email, password }: { email: string; password: string }) => {
    const response = await authAPI.login(email, password);
    // Store token securely
    await SecureStore.setItemAsync('userToken', response.token);
    return response;
  }
);
```

---

## 🧪 Integration Testing

### **1. API Testing Scenarios**

#### **Registration Flow Test**
```typescript
// __tests__/integration/registration.test.ts
describe('Registration Integration', () => {
  beforeAll(async () => {
    // Ensure backend is running
    await waitForBackend();
  });

  it('should register user successfully', async () => {
    const userData = {
      email: 'test@example.com',
      password: 'SecurePassword123!',
      firstName: 'John',
      lastName: 'Doe',
    };

    const response = await authAPI.register(userData);
    
    expect(response.token).toBeDefined();
    expect(response.user.email).toBe(userData.email);
    expect(response.user.firstName).toBe(userData.firstName);
  });

  it('should handle duplicate email error', async () => {
    const userData = {
      email: 'existing@example.com',
      password: 'SecurePassword123!',
      firstName: 'Jane',
      lastName: 'Doe',
    };

    // First registration should succeed
    await authAPI.register(userData);

    // Second registration should fail
    await expect(authAPI.register(userData))
      .rejects.toThrow('EMAIL_ALREADY_EXISTS');
  });
});
```

### **2. E2E Testing**

#### **Complete User Flow Test**
```typescript
// e2e/userJourney.e2e.ts
describe('Complete User Journey', () => {
  it('should complete registration to skill booking flow', async () => {
    // 1. Open app
    await device.launchApp();
    
    // 2. Navigate to registration
    await element(by.id('register-button')).tap();
    
    // 3. Fill registration form
    await element(by.id('email-input')).typeText('newuser@example.com');
    await element(by.id('password-input')).typeText('SecurePassword123!');
    await element(by.id('first-name-input')).typeText('John');
    await element(by.id('last-name-input')).typeText('Doe');
    
    // 4. Submit registration
    await element(by.id('register-submit')).tap();
    
    // 5. Verify successful registration
    await expect(element(by.text('Welcome to SkillSwap!'))).toBeVisible();
    
    // 6. Continue with skill search
    await element(by.id('explore-skills')).tap();
    await expect(element(by.id('skills-screen'))).toBeVisible();
  });
});
```

---

## 🔧 Development Workflow

### **1. Backend-First Integration**

#### **Step 1: Backend API Development**
```bash
# Backend Agent workflow
git checkout -b feat/US-001-registration-api
# Implement registration endpoint
# Write unit tests
# Commit with message: "feat(US-001): implement user registration API"
git push origin feat/US-001-registration-api
```

#### **Step 2: Frontend Integration**
```bash
# Frontend Agent workflow
git checkout -b feat/US-002-integrate-registration
# Update API client with Backend's actual implementation
# Implement error handling
# Write integration tests
# Commit with message: "feat(US-002): integrate registration with Backend API"
git push origin feat/US-002-integrate-registration
```

#### **Step 3: Integration Testing**
```bash
# Coordinator Agent ensures
# 1. Both branches are merged
# 2. Integration tests pass
# 3. E2E tests pass
# 4. Documentation is updated
```

### **2. API Contract Validation**

#### **Automated Contract Testing**
```typescript
// contracts/auth.contract.test.ts
import { Pact } from '@pact-foundation/pact';

describe('Auth API Contract', () => {
  const provider = new Pact({
    consumer: 'SkillSwap-Frontend',
    provider: 'SkillSwap-Backend',
  });

  it('should register user with valid data', async () => {
    await provider
      .given('user does not exist')
      .uponReceiving('a registration request')
      .withRequest({
        method: 'POST',
        path: '/api/auth/register',
        headers: { 'Content-Type': 'application/json' },
        body: {
          email: 'test@example.com',
          password: 'SecurePassword123!',
          firstName: 'John',
          lastName: 'Doe',
        },
      })
      .willRespondWith({
        status: 201,
        headers: { 'Content-Type': 'application/json' },
        body: {
          token: Matchers.string(),
          user: {
            id: Matchers.integer(),
            email: 'test@example.com',
            firstName: 'John',
            lastName: 'Doe',
          },
          expiresIn: Matchers.integer(),
        },
      });

    const response = await authAPI.register({
      email: 'test@example.com',
      password: 'SecurePassword123!',
      firstName: 'John',
      lastName: 'Doe',
    });

    expect(response.token).toBeDefined();
  });
});
```

---

## 🚨 Troubleshooting Guide

### **Common Integration Issues**

#### **1. CORS Issues**
```java
// Backend: CorsConfig.java
@Configuration
@EnableWebSecurity
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

#### **2. Network Configuration (React Native)**
```typescript
// config/api.ts
const API_BASE_URL = __DEV__ 
  ? Platform.OS === 'ios' 
    ? 'http://localhost:8080/api/v1'
    : 'http://10.0.2.2:8080/api/v1'  // Android emulator
  : 'https://api.skillswap.com/api/v1';

// Add network security config for Android
// android/app/src/main/res/xml/network_security_config.xml
```

#### **3. Token Management**
```typescript
// utils/tokenManager.ts
import * as SecureStore from 'expo-secure-store';

export const TokenManager = {
  async getToken(): Promise<string | null> {
    try {
      return await SecureStore.getItemAsync('userToken');
    } catch (error) {
      console.error('Error getting token:', error);
      return null;
    }
  },

  async setToken(token: string): Promise<void> {
    try {
      await SecureStore.setItemAsync('userToken', token);
    } catch (error) {
      console.error('Error setting token:', error);
    }
  },

  async removeToken(): Promise<void> {
    try {
      await SecureStore.deleteItemAsync('userToken');
    } catch (error) {
      console.error('Error removing token:', error);
    }
  },
};

// API client with automatic token injection
export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

apiClient.interceptors.request.use(async (config) => {
  const token = await TokenManager.getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

---

## ✅ Integration Checklist

### **Before Integration**
- [ ] Backend API endpoints are implemented and tested
- [ ] API documentation is updated in `API-Contract.md`
- [ ] Frontend API client interfaces are defined
- [ ] Error handling patterns are agreed upon
- [ ] Network configuration is properly set up

### **During Integration**
- [ ] API calls are working correctly
- [ ] Error responses are handled gracefully
- [ ] Token management is working
- [ ] Loading states are implemented
- [ ] Data validation is working on both ends

### **After Integration**
- [ ] Integration tests are passing
- [ ] E2E tests are working
- [ ] Performance is acceptable
- [ ] Error scenarios are tested
- [ ] Documentation is updated

### **Production Readiness**
- [ ] Security headers are configured
- [ ] Rate limiting is in place
- [ ] Monitoring and logging are set up
- [ ] HTTPS is enforced
- [ ] GDPR compliance is verified

---

## 📊 Success Metrics

### **Integration Quality Metrics**
- ✅ **API Response Time:** < 500ms p95
- ✅ **Error Rate:** < 1% for 4xx/5xx responses
- ✅ **Test Coverage:** > 85% for integration code
- ✅ **User Experience:** < 3 seconds for complete user flows

### **Development Velocity Metrics**
- ✅ **Integration Time:** < 1 day per major feature
- ✅ **Bug Resolution:** < 4 hours for integration issues
- ✅ **Deployment Time:** < 30 minutes for full stack deployment

---

*Última actualización: 6 de septiembre de 2025*  
*Próxima revisión: Después de cada integración major*

## 🎯 Ready for Seamless Integration!

This guide ensures smooth Backend-Frontend collaboration with minimal integration issues and maximum development velocity.

**🚀 Let's build amazing integrations! 🔗**
