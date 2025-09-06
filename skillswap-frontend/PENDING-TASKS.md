# 📱 FRONTEND AGENT - PENDING TASKS
**Last Updated:** 6 septiembre 2025 - 14:30  
**Status:** 🟡 READY TO START  
**Current Priority:** US-002 (Registration Screen)

---

## 🎯 **CURRENT FOCUS**

### **📱 US-002: Registration Screen** 
**Priority:** 🔴 CRITICAL  
**Story Points:** 8  
**Dependencies:** Can start structure, API coming soon (US-001)  
**Deadline:** 9 septiembre (Day 4)

**📍 EXACTLY WHAT TO DO NEXT:**

1. **Initialize React Native Project**
   ```bash
   # In this directory (skillswap-frontend/):
   npx react-native@latest init SkillSwapApp --template react-native-template-typescript
   cd SkillSwapApp
   npm install
   ```

2. **Setup Project Structure**
   ```
   src/
   ├── components/           # Reusable UI components
   │   ├── forms/           # Form components
   │   ├── common/          # Common UI elements
   │   └── auth/           # Auth-specific components
   ├── screens/            # Screen components
   │   ├── auth/           # Login, Register, etc.
   │   ├── profile/        # Profile screens
   │   └── main/           # Main app screens
   ├── navigation/         # Navigation configuration
   ├── store/             # Redux store & slices
   ├── services/          # API services
   ├── hooks/             # Custom hooks
   ├── utils/             # Utility functions
   └── types/             # TypeScript types
   ```

3. **Create Registration Screen UI**
   ```tsx
   // File: src/screens/auth/RegisterScreen.tsx
   import React from 'react';
   import { View, Text, TextInput, TouchableOpacity } from 'react-native';
   import { useForm } from 'react-hook-form';

   interface RegisterForm {
     email: string;
     password: string;
     confirmPassword: string;
     firstName: string;
     lastName: string;
     acceptTerms: boolean;
   }

   export const RegisterScreen = () => {
     const { control, handleSubmit, formState: { errors } } = useForm<RegisterForm>();
     
     const onSubmit = (data: RegisterForm) => {
       // Will integrate with API once US-001 is ready
       console.log('Registration data:', data);
     };

     return (
       <View style={styles.container}>
         {/* Form fields with validation */}
       </View>
     );
   };
   ```

---

## 📋 **ACCEPTANCE CRITERIA CHECKLIST**

### **Must Complete Today:**
- [ ] React Native project initialized and running
- [ ] Basic navigation structure setup
- [ ] Registration screen component created
- [ ] Form structure with all required fields

### **Must Complete Tomorrow:**
- [ ] Form validation working (real-time)
- [ ] Password strength indicator
- [ ] Terms & conditions checkbox
- [ ] Loading states and error handling
- [ ] Responsive design for different screen sizes

---

## 🎨 **UI/UX SPECIFICATIONS**

### **Registration Form Fields:**
1. **Email** - TextInput with email validation
2. **Password** - Secure TextInput with strength indicator
3. **Confirm Password** - Must match password
4. **First Name** - TextInput, required
5. **Last Name** - TextInput, required  
6. **Terms & Conditions** - Checkbox, required

### **Design Requirements:**
- **Colors:** Primary #007AFF (iOS blue), Secondary #34C759
- **Typography:** SF Pro (iOS) / Roboto (Android)
- **Layout:** Card-based design with rounded corners
- **Spacing:** 16px padding, 12px between fields
- **Responsive:** Work from iPhone SE (375px) to iPad Pro

### **Validation Rules:**
```typescript
const validationRules = {
  email: {
    required: 'Email is required',
    pattern: {
      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
      message: 'Invalid email address'
    }
  },
  password: {
    required: 'Password is required',
    minLength: {
      value: 8,
      message: 'Password must be at least 8 characters'
    },
    pattern: {
      value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/,
      message: 'Password must contain uppercase, lowercase, number and special character'
    }
  }
};
```

---

## 🛠️ **DEPENDENCIES TO INSTALL**

### **Core Dependencies:**
```bash
npm install @reduxjs/toolkit react-redux
npm install @react-navigation/native @react-navigation/stack @react-navigation/bottom-tabs
npm install react-native-screens react-native-safe-area-context
npm install react-hook-form
npm install react-native-paper react-native-vector-icons
npm install @react-native-async-storage/async-storage
```

### **Development Dependencies:**
```bash
npm install --save-dev @types/react-native-vector-icons
npm install --save-dev @testing-library/react-native jest
```

