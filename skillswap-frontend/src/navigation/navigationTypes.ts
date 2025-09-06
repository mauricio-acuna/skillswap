import type { NavigatorScreenParams } from '@react-navigation/native';
import type { StackScreenProps } from '@react-navigation/stack';
import type { BottomTabScreenProps } from '@react-navigation/bottom-tabs';
import type { DrawerScreenProps } from '@react-navigation/drawer';

// Auth Stack Navigation Types
export type AuthStackParamList = {
  Welcome: undefined;
  Login: undefined;
  Register: undefined;
  ForgotPassword: undefined;
};

export type AuthStackScreenProps<T extends keyof AuthStackParamList> = 
  StackScreenProps<AuthStackParamList, T>;

// Onboarding Stack Navigation Types
export type OnboardingStackParamList = {
  OnboardingIntro: undefined;
  SkillSelection: undefined;
  ProfileSetup: undefined;
  LocationPermission: undefined;
  NotificationPermission: undefined;
};

export type OnboardingStackScreenProps<T extends keyof OnboardingStackParamList> = 
  StackScreenProps<OnboardingStackParamList, T>;

// Main Tab Navigation Types
export type MainTabParamList = {
  HomeTab: NavigatorScreenParams<HomeStackParamList>;
  ExploreTab: NavigatorScreenParams<ExploreStackParamList>;
  MatchesTab: NavigatorScreenParams<MatchesStackParamList>;
  SessionsTab: NavigatorScreenParams<SessionsStackParamList>;
  ProfileTab: NavigatorScreenParams<ProfileStackParamList>;
};

export type MainTabScreenProps<T extends keyof MainTabParamList> = 
  BottomTabScreenProps<MainTabParamList, T>;

// Home Stack Navigation Types
export type HomeStackParamList = {
  Home: undefined;
  Notifications: undefined;
  Settings: undefined;
};

export type HomeStackScreenProps<T extends keyof HomeStackParamList> = 
  StackScreenProps<HomeStackParamList, T>;

// Explore Stack Navigation Types
export type ExploreStackParamList = {
  Explore: undefined;
  SkillCategories: undefined;
  SkillDetail: { skillId: string };
  UserProfile: { userId: string };
};

export type ExploreStackScreenProps<T extends keyof ExploreStackParamList> = 
  StackScreenProps<ExploreStackParamList, T>;

// Matches Stack Navigation Types
export type MatchesStackParamList = {
  Matches: undefined;
  DiscoverMatches: undefined;
  MatchDetail: { matchId: string };
  SendMatchRequest: { userId: string };
  MatchRequests: undefined;
  MatchingPreferences: undefined;
};

export type MatchesStackScreenProps<T extends keyof MatchesStackParamList> = 
  StackScreenProps<MatchesStackParamList, T>;

// Sessions Stack Navigation Types
export type SessionsStackParamList = {
  Sessions: undefined;
  SessionDetail: { sessionId: string };
  ScheduleSession: { matchId?: string };
  JoinSession: { sessionId: string };
  ActiveSession: { sessionId: string };
  SessionFeedback: { sessionId: string };
  VideoCallLobby: { sessionId: string };
  VideoCall: { sessionId: string };
  CallEnded: { sessionId: string; duration: number };
};

export type SessionsStackScreenProps<T extends keyof SessionsStackParamList> = 
  StackScreenProps<SessionsStackParamList, T>;

// Profile Stack Navigation Types
export type ProfileStackParamList = {
  Profile: undefined;
  ProfileSettings: undefined;
  SkillsOverview: undefined;
  AddSkill: undefined;
  SkillVerification: { skillId: string };
  CreditsOverview: undefined;
  CreditHistory: undefined;
  TransferCredits: undefined;
  EarnCredits: undefined;
  NotificationSettings: undefined;
  PrivacySettings: undefined;
  LanguageSettings: undefined;
  About: undefined;
  Help: undefined;
};

export type ProfileStackScreenProps<T extends keyof ProfileStackParamList> = 
  StackScreenProps<ProfileStackParamList, T>;

// Drawer Navigation Types
export type DrawerParamList = {
  MainTabs: NavigatorScreenParams<MainTabParamList>;
  Settings: undefined;
  Help: undefined;
  About: undefined;
  Logout: undefined;
};

export type DrawerScreenProps<T extends keyof DrawerParamList> = 
  DrawerScreenProps<DrawerParamList, T>;

// Root Stack Navigation Types
export type RootStackParamList = {
  AuthStack: NavigatorScreenParams<AuthStackParamList>;
  OnboardingStack: NavigatorScreenParams<OnboardingStackParamList>;
  MainDrawer: NavigatorScreenParams<DrawerParamList>;
  // Modal screens
  VideoCallModal: { sessionId: string };
  SettingsModal: undefined;
};

export type RootStackScreenProps<T extends keyof RootStackParamList> = 
  StackScreenProps<RootStackParamList, T>;

// Global navigation props (accessible from any screen)
declare global {
  namespace ReactNavigation {
    interface RootParamList extends RootStackParamList {}
  }
}

// Navigation utilities types
export interface NavigationConfig {
  initialRouteName?: keyof RootStackParamList;
  linking?: {
    prefixes: string[];
    config: LinkingConfig;
  };
}

export interface LinkingConfig {
  screens: {
    AuthStack: {
      screens: {
        Welcome: 'welcome';
        Login: 'login';
        Register: 'register';
        ForgotPassword: 'forgot-password';
        EmailVerification: 'verify-email/:email';
        GDPRConsent: 'gdpr-consent';
      };
    };
    OnboardingStack: {
      screens: {
        OnboardingIntro: 'onboarding';
        SkillSelection: 'onboarding/skills';
        ProfileSetup: 'onboarding/profile';
        LocationPermission: 'onboarding/location';
        NotificationPermission: 'onboarding/notifications';
      };
    };
    MainDrawer: {
      screens: {
        MainTabs: {
          screens: {
            HomeTab: {
              screens: {
                Home: 'home';
                Notifications: 'notifications';
                Settings: 'settings';
              };
            };
            ExploreTab: {
              screens: {
                Explore: 'explore';
                SkillCategories: 'explore/categories';
                SkillDetail: 'explore/skill/:skillId';
                UserProfile: 'explore/user/:userId';
              };
            };
            MatchesTab: {
              screens: {
                Matches: 'matches';
                DiscoverMatches: 'matches/discover';
                MatchDetail: 'matches/detail/:matchId';
                SendMatchRequest: 'matches/request/:userId';
                MatchRequests: 'matches/requests';
                MatchingPreferences: 'matches/preferences';
              };
            };
            SessionsTab: {
              screens: {
                Sessions: 'sessions';
                SessionDetail: 'sessions/detail/:sessionId';
                ScheduleSession: 'sessions/schedule';
                JoinSession: 'sessions/join/:sessionId';
                ActiveSession: 'sessions/active/:sessionId';
                SessionFeedback: 'sessions/feedback/:sessionId';
              };
            };
            ProfileTab: {
              screens: {
                Profile: 'profile';
                ProfileSettings: 'profile/settings';
                SkillsOverview: 'profile/skills';
                AddSkill: 'profile/skills/add';
                CreditsOverview: 'profile/credits';
                CreditHistory: 'profile/credits/history';
              };
            };
          };
        };
      };
    };
    VideoCallModal: 'call/:sessionId';
    SettingsModal: 'settings-modal';
  };
}
