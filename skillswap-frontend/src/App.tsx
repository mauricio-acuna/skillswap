import React from 'react';
import {
  SafeAreaView,
  StatusBar,
  StyleSheet,
  Text,
  View,
  ScrollView,
} from 'react-native';
import { colors } from './styles/theme';
// Uncomment when navigation dependencies are installed:
// import RootNavigator from './navigation/RootNavigator';
// import { useSelector } from 'react-redux';
// import { RootState } from '@store';

const App: React.FC = () => {
  // When Redux is properly set up, uncomment these lines:
  // const isAuthenticated = useSelector((state: RootState) => state.auth?.isAuthenticated || false);
  // const hasCompletedOnboarding = useSelector((state: RootState) => state.auth?.user?.hasCompletedOnboarding || false);

  // For now, show the status screen until dependencies are installed
  // When ready, replace with: return <RootNavigator isAuthenticated={isAuthenticated} hasCompletedOnboarding={hasCompletedOnboarding} />;

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar
        barStyle="dark-content"
        backgroundColor={colors.background.primary}
      />
      <ScrollView contentInsetAdjustmentBehavior="automatic">
        <View style={styles.content}>
          <Text style={styles.title}>🎯 SkillSwap</Text>
          <Text style={styles.subtitle}>React Native Mobile App</Text>
          
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>✅ Navigation System Complete</Text>
            <Text style={styles.description}>
              The SkillSwap navigation architecture has been implemented with:
            </Text>
            
            <View style={styles.featureList}>
              <Text style={styles.feature}>🧭 Complete Navigation Setup (Stack + Tab + Drawer)</Text>
              <Text style={styles.feature}>🔐 Authentication Flow Navigation</Text>
              <Text style={styles.feature}>📱 5 Main Tabs: Home, Explore, Matches, Sessions, Profile</Text>
              <Text style={styles.feature}>🗂️ TypeScript Navigation Types</Text>
              <Text style={styles.feature}>🔗 Deep Linking Configuration</Text>
              <Text style={styles.feature}>🎨 Theme-based Navigation Styling</Text>
            </View>
          </View>

          <View style={styles.section}>
            <Text style={styles.sectionTitle}>🏗️ Architecture Implemented</Text>
            <Text style={styles.description}>
              Professional multi-level navigation structure:
            </Text>
            <View style={styles.featureList}>
              <Text style={styles.feature}>📋 AuthNavigator: Welcome → Login → Register</Text>
              <Text style={styles.feature}>🏠 HomeStack: Dashboard, Notifications, Settings</Text>
              <Text style={styles.feature}>🔍 ExploreStack: Skills, Categories, User Profiles</Text>
              <Text style={styles.feature}>🤝 MatchesStack: Discover, Requests, Preferences</Text>
              <Text style={styles.feature}>📞 SessionsStack: Schedule, Join, Video Calls</Text>
              <Text style={styles.feature}>👤 ProfileStack: Skills, Credits, Settings</Text>
            </View>
          </View>

          <View style={styles.section}>
            <Text style={styles.sectionTitle}>🚀 Next Steps</Text>
            <Text style={styles.description}>
              Ready for implementation:
            </Text>
            <View style={styles.featureList}>
              <Text style={styles.feature}>� Install Navigation Dependencies</Text>
              <Text style={styles.feature}>🔐 Authentication Screens Implementation</Text>
              <Text style={styles.feature}>📋 Basic Component Library</Text>
              <Text style={styles.feature}>🔌 API Client Integration</Text>
            </View>
          </View>

          <View style={styles.section}>
            <Text style={styles.sectionTitle}>📋 Commands to Run</Text>
            <Text style={styles.description}>
              When Node.js is available:
            </Text>
            <View style={styles.featureList}>
              <Text style={styles.feature}>npm install (install all dependencies)</Text>
              <Text style={styles.feature}>npm start (start Metro bundler)</Text>
              <Text style={styles.feature}>npm run ios (run on iOS simulator)</Text>
              <Text style={styles.feature}>npm run android (run on Android emulator)</Text>
            </View>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background.primary,
  },
  content: {
    padding: 24,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: colors.primary[500],
    textAlign: 'center',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 18,
    color: colors.text.secondary,
    textAlign: 'center',
    marginBottom: 32,
  },
  section: {
    marginBottom: 24,
    padding: 16,
    backgroundColor: colors.surface.secondary,
    borderRadius: 12,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: '600',
    color: colors.text.primary,
    marginBottom: 12,
  },
  description: {
    fontSize: 16,
    color: colors.text.secondary,
    lineHeight: 24,
    marginBottom: 16,
  },
  featureList: {
    gap: 8,
  },
  feature: {
    fontSize: 14,
    color: colors.text.primary,
    lineHeight: 20,
    paddingLeft: 8,
  },
});

export default App;