---

## 🗂️ **PROJECT STRUCTURE TO CREATE**

### **Navigation Setup:**
```tsx
// src/navigation/AppNavigator.tsx
import { createStackNavigator } from '@react-navigation/stack';
import { RegisterScreen } from '../screens/auth/RegisterScreen';
import { LoginScreen } from '../screens/auth/LoginScreen';

const Stack = createStackNavigator();

export const AppNavigator = () => {
  return (
    <Stack.Navigator initialRouteName="Register">
      <Stack.Screen name="Register" component={RegisterScreen} />
      <Stack.Screen name="Login" component={LoginScreen} />
    </Stack.Navigator>
  );
};
```

### **Redux Store Setup:**
```tsx
// src/store/index.ts
import { configureStore } from '@reduxjs/toolkit';
import authSlice from './slices/authSlice';

export const store = configureStore({
  reducer: {
    auth: authSlice,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
```

---

## 🔗 **API INTEGRATION (Once Backend Ready)**

### **Registration API Call:**
```typescript
// src/services/authService.ts
import { API_BASE_URL } from '../config';

interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  acceptTerms: boolean;
}

export const registerUser = async (data: RegisterRequest) => {
  const response = await fetch(`${API_BASE_URL}/auth/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    throw new Error('Registration failed');
  }

  return response.json();
};
```

---

## 📊 **PROGRESS TRACKING**

### **Daily Commits Expected:**
```bash
# Day 1 (Today):
feat(project): initialize react native project with typescript ✅
feat(US-002): setup navigation and project structure
feat(US-002): create registration screen basic UI

# Day 2:
feat(US-002): implement form validation with react-hook-form
feat(US-002): add password strength indicator
feat(US-002): create responsive design and loading states

# Day 3:
feat(US-002): integrate registration form with backend API
feat(US-002): add error handling and success navigation
test(US-002): add component tests for registration flow ✅
```

---

## 🔄 **NEXT TASKS AFTER US-002**

### **US-008: Login Screen (4 pts)**
- Login form with email/password
- "Remember me" functionality
- Biometric login integration
- Forgot password flow

### **US-004: Profile Screen (5 pts)**
- Profile information form
- Avatar upload functionality
- Skills management UI

---

## 🎨 **MOCKUP REFERENCES**

### **Registration Screen Layout:**
```
┌─────────────────────────────┐
│        SkillSwap           │
│      Create Account        │ 
│                            │
│  ┌─────────────────────┐    │
│  │ Email              │    │
│  └─────────────────────┘    │
│  ┌─────────────────────┐    │
│  │ Password     [👁️]   │    │
│  └─────────────────────┘    │
│  Password Strength: ████   │
│  ┌─────────────────────┐    │
│  │ Confirm Password   │    │
│  └─────────────────────┘    │
│  ┌─────────────────────┐    │
│  │ First Name         │    │
│  └─────────────────────┘    │
│  ┌─────────────────────┐    │
│  │ Last Name          │    │
│  └─────────────────────┘    │
│                            │
│  ☑️ I accept Terms & Cond. │
│                            │
│  ┌─────────────────────┐    │
│  │   Create Account   │    │
│  └─────────────────────┘    │
│                            │
│   Already have account?    │
│        Sign In             │
└─────────────────────────────┘
```

---

## 🚨 **DEPENDENCIES & BLOCKERS**

### **Current Status:**
- ✅ Can start project setup and UI structure immediately
- 🟡 API integration pending (US-001 from Backend)
- ✅ All design specifications available

### **When API Ready:**
- Backend will commit: `feat(US-001): complete registration API ✅`
- You can then integrate with `/api/v1/auth/register`
- Test end-to-end registration flow

---

## 🎯 **SUCCESS DEFINITION**

**US-002 is DONE when:**
- [ ] User can fill registration form with validation
- [ ] Form shows real-time validation errors
- [ ] Password strength indicator works
- [ ] Loading state during API call
- [ ] Success navigation after registration
- [ ] Error handling for API failures
- [ ] Works on both iOS and Android
- [ ] Responsive design tested on different screen sizes

---

**🔄 WHEN STUCK:** Just write "revisa y continúa" and I'll analyze your progress + give next specific steps!

**📞 COMMUNICATION:** Commit messages with progress updates expected every 2-3 hours during development.

**🎊 Ready to start building the mobile experience? Let's go! 📱🚀**
