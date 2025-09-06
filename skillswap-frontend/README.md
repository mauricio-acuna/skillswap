# SkillSwap Frontend - PRD Específico
## React Native Mobile App (iOS + Android)

### 🎯 SCOPE DEL FRONTEND

Este documento es específico para el desarrollo del **frontend mobile de SkillSwap**. Para contexto completo, consultar:
- `../shared-docs/PRD.md` - PRD general del proyecto
- `../shared-docs/SkillSwap-TechnicalSpecs.md` - Especificaciones técnicas
- `../shared-docs/MercadoObjetivo.md` - Análisis de mercado

---

## 📱 RESPONSABILIDADES DEL FRONTEND

### Core Responsibilities
1. **User Interface** (iOS + Android adaptive design)
2. **Authentication Flows** (Login, Register, OAuth2, Biometric)
3. **User Profile Management** (Skills, preferences, photo)
4. **Skill Discovery & Management** (Browse, add, search skills)
5. **Matching Interface** (Browse matches, send/accept requests)
6. **Session Management** (Schedule, join, manage sessions)
7. **Video Calling** (In-app video calls con WebRTC)
8. **Real-time Features** (Push notifications, live updates)
9. **Credit System UI** (Balance, transactions, history)
10. **European Localization** (5 languages, GDPR compliance)
11. **Offline Capabilities** (Cache, sync when online)
12. **Performance Optimization** (Device compatibility, smooth UX)

---

## 🏗️ ARQUITECTURA FRONTEND

