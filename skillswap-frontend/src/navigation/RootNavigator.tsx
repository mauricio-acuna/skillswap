import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { RootStackParamList } from './navigationTypes';

// Import navigators
import AuthNavigator from './AuthNavigator';
import DrawerNavigator from './DrawerNavigator';

// Import onboarding stack (placeholder for now)
const OnboardingNavigator = () => null; // Will be implemented later

const Stack = createStackNavigator<RootStackParamList>();

// Deep linking configuration
const linking = {
  prefixes: ['skillswap://'],
  config: {
    screens: {
      AuthStack: {
        screens: {
          Welcome: 'welcome',
          Login: 'login',
          Register: 'register',
          ForgotPassword: 'forgot-password',
          EmailVerification: 'verify-email/:email',
          GDPRConsent: 'gdpr-consent',
        },
      },
      OnboardingStack: {
        screens: {
          OnboardingIntro: 'onboarding',
          SkillSelection: 'onboarding/skills',
          ProfileSetup: 'onboarding/profile',
          LocationPermission: 'onboarding/location',
          NotificationPermission: 'onboarding/notifications',
        },
      },
      MainDrawer: {
        screens: {
          MainTabs: {
            screens: {
              HomeTab: {
                screens: {
                  Home: 'home',
                  Notifications: 'notifications',
                  Settings: 'settings',
                },
              },
              ExploreTab: {
                screens: {
                  Explore: 'explore',
                  SkillCategories: 'explore/categories',
                  SkillDetail: 'explore/skill/:skillId',
                  UserProfile: 'explore/user/:userId',
                },
              },
              MatchesTab: {
                screens: {
                  Matches: 'matches',
                  DiscoverMatches: 'matches/discover',
                  MatchDetail: 'matches/detail/:matchId',
                  SendMatchRequest: 'matches/request/:userId',
                  MatchRequests: 'matches/requests',
                  MatchingPreferences: 'matches/preferences',
                },
              },
              SessionsTab: {
                screens: {
                  Sessions: 'sessions',
                  SessionDetail: 'sessions/detail/:sessionId',
                  ScheduleSession: 'sessions/schedule',
                  JoinSession: 'sessions/join/:sessionId',
                  ActiveSession: 'sessions/active/:sessionId',
                  SessionFeedback: 'sessions/feedback/:sessionId',
                },
              },
              ProfileTab: {
                screens: {
                  Profile: 'profile',
                  ProfileSettings: 'profile/settings',
                  SkillsOverview: 'profile/skills',
                  AddSkill: 'profile/skills/add',
                  CreditsOverview: 'profile/credits',
                  CreditHistory: 'profile/credits/history',
                },
              },
            },
          },
        },
      },
      VideoCallModal: 'call/:sessionId',
      SettingsModal: 'settings-modal',
    },
  },
};

interface RootNavigatorProps {
  isAuthenticated?: boolean;
  hasCompletedOnboarding?: boolean;
}

const RootNavigator: React.FC<RootNavigatorProps> = ({ 
  isAuthenticated = false, 
  hasCompletedOnboarding = false 
}) => {
  // Determine initial route based on auth state
  const getInitialRouteName = (): keyof RootStackParamList => {
    if (!isAuthenticated) {
      return 'AuthStack';
    }
    if (!hasCompletedOnboarding) {
      return 'OnboardingStack';
    }
    return 'MainDrawer';
  };

  return (
    <NavigationContainer linking={linking}>
      <Stack.Navigator
        initialRouteName={getInitialRouteName()}
        screenOptions={{
          headerShown: false, // Each navigator handles its own headers
          cardStyle: { backgroundColor: 'transparent' },
          animationEnabled: true,
        }}>
        <Stack.Screen 
          name="AuthStack" 
          component={AuthNavigator}
          options={{
            animationTypeForReplace: !isAuthenticated ? 'pop' : 'push',
          }}
        />
        <Stack.Screen 
          name="OnboardingStack" 
          component={OnboardingNavigator}
        />
        <Stack.Screen 
          name="MainDrawer" 
          component={DrawerNavigator}
        />
        {/* Modal screens */}
        <Stack.Group screenOptions={{ presentation: 'modal' }}>
          <Stack.Screen 
            name="VideoCallModal" 
            component={() => null} // Will be implemented with video calling
            options={{
              headerShown: false,
              animationTypeForReplace: 'push',
            }}
          />
          <Stack.Screen 
            name="SettingsModal" 
            component={() => null} // Settings modal component
            options={{
              title: 'Settings',
              headerShown: true,
            }}
          />
        </Stack.Group>
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default RootNavigator;
