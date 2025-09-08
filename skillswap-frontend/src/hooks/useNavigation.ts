import { useNavigation } from '@react-navigation/native';
import { useSelector } from 'react-redux';
import { RootState } from '../store';

/**
 * Custom hook for navigation utilities
 * Provides typed navigation and common navigation patterns
 */
export const useAppNavigation = () => {
  const navigation = useNavigation();
  
  // Get auth state from Redux
  const isAuthenticated = useSelector((state: RootState) => state?.auth?.isAuthenticated || false);
  const user = useSelector((state: RootState) => state?.auth?.user);

  /**
   * Navigate to authentication flow
   */
  const navigateToAuth = () => {
    navigation.navigate('AuthStack', { screen: 'Welcome' });
  };

  /**
   * Navigate to main app (after successful login)
   */
  const navigateToMain = () => {
    navigation.navigate('MainDrawer', { 
      screen: 'MainTabs',
      params: {
        screen: 'ExploreTab',
        params: {
          screen: 'Explore'
        }
      }
    });
  };

  /**
   * Navigate to onboarding flow
   */
  const navigateToOnboarding = () => {
    navigation.navigate('OnboardingStack', { screen: 'OnboardingIntro' });
  };

  /**
   * Navigate to a specific skill detail
   */
  const navigateToSkillDetail = (skillId: string) => {
    navigation.navigate('MainDrawer', {
      screen: 'MainTabs',
      params: {
        screen: 'ExploreTab',
        params: {
          screen: 'SkillDetail',
          params: { skillId },
        },
      },
    });
  };

  /**
   * Navigate to user profile
   */
  const navigateToUserProfile = (userId: string) => {
    navigation.navigate('MainDrawer', {
      screen: 'MainTabs',
      params: {
        screen: 'ExploreTab',
        params: {
          screen: 'UserProfile',
          params: { userId },
        },
      },
    });
  };

  /**
   * Navigate to video call
   */
  const navigateToVideoCall = (sessionId: string) => {
    navigation.navigate('VideoCallModal', { sessionId });
  };

  /**
   * Navigate to session detail
   */
  const navigateToSessionDetail = (sessionId: string) => {
    navigation.navigate('MainDrawer', {
      screen: 'MainTabs',
      params: {
        screen: 'SessionsTab',
        params: {
          screen: 'SessionDetail',
          params: { sessionId },
        },
      },
    });
  };

  /**
   * Navigate to match detail
   */
  const navigateToMatchDetail = (matchId: string) => {
    navigation.navigate('MainDrawer', {
      screen: 'MainTabs',
      params: {
        screen: 'MatchesTab',
        params: {
          screen: 'MatchDetail',
          params: { matchId },
        },
      },
    });
  };

  /**
   * Go back safely (checks if can go back)
   */
  const goBackSafely = () => {
    if (navigation.canGoBack()) {
      navigation.goBack();
    }
  };

  /**
   * Reset navigation stack to main app
   */
  const resetToMain = () => {
    navigation.reset({
      index: 0,
      routes: [{ name: 'MainDrawer' }],
    });
  };

  /**
   * Reset navigation stack to auth
   */
  const resetToAuth = () => {
    navigation.reset({
      index: 0,
      routes: [{ name: 'AuthStack' }],
    });
  };

  return {
    // Basic navigation
    navigation,
    goBackSafely,
    
    // Auth state
    isAuthenticated,
    user,
    
    // High-level navigation functions
    navigateToAuth,
    navigateToMain,
    navigateToOnboarding,
    navigateToSkillDetail,
    navigateToUserProfile,
    navigateToVideoCall,
    navigateToSessionDetail,
    navigateToMatchDetail,
    
    // Stack resets
    resetToMain,
    resetToAuth,
  };
};

/**
 * Hook for deep link handling
 */
export const useDeepLinking = () => {
  const { navigateToSkillDetail, navigateToUserProfile, navigateToSessionDetail } = useAppNavigation();

  /**
   * Handle incoming deep links
   */
  const handleDeepLink = (url: string) => {
    try {
      const urlObj = new URL(url);
      const path = urlObj.pathname;
      const segments = path.split('/').filter(Boolean);

      switch (segments[0]) {
        case 'skill':
          if (segments[1]) {
            navigateToSkillDetail(segments[1]);
          }
          break;
        case 'user':
          if (segments[1]) {
            navigateToUserProfile(segments[1]);
          }
          break;
        case 'session':
          if (segments[1]) {
            navigateToSessionDetail(segments[1]);
          }
          break;
        default:
          // Handle unknown deep links
          console.warn('Unknown deep link:', url);
      }
    } catch (error) {
      console.error('Error handling deep link:', error);
    }
  };

  return {
    handleDeepLink,
  };
};

export default useAppNavigation;