### Tech Stack
```
┌─────────────────────────────────────────────────────────────┐
│                   SKILLSWAP MOBILE APP                      │
├─────────────────────────────────────────────────────────────┤
│  React Native 0.72+ (TypeScript)                          │
│  ├── React Navigation 6 (Stack + Tab + Drawer)            │
│  ├── Redux Toolkit + RTK Query (State + API)              │
│  ├── React Hook Form (Forms & Validation)                 │
│  ├── React Native Paper (UI Components)                   │
│  ├── React Native Vector Icons (Icons)                    │
│  └── React Native Localize (i18n)                         │
├─────────────────────────────────────────────────────────────┤
│  Real-time & Video                                         │
│  ├── React Native WebRTC (Video calling)                  │
│  ├── Socket.io Client (Real-time events)                  │
│  ├── React Native Calendar Kit (Scheduling)               │
│  └── React Native Permissions (Camera, Mic)               │
├─────────────────────────────────────────────────────────────┤
│  Storage & Cache                                           │
│  ├── AsyncStorage (User preferences)                      │
│  ├── React Native MMKV (High-performance cache)           │
│  ├── React Native Fast Image (Image caching)              │
│  └── Redux Persist (State persistence)                    │
├─────────────────────────────────────────────────────────────┤
│  Device Integration                                        │
│  ├── React Native Biometrics (Touch/Face ID)              │
│  ├── React Native Push Notification (FCM)                 │
│  ├── React Native Geolocation (Location services)         │
│  ├── React Native Camera (Profile photos)                 │
│  └── React Native Share (Social sharing)                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 ESTRUCTURA DE CARPETAS FRONTEND

```
skillswap-frontend/
├── android/                                # Android-specific files
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── AndroidManifest.xml         # Permissions, deep links
│   │   │   ├── res/                        # Resources (icons, strings)
│   │   │   └── java/com/skillswap/         # Native Android code
│   │   └── build.gradle                    # Android dependencies
│   ├── gradle/                             # Gradle configuration
│   └── build.gradle                        # Project-level config
├── ios/                                    # iOS-specific files
│   ├── SkillSwap/
│   │   ├── Info.plist                      # iOS app configuration
│   │   ├── AppDelegate.h/m                 # iOS app delegate
│   │   ├── Images.xcassets/                # iOS assets
│   │   └── LaunchScreen.storyboard         # Launch screen
│   ├── SkillSwap.xcodeproj/                # Xcode project
│   └── Podfile                             # iOS dependencies
├── src/                                    # Source code
│   ├── components/                         # Reusable UI components
│   │   ├── common/                         # Generic components
│   │   │   ├── Button/
│   │   │   │   ├── Button.tsx              # Custom button component
│   │   │   │   ├── Button.styles.ts        # Styled components
│   │   │   │   └── Button.test.tsx         # Component tests
│   │   │   ├── Input/                      # Custom input component
│   │   │   ├── LoadingSpinner/             # Loading indicator
│   │   │   ├── ErrorBoundary/              # Error handling
│   │   │   ├── NetworkStatus/              # Network connectivity
│   │   │   └── ConfirmationModal/          # Confirmation dialogs
│   │   ├── auth/                           # Authentication components
│   │   │   ├── LoginForm/                  # Login form
│   │   │   ├── RegisterForm/               # Registration form
│   │   │   ├── ForgotPasswordForm/         # Password reset
│   │   │   ├── BiometricLogin/             # Touch/Face ID
│   │   │   └── SocialLoginButtons/         # OAuth2 buttons
│   │   ├── profile/                        # Profile components
│   │   │   ├── ProfileCard/                # User profile display
│   │   │   ├── ProfileEditForm/            # Edit profile form
│   │   │   ├── SkillsList/                 # User skills display
│   │   │   ├── AddSkillModal/              # Add new skill
│   │   │   └── ProfileImagePicker/         # Photo selection
│   │   ├── skills/                         # Skill-related components
│   │   │   ├── SkillCard/                  # Individual skill display
│   │   │   ├── SkillCategoryList/          # Skill categories
│   │   │   ├── SkillSearchBar/             # Search functionality
│   │   │   ├── SkillLevelSelector/         # Skill level picker
│   │   │   └── SkillVerificationBadge/     # Verification status
│   │   ├── matching/                       # Matching components
│   │   │   ├── MatchCard/                  # Match display card
│   │   │   ├── MatchRequestModal/          # Send match request
│   │   │   ├── MatchingFilters/            # Filter options
│   │   │   ├── SwipeableMatchCard/         # Tinder-like matching
│   │   │   └── MatchingAlgorithmInfo/      # How matching works
│   │   ├── sessions/                       # Session components
│   │   │   ├── SessionCard/                # Session display
│   │   │   ├── SessionScheduler/           # Calendar integration
│   │   │   ├── SessionTimer/               # Session countdown
│   │   │   ├── SessionRating/              # Rate session
│   │   │   └── SessionHistory/             # Past sessions
│   │   ├── video/                          # Video calling components
│   │   │   ├── VideoCallScreen/            # Main video interface
│   │   │   ├── VideoControls/              # Mute, camera, etc.
│   │   │   ├── ScreenShare/                # Screen sharing
│   │   │   ├── ChatOverlay/                # Text chat during call
│   │   │   └── CallQualityIndicator/       # Connection status
│   │   ├── credits/                        # Credit system components
│   │   │   ├── CreditBalance/              # Balance display
│   │   │   ├── CreditHistory/              # Transaction history
│   │   │   ├── CreditTransferModal/        # Transfer credits
│   │   │   └── EarnCreditsInfo/            # How to earn credits
│   │   └── notifications/                  # Notification components
│   │       ├── NotificationCard/           # Individual notification
│   │       ├── NotificationList/           # List of notifications
│   │       ├── PushNotificationHandler/    # FCM integration
│   │       └── InAppNotification/          # In-app notifications
│   ├── screens/                            # App screens/pages
│   │   ├── auth/                           # Authentication screens
│   │   │   ├── WelcomeScreen.tsx           # App introduction
│   │   │   ├── LoginScreen.tsx             # Login form
│   │   │   ├── RegisterScreen.tsx          # Registration form
│   │   │   ├── ForgotPasswordScreen.tsx    # Password reset
│   │   │   ├── EmailVerificationScreen.tsx # Email confirmation
│   │   │   └── GDPRConsentScreen.tsx       # GDPR consent
│   │   ├── onboarding/                     # User onboarding
│   │   │   ├── OnboardingIntroScreen.tsx   # App walkthrough
│   │   │   ├── SkillSelectionScreen.tsx    # Initial skill setup
│   │   │   ├── ProfileSetupScreen.tsx      # Profile creation
│   │   │   ├── LocationPermissionScreen.tsx # Location access
│   │   │   └── NotificationPermissionScreen.tsx # Push notifications
│   │   ├── main/                           # Main app screens
│   │   │   ├── HomeScreen.tsx              # Dashboard/home
│   │   │   ├── ExploreScreen.tsx           # Discover skills/users
│   │   │   ├── MatchesScreen.tsx           # Match management
│   │   │   ├── SessionsScreen.tsx          # Session management
│   │   │   └── ProfileScreen.tsx           # User profile
│   │   ├── skills/                         # Skill management screens
│   │   │   ├── SkillsOverviewScreen.tsx    # User's skills overview
│   │   │   ├── AddSkillScreen.tsx          # Add new skill
│   │   │   ├── SkillDetailScreen.tsx       # Individual skill details
│   │   │   ├── SkillCategoriesScreen.tsx   # Browse categories
│   │   │   └── SkillVerificationScreen.tsx # Skill verification
│   │   ├── matching/                       # Matching screens
│   │   │   ├── DiscoverMatchesScreen.tsx   # Browse potential matches
│   │   │   ├── MatchDetailScreen.tsx       # Individual match details
│   │   │   ├── SendMatchRequestScreen.tsx  # Send match request
│   │   │   ├── MatchRequestsScreen.tsx     # Incoming requests
│   │   │   └── MatchingPreferencesScreen.tsx # Matching settings
│   │   ├── sessions/                       # Session screens
│   │   │   ├── SessionsListScreen.tsx      # All sessions
│   │   │   ├── SessionDetailScreen.tsx     # Session details
│   │   │   ├── ScheduleSessionScreen.tsx   # Create new session
│   │   │   ├── JoinSessionScreen.tsx       # Pre-session screen
│   │   │   ├── ActiveSessionScreen.tsx     # During session
│   │   │   └── SessionFeedbackScreen.tsx   # Post-session rating
│   │   ├── video/                          # Video calling screens
│   │   │   ├── VideoCallLobbyScreen.tsx    # Pre-call preparation
│   │   │   ├── VideoCallScreen.tsx         # Main video call
│   │   │   ├── CallEndedScreen.tsx         # Post-call summary
│   │   │   └── CallSettingsScreen.tsx      # Video call preferences
│   │   ├── credits/                        # Credit system screens
│   │   │   ├── CreditsOverviewScreen.tsx   # Balance and overview
│   │   │   ├── CreditHistoryScreen.tsx     # Transaction history
│   │   │   ├── TransferCreditsScreen.tsx   # Send credits
│   │   │   └── EarnCreditsScreen.tsx       # Ways to earn credits
│   │   └── settings/                       # Settings screens
│   │       ├── SettingsScreen.tsx          # Main settings
│   │       ├── ProfileSettingsScreen.tsx   # Profile preferences
│   │       ├── NotificationSettingsScreen.tsx # Notification prefs
│   │       ├── PrivacySettingsScreen.tsx   # Privacy & GDPR
│   │       ├── LanguageSettingsScreen.tsx  # Language selection
│   │       ├── AboutScreen.tsx             # App information
│   │       └── HelpScreen.tsx              # Help & support
│   ├── navigation/                         # Navigation configuration
│   │   ├── RootNavigator.tsx               # Main navigation setup
│   │   ├── AuthNavigator.tsx               # Authentication flow
│   │   ├── MainTabNavigator.tsx            # Bottom tab navigation
│   │   ├── StackNavigator.tsx              # Stack navigation
│   │   ├── DrawerNavigator.tsx             # Drawer navigation
│   │   └── navigationTypes.ts              # Navigation type definitions
│   ├── services/                           # External service integrations
│   │   ├── api/                            # API communication
│   │   │   ├── client.ts                   # Axios/HTTP client setup
│   │   │   ├── authApi.ts                  # Authentication endpoints
│   │   │   ├── userApi.ts                  # User management endpoints
│   │   │   ├── skillApi.ts                 # Skill management endpoints
│   │   │   ├── matchApi.ts                 # Matching endpoints
│   │   │   ├── sessionApi.ts               # Session endpoints
│   │   │   ├── creditApi.ts                # Credit system endpoints
│   │   │   └── notificationApi.ts          # Notification endpoints
│   │   ├── websocket/                      # Real-time services
│   │   │   ├── socketClient.ts             # Socket.io client
│   │   │   ├── matchingEvents.ts           # Matching real-time events
│   │   │   ├── sessionEvents.ts            # Session real-time events
│   │   │   └── notificationEvents.ts       # Notification events
│   │   ├── video/                          # Video calling services
│   │   │   ├── webrtcService.ts            # WebRTC implementation
│   │   │   ├── agoraService.ts             # Agora.io integration
│   │   │   ├── callManager.ts              # Call state management
│   │   │   └── mediaDeviceManager.ts       # Camera/mic management
│   │   ├── storage/                        # Local storage services
│   │   │   ├── asyncStorageService.ts      # AsyncStorage wrapper
│   │   │   ├── secureStorageService.ts     # Keychain/Keystore
│   │   │   ├── cacheService.ts             # MMKV cache service
│   │   │   └── offlineStorageService.ts    # Offline data management
│   │   ├── location/                       # Location services
│   │   │   ├── geolocationService.ts       # GPS services
│   │   │   ├── locationPermissions.ts      # Permission handling
│   │   │   └── geocodingService.ts         # Address conversion
│   │   ├── calendar/                       # Calendar integration
│   │   │   ├── calendarService.ts          # Calendar operations
│   │   │   ├── eventManager.ts             # Event creation/management
│   │   │   └── reminderService.ts          # Session reminders
│   │   ├── push/                           # Push notifications
│   │   │   ├── fcmService.ts               # Firebase Cloud Messaging
│   │   │   ├── notificationHandler.ts      # Notification processing
│   │   │   └── notificationScheduler.ts    # Local notifications
│   │   └── biometric/                      # Biometric authentication
│   │       ├── biometricService.ts         # Touch/Face ID
│   │       ├── biometricPrompt.ts          # Biometric prompts
│   │       └── fallbackAuth.ts             # PIN/Pattern fallback
│   ├── store/                              # Redux store configuration
│   │   ├── index.ts                        # Store setup and configuration
│   │   ├── slices/                         # Redux Toolkit slices
│   │   │   ├── authSlice.ts                # Authentication state
│   │   │   ├── userSlice.ts                # User profile state
│   │   │   ├── skillSlice.ts               # Skills state
│   │   │   ├── matchSlice.ts               # Matching state
│   │   │   ├── sessionSlice.ts             # Sessions state
│   │   │   ├── creditSlice.ts              # Credits state
│   │   │   ├── notificationSlice.ts        # Notifications state
│   │   │   ├── videoCallSlice.ts           # Video call state
│   │   │   └── appSettingsSlice.ts         # App settings state
│   │   ├── api/                            # RTK Query API slices
│   │   │   ├── authApiSlice.ts             # Auth API queries
│   │   │   ├── userApiSlice.ts             # User API queries
│   │   │   ├── skillApiSlice.ts            # Skill API queries
│   │   │   ├── matchApiSlice.ts            # Match API queries
│   │   │   ├── sessionApiSlice.ts          # Session API queries
│   │   │   └── creditApiSlice.ts           # Credit API queries
│   │   ├── middleware/                     # Custom middleware
│   │   │   ├── authMiddleware.ts           # Auth token management
│   │   │   ├── offlineMiddleware.ts        # Offline action queueing
│   │   │   ├── analyticsMiddleware.ts      # Analytics tracking
│   │   │   └── errorReportingMiddleware.ts # Error tracking
│   │   ├── selectors/                      # Reselect selectors
│   │   │   ├── authSelectors.ts            # Auth state selectors
│   │   │   ├── userSelectors.ts            # User state selectors
│   │   │   ├── matchSelectors.ts           # Match state selectors
│   │   │   └── sessionSelectors.ts         # Session state selectors
│   │   └── persistConfig.ts                # Redux persist configuration
│   ├── hooks/                              # Custom React hooks
│   │   ├── useAuth.ts                      # Authentication hook
│   │   ├── useApi.ts                       # API calling hook
│   │   ├── useWebSocket.ts                 # WebSocket hook
│   │   ├── useVideoCall.ts                 # Video calling hook
│   │   ├── useGeolocation.ts               # Location services hook
│   │   ├── useNotifications.ts             # Push notification hook
│   │   ├── useBiometric.ts                 # Biometric auth hook
│   │   ├── useOfflineSync.ts               # Offline synchronization
│   │   ├── useDeepLinking.ts               # Deep link handling
│   │   ├── useKeyboard.ts                  # Keyboard handling
│   │   ├── useNetworkStatus.ts             # Network connectivity
│   │   └── useAppState.ts                  # App state changes
│   ├── utils/                              # Utility functions
│   │   ├── validation/                     # Form validation utilities
│   │   │   ├── authValidation.ts           # Auth form validation
│   │   │   ├── profileValidation.ts        # Profile form validation
│   │   │   ├── skillValidation.ts          # Skill form validation
│   │   │   └── commonValidation.ts         # Shared validations
│   │   ├── formatting/                     # Data formatting utilities
│   │   │   ├── dateFormatting.ts           # Date/time formatting
│   │   │   ├── numberFormatting.ts         # Number formatting
│   │   │   ├── textFormatting.ts           # Text utilities
│   │   │   └── currencyFormatting.ts       # Currency formatting
│   │   ├── helpers/                        # General helper functions
│   │   │   ├── asyncUtils.ts               # Async utilities
│   │   │   ├── arrayUtils.ts               # Array manipulation
│   │   │   ├── objectUtils.ts              # Object utilities
│   │   │   ├── stringUtils.ts              # String utilities
│   │   │   └── mathUtils.ts                # Math utilities
│   │   ├── constants/                      # App constants
│   │   │   ├── apiConstants.ts             # API endpoints
│   │   │   ├── appConstants.ts             # App configuration
│   │   │   ├── colorConstants.ts           # Color palette
│   │   │   ├── fontConstants.ts            # Typography
│   │   │   └── dimensionConstants.ts       # Layout dimensions
│   │   ├── permissions/                    # Permission utilities
│   │   │   ├── cameraPermissions.ts        # Camera permissions
│   │   │   ├── locationPermissions.ts      # Location permissions
│   │   │   ├── microphonePermissions.ts    # Microphone permissions
│   │   │   └── notificationPermissions.ts  # Notification permissions
│   │   └── device/                         # Device-specific utilities
│   │       ├── deviceInfo.ts               # Device information
│   │       ├── platformUtils.ts            # iOS/Android differences
│   │       ├── orientationUtils.ts         # Screen orientation
│   │       └── keyboardUtils.ts            # Keyboard handling
│   ├── types/                              # TypeScript type definitions
│   │   ├── api/                            # API response types
│   │   │   ├── authTypes.ts                # Authentication types
│   │   │   ├── userTypes.ts                # User-related types
│   │   │   ├── skillTypes.ts               # Skill-related types
│   │   │   ├── matchTypes.ts               # Matching types
│   │   │   ├── sessionTypes.ts             # Session types
│   │   │   └── creditTypes.ts              # Credit system types
│   │   ├── navigation/                     # Navigation types
│   │   │   ├── authNavigationTypes.ts      # Auth navigation
│   │   │   ├── mainNavigationTypes.ts      # Main app navigation
│   │   │   └── rootNavigationTypes.ts      # Root navigation
│   │   ├── components/                     # Component prop types
│   │   │   ├── buttonTypes.ts              # Button component types
│   │   │   ├── inputTypes.ts               # Input component types
│   │   │   └── cardTypes.ts                # Card component types
│   │   ├── store/                          # Redux state types
│   │   │   ├── authStateTypes.ts           # Auth state types
│   │   │   ├── userStateTypes.ts           # User state types
│   │   │   └── appStateTypes.ts            # App state types
│   │   └── global/                         # Global type definitions
│   │       ├── commonTypes.ts              # Shared types
│   │       ├── errorTypes.ts               # Error types
│   │       └── utilityTypes.ts             # Utility types
│   ├── styles/                             # Styling and theming
│   │   ├── theme/                          # Theme configuration
│   │   │   ├── colors.ts                   # Color palette
│   │   │   ├── typography.ts               # Font styles
│   │   │   ├── spacing.ts                  # Layout spacing
│   │   │   ├── shadows.ts                  # Shadow styles
│   │   │   └── index.ts                    # Theme exports
│   │   ├── components/                     # Component-specific styles
│   │   │   ├── buttonStyles.ts             # Button styles
│   │   │   ├── cardStyles.ts               # Card styles
│   │   │   ├── inputStyles.ts              # Input styles
│   │   │   └── listStyles.ts               # List styles
│   │   ├── screens/                        # Screen-specific styles
│   │   │   ├── authScreenStyles.ts         # Auth screen styles
│   │   │   ├── homeScreenStyles.ts         # Home screen styles
│   │   │   └── profileScreenStyles.ts      # Profile screen styles
│   │   └── global/                         # Global styles
│   │       ├── globalStyles.ts             # App-wide styles
│   │       ├── layoutStyles.ts             # Layout utilities
│   │       └── animationStyles.ts          # Animation styles
│   ├── assets/                             # Static assets
│   │   ├── images/                         # Image assets
│   │   │   ├── icons/                      # App icons
│   │   │   ├── illustrations/              # Onboarding illustrations
│   │   │   ├── logos/                      # Brand logos
│   │   │   └── placeholders/               # Placeholder images
│   │   ├── fonts/                          # Custom fonts
│   │   │   ├── Roboto-Regular.ttf          # Primary font
│   │   │   ├── Roboto-Bold.ttf             # Bold font
│   │   │   └── Roboto-Light.ttf            # Light font
│   │   ├── animations/                     # Lottie animations
│   │   │   ├── loading.json                # Loading animation
│   │   │   ├── success.json                # Success animation
│   │   │   └── error.json                  # Error animation
│   │   └── sounds/                         # Audio assets
│   │       ├── notification.mp3            # Notification sound
│   │       ├── call-incoming.mp3           # Incoming call sound
│   │       └── call-ended.mp3              # Call ended sound
│   ├── i18n/                               # Internationalization
│   │   ├── index.ts                        # i18n configuration
│   │   ├── resources/                      # Translation files
│   │   │   ├── en/                         # English translations
│   │   │   │   ├── common.json             # Common translations
│   │   │   │   ├── auth.json               # Auth screen translations
│   │   │   │   ├── profile.json            # Profile translations
│   │   │   │   ├── skills.json             # Skills translations
│   │   │   │   ├── matches.json            # Matching translations
│   │   │   │   ├── sessions.json           # Session translations
│   │   │   │   └── errors.json             # Error messages
│   │   │   ├── es/                         # Spanish translations
│   │   │   ├── fr/                         # French translations
│   │   │   ├── de/                         # German translations
│   │   │   └── it/                         # Italian translations
│   │   ├── dateFormats/                    # Date format configurations
│   │   │   ├── enDateFormats.ts            # English date formats
│   │   │   ├── esDateFormats.ts            # Spanish date formats
│   │   │   └── otherDateFormats.ts         # Other language formats
│   │   └── pluralization/                  # Pluralization rules
│   │       ├── enPluralization.ts          # English plural rules
│   │       └── otherPluralization.ts       # Other language rules
│   ├── config/                             # App configuration
│   │   ├── apiConfig.ts                    # API configuration
│   │   ├── environmentConfig.ts            # Environment variables
│   │   ├── featureFlags.ts                 # Feature flag configuration
│   │   ├── analyticsConfig.ts              # Analytics setup
│   │   ├── crashReportingConfig.ts         # Crash reporting setup
│   │   └── deepLinkingConfig.ts            # Deep linking setup
│   └── App.tsx                             # Main App component
├── __tests__/                              # Test files
│   ├── components/                         # Component tests
│   ├── screens/                            # Screen tests
│   ├── services/                           # Service tests
│   ├── utils/                              # Utility tests
│   ├── __mocks__/                          # Mock files
│   ├── setup.ts                            # Test setup
│   └── testUtils.tsx                       # Test utilities
├── docs/                                   # Frontend documentation
│   ├── components/                         # Component documentation
│   ├── screens/                            # Screen documentation
│   ├── services/                           # Service documentation
│   ├── styling-guide.md                    # Styling guidelines
│   ├── testing-guide.md                    # Testing guidelines
│   └── deployment-guide.md                 # Deployment instructions
├── scripts/                                # Build and utility scripts
│   ├── build-android.sh                    # Android build script
│   ├── build-ios.sh                        # iOS build script
│   ├── clean.sh                            # Clean build artifacts
│   └── setup-env.sh                        # Environment setup
├── package.json                            # Dependencies and scripts
├── tsconfig.json                           # TypeScript configuration
├── babel.config.js                         # Babel configuration
├── metro.config.js                         # Metro bundler configuration
├── react-native.config.js                 # React Native configuration
├── jest.config.js                          # Jest testing configuration
├── .eslintrc.js                            # ESLint configuration
├── .prettierrc                             # Prettier configuration
├── .gitignore                              # Git ignore rules
├── .env.example                            # Environment variables template
├── README.md                               # Frontend-specific README
└── app.json                                # Expo configuration (if using Expo)
```

---

## 🎨 UI/UX DESIGN SYSTEM

### Design Principles
1. **Mobile-First:** Diseñado específicamente para móviles
2. **Adaptive Design:** Se adapta a iOS y Android guidelines
3. **Accessibility:** WCAG 2.1 AA compliance
4. **Performance:** 60fps animations, smooth interactions
5. **European Standards:** Multi-language, GDPR-friendly

### Color Palette
```typescript
// src/styles/theme/colors.ts
export const colors = {
  primary: {
    50: '#E3F2FD',
    100: '#BBDEFB',
    500: '#2196F3',  // Main brand color
    700: '#1976D2',
    900: '#0D47A1'
  },
  secondary: {
    50: '#F3E5F5',
    100: '#E1BEE7',
    500: '#9C27B0',  // Secondary brand
    700: '#7B1FA2',
    900: '#4A148C'
  },
  success: '#4CAF50',
  warning: '#FF9800',
  error: '#F44336',
  text: {
    primary: '#212121',
    secondary: '#757575',
    disabled: '#BDBDBD'
  },
  background: {
    primary: '#FFFFFF',
    secondary: '#FAFAFA',
    card: '#FFFFFF'
  }
};
```

### Typography
```typescript
// src/styles/theme/typography.ts
export const typography = {
  h1: { fontSize: 32, fontWeight: 'bold', lineHeight: 40 },
  h2: { fontSize: 28, fontWeight: 'bold', lineHeight: 36 },
  h3: { fontSize: 24, fontWeight: 'bold', lineHeight: 32 },
  h4: { fontSize: 20, fontWeight: '600', lineHeight: 28 },
  body1: { fontSize: 16, fontWeight: 'normal', lineHeight: 24 },
  body2: { fontSize: 14, fontWeight: 'normal', lineHeight: 20 },
  caption: { fontSize: 12, fontWeight: 'normal', lineHeight: 16 }
};
```

### Component Library
- **Button:** Primary, Secondary, Outline, Text variants
- **Input:** Text, Email, Password, Search variants
- **Card:** Elevation, borders, interactive states
- **List:** Flat, sectioned, infinite scroll
- **Modal:** Bottom sheet, center modal, full screen
- **Navigation:** Tab bar, stack header, drawer

---

## 📱 SCREEN FLOWS

### Authentication Flow
```
WelcomeScreen → LoginScreen/RegisterScreen → EmailVerificationScreen → 
GDPRConsentScreen → OnboardingIntroScreen → SkillSelectionScreen → 
ProfileSetupScreen → LocationPermissionScreen → NotificationPermissionScreen → 
HomeScreen
```

### Main App Flow
```
HomeScreen (Dashboard) ←→ ExploreScreen (Discover)
    ↓                           ↓
MatchesScreen ←→ SessionsScreen ←→ ProfileScreen
    ↓                ↓              ↓
MatchDetailScreen  SessionDetailScreen  ProfileSettingsScreen
    ↓                ↓              ↓
VideoCallScreen   ActiveSessionScreen  Various Settings
```

### Video Call Flow
```
SessionDetailScreen → VideoCallLobbyScreen → VideoCallScreen → 
CallEndedScreen → SessionFeedbackScreen → SessionsListScreen
```

---

## 🔌 API INTEGRATION

### API Client Configuration
```typescript
// src/services/api/client.ts
import axios from 'axios';
import { store } from '../store';
import { logout } from '../store/slices/authSlice';

const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Request interceptor for auth token
apiClient.interceptors.request.use((config) => {
  const token = store.getState().auth.accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      store.dispatch(logout());
    }
    return Promise.reject(error);
  }
);
```

### RTK Query API Slices
```typescript
// src/store/api/authApiSlice.ts
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

export const authApiSlice = createApi({
  reducerPath: 'authApi',
  baseQuery: fetchBaseQuery({
    baseUrl: '/api/auth',
    prepareHeaders: (headers, { getState }) => {
      const token = (getState() as RootState).auth.accessToken;
      if (token) {
        headers.set('authorization', `Bearer ${token}`);
      }
      return headers;
    }
  }),
  tagTypes: ['User'],
  endpoints: (builder) => ({
    login: builder.mutation({
      query: (credentials) => ({
        url: '/login',
        method: 'POST',
        body: credentials
      })
    }),
    register: builder.mutation({
      query: (userData) => ({
        url: '/register',
        method: 'POST',
        body: userData
      })
    }),
    refreshToken: builder.mutation({
      query: (refreshToken) => ({
        url: '/refresh',
        method: 'POST',
        body: { refreshToken }
      })
    })
  })
});
```

---

## 🔄 STATE MANAGEMENT

### Redux Store Structure
```typescript
// src/store/index.ts
export interface RootState {
  auth: AuthState;
  user: UserState;
  skills: SkillState;
  matches: MatchState;
  sessions: SessionState;
  credits: CreditState;
  notifications: NotificationState;
  videoCall: VideoCallState;
  appSettings: AppSettingsState;
}
```

### Redux Toolkit Slices
```typescript
// src/store/slices/authSlice.ts
interface AuthState {
  isAuthenticated: boolean;
  accessToken: string | null;
  refreshToken: string | null;
  user: User | null;
  isLoading: boolean;
  error: string | null;
  biometricEnabled: boolean;
}

export const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    loginStart: (state) => {
      state.isLoading = true;
      state.error = null;
    },
    loginSuccess: (state, action) => {
      state.isAuthenticated = true;
      state.accessToken = action.payload.accessToken;
      state.refreshToken = action.payload.refreshToken;
      state.user = action.payload.user;
      state.isLoading = false;
    },
    loginFailure: (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    },
    logout: (state) => {
      state.isAuthenticated = false;
      state.accessToken = null;
      state.refreshToken = null;
      state.user = null;
    }
  }
});
```

---

## 📞 VIDEO CALLING IMPLEMENTATION

### WebRTC Service
```typescript
// src/services/video/webrtcService.ts
import {
  RTCPeerConnection,
  RTCIceCandidate,
  RTCSessionDescription,
  mediaDevices
} from 'react-native-webrtc';

export class WebRTCService {
  private peerConnection: RTCPeerConnection;
  private localStream: MediaStream | null = null;
  private remoteStream: MediaStream | null = null;

  constructor() {
    this.peerConnection = new RTCPeerConnection({
      iceServers: [
        { urls: 'stun:stun.l.google.com:19302' },
        { urls: 'stun:stun1.l.google.com:19302' }
      ]
    });
  }

  async initializeCall(): Promise<MediaStream> {
    const constraints = {
      audio: true,
      video: {
        width: 640,
        height: 480,
        frameRate: 30,
        facingMode: 'user'
      }
    };

    this.localStream = await mediaDevices.getUserMedia(constraints);
    this.localStream.getTracks().forEach(track => {
      this.peerConnection.addTrack(track, this.localStream!);
    });

    return this.localStream;
  }

  async createOffer(): Promise<RTCSessionDescription> {
    const offer = await this.peerConnection.createOffer();
    await this.peerConnection.setLocalDescription(offer);
    return offer;
  }

  async handleAnswer(answer: RTCSessionDescription): Promise<void> {
    await this.peerConnection.setRemoteDescription(answer);
  }

  async addIceCandidate(candidate: RTCIceCandidate): Promise<void> {
    await this.peerConnection.addIceCandidate(candidate);
  }
}
```

### Video Call Screen Component
```typescript
// src/screens/video/VideoCallScreen.tsx
import React, { useEffect, useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { RTCView } from 'react-native-webrtc';
import { useVideoCall } from '../../hooks/useVideoCall';
import VideoControls from '../../components/video/VideoControls';

export const VideoCallScreen: React.FC = () => {
  const {
    localStream,
    remoteStream,
    isConnected,
    isMuted,
    isVideoEnabled,
    endCall,
    toggleMute,
    toggleVideo
  } = useVideoCall();

  return (
    <View style={styles.container}>
      {remoteStream && (
        <RTCView
          style={styles.remoteVideo}
          streamURL={remoteStream.toURL()}
        />
      )}
      
      {localStream && (
        <RTCView
          style={styles.localVideo}
          streamURL={localStream.toURL()}
          mirror={true}
        />
      )}
      
      <VideoControls
        isMuted={isMuted}
        isVideoEnabled={isVideoEnabled}
        onToggleMute={toggleMute}
        onToggleVideo={toggleVideo}
        onEndCall={endCall}
      />
    </View>
  );
};
```

---

## 🌍 LOCALIZATION & EUROPEAN COMPLIANCE

### i18n Implementation
```typescript
// src/i18n/index.ts
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import * as RNLocalize from 'react-native-localize';

// Import translation files
import en from './resources/en';
import es from './resources/es';
import fr from './resources/fr';
import de from './resources/de';
import it from './resources/it';

const resources = { en, es, fr, de, it };

const fallback = { languageTag: 'en', isRTL: false };
const { languageTag } = RNLocalize.findBestAvailableLanguage(
  Object.keys(resources)
) || fallback;

i18n.use(initReactI18next).init({
  resources,
  lng: languageTag,
  fallbackLng: 'en',
  interpolation: {
    escapeValue: false
  }
});

export default i18n;
```

### GDPR Compliance Components
```typescript
// src/components/auth/GDPRConsentScreen.tsx
export const GDPRConsentScreen: React.FC = () => {
  const [consents, setConsents] = useState({
    essential: true, // Required, cannot be disabled
    analytics: false,
    marketing: false,
    location: false
  });

  const handleConsentChange = (type: string, value: boolean) => {
    setConsents(prev => ({ ...prev, [type]: value }));
  };

  const handleAcceptAll = () => {
    setConsents({
      essential: true,
      analytics: true,
      marketing: true,
      location: true
    });
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>{t('gdpr.title')}</Text>
      <Text style={styles.description}>{t('gdpr.description')}</Text>
      
      <ConsentToggle
        title={t('gdpr.essential.title')}
        description={t('gdpr.essential.description')}
        value={consents.essential}
        disabled={true}
        required={true}
      />
      
      <ConsentToggle
        title={t('gdpr.analytics.title')}
        description={t('gdpr.analytics.description')}
        value={consents.analytics}
        onChange={(value) => handleConsentChange('analytics', value)}
      />
      
      {/* More consent toggles... */}
      
      <View style={styles.buttonContainer}>
        <Button title={t('gdpr.acceptSelected')} onPress={handleAcceptSelected} />
        <Button title={t('gdpr.acceptAll')} onPress={handleAcceptAll} />
      </View>
    </ScrollView>
  );
};
```

---

## 📲 PUSH NOTIFICATIONS

### FCM Service Implementation
```typescript
// src/services/push/fcmService.ts
import messaging from '@react-native-firebase/messaging';
import { Platform } from 'react-native';

export class FCMService {
  static async initializeFirebase(): Promise<void> {
    // Request permission
    const authStatus = await messaging().requestPermission();
    const enabled =
      authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
      authStatus === messaging.AuthorizationStatus.PROVISIONAL;

    if (enabled) {
      console.log('Authorization status:', authStatus);
    }
  }

  static async getFCMToken(): Promise<string | null> {
    try {
      const token = await messaging().getToken();
      console.log('FCM Token:', token);
      return token;
    } catch (error) {
      console.error('Error getting FCM token:', error);
      return null;
    }
  }

  static setupNotificationHandlers(): void {
    // Foreground messages
    messaging().onMessage(async (remoteMessage) => {
      console.log('Foreground message:', remoteMessage);
      // Handle notification in app
    });

    // Background/Quit messages
    messaging().setBackgroundMessageHandler(async (remoteMessage) => {
      console.log('Background message:', remoteMessage);
    });

    // Notification opened app
    messaging().onNotificationOpenedApp((remoteMessage) => {
      console.log('Notification opened app:', remoteMessage);
      // Navigate to specific screen
    });
  }
}
```

---

## 🧪 TESTING STRATEGY

### Component Testing
```typescript
// __tests__/components/common/Button.test.tsx
import React from 'react';
import { render, fireEvent } from '@testing-library/react-native';
import { Button } from '../../../src/components/common/Button';

describe('Button Component', () => {
  it('renders correctly with title', () => {
    const { getByText } = render(
      <Button title="Test Button" onPress={() => {}} />
    );
    
    expect(getByText('Test Button')).toBeTruthy();
  });

  it('calls onPress when pressed', () => {
    const mockOnPress = jest.fn();
    const { getByText } = render(
      <Button title="Test Button" onPress={mockOnPress} />
    );
    
    fireEvent.press(getByText('Test Button'));
    expect(mockOnPress).toHaveBeenCalledTimes(1);
  });

  it('is disabled when loading', () => {
    const { getByTestId } = render(
      <Button title="Test Button" loading={true} onPress={() => {}} />
    );
    
    const button = getByTestId('button');
    expect(button.props.accessibilityState.disabled).toBe(true);
  });
});
```

### E2E Testing Setup
```typescript
// e2e/loginFlow.test.ts
import { device, element, by, expect } from 'detox';

describe('Login Flow', () => {
  beforeAll(async () => {
    await device.launchApp();
  });

  beforeEach(async () => {
    await device.reloadReactNative();
  });

  it('should complete login flow successfully', async () => {
    // Navigate to login
    await element(by.id('loginButton')).tap();
    
    // Enter credentials
    await element(by.id('emailInput')).typeText('test@example.com');
    await element(by.id('passwordInput')).typeText('password123');
    
    // Submit form
    await element(by.id('submitButton')).tap();
    
    // Verify navigation to home screen
    await expect(element(by.id('homeScreen'))).toBeVisible();
  });
});
```

---

## 🚀 DEPLOYMENT & BUILD

### Build Scripts
```json
// package.json scripts
{
  "scripts": {
    "android": "react-native run-android",
    "ios": "react-native run-ios",
    "start": "react-native start",
    "test": "jest",
    "lint": "eslint src --ext .ts,.tsx",
    "build:android:dev": "cd android && ./gradlew assembleDebug",
    "build:android:release": "cd android && ./gradlew assembleRelease",
    "build:ios:dev": "xcodebuild -workspace ios/SkillSwap.xcworkspace -scheme SkillSwap -configuration Debug",
    "build:ios:release": "xcodebuild -workspace ios/SkillSwap.xcworkspace -scheme SkillSwap -configuration Release",
    "bundle:android": "react-native bundle --platform android --dev false --entry-file index.js --bundle-output android/app/src/main/assets/index.android.bundle",
    "bundle:ios": "react-native bundle --platform ios --dev false --entry-file index.js --bundle-output ios/main.jsbundle"
  }
}
```

### Environment Configuration
```typescript
// src/config/environmentConfig.ts
interface Config {
  API_BASE_URL: string;
  AGORA_APP_ID: string;
  GOOGLE_CALENDAR_CLIENT_ID: string;
  FCM_SENDER_ID: string;
  ENVIRONMENT: 'development' | 'staging' | 'production';
}

const developmentConfig: Config = {
  API_BASE_URL: 'http://localhost:8080/api/v1',
  AGORA_APP_ID: 'dev_agora_app_id',
  GOOGLE_CALENDAR_CLIENT_ID: 'dev_google_client_id',
  FCM_SENDER_ID: 'dev_fcm_sender_id',
  ENVIRONMENT: 'development'
};

const productionConfig: Config = {
  API_BASE_URL: 'https://api.skillswap.com/api/v1',
  AGORA_APP_ID: 'prod_agora_app_id',
  GOOGLE_CALENDAR_CLIENT_ID: 'prod_google_client_id',
  FCM_SENDER_ID: 'prod_fcm_sender_id',
  ENVIRONMENT: 'production'
};

export const config = __DEV__ ? developmentConfig : productionConfig;
```

---

## 📊 PERFORMANCE OPTIMIZATION

### Device Tier Optimization
```typescript
// src/utils/device/deviceInfo.ts
export const getDeviceTier = (): 'budget' | 'mid' | 'high' => {
  const { totalMemory } = getSystemMemoryInfo();
  
  if (totalMemory < 3000) return 'budget';
  if (totalMemory < 6000) return 'mid';
  return 'high';
};

// Adaptive video quality based on device
export const getVideoConstraints = (tier: string) => {
  const constraints = {
    budget: { width: 480, height: 320, frameRate: 24 },
    mid: { width: 720, height: 480, frameRate: 30 },
    high: { width: 1280, height: 720, frameRate: 30 }
  };
  
  return constraints[tier] || constraints.mid;
};
```

### Memory Management
```typescript
// src/hooks/useMemoryOptimization.ts
export const useMemoryOptimization = () => {
  useEffect(() => {
    const cleanup = () => {
      // Clear image cache periodically
      FastImage.clearDiskCache();
      
      // Clear Redux cache for old data
      dispatch(clearOldCacheData());
    };

    const interval = setInterval(cleanup, 300000); // 5 minutes
    return () => clearInterval(interval);
  }, []);
};
```

---

## 🤝 INTEGRACIÓN CON BACKEND

### API Response Handling
```typescript
// src/services/api/responseHandler.ts
export const handleApiResponse = <T>(response: AxiosResponse): T => {
  if (response.status >= 200 && response.status < 300) {
    return response.data;
  }
  
  throw new ApiError(
    response.data.message || 'Something went wrong',
    response.status,
    response.data.code
  );
};
```

### WebSocket Integration
```typescript
// src/services/websocket/socketClient.ts
import io from 'socket.io-client';

export class SocketClient {
  private socket: SocketIOClient.Socket;

  connect(token: string): void {
    this.socket = io(config.API_BASE_URL, {
      auth: { token },
      transports: ['websocket']
    });

    this.socket.on('match_request_received', (data) => {
      store.dispatch(addMatchRequest(data));
      showNotification('New match request!');
    });

    this.socket.on('session_starting', (data) => {
      store.dispatch(updateSession(data));
      showNotification('Your session is starting in 5 minutes');
    });
  }

  disconnect(): void {
    if (this.socket) {
      this.socket.disconnect();
    }
  }
}
```

---

## 🎯 ROADMAP FRONTEND (8 Sprints)

### Sprint 1-2: Foundation & Setup (4 semanas)
- [ ] React Native project initialization
- [ ] Navigation setup (Stack + Tab + Drawer)
- [ ] Redux store configuration
- [ ] Basic component library setup
- [ ] Authentication screens (Login/Register)
- [ ] API client configuration
- [ ] Basic theming and styling

### Sprint 3-4: Core Screens (4 semanas)
- [ ] User profile management
- [ ] Skill management interface
- [ ] Basic matching interface
- [ ] Session scheduling screens
- [ ] Credit system UI
- [ ] Form validation implementation
- [ ] Loading states and error handling

### Sprint 5-6: Advanced Features (4 semanas)
- [ ] Video calling integration
- [ ] Real-time WebSocket features
- [ ] Push notification implementation
- [ ] Camera and image handling
- [ ] Calendar integration
- [ ] Offline capabilities
- [ ] Performance optimizations

### Sprint 7-8: Polish & Launch (4 semanas)
- [ ] Multi-language implementation
- [ ] GDPR compliance features
- [ ] Biometric authentication
- [ ] App store preparations
- [ ] Performance testing
- [ ] Accessibility improvements
- [ ] Final polish and bug fixes

---

*Documento específico para desarrollo del Frontend de SkillSwap*
*Para coordinarse con Backend, consultar: `../shared-docs/`*
*Última actualización: 6 de septiembre de 2025*
